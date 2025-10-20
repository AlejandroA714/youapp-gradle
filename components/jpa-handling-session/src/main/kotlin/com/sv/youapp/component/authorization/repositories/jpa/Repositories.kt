package com.sv.youapp.component.authorization.repositories.jpa

import com.sv.youapp.component.authorization.entities.jpa.AuthenticationMethodEntity
import com.sv.youapp.component.authorization.entities.jpa.GrantTypeEntity
import com.sv.youapp.component.authorization.entities.jpa.PostLogoutRedirectUriEntity
import com.sv.youapp.component.authorization.entities.jpa.RedirectUriEntity
import com.sv.youapp.component.authorization.entities.jpa.ScopeEntity
import com.sv.youapp.component.authorization.entities.jpa.SettingsEntity
import com.sv.youapp.component.authorization.entities.jpa.TokenSettingsEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AuthenticationMethodRepository : JpaRepository<AuthenticationMethodEntity, Long>

interface GrantTypeRepository : JpaRepository<GrantTypeEntity, Long>

interface RedirectUriRepository : JpaRepository<RedirectUriEntity, Long>

interface PostLogoutRedirectUriRepository : JpaRepository<PostLogoutRedirectUriEntity, Long>

interface ScopeRepository : JpaRepository<ScopeEntity, Long>

interface SettingsRepository : JpaRepository<SettingsEntity, Long>

interface TokenSettingsRepository : JpaRepository<TokenSettingsEntity, Long>
