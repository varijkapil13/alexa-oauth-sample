package com.oauth.server.database.service;

import com.oauth.server.database.modal.OAuthPartner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOAuthPartner extends JpaRepository<OAuthPartner, Integer> {
  
  Optional<OAuthPartner> findByPartnerId(String partnerId);
}
