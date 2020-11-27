package org.kryptonmlt.cloudflare.cli.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrintObject {

  private List<Map<String, String>> rows = new ArrayList<>();

}
