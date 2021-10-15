package org.kryptonmlt.cloudflare.cli.services;

import eu.roboflax.cloudflare.CloudflareAccess;
import eu.roboflax.cloudflare.CloudflareCallback;
import eu.roboflax.cloudflare.CloudflareRequest;
import eu.roboflax.cloudflare.CloudflareResponse;
import eu.roboflax.cloudflare.constants.Category;
import eu.roboflax.cloudflare.objects.dns.DNSRecord;
import eu.roboflax.cloudflare.objects.zone.Zone;
import org.kryptonmlt.cloudflare.cli.cache.CloudflareCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SubDomainService {

    @Autowired
    private CloudflareAccess cloudflareAccess;

    @Autowired
    private CloudflareCache cloudflareCache;

    public String updateRecord(String from, String to) {
        List<Zone> zones = cloudflareCache.getZones();
        for (Zone zone : zones) {
            List<DNSRecord> records = cloudflareCache.getDnsRecordsById().get(zone.getId());
            List<DNSRecord> recordsToChanges = new ArrayList<>();
            for (DNSRecord record : records) {
                if (record.getContent().equalsIgnoreCase(from)) {
                    recordsToChanges.add(record);
                }
            }
            for (DNSRecord recordToChange : recordsToChanges) {
                String old = recordToChange.getContent();
                new CloudflareRequest(Category.UPDATE_DNS_RECORD, cloudflareAccess)
                        .body("type", recordToChange.getType())
                        .body("name", recordToChange.getName())
                        .body("content", to)
                        .body("proxied", recordToChange.getProxied())
                        .identifiers(zone.getId())
                        .identifiers(recordToChange.getId())
                        .asObject(new CloudflareCallback<CloudflareResponse<DNSRecord>>() {
                            @Override
                            public void onSuccess(CloudflareResponse<DNSRecord> r) {
                                System.out.println("Switched: " + zone.getName() + " " + old + " to: " + r.getObject().getContent());
                            }

                            @Override
                            public void onFailure(Throwable t, int statusCode, String statusMessage,
                                                  Map<Integer, String> errors) {
                                System.out.println("Error updating zone: " + errors);
                            }
                        }, DNSRecord.class);
                cloudflareCache.clearCacheByZone(zone);
            }
        }
        return "Mass target switch finished";
    }

    public List<DNSRecord> getAllDnsRecordsFromDomain(String domain) {
        List<Zone> zones = cloudflareCache.getZones();
        for (Zone zone : zones) {
            if (zone.getName().contains(domain) || zone.getId().equals(domain)) {
                return cloudflareCache.getDnsRecordsById().get(zone.getId());
            }
        }
        System.err.println("Not able to find domain: " + domain);
        return new ArrayList<>();
    }

    public String getDNSForService(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Zone> zones = cloudflareCache.getZones();
        for (Zone zone : zones) {
            List<DNSRecord> records = cloudflareCache.getDnsRecordsById().get(zone.getId());
            for (DNSRecord record : records) {
                if (record.getName().contains(text) || record.getContent().contains(text)) {
                    stringBuilder.append(record.getType() + " , " + record.getName() + " , " + record.getContent() + "\n");
                }
            }
        }
        return stringBuilder.toString();
    }

    public String deleteRecord(String zoneToUse, String prefix) {
        List<Zone> zones = cloudflareCache.getZones();
        for (Zone zone : zones) {
            if (zone.getName().equalsIgnoreCase(zoneToUse) || zone.getId().equals(zoneToUse) || zoneToUse.equals("*")) {
                List<DNSRecord> records = cloudflareCache.getDnsRecordsById().get(zone.getId());
                String recordId = null;
                for (DNSRecord record : records) {
                    if (record.getName().startsWith(prefix)) {
                        recordId = record.getId();
                        break;
                    }
                }
                if (recordId != null) {
                    System.out.println("Prefix: " + prefix + " is being deleted for: " + zone.getName());
                    new CloudflareRequest(Category.DELETE_DNS_RECORD, cloudflareAccess)
                            .identifiers(zone.getId())
                            .identifiers(recordId)
                            .asObject(new CloudflareCallback<CloudflareResponse<DNSRecord>>() {
                                @Override
                                public void onSuccess(CloudflareResponse<DNSRecord> r) {
                                    System.out.println("success" + r.getObject());
                                }

                                @Override
                                public void onFailure(Throwable t, int statusCode, String statusMessage,
                                                      Map<Integer, String> errors) {
                                    System.out.println("Error creating zone: " + errors);
                                }
                            }, DNSRecord.class);
                    cloudflareCache.clearCacheByZone(zone);
                } else {
                    System.out.println("Prefix: " + prefix + " was not found for: " + zone.getName());
                }
            }
        }
        return "Zone not found";
    }

    public String saveDNS(String zoneToUse, String type, String prefix, String content, boolean proxied) {
        List<Zone> zones = cloudflareCache.getZones();
        for (Zone zone : zones) {
            if (zone.getName().equalsIgnoreCase(zoneToUse) || zone.getId().equals(zoneToUse) || zoneToUse.equals("*")) {
                List<DNSRecord> records = cloudflareCache.getDnsRecordsById().get(zone.getId());
                boolean missing = true;
                for (DNSRecord record : records) {
                    if (record.getName().startsWith(prefix)) {
                        missing = false;
                        break;
                    }
                }
                if (missing) {
                    System.out.println("Prefix: " + prefix + " is being created for: " + zone.getName());
                    new CloudflareRequest(Category.CREATE_DNS_RECORD, cloudflareAccess)
                            .body("type", type)
                            .body("name", prefix)
                            .body("content", content)
                            .body("proxied", proxied)
                            .identifiers(zone.getId())
                            .asObject(new CloudflareCallback<CloudflareResponse<DNSRecord>>() {
                                @Override
                                public void onSuccess(CloudflareResponse<DNSRecord> r) {
                                    System.out.println("success" + r.getObject());
                                }

                                @Override
                                public void onFailure(Throwable t, int statusCode, String statusMessage,
                                                      Map<Integer, String> errors) {
                                    System.out.println("Error creating zone: " + errors);
                                }
                            }, DNSRecord.class);
                    cloudflareCache.clearCacheByZone(zone);
                } else {
                    this.updateDNS(zone.getId(), type, prefix, content, proxied);
                    System.out.println("Prefix: " + prefix + " was already found for: " + zone.getName());
                }
            }
        }
        return "Finished Parsing Zones";
    }

    public String updateDNS(String zoneToUse, String type, String prefix, String content, boolean proxied) {
        List<Zone> zones = cloudflareCache.getZones();
        for (Zone zone : zones) {
            if (zoneToUse == null || zone.getName().equalsIgnoreCase(zoneToUse) || zone.getId().equals(zoneToUse)) {
                List<DNSRecord> records = cloudflareCache.getDnsRecordsById().get(zone.getId());
                boolean missing = true;
                String recordId = null;
                String old = "";
                for (DNSRecord record : records) {
                    if (record.getName().startsWith(prefix)) {
                        recordId = record.getId();
                        old = record.getName();
                        missing = false;
                        break;
                    }
                }
                if (missing) {
                    System.out.println("Skipping " + zone.getName() + " as name (" + prefix + ")not found");
                    continue;
                }

                final String oldRec = old;
                new CloudflareRequest(Category.UPDATE_DNS_RECORD, cloudflareAccess)
                        .body("type", type)
                        .body("name", prefix)
                        .body("content", content)
                        .body("proxied", proxied)
                        .identifiers(zone.getId())
                        .identifiers(recordId)
                        .asObject(new CloudflareCallback<CloudflareResponse<DNSRecord>>() {
                            @Override
                            public void onSuccess(CloudflareResponse<DNSRecord> r) {
                                System.out.println("Switching: " + oldRec + " to: " + r.getObject().getName());
                            }

                            @Override
                            public void onFailure(Throwable t, int statusCode, String statusMessage,
                                                  Map<Integer, String> errors) {
                                System.out.println("Error creating zone: " + errors);
                            }
                        }, DNSRecord.class);
                cloudflareCache.clearCacheByZone(zone);
            }
        }
        return "Finished dns mass update";
    }
}
