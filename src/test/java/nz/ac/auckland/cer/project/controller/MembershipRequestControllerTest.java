package nz.ac.auckland.cer.project.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import static org.mockito.Mockito.*;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.MembershipRequest;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
import nz.ac.auckland.cer.project.util.EmailUtil;
import nz.ac.auckland.cer.project.util.Person;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/MembershipRequestControllerTest-context.xml", "/root-context.xml" })
@WebAppConfiguration
public class MembershipRequestControllerTest {

    @Autowired private WebApplicationContext wac;
    @Autowired private ProjectDatabaseDao projectDao;
    @Autowired private EmailUtil emailUtil;
    private MockMvc mockMvc;
    private MembershipRequest mr;
    private Project p;
    private Person person;

    @Before
    public void setup() throws Exception {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        mr = new MembershipRequest();
        mr.setProjectCode("dummy007");

        this.person = new Person();
        person.setIsResearcher(true);
        person.setFullName("Jane Doe");
        person.setId(42);
        person.setInstitutionalRoleId(1);

        this.p = new Project();
        p.setName("Some title");
        p.setDescription("Some description");
        p.setId(42);
    }

    @Test
    public void testGetSuccess() throws Exception {

        RequestBuilder rb = get("/request_membership").requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_membership")).andExpect(model().hasNoErrors());
    }

    @Test
    public void testGetRedirectIfPersonIsNull() throws Exception {

        RequestBuilder rb = get("/request_membership");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isFound()).andExpect(view().name("redirect:/ceraccount/html/request_account_info"));
    }

    @Test
    public void testGetPersonIsNotResearcher() throws Exception {

        this.person.setIsResearcher(false);
        RequestBuilder rb = get("/request_membership").requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_membership_response"))
                .andExpect(model().attributeExists("error_message"));
    }

    @DirtiesContext
    // required so that checks for method calls via verify work in subsequent
    // tests
    @Test
    public void testPostSuccess() throws Exception {

        when(projectDao.getProjectForCode(anyString())).thenReturn(this.p);
        RequestBuilder rb = post("/request_membership").requestAttr("person", this.person).param("projectCode",
                mr.getProjectCode());
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("request_membership_response"))
                .andExpect(model().attributeErrorCount("membershiprequest", 0));
        verify(projectDao, times(1)).getProjectForCode(this.mr.getProjectCode());
        verify(emailUtil, times(1)).sendMembershipRequestRequestEmail((Project) any(), eq(person.getFullName()));
    }

    @DirtiesContext
    @Test
    public void testPostRedirectIfPersonIsNull() throws Exception {

        RequestBuilder rb = post("/request_membership").param("projectCode", mr.getProjectCode());
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isFound()).andExpect(view().name("redirect:/ceraccount/html/request_account_info"));
        verify(projectDao, times(0)).getProjectForCode(this.mr.getProjectCode());
        verify(emailUtil, times(0)).sendMembershipRequestRequestEmail((Project) any(), eq(person.getFullName()));
    }

}