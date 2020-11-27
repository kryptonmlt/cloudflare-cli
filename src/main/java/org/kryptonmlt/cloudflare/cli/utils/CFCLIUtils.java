package org.kryptonmlt.cloudflare.cli.utils;

import java.io.File;

public class CFCLIUtils {

  private CFCLIUtils() {

  }

  public static void createExportFolder(String exportPath) {
    // create or delete contents of the export path
    File folder = new File(exportPath);
    if (folder.exists()) {
      for (File file : folder.listFiles()) {
        if (!file.isDirectory()) {
          file.delete();
        }
      }
    } else {
      folder.mkdir();
    }
  }
}
