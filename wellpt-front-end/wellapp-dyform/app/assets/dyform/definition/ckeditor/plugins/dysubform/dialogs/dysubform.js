var pluginName = "dysubform";
CKEDITOR.dialog.add(pluginName,
    function (editor) {
        var containerID = "container_" + pluginName;
        return {
            title: "设置从表",
            minHeight: 450,
            minWidth: 1000,
            contents: [{
                id: "subformProperty",
                label: "label",
                title: "title",
                expand: true,
                padding: 0,
                elements: [
                    {
                        id: "table_html",
                        type: "html",
                        style: "width: 100%;",
                        html: "<div id='" + containerID + "'>设置从表属性</div>",
                        //html:"<iframe  id=container_" + pluginName + " src='" + ctx +"/resources/ckeditor4.1/plugins/dysubform/index.html' />",
                        onLoad: function () {

                        }
                    }
                ]
            }],
            onOk: function () {
                collectFormAndFillCkeditor(editor);
                exitDialog(editor);
            },
            onCancel: function () {//退出窗口时清空属性窗口的缓存
                if ((typeof exitDialog) != "undefined") {
                    exitDialog(editor);
                }
            },
            onShow: function () {
                //$("#attrCfgDiv").remove();//防止缓存 ，出现多个不同的属性弹出框由于缓存，出现重复元素
                var date = new Date();
                var subformDialogHtmlFile = CKEDITOR.plugins.basePath + pluginName + "/dialogs/property.html?" + date.getTime();//"检验规则"页签
                //以GET方式(不得以POST方式)加载属性窗口的html
                $("#" + containerID).load(subformDialogHtmlFile, function () {
                    initPropertyDialog(editor);//初始化属性窗口
                    $.ControlConfigUtil.enableFocus(editor, CKEDITOR.dialog._.currentTop, containerID);

                    if (editor.focusedDom != null && (typeof editor.focusedDom != "undefined")) {//通过双击ckeditor中的从表元素,表示修改从表属性,则不需要做焦点位置判断
                        return;
                    } else {
                        //判断光标焦点位置是不是在从表中，若在其他从表中则不允许插入新的从表
                        var selection = editor.getSelection();
                        var selected_ranges = selection.getRanges(); // getting ranges
                        var node = selected_ranges[0].startContainer; // selecting the starting node

                        var parents = node.getParents(true);
                        var parentsLength = parents.length;
                        if (parentsLength > 3) {
                            var outestParent = parents[parentsLength - 3];//最外层的容器
                            if (outestParent.getName() == "table"
                                && $(outestParent).attr("formUuid") != "" && $(outestParent).attr("formUuid") != undefined) {
                                //如果光标的位置在从表中，这时需要提示用户位置错误，不能在从表的位置插入从表.
                                this.hide();
                                alert("不能在从表的位置插入从表，请将光标移至正确的位置!!");

                            }
                        }
                    }
                });

            }
        };

    });
