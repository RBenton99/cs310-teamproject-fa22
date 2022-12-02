package edu.jsu.mcis.cs310.tas_fa22;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;

public class Punch {
    private final int terminalid;
    private final EventType punchtype;
    private final Badge badge;
    private final Integer id;
    private final LocalDateTime originaltimestamp;
    private LocalDateTime adjustedtimestamp;
    private PunchAdjustmentType adjustmenttype;
    
    public Punch (int terminalid, Badge badge, EventType punchtype) {
        this.id = null;
        this.terminalid = terminalid;
        this.badge = badge;
        this.punchtype = punchtype;
        this.originaltimestamp = LocalDateTime.now();
    }
    
    public Punch (int id, int terminalid, Badge badge, LocalDateTime originaltimestamp, EventType punchtype) {
        this.id = id;
        this.terminalid = terminalid;
        this.badge = badge;
        this.punchtype = punchtype;
        this.originaltimestamp = originaltimestamp.withNano(0);
    }
    
    public LocalDateTime getOriginaltimestamp() {
        return originaltimestamp;
    }
    
    public LocalDateTime getAdjustedtimestamp() {
        return adjustedtimestamp;
    }
    
    public PunchAdjustmentType getAdjustmentType() {
        return adjustmenttype;
    }
    
    public int getTerminalid() {
        return terminalid;
    }

    public EventType getPunchtype() {
        return punchtype;
    }

    public Badge getBadge() {
        return badge;
    }

    public int getId() {
        return id;
    }
    
public void adjust(Shift s) {
        boolean adjusted = false;
        int shiftInterval = s.getRoundInterval();
        adjustedtimestamp = originaltimestamp;
        int originalMinute = originaltimestamp.getMinute();

        LocalDateTime shiftStart = originaltimestamp
                .withHour(s.getShiftStart().getHour())
                .withMinute(s.getShiftStart().getMinute())
                .withSecond(0);

        LocalDateTime shiftStop = originaltimestamp
                .withHour(s.getShiftStop().getHour())
                .withMinute(s.getShiftStop().getMinute())
                .withSecond(0);

        LocalDateTime lunchStart = originaltimestamp
                .withHour(s.getLunchStart().getHour())
                .withMinute(s.getLunchStart().getMinute())
                .withSecond(0);

        LocalDateTime lunchStop = originaltimestamp
                .withHour(s.getLunchStart().getHour())
                .withMinute(s.getLunchStart().getMinute())
                .withSecond(0);


        LocalDateTime intervalStart = shiftStart;
        intervalStart.plusMinutes(-(s.getRoundInterval()));

        LocalDateTime intervalStop = shiftStart;
        intervalStop.plusMinutes(s.getRoundInterval());
        
        LocalDateTime graceStart = shiftStart;
        graceStart.plusMinutes(s.getGracePeriod()); 
        
        LocalDateTime graceStop = shiftStart;
        graceStop.plusMinutes( -s.getGracePeriod());

        LocalDateTime dockStart = shiftStart;
        dockStart.plusMinutes( s.getDockPenalty());

        LocalDateTime dockStop = shiftStart;
        dockStop.plusMinutes( -s.getDockPenalty());
        
        if(originaltimestamp.getDayOfWeek() != DayOfWeek.SATURDAY || originaltimestamp.getDayOfWeek() != DayOfWeek.SUNDAY){
            if(punchtype == EventType.CLOCK_IN){
                
                if(intervalStart.isBefore(originaltimestamp)&& shiftStart.isAfter(originaltimestamp)){
                    adjustedtimestamp = shiftStart;
                    adjustmenttype = PunchAdjustmentType.SHIFT_START;
                    adjusted = true;
                }else if(originaltimestamp.isAfter(shiftStart)&& graceStart.isAfter(originaltimestamp)){
                    adjustedtimestamp = shiftStart;
                    adjustmenttype = PunchAdjustmentType.SHIFT_START;
                    adjusted = true;
                }else if(originaltimestamp.isAfter(graceStart)&& originaltimestamp.isBefore(dockStart)){
                    adjustedtimestamp = dockStart;
                    adjustmenttype = PunchAdjustmentType.SHIFT_DOCK;
                    adjusted = true;
                }else if(originaltimestamp.isAfter(lunchStart)&& lunchStop.isAfter(originaltimestamp)){
                    adjustedtimestamp = lunchStop;
                    adjustmenttype = PunchAdjustmentType.LUNCH_STOP;
                    adjusted = true;
                }
            }else if (punchtype == EventType.CLOCK_OUT){
                if(originaltimestamp.isAfter(lunchStart) && originaltimestamp.isBefore(lunchStop)){
                    adjustedtimestamp = lunchStart;
                    adjustmenttype = PunchAdjustmentType.LUNCH_START;
                    adjusted = true;
                }else if (originaltimestamp.isAfter(dockStop) && originaltimestamp.isBefore(graceStart)){
                    adjustedtimestamp = dockStop;
                    adjustmenttype = PunchAdjustmentType.SHIFT_DOCK;
                    adjusted = true;
                } 
                else if (originaltimestamp.isAfter(graceStop) && originaltimestamp.isBefore(shiftStop)){
                    adjustedtimestamp = shiftStop;
                    adjustmenttype = PunchAdjustmentType.SHIFT_STOP;
                }
                else if (originaltimestamp.isAfter(shiftStop) && originaltimestamp.isBefore(intervalStop)){
                    adjustedtimestamp = shiftStop;
                    adjustmenttype = PunchAdjustmentType.SHIFT_STOP;
                }
            }
        }
        if (!adjusted){
            if(originalMinute % shiftInterval != 0){
                int adjustedMinute;
                if(originalMinute % shiftInterval < shiftInterval/2){
                    adjustedMinute = Math.round(originalMinute/shiftInterval) * shiftInterval;
                }
                else{
                    adjustedMinute = Math.round(originalMinute/shiftInterval) * shiftInterval + shiftInterval;
                }
                adjustedtimestamp.plusMinutes(adjustedMinute - originalMinute);
                adjustedtimestamp = adjustedtimestamp.withSecond(0);
                adjustmenttype = PunchAdjustmentType.INTERVAL_ROUND;
            }
            else {
                adjustedtimestamp = adjustedtimestamp.withSecond(0);
 
                adjustmenttype = PunchAdjustmentType.NONE;
            }
        }
    }

    public String printAdjusted(){
        StringBuilder s = new StringBuilder();
        DateTimeFormatter DTF = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy HH:mm:ss");
        s.append("#").append(badge.getId()).append(" ");
        s.append(punchtype);
        s.append(": ").append(DTF.format(adjustedtimestamp).toUpperCase());

        s.append(" (").append(adjustmenttype).append(")");
        return s.toString();
    }
    
    public String printOriginal(){
        StringBuilder s = new StringBuilder();
        DateTimeFormatter DTF = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy HH:mm:ss");
        s.append("#").append(badge.getId()).append(" ");
        s.append(punchtype);
        s.append(": ").append(DTF.format(originaltimestamp).toUpperCase());
        
        return s.toString();
    }











}