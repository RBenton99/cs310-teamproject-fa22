package edu.jsu.mcis.cs310.tas_fa22.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import edu.jsu.mcis.cs310.tas_fa22.Badge;
import edu.jsu.mcis.cs310.tas_fa22.Department;
import edu.jsu.mcis.cs310.tas_fa22.EventType;
import edu.jsu.mcis.cs310.tas_fa22.Punch;

public class PunchDAO {

    private static final String QUERY_ID = "SELECT * FROM event WHERE id = ?;";
    private static final String GET_ALL_PUNCHES_BY_BADGE_AND_DATE = "SELECT * FROM event WHERE badgeid = ? AND timestamp = ? ORDER BY timestamp DESC;";
    private static final String GET_ALL_PUNCHES_BY_BADGE_BETWEEN_TIMESTAMPS = "SELECT * FROM event WHERE badgeid = ? AND timestamp BETWEEN ? AND ? ORDER BY timestamp DESC;";
    private static final String GET_ALL_PUNCHES_BY_BADGE_FOR_FOLLOWING_DAY = "SELECT * FROM event WHERE badgeid = ? AND timestamp = ? AND eventtypeid = ? OR eventtypeid = ? ORDER BY timestamp ASC LIMIT 1;";
    private static final String SQL_INSERT = "INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) VALUES (?, ?, ?, ?)";
    private HashMap<String, String> map = new HashMap<>();
    private DAOFactory daofactory = null;
    private final BadgeDAO badgeDAO;

    public PunchDAO(DAOFactory daoFactory) {
        this.daofactory = daoFactory;
        this.badgeDAO = new BadgeDAO(daofactory);
    }

    public Punch find(int id) {
        Punch p = null;
        PreparedStatement PS = null;
        ResultSet RS = null;

        try {
            Connection conn = daofactory.getConnection();

            if (conn.isValid(0)) {
                PS = conn.prepareStatement(QUERY_ID);
                PS.setInt(1, id);

                boolean hasresults = PS.execute();

                if (hasresults) {
                    RS = PS.getResultSet();

                    while (RS.next()) {

                        map.put("id", RS.getString("id"));
                        map.put("terminalid", RS.getString("terminalid"));
                        map.put("badgeid", RS.getString("badgeid"));
                        map.put("timestamp", RS.getString("timestamp"));
                        map.put("eventtypeid", RS.getString("eventtypeid"));
                        Badge badge = badgeDAO.find(RS.getString("badgeid"));
                        p = new Punch(map, badge); // shift class using HashMap constructor
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (RS != null) {
                try {
                    RS.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (PS != null) {
                try {
                    PS.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return p;
    }

    public ArrayList<Punch> list(Badge badge, LocalDate begin, LocalDate end) {
        Punch p = null;
        PreparedStatement todayPS = null;
        ResultSet RS = null;
        Timestamp beignTimestamp = Timestamp.valueOf(begin.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(end.atStartOfDay());
        ArrayList<Punch> punches = new ArrayList<>();

        try {
            Connection conn = daofactory.getConnection();

            if (conn.isValid(0)) {
                todayPS = conn.prepareStatement(GET_ALL_PUNCHES_BY_BADGE_BETWEEN_TIMESTAMPS);
                todayPS.setString(1, badge.getId());
                todayPS.setTimestamp(2, beignTimestamp);
                todayPS.setTimestamp(3, endTimestamp);

                boolean hasresults = todayPS.execute();

                if (hasresults) {
                    RS = todayPS.getResultSet();

                    while (RS.next()) {

                        map.put("id", RS.getString("id"));
                        map.put("terminalid", RS.getString("terminalid"));
                        map.put("badgeid", RS.getString("badgeid"));
                        map.put("timestamp", RS.getString("timestamp"));
                        map.put("eventtypeid", RS.getString("eventtypeid"));
                        Badge b = badgeDAO.find(RS.getString("badgeid"));
                        p = new Punch(map, b); // shift class using HashMap constructor
                        // add the punch to the punches list
                        punches.add(p);
                    }
                }

            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (RS != null) {
                try {
                    RS.close();
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

    public ArrayList<Punch> list(Badge b, LocalDate toLocalDate) {

        Punch p = null;
        PreparedStatement todayPS = null;
        ResultSet RS = null;
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
                    RS = todayPS.getResultSet();

                    while (RS.next()) {

                        map.put("id", RS.getString("id"));
                        map.put("terminalid", RS.getString("terminalid"));
                        map.put("badgeid", RS.getString("badgeid"));
                        map.put("timestamp", RS.getString("timestamp"));
                        map.put("eventtypeid", RS.getString("eventtypeid"));
                        Badge badge = badgeDAO.find(RS.getString("badgeid"));
                        p = new Punch(map, badge); // shift class using HashMap constructor
                        // add the punch to the punches list
                        punches.add(p);
                    }
                }

                // this block get the first punch from the following day and LIMITs to 1 if it
                // is available
                if (nexDayHasResults) {
                    RS = followingDayPS.getResultSet();

                    while (RS.next()) {

                        map.put("id", RS.getString("id"));
                        map.put("terminalid", RS.getString("terminalid"));
                        map.put("badgeid", RS.getString("badgeid"));
                        map.put("timestamp", RS.getString("timestamp"));
                        map.put("eventtypeid", RS.getString("eventtypeid"));
                        Badge badge = badgeDAO.find(RS.getString("badgeid"));
                        p = new Punch(map, badge); // shift class using HashMap constructor
                        // add the punch to the punches list
                        punches.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (RS != null) {
                try {
                    RS.close();
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

    public int create(Punch p1) {
        PreparedStatement pst = null;

        try {
            Connection conn = daofactory.getConnection();
            pst = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            Badge badge = p1.getBadge();
            Department d = daofactory.getDepartmentDAO().find(badge);

            if (p1.getTerminalid() != d.getTerminalid() && p1.getTerminalid() != 0) {
                return 0;
            }

            pst.setInt(1, p1.getTerminalid());
            pst.setString(2, p1.getBadgeid());
            pst.setString(3, p1.getOriginaltimestamp().toString());
            pst.setInt(4, p1.getEventtypeid());

            int rows = pst.executeUpdate();

            if (rows == 0) {
                throw new DAOException("Creating user failed, no rows affected.");
            }

            ResultSet generatedKeys = pst.getGeneratedKeys();

            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }

        } catch (Exception e) {
            throw new DAOException(e.getMessage());

        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return 0;
    }
}
