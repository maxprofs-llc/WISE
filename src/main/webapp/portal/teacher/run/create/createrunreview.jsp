<%@ include file="../../../include.jsp"%>

<!DOCTYPE html>
<html dir="${textDirection}">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="chrome=1" />
<link rel="shortcut icon" href="${contextPath}/<spring:theme code="favicon"/>" />
<title><spring:message code="teacher.run.create.createrunreview.settingUpAProjectRunStep5" /></title>

<link href="${contextPath}/<spring:theme code="jquerystylesheet"/>" rel="stylesheet" type="text/css" />
<link href="${contextPath}/<spring:theme code="globalstyles"/>" media="screen" rel="stylesheet"  type="text/css" />
<link href="${contextPath}/<spring:theme code="stylesheet"/>" media="screen" rel="stylesheet" type="text/css" />
<link href="${contextPath}/<spring:theme code="teacherprojectstylesheet" />" media="screen" rel="stylesheet" type="text/css" />
<link href="${contextPath}/<spring:theme code="teacherhomepagestylesheet" />" media="screen" rel="stylesheet" type="text/css" />
<link href="${contextPath}/<spring:theme code="teacherrunstylesheet"/>" media="screen" rel="stylesheet"  type="text/css" />
<link href="${contextPath}/<spring:theme code="superfishstylesheet"/>" rel="stylesheet" type="text/css" >
<c:if test="${textDirection == 'rtl' }">
    <link href="${contextPath}/<spring:theme code="rtlstylesheet"/>" rel="stylesheet" type="text/css" >
</c:if>
    
<script src="${contextPath}/<spring:theme code="jquerysource"/>" type="text/javascript"></script>
<script src="${contextPath}/<spring:theme code="jqueryuisource"/>" type="text/javascript"></script>
<script src="${contextPath}/<spring:theme code="superfishsource"/>" type="text/javascript"></script>
<script src="${contextPath}/<spring:theme code="generalsource"/>" type="text/javascript"></script>

<script type="text/javascript">
        var doneClicked=false;

    	//preload image if browser is not IE because animated gif will just freeze if user is using IE
    	if(navigator.appName != "Microsoft Internet Explorer") {
    		loadingImage = new Image();
    		loadingImage.src = "${contextPath}/themes/default/images/rel_interstitial_loading.gif";
    	}
        
    	/**
		 * copies project and then create run with the new project
		 * @param pId project id
		 * @param projectWiseVersion integer version of project [4,5] not null.
		 * @param type the project type e.g. "LD"
		 * @param name the project name
		 * @param fileName the project file name e.g. "wise4.project.json"
		 * @return true iff project was successfully copied. 
		 */
    	function createRun(pID, projectWiseVersion, type, projectName, fileName) {
        	// ensure that project doesn't get copied multiple times
        	if (!doneClicked) {
            	doneClicked = true;
                if (projectWiseVersion == 5) {
                    return true;  // if this is WISE5 project, a new project and a run will be created on the server-side
                } else {
                    // if this is a WISE4 project, a new project will be created via ajax requests on this page and the new
                    // run will be created using the new project on the server-side.
                    var result = copy(pID, type, projectName, fileName);
                    if (!result) {
                        alert('<spring:message code="teacher.run.create.createrunreview.errorCreatingRun" />');
                    }
                    return result;
                }
        	}
    	};
    	
    	/**
		 * asynchronously copies project
		 * @param pId project id
		 * @param type the project type e.g. "LD"
		 * @param name the project name
		 * @param fileName the project file name e.g. "wise4.project.json"
		 * @return true iff project was successfully copied. 
		 */
        function copy(pID, type, projectName, fileName) {
            var isSuccess = false;
            var newProjectId = null;
   			if (type == 'LD') {
   	   			//calls filemanager to copy project folder contents
   	   			$.ajax({
   	   	   				url: '${contextPath}/author/authorproject.html',
   	   	   	   			async: false,
   	   	   	   			type:"POST",
   	   	   	   			data:'forward=filemanager&projectId=' + pID + '&command=copyProject',
   	   	   	   			dataType:'text',
   	   	   	   			success: function(returnData){
   							/*
							 * returnData is the new project folder
							 * e.g.
							 * 513
							 */
							
							/*
							 * get the relative project file path for the new project
							 * e.g.
							 * /513/wise4.project.json
							 */ 
   							var projectPath = '/' + returnData + '/' + fileName;
   							
   							//call to make the project on the portal with the new folder
   							$.ajax({
   	   							url:"${contextPath}/author/authorproject.html",
   	   							async:false,
   	   							type:"POST",
   	   							data:{"command":"createProject","parentProjectId":pID,"projectPath":projectPath,"projectName":projectName},
   	   							dataType:'text',
   	   							success:function(returnData){
   	   								isSuccess = true;
   	   								newProjectId = returnData;
   	   								$("#newProjectId").attr("value", newProjectId);
									//alert('The LD project has been successfully copied with the name Copy of ' + projectName + '. The project can be found in the My Projects section.');
								},
   	   	   						error:function(returnData){alert('<spring:message code="teacher.run.create.createrunreview.projectCopiedButNotRegistered" />');}
   							});
   						},
   	   	   	   	   		error:function(returnData){alert('<spring:message code="teacher.run.create.createrunreview.couldNotCopyProjectFolder" />');}
   	   			});
			    return isSuccess;   	   			
   			};
    	};
    	
   	// Set up view project details click action for each project id link
	$('a.projectDetail').on('click',function(){
		var title = $(this).attr('title');
		var projectId = $(this).attr('id').replace('projectDetail_','');
		var path = "${contextPath}/projectInfo?projectId=" + projectId;
		var div = $('#projectDetailDialog').html('<iframe id="projectIfrm" width="100%" height="100%"></iframe>');
		div.dialog({
			width: '800',
			height: '400',
			title: title,
			position: 'center',
			close: function(){ $(this).html(''); },
			buttons: {
				Close: function(){
					$(this).dialog('close');
				}
			}
		});
		$("#projectDetailDialog > #projectIfrm").attr('src',path);
	});
