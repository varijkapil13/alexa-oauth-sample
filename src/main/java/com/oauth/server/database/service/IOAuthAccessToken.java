package com.oauth.server.database.service;

import com.oauth.server.database.modal.OAuthAccessToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOAuthAccessToken extends JpaRepository<OAuthAccessToken, Integer> {
  
  Optional<OAuthAccessToken> findByTokenId(String tokenId);
  
  List<OAuthAccessToken> findAllByAuthenticationId(String authenticationId);
  
  List<OAuthAccessToken> findAllByRefreshToken(String refreshToken);
  
  List<OAuthAccessToken> findAllByClientId(String clientId);
  
  List<OAuthAccessToken> findAllByClientIdAndUserName(String clientId, String userName);
  
}
