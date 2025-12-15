define([ "mui", "constant", "commons", "appModal", "formBuilder" ], function($, constant, commons, appModal, formBuilder) {
	var StringUtils = commons.StringUtils;
	var OrgUnit = function(options) {
		var self = this;
		self.options = $.extend({
			labelField : null,
			valueField : null,
			initNames : null,// 仅一个目标域值时，以;分割的文本值，当为多个目标域值时则为与psTargetNames一一对应的数组成员值。?
			initIDs : null,// 仅一个目标域值时，以;分割的文本值，当为多个目标域值时则为与psTargetNames一一对应的数组成员值。?
			title : "人员选择",
			multiple : true,// 是否多选，默认为true
			selectType : 1,// 限制用户只能选择的节点类型(默认"1")；0-都不能选择，1-都可以选择，2-仅允许选择部门，4-仅允许选择人员，8-表示仅允许选择公共群组，32-标识仅允许选择职位
			// 其他值为0/1/2/4/8/16/32相加组合。
			nameType : "21",// 返回的组织名称格式，两个字符(默认“21”)：分别表示部门节点/人员节点；每一个字符表示，“1”-名称，“2”-不带根全路径（集团版自动带根），“3”-带根全路径。
			showType : true,// 是否显示类型选择下拉框。默认显示。
			type : "MyUnit",// 备选值：集团|All;我的单位|MyUnit;我的部门|MyDept;我的领导|MyLeader;我的下属|MyUnderling;公共群组|PublicGroup;个人群组|PrivateGroup;上级部门|MyParentDept;在线人员|LoginUser;部门树|Dept也可以是节点值（可以多值，且要求支持路径值或者id），用来选择此节点树下的值。默认为MyUnit.，当值为Dept，不显示姓氏下拉框。
			loginStatus : false,// 是否显示在线/离线状态，默认不显示
			excludeValues : null,// 未知作用，未处理
			typeList : null,// 类型过滤
			sexField : null,
			emailField : null,
			employeeNumberField : null,
			loginNameField : null,
			filterCondition : "",
			isShowGroup : false,// 未知作用，未处理
			disableId : null,
			isSetChildWin : false,// 未知作用，未处理
			separator : ';',
			readonly : false,
			changeLayoutOverflow : true
		// 未知作用，未处理
		// 是否设置显示在父窗口
		// 不允许选择节点值，多值以;分割。
		}, options);
		self.id = commons.UUID.createUUID();
		self.data = {};
		self.value = [];
		self.visitChain = [];
		self.types = self.loadType();
		// self.data[self.options.type] = self.loadData(self.options.type);
		if(self.options.valueField && self.options.valueField.indexOf("#")===0){
			self.valueElement = $(self.options.valueField)[0];
		}else{
			self.valueElement = document.getElementById(self.options.valueField);
		}
		if(self.options.labelField && self.options.labelField.indexOf("#")===0){
			self.labelElement = $(self.options.labelField)[0];
		}else{
			self.labelElement = document.getElementById(self.options.labelField);
		}
		var idStringValue = "";
		var nameStringValue = "";
		if (self.options.labelField != null && self.options.initNames == null) {
			nameStringValue = self.labelElement.value;
		} else {
			nameStringValue = self.options.initNames;
		}
		if (self.options.valueField != null && self.options.initIDs == null) {
			idStringValue = self.valueElement.value;
		} else {
			idStringValue = self.options.initIDs;
		}
		if (StringUtils.isBlank(idStringValue) && self.labelElement && typeof (self.labelElement.getAttribute("hiddenValue")) != "undefined") {
			idStringValue = self.labelElement.getAttribute("hiddenValue");
		}
		if (StringUtils.isNotBlank(idStringValue)) {
			var idValue = idStringValue.split(self.options.separator);
			var nameValue = nameStringValue.split(self.options.separator);
			for ( var i in idValue) {
				var value = {};
				value.id = idValue[i];
				value.name = nameValue[i];
				self.value.push(value);
			}
		}
	};
	OrgUnit.prototype.onPanelClose = function() {
		var self = this;
		var name = [];
		var id = [];
		var email = [];
		var employeeNumber = [];
		var loginName = [];
		$.each(self.value, function(i, unit) {
			name.push(unit.name);
			id.push(unit.id);
			email.push(unit.email || "");
			employeeNumber.push(unit.employeeNumber || "");
			loginName.push(unit.loginName || "");
		});
		var returnValue = {
			id : id.join(self.options.separator),
			name : name.join(self.options.separator),
			email : email.join(self.options.separator),
			employeeNumber : employeeNumber.join(self.options.separator),
			loginName : loginName.join(self.options.separator),
		}
		if (self.labelElement) {
			self.labelElement.value = returnValue.name;
			self.labelElement.setAttribute("hiddenValue", returnValue.id);
		}
		if (self.valueElement) {
			self.valueElement.value = returnValue.id;
		}
		if (self.options.sexField != null) {
			$("#" + self.options.sexField)[0].value = returnValue.sex;
		}
		if (self.options.emailField != null) {
			$("#" + self.options.emailField)[0].value = returnValue.email;
		}
		if (self.options.employeeNumberField != null) {
			$("#" + self.options.employeeNumberField)[0].value = returnValue.employeeNumber;
		}
		if (self.options.loginNameField != null) {
			$("#" + self.options.loginNameField)[0].value = returnValue.loginName;
		}
		if (self.options.isSetChildWin) {
		}

		if (self.options.afterSelect) {
			self.options.afterSelect.call(self, returnValue);
		}
		if (self.options.ok) {
			self.options.ok.call(self, returnValue);
		}

	};
	OrgUnit.prototype.getId = function() {
		return this.id;
	};
	OrgUnit.prototype.open = function() {
		var _self = this;
		var options = _self.options;
		var unitPanelId = _self.getId();
		var typeIndex = _self._getTypeIndex(options.type);
		var wrapper = document.createElement("div");
		wrapper.id = unitPanelId
		var pageContainer = appContext.getPageContainer();
		if (pageContainer) {
			var renderPlaceholder = pageContainer.getRenderPlaceholder();
			renderPlaceholder[0].appendChild(wrapper);
		} else {
			document.body.appendChild(wrapper);
		}
		formBuilder.buildPanel({
			title : _self.options.title,
			content : "",
			container : "#" + unitPanelId
		})
		$.ui.loadContent("#" + unitPanelId);
		var nvaHtml = new commons.StringBuilder();
		nvaHtml.appendFormat('<nav class="mui-bar mui-bar-tab">');
		nvaHtml.appendFormat('	<a class="mui-tab-item mui-active {1}" id="{0}_ok" href="#">确认</a>', unitPanelId, options.btnActiveClass || "");
		// nvaHtml.appendFormat('	<a class="mui-tab-item mui-active" href="#"></a>');
		nvaHtml.appendFormat('	<a class="mui-tab-item mui-active" id="{0}_cancel" href="#">取消</a>', unitPanelId);
		nvaHtml.appendFormat('</nav>');
		var unitPanel = document.getElementById(unitPanelId);
		var contentContainer = wrapper.querySelector("#" + unitPanelId + " .mui-content");
		unitPanel.insertBefore($.dom(nvaHtml.toString())[0], contentContainer)

		var html = new commons.StringBuilder();
		html.appendFormat('<div  class="mui-slider mui-fullscreen">');
		if (_self.options.showType && _self.types.length > 1) {
			html.appendFormat('<div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">');
			html.appendFormat('	<div class="mui-scroll">');
			$.each(_self.types, function(index, type) {
				var activeClass = typeIndex == index ? "mui-active" : "";
				html.appendFormat('	<a class="mui-control-item {3}"  href="#div_{0}_content" id="{1}">{2}</a>', type.id, type.id, type.name, activeClass);
			});
			html.appendFormat('	</div >');
			html.appendFormat('	</div >');
		}
//		html.appendFormat('<div class="mui-table" style="table-layout: auto;margin-top: 0px;" >');
//		html.appendFormat('<div class="mui-table-cell"  style="width: auto;">');
		html.appendFormat('<form class="mui-input-row mui-search" style="top:0px;" action="" onsubmit="return false;">');
		html.appendFormat('	<input type="search" class="mui-input-clear" placeholder="搜索">');
		html.appendFormat('</form>');
//		html.appendFormat('</div>');
//		html.appendFormat('<div class="mui-table-cell div_search_btn" style="width: 75px;position: relative;vertical-align: top;left: 10px;display : none;">');
//		html.appendFormat('	<button type="button" class="mui-btn mui-btn-outlined">搜索</button>');
//		html.appendFormat('</div>');
//		html.appendFormat('</div>');

		html.appendFormat('	<div class="mui-slider-group" style="top: {0}px; ">', (_self.options.showType&& _self.types.length > 1 ? "89" : "-10"));
		$.each(_self.types, function(index, type) {
			html.appendFormat('	<div id="div_{0}_content" class="mui-slider-item mui-control-content">', type.id);
			html.appendFormat('	<div class="mui-scroll-wrapper">');
			html.appendFormat('	<div class="mui-scroll">');
			html.appendFormat('	</div >');
			html.appendFormat('	</div >');
			html.appendFormat('	</div >');
		});
		html.appendFormat('	</div >');
		html.appendFormat('	</div >');
		// var contentContainer = wrapper.querySelector("#" + unitPanelId + "
		// .mui-content");
		contentContainer.appendChild($.dom(html.toString())[0])
		var deceleration = mui.os.ios ? 0.0003 : 0.0009;
		$("#" + unitPanelId + ' .mui-scroll-wrapper').scroll({
			bounce : false,
			indicators : true, // 是否显示滚动条
			deceleration : deceleration
		});
		$("#" + unitPanelId + "_cancel")[0].addEventListener("tap", function() {
			$.ui.goBack();
		});
		if(options.readonly === true) { // 只读时,隐藏确认按钮
			$("#" + unitPanelId + "_ok")[0].classList.add("mui-hidden");
		} else {
			$("#" + unitPanelId + "_ok")[0].addEventListener("tap", function() {
				$.ui.goBack(); // 先关闭
				_self.onPanelClose();
			});
		}
		// 节点选择状态变更事件
		$("#" + unitPanelId).on("change", "input[name='unit_check']", function(event) {
			var dataPath = this.parentNode.getAttribute("dataPath");
			var paths = dataPath.split(".");
			var data = _self.getDataByPath(paths);
			if (this.checked) {
				_self._selectedUnit(data);
			} else {
				_self._unSelectedUnit(data.id);
			}
			_self.renderSelectedNum();
		});
		// 点击进入子节点
		$("#" + unitPanelId).on("tap", ".div_unit_node", function(event) {
			if (event.target.tagName.toLocaleLowerCase() == 'input') {
				return;
			}
			var dataPath = this.getAttribute("dataPath");
			var paths = dataPath.split(".");
			_self.renderNodeList(paths);
			
			var scrolls = $("#" + unitPanelId + ' .mui-scroll-wrapper').scroll();
			if(scrolls) {
				$.each(scrolls, function() {
					if(this.refresh) {
						this.refresh();
					}
				});
			}
			
			// 人员搜索：短部门+姓名 显示时，提示完整路径
			var unitLabel = this.querySelector(".unit-label");
			if(unitLabel != null) {
				var unitName = unitLabel.getAttribute("unitName");
				if(StringUtils.isNotBlank(unitName)) {
					appModal.info(unitName);
				}
			}
		});
		var $searchInput = $("#" + unitPanelId + ' .mui-search input');
		$searchInput.input();
		// 搜索按钮点击搜索
		var searchFn = function(event) {
			var searchText = this.value;
			var type = _self.visitChain[0];
			var id = "div_" + type + "_content";
			_self.data[type] = _self.loadSearchData(type, searchText);
			_self.visitChain = [];
			_self.visitChain.push(type);
			_self.appendChildrenItem($("#" + id + " .mui-scroll")[0], _self.data[type], null, true);
		}
		$searchInput[0].addEventListener("blur", searchFn);
		$searchInput[0].addEventListener("search", searchFn);
		if (_self.options.showType&& _self.types.length > 1) {
			// 左右滑动初始化
			var $slider = $("#" + unitPanelId + ' .mui-slider');
			$slider.slider({
				scrollTime : 0
			});
			// 左右滑动切换类型
			$slider[0].addEventListener("slide", function(event) {
				var type = $("#" + unitPanelId + ' .mui-control-item')[event.detail.slideNumber].id;
				_self.options.type = type;
				_self.data[type] = _self.loadData(type);
				$searchInput[0].value = "";
				_self.renderNodeList([ type ]);
			});
			if (typeIndex == 0) {
				_self.data[_self.types[typeIndex].id] = _self.loadData(_self.types[typeIndex].id);
				_self.renderNodeList([ _self.types[typeIndex].id ]);
			} else {
				$slider.slider().gotoItem(typeIndex);
			}
		} else {
			_self.data[_self.options.type] = _self.loadData(_self.options.type);
			_self.renderNodeList([ _self.options.type ]);
		}
		_self.renderSelectedNum();
		// 打开选中列表
		$("#" + unitPanelId + " .button-selected-msg")[0].addEventListener("tap", function(event) {
			_self.openSelectedPanel();
		});
	};
	OrgUnit.prototype._selectedUnit = function(data) {
		var _self = this;
		var index = _self.getSelectedDataIndex(data.id);
		if (index == -1) {
			// 单选模式，清空已选择
			if(_self.options.multiple === false) {
				_self.value = [];
			}
			_self.value.push(data);
		}
	};
	OrgUnit.prototype.getSelectedDataIndex = function(id) {
		var _self = this;
		for ( var i in _self.value) {
			if (_self.value[i].id == id) {
				return i;
			}
		}
		return -1;
	};
	OrgUnit.prototype._unSelectedUnit = function(id) {
		var _self = this;
		var dataIndex = _self.getSelectedDataIndex(id);
		if (dataIndex != -1) {
			_self.value.splice(dataIndex, 1);
		}
	};
	OrgUnit.prototype._getTypeIndex = function(type) {
		var _self = this;
		for ( var i in _self.types) {
			if (_self.types[i].id == type) {
				return i;
			}
		}
		return 0;
	};
	OrgUnit.prototype._getTypeLabel = function(type) {
		var _self = this;
		var index = _self._getTypeIndex(type);
		var label = index != -1 ? _self.types[index].name : "";
		return StringUtils.isBlank(label) ? _self.options.title : label;
	};
	OrgUnit.prototype.renderNodeList = function(paths) {
		var _self = this;
		var unitId = paths[paths.length - 1];
		var type = paths[0];
		var data = _self.getDataByPath(paths);
		if (paths.length == 1 || data.isLeaf == "0") {
			_self.visitChain = paths.concat();
			var divId = "div_" + type + "_content";
			if (paths.length == 1) {
				_self.appendChildrenItem($("#" + divId + " .mui-scroll")[0], _self.data[type]);
			} else {
				_self.appendChildrenItem($("#" + divId + " .mui-scroll")[0], _self.loadChildrenData(type, data));
			}
		}
	};
	OrgUnit.prototype.openSelectedPanel = function() {
		var _self = this;
		var panelId = _self.getId() + "SelectedPanel";
		var wrapper = $("div[id="+panelId+"]")[0];
		if(wrapper == null || typeof wrapper === "undefined"){
			wrapper = document.createElement("div");
			wrapper.id = panelId
			wrapper.classList.add("mui-fullscreen");
			var pageContainer = appContext.getPageContainer();
			if (pageContainer) {
				var renderPlaceholder = pageContainer.getRenderPlaceholder();
				renderPlaceholder[0].appendChild(wrapper);
			} else {
				document.body.appendChild(wrapper);
			}
		}
		var html = new commons.StringBuilder();
		if (_self.value.length > 0) {
			html.appendFormat('<ul class="mui-table-view">');
			for ( var i in _self.value) {
				html.appendFormat('<li class="mui-table-view-cell mui-input-row" unitId="{0}">', _self.value[i].id);
				html.appendFormat('	<label >{0}</label>', _self.value[i].name);
				html.appendFormat('	<span class="mui-icon mui-badge mui-icon-trash icon-delete-row"></span>');
				html.appendFormat('</li>');
			}
			html.appendFormat('</ul>');
		}
		formBuilder.buildPanel({
			title : _self.options.title,
			content : html.toString(),
			container : "#" + panelId,
			contentClass : "mui-content unit-selected-content"
		});
		$.ui.loadContent("#" + panelId);
		$("#" + panelId).on("tap", ".icon-delete-row", function() {
			var liElement = this.parentNode;
			var unitId = liElement.getAttribute("unitId");
			liElement.parentNode.removeChild(liElement);
			_self._unSelectedUnit(unitId);
		});
		$("#" + panelId)[0].addEventListener("panel.back", function() {
			_self.renderNodeList(_self.visitChain);
			_self.renderSelectedNum();
		});
	};
	OrgUnit.prototype.renderSelectedNum = function() {
		var _self = this;
		var $buttonChoise = $("#" + _self.getId() + ' .button-selected-msg');
		if (!$buttonChoise.length) {
			var header = $("#" + _self.getId() + ' header.mui-bar')[0];
			var html = '<button class="mui-btn-link mui-pull-right button-selected-msg"></button>';
			header.appendChild($.dom(html.toString())[0]);
			$buttonChoise = $("#" + _self.getId() + ' .button-selected-msg');
			// 右菜单
			var element = $buttonChoise[0];
			var cb = function(event) {
				$.trigger(element, "tap");
			}
			$.trigger(element, "optionmenu.change", {
				title : "",
				callback : cb,
				customOptionMenu : true
			});
		}
		$buttonChoise[0].innerHTML = "已选(" + _self.value.length + ")";
	};
	OrgUnit.prototype.getDataChainByPath = function(paths) {
		paths = paths.concat();
		var _self = this;
		var dataChain = [];
		var type = paths.shift();
		var data = _self.data[type];
		dataChain.push(data);
		$.each(paths, function(index, path) {
			var children = data;
			if (!Array.isArray(data)) {
				children = data.children;
			}
			for ( var i in children) {
				if (children[i].id == path) {
					data = children[i];
					dataChain.push(data);
					return;
				}
			}
		});
		return dataChain;
	};
	OrgUnit.prototype.getDataByPath = function(paths) {
		paths = paths.concat();
		var _self = this;
		var type = paths.shift();
		var data = _self.data[type];
		$.each(paths, function(index, path) {
			var children = data;
			if (!Array.isArray(data)) {
				children = data.children;
			}
			for ( var i in children) {
				if (children[i].id == path) {
					data = children[i];
					return;
				}
			}
		});
		return data;
	};
	OrgUnit.prototype.hasCheckbox = function(lsType) {
		var lbCheckbox = true;
		var selectType = this.options.selectType;
		if (selectType == 0 || lsType == "0" || lsType == "5") {
			lbCheckbox = false;
		} else if (selectType == 1) {
			lbCheckbox = true;
		} else if (lsType == "1" && (selectType & 2) != 2) {
			lbCheckbox = false;
		} else if (lsType == "2" && (selectType & 4) != 4) {
			lbCheckbox = false;
		} else if (lsType == "3" && (selectType & 8) != 8) {
			lbCheckbox = false;
		} else if (lsType == "4" && (selectType & 16) != 16) {
			lbCheckbox = false;
		} else if (lsType == "6" && (selectType & 32) != 32) {
			lbCheckbox = false;
		} else if (lsType == "7") {
			lbCheckbox = false;
		}
		return lbCheckbox;
	};
	OrgUnit.prototype.getItemHtml = function(unit, search) {
		var self = this;
		var options = self.options;
		// 处理名称
		var lsName = unit.path;
		if(options.type === "JobDuty") {
		}else {
			if(options.nameType.charAt(1)=="1"){
				lsName = lsName.substring(lsName.lastIndexOf("/")+1,lsName.length);
			}else if(options.nameType.charAt(1)=="2"){
				if(lsName.indexOf("/")!=-1) lsName = lsName.substring(lsName.indexOf("/")+1,lsName.length);
			}
		}
		unit.name = lsName;
		
		var html = new commons.StringBuilder();
		var dataPath = self.visitChain.join(".") + "." + unit.id;
		var checkClass = self.options.multiple ? "mui-checkbox" : "mui-radio";
		var checkType = self.options.multiple ? "checkbox" : "radio";
		var isLefe = unit.isLeaf == "0" ? "mui-navigate-right" : "";
		html.appendFormat('<div class="div_unit_node mui-input-row {0} mui-left {1}" dataPath="{2}">', checkClass, isLefe, dataPath);
		// 人员搜索：短部门+姓名
		if(unit.type == "2" && StringUtils.isNotBlank(unit.path) && unit.path.lastIndexOf("/") != -1) {
			var subfix = unit.path.substring(0, unit.path.lastIndexOf("/"));
			if(search && subfix.lastIndexOf("/") != -1) { // 搜索
				var postfix = unit.path.substring(unit.path.lastIndexOf("/"));
				postfix = subfix.substring(subfix.lastIndexOf("/") + 1) + postfix;
				html.appendFormat('	<label class="unit-label" unitName="{1}">{0}</label>', postfix, unit.path);
			} else {
				html.appendFormat('	<label>{0}</label>', unit.name);
			}
		} else {
			html.appendFormat('	<label>{0}</label>', unit.name);
		}
		if (self.hasCheckbox(unit.type)) {
			var checked = self.hasChecked(unit.id) ? "checked" : "";
			html.appendFormat('	<input name="unit_check" type="{0}" unitId="{2}" {1}>', checkType, checked, unit.id);
		}
		html.appendFormat('</div>');
		return html.toString();
	};
	OrgUnit.prototype.hasChecked = function(id) {
		return this.getSelectedDataIndex(id) != -1;
	};
	OrgUnit.prototype.appendChildrenItem = function($placeHolder, data, paths, search) {
		var _self = this;
		var dataChain = _self.getDataChainByPath(_self.visitChain);
		var html = new commons.StringBuilder();
		html.appendFormat('<ul class="mui-table-view mui-unit-content">');
		html.appendFormat('<li class="mui-table-view-cell">');
		var dataPath = _self.visitChain[0];
		$.each(dataChain, function(index, unit) {
			var clas = (index == dataChain.length - 1) ? "" : "div_unit_node";
			if (index > 0) {
				dataPath += "." + _self.visitChain[index];
				html.appendFormat('<span class="mui-icon mui-icon-arrowright"></span>');
			}
			var name = index == 0 ? _self._getTypeLabel(_self.visitChain[0]) : unit.name;
			html.appendFormat('<span class="{0}" style="font-size: large;display: inline;" dataPath="{1}" href="#">{2}</span>', clas, dataPath, name);

		});
		html.appendFormat('</li>');
		$.each(data, function(index, unit) {
			html.appendFormat('<li class="mui-table-view-cell" style="padding-top: 0px;padding-bottom: 0px;">');
			html.appendFormat(_self.getItemHtml(unit, search));
			html.appendFormat('</li>');
		});
		html.appendFormat('</ul>');
		$placeHolder.innerHTML = html.toString();
	};
	OrgUnit.prototype.loadSearchData = function(type, searchText) {
		var result = [];
		var options = this.options;
		var login = options.loginStatus ? "1" : "0";
		var url = ctx + "/org/unit/tree/json/search";
		$.ajax({
			url : url,
			type : "GET",
			async : false,
			data : {
				all : "1",
				login : login,
				optionType : type,
				searchValue : searchText,
				filterCondition : options.filterCondition
			},
			dataType : 'json',
			error : function(data) {
			},
			success : function(data) {
				result = data;
			}
		});
		return result;

	};
	OrgUnit.prototype.loadData = function(type) {
		var result = [];
		var options = this.options;
		var login = options.loginStatus ? "1" : "0";
		var url = ctx + "/org/unit/tree/json/unit";
		$.ajax({
			url : url,
			type : "GET",
			async : false,
			data : {
				all : "1",
				login : login,
				optionType : type,
				filterCondition : options.filterCondition,
				displayList : options.disableId
			},
			dataType : 'json',
			error : function(data) {
			},
			success : function(data) {
				result = data;
			}
		});
		return result;
	};
	OrgUnit.prototype.loadChildrenData = function(type, parent) {
		var options = this.options;
		if (Object.keys(parent.children).length) {
			return parent.children;
		}
		var login = options.loginStatus ? "1" : "0";
		var url = ctx + "/org/unit/tree/json/toggle/unit";
		$.ajax({
			url : url,
			type : "GET",
			async : false,
			data : {
				all : "0",
				login : login,
				optionType : type,
				id : parent.id,
				filterCondition : options.filterCondition,
				displayList : options.disableId
			},
			dataType : 'json',
			error : function(data) {
			},
			success : function(data) {
				parent.children = data[0].children;
			}
		});
		return parent.children;
	};
	OrgUnit.prototype.loadType = function() {
		var result = [];
		var _self = this;
		if (_self.options.showType === true) {
			$.ajax({
				type : "POST",
				url : window.ctx + "/org/unit/tree/json/type",
				dataType : "json",
				async : false,
				success : function(data, statusText, jqXHR) {
					if (_self.options.typeList && _self.options.typeList.length) {
						$.each(data, function(i, type) {
							if (_self.options.typeList.indexOf(type.id) != -1) {
								result.push(type);
							}
						});
					} else {
						result = data;
					}

				},
				error : function(jqXHR, statusText, error) {

				}
			});
		} else {
			result.push({
				id : _self.options.type,
				name : ""
			});
		}
		return result;
	};
	var unit = {};
	unit.open = function(options) {
		var orgUnit = new OrgUnit(options);
		orgUnit.open();
	};
	$.unit = unit;
	return unit;
});