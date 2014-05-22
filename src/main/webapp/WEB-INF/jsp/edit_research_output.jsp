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
    <h3>Edit Research Output</h3>

    <form:form method="POST" commandName="researchOutput" action='edit_research_output'>
      <c:if test="${not empty error_message}">
        <div class="errorblock">${error_message}</div>
      </c:if>
      <form:errors path="*" cssClass="errorblock" element="div"/>
      <table cellpadding="5">
        <tr>
          <td>
            Type:<br>
            <form:select path="typeId">
              <form:options items="${researchOutputTypeMap}" />
            </form:select>
          </td>
        </tr>
        <tr>
          <td>
            Description/Citation:<br>
            <form:textarea path="description" type="text" cols="80" rows="10"/>
            <form:hidden path="id"/>
            <form:hidden path="date"/>
            <form:hidden path="adviserId"/>
            <form:hidden path="projectId"/>
          </td>
        </tr>
      </table>
      <input type="submit" value="Submit">
      <input type="reset" value="Reset">
    </form:form>
  </body>
  
</html>
