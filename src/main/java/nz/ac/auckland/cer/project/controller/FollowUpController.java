package nz.ac.auckland.cer.project.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.util.AffiliationUtil;
import nz.ac.auckland.cer.project.util.EmailUtil;

@Controller
public class FollowUpController {

    private Logger log = Logger.getLogger(FollowUpController.class.getName());
    @Autowired private ProjectDatabaseDao projectDao;
    @Autowired private AffiliationUtil affilUtil;
    @Autowired private EmailUtil emailUtil;

}
