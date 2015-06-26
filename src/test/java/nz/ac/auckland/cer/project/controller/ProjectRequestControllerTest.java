package nz.ac.auckland.cer.project.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.LinkedList;
import java.util.List;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;

import nz.ac.auckland.cer.common.db.project.dao.ProjectDbDao;
import nz.ac.auckland.cer.common.db.project.pojo.Affiliation;
import nz.ac.auckland.cer.common.db.project.pojo.Limitations;
import nz.ac.auckland.cer.common.db.project.pojo.Project;
import nz.ac.auckland.cer.common.db.project.pojo.ProjectProperty;
import nz.ac.auckland.cer.common.db.project.pojo.ProjectRequest;
import nz.ac.auckland.cer.common.db.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.common.db.project.pojo.Researcher;
import nz.ac.auckland.cer.common.db.project.util.Person;
import nz.ac.auckland.cer.project.util.EmailUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ProjectRequestControllerTest-context.xml", "classpath:root-context.xml" })
@WebAppConfiguration
public class ProjectRequestControllerTest {

    private List<Affiliation> affiliations;
    private final String expectedRedirect = "request_project_response";
    private Limitations limitations;
    private MockMvc mockMvc;
    private Project p;
    private Person person;
    private Researcher superviser;
    private ProjectRequest pr;
    @Autowired private ProjectDbDao projectDao;
    private Researcher[] researchers;
    @Autowired private WebApplicationContext wac;
    @Autowired private EmailUtil eu;
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
        person.setEmail("jane@doe.org");
        person.setId(42);
        person.setInstitutionalRoleId(1);

        this.superviser = new Researcher();
        superviser.setFullName("James Superviser");
        superviser.setInstitution("Some Institution");
        superviser.setDivision("Some Division");
        superviser.setDepartment("Some Department");
        superviser.setEmail("james@superviser.org");
        superviser.setPhone("1234");

        this.p = new Project();
        p.setName(pr.getProjectTitle());
        p.setDescription(pr.getProjectDescription());
        p.setId(42);

