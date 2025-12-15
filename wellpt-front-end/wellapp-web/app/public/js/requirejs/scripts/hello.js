define(["jquery"], function($) {
	return {
		hello : function(world){
			// $("title").html("Hello " + world);
			return alert("Hello" + world);
		},
	};
})