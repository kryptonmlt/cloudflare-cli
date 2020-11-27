package org.kryptonmlt.cloudflare.cli.config;

import eu.roboflax.cloudflare.CloudflareAccess;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CFConfig {

  @Bean
  public CloudflareAccess getCfAccess(@Value("${cloudflare.email}") String email,
      @Value("${cloudflare.apikey}") String apiKey) {

    return new CloudflareAccess(apiKey, email);
  }
}
