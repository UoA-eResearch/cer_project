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
                $(div_name).find('input, textarea, select').prop("disabled", false);
            } else {
                $(div_name).fadeTo("fast",0.2);
                $(div_name).find('input, textarea, select').prop("disabled", true);
                $(div_name).find('input, textarea').val('');
                $(div_name).find('input, textarea, select').removeAttr('checked');
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

    <h2>Project Survey</h2>

    <c:choose>
      <c:when test="${not empty error_message}">
        <div class="errorblock">
          ${error_message}
        </div>
      </c:when>
      <c:otherwise>
    
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
      
  
      <form:form id="form" method="POST" commandName="survey" action='survey'>
      
        <form:errors path="*" cssClass="errorblock" element="div" />
        <form:hidden path="projectCode"/>
        <form:hidden id="addResearchOutputRow" path="researchOutcome.addResearchOutputRow"/>
      
        <!-- Performance Improvements -->
        <div class="survey-section">
        <div class="survey-section-header">1. Performance Improvements</div>
        <div class="survey-section-description">
          Please tick the appropriate boxes if the cluster enables you to run more, larger or faster jobs. 
          More than one box could apply to you.<br>
          If this is the first time we have asked for your feedback, please think of this section as <i>Performance improvements
          compared to using your desktop environment, or machines available in your lab</i>.<br>
          Otherwise, please specify improvements since last year's survey.
        </div>
        <div class="survey-section-body">
            
        <table cellpadding="10" cellspacing="2">
          <tr>
            <td valign="top">
              <form:checkbox id="improv_faster" path="improvements" value="faster" label="My jobs run faster"/>
              <div id="faster_div">
                <hr>
                <p>
                  My jobs run <form:input path="faster.factor" size="5" maxlength="5"/> times faster
                  than before, thanks to:
                </p>
                <p>
                  <form:checkbox path="faster.reasons" value="sharedMemPar" label="Shared memory parallelisation"/><br>
                  <form:checkbox path="faster.reasons" value="distMemPar" label="Distributed memory parallelisation"/><br>
                  <form:checkbox path="faster.reasons" value="algorithmOptimisation" label="Algorithmic improvements to my software"/><br>
                  <form:checkbox path="faster.reasons" value="buildOptimisation" label="Compile-time optimization of my software"/><br>
                  <form:checkbox path="faster.reasons" value="dontKnow" label="I don't know and need help figuring out why"/><br>
                  <p>
                    Other reason:<br>
                    <form:textarea path="faster.other" rows="3" cols="40"/>
                  </p>
                </p>
              </div>
            </td>
          
            <td valign="top" bgcolor="#fff">
              <form:checkbox id="improv_bigger" path="improvements" value="bigger" label="I can run larger jobs"/>
              <div id="bigger_div">
                <hr>
                <p>
                  I can run larger jobs now, up to <form:input path="bigger.factor" size="5" maxlength="5"/> times
                  larger than before, thanks to:
                </p>
                <p>
                  <form:checkbox path="bigger.reasons" value="moreMem" label="More memory available"/><br>
                  <form:checkbox path="bigger.reasons" value="distMemPar" label="Distributed memory parallelisation"/><br>
                  <form:checkbox path="bigger.reasons" value="moreDisk" label="More disk space"/><br>
                  <form:checkbox path="bigger.reasons" value="dontKnow" label="I don't know and need help figuring out why"/><br>
                  <p>
                    Other reason:<br>
                    <form:textarea path="bigger.other" rows="3" cols="40"/>
                  </p>
                </p>
              </div>
            </td>

            <td valign="top" bgcolor="#fff">
              <form:checkbox id="improv_more" path="improvements" value="more" label="I can run more jobs"/>
              <div id="more_div">
                <hr>
                <p>
                  I can run up to <form:input path="more.number" size="5" maxlength="5"/> jobs
                  at the same time, which is <form:input path="more.factor" size="5" maxlength="5"/>
                  times more concurrent jobs than before
                </p>
              </div>
            </td>
      
          </tr>
      
          <tr>
            <td colspan="3" bgcolor="#fff">
              <form:checkbox id="improv_same" path="improvements" value="same" label="No improvements"/>
            </td>
          </tr>
        </table>
        </div>
        </div>


      
        <!-- Research Outcomes -->
        <br><br>
        <a id="ro"/>
        <div class="survey-section">
        <div class="survey-section-header">2. Research Outcomes</div>
        <div class="survey-section-description">
          In order to demonstrate our value to the institution, we need to provide evidence of research
          outcomes that we have in some way enabled via our services.<br>
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
                <form:textarea path="researchOutcome.researchOutputs[${i.index}].description" placeholder="Add the description/citation here" rows="3" cols="100"/>        
              </td>
            </tr>
          </c:forEach>
          <tr>
            <td colspan="2">
              <form:input id="doAddResearchOutputRow" path="researchOutcome.addResearchOutputRow" type="submit" value="Add new blank row to add more research outputs"/>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <br>
              <form:checkbox path="researchOutcome.noResearchOutput" value="1" label="There are no new research outputs to list for this project"/>
            </td>
          </tr>
        </table>

        <c:if test="${f:length(pw.researchOutputs) > 0}">
        <p>
          <b>List of research outcomes we already have registered for this project:</b><br>
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
      


        <!-- Future needs -->
        <br><br>
        <div class="survey-section">
        <div class="survey-section-header">3. Anticipated Future Needs (optional)</div>
        <div class="survey-section-description">
          Your feedback in this section will help us make decisions on future hardware purchases.
        </div>
        <div class="survey-section-body">
          To be able to conduct my research on this project, or to scale my research further, I will need
          <table cellpadding="10" cellspacing="2">
            <tr>
              <td valign="top" bgcolor="#fff">
                <p>
                  <form:checkbox path="futureNeeds.comments" value="moreCpus" label="More CPU cores per machine to run larger multi-threaded jobs"/><br>
                  <form:checkbox path="futureNeeds.comments" value="fastInterconnect" label="Fast interconnect between the machines for my MPI jobs"/><br>
                  <form:checkbox path="futureNeeds.comments" value="moreGpus" label="More GPUs"/><br>
                  <form:checkbox path="futureNeeds.comments" value="morePhis" label="More Intel Xeon Phi's"/><br>
                  <form:checkbox path="futureNeeds.comments" value="moreMemory" label="More memory per machine"/><br>
                  <form:checkbox path="futureNeeds.comments" value="moreDisk" label="More disk space"/><br>
                  <form:checkbox path="futureNeeds.comments" value="shorterWaitTimes" label="Shorter wait times"/><br>
                  <form:checkbox path="futureNeeds.comments" value="moreScalingAdvice" label="Advice on how to parallelise/scale/tune my software"/><br>
                  <form:checkbox path="futureNeeds.comments" value="moreSupport" label="More general support around the cluster"/>
                </p>
            
                <p>
                  Other:<br>
                  <form:textarea path="futureNeeds.other" rows="5" cols="100"/>
                </p>
              </td>
            </tr>
          </table>
      </div>
      </div>
      

      
      <!-- Feedback -->
      <br><br>
      <div class="survey-section">
      <div class="survey-section-header">4. Feedback (optional)</div>
      <div class="survey-section-description">
        Your feedback will help us improve the service we provide.
      </div>
      <div class="survey-section-body">
      
          Feedback:<br>
          <form:textarea path="feedback.feedback" rows="8" cols="100"/>
      </div>
      </div>
      
      
      <p>
        <input type="submit" value="Submit Survey">
      </p>
    </form:form>
    
    </c:otherwise>
    </c:choose>
    
  </body>

</html>
