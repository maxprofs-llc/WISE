define(['configService'], function(configService) {

    var service = ['$http', '$rootScope', '$websocket', 'ConfigService', 'CurrentNodeService', 'StudentDataService',
                   function($http, $rootScope, $websocket, ConfigService, CurrentNodeService, StudentDataService) {
        var serviceObject = {};
        
        serviceObject.dataStream = null;
        
        /**
         * Initialize the websocket connection
         */
        serviceObject.initialize = function() {
            
            // get the mode
            var mode = ConfigService.getConfigParam('mode');
            
            if (mode === 'preview') {
                // we are previewing the project
            } else {
                // we are in a run
                
                // get the parameters for initializing the websocket connection
                var runId = ConfigService.getRunId();
                var periodId = ConfigService.getPeriodId();
                var workgroupId = ConfigService.getWorkgroupId();
                var webSocketURL = ConfigService.getWebSocketURL();
                webSocketURL += "?runId=" + runId + "&periodId=" + periodId + "&workgroupId=" + workgroupId;
                
                // start the websocket connection
                this.dataStream = $websocket(webSocketURL);
                
                this.dataStream.onMessage(function(message) {
                   console.log('message received: ' + message.data); 
                });
            }
        };
        
        /**
         * Send the student status to the server through websockets
         */
        serviceObject.sendStudentStatus = function() {
            
            var mode = ConfigService.getConfigParam('mode');
            
            if (mode !== 'preview') {
                // we are in a run
                
                // get the current node id
                var currentNodeId = CurrentNodeService.getCurrentNodeId();
                
                // get the node statuses
                var nodeStatuses = StudentDataService.getNodeStatuses();
                
                // get the latest node visit
                var latestCompletedNodeVisit = StudentDataService.getLatestCompletedNodeVisit();
                
                // make the websocket message
                var messageJSON = {};
                messageJSON.messageType = 'studentStatus';
                messageJSON.messageParticipants = 'studentToTeachers';
                messageJSON.currentNodeId = currentNodeId;
                messageJSON.previousNodeVisit = latestCompletedNodeVisit;
                messageJSON.nodeStatuses = nodeStatuses;
                
                // send the websocket message
                this.dataStream.send(messageJSON);
            }
        };
        
        return serviceObject;
    }];
    
    return service;
});