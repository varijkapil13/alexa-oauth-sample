/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Licensed under the Amazon Software License
 * http://aws.amazon.com/asl/
 */
package com.oauth.server.database.dao;

import com.oauth.server.database.modal.OAuthAccessToken;
import com.oauth.server.database.modal.OAuthRefreshToken;
import com.oauth.server.database.service.IOAuthAccessToken;
import com.oauth.server.database.service.IOAuthRefreshToken;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * A DAO to access {@link OAuth2AccessToken} in DynamoDB. This is an implementation of token services that stores tokens in
 * DynamoDB. This was primarily based off of the functionality of the {@link JdbcTokenStore}.
 *
 * @author Varij Kapil
 */
public class DynamoDBTokenDAO implements TokenStore {
  
  private final AuthenticationKeyGenerator authenticationKeyGenerator;
  
  private final IOAuthAccessToken ioAuthAccessToken;
  private final IOAuthRefreshToken ioAuthRefreshToken;
  
  public DynamoDBTokenDAO(IOAuthAccessToken ioAuthAccessToken, IOAuthRefreshToken ioAuthRefreshToken) {
    this.ioAuthAccessToken = ioAuthAccessToken;
    this.ioAuthRefreshToken = ioAuthRefreshToken;
    this.authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
  }
  
  public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
    return readAuthentication(token.getValue());
  }
  
  public OAuth2Authentication readAuthentication(String token) {
    String tokenId = extractTokenKey(token);
    return ioAuthAccessToken.findByTokenId(tokenId)
        .map(OAuthAccessToken::getAuthentication)
        .orElse(null);
  }
  
  public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
    String refreshToken = null;
    if (token.getRefreshToken() != null) {
      refreshToken = token.getRefreshToken().getValue();
    }
    
    OAuthAccessToken accessToken = OAuthAccessToken.builder()
        .tokenId(extractTokenKey(token.getValue()))
        .token(token)
        .authenticationId(authenticationKeyGenerator.extractKey(authentication))
        .authentication(authentication)
        .clientId(authentication.getOAuth2Request().getClientId())
        .refreshToken(extractTokenKey(refreshToken))
        .userName(StringUtils.isNotBlank(authentication.getName()) ? authentication.getName() : "#")
        .build();
    
    ioAuthAccessToken.save(accessToken);
  }
  
  public OAuth2AccessToken readAccessToken(String tokenValue) {
    String tokenId = extractTokenKey(tokenValue);
    
    return ioAuthAccessToken.findByTokenId(tokenId)
        .map(OAuthAccessToken::getToken)
        .orElse(null);
  }
  
  public void removeAccessToken(OAuth2AccessToken token) {
    removeAccessToken(token.getValue());
  }
  
  public void removeAccessToken(String tokenValue) {
    String tokenId = extractTokenKey(tokenValue);
    OAuthAccessToken itemToDelete = OAuthAccessToken.builder().tokenId(tokenId).build();
    
    ioAuthAccessToken.delete(itemToDelete);
  }
  
  public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
    
    OAuthRefreshToken itemToSave = OAuthRefreshToken.builder()
        .tokenId(extractTokenKey(refreshToken.getValue()))
        .token(refreshToken)
        .authentication(authentication)
        .build();
    
    ioAuthRefreshToken.save(itemToSave);
  }
  
  public OAuth2RefreshToken readRefreshToken(String token) {
    String tokenId = extractTokenKey(token);
    
    return ioAuthRefreshToken.findByTokenId(tokenId)
        .map(OAuthRefreshToken::getToken)
        .orElse(null);
  }
  
  public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
    return readAuthenticationForRefreshToken(token.getValue());
  }
  
  public void removeRefreshToken(OAuth2RefreshToken token) {
    removeRefreshToken(token.getValue());
  }
  
  public void removeRefreshToken(String token) {
    String tokenId = extractTokenKey(token);
    OAuthRefreshToken itemToDelete = OAuthRefreshToken.builder().tokenId(tokenId).build();
    
    ioAuthRefreshToken.delete(itemToDelete);
  }
  
  public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
    removeAccessTokenUsingRefreshToken(refreshToken.getValue());
  }
  
  public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
    String authenticationId = authenticationKeyGenerator.extractKey(authentication);
    List<OAuthAccessToken> accessTokens = ioAuthAccessToken.findAllByAuthenticationId(authenticationId);
    
    return accessTokens.stream().findAny().map(OAuthAccessToken::getToken).orElse(null);
    
    
  }
  
  public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
    List<OAuthAccessToken> accessTokens = ioAuthAccessToken.findAllByClientIdAndUserName(clientId, userName);
    return accessTokens.stream().map(OAuthAccessToken::getToken).collect(Collectors.toList());
  }
  
  public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
    
    List<OAuthAccessToken> accessTokens = ioAuthAccessToken.findAllByClientId(clientId);
    return accessTokens.stream().map(OAuthAccessToken::getToken).collect(Collectors.toList());
  }
  
  public void removeAccessTokenUsingRefreshToken(String refreshToken) {
    String refreshTokenId = extractTokenKey(refreshToken);
    
    List<OAuthAccessToken> accessTokens = ioAuthAccessToken.findAllByRefreshToken(refreshTokenId);
    
    ioAuthAccessToken.deleteInBatch(accessTokens);
  }
  
  public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
    String tokenId = extractTokenKey(value);
    
    return ioAuthRefreshToken.findByTokenId(tokenId)
        .map(OAuthRefreshToken::getAuthentication)
        .orElse(null);
  }
  
  protected String extractTokenKey(String value) {
    if (value == null) {
      return null;
    }
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
    }
    
    byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
    return String.format("%032x", new BigInteger(1, bytes));
  }
  
}
