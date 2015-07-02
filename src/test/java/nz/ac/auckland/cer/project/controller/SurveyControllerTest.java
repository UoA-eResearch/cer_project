package nz.ac.auckland.cer.project.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;

import nz.ac.auckland.cer.common.db.project.dao.ProjectDbDao;
import nz.ac.auckland.cer.common.db.project.pojo.FollowUp;
import nz.ac.auckland.cer.common.db.project.pojo.Project;
import nz.ac.auckland.cer.common.db.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutputType;
import nz.ac.auckland.cer.common.db.project.util.Person;
import nz.ac.auckland.cer.project.util.EmailUtil;

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
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:SurveyControllerTest-context.xml", "classpath:root-context.xml" })
@WebAppConfiguration
public class SurveyControllerTest {

    private MockMvc mockMvc;
    private Project p;
    private ProjectWrapper pw;
    private Person person;
    @Autowired private ProjectDbDao projectDao;
    @Autowired private WebApplicationContext wac;
    @Autowired private EmailUtil eu;
    private GreenMail smtpServer;

    @Before
    public void setup() throws Exception {

        this.smtpServer = new GreenMail(ServerSetupTest.SMTP);
        this.smtpServer.start();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.pw = new ProjectWrapper();
        this.p = new Project();
        p.setId(42);
        p.setName("Project Title");
        StringBuffer pd = new StringBuffer();
        for (int i = 0; i < 500; i++) {
            pd.append("a");
        }
        p.setDescription(pd.toString());
        pw.setProject(p);

        this.person = new Person();
        person.setIsResearcher(true);
        person.setFullName("Jane Doe");
        person.setId(42);
        person.setInstitutionalRoleId(1);        
    }

    @After
    public void tearDown() throws Exception {

        this.smtpServer.stop();
    }

    @Test
    public void testPostSimple() throws Exception {

        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(this.pw);
        when(projectDao.getResearchOutputTypes()).thenReturn(new ResearchOutputType[0]);
        RequestBuilder rb = post("/survey")
        		.param("pCode", this.p.getId().toString())
        		.param("improvements", "same")
        		.param("researchOutcome.hasNoResearchOutput", "true")
        		.param("yourViews.recommendChoice", "Agree")
        		.param("yourViews.meetNeedChoice", "Agree")
        		.param("yourViews.adequateSupportChoice", "Agree")
        		.requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk())
            .andExpect(model().attributeErrorCount("survey", 0));
        //.andDo(print());
        verify(projectDao, times(1)).addOrUpdateFollowUp((FollowUp) any());
        verify(projectDao, times(0)).addOrUpdateResearchOutput((ResearchOutput) any());
        assert (this.smtpServer.getReceivedMessages().length == 1);
        Message m = smtpServer.getReceivedMessages()[0];
        assert (this.eu.getEmailFrom().equals(((InternetAddress) m.getFrom()[0]).toString()));
        assert (this.eu.getEmailTo().equals(((InternetAddress) m.getRecipients(RecipientType.TO)[0]).toString()));
        assert (this.eu.getReplyTo().equals(((InternetAddress) m.getReplyTo()[0]).toString()));
        assert ("2015 CeR annual survey reply".equals(m.getSubject()));
        String body = GreenMailUtil.getBody(m);
        assert (!body.contains("__"));
        assert (body.contains("Performance Improvements:\r\nN/A"));
        assert (body.contains("Future Needs:\r\nN/A"));
        assert (body.contains("Feedback:\r\nN/A"));
        assert (body.contains("Research Outcome:\r\nN/A"));
    }

}