        this.affiliations = new LinkedList<Affiliation>();
        this.affiliations.add(new Affiliation("Test", "Test", "Test"));
        this.researchers = new Researcher[1];
        Researcher tmp = new Researcher();
        tmp.setFullName("John Doe");
        this.researchers[0] = tmp;
    }

    @After
    public void tearDown() throws Exception {

        this.smtpServer.stop();
    }

    @DirtiesContext
    // required so that checks for method calls via verify work in subsequent
    // tests
    @Test
    public void testNoSuperviserSuccess() throws Exception {

        when(projectDao.createProject((ProjectWrapper) any())).thenReturn(this.p);
        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        when(projectDao.getScienceStudyNameForId(anyString())).thenReturn("Astronomy");
        when(projectDao.getScienceDomainNameForScienceStudyId(anyString())).thenReturn("Earth and Space");
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId())
                .param("askForSuperviser", pr.getAskForSuperviser().toString()).param("motivation", pr.getMotivation())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", "true").param("fundingSource", "Some funding source"); // with funding source
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name(expectedRedirect))
                .andExpect(model().attributeErrorCount("projectrequest", 0));
        verify(projectDao, times(1)).createProject((ProjectWrapper) any());
        verify(projectDao, times(7)).createProjectProperty((ProjectProperty) any());
        
        assert (smtpServer.getReceivedMessages().length == 1);
        Message m = smtpServer.getReceivedMessages()[0];
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getReplyTo().equals(((InternetAddress) m.getReplyTo()[0]).toString()));
        assert ("New Pan cluster project request".equals(m.getSubject()));
        String body = GreenMailUtil.getBody(m);
        assert (!body.contains("__"));
        assert (!body.contains("N/A"));
    }

    @DirtiesContext
    @Test
    public void testWithOtherSuperviserOtherInstitutionSuccess() throws Exception {

        when(projectDao.createProject((ProjectWrapper) any())).thenReturn(this.p);
        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        when(projectDao.getScienceStudyNameForId(anyString())).thenReturn("Astronomy");
        when(projectDao.getScienceDomainNameForScienceStudyId(anyString())).thenReturn("Earth and Space");
        this.person.setInstitutionalRoleId(2);
        this.person.setEmail("Some email");
        this.pr.setAskForSuperviser(true);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId())
                .param("askForSuperviser", pr.getAskForSuperviser().toString()).param("superviserId", "-1")
                .param("superviserName", "My superviser").param("superviserEmail", "superviser@company.org")
                .param("superviserPhone", "123123213").param("superviserAffiliation", "Other")
                .param("superviserOtherInstitution", "Some Uni").param("superviserOtherDivision", "Some Div")
                .param("superviserOtherDepartment", "Some Dep").param("motivation", pr.getMotivation())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name(expectedRedirect))
                .andExpect(model().attributeErrorCount("projectrequest", 0));
        verify(projectDao, times(1)).createProject((ProjectWrapper) any());
        verify(projectDao, times(7)).createProjectProperty((ProjectProperty) any());
        
        assert (smtpServer.getReceivedMessages().length == 2);

        Message m = smtpServer.getReceivedMessages()[0];
        String body = GreenMailUtil.getBody(m);
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getReplyTo().equals(((InternetAddress) m.getReplyTo()[0]).toString()));
        assert (this.eu.getOtherAffiliationEmailSubject().equals(m.getSubject()));
        assert (!body.contains("__"));
        assert (!body.contains("N/A"));

        m = smtpServer.getReceivedMessages()[1];
        body = GreenMailUtil.getBody(m);
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getReplyTo().equals(((InternetAddress) m.getReplyTo()[0]).toString()));
        assert (this.eu.getProjectRequestEmailSubject().equals(m.getSubject()));
        assert (body.contains("Supervisor information:"));
        assert (!body.contains("__"));
        assert (!body.contains("N/A"));
    }

    @DirtiesContext
    @Test
    public void testWithOtherSuperviserSuccess() throws Exception {

        when(projectDao.createProject((ProjectWrapper) any())).thenReturn(this.p);
        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        when(projectDao.getScienceStudyNameForId(anyString())).thenReturn("Astronomy");
        when(projectDao.getScienceDomainNameForScienceStudyId(anyString())).thenReturn("Earth and Space");
        this.person.setInstitutionalRoleId(2);
        this.pr.setAskForSuperviser(true);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId())
                .param("askForSuperviser", pr.getAskForSuperviser().toString()).param("superviserId", "3")
                .param("superviserName", "My superviser").param("superviserEmail", "superviser@company.org")
                .param("superviserPhone", "123123213")
                .param("superviserAffiliation", "Some University -- Some Division -- Some Department")
                .param("motivation", pr.getMotivation()).param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name(expectedRedirect))
                .andExpect(model().attributeErrorCount("projectrequest", 0));
        verify(projectDao, times(1)).createProject((ProjectWrapper) any());
        verify(projectDao, times(7)).createProjectProperty((ProjectProperty) any());
        assert (smtpServer.getReceivedMessages().length == 1);
        Message m = smtpServer.getReceivedMessages()[0];
        String body = GreenMailUtil.getBody(m);
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getReplyTo().equals(((InternetAddress) m.getReplyTo()[0]).toString()));
        assert (this.eu.getProjectRequestEmailSubject().equals(m.getSubject()));
        assert (body.contains("Supervisor information:"));
        assert (body.contains("The supervisor does not yet exist in the database."));
        assert (!body.contains("__"));
        assert (!body.contains("N/A"));
    }

    @DirtiesContext
    @Test
    public void testWithSuperviserSuccess() throws Exception {

        when(projectDao.createProject((ProjectWrapper) any())).thenReturn(this.p);
        when(projectDao.getAffiliations()).thenReturn(this.affiliations);
        when(projectDao.getAllStaffOrPostDocs()).thenReturn(this.researchers);
        when(projectDao.getResearcherForId(anyInt())).thenReturn(this.superviser);
        
        this.person.setInstitutionalRoleId(2);
        this.pr.setAskForSuperviser(true);
        RequestBuilder rb = post("/request_project").requestAttr("person", this.person)
                .param("projectTitle", pr.getProjectTitle()).param("projectDescription", pr.getProjectDescription())
                .param("scienceStudyId", pr.getScienceStudyId())
                .param("askForSuperviser", pr.getAskForSuperviser().toString()).param("superviserId", "3")
                .param("motivation", pr.getMotivation())
                .param("currentCompEnv", pr.getCurrentCompEnv())
                .param("funded", Boolean.toString(pr.getFunded()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name(expectedRedirect))
                .andExpect(model().attributeErrorCount("projectrequest", 0));
        verify(projectDao, times(1)).getResearcherForId(anyInt());
        verify(projectDao, times(1)).createProject((ProjectWrapper) any());
        verify(projectDao, times(5)).createProjectProperty((ProjectProperty) any());
        assert (smtpServer.getReceivedMessages().length == 1);
        Message m = smtpServer.getReceivedMessages()[0];
        String body = GreenMailUtil.getBody(m);
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getReplyTo().equals(((InternetAddress) m.getReplyTo()[0]).toString()));
        assert (this.eu.getProjectRequestEmailSubject().equals(m.getSubject()));
        assert (body.contains("Supervisor information:"));
        assert (body.contains("The supervisor already exists in the database."));
        assert (!body.contains("__"));
        assert (!body.contains("N/A"));
    }

}
