(function($) {
	var columnProperty = {
		// 控件字段属性
		applyTo : null,// 应用于
		columnName : null,// 字段定义 fieldname
		displayName : null,// 描述名称 descname
		dbDataType : '',// 字段类型 datatype type
		indexed : null,// 是否索引
		showed : null,// 是否界面表格显示
		sorted : null,// 是否排序
		sysType : null,// 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
		length : null,// 长度
		showType : '1',// 显示类型 1,2,3,4 datashow
		defaultValue : null,// 默认值
		valueCreateMethod : '1',// 默认值创建方式 1用户输入
		onlyreadUrl : null,// 只读状态下设置跳转的url
	};

	// 控件公共属性
	var commonProperty = {
		inputMode : null,// 输入样式 控件类型 inputDataType
		fieldCheckRules : null,
		fontSize : null,// 字段的大小
		fontColor : null,// 字段的颜色
		ctlWidth : null,// 宽度
		ctlHight : null,// 高度
		textAlign : null,// 对齐方式
	};

	/*
	 * UNIT CLASS DEFINITION ======================
	 */
	var Unit2 = function($placeHolder, options) {
		this.options = $.extend({}, $.fn["wunit2"].defaults, options);
		this.value = "";
		this.$editableElem = null;
		this.$labelElem = null;
		this.$placeHolder = $placeHolder;
	};

	Unit2.prototype = {
		constructor : Unit2
	};

	$.Unit2 = {
        setValue2LabelElem: function () {
            var self = this;
            if (self.$labelElem == null) {
                return;
            }
            var value = self.value;
            if(self.isValueMap()){
                value = self.getDisplayValue();
            }
            self.$labelElem.text(value).attr("title", value);
			if(this.isShowAsLabel()) {
        		this.renderFieldTag();
			}
		},
		bind: function(eventname, event, custom) {
			var self = this, result;
			result = $.wControlInterface.bind.apply(this, arguments);
			if(self.$editableElem){
				self.$editableElem.next(".org-select-container").on(eventname, event);
			}
            return result;
        },

        // unbind函数，桥接
        unbind: function(eventname) {
			var self = this, result;
			result = $.wControlInterface.unbind.apply(this, arguments);
			if(self.$editableElem){
				self.$editableElem.next(".org-select-container").off(eventname);
			}
            return result;
        },
        renderFieldTag: function () {
            var _displayValue = this.getDisplayValue();
            if(typeof _displayValue === 'string') {
                if(_displayValue.indexOf(',') > -1) {
                    _displayValue = _displayValue ? _displayValue.split(',') : [];
                } else {
                    _displayValue = _displayValue ? _displayValue.split(';') : [];
                }
            }

            this.$labelElem.siblings('.form-field-tag-wrap').remove();
            var $tagWrap = $('<div>',{
                'class': 'form-field-tag-wrap'
            });
            var showControlBtn = false;
            $.each(_displayValue,function (i,v) {
                $tagWrap.append('<div class="form-field-tag">' + v + '</div>');
            });
            this.$labelElem.hide().after($tagWrap);
            var tagWrapTop = $tagWrap.offset().top;
            $tagWrap.find('.form-field-tag').each(function () {
                var $this = $(this);
                var _top = $this.offset().top;
                if(_top - tagWrapTop > 60) {
                    showControlBtn = true;
                    $this.addClass('is-more-tag').hide();
                }
            });
            var $parent = $tagWrap.parent();
            if(showControlBtn) {
                $parent.addClass('form-field-tag-control').css({
                    'position': 'relative',
                    'padding-right': '34px'
                });
                $tagWrap.after('<div class="control-btn" data-status="open"><span>展开<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></span></div>');
            }
            $parent.on('click','.control-btn',function () {
                var $this = $(this);
                var _status = $this.data('status');
                if(_status === 'open') {
                    $this.data('status','close').html('<span>收起<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-shang"></i></span>');
                    $tagWrap.find('.is-more-tag').fadeIn('fast');
                } else {
                    $this.data('status','open').html('<span>展开<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></span>');
                    $tagWrap.find('.is-more-tag').fadeOut('fast');
                }
            })
        },
		/* 设置到可编辑元素中 */
		setValue2EditableElem : function() {
			var self = this;
			if (self.$editableElem == null) {
				return;
			}
			self.$editableElem.attr("hiddenvalue", self.getValue());
			self.$editableElem.val(self.getDisplayValue());
			self.$editableElem.trigger("change.orgSelect");
		},
        hideEditableElem: function () {
        	$.wControlInterface.hideEditableElem.apply(this, arguments);
            this.$editableElem && this.$editableElem.hide().orgSelect("hide");
        },
        showEditableElem: function () {
        	$.wControlInterface.showEditableElem.apply(this, arguments);
            this.$editableElem.hide().orgSelect("show");
        },
		createEditableElem : function() {
			if (this.$editableElem != null) {// 创建可编辑框
				return;
			}

			var options = this.options;
			var ctlName = this.getCtlName();
      var editableElem = document.createElement("input");
      options.selectTypeList = options.selectTypeList || [];
			editableElem.setAttribute("class", this.editableClass);
			editableElem.setAttribute("name", ctlName);
			editableElem.setAttribute("id", ctlName);
			editableElem.setAttribute("type", "text");

			$(editableElem).css(this.getTextInputCss());

			this.$placeHolder.after($(editableElem));
			this.$editableElem = this.$placeHolder.next("."
					+ this.editableClass);
			this.$editableElem.mouseover(function() {
				$(this).attr("title", $(this).val());
			}).blur(function(event) {
				 // 清空显示值时,同步设置真实值
				 var realVal, $this = $(this);
				 if(!(realVal = $this.val()) && $this.attr("hiddenvalue")) {
					 $this.attr("hiddenvalue", realVal);
					 _this.setValue2Object();
					 _this.afterUnitChoose();
				 }
			 });

			this.$editableElem.addClass("input-people");

			var _this = this;
			var typeList = options.typeList || "all";
			var viewStyle = options.columnProperty.viewStyle;
			var mappingValues = options.columnProperty.mappingValues;
			var computeInitData = $.isPlainObject(mappingValues) ? function(initData){
				$.each(mappingValues, function(mappingName, mappingObj){
					var controlName = null;
					if (_this.getPos() == dyControlPos.subForm) {
						controlName = _this.getCellId(_this.getDataUuid(), mappingObj.fieldName);
					}else {
						controlName = _this.getCellId(null, mappingObj.fieldName);
					}
					var control = $.ControlManager.getCtl(controlName);
					if(null == control || null == control.getValue){
						return null;
					}
					var values = control.getValue();
					if(typeof values === "string" && values.length > 0){
						values = values.split(";");
					}
					if($.isArray(values) && values.length === initData.length){
						$.each(initData, function(idx, node){
							node.extValues = node.extValues || {};
							node.extValues[mappingName] = values[idx];
						});
					};
				});
			} : null;
			var viewStyles = null
			if($.isArray(viewStyle) && viewStyle.length == typeList.length){
				viewStyles = {};
				$.each(typeList, function(idx, type){
					viewStyles[type] = viewStyle[idx];
				});
			}
			// 默认展现带图标
			this.$editableElem.orgSelect({
				trigger : "click",
				orgOptions: orgOptions,
				orgStyle : options.orgStyle || $.unit2.orgStyles.orgStyle3
			});
			function orgOptions(){
				if (!_this.isReadOnly()) {
					if (_this.isSelection()) {
						return;
					}
					var unitId = _this.getSystemUnitId();
					var excludeValues = [], otherParams;
					var filterCondition = _this.options.filterCondition;
					if( filterCondition ){
						var paramsSchema = "otherParams://";
						if(filterCondition.indexOf(paramsSchema) === 0){
							try{
								otherParams = eval("(" + filterCondition.substr(paramsSchema.length) + ")");// $.parseJSON(filterCondition.substr(paramsSchema.length));
								if(otherParams && otherParams.filterCondition){
									excludeValues = otherParams.filterCondition.split(/;|,|；|，/);
								}
							} catch(ex){

							}
						} else {
							//按中英文的逗号和分号分割
							excludeValues = filterCondition.split(/;|,|；|，/);
						}
					}
					var valueFormat = _this.getFieldParams().valueFormat;
					// 默认选中
					var defaultSelectIds = _this.getFieldParams().defaultSelectIds;
					var initValues = _this.$editableElem.attr("hiddenvalue");
					if( StringUtils.isBlank(initValues) ){
						initValues = defaultSelectIds;
					}
          if(!_this.options.selectTypeObj) {
              if(_this.options.selectTypeList && _this.options.selectTypeList.indexOf('E') > -1) {
                  _this.options.selectTypeList.push('C');
              }
          }
					return {
						valueField : "",
						labelField : ctlName,
						title: "组织选择",
						multiple : _this.options.mutiSelect,
						type : _this.options.typeList.join(";"),
						defaultType : _this.options.defaultType,
						selectTypes : _this.options.selectTypeList.join(";"),
						callback : function(values, labels, treeNodes ){
							var oids = _this.$editableElem.attr("hiddenvalue") || "";
							_this.$editableElem.attr("hiddenvalue", values.join(";"));
							// _this.setToRealDisplayColumn();
							if($.isPlainObject(mappingValues)){
								$.each(mappingValues, function(mappingName, mappingObj){
									var controlName = null;
									if (_this.getPos() == dyControlPos.subForm) {
										controlName = _this.getCellId(_this.getDataUuid(), mappingObj.fieldName);
									}else {
										controlName = _this.getCellId(null, mappingObj.fieldName);
									}
									var control = $.ControlManager.getCtl(controlName);
									if(null == control || null == control.setValue){
										return null;
									}
									var values = [], msg;
									var mappingChn={'Dept':'部门','Job':'职位','Unit':'单位','OrgElement':'组织'};
									var getMappingChnName=function(mn){
										for(var k in mappingChn){
											if(mn.indexOf(k)==0){
												return mappingChn[k];
											}
										}
									}
									for(var i=0;i<treeNodes.length;i++){
										var treeNode = treeNodes[i];
										var mappingValue = treeNode.extValues && treeNode.extValues[mappingName];
                    if ($.trim(mappingValue).length > 0) {
											values.push(mappingObj.path ? ((treeNode.extValues.VersionName && (mappingValue.split('/')[0] !== treeNode.extValues.VersionName) ? treeNode.extValues.VersionName + '/' : "")+mappingValue) : mappingValue.substr(mappingValue.lastIndexOf("/")+1));
										}else {
											if(oids.indexOf(treeNode.id) > -1){
												msg = treeNode.name+"无法获取"+getMappingChnName(mappingName)+"，因为已有数据无法获取组织选择项，请先删除，再重新选择该人员!"
											}else {
												msg = treeNode.name+"无法获取"+getMappingChnName(mappingName);
											}
											appModal.error(msg);
											_this.setValue('',true);
											throw new Error(msg);
										}
									}
									control.setValue(values.join(";"));
								});
							}
							setTimeout(function () {
                                _this.afterUnitChoose( treeNodes );
                            },0);
							_this.setValue2Object();
						},
						otherParams : otherParams,
						initValues : initValues,
						excludeValues : excludeValues,
						valueFormat : valueFormat ? valueFormat : "justId",
            viewStyles: viewStyles,
            nameDisplayMethod: options.nameDisplayMethod,
						computeInitData : computeInitData,
					}
				}
			}

			this.$editableElem.click(function() {
				if (!_this.isReadOnly()) {
					if (_this.isSelection()) {
						return;
					}
					//防止双击组织弹出框显示蓝色背景
					_this.$editableElem.attr('disabled','disabled');
					window.setTimeout(function(){
						// console.log("通过表单打开组织弹出框------开始");
						// _this.$editableElem.orgSelect("open", orgOptions());
						// $.unit2.open();
						// console.log("通过表单打开组织弹出框------结束");
						_this.$editableElem.removeAttr('disabled');
					}, 200);
				}
			});
		},
		getSelectType : function() {
			var selectType = 0;
			for ( var i in this.options.selectTypeList) {
				if(this.options.selectTypeList.hasOwnProperty(i)){
					selectType += parseInt(this.options.selectTypeList[i])
				}
			}
			return selectType;
		}
	};

	/*
	 * UNIT PLUGIN DEFINITION =========================
	 */
	$.fn.wunit2 = function(option) {
		var method = false;
		var args = null;
		if (arguments.length == 2) {
			method = true;
			args = arguments[1];
		}

		if (typeof option == 'string') {
			if (option === 'getObject') { // 通过getObject来获取实例
				var $this = $(this);
				data = $this.data('wunit2');
				if (data) {
					return data; // 返回实例对象
				} else {
					throw new Error('This object is not available');
				}
			}
		}

		var $this = $(this), data = $this.data('wunit2'), options = typeof option == 'object'
				&& option;
		if (!data) {
			data = new Unit2($(this), options);
			$.extend(data, $.wControlInterface);
			$.extend(data, $.wTextCommonMethod);
			$.extend(data, $.wUnitCommonMethod);
			$.extend(data, $.Unit2);
			$this.data('wunit2', data);
			if(options.columnProperty.uninit){
				return data;
			}
			data.init();
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

	$.fn.wunit2.Constructor = Unit2;

	$.fn.wunit2.defaults = {
		columnProperty : columnProperty,// 字段属性
		commonProperty : commonProperty,// 公共属性
		readOnly : false,
		disabled : false,
		isHide : false,// 是否隐藏
		mutiSelect : true,
		showUnitType : true,
		filterCondition : null

	};

})(jQuery);
