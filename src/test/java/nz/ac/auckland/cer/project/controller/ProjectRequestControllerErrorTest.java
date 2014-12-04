package nz.ac.auckland.cer.project.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Affiliation;
import nz.ac.auckland.cer.project.pojo.Limitations;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectProperty;
import nz.ac.auckland.cer.project.pojo.ProjectRequest;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ProjectRequestControllerTest-context.xml", "classpath:root-context.xml" })
@WebAppConfiguration
public class ProjectRequestControllerErrorTest {

    private Affiliation[] affiliations;
    private Limitations limitations;
    private MockMvc mockMvc;
    private Project p;
    private Person person;
    private ProjectRequest pr;
    @Autowired private ProjectDatabaseDao projectDao;
    private Researcher[] researchers;
    @Autowired private WebApplicationContext wac;
    private GreenMail smtpServer;

    @Before
    public void setup() throws Exception {

        this.smtpServer = new GreenMail(ServerSetupTest.SMTP);
        this.smtpServer.start();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        pr = new ProjectRequest();
        pr.setProjectTitle("Project Title");
        StringBuffer pd = new StringBuffer();
        for (int i = 0; i < 500; i++) {
            pd.append("a");
        }
        pr.setProjectDescription(pd.toString());
        pr.setScienceStudyId("1");
        pr.setAskForSuperviser(false);
        this.limitations = new Limitations();
        limitations.setConcurrency("10");
        limitations.setCpuCores("5");
        limitations.setMemory("4");
        pr.setLimitations(limitations);
        pr.setMotivation("inadequate_computational_equipment");
        pr.setCurrentCompEnv("standard_computer");
        pr.setFunded(new Boolean("false"));

        this.person = new Person();
        person.setIsResearcher(true);
        person.setFullName("Jane Doe");
        person.setId(42);
        person.setInstitutionalRoleId(1);

        this.p = new Project();
        p.setName(pr.getProjectTitle());
        p.setDescription(pr.getProjectDescription());
        p.setId(42);

        this.affiliations = new Affiliation[] { new Affiliation("Test", "Test", "Test") };
        this.researchers = new Researcher[1];
        Researcher tmp = new Researcher();
        tmp.setFullName("John Doe");
        this.researchers[0] = tmp;
    }

    @After
    public void tearDown() throws Exception {

        this.smtpServer.stop();
    }

    @Test
    public void testInvalidSuperviserEmail() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        this.pr.setAskForSuperviser(true);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId())
                .param("askForSuperviser", pr.getAskForSuperviser().toString()).param("superviserId", "-1")
                .param("superviserName", "My superviser").param("superviserEmail", "invalid e-mail address")
                .param("superviserAffiliation", "Some University").param("superviserPhone", "123123213")
                .param("motivation", pr.getMotivation()).param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                // .andDo(print())
                .andExpect(model().attributeHasFieldErrors("projectrequest", "superviserEmail"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingComputationalEnvironment1() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        pr.setCurrentCompEnv("cluster_or_set_of_computers");
        pr.setLimitations(new Limitations());
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId())
                .param("askForSuperviser", pr.getAskForSuperviser().toString()).param("motivation", pr.getMotivation())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk())
                .andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 3))
                .andExpect(
                        model().attributeHasFieldErrors("projectrequest", "limitations.cpuCores", "limitations.memory",
                                "limitations.concurrency"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingComputationalEnvironment2() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        pr.setCurrentCompEnv("OTHER");
        pr.setLimitations(new Limitations());
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId())
                .param("askForSuperviser", pr.getAskForSuperviser().toString()).param("motivation", pr.getMotivation())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk())
                .andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 4))
                .andExpect(
                        model().attributeHasFieldErrors("projectrequest", "otherCompEnv", "limitations.cpuCores",
                                "limitations.memory", "limitations.concurrency"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingDescription() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("scienceStudyId", pr.getScienceStudyId())
                .param("motivation", pr.getMotivation()).param("askForSuperviser", pr.getAskForSuperviser().toString())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                .andExpect(model().attributeHasFieldErrors("projectrequest", "projectDescription"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingScienceStudyId() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("motivation", pr.getMotivation()).param("askForSuperviser", pr.getAskForSuperviser().toString())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                .andExpect(model().attributeHasFieldErrors("projectrequest", "scienceStudyId"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingMotivation() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId())
                .param("askForSuperviser", pr.getAskForSuperviser().toString())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                .andExpect(model().attributeHasFieldErrors("projectrequest", "motivation"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingOtherMotivation() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        pr.setMotivation("__OTHER__");
        RequestBuilder rb = post("/request_project").param("projectTitle", pr.getProjectTitle())
                .param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId())
                .param("askForSuperviser", pr.getAskForSuperviser().toString()).param("motivation", pr.getMotivation())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                .andExpect(model().attributeHasFieldErrors("projectrequest", "otherMotivation"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingSuperviserDetails() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        this.pr.setAskForSuperviser(true);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId()).param("superviserId", "-1")
                .param("askForSuperviser", pr.getAskForSuperviser().toString()).param("motivation", pr.getMotivation())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk())
                .andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 4))
                .andExpect(
                        model().attributeHasFieldErrors("projectrequest", "superviserName", "superviserEmail",
                                "superviserPhone", "superviserAffiliation"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingSuperviserId() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        this.pr.setAskForSuperviser(true);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId()).param("superviserId", "-2")
                .param("askForSuperviser", pr.getAskForSuperviser().toString()).param("motivation", pr.getMotivation())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                // .andDo(print())
                .andExpect(model().attributeHasFieldErrors("projectrequest", "superviserId"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingSuperviserOtherInstitution() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        this.pr.setAskForSuperviser(true);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId()).param("superviserId", "-1")
                .param("askForSuperviser", pr.getAskForSuperviser().toString())
                .param("superviserName", "My superviser").param("superviserEmail", "a@b.org")
                .param("superviserPhone", "123").param("superviserAffiliation", "OTHER")
                .param("motivation", pr.getMotivation()).param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                .andExpect(model().attributeHasFieldErrors("projectrequest", "superviserOtherInstitution"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingTitle() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId()).param("motivation", pr.getMotivation())
                .param("askForSuperviser", pr.getAskForSuperviser().toString())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                .andExpect(model().attributeHasFieldErrors("projectrequest", "projectTitle"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testTooShortDescription() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        this.pr.setProjectDescription("too short");
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId()).param("motivation", pr.getMotivation())
                .param("askForSuperviser", pr.getAskForSuperviser().toString())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                .andExpect(model().attributeHasFieldErrors("projectrequest", "projectDescription"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingFundingInformation() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId()).param("motivation", pr.getMotivation())
                .param("askForSuperviser", pr.getAskForSuperviser().toString())
                .param("currentCompEnv", pr.getCurrentCompEnv());
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                .andExpect(model().attributeHasFieldErrors("projectrequest", "funded"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testMissingFundingSource() throws Exception {

        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId()).param("motivation", pr.getMotivation())
                .param("askForSuperviser", pr.getAskForSuperviser().toString())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", "true");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_project"))
                .andExpect(model().attributeErrorCount("projectrequest", 1))
                .andExpect(model().attributeHasFieldErrors("projectrequest", "fundingSource"));
        verify(projectDao, times(0)).createProject((ProjectWrapper) any());
        verify(projectDao, times(0)).createProjectProperty((ProjectProperty) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }


}
