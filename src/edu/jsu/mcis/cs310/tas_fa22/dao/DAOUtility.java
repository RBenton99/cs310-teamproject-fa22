package edu.jsu.mcis.cs310.tas_fa22.dao;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.*;

import edu.jsu.mcis.cs310.tas_fa22.Punch;

/**
 * 
 * Utility class for DAOs. This is a final, non-constructable class containing
 * common DAO logic and other repeated and/or standardized code, refactored into
 * individual static methods.
 * 
 */
public final class DAOUtility {

  public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist) {
    /* Create ArrayList Object */
    ArrayList<HashMap<String, String>> jsonData = new ArrayList<>();
    HashMap<String, String> map;
    for (Punch p : dailypunchlist) {
      map = new HashMap<>();
      map.put("id", String.valueOf(p.getId()));
      map.put("badgeid", p.getBadge().getId());
      map.put("terminalid", String.valueOf(p.getTerminalid()));
      map.put("punchtype", p.getPunchtype().toString());
      map.put("adjustmenttype", p.getAdjustmentType().toString());
      map.put("originaltimestamp", p.getOriginaltimestamp().toString());
      map.put("adjustedtimestamp", p.getAdjustedtimestamp().toString());

      jsonData.add(map);
    }

    String json = JSONValue.toJSONString(jsonData);

    return json;
  }

}