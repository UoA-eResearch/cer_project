package nz.ac.auckland.cer.project.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import nz.ac.auckland.cer.project.util.AuditUtil;
import nz.ac.auckland.cer.project.dao.ProjectDatabaseDao;
import nz.ac.auckland.cer.project.pojo.Adviser;
import nz.ac.auckland.cer.project.pojo.Researcher;
import nz.ac.auckland.cer.project.util.Person;

/*
 * TODO: Send e-mail if expected request attributes are not there
 */
public class AdminFilter implements Filter {

    @Autowired private ProjectDatabaseDao pdDao;
    @Autowired private AuditUtil auditUtil;
    private Logger log = Logger.getLogger(AdminFilter.class.getName());
    private Logger flog = Logger.getLogger("file." + AdminFilter.class.getName());

    public void doFilter(
            ServletRequest req,
            ServletResponse resp,
            FilterChain fc) throws IOException, ServletException {

        try {
            HttpServletRequest request = (HttpServletRequest) req;
            String sharedToken = (String) request.getAttribute("shared-token");
            String cn = (String) request.getAttribute("cn");
            flog.info(auditUtil.createAuditLogMessage(request, "cn=\"" + cn +"\" shared-token=" + sharedToken));
            if (cn == null || sharedToken == null) {
                log.error("At least one required Tuakiri attribute is null: cn='" + cn + "', shared-token=" + sharedToken);
            }
            Researcher r = this.pdDao.getResearcherForTuakiriSharedToken(sharedToken);
            Adviser a = this.pdDao.getAdviserForTuakiriSharedToken(sharedToken);
            boolean hasPersonRegistered = (a == null && r == null) ? false : true;
            request.setAttribute("hasPersonRegistered", hasPersonRegistered);
            if (hasPersonRegistered) {
                boolean isResearcher = (r == null) ? false : true;
                request.setAttribute("person", isResearcher ? new Person(r) : new Person(a));
            }
        } catch (final Exception e) {
            log.error("Unexpected error in AdminFilter", e);
            return;
        }
        fc.doFilter(req, resp);
    }

    public void init(
            FilterConfig fc) throws ServletException {

    }

    public void destroy() {

    }

}
