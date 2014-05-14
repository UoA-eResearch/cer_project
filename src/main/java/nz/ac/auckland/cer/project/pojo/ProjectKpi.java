package nz.ac.auckland.cer.project.pojo;

import java.io.Serializable;

public class ProjectKpi implements Serializable {

    private Integer adviserId;
    private String adviserName;
    private Integer code;
    private String codeName;
    private String date;
    private Integer id;
    private Integer kpiId;
    private String kpiTitle;
    private String kpiType;
    private String notes;
    private String projectCode;
    private Integer projectId;
    private Float value;

    public Integer getAdviserId() {
        return adviserId;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public Integer getCode() {
        return code;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getDate() {
        return date;
    }

    public Integer getId() {
        return id;
    }

    public Integer getKpiId() {
        return kpiId;
    }

    public String getKpiTitle() {
        return kpiTitle;
    }

    public String getKpiType() {
        return kpiType;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Float getValue() {
        return value;
    }

    public void setAdviserId(final Integer adviserId) {
        this.adviserId = adviserId;
    }

    public void setAdviserName(final String adviserName) {
        this.adviserName = adviserName;
    }

    public void setCode(final Integer code) {
        this.code = code;
    }

    public void setCodeName(final String codeName) {
        this.codeName = codeName;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setKpiId(final Integer kpiId) {
        this.kpiId = kpiId;
    }

    public void setKpiTitle(final String kpiTitle) {
        this.kpiTitle = kpiTitle;
    }

    public void setKpiType(final String kpiType) {
        this.kpiType = kpiType;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public void setProjectId(final Integer projectId) {
        this.projectId = projectId;
    }

    public void setValue(final Float value) {
        this.value = value;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
}
