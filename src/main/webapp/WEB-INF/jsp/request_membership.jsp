<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
  <head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style/common.css" type="text/css" />
  </head>

  <body>
    <h3>Ask to join an existing project</h3>

    <form:form method="POST" commandName="membershiprequest" action='request_membership'>
      <c:if test="${not empty unexpected_error}">
        <div id="unexpected_error" class="errorblock">${unexpected_error}</div>
      </c:if>
      <form:errors path="*" cssClass="errorblock" element="div" />
        <table cellpadding="5">
          <tbody>
            <tr>
              <td valign="top">
                <p>
                  Please specify the project code of the project you want to join.
                  If in doubt, please contact the person who asked you to join.
                </p>
                Project code:<br><form:input path="projectCode" type="text" />
              </td>
            </tr>
          </tbody>
        </table>

        <br>
        <input type="submit" value="Submit">

    </form:form>

  </body>
</html>
