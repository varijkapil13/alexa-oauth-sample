package com.oauth.server.database.service;

import com.oauth.server.database.modal.OAuthPartnerToken;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOAuthPartnerToken extends JpaRepository<OAuthPartnerToken, Integer> {
  
  List<OAuthPartnerToken> findAllByAuthenticationId(String authenticationId);
  
}
