package nz.ac.auckland.cer.project.dao;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nz.ac.auckland.cer.common.util.SSLCertificateValidation;
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

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

public class ProjectDatabaseDaoImpl extends SqlSessionDaoSupport implements ProjectDatabaseDao {

    private String restBaseUrl;
    private String restRemoteUserHeaderName;
    private String restRemoteUserHeaderValue;
    private String restAuthzHeaderValue;
    private RestTemplate restTemplate;
    private Logger log = Logger.getLogger(ProjectDatabaseDaoImpl.class.getName());

    public ProjectDatabaseDaoImpl() {

        // disable host certificate validation until run in production,
        // because the test REST service uses a self-signed certificate.
        // FIXME in production: enable host certificate validation again.
        SSLCertificateValidation.disable();
    }

    public Adviser getAdviserForTuakiriSharedToken(
            String sharedToken) throws Exception {

        List<Adviser> list = getSqlSession().selectList("getAdviserForTuakiriSharedToken", sharedToken);
        if (list != null) {
            if (list.size() == 0) {
                return null;
            } else if (list.size() > 1) {
                log.error("Internal error: More than one adviser in database with shared token " + sharedToken);
            }
            return list.get(0);
        }
        return null;
    }

    public Adviser getAdviserForEppn(
            String eppn) throws Exception {

        List<Adviser> list = getSqlSession().selectList("getAdviserForEppn", eppn);
        if (list != null) {
            if (list.size() == 0) {
                return null;
            } else if (list.size() > 1) {
                log.error("Internal error: More than one adviser in database with eppn " + eppn);
            }
            return list.get(0);
        }
        return null;
    }

    public Researcher[] getAllStaffOrPostDocs() throws Exception {

        Researcher[] researchers = new Researcher[0];
        String url = restBaseUrl + "researchers/institutionalRoleId/1";
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(this.setupHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            researchers = gson.fromJson(response.getBody(), Researcher[].class);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error("An unexpected error occured.", e);
            throw new Exception("An unexpected error occured.", e);
        }
        return researchers;
    }

    public Researcher getResearcherForTuakiriSharedToken(
            String sharedToken) throws Exception {

        List<Researcher> list = getSqlSession().selectList("getResearcherForTuakiriSharedToken", sharedToken);
        if (list != null) {
            if (list.size() == 0) {
                return null;
            } else if (list.size() > 1) {
                log.error("Internal error: More than one researcher in database with shared token " + sharedToken);
            }
            return list.get(0);
        }
        return null;
    }

    public Researcher getResearcherForEppn(
            String eppn) throws Exception {

        List<Researcher> list = getSqlSession().selectList("getResearcherForEppn", eppn);
        if (list != null) {
            if (list.size() == 0) {
                return null;
            } else if (list.size() > 1) {
                log.error("Internal error: More than one researcher in database with eppn " + eppn);
            }
            return list.get(0);
        }
        return null;
    }

    public Researcher getResearcherForId(
            Integer id) throws Exception {

        Researcher r = new Researcher();
        String url = restBaseUrl + "researchers/" + id.toString();
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(this.setupHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            r = gson.fromJson(response.getBody(), Researcher.class);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error("An unexpected error occured.", e);
            throw new Exception("An unexpected error occured.", e);
        }
        return r;
    }

    public List<Project> getProjectsOfResearcher(
            Integer researcherId) throws Exception {

        List<Project> projects = new LinkedList<Project>();
        String url = restBaseUrl + "researchers/" + researcherId.toString() + "/projects";
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(this.setupHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            Project[] tmp = gson.fromJson(response.getBody(), Project[].class);
            if (tmp != null) {
                for (Project p : tmp) {
                    if (p.getProjectCode().startsWith("uoa") ||
                            p.getProjectCode().startsWith("uoo") ||
                            p.getProjectCode().startsWith("rvmf") ||
                            p.getProjectCode().startsWith("nesi") ||
                            p.getProjectCode().startsWith("landcare") ) {
                        projects.add(p);
                    }
                }
            }
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error("An unexpected error occured.", e);
            throw new Exception("An unexpected error occured.", e);
        }
        return projects;
    }

    public Affiliation[] getAffiliations() throws Exception {

        Affiliation[] affiliations = new Affiliation[0];
        String url = restBaseUrl + "advisers/affil";
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(this.setupHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            affiliations = gson.fromJson(response.getBody(), Affiliation[].class);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error("An unexpected error occured.", e);
            throw new Exception("An unexpected error occured.", e);
        }
        return affiliations;
    }

    public ResearchOutputType[] getResearchOutputTypes() throws Exception {

        ResearchOutputType[] researchOutputTypes = new ResearchOutputType[0];
        String url = restBaseUrl + "projects/rotype";
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(this.setupHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            researchOutputTypes = gson.fromJson(response.getBody(), ResearchOutputType[].class);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error("An unexpected error occured.", e);
            throw new Exception("An unexpected error occured.", e);
        }
        return researchOutputTypes;
    }

    public InstitutionalRole[] getInstitutionalRoles() throws Exception {

        InstitutionalRole[] iRoles = new InstitutionalRole[0];
        String url = restBaseUrl + "researchers/iroles";
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(this.setupHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            iRoles = gson.fromJson(response.getBody(), InstitutionalRole[].class);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error("An unexpected error occured.", e);
            throw new Exception("An unexpected error occured.", e);
        }
        return iRoles;
    }

    public List<ScienceStudy> getScienceStudies() throws Exception {
       List<ScienceStudy> list = getSqlSession().selectList("getScienceStudies");
       return list;
    }
    
    public String getScienceStudyNameForId(
            String id) throws Exception {
        return getSqlSession().selectOne("getScienceStudyNameForId", id);
    }

