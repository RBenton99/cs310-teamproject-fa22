package edu.jsu.mcis.cs310.tas_fa22;

/**
 *
 * @author admin
 */
public enum EmployeeType {
   PART_TIME("Temporary / Part-Time"),
   FULL_TIME("Full-Time");
   private final String description;
   private EmployeeType(String d) {
      description = d;
   }
   @Override
   public String toString() {
      return description;
   }
}
