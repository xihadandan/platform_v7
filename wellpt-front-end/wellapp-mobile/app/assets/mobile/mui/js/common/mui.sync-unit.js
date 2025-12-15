requirejs.config({
  paths: {
    indexedlist: staticPrefix + '/mobile/mui/js/mui.indexedlist'
  }
});
define(['mui', 'constant', 'commons', 'server', 'appModal', 'formBuilder', 'indexedlist'], function (
  $,
  constant,
  commons,
  server,
  appModal,
  formBuilder
) {
  var UNIT_TREE_TYPE = ['MyUnit', 'MyDept', 'MyLeader', 'MyUnderling', 'MyCompany', 'DutyGroup', 'PublicGroup', 'MyGroup'];
  var UNIT_TREE_TYPE_NAME = ['我的单位', '我的部门', '我的领导', '我的下属', '我的集团', '职务群组', '公共群组', '个人群组'];
  var UNIT_TREE_ALL_SELECT_TYPE = ['O', 'B', 'D', 'J', 'U', 'G', 'DU', 'E', 'R'];
  var JDS = server.JDS;
  var StringUtils = commons.StringUtils;
  var OrgUnit = function (options) {
    var self = this;
    if (false === self.checkParams(options)) {
      return false;
    }
    self.options = $.extend(
      {
        title: '选择对象', // 组织弹出框的标题
        labelField: null, //必填参数，可以为空字符串
        valueField: null, //必填参数，可以为空字符串
        multiple: true, // 是否多选，默认为true
        // 下拉框的选择范围，all:全部，MyUnit|我的单位,具体值参考 multiUnitTree.js文件的, 或者参考组织选项里面的配置
        // UNIT_TREE_TYPE 变量的定义
        // 如果显示多个用分号分隔符
        type: 'all', //参考组织选项里面的配置
        defaultType: 'MyUnit', //默认激活的组织类型
        separator: ';', //多选时，数据的分割符
        selectTypes: 'all', //可以选择的类型，all:全部，M:组织，O：单位，D:部门，J：职位，U:账号，G：群组，如果有多个则以分号多分割
        callback: null, //点击确认后的回调函数， 传参为 values, labels， 均为数组格式
        excludeValues: [], //数组格式，需要排除的选项,
        unitId: null, //用户的归属单位ID, 非必填，如有指定，则显示该单位的数据，该参数只对我的单位，我的集团,职务群组有效
        orgVersionId: null, //可选，如果设置，则只展示该版本的数据，如果没有设置，则显示最新版本的数据
        isInMyUnit: true, //是否只展示本单位内的数据，不跨单位
        initValues: null, //初始化真实值，数组格式,
        initLabels: null, //初始化显示值，数组格式,与initValues配套使用
        otherParams: {}, //额外参数，主要供给二开使用
        close: null, //弹出框的关闭事件
        valueFormat: 'all', //all 代表完整格式( V0000000001/U000000001 ), justId : 代表仅组织ID( U0000000001 )
        moreOptList: [], //提供额外的自定义选项列表 [{"id":"XXXX","name":"模块通讯录"}]
        // defaultType: "", //默认选中的类型
        eleIdPath: null,
        separator: ';',
        showRole: false, // 是否显示角色， type为all的时候做判断，false不显示，true显示，其他时候type有传role则显示
        //
        readonly: false, // 手机特有
        changeLayoutOverflow: true // 手机特有
      },
      options,
      {
        nameType: '1', // 名称显示全路径(手机特有)
        viewJob: 'hide', // 在树的显示模式下，部门下直接显示人员
        searchUserStyle: 'followDeptAndJob', // 人员的搜索结果按部门-职位展示
        showType: true // 手机特有
      }
    );
    self.id = commons.UUID.createUUID();
    self.data = {};
    self.value = [];
    self.visitChain = [];
    var showRoot = self.options.showRoot; // 是否展示根节点
    if (showRoot == null || typeof showRoot === 'undefined') {
      options.otherParams = options.otherParams || {};
      self.options.showRoot = !!(options.eleIdPath || options.otherParams.eleIdPath);
    }
    self.options.orgTypes = self.options.orgTypes || {};
    //将selectTypes转成数组格式
    if ('all' == self.options.selectTypes.toLowerCase()) {
      self.options.selectTypes = UNIT_TREE_ALL_SELECT_TYPE;
    } else {
      self.options.selectTypes = self.options.selectTypes.split(';');
    }
    //将type转成数组格式
    self.orgOptionList = self.loadOrgOptionList() || [];
    if ('all' == self.options.type.toLowerCase()) {
      // 显示全部
      if (self.orgOptionList) {
        self.options.types = [];
        $.each(self.orgOptionList, function (index, opt) {
          if (opt.id === 'Role' && self.options.showRole) {
            self.options.types.push(opt.id);
          } else if (opt.isShow) {
            self.options.types.push(opt.id);
          }
        });
      } else {
        self.options.types = UNIT_TREE_TYPE;
      }
    } else {
      self.options.types = self.options.type.split(';');
    }
    $.each(self.options.moreOptList, function (index, item) {
      self.options.types.push(item.id);
    });
    // self.data[self.options.type] = self.loadData(self.options.type);
    if (self.options.valueField && self.options.valueField.indexOf('#') === 0) {
      self.valueElement = $(self.options.valueField)[0];
    } else if (StringUtils.isNotBlank(self.options.valueField)) {
      self.valueElement = document.getElementById(self.options.valueField);
    }
    if (self.options.labelField && self.options.labelField.indexOf('#') === 0) {
      self.labelElement = $(self.options.labelField)[0];
    } else if (StringUtils.isNotBlank(self.options.labelField)) {
      self.labelElement = document.getElementById(self.options.labelField);
    }
    var idStringValue = '';
    var nameStringValue = '';
    if (self.options.initNames != null && self.options.initLabels == null) {
      console.warn('multiOrg options.initNames is @Deprecated');
      self.options.initLabels = self.options.initNames;
    }
    if (StringUtils.isNotBlank(self.options.labelField) && self.options.initLabels == null) {
      nameStringValue = self.labelElement.value;
    } else {
      nameStringValue = self.options.initLabels;
    }
    if (self.options.initIDs != null && self.options.initValues == null) {
      console.warn('multiOrg options.initIDs is @Deprecated');
      self.options.initValues = self.options.initIDs;
    }
    if (StringUtils.isNotBlank(self.options.valueField) && self.options.initValues == null) {
      idStringValue = self.valueElement.value;
    } else {
      idStringValue = self.options.initValues;
    }
    if (StringUtils.isBlank(idStringValue) && self.labelElement && typeof self.labelElement.getAttribute('hiddenValue') != 'undefined') {
      idStringValue = self.labelElement.getAttribute('hiddenValue');
    }
    if (StringUtils.isNotBlank(idStringValue)) {
      self.value = self.computeInitData(idStringValue, nameStringValue);
      self.options.computeInitData && self.options.computeInitData(self.value);
    }
  };

  OrgUnit.prototype.computeInitData = function (initValues, initLabels) {
    var self = this;
    var initDatas = [];
    if (initValues && initLabels) {
      var values = initValues.split(';');
      var labels = initLabels.split(';');
      for (var i = 0; i < values.length; i++) {
        //检查是否标准格式
        if (values[i].substr(0, 1) == 'V') {
          var idAndVersion = values[i].split('/');
          var versionId = idAndVersion[0];
          var id = idAndVersion[1];
          var name = labels[i];
          var node = {
            id: id,
            type: id.substr(0, 1),
            name: name,
            orgVersionId: versionId
          };
          initDatas.push(node);
        } else {
          //非标准格式，则采用原值，那只有ID,NAME, 主要是用于邮件功能
          var node = {
            id: values[i],
            name: labels[i]
          };
          // 如果是个合法的组织ID,则计算type
          if (self.isValidOrgId(values[i])) {
            node.type = values[i].substr(0, 1);
          }
          initDatas.push(node);
        }
      }
    }
    return initDatas;
  };
  OrgUnit.prototype.isValidOrgId = function (orgId) {
    if (StringUtils.isNotBlank(orgId)) {
      var type = orgId.substr(0, 1);
      if ('U' == type) {
        //用户
        return true;
      } else if ('O' == type) {
        //组织节点
        return true;
      } else if ('B' == type) {
        //业务单位
        return true;
      } else if ('D' == type) {
        //部门
        return true;
      } else if ('J' == type) {
        //职位
        return true;
      } else if ('DU' == type) {
        //职务
        return true;
      } else if ('G' == type) {
        //群组
        return true;
      }
    }
    return false;
  };
  OrgUnit.prototype.checkParams = function (params) {
    //检查基本参数
    if (StringUtils.isBlank(params.type)) {
      console.error('参数type为空,默认为MyUnit');
    }
    if (StringUtils.isBlank(params.selectTypes) && StringUtils.isNotBlank(params.selectType)) {
      // 制用户只能选择的节点类型(默认"1")；0-都不能选择，1-都可以选择，2-仅允许选择部门，4-仅允许选择人员，8-表示仅允许选择公共群组，32-标识仅允许选择职位 其他值为0/1/2/4/8/16/32相加组合。
      var selectTypes = [];
      if (params.selectType == '1' || params.selectType == 1) {
        selectTypes = UNIT_TREE_ALL_SELECT_TYPE;
      } else {
        if ((params.selectType & 2) == 2) {
          selectTypes.push('D');
        }
        if ((params.selectType & 4) == 4) {
          selectTypes.push('U');
        }
        if ((params.selectType & 8) == 8) {
          selectTypes.push('G');
        }
        if ((params.selectType & 16) == 16) {
          selectTypes.push('J');
        }
        if ((params.selectType & 32) == 32) {
          selectTypes.push('O');
        }
      }
      params.selectTypes = selectTypes.join(';');
      console.error('参数selectType已经过期,请使用selectTypes');
    }
    if ($.isArray(params.types)) {
      //按照类型检查对应的参数
      for (var i = 0; i < params.types.length; i++) {
        if ('MyUnit' == params.types[i]) {
          //我的单位
        }
        if ('MyDept' == params.types[i]) {
          //我的部门
        }
      }
    }
    return true;
  };
  OrgUnit.prototype.onClose = function () {
    var self = this;
    var options = self.options;
    if ($.isFunction(options.close)) {
      options.close.call(self);
    }
  };
  OrgUnit.prototype.onOk = function () {
    var self = this;
    var name = [];
    var id = [];
    var email = [];
    var employeeNumber = [];
    var loginName = [];
    var options = self.options;
    $.each(self.value, function (i, unit) {
      if (StringUtils.isBlank(unit.orgVersionId) || 'justId' === options.valueFormat) {
        id.push(unit.id);
      } else {
        id.push(unit.orgVersionId + '/' + unit.id);
      }
      name.push(unit.name);
      email.push(unit.email || '');
      employeeNumber.push(unit.employeeNumber || '');
      loginName.push(unit.loginName || '');
    });
    var returnValue = {
      id: id.join(self.options.separator),
      name: name.join(self.options.separator),
      email: email.join(self.options.separator),
      employeeNumber: employeeNumber.join(self.options.separator),
      loginName: loginName.join(self.options.separator)
    };
    if (self.labelElement) {
      self.labelElement.value = returnValue.name;
      self.labelElement.setAttribute('hiddenValue', returnValue.id);
    }
    if (self.valueElement) {
      self.valueElement.value = returnValue.id;
    }
    if (options.sexField != null) {
      $('#' + options.sexField)[0].value = returnValue.sex;
    }
    if (options.emailField != null) {
      $('#' + options.emailField)[0].value = returnValue.email;
    }
    if (options.employeeNumberField != null) {
      $('#' + options.employeeNumberField)[0].value = returnValue.employeeNumber;
    }
    if (options.loginNameField != null) {
      $('#' + options.loginNameField)[0].value = returnValue.loginName;
    }
    if (options.isSetChildWin) {
    }
    if ($.isFunction(options.callback)) {
      options.callback.call(self, id, name, self.value);
    }
    if ($.isFunction(options.afterSelect)) {
      options.afterSelect.call(self, returnValue, self.value);
    }
    if ($.isFunction(options.ok)) {
      options.ok.call(self, returnValue, self.value);
    }
    self.onClose();
  };
  OrgUnit.prototype.getId = function () {
    return this.id;
  };
  OrgUnit.prototype.open = function () {
    var _self = this;
    var options = _self.options;
    var unitPanelId = _self.getId();
    var typeIndex = _self._getTypeIndex(options.defaultType);
    var wrapper = document.createElement('div');
    wrapper.id = unitPanelId;
    var pageContainer = appContext.getPageContainer();
    if (pageContainer && pageContainer.getRenderPlaceholder) {
      var renderPlaceholder = pageContainer.getRenderPlaceholder();
      renderPlaceholder[0].appendChild(wrapper);
    } else {
      document.body.appendChild(wrapper);
    }
    formBuilder.buildPanel({
      title: _self.options.title,
      content: '',
      container: '#' + unitPanelId
    });
    $.ui.loadContent('#' + unitPanelId);
    var nvaHtml = new commons.StringBuilder();
    nvaHtml.appendFormat('<nav class="mui-bar mui-bar-tab">');
    nvaHtml.appendFormat('	<a class="mui-tab-item mui-active {1}" id="{0}_ok" href="#">确认</a>', unitPanelId, options.btnActiveClass || '');
    nvaHtml.appendFormat('	<a class="mui-tab-item mui-active" id="{0}_cancel" href="#">取消</a>', unitPanelId);
    nvaHtml.appendFormat('</nav>');
    var unitPanel = document.getElementById(unitPanelId);
    var contentContainer = wrapper.querySelector('#' + unitPanelId + ' .mui-content');
    unitPanel.insertBefore($.dom(nvaHtml.toString())[0], contentContainer);

    var html = new commons.StringBuilder();
    html.appendFormat('<div  class="mui-slider mui-fullscreen">');
    if (_self.options.showType && _self.orgOptionList.length > 1) {
      html.appendFormat('<div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">');
      html.appendFormat('	<div class="mui-scroll">');
      $.each(_self.orgOptionList, function (index, type) {
        if ($.inArray(type.id, options.types) < 0) {
          return;
        }
        var activeClass = typeIndex == index ? 'mui-active' : '';
        html.appendFormat(
          '	<a class="mui-control-item {3}"  href="#div_{0}_content" id="{1}" data-viewstype="{4}">{2}</a>',
          type.id,
          type.id,
          type.name,
          activeClass,
          type.attach
        );
      });
      html.appendFormat('	</div >');
      html.appendFormat('	</div >');
    }
    html.appendFormat('<form class="mui-input-row mui-search mui-unit-search-form" style="top:0px;" action="" onsubmit="return false;">');
    html.appendFormat('	<input type="search" class="mui-input-clear" placeholder="搜索">');
    html.appendFormat('	<span class="btn-group">');
    html.appendFormat('	<button id="btn-hide-job" type="button" class="mui-btn active">隐藏职位</button>');
    html.appendFormat('	<button id="btn-show-job" type="button" class="mui-btn">显示职位</button>');
    html.appendFormat('	<button id="btn-show-userlist" type="button" class="mui-btn">人员列表</button>');
    html.appendFormat('	</span>');
    html.appendFormat('</form>');

    html.appendFormat(
      '	<div id="div-units-wrapper" class="mui-slider-group" style="top: {0}px; ">',
      _self.options.showType && _self.orgOptionList.length > 1 ? '89' : '-10'
    );
    $.each(_self.orgOptionList, function (index, type) {
      html.appendFormat('	<div id="div_{0}_content" class="mui-slider-item mui-control-content">', type.id);
      html.appendFormat('	<div class="mui-scroll-wrapper">');
      html.appendFormat('	<div class="mui-scroll">');
      html.appendFormat('	</div >');
      html.appendFormat('	</div >');
      html.appendFormat('	</div >');
    });
    html.appendFormat('	</div >');
    html.appendFormat('	</div >');
    contentContainer.appendChild($.dom(html.toString())[0]);
    var deceleration = mui.os.ios ? 0.0003 : 0.0009;
    $('#' + unitPanelId + ' .mui-scroll-wrapper').scroll({
      bounce: false,
      indicators: true, // 是否显示滚动条
      deceleration: deceleration
    });

    $('#' + unitPanelId + '_cancel')[0].addEventListener('tap', function () {
      $.ui.goBack();
      _self.onClose();
    });
    if (options.readonly === true) {
      // 只读时,隐藏确认按钮
      $('#' + unitPanelId + '_ok')[0].classList.add('mui-hidden');
    } else {
      $('#' + unitPanelId + '_ok')[0].addEventListener('tap', function () {
        $.ui.goBack(); // 先关闭
        _self.onOk();
      });
    }
    var $searchForm = $('#' + unitPanelId + ' .mui-unit-search-form');
    $searchForm.on('tap', '.btn-group>button', function (event) {
      var target = event.target;
      if (!target || target.classList.contains('active')) {
        return;
      }
      var id = target.id;
      var $button = $('.btn-group>button.active', $searchForm[0]);
      $button[0].classList.remove('active');
      target.classList.add('active');
      if (id === 'btn-hide-job') {
        options.viewJob = 'hide';
        options.viewStyle = 'tree';
        _self.renderNodeList(_self.visitChain);
      } else if (id === 'btn-show-job') {
        options.viewJob = 'show';
        options.viewStyle = 'tree';
        _self.renderNodeList(_self.visitChain);
      } else if (id === 'btn-show-userlist') {
        options.viewStyle = 'list';
        _self.renderNodeList([_self.options.type]); // ["MyUnit"]  ["MyCompany"]
      }
    });
    var $searchInput = $('#' + unitPanelId + ' .mui-search input');
    var searchInputApi = $searchInput.input();
    // 搜索按钮点击搜索
    var searchFn = function (event) {
      var searchText = $searchInput[0].value;
      var type = _self.visitChain[0];
      var id = 'div_' + type + '_content';
      if (StringUtils.isBlank(searchText)) {
        _self.renderNodeList(_self.visitChain);
        _self.renderSelectedNum();
      } else if (searchText != _self.searchText) {
        var searchData = _self.loadSearchData(type, searchText);
        _self.appendChildrenItem($('#' + id + ' .mui-scroll')[0], searchData, null, true);
        _self.scrollTopLeft(type);
      }
      _self.searchText = searchText;
    };
    $searchInput[0].addEventListener('blur', searchFn); // 失焦触发搜索(条件为空时，有展示数据)
    function showSearchGroupButton(event) {
      var searchText = $searchInput[0].value;
      if ($.trim(searchText).length <= 0) {
        var $buttonGroup = $('.btn-group', $searchForm[0]);
        $buttonGroup[0].style.display = '';
        var groupWidth = $buttonGroup[0].clientWidth + 28;
        $searchInput[0].setAttribute('style', 'width:calc(100% - ' + groupWidth + 'px);');
        var $activeButton = $('.btn-group>button.active', $searchForm[0]);
        $activeButton.length > 0 && $activeButton[0].focus();
      }
    }
    $searchInput[0].addEventListener('blur', showSearchGroupButton);
    function hideSearchGroupButton(event) {
      var $buttonGroup = $('.btn-group', $searchForm[0]);
      $buttonGroup[0].style.display = 'none';
      $searchInput[0].setAttribute('style', 'width:100%;');
    }
    $searchInput[0].addEventListener('focus', hideSearchGroupButton);
    $searchInput[0].addEventListener('search', searchFn);
    // 组织版本切换
    $('#' + unitPanelId).on('tap', '#org-type', function (event) {
      var type = _self.options.type;
      var unitPanelId2 = unitPanelId + '-org-type';
      // formBuilder.showPanel
      function ok(valueObj, event, panel) {
        options.orgTypes[type] = valueObj.ids[0];
        _self.renderNodeList([type]); // 重新渲染
        var searchText = $searchInput[0].value;
        if (StringUtils.isNotBlank(searchText)) {
          _self.searchText = null;
          $.trigger($searchInput[0], 'search');
        }
      }
      var data = _self.data[_self.options.type];
      $.each(data, function (idx, item) {
        item.text = item.text || item.name;
      });
      formBuilder.selectEditor({
        name: unitPanelId2,
        val: options.orgTypes[type] || '',
        title: '组织版本切换',
        items: data,
        require: true,
        container: '#' + unitPanelId2,
        callback: ok
      });
    });
    // 节点选择状态变更事件
    $('#' + unitPanelId).on('change', "input[name='unit_check']", function (event) {
      var checkBoxElement = this;
      var dataPath = checkBoxElement.parentNode.getAttribute('dataPath');
      var paths = dataPath.split('.'),
        nodePaths = [];
      var data = _self.getDataByPath(paths, nodePaths);
      if (checkBoxElement.checked) {
        _self.setNodeExtValues(data, nodePaths);
        _self._selectedUnit(data);
      } else {
        _self._unSelectedUnit(data.id);
      }
      _self.renderSelectedNum();
    });
    // 点击进入子节点
    $('#' + unitPanelId).on('tap', '.div_unit_node', function (event) {
      var unitNodeElement = this;
      var tarTagName = event.target.tagName;
      if (tarTagName.toLowerCase() === 'input') {
        return;
      }
      var dataPath = unitNodeElement.getAttribute('dataPath');
      var paths = dataPath.split('.');
      _self.renderNodeList(paths);
      // 点击根节点清空搜索
      if (paths.length === 1 && StringUtils.isNotBlank(_self.searchText)) {
        _self.searchText = '';
        searchInputApi.clearActionClick(event);
      }

      var scrolls = $('#' + unitPanelId + ' .mui-scroll-wrapper').scroll();
      // 重新计算滚动区域（可视区域）
      scrolls = $.isArray(scrolls) ? scrolls : [scrolls];
      $.each(scrolls, function (idx, scroll) {
        scroll.refresh && scroll.refresh();
      });

      // 人员搜索：短部门+姓名 显示时，提示完整路径
      var unitLabel = unitNodeElement.querySelector('.unit-label[fullPath]');
      if (unitLabel != null) {
        var unitName = unitLabel.getAttribute('fullPath');
        if (StringUtils.isNotBlank(unitName)) {
          appModal.info(unitName);
        }
      }
    });

    if (_self.options.showType && _self.orgOptionList.length > 1) {
      // 左右滑动初始化
      var $slider = $('#' + unitPanelId + ' .mui-slider');
      $slider.slider({
        scrollTime: 0
      });
      function renderSearchGroupButton(typeElement) {
        var btnHideJob = $('#' + unitPanelId + ' #btn-hide-job')[0];
        var btnShowJob = $('#' + unitPanelId + ' #btn-show-job')[0];
        var btnShowUserList = $('#' + unitPanelId + ' #btn-show-userlist')[0];
        if (null != btnShowUserList) {
          var viewStype = typeElement.getAttribute('data-viewstype');
          // 支持按列表展示,默认只支持树
          if (viewStype && viewStype.indexOf('list') > -1 && $.inArray('U', options.selectTypes) > -1) {
            btnShowUserList.classList.remove('mui-hidden');
          } else {
            options.viewStyle = null;
            btnShowUserList.classList.add('mui-hidden');
            if (btnHideJob && btnShowUserList.classList.contains('active')) {
              btnShowUserList.classList.remove('active');
              btnHideJob.classList.add('active');
            }
          }
        }
        if (null != btnHideJob && null != btnShowJob) {
          if ($.inArray(typeElement.id, ['MyUnit', 'MyDept', 'MyLeader', 'MyUnderling', 'MyCompany']) > -1) {
            btnHideJob.classList.remove('mui-hidden');
            btnShowJob.classList.remove('mui-hidden');
          } else {
            btnHideJob.classList.add('mui-hidden');
            btnShowJob.classList.add('mui-hidden');
          }
        }
        showSearchGroupButton(event);
      }
      // 左右滑动切换类型
      $slider[0].addEventListener('slide', function (event) {
        var typeElement = $('#' + unitPanelId + ' .mui-control-item')[event.detail.slideNumber];
        renderSearchGroupButton(typeElement);
        var type = typeElement.id;
        // 第一次加载(设置默认样式)
        if (null == options.viewStyle && options.viewStyles && options.viewStyles[type] === 'list') {
          options.viewStyle = 'list';
          options.viewStyles[type] = null;
          var $button = $('.btn-group>button.active', $searchForm[0]);
          $button[0].classList.remove('active');
          $('.btn-group>button[id="btn-show-userlist"]', $searchForm[0])[0].classList.add('active');
        }
        _self.options.type = type;
        _self.data[type] = _self.loadData(type);
        $searchInput[0].value = _self.searchText = '';
        _self.renderNodeList([type]);
      });
      if (typeIndex == 0) {
        // _self.options.type = _self.orgOptionList[typeIndex].id;// 同步type
        // _self.data[_self.orgOptionList[typeIndex].id] = _self.loadData(_self.orgOptionList[typeIndex].id);
        // _self.renderNodeList([ _self.orgOptionList[typeIndex].id ]);
        $.trigger($slider[0], 'slide', { slideNumber: 0 });
      } else {
        $slider.slider().gotoItem(typeIndex);
      }
    } else {
      _self.data[_self.options.type] = _self.loadData(_self.options.type);
      _self.renderNodeList([_self.options.type]);
    }
    _self.renderSelectedNum();
    // 打开选中列表
    $('#' + unitPanelId + ' .button-selected-msg')[0].addEventListener('tap', function (event) {
      _self.openSelectedPanel();
    });
  };
  OrgUnit.prototype._selectedUnit = function (data) {
    var _self = this;
    var index = _self.getSelectedDataIndex(data.id);
    if (index === -1) {
      // 单选模式，清空已选择
      if (_self.options.multiple === false) {
        _self.value = [];
      }
      _self.value.push(data);
    }
  };
  OrgUnit.prototype.getSelectedDataIndex = function (id) {
    var _self = this;
    for (var i = 0; i < _self.value.length; i++) {
      if (_self.value[i].id === id) {
        return i;
      }
    }
    return -1;
  };
  OrgUnit.prototype._unSelectedUnit = function (id) {
    var _self = this;
    var dataIndex = _self.getSelectedDataIndex(id);
    if (dataIndex != -1) {
      _self.value.splice(dataIndex, 1);
    }
  };
  OrgUnit.prototype._getTypeIndex = function (type) {
    var _self = this;
    for (var i = 0; i < _self.orgOptionList.length; i++) {
      if (_self.orgOptionList[i].id === type) {
        return i;
      }
    }
    return 0;
  };
  OrgUnit.prototype._getTypeLabel = function (type) {
    var _self = this;
    var index = _self._getTypeIndex(type);
    var label = index != -1 ? _self.orgOptionList[index].name : '';
    return StringUtils.isBlank(label) ? _self.options.title : label;
  };
  OrgUnit.prototype.renderNodeList = function (paths) {
    var _self = this;
    var type = paths[0];
    var options = _self.options;
    if (paths.length === 1 && _self.isSelectOrgType(type)) {
      // 组织版本不作为跟节点
      paths.push(options.orgTypes[type]);
    }
    var unitId = paths[paths.length - 1];
    var data = _self.getDataByPath(paths);
    if (paths.length == 1 || data.isParent === true || ($.isArray(data.children) && data.children.length > 0)) {
      _self.visitChain = paths.concat(); // 复制paths
      var divId = 'div_' + type + '_content';
      if (paths.length == 1) {
        _self.appendChildrenItem($('#' + divId + ' .mui-scroll')[0], _self.data[type]);
      } else {
        _self.appendChildrenItem($('#' + divId + ' .mui-scroll')[0], _self.loadChildrenData(type, data));
      }
    }
    _self.scrollTopLeft(type);
  };
  OrgUnit.prototype.openSelectedPanel = function () {
    var _self = this;
    var panelId = _self.getId() + 'SelectedPanel';
    var wrapper = $('div[id=' + panelId + ']')[0];
    if (wrapper == null || typeof wrapper === 'undefined') {
      wrapper = document.createElement('div');
      wrapper.id = panelId;
      wrapper.classList.add('mui-fullscreen');
      var pageContainer = appContext.getPageContainer();
      if (pageContainer && pageContainer.getRenderPlaceholder) {
        var renderPlaceholder = pageContainer.getRenderPlaceholder();
        renderPlaceholder[0].appendChild(wrapper);
      } else {
        document.body.appendChild(wrapper);
      }
    }
    var html = new commons.StringBuilder();
    if (_self.value.length > 0) {
      html.appendFormat('<ul class="mui-table-view">');
      for (var i in _self.value) {
        html.appendFormat('<li class="mui-table-view-cell mui-input-row" unitId="{0}">', _self.value[i].id);
        html.appendFormat('	<label >{0}</label>', _self.value[i].name);
        html.appendFormat('	<span class="mui-icon mui-badge mui-icon mui-icon-closeempty icon-delete-row"></span>');
        html.appendFormat('</li>');
      }
      html.appendFormat('</ul>');
    }
    formBuilder.buildPanel({
      title: _self.options.title,
      content: html.toString(),
      container: '#' + panelId,
      contentClass: 'mui-content unit-selected-content'
    });
    $.ui.loadContent('#' + panelId);
    $('#' + panelId).on('tap', '.icon-delete-row', function () {
      var liElement = this.parentNode;
      var unitId = liElement.getAttribute('unitId');
      liElement.parentNode.removeChild(liElement);
      _self._unSelectedUnit(unitId);
    });
    $('#' + panelId)[0].addEventListener('panel.back', function () {
      _self.renderNodeList(_self.visitChain);
      _self.renderSelectedNum();
    });
  };
  OrgUnit.prototype.renderSelectedNum = function () {
    var _self = this;
    var $buttonChoise = $('#' + _self.getId() + ' .button-selected-msg');
    if ($buttonChoise.length <= 0) {
      var header = $('#' + _self.getId() + ' header.mui-bar')[0];
      var html = '<button class="mui-btn-link mui-pull-right button-selected-msg"></button>';
      header.appendChild($.dom(html.toString())[0]);
      $buttonChoise = $('#' + _self.getId() + ' .button-selected-msg');
      // 右菜单
      var element = $buttonChoise[0];
      var cb = function (event) {
        $.trigger(element, 'tap');
      };
      $.trigger(element, 'optionmenu.change', {
        title: '',
        callback: cb,
        customOptionMenu: true
      });
    }
    $buttonChoise[0].innerHTML = '已选(' + _self.value.length + ')';
  };
  OrgUnit.prototype.getDataChainByPath = function (paths, type) {
    var _self = this;
    var data,
      dataChain = [];
    var clonePaths = paths.concat();
    if (StringUtils.isBlank(type)) {
      // 默认第一节点
      type = clonePaths.shift();
      data = _self.data[type];
      dataChain.push(data);
    } else {
      data = _self.data[type];
    }
    $.each(clonePaths, function (index, path) {
      var children = data;
      if (false === $.isArray(data)) {
        children = data.children;
      }
      for (var i = 0; i < children.length; i++) {
        if (children[i].id === path) {
          data = children[i];
          dataChain.push(data);
          return;
        }
      }
    });
    return dataChain;
  };
  OrgUnit.prototype.getDataByPath = function (paths, nodePaths) {
    var _self = this;
    var clonePaths = paths.concat();
    var type = clonePaths.shift();
    var data = _self.data[type];
    $.each(clonePaths, function (index, path) {
      var children = data;
      if (false === $.isArray(data)) {
        children = data.children;
      }
      for (var i = 0; i < children.length; i++) {
        if (children[i].id === path) {
          data = children[i];
          nodePaths && nodePaths.push(data);
          return;
        }
      }
    });
    return data;
  };
  // 设置人员扩展信息
  OrgUnit.prototype.setNodeExtValues = function (data, nodePaths) {
    var self = this;
    var options = self.options;
    var extValues = data.extValues || (data.extValues = {});
    if (data.type === 'U' && $.isArray(nodePaths)) {
      // 过滤最近的type节点
      function getNodeType(nodePaths, type) {
        var nodePaths2 = nodePaths.concat();
        for (var i = nodePaths.length - 1; i >= 0; i--) {
          if (nodePaths[i].type === type) {
            break; // return nodePaths2;
          } else {
            // 删除后面的
            nodePaths2.splice(i, 1);
          }
        }
        return nodePaths2;
      }
      // 获取属性路径
      function getPropPath(nodePaths, prop) {
        var props = [];
        for (var i = 0; i < nodePaths.length; i++) {
          var value = nodePaths[i][prop];
          $.trim(value).length > 0 && props.push(value);
        }
        return props;
      }
      extValues.JobId = extValues.JobId || getPropPath(getNodeType(nodePaths, 'J'), 'id').join('/');
      extValues.JobName = extValues.JobName || getPropPath(getNodeType(nodePaths, 'J'), 'name').join('/');
      extValues.DeptId = extValues.DeptId || getPropPath(getNodeType(nodePaths, 'D'), 'id').join('/');
      extValues.DeptName = extValues.DeptName || getPropPath(getNodeType(nodePaths, 'D'), 'name').join('/');
      extValues.UnitId = extValues.UnitId || getPropPath(getNodeType(nodePaths, 'B'), 'id').join('/');
      extValues.UnitName = extValues.UnitName || getPropPath(getNodeType(nodePaths, 'B'), 'name').join('/');
      extValues.VersionId = extValues.VersionId || getPropPath(getNodeType(nodePaths, 'O'), 'id').join('/');
      extValues.VersionName = extValues.VersionName || getPropPath(getNodeType(nodePaths, 'O'), 'name').join('/');
      extValues.fromTypeId = options.type;
      extValues.fromTypeText = self._getTypeLabel(options.type);
    }
  };
  OrgUnit.prototype.hasCheckbox = function (lsType) {
    var self = this;
    return $.inArray(lsType, self.options.selectTypes) > -1;
  };
  OrgUnit.prototype.getItemHtml = function (unit, search) {
    var self = this;
    var options = self.options;
    // 处理名称
    if (StringUtils.isBlank(unit.id)) {
      unit.id = 'undefined-unit-id-' + ++$.uuid;
      console.error('unit.id is blank for:' + unit.name);
    }
    var lsName = unit.name;
    var nameType = options.nameType;
    if (options.type === 'JobDuty') {
    } else if (nameType && nameType.charAt(1) == '1') {
      lsName = lsName.substring(lsName.lastIndexOf('/') + 1, lsName.length);
    } else if (nameType && nameType.charAt(1) == '2') {
      if (lsName.indexOf('/') != -1) lsName = lsName.substring(lsName.indexOf('/') + 1, lsName.length);
    }
    unit.name = lsName;

    var html = new commons.StringBuilder();
    var checkClass = self.options.multiple ? 'mui-checkbox' : 'mui-radio';
    var checkType = self.options.multiple ? 'checkbox' : 'radio';
    var isLefe = unit.isParent === true || unit.children.length > 0 ? 'mui-navigate-right' : '';
    // 人员搜索：短部门+姓名
    if (search && unit.searchPath) {
      var unitIdPath = unit.searchPath;
      var dataChain = self.getDataChainByPath(unitIdPath);
      var postfix = '',
        parentName,
        parentDeptName,
        parentJobName;
      $.each(dataChain, function (idx, data) {
        if (data.id === unit.id) {
          // break;
          return false;
        }
        var unitName = data.name;
        if (StringUtils.isBlank(unitName)) {
          // continue;
          return;
        }
        if (data.type === 'D') {
          parentDeptName = unitName;
        } else if (data.type === 'J') {
          parentJobName = unitName;
        }
        parentName = unitName;
        postfix += '/' + unitName;
      });
      var dataPath = unitIdPath.join('.');
      // "/"开头表示根节点
      var fullPath = postfix + '/' + unit.name;
      if (unit.type === 'U' && options.searchUserStyle === 'followDeptAndJob') {
        var shortName = unit.name;
        if ($.trim(parentDeptName).length > 0) {
          shortName += '<span class="user-follow unit-user-dept">' + parentDeptName + '</span>';
        }
        if ($.trim(parentJobName).length > 0) {
          shortName += '<span class="user-follow unit-user-job">' + parentJobName + '</span>';
        }
        html.appendFormat('<div class="div_unit_node mui-input-row {0} mui-left {1}" dataPath="{2}">', checkClass, isLefe, dataPath);
        html.appendFormat(
          '<label for="{2}" class="unit-label unit-type-{3}" fullPath="{1}">{0}</label>',
          shortName,
          fullPath,
          unit.id,
          unit.type
        );
      } else {
        var shortName = (parentDeptName || parentName || '') + '/' + unit.name;
        html.appendFormat('<div class="div_unit_node mui-input-row {0} mui-left {1}" dataPath="{2}">', checkClass, isLefe, dataPath);
        html.appendFormat(
          '<label for="{2}" class="unit-label unit-type-{3}" fullPath="{1}">{0}</label>',
          shortName,
          fullPath,
          unit.id,
          unit.type
        );
      }
    } else {
      if (unit.type == 'U' && options.viewJob === 'hide') {
        if (unit.path) {
          var newPath = unit.path.split('/');
          var dataPath = self.visitChain.join('.') + '.' + newPath.slice(-2).join('.');
        } else {
          var dataPath = self.visitChain.join('.') + '.' + unit.id;
        }
        html.appendFormat('<div class="div_unit_node mui-input-row {0} mui-left {1}" dataPath="{2}">', checkClass, isLefe, dataPath);
        html.appendFormat(
          '<label for="{1}" class="unit-label unit-type-{2}">{0}<span class="unit-user-job">{3}</span></label>',
          unit.name,
          unit.id,
          unit.type,
          unit.jobName || ''
        );
      } else {
        var dataPath = self.visitChain.join('.') + '.' + unit.id;
        html.appendFormat('<div class="div_unit_node mui-input-row {0} mui-left {1}" dataPath="{2}">', checkClass, isLefe, dataPath);
        html.appendFormat('<label for="{1}" class="unit-label unit-type-{2}">{0}</label>', unit.name, unit.id, unit.type);
      }
    }
    if (self.hasCheckbox(unit.type)) {
      var checked = self.hasChecked(unit.id) ? 'checked' : '';
      html.appendFormat('<input name="unit_check" id="{2}" type="{0}" unitId="{2}" {1}>', checkType, checked, unit.id);
    }
    html.appendFormat('</div>');
    return html.toString();
  };
  OrgUnit.prototype.hasChecked = function (id) {
    var self = this;
    return self.getSelectedDataIndex(id) != -1;
  };
  OrgUnit.prototype.appendChildrenItem = function ($placeHolder, data, paths, search) {
    var _self = this;
    var options = _self.options;
    var scrollWrapper = $($placeHolder).closest('.mui-scroll-wrapper');
    var scrollApi = $(scrollWrapper).scroll();
    if (options.viewStyle === 'list') {
      scrollApi.setStopped(true);
      _self.renderListItem($placeHolder, data, paths, search);
    } else {
      scrollApi.setStopped(false);
      _self.renderTreeItem($placeHolder, data, paths, search);
    }
  };
  OrgUnit.prototype.renderTreeItem = function ($placeHolder, data, paths, search) {
    var _self = this;
    var options = _self.options;
    var excludeValues = options.excludeValues || [];
    var dataChain = _self.getDataChainByPath(_self.visitChain);
    var html = new commons.StringBuilder();
    html.appendFormat('<ul class="mui-table-view mui-unit-content">');
    html.appendFormat('<li class="mui-table-view-cell" style="margin-bottom: 8px;">');
    html.appendFormat('<div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">');
    html.appendFormat('	<div class="mui-scroll">');
    var dataPath = _self.visitChain[0];
    $.each(dataChain, function (index, unit) {
      var clas = index == dataChain.length - 1 ? '' : 'div_unit_node',
        clas2;
      var name = index == 0 ? _self._getTypeLabel(_self.visitChain[0]) : unit.name;
      if (_self.isSelectOrgType(options.type)) {
        if (index === 0) {
          html.appendFormat('<span class="{0}" style="display: none;" dataPath="{1}" href="#">{2}</span>', clas, dataPath, name);
        } else if (index === 1) {
          // 组织版本切换
          var verList = _self.data[_self.options.type];
          if (!verList || verList.length <= 1) {
            clas2 += ' mui-hidden';
          }
          dataPath += '.' + _self.visitChain[index];
          html.appendFormat('<span class="mui-icon mui-icon-arrowright mui-hidden"></span>');
          html.appendFormat(
            '<span class="{0}" style="display: inline;" dataPath="{1}" href="#">{2}<span id="org-type" class="{3} icon iconfont icon-oa-qiehuan" style="color: blue;"></span></span>',
            clas,
            dataPath,
            name,
            clas2
          );
        } else {
          dataPath += '.' + _self.visitChain[index];
          html.appendFormat('<span class="mui-icon mui-icon-arrowright"></span>');
          html.appendFormat('<span class="{0}" style="display: inline;" dataPath="{1}" href="#">{2}</span>', clas, dataPath, name);
        }
      } else {
        if (index > 0) {
          dataPath += '.' + _self.visitChain[index];
          html.appendFormat('<span class="mui-icon mui-icon-arrowright"></span>');
        }
        html.appendFormat('<span class="{0}" style="display: inline;" dataPath="{1}" href="#">{2}</span>', clas, dataPath, name);
      }
    });
    html.appendFormat('</div>');
    html.appendFormat('</div>');
    html.appendFormat('</li>');
    $.each(data, function (index, unit) {
      if ($.inArray(unit.id, excludeValues) >= 0) {
        return;
      }
      html.appendFormat('<li class="mui-table-view-cell" style="padding-top: 0px;padding-bottom: 0px;">');
      html.appendFormat(_self.getItemHtml(unit, search));
      html.appendFormat('</li>');
    });
    html.appendFormat('</ul>');
    $placeHolder.innerHTML = html.toString();
  };
  OrgUnit.prototype.renderListItem = function ($placeHolder, data, paths, search) {
    var _self = this;
    var options = _self.options;
    var dataChain = _self.getDataChainByPath(_self.visitChain);
    var html = new commons.StringBuilder();
    html.appendFormat('<div class="mui-indexed-list mui-table-view mui-unit-content">');
    html.appendFormat(
      '<div class="mui-indexed-list-search mui-input-row mui-search mui-hidden"><input type="search" class="mui-input-clear mui-indexed-list-search-input" placeholder="搜索机场"></div>'
    );
    html.appendFormat('<div class="mui-table-view-cell" style="margin-bottom: 8px;">');
    var dataPath = _self.visitChain[0];
    var name = _self._getTypeLabel(_self.visitChain[0]);
    if (_self.isSelectOrgType(options.type) && dataChain.length > 1) {
      html.appendFormat('<span class="{0}" style="display: none;" dataPath="{1}" href="#">{2}</span>', '', dataPath, name);
      // 组织版本切换
      var verList = _self.data[_self.options.type],
        verClazz = '';
      if (!verList || verList.length <= 1) {
        verClazz += 'mui-hidden';
      }
      dataPath += '.' + _self.visitChain[1];
      html.appendFormat('<span class="mui-icon mui-icon-arrowright mui-hidden"></span>');
      html.appendFormat(
        '<span class="{0}-ver" style="display: inline;" dataPath="{1}" href="#">{2}<span id="org-type" class="{0} icon iconfont icon-oa-qiehuan" style="color: blue;"></span></span>',
        verClazz,
        dataPath,
        dataChain[1].name
      );
    } else {
      html.appendFormat('<span class="{0}" style="display: inline;" dataPath="{1}" href="#">{2}</span>', verClazz, dataPath, name);
    }
    html.appendFormat('</div>');
    html.appendFormat(_self.getListItemHtml(data, paths, search));
    html.appendFormat('</div>');
    $placeHolder.innerHTML = html.toString();
    // 避免mui-scroll-wrapper被挡住时获取clientHeight为0
    setTimeout(function () {
      // //create IndexedList
      var list = $('div.mui-indexed-list', $placeHolder)[0];
      var scrollWrapper = $($placeHolder).closest('.mui-scroll-wrapper');
      var indexHeight = scrollWrapper.clientHeight;
      list.style.height = (indexHeight < 90 ? 90 : indexHeight) + 'px';
      _self.indexedList = new mui.IndexedList(list, {
        barJustify: true
      });
    }, 1000 / 24);
  };
  OrgUnit.prototype.getListItemHtml = function getListItemHtml(data, paths, search) {
    var _self = this;
    var options = _self.options;
    var excludeValues = options.excludeValues || [];
    var html = new commons.StringBuilder();
    var listBarHtml = new commons.StringBuilder();
    var listInnerHtml = new commons.StringBuilder();
    function filterUserList(userList, data, dataPath, parentDeptName, parentJobName) {
      $.each(data, function (index, unit) {
        if ($.inArray(unit.id, excludeValues) >= 0) {
          return;
        } else if (unit.type === 'U' && unit.data) {
          userList.push(unit);
          // 补齐信息
          var nodeData = unit.data;
          nodeData.dataPath = nodeData.dataPath || dataPath + '.' + unit.id;
          nodeData.parentJobName = nodeData.parentJobName || parentJobName;
          nodeData.parentDeptName = nodeData.parentDeptName || parentDeptName;
          nodeData.userNamePy = (nodeData.userNamePy || nodeData.shortName).toLowerCase() || '';
        } else if ($.isArray(unit.children)) {
          if (unit.type === 'D') {
            parentDeptName = unit.name;
          } else if (unit.type === 'J') {
            parentJobName = unit.name;
          }
          filterUserList(userList, unit.children, dataPath + '.' + unit.id, parentDeptName, parentJobName);
        }
      });
    }
    var userList = [];
    filterUserList(userList, data, _self.visitChain.join('.'));
    userList.sort(function (a, b) {
      var userNamePy1 = a.data.userNamePy;
      var userNamePy2 = b.data.userNamePy;
      // 按ABC排序
      return userNamePy1 > userNamePy2 ? 1 : -1;
    });
    var other = [],
      groups = [];
    var aCode = 'A'.charCodeAt(0);
    var zCode = 'Z'.charCodeAt(0);
    for (var i = aCode; i <= zCode; i++) {
      // 初始化分组表
      groups.push([]);
    }
    groups.push(other);
    for (var i = 0; i < userList.length; i++) {
      var node = userList[i];
      var shortName = node.data.userNamePy; //使用人员拼音排序
      var tag = shortName.charAt(0).toUpperCase();
      var group = groups[tag.charCodeAt(0) - aCode];
      // 如果分组存在，则写入分组，否则写入其他
      if (group == null || typeof group === 'undefined') {
        node.tag = '#';
        other.push(node);
      } else {
        node.tag = tag;
        group.push(node);
      }
    }
    listBarHtml.append('<div class="mui-indexed-list-bar"><div class="bar-inner">');
    // listBarHtml.append("<a class=\"icon iconfont icon-ptkj-sousuochaxun\"></a>");// 搜索
    listInnerHtml.append(
      '<div class="mui-indexed-list-inner"><div class="mui-indexed-list-empty-alert">没有数据</div><ul class="mui-table-view" style="margin-bottom: 65px;">'
    );
    for (var i = 0; i < groups.length; i++) {
      var group = groups[i];
      if (group.length <= 0) {
        continue;
      }
      var tag = group[0].tag;
      listBarHtml.appendFormat('<a class="tag-{0}">{0}</a>', tag);
      listInnerHtml.appendFormat('<li data-group="{0}" class="mui-table-view-divider mui-indexed-list-group">{0}</li>', tag);
      for (var j = 0; j < group.length; j++) {
        var node = group[j];
        var id = node.id;
        var name = node.name;
        var nodeDate = node.data;
        var dataPath = nodeDate.dataPath;
        var userNamePy = nodeDate.userNamePy;
        var parentJobName = nodeDate.parentJobName;
        var parentDeptName = nodeDate.parentDeptName;
        var clazz = options.multiple ? 'mui-checkbox' : 'mui-radio';
        listInnerHtml.appendFormat(
          '<li data-value="{0}" data-name="{1}" data-tags="{2}" class="mui-table-view-cell mui-indexed-list-item" style="padding-top: 0px;padding-bottom: 0px;">',
          id,
          name,
          userNamePy
        );
        // 人员详情
        listInnerHtml.appendFormat('<div class="div_unit_node mui-input-row mui-left {0}" datapath="{1}">', clazz, dataPath);
        listInnerHtml.appendFormat('<label for="{0}" class="unit-label unit-type-{1}">{2}', id, node.type, name);
        if ($.trim(parentDeptName).length > 0) {
          listInnerHtml.appendFormat('<span class="user-follow unit-user-dept">' + parentDeptName + '</span>');
        }
        if ($.trim(parentJobName).length > 0) {
          listInnerHtml.appendFormat('<span class="user-follow unit-user-job">' + parentJobName + '</span>');
        }
        listInnerHtml.appendFormat('</label>');
        if (options.multiple) {
          listInnerHtml.appendFormat('<input type="checkbox" id="{0}" name="unit_check"/>', id, options.type);
        } else {
          listInnerHtml.appendFormat('<input type="radio" id="{0}" name="unit_check"/>', id, options.type);
        }
        listInnerHtml.appendFormat('</div>');
        listInnerHtml.appendFormat('</li>');
      }
    }
    listBarHtml.append('</div></div>');
    listInnerHtml.append('</ul></div>');
    html.append(listBarHtml.toString());
    html.append('<div class="mui-indexed-list-alert"></div>');
    html.append(listInnerHtml.toString());
    return html.toString();
  };
  // 是否选择组织版本(组织版本不作为跟节点)
  OrgUnit.prototype.isSelectOrgType = function (type) {
    var self = this;
    var options = self.options;
    return !options.showRoot && $.inArray(type, ['MyUnit', 'MyCompany']) > -1;
  };
  OrgUnit.prototype.loadSearchData = function (type, searchText) {
    var _self = this;
    var options = _self.options;
    // 搜索根路径
    var searchPath = _self.visitChain.concat();
    searchText = searchText.toLowerCase();
    var data = _self.getDataByPath(searchPath);
    var filter = function (basePath, data, result) {
      result = result || [];
      $.each(data, function (idx, unit) {
        var unitId = unit.id;
        var unitName = unit.name;
        var unitData = unit.data;
        var shortName = unitData.shortName;
        var userNamePy = unitData.userNamePy;
        /*if(options.viewStyle === "list" && false === (unit.type === "U")){
					// 按列表展示时，忽略非人员节点// 在renderListItem中统一忽略
				}else */ if (
          unitId &&
          unitId.toLowerCase().indexOf(searchText) > -1
        ) {
          result.push(unit);
        } else if (unitName && unitName.toLowerCase().indexOf(searchText) > -1) {
          result.push(unit);
        } else if (shortName && shortName.toLowerCase().indexOf(searchText) > -1) {
          result.push(unit);
        } else if (userNamePy && userNamePy.toLowerCase().indexOf(searchText) > -1) {
          result.push(unit);
        }
        // 单位节点搜索路径
        unit.searchPath = unit.searchPath || basePath.concat(unitId);
        filter(unit.searchPath, unit.children, result);
      });
      return result;
    };
    var filterData = filter(searchPath, $.isArray(data) ? data : data.children);
    return filterData;
  };
  OrgUnit.prototype.loadData = function (type) {
    var self = this;
    var options = self.options;
    var resultData = self.data[type];
    if (resultData == null || typeof resultData === 'undefined') {
      // 默认不要用户数据
      var isNeedUser = '0';
      if ($.inArray('U', options.selectTypes) >= 0) {
        isNeedUser = '1';
      }
      var params = {
        isNeedUser: isNeedUser, //是否展示用户
        orgVersionId: options.orgVersionId, //指定对应的版本
        isInMyUnit: options.isInMyUnit /*, 
				eleIdPath : options.eleIdPath */
      };
      if ('MyUnit' === type) {
        // 我的单位
        params.unitId = options.unitId;
      } else if ('MyDept' === type) {
        // 我的部门
      } else if ('MyLeader' === type) {
        // 我的上级
      } else if ('MyUnderling' === type) {
        // 我的下属
      } else if ('PublicGroup' === type) {
        // 公共群组
      } else if ('PrivateGroup' === type) {
        // 个人群组
      } else if ('MyCompany' === type) {
        // 我的集团
        params.unitId = options.unitId;
      }
      params.otherParams = options.otherParams;
      JDS.call({
        service: 'multiOrgTreeDialogService.queryUnitTreeDialogDataByType',
        data: [type, params],
        async: false,
        success: function (result) {
          resultData = self.data[type] = result.data;
        }
      });
    }
    if (self.isSelectOrgType(type) && !options.orgTypes[type]) {
      options.orgTypes[type] = resultData[0].id; // 默认选中第一个组织版本
    }
    return resultData;
  };
  OrgUnit.prototype.loadChildrenData = function (type, parent) {
    var self = this;
    var options = self.options;
    if (Object.keys(parent.children).length) {
      if (options.viewJob === 'show') {
        return parent.children;
      }
      var child = parent.children;
      var newData = [];
      for (var i = 0; i < child.length; i++) {
        if (child[i].type == 'J') {
          var jobName = child[i].name;
          if (child[i].children.length > 0) {
            var userList = child[i].children;
            for (var j = 0; j < userList.length; j++) {
              userList[j].jobName = jobName;
              newData.push(userList[j]);
            }
          }
        } else {
          newData.push(child[i]);
        }
      }
      return newData;
    }
    return parent.children || [];
  };
  OrgUnit.prototype.loadOrgOptionList = function (unitId) {
    var self = this;
    var options = self.options;
    var orgOptionList = $.unit.orgOptionMap[unitId];
    if (orgOptionList == null || typeof orgOptionList === 'undefined') {
      JDS.call({
        service: 'multiOrgService.getOrgOptionListByUnitId',
        data: [unitId, false],
        async: false,
        success: function (result) {
          orgOptionList = $.unit.orgOptionMap[unitId] = result.data;
        }
      });
    }
    if (!options.showRole) {
      return orgOptionList.filter(function (currentValue, index, arr) {
        return !(currentValue.id === 'Role');
      });
    }
    return orgOptionList;
  };
  OrgUnit.prototype.scrollTopLeft = function (paths) {
    setTimeout(function (t) {
      var wrappers = $('#div_' + paths + '_content .mui-table-view .mui-scroll-wrapper');
      if (null == wrappers || wrappers.length <= 0) {
        return; // 列表模式不需滚动
      }
      var wrapperWidth =
        $('#div_' + paths + '_content .mui-table-view .mui-scroll-wrapper .mui-scroll')[0].clientWidth -
        $('#div_' + paths + '_content')[0].clientWidth +
        30;
      var newWidth = wrapperWidth > 0 ? -wrapperWidth : 0;
      var deceleration = mui.os.ios ? 0.0003 : 0.0009;
      wrappers
        .scroll({
          bounce: false,
          indicators: false, // 是否显示滚动条
          deceleration: deceleration
        })
        .scrollTo(newWidth, 0, 0);
      if (wrapperWidth > 0) {
        $('#div_' + paths + '_content .mui-table-view .mui-table-view-cell')[0].style.padding = '12px 0';
      }
    }, 0);
  };
  var unit = {};
  unit.orgOptionMap = {};
  unit.open = function (options) {
    var orgUnit = new OrgUnit(options);
    orgUnit.open();
  };
  $.unit = $.unit2 = unit;
  return unit;
});
