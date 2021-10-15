package org.kryptonmlt.cloudflare.cli.commands;

import org.kryptonmlt.cloudflare.cli.printer.CLIPrinter;
import org.kryptonmlt.cloudflare.cli.services.SubDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class DNSCommands {

    @Autowired
    private SubDomainService subDomainService;

    @ShellMethod(value = "Returns any matching dns records to the query")
    public String find(
            @ShellOption() String query) {
        return subDomainService.getDNSForService(query);
    }

    @ShellMethod(value = "Creates a dns record under the matching zone")
    public String create(
            @ShellOption(help = "DNS record will be saved under this Zone id or name") String zone,
            @ShellOption(help = "dns record type example A CNAME TXT etc.. check Cloudflare documentation") String type,
            @ShellOption(help = "dns record name") String name,
            @ShellOption(help = "Target of dns record") String target,
            @ShellOption(defaultValue = "true") String proxied) {
        return subDomainService.saveDNS(zone, type, name, target, "true".equalsIgnoreCase(proxied));
    }

    @ShellMethod(value = "Delete a dns record under the matching zone")
    public String deleteDns(
            @ShellOption(help = "DNS record will be saved under this Zone id or name (* for everything)") String zone,
            @ShellOption(help = "dns record name") String prefix) {
        return subDomainService.deleteRecord(zone, prefix);
    }

    @ShellMethod(value = "Updates all matching dns records (checks that name starts the same) under the cloudflare account")
    public String update(
            @ShellOption(help = "dns record type example A CNAME TXT etc.. check Cloudflare documentation") String type,
            @ShellOption(help = "dns record name") String name,
            @ShellOption(help = "Target of dns record") String target,
            @ShellOption(defaultValue = "true") String proxied,
            @ShellOption(defaultValue = "false") String force) {
        if (force.equalsIgnoreCase("false")) {
            return "This is not a safe command please use --force true to acknowledge that you know what you are doing, otherwise use 'help update' to know more";
        }
        return subDomainService.updateDNS(null, type, name, target, "true".equalsIgnoreCase(proxied));
    }

    @ShellMethod(value = "Updates matching dns records under one zone")
    public String singleUpdate(
            @ShellOption(help = "DNS record will be saved under this Zone id or name") String zone,
            @ShellOption(help = "dns record type example A CNAME TXT etc.. check Cloudflare documentation") String type,
            @ShellOption(help = "dns record name") String name,
            @ShellOption(help = "Target of dns record") String target,
            @ShellOption(defaultValue = "true") String proxied,
            @ShellOption(defaultValue = "false") String force) {
        if (force.equalsIgnoreCase("false")) {
            return "This is not a safe command please use --force true to acknowledge that you know what you are doing, otherwise use 'help single-update' to know more";
        }
        return subDomainService.updateDNS(zone, type, name, target, "true".equalsIgnoreCase(proxied));
    }

    @ShellMethod(value = "Lists all dns records under the matching zone")
    public String list(
            @ShellOption() String domain,
            @ShellOption(defaultValue = "true") String showId) {
        return CLIPrinter
                .printSubDomains(subDomainService.getAllDnsRecordsFromDomain(domain), "true".equalsIgnoreCase(showId), null);
    }

    @ShellMethod(value = "Switches the target in all dns records that match within all domains")
    public String switchTarget(
            @ShellOption() String from,
            @ShellOption() String to,
            @ShellOption(defaultValue = "false") String force) {
        if (force.equalsIgnoreCase("false")) {
            return "This is not a safe command please use --force true to acknowledge that you know what you are doing, otherwise use help 'switch-target' to know more";
        }
        return subDomainService.updateRecord(from, to);
    }


}
