## 可编辑代码插件

## https://ace.c9.io/


# ace在线代码编辑器使用说明

 - requirejs的依赖引用：`ace_code_editor`、`ace_ext_language_tools`

 - 使用用例：

		html:

			<pre id="jsCodeEditor" style="min-height:200px"></pre>

		js:
		
		//设置默认值，只需要给jsCodeEditor设置默认文本即可:$("#jsCodeEditor").text('test');
		var editor = ace.edit("jsCodeEditor");
        editor.setTheme("ace/theme/chrome");
		//语言
        editor.session.setMode("ace/mode/javascript");
		//editor.session.setMode("ace/mode/groovy");
        //启用提示菜单
        ace.require("ace/ext/language_tools");
        editor.setOptions({
            enableBasicAutocompletion: true,
            enableSnippets: true,
            enableLiveAutocompletion: true
        });

		editor.getValue();//获取输入的代码




	