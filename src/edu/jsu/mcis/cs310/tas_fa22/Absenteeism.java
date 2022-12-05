package edu.jsu.mcis.cs310.tas_fa22;
import java.time.LocalDate;

public class Absenteeism {
    final Employee employee;
    final LocalDate payperiod;
    final Double Percent;

    public Absenteeism(Employee employee, LocalDate payroll, Double Percent) {
        this.employee = employee;
        this.payperiod = payroll;
        this.Percent = Percent;
    }
    
    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getPayperiod() {
        return payperiod;
    }

    public Double getPercent() {
        return Percent;
    }

    
    @Override
    public String toString() {
        return "Absenteeism{" + "employee=" + employee + ", payperiod=" + payperiod + ", Percent=" + Percent + '}';
    }
    
}
