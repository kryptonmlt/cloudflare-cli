package org.kryptonmlt.cloudflare.cli.printer;

import org.kryptonmlt.cloudflare.cli.dao.PrintObject;
import eu.roboflax.cloudflare.objects.dns.DNSRecord;
import eu.roboflax.cloudflare.objects.zone.Zone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLIPrinter {

    private CLIPrinter() {

    }

    public static String printZones(List<Zone> zones, boolean printRowNumber, String... columns) {
        PrintObject printObject = new PrintObject();
        for (Zone zone : zones) {
            Map row = new HashMap<>();
            row.put("name", zone.getName());
            row.put("nameservers", String.join(", ", zone.getNameServers()));
            row.put("id", String.join(", ", zone.getId()));
            printObject.getRows().add(row);
        }
        return print(printObject, columns, printRowNumber);
    }

    public static String printSubDomains(List<DNSRecord> objects, boolean printRowNumber,
                                         String... columns) {
        PrintObject printObject = new PrintObject();
        for (DNSRecord object : objects) {
            Map row = new HashMap<>();
            row.put("type", object.getType());
            row.put("name", object.getName());
            row.put("content", object.getContent());
            printObject.getRows().add(row);
        }
        return print(printObject, columns, printRowNumber);
    }

    public static String print(PrintObject printObject, String[] columns, boolean printRowNumber) {
        StringBuilder result = new StringBuilder();
        int counter = 0;
        // print data
        for (Map<String, String> row : printObject.getRows()) {
            StringBuilder sb = new StringBuilder();
            for (String key : row.keySet()) {
                if (columns == null) {
                    sb.append(row.get(key));
                    sb.append(" - ");
                    continue;
                }
                for (String column : columns) {
                    if (key.equalsIgnoreCase(column)) {
                        sb.append(row.get(key));
                        sb.append(" - ");
                        continue;
                    }
                }
            }
            if (sb.length() > 0) {
                if (printRowNumber) {
                    sb.append(counter);
                    counter++;
                }
            }
            //remove last -
            String line = sb.toString();
            if (line.lastIndexOf("-") == line.length() - 2) {
                line = line.substring(0, line.length() - 3);
            }
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }
}
