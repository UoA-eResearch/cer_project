package nz.ac.auckland.cer.project.pojo;

public class ProjectProperty {

    private Integer id;
    private Integer facilityId;
    private Integer projectId;
    private String timestamp;
    private String propname;
    private String propvalue;

    public Integer getId() {

        return id;
    }

    public void setId(
            Integer id) {

        this.id = id;
    }

    public Integer getFacilityId() {

        return facilityId;
    }

    public void setFacilityId(
            Integer facilityId) {

        this.facilityId = facilityId;
    }

    public Integer getProjectId() {

        return projectId;
    }

    public void setProjectId(
            Integer projectId) {

        this.projectId = projectId;
    }

    public String getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(
            String timestamp) {

        this.timestamp = timestamp;
    }

    public String getPropname() {

        return propname;
    }

    public void setPropname(
            String propname) {

        this.propname = propname;
    }

    public String getPropvalue() {

        return propvalue;
    }

    public void setPropvalue(
            String propvalue) {

        this.propvalue = propvalue;
    }

}