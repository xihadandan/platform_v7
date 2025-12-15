/**
 * 在page端提供wellOfficeAssistant对象
 * 
 * 
 */
(function(global) {
	var mid = 10000;
	var wellOfficeAssistant = {}, callbackOptions = {};
	global.addEventListener("message", function(event) {
		var data = event.data;
		if (event.source == global && data.to == wellOfficeExt.Common.messageDir.page && data.from == wellOfficeExt.Common.messageDir.content) {
			wellOfficeExt.Common.doFilter(event.data, event, {
				position : wellOfficeExt.Common.messageDir.page
			});
		}
	});
	// 
	function doAction(options) {
		options.type = wellOfficeExt.Common.messageType.request
		return wellOfficeExt.Common.doFilter(options, null, {
			position : wellOfficeExt.Common.messageDir.page,
			sendRequest : function(reqest, response) {
				var reqMessage = reqest.buildRequest(wellOfficeExt.Common.messageDir.content);
				global.postMessage(reqMessage, "*");
			}
		});
	}
	// 
	function addAction(action, actionFn) {
		wellOfficeAssistant[action] = function(options) {
			options = options || {};
			var error = options.error;
			var success = options.success;
			var complete = options.complete;
			delete options.error;
			delete options.success;
			delete options.complete;
			return doAction({
				action : action,
				params : options,
				error : error,
				success : success,
				complete : complete
			});
		};
		wellOfficeExt.Common.addFilter(action, actionFn);
	}
	function noConflict() {
		if (global.wellOfficeExt === wellOfficeExt) {
			global.wellOfficeExt = _wellOfficeExt;
		}
		return _wellOfficeExt;
	}
	// 导出wellOfficeAssistant
	wellOfficeAssistant.doAction = doAction;
	wellOfficeAssistant.addAction = addAction;
	wellOfficeAssistant.noConflict = noConflict;
	global.wellOfficeAssistant = wellOfficeAssistant;
}(typeof window !== "undefined" ? window : this));

// 
wellOfficeAssistant.addAction("saveAs");