    public String getScienceDomainNameForScienceStudyId(
            String id) throws Exception {
        return getSqlSession().selectOne("getScienceDomainNameForScienceStudyId", id);        
    }
    
    public Project createProject(
            ProjectWrapper pw) throws Exception {

        String url = restBaseUrl + "projects/";
        Gson gson = new Gson();
        JSONObject json = new JSONObject();
        try {
            HttpEntity<byte[]> request = new HttpEntity<byte[]>(gson.toJson(pw).getBytes("UTF-8"), this.setupHeaders());
            HttpEntity<String> he = restTemplate.postForEntity(url, request, String.class);
            Project p = pw.getProject();
            p.setId(new Integer(he.getBody()));
            return p;
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error("An unexpected error occured.", e);
            throw new Exception("An unexpected error occured.", e);
        }
    }

    public void createProjectProperty(
            ProjectProperty pp) throws Exception {

        String url = restBaseUrl + "projects/prop";
        Gson gson = new Gson();
        JSONObject json = new JSONObject();
        try {
            HttpEntity<byte[]> request = new HttpEntity<byte[]>(gson.toJson(pp).getBytes("UTF-8"), this.setupHeaders());
            restTemplate.put(url, request);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error("An unexpected error occured.", e);
            throw new Exception("An unexpected error occured.", e);
        }
    }

    public ProjectWrapper getProjectForIdOrCode(
            String identifier) throws Exception {

        String url = restBaseUrl + "projects/" + identifier;
        Gson gson = new Gson();
        try {
            HttpEntity<String> request = new HttpEntity<String>(this.setupHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            String body = response.getBody();
            JSONObject projectWrapper = new JSONObject(body);
            return gson.fromJson(projectWrapper.toString(), ProjectWrapper.class);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e3) {
            log.error("An unexpected error occured.", e3);
            throw new Exception("An unexpected error occured.", e3);
        }
    }

    public void addResearcherToProject(
            RPLink rpl) throws Exception {

        String url = restBaseUrl + "projects/rp";
        Gson gson = new Gson();
        try {
            HttpEntity<byte[]> entity = new HttpEntity<byte[]>(gson.toJson(rpl).getBytes("UTF-8"), this.setupHeaders());
            restTemplate.put(url, entity);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e3) {
            log.error("An unexpected error occured.", e3);
            throw new Exception("An unexpected error occured.", e3);
        }
    }

    public void addOrUpdateFollowUp(
            FollowUp fu) throws Exception {

        String url = restBaseUrl + "projects/followup";
        Gson gson = new Gson();
        try {
            HttpEntity<byte[]> entity = new HttpEntity<byte[]>(gson.toJson(fu).getBytes("UTF-8"), this.setupHeaders());
            restTemplate.put(url, entity);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            System.err.println(tmp);
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e3) {
            log.error("An unexpected error occured.", e3);
            throw new Exception("An unexpected error occured.", e3);
        }
    }

    public void addOrUpdateResearchOutput(
            ResearchOutput ro) throws Exception {

        String url = restBaseUrl + "projects/ro";
        Gson gson = new Gson();
        try {
            HttpEntity<byte[]> entity = new HttpEntity<byte[]>(gson.toJson(ro).getBytes("UTF-8"), this.setupHeaders());
            restTemplate.put(url, entity);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            System.err.println(tmp);
            JSONObject json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e3) {
            log.error("An unexpected error occured.", e3);
            throw new Exception("An unexpected error occured.", e3);
        }
    }

    public void updateProject(
            Integer projectId,
            String object,
            String field,
            String timestamp,
            String newValue) throws Exception {

        String url = restBaseUrl + "projects/" + projectId + "/" + object + "/" + field + "/" + timestamp + "/";
        JSONObject json = new JSONObject();
        try {
            HttpEntity<byte[]> request = new HttpEntity<byte[]>(newValue.getBytes("UTF-8"), this.setupHeaders());
            restTemplate.postForEntity(url, request, String.class);
        } catch (HttpStatusCodeException hsce) {
            log.error("Status Code Exception.", hsce);
            String tmp = hsce.getResponseBodyAsString();
            json = new JSONObject(tmp);
            throw new Exception(json.getString("message"));
        } catch (Exception e) {
            log.error("An unexpected error occured.", e);
            throw new Exception("An unexpected error occured.", e);
        }
    }

    public Map<Integer, String> getRolesOnProjectsForResearcher(Integer researcherId) throws Exception {
        Map<Integer,HashMap<String,String>> m = getSqlSession().selectMap("getRolesOnProjectsForResearcher", researcherId, "pid");
        Map<Integer,String> tmp = new HashMap<Integer,String>(m.size());
        for (Integer i: m.keySet()) {
            tmp.put(i, m.get(i).get("role"));
        }
        return tmp;
    }
    
    public void setRestTemplate(
            RestTemplate restTemplate) {

        SSLCertificateValidation.disable();
        this.restTemplate = restTemplate;
    }

    public void setRestBaseUrl(
            String restBaseUrl) {

        this.restBaseUrl = restBaseUrl;
    }

    public void setRestAuthzHeaderValue(
            String restAuthzHeaderValue) {

        this.restAuthzHeaderValue = restAuthzHeaderValue;
    }

    public void setRestRemoteUserHeaderName(
            String restRemoteUserHeaderName) {

        this.restRemoteUserHeaderName = restRemoteUserHeaderName;
    }

    public void setRestRemoteUserHeaderValue(
            String restRemoteUserHeaderValue) {

        this.restRemoteUserHeaderValue = restRemoteUserHeaderValue;
    }

    private HttpHeaders setupHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(this.restRemoteUserHeaderName, this.restRemoteUserHeaderValue);
        headers.set("Authorization", this.restAuthzHeaderValue);
        return headers;
    }

}
