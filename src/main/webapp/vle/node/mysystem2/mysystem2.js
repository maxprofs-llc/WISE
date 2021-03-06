/*globals MYSYSTEM2STATE MySystem eventManager */
/**
 * This is the constructor for the object that will perform the logic for
 * the step when the students work on it. An instance of this object will
 * be created in the .html for this step (look at mysystem.html)
 */
function Mysystem2(node,view,secondTime) {
  this.node = node;
  this.view = node.view;
  this.content = node.getContent().getContentJSON();

  this.domIO = document.getElementById('my_system_state');
  
  if (typeof node.studentWork != 'undefined' && node.studentWork !== null) {
    this.states = node.studentWork; 
    
    MySystem.registerExternalSaveFunction(this.saveTriggeredByMySystem, this);
    
    this.node.view.eventManager.subscribe('processPostResponseComplete', this.saveSuccessful);
    // MySystem is set by default to save every 20 seconds. If we want to change that frequency, we can call
    // MySystem.setAutoSaveFrequency(20000)
  } 
  else {
    this.states = [];
  }
  
  this.mostRecentSavedState = null;
  this.hasRegisteredDomListeners = false;
  if(secondTime == true) {
    this.hasRegisteredDomListeners = true;
  }
}

Mysystem2.prototype.saveTriggeredByMySystem = function(isSubmit) {
  this.save(isSubmit);

  // save back to the server. In a single node visit, this will use the same
  // node visit id each time, so it will save the (growing) stack back to the same
  // place each time. 
  this.node.view.postCurrentNodeVisit(this.node.view.getState().getCurrentNodeVisit());
};

/**
 * This function renders everything the student sees when they visit the step.
 * This includes setting up the html ui elements as well as reloading any
 * previous work the student has submitted when they previously worked on this
 * step, if any.
 */
Mysystem2.prototype.render = function() {
	var workToImport = [],
      latestResponse = null,
      initialDiagram = null,
      tagMaps  = null;
	
  var filterIntialDiagram = function(obj) {
    return {
      'MySystem.Link': obj['MySystem.Link'], 
      'MySystem.Node': obj['MySystem.Node']
    };
  }
	//process the tag maps if we are not in authoring mode
	if(this.view.authoringMode == null || !this.view.authoringMode) {
		//get the tag map results
		tagMapResults = this.processTagMaps();
		workToImport = tagMapResults.workToImport;
	}
	
  var latestState = this.getLatestState();
  if (latestState !== null) {
    /*
     * get the response from the latest state. the response variable is
     * just provided as an example. you may use whatever variables you
     * would like from the state object (look at templatestate.js)
     */
    latestResponse = latestState.response;
    this.domIO.textContent = latestResponse;
  }
  if(workToImport.length > 0) {
	//get the student work
	var studentWork = workToImport[workToImport.length - 1];
	  
	if(studentWork != null) {
		//get the node we are importing from
		var importFromNode = studentWork.node;

		if(importFromNode != null) {
			//check if we can import work from the other node type
			if (this.node.canImportWork(importFromNode)) {

				if (importFromNode.type == "SVGDrawNode") {
					//the other node is a draw node

					//get the student work svg string
					var studentWorkSVGString = importFromNode.getStudentWorkSVGString(studentWork);

					//make an svg element from the svg string
					var studentWorkSVGElement = $(studentWorkSVGString);
					
					//get the children of the svg element
					var children = studentWorkSVGElement.children();
					
					//append the children to the svg
					$('.diagram-background svg').append(children);
				} else {
					/*
					 * the student has not done any work for this step and
					 * there is work to import so we will use the work to import
					 * but lets filter it first, only including the Links and Nodes.
					 */
					initialDiagram = JSON.parse(studentWork.response);
					initialDiagram = filterIntialDiagram(initialDiagram);
					if (latestState == null) {
						latestResponse = JSON.stringify(initialDiagram);
						this.domIO.textContent = latestResponse;
					}
				}
			}
		}
	}
  }
  
  // It turns out that sometimes when firebug is enabled and reloading
  // the page and switching back to the step: The SC.onReady.done method is never called
  // which should mean the jquery ready method is never called either.  
  // It seems this has to do with how the step iframe is setup. Its contents are injected
  // instead of loading it from a url.  So far the approach below seems to fix this problem
  // and not cause other problems.
  if(!SC.isReady) {
    SC.onReady.done();
  }

  // This is the authoring content:
  if (this.content) {
    // not sure why we are getting called when its not a
    // mysystem 2 state -- but it happens.
    if (this.content['type'] === "mysystem2") {
      if (initialDiagram) {
        this.content['initialDiagramJson'] = JSON.stringify(initialDiagram);
      }
      MySystem.loadWiseConfig(this.content,latestState);
    }
  }

  if (latestState) {
    MySystem.updateFromDOM();
  }
  eventManager.fire('mySystemPreviewFrameLoaded');
  
  // TODO: Do we know if we are in preview mode?
  this.keepStudentLogedIn();
};

