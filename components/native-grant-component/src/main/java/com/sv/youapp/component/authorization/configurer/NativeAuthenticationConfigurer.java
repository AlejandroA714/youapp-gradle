package com.sv.youapp.component.authorization.configurer;

import com.sv.youapp.common.authorization.services.NativeAuthenticationService;
import com.sv.youapp.component.authorization.converter.NativeAuthenticationConverter;
import com.sv.youapp.component.authorization.providers.NativeAuthenticationProvider;
import com.sv.youapp.component.authorization.services.impl.DefaultNativeAuthenticationService;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class NativeAuthenticationConfigurer extends AbstractHttpConfigurer<NativeAuthenticationConfigurer, HttpSecurity> {

    private final List<AuthenticationConverter> accessTokenRequestConverters = new ArrayList<>();
    private final List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

    public NativeAuthenticationConfigurer accessTokenRequestConverter(AuthenticationConverter converter) {
        if (converter != null) {
            this.accessTokenRequestConverters.add(converter);
        }
        return this;
    }

    public NativeAuthenticationConfigurer authenticationProvider(AuthenticationProvider authenticationProvider) {
        if (authenticationProvider != null) {
            this.authenticationProviders.add(authenticationProvider);
        }
        return this;
    }

    @Override
    public void init(HttpSecurity builder) {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        builder.getConfigurer(OAuth2AuthorizationServerConfigurer.class);
        if (authorizationServerConfigurer == null) {
            throw new IllegalStateException("MUST CONFIGURE AFTER OAuth2AuthorizationServerConfigurer");
        }

        List<AuthenticationProvider> defaultProviders = createDefaultNativeProviders(builder);
        if (!this.authenticationProviders.isEmpty()) {
            defaultProviders.addAll(0, this.authenticationProviders);
        }
        for (AuthenticationProvider provider : defaultProviders) {
        builder.authenticationProvider(this.postProcess(provider));
    }

        List<AuthenticationConverter> defaultConverters = createDefaultNativeConverters();
        if (!this.accessTokenRequestConverters.isEmpty()) {
            defaultConverters.addAll(0, this.accessTokenRequestConverters);
        }

        authorizationServerConfigurer.tokenEndpoint(tokenEndpoint ->
        tokenEndpoint.accessTokenRequestConverters(converters -> converters.addAll(defaultConverters))
        );
    }

    private List<AuthenticationConverter> createDefaultNativeConverters() {
        List<AuthenticationConverter> authenticationConverters = new ArrayList<>();
        authenticationConverters.add(new NativeAuthenticationConverter());
        return authenticationConverters;
    }

    private List<AuthenticationProvider> createDefaultNativeProviders(HttpSecurity http) {
        List<AuthenticationProvider> providers = new ArrayList<>();
        OAuth2AuthorizationService authorizationService = getAuthorizationService(http);
        OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = getTokenGenerator(http);
        UserDetailsService userDetails = getBean(http, UserDetailsService.class);
        PasswordEncoder passwordEncoder = getBean(http, PasswordEncoder.class);
        NativeAuthenticationService nativeAuthenticationService =
        new DefaultNativeAuthenticationService(userDetails, passwordEncoder);
        providers.add(new NativeAuthenticationProvider(authorizationService, tokenGenerator, nativeAuthenticationService));
        return providers;
    }

    private AuthorizationServerSettings getAuthorizationServerSettings(HttpSecurity httpSecurity) {
        AuthorizationServerSettings settings = httpSecurity.getSharedObject(AuthorizationServerSettings.class);
        if (settings == null) {
            settings = getBean(httpSecurity, AuthorizationServerSettings.class);
            httpSecurity.setSharedObject(AuthorizationServerSettings.class, settings);
        }
        return settings;
    }

    private OAuth2AuthorizationService getAuthorizationService(HttpSecurity httpSecurity) {
        OAuth2AuthorizationService authorizationService =
        httpSecurity.getSharedObject(OAuth2AuthorizationService.class);
        if (authorizationService == null) {
            authorizationService = getOptionalBean(httpSecurity, OAuth2AuthorizationService.class);
            if (authorizationService == null) {
                authorizationService = new InMemoryOAuth2AuthorizationService();
            }
            httpSecurity.setSharedObject(OAuth2AuthorizationService.class, authorizationService);
        }
        return authorizationService;
    }

    private OAuth2TokenGenerator<? extends OAuth2Token> getTokenGenerator(HttpSecurity httpSecurity) {
        OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator =
            httpSecurity.getSharedObject(OAuth2TokenGenerator.class);
        if (tokenGenerator == null) {
            tokenGenerator = getOptionalBean(httpSecurity, OAuth2TokenGenerator.class);
            if (tokenGenerator == null) {
                throw new NoSuchBeanDefinitionException(OAuth2TokenGenerator.class);
            }
        }
        return tokenGenerator;
    }

    private <T> T getOptionalBean(HttpSecurity httpSecurity, Class<T> type) {
        ApplicationContext context = httpSecurity.getSharedObject(ApplicationContext.class);
        @SuppressWarnings("unchecked")
        Map<String, T> beansMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, type);
        if (beansMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(
                    type,
            beansMap.size(),
            "Expected single matching bean of type '" + type.getName() + "' but found " + beansMap.size() +
                    ": " + StringUtils.collectionToCommaDelimitedString(beansMap.keySet())
            );
        }
        return beansMap.isEmpty() ? null : beansMap.values().iterator().next();
    }

    private <T> T getBean(HttpSecurity httpSecurity, Class<T> type) {
        ApplicationContext context = httpSecurity.getSharedObject(ApplicationContext.class);
        String[] names = context.getBeanNamesForType(type);
        if (names.length == 1) {
            @SuppressWarnings("unchecked")
            T bean = (T) context.getBean(names[0]);
            return bean;
        } else if (names.length > 1) {
            throw new NoUniqueBeanDefinitionException(type, names);
        } else {
            throw new NoSuchBeanDefinitionException(type);
        }
    }
}
