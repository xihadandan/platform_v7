/**
 * 基于mui的简单ztree实现
 */
(function(factory) {
	if (typeof define === "function" && define.amd) {
		// AMD. Register as an anonymous module.
		define([ "mui" ], factory);
	} else {
		// Browser globals
		factory(mui);
	}
})(function($) {

});