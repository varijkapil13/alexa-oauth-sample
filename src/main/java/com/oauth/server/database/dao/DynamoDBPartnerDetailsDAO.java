/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Licensed under the Amazon Software License
 * http://aws.amazon.com/asl/
 */
package com.oauth.server.database.dao;

import com.oauth.server.database.modal.OAuthPartner;
import com.oauth.server.database.service.IOAuthPartner;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

/**
 * A DAO to access {@link OAuthPartner} in DynamoDB.
 *
 * @author Varij Kapil
 */
@Log4j2
public class DynamoDBPartnerDetailsDAO {
    
    private IOAuthPartner ioAuthPartner;
    
    public DynamoDBPartnerDetailsDAO(IOAuthPartner ioAuthPartner) {
        this.ioAuthPartner = ioAuthPartner;
    }
    
    /**
     * Returns an OAuthPartner object whose keys match those of the prototype key object given, or null if no such item exists.
     *
     * @param partnerId partnerId.
     * @return {@link OAuthPartner} or null if not found.
     */
    public OAuthPartner loadPartnerByPartnerId(@NonNull String partnerId) {
        return ioAuthPartner.findByPartnerId(partnerId).orElse(null);
    }

    /**
     * Scans through an Amazon DynamoDB table and returns the matching results as an unmodifiable list of instantiated objects.
     *
     * @return a list of {@link OAuthPartner}.
     */
    public List<OAuthPartner> listPartners() {
        return ioAuthPartner.findAll();
    }
    
    /**
     * Save the {@link OAuthPartner} provided.
     *
     * @param partner {@link OAuthPartner}
     */
    public void savePartner(OAuthPartner partner) {
        ioAuthPartner.save(partner);
    }
    
    /**
     * Delete the {@link OAuthPartner} by partnerId.
     *
     * @param partnerId partnerId
     */
    public void deletePartnerByPartnerId(@NonNull String partnerId) {
        Optional<OAuthPartner> partner = ioAuthPartner.findByPartnerId(partnerId);
        if (!partner.isPresent()) {
            log.error("partner {} already deleted.", partnerId);
        } else {
            ioAuthPartner.delete(partner.get());
        }
    }
}
