package org.kryptonmlt.cloudflare.cli.cache;

import org.kryptonmlt.cloudflare.cli.utils.CFCLIUtils;
import org.kryptonmlt.cloudflare.cli.utils.ZoneUtils;
import eu.roboflax.cloudflare.CloudflareAccess;
import eu.roboflax.cloudflare.CloudflareRequest;
import eu.roboflax.cloudflare.CloudflareResponse;
import eu.roboflax.cloudflare.Pagination;
import eu.roboflax.cloudflare.constants.Category;
import eu.roboflax.cloudflare.objects.dns.DNSRecord;
import eu.roboflax.cloudflare.objects.zone.Zone;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CloudflareCache {

    @Autowired
    private CloudflareAccess cloudflareAccess;

    private List<Zone> zones;

    private Map<String, List<DNSRecord>> dnsRecordsById;

    private Map<String, List<DNSRecord>> dnsRecordsByDomain;

    private Pagination pagination = Pagination.builder().build();

    private DecimalFormat df = new DecimalFormat();

    private final String exportPath = "export/";

    @Value("${cloudflare.accountId}")
    private String accountId;

    @Value("${cloudflare.parallel}")
    private int parallel;
    private static int counter = 0;

    @PostConstruct
    public void init() {
        if (cloudflareAccess.getXAuthEmail().isEmpty()) {
            System.out.println("No Auth Email given in properties file");
        } else {
            pagination.setPerPage(500);
            df.setMaximumFractionDigits(2);
            reloadCache();
        }
    }

    public String reloadCache() {
        System.out.println("Started loading ZONE cache..");
        zones = this.getAllDomains();
        System.out.println("Finished warming up ZONE (" + zones.size() + ") cache..");
        System.out.println("Started loading DNS cache.. This may take a some minutes if you have 100+ domains");
        dnsRecordsById = new HashMap<>();
        dnsRecordsByDomain = new HashMap<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool(parallel);
        try {
            if (zones != null) {
                forkJoinPool.submit(() ->
                        zones.stream().parallel().forEach(zone -> clearCacheByZone(zone, zones.size(), true))
                ).get();
            }
            System.out.print("\r100%   ");
            System.out.println("");
            counter = 0;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to reload cache - best to close application";
        }
        return "Finished warming up DNS cache";
    }

    public List<Zone> getZones() {
        return zones;
    }

    public Map<String, List<DNSRecord>> getDnsRecordsById() {
        return dnsRecordsById;
    }

    public void clearCacheByZone(Zone zone) {
        clearCacheByZone(zone, 0, false);
    }

    public void clearCacheByZone(Zone zone, int size, boolean display) {
        if (display) {
            float per = counter / (zones.size() * 1f) * 100;
            System.out.print("\r" + df.format(per) + " %   ");
            counter++;
        }
        List<DNSRecord> records = this.getAllDnsRecords(zone.getId());
        dnsRecordsById.put(zone.getId(), records);
        dnsRecordsById.put(zone.getName(), records);
    }

    public Map<String, List<DNSRecord>> getDnsRecordsByDomain() {
        return dnsRecordsByDomain;
    }

    private List<DNSRecord> getAllDnsRecords(String zoneId) {
        CloudflareResponse<List<DNSRecord>> response = new CloudflareRequest(
                Category.LIST_DNS_RECORDS,
                cloudflareAccess).pagination(pagination).identifiers(zoneId)
                .asObjectList(DNSRecord.class);
        return response.getObject();
    }

    private List<Zone> getAllDomains() {
        CloudflareResponse<List<Zone>> response = new CloudflareRequest(Category.LIST_ZONES,
                cloudflareAccess).pagination(pagination)
                .asObjectList(Zone.class);
        return response.getObject();
    }

    public String exportWrangler(String pre, String post) {
        CFCLIUtils.createExportFolder(exportPath);
        //output wrangler.toml files
        for (Zone zone : zones) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(exportPath + zone.getId() + "-wrangler.toml"));
                bw.write(ZoneUtils
                        .getWranglerSite(zone.getName().replaceAll("\\.", "-") + "-worker", zone.getId(),
                                accountId,
                                pre + zone.getName() + post));
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "Wrangler export failed: " + e.getLocalizedMessage();
            }
        }
        return "Wrangler export success: " + exportPath;
    }

    public String exportRoutePost(String email, String apiKey, String workerScript, String preDomain, String postDomain) {
        CFCLIUtils.createExportFolder(exportPath);
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(exportPath + "deploy_routes"));
            for (Zone zone : zones) {
                bw.write(ZoneUtils
                        .getWorkerRoutePost(zone.getId(), email, apiKey, preDomain + zone.getName() + postDomain,
                                workerScript));
            }
            bw.flush();
            bw.close();
            return "Worker Routes export success: " + exportPath + "deploy_routes";
        } catch (IOException e) {
            e.printStackTrace();
            return "Worker Routes export failure: " + e.getLocalizedMessage();
        }
    }
}
