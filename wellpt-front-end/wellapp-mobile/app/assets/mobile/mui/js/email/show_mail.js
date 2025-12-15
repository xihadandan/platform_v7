define([ "mui", "commons", "server", "appContext", "appModal", "mui-webmailBox"], function(
		$, commons, server, appContext, appModal, webmailBox) {
	return function(options){
		return webmailBox.showEmail.apply(this, arguments);
	}
});