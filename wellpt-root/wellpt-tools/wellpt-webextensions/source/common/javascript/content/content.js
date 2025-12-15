var wellOfficeExt = wellOfficeExt || {}; // eslint-disable-line
// no-use-before-define

wellOfficeExt.Content = wellOfficeExt.Content || {};

// 向页面注入JS
function injectCustomJs(jsPaths, success, error) {
	jsPath = jsPaths.shift();
	var jsScript = document.getElementById(jsPath);
	if (jsScript && jsScript.src) {
		// 防止多次注入
		return console.log("jsPath[" + jsPath + "] injected, ignore!");
	}
	jsScript = document.createElement('script');
	jsScript.setAttribute('id', jsPath);
	jsScript.setAttribute('type', 'text/javascript');
	// 获得的地址类似：chrome-extension://ihcokhadfjfchaeagdoclpnjdiokfakg/inject-scripts/page-script.js
	jsScript.src = browser.extension.getURL(jsPath);
	jsScript.onerror = error;
	jsScript.onload = function() {
		var self = this;
		// 放在页面不好看，执行完后移除掉
		self.parentNode.removeChild(self);
		if (jsPaths.length == 0) {
			success.apply(self, arguments);
		} else {
			injectCustomJs(jsPaths, success, error);
		}
	};
	document.head.appendChild(jsScript);
}
injectCustomJs([ 'common/javascript/common.js', 'page/page.js' ], function() {

});

browser.runtime.onMessage.addListener(function(message, sender, sendResponse){
	return new Promise(function (resolve, reject) {
		wellOfficeExt.Common.doFilter(message, sender, {
			sendResponse : function(result, reqest){
				resolve(result);
			},
			position : wellOfficeExt.Common.messageDir.content
		});
	});
});

window.addEventListener("message", function(event) {
	var data = event.data || {};
	if (event.source == window && data.to == wellOfficeExt.Common.messageDir.content && data.from == wellOfficeExt.Common.messageDir.page) {
		wellOfficeExt.Common.doFilter(event.data, event, {
			position : wellOfficeExt.Common.messageDir.content,
			sendResponse : function(result, reqest){
				var reqMessage = reqest.buildResponse(result);
				window.postMessage(reqMessage, "*");
			},
			sendRequest : function(reqest, response) {
				var to = reqest.getParam("to");
				var reqMessage = reqest.buildRequest(to || wellOfficeExt.Common.messageDir.background);
				var sending = browser.runtime.sendMessage(reqMessage);
				sending.then(function(result) {
					response.success(result);
				}).catch(function(err) {
					response.error(err);
				});
			}
		});
	}
});