</script>
</head>
<body>
<div id="pageWrapper">
	<%@ include file="../../../headermain.jsp"%>
	<div id="page">
		<div id="pageContent">
			<div class="contentPanel">
				<div class="panelHeader">
					<spring:message code="teacher.run.create.createrunreview.setupAClassroomRun" />
					<span class="pageTitle"><spring:message code="teacher.run.create.createrunreview.management"/></span>
				</div>
				<div class="panelContent">
					<div id="reviewRunBox">
						<div id="stepNumber" class="sectionHead"><spring:message code="teacher.run.create.createrunreview.step5Of5"/>&nbsp;<spring:message code="teacher.run.create.createrunreview.reviewTheProject"/></div>
						<div class="sectionContent">
	
							<h5 style="color:red;"><spring:message code="teacher.run.create.createrunreview.effectiveWhenTeacherFamiliarWithContent" htmlEscape="true" /></h5>
	
							<ol>
								<li><spring:message code="teacher.run.create.createrunreview.please"/> <a id="projectDetail_${projectId}" class="projectDetail" title="Project Details"><spring:message code="teacher.run.create.createrunreview.reviewTheProjectDetails"/></a>&nbsp;<spring:message code="teacher.run.create.createrunreview.beforeRunningProject"/></li>
							
								<li><spring:message code="teacher.run.create.createrunreview.weHighlyRecommend"/> <a href="<c:url value="/previewproject.html"><c:param name="projectId" value="${projectId}"/></c:url>" target="_blank">
										<spring:message code="teacher.run.create.createrunreview.previewTheProject"/></a> <spring:message code="teacher.run.create.createrunreview.beforeRunningInClassroom"/></li>
									
								<li><spring:message code="teacher.run.create.createrunreview.firstTimeCarryingRun"/></li>
							</ol>
	
							<h5><spring:message code="teacher.run.create.createrunreview.whenYoureReady"/></h5>
						</div>
					</div>
					<form:form method="post" modelAttribute="runParameters" class="center" onSubmit="return createRun('${projectId}','${projectWiseVersion}','${projectType}','${projectName}','${projectJSONFilename}')">
						<input type="submit" name="_cancel" value="<spring:message code="teacher.run.create.createrunreview.cancel" />" />
						<input type="submit" id="submit_form" name="_finish" value="<spring:message code="teacher.run.create.createrunreview.done" />" />
						<input type="hidden" id="newProjectId" name="newProjectId" value="" />
					</form:form>
				</div>
			</div>
		</div>
		<div style="clear: both;"></div>
		<div id="projectDetailDialog" style="overflow:hidden;" class="dialog"></div>
	</div>   <!-- End of page-->
	<%@ include file="../../../footer.jsp"%>
</div>
</body>
</html>