Mysystem2.prototype.keepStudentLogedIn = function() {
  // only register dom listeners once...
  if (!this.hasRegisteredDomListeners) {
    var lastRenewal = 0;
    var interval    = 30; // seconds
      if (typeof eventManager != 'undefined') {
      // watch for changes to the student data and renew the session whenever it changes
      $('#my_system_state').bind("DOMSubtreeModified", {thisView:this.view}, function(event) {
        var now = new Date().getTime();
        var state = $('#my_system_state').text();
        var elapsed = (now - lastRenewal) / 1000;
        if (elapsed > interval) {  // only renew at most once every interval seconds
          SC.Logger.log("renewing session (" + elapsed + "s)");
          lastSate = state;
          event.data.thisView.sessionManager.renewSession();
          lastRenewal = now;
        }
      });
    }
    this.hasRegisteredDomListeners = true;
  }
};

/**
 * This function retrieves the latest student work
 *
 * @return the latest state object or null if the student has never submitted
 * work for this step
 */
Mysystem2.prototype.getLatestState = function() {
  var latestState = null,
      state,
      i,
      numberOfOwnProperties = function (obj) {
        var p, n = 0;

        for (p in obj) {
          if (obj.hasOwnProperty(p)) n++;
        }
        return n;
      };
      
  if (this.states) {
    for (i = this.states.length - 1; i >= 0; i--) {
      state = this.states[i];
      // because of WISE4 corruption issues, reject states that may have been saved to our states array
      // by non-MySystem steps such as openresponse steps
      if (state.type === "mysystem2" || (typeof state.type === 'undefined' && numberOfOwnProperties(state) === 1)) {
        latestState = state;
        break;
      }
    }
  }
  
  return latestState;
};

// this gets called as part of the window.onExit function, called by Wise2 when
// we leave a step, and allows us to perform any final or cleanup work before
// we save.
Mysystem2.prototype.preSave = function() {
  MySystem.preExternalSave();
};

/**
 * This function retrieves the student work from the html ui, creates a state
 * object to represent the student work, and then saves the student work.
 * 
 * note: you do not have to use 'studentResponseTextArea', they are just 
 * provided as examples. you may create your own html ui elements in
 * the .html file for this step (look at mysystem.html).
 */
Mysystem2.prototype.save = function(isSubmit) {
  var response,
      state;

  // sometimes we are called without a dom element, in which case,
  // return immediately...
  if (this.domIO === null) { return; }
  
  // We use a simple dom element for our data passing
  response =this.domIO.textContent;
  
  /*
   * only create a new student state object 
   * if there isn't one already or if the last one was a submit so it
   * shouldn't be changed. 
   */
  if(!this.mostRecentSavedState || this.mostRecentSavedState.isSubmit) {
    this.mostRecentSavedState = state = new MYSYSTEM2STATE(response, isSubmit);

    /*
     * fire the event to push this state to the global view.states object.
     * the student work is saved to the server once they move on to the
     * next step.
     */
    this.view.pushStudentWork(this.node.id, state);

    // push the state object into this or object's own copy of states
    this.states.push(state);
  } else {
    this.mostRecentSavedState.response = response;
    this.mostRecentSavedState.isSubmit = isSubmit;
    state = this.mostRecentSavedState;
  }

  if (response != null) {
    // get the latest node visit
    var nodeVisit = this.view.getState().getCurrentNodeVisit();
    
    // get the response as a JSON object
    var responseJSON = JSON.parse(response);
    
    // process any teacher notifications if necessary
    this.processTeacherNotifications(nodeVisit, state, responseJSON);
  }
};

/**
 * Process any teacher notifications if necessary
 * @param nodeVisit the node visit
 * @param state the node state
 * @param response the response that is saved in the node state
 */
