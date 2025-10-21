package com.sv.youapp.component.authorization.configurer

import com.sv.youapp.common.authorization.services.NativeAuthenticationService
import com.sv.youapp.component.authorization.converter.NativeAuthenticationConverter
import com.sv.youapp.component.authorization.providers.NativeAuthenticationProvider
import com.sv.youapp.component.authorization.services.impl.DefaultNativeAuthenticationService
import org.springframework.beans.factory.BeanFactoryUtils
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.NoUniqueBeanDefinitionException
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.OAuth2Token
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.function.Consumer

@Component
class NativeAuthenticationConfigurer : AbstractHttpConfigurer<NativeAuthenticationConfigurer, HttpSecurity>() {
    private val accessTokenRequestConverters: MutableList<AuthenticationConverter> = ArrayList()

    private val authenticationProviders: MutableList<AuthenticationProvider> = ArrayList()

    fun accessTokenRequestConverter(converter: AuthenticationConverter?): NativeAuthenticationConfigurer {
        converter?.let {
            this.accessTokenRequestConverters.add(it)
        }
        return this
    }

    fun authenticationProvider(authenticationProvider: AuthenticationProvider?): NativeAuthenticationConfigurer {
        authenticationProvider?.let {
            this.authenticationProviders.add(it)
        }
        return this
    }

    override fun init(builder: HttpSecurity) {
        // val authorizationServerSettings = getAuthorizationServerSettings(builder)
        val authorizationServerConfigurer =
            builder.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
                ?: throw IllegalStateException("MUST CONFIGURE AFTER OAuth2AuthorizationServerConfigurer")
        val defaultProviders: MutableList<AuthenticationProvider> = createDefaultNativeProviders(builder)
        if (!this.authenticationProviders.isEmpty()) {
            defaultProviders.addAll(0, this.authenticationProviders)
        }
        defaultProviders.forEach(Consumer { authenticationProvider: AuthenticationProvider? -> builder.authenticationProvider(this.postProcess<AuthenticationProvider?>(authenticationProvider)) })
        val defaultConverters = createDefaultNativeConverters()
        if (!this.accessTokenRequestConverters.isEmpty()) {
            defaultConverters.addAll(0, this.accessTokenRequestConverters)
        }
        authorizationServerConfigurer.tokenEndpoint { tokenEndpoint ->
            tokenEndpoint.accessTokenRequestConverters {
                it.addAll(defaultConverters)
            }
        }
    }

    private fun createDefaultNativeConverters(): MutableList<AuthenticationConverter> {
        val authenticationConverters: MutableList<AuthenticationConverter> = ArrayList()
        authenticationConverters.add(NativeAuthenticationConverter())
        return authenticationConverters
    }

    private fun createDefaultNativeProviders(http: HttpSecurity): MutableList<AuthenticationProvider> {
        val authenticationProviders: MutableList<AuthenticationProvider> = ArrayList()
        val authorizationService: OAuth2AuthorizationService = getAuthorizationService(http)
        val tokenGenerator: OAuth2TokenGenerator<out OAuth2Token> = getTokenGenerator(http)
        val userDetails: UserDetailsService = getBean(http, UserDetailsService::class.java)
        val passwordEncoder: PasswordEncoder = getBean(http, PasswordEncoder::class.java)
        val nativeAuthenticationService: NativeAuthenticationService = DefaultNativeAuthenticationService(userDetails, passwordEncoder)
        authenticationProviders.add(NativeAuthenticationProvider(authorizationService, tokenGenerator, nativeAuthenticationService))
        return authenticationProviders
    }

    private fun getAuthorizationServerSettings(httpSecurity: HttpSecurity): AuthorizationServerSettings {
        var authorizationServerSettings = httpSecurity.getSharedObject(AuthorizationServerSettings::class.java)
        if (authorizationServerSettings == null) {
            authorizationServerSettings = getBean<AuthorizationServerSettings>(httpSecurity, AuthorizationServerSettings::class.java)
            httpSecurity.setSharedObject(AuthorizationServerSettings::class.java, authorizationServerSettings)
        }
        return authorizationServerSettings
    }

    private fun getAuthorizationService(httpSecurity: HttpSecurity): OAuth2AuthorizationService {
        var authorizationService = httpSecurity.getSharedObject(OAuth2AuthorizationService::class.java)
        if (authorizationService == null) {
            authorizationService = getOptionalBean<OAuth2AuthorizationService>(httpSecurity, OAuth2AuthorizationService::class.java)
            if (authorizationService == null) {
                authorizationService = InMemoryOAuth2AuthorizationService()
            }

            httpSecurity.setSharedObject(OAuth2AuthorizationService::class.java, authorizationService)
        }
        return authorizationService
    }

    private fun getTokenGenerator(httpSecurity: HttpSecurity): OAuth2TokenGenerator<out OAuth2Token?> {
        var tokenGenerator = httpSecurity.getSharedObject(OAuth2TokenGenerator::class.java)
        if (tokenGenerator == null) {
            tokenGenerator = getOptionalBean<OAuth2TokenGenerator<*>>(httpSecurity, OAuth2TokenGenerator::class.java)
            if (tokenGenerator == null) {
                throw NoSuchBeanDefinitionException(OAuth2TokenGenerator::class.java)
            }
        }

        return tokenGenerator
    }

    private inline fun <reified T> getOptionalBean(
        httpSecurity: HttpSecurity,
        type: Class<T>,
    ): T? {
        val beansMap = BeanFactoryUtils.beansOfTypeIncludingAncestors<T?>((httpSecurity.getSharedObject(ApplicationContext::class.java) as ListableBeanFactory?)!!, type)
        if (beansMap.size > 1) {
            val var10003 = beansMap.size
            val var10004 = type.getName()
            throw NoUniqueBeanDefinitionException(type, var10003, "Expected single matching bean of type '" + var10004 + "' but found " + beansMap.size + ": " + StringUtils.collectionToCommaDelimitedString(beansMap.keys))
        } else {
            return (if (!beansMap.isEmpty()) beansMap.values.iterator().next() else null)
        }
    }

    private inline fun <reified T> getBean(
        httpSecurity: HttpSecurity,
        type: Class<T>,
    ): T {
        val context = httpSecurity.getSharedObject(ApplicationContext::class.java) as ApplicationContext
        val names = context.getBeanNamesForType(type)
        if (names.size == 1) {
            return context.getBean(names[0]!!) as T
        } else if (names.size > 1) {
            throw NoUniqueBeanDefinitionException(type, *names)
        } else {
            throw NoSuchBeanDefinitionException(type)
        }
    }
}
