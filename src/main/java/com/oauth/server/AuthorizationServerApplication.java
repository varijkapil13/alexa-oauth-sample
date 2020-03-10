/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * Licensed under the Amazon Software License
 * http://aws.amazon.com/asl/
 */
package com.oauth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * This the entry point of the application.
 */
@SpringBootApplication
@Configuration
@EnableJpaAuditing
public class AuthorizationServerApplication {
  
  private final SampleDataLoader sampleDataLoader;
  
  public AuthorizationServerApplication(SampleDataLoader sampleDataLoader) {
    this.sampleDataLoader = sampleDataLoader;
  }
  
  public static void main(String[] args) {
    SpringApplication.run(AuthorizationServerApplication.class, args);
  }
  
  /**
   * TODO: Remove the sample data when testing is completed.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void applicationReady() {
    sampleDataLoader.loadSampleData();
  }
}
