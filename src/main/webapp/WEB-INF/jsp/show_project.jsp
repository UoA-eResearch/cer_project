<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
  <head>
    <meta charset="utf-8">
    <script src="<%=request.getContextPath()%>/js/jquery-1.8.3.min.js"></script>
    <script src="<%=request.getContextPath()%>/js/jquery.tablesorter.min.js"></script>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style/common.css" type="text/css" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style/tablesorter/theme.default.css" type="text/css" />
    <script>
      $(document).ready(function() {
        $("#researcherTable").tablesorter({theme:'default', sortList: [[0,0]]});
        $("#researchOutputTable").tablesorter({theme:'default', sortList: [[0,1]]});
        $("#feedbackTable").tablesorter({theme:'default', sortList: [[0,1]]});
      });
    </script>
  </head>

  <body>
    <h3>Project Details</h3>
    
    <c:choose>
      <c:when test="${not empty error_message}">
        <div class="errorblock">
          ${error_message}
        </div>
      </c:when>
      <c:otherwise>
      <!-- 
        <div class="infoblock">
          Please send us an e-mail for changes you wish to do, but cannot do here.
        </div>
      -->
        <table id="projectTable" cellpadding="5">
          <tr>
            <td valign="top"><nobr><b>Title</b>:</nobr></td>
            <td>${pw.project.name}</td>
          </tr>
          <tr>
            <td valign="top"><nobr><b>Description</b>:</nobr></td>
            <td>${pw.project.description}</td>
          </tr>
          <tr>
            <td valign="top"><nobr><b>Code</b>:</nobr></td>
            <td>${pw.project.projectCode}</td>
          </tr>
          <tr>
            <td valign="top"><nobr><b>Status</b>:</nobr></td>
            <td>${pw.project.statusName}</td>
          </tr>
          <tr>
            <td valign="top"><nobr><b>First Day</b>:</nobr></td>
            <td>${pw.project.startDate}</td>
          </tr>
          <tr>
            <td valign="top"><nobr><b>Last Day</b>:</nobr></td>
            <td>${pw.project.endDate}</td>
          </tr>
        </table>


        <h3>Researchers on project</h3>
          <table id="researcherTable" class="tablesorter">
            <thead>
              <th>Name</th>
              <th>Role on project</th>
            </thead>
            <tbody>
              <c:forEach items="${pw.rpLinks}" var="rpLink">
                <tr>
                  <td>${rpLink.researcher.fullName}</td>
                  <td>${rpLink.researcherRoleName}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
          <br>
           
        <h3>Research Output</h3>
            <table id="researchOutputTable" class="tablesorter">
              <thead>
                <tr>
                  <th><nobr>Date added</nobr></th>
                  <th>Type</th>
                  <th>Description</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach items="${pw.researchOutputs}" var="ro">
                  <tr>
                    <td>${ro.date}&nbsp;</td>
                    <td>${ro.type}&nbsp;</td>
                    <td><a href="edit_research_output?pid=${ro.projectId}&rid=${ro.id}">${ro.description}</a>&nbsp;</td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
        
        <p>
          <form action="add_research_output" method="GET" style="display: inline;">
            <input type="submit" value="Add Research Output"/>
            <input type="hidden" name="pid" value="${pw.project.id}"/>
          </form>
        </p>
        <br>

        <h3>Feedback</h3>
            <table id="feedbackTable" class="tablesorter">
              <thead>
                <tr>
                  <th><nobr>Date added</nobr></th>
                  <th>Last updated by</th>
                  <th>Notes</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach items="${pw.followUps}" var="fu">
                  <!--  IMPORTANT: Only show entries made by researchers. Hide those from advisers -->
                  <c:if test="${not empty fu.researcherId}">
                    <tr>
                      <td>${fu.date}&nbsp;</td>
                      <td>${fu.researcherName}&nbsp;</td>
                      <td><a href="edit_followup?pid=${fu.projectId}&fid=${fu.id}">${fu.notes}</a>&nbsp;</td>
                    </tr>
                  </c:if>
                </c:forEach>
              </tbody>
            </table>
        
        <p>
          <form action="add_followup" method="GET" style="display: inline;">
            <input type="submit" value="Add Feedback"/>
            <input type="hidden" name="pid" value="${pw.project.id}"/>
          </form>
        </p>
      
      </c:otherwise>
    </c:choose>
  </body>

</html>
