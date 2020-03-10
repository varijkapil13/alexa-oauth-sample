package com.oauth.server.database.service;

import com.oauth.server.database.modal.OAuthRefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOAuthRefreshToken extends JpaRepository<OAuthRefreshToken, Integer> {
  
  Optional<OAuthRefreshToken> findByTokenId(String tokenId);
  
}
