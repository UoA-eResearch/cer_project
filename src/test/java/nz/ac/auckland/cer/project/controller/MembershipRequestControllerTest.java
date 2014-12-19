package nz.ac.auckland.cer.project.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.LinkedList;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.MembershipRequest;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.pojo.RPLink;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
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
@ContextConfiguration(locations = { "classpath:MembershipRequestControllerTest-context.xml",
    "classpath:root-context.xml" })
@WebAppConfiguration
public class MembershipRequestControllerTest {

    private MockMvc mockMvc;
    private MembershipRequest mr;
    private Person person;
    private Project project;
    @Autowired private ProjectDatabaseDao projectDao;
    private ProjectWrapper projectWrapper;
    @Autowired private WebApplicationContext wac;
    @Autowired private EmailUtil eu;
    private GreenMail smtpServer;

    @BeforeClass
    public static void beforeClass() {

    }

    @Before
    public void setup() throws Exception {

        this.smtpServer = new GreenMail(ServerSetupTest.SMTP);
        this.smtpServer.start();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        mr = new MembershipRequest();
        mr.setProjectCode("dummy007");

        this.person = new Person();
        person.setIsResearcher(true);
        person.setFullName("Jane Doe");
        person.setEmail("jane@doe.co.nz");
        person.setId(42);
        person.setInstitutionalRoleId(1);

        this.projectWrapper = new ProjectWrapper();
        this.project = new Project();
        project.setProjectCode("uoa00042");
        project.setName("Some title");
        project.setDescription("Some description");
        project.setId(42);
        projectWrapper.setProject(project);
    }

    @After
    public void tearDown() throws Exception {
        smtpServer.stop();        
    }

    @Test
    public void testGetPersonIsNotResearcher() throws Exception {

        this.person.setIsResearcher(false);
        RequestBuilder rb = get("/request_membership").requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_membership_response"))
                .andExpect(model().attributeExists("error_message"));
    }

