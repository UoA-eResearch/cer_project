package nz.ac.auckland.cer.project.dao;

import java.util.List;
import java.util.Map;

import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.FollowUp;
import nz.ac.auckland.cer.project.pojo.InstitutionalRole;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectProperty;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.pojo.RPLink;
import nz.ac.auckland.cer.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.project.pojo.ResearchOutputType;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.pojo.ScienceStudy;

public interface ProjectDatabaseDao {

    public Affiliation[] getAffiliations() throws Exception;

    public InstitutionalRole[] getInstitutionalRoles() throws Exception;

    public ResearchOutputType[] getResearchOutputTypes() throws Exception;

    public Adviser getAdviserForTuakiriSharedToken(
            String sharedToken) throws Exception;

    public Researcher getResearcherForTuakiriSharedToken(
            String sharedToken) throws Exception;

    public Researcher getResearcherForId(
            Integer id) throws Exception;

    public List<Project> getProjectsOfResearcher(
            Integer researcherId) throws Exception;

    public Researcher[] getAllStaffOrPostDocs() throws Exception;

    public List<ScienceStudy> getScienceStudies() throws Exception;

    public String getScienceStudyNameForId(
            String id) throws Exception;

    public String getScienceDomainNameForScienceStudyId(
            String id) throws Exception;

    public Project createProject(
            ProjectWrapper pw) throws Exception;

    public void createProjectProperty(
            ProjectProperty pp) throws Exception;

    public ProjectWrapper getProjectForIdOrCode(
            String projectCode) throws Exception;

    public void addResearcherToProject(
            RPLink rpl) throws Exception;

    public void addOrUpdateFollowUp(
            FollowUp fu) throws Exception;

    public void addOrUpdateResearchOutput(
            ResearchOutput ro) throws Exception;

    public void updateProject(
            Integer projectId,
            String object,
            String field,
            String timestamp,
            String newValue) throws Exception;

    public Map<Integer, String> getRolesOnProjectsForResearcher(
            Integer researcherId) throws Exception;

}
