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
    <h3>Add Research Output</h3>

    <form:form method="POST" commandName="researchOutput" action='add_research_output'>
      <c:if test="${not empty error_message}">
        <div class="errorblock">${error_message}</div>
      </c:if>
      <form:errors path="*" cssClass="errorblock" element="div"/>
      <table cellpadding="5">
        <tr>
          <td>
            Type:<br>
            <form:select path="typeId">
              <form:option value="-1" label="Please Select" />
              <form:options items="${researchOutputTypeMap}" />
            </form:select>
          </td>
        </tr>
        <tr>
          <td>
            Description/Citation:<br>
            <form:textarea path="description" type="text" cols="80" rows="10"/>
            <form:hidden path="projectId"/>
          </td>
        </tr>
      </table>
      <input type="submit" value="Submit">
    </form:form>
  </body>
  
</html>
