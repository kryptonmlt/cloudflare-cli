package org.kryptonmlt.cloudflare.cli.commands;

import org.kryptonmlt.cloudflare.cli.cache.CloudflareCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class CacheCommands {

    @Autowired
    private CloudflareCache cloudflareCache;

    @ShellMethod("Reload the cloudflare-cli zone/records cache")
    public String reload() {
        return cloudflareCache.reloadCache();
    }
}