    @Test
    public void testGetRedirectIfPersonIsNull() throws Exception {

        RequestBuilder rb = get("/request_membership");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isFound()).andExpect(redirectedUrl("redirect"));
    }

    @Test
    public void testGetSuccess() throws Exception {

        RequestBuilder rb = get("/request_membership").requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_membership")).andExpect(model().hasNoErrors());
    }

    @DirtiesContext
    @Test
    public void testPostRedirectIfPersonIsNull() throws Exception {

        RequestBuilder rb = post("/request_membership").param("projectCode", mr.getProjectCode());
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isFound()).andExpect(redirectedUrl("redirect"));
        verify(projectDao, times(0)).getProjectForIdOrCode(this.mr.getProjectCode());
    }

    @DirtiesContext
    @Test
    public void testPostNonExistingProjectCode() throws Exception {

        when(projectDao.getProjectForIdOrCode(anyString())).thenThrow(new Exception());
        RequestBuilder rb = post("/request_membership").requestAttr("person", this.person).param("projectCode",
                this.mr.getProjectCode());
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_membership"))
                .andExpect(model().attributeErrorCount("membershiprequest", 1));
        verify(projectDao, times(1)).getProjectForIdOrCode(this.mr.getProjectCode());
        assert(smtpServer.getReceivedMessages().length == 0);
    }


    @DirtiesContext
    @Test
    public void testPostSuccess_noOwner() throws Exception {

        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(this.projectWrapper);
        RequestBuilder rb = post("/request_membership").requestAttr("person", this.person).param("projectCode",
                mr.getProjectCode());
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_membership_response"))
                .andExpect(model().attributeErrorCount("membershiprequest", 0));
        verify(projectDao, times(1)).getProjectForIdOrCode(this.mr.getProjectCode());
        assert(smtpServer.getReceivedMessages().length == 1);
        Message m = smtpServer.getReceivedMessages()[0];
        String body = GreenMailUtil.getBody(m);
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getReplyTo().equals(((InternetAddress) m.getReplyTo()[0]).toString()));
        assert (m.getRecipients(RecipientType.CC) == null);
        assert (this.eu.getMembershipRequestEmailSubject().equals(m.getSubject()));
        assert (body.contains(this.project.getProjectCode()));
        assert (body.contains(this.project.getName()));
        assert (body.contains(this.project.getDescription()));
        assert (body.contains("There was a problem processing the request though: "
                + "No project owner is registered with this project."));
        assert (!body.contains("__"));
        assert (!body.contains("N/A"));
    }

    @DirtiesContext
    @Test
    public void testPostSuccess_noOwnerEmail() throws Exception {

        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(this.projectWrapper);
        // add project owner but no e-mail address
        this.projectWrapper.setRpLinks(new LinkedList<RPLink>());
        this.projectWrapper.getRpLinks().add(new RPLink());
        this.projectWrapper.getRpLinks().get(0).setResearcherRoleId(1);
        this.projectWrapper.getRpLinks().get(0).setResearcher(new Researcher());
        //this.projectWrapper.getRpLinks().get(0).getResearcher().setEmail("test@test.org");

        RequestBuilder rb = post("/request_membership").requestAttr("person", this.person).param("projectCode",
                mr.getProjectCode());
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_membership_response"))
                .andExpect(model().attributeErrorCount("membershiprequest", 0));
        verify(projectDao, times(1)).getProjectForIdOrCode(this.mr.getProjectCode());
        assert(smtpServer.getReceivedMessages().length == 1);
        Message m = smtpServer.getReceivedMessages()[0];
        String body = GreenMailUtil.getBody(m);
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getReplyTo().equals(((InternetAddress) m.getReplyTo()[0]).toString()));
        assert (m.getRecipients(RecipientType.CC) == null);
        assert (this.eu.getMembershipRequestEmailSubject().equals(m.getSubject()));
        assert (body.contains(this.project.getProjectCode()));
        assert (body.contains(this.project.getName()));
        assert (body.contains(this.project.getDescription()));
        assert (body.contains("There was a problem processing the request though: "
                + "No e-mail address is registered with the owner of this project."));
        assert (!body.contains("__"));
        assert (!body.contains("N/A"));
    }

    @DirtiesContext
    @Test
    public void testPostSuccess() throws Exception {

        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(this.projectWrapper);
        // add project owner but no e-mail address
        String cc = "cc@test.cer.auckland.ac.nz";
        this.projectWrapper.setRpLinks(new LinkedList<RPLink>());
        this.projectWrapper.getRpLinks().add(new RPLink());
        this.projectWrapper.getRpLinks().get(0).setResearcherRoleId(1);
        this.projectWrapper.getRpLinks().get(0).setResearcher(new Researcher());
        this.projectWrapper.getRpLinks().get(0).getResearcher().setFullName("John Peter Doe");
        this.projectWrapper.getRpLinks().get(0).getResearcher().setEmail(cc);

        RequestBuilder rb = post("/request_membership").requestAttr("person", this.person).param("projectCode",
                mr.getProjectCode());
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_membership_response"))
                .andExpect(model().attributeErrorCount("membershiprequest", 0));
        verify(projectDao, times(1)).getProjectForIdOrCode(this.mr.getProjectCode());
        assert(smtpServer.getReceivedMessages().length > 0);
        Message m = smtpServer.getReceivedMessages()[0];
        String body = GreenMailUtil.getBody(m);
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert(cc.equals(((InternetAddress) m.getRecipients(RecipientType.CC)[0]).toString()));
        assert(this.eu.getReplyTo().equals(((InternetAddress) m.getReplyTo()[0]).toString()));
        assert(this.eu.getMembershipRequestEmailSubject().equals(m.getSubject()));
        assert(body.contains("Hi John"));
        assert(body.contains(this.project.getProjectCode()));
        assert(body.contains(this.project.getName()));
        assert(body.contains(this.project.getDescription()));
        assert(!body.contains("There was a problem"));
        assert(!body.contains("__"));
        assert (!body.contains("N/A"));
    }

}
