 package edu.jsu.mcis.cs310.tas_fa22.dao;

import java.sql.*;
import edu.jsu.mcis.cs310.tas_fa22.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
/**
 *
 * @author Colbylee Mincey
 */
public class EmployeeDAO {
    private static final String QUERY_FIND_ID = "SELECT * FROM employee WHERE id = ?";
    private static final String QUERY_FIND_BADGE = "SELECT * FROM employee WHERE badgeid = ?";
    
    private final DAOFactory daoFactory;
    
    public EmployeeDAO (DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    public Employee find(int id) {
        Employee employee = null;
        PreparedStatement PS = null;
        ResultSet RS = null;
        
        try {
            Connection conn = daoFactory.getConnection();
            
            if (conn.isValid(0)) {
                PS = conn.prepareStatement(QUERY_FIND_ID);
                PS.setInt(1, id);
                boolean results = PS.execute();
                
                if (results) {
                    RS = PS.getResultSet();
                    
                    while(RS.next()) {
                        
                        
                        BadgeDAO badgeDAO = new BadgeDAO(daoFactory);
                        ShiftDAO shiftDAO = new ShiftDAO(daoFactory);
                        DepartmentDAO departmentDAO = new DepartmentDAO(daoFactory);
                        Badge badge = badgeDAO.find(RS.getString("badge"));
                        Shift shift = shiftDAO.find(RS.getInt("shift"));
                        Department department = departmentDAO.find(RS.getInt("department"));
                        

                        
                        String firstname = RS.getString("firstname");
                        String middlename = RS.getString("middlename");
                        String lastname = RS.getString("lastname");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime active = LocalDateTime.parse(RS.getString("active"), formatter);
                        EmployeeType employeetype = EmployeeType.values()[RS.getInt("employeetype")];

                        employee = new Employee (id, firstname, middlename, lastname, active, badge, department, shift, employeetype);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }finally {
            if (RS != null) {
                try {
                    RS.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        if (PS != null){
            try {
                PS.close();
            } catch (SQLException e) {
                throw new DAOException(e.getMessage());
            }
        }
        return employee;
    }
    public Employee find(Badge badge) {
        Employee employee = null;
        PreparedStatement PS = null;
        ResultSet RS = null;

        try {
            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                PS = conn.prepareStatement(QUERY_FIND_BADGE);
                PS.setString(1, badge.getId());
                boolean results = PS.execute();

                if (results) {
                    RS = PS.getResultSet();

                    while (RS.next()) {
                        int id = RS.getInt("id");
                        String firstname = RS.getString("firstname");
                        String middlename = RS.getString("middlename");
                        String lastname = RS.getString("lastname");
                        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime active = LocalDateTime.parse(RS.getString("active"), dateformat);
                        EmployeeType employeetype = EmployeeType.values()[RS.getInt("employeetype")];
                        
                        DepartmentDAO DepartmentDAO = daoFactory.getDepartmentDAO();
                        BadgeDAO BadgeDAO = daoFactory.getBadgeDAO();
                        ShiftDAO ShiftDAO = daoFactory.getShiftDAO();
                        Department department = DepartmentDAO.find(RS.getInt("department"));
                        Shift shift = ShiftDAO.find(badge);
                        employee = new Employee(id, firstname, middlename, lastname, active, badge, department, shift, employeetype);
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
                    RS.close();
                } catch (SQLException e) {
                    throw  new DAOException(e.getMessage());
                }
            }
        }
        return employee;
    }   
    
}
