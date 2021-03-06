/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Licensed under the Amazon Software License
 * http://aws.amazon.com/asl/
 */
package com.oauth.server.database.modal;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * An DTO object represents an OAuth access token.
 *
 * @author Varij Kapil
 */

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth_access_token")
public class OAuthAccessToken extends AuditModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    
    String tokenId;
    
    @Convert(converter = OAuth2AccessTokenConverter.class)
    OAuth2AccessToken token;
    
    String authenticationId;
    
    String clientId;
    
    String userName;
    
    @Convert(converter = OAuth2AuthenticationConverter.class)
    OAuth2Authentication authentication;
    
    String refreshToken;
    
}
