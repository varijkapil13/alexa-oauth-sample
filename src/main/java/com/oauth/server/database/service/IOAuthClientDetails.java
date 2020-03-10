package com.oauth.server.database.service;

import com.oauth.server.database.modal.OAuthClientDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOAuthClientDetails extends JpaRepository<OAuthClientDetails, Integer> {
  
  Optional<OAuthClientDetails> findByClientId(String clientId);
}
