define([ "jquery", "commons", "constant", "server", "appModal","QuickSendMsgDialog" ],
    function($, commons, constant, server, appModal,QuickSendMsgDialog) {
   
    return function(){
        QuickSendMsgDialog.open();
    };
  
});