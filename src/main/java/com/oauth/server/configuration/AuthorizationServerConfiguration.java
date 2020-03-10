/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Licensed under the Amazon Software License
 * http://aws.amazon.com/asl/
 */
package com.oauth.server.configuration;

import com.oauth.server.authentication.AuthenticationServiceProvider;
import com.oauth.server.database.dao.DynamoDBAuthorizationCodeDAO;
import com.oauth.server.database.dao.DynamoDBClientDetailsDAO;
import com.oauth.server.database.dao.DynamoDBPartnerDetailsDAO;
import com.oauth.server.database.dao.DynamoDBPartnerTokenDAO;
import com.oauth.server.database.dao.DynamoDBTokenDAO;
import com.oauth.server.database.service.IOAuthAccessToken;
import com.oauth.server.database.service.IOAuthClientDetails;
import com.oauth.server.database.service.IOAuthCode;
import com.oauth.server.database.service.IOAuthPartner;
import com.oauth.server.database.service.IOAuthPartnerToken;
import com.oauth.server.database.service.IOAuthRefreshToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * Configuration for authorization server.
 *
 * @author Varij Kapil
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
  
  private final IOAuthPartnerToken ioAuthPartnerToken;
  
  private final IOAuthAccessToken ioAuthAccessToken;
  private final IOAuthRefreshToken ioAuthRefreshToken;
  
  private final IOAuthCode ioAuthCode;
  
  private final IOAuthClientDetails ioAuthClientDetails;
  
  private final IOAuthPartner ioAuthPartner;
  
  public AuthorizationServerConfiguration(IOAuthPartnerToken ioAuthPartnerToken, IOAuthAccessToken ioAuthAccessToken,
      IOAuthRefreshToken ioAuthRefreshToken, IOAuthCode ioAuthCode, IOAuthClientDetails ioAuthClientDetails, IOAuthPartner ioAuthPartner) {
    this.ioAuthPartnerToken = ioAuthPartnerToken;
    this.ioAuthAccessToken = ioAuthAccessToken;
    this.ioAuthRefreshToken = ioAuthRefreshToken;
    this.ioAuthCode = ioAuthCode;
    this.ioAuthClientDetails = ioAuthClientDetails;
    this.ioAuthPartner = ioAuthPartner;
  }
  
  @Bean
  @Scope(proxyMode = ScopedProxyMode.INTERFACES)
  public ClientTokenServices clientTokenServices() {
    return
        new DynamoDBPartnerTokenDAO(ioAuthPartnerToken);
  }
  
  @Bean
  public DynamoDBPartnerDetailsDAO dynamoDBPartnerDetailsService() {
    return new DynamoDBPartnerDetailsDAO(ioAuthPartner);
  }
  
  @Bean
  public DynamoDBPartnerTokenDAO dynamoDBPartnerTokenService() {
    return new DynamoDBPartnerTokenDAO(ioAuthPartnerToken);
  }
  
  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
    oauthServer.allowFormAuthenticationForClients();
  }
  
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(dynamoDBClientDetailsService());
  }
  
  @Bean
  public DynamoDBClientDetailsDAO dynamoDBClientDetailsService() {
    return new DynamoDBClientDetailsDAO(ioAuthClientDetails, passwordEncoder());
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
    endpoints
        .approvalStore(approvalStore())
        .authorizationCodeServices(authorizationCodeServices())
        .tokenStore(tokenStore())
        .authenticationManager(authenticationServiceProvider())
        .userDetailsService(authenticationServiceProvider());
  }
  
  @Bean
  @Scope(proxyMode = ScopedProxyMode.INTERFACES)
  public ApprovalStore approvalStore() {
    TokenApprovalStore approvalStore = new TokenApprovalStore();
    approvalStore.setTokenStore(tokenStore());
    return approvalStore;
  }
  
  @Bean
  @Scope(proxyMode = ScopedProxyMode.INTERFACES)
  public AuthorizationCodeServices authorizationCodeServices() {
    return new DynamoDBAuthorizationCodeDAO(ioAuthCode);
  }
  
  @Bean
  @Scope(proxyMode = ScopedProxyMode.INTERFACES)
  public TokenStore tokenStore() {
    return new DynamoDBTokenDAO(ioAuthAccessToken, ioAuthRefreshToken);
  }
  
  @Bean
  public AuthenticationServiceProvider authenticationServiceProvider() {
    return new AuthenticationServiceProvider(passwordEncoder());
  }
}
