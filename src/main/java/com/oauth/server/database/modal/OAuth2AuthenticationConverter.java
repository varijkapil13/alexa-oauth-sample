/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Licensed under the Amazon Software License
 * http://aws.amazon.com/asl/
 */
package com.oauth.server.database.modal;

import java.util.Base64;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * This is a DynamoDBTypeConverter that converts between OAuth2Authentication to String.
 *
 * @author Varij Kapil
 */
@Converter
public class OAuth2AuthenticationConverter implements AttributeConverter<OAuth2Authentication, String> {
    
    
    @Override
    public String convertToDatabaseColumn(OAuth2Authentication authentication) {
        byte[] bytes = SerializationUtils.serialize(authentication);
        return new String(Base64.getEncoder().encode(bytes));
    }
    
    @Override
    public OAuth2Authentication convertToEntityAttribute(String authenticationString) {
        byte[] bytes = Base64.getDecoder().decode(authenticationString.getBytes());
        return SerializationUtils.deserialize(bytes);
        
    }
}
