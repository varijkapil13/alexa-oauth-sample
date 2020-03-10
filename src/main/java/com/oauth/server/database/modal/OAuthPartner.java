/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Licensed under the Amazon Software License
 * http://aws.amazon.com/asl/
 */
package com.oauth.server.database.modal;

import java.util.ArrayList;
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
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.StringUtils;

/**
 * An DTO object represents an OAuth partner.
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
@Table(name = "oauth_partner")
public class OAuthPartner extends AuditModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    
    private String partnerId;
    
    private String clientId;
    
    private String clientSecret;
    
    private String accessTokenUri;
    
    private String userAuthorizationUri;

    private String preEstablishedRedirectUri;

    private String scopes;

    public OAuth2ProtectedResourceDetails toProtectedResourceDetails() {
        AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();

        resourceDetails.setId(partnerId);
        resourceDetails.setClientId(clientId);
        resourceDetails.setClientSecret(clientSecret);

        resourceDetails.setAccessTokenUri(accessTokenUri);
        resourceDetails.setUserAuthorizationUri(userAuthorizationUri);
        resourceDetails.setPreEstablishedRedirectUri(preEstablishedRedirectUri);

        if (scopes != null) {
            resourceDetails.setScope(new ArrayList<>(StringUtils.commaDelimitedListToSet(scopes)));
        }

        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.header);
        resourceDetails.setAuthenticationScheme(AuthenticationScheme.header);
        resourceDetails.setTokenName(OAuth2AccessToken.ACCESS_TOKEN);

        return resourceDetails;
    }
}
