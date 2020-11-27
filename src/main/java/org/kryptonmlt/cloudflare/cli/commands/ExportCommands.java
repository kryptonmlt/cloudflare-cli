package org.kryptonmlt.cloudflare.cli.commands;

import org.kryptonmlt.cloudflare.cli.cache.CloudflareCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ExportCommands {

    @Autowired
    private CloudflareCache cloudflareCache;

    @Value("${cloudflare.email}")
    private String email;
    @Value("${cloudflare.apikey}")
    private String apiKey;

    @ShellMethod("Reload the cloudflare-cli zone/records cache")
    public String wrangler(
            @ShellOption(defaultValue = "", help = "route subdomain") String preDomain,
            @ShellOption(defaultValue = "", help = "route after domain") String postDomain) {
        return cloudflareCache.exportWrangler(preDomain, postDomain);
    }

    @ShellMethod("Reload the cloudflare-cli zone/records cache")
    public String WorkerRoutes(
            @ShellOption(help = "Name of Cloudflare worker to use") String workerScript,
            @ShellOption(defaultValue = "", help = "route subdomain") String preDomain,
            @ShellOption(defaultValue = "", help = "route after domain") String postDomain) {
        return cloudflareCache.exportRoutePost(email, apiKey, workerScript, preDomain, postDomain);
    }
}
