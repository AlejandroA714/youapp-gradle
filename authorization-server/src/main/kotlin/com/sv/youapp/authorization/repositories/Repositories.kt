package com.sv.youapp.authorization.repositories

import com.sv.youapp.authorization.entities.AuthenticationMethodEntity
import com.sv.youapp.authorization.entities.GrantTypeEntity
import com.sv.youapp.authorization.entities.PostLogoutRedirectUriEntity
import com.sv.youapp.authorization.entities.RedirectUriEntity
import com.sv.youapp.authorization.entities.ScopeEntity
import com.sv.youapp.authorization.entities.SettingsEntity
import com.sv.youapp.authorization.entities.TokenSettingsEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AuthenticationMethodRepository : JpaRepository<AuthenticationMethodEntity, Long>

interface GrantTypeRepository : JpaRepository<GrantTypeEntity, Long>

interface RedirectUriRepository : JpaRepository<RedirectUriEntity, Long>

interface PostLogoutRedirectUriRepository : JpaRepository<PostLogoutRedirectUriEntity, Long>

interface ScopeRepository : JpaRepository<ScopeEntity, Long>

interface SettingsRepository : JpaRepository<SettingsEntity, Long>

interface TokenSettingsRepository : JpaRepository<TokenSettingsEntity, Long>
