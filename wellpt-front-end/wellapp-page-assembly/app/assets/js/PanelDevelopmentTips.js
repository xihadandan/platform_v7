define([ "jquery", "commons", "constant", "server", "PanelWidgetDevelopment" ], function($, commons, constant, server,
		PanelWidgetDevelopment) {
	var PanelDevelopmentTips = function() {
		PanelWidgetDevelopment.apply(this, arguments);
	};
	commons.inherit(PanelDevelopmentTips, PanelWidgetDevelopment, {
		prepare : function() {

		},
		create : function() {

		},
		init : function() {

		},
		beforeRender : function(options, configuration) {
			var self = this;
			var element = self.widget.element;
			$(element).addClass("jquery-ui-tips");
			var $panel = $(">.panel-body", element);
			$panel.css("position", "relative");
			$panel.css({"height":"auto","overflow":"visible"});
			$panel.append('<div class="arrow glyphicon glyphicon-chevron-down" style="position: absolute;"></div>');
			$(".panel-body>.glyphicon-chevron-down", element).click(function(event){
				var $this = $(this);
				if($this.is(".glyphicon-chevron-down")){
					$(".col-xs-2 .ui-wHtml").find("h1").css("display","none");				
					$panel.css({"height":"58","overflow":"hidden"});
					$this.removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
				}else {
					$(".col-xs-2 .ui-wHtml").find("h1").css("display","block");
					$panel.css({"height":"auto","overflow":"visible"});
					$this.removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
				}
			});
			
			$(".row>.column>.panel", element).addClass("panel-nocolor");
		},
		afterRender : function(options, configuration) {
		}
	});
	return PanelDevelopmentTips;
});
