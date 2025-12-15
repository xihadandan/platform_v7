(function(){
	if(typeof CkPlugin == "undefined"){
		return;
	}
	var pluginName = CkPlugin.CHANGEMODE;
	CKEDITOR.plugins.add(pluginName,{
		init:function(a){
			var _self = this;
			 //定义一个按钮,用于触发缩进按钮
            a.ui.addButton(pluginName, {
                label: "切换菜单模式",//按钮的名称
                command: pluginName,
                icon: _self.path + "images/iconUp.png"//在toolbar中的图标
            });

          //定义命令，用于切换菜单模式操作
            a.addCommand(pluginName,{
	        	 exec : function() {
	        	 	var iconUp = _self.path + "images/iconUp.png";
	        	 	var iconDown = _self.path + "images/iconDown.png";
	        	 	var cke_toolbar_lastChild = $('#cke_' + a.name + ' .cke_toolbar:last-child');
	        	 	var cke_button__changemode_icon = $('#cke_' + a.name + ' .cke_button__changemode_icon');
	        	 	if(cke_toolbar_lastChild.css('display') === 'none') {
                        cke_toolbar_lastChild.show();
                        cke_button__changemode_icon.css('backgroundImage','url(' + iconUp + ')');
					} else {
                        cke_toolbar_lastChild.hide();
                        cke_button__changemode_icon.css('backgroundImage','url(' + iconDown + ')');
					}
	        	 }
            });
		}
	})
})()