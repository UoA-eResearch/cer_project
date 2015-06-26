package nz.ac.auckland.cer.project.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import nz.ac.auckland.cer.common.db.project.dao.ProjectDbDao;
import nz.ac.auckland.cer.common.db.project.pojo.Adviser;
import nz.ac.auckland.cer.common.db.project.pojo.Researcher;
import nz.ac.auckland.cer.common.db.project.util.Person;
import nz.ac.auckland.cer.common.util.AuditLog;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * TODO: Send e-mail if expected request attributes are not there
 */
public class AdminFilter implements Filter {

    @Autowired private ProjectDbDao pdDao;
    @Autowired private AuditLog auditLog;
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
            String eppn = (String) request.getAttribute("eppn");
            String o = (String) request.getAttribute("o");
            flog.info(auditLog.createAuditLogMessage(request, "eppn=" + eppn + " cn=\"" + cn +"\" shared-token=" + sharedToken));
            if (cn == null || sharedToken == null || o == null) {
                log.error("At least one mandatory Tuakiri attribute is null: cn='" + cn + "', shared-token='" + sharedToken + "', o='" + o + "'");
            }
            Researcher r = this.pdDao.getResearcherForTuakiriSharedToken(sharedToken);
            if (r == null && eppn != null) {
            	r = this.pdDao.getResearcherForEppn(eppn);
            }
            Adviser a = this.pdDao.getAdviserForTuakiriSharedToken(sharedToken);
            if (a == null && eppn != null) {
            	a = this.pdDao.getAdviserForEppn(eppn);
            }
            boolean isResearcher = (r == null) ? false : true;
            boolean hasPersonRegistered = (a == null && r == null) ? false : true;
            request.setAttribute("hasPersonRegistered", hasPersonRegistered);
            if (hasPersonRegistered) {
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
