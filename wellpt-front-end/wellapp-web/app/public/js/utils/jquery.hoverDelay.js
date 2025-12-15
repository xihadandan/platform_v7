(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($) {
	"use strict";
	$.fn.hoverDelay = function(options) {
		var defaults = {
			hoverDuring : 200,
			outDuring : 200,
			hoverEvent : function() {
				$.noop();
			},
			outEvent : function() {
				$.noop();
			}
		};
		var sets = $.extend(defaults, options || {});
		var hoverTimer, outTimer, that = this;
		return $(this).each(function() {
			$(this).hover(function(e) {
				clearTimeout(outTimer);
				hoverTimer = setTimeout(function() {
					sets.hoverEvent.apply(that, [ e ]);
				}, sets.hoverDuring);
			}, function(e) {
				clearTimeout(hoverTimer);
				outTimer = setTimeout(function() {
					sets.outEvent.apply(that, [ e ]);
				}, sets.outDuring);
			});
		});
	};
}));