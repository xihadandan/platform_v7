requirejs.config({
	// context: "common",
	baseUrl : "/resources",
	urlArgs : 'V=V600',
	paths : {
		"jquery" : "jquery/1.12.4/jquery-1.12.4",
	},
	shim : {
		"jquery" : {
			deps : [],
			exports : "jquery"
		},
	}
})
requirejs(["jquery", "requirejs/scripts/hello"], function($, util) {
	var dyformRequirejs = requirejs.config({
		context: "dyform",
		baseUrl : "",
		urlArgs : 'V=V600',
	})
	dyformRequirejs(["require", "scripts/hello2-1"], function(require, util) {
		require(["scripts/hello2-2"], function($){
			return util.hello2(" World3");
		});
		requirejs(["jquery"], function($){
			return util.hello2(" World2");
		});
		return util.hello2(" World1");
	});
	requirejs(["jquery"], function($){
		return util.hello(" World2");
	});
	return util.hello(" World1");
});
