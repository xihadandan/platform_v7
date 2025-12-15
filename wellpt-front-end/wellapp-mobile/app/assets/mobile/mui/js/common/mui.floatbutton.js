(function(factory) {
	if (typeof define === "function" && define.amd) {
		// AMD. Register as an anonymous module.
		define([ "mui"], factory);
	} else {
		// Browser globals
		factory(mui);
	}
})(function($, server) {
	
	var domBuffer = '<i class="pt-icon mui-icon mui-icon-info pt-info"></i>\
		<i class="pt-icon mui-icon mui-icon-close pt-close operation"></i>\
		<i class="pt-icon mui-icon mui-icon-help pt-help operation"></i>';
	/**
	 * btns : [
	 * 	{
	 * 		icon : "mui-icon-help", // require
	 * 		text : "帮助",// optional
	 * 		info : "点击显示帮助信息", // optional
	 * 		callback : function(event){ // require
	 * 		}
	 * 	}
	 * ]
	 */
	var FloatButton = $.FloatButton = $.Class.extend({
		init : function(options){
			var self = this;
			options = self.options = $.extend({
				hold : true, // 是否支持hold:true时tap会直接关闭,hold会展开操作项;false时tap会展开操作项
				btns : [], // 自定义操作
				dir : "v", // 操作按钮排列方向,v水平,h垂直,c园
				style : "rb",// 操作位置：lmr*tmb
				className : "floatbotton-default" // 自定义class
			}, options);
			$.init({
				gestureConfig: {
					tap: true, //默认为true
					hold: options.hold //默认为false，不监听
				}
			});
			var floatBotton = document.createElement("div");
			floatBotton.classList.add("pt-floatbotton");
			floatBotton.classList.add("pt-" + options.dir); 
			floatBotton.classList.add("pt-" + options.style); 
			floatBotton.classList.add(options.className); 
			var btnGroup = document.createElement("div");
			btnGroup.classList.add("btns-group");
			floatBotton.appendChild(btnGroup);
			btnGroup.innerHTML = domBuffer;
			var operations = [];
			$.each(options.btns, function(idx, btn){
				if(btn.icon && $.isFunction(btn.callback)) {
					var operation = document.createElement("i");
					operation.addEventListener("tap", function() {
						self.hide();
						self.toggleHelp(false);
						btn.callback.apply(this, arguments);
					});
					operation.classList.add("pt-icon");
					operation.classList.add("operation");
					var icons = btn.icon.split(" ");
					for(var i = 0; i < icons.length; i++){
						operation.classList.add(icons[i]);
					}
					//operation.classList.add("mui-hidden");
					btnGroup.appendChild(operation);
					operations.push(operation);
				}
			});
			document.body.appendChild(floatBotton);
			var html = "<ul class='help-list'>";
			options.btns.push({
				text : "关闭",
				icon : "pt mui-icon mui-icon-close",
				info : "点击关闭"
			});
			options.btns.push({
				text : "帮助",
				icon : "pt mui-icon mui-icon-help",
				info : "点击显示帮助信息"
			})
			$.each(options.btns, function(i, btn){
				html += "<li class='help-item'>";
				html += "<div class='help-text'><i class='pt-icon " + btn.icon + "'></i>"+(btn.text || "")+"</div>";
				html += "<div class='help-info'>"+(btn.info || "")+"</div>";
				html += "</li>";
			})
			html += "</ul>";
			var tip = document.createElement("div");
			tip.classList.add("pt-floatbutton-help");
			tip.classList.add("mui-hidden");
			tip.innerHTML = html;
			document.body.appendChild(tip);
			var ui = self.ui = {
				tip : tip,
				operations : operations,
				floatBotton: floatBotton,
				help: $('.pt-help', floatBotton)[0],
				info: $('.pt-info', floatBotton)[0],
				close: $('.pt-close', floatBotton)[0]
			};
			ui.close.addEventListener("tap", function(event) {
				if($.isFunction(options.close) && options.close(event) === false){
					return false;
				}
				self.dispose();
			});
			ui.help.addEventListener("tap", function(event) {
				self.hide();
				self.toggleHelp(true);
			});
			var infoTap = function(event) {
				if($.isFunction(options.close) && options.close(event) === false){
					return false;
				}
				self.dispose();
			};
			var infoHold = function(event) {
				if(options.show) {
					self.show();
				}else {
					self.hide();
				}
				self.toggleHelp(false);
			};
			ui.info.addEventListener("tap", options.hold ? infoTap : infoHold);
			ui.info.addEventListener("hold", infoHold);
			$.trigger(ui.info, "hold", {});
		},
		toggleHelp : function(show){
			var self = this;
			var ui = self.ui;
			var options = self.options;
			options.help = !options.help && show;
			var action = options.help ? "remove" : "add";
			ui.tip.classList[action]("mui-hidden");
			return options.help;
		},
		show : function() {
			var self = this;
			var ui = self.ui;
			self.options.show = !self.options.show; 
			ui.close.classList.add("active");
			ui.help.classList.add("active");
			$.each(ui.operations, function(idx, operation){
				operation.classList.add("active");
			});
		},
		hide : function() {
			var self = this;
			var ui = self.ui;
			self.options.show = !self.options.show; 
			ui.close.classList.remove("active");
			ui.help.classList.remove("active");
			$.each(ui.operations, function(idx, operation){
				operation.classList.remove("active");
			});
		},
		close : function(){
			var self = this;
			var ui = self.ui;
			if(ui && ui.close){
				$.trigger(ui.close, "tap", {});
			}
		},
		dispose : function(){
			var self = this;
			var ui = self.ui;
			self.hide();
			self.toggleHelp(false);
			ui.tip.parentNode.removeChild(ui.tip);
			ui.floatBotton.parentNode.removeChild(ui.floatBotton);
			for (var name in self) {
				self[name] = null;
				delete self[name];
			};
		}
	});
	return FloatButton;
})
