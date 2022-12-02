 package edu.jsu.mcis.cs310.tas_fa22.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;

import edu.jsu.mcis.cs310.tas_fa22.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PunchDAO {

    private static final String QUERY_ID = "SELECT * FROM event WHERE id = ?;";
    private static final String GET_ALL_PUNCHES_BY_BADGE_AND_DATE = "SELECT * FROM event WHERE badgeid = ? AND timestamp = ? ORDER BY timestamp DESC;";
    private static final String GET_ALL_PUNCHES_BY_BADGE_BETWEEN_TIMESTAMPS = "SELECT * FROM event WHERE badgeid = ? AND timestamp BETWEEN ? AND ? ORDER BY timestamp DESC;";
    private static final String GET_ALL_PUNCHES_BY_BADGE_FOR_FOLLOWING_DAY = "SELECT * FROM event WHERE badgeid = ? AND timestamp = ? AND eventtypeid = ? OR eventtypeid = ? ORDER BY timestamp ASC LIMIT 1;";
    private static final String SQL_INSERT = "INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) VALUES (?, ?, ?, ?)";
    private DAOFactory daofactory = null;
    private final BadgeDAO badgeDAO;

    public PunchDAO(DAOFactory daoFactory) {
        this.daofactory = daoFactory;
        this.badgeDAO = new BadgeDAO(daofactory);
    }

    public Punch find(int id) {
        Punch p = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daofactory.getConnection();
            BadgeDAO badgeDAO = daofactory.getBadgeDAO();
            
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_ID);
                ps.setInt(1, id);

                boolean hasresults = ps.execute();

                if (hasresults) {
                    rs = ps.getResultSet();

                    if (rs.next()) {
                        
                        int terminalid = rs.getInt("terminalid");
                        Badge badge = badgeDAO.find(rs.getString("badgeid"));
                        LocalDateTime ots = rs.getTimestamp("timestamp").toLocalDateTime().withNano(0);
                        EventType eventtype = EventType.values()[rs.getInt("eventtypeid")];
                        
                        p = new Punch(id, terminalid, badge, ots, eventtype);
                        
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return p;
    }

    public ArrayList<Punch> list(Badge badge, LocalDate begin, LocalDate end) {
        
        ArrayList<Punch> punches = new ArrayList<>();
        
        LocalDate current = begin;
        
        while ( !current.isAfter(end) ) {
            punches.addAll(list(badge, current));
            current.plus(1, ChronoUnit.DAYS);
        }
        
        return punches;
        
    }

    public ArrayList<Punch> list(Badge b, LocalDate toLocalDate) {

        Punch p = null;
        PreparedStatement todayPS = null;
        ResultSet rs = null;
        Timestamp timestamp = Timestamp.valueOf(toLocalDate.atStartOfDay());
        ArrayList<Punch> punches = new ArrayList<>();

        // Following day calculations
        Punch followingDayPunch = null;
        LocalDate followingDay = toLocalDate.plusDays(1);
        PreparedStatement followingDayPS = null;

        try {
            Connection conn = daofactory.getConnection();

            if (conn.isValid(0)) {
                todayPS = conn.prepareStatement(GET_ALL_PUNCHES_BY_BADGE_AND_DATE);
                todayPS.setString(1, b.getId());
                todayPS.setTimestamp(2, timestamp);

                followingDayPS = conn.prepareStatement(GET_ALL_PUNCHES_BY_BADGE_FOR_FOLLOWING_DAY);
                followingDayPS.setString(1, b.getId());
                followingDayPS.setTimestamp(2, Timestamp.valueOf(followingDay.atStartOfDay()));
                followingDayPS.setObject(3, EventType.CLOCK_OUT);
                followingDayPS.setObject(3, EventType.TIME_OUT);

                boolean nexDayHasResults = followingDayPS.execute();

                boolean hasresults = todayPS.execute();

                if (hasresults) {
                    rs = todayPS.getResultSet();

                    while (rs.next()) {

                        int id = rs.getInt("id");
                        int terminalid = rs.getInt("terminalid");
                        Badge badge = badgeDAO.find(rs.getString("badgeid"));
                        LocalDateTime ots = rs.getTimestamp("timestamp").toLocalDateTime().withNano(0);
                        EventType eventtype = EventType.values()[rs.getInt("eventtypeid")];
                        // add the punch to the punches list
                        p = new Punch(id, terminalid, badge, ots, eventtype);
                        punches.add(p);
                        
                    }
                }

                // this block get the first punch from the following day and LIMITs to 1 if it
                // is available
                if (nexDayHasResults) {
                    rs = followingDayPS.getResultSet();

                    while (rs.next()) {

                        int id = rs.getInt("id");
                        int terminalid = rs.getInt("terminalid");
                        Badge badge = badgeDAO.find(rs.getString("badgeid"));
                        LocalDateTime ots = rs.getTimestamp("timestamp").toLocalDateTime().withNano(0);
                        EventType eventtype = EventType.values()[rs.getInt("eventtypeid")];
                        // add the punch to the punches list
                        p = new Punch(id, terminalid, badge, ots, eventtype);
                        punches.add(p);
                        
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (todayPS != null) {
                try {
                    todayPS.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return punches;
    }

    public Integer create(Punch p1) {
        
        Integer key = null;
        
        PreparedStatement pst = null;

        try {
            
            Connection conn = daofactory.getConnection();
            pst = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            Badge badge = p1.getBadge();
            Employee employee = daofactory.getEmployeeDAO().find(badge);
            Department d = daofactory.getDepartmentDAO().find(employee.getId());

            if ( !(p1.getTerminalid() != d.getTerminalid() && p1.getTerminalid() != 0) ) {
                
                // "INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) VALUES (?, ?, ?, ?)";

                pst.setInt(1, p1.getTerminalid());
                pst.setString(2, p1.getBadge().getId());
                pst.setTimestamp(3, java.sql.Timestamp.valueOf(p1.getOriginaltimestamp()));
                pst.setInt(4, p1.getPunchtype().ordinal());

                int rows = pst.executeUpdate();

                if (rows == 0) {
                    throw new DAOException("Creating user failed, no rows affected.");
                }

                ResultSet generatedKeys = pst.getGeneratedKeys();

                if (generatedKeys.next()) {
                    key = generatedKeys.getInt(1);
                }
                
            }

        }
        catch (Exception e) {
            throw new DAOException(e.getMessage());
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        
        return key;
        
    }
}
