<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
  <head>
    <meta charset="utf-8">
    <script src="<%=request.getContextPath()%>/js/jquery-1.8.3.js"></script>
    <script src="<%=request.getContextPath()%>/js/jquery-ui.js"></script>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style/common.css" type="text/css" />
    <link rel="stylesheet" href="<%=request.getContextPath()%>/style/jquery-ui.css" type="text/css" />
    <script type="text/javascript">
    
      <c:set var="compEnv2" value="cluster_or_set_of_computers"/>

      function show_hide_other_motivation() {
          if ($("input[name='motivation']:checked").val() == "__OTHER__") {
              $("#other_motivation").css("display", "inline");
          } else {
              $("#other_motivation").css("display", "none");
          }       
      }
      
      function show_hide_limitations() {
          if ($("input[name='currentEnv']:checked").val() == '${compEnv2}') {
              $("#limitations").css("display", "inline");
            } else {
              $("#limitations").css("display", "none");
            }         
      }
      
      function show_hide_other_superviser() {
          if ( $("#superviserId option:selected").val() == "-1") {
              $("#other_superviser").css("display", "inline");
          } else {
              $("#other_superviser").css("display", "none");          
          }       
      }
      
      function show_hide_other_superviser_affiliation() {
          if ($("#superviserAffiliation option:selected").val() == "OTHER") {
              $("#other_superviser_affiliation").css("display", "inline");
          } else {
              $("#other_superviser_affiliation").css("display", "none");          
          }       
      }
      
      $(document).ready(function() {
        // on page load 
        show_hide_other_motivation();
        show_hide_limitations();
        show_hide_other_superviser();
        show_hide_other_superviser_affiliation();
        
        // on change events 
        $("input[name='motivation']").change(function() { show_hide_other_motivation(); });
        $("input[name='currentEnv']").change(function() { show_hide_limitations(); });
        $("#superviserId").change(function() { show_hide_other_superviser(); });
        $("#superviserAffiliation").change(function() { show_hide_other_superviser_affiliation(); });
      });
    </script>
  </head>

  <body>
    <h3>Request a new project</h3>
    <p><b>Please provide the following details</b></p>
    <form:form method="POST" commandName="projectrequest" action='request_project'>
      <c:if test="${not empty unexpected_error}">
        <div id="unexpected_error" class="errorblock">${unexpected_error}</div>
      </c:if>

      <form:errors path="*" cssClass="errorblock" element="div" />
      <table cellpadding="5">
        <tbody>
          <tr>
            <td>
              Project title (max 160 characters. No special characters please):<br>
              <form:input path="projectTitle" type="text" size="100" maxlength="160"/>
            </td>
          </tr>
          <tr>
            <td>
              Project description (100 - 2500 characters. No special characters please):<br>
              <form:textarea path="projectDescription" type="text" cols="88" rows="20"/>
            </td>
          </tr>

        <!-- supervisor information -->
        <c:if test="${projectrequest.askForSuperviser}">
          <tr>
            <td colspan="2"></td>
          </tr>
          <tr>
            <td colspan="2"><h3>Supervisor information</h3></td>
          </tr>
          <tr>
            <td colspan="2">
              Please select your supervisor:<br>
              <form:hidden path="askForSuperviser" value="${projectrequest.askForSuperviser}"/>
              <form:select path="superviserId">
                <form:option value="-2" label="Please Select"></form:option>
                <form:options items="${superviserDropdownMap}"/>
                <form:option value="-1" label="Other"/>
              </form:select>
              <p>
                If your supervisor is not listed, choose "Other" at the end of the list 
                and specify details.<br>
                Please note, that your supervisor will have access to the data generated 
                in the context of this project.
              </p>
              <div id="other_superviser" style="display: none;">
                <p>Please specify:</p>
                <table cellpadding="5">
                  <tr>
                    <td>
                      Name:<br>
                      <form:input path="superviserName" type="text" size="50" maxlength="50"/>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      E-mail:<br>
                      <form:input path="superviserEmail" type="text" size="50" maxlength="50"/>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      Phone:<br>
                      <form:input path="superviserPhone" type="text" size="50" maxlength="50"/>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      Affiliation:<br>
                      <form:select path="superviserAffiliation">
                        <form:option value="" label="Please Select"></form:option>
                        <form:options items="${affiliations}"/>
                        <form:option value="OTHER" label="Other"/>
                      </form:select>
                      <p>
                        If the affiliation of your supervisor is not listed, choose "Other" at the end of the list and specify.
                      </p>
                      <div id="other_superviser_affiliation" style="display: none;">
                        Please specify:<br>
                        <form:input path="superviserOtherAffiliation" type="text" size="50" maxlength="50"/>
                      </div>
                    </td>
                  </tr>
                </table>
              </div>
            </td>
          </tr>
        </c:if>
       
          <!--  survey -->
          <tr>
            <td colspan="2"></td>
          </tr>
          <tr>
            <td><h3>Motivation for using the cluster for this project:</h3></td>
          </tr>
          <tr>
            <td><form:radiobutton name="motivation" path="motivation" 
                value="inadequate_computational_equipment"
                label="I have inadequate computational equipment to run my computations" />
            </td>
          </tr>
          <tr>
            <td><form:radiobutton name="motivation" path="motivation" 
                value="free_up_my_computer"
                label="I could run the computations on my laptop/desktop computer, but it would block me from doing other work on the computer" />
            </td>
          </tr>
          <tr>
            <td><form:radiobutton name="motivation" path="motivation" 
                value="on_recommendation"
                label="I don't really need the cluster, but someone recommended using it" />
            </td>
          </tr>
          <tr>
            <td><form:radiobutton name="motivation" path="motivation" value="__OTHER__" label="Other" />
              <div id="other_motivation" style="display: none;">
                <br> <br>Please specify:<br>
                <form:input path="otherMotivation" type="text" size="100" maxlength="100" />
              </div>
            </td>  
          </tr>
          
          
          <tr>
            <td colspan="2"></td>
          </tr>
          <tr>
            <td><h3>What is the computational environment to your availability now?</h3></td>
          </tr>          
          <tr>
            <td valign="top"><form:radiobutton name="currentEnv" path="currentEnv"
                value="standard_computer"
                label="I have a standard desktop/laptop computer" /></td>
          </tr>
          <tr>
            <td valign="top">
              <form:radiobutton name="currentEnv" path="currentEnv" 
                value="${compEnv2}"
                label="I have access to a small cluster or a set of computers to run my jobs on" />

              <div id="limitations" style="display: none;">
                <p>Please specify your current job submission characteristics:</p>
                <table>
                  <tbody>
                    <tr>
                      <td valign="top">I can currently run jobs using max <form:input path="limitations.cpuCores"
                          size="4" maxlength="4" /> CPU cores.
                      </td>
                    </tr>
                    <tr>
                      <td valign="top">I can currently run jobs using max <form:input path="limitations.memory"
                          size="4" maxlength="4" /> GB memory.
                      </td>
                    </tr>
                    <tr>
                      <td valign="top">I can currently run max <form:input path="limitations.concurrency" size="4"
                          maxlength="4" /> jobs concurrently.
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      
      <br>
      <input type="submit" value="Submit">
    </form:form>

  </body>

</html>