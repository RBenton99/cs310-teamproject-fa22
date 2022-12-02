package edu.jsu.mcis.cs310.tas_fa22;
import java.time.LocalDate;

public class Absenteeism {
    final Employee employee;
    final LocalDate payroll;
    final Double Percent;

    public Absenteeism(Employee employee, LocalDate payroll, Double Percent) {
        this.employee = employee;
        this.payroll = payroll;
        this.Percent = Percent;
    }
    
    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getPayroll() {
        return payroll;
    }

    public Double getPercent() {
        return Percent;
    }

    @Override
    public String toString() {
        return "Absenteeism{" + "employee=" + employee + ", payroll=" + payroll + ", Percent=" + Percent + '}';
    }
    
}
