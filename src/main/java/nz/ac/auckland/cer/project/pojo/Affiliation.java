package nz.ac.auckland.cer.project.pojo;

public class Affiliation {

    private String institution;
    private String division;
    private String department;

    public Affiliation() {

    }

    public Affiliation(
            String institution,
            String division,
            String department) {
        
        this.institution = institution;
        this.division = division;
        this.department = department;
    }

    public String getInstitution() {

        return institution;
    }

    public void setInstitution(
            String institution) {

        this.institution = institution;
    }

    public String getDivision() {

        return division;
    }

    public void setDivision(
            String division) {

        this.division = division;
    }

    public String getDepartment() {

        return department;
    }

    public void setDepartment(
            String department) {

        this.department = department;
    }

}
