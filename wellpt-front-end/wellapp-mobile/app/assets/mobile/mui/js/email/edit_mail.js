define([ "mui", "commons", "server", "appContext", "appModal", "mui-webmailBox"], function(
		$, commons, server, appContext, appModal, webmailBox) {
	return function(options){
		options = options || {};
		options.newEmail = true;
		return webmailBox.editEmail.apply(this, arguments);
	}
});