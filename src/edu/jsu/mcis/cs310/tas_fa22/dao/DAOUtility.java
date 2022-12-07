package edu.jsu.mcis.cs310.tas_fa22.dao;

import edu.jsu.mcis.cs310.tas_fa22.*;
import java.time.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import org.json.simple.*;

/**
 * 
 * Utility class for DAOs.  This is a final, non-constructable class containing
 * common DAO logic and other repeated and/or standardized code, refactored into
 * individual static methods.
 *
 */
public final class DAOUtility {
    
        public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift s) {
        
        int m = 0;
        int StrHours = 0;
        int StpHours;
        int StrMin = 0;
        int StpMin;
        int Calc;
        int totalWithLunch;
        int lunchDuration = s.getLunchDuration();
        
        LocalDateTime punches;
        
        boolean pair = false;
        boolean currentDay = false;
        
       
        for (Punch p : dailypunchlist){
            if (p.getPunchtype() == EventType.CLOCK_IN || 
                    p.getPunchtype() == EventType.CLOCK_OUT){
                }
                if (p.getPunchtype() == EventType.CLOCK_IN){
                   pair = false;
                   
                }
                
                if (p.getPunchtype() == EventType.CLOCK_OUT){
                   pair = true; 
                }
           
           
           if (pair == false) {
               punches = p.getAdjustedtimestamp();
               StrHours = punches.getHour();
               StrMin = punches.getMinute();
           }
           
           else if (pair){ 
                
               currentDay = true;
               if(currentDay) {
                   

                    punches = p.getAdjustedtimestamp();
                    StpHours = punches.getHour();
                    StpMin = punches.getMinute();
                    totalWithLunch = ((StpHours - StrHours) * 60)
                            + (StpMin - StrMin);

                    if (totalWithLunch > s.getLunchThreshold()){
                        Calc = totalWithLunch - lunchDuration;
                        m = m + Calc;
                    }

                    else if (totalWithLunch <= s.getLunchThreshold()){
                        Calc = ((StpHours - StrHours) * 60)
                                + (StpMin - StrMin);
                        m = m + Calc; 
                    }
                }
           }
        }  
    return m;
    }
        
        public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist){
           
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        
        for(int i = 0; i < dailypunchlist.size(); i++){
            
            HashMap<String, String> punchData = new HashMap<>();
          
            punchData.put("id", Integer.toString(dailypunchlist.get(i).getId()));
            punchData.put("punchtype", dailypunchlist.get(i).getPunchtype().toString());
            punchData.put("adjustmenttype", dailypunchlist.get(i).getAdjustmentType().toString());
            punchData.put("badgeid", dailypunchlist.get(i).getBadge().getId());
            punchData.put("terminalid", Integer.toString(dailypunchlist.get(i).getTerminalid()));
            punchData.put("adjustedtimestamp", dailypunchlist.get(i).getAdjustedtimestamp().format(DateTimeFormatter.ofPattern("E MM/dd/yyyy HH:mm:ss")).toUpperCase());
            punchData.put("originaltimestamp", dailypunchlist.get(i).getOriginaltimestamp().format(DateTimeFormatter.ofPattern("E MM/dd/yyyy HH:mm:ss")).toUpperCase());
        
            list.add(punchData);
            
        }
        
        String json = JSONValue.toJSONString(list);
         return json;
    }
        
       public static String getPunchListPlusTotalsAsJSON(ArrayList<Punch> punchlist, Shift shift){
           JSONObject json = new JSONObject();
           JSONArray punches = new JSONArray();
           ArrayList<HashMap<String, String>> jsonData = new ArrayList<>();
           
           String Strjson;
           String absenteeism = String.format("%.2f%%", calculateAbsenteeism(punchlist, shift)*100);
           String totalMinutes = String.valueOf(calculateTotalMinutes(punchlist, shift));
           
           for(int i = 0; i < punchlist.size(); i++){
               
            HashMap<String, String> punchData = new HashMap<>();
            
            punchData.put("id", Integer.toString(punchlist.get(i).getId()));            
            punchData.put("punchtype", punchlist.get(i).getPunchtype().toString());
            punchData.put("adjustmenttype", punchlist.get(i).getAdjustmentType().toString());
            punchData.put("terminalid", Integer.toString(punchlist.get(i).getTerminalid()));
            punchData.put("badgeid", punchlist.get(i).getBadge().getId());
            punchData.put("adjustedtimestamp", punchlist.get(i).getAdjustedtimestamp().format(DateTimeFormatter.ofPattern("E MM/dd/yyyy HH:mm:ss")).toUpperCase());
            punchData.put("originaltimestamp", punchlist.get(i).getOriginaltimestamp().format(DateTimeFormatter.ofPattern("E MM/dd/yyyy HH:mm:ss")).toUpperCase());
                    
            jsonData.add(punchData);
           }
           
         json.put("absenteeism", absenteeism);
         json.put("totalMinutes", totalMinutes);
         json.put("punchlist", punches);
         
         Strjson = JSONValue.toJSONString(jsonData);
         return Strjson;
       }
        
    public static double calculateAbsenteeism(ArrayList<Punch>punchlist, Shift shift) {
        double percentage;
        float min = calculateTotalMinutes(punchlist, shift);
        float work = (shift.getShiftDuration() - shift.getLunchDuration()) * 5;
        
        percentage = ((work - min) / work);
        return percentage;
    }
}