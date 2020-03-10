/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Licensed under the Amazon Software License
 * http://aws.amazon.com/asl/
 */

package com.oauth.server.database.dao;

import com.oauth.server.database.modal.OAuthClientDetails;
import com.oauth.server.database.service.IOAuthClientDetails;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.util.StringUtils;

/**
 * A DAO to access {@link ClientDetails} in DynamoDB.
 *
 * @author Varij Kapil
 */
@RequiredArgsConstructor
@Log4j2
public class DynamoDBClientDetailsDAO implements ClientDetailsService, ClientRegistrationService {
  
  private final IOAuthClientDetails ioAuthClientDetails;
  private final PasswordEncoder passwordEncoder;
  
  /**
   * Load {@link ClientDetails} by clientId provided.
   *
   * @param clientId client id.
   * @return client details.
   *
   * @throws NoSuchClientException if clientId not found.
   */
  @Override
  public ClientDetails loadClientByClientId(String clientId) throws NoSuchClientException {
    return ioAuthClientDetails.findByClientId(clientId)
        .map(OAuthClientDetails::toClientDetails)
        .orElseThrow(() -> new NoSuchClientException("Client: " + clientId + " not found."));
  }
  
  /**
   * Add a new {@link ClientDetails} into Database.
   *
   * @param clientDetails client details to be added.
   * @throws ClientAlreadyExistsException if client details already exists.
   */
  @Override
  public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
    
    Optional<OAuthClientDetails> oAuthClientDetails = ioAuthClientDetails.findByClientId(clientDetails.getClientId());
    
    if (oAuthClientDetails.isPresent()) {
      throw new ClientAlreadyExistsException("client already exists: " + clientDetails.getClientId());
    }
    
    addOrUpdateClientDetails(clientDetails);
  }
  
  /**
   * Update an existing {@link ClientDetails} in database.
   *
   * @param clientDetails client details.
   * @throws NoSuchClientException if client not exit.
   */
  @Override
  public void updateClientDetails(@NonNull ClientDetails clientDetails) throws NoSuchClientException {
    Optional<OAuthClientDetails> oAuthClientDetails = ioAuthClientDetails.findByClientId(clientDetails.getClientId());
    
    if (!oAuthClientDetails.isPresent()) {
      throw new NoSuchClientException("client not exists: " + clientDetails.getClientId());
    }
    
    addOrUpdateClientDetails(clientDetails);
  }
  
  /**
   * Update the client secret for a specific client id.
   *
   * @param clientId client id.
   * @param secret client secret.
   * @throws NoSuchClientException if client not exist.
   */
  @Override
  public void updateClientSecret(@NonNull String clientId, @NonNull String secret) throws NoSuchClientException {
    Optional<OAuthClientDetails> oAuthClientDetails = ioAuthClientDetails.findByClientId(clientId);
    
    if (!oAuthClientDetails.isPresent()) {
      throw new NoSuchClientException("client not exists: " + clientId);
    }
    
    OAuthClientDetails updatedItem = oAuthClientDetails.get().toBuilder().clientSecret(passwordEncoder.encode(secret))
        .build();
    ioAuthClientDetails.save(updatedItem);
  }
  
  /**
   * Remove a specific client details by clientId.
   *
   * @param clientId client id.
   */
  @Override
  public void removeClientDetails(@NonNull String clientId) {
    Optional<OAuthClientDetails> oAuthClientDetails = ioAuthClientDetails.findByClientId(clientId);
    
    if (!oAuthClientDetails.isPresent()) {
      log.error("clientId {} already deleted.", clientId);
    } else {
      ioAuthClientDetails.delete(oAuthClientDetails.get());
    }
  }
  
  /**
   * List all the oauth clients in database by scanning the database.
   *
   * @return all client details.
   */
  @Override
  public List<ClientDetails> listClientDetails() {
    return ioAuthClientDetails.findAll()
        .stream()
        .map(OAuthClientDetails::toClientDetails)
        .collect(Collectors.toList());
  }
  
  /**
   * Add or update a client details in database.
   *
   * @param clientDetails client details.
   */
  public void addOrUpdateClientDetails(@NonNull ClientDetails clientDetails) {
    List<String> autoApproveList = clientDetails.getScope().stream()
        .filter(clientDetails::isAutoApprove)
        .collect(Collectors.toList());
    
    OAuthClientDetails oAuthClientDetails = OAuthClientDetails
        .builder()
        .clientId(clientDetails.getClientId())
        .authorities(StringUtils.collectionToCommaDelimitedString(clientDetails.getAuthorities()))
        .authorizedGrantTypes(
            StringUtils.collectionToCommaDelimitedString(clientDetails.getAuthorizedGrantTypes()))
        .scopes(StringUtils.collectionToCommaDelimitedString(clientDetails.getScope()))
        .webServerRedirectUri(
            StringUtils.collectionToCommaDelimitedString(clientDetails.getRegisteredRedirectUri()))
        .accessTokenValidity(clientDetails.getAccessTokenValiditySeconds())
        .refreshTokenValidity(clientDetails.getRefreshTokenValiditySeconds())
        .autoapprove(StringUtils.collectionToCommaDelimitedString(autoApproveList))
        .build();
    
    ioAuthClientDetails.save(oAuthClientDetails);
  }
}
