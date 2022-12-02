package edu.jsu.mcis.cs310.tas_fa22.dao;
import edu.jsu.mcis.cs310.tas_fa22.*;
import java.sql.*;
import java.time.LocalDate;

public class AbsenteeismDAO {
   
    private static final String QUERY = "SELECT * FROM absenteeism WHERE employeeid = ? AND payroll = ?";
    private final DAOFactory daofactory;

    public AbsenteeismDAO(DAOFactory daofactory) {
        this.daofactory = daofactory;
    }
     
 public Absenteeism find(int id,Employee employee, LocalDate payroll){
        Absenteeism absenteeism = null;
        PreparedStatement PS = null;
        ResultSet RS = null;
        
        try{
            Connection conn = daofactory.getConnection();
            
            if(conn.isValid(0)){
                PS = conn.prepareStatement(QUERY);
                PS.setInt(1, id);
                
                boolean hasresults = PS.execute();
                
            if (conn.isValid(0)) {
                
                PS = conn.prepareStatement(QUERY);
                PS.setInt(1, employee.getId());
                PS.setDate(2, Date.valueOf(payroll));
                
                if (hasresults) {

                    RS = PS.getResultSet();

                    while (RS.next()) {
                      
                        double percentage = RS.getDouble("percent");
                        absenteeism = new Absenteeism(employee, payroll, percentage);
                        }
                }
            }
                }
            }
            catch (SQLException e) {
                throw new DAOException(e.getMessage());
            }
            finally {
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
        return absenteeism;
    }

}
