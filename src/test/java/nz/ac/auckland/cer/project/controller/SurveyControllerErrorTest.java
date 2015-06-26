package nz.ac.auckland.cer.project.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import nz.ac.auckland.cer.common.db.project.dao.ProjectDbDao;
import nz.ac.auckland.cer.common.db.project.pojo.FollowUp;
import nz.ac.auckland.cer.common.db.project.pojo.Project;
import nz.ac.auckland.cer.common.db.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutput;
import nz.ac.auckland.cer.common.db.project.pojo.ResearchOutputType;
import nz.ac.auckland.cer.common.db.project.util.Person;

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
@ContextConfiguration(locations = { "classpath:SurveyControllerTest-context.xml", "classpath:root-context.xml" })
@WebAppConfiguration
public class SurveyControllerErrorTest {

    private MockMvc mockMvc;
    private Project p;
    private ProjectWrapper pw;
    private Person person;
    @Autowired private ProjectDbDao projectDao;
    @Autowired private WebApplicationContext wac;
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
    public void testGetWithoutClusterAccount() throws Exception {

    	String expectedMessage = "Cannot display survey: You don't seem to have a cluster account. "
                + "Only researchers with a cluster account can view the survey.";
        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(this.pw);
        RequestBuilder rb = get("/survey").param("pCode", "42");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(model().attribute("error_message", expectedMessage));
        verify(projectDao, times(0)).addOrUpdateFollowUp((FollowUp) any());
        verify(projectDao, times(0)).addOrUpdateResearchOutput((ResearchOutput) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }


    @Test
    public void testGetWithoutProjectCode() throws Exception {

    	String expectedMessage = "Cannot display survey: No project code specified";
        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(this.pw);
        RequestBuilder rb = get("/survey").requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(model().attribute("error_message", expectedMessage));
        verify(projectDao, times(0)).addOrUpdateFollowUp((FollowUp) any());
        verify(projectDao, times(0)).addOrUpdateResearchOutput((ResearchOutput) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testGetWithoutBeingMember() throws Exception {

    	String expectedMessage = "Cannot display survey: You are not a member of the project covered by this survey";
        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(this.pw);
        when(projectDao.getRolesOnProjectsForResearcher(anyInt())).thenReturn(new HashMap<Integer, String>());
        RequestBuilder rb = get("/survey").param("pCode", this.p.getId().toString()).requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(model().attribute("error_message", expectedMessage));
        verify(projectDao, times(0)).addOrUpdateFollowUp((FollowUp) any());
        verify(projectDao, times(0)).addOrUpdateResearchOutput((ResearchOutput) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testPostEmptyForm() throws Exception {

        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(this.pw);
        when(projectDao.getResearchOutputTypes()).thenReturn(new ResearchOutputType[0]);
        RequestBuilder rb = post("/survey").param("pCode", this.p.getId().toString()).requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk())
            .andExpect(model().attributeErrorCount("survey", 2))
            .andExpect(model().attributeHasFieldErrors("survey", "researchOutcome.researchOutputs", "improvements"));
        //.andDo(print());
        verify(projectDao, times(0)).addOrUpdateFollowUp((FollowUp) any());
        verify(projectDao, times(0)).addOrUpdateResearchOutput((ResearchOutput) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

    @Test
    public void testPostNoResearchOutcomes() throws Exception {

        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(this.pw);
        when(projectDao.getResearchOutputTypes()).thenReturn(new ResearchOutputType[0]);
        RequestBuilder rb = post("/survey")
        		.param("pCode", this.p.getId().toString())
        		.param("improvements", "same")
        		.requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk())
            .andExpect(model().attributeErrorCount("survey", 1))
            .andExpect(model().attributeHasFieldErrors("survey", "researchOutcome.researchOutputs"));
        //.andDo(print());
        verify(projectDao, times(0)).addOrUpdateFollowUp((FollowUp) any());
        verify(projectDao, times(0)).addOrUpdateResearchOutput((ResearchOutput) any());
        assert (this.smtpServer.getReceivedMessages().length == 0);
    }

}
