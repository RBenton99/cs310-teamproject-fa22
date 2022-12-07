package edu.jsu.mcis.cs310.tas_fa22;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Colbylee Mincey
 */
public class Employee {
    private final Integer id;
    private final String firstname, middlename, lastname;
    private final LocalDateTime active;
    private final Badge badge;
    private final Department department;
    private final Shift shift;
    private final EmployeeType employeeType;

    public Employee(Integer id, String firstname, String middlename, String lastname, LocalDateTime active, Badge badge, Department department, Shift shift, EmployeeType employeeType) {
        this.id = id;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.active = active;
        this.badge = badge;
        this.department = department;
        this.shift = shift;
        this.employeeType = employeeType;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public LocalDateTime getActive() {
        return active;
    }

    public Badge getBadge() {
        return badge;
    }

    public Department getDepartment() {
        return department;
    }

    public Shift getShift() {
        return shift;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }
    @Override
    public String toString() {
        String results = String.format("ID #%d: %s, %s %s (#%s), Type: %s, Department: %s, Active: %s",
        getId(), getLastname(), getFirstname(), getMiddlename(),
        getBadge().getId(), getEmployeeType().toString() , getDepartment().getDescription(), getActive().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

        return results;
    }
}
