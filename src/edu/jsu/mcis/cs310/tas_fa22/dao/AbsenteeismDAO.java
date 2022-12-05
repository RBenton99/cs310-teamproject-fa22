package edu.jsu.mcis.cs310.tas_fa22.dao;
import edu.jsu.mcis.cs310.tas_fa22.*;
import java.sql.*;
import java.time.LocalDate;

public class AbsenteeismDAO {
   
    private static final String QFIND = "SELECT * FROM absenteeism WHERE employeeid = ? AND payperiod = ?";
    private static final String QCREATE = "INSERT INTO absenteeism (employeeid, payperiod, percentage) VALUES (?, ?, ?)";
    private static final String QUPDATE = "UPDATE absenteeism SET percentage percentage = ? WHERE payperiod = ? AND employeeid = ?";
    private final DAOFactory daofactory;

    public AbsenteeismDAO(DAOFactory daofactory) {
        this.daofactory = daofactory;
    }
     
 public Absenteeism find(int id,Employee employee, LocalDate payperiod){
        Absenteeism absenteeism = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            Connection conn = daofactory.getConnection();
            
            if(conn.isValid(0)){
                ps = conn.prepareStatement(QFIND);
                ps.setInt(1, id);
                
                boolean hasresults = ps.execute();
                
            if (conn.isValid(0)) {
                
                ps = conn.prepareStatement(QFIND);
                ps.setInt(1, employee.getId());
                ps.setDate(2, Date.valueOf(payperiod));
                
                if (hasresults) {

                    rs = ps.getResultSet();

                    while (rs.next()) {
                      
                        double percentage = rs.getDouble("percent");
                        absenteeism = new Absenteeism(employee, payperiod, percentage);
                        }
                }
            }
                }
            }
            catch (SQLException e) {
                throw new DAOException(e.getMessage());
            }
            finally {
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
        return absenteeism;
    }
 public Absenteeism create (Absenteeism absenteeism){
             
        PreparedStatement ps = null;
       
        AbsenteeismDAO DAO_absenteeism = new AbsenteeismDAO(daofactory);
        return absenteeism;
 }

}
