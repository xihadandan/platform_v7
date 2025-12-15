define([ "mui", "constant", "commons", "appModal",'appContext' ,"formBuilder"], function($, constant, commons, appModal,appContext, formBuilder) {
	var StringUtils = commons.StringUtils;
	var AddressBook = function(options) {
		this.options = $.extend({
			labelField : null,
			valueField : null,
			initNames : null,// 仅一个目标域值时，以;分割的文本值，当为多个目标域值时则为与psTargetNames一一对应的数组成员值。?
			initIDs : null,// 仅一个目标域值时，以;分割的文本值，当为多个目标域值时则为与psTargetNames一一对应的数组成员值。?
			title : "通讯录",
			multiple : null,// 是否多选，默认为true
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
			changeLayoutOverflow : true
		// 未知作用，未处理
		// 是否设置显示在父窗口
		// 不允许选择节点值，多值以;分割。
		}, options);
		this.id = commons.UUID.createUUID();
		this.data = {};
		this.value = [];
		this.visitChain = [];
		this.types = this.loadType();
		// this.data[this.options.type] = this.loadData(this.options.type);
		this.valueElement = document.getElementById(this.options.valueField);
		this.labelElement = document.getElementById(this.options.labelField);
		var idStringValue = "";
		var nameStringValue = "";
		if (this.options.labelField != null && this.options.initNames == null) {
			nameStringValue = this.labelElement.value;
		} else {
			nameStringValue = this.options.initNames;
		}
		if (this.options.valueField != null && this.options.initIDs == null) {
			idStringValue = this.valueElement.value;
		} else {
			idStringValue = this.options.initIDs;
		}
		if (StringUtils.isBlank(idStringValue) && this.labelElement && typeof (this.labelElement.getAttribute("hiddenValue")) != "undefined") {
			idStringValue = this.labelElement.getAttribute("hiddenValue");
		}
		if (StringUtils.isNotBlank(idStringValue)) {
			var idValue = idStringValue.split(this.options.separator);
			var nameValue = nameStringValue.split(this.options.separator);
			for ( var i in idValue) {
				var value = {};
				value.id = idValue[i];
				value.name = nameValue[i];
				this.value.push(value);
			}
		}
	};
	AddressBook.prototype.onPanelClose = function() {
		var name = [];
		var id = [];
		var email = [];
		var employeeNumber = [];
		var loginName = [];
		$.each(this.value, function(i, unit) {
			name.push(unit.name);
			id.push(unit.id);
			email.push(unit.email || "");
			employeeNumber.push(unit.employeeNumber || "");
			loginName.push(unit.loginName || "");
		});
		var returnValue = {
			id : id.join(this.options.separator),
			name : name.join(this.options.separator),
			email : email.join(this.options.separator),
			employeeNumber : employeeNumber.join(this.options.separator),
			loginName : loginName.join(this.options.separator),
		}
		if (this.labelElement) {
			this.labelElement.value = returnValue.name;
			this.labelElement.setAttribute("hiddenValue", returnValue.id);
		}
		if (this.valueElement) {
			this.valueElement.value = returnValue.id;
		}
		if (this.options.sexField != null) {
			$("#" + this.options.sexField)[0].value = returnValue.sex;
		}
		if (this.options.emailField != null) {
			$("#" + this.options.emailField)[0].value = returnValue.email;
		}
		if (this.options.employeeNumberField != null) {
			$("#" + this.options.employeeNumberField)[0].value = returnValue.employeeNumber;
		}
		if (this.options.loginNameField != null) {
			$("#" + this.options.loginNameField)[0].value = returnValue.loginName;
		}
		if (this.options.isSetChildWin) {
		}

		if (this.options.afterSelect) {
			this.options.afterSelect.call(this, returnValue);
		}
		if (this.options.ok) {
			this.options.ok.call(this, returnValue);
		}

	};
	AddressBook.prototype.getId = function() {
		return this.id;
	};
	AddressBook.prototype.open = function() {
		var _self = this;
		var unitPanelId = this.getId();
		var typeIndex = this._getTypeIndex(this.options.type);
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
			title : this.options.title,
			content : "",
			container : "#" + unitPanelId
		})
		$.ui.loadContent("#" + unitPanelId);
		var unitPanel = document.getElementById(unitPanelId);
		var contentContainer = wrapper.querySelector("#" + unitPanelId + " .mui-content");
		var html = new commons.StringBuilder();
		html.appendFormat('<div  class="mui-container">');
		html.appendFormat('<form class="mui-input-row mui-search" style="top:0px;" action="" onsubmit="return false;">');
		html.appendFormat('	<input type="search" class="mui-input-clear" placeholder="搜索">');
		html.appendFormat('</form>');
		html.appendFormat('	<ul class="mui-table-view">');
		$.each(this.types, function(index, type) {
			html.appendFormat("<li class='mui-table-view-cell'>");
			html.appendFormat('	<a class="mui-navigate-right"  id="{1}">{2}</a>', type.id, type.id, type.name);
			html.appendFormat("</li>");
		});
		html.appendFormat('	</ul >');
		html.appendFormat('	</div >');
		contentContainer.appendChild($.dom(html.toString())[0])
		
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
			//_self.appendChildrenItem($("#" + id + " .mui-scroll")[0], _self.data[type], null, true);
		}
		$searchInput[0].addEventListener("blur", searchFn);
		$searchInput[0].addEventListener("search", searchFn);
		
	/*	var deceleration = mui.os.ios ? 0.0003 : 0.0009;
		$("#" + unitPanelId + ' .mui-table-view').scroll({
			bounce : false,
			indicators : true, // 是否显示滚动条
			deceleration : deceleration
		});*/
		//进入到子页面
		
		$("#" + unitPanelId).on("tap",".mui-table-view-cell",function(){
			var title = $(this)[0].querySelector("a").innerHTML;
			var typeId = $(this)[0].querySelector("a").getAttribute("id");
			_self.openChildrenPanel(title,typeId)
		})
	};
	
	AddressBook.prototype.openChildrenPanel = function(title,type) {
		var panelId = this.getId() + "childrenPanel";
		var _self = this;
		var wrapper = $("div[id="+panelId+"]")[0];
		if(wrapper == null || typeof wrapper === "undefined"){
			wrapper = document.createElement("div");
			wrapper.id = panelId
			wrapper.classList.add("mui-container");
			var pageContainer = appContext.getPageContainer();
			if (pageContainer) {
				var renderPlaceholder = pageContainer.getRenderPlaceholder();
				renderPlaceholder[0].appendChild(wrapper);
			} else {
				document.body.appendChild(wrapper);
			}
		}
		
		formBuilder.buildPanel({
			title : title,
			content : '',
			container : "#" + panelId,
			contentClass : "mui-content"
		});
		$.ui.loadContent("#" + panelId);
		
		var unitPanel = document.getElementById(panelId);
		var contentContainer = wrapper.querySelector("#" + panelId + " .mui-content");
		var html = new commons.StringBuilder();
		html.appendFormat('<div class="mui-container">')
		html.appendFormat('<form class="mui-input-row mui-search" style="top:0px;" action="" onsubmit="return false;">');
		html.appendFormat('	<input type="search" class="mui-input-clear" placeholder="搜索">');
		html.appendFormat('</form>');
		html.appendFormat('<div class="list-container">');
		html.appendFormat('</div>');
		html.appendFormat('</div>');
		contentContainer.appendChild($.dom(html.toString())[0]);
		
		var paths = [type];

		_self.data[type] = _self.loadData(type);
		
		_self.renderNodeList(paths);
		
		// 点击进入子节点
		$("#" + panelId).on("tap", ".div_unit_node", function(event) {
			if (event.target.tagName.toLocaleLowerCase() == 'input') {
				return;
			}
			var dataPath = this.getAttribute("dataPath");
			var paths = dataPath.split(".");
			_self.renderNodeList(paths);
						
			// 人员搜索：短部门+姓名 显示时，提示完整路径
			var unitLabel = this.querySelector(".unit-label");
			if(unitLabel != null) {
				var unitName = unitLabel.getAttribute("unitName");
				if(StringUtils.isNotBlank(unitName)) {
					appModal.info(unitName);
				}
			}
		});
	};
	AddressBook.prototype._selectedUnit = function(data) {
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
	AddressBook.prototype.getSelectedDataIndex = function(id) {
		for ( var i in this.value) {
			if (this.value[i].id == id) {
				return i;
			}
		}
		return -1;
	};
	AddressBook.prototype._unSelectedUnit = function(id) {
		var dataIndex = this.getSelectedDataIndex(id);
		if (dataIndex != -1) {
			this.value.splice(dataIndex, 1);
		}
	};
	AddressBook.prototype._getTypeIndex = function(type) {
		for ( var i in this.types) {
			if (this.types[i].id == type) {
				return i;
			}
		}
		return 0;
	};
	AddressBook.prototype._getTypeLabel = function(type) {
		var index = this._getTypeIndex(type);
		var label = index != -1 ? this.types[index].name : "";
		return StringUtils.isBlank(label) ? this.options.title : label;
	};
	AddressBook.prototype.renderNodeList = function(paths) {
		var _self = this;
		var unitId = paths[paths.length - 1];
		var type = paths[0];
		var data = _self.getDataByPath(paths);
		if (paths.length == 1 || data.isLeaf == "0") {
			_self.visitChain = paths.concat();
			var panelId = this.getId() + "childrenPanel";
			if (paths.length == 1) {
				_self.appendChildrenItem($("#" + panelId + " .list-container")[0], _self.data[type]);
			} else {
				_self.appendChildrenItem($("#" + panelId + " .list-container")[0], _self.loadChildrenData(type, data));
			}
		}
	};
	AddressBook.prototype.getDataChainByPath = function(paths) {
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
	AddressBook.prototype.getDataByPath = function(paths) {
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
	AddressBook.prototype.hasCheckbox = function(lsType) {
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
	AddressBook.prototype.getItemHtml = function(unit, search) {
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
		var dataPath = this.visitChain.join(".") + "." + unit.id;
		var checkType,checkClass;
		if(this.options.multiple != null){
			checkClass = this.options.multiple ? "mui-checkbox" : "mui-radio";
			checkType = this.options.multiple ? "checkbox" : "radio";
		}else{
			checkClass = "";
			checkType = "";
		}		
		var isLefe = unit.isLeaf == "0" ? "mui-navigate-right" : "contact-item";
		html.appendFormat('<div class="div_unit_node mui-input-row {0} mui-left {1}" dataPath="{2}">', checkClass, isLefe, dataPath);		
		// 人员搜索：短部门+姓名
		if(unit.type == "2" && StringUtils.isNotBlank(unit.path) && unit.path.lastIndexOf("/") != -1) {
			var subfix = unit.path.substring(0, unit.path.lastIndexOf("/"));
			if(search && subfix.lastIndexOf("/") != -1) { // 搜索
				var postfix = unit.path.substring(unit.path.lastIndexOf("/"));
				postfix = subfix.substring(subfix.lastIndexOf("/") + 1) + postfix;
				html.appendFormat('	<label class="unit-label" unitName="{1}">{0}</label>', postfix, unit.path);
			} else {
				if(isLefe == "contact-item"){
					html.appendFormat("<span class='addressBook-tags'>{0}</span>",unit.name.substring(0,2))
				}
				html.appendFormat('	<label>{0}</label>', unit.name);
			}
		} else {
			if(isLefe == "contact-item"){
				html.appendFormat("<span class='addressBook-tags'>{0}</span>",unit.name.substring(0,2))
			}
			html.appendFormat('	<label>{0}</label>', unit.name);
		}
		if(checkType != ""){
			if (this.hasCheckbox(unit.type)) {
				var checked = this.hasChecked(unit.id) ? "checked" : "";
				html.appendFormat('	<input name="unit_check" type="{0}" unitId="{2}" {1}>', checkType, checked, unit.id);
			}
		}		
		html.appendFormat('</div>');
		return html.toString();
	};
	AddressBook.prototype.hasChecked = function(id) {
		return this.getSelectedDataIndex(id) != -1;
	};
	AddressBook.prototype.appendChildrenItem = function($placeHolder, data, paths, search) {
		var _self = this;
		var dataChain = _self.getDataChainByPath(_self.visitChain);
		var html = new commons.StringBuilder();
		html.appendFormat("<div class='bread-nav'>");
		var dataPath = _self.visitChain[0];
		$.each(dataChain, function(index, unit) {
			var clas = (index == dataChain.length - 1) ? "" : "div_unit_node";
			if (index > 0) {
				dataPath += "." + _self.visitChain[index];
				html.appendFormat('<span class="mui-icon mui-icon-arrowright"></span>');
			}
			var name = index == 0 ? _self._getTypeLabel(_self.visitChain[0]) : unit.name;
			html.appendFormat('<span class="{0}" style="display: inline;" dataPath="{1}" href="#">{2}</span>', clas, dataPath, name);

		});
		html.appendFormat("</div>");
		html.appendFormat('<ul class="mui-table-view">');
		$.each(data, function(index, unit) {
			html.appendFormat('<li class="mui-table-view-cell">');
			html.appendFormat(_self.getItemHtml(unit, search));
			html.appendFormat('</li>');
		});
		html.appendFormat('</ul>');
		$placeHolder.innerHTML = html.toString();
		
		$(".mui-table-view").on("tap",".contact-item",function(){
			var dataPath = $(this)[0].getAttribute("datapath");
			var contacterIdArr = dataPath.split(".");
			var contacterId = contacterIdArr[contacterIdArr.length-1];
			appContext.require(["mui-ShowUserInfo"],function(showUserInfo){
				showUserInfo({"userId":contacterId});
			});
		})
		
	};
	AddressBook.prototype.loadSearchData = function(type, searchText) {
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
	AddressBook.prototype.loadData = function(type) {
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
	AddressBook.prototype.loadChildrenData = function(type, parent) {
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
	AddressBook.prototype.loadType = function() {
		var result = [];
		var _self = this;
		if (this.options.showType === true) {
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
				id : this.options.type,
				name : ""
			});
		}
		return result;
	};
	var addressBook = {};
	addressBook.open = function(options) {
		var newAddressBook = new AddressBook(options);
		newAddressBook.open();
	};
	$.addressBook = addressBook;
	return  addressBook.open;
});