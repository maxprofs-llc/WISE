
/*
 * This handles events and calls the appropriate function to handle
 * the event.
 * 
 * TODO: rename highchartsTestDispatcher
 * For example if you are creating a quiz node you would change it to
 * quizDispatcher
 */
View.prototype.highchartsTestDispatcher = function(type,args,obj){
	/*
	 * check to see if the event name matches 
	 * 
	 * TODO: rename highchartsTestUpdatePrompt
	 * wait until you implement the authoring before you rename this
	 */ 
	if(type == 'highchartsTestUpdatePrompt') {
		/*
		 * the event name matches so we will call the function that
		 * handles that event
		 * 
		 * TODO: rename HighchartsTestNode
		 * wait until you implement the authoring before you rename this 
		 */
		obj.HighchartsTestNode.updatePrompt();
	}
};

/*
 * this is a list of events that can be fired. when the event is fired,
 * the dispatcher function above will be called and then call the
 * appropriate function to handle the event.
 */
var events = [
	/*
	 * TODO: rename highchartsTestUpdatePrompt
	 * wait until you implement the authoring before you rename this
	 */
	'highchartsTestUpdatePrompt'
];

/*
 * add all the events to the vle so the vle will listen for these events
 * and call the dispatcher function when the event is fired
 */
for(var x=0; x<events.length; x++) {
	/*
	 * TODO: rename highchartsTestDispatcher
	 * For example if you are creating a quiz node you would change it to
	 * quizDispatcher. The name for the dispatcher should match the function
	 * name at the top of this file.
	 */
	componentloader.addEvent(events[x], 'highchartsTestDispatcher');
};

//used to notify scriptloader that this script has finished loading
if(typeof eventManager != 'undefined'){
	/*
	 * TODO: rename highchartsTest to your new folder name
	 * TODO: rename highchartsTestEvents
	 * 
	 * e.g. if you were creating a quiz step it would look like
	 * 
	 * eventManager.fire('scriptLoaded', 'vle/node/quiz/quizEvents.js');
	 */
	eventManager.fire('scriptLoaded', 'vle/node/highchartsTest/highchartsTestEvents.js');
};