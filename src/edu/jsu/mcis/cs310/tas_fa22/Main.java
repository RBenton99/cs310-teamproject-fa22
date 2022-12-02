package edu.jsu.mcis.cs310.tas_fa22;

import edu.jsu.mcis.cs310.tas_fa22.dao.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import org.json.simple.*;
// Will McAdams
// Adam Parton
// Colbylee Mincey
// Ryan Benton
// Ahmed Alkhawaja
public class Main {
    
    public static void main(String[] args) {
        
        BigDecimal lhs = new BigDecimal("0.1");
        BigDecimal rhs = new BigDecimal("0.2");
        
        System.out.println(lhs.add(rhs));
        
        /*
        // test database connectivity; get DAOs

        DAOFactory daoFactory = new DAOFactory("tas.jdbc");
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        
        // find badge

        Badge b = badgeDAO.find("31A25435");
        
        // output should be "Test Badge: #31A25435 (Munday, Paul J)"
        
        System.err.println("Test Badge: " + b.toString());
*/
    }

}
