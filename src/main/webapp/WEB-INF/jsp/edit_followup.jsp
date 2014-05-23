<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
  <head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="../style/common.css" type="text/css" />
  </head>

  <body>
    <h3>Edit feedback</h3>

    <form:form method="POST" commandName="followUp" action='edit_followup'>
      <c:if test="${not empty error_message}">
        <div class="errorblock">${error_message}</div>
      </c:if>
      <form:errors path="*" cssClass="errorblock" element="div"/>
      <table cellpadding="5">
        <tr>
          <td>
            Notes:<br>
            <form:textarea value="${followup}" path="notes" type="text" cols="80" rows="10"/>
            <form:hidden path="id"/>
            <form:hidden path="date"/>
            <form:hidden path="adviserId"/>
            <form:hidden path="researcherId"/>
            <form:hidden path="projectId"/>
            <form:hidden path="adviserName"/>
          </td>
        </tr>
      </table>
      <input type="submit" value="Submit">
      <input type="reset" value="Reset">
    </form:form>
  </body>
  
</html>
