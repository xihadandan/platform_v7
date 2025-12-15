$(function($) {
	// 重写和拓展基础方法
	DesignerUtils.setPageAndDialogTile = function(uuid) {
		if (StringUtils.isBlank(uuid)) {
			$('#title').html("新建存储单据");
			$('#title_h2').html("新建存储单据");
		} else {
			$('#title').html("(" + formDefinition.name + ")编辑----存储单据");
			$('#title_h2').html("(" + formDefinition.name + ")编辑表单定义----存储单据");
		}
	};
	// 初始化
	if(window.parent.initDyform && parent.initDyform.call(window, DesignerUtils) === false){
		return;
	}
	DesignerUtils.init();
});