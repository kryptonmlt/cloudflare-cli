package org.kryptonmlt.cloudflare.cli.utils;

public class ZoneUtils {

    private ZoneUtils() {

    }

    public static String getWranglerSite(String name, String zoneId, String accountId, String route) {
        return "name = \"" + name + "\"\ntype = \"javascript\"\nzone_id = \"" + zoneId
                + "\"\nprivate = true\naccount_id = \"" + accountId + "\"\nroute = \"" + route + "\"";
    }

    public static String getWorkerRoutePost(String zoneId, String email, String apiKey, String route,
                                            String script) {
        return "echo \"pushing route: " + route + " to worker: " + script + "\"\ncurl -X POST \"https://api.cloudflare.com/client/v4/zones/" + zoneId
                + "/workers/routes\" -H \"X-Auth-Email: " + email
                + "\" -H \"X-Auth-Key: " + apiKey + "\" -H \"Content-Type: application/json\" --data '{\"pattern\":\""
                + route + "\",\"script\":\"" + script + "\"}' || true\n";
    }
}