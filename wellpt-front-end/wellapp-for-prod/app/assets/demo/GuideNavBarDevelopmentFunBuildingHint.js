define([ "constant", "commons", "server"], function(constant, commons, server,
		) {
	return function(options){//务必要返回一个函数，这里返回的是一个匿名函数, 在函数中执行
		console.log(options);
		var menuName = options.params.menuName 
		appModal.info((menuName == undefined ? "" : (menuName + ":"))+ "功能完善中, 敬请期待", function(){
			appModal.info("参数JSON化: " + JSON.stringify(options.params))
		});
	}
});