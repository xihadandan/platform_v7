;
(function($) {
    $.ControlUtil = {
        /**
         * 设置控件的公共属性
         * 
         * @param elment
         * @param options
         */
        setCommonCtrAttr: function(elment, options) {
            // 公共属性设置
            elment.attr("applyTo", options.columnProperty.applyTo); // 应用于
            elment.attr("controlName", options.columnProperty.controlName); // 字段定义
            elment.attr("columnName", options.columnProperty.columnName); // 字段定义
            elment.attr("displayName", options.columnProperty.displayName); // 描述名称
            elment.attr("dbDataType", options.columnProperty.dbDataType); // 字段类型?
            elment.attr("indexed", options.columnProperty.indexed); // 是否索引
            elment.attr("showed", options.columnProperty.showed); // 是否界面表格显示
            elment.attr("sorted", options.columnProperty.sorted); // 是否排序
            elment.attr("sysType", options.columnProperty.sysType); // 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
            elment.attr("length", options.columnProperty.length); // 字段长度
            elment.attr("showType", options.columnProperty.showType); // 是否隐藏
            elment.attr("defaultValue", options.columnProperty.defaultValue); // 默认值
            elment.attr("valueCreateMethod",
                options.columnProperty.valueCreateMethod); // 默认值创建方式 1用户输入
            elment.attr("onlyreadUrl", options.columnProperty.onlyreadUrl); // 只读状态下设置跳转的url

            elment.attr("inputMode", options.commonProperty.inputMode); // 输入方式
            elment.attr("fieldCheckRules", options.commonProperty.fieldCheckRules); // 校验规则
            elment.attr("unitUnique", options.commonProperty.unitUnique); // 校验规则
            elment.attr("textAlign", options.commonProperty.textAlign); // 对齐方式
            elment.attr("fontSize", options.commonProperty.fontSize); // 字段的大小
            elment.attr("fontColor", options.commonProperty.fontColor); // 字段的颜色
            elment.attr("ctlWidth", options.commonProperty.ctlWidth); // 宽度
            elment.attr("ctlHight", options.commonProperty.ctlHight); // 高度
            /*
             * elment.attr("uploadfiletype",options.uploadfiletype); //附件上传样式
             * elment.attr("isgetzhengwen",options.isgetzhengwen);//附件上传是否正文
             * TODO
             */
        },

        /**
         * 设置是否可用（html属性为disabled）
         */
        setEnable: function(elment, isenable) {
            if (isenable) {
                elment.removeAttr("disabled");
            } else {
                elment.attr("disabled", "disabled");
            }
        },

        /**
         * 设置是否可用（html属性为readonly）
         */
        setReadOnly: function(elment, isreadonly) {
            if (isreadonly) {
                elment.attr("readonly", "readonly");
            } else {
                elment.removeAttr("readonly");
            }
        },

        /**
         * 设置是否可用（html属性为readonly）
         */
        setVisible: function(elment, isvisible) {
            if (isvisible) {
                elment.show();
                // label也需要隐藏
                if (elment.parent().is("tr[class='field']")) {
                    elment.parent().show();
                } else if (elment.parent().parent().is("tr[class='field']")) {
                    elment.parent().parent().show();
                } else if (elment.parent().parent().parent().is(
                        "tr[class='field']")) {
                    elment.parent().parent().parent().show();
                }
            } else {
                elment.hide();
                // label也需要隐藏
                if (elment.parent().is("tr[class='field']")) {
                    elment.parent().hide();
                } else if (elment.parent().parent().is("tr[class='field']")) {
                    elment.parent().parent().hide();
                } else if (elment.parent().parent().parent().is(
                        "tr[class='field']")) {
                    elment.parent().parent().parent().hide();
                } else {
                    // add by wujx 20160621
                    console.error("如果要实现控件所在行整行要隐藏，需要设置所属父节点tr的class='field'，才能生效！");
                }
            }
        },

        /**
         * setIsDisplayAsLabel的逆向操作
         * 
         * @param elment
         * @param options
         * @param islabelshow
         */
        setDisplayAsCtl: function(elment, options) {
            if (options.isShowAsLabel == false) {
                return;
            }
            elment.show();
            if (elment.next().is('span')) {
                elment.next().remove();
            }
            options.isShowAsLabel = false;
            // 聚焦
            elment.focus();
        },

        /**
         * 设置控件作为标签显示
         */
        setDisplayAsLabel: function(elment, options, islabelshow, ctlObj) {
            /*
             * if(options.isShowAsLabel==true){ return; }
             */
            if (elment.attr("valid") == "false") { // 验证不通过时方不可显示为label
                return;
            }
            var name = elment.attr("name");
            $("#span" + name).remove();
            // 只显示为label.
            // console.log(JSON.cStringify(elment[0].tagName));
            var val = "";
            if (ctlObj) {
                val = ctlObj.getValue();
            } else {
                val = elment.val();
            }
            elment.hide();
            this.setSpanStyle(elment, options.commonProperty.textAlign,
                options.commonProperty.fontSize,
                options.commonProperty.fontColor,
                options.commonProperty.ctlWidth,
                options.commonProperty.ctlHight, val);
            options.isShowAsLabel = true;
        },

        /**
         * 设置必输 返回设置后的规则.
         */
        setRequired: function(isrequire, options) {
            var checkruleisempty;
            var fieldCheckRules = options.commonProperty.fieldCheckRules;
            if (fieldCheckRules == "" || fieldCheckRules == undefined) {
                checkruleisempty = true;
            } else {
                checkruleisempty = false;
            };
            if (checkruleisempty) {
                if (isrequire) {
                    fieldCheckRules = "[{value:'1',label:'非空'}]";
                }
            } else {
                if (typeof fieldCheckRules == "string") {
                    var jsonarray = eval('(' + fieldCheckRules + ')');
                } else {
                    var jsonarray = fieldCheckRules;
                }

                // 增加元素
                if (isrequire) {
                    var isaddrequire = true;
                    for (var k = 0; k < jsonarray.length; k++) {
                        if (dyCheckRule.notNull == jsonarray[k].value) {
                            isaddrequire = false;
                            break;
                        }
                    }
                    if (isaddrequire) {
                        jsonarray.push({
                            value: '1',
                            label: "非空"
                        });
                    }
                }
                var checkrules = [];
                for (var k = 0; k < jsonarray.length; k++) {
                    if (dyCheckRule.notNull == jsonarray[k].value && !isrequire) {
                        continue;
                    }
                    checkrules.push({
                        value: jsonarray[k].value,
                        label: jsonarray[k].label
                    });
                }
                fieldCheckRules = JSON.cStringify(checkrules);
            }

            // 删除必输
            //			if (!isaddrequire && fieldCheckRules.length > 0) {
            //				var rules = eval("(" + fieldCheckRules + ")");
            //				var rules2 = [];
            //				for (var z = 0; z < rules.length; z++) {
            //					if (rules[z].value == dyCheckRule.notNull) {//
            //						rules2.push({
            //							value : rules[z].value,
            //							label : rules[z].label
            //						});
            //					}
            //				}
            //				fieldCheckRules = JSON.cStringify(rules2);
            //			}
            options.commonProperty.fieldCheckRules = fieldCheckRules;
        },

        /**
         * eg. email: { required: "请输入Email地址", email: "请输入正确的email地址" }, email: {
         * required: true, email: true },
         * 
         */
        getCheckRuleAndMsg: function(options) {
            var ruleAndMsgMap = {};
            var msgmap = {};
            var rulemap = {};
            var commonProperty = options.commonProperty;
            var fieldCheckRules = commonProperty.fieldCheckRules;
            var columnProperty = options.columnProperty;
            if (fieldCheckRules == "" || fieldCheckRules == undefined) {

            } else {
                var rules = eval(fieldCheckRules);
                var ruleAndMsgMap = {};
                var msgmap = {};
                var rulemap = {};
                for (var k = 0; k < rules.length; k++) {
                    var reminderMsg;
                    //判断正则校验提示是否存在，若设置正则校验提示，则使用正则校验提示，若没有，采用默认提示
                    if (commonProperty.regularValidateReminder &&
                        $.trim(commonProperty.regularValidateReminder).length > 0) {
                        reminderMsg = commonProperty.regularValidateReminder;
                    } else {
                        reminderMsg = rules[k].label;
                    }
                    if (dyCheckRule.notNull == rules[k].value) { // 非空
                        //判断非空提示是否存在，若设置非空提示，则使用非空提示，若没有，采用默认提示
                        if (commonProperty.noNullValidateReminder != undefined && commonProperty.noNullValidateReminder != null &&
                            commonProperty.noNullValidateReminder != "") {
                            msgmap["required"] = commonProperty.noNullValidateReminder;
                        } else {
                            msgmap["required"] = rules[k].label;
                        }
                        rulemap["required"] = true;
                    } else if (dyCheckRule.url == rules[k].value) { // url						
                        msgmap["url"] = reminderMsg;
                        rulemap["url"] = true;
                    } else if (dyCheckRule.email == rules[k].value) { // email
                        msgmap["email"] = reminderMsg;
                        rulemap["email"] = true;
                    } else if (dyCheckRule.idCard == rules[k].value) { // 身份证
                        msgmap["isIdCardNo"] = reminderMsg;
                        rulemap["isIdCardNo"] = true;
                    } else if (dyCheckRule.postcode == rules[k].value) { // 邮编
                        msgmap["isPostcode"] = reminderMsg;
                        rulemap["isPostcode"] = true;
                    } else if (dyCheckRule.customizeRegular == rules[k].value) { //自定义规则
                        msgmap["isCustomizeRegularText"] = reminderMsg;
                        rulemap["isCustomizeRegularText"] = commonProperty.customizedRegularText;

                    } else if (dyCheckRule.unique == rules[k].value) { // 唯一校验
                        //判断唯一校验提示是否存在，若设置唯一校验提示，则使用唯一校验提示，若没有，采用默认提示
                        if (commonProperty.uniqueValidateReminder != undefined && commonProperty.uniqueValidateReminder != null &&
                            commonProperty.uniqueValidateReminder != "") {
                            msgmap["unique"] = commonProperty.uniqueValidateReminder;
                        } else {
                            msgmap["unique"] = rules[k].label;
                        }
                        rulemap["unique"] = true;
                    } else if (dyCheckRule.tel == rules[k].value) { // 电话
                        msgmap["isPhone"] = reminderMsg;
                        rulemap["isPhone"] = true;
                    } else if (dyCheckRule.mobilePhone == rules[k].value) { // 手机
                        msgmap["isMobile"] = reminderMsg;
                        rulemap["isMobile"] = true;
                    } else if (dyCheckRule.num_int == rules[k].value) { // 整数
                        msgmap["isInteger"] = rules[k].label;
                        rulemap["isInteger"] = true;
                    } else if (dyCheckRule.num_long == rules[k].value) { // 长整数
                        msgmap["isInteger"] = rules[k].label;
                        rulemap["isInteger"] = true;
                    } else if (dyCheckRule.num_float == rules[k].value) { // 浮点数
                        msgmap["isFloat"] = rules[k].label;
                        rulemap["isFloat"] = true;
                    } else if (dyCheckRule.num_double == rules[k].value) { // 双精度浮点数
                        msgmap["isFloat"] = rules[k].label;
                        rulemap["isFloat"] = true;
                    }
                }
            }


            if (columnProperty.length &&
                $.trim(columnProperty.length).length > 0) { // 长度
                if ((columnProperty.dbDataType != dyFormDataType._clob && columnProperty.dbDataType != dyFormInputType._clob) && columnProperty.dbDataType != dyFormDataType.date &&
                    columnProperty.inputMode != dyFormInputMode.accessory1 &&
                    columnProperty.inputMode != dyFormInputMode.accessory3 &&
                    columnProperty.inputMode != dyFormInputMode.accessoryImg &&
                    columnProperty.inputMode != dyFormInputMode.textBody
                ) { // 大字段, 日期,不检验长度
                    rulemap["maxlength"] = parseInt(columnProperty.length, 10);
                    msgmap["maxlength"] = jQuery.validator.messages.maxlength;
                }
            }
            if (commonProperty.validateEventManage && $.trim(commonProperty.validateEventManage).length > 0) {
                rulemap["validateEvent"] = $.trim(commonProperty.validateEventManage);
                //判断唯一校验提示是否存在，若设置唯一校验提示，则使用唯一校验提示，若没有，采用默认提示
                if (commonProperty.eventValidateReminder != undefined && commonProperty.eventValidateReminder != null &&
                    commonProperty.eventValidateReminder != "") {
                    msgmap["validateEvent"] = commonProperty.eventValidateReminder;
                } else {
                    msgmap["validateEvent"] = "校验不通过！";
                }
            }

            ruleAndMsgMap["rule"] = rulemap;
            ruleAndMsgMap["msg"] = msgmap;

            return ruleAndMsgMap;
        },

        isRequired: function(options) {
            var fieldCheckRules = options.commonProperty.fieldCheckRules;
            if (fieldCheckRules == "" || fieldCheckRules == undefined) {
                return false;
            }
            // 往校验规则添加required
            var checkrules = "";
            if (typeof fieldCheckRules == "object") {
                checkrules = fieldCheckRules;
            } else {
                checkrules = eval('(' + fieldCheckRules + ')');
            }

            var isexistrequired = false;
            for (var k = 0; k < checkrules.length; k++) {
                if (dyCheckRule.notNull == checkrules[k].value) {
                    isexistrequired = true;
                    break;
                };
            };
            return isexistrequired;
        },

        /**
         * 设置只读状态下的span的样式
         * 
         * @param elment
         * @param textAlign
         * @param fontSize
         * @param fontColor
         * @param ctlWidth
         * @param ctlHight
         * @param val
         */
        setSpanStyle: function(elment, textAlign, fontSize, fontColor,
            ctlWidth, ctlHight, val) {
            var name = elment.attr("name");
            $("#span" + name).remove();
            var spanstyle = this.getStyleHtmlByParams(textAlign, fontSize,
                fontColor, ctlWidth, ctlHight, '');

            elment.after("<span  id='span" + name + "'" + spanstyle + " >" +
                val + "</span>");
        },

        /**
         * 通过参数获得初始化的样式。
         * 
         * @param textAlign
         * @param fontSize
         * @param fontColor
         * @param ctlWidth
         * @param ctlHight
         * @returns {String}
         */
        getStyleHtmlByParams: function(textAlign, fontSize, fontColor,
            ctlWidth, ctlHight, otherstyle) {
            var cssTemp = "style = '";
            if (textAlign == "center") {
                cssTemp += "text-align:center;";
            } else if (textAlign == "left") {
                cssTemp += "text-align:left;";
            } else if (textAlign == "right") {
                cssTemp += "text-align:right;";
            }
            if (fontSize != null && fontSize != "") {
                cssTemp += "font-size:" + fontSize + "px;";
            }
            if (fontColor != null && fontColor != "") {
                cssTemp += "color:" + fontColor + ";";
            }
            if (ctlWidth != null && ctlWidth != "") {
                cssTemp += "width:" + ctlWidth + "px;";
            }
            if (ctlHight != null && ctlHight != "") {
                cssTemp += "height:" + ctlHight + "px;";
            }
            if (otherstyle != null && otherstyle != "") {
                cssTemp += otherstyle;
            }
            cssTemp += "'";
            return cssTemp;
        },
    };

})(jQuery);