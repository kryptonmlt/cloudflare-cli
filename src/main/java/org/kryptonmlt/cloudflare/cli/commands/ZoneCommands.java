package org.kryptonmlt.cloudflare.cli.commands;

import org.kryptonmlt.cloudflare.cli.printer.CLIPrinter;
import org.kryptonmlt.cloudflare.cli.services.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ZoneCommands {

    @Autowired
    private ZoneService zoneService;

    @ShellMethod("Prints all zones in account")
    public String zone(
            @ShellOption(defaultValue = "true") String details,
            @ShellOption(defaultValue = "false") String showId) {
        // invoke service
        if ("true".equalsIgnoreCase(details)) {
            return CLIPrinter.printZones(zoneService.getAllDomains(), "true".equalsIgnoreCase(showId), null);
        } else {
            String[] columns = {"name"};
            return CLIPrinter.printZones(zoneService.getAllDomains(), "true".equalsIgnoreCase(showId), columns);
        }
    }
}
