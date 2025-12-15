(function(global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) : typeof define === 'function' && define.amd ? define([ 'exports' ], factory)
			: (factory((global._wps = {})));
}(this, (function(exports) {
	'use strict';
	// 消息监听
	var saveTimeout = 1000 * 3;
	window.addEventListener("message", function(event) {
		// debugger
		// console.log(event);//
		var data = event.data;
		if (typeof data === "string") {
			data = JSON.parse(data);
		}
		var fName = data.fName;
		var fArgs = JSON.parse(data.fArgs);
		var scb = function(Application) {
			exports.Application = Application;
			exports[fName].apply(exports, fArgs);
			// alert("初始化成功")
			console.log("初始化成功");
		}, fcb = function() {
			// console.log("初始化失败");
			alert("初始化失败");
			// typeof wpsCallback.fail === "function" && wpsCallback.fail();
		};
		var i = 10, intervalId;
		var app = exports.Application || suwell.ofdReaderInit("webwps_id", "100%", "100%");
		(function checkInterval() {
			if (app && app.valid()) {
				return scb(app);
			} else if ((i--) > 0) {
				intervalId = setTimeout(checkInterval, 1000);
			} else {
				return fcb();
			}
		})();
	});
	// 退出时做清理
	window.onbeforeunload = function(event) {
		if (wpsCallback && wpsCallback.onbeforeunload) {
			wpsCallback.onbeforeunload.apply(this, event);
		}
		// 跨浏览器时，会关闭其他浏览器WPS(单个浏览器的所有WPS窗体关闭时，会影响其他浏览器的WPS，一起关闭)
		exports.Application && exports.Application.exit();
	};

	function _inArray(suffix, array) {
		for (var i = 0; i < array.length; i++) {
			if (suffix.indexOf(array[i]) != -1)
				return true;
		}
		return false;
	}

	var openOfd = function openOfd(params) {
		var self = this;
		var app = self.Application, ret;
		var fileName = params.fileName;
		// fileName = "http://192.168.40.13:8080/resources/wps/newfile.ofd";
		var openType = params["openType"] || params["_openType"], protectTypes = [ 0, 1, 2, 3 ];
		var isReadonly = !!(openType && protectTypes.indexOf(openType.protectType) > -1);
		if (fileName && (fileName.indexOf("http") === 0 || fileName.indexOf("ftp") === 0)) {
			if (params.uploadPath && isReadonly === false) {
				if(params.newFileName){
					params.uploadPath += "&filename="+encodeURIComponent(params.newFileName);
				}
				ret = app.openurl(fileName, params.uploadPath, isReadonly);
			} else {
				ret = app.openFile(fileName, isReadonly);
			}
		}
		params.userName && app.setUserName(params.userName);
		if (params.uploadPath && isReadonly === false) {
			app.registListener("f_saveurl", "savePerformed", true);
		}
	}
	exports.openOfd = openOfd;
	Object.defineProperty(exports, '__esModule', {
		value : true
	});
})));