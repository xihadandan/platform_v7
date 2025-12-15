(function() {
	if (window.XuntongJSBridge) {
		// Android加上了这个if判断，如果当前window已经定义了XuntongBridge对象，不再重新加载
		// 避免重新初始化_callback_map等变量，导致之前的消息回调失败，返回cb404
		// alert('window already has a XuntongBridge object!!!');
		return;
	}
	;
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////本地调用的实际逻辑////////////////////////////////////////////
	var _CUSTOM_PROTOCOL_SCHEME = 'xuntong', callbacksCount = 1, callbacks = {};
	function _handleMessageFromXT(callbackId, message) {
		try {
			var callback = callbacks[callbackId];
			if (!callback)
				return;
			callback.apply(null, [ message ]);
		} catch (e) {
			alert(e)
		}
	}

	// Use this in javascript to request native objective-c code
	// functionName : string (I think the name is explicit :p)
	// args : array of arguments
	// callback : function with n-arguments that is going to be called when the
	// native code returned
	function _call(functionName, message, callback) {

		var hasCallback = callback && typeof callback == "function";
		var callbackId = hasCallback ? callbacksCount++ : 0;

		if (hasCallback)
			callbacks[callbackId] = callback;

		var iframe = document.createElement("IFRAME");
		iframe.setAttribute("src", _CUSTOM_PROTOCOL_SCHEME + ":" + functionName
				+ ":" + callbackId + ":"
				+ encodeURIComponent(JSON.stringify(message)));
		// For some reason we need to set a non-empty size for the iOS6
		// simulator...
		iframe.setAttribute("style", "width:0;height:0;display:none;");

		document.documentElement.appendChild(iframe);
		setTimeout(function() {
			if (Object.prototype.toString.call(iframe) === "[object Null]") {
                return;
            }else {
                iframe.parentNode.removeChild(iframe);
            }
		}, 200);
		iframe = null;
	}

	var __XuntongJSBridge = {
		// public
		invoke : _call,
		call : _call,
		handleMessageFromXT : _handleMessageFromXT
	};
	window.XuntongJSBridge = __XuntongJSBridge;
	return XuntongJSBridge;

})();