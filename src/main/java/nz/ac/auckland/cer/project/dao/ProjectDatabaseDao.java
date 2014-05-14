package nz.ac.auckland.cer.project.dao;

import java.util.List;

import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectProperty;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.pojo.RPLink;
import nz.ac.auckland.cer.project.pojo.Researcher;

public interface ProjectDatabaseDao {

    public Affiliation[] getAffiliations() throws Exception;

    public InstitutionalRole[] getInstitutionalRoles() throws Exception;

    public Adviser getAdviserForTuakiriSharedToken(
            String sharedToken) throws Exception;

    public Researcher getResearcherForTuakiriSharedToken(
            String sharedToken) throws Exception;

    public Researcher getResearcherForId(
            Integer id) throws Exception;

    public List<Project> getProjectsOfResearcher(
            Integer researcherId) throws Exception;

    public Researcher[] getAllStaffOrPostDocs() throws Exception;

    public Project createProject(
            ProjectWrapper pw) throws Exception;

    public void createProjectProperty(
            ProjectProperty pp) throws Exception;

    public ProjectWrapper getProjectForIdOrCode(
            String projectCode) throws Exception;

    public void addResearcherToProject(
            RPLink rpl) throws Exception;
}
