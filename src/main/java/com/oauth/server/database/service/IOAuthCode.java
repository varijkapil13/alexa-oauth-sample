package com.oauth.server.database.service;

import com.oauth.server.database.modal.OAuthCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOAuthCode extends JpaRepository<OAuthCode, Integer> {
  
  Optional<OAuthCode> findByCode(String code);
  
}
