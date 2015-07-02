<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
  <head>
    <meta charset="utf-8">
    <script src="../js/jquery-1.8.3.min.js"></script>
    <script src="../js/jquery.tablesorter.min.js"></script>
    <link rel="stylesheet" href="../style/common.css" type="text/css" />
    <link rel="stylesheet" href="../style/survey.css" type="text/css" />
    <link rel="stylesheet" href="../style/tablesorter/theme.default.css" type="text/css" />
    <script>
        function show_hide_perf_spec(prop) {
            var div_name = "#" + prop + "_div";
        	if ($("#improv_" + prop).is(':checked')) {
                $('#improv_same').removeAttr('checked');
                $(div_name).fadeTo("fast",1.0);
                $(div_name).find('textarea,:text,:checkbox').prop("disabled", false);
            } else {
                $(div_name).fadeTo("fast", 0.4);
                $(div_name).find('textarea,:text,:checkbox').prop("disabled", true);
                $(div_name).find('textarea,:text').val('');
                $(div_name).find(':checkbox').removeAttr('checked');
            }    	
        }
        
        function on_no_perf_improvements() {
        	if ($('#improv_same').is(':checked')) {
                $('#improv_faster').removeAttr('checked');
                $('#improv_bigger').removeAttr('checked');
                $('#improv_more').removeAttr('checked');
                show_hide_perf_spec("faster");
                show_hide_perf_spec("bigger");
                show_hide_perf_spec("more");        		
        	}
        }
        
        function on_add_row_click() {
        	$('#addResearchOutputRow').val("1");
        	$('#form').submit();
        }
        
        $(document).ready(function() {
          $("#researchOutputTable").tablesorter({theme:'default', sortList: [[0,1]]});
          $("#researcherTable").tablesorter({theme:'default', sortList: [[1,0]]});
          
          show_hide_perf_spec("faster");
          show_hide_perf_spec("bigger");
          show_hide_perf_spec("more");
          
          // on change events 
          $("#improv_faster").change(function() { show_hide_perf_spec("faster"); });
          $("#improv_bigger").change(function() { show_hide_perf_spec("bigger"); });
          $("#improv_more").change(function() { show_hide_perf_spec("more"); });
          $("#improv_same").change(function() { on_no_perf_improvements(); });
          
          $('#doAddResearchOutputRow').click(on_add_row_click);
          if ($('#addResearchOutputRow').val() == "1") {
              location.href="#ro";
              $('#addResearchOutputRow').val('0');
          }
      });
    </script>
  </head>

  <body>


    <c:choose>
      <c:when test="${not empty error_message}">
        <div class="errorblock">
          ${error_message}
        </div>
      </c:when>
      <c:otherwise>
    
      
      <form:form id="form" method="POST" commandName="survey" action='survey'>
            
        <h2>Project Survey</h2>
  
        <form:errors path="*" cssClass="errorblock" element="div" />
      
        <table id="projectTable" cellpadding="5">
          <tr>
            <td valign="top"><nobr><b>Project Title</b>:</nobr></td>
            <td>${pw.project.name}</td>
          </tr>
          <tr>
            <td valign="top"><nobr><b>Project Description</b>:</nobr></td>
            <td>${pw.project.description}</td>
          </tr>
          <tr>
            <td valign="top"><nobr><b>Researchers</b>:</nobr></td>
            <td>
              <table id="researcherTable" class="tablesorter">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Role on project</th>
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
            
            </td>
          </tr>
          <tr>
            <td valign="top"><nobr><b>Project Code</b>:</nobr></td>
            <td>${pw.project.projectCode}</td>
          </tr>
        </table>

        <p/>
      
        <form:hidden path="projectCode"/>
        <form:hidden id="addResearchOutputRow" path="addResearchOutputRow"/>
      
        <!-- Performance Improvements -->
        <div class="survey-section">
        <div class="survey-section-header">1. Performance Improvements</div>
        <div class="survey-section-description">
          Please tick the appropriate boxes if the NeSI Auckland Pan cluster or the 
          Centre for eResearch Research VM Farm enables you to run more, larger or faster jobs. 
          More than one box could apply to you.<br/>
          If this is the first time we have asked for your feedback, please think of 
          this section as <i>Performance improvements compared to using your desktop 
          environment, or machines available in your lab</i>.<br/>
          Otherwise, please specify improvements since last year's survey.
        </div>
        <div class="survey-section-body">
            
        <table cellpadding="10" cellspacing="2">
          <tr>

            <td valign="top">
              <form:checkbox id="improv_more" path="improvements" 
                  value="more" label="I can run more jobs"/>
              <div id="more_div">
                <hr>
                <p>
                  I can run up to
                  <form:input path="perfImpMore.number" size="5" maxlength="5"/>
                  jobs at the same time, which is 
                  <form:input path="perfImpMore.factor" size="5" maxlength="5"/>
                  times more concurrent jobs than before
                </p>
              </div>
            </td>

            <td valign="top">
              <form:checkbox id="improv_faster" path="improvements" 
                  value="faster" label="The code runs faster"/>
              <div id="faster_div">
                <hr>
                <p>
                  My jobs run
                  <form:input path="perfImpFaster.factor" size="5" maxlength="5"/>
                  times faster than before, thanks to:
                </p>
                <p>
                  <form:checkboxes items="${survey.perfImpFaster.optionStrings}" 
                      path="perfImpFaster.options" 
                      delimiter="<br/>"/>
                </p>
                <p>
                  Other reason:<br/>
                  <form:textarea path="perfImpFaster.otherReason" rows="3" cols="40"/>
                </p>
              </div>
            </td>
          
            <td valign="top">
              <form:checkbox id="improv_bigger" path="improvements" 
                  value="bigger" label="I can run larger problems"/>
              <div id="bigger_div">
                <hr>
                <p>
                  I can simulate larger problems now, up to
                  <form:input path="perfImpBigger.factor" size="5" maxlength="5"/>
                  times larger than before, thanks to:
                </p>
                <p>
                  <form:checkboxes items="${survey.perfImpBigger.optionStrings}" 
                      path="perfImpBigger.options" 
                      delimiter="<br/>"/>
                </p>
                <p>
                    Other reason:<br/>
                    <form:textarea path="perfImpBigger.otherReason" 
                        rows="3" cols="40"/>
                </p>
              </div>
            </td>

          </tr>
      
          <tr>
            <td colspan="3">
              <form:checkbox id="improv_same" path="improvements" 
                  value="same" label="No improvements"/>
            </td>
          </tr>
        </table>
        </div>
        </div>


      
        <!-- Research Outcomes -->
        <br/><br/>
        <div class="survey-section">
        <div class="survey-section-header">2. Research Outcomes</div>
        <div class="survey-section-description">
          In order to demonstrate our value to the institution, we need to provide evidence of research
          outcomes that we have in some way enabled via our services.<br/>
          Please list here any relevant research outcomes (publications, conference proceedings,
          talks, posters, etc) since your last review, or tick the <i>There are no new research outputs
          to list for this project</i> checkbox.
        </div>
        <div class="survey-section-body">
        <table>
          <c:forEach items="${survey.researchOutcome.researchOutputs}" var="ro" varStatus="i" begin="0">
            <tr>
              <td valign="top">
                <form:select path="researchOutcome.researchOutputs[${i.index}].typeId">
                  <form:option value="-1" label="Please select research output type" />
                  <form:options items="${researchOutputTypeMap}" />
                </form:select>
              </td>
              <td>
                <form:textarea path="researchOutcome.researchOutputs[${i.index}].description" 
                    placeholder="Add the description/citation here" 
                    rows="3" cols="100"/>        
              </td>
            </tr>
          </c:forEach>
          <tr>
            <td colspan="2">
              <form:input id="doAddResearchOutputRow" path="addResearchOutputRow" 
                  type="submit" 
                  value="Add new blank row to add more research outputs"/>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <br/>
              <form:checkbox path="researchOutcome.hasNoResearchOutput" value="true" label="There are no new research outputs to list for this project"/>
            </td>
          </tr>
        </table>

        <c:if test="${f:length(pw.researchOutputs) > 0}">
        <p>
          <b>List of research outcomes we already have registered for this project:</b><br/>
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
                  <td><nobr>${ro.type}&nbsp;</nobr></td>
                  <td>${ro.description}&nbsp;</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </p>
        </c:if>
        </div>
        </div>



        <!-- Your views -->
        <br/><br/>
        <div class="survey-section">
        <div class="survey-section-header">3. Your Views</div>
        <div class="survey-section-description">
          Please indicate how much you agree with the following statements about the
          NeSI Auckland Pan cluster and the Centre for eResearch Research VM Farm.<br>
          <i>If you are using only one of those services at the moment,
             rate your experience based on the service you are familiar with.</i>
        </div>
        <div class="survey-section-body">
          <table cellpadding="3" cellspacing="2" class="fixed">
            <col/>
            <c:forEach var="choice" items="${survey.yourViews.choices}">
              <col width="80px" />
            </c:forEach>
           	<tr>
           	  <td>&nbsp;</td>
           	  <c:forEach var="choice" items="${survey.yourViews.choices}">
           	    <td style='text-align: center'>${choice.value}</td>
           	  </c:forEach>
           	</tr>
           	<tr>
           	  <td>${survey.yourViews.recommend}</td>
              <form:radiobuttons path="yourViews.recommendChoice" 
                  items="${survey.yourViews.choices}" 
                  itemValue="value" itemLabel="label" 
                  element="td style='text-align: center'"/>
            </tr>
            <tr>
              <td>${survey.yourViews.meetNeed}</td> 	
              <form:radiobuttons path="yourViews.meetNeedChoice"
                  items="${survey.yourViews.choices}" 
                  itemValue="value" itemLabel="label" 
                  element="td style='text-align: center'"/>
            </tr>
            <tr>
              <td>${survey.yourViews.adequateSupport}</td>
              <form:radiobuttons path="yourViews.adequateSupportChoice" 
                  items="${survey.yourViews.choices}" 
                  itemValue="value" itemLabel="label" 
                  element="td style='text-align: center'"/>          
          </table>
        </div>
        </div>
    
        <!-- Future needs -->
        <br/><br/>
        <div class="survey-section">
        <div class="survey-section-header">4. Anticipated Future Needs (optional)</div>
        <div class="survey-section-description">
          Your feedback in this section will help us make decisions on future hardware purchases.
        </div>
        <div class="survey-section-body">
          To be able to conduct my research on this project, or to scale my research further, I will need
          <table cellpadding="10" cellspacing="2">
            <tr>
              <td valign="top">
                <p>
                  <form:checkboxes items="${survey.futureNeeds.optionStrings}"
                      path="futureNeeds.options"
                      delimiter="<br/>"/>
                </p>
                <p>
                  Other:<br/>
                  <form:textarea path="futureNeeds.otherReason" rows="5" cols="100"/>
                </p>
              </td>
            </tr>
          </table>
      </div>
      </div>
      

      
      <!-- Feedback -->
      <br/><br/>
      <div class="survey-section">
      <div class="survey-section-header">5. Feedback (optional)</div>
      <div class="survey-section-description">
        What could we do to improve your ability to conduct research?
        Your feedback will help us improve the service we provide.
      </div>
      <div class="survey-section-body">
          Feedback:<br/>
          <form:textarea path="feedback.feedback" rows="8" cols="100"/>
      </div>
      </div>
      
      
      <p>
        <form:checkbox id="getBackToMe" path="getBackToMe" 
            value="True" label="I would like you to follow up with me"/>
      </p>
      
      <p>
        <input type="submit" value="Submit Survey">
      </p>
    
    </form:form>
    
    </c:otherwise>
    </c:choose>
    
  </body>

</html>
