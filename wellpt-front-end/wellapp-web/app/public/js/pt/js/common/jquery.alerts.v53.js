(function(root, factory) {
	"use strict";
	if (typeof define === "function" && define.amd) {
		// AMD. Register as an anonymous module.
		define([ "jquery", "appModal"], factory);
	} else if (typeof exports === "object") {
		// Node. Does not work with strict CommonJS, but
		// only CommonJS-like environments that support module.exports,
		// like Node.
		module.exports = factory(require(["jquery", "appModal"]));
	} else {
		// Browser globals (root is window)
		factory(root.jQuery, root.appModal);
	}

}(this, function init($, appModal) {
	// 提示框5秒自动关闭
	$.oAlert = oAlert =function(message, callback, title, width, height) {
		// TODO auto close
		appModal.info({
			title : title,
			width : width,
			height : height,
			message : message,
			callback : callback
		});
	};
	// 提示框不自动关闭
	$.oAlert2 = oAlert2 = function(message, callback, title, width, height) {
		appModal.info({
			title : title,
			width : width,
			height : height,
			message : message,
			callback : callback
		});
	};
	// 确认框
	$.oConfirm = oConfirm = function(message, callback, argument, title, width, height) {
		function innerCallback(ok){
			if(ok && callback){
				callback.call(this);
			}
		}
		appModal.confirm({
			title : title,
			width : width,
			height : height,
			message : message,
			callback : innerCallback,
			argument : argument
		});
	};
	// 确认框(取消带回调)
	$.oConfirm2 = oConfirm2 = function(obj) {
		var callback = obj.callback;
		function innerCallback(ok){
			if(ok && callback){
				callback.call(this);
			}
		}
		obj.callback = innerCallback;
		appModal.confirm(obj);
	};
	// 正在加载，遮罩
	$.pageLock = pageLock = function(status) {
		if(status === "show"){
			appModal.showMask();
		}else if(status === "hide"){
			appModal.hideMask();
		}
	};
	// 打开弹出框
	$.showDialog = showDialog = function(options) {
		// options.title = options.title;
		options.message = options.content;
		appModal.dialog(options);
	};
	$.closeDialog = closeDialog = function(container) {
		appModal.hide(container);
	};
	return $;
}))
