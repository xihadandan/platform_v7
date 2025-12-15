;
(function ($) {

    var columnProperty = {
        // 控件字段属性
        applyTo: null,// 应用于
        columnName: null,// 字段定义 fieldname
        displayName: null,// 描述名称 descname
        dbDataType: '',// 字段类型 datatype type
        indexed: null,// 是否索引
        showed: null,// 是否界面表格显示
        sorted: null,// 是否排序
        sysType: null,// 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
        length: null,// 长度
        showType: '1',// 显示类型 1,2,3,4 datashow
        defaultValue: null,// 默认值
        valueCreateMethod: '1',// 默认值创建方式 1用户输入
        onlyreadUrl: null,// 只读状态下设置跳转的url
    };

    // 控件公共属性
    var commonProperty = {
        inputMode: null,// 输入样式 控件类型 inputDataType
        fieldCheckRules: null,
        fontSize: null,// 字段的大小
        fontColor: null,// 字段的颜色
        ctlWidth: null,// 宽度
        ctlHight: null,// 高度
        textAlign: null,// 对齐方式

    };

    /*
     * DIALOG CLASS DEFINITION ======================
     */
    var Dialog = function ($placeHolder, options) {
        this.options = $.extend({}, $.fn["wdialog"].defaults, options);
        this.value = "";
        this.$editableElem = null;
        this.$labelElem = null;
        this.$placeHolder = $placeHolder;
    };

    Dialog.prototype = {
        constructor: Dialog
    };

    $.Dialog = {

        createEditableElem: function () {
            if (this.$editableElem != null) {// 创建可编辑框
                return;
            }

            var _this = this;
            var options = this.options;

            var _relationDataDefiantion = this.options.relationDataDefiantion;// 格式为:{"sqlTitle":"\u5355\u4f4d\u540d\u79f0","sqlField":"title","formTitle":"\u540d\u79f0","formField":"mc","search":"no"}|{"sqlTitle":"\u7ec4\u7ec7\u673a\u6784\u4ee3\u7801","sqlField":"reservedText1","formTitle":"\u4ee3\u7801","formField":"dm","search":"no"}
            if ($.trim(_relationDataDefiantion).length == 0 && this.options.isRelevantWorkFlow != "1" && !this.options.countNum2Input) {
                alert("请添加弹出框控件的字段映射关系:" + _this.getCtlName());
                return;
            }
            var _relationDataIdTwo, _relationDataValueTwo, _relationDataTwoSql;
            _relationDataIdTwo = this.options.relationDataIdTwo;
            _relationDataValueTwo = this.options.relationDataValueTwo;// 数据来源id
            _relationDataTwoSql = this.options.relationDataTwoSql;
            this.options.relationFieldMappingstrArray = [];
            var relationFieldMappingArray = [];
            if (this.options.isRelevantWorkFlow != "1" && !this.options.countNum2Input) {
                // alert(_relationDataDefiantion + "----" + ctlName);
                var relationFieldMappingstrArray = _relationDataDefiantion.split("|");
                for (var i = 0; i < relationFieldMappingstrArray.length; i++) {
                    relationFieldMappingArray.push(JSON.parse(relationFieldMappingstrArray[i]));
                }

                this.options.relationFieldMappingstrArray = relationFieldMappingstrArray;
            }

            if (typeof options.columnProperty.relativeMethod == "undefined" || options.columnProperty.relativeMethod == dyRelativeMethod.DIALOG) {
                this.create$EditableElemOfInput();
                this.create$DialogElement(relationFieldMappingArray, _relationDataValueTwo, _relationDataTwoSql, _relationDataIdTwo);// 创建弹出框
            } else {
                if (this.options.searchMultiple) {
                    this.create$EditableElemOfMultiple();
                    this.create$SearchElement(relationFieldMappingArray, _relationDataValueTwo, _relationDataTwoSql, this.options.pageSize, true);// 创建搜索框
                } else {
                    this.create$EditableElemOfInput();
                    this.create$SearchElement(relationFieldMappingArray, _relationDataValueTwo, _relationDataTwoSql, this.options.pageSize, false);// 创建搜索框
                }
            }

        },

        bind: function (event, callback) {
            this.options[event] = callback;
        },

        createCountHref: function (value) {
            var _this = this;
            if (this.doneCreateCoutHref) {
                return;
            }
            this.doneCreateCoutHref = true;
            var createHref = function (v) {
                //隐藏并增加超链接
                _this.$editableElem.hide();
                var param = appContext.resolveParams({'text': _this.options.countNumHrefText}, {'count': v});
                var $a = $("<a>").text(param.text);
                $a.insertBefore(_this.$editableElem);
                if (_this.$labelElem) {
                    _this.$labelElem.hide();
                }
                $a.click(function () {
                    _this.$editableElem.trigger('click');
                });
            }
            if (this.options.countNum2Input) {
                if (this.value == undefined || this.value == null || this.value == '') {
                    var DataStore = require('dataStoreBase');
                    var Datastore = new DataStore({
                        dataStoreId: _this.options.relationDataSourceTwo,
                        receiver: _this,
                        defaultCriterions: (function () {
                            var defaultConditions = [];
                            if (StringUtils.isNotBlank(_this.options.relationDsDefaultCondition)) {
                                var criterion = {};
                                criterion.sql = _this.options.relationDsDefaultCondition;
                                defaultConditions.push(criterion);
                            }
                            return defaultConditions;
                        })(),
                        pageSize: 1,
                        onDataChange: function () {
                            _this.setValue(arguments[1] + '', false);
                            if (_this.options.countNumHref) {
                                createHref(arguments[1]);
                            }
                        }
                    });
                    Datastore.load([], null);
                } else {
                    //隐藏并增加超链接
                    createHref(this.value);
                }
            }


        },

        /**
         * 创建input的EditableElem
         */
        create$EditableElemOfInput: function () {
            var _this = this;
            var ctlName = this.getCtlName();

            var editableElem = document.createElement("input");
            editableElem.setAttribute("class", this.editableClass);
            editableElem.setAttribute("name", ctlName);
            editableElem.setAttribute("type", "text");

            $(editableElem).css(this.getTextInputCss());

            this.$placeHolder.after($(editableElem));
            this.$editableElem = this.$placeHolder.next("." + this.editableClass);
            this.$editableElem.addClass("input-search");// css in
            // wellnewoa.css

            // 绑定事件
            this.$editableElem.bind("paste", function () {// 粘贴事件
                var _dom = this;
                window.setTimeout(function () {
                    _this.setValue($(_dom).val(), false);
                    _this.invoke("valueChange");
                }, 100);
            });

            this.$editableElem.bind("keyup", function () {// 输入事件
                _this.setValue($(this).val(), false);
                _this.invoke("valueChange");
            });

            this.$editableElem.bind("change", function () {// 输入事件
                _this.setValue($(this).val(), false);
                _this.invoke("valueChange");
            });
        },

        /**
         * 创建多选的EditableElem
         */
        create$EditableElemOfMultiple: function () {
            var _this = this;
            var ctlName = this.getCtlName();
            var editableElem = $('<div class="dyfrom-container-multi">'
                + '<ul class="dyfrom-choices-multi">'
                + '<li class="dyfrom-search-field">'
                + '<input class="' + this.editableClass + ' dyfrom-multiple-input" name="' + ctlName + '" type="text" style="border-left:0px;border-top:0px;border-right:0px;border-bottom:1px;height:15px;margin-left:5px;-webkit-box-shadow:none;"/>'
                + '</li>'
                + '</ul>'
                + '</div>'
            );

            $(editableElem).css(this.getTextInputCss());

            this.$placeHolder.after($(editableElem));
            this.$editableElem = editableElem.find("." + this.editableClass);

            // 宽度自适应内容

            this.$editableElem.bind('keydown', function () {
                var sensor = $('<pre>' + $(this).val() + '</pre>').css({display: 'none'});
                $('body').append(sensor);
                var width = sensor.width() * 1.8;
                sensor.remove();
                var controlWidth = $(this).parent().parent().parent().width() - 10;
                if (width < controlWidth) {
                    if (width < 25) {
                        $(this).width(25);
                    } else {
                        $(this).width(width);
                    }
                } else {
                    $(this).width(controlWidth);
                }
                var maxWidth = $(this).parent().parent().width() - 10;
                if ($(this).parent().prev().length > 0) {
                    var useWidth = $(this).parent().prev().offset().left + 40 + $(this).parent().prev().width() - $(this).parent().parent().offset().left;
                } else {
                    var useWidth = 0;
                }
                if (width < (maxWidth - useWidth)) {
                    $(this).width(maxWidth - useWidth);
                } else {
                    $(this).width(maxWidth);
                }
            });
            this.$editableElem.keydown();


            editableElem.bind("click", function () {
                _this.$editableElem.select();
                _this.$editableElem.click();
            });
        },


        /**
         * 创建搜索框
         *
         * @param relationFieldMapping
         *            字段关系映射, 成员为字符串
         * @param dataSourceId
         *            数据源id,
         * @param contrainSql
         *            数据源sql约束条件,
         */
        create$SearchElement: function (relationFieldMapping, dataSourceId, contrainSql, pageSize, multiple) {
            var _this = this;
            // 取得输入框JQuery对象
            var $searchInput = this.$editableElem;
            // 关闭浏览器提供给输入框的自动完成
            $searchInput.attr('autocomplete', 'off');
            var $autocomplete = $(".autocomplete[name=viewpick]");
            // 创建自动完成的下拉列表，用于显示服务器返回的数据,插入在搜索按钮的后面，等显示的时候再调整位置
            if ($autocomplete == null || typeof $autocomplete == "undefined" || $autocomplete.length <= 0) {
                $autocomplete = $('<div class="autocomplete" name="viewpick"></div>').hide().appendTo($("body"))

                $autocomplete.mouseover(function (evt) {
                    isInAutoCompleteDiv = true;
                });
                $autocomplete.mouseout(function (evt) {

                    var __this = evt.currentTarget;
                    var rect = __this.getBoundingClientRect();

                    isInAutoCompleteDiv = false;
                    var inDiv = (evt.clientX >= rect.left && evt.clientX <= rect.right);
                    inDiv = inDiv && (evt.clientY >= rect.top && evt.clientY <= rect.bottom);
                    if (!inDiv) {
                        setTimeout(clear, 350);
                    }
                });
            }

            this.$autocomplete = $autocomplete;


            // 清空下拉列表的内容并且隐藏下拉列表区
            var clear = function () {
                $autocomplete.empty().hide();
            };

            isInAutoCompleteDiv = false;


            // 注册事件，当输入框失去焦点的时候清空下拉列表并隐藏
            $searchInput.blur(function (evt) {
                if (isInAutoCompleteDiv) {
                } else {
                    setTimeout(clear, 350);
                }
                isInAutoCompleteDiv = false;
                _this.clearAllSession();// 先清空所有的请求会话
                if (multiple) {
                    _this.$editableElem.val("");
                }
            });


            // 设置下拉项的高亮背景
            var setCheckedItem = function (_this) {
                // $(_this).parent().removeClass('highlight');
                clearCheckedItem(_this);
                $(_this).addClass('highlight');
            };

            // 设置下拉项的高亮背景
            var getCheckedItem = function () {
                return $autocomplete.find(".highlight");
            };

            var moveCheckedItem2Next = function () {
                if ($autocomplete.find(".highlight")[0] == $autocomplete.find("tr:last")[0]) {
                    return;
                }

                var $dom = getCheckedItem();
                if ($dom.size() == 0) {
                    setCheckedItem($autocomplete.find("tr").first());
                    return;
                }
                var dom = $dom[0];
                clearCheckedItem($(dom));

                setCheckedItem($(dom).next());

            };
            var moveCheckedItem2Prev = function () {
                if ($autocomplete.find(".highlight")[0] == $autocomplete.find("tr:first")[0]) {
                    return;
                }
                var dom = $autocomplete.find(".highlight")[0];
                clearCheckedItem($(dom));

                setCheckedItem($(dom).prev());
            };


            // 清空下拉项的高亮背景
            var clearCheckedItem = function (_this) {
                $(_this).siblings().removeClass('highlight');
                // $(_this).addClass('highlight');
            };

            var selectItem = function (mappingFields) {
                var $checkedTr = $autocomplete.find(".highlight");
                var $checkedTd = $checkedTr.find("td");
                var fieldsKeyValue = {};
                $checkedTd.each(function () {
                    // 此处的text()方法若换成html()方法则取到的值会默认被浏览器转义(如">",取到"&gt;")
                    fieldsKeyValue[$(this).attr("fieldname").toLowerCase()] = $(this).find("div").first().text();
                });

                _this.setValue2Controls(mappingFields, fieldsKeyValue);
            };

            var sqlFields = [];
            var outfields = []
            for (var i = 0; i < relationFieldMapping.length; i++) {
                if(relationFieldMapping[i].isSearch){
                    sqlFields.push(relationFieldMapping[i].sqlField);
                }
                outfields.push(relationFieldMapping[i].sqlField)
            }

            // 约束条件relationDataTwoSql
            var contraint = {};
            var relationDataTwoSql = this.options.relationDataTwoSql;

            if ($searchInput.parents("form").size() > 0) {
                var formWidth = $searchInput.parents("form").width();
            } else {
                var scrollWidth = document.documentElement.scrollWidth
                var formWidth = scrollWidth;
            }

            var ajax_request = function (evt) {

                contraint = _this.getContraint();
                _this.clearAllSession();// 先清空所有的请求会话
                var currentSessionId = _this.createSession();// 创建新的会话
                $searchInput.addClass("input-waiting");

                _this.invoke("beforeAutoCompleteRequest", contraint);
                // ajax服务端通信
                $.ajax({
                    'url': contextPath + '/pt/dyform/data/autoComplete', // 服务器的地址
                    // {"sqlTitle":"\u5355\u4f4d\u540d\u79f0","sqlField":"title","formTitle":"\u540d\u79f0","formField":"mc","search":"no"}|{"sqlTitle":"\u7ec4\u7ec7\u673a\u6784\u4ee3\u7801","sqlField":"reservedText1","formTitle":"\u4ee3\u7801","formField":"dm","search":"no"}

                    'data': {
                        'searchText': $searchInput.val(),
                        'fields': JSON.cStringify(sqlFields),
                        'dataSourceId': dataSourceId,
                        'outfields':JSON.cStringify(outfields),
                        contraint: JSON.cStringify(contraint),
                        pageSize: typeof pageSize == "undefined" ? "300" : pageSize
                    }, // 参数
                    'dataType': 'json', // 返回数据类型
                    'type': 'POST', // 请求类型
                    'success': function (data) {
                        $searchInput.removeClass("input-waiting");
                        if (_this.isSessionTimeout(currentSessionId)) {// 会话已被取消
                            return;
                        }
                        clear();
                        _this.invoke("beforeAutoCompleteShow", data);

                        if (data && data.length > 0 && multiple && StringUtils.isNotBlank(_this.getValue())) {
                            // 过滤数据
                            _this.filterSelectData(data, relationFieldMapping);
                        }

                        if (data.length) {
                            $autocomplete.append("<table class='table table-hover' style='margin-bottom:0px;'></table>");
                            // 遍历data，添加到自动完成区
                            $.each(data, function (index, term) {
                                if (typeof term == "string") {
                                    // 创建li标签,添加到下拉列表中
                                    $('<tr></tr>').html("<td>" + term + "</td>").appendTo($autocomplete.find("table")).addClass('clickable').hover(function () {

                                        setCheckedItem(this);// 设置高亮
                                    }, function () {
                                        // setCheckedItem(this);
                                        // clearCheckedItem(this);//清空高亮
                                    }).click(function () {
                                        clear();// 清空下拉项
                                    });
                                } else {
                                    var content = "";
                                    for (var i in term) {
                                        var isHide = _this.isFieldHide(relationFieldMapping, i);
                                        var width = _this.getFieldWidth(relationFieldMapping, i);
                                        content += "<td fieldName='" + i + "' style='" + (isHide ? "display:none;" : "") + ";border:none;' title='" + term[i] + "' ><div style='width:" + width + "px;" + "'>" + term[i] + "</div></td>";
                                    }

                                    // 创建li标签,添加到下拉列表中
                                    var $rendTr = $('<tr></tr>').html(content).appendTo($autocomplete.find("table")).addClass('clickable').hover(function () {
                                        setCheckedItem(this);// 设置高亮
                                    }, function () {
                                        clearCheckedItem(this);// 清空高亮
                                    }).click(function () {
                                        selectItem(relationFieldMapping);
                                        clear();// 清空下拉项
                                    });
                                    // tr渲染回调
                                    _this.invoke("autoCompleteRendering", $rendTr, term);
                                }
                            });// 事件注册完毕
                            // 设置下拉列表的位置，然后显示下拉列表

                            var $searchControl = multiple ? $searchInput.parent().parent().parent() : $searchInput;

                            var inputRect = $searchControl[0].getBoundingClientRect();

                            var scroll_top = 0;
                            if (document.documentElement && document.documentElement.scrollTop) {
                                scroll_top = document.documentElement.scrollTop;
                                scrollWidth = document.documentElement.scrollWidth
                            } else {
                                scroll_top = document.body.scrollTop;
                                /* 某些情况下Chrome不认document.documentElement.scrollTop则对于Chrome的处理。 */
                                scrollWidth = document.body.scrollWidth;
                            }


                            var y = scrollWidth - inputRect.left;
                            var z = $autocomplete.width();
                            var left = 0;


                            if (y < z) {
                                left = inputRect.left - (z - y) - (scrollWidth - formWidth) / 2;
                            } else {
                                left = inputRect.left;
                            }

                            $autocomplete.css({
                                'position': 'absolute',
                                'left': left + "px",
                                'top': (inputRect.top + scroll_top + $searchControl.height() + 10) + "px",
                                'z-index': 9999,
                                "overflow-x": "auto",
                                "overflow-y": "auto",
                                "min-height": "200 px",
                                "max-height": "300px",
                                "border": "1px solid #c5dbec",
                                "background-color": "#fff",
                                "text-align": "left",
                                "box-shadow": "0 0 7px rgba(0, 0, 0, 0.3)"
                            });


                            $autocomplete.show();

                            if (!_this.isInSubform()) {
                                $autocomplete.css('width', $searchControl.outerWidth(true));
                            }
                        }

                        if (_this.options.afterAutoCompleteShow) {
                            _this.options.afterAutoCompleteShow.call(_this, evt);
                        }
                    }
                });
            };

            // timeout的ID
            var timeoutid = null;
            // 点击事件
            $searchInput.click(function (event) {
                event.stopPropagation();

                if (_this.isReadOnly() || _this.invoke("beforeAutoCompleteShowing", event) === false) {// 只读时不可搜索
                    return;
                }
                clear();// 清空下拉项
                clearTimeout(timeoutid);
                timeoutid = setTimeout(function () {

                    ajax_request.call(this, event);
                }, 500);
            });

            // 改变事件
            if (!multiple) {
                $searchInput.change(function () {

                    if (_this.options.allowValNotInDs) {
                        return false;
                    }
                    var fieldsKeyValue = {};
                    for (var i = 0; i < relationFieldMapping.length; i++) {
                        fieldsKeyValue[relationFieldMapping[i]["formField"].toLowerCase()] = "";
                    }
                    _this.setValue2ControlsWithOutAfterDialogSelect(relationFieldMapping, fieldsKeyValue);
                });
            }


            // 对输入框进行事件注册
            $searchInput.keyup(function (event) {
                if (_this.isReadOnly() || _this.invoke("beforeAutoCompleteShowing", event) === false) {// 只读时不可搜索
                    return;
                }
                // 字母数字，退格，空格
                if (event.keyCode > 40 || event.keyCode == 8 || event.keyCode == 32) {
                    // 首先删除下拉列表中的信息
                    clear();// 清空下拉项
                    clearTimeout(timeoutid);
                    timeoutid = setTimeout(function () {
                        ajax_request.call(this, event);
                    }, 500);
                }
                else if (event.keyCode == 38) {
                    // 上
                    // CheckedItem = -1 代表鼠标离开
                    moveCheckedItem2Prev();
                    event.preventDefault();
                }
                else if (event.keyCode == 40) {
                    // 下
                    moveCheckedItem2Next();
                    event.preventDefault();
                }
            }).keypress(function (event) {
                // enter键
                if (event.keyCode == 13) {
                    // selectItem(relationFieldMapping);
                    // clear();
                    // event.preventDefault();
                    getCheckedItem().trigger("click");
                    if (multiple) {
                        _this.$editableElem.val("");
                    }
                }
            }).keydown(function (event) {
                // esc键
                if (event.keyCode == 27) {
                    clear();
                    event.preventDefault();
                }
            });
            // 注册窗口大小改变的事件，重新调整下拉列表的位置
            $(window).resize(function () {
                var ypos = $searchInput.position().top;
                var xpos = $searchInput.position().left;
                $autocomplete.css('width', $searchInput.css('width'));
                $autocomplete.css({
                    'position': 'absolute',
                    'left': xpos + "px",
                    'top': ypos + "px"
                });
            });

        },

        /**
         * 过滤选择的数据
         */
        filterSelectData: function (data, relationFieldMapping) {
            if (data && data.length > 0 && relationFieldMapping && relationFieldMapping.length > 0) {
                var filterRelationFieldMapping = [];
                for (var i = 0; i < relationFieldMapping.length; i++) {
                    if (true == relationFieldMapping[i].uniqueness) {
                        filterRelationFieldMapping.push(relationFieldMapping[i]);
                    }
                }
                // 当映射字段都没有设置唯一时 以全部字段为判断
                if (filterRelationFieldMapping.length < 1) {
                    filterRelationFieldMapping = relationFieldMapping;
                }
                var valueJsons = [];
                var filterFormFields = [];

                var issubformdialog = false;
                if (this.getCtlName().indexOf('____' + this.options.columnProperty.columnName) > 0) {
                    issubformdialog = true;
                } else if (this.getCtlName().indexOf('___' + this.options.columnProperty.columnName) > 0) {
                    issubformdialog = false;
                }

                // 得到过滤字段 过滤字段的值
                for (var i = 0; i < filterRelationFieldMapping.length; i++) {
                    var tempObj = filterRelationFieldMapping[i];
                    var sqlField = tempObj.sqlField;
                    var formField = tempObj.formField;

                    filterFormFields.push(tempObj);

                    var control = {};
                    if (this.getPos() == dyControlPos.subForm) {
                        var datauuid = _this.getDataUuid();
                        if (issubformdialog) {
                            control = $.ControlManager.getCtl(datauuid + '____' + formField);
                        } else {
                            var cellId = $.wSubFormMethod.getCellId(datauuid, formField);
                            control = $.ControlManager.getCtl(cellId);
                        }
                        if (typeof control == "undefined" || control == null) {
                            continue;
                        }
                        var values = control.getValue().split(SEPARATOR);
                    } else {
                        control = $.ControlManager.getCtl(formField);
                        // alert(control.getCtlName() + "---" + value);
                        if (control == undefined || control == null) {// 设置自定义映射
                            values = $("#" + formField).val().split(SEPARATOR);
                        } else {
                            if (typeof control == "undefined" || typeof control.setValue == "undefined") {
                                continue;
                            }

                            var values = control.getValue().split(SEPARATOR);
                        }
                    }
                    for (var j = 0; j < values.length; j++) {
                        if (valueJsons.length < (j + 1)) {
                            var valueJson = {};
                            valueJsons.push(valueJson);
                        } else {
                            var valueJson = values[j]
                        }
                        valueJson[formField] = values[j];
                    }
                }
                // 过滤
                var filterIndexs = [];
                for (var i = 0; i < data.length; i++) {
                    var dataJson = data[i];
                    for (var j = 0; j < valueJsons.length; j++) {
                        var valueJson = valueJsons[j];

                        var isSame = true;
                        for (var index = 0; index < filterFormFields.length; index++) {
                            if (dataJson[filterFormFields[index]['sqlField']] != valueJson[filterFormFields[index]['formField']]) {
                                isSame = false;
                                break;
                            }
                        }

                        if (isSame) {
                            filterIndexs.push(i);
                        }
                    }
                }
                // 删除过滤
                for (var i = filterIndexs.length - 1; i >= 0; i--) {
                    data.splice(filterIndexs[i], 1);
                }
            }
        },


        isFieldHide: function (mappingFields, fieldName) {
            for (var i = 0; i < mappingFields.length; i++) {
                var tempObj = mappingFields[i];
                var sqlField = tempObj.sqlField;
                var formField = tempObj.formField;
                var isHide = tempObj.isHide;
                if (fieldName.toLowerCase() == sqlField.toLowerCase()) {
                    if (typeof isHide == "undefined") {
                        return false;
                    } else {
                        return isHide;
                    }

                }
            }
            return false;

        },
        getFieldWidth: function (mappingFields, fieldName) {
            for (var i = 0; i < mappingFields.length; i++) {
                var tempObj = mappingFields[i];
                var sqlField = tempObj.sqlField;
                var formField = tempObj.formField;
                var width = tempObj.width;
                if (fieldName.toLowerCase() == sqlField.toLowerCase()) {
                    if (typeof width == "undefined" || $.trim(width).length == 0) {
                        break;
                    } else {
                        return width;
                    }

                }
            }
            return "";
        },

        isSessionTimeout: function (sessionId) {
            if (typeof jqueryWDialogSession[sessionId] == "undefined") {
                return true;
            } else {
                return false;
            }
        },
        createSession: function () {
            var sessionId = new UUID().id.toLowerCase();
            jqueryWDialogSession[sessionId] = sessionId;
            return sessionId;
        },
        clearAllSession: function () {
            jqueryWDialogSession = {};
        },
        getContraint: function () {
            var _this = this;
            var relationDataTwoSql = this.options.relationDataTwoSql;
            var contraint = {};
            if (typeof relationDataTwoSql != "undefined" && $.trim(relationDataTwoSql).length > 0) {
                var fieldvalues = relationDataTwoSql.split(";");
                for (var i = 0; i < fieldvalues.length; i++) {
                    var fieldValue = fieldvalues[i].split("=");
                    if (fieldValue.length != 2) {
                        continue;
                    }
                    var field = fieldValue[0];
                    var valueField = fieldValue[1];

                    if (_this.getPos() == dyControlPos.subForm) {// 在从表中
                        var control = $.ControlManager.getCtl(_this.getContronId(valueField));
                        if (typeof control == "undefined" || control == null) {// 找不到控件
                        	control = $.ControlManager.getCtl(_this.getCellId(null, valueField));
                        }
                        if (typeof control == "undefined" || control == null) {// 找不到控件
                            continue;
                        }

                        var value = control.getValue();
                        if (typeof value == "undefined" || value == null || $.trim(value).length == 0) {// 没有设值
                            continue;
                        }

                        // sqlFields.push(field);

                        contraint[field] = value;
                    } else {
                        var control = $.ControlManager.getCtl(_this.getContronId(valueField));
                        if (typeof control == "undefined" || control == null) {// 找不到控件
                            continue;
                        }


                        var value = control.getValue();
                        if (typeof value == "undefined" || value == null || $.trim(value).length == 0) {// 没有设值
                            continue;
                        }

                        // sqlFields.push(field);

                        contraint[field] = value;
                    }


                }
            }

            return contraint;
        },
        /* 设置到可编辑元素中 */
        setValue2EditableElem: function () {
            if (this.$editableElem == null) {
                return;
            }

            if (this.options.isRelevantWorkFlow == "1") {// 流程列表控件
                this.drawAboutDocument();

            } else {
                // modify by xujm 2015-12-23 新增多选 start
                if (this.options.searchMultiple) {
                    this.drawAboutDocumentForMultiple(this.options.columnProperty.showType == dyshowType.edit);
                } else if (this.options.countNum2Input) {
                    this.createCountHref(this.value);
                    $.wTextCommonMethod.setValue2EditableElem.call(this);
                } else {
                    $.wTextCommonMethod.setValue2EditableElem.call(this);
                }
                // modify by xujm 2015-12-23 新增多选 end
            }

        },

        afterInit: function () {
            if (this.options.countNum2Input) {
                this.createCountHref(this.value);
            }
        },

        setValue2LabelElem: function () {
            if (this.$labelElem == null) {
                return;
            }
            if (this.options.isRelevantWorkFlow == "1") {// 流程列表控件
                this.drawAboutDocument();
            } else {
                $.wTextCommonMethod.setValue2LabelElem.call(this);
            }

        },
        /**
         * add by xujm 2015-12-23 多选显示方法 isEdit 是否可以编辑
         */
        drawAboutDocumentForMultiple: function (isEdit) {
            var _this = this;
            if (this.$docContainer == null || this.$docContainer == undefined) {
                if (this.$editableElem == null || this.$editableElem == undefined) {
                    this.create$EditableElemOfMultiple();
                }
                this.$docContainer = this.$editableElem.parent().parent();
            }
            this.$docContainer.find(".dyfrom-search-choice").remove();
            if (this.value) {
                var values = this.value.split(SEPARATOR);
                for (var i = 0; i < values.length; i++) {
                    this.$editableElem.parent().before('<li class="dyfrom-search-choice">'
                        + '<span>' + values[i] + '</span>'
                        + (isEdit ? '<a href="javascript:void(0)" class="dyfrom-search-choice-remove" rel="' + i + '">X</a>' : '')
                        + '</li>');
                }

                // 删除按钮删除事件
                this.$docContainer.find(".dyfrom-search-choice-remove").bind('click', function () {
                    _this.removeValueByIndex($(this).attr('rel'));
                });
            }
            if (isEdit) {
                this.$editableElem.show();
                this.$editableElem.keydown();
            } else {
                this.$editableElem.hide();
            }
        },
        removeValueByIndex: function (index) {
            var issubformdialog = false;
            if (this.getCtlName().indexOf('____' + this.options.columnProperty.columnName) > 0) {
                issubformdialog = true;
            } else if (this.getCtlName().indexOf('___' + this.options.columnProperty.columnName) > 0) {
                issubformdialog = false;
            }
            for (var i = 0; i < this.options.relationFieldMappingstrArray.length; i++) {
                var tempObj = JSON.parse(this.options.relationFieldMappingstrArray[i]);
                var sqlField = tempObj.sqlField;
                var formField = tempObj.formField;
                var control = {};
                if (this.getPos() == dyControlPos.subForm) {
                    var datauuid = _this.getDataUuid();
                    if (issubformdialog) {
                        control = $.ControlManager.getCtl(datauuid + '____' + formField);
                    } else {
                        var cellId = $.dyform.getCellId(datauuid, formField);
                        control = $.ControlManager.getCtl(cellId);
                    }
                    if (typeof control == "undefined" || control == null) {
                        continue;
                    }
                    this.removeControlIndexVal(control, index);
                } else {
                    control = $.ControlManager.getCtl(formField);
                    // alert(control.getCtlName() + "---" + value);
                    if (control == undefined || control == null) {// 设置自定义映射
                        $("#" + formField).val(value);
                    } else {
                        if (typeof control == "undefined" || typeof control.setValue == "undefined") {
                            continue;
                        }

                        this.removeControlIndexVal(control, index);
                    }
                }
                // modify by wujx 20161010 begin
                // if(control!=undefined && control != null){
                // control.validateElem();
                // }
                // modify by wujx 20161010 begin
            }
        },
        removeControlIndexVal: function (control, index) {
            var values = control.getValue().split(SEPARATOR);
            values.splice(index, 1);
            control.setValue(values.join(SEPARATOR));
        },
        drawAboutDocument: function () {
            if (typeof this.value == "undefined" || this.value == null || $.trim(this.value).length == 0) {// 清空
                if (this.$docContainer == null || this.$docContainer == undefined) {
                    return;
                } else {
                    this.$docContainer.empty();
                }
                return;
            }
            // /this.$placeHolder.
            if (this.$docContainer == null || this.$docContainer == undefined) {
                this.$docContainer = $("<span class='about_document'></span>");
                this.$placeHolder.after(this.$docContainer);

            }
            this.$docContainer.empty();
            var values = this.value;
            if (typeof values == "string") {
                values = JSON.parse(values)
            }
            for (var i = 0; i < values.length; i++) {
                var val = values[i];
                if (this.$docContainer.html().length == 0) {

                } else {
                    // this.$docContainer.append("<br/>");
                }
                this.$docContainer.append("<div class='glRowDate'><a target='_blank' href='" + ctx + "/workflow/work/view/work?flowInstUuid=" + val.uuid + "' uuid='" + val.uuid + "'  formUuid='" + val.formUuid + "' dataUuid='" + val.dataUuid + "' style='cursor:pointer;' title='" + val.title + "'>" + val.title + "</a><button style='height: 24px;padding: 0;margin: 0 10px;' class='deleteRowDateBut' uuid='" + val.uuid + "'  " + (this.options.isShowAsLabel ? "display='none" : "") + ">删除</button><div>");
                // this.$docContainer.append("");
            }
            // workflow/work/view/colligate?taskUuid=7b880853-b632-4799-b422-0616f3f42e89&flowInstUuid=&moduleid=7605a2d2-a7cf-430e-a2df-86fbaf074314&wid=View&indexLdxDffModuleId=&pageCurrentPage=1
            // 默认显示5条,点击更多显示全部
            if (values.length > 0) {
                var _this = this;
                var maxRowNum = 5;
                if (values.length > maxRowNum) {
                    _this.$docContainer.append("<div><button style='height: 24px;padding: 0;margin: 0;' id='showMoreGlRowDate'>更多</button><div>");
                    _this.$docContainer.find("#showMoreGlRowDate").toggle(function () {
                        _this.$docContainer.find(".glRowDate:gt(" + (maxRowNum - 1) + ")").show();
                        $(this).text("取消更多");
                    }, function () {
                        _this.$docContainer.find(".glRowDate:gt(" + (maxRowNum - 1) + ")").hide();
                        $(this).text("更多");
                    });
                }
                _this.$docContainer.find(".glRowDate:gt(" + (maxRowNum - 1) + ")").hide();
                // 删除
                _this.$docContainer.find(".deleteRowDateBut").bind('click', function () {
                    var delUuid = $(this).attr('uuid');
                    var oldValues = _this.value;
                    if (typeof oldValues == "string") {
                        oldValues = JSON.parse(oldValues)
                    }
                    var newValues = [];
                    for (var i = 0; i < oldValues.length; i++) {
                        var val = oldValues[i];
                        if (val.uuid != delUuid) {
                            newValues.push(val);
                        }
                    }
                    // 流程弹出框
                    if (_this.options.isRelevantWorkFlow == "1") {
                        _this.setValue(JSON.stringify(newValues));
                        // 其他视图弹出框
                    } else {
                        if (_this.invoke("beforePressConfirm", newValues) == false)
                            return;

                        if (_this.options.destType == dyControlPos.subForm) {// 从表
                            _this.addRowData(_this.options.relationFieldMappingstrArray, newValues);// 添加到从表中
                        } else {
                            _this.setValue2Controls(_this.options.relationFieldMappingstrArray, newValues[0]);
                        }
                    }
                    return false;
                });
            }
            // modifiey by xujm 增加默认显示5条,点击更多显示全部 和 删除 2015-08-19 end
        },


        create$DialogElement: function (relationFieldMappingArray, _relationDataValueTwo, _relationDataTwoSql, _relationDataIdTwo) {

            if (this.getPos() == dyControlPos.subForm && this.options.destType != dyControlPos.mainForm) {
                alert(this.getCtlName + "-" + "不宜将目标为从表的弹出框控件做为从表的控件");
                return;
            }


            var _this = this;
            if (_this.options.isRelevantWorkFlow == "1") {// 流程弹出框
                _this.$editableElem.val("点击选择相关文档");
                _this.$editableElem.attr("readonly", "readonly");
            }
            this.$editableElem.unbind("click");
            this.$editableElem.click(function () {
            	var contraint = _this.getContraint();
                var show = _this.invoke("beforeDialogShow", contraint);


                if (show === false) {
                    return false;
                }

                // 获得要查询的字段
                var str = "";
                var path = "";
                if (_this.options.isRelevantWorkFlow == "1") {// 流程弹出框
                    var title = _this.options.dialogTitle == null || _this.options.dialogTitle == '' ? "选择(" + columnProperty.displayName + ")" : _this.options.dialogTitle;
                    var json = new Object();
                    json.message = "<div class='dialog-picker' id='dialog-picker'><div style='width:99%;' id='" + (_relationDataIdTwo) + "'></div>";
                    json.title = title;
                    json.size = _this.options.dialogSize;
                    json.buttons = {
                        "确定": function () {
                            var datas = $("#" + (_relationDataIdTwo)).wBootstrapTable("getSelections");// 收集数据
                            if (datas.length <= 0) {
                                alert("请选择一行数据");
                                return false;
                            }
                            if (_this.invoke("beforePressConfirm", datas) == false) {
                                return false;
                            }
                            var vals = [];
                            for (var i = 0; i < datas.length; i++) {
                                var val = datas[i];
                                vals.push({
                                    uuid: val["uuid"],
                                    formUuid: val["formUuid"],
                                    dataUuid: val["dataUuid"],
                                    title: val["title"]
                                });

                            }
                            _this.setValue(JSON.stringify(vals));
                            appModal.hide();
                        }
                    };
                    appModal.dialog(json);
                    _this.invoke("afterDialogShow");

                    appContext.renderWidget({
                        refreshIfExists: false,
                        renderTo: "dialog-picker",
                        widgetDefId: _relationDataIdTwo,
                        callback: function () {
                            var $bt = $("#" + (_relationDataIdTwo));
                            var workflow = _this.options.workflow;
                            if (workflow && workflow.workflowId) {
                                var otherConditions = [];
                                otherConditions.push({
                                    columnIndex: "flowDefId",
                                    value: workflow.workflowId,
                                    type: 'in'
                                })
                                // 设置条件
                                // $bt.wBootstrapTable("addOtherConditions", otherConditions);
                                $bt.wBootstrapTable("addParam", "flowDefId", workflow.workflowId);
                                setTimeout(function () {
                                    $bt.wBootstrapTable("refresh");
                                }, 32);
                            }
                            $(".dialog-picker").die("dblclick").live("dblclick", function (event) {
                                var $target = null;
                                if (event.target && ($target = $(event.target).parents("tr[data-index]")).length) {
                                    var dataIndex = $target.attr("data-index");
                                    var jsonObj = $bt.wBootstrapTable("getData")[dataIndex];
                                    var vals = [];
                                    vals.push({
                                        uuid: jsonObj["uuid"],
                                        formUuid: jsonObj["formUuid"],
                                        dataUuid: jsonObj["dataUuid"],
                                        title: jsonObj["title"]
                                    });
                                    _this.setValue(JSON.stringify(vals));
                                    appModal.hide();
                                }
                            });
                        },
                    });
                } else {// 其他视图弹出框
                    for (var j = 0; j < relationFieldMappingArray.length; j++) {
                        if (relationFieldMappingArray[j] != "") {
                            var tempObj = relationFieldMappingArray[j];
                            str += ',' + tempObj.sqlField;
                        }
                    }
                    /*
                    for(var i = 0; i < relationFieldMappingArray.length; i++){// 去掉下划线
                        var fields = relationFieldMappingArray[i];
                        //fields.sqlField = fields.sqlField.replace("_","");
                        //fields.sqlField = fields.sqlField.replace("_","");
                        //fields.sqlField = fields.sqlField.replace("_","");
                    }
                    */

                    
                    var title = _this.options.dialogTitle == null || _this.options.dialogTitle == '' ? "选择(" + _this.options.columnProperty.displayName + ")" : _this.options.dialogTitle;
                    var json = new Object();
                    json.message = "<div class='dialog-picker' id='dialog-picker'><div style='width:99%;' id='" + (_relationDataIdTwo) + "'></div>";
                    json.title = title;
                    json.size= _this.options.dialogSize;
                    json.buttons = {
                        "确定": function () {
                            var datas = $("#" + (_relationDataIdTwo)).wBootstrapTable("getSelections");// 收集数据
                            if (datas.length <= 0 && !_this.options.countNum2Input) {
                                alert("请选择一行数据");
                                return false;
                            }
                            if (_this.invoke("beforePressConfirm", datas) == false) {
                                return false;
                            }

                            if (_this.options.destType == dyControlPos.subForm) {// 从表
                                _this.addRowData(relationFieldMappingArray, datas);// 添加到从表中
                            } else {
                                var data = {};
                                for (var i = 0; i < datas.length; i++) {
                                    var obj = datas[i];
                                    for (var key in obj) {
                                        if (StringUtils.isBlank(obj[key])) {
                                            continue;
                                        }
                                        var prefix = ";";
                                        if (typeof data[key] === "undefined" || data[key] == null) {
                                            prefix = "";
                                            data[key] = "";
                                        }
                                        data[key] += prefix + obj[key];
                                    }
                                }
                                _this.setValue2Controls(relationFieldMappingArray, data);
                            }
                            appModal.hide();
                        }
                    };

                    appModal.dialog(json);
                    _this.invoke("afterDialogShow");
                    // 表格视图的默认条件,条件语句支持解析表单字段变量
		            var formData = {};
		            _this.dyform$Context().collectFormData(function (result) {
		                formData = result.formDatas[result.formUuid][0];
		            }, $.noop);
		            var queryDefaultCondition = _this.options.queryDefaultCondition;
		            if (queryDefaultCondition) {
		                var returnParams = appContext.resolveParams({"result": queryDefaultCondition}, formData);
		                queryDefaultCondition = returnParams.result;
		            }
                    appContext.renderWidget({
                        refreshIfExists: false,
                        renderTo: "dialog-picker",
                        widgetDefId: _relationDataIdTwo,
                        defaultCondition: queryDefaultCondition,
                        callback: function () {
                            var $bt = $("#" + (_relationDataIdTwo));
                            var otherConditions = [];
                            for (var key in contraint) {
                                otherConditions.push({
                                    columnIndex: key,
                                    value: contraint[key],
                                    type: 'eq'
                                })
                            }
//                            var queryDefaultCondition =_this.getQueryDefaultCondition();
//                            otherConditions = otherConditions.concat(queryDefaultCondition);
                            // 设置条件
                            $bt.wBootstrapTable("addOtherConditions", otherConditions);
//                            $bt.wBootstrapTable("addOtherConditions", queryDefaultCondition);
                            if (otherConditions && otherConditions.length) {
                                setTimeout(function () {
                                    $bt.wBootstrapTable("refresh", otherConditions);
                                }, 32);
                            }
                            $(".dialog-picker").die("dblclick").live("dblclick", function (event) {
                                var $target = null;
                                if (event.target && ($target = $(event.target).parents("tr[data-index]")).length) {
                                    var dataIndex = $target.attr("data-index");
                                    var jsonObj = $bt.wBootstrapTable("getData")[dataIndex];
                                    var datas = [];
                                    datas.push(jsonObj);
                                    if (_this.options.destType == dyControlPos.subForm) {// 从表
                                        _this.addRowData(relationFieldMappingArray, datas);// 添加到从表中
                                    } else {
                                        _this.setValue2Controls(relationFieldMappingArray, jsonObj);
                                    }
                                    appModal.hide();
                                }
                            });
                        },
                    });

                }


            });
        },
        /*******************************************************************
         * 收集选中行的数据
         */
        collectData: function () {
            var $checkeds = $("#dialogModule").find(".checkeds:checked");
            var viewDatas = [];
            $checkeds.each(function () {
                if ($(this).parent().parent().is(".dataTr")) {
                    var $tr = $(this).parent().parent();
                    var jsonstr = $tr.attr("jsonstr");
                    var jsonObj = eval("(" + urldecode(jsonstr) + ")");

                    viewDatas.push(jsonObj);
                }
            });
            return viewDatas;
        },

        /**
         * 将数据添加到从表中
         */
        addRowData: function (mappingFields, datas) {
            var $form = this.$placeHolder.parents("form");
            var destFormId = this.options.destSubform;
            var paramsObjs = [];
            for (var index = 0; index < datas.length; index++) {
                var fieldsKeyValue = datas[index];
                var fieldsKeyValueObj = {};
                for (var i in fieldsKeyValue) {
                    fieldsKeyValueObj[i.toLowerCase()] = fieldsKeyValue[i];
                }
                fieldsKeyValue = fieldsKeyValueObj;
                var _this = this;
                var paramsObj = {};

                for (var i = 0; i < mappingFields.length; i++) {
                    var tempObj = mappingFields[i];
                    var sqlField = tempObj.sqlField;
                    var formField = tempObj.formField;
                    var value = fieldsKeyValue[sqlField.toLowerCase()];
                    if (typeof value == "undefined") {
                        value = "";
                    }
                    paramsObj[formField] = value;
                }
                paramsObjs.push(paramsObj);
                $form.dyform("addRowData", destFormId, paramsObj);// 添加到从表中
            }

            // alert(JSON.cStringify(paramsObjs));


            if (_this.options.afterDialogSelect) {
                _this.options.afterDialogSelect.call(this, this.getCtlName(), paramsObjs, datas);
            }
        },
        setValue2ControlsWithOutAfterDialogSelect: function (mappingFields, fieldsKeyValue) {
            var issubformdialog = false;
            if (this.getCtlName().indexOf('____' + this.options.columnProperty.columnName) > 0) {
                issubformdialog = true;
            } else if (this.getCtlName().indexOf('___' + this.options.columnProperty.columnName) > 0) {
                issubformdialog = false;
            }
            var fieldsKeyValueObj = {};
            for (var i in fieldsKeyValue) {
                fieldsKeyValueObj[i.toLowerCase()] = fieldsKeyValue[i];
            }
            fieldsKeyValue = fieldsKeyValueObj;
            var _this = this;
            var paramsObj = {};
            for (var i = 0; i < mappingFields.length; i++) {
                var tempObj = mappingFields[i];
                var sqlField = tempObj.sqlField;
                var formField = tempObj.formField;
                var value = fieldsKeyValue[sqlField.toLowerCase()];
                if (typeof value == "undefined") {
                    value = "";
                }
                var control = {};
                if (_this.getPos() == dyControlPos.subForm) {
                    var datauuid = _this.getDataUuid();
                    if (issubformdialog) {
                        control = $.ControlManager.getCtl(datauuid + '____' + formField);
                        paramsObj[datauuid + '____' + formField] = value;
                    } else {
                        var cellId = $.wSubFormMethod.getCellId(datauuid, formField);
                        control = $.ControlManager.getCtl(cellId);
                        paramsObj[cellId] = value;
                    }
                    if (typeof control == "undefined" || control == null) {
                        continue;
                    }

                    _this.setControlValue(control, value);
                } else {
                	var name = _this.getCellId(null, formField);
                    control = $.ControlManager.getCtl(name);
                    if (control == undefined || control == null) {// 设置自定义映射
                        if ($("#" + formField).val()) {
                            $("#" + formField).val($("#" + formField).val() + SEPARATOR + value);
                        } else {
                            $("#" + formField).val(value);
                        }
                    } else {
                        if (typeof control == "undefined" || typeof control.setValue == "undefined") {
                            continue;
                        }
                        _this.setControlValue(control, value);
                        paramsObj[formField] = value;
                    }
                }
            }
            return paramsObj;
        },
        setControlValue: function (control, value) {
            if (this.options.searchMultiple) {
                var valueOfControl = control.getValue();
                if (StringUtils.isNotBlank(control.getValue())) {
                    value = control.getValue() + SEPARATOR + value;
                }
            }
            control.setValue(value);
        },
        /**
         * 将值设置到各映射字段中
         */
        setValue2Controls: function (mappingFields, fieldsKeyValue) {
            var paramsObj = this.setValue2ControlsWithOutAfterDialogSelect(mappingFields, fieldsKeyValue);
            if (this.options.afterDialogSelect) {
                this.options.afterDialogSelect.call(this, this.getCtlName(), paramsObj, fieldsKeyValue);
            }

            this.validateElem();// 再验证

        },
    };

    /*
     * DIALOG PLUGIN DEFINITION =========================
     */
    $.fn.wdialog = function (option) {
        var method = false;
        var args = null;
        if (arguments.length == 2) {
            method = true;
            args = arguments[1];
        }

        if (typeof option == 'string') {
            if (option === 'getObject') { // 通过getObject来获取实例
                var $this = $(this),
                    data = $this.data('wdialog');
                if (data) {
                    return data; // 返回实例对象
                } else {
                    throw new Error('This object is not available');
                }
            }
        }


        var $this = $(this),
            data = $this.data('wdialog'),
            options = typeof option == 'object'
                && option;
        if (!data) {
            data = new Dialog($(this), options);

            $.extend(data, $.wControlInterface);

            $.extend(data, $.wTextCommonMethod);
            $.extend(data, $.Dialog);
            data.init();
            $this.data('wdialog', data);
        }
        if (typeof option == 'string') {
            if (method == true && args != null) {
                return data[option](args);
            } else {
                return data[option]();
            }


        } else {
            return data;
        }


    };


    $.fn.wdialog.Constructor = Dialog;

    $.fn.wdialog.defaults = {
        columnProperty: columnProperty,// 字段属性
        commonProperty: commonProperty,// 公共属性
        readOnly: false,
        isShowAsLabel: false,
        disabled: false,
        countNum2Input: false,
        isHide: false,// 是否隐藏
        relationDataTextTwo: "",
        relationDataValueTwo: "",
        relationDataSourceTwo: "",
        countNumHref: false,
        countNumHrefText: null,
        relationDsDefaultCondition: "",
        relationDataTwoSql: "",
        relationDataDefiantion: "",
        relationDataShowMethod: "",
        relationDataShowType: "",
        dialogTitle: null,
        dialogSize:null,
        allowValNotInDs: false,//搜索项时候是否允许输入值非数据仓库的数据值
        afterDialogSelect: function () {
        },
        searchMultiple: false,// 是否多选，默认为false
    };

    var SEPARATOR = ";";
})(jQuery);
var jqueryWDialogSession = {};