Mysystem2.prototype.processTeacherNotifications = function(nodeVisit, nodeState, response) {
    
    if (nodeVisit != null && nodeState != null && response != null) {
        
        if (nodeState.isSubmit) {
            // the student is submitting their work
            
            // get the step content
            var stepContent = this.node.content.getContentJSON()
            
            // get teacher notifications from the step content
            var teacherNotifications = stepContent.teacherNotifications;
                
            if (teacherNotifications != null) {
                
                /*
                 * loop through all the teacher notifications for this step
                 * and check if we need to activate any of them
                 */
                for (var t = 0; t < teacherNotifications.length; t++) {
                    // get a teacher notification
                    var teacherNotification = teacherNotifications[t];
                    
                    if (teacherNotification != null) {
                        var teacherNotificationType = teacherNotification.type;
                        
                        if (teacherNotificationType === 'attemptScore') {
                            /*
                             * this teacher notification becomes active when the
                             * student gets a certain score on a certain attempt
                             * number
                             */
                            
                            var score = null;
                            var attemptNumber = null;
                            
                            var rubricScore = response['MySystem.RubricScore'];
                            var ruleFeedback = response['MySystem.RuleFeedback'];
                            
                            if (rubricScore != null && ruleFeedback != null) {
                                
                                // get the score the student received
                                var lastScoreId = rubricScore['LAST_SCORE_ID'];
                                if (lastScoreId != null) {
                                    score = lastScoreId['score'];
                                }
                                
                                // get the student's number of attempts
                                var lastFeedback = ruleFeedback['LAST_FEEDBACK'];
                                if (lastFeedback != null) {
                                    attemptNumber = lastFeedback['numOfSubmits'];
                                }
                            }
                            
                            if (score != null && attemptNumber != null) {
                                /*
                                 * get the number of attempts and score that
                                 * will activate the teacher notification
                                 */
                                var teacherNotificationAttemptNumber = teacherNotification.attemptNumber;
                                var teacherNotificationScore = teacherNotification.score;
                                
                                if (attemptNumber === teacherNotificationAttemptNumber) {
                                    // the attempt number matches
                                    
                                    if (score != null && score.toString().match("[" + teacherNotificationScore + "]")) {
                                        // the score matches the score we are looking for
                                        
                                        /*
                                         * create a new notification annotation and associate it
                                         * with the current node visit
                                         */
                                        var newTeacherNotification = this.view.createTeacherNotificationAnnotationValue(teacherNotification, nodeState);
                                        newTeacherNotification.attemptNumber = attemptNumber;
                                        newTeacherNotification.score = score;
                                        this.view.addNotificationAnnotation(nodeVisit, newTeacherNotification);
                                    }
                                }
                            }
                        } else if (teacherNotificationType === 'minTotalTimeSpentOnStep') {
                            
                            this.view.handleMinTotalTimeSpentOnStepTeacherNotification(
                                    this.node.id, teacherNotification, nodeVisit, nodeState);

                        } else if (teacherNotificationType === 'maxTotalTimeSpentOnStep') {
                            
                            this.view.handleMaxTotalTimeSpentOnStepTeacherNotification(
                                    this.node.id, teacherNotification, nodeVisit, nodeState);
                            
                        }
                    }
                }
            }
        }
    }
};

Mysystem2.prototype.wise4InitiatedSave = function() {
  // only save a new state if the data has changed
  if(window['MySystem'] !== undefined && MySystem.savingController.get("dataIsDirty")){
    mysystem2.preSave();
    mysystem2.save(false);
  }
};

// we subscribe this function to the eventManager's processPostResponseComplete event,
// so it ought to get called after postCurrentNodeVisit receives a success message from the post
Mysystem2.prototype.saveSuccessful = function () {
  // it would be great if we could unsubscribe from the event manager when we leave the step.
  // as we can't, our subscription hangs around, even if we are in some other step.
  // Check we have a MySystem first
  if (window['MySystem'] !== undefined){
    window['MySystem'].externalSaveSuccessful(true);
  }
};


/**
 * Process the tag maps and obtain the results
 * @return an object containing the results from processing the
 * tag maps. the object contains three fields
 * enableStep
 * message
 * workToImport
 */
Mysystem2.prototype.processTagMaps = function() {
	var enableStep = true;
	var message = '';
	var workToImport = [];
	
	//the tag maps
	var tagMaps = this.node.tagMaps;
	
	//check if there are any tag maps
	if(tagMaps != null) {
		
		//loop through all the tag maps
		for(var x=0; x<tagMaps.length; x++) {
			
			//get a tag map
			var tagMapObject = tagMaps[x];
			
			if(tagMapObject != null) {
				//get the variables for the tag map
				var tagName = tagMapObject.tagName;
				var functionName = tagMapObject.functionName;
				var functionArgs = tagMapObject.functionArgs;
				
				if(functionName == "importWork") {
					//get the work to import
					workToImport = this.node.getWorkToImport(tagName, functionArgs);
				} else if(functionName == "showPreviousWork") {
					//show the previous work in the previousWorkDiv
					this.node.showPreviousWork($('#previousWorkDiv'), tagName, functionArgs);
				} else if(functionName == "checkCompleted") {
					//we will check that all the steps that are tagged have been completed
					
					//get the result of the check
					var result = this.node.checkCompleted(tagName, functionArgs);
					enableStep = enableStep && result.pass;
					
					if(message == '') {
						message += result.message;
					} else {
						//message is not an empty string so we will add a new line for formatting
						message += '<br>' + result.message;
					}
				}
			}
		}
	}
	
	if(message != '') {
		//message is not an empty string so we will add a new line for formatting
		message += '<br>';
	}
	
	//put the variables in an object so we can return multiple variables
	var returnObject = {
		enableStep:enableStep,
		message:message,
		workToImport:workToImport
	};
	
	return returnObject;
};

//used to notify scriptloader that this script has finished loading
if(typeof eventManager != 'undefined'){
	eventManager.fire('scriptLoaded', 'vle/node/mysystem2/mysystem2.js');
}