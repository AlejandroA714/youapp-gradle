package com.sv.youapp.authorization.repositories.redis

import com.sv.youapp.authorization.entities.redis.AbstractGrantAuthorization
import com.sv.youapp.authorization.entities.redis.OAuth2AuthorizationCodeGrantAuthorization
import com.sv.youapp.authorization.entities.redis.OAuth2DeviceCodeGrantAuthorization
import com.sv.youapp.authorization.entities.redis.OidcAuthorizationCodeGrantAuthorization
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@Suppress("FunctionName")
interface OAuth2AuthorizationGrantAuthorizationRepository : CrudRepository<AbstractGrantAuthorization, String> {
    fun <T : OAuth2AuthorizationCodeGrantAuthorization?> findByState(state: String?): T?

    fun <T : OAuth2AuthorizationCodeGrantAuthorization?> findByAuthorizationCode_TokenValue(authorizationCode: String?): T?

    fun <T : OAuth2AuthorizationCodeGrantAuthorization?> findByStateOrAuthorizationCode_TokenValue(
        state: String?,
        authorizationCode: String?,
    ): T?

    fun <T : AbstractGrantAuthorization?> findByAccessToken_TokenValue(accessToken: String?): T?

    fun <T : AbstractGrantAuthorization?> findByRefreshToken_TokenValue(refreshToken: String?): T?

    fun <T : AbstractGrantAuthorization?> findByAccessToken_TokenValueOrRefreshToken_TokenValue(
        accessToken: String?,
        refreshToken: String?,
    ): T?

    fun <T : OidcAuthorizationCodeGrantAuthorization?> findByIdToken_TokenValue(idToken: String?): T?

    fun <T : OAuth2DeviceCodeGrantAuthorization?> findByDeviceState(deviceState: String?): T?

    fun <T : OAuth2DeviceCodeGrantAuthorization?> findByDeviceCode_TokenValue(deviceCode: String?): T?

    fun <T : OAuth2DeviceCodeGrantAuthorization?> findByUserCode_TokenValue(userCode: String?): T?

    fun <T : OAuth2DeviceCodeGrantAuthorization?> findByDeviceStateOrDeviceCode_TokenValueOrUserCode_TokenValue(
        deviceState: String?,
        deviceCode: String?,
        userCode: String?,
    ): T?
}
