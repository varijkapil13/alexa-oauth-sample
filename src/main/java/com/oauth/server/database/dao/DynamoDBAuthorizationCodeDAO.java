/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Licensed under the Amazon Software License
 * http://aws.amazon.com/asl/
 */
package com.oauth.server.database.dao;

import com.oauth.server.database.modal.OAuthCode;
import com.oauth.server.database.service.IOAuthCode;
import java.util.Optional;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

/**
 * A DAO to access {@link OAuth2Authentication} in DynamoDB.
 *
 * @author Varij Kapil
 */
public class DynamoDBAuthorizationCodeDAO extends RandomValueAuthorizationCodeServices {
  
  private IOAuthCode ioAuthCode;
  
  public DynamoDBAuthorizationCodeDAO(IOAuthCode ioAuthCode) {
    this.ioAuthCode = ioAuthCode;
  }
  
  /**
   * Store the authorization code for a authenticated user.
   *
   * @param code authorization code.
   * @param authentication authentication for the user.
   */
  @Override
  protected void store(String code, OAuth2Authentication authentication) {
    OAuthCode oAuthCode = OAuthCode.builder().code(code).authentication(authentication).build();
    
    ioAuthCode.save(oAuthCode);
  }
  
  /**
   * Remove/Invalidate the authorization code.
   *
   * @param code authorization code.
   * @return user authentication.
   */
  @Override
  public OAuth2Authentication remove(String code) {
    Optional<OAuthCode> oAuthCode = ioAuthCode.findByCode(code);
    
    if (!oAuthCode.isPresent()) {
      return null;
    }
    OAuthCode codeFound = oAuthCode.get();
    ioAuthCode.delete(codeFound);
    
    return codeFound.getAuthentication();
  }
}
