(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'mui', 'commons', 'server', 'constant' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, commons, server, constant) {
	"use strict";
	var mouseover = constant.getValueByKey(constant.EVENT_TYPE, "MOUSE_OVER");
	$.widget("ui.wMobileSlider", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {}
		},
		_createView : function() {
			this._renderSlider();
			this._bindEvents();
		},
		_renderSlider : function() {
			// 生成页面组件
			var _self = this;
			var options = _self.options;
			var element = _self.element[0];
			var configuration = options.widgetDefinition.configuration;
			var StringBuilder = commons.StringBuilder;
			var sliderImgArr = configuration.sliderImg.sliderItems.filter(function(item){return item.hidden !="1"});
			var sb = new StringBuilder();
			var sliderId = "mui-slider_" + _self.getId();
			var sliderHeight = configuration.sliderHeight ? configuration.sliderHeight : "auto";
			sb.append('<div id="' + sliderId + '" class="mui-slider">');
			sb.append('<div class="mui-slider-group {0}">', sliderImgArr.length > 1 ? 'mui-slider-loop' : '');
			if(sliderImgArr.length == 1){
				sb.append('<div class="mui-slider-item">');
				sb.append('<div class="banner"><img src="'+ctx+sliderImgArr[0].icon.iconPath+'" width="100%" height="'+sliderHeight+'"></div>');
				sb.append('<div class="desc-text">'+sliderImgArr[0].text+'</div>');
				sb.append('</div>');
			}else{
				sb.append('<div class="mui-slider-item mui-slider-item-duplicate">');
				sb.append('<div class="banner"><img src="'+ctx+sliderImgArr[sliderImgArr.length-1].icon.iconPath+'" width="100%" height="'+sliderHeight+'"></div>');
				sb.append('</div>');
				for (var i = 0; i < sliderImgArr.length; i++) {
					sb.append('<div class="mui-slider-item">');
					sb.append('<div class="banner"><img src="'+ctx+sliderImgArr[i].icon.iconPath+'" width="100%" height="'+sliderHeight+'"></div>');
					sb.append('<div class="desc-text">'+sliderImgArr[i].text+'</div>');
					sb.append('</div>');
				};
				sb.append('<div class="mui-slider-item mui-slider-item-duplicate">');
				sb.append('<div class="banner"><img src="'+ctx+sliderImgArr[0].icon.iconPath+'" width="100%" height="'+sliderHeight+'"></div>');
				sb.append('</div>');
				sb.append('</div>');
				sb.append('<div class="mui-slider-indicator">');
				for(var j = 0; j < sliderImgArr.length; j++) {
					if(j == 0){
						sb.append('<div class="mui-indicator mui-active"></div>');
					}else{
						sb.append('<div class="mui-indicator"></div>');
					}
				}
				sb.append('</div>');
			}
			sb.append('</div>');
      element.innerHTML = sb.toString();
      if (sliderImgArr.length > 1) { // 多张图片时才滚动
				this._initSlider(configuration);
			}
		},
		_initSlider:function(config){
			var sliderId = $('.mui-slider',this.element[0]);
			if(config.isLoop == 'on'){
				sliderId.slider({
					interval: config.time, //设置为0，则不定时轮播
				})
			}else{
				sliderId.slider();
			}

		},
		_bindEvents : function() {
		}
	});
}));
