package com.sv.youapp.component.authorization.repositories.jpa

import com.sv.youapp.component.authorization.entities.jpa.AuthenticationMethodEntity
import com.sv.youapp.component.authorization.entities.jpa.GrantTypeEntity
import com.sv.youapp.component.authorization.entities.jpa.PostLogoutRedirectUriEntity
import com.sv.youapp.component.authorization.entities.jpa.RedirectUriEntity
import com.sv.youapp.component.authorization.entities.jpa.ScopeEntity
import com.sv.youapp.component.authorization.entities.jpa.SettingsEntity
import com.sv.youapp.component.authorization.entities.jpa.TokenSettingsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthenticationMethodRepository : JpaRepository<AuthenticationMethodEntity, Long>

@Repository
interface GrantTypeRepository : JpaRepository<GrantTypeEntity, Long>

@Repository
interface RedirectUriRepository : JpaRepository<RedirectUriEntity, Long>

@Repository
interface PostLogoutRedirectUriRepository : JpaRepository<PostLogoutRedirectUriEntity, Long>

@Repository
interface ScopeRepository : JpaRepository<ScopeEntity, Long>

@Repository
interface SettingsRepository : JpaRepository<SettingsEntity, Long>

@Repository
interface TokenSettingsRepository : JpaRepository<TokenSettingsEntity, Long>
