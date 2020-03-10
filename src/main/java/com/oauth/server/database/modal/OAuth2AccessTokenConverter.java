package com.oauth.server.database.modal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Converter
public class OAuth2AccessTokenConverter implements AttributeConverter<OAuth2AccessToken, String> {
  
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  @Override
  public String convertToDatabaseColumn(OAuth2AccessToken oAuth2AccessToken) {
    try {
      return objectMapper.writeValueAsString(oAuth2AccessToken);
    } catch (JsonProcessingException e) {
      return "";
    }
  }
  
  @Override
  public OAuth2AccessToken convertToEntityAttribute(String s) {
    try {
      return objectMapper.readValue(s, OAuth2AccessToken.class);
    } catch (IOException e) {
      return null;
    }
  }
}
