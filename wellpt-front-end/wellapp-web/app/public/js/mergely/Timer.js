"use strict";

(function() {
	var Timer = {};
	Timer.start = function(){
		Timer.t0 = Date.now();
	}

	Timer.stop = function(){
		var t1 = Date.now();
		var td = t1 - Timer.t0;
		this.t0 = t1;
		return td;
	}
	window.Timer = Timer;
	return Timer;
})()




