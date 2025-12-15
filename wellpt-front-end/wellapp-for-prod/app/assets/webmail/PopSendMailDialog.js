define([ "jquery", "commons", "constant", "server", "appModal","QuickSendMailDialog" ], function($, commons, constant, server, appModal,QuickSendMailDialog) {
	
      return function(){
            QuickSendMailDialog.open();
      };
	
});