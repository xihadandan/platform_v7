(function () {
  if (typeof CkPlugin == 'undefined') {
    return;
  }
  var pluginName = CkPlugin.TEMPLATECONTAINER;
  CKEDITOR.plugins.add(pluginName, {
    requires: ['dialog'],
    init: function (a) {
      a.templatePropertyUrl4Form = this.path + 'dialogs/' + pluginName + '.html'; //属性窗口中的html对应的地址,dysubform.js中要使用到该变量

      //定义"设置表单"对话框
      CKEDITOR.dialog.add(pluginName, this.path + 'dialogs/' + pluginName + '.js');

      //定义命令，用于打开"设置表单"对话框
      a.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));

      function createDef(def) {
        return CKEDITOR.tools.extend(def || {}, {
          contextSensitive: 1,
          refresh: function (editor, path) {
            this.setState(path.contains('table', 1) ? CKEDITOR.TRISTATE_OFF : CKEDITOR.TRISTATE_DISABLED);
          }
        });
      }

      var templateuuid = null;
      var currentTemplate = null;
      a.addCommand('deleteTemplate', {
        exec: function (editor) {
          // 执行删子表单的代码
          currentTemplate.remove();
          formDefinition.deleteTemplate(templateuuid);
        }
      });

      // 将删子表单添加到右键菜单中
      if (a.addMenuItem) {
        a.addMenuGroup('deleteTemplate');

        a.addMenuItem('deleteTemplate', {
          label: '删除子表单',
          command: 'deleteTemplate',
          group: 'deleteTemplate'
        });

        if (a.contextMenu) {
          // 监听右键菜单事件
          a.contextMenu.addListener(function (element) {
            var parents = element.getParents();
            for (var i = 0, len = parents.length; i < len; i++) {
              var classes = parents[i].$.getAttribute('class');
              if (classes && classes.indexOf('template-wrapper') != -1) {
                // 判断是子表单
                templateuuid = parents[i].getAttribute('templateuuid');
                currentTemplate = parents[i];
                return {
                  deleteTemplate: CKEDITOR.TRISTATE_OFF
                };
              }
            }
          });
        }
      }

      a.addCommand(
        'tableDelete',
        createDef({
          //重新定义表格删除事件,主表不得删除
          exec: function (editor) {
            var path = editor.elementPath(),
              table = path.contains('table', 1);

            if (!table) return;

            // If the table's parent has only one child remove it as well (unless it's the body or a table cell) (#5416, #6289)
            var parent = table.getParent();
            if (parent.getChildCount() == 1 && !parent.is('body', 'td', 'th')) table = parent;
            /*var clazzOfTbl = $(table).attr("class");
          if(typeof clazzOfTbl != "undefined" && clazzOfTbl.indexOf("mainform") != -1){
            alert("主表不得删除");
            return;
          }*/

            var range = editor.createRange();
            range.moveToPosition(table, CKEDITOR.POSITION_BEFORE_START);
            table.remove();
            range.select();
            var formUuid = $(table).attr('formUuid');
            if (typeof formUuid != 'undefined') {
              //从表
              //从JSON定义中删除从表的定义信息
              delete formDefinition.subforms[formUuid];
            }
          }
        })
      );

      //定义一个按钮,用于触发打开"设置表单"对话框的命令
      a.ui.addButton(pluginName, {
        label: '子表单', //调用dialog时显示的名称
        command: pluginName,
        icon: this.path + 'images/anchor.png' //在toolbar中的图标
      });

      //定义双击事件
      a.on('doubleclick', function (evt) {
        var element = evt.data.element; //element是CKEDITOR.dom.node类的对象
        var pluginContainerDomElement = CKEDITOR.getPluginContainerDomElement(pluginName, element);
        if (pluginContainerDomElement != null) {
          a.focusedDom = pluginContainerDomElement;
          if ($.trim(a.focusedDom.getAttribute('templateflag')).length === 0) {
            return;
          } else {
            evt.data.dialog = pluginName;
            window.setTimeout(function () {
              templatecontainer && templatecontainer.initPropertyDialog(a); //重新初始化属性窗口
            }, 300);
          }
        }
      });
    }
  });
})();
