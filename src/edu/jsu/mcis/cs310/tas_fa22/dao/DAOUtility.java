package edu.jsu.mcis.cs310.tas_fa22.dao;

import edu.jsu.mcis.cs310.tas_fa22.*;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import org.json.simple.*;
import java.util.ArrayList;
import java.util.HashMap;
import edu.jsu.mcis.cs310.tas_fa22.Punch;

/**
 * 
 * Utility class for DAOs.  This is a final, non-constructable class containing
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
      map.put("id", p.getId());
      map.put("badgeid", p.getBadgeid());
      map.put("terminalid", p.getTerminalid());
      map.put("punchtype", p.getPunchtype());
      map.put("adjustmenttype", p.getAdjustmenttype());
      map.put("originaltimestamp", p.getOriginaltimestamp());
      map.put("adjustedtimestamp", p.getAdjustedtimestamp());

      jsonData.add(map);
    }

    String json = JSONValue.toJSONString(jsonData);

    return json;
  }
}