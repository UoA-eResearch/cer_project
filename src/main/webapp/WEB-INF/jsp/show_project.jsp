<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
  <head>
    <meta charset="utf-8">
    <script src="<%=request.getContextPath()%>/js/jquery-1.8.3.js"></script>
    <script src="<%=request.getContextPath()%>/js/jquery-ui.js"></script>
    <script src="<%=request.getContextPath()%>/js/jquery.tablesorter.min.js"></script>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style/common.css" type="text/css" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style/jquery-ui.css" type="text/css" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style/tablesorter/blue/style.css" type="text/css" />
    <script>
      $(document).ready(function() {
        $("#researcherTable").tablesorter({sortList: [[0,0]]});
        $("#researchOutputTable").tablesorter({sortList: [[0,1]]});
        $("#feedbackTable").tablesorter({sortList: [[0,1]]});
      });
    </script>
  </head>

  <body>
    <h3>Project Details</h3>
    
    <div class="infoblock">
      For changes you wish to do, but cannot do here, please send us an e-mail.
    </div>
    
    <c:choose>
      <c:when test="${not empty error_message}">
        <div class="errorblock">
          ${error_message}
        </div>
      </c:when>
      <c:otherwise>
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
        </table>
        
        <h3>Researchers on project</h3>
        <table id="researcherTable" class="tablesorter">
          <thead>
            <tr>
              <th>Name</th>
              <th>Role on Project</th>
            </tr>
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
        
        <h3>Research Output</h3>
        <c:choose>
        <c:when test="${f:length(pw.researchOutputs) gt 0}">
          <table id="researchOutputTable" class="tablesorter">
            <thead>
              <tr>
                <th>Date</th>
                <th>Type</th>
                <th>Description</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${pw.researchOutputs}" var="ro">
                <tr>
                  <td>${ro.date}</td>
                  <td>${ro.type}</td>
                  <td>${ro.description}</td>
                  <td>Edit</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:when>
        <c:otherwise>
          N/A<br>
        </c:otherwise>
        </c:choose>
        Add research output

        <!-- TODO: Display only feedback AFTER the CeR portal has been published -->
        <h3>Feedback</h3>
        <c:choose>
        <c:when test="${f:length(pw.followUps) gt 0}">
          <table id="feedbackTable" class="tablesorter">
            <thead>
              <tr>
                <th>Date</th>
                <th>Notes</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${pw.followUps}" var="fu">
                <tr>
                  <td>${fu.date}</td>
                  <td>${fu.notes}</td>
                  <td>Edit</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:when>
        <c:otherwise>
          N/A<br>
        </c:otherwise>
        </c:choose>
        Add Feedback

      </c:otherwise>
    </c:choose>
  </body>

</html>
