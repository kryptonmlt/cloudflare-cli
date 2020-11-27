package org.kryptonmlt.cloudflare.cli.services;

import org.kryptonmlt.cloudflare.cli.cache.CloudflareCache;
import eu.roboflax.cloudflare.CloudflareAccess;
import eu.roboflax.cloudflare.objects.zone.Zone;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZoneService {

  @Autowired
  private CloudflareAccess cloudflareAccess;

  @Autowired
  private CloudflareCache cloudflareCache;

  public List<Zone> getAllDomains() {
    return cloudflareCache.getZones();
  }
}
