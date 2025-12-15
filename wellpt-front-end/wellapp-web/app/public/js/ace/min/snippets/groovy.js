ace.define("ace/snippets/groovy", ["require", "exports", "module"], function (require, exports, module) {
    "use strict";

    exports.snippetText = "# def\n\
snippet def 变量修饰符\n\
	def";
    exports.scope = "groovy";
    exports.includeScopes = ['wellsoft_groovy'];


});
(function () {
    ace.require(["ace/snippets/groovy"], function (m) {
        if (typeof module == "object" && typeof exports == "object" && module) {
            module.exports = m;
        }
    });
})();
            