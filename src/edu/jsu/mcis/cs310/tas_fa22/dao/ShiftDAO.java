package edu.jsu.mcis.cs310.tas_fa22.dao;

import edu.jsu.mcis.cs310.tas_fa22.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Colbylee Mincey
 *         Adam Parton
 */
public class ShiftDAO {
    private static final String QUERY_ID = "SELECT * FROM shift WHERE id = ?";
    private static final String QUERY_BADGE = "SELECT shiftid FROM employee WHERE badgeid = ?";
    private HashMap<String, String>map = new HashMap<>();
    private final DAOFactory daofactory;
    
    ShiftDAO (DAOFactory daofactory) {
        this.daofactory = daofactory;
    }
    
    public Shift find(int id) {
        Shift shift = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            Connection conn = daofactory.getConnection();
            
            if(conn.isValid(0)){
            ps = conn.prepareStatement(QUERY_ID);
            ps.setInt(1, id);
            
            boolean hasresults = ps.execute();
            
                if (hasresults) {
                   rs = ps.getResultSet();
                   
                   
                while (rs.next()) {
                    map.put("id", rs.getString("id"));
                    map.put("description", rs.getString("description"));
                    map.put("shiftstart", rs.getString("shiftstart"));
                    map.put("shiftstop", rs.getString("shiftstop"));
                    map.put("roundinterval", rs.getString("roundinterval"));
                    map.put("graceperiod", rs.getString("graceperiod"));
                    map.put("dockpenalty", rs.getString("dockpenalty"));
                    map.put("lunchstart", rs.getString("lunchstart"));
                    map.put("lunchstop", rs.getString("lunchstop"));
                    map.put("lunchthreshold", rs.getString("lunchthreshold"));
                    shift = new Shift(map); // shift class using HashMap constructor
                    }
                }
            }
        }catch (SQLException e) {
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
return shift;
}
    public Shift find(Badge badge){
        Shift shift = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            Connection conn = daofactory.getConnection();
            
            if(conn.isValid(0)){
                ps = conn.prepareStatement(QUERY_BADGE);
                ps.setString(1, badge.getId());
                
                boolean hasresults = ps.execute();
                
                if(hasresults){
                    rs = ps.getResultSet();
                    
                    while (rs.next()){
                        int anId = Integer.parseInt(rs.getString("shiftid"));
                        shift = ShiftDAO.this.find(anId);
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
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                throw new DAOException(e.getMessage());
            }
        }
        return shift;
    }
} 
