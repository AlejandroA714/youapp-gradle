package com.sv.youapp.component.authorization.repositories.redis;

import com.sv.youapp.component.authorization.entities.redis.AbstractGrantAuthorization;
import com.sv.youapp.component.authorization.entities.redis.OAuth2AuthorizationCodeGrantAuthorization;
import com.sv.youapp.component.authorization.entities.redis.OAuth2DeviceCodeGrantAuthorization;
import com.sv.youapp.component.authorization.entities.redis.OidcAuthorizationCodeGrantAuthorization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("FunctionName")
public interface OAuth2AuthorizationGrantAuthorizationRepository
extends CrudRepository<AbstractGrantAuthorization, String> {

    <T extends OAuth2AuthorizationCodeGrantAuthorization> T findByState(String state);

    <T extends OAuth2AuthorizationCodeGrantAuthorization> T findByAuthorizationCode_TokenValue(String authorizationCode);

    <T extends OAuth2AuthorizationCodeGrantAuthorization> T findByStateOrAuthorizationCode_TokenValue(
            String state,
    String authorizationCode
    );

    <T extends AbstractGrantAuthorization> T findByAccessToken_TokenValue(String accessToken);

    <T extends AbstractGrantAuthorization> T findByRefreshToken_TokenValue(String refreshToken);

    <T extends AbstractGrantAuthorization> T findByAccessToken_TokenValueOrRefreshToken_TokenValue(
            String accessToken,
    String refreshToken
    );

    <T extends OidcAuthorizationCodeGrantAuthorization> T findByIdToken_TokenValue(String idToken);

    <T extends OAuth2DeviceCodeGrantAuthorization> T findByDeviceState(String deviceState);

    <T extends OAuth2DeviceCodeGrantAuthorization> T findByDeviceCode_TokenValue(String deviceCode);

    <T extends OAuth2DeviceCodeGrantAuthorization> T findByUserCode_TokenValue(String userCode);

    <T extends OAuth2DeviceCodeGrantAuthorization> T findByDeviceStateOrDeviceCode_TokenValueOrUserCode_TokenValue(
            String deviceState,
    String deviceCode,
    String userCode
    );
}
