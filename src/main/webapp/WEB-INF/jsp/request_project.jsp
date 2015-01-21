<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
  <head>
    <meta charset="utf-8">
    <script src="../js/jquery-1.8.3.min.js"></script>
    <link rel="stylesheet" href="../style/common.css" type="text/css" />
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
    	  var val = $("input[name='currentCompEnv']:checked").val();
          if (val == '${compEnv2}' || val == "OTHER") {
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
          if ($("#superviserAffiliation option:selected").val() == "Other") {
              $("#other_superviser_other_affiliation").css("display", "inline");
          } else {
              $("#other_superviser_other_affiliation").css("display", "none");          
          }       
      }

      function show_hide_other_comp_env() {
          var val = $("input[name='currentCompEnv']:checked").val();
          if (val == "OTHER") {
              $("#other_comp_env").css("display", "inline");
            } else {
              $("#other_comp_env").css("display", "none");
            }         
      }

      function show_hide_funding_source() {
          var val = $("input[name='funded']:checked").val();
          if (val == "true") {
              $("#funding_source").css("display", "inline");
            } else {
              $("#funding_source").css("display", "none");
            }         
      }

      $(document).ready(function() {
        // on page load 
        show_hide_other_motivation();
        show_hide_limitations();
        show_hide_other_superviser();
        show_hide_other_superviser_affiliation();
        show_hide_other_comp_env();
        show_hide_funding_source();
        
        // on change events 
        $("input[name='motivation']").change(function() { show_hide_other_motivation(); });
        $("input[name='currentCompEnv']").change(function() { show_hide_limitations(); show_hide_other_comp_env(); });
        $("#superviserId").change(function() { show_hide_other_superviser(); });
        $("#superviserAffiliation").change(function() { show_hide_other_superviser_affiliation(); });
        $("input[name='funded']").change(function() { show_hide_funding_source(); });
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
              Project title (max 160 characters):<br>
              <form:input id="projectTitle" path="projectTitle" type="text" size="100" maxlength="160"/>
            </td>
          </tr>
          <tr>
            <td>
              Project description (500 - 2500 characters):<br>
              <form:textarea id="projectDescription" path="projectDescription" type="text" cols="88" rows="20"/>
            </td>
          </tr>
          <tr>
            <td>
              Field of science:<br>
              <form:select id="scienceStudyId" path="scienceStudyId">
                <form:option value="-1" label="Please select"></form:option>
                <form:options items="${scienceStudies}"/>
                <form:option value="0" label="Other"></form:option>
              </form:select>
              <p>
              If your field of science is not listed, choose "Other" from the bottom of the list. 
              </p>
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
              <form:select id="superviserId" path="superviserId">
                <form:option value="-2" label="Please select"></form:option>
                <form:options items="${superviserDropdownMap}"/>
                <form:option value="-1" label="Other"/>
              </form:select>
              <p>
                If your supervisor is not listed, choose "Other". 
                You will be asked to provide details about your supervisor.
                <div class="infoblock">
                  Please note, that your supervisor will have access to the data generated 
                  in the context of this project.
                </div>
              </p>
              <div id="other_superviser" style="display: none;">
                <p>Please specify details of your supervisor:</p>
                <table cellpadding="5">
                  <tr>
                    <td>
                      Name:<br>
                      <form:input id="superviserName" path="superviserName" type="text" size="50" maxlength="50"/>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      E-mail:<br>
                      <form:input id="superviserEmail" path="superviserEmail" type="text" size="50" maxlength="50"/>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      Phone:<br>
                      <form:input id="superviserPhone" path="superviserPhone" type="text" size="50" maxlength="50"/>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      Affiliation:<br>
                      <form:select id="superviserAffiliation" path="superviserAffiliation">
                        <form:option value="" label="Please select"></form:option>
                        <form:options items="${affiliations}"/>
                      </form:select>
                      <p>
                        If the affiliation of your supervisor is not listed, please choose
                        "Other". You will be asked to provide this information as text.
                      </p>
                      <div id="other_superviser_other_affiliation" style="display: none;">
                         (Division/Faculty and/or Department may remain empty if they don't apply)
                         <table cellpadding="5">
                          <tr>
                            <td>Institution:</td>
                            <td><form:input id="superviserOtherInstitution" path="superviserOtherInstitution"/></td>
                          </tr>
                          <tr>
                            <td>Division or Faculty:</td>
                            <td><form:input id="superviserOtherDivision" path="superviserOtherDivision" /></td>
                          </tr>
                          <tr>
                            <td>Department:</td>
                            <td><form:input id="superviserOtherDepartment" path="superviserOtherDepartment" /></td>
                          </tr>
                        </table>
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
            <td><h3>Motivation for using the cluster for this project</h3></td>
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
                label="I don't know if I need the cluster, but someone recommended using it" />
            </td>
          </tr>
          <tr>
            <td><form:radiobutton name="motivation" path="motivation" value="__OTHER__" label="Other" />
              <div id="other_motivation" style="display: none;">
                <br><br>Please specify:<br>
                <form:input path="otherMotivation" type="text" size="100" maxlength="100" />
              </div>
            </td>  
          </tr>
          
          
          <tr>
            <td colspan="2"></td>
          </tr>
          <tr>
            <td><h3>What is the computational environment available to you now?</h3></td>
          </tr>          
          <tr>
            <td valign="top">
              <form:radiobutton name="currentCompEnv" path="currentCompEnv"
                value="standard_computer"
                label="I have a standard desktop/laptop computer" />
            </td>
          </tr>
          <tr>
            <td valign="top">
              <form:radiobutton name="currentCompEnv" path="currentCompEnv" 
                value="${compEnv2}"
                label="I have access to a cluster or a set of computers to run my jobs on" />
            </td>
          </tr>
          <tr>
            <td valign="top">
              <form:radiobutton name="currentCompEnv" path="currentCompEnv" 
                value="OTHER" label="Other" />
            </td>
          </tr>
          <tr>
            <td>
              <div id="other_comp_env" style="display: none;">
                Please specify:<br>
                <form:input id="otherCompEnv" path="otherCompEnv" type="text" size="100" maxlength="100" />           
              </div>
              <div id="limitations" style="display: none;">
                <p>Please specify your current job submission characteristics:</p>
                <table>
                  <tbody>
                    <tr>
                      <td valign="top">I can currently run jobs using max <form:input id="limitations.cpuCores" path="limitations.cpuCores"
                          size="4" maxlength="4" /> CPU cores.
                      </td>
                    </tr>
                    <tr>
                      <td valign="top">I can currently run jobs using max <form:input id="limitations.memory" path="limitations.memory"
                          size="4" maxlength="4" /> GB memory.
                      </td>
                    </tr>
                    <tr>
                      <td valign="top">I can currently run max <form:input id="limitations.concurrency" path="limitations.concurrency" size="4"
                          maxlength="4" /> jobs concurrently.
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </td>
          </tr>
        </tbody>

        <tr>
          <td>
            <h3>Priority Boost</h3>
            Is this project funded through an external research grant?
            (Projects funded by external sources may be eligible 
            for higher priority on the cluster)
          </td>
        </tr>
        <tr>
          <td valign="top">
            <form:radiobutton name="funded" path="funded" value="true" label="Yes" />
            <div id="funding_source" style="display: none;">
              &nbsp;&nbsp; Please specify: <form:input id="fundingSource" path="fundingSource" type="text" size="80" maxlength="200"/>
            </div>
          </td>
        </tr>
        <tr>
          <td valign="top">
            <form:radiobutton name="funded" path="funded" value="false" label="No" />
          </td>
        </tr>
      </table>
      
      <br>
      <input type="submit" value="Submit">
    </form:form>

  </body>

</html>
