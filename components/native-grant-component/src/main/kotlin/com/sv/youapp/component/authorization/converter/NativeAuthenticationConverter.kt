package com.sv.youapp.component.authorization.converter

import com.sv.youapp.common.authorization.authentication.NATIVE_GRANT_TYPE
import com.sv.youapp.common.authorization.authentication.NativeAuthentication
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.StringUtils

class NativeAuthenticationConverter : AuthenticationConverter {
    override fun convert(request: HttpServletRequest): Authentication? {
        val grantType: String = request.getParameter(OAuth2ParameterNames.GRANT_TYPE)
        if (!NATIVE_GRANT_TYPE.value.equals(grantType)) {
            return null
        }
        val clientPrincipal = SecurityContextHolder.getContext().authentication
        val parameters: MultiValueMap<String?, String?> = getParameters(request)
        // username: (REQUIRED)
        val username: String =
            requireNotNull(parameters.getFirst(OAuth2ParameterNames.USERNAME)) {
                throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST)
            }
        // password: (REQUIRED)
        val password: String =
            requireNotNull(parameters.getFirst(OAuth2ParameterNames.PASSWORD)) {
                throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST)
            }
        // state: (OPTIONAL)
        val state: String? = parameters.getFirst(OAuth2ParameterNames.STATE)
        if (StringUtils.hasText(state) && parameters[OAuth2ParameterNames.STATE]!!.size != 1) {
            throw OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST)
        }
        // scope (OPTIONAL)
        var scopes: MutableSet<String?>? = null
        val scope = parameters.getFirst(OAuth2ParameterNames.SCOPE)
        if (StringUtils.hasText(scope) && parameters[OAuth2ParameterNames.SCOPE]!!.size != 1) {
            val error = "OAuth 2.0 Parameter: " + OAuth2ParameterNames.SCOPE
            throwError(error)
        }
        if (StringUtils.hasText(scope)) {
            if (scope!!.contains(','))
                {
                    val error = "OAuth 2.0 Parameter: " + OAuth2ParameterNames.SCOPE + " must be space separated"
                    throwError(error)
                }
            scopes = HashSet(listOf(*StringUtils.delimitedListToStringArray(scope, " ")))
        }
        return NativeAuthentication(
            username,
            password,
            clientPrincipal,
            state,
            scopes?.map { SimpleGrantedAuthority(it) }?.toSet() ?: setOf(),
            null,
        )
    }

    private fun throwError(message: String) {
        throw OAuth2AuthenticationException(
            OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, message, null),
        )
    }
}

private fun getParameters(request: HttpServletRequest): MultiValueMap<String?, String?> {
    val parameterMap = request.parameterMap
    val parameters: MultiValueMap<String?, String?> = LinkedMultiValueMap<String?, String?>()
    parameterMap.forEach { (key: String?, values: Array<String?>?) ->
        val queryString = if (StringUtils.hasText(request.queryString)) request.queryString else ""
        if (!queryString.contains(key!!) && values!!.isNotEmpty()) {
            for (value in values) {
                parameters.add(key, value)
            }
        }
    }
    return parameters
}
