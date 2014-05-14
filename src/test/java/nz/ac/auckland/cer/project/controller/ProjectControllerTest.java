package nz.ac.auckland.cer.project.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Arrays;
import java.util.LinkedList;

import static org.mockito.Mockito.*;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Project;
import nz.ac.auckland.cer.project.pojo.ProjectWrapper;
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
@ContextConfiguration(locations = { "classpath:ProjectControllerTest-context.xml", "classpath:root-context.xml" })
@WebAppConfiguration
public class ProjectControllerTest {

    @Autowired private WebApplicationContext wac;
    @Autowired private ProjectDatabaseDao projectDao;
    private MockMvc mockMvc;
    private Person person;
    private Project project;
    private ProjectWrapper projectWrapper;

    @Before
    public void setup() throws Exception {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.person = new Person();
        person.setIsResearcher(true);
        person.setFullName("Jane Doe");
        person.setId(42);
        person.setInstitutionalRoleId(1);

        this.projectWrapper = new ProjectWrapper();
        this.project = new Project();
        project.setName("Some title");
        project.setDescription("Some description");
        project.setId(42);
        projectWrapper.setProject(project);
    }

    @DirtiesContext
    @Test
    public void showProjects_GetSuccess() throws Exception {

        when(projectDao.getProjectsOfResearcher(anyInt())).thenReturn(new LinkedList<Project>(Arrays.asList(project)));
        RequestBuilder rb = get("/view_projects").requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("show_projects"))
                .andExpect(model().attributeExists("projects"));
        verify(projectDao, times(1)).getProjectsOfResearcher(eq(this.person.getId()));
    }

    @Test
    public void showProjects_GetRedirectIfPersonIsNull() throws Exception {

        RequestBuilder rb = get("/view_projects");
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isFound()).andExpect(view().name("redirect:/test"));
        verify(projectDao, times(0)).getProjectsOfResearcher(anyInt());
    }

    @Test
    public void showProjects_GetPersonIsNotResearcher() throws Exception {

        this.person.setIsResearcher(false);
        RequestBuilder rb = get("/view_projects").requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("show_projects"))
                .andExpect(model().attributeExists("error_message"));
        verify(projectDao, times(0)).getProjectsOfResearcher(anyInt());
    }

    @DirtiesContext
    @Test
    public void showProjects_ExceptionWhenFetchingProjects() throws Exception {

        when(projectDao.getProjectsOfResearcher(anyInt())).thenThrow(
                new Exception("This exception is intended in tests."));
        RequestBuilder rb = get("/view_projects").requestAttr("person", this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("show_projects"))
                .andExpect(model().attributeExists("error_message"));
        verify(projectDao, times(1)).getProjectsOfResearcher(this.person.getId());
    }

    @DirtiesContext
    @Test
    public void showProject_GetSuccess() throws Exception {

        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(projectWrapper);
        RequestBuilder rb = get("/view_project").param("id", Integer.toString(project.getId())).requestAttr("person",
                this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("show_project"))
                .andExpect(model().attributeExists("pw"));
        verify(projectDao, times(1)).getProjectForIdOrCode(Integer.toString(project.getId()));
    }

    @Test
    public void showProject_MissingParameter() throws Exception {

        when(projectDao.getProjectForIdOrCode(anyString())).thenReturn(projectWrapper);
        RequestBuilder rb = get("/view_project").requestAttr("person",
                this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("show_project"))
                .andExpect(model().attributeExists("error_message"));
        verify(projectDao, times(0)).getProjectForIdOrCode(anyString());
    }

    @Test
    public void showProject_GetRedirectIfPersonIsNull() throws Exception {

        RequestBuilder rb = get("/view_project").param("id", Integer.toString(project.getId()));
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isFound()).andExpect(view().name("redirect:/test"));
        verify(projectDao, times(0)).getProjectForIdOrCode(anyString());
    }

    @Test
    public void showProject_GetPersonIsNotResearcher() throws Exception {

        this.person.setIsResearcher(false);
        RequestBuilder rb = get("/view_project").param("id", Integer.toString(project.getId())).requestAttr("person",
                this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("show_project"))
                .andExpect(model().attributeExists("error_message"));
        verify(projectDao, times(0)).getProjectForIdOrCode(anyString());
    }

    @DirtiesContext
    @Test
    public void showProject_ExceptionWhenFetchingProjects() throws Exception {

        when(projectDao.getProjectForIdOrCode(anyString())).thenThrow(
                new Exception("This exception is intended in tests."));
        RequestBuilder rb = get("/view_project").param("id", Integer.toString(project.getId())).requestAttr("person",
                this.person);
        ResultActions ra = this.mockMvc.perform(rb);
        ra.andExpect(status().isOk()).andExpect(view().name("show_project"))
                .andExpect(model().attributeExists("error_message"));
        verify(projectDao, times(1)).getProjectForIdOrCode(Integer.toString(project.getId()));
    }

}
