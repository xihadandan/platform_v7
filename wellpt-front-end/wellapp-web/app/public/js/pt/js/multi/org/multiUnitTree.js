/*
 * 各个类型，支持的参数，以及各个参数的优先级
 * 我的单位：[unitId, orgVersionId], orgVersionId>unitId>登录用户的unitId
 * 我的部门：[userId, orgVersionId], orgVersionId>userId>登录用户的Id
 * 我的领导：[userId, orgVersionId], orgVersionId>userId>登录用户的Id
 * 我的下属：[userId, orgVersionId], orgVersionId>userId>登录用户的Id
 * 我的集团：[unitId, orgVersionId], orgVersionId>unitId>登录用户的unitId
 * 职务群组：[unitId, orgVerisonId], orgVersionId>unitId>登录用户的unitId
 */
var UNIT_TREE_TYPE = ['MyUnit', 'MyDept', 'MyLeader', 'MyUnderling', 'MyCompany', 'DutyGroup', 'PublicGroup', 'MyGroup', 'AllUnits', 'DisablePersons'];
var UNIT_TREE_TYPE_NAME = ['我的单位', '我的部门', '我的领导', '我的下属', '我的集团', '职务群组', '公共群组', '个人群组', '全部单位', '交接人'];
(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'appModal'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($) {
  var ztree = $.fn.zTree;
  if (ztree && ztree._z && ztree._z.data && ztree._z.data.addInnerAfterA) {
    var data = ztree._z.data;
    data.addInnerAfterA(function (setting, node, array) {
      var mainJobName = node[setting.data.jobName];
      var mainDepartmentName = node[setting.data.departmentName];
      var mainJobNamePath = node[setting.data.mainJobNamePath];
      var UnitType = $('#UnitType .active').attr('type');
      var isGroup = UnitType.indexOf('Group') > -1 ? true : false;
      var isTaskUsers = UnitType.indexOf('TaskUsers') > -1 ? true : false;
      var _text = '';
      if (node.type === 'U' && node.smartNamePath) {
        _text = node.smartNamePath;
      } else if (mainJobNamePath) {
        _text = mainJobNamePath;
      } else if (mainDepartmentName && mainJobName) {
        _text = mainDepartmentName + '/' + mainJobName;
      } else if (mainDepartmentName) {
        _text = mainDepartmentName;
      } else if (mainJobName) {
        _text = mainJobName;
      }
      if (isTaskUsers) {
        // bug#56528
        // $('#ID_LTree').addClass('limited-width');
      } else {
        $('#ID_LTree').removeClass('limited-width');
      }
      array.push('<span class="node-dj" style="margin-left: 10px" title="' + (_text || '') + '">' + (_text || '') + '</span>');
    });
  }

  function flattenChildren (treeNode, arr) {
    if (treeNode.children.length && treeNode.open) {
      treeNode.children.forEach((item) => {
        arr.push(item);
        if (item.children) {
          flattenChildren(item, arr);
        }
      });
    }
  }

  var currentParentId;
  var startTime = 0,
    endTime = 0,
    perCount = 100,
    perTime = 100;
  var UnitTree = {
    // 设置所有参数的默认值
    defaultOptions: {
      // 下拉框的选择范围，all:全部
      // 具体值参考 UNIT_TREE_TYPE 变量的定义
      // 如果显示多个用分号分隔符
      type: 'all',
      multiple: true, // 是否支持多选;
      data: null, // 初始化数据, treeNode格式，必须要有id,name,type三个要素
      separator: ';', // 多选时候数据的分割符
      selectTypes: 'all', // 可以选择的类型，all:全部，O:组织，B：业务单位，D:部门，J：职位，U:人员，G：群组，DU:职务如果有多个则以分号多分割
      unitId: null, // 用户的归属单位ID,必填
      excludeValues: [], // 数组格式，需要排除的选项
      orgVersionId: null, // 可选参数，如果设置，则只展示该版本的数据，如果没有设置，则显示最新版本的数据
      isInMyUnit: true, // 是否只在查找本单位内的数据,
      otherParams: {}, // 额外参数，主要是供给二开使用
      moreOptList: [], // 提供额外的自定义选项列表 [{"id":"XXXX","name":"模块通讯录"}]
      defaultType: '', // 默认激活的组织类型
      checkMove: true, // 选中移动,左侧树选中后，立刻移动到右侧视图
      valueFormat: null,
      nameFormat: null,
      nameDisplayMethod: '1', // 0:节点名称 1:智能全路径 2:节点简称
      viewStyles: null, // 默认树展示还是按列表展示
      showRole: false // 是否显示角色， type为all的时候做判断，false不显示，true显示，其他时候type有传role则显示
    },
    orgOptionList: null,
    options: {},
    // 缓存搜索结果
    cacheData: {},
    // 组织树的默认设置，
    defaultTreeSetting: {
      callback: {
        // 1，如果需要展示用户，则点击的时候，需要计算出人员，列在中间
        onClick: function (event, treeId, treeNode) {
          if (treeNode == null) {
            return;
          }
          // 展开下级节点,并触发对应的onExpand方法
          var ztree = $.fn.zTree.getZTreeObj(treeId);

          if (!treeNode.isParent) {
            ztree.checkNode(treeNode, !treeNode.checked, true, true);
          }

          ztree.expandNode(treeNode, true, false, true, true, true);
          // 判断是否需要展示账号, 并且节点类型为部门，职位，单位才需要触发异步请求数据
          if ($.inArray('U', UnitTree.options.selectTypes) >= 0) {
            var nodeType = treeNode.type;
            if ($.inArray(nodeType, ['O', 'D', 'J', 'B', 'R', 'C']) > -1) {
              if (UnitTree.options.type == 'TaskUsers') {
                UnitTree.loadTaskUsersNewAccount(treeNode);
              } else {
                UnitTree.getTreeNodeData(treeNode, function (data) {
                  UnitTree.loadNewAccount(data.nodes);
                });
              }
            }
          }
          return true;
        },
        onCheck: function (event, treeId, treeNode) {
          if (treeNode == null) {
            return;
          }
          if (UnitTree.options.checkMove) {
            if (treeNode.checked) {
              // 选中
              if (UnitTree.treeAllChecked) {
                return;
              }
              var orgVersion = $('#ID_OrgType').wellSelect('data');
              var orgVersionName =
                orgVersion && (orgVersion.id || orgVersion.value) && orgVersion.text ? orgVersion.text.split('-')[0] : '';
              treeNode.allPath = UnitTree.getAllPath(treeNode, orgVersionName);
              UnitTree.newAddSelectedItem(treeNode);
            } else {
              // 取消选中
              UnitTree.removeSelectedItem(treeNode);
            }
            return true;
          }
        },
        // 节点创建时，根据 selectTypes来决定是否要展示checkbox或radio
        onNodeCreated: function (event, treeId, treeNode) {
          if (treeNode.parentTId && currentParentId !== treeNode.parentTId) {
            currentParentId = treeNode.parentTId;
            var itemHeight = 23.4;
            var boxHeight = $('.left-tree').height(); // 容器高度
            var currentHeight = $('#' + treeNode.tId).parent().height(); // 将要展开节点总的高度
            var boxTop = $('.left-tree').offset().top;
            if (currentHeight > boxHeight) {
              boxTop = boxTop + itemHeight;
            }
            var currentTop = $('#' + treeNode.tId).offset().top;
            var nodeTop = currentTop - boxTop;
            var currentBottom = boxHeight - nodeTop;
            if (currentHeight > currentBottom) {
              var position = $('.left-tree').scrollTop();
              if (currentHeight < boxHeight) {
                $('.left-tree').scrollTop(position + (currentHeight -currentBottom));
              } else {
                $('.left-tree').scrollTop(position + nodeTop);
              }
            }
          }

          var nodeType = treeNode.type;
          var ztree = $.fn.zTree.getZTreeObj(treeId);
          var unitType = $('#UnitType .active').attr('type');
          var isGroup = unitType.indexOf('Group') > -1 ? true : false; //群组
          var isTaskUsers = unitType.indexOf('TaskUsers') > -1 ? true : false; //办理人
          if (isGroup || isTaskUsers) {
            treeNode.realName = treeNode.name.split('/').pop();
            treeNode.name = treeNode.type !== 'U' ? treeNode.smartNamePath || treeNode.name : treeNode.name;
            ztree.updateNode(treeNode);
          }
          // 判断是否是需要排除的选项，如果是的话，则需要隐藏该节点
          if ($.inArray(treeNode.id, UnitTree.options.excludeValues) >= 0) {
            ztree.removeNode(treeNode);
          } else {
            // 不支持选择，设置 checkbox或radio 隐藏
            if ($.inArray(nodeType, UnitTree.options.selectTypes) < 0) {
              treeNode.nocheck = true;
              ztree.updateNode(treeNode);
            }
          }
          // 如果该节点在已选中的列表中，则需要标识为选中选中状态
          if ($('#ID_RTree #' + treeNode.id).length > 0) {
            // length>0
            // 表示有此控件，则就是有值
            // treeNode.nocheck = false;
            ztree.checkNode(treeNode, true, false, true);
          }
        },
        beforeExpand: function (treeId, treeNode) {
          var zTree = $.fn.zTree.getZTreeObj(treeId);
          if (treeNode.isParent) {
            treeNode.children = treeNode.children || [];
          }
          if (treeNode.children.length) {
            return true;
          }
          if (!treeNode.isAjaxing) {
            treeNode.times = 1;
            var otherParam = zTree.setting.async.otherParam;
            var data = otherParam.data;
            var ID_OrgType_data = $('#ID_OrgType').wellSelect('data');
            var orgVersionId = ID_OrgType_data ? ID_OrgType_data.id : '';
            data[1].orgVersionId = orgVersionId;
            data[1].treeNodeId = treeNode.id;
            data[1].checkedIds = $.map(UnitTree.rightListDataIds || [], function (item) {
              return item;
            });
            data[1].rootId = UnitTree.getCurrentRootId(treeNode);

            var t = $('#UnitType').find(".active").attr("type");
            if ('AllUnits' == t) {
              if (treeNode.idPath && treeNode.idPath.indexOf('/') > -1) {
                data[1].orgVersionId = treeNode.idPath.split('/')[0];
              }
            }

            otherParam.data = data;
            zTree.updateNode(treeNode);
            zTree.reAsyncChildNodes(treeNode, 'refresh', true);
            return true;
          } else {
            appModal.info('数据加载中，请稍后展开节点...');
            return false;
          }
        },
        onExpand: function (event, treeId, treeNode) {
          var nodeArr = [];
          flattenChildren(treeNode, nodeArr);
          if (treeNode.children.length && treeNode.open) {
              var itemHeight = 23.4;
              var boxHeight = $('.left-tree').height(); // 容器高度 356
              var currentHeight = nodeArr.length * itemHeight; // 子级的高度
              var boxTop = $('.left-tree').offset().top; // 容器离父容器的高度
              if (currentHeight > boxHeight) {
                boxTop = boxTop + itemHeight;
              }
              var currentTop = $('#' + treeNode.tId).offset().top + itemHeight; // 当前选中节点离.left-tree 的父容器的高度
              var nodeTop = currentTop - boxTop; // 当前选中节点离父容器的距离
              var currentBottom = boxHeight - nodeTop;// 当前选中节点离父容器底部的距离
              if (currentHeight > currentBottom) {
                var position = $('.left-tree').scrollTop();
                if (currentHeight < boxHeight) {
                  $('.left-tree').scrollTop(position + (currentHeight -currentBottom));
                } else {
                  $('.left-tree').scrollTop(position + nodeTop);
                }
              }
          }

          UnitTree.resizeLTreeScroll();
        },
        onCollapse: function (event, treeId, treeNode) {
          UnitTree.resizeLTreeScroll();
        },
        onAsyncSuccess: function (event, treeId, treeNode, msg) {
          var zTree = $.fn.zTree.getZTreeObj(treeId);
          if (!msg || msg.length === 0) {
            return;
          }
          if (treeNode) {
            treeNode.icon = '';
            zTree.updateNode(treeNode);
            var child = treeNode.children || [];
            $.each(child, function (i, item) {
              var orgVersion = '';
              if ($('#ID_OrgType').data('wellSelect')) {
                orgVersion = $('#ID_OrgType').wellSelect('data').text.split('-')[0];
              }
              if (item.namePath) {
                item.realNamePath = item.namePath;
              } else if (treeNode.namePath || treeNode.realNamePath) {
                if (item.type === 'U') {
                  item.realNamePath = treeNode.namePath || treeNode.realNamePath || item.name;
                } else {
                  item.realNamePath = (treeNode.namePath || treeNode.realNamePath) + '/' + item.name;
                }
              } else {
                item.realNamePath = item.name;
              }
              if (item.realNamePath.split('/')[0] !== orgVersion) {
                item.realNamePath = orgVersion ? orgVersion + '/' + item.realNamePath : item.realNamePath;
              }
              zTree.updateNode(item);
              if (item.type === 'U' && UnitTree.rightListDataIds.indexOf(item.id) > -1) {
                var nodeData = UnitTree.getSelectedDataById(item.id);
                //父级为群组时不做判断
                if (treeNode.type !== 'G' && nodeData.dj.indexOf(treeNode.name) < 0) {
                  // zTree.checkNode(item, false, true, false);
                }
              }
            });
            // 组织树的展开和收起都会对滚动条进行resize，但是当加载比较慢的时候，就会出现resize的时候数据还没有出现，导致滚动条无变化
            // 而请求结束后，滚动条无变化，就无法滚动查看加载出来的数据，导致bug的产生。因此这边加载成功时，也resize下
            UnitTree.resizeLTreeScroll();
          }
        },
        onAsyncError: function (event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
          var zTree = $.fn.zTree.getZTreeObj(treeId);
          appModal.info('获取数据出现异常。');
          treeNode.icon = '';
          zTree.updateNode(treeNode);
        }
      },
      check: {
        enable: true, // 显示checkbox或radio
        chkStyle: 'checkbox', // checkbox 或者 radio
        chkboxType: {
          // 设置checkbox自己只管控自己，不要上下级联动管控
          Y: '',
          N: ''
        }
      },
      view: {
        showTitle: false,
        addHoverDom: function (treeId, treeNode) {
          $('#' + treeId + ' #' + treeNode.tId).addClass('hover');
          var $nodeName = $('#' + treeId + ' #' + treeNode.tId + '_span');
          var UnitType = $('#UnitType .active').attr('type');
          if (!$nodeName.attr('title')) {
            var path = treeNode.realNamePath || UnitTree.getNodeAllPath(treeNode.id);
            $('#' + treeId + ' #' + treeNode.tId + '_span').attr('title', UnitType === 'TaskUsers' ? treeNode.name : path);
          }
        },
        removeHoverDom: function (treeId, treeNode) {
          $('#' + treeId + ' li.hover').removeClass('hover');
        }
      },
      async: {
        enable: true,
        url: ctx + '/json/data/services',
        autoParam: ['id'],
        otherParam: {
          methodName: 'children',
          serviceName: 'multiOrgTreeDialogService',
          validate: false,
          data: []
        },
        contentType: 'application/json',
        dataType: 'json'
      },
      data: {
        jobName: 'mainJobName',
        departmentName: 'mainDepartmentName',
        mainJobNamePath: 'mainJobNamePath'
        // key: {
        //   title: 'realNamePath'
        // }
      }
    },

    // 获取节点的根节点Id
    getCurrentRootId: function (treeNode) {
      var self = this;
      if (treeNode.getParentNode() != null) {
        var parentNode = treeNode.getParentNode();
        return self.getCurrentRootId(parentNode);
      } else {
        return treeNode.id;
      }
    },

    checkParams: function (params) {
      // 检查基本参数
      if (params.type == null) {
        console.error('参数  type 不能为空');
        return false;
      } else if (params.selectTypes == null) {
        console.error('参数  selectTypes 不能为空');
        return false;
      }
      // 按照类型检查对应的参数
      for (var i = 0; i < params.types.length; i++) {
        if ('MyUnit' == params.types[i]) {
          // 我的单位
        }
        if ('MyDept' == params.types[i]) {
          // 我的部门
        }
      }
      return true;
    },
    getAllPath: function (treeNode, orgVersionName) {
      var paths = treeNode.getPath();
      var pathArr = [];
      if (orgVersionName) {
        pathArr.push(orgVersionName);
      }
      for (var i = 0; i < paths.length; i++) {
        if (paths[i].type === 'G' || paths[i].type === 'U') {
          continue;
        }
        pathArr.push(paths[i].name);
      }
      return pathArr.join('/');
    },
    init: function (params) {
      console.log('multiUnitTree.init 打开组织弹出框-----开始');
      var _self = this;

      _self.rightListDataIds = [];
      this.orgOptionList = JSON.parse($('#div_orgOptionList').html());
      // 合并参数和默认配置
      this.options = $.extend({}, this.defaultOptions, params);
      // var showRoot = this.options.showRoot;// 是否展示根节点
      // if(showRoot == null || typeof showRoot === "undefined") {
      // 	this.options.showRoot = !!(params.eleIdPath || params.otherParams.eleIdPath);
      // }
      // 将selectTypes转成数组格式
      if ('all' === this.options.selectTypes.toLowerCase()) {
        this.options.selectTypes = ['O', 'B', 'D', 'J', 'U', 'G', 'DU', 'E', 'R', 'C', 'MG', 'MC', 'V'];
      } else {
        this.options.selectTypes = this.options.selectTypes.split(';');
      }
      // 将type转成数组格式
      if ('all' === this.options.type.toLowerCase()) {
        // 显示全部
        UnitTree.options.types = [];
        if (_self.orgOptionList) {
          $.each(_self.orgOptionList, function (index, opt) {
            if (opt.id === 'Role' && _self.options.showRole) {
              UnitTree.options.types.push(opt.id);
            } else if (opt.isShow) {
              UnitTree.options.types.push(opt.id);
            }
          });
        } else {
          this.options.types = UNIT_TREE_TYPE;
        }
      } else {
        this.options.types = this.options.type.split(';');
      }

      // 检查参数
      var res = this.checkParams(this.options);
      if (!res) {
        return;
      }

      // 单选模式
      if (this.options.multiple == false) {
        this.defaultTreeSetting = $.extend(true, this.defaultTreeSetting, {
          check: {
            chkStyle: 'radio',
            radioType: 'all'
          }
        });
      }

      // 初始化下拉框选项
      this.initUnitComboBox(this.options);
      // 初始化列表滚动条
      this.setListScroll();
      this._resizeLTreeScroll();
      // 设置按钮事件
      this.initBtnEvent();
      // 初始化列表视图
      this.$indexlist = $('.indexed-list').indexlist(
        $.extend({
            sort: function (a, b) {
              return a.data.shortName > b.data.shortName ? 1 : -1;
            }
          },
          this.options.indexOptions
        )
      );
      // 初始化数据
      if (this.options.data && $.trim(this.options.data) != '') {
        this.initData(this.options.data, this.options);
      }

      var $defaultTypeEle = $('#UnitType li:first');
      if (this.options.defaultType && this.options.defaultType != '') {
        if ($('#UnitType li[type=' + this.options.defaultType + ']').length > 0) {
          $defaultTypeEle = $('#UnitType li[type=' + this.options.defaultType + ']');
        }
      }
      $defaultTypeEle.trigger('click');

      this.lazyRenderRightList(this.rightListDataStore);
      //单选时隐藏全选
      if (!this.options.multiple) {
        $('#ID_LCheckAll').hide();
        $('#ID_MSelectAll_Box').hide().siblings('label').hide();
      }
      console.log('multiUnitTree.init 打开组织弹出框-----结束');
    },
    // 初始化单位类型选择下拉框数据
    initUnitComboBox: function (options) {
      var _self = this;
      var orgOptionMap = (function () {
        var map = {};
        for (var i = 0; i < UnitTree.orgOptionList.length; i++) {
          map[UnitTree.orgOptionList[i].id] = UnitTree.orgOptionList[i];
        }
        return map;
      })();
      if (options.moreOptList && options.moreOptList.length > 0) {
        $.each(options.moreOptList, function (index, item) {
          if ($.inArray(item.id, options.types) == -1 || orgOptionMap[item.id] == undefined) {
            options.types.push(item.id);
            if (!orgOptionMap[item.id]) {
              UnitTree.orgOptionList.push(item);
            }
          }
        });
      }
      for (var i = 0; i < UnitTree.orgOptionList.length; i++) {
        var t = UnitTree.orgOptionList[i].id;
        var n = UnitTree.orgOptionList[i].name;
        var viewStyles = UnitTree.orgOptionList[i].attach;
        if ($.inArray(t, options.types) >= 0) {
          var html = "<li class='unit-list-item' type='" + t + "' title='" + n + "' view-styles='" + viewStyles + "'>" + n + '</li>';
          $('#UnitType').append(html);
        }
        //交接人
        if ($.inArray("DisablePersons", options.types) >= 0) {
          var html = "<li class='unit-list-item' type='" + "DisablePersons" + "' title='" + n + "' view-styles='" + viewStyles + "'>" + "交接人" + '</li>';
          if ($('#UnitType').html().length == 0 || $('#UnitType').html().indexOf("DisablePersons") == 0) {
            $('#UnitType').append(html);
          }
        }
      }

      // 根据不同的选项展示不同的数据
      $('#UnitType li').click(options, function () {
        _self.expandTimes = 0;
        // 流程办理人用户列表隐藏展开、折叠按钮，树形列表显示展开、折叠按钮
        var viewType = $('#ID_SwitchView').hasClass('icon-ptkj-zuzhijiagoufenjishitu') ? 'tree' : 'user';
        if (UnitTree.options.type == 'TaskUsers') {
          $('#ID_LVIEWPORT').next('.form_operate').show();
          if (viewType == 'user') {
            $('#ID_LVIEWPORT').next('.form_operate').find('#ID_LCollapseAll,#ID_LExpandAll').hide();
          } else {
            $('#ID_LVIEWPORT').next('.form_operate').find('#ID_LCollapseAll,#ID_LExpandAll').show();
          }
        } else {
          $('#ID_LVIEWPORT').next('.form_operate').show();
          $('#ID_LVIEWPORT').next('.form_operate').find('#ID_LCollapseAll,#ID_LExpandAll').show();
        }
        // 其他按钮设置为未选中效果，该按钮设置成选中效果
        $('#UnitType li').removeClass('active');
        $(this).addClass('active');
        $('#ID_LSelectAll_Box').removeAttr('checked');
        // 清空中间的数据
        // $("#ID_MTree").empty();
        UnitTree.emptyMtree();

        var t = $(this).attr('type');
        var viewStyles = $(this).attr('view-styles');
        // 默认不要用户数据
        var isNeedUser = options.isNeedUser ? options.isNeedUser : '0';
        if (!options.isNeedUser && $.inArray('U', options.selectTypes) >= 0) {
          isNeedUser = '1';
        }
        if (!options.orgVersionId) {
          $('#ID_OrgType').wellSelect('val', '');
        }
        var params = {
          isNeedUser: isNeedUser, // 是否展示用户
          orgVersionId: options.orgVersionId, // 指定对应的版本
          isInMyUnit: options.isInMyUnit, //
          eleIdPath: options.eleIdPath
        };
        if ('MyUnit' == t) {
          // 我的单位
          params.unitId = options.unitId;
        } else if ('DisablePersons' == t) {
          //交接人
          params.unitId = options.unitId;
        } else if ('MyDept' == t) {
          // 我的部门
        } else if ('MyLeader' == t) {
          // 我的上级
        } else if ('MyUnderling' == t) {
          // 我的下属
        } else if ('PublicGroup' == t) {
          // 公共群组
        } else if ('PrivateGroup' == t) {
          // 个人群组
        } else if ('MyCompany' == t) {
          // 我的集团
          params.unitId = options.unitId;
        }
        if ($.trim(viewStyles).length && viewStyles.indexOf('list') > -1 && $.inArray('U', UnitTree.options.selectTypes) > -1) {
          $('.lKeyWords').css('width', '325px');
          var defaultViewStyles = UnitTree.options.viewStyles;
          if (defaultViewStyles && defaultViewStyles[t] === 'list') {
            $('#ID_SwitchView').trigger('click');
            delete defaultViewStyles[t];
          }
        } else {
          $('.lKeyWords').css({
            width: '350px',
            height: '34px'
          });
          $('#ID_LTree').show();
          $('#ID_LView').hide();
          $('#ID_SwitchView').removeClass('icon-ptkj-pingjishitu').addClass('icon-ptkj-zuzhijiagoufenjishitu');
        }

        params.otherParams = options.otherParams;
        if (_self.rightListDataIds && _self.rightListDataIds.length) {
          params.checkedIds = $.map(_self.rightListDataIds, function (item) {
            return item;
          });
        }
        params.nameDisplayMethod = options.nameDisplayMethod;
        console.log('multiUnitTree.init 获取数据-----开始');
        var setting = UnitTree.getTreeSetting();

        var localData = _self.getLocalData(t);
        // 将数据按照  用户、公共群组、职务群组排序
        localData.sort(function (obj1, obj2) {
          var str = ['U', 'G', 'D'];
          var idx1 = str.indexOf(obj1.type);
          var idx2 = str.indexOf(obj2.type);
          if (idx2 < idx1) {
            return 1;
          } else if (idx2 > idx1) {
            return -1;
          } else {
            return 0;
          }
        });

        // 有本地数据，则采用本地数据
        this.params = params;
        if (localData && localData.length > 0) {
        } else {
          $.ajax({
            url: ctx + '/api/org/tree/dialog/children',
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
              type: t,
              params: params
            }),
            async: false,
            success: function (result) {
              var treeNodes = result.data;
              localData = treeNodes;
              if ('AllUnits' == t && result.data.length > 0 && result.data[0].type == 'V') {
                options.orgVersionId = result.data[0].id;
                params.orgVersionId = result.data[0].id;
              }
            }
          });
        }
        setting.async.otherParam.data = [t, params];
        UnitTree.showLTree(localData, setting);
        // 隐藏起来的供给搜索使用的树
        var searchTree = $.fn.zTree.init($('#ID_SearchTree'), {}, localData);
        // 控制组织类型展示并调整左视窗高度
        if (!options.showRoot && $.inArray(t, ['MyUnit', 'MyUnitDept', 'MyDept', 'MyLeader', 'MyUnderling', 'DisablePersons']) > -1) {
          var optionHtml = '';
          for (var i = 0, len = localData.length; i < len; i++) {
            var optionItem = localData[i];
            optionHtml += '<option value="' + optionItem.id + '">' + optionItem.name + '</option>';
          }
          $('#ID_OrgType').html(optionHtml);
          if (!$('#ID_OrgType').data('wellSelect')) {
            $('#ID_OrgType').wellSelect();
          } else {
            $('#ID_OrgType').wellSelect('reRenderOption');
          }
          if (localData[0]) {
            $('#ID_OrgType').wellSelect('val', localData[0].id).trigger('change');
          }
        } else {
          $('#ID_OrgType').hide();
          if ($('#ID_OrgType').data('wellSelect')) {
            $('#ID_OrgType').wellSelect('destroy');
          }
        }
        UnitTree.updateLViewHeight();
        console.log('multiUnitTree.init 获取数据-----结束');
        // 流程办理人树形列表默认展开第一个结点
        // if (UnitTree.options.type == "TaskUsers" && viewType == "tree") {
        //   var ztree = UnitTree.getZtree();
        //   if (ztree != null) {
        //     var nodes = ztree.getNodes();
        //     if (nodes.length > 0) {
        //       ztree.expandNode(nodes[0], true, false, true, true, true);
        //     }
        //   }
        // }
      });
    },
    setListScroll: function () {
      // 左
      $('#ID_LVIEWPORT').niceScroll({
        cursorcolor: '#cecece',
        zindex: 11,
        railoffset: {
          left: 5
        },
        railhoffset: {
          top: 5
        },
        autohidemode: false //滚动条是否自动隐藏，默认为true
      });
      $('#ID_LView .indexed-list-inner').niceScroll({
        cursorcolor: '#cecece',
        zindex: 11,
        railoffset: {
          left: 5
        },
        railhoffset: {
          top: 5
        },
        autohidemode: false //滚动条是否自动隐藏，默认为true
      });
      // 中
      $('#ID_MTree', '#ID_MTree_box').niceScroll({
        cursorcolor: '#cecece',
        zindex: 11,
        height: 390,
        autohidemode: false //滚动条是否自动隐藏，默认为true
      });
      // 右
      $('#ID_RTree').niceScroll({
        cursorcolor: '#cecece',
        zindex: 11,
        autohidemode: false //滚动条是否自动隐藏，默认为true
      });
      this._resizeLViewScroll();
    },
    _resizeLTreeScroll: function () {
      this.resizeLTreeScroll = _.debounce(function () {
        // setTimeout(function () {
        var scrollbar = $('#ID_LVIEWPORT').getNiceScroll();
        scrollbar.resize();

        // var newTop = parseInt($('#' + scrollbar[0].id).css('top')) - 18;
        // $('#ID_LVIEWPORT').getNiceScroll(0).doScrollTop(newTop);
        // scrollbar[0].lastposition && (scrollbar[0].lastposition.top = newTop);

        // $('#' + scrollbar[0].id).css('top', newTop);

        // var hrScrollbar = $('#' + scrollbar[0].id + '-hr');
        // if (hrScrollbar.length) {
        // var hrTop = parseInt(hrScrollbar.css('top')) - 18;
        // $('#' + scrollbar[0].id + '-hr').css('top', hrTop);
        // }

        UnitTree.updateLViewHeight();
      }, 300);
    },
    resizeRTreeScroll: function () {
      var scrollbar = $('#ID_RTree').getNiceScroll();
      scrollbar.resize();

      // var newTop = parseInt($('#' + scrollbar[0].id).css('top')) - 18;
      // scrollbar[0].lastposition && (scrollbar[0].lastposition.top = newTop);
      // $('#' + scrollbar[0].id).css('top', newTop);

      // var hrScrollbar = $('#' + scrollbar[0].id + '-hr');

      // if (hrScrollbar.length) {
      //   console.log(11)
      //   var hrTop = parseInt(hrScrollbar.css('top')) - 18;
      //   $('#' + scrollbar[0].id + '-hr').css('top', hrTop);
      // }
    },
    _resizeLViewScroll: function () {
      this.resizeLViewScroll = _.debounce(function () {
        var scrollbar = $('#ID_LView .indexed-list-inner').getNiceScroll();
        scrollbar.resize();
      }, 300);
    },
    // 获取本地数据，而不是去服务端请求
    getLocalData: function (type) {
      var data = [];
      var moreOptList = this.options.moreOptList;
      if (moreOptList) {
        $.each(moreOptList, function (i) {
          if (moreOptList[i].id == type && moreOptList[i].treeData) {
            // ztree BUG,必须是复制一份数据出来，否则节点展示不出来, 通过json转化方式来快速复制
            var jsonStr = JSON.stringify(moreOptList[i].treeData);
            data = JSON.parse(jsonStr);
          }
        });
        $.each(data, function (i, item) {
          item.type = item.iconSkin = item.id[0];
        });
      }
      var _dataIdArr = data.map(function (item) {
        return item.id;
      });
      if (_dataIdArr.length && type != 'TaskUsers') {
        this.updateNodeData(data, _dataIdArr);
      }
      return data;
    },

    showLTree: function (treeNodes, setting, full) {
      if (!treeNodes || !treeNodes.length) {
        $.fn.zTree.destroy('ID_LTree');
        $('#ID_LTree').html('<div class="no-data">暂无数据</div>');
        this.resizeLTreeScroll();
        return;
      }
      var orgVersion = '';
      if ($('#ID_OrgType').data('wellSelect')) {
        orgVersion = $('#ID_OrgType').wellSelect('data').text.split('-')[0];
      }
      for (var i = 0; i < treeNodes.length; i++) {
        treeNodes[i].realNamePath = orgVersion ? orgVersion + '/' + treeNodes[i].name : treeNodes[i].name;
      }
      var zTree = $.fn.zTree.init($('#ID_LTree'), setting, treeNodes);
      // 默认展开第一个节点
      var nodes = zTree.getNodes();
      if (nodes.length === 1) {
        var node = nodes[0];
        zTree.expandNode(node, true, false, false, true);
      }
      if (full) {
        zTree.expandAll(true);
      }
      this.resizeLTreeScroll();
    },

    getTreeSetting: function () {
      return $.extend({}, UnitTree.defaultTreeSetting);
    },
    // 初始化各个按钮事件
    initBtnEvent: function (options) {
      console.log('initBtnEvent');
      // 左侧按钮
      this.initLeftBtnEvent(options);
      // 中间按钮
      this.initMiddleBtnEvent(options);
      // 右侧按钮
      this.initRightBtnEvent(options);
      // 搜索按钮事件
      this.initSearchBtnEvent(options);
    },

    renderLTreeView: function (id, full) {
      var _self = this;
      var setting = this.getTreeSetting();
      var _treeNodes = [];
      var t = $('#UnitType li.active').attr('type');
      var service = '/api/org/tree/dialog/children';
      var params = {
        isNeedUser: _self.options.isNeedUser || '1', // 是否展示用户
        orgVersionId: this.options.orgVersionId, // 指定对应的版本
        isInMyUnit: this.options.isInMyUnit, //
        eleIdPath: this.options.eleIdPath
      };
      params.otherParams = _self.options.otherParams;
      params.excludeValues = _self.options.excludeValues;
      if ($.inArray(t, ['MyUnit', 'MyCompany', 'DisablePersons']) > -1) {
        params.unitId = this.options.unitId;
      }
      params.treeNodeId = id;
      params.orgVersionId = id;
      if (full) {
        params.full = full;
        service = '/api/org/tree/dialog/full';
      }
      params.checkedIds = $.map(_self.rightListDataIds || [], function (item) {
        return item;
      });
      $.ajax({
        url: ctx + service,
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify({
          type: t,
          params: params
        }),
        success: function (result) {
          _treeNodes = result.data;
          UnitTree.showLTree(_treeNodes, setting, full);
        }
      });
    },

    renderLTreeViewReq: function (service, t, params) {
      var defer = $.Deferred();
      JDS.call({
        service: service,
        data: [t, params],
        async: false,
        success: function (result) {
          defer.resolve(result);
        }
      });
      return defer.promise();
    },

    renderSearchList: function (data, type) {
      var _self = this;
      $('#ID_LTree').hide();
      $('#ID_LVIEWPORT').next().hide();
      $('#ID_LView').show().find('.table-view .table-view-list').empty();
      _self.updateLViewHeight();
      if (!data) {
        $('#ID_LView .bar-inner').empty();
        return;
      }
      if (type === 'user') {
        _self.renderIndexListBar(data.py);
        _self.lazyLoadSearchList(data.nodes);
      } else {
        _self.lazyLoadSearchList(data);
      }
    },

    renderIndexListBar: function (list) {
      var _self = this;
      var $barInner = $('#ID_LView').find('.bar-inner');
      var $tableaView = $('#ID_LView').find('.table-view .table-view-list');
      $barInner.empty();
      $.each(list, function (i, item) {
        $barInner.append('<a>' + item.toUpperCase() + '</a>');
      });

      $barInner.on('click', 'a', function (event) {
        var $this = $(this);
        $this.addClass('active').siblings('.active').removeClass('active');
        var group = $this.text();
        var index = _self.getSearchListIndex(group);
        $('#ID_LView')
          .find('.indexed-list-item')
          .each(function () {
            var _$this = $(this);
            var _index = _$this.attr('data-index');
            var _$next = _$this.next();
            var item = _self.searchDataStore[index];
            if ((!_$next.length && _index < index) || (_$next.length && _index < index && _$next.attr('data-index') > index)) {
              JDS.call({
                service: 'multiOrgUserTreeNodeService.gerUserJob',
                data: [
                  [item.id]
                ],
                async: false,
                success: function (result) {
                  var dj = result.data[item.id];
                  if (dj && dj.iconSkin) {
                    item.iconSkin = dj.iconSkin;
                  }
                  if (dj && dj.mainJobs.length) {
                    if (dj.mainJobs[0].parent) {
                      item.dj = dj.mainJobs[0].parent.name + '/' + dj.mainJobs[0].name;
                    } else {
                      item.dj = dj.mainJobs[0].name;
                    }
                  } else if (dj && dj.otherJobs.length) {
                    if (dj.otherJobs[0].parent) {
                      item.dj = dj.otherJobs[0].parent.name + '/' + dj.otherJobs[0].name;
                    } else {
                      item.dj = dj.otherJobs[0].name;
                    }
                  }
                }
              });
              _self.insetSearchList($tableaView, [item], 'down', _index);
              return false;
            }
          });

        var curEle = $tableaView.find('[data-index="' + index + '"]');
        _self.appendSearchList($tableaView, 'up', curEle);
        _self.appendSearchList($tableaView, 'down', curEle);

        setTimeout(function () {
          var scrollTop = $tableaView.find('.indexed-list-item').index(curEle) * 28;
          $('#ID_LView').find('.indexed-list-inner').scrollTop(scrollTop);
        }, 300);
      });
    },

    getSearchListIndex: function (group, id) {
      var _self = this;
      var searchDataStore = _self.searchDataStore;
      var firstIndex;
      $.each(searchDataStore, function (i, item) {
        if (id) {
          if (item.id === id) {
            firstIndex = i;
            return false;
          }
        } else {
          var tag = item.namePy && item.namePy.charAt(0).toUpperCase();
          if (tag === group) {
            firstIndex = i;
            return false;
          }
        }
      });
      return firstIndex;
    },

    setSearchList: function (list) {
      $.each(list, function (i, item) {
        item._index = i;
      });
      this.searchDataStore = list;
    },

    buildIndexedListItem: function (item) {
      var _self = this;
      if (item.isRender) {
        return '';
      }
      item.isRender = true;
      var tpl = '';
      var id = 'LU-' + $.uuid++;
      if (item.type == undefined || $.inArray(item.type, this.options.selectTypes) >= 0) {
        var inputType = this.options.multiple ? 'checkbox' : 'radio';
        tpl +=
          '<input name="leftItem" data-id="' +
          item.id +
          '" id="' +
          id +
          '" type="' +
          inputType +
          '" ' +
          (item.checked ? 'checked="checked"' : '') +
          '">';
      }

      tpl += '<label for="' + id + '" class="chkbox iconfont"></label>';
      tpl += '<i class="icon U ' + item.iconSkin + '"></i>';
      tpl += '<label for="' + id + '" class="name" title="' + item.name + '">' + item.name + '</label>';
      var labelText = '';
      var _OrgType = $('#ID_OrgType').data('wellSelect') ? $('#ID_OrgType').data('wellSelect').val() : '';
      if (_OrgType) {
        labelText = item.namePath ? item.namePath.split('/').splice(1).join('/') : '';
      } else {
        labelText = item.namePath ? item.namePath : '';
      }
      if (labelText) {
        tpl += '<label for="' + id + '" class="job" title="' + labelText + '">' + labelText + '</label>';
      }
      var tag = item.namePy ? item.namePy.charAt(0).toUpperCase() : '';
      var $li = $('<li>', {
        'data-index': item._index,
        'data-id': item.id,
        'data-value': item.name,
        'data-tags': tag,
        class: 'table-view-cell indexed-list-item'
      });
      $li.data('node', item).append(tpl);
      $.each(_self.rightListDataIds, function (i, _id) {
        if (_id === item.id) {
          var rightData = _self.getSelectedDataById(_id);
          if ((item.dj && item.dj.indexOf(rightData.dj) > -1) || (rightData.dj && rightData.dj.indexOf(item.dj) > -1)) {
            if (item.iconSkin !== rightData.iconSkin) {
              _self.updateRightListItemIcon(item);
            }
            $li.find('input').attr('checked', true);
          }
          return false;
        }
      });
      return $li;
    },

    appendSearchList: function (ele, type, curEle) {
      var _self = this;
      var data;
      if (!ele && !type) {
        data = _self.searchDataStore;
        // return data;
      }
      ele = ele || $('#ID_LView .table-view .table-view-list');
      if (type === 'first' || !type) {
        data = _self.searchDataStore.slice(0, 30);
      } else {
        var _index;
        if (type === 'up') {
          _index = _self.getSearchListIndex(null, curEle.attr('data-id'));
          data = _self.searchDataStore.slice(_index > 50 ? _index - 50 : 0, _index).reverse();
        } else if (type === 'down') {
          _index = _self.getSearchListIndex(null, curEle.attr('data-id'));
          data = _self.searchDataStore.slice(_index, _index + 50);
        }
      }

      var userList = $.map(data, function (item) {
        if (item.id[0] === 'U') {
          return item.id;
        }
      });

      if (userList.length) {
        $.each(data, function (i, item) {
          if (item.type === 'U' && item.id[0] === 'U') {
            item.dj = item.namePath ? item.namePath : '';
          }
        });
        _self.insetSearchList(ele, data, type);
      } else {
        _self.insetSearchList(ele, data, type);
      }
    },

    insetSearchList: function (ele, data, type, _index) {
      var _self = this;
      $.each(data, function (i, item) {
        var mountLi = _self.buildIndexedListItem(item);
        if (!mountLi) {
          return true;
        }
        if (mountLi.find('input').length == 0) {
          //非可勾选
          mountLi.addClass("indexed-list-item-not-check");
        }
        if (_self.rightListDataIds.indexOf(item.id) > -1) {
          var rightData = _self.getSelectedDataById(item.id);
          if (
            (item.dj && item.dj.indexOf(rightData.dj) > -1) ||
            (rightData.dj && rightData.dj.indexOf(item.dj) > -1) ||
            // (item.namePath && item.namePath.indexOf(rightData.dj) > -1) ||
            (item.namePath && item.namePath === (rightData.namePath || rightData.realNamePath))
          ) {
            if (item.iconSkin !== rightData.iconSkin) {
              _self.updateRightListItemIcon(item);
            }
            mountLi.find('input').attr('checked', true);
          }
        }
        if (type === 'up') {
          ele.find('[data-index="' + (item._index + 1) + '"]').before(mountLi);
        } else if (type === 'down') {
          if (_index) {
            ele.find('[data-index="' + _index + '"]').after(mountLi);
          } else {
            ele.find('[data-index="' + (item._index - 1) + '"]').after(mountLi);
          }
        } else {
          ele.append(mountLi);
        }
      });

      //添加事件-非可勾选的元素点击事件
      $(ele).find(".indexed-list-item-not-check").unbind().bind("click", function () {
        var data_id = $(this).attr("data-id");
        _self._searchNotCheckgetTreeNodeData(data_id);

      });
      _self.resizeLViewScroll();
    },
    _searchNotCheckgetTreeNodeData: function (data_id) {
      var _self = this;
      var t = $('#UnitType li.active').attr('type');
      var orgVersionId = $('#ID_OrgType').val();
      var params = {
        isNeedUser: '1', // 是否展示用户
        orgVersionId: orgVersionId, // 指定对应的版本
        isInMyUnit: _self.options.isInMyUnit,
        eleIdPath: _self.options.eleIdPath,
        sort: 'code'
      };
      params.treeNodeId = data_id;
      params.otherParams = _self.options.otherParams;
      params.excludeValues = _self.options.excludeValues;
      $.ajax({
        url: ctx + '/api/org/tree/dialog/allUserSearch',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify({
          type: t,
          params: params
        }),
        async: false,
        success: function (result) {
          _self.loadNewAccount(result.data.nodes);
        }
      });
    },

    lazyLoadSearchList: function (list) {
      var _self = this;
      var $LView = $('#ID_LView');
      var $LViewH = $LView.outerHeight();
      var $LVIEWPORT = $('#ID_LVIEWPORT');
      var $LOperate = $LVIEWPORT.next('.form_operate');
      if ($LOperate.is(':visible')) {
        $LViewH = $LViewH - 35;
      }
      var $listInner = $LView.find('.indexed-list-inner').css('height', $LViewH);
      var $tableView = $listInner.find('.table-view .table-view-list').css({
        height: $LViewH
      });
      _self.appendSearchListStatus = false;
      _self.setSearchList(list);
      _self.appendSearchList($tableView, 'first');
      $listInner.scroll(function () {
        var scrollTop = $(this).scrollTop();
        if (!_self.listInnerScrollTop) {
          _self.listInnerScrollTop = 0;
        }
        var curEle;
        if (_self.listInnerScrollTop < scrollTop) {
          _self.listInnerScrollTop = scrollTop;
          curEle = $tableView.find('.indexed-list-item').eq(parseInt(scrollTop + 370) / 28);
          _self.appendSearchList($tableView, 'down', curEle);
        } else if (_self.listInnerScrollTop > scrollTop) {
          _self.listInnerScrollTop = scrollTop;
          curEle = $tableView.find('.indexed-list-item').eq(parseInt(scrollTop / 28));
          _self.appendSearchList($tableView, 'up', curEle);
        }
      });

      $tableView.off('change').on('change', 'input', function (event) {
        var $this = $(this);
        var node = $this.closest('li').data('node');
        if (!node) {
          return;
        }
        var checked = $this.prop('checked');
        if (checked) {
          _self.newAddSelectedItem(node);
        } else {
          _self.removeSelectedItem(node);
        }
      });
    },

    getTreeNodeData: function (treeNode, cb) {
      var _self = this;
      var t = $('#UnitType li.active').attr('type');
      var orgVersionId = $('#ID_OrgType').val();
      var params = {
        isNeedUser: '1', // 是否展示用户
        orgVersionId: orgVersionId, // 指定对应的版本
        isInMyUnit: _self.options.isInMyUnit,
        eleIdPath: _self.options.eleIdPath,
        sort: 'code'
      };
      params.treeNodeId = treeNode.id;
      params.otherParams = _self.options.otherParams;
      params.excludeValues = _self.options.excludeValues;
      if ($.inArray(t, ['MyUnit', 'MyCompany', 'DisablePersons']) > -1) {
        params.unitId = _self.options.unitId;
      }

      if ('AllUnits' == t) {
        params.orgVersionId = treeNode.idPath.split('/')[0];
      }

      $.ajax({
        url: ctx + '/api/org/tree/dialog/allUserSearch',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify({
          type: t,
          params: params
        }),
        async: false,
        success: function (result) {
          cb && cb(result.data);
        }
      });
    },
    searchData: function () {
      var _self = this;
      var t = $('#UnitType li.active').attr('type');
      var orgVersionId = $('#ID_OrgType').data('wellSelect') ? $('#ID_OrgType').data('wellSelect').val() : '';
      var viewType = $('#ID_SwitchView').hasClass('icon-ptkj-zuzhijiagoufenjishitu') ? 'tree' : 'user';
      var service = '/api/org/tree/dialog/search';
      $('.indexed-list-bar').hide();
      var params = {
        isNeedUser: _self.options.isNeedUser || '1', // 是否展示用户
        orgVersionId: orgVersionId, // 指定对应的版本
        isInMyUnit: _self.options.isInMyUnit,
        eleIdPath: _self.options.eleIdPath
      };
      if ($.inArray(t, ['MyUnit', 'MyCompany']) > -1) {
        params.unitId = _self.options.unitId;
      }
      params.otherParams = _self.options.otherParams;
      params.excludeValues = _self.options.excludeValues;
      params.keyword = $('#KeyWords').val();
      if (viewType === 'user') {
        params.isNeedUser = '2';
        service = '/api/org/tree/dialog/allUserSearch';
        $('.indexed-list-bar').show();
      }
      var startTime = new Date().getTime();
      $.ajax({
        type: 'POST',
        url: ctx + service,
        async: false,
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify({
          type: t,
          params: params
        }),
        success: function (result) {
          var endTime = new Date().getTime();
          console.log('加载数据消耗' + (endTime - startTime) + 'ms');
          _self.renderSearchList(result.data, viewType);
        }
      });
    },
    searchTaskUsersData: function () {
      var _self = this;
      var t = $('#UnitType li.active').attr('type');
      var orgVersionId = $('#ID_OrgType').data('wellSelect') ? $('#ID_OrgType').data('wellSelect').val() : '';
      var viewType = $('#ID_SwitchView').hasClass('icon-ptkj-zuzhijiagoufenjishitu') ? 'tree' : 'user';
      var service = '/api/org/tree/dialog/search';
      $('.indexed-list-bar').hide();
      if (viewType === 'user') {
        $('.indexed-list-bar').show();
      }
      var ztree = UnitTree.getZtree();
      if (ztree == null) {
        return;
      }
      var keyword = $.trim($('#KeyWords').val());
      var searchNodes = ztree.getNodesByFilter(function (node) {
        if (node.name && node.name.indexOf(keyword) > -1) {
          return true;
        }
        if (node.namePy && node.namePy.indexOf(keyword) > -1) {
          return true;
        }
        return false;
      });
      var dataList = [];
      $.each(searchNodes, function (index, node) {
        if (node.type == 'U') {
          dataList.push({
            iconSkin: node.iconSkin,
            id: node.id,
            idPath: node.idPath,
            name: node.name,
            namePath: node.dataListJobName,
            namePy: node.namePy,
            type: node.type
          });
        }
      });
      if (viewType === 'user') {
        _self.renderSearchList({
            nodes: dataList,
            py: ['A', 'B', 'C', 'D', 'E', 'H', 'J', 'K', 'L', 'O', 'P', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z', '#']
          },
          viewType
        );
      } else {
        _self.renderSearchList(dataList, viewType);
      }
      // 流程办理人用户列表隐藏展开、折叠按钮，树形列表显示展开、折叠按钮
      if (UnitTree.options.type == 'TaskUsers' && viewType == 'user') {
        $('#ID_LVIEWPORT').next('.form_operate').show();
        $('#ID_LVIEWPORT').next('.form_operate').find('#ID_LCollapseAll,#ID_LExpandAll').hide();
      } else {
        $('#ID_LVIEWPORT').next('.form_operate').show();
        $('#ID_LVIEWPORT').next('.form_operate').find('#ID_LCollapseAll,#ID_LExpandAll').show();
      }
      $('#ID_LVIEWPORT').next('.form_operate')[searchNodes.length > 0 ? "show" : "hide"]();
      _self.updateLViewHeight();
    },

    initSearchBtnEvent: function (options) {
      var _self = this;
      $('#ID_Search').click(function () {
        var t = $('#UnitType li.active').attr('type');
        //        if (t === 'TaskUsers') {
        //          var ztree = UnitTree.getZtree();
        //          var keyword = $.trim($('#KeyWords').val());
        //          var allNodes = ztree.getNodes();
        //          var searchNodes = ztree.getNodesByParamFuzzy('name', keyword);
        //          ztree.hideNodes(allNodes);
        //          ztree.showNodes(searchNodes);
        //        } else {
        $('#ID_LSelectAll_Box').removeAttr('checked');
        var viewType = $('#ID_SwitchView').hasClass('icon-ptkj-zuzhijiagoufenjishitu') ? 'tree' : 'user';
        if ($.trim($('#KeyWords').val()) || viewType === 'user') {
          if (t === 'TaskUsers') {
            _self.searchTaskUsersData();
          } else {
            _self.searchData();
          }
        } else {
          $('#ID_LTree').show();
          $('#ID_LVIEWPORT').next().show();
          $('#ID_LView').hide();
          _self.updateLViewHeight();
        }
        if (viewType === 'tree') {
          //树形新的数据设置展开次数为0
          _self.expandTimes = 0;
        }
        setTimeout(function () {
          if ($('#ID_OrgType').data('wellSelect')) {
            $('#ID_OrgType').wellSelect('close');
          }
        }, 0);
        //        }
      });

      $('#ID_OrgType').change(function (event) {
        $('#ID_Search').trigger('click');
        var data = $(this).wellSelect('data');
        var _id = data ? data.id : '';
        _self.renderLTreeView(_id);
        _self.loadNewAccount([]);
      });

      $('#KeyWords').keyup(function (event) {
        var code = event.keyCode;
        var $this = $(this);
        var val = $this.val();
        if ($.trim(val)) {
          $this.next().show();
        } else {
          $this.next().hide();
        }
        if (code === 13) {
          // 输入回车键，触发搜索
          $('#ID_Search').trigger('click');
        }
      });
      var $RTree = $('#ID_RTree');
      $('#KeyWords2').keyup(function (event) {
        var code = event.keyCode;
        var $this = $(this);
        var text = $.trim($this.val());
        if (text) {
          $this.next().show();
        } else {
          $this.next().hide();
        }
        if (code === 13) {
          // 输入回车键，触发搜索
          $('#ID_Search2').trigger('click');
        }
      });
      $('#ID_Search2').on('click', function () {
        var keyword = $.trim($('#KeyWords2').val());
        _self.searchRightList(keyword);
        _self.rightScrollTop = 0;
      });
      $('.ID_clear').on('click', function () {
        var $this = $(this);
        $this.hide().prev().val('');
        $this.next().trigger('click');
        _self.rightScrollTop = 0;
      });
    },

    searchRightList: function (keyword) {
      var _self = this;
      var $RTree = $('#ID_RTree .right-tree-list');
      $RTree.empty();
      if (!keyword) {
        $('#ID_RTree').getNiceScroll()[0].show();
        _self.appendRightList($RTree, 'first');
      } else {
        var userIds = [];
        var searchList = $.map(_self.rightListDataStore, function (item) {
          if (item && item.name && item.name.indexOf(keyword) > -1) {
            userIds.push(item.id);
            return item;
          }
        });

        if (userIds.length) {
          JDS.call({
            service: 'multiOrgUserTreeNodeService.gerUserJob',
            data: [userIds],
            async: true,
            success: function (result) {
              $.each(searchList, function (i, item) {
                var dj = result.data[item.id];
                if (dj && dj.iconSkin) {
                  item.iconSkin = dj.iconSkin;
                }
                if (dj && dj.mainJobs.length) {
                  if (dj.mainJobs[0].parent) {
                    item.dj = dj.mainJobs[0].parent.name + '/' + dj.mainJobs[0].name;
                  } else {
                    item.dj = dj.mainJobs[0].name;
                  }
                } else if (dj && dj.otherJobs.length) {
                  if (dj.otherJobs[0].parent) {
                    item.dj = dj.otherJobs[0].parent.name + '/' + dj.otherJobs[0].name;
                  } else {
                    item.dj = dj.otherJobs[0].name;
                  }
                }
              });

              _self.rightListRender(searchList);
            }
          });
        } else {
          $('#ID_RTree').getNiceScroll()[0].hide();
        }
      }
    },
    rightListRender: function (searchList) {
      var _self = this;
      var $ID_RTree = $('#ID_RTree .right-tree-list');
      $.each(searchList, function (i, item) {
        $ID_RTree.append(_self.buildRightListItem(item));
      });
      _self.resizeRTreeScroll();
    },

    getNodeAllPath: function (eleId) {
      var result;
      var orgVersionId = $('#ID_OrgType').data('wellSelect') ? $('#ID_OrgType').wellSelect('data').id : '';
      var result = [];
      $.ajax({
        url: '/api/org/tree/dialog/fullNamePath',
        contentType: 'application/x-www-form-urlencoded',
        type: 'post',
        async: false,
        data: {
          orgVersionId: eleId[0] !== 'U' ? orgVersionId : '',
          eleId: eleId
        },
        success: function (res) {
          result = res.data;
        }
      });
      return result;
    },

    getSmartName: function (ids, labels) {
      if (ids instanceof String) {
        ids = ids.split(',');
      }
      var result = [];
      $.ajax({
        url: '/proxy/api/org/tree/dialog/smartName',
        async: false,
        contentType: 'application/json',
        dataType: 'json',
        type: 'post',
        data: JSON.stringify({
          nodeIds: ids,
          nodeNames: labels,
          nameDisplayMethod: this.options.nameDisplayMethod
        }),
        success: function (res) {
          result = res.data;
        }
      });
      return result;
    },

    updateNodeData: function (data, ids) {
      var labels = [];
      for (var i = 0; i < data.length; i++) {
        labels.push(data[i].name);
      }

      var _smartName = this.getSmartName(ids, labels);
      for (var i = 0; i < data.length; i++) {
        if (_smartName[data[i].id] && _smartName[data[i].id].namePath) {
          data[i].namePath = _smartName[data[i].id].namePath;
        } else {
          data[i].namePath = data[i].name;
          data[i].smartNamePath = data[i].name;
          data[i].shortName = data[i].name;
          continue;
        }
        switch (this.options.nameDisplayMethod) {
          case '0':
            if (data[i].type === 'U') {
              data[i].smartNamePath = _smartName[data[i].id].smartNamePath;
            }
            break;
          case '1':
            data[i].smartNamePath = _smartName[data[i].id].smartNamePath;
            break;
          case '2':
            if (data[i].type === 'U') {
              data[i].smartNamePath = _smartName[data[i].id].smartNamePath;
              data[i].shortName = _smartName[data[i].id].smartNamePath;
            } else {
              data[i].shortName = _smartName[data[i].id].shortName || _smartName[data[i].id].name;
            }
            break;
        }
      }
    },

    getTopLvNodeStatus: function (node) {
      var _self = this;
      var parentNode = node.getParentNode();
      if (node.level > 1) {
        if (parentNode.open) {
          return _self.getTopLvNodeStatus(parentNode);
        }
      } else {
        if (parentNode.open) {
          return true;
        }
        return false;
      }
    },
    // 设置左侧按钮事件
    initLeftBtnEvent: function () {
      var self = this;
      // 全选
      $('#ID_LSelectAll').click(function () {
        self.treeAllChecked = true;
        self.leftListDataStore = [];
        appModal.showMask();
        self.setMaskStatus(true);
        setTimeout(function () {
          var ztree = UnitTree.getZtree();
          var nodes = ztree.transformToArray(ztree.getNodes());
          var showNodes = [];
          $.each(nodes, function (i, node) {
            if ((node.level === 0 || self.getTopLvNodeStatus(node)) && $.inArray(node.type, self.options.selectTypes) > -1) {
              showNodes.push(node);
              ztree.checkNode(node, true, true, true);
            }
          });
          var finalNodeIDs = [];
          var finalNodes = $.map(showNodes, function (item) {
            if (finalNodeIDs.indexOf(item.id) < 0) {
              finalNodeIDs.push(item.id);
              return item;
            }
          });
          $.each(finalNodes, function (i, item) {
            if (self.rightListDataIds.indexOf(item.id) < 0) {
              self.leftListDataStore.push(item);
            }
          });
          var timer = setInterval(function () {
            var checks = ztree.getNodesByFilter(function (item) {
              if ((item.level === 0 || self.getTopLvNodeStatus(item)) && !item.nocheck) {
                return !item.checked;
              }
            });
            if (!checks.length) {
              self.pushAllLeftListToRight();
              appModal.hideMask();
              self.treeAllChecked = false;
              self.setMaskStatus(false);
              clearInterval(timer);
            }
          }, 10);
        }, 10);
      });
      // 不选
      $('#ID_LUnSelectAll').click(function () {
        appModal.showMask();
        self.setMaskStatus(true);
        setTimeout(function () {
          var ztree = UnitTree.getZtree();

          var s = new Date().getTime();
          var nodes = ztree.transformToArray(ztree.getNodes());
          // for (var i=0, l=nodes.length; i < l; i++) {
          //     ztree.checkNode(nodes[i], false, true, true);
          //     var end = new Date().getTime();
          //     console.log('uncheck---'+ i + '---',end - s);
          // }

          ztree.checkAllNodes(false);
          setTimeout(function () {
            for (var i = 0, l = nodes.length; i < l; i++) {
              self.removeRightListDataStoreItem(nodes[i]);
              var end = new Date().getTime();
              console.log('uncheck---' + i + '---', end - s);
            }
            $('#ID_RTree .right-tree-list').empty();
            self.appendRightList($('#ID_RTree .right-tree-list'), 'first');
            self.computeTotalSelectedNum();
          }, 0);

          var timer = setInterval(function () {
            var checks = ztree.getNodesByFilter(function (item) {
              return item.checked;
            });
            if (!checks.length) {
              appModal.hideMask();
              self.setMaskStatus(false);
              clearInterval(timer);
            }
          }, 10);
        }, 10);
      });
      $('#ID_LSelectAll_Box').change(function (event) {
        var ztree = UnitTree.getZtree();
        if (!ztree) {
          appModal.warning('当前无数据,无法全选');
          this.checked = !this.checked;
          return;
        }
        // 办理人用户列表全选、单选处理
        var viewType = $('#ID_SwitchView').hasClass('icon-ptkj-zuzhijiagoufenjishitu') ? 'tree' : 'user';
        if (UnitTree.options.type == 'TaskUsers' && viewType == 'user') {
          var $checkboxs = $('.table-view .table-view-list', '#ID_LView').find("li>input[type='checkbox']");
          if (this.checked) {
            $checkboxs.attr('checked', 'checked');
          } else {
            $checkboxs.removeAttr('checked');
          }
          $checkboxs.trigger('change');
        } else {
          $(this.checked ? '#ID_LSelectAll' : '#ID_LUnSelectAll').trigger('click');
        }
      });

      // 展开
      $('#ID_LExpandAll').click(function () {
        if (UnitTree.getZtree() == null) {
          // 组织树为空，当前树没有数据，无法展开，跳过
          return;
        }
        var unitType = $('#UnitType .active').attr('type');
        if (unitType == 'TaskUsers') {
          appModal.showMask();
          UnitTree.getZtree().expandAll(true);
          appModal.hideMask();
          UnitTree.resizeLTreeScroll();
          return;
        }
        appModal.showMask();
        self.setMaskStatus(true);
        setTimeout(function () {
          var ztree = UnitTree.getZtree();
          var orgID = $('#ID_OrgType').val();
          //根据展开次数判断是否需要请求数据
          if (!self.expandTimes) {
            self.expandTimes = 1;
            self.renderLTreeView(orgID, 1);
          } else {
            ztree.expandAll(true);
          }
          var timer = setInterval(function () {
            var nodes = ztree.getNodesByFilter(function (node) {
              return node.isParent && !node.open;
            });
            if (!nodes.length) {
              appModal.hideMask();
              self.setMaskStatus(false);
              UnitTree.resizeLTreeScroll();
              clearInterval(timer);
            }
          }, 10);
        }, 10);
      });
      // 折叠
      $('#ID_LCollapseAll').click(function () {
        var ztree = UnitTree.getZtree();
        if (ztree == null) {
          // 组织树为空，当前树没有数据，无法折叠，跳过
          return;
        }
        appModal.showMask();
        self.setMaskStatus(true);
        setTimeout(function () {
          ztree.expandAll(false);
          var timer = setInterval(function () {
            var nodes = ztree.getNodesByFilter(function (node) {
              return node.isParent && node.open;
            });
            if (!nodes.length) {
              appModal.hideMask();
              self.setMaskStatus(false);
              clearInterval(timer);
              UnitTree.resizeLTreeScroll();
            }
          }, 10);
        }, 0);
      });
      // 视图切换
      $('#ID_SwitchView').click(function (event) {
        var $this = $(this);
        if ($this.hasClass('icon-ptkj-pingjishitu')) {
          UnitTree.switchView(true);
          $this.removeClass('icon-ptkj-pingjishitu').addClass('icon-ptkj-zuzhijiagoufenjishitu');
        } else {
          $('#ID_MTree').empty();
          UnitTree.switchView(false);
          $this.removeClass('icon-ptkj-zuzhijiagoufenjishitu').addClass('icon-ptkj-pingjishitu');
        }
        var viewType = $('#ID_SwitchView').hasClass('icon-ptkj-zuzhijiagoufenjishitu') ? 'tree' : 'user';
        // 办理人用户列表刷新
        if (UnitTree.options.type == 'TaskUsers' && viewType == 'tree') {
          $('#ID_LVIEWPORT').next('.form_operate').find('#ID_LCollapseAll,#ID_LExpandAll').show();
        }
        $("#ID_MSelectAll_Box").attr("checked", false);
        self.resizeLTreeScroll();
      });
    },
    // 设置中间按钮事件
    initMiddleBtnEvent: function () {
      var _self = this;
      if (UnitTree.options.multiple) {
        // 全选
        $('#ID_MSelectAll').click(function () {
          $('#ID_MTree :checkbox').attr('checked', 'checked');
          _self.rightScrollTop = 0;
          _self.pushAllMidListToRight();
        });
        // 不选
        $('#ID_MUnSelectAll').click(function () {
          $('#ID_MTree :checkbox').removeAttr('checked').trigger('change');

          if (typeof _self.midListDataStore == 'undefined' || _self.midListDataStore == null) {
            return;
          }

          $.each(_self.midListDataStore, function (mIndex, mItem) {
            var rIndex = _.findIndex(_self.rightListDataStore, function (o) {
              return o.id == mItem.id
            })
            if (rIndex > -1) {
              _self.rightListDataStore.splice(rIndex, 1);
              $("#ID_RTree").find("#" + mItem.id).remove();
            }
          });

          _self.computeTotalSelectedNum();
          _self.rightScrollTop = 0;

          var $ID_RTree = $('#ID_RTree .right-tree-list');
          if ($ID_RTree.find('.org-item').length == 0) {
            _self.appendRightList($ID_RTree, "first");
          } else {
            var curEle = $ID_RTree.find('.org-item').last();
            _self.appendRightList($ID_RTree, 'down', curEle);
          }
        });
        $('#ID_MSelectAll_Box').change(function (event) {
          if ($("#ID_MTree").children().attr("class") == 'no-data') {
            appModal.warning('当前无数据,无法全选');
            this.checked = !this.checked;
            return;
          }
          $(this.checked ? '#ID_MSelectAll' : '#ID_MUnSelectAll').trigger('click');
        });
      } else {
        $('#ID_MSelectAll').hide();
        $('#ID_MUnSelectAll').hide();
        $('#ID_MSelectAll_Box').parent('label').hide();
      }
      // 添加选择
      $('button.gt').click(function () {
        var ztree = UnitTree.getZtree();
        var checkedNodes = ztree.getCheckedNodes();
        for (var i = 0; i < checkedNodes.length; i++) {
          var treeNode = checkedNodes[i];
          UnitTree.addSelectedItem(treeNode);
        }
        // 更新拼音选中
        UnitTree.rendererLView();
      });
      // 删除选择
      $('button.lt').click(function () {
        $('#ID_RDeleteSelected').trigger('click');
      });
      // 添加所有
      $('button.gtgt').click(function () {
        var ztree = UnitTree.getZtree();
        ztree.checkAllNodes(true);
        $('button.gt').trigger('click');
      });
      // 清空所有
      $('button.ltlt').click(function () {
        $('#ID_REmptySelected').trigger('click');
      });
    },
    // 设置右侧按钮事件
    initRightBtnEvent: function () {
      var _self = this;
      // 删除按钮
      $('#ID_RDeleteSelected').click(function () {
        $('#ID_RTree')
          .find(':checked')
          .each(function () {
            var ztree = UnitTree.getZtree();
            // 触发左侧树节点的取消选中事件
            var id = $(this).attr('id');
            var nodes = ztree.getNodesByParam('id', id);
            if (nodes.length == 0) {
              // 右侧的移除选择项
              UnitTree.removeSelectedItem({
                id: id
              });
              return;
            }
            for (var i = 0; i < nodes.length; i++) {
              ztree.checkNode(nodes[i], false, false, true);
              // 如果nodes[i] 从未被展开过，就算ztree.checkNode设置了触发
              // oncheck的方法，也不生效
              // 所以必须再次删除下
              UnitTree.removeSelectedItem(nodes[i]);
            }
          });
        UnitTree.rendererLView();
      });

      $('#ID_RTree').on('click', '.org-item', function () {
        $(this).addClass('active').siblings().removeClass('active');
      });

      // hover删除
      $('#ID_RTree').on('click', '.org-item>.dismiss,.org-item>.btn-del', function (event) {
        var id = $(this).parent().attr('id');
        // 右侧的移除选择项
        UnitTree.removeSelectedItem({
          id: id
        });
        $('#ID_MSelectAll_Box').removeAttr('checked');

        var $ID_RTree = $('#ID_RTree .right-tree-list');
        if ($ID_RTree.find('.org-item').length <= 14) {
          var curEle = $ID_RTree.find('.org-item').last();
          _self.appendRightList($ID_RTree, 'down', curEle);
        }
      });
      // 清空按钮
      $('#ID_REmptySelected').click(function () {
        UnitTree.treeAllChecked = false;
        UnitTree.setRightListDataStore(null, 'clear');
        UnitTree.clearSelectedItem();
        // UnitTree.rendererLView();
        _self.rightScrollTop = 0;
      });
      // 上移按钮
      $('#ID_RMoveUpSelected').click(function () {
        var $orgItems = $('#ID_RTree .org-item');
        var $activeOrgItem = $('#ID_RTree .org-item.active');

        var originalIdx = $orgItems.index($('#ID_RTree .org-item.active'));
        if (originalIdx) {
          UnitTree.setRightListDataStore([originalIdx - 1, originalIdx], 'switch');
        }

        var $activeOrgItem = $('#ID_RTree .org-item.active');
        if ($activeOrgItem.prev().length) {
          $activeOrgItem.insertBefore($activeOrgItem.prev());
        }
      });
      // 下移按钮
      $('#ID_RMoveDownSelected').click(function () {
        var $orgItems = $('#ID_RTree .org-item');
        var $activeOrgItem = $('#ID_RTree .org-item.active');

        var originalIdx = $orgItems.index($activeOrgItem);
        if (originalIdx <= $orgItems.length - 1) {
          UnitTree.setRightListDataStore([originalIdx, originalIdx + 1], 'switch');
        }

        if ($activeOrgItem.next().length) {
          $activeOrgItem.insertAfter($activeOrgItem.next());
        }
      });
    },

    // 添加选中的选项
    addSelectedItem: function (treeNode) {
      // console.log("添加选择项");
      if (this.options.multiple == false) {
        // 单选模式，需要先清掉所有数据
        // $("#ID_MTree").empty();
        $('#ID_RTree .right-tree-list').empty();
      }
      var id = treeNode.id;
      // 同步勾选中中间的选项
      if ('U' == treeNode.type) {
        $('#ID_MTree #' + id).attr('checked', 'checked');
      }
      var ldx,
        nodeName = treeNode.namePath || treeNode.name;
      if (this.options.nameFormat === 'justName') {
        if (typeof nodeName === 'string' && nodeName.indexOf('/')) {
          nodeName = nodeName.substr(nodeName.lastIndexOf('/') + 1);
        }
      } else if ('U' === treeNode.type) {
        if ((ldx = nodeName.lastIndexOf('/')) > 0) {
          nodeName = nodeName.substr(ldx + 1);
        }
        // treeNode.name = nodeName;
      } else if ((ldx = nodeName.indexOf('/')) > 0) {
        nodeName = nodeName.substr(ldx + 1);
      }
      // 右侧添加选中项
      var newHtml =
        "<div class='org-item' title='" +
        nodeName +
        "'><input id='" +
        id +
        "' type='checkbox'><i class='icon " +
        treeNode.type +
        ' ' +
        ((treeNode.data && treeNode.data.remark) || '') +
        "'></i><label for='RID-" +
        id +
        "'>" +
        nodeName +
        '</label>';
      newHtml += '<label class="node-dj" for="RID-' + id + '">';
      if (treeNode.mainDepartmentName && treeNode.mainJobName) {
        newHtml += treeNode.mainDepartmentName + '/' + treeNode.mainJobName;
      } else if (treeNode.mainDepartmentName) {
        newHtml += treeNode.mainDepartmentName;
      } else if (treeNode.mainJobName) {
        newHtml += treeNode.mainJobName;
      }
      newHtml += '</label>';
      newHtml += "<span class='btn-del iconfont icon-ptkj-dacha'></span></div>";
      // 检查是否有重复的，如果没有则追加
      var objs = $("#ID_RTree input[id='" + id + "']");
      if (objs.length == 0) {
        var $last = $('#ID_RTree')
          .find('i.' + treeNode.type + ':last')
          .closest('.org-item');
        if ($last.length > 0) {
          $last.after(newHtml);
        } else {
          $('#ID_RTree').append(newHtml);
        }
        $("#ID_RTree input[id='" + id + "']").data('treeNode', treeNode);
      }

      // 重新计算已选中的总数量
      UnitTree.computeTotalSelectedNum();
      UnitTree.resizeRTreeScroll();
    },

    newAddSelectedItem: function (node) {
      var _self = this;
      if (!this.options.multiple) {
        // 单选模式，需要先清掉所有数据
        $('#ID_RTree .right-tree-list').empty();
        _self.setRightListDataStore(null, 'clear');
      }
      var ztree = _self.getZtree();
      var treeNodes = ztree.getNodesByParam('id', node.id, null);
      $.each(treeNodes, function (i, item) {
        if (node.getParentNode) {
          if (JSON.stringify(item.getParentNode()) === JSON.stringify(node.getParentNode())) {
            ztree.checkNode(item, true, true, false);
            return false;
          }
        } else if (node.extValues && node.extValues.JobId) {
          var parentNode = item.getParentNode();
          if (parentNode && parentNode.type === 'J' && parentNode.id === node.extValues.JobId && node.id === item.id) {
            ztree.checkNode(item, true, true, false);
            return false;
          }
        } else {
          if (node.id === item.id) {
            ztree.checkNode(item, true, true, false);
          }
        }
      });
      // 同步勾选中中间的选项
      if ('U' === node.type) {
        $('#ID_MTree #' + node.id).attr('checked', 'checked');
      }

      if ($('#ID_RTree #' + node.id).length) {
        return;
      }

      var ID_OrgType_data = $('#ID_OrgType').wellSelect('data');
      var orgVersionId = ID_OrgType_data ? ID_OrgType_data.id : '';
      node.orgVersionId = orgVersionId;
      if (node.type === 'U' || node.id[0] === 'U') {
        _self.getUserJobAndRightList(node);
      } else {
        _self.insertRightListLast(node);
        _self.computeTotalSelectedNum();
      }
    },

    insertRightListLast: function (node) {
      var _self = this;
      var $RTree = $('#ID_RTree .right-tree-list');
      var RTreeLen = $RTree.find('.org-item').length;
      if (RTreeLen < _self.rightListDataStore.length) {
        var rIndex = _.findIndex(_self.rightListDataStore, function (o) {
          return o.id == node.id
        })
        if (!$('#ID_RTree #' + node.id).length && rIndex == -1) {
          _self.rightListDataStore.push(node);
        }
        var list = _self.rightListDataStore.slice(RTreeLen, 20);
        _self.insetRightList($RTree, list);
      } else {
        _self.insetRightList($('#ID_RTree .right-tree-list'), [node], 'down');
      }
    },

    getUserJobAndRightList: function (node) {
      var _self = this;
      if (node.dj) {
        node.extValues = _self.setNodeExtValues(node);
        // _self.insetRightList($('#ID_RTree .right-tree-list'), [node], 'down');
        _self.insertRightListLast(node);
        _self.computeTotalSelectedNum();
      } else if (node.mainJobs || node.otherJobs) {
        node.extValues = _self.setNodeExtValues(node);
        node.dj = node.extValues.dj;
        // _self.insetRightList($('#ID_RTree .right-tree-list'), [node], 'down');
        _self.insertRightListLast(node);
        _self.computeTotalSelectedNum();
      } else if (node.id[0] !== 'U') {
        // _self.insetRightList($('#ID_RTree .right-tree-list'), [node], 'down');
        _self.insertRightListLast(node);
        _self.computeTotalSelectedNum();
      } else {
        JDS.call({
          service: 'multiOrgUserTreeNodeService.gerUserJob',
          data: [
            [node.id]
          ],
          async: true,
          success: function (result) {
            var dj = result.data[node.id];
            if (dj && dj.iconSkin) {
              node.iconSkin = dj.iconSkin;
            }
            node.mainJobs = dj.mainJobs;
            node.otherJobs = dj.otherJobs;
            node.extValues = _self.setNodeExtValues(node);
            node.dj = node.extValues.dj;
            // _self.insetRightList($('#ID_RTree .right-tree-list'), [node], 'down');
            _self.insertRightListLast(node);
            _self.computeTotalSelectedNum();
          }
        });
      }
    },

    // 移除选中的选择项
    removeSelectedItem: function (treeNode) {
      var id = treeNode.id;
      // 如果nodes[i] 从未被展开过，就算ztree.checkNode设置了触发 oncheck的方法，也不生效

      //左侧的取消勾选状态
      $('.table-view .indexed-list-item input[data-id="' + id + '"]').removeAttr('checked');
      // 中间的取消勾选状态
      $("#ID_MTree input[id='" + id + "']").removeAttr('checked');
      if ($("#ID_MTree input[id='" + id + "']").length) {
        $('#ID_MSelectAll_Box').removeAttr('checked');
      }
      // 右侧的移除选择项
      $("#ID_RTree .org-item[data-id='" + id + "']").remove();

      UnitTree.removeRightListDataStoreItem(treeNode);
      // 重新计算已选中的总数量
      UnitTree.computeTotalSelectedNum();

      // 因为同一个节点可以挂在不同的树下面，所以取消的时候，相同节点的需要全部取消
      var ztree = this.getZtree();
      var checkeds = ztree.getCheckedNodes(true);
      $.each(checkeds, function (i) {
        var node = checkeds[i];
        if (node.id == id) {
          // 这里 callbackFlag,需要设置成false,不需要重复触发  removeSelectedItem 这个方法
          ztree.checkNode(node, false, false, false);
        }
      });

      UnitTree.resizeRTreeScroll();
    },
    // 清空所有的选项
    clearSelectedItem: function () {
      var ztree = this.getZtree();
      var selectedItemList = $('#ID_RTree .org-item');
      if (selectedItemList.length == 1 && ztree) {
        var id = $(selectedItemList[0]).attr('id');
        var nodes = ztree.getNodesByParam('id', id);
        // 因为邮件账号不在于组织树中的情况，所以需要判断节点是否存在
        if (nodes && nodes.length > 0) {
          ztree.checkNode(nodes[0], false, false, true);
          // 如果nodes[i] 从未被展开过，就算ztree.checkNode设置了触发 oncheck的方法，也不生效
          // 所以必须再次删除下
          UnitTree.removeSelectedItem(nodes[0]);
        }
      } else if (selectedItemList.length > 1 && ztree) {
        // checkAllNodes 方法只对checkbox有效，对radio无效
        // ,所以必须当length=1的时候，用checkNode来取消radio的情况
        ztree.checkAllNodes(false);
        $('#ID_LSelectAll_Box').removeAttr('checked');
      }
      $('#ID_RTree .right-tree-list').empty();
      //取消左侧列表选中项
      $('.table-view .indexed-list-item input').removeAttr('checked');
      // 同步取消中间
      $('#ID_MTree :checked').removeAttr('checked');
      $('#ID_MSelectAll_Box').removeAttr('checked');

      // 重新计算已选中的总数量
      UnitTree.computeTotalSelectedNum();
      UnitTree.resizeRTreeScroll();
    },
    // 获取选中的数据
    getSelectedData: function () {
      return this.rightListDataStore;
    },
    getSelectedDataById: function (id) {
      var result = null;
      $.each(UnitTree.rightListDataStore, function (i, item) {
        if (item.id === id) {
          result = item;
          return false;
        }
      });
      return result;
    },
    // 初始化选中的值，不同类型，数据的初始化情况不太一样
    initData: function (data, options) {
      if (data) {
        var _dataIdArr = data.map(function (item) {
          return item.id;
        });
        this.updateNodeData(data, _dataIdArr);
        this.setRightListDataStore(data, 'concat');
        this.computeTotalSelectedNum();
        this.appendRightList($('#ID_RTree .right-tree-list'), 'first');
      }
    },
    // 获取左侧树对象
    getZtree: function () {
      return $.fn.zTree.getZTreeObj('ID_LTree');
    },
    emptyMtree: function () {
      $('#ID_DMemberCount').html(0);
      $('#ID_MTree').html('<li class="no-data">暂无预览</li>');
    },
    // 从客户端组织树算出用户数据
    loadAccount: function (treeId, treeNode) {
      var ztree = $.fn.zTree.getZTreeObj(treeId);
      var objs = ztree.getNodesByParamFuzzy('path', treeNode.path + '/');
      // 先清空，然后重新加入
      var dRememberCount = 0;
      $('#ID_MTree').empty();
      var inputType = this.options.multiple ? 'checkbox' : 'radio';
      for (var i = 0; i < objs.length; i++) {
        var obj = objs[i];
        var id = obj.id;
        var name = obj.name;
        if (obj.data && obj.data.type == 'U') {
          if ($('#ID_MTree #' + id).length > 0) {
            // 已经存在了，就不需要再显示了，去重操作
          } else {
            dRememberCount++;
            var newHtml =
              "<li class='middle-item'><input name='option_user' id='" +
              id +
              "' type='" +
              inputType +
              "'><label for='" +
              id +
              "' class='chkbox iconfont'></label><i class='icon U " +
              (obj.data.remark || '') +
              "'></i><label for='" +
              id +
              "'>" +
              name +
              '</label></li>';
            $('#ID_MTree').append(newHtml);
            $('#ID_MTree #' + id).data('treeNode', obj);
          }
        }
      }
      if (dRememberCount > 0) {
        $('#ID_DMemberCount').html(dRememberCount);
      } else {
        UnitTree.emptyMtree();
      }
      $('#ID_MTree').getNiceScroll().resize();
      // 检查勾选状态
      $('#ID_RTree :checkbox').each(function () {
        var id = $(this).attr('id');
        $('#ID_MTree #' + id).attr('checked', 'checked');
      });

      // 因为有可能是checkbox也有可能是radio，所以这里采用input来统一控制
      $('#ID_MTree :input').each(function () {
        $(this).change(function (event) {
          var treeNode = $(this).data('treeNode');
          // 因为存在同一个账号挂在不同的职位下的情况，所以选中的话，默认当前展示框的节点被选中，
          // 取消的话，是所有相同节点都取消
          console.log(111)
          if ($(this).attr('checked')) {
            // 触发左侧树节点的选中事件
            ztree.checkNode(treeNode, true, false, true);
          } else {
            // 触发左侧树节点的取消选中事件
            var id = treeNode.id;
            var nodes = ztree.getNodesByParam('id', id);
            for (var i = 0; i < nodes.length; i++) {
              //先置为已选再取消选择才会触发oncheck事件
              nodes[i].checked = true;
              ztree.checkNode(nodes[i], false, false, true);
            }
          }
        });
      });
    },
    lazyRenderMidList: function (list) {
      var _self = this;
      var $ID_MTree = $('#ID_MTree');
      _self.midListDataStore = list;
      _self.appendMidListStatus = false;
      _self.appendMidList($ID_MTree, 'first');
      $ID_MTree.scroll(function () {
        var scrollTop = $(this).scrollTop();
        if (!_self.midScrollTop) {
          _self.midScrollTop = 0;
        }
        var curEle;
        if (_self.midScrollTop < scrollTop) {
          _self.midScrollTop = scrollTop;
          curEle = $ID_MTree.find('li').eq(parseInt((scrollTop + 380) / 28));
          _self.appendMidList($ID_MTree, 'down', curEle);
        } else if (_self.midScrollTop > scrollTop) {
          _self.midScrollTop = scrollTop;
          curEle = $ID_MTree.find('li').eq(parseInt(scrollTop / 28));
          _self.appendMidList($ID_MTree, 'up', curEle);
        }
      });

      $ID_MTree.off('change').on('change', 'input', function (event) {
        var $this = $(this);
        var node = $this.closest('li').data('node');
        if (!node) {
          return;
        }
        var checked = $this.prop('checked');
        if (checked) {
          _self.newAddSelectedItem(node);
          console.log(1)
        } else {
          _self.removeSelectedItem(node);
        }
      });
    },
    getMidListIndex: function (id) {
      var _self = this;
      var midListDataStore = _self.midListDataStore;
      var firstIndex;
      $.each(midListDataStore, function (i, item) {
        if (item.id === id) {
          firstIndex = i;
          return false;
        }
      });
      return firstIndex;
    },
    appendMidList: function (ele, type, curEle) {
      var _self = this;
      _self.appendMidListStatus = true;

      var data;
      if (type === 'first') {
        data = _self.midListDataStore.slice(0, 30);
      } else {
        var _index;
        if (type === 'up') {
          _index = _self.getMidListIndex(curEle.attr('data-id'));
          data = _self.midListDataStore.slice(_index > 50 ? _index - 50 : 0, _index).reverse();
        } else if (type === 'down') {
          _index = _self.getMidListIndex(curEle.attr('data-id'));
          data = _self.midListDataStore.slice(_index, _index + 50);
        }
      }

      var userList = $.map(data, function (item) {
        return item.id;
      });

      if (userList.length) {
        JDS.call({
          service: 'multiOrgUserTreeNodeService.gerUserJob',
          data: [userList],
          async: true,
          success: function (result) {
            $.each(data, function (i, item) {
              if (item.iconSkin == 0) {
                item.iconSkin = 'women';
              } else if (item.iconSkin == 1) {
                item.iconSkin = 'man';
              }
              var dj = result.data[item.id];
              if (dj && dj.iconSkin) {
                item.iconSkin = dj.iconSkin;
              }
              if (dj && dj.mainJobs.length) {
                if (dj.mainJobs[0].parent) {
                  item.dj = dj.mainJobs[0].parent.name + '/' + dj.mainJobs[0].name;
                } else {
                  item.dj = dj.mainJobs[0].name;
                }
              } else if (dj && dj.otherJobs.length) {
                if (dj.otherJobs[0].parent) {
                  item.dj = dj.otherJobs[0].parent.name + '/' + dj.otherJobs[0].name;
                } else {
                  item.dj = dj.otherJobs[0].name;
                }
              }
            });
            _self.insetMidList(ele, data, type);
          }
        });
      }
    },
    insetMidList: function (ele, data, type) {
      var _self = this;
      var start = new Date().getTime();
      $.each(data, function (i, item) {
        if ($('#ID_MTree #' + item.id).length > 0) {
          return true;
        }
        var mountLi = _self.buildMidListItem(item);
        if (type === 'up') {
          ele.find('[data-index="' + (item._index + 1) + '"]').before(mountLi);
        } else if (type === 'down') {
          ele.find('[data-index="' + (item._index - 1) + '"]').after(mountLi);
        } else {
          ele.append(mountLi);
        }
      });
      var end = new Date().getTime();
      console.log('插入中间列表数据消耗' + (end - start) + 'ms');
      _self.appendMidListStatus = false;
    },
    buildMidListItem: function (item) {
      var _self = this;
      var inputType = this.options.multiple ? 'checkbox' : 'radio';
      var $li = $('<li></li>', {
        'data-id': item.id,
        'data-index': _self.getMidListIndex(item.id)
      });
      $li.append(
        "<input name='option_user' id='" +
        item.id +
        "' type='" +
        inputType +
        "'>" +
        "<label for='" +
        item.id +
        "' class='chkbox iconfont'></label>" +
        "<i class='icon U " +
        item.iconSkin +
        "'></i>" +
        "<label for='" +
        item.id +
        "'>" +
        item.name +
        '</label>'
      );
      $li.data('node', item);
      $.each(_self.rightListDataStore, function (i, node) {
        if (node.id === item.id) {
          $li.find('input').attr('checked', true);
          return false;
        }
      });
      return $li;
    },

    lazyRenderRightList: function (list) {
      var _self = this;
      var $ID_RTree = $('#ID_RTree .right-tree-list');
      _self.rightListDataStore = list || [];
      _self.rightListDataIds = $.map(_self.rightListDataStore, function (item) {
        return item.id;
      });
      _self.appendRightListStatus = false;
      _self.rightScrollTop = 0;
      _self.appendRightList($ID_RTree, 'first');
      $('#ID_RTree').scroll(function () {
        var scrollTop = $(this).scrollTop();
        var curEle;
        if (_self.rightScrollTop < scrollTop) {
          _self.rightScrollTop = scrollTop;
          curEle = $ID_RTree.find('.org-item').eq(parseInt((scrollTop + 380) / 28));
          _self.appendRightList($ID_RTree, 'down', curEle);
        }
      });
    },
    getRightListIndex: function (id) {
      var _self = this;
      var rightListDataStore = _self.rightListDataStore;
      var firstIndex;
      $.each(rightListDataStore, function (i, item) {
        if (item.id === id) {
          firstIndex = i;
          return false;
        }
      });
      return firstIndex;
    },
    appendRightList: function (ele, type, curEle) {
      var _self = this;
      _self.appendRightListStatus = true;

      var data;
      if (type === 'first') {
        data = _self.rightListDataStore.slice(0, 30);
      } else {
        var _index;
        if (type === 'up') {
          _index = _self.getRightListIndex(curEle.attr('data-id'));
          data = _self.rightListDataStore.slice(_index > 50 ? _index - 50 : 0, _index).reverse();
        } else if (type === 'down') {
          _index = _self.getRightListIndex(curEle.attr('data-id'));
          data = _self.rightListDataStore.slice(_index, _index + 50);
        }
      }

      var userList = $.map(data, function (item) {
        if (item.id[0] === 'U' || item.type === 'U') {
          return item.id;
        }
      });

      if (userList.length) {
        JDS.call({
          service: 'multiOrgUserTreeNodeService.gerUserJob',
          data: [userList],
          async: true,
          success: function (result) {
            $.each(data, function (i, item) {
              item.type = item.type || item.id[0];
              if (item.type !== 'U') {
                return true;
              }
              if (item.extValues && item.extValues.JobName) {
                // item.dj = item.extValues.JobName.split('-').slice(-2).join('-');
                item.dj = item.extValues.JobName;
              } else {
                var dj = result.data[item.id];
                if (dj && dj.iconSkin) {
                  item.iconSkin = dj.iconSkin;
                }
                if (dj && dj.mainJobs.length) {
                  if (dj.mainJobs[0].parent) {
                    item.dj = dj.mainJobs[0].parent.name + '/' + dj.mainJobs[0].name;
                  } else {
                    item.dj = dj.mainJobs[0].name;
                  }
                } else if (dj && dj.otherJobs.length) {
                  if (dj.otherJobs[0].parent) {
                    item.dj = dj.otherJobs[0].parent.name + '/' + dj.otherJobs[0].name;
                  } else {
                    item.dj = dj.otherJobs[0].name;
                  }
                }
              }
            });
            _self.insetRightList(ele, data, type);
          }
        });
      } else {
        _self.insetRightList(ele, data, type);
      }
    },
    insetRightList: function (ele, data, type) {
      var _self = this;
      var initStatus = _self.rightListDataStore.length ? true : false;
      $.each(data, function (i, item) {
        if ($('#ID_RTree #' + item.id).length > 0) {
          return true;
        }
        if (type !== 'first' || (type === 'first' && !initStatus)) {
          var hasNode = $.map(_self.rightListDataStore, function (node) {
            if (node.id === item.id) {
              return node;
            }
          });
          if (!hasNode.length) {
            _self.setRightListDataStore(item, 'push');
          }
        }
        // if (!$(".right-tree-list").find(".org-item").length < 0 && $(".right-tree-list").find(".org-item").length < _self.rightListDataStore.length - 1) {
        var mountLi = _self.buildRightListItem(item);
        ele.append(mountLi);
        // }
      });

      _self.computeTotalSelectedNum();
      _self.appendRightListStatus = false;
      setTimeout(function () {
        _self.resizeRTreeScroll();
      }, 100)
    },
    //todo
    buildRightListItem: function (item) {
      var _self = this;
      var nodeName = item.name;
      var nodeDj;
      var nodeTitle;
      if (item.type && item.type !== 'U') {
        switch (_self.options.nameDisplayMethod) {
          case '0':
            if (item.type !== 'U') {
              var nodeNameArr = nodeName.split('/');
              if (nodeNameArr.length) {
                nodeName = nodeNameArr[nodeNameArr.length - 1];
              }
            }
            break;
          case '1':
            nodeName = item.smartNamePath || item.allPath || item.namePath || item.name;
            break;
          case '2':
            nodeName = item.shortName || item.realName || item.name;
            break;
        }
      } else {
        nodeDj = item.smartNamePath || item.allPath || item.namePath || item.dj;
        nodeTitle = item.namePath || nodeDj;
      }

      var _title = item.type === 'U' || item.id[0] === 'U' ? item.name : item.realNamePath || item.allPath || item.namePath || item.name;
      var $div = $(
        '<div id="' +
        item.id +
        '" class="org-item" title="' +
        _title +
        '" data-index="' +
        _self.getRightListIndex(item.id) +
        '" data-id="' +
        item.id +
        '"></div>'
      );
      item.type = item.type || item.id[0];
      // 同步勾选中中间的选项
      if ('U' === item.type) {
        if (item.name.indexOf('/') > -1) {
          var nameArr = item.name.split('/');
          item.name = nameArr[nameArr.length - 1];
        }
        $('#ID_MTree #' + item.id).attr('checked', 'checked');
      }
      // 右侧添加选中项
      $div.append(
        "<i class='icon U " +
        (item.iconSkin || item.type) +
        "'></i>" +
        "<label for='RID-" +
        item.id +
        "' style='" +
        (item.type !== 'U' ? 'width: auto' : '') +
        "'>" +
        nodeName +
        '</label>' +
        (item.type === 'U' && item.id[0] === 'U' ?
          "<label class='node-dj' title='" + nodeTitle + "' for='RID-" + item.id + "'>" + nodeDj + '</label>' :
          '') +
        "<span class='btn-del iconfont icon-ptkj-dacha'></span>"
      );
      $div.data('node', item);
      return $div;
    },

    //更新右侧列表图标
    updateRightListItemIcon: function (item) {
      $('#ID_RTree #' + item.id)
        .find('.icon')
        .addClass(item.iconSkin);
    },

    // 设置人员扩展信息
    setNodeExtValues: function (data) {
      if ($.isArray(data)) {
        if (data.length == 0) {
          return;
        }
        if (data.length > 1) {
          return;
        } else {
          data = data[0];
        }
      }
      if (!$.isEmptyObject(data.extValues)) {
        data.dj = data.extValues.dj || data.extValues.JobName;
        return data.extValues;
      }
      var extValues = data.extValues || (data.extValues = {});
      if (data.type === 'U' || data.id[0] === 'U') {
        var _pid = null;
        var targetJob = null;
        if (data.parentTId) {
          //通过树形层级逐级选择的数据
          var parentNode = data.getParentNode();
          _pid = parentNode.id;
          var jobfield = ['mainJobs', 'otherJobs'];
          for (var j = 0, jlen = jobfield.length; j < jlen; j++) {
            var item = data[jobfield[j]];
            if (item && item.length) {
              for (var k = 0; k < item.length; k++) {
                var temp = item[k];
                while (temp) {
                  if (temp.id === _pid) {
                    targetJob = item[k];
                    break;
                  }
                  temp = temp.parent;
                }
              }
              if (targetJob != null) {
                break;
              }
            }
          }
        }

        //上述层级并不能解析到职位的情况下
        if (targetJob == null) {
          // 非树形节点选择的数据
          if (data.mainJobs && data.mainJobs.length) {
            targetJob = data.mainJobs[0];
          } else if (data.otherJobs && data.otherJobs.length) {
            targetJob = data.otherJobs[0];
          }
        }
        if (targetJob) {
          extValues.fromTypeText = $('#UnitType>li.active').text();
          extValues.fromTypeId = $('#UnitType>li.active').attr('type');
          var jobParentNames = [];
          var jobParentIds = [];
          var idmap = {
            D: 'DeptId',
            B: 'UnitId',
            O: 'OrgElementId',
            J: 'JobId'
          };
          var namemap = {
            D: 'DeptName',
            B: 'UnitName',
            O: 'OrgElementName',
            J: 'JobName'
          };
          extValues.JobId = targetJob.id;
          jobParentIds.push(targetJob.id);
          jobParentNames.push(targetJob.name);
          var _p = targetJob.parent;
          while (_p != null) {
            if (_p.id[0] === 'V') {
              // 组织版本
              extValues.VersionName = _p.name;
              extValues.VersionId = _p.id;
            }
            if (idmap[_p.id[0]]) {
              extValues[idmap[_p.id[0]]] = _p.id;
              jobParentIds.push(_p.id);
              jobParentNames.push(_p.name);
              _p = _p.parent;
              continue;
            }
            break;
          }
          for (var i in idmap) {
            var index = jobParentIds.indexOf(extValues[idmap[i]]);
            if (index !== -1) {
              extValues[namemap[i]] = jobParentNames.slice(index).reverse().join('/');
            }
          }
          // if (extValues.JobName.indexOf('/') > 1) {
          //   // extValues.dj = extValues.JobName.split('-').slice(-2).join('-');
          //   extValues.dj = extValues.JobName;
          // } else {
          //   extValues.dj = extValues.JobName = extValues.VersionName + '/' + extValues.JobName;
          // }
          if (jobParentIds.indexOf(extValues.VersionId) < 0) {
            extValues.dj = extValues.JobName = extValues.VersionName + '/' + extValues.JobName;
          } else {
            extValues.dj = extValues.JobName;
          }
          return extValues;
        }

        if (data.idPath) {
          extValues.fromTypeText = $('#UnitType>li.active').text();
          extValues.fromTypeId = $('#UnitType>li.active').attr('type');
          var namePath = data.namePath.split('/').reverse();
          var idPath = data.idPath.split('/').reverse();
          $.each(idPath, function (i, item) {
            if (extValues.VersionId) {
              return false;
            }
            switch (item[0]) {
              case 'J':
                extValues.JobId = item;
                extValues.JobName = namePath[i];
                break;
              case 'D':
                if (!extValues.DeptId) {
                  extValues.DeptId = item;
                  extValues.DeptName = namePath[i];
                }
                break;
              case 'B':
                if (!extValues.UnitId) {
                  extValues.UnitId = item;
                  extValues.UnitName = namePath[i];
                }
                break;
              case 'O':
                if (!extValues.OrgElementId) {
                  extValues.OrgElementId = item;
                  extValues.OrgElementName = namePath[i];
                }
                break;
              case 'V':
                extValues.VersionId = item;
                extValues.VersionName = namePath[i];
                break;
            }
          });
          if (extValues.DeptName) {
            extValues.dj = extValues.JobName = extValues.DeptName + '/' + extValues.JobName;
          } else if (extValues.UnitName) {
            extValues.dj = extValues.JobName = extValues.UnitName + '/' + extValues.JobName;
          } else if (extValues.OrgElementName) {
            extValues.dj = extValues.JobName = extValues.OrgElementName + '/' + extValues.JobName;
          } else if (extValues.VersionName) {
            extValues.dj = extValues.JobName = extValues.VersionName + '/' + extValues.JobName;
          }
          return extValues;
        }
      }
    },
    //设置右侧列表数据
    setRightListDataStore: function (data, type) {
      var _self = this;
      _self.rightListDataStore = _self.rightListDataStore || [];
      _self.rightListDataIds = _self.rightListDataIds || [];
      if (type === 'concat') {
        _self.setNodeExtValues(data);
        _self.rightListDataStore = _self.rightListDataStore.concat(data);
        $.each(data, function (i, item) {
          _self.rightListDataIds.push(item.id);
        });
      } else if (type === 'push') {
        _self.setNodeExtValues(data);
        _self.rightListDataStore.push(data);
        _self.rightListDataIds.push(data.id);
      } else if (type === 'clear') {
        _self.rightListDataStore = [];
        _self.rightListDataIds = [];
      } else if (type === 'switch') {
        _self.switchArrayItemByIndex(_self.rightListDataStore, data[0], data[1]);
        _self.switchArrayItemByIndex(_self.rightListDataIds, data[0], data[1]);
      }
    },

    switchArrayItemByIndex: function (arr, idx1, idx2) {
      var temp = arr[idx1];
      arr[idx1] = arr[idx2];
      arr[idx2] = temp;
    },

    pushAllLeftListToRight: function () {
      var _self = this;
      var $RTree = $('#ID_RTree .right-tree-list');
      var RTreeLen = $RTree.find('.org-item').length;
      _self.setRightListDataStore(_self.leftListDataStore, 'concat');

      if (RTreeLen < _self.rightListDataStore.length) {
        _self.appendRightList($RTree, 'first');
      }
      _self.computeTotalSelectedNum();
    },

    pushAllMidListToRight: function () {
      var _self = this;
      var $RTree = $('#ID_RTree .right-tree-list');
      var RTreeLen = $RTree.find('.org-item').length;

      if (typeof _self.midListDataStore == 'undefined' || null == _self.midListDataStore) {
        return;
      }

      $.map(_self.midListDataStore, function (item) {
        var rIndex = _.findIndex(_self.rightListDataStore, function (o) {
          return o.id == item.id
        })
        if (!$('#ID_RTree #' + item.id).length && rIndex == -1) {
          _self.rightListDataStore.push(item);
        }
      });

      if (RTreeLen < _self.rightListDataStore.length) {
        var list = _self.rightListDataStore.slice(RTreeLen, 20);
        _self.insetRightList($RTree, list);
      }
      _self.computeTotalSelectedNum();
    },

    removeRightListDataStoreItem: function (node) {
      var _self = this;
      $.each(_self.rightListDataStore, function (i, item) {
        if (item.id === node.id) {
          _self.rightListDataStore.splice(i, 1);
          _self.rightListDataIds.splice(i, 1);
          return false;
        }
      });
    },
    loadTaskUsersNewAccount: function (treeNode) {
      var userNodes = [];
      var fetchUserNodes = function (treeNode) {
        if (treeNode.type == 'U') {
          userNodes.push({
            id: treeNode.id,
            idPath: treeNode.idPath,
            name: treeNode.name,
            namePath: treeNode.namePath,
            namePy: treeNode.namePy
          });
        }
        var children = treeNode.children || [];
        for (var i = 0; i < children.length; i++) {
          var node = children[i];
          fetchUserNodes(node);
        }
      };
      fetchUserNodes(treeNode, userNodes);
      this.loadNewAccount(userNodes);
    },
    loadNewAccount: function (data) {
      var _self = this;
      // 先清空，然后重新加入
      var dRememberCount = 0;
      $('#ID_MTree').empty();

      var inputType = this.options.multiple ? 'checkbox' : 'radio';
      var midListDataIds = [];
      var midListDatas = [];
      var _index = 0;
      $.each(data, function (i, item) {
        var id = item.id;
        var name = item.name;
        // if (id[0] === 'U') {
        if (midListDataIds.indexOf(id) < 0) {
          midListDataIds.push(id);
          item._index = _index++;
          midListDatas.push(item);
          dRememberCount++;
        }
        // }
      });
      _self.lazyRenderMidList(midListDatas);
      if (dRememberCount > 0) {
        $('#ID_DMemberCount').html(dRememberCount);
      } else {
        UnitTree.emptyMtree();
      }
      // 检查勾选状态
      var checkedNum = 0;
      $('#ID_RTree :checkbox').each(function () {
        var id = $(this).attr('id');
        if ($('#ID_MTree #' + id).length) {
          checkedNum++;
        }
        $('#ID_MTree #' + id).attr('checked', 'checked');
      });

      if (dRememberCount && dRememberCount === checkedNum) {
        $('#ID_MSelectAll_Box').attr('checked', 'checked');
      } else {
        $('#ID_MSelectAll_Box').removeAttr('checked');
      }

      // 因为有可能是checkbox也有可能是radio，所以这里采用input来统一控制
      $('#ID_MTree :input').on('change', function () {
        var treeNode = $(this).data('treeNode');
        // 因为存在同一个账号挂在不同的职位下的情况，所以选中的话，默认当前展示框的节点被选中，
        // 取消的话，是所有相同节点都取消
        // if ($(this).attr("checked")) {
        //     // 触发左侧树节点的选中事件
        //     ztree.checkNode(treeNode, true, false, true);
        // } else {
        //     // 触发左侧树节点的取消选中事件
        //     var id = treeNode.id;
        //     var nodes = ztree.getNodesByParam("id", id);
        //     for (var i = 0; i < nodes.length; i++) {
        //         //先置为已选再取消选择才会触发oncheck事件
        //         nodes[i].checked=true;
        //         ztree.checkNode(nodes[i], false, false, true);
        //     }
        // }

        if ($(this).attr('checked')) {
          _self.newAddSelectedItem(treeNode);
        } else {
          _self.removeSelectedItem(treeNode);
        }
      });
    },

    computeTotalSelectedNum: function () {
      var num = this.rightListDataStore.length;
      $('#ID_RMemberCount').html(num);
      UnitTree.onSelectedChange && UnitTree.onSelectedChange(num);
    },
    updateLViewHeight: function () {
      var _self = this;
      var $OrgType = $('#ID_OrgType').prev();
      var $LVIEWPORT = $('#ID_LVIEWPORT');
      var $LOperate = $LVIEWPORT.next('.form_operate');
      var height = $LVIEWPORT.closest('.unit-table').height();
      var $LTree = $('#ID_LTree');
      var $LView = $('#ID_LView');
      if ($OrgType.length) {
        height -= 34;
      } else {
        // height -= 10;
      }
      if ($LOperate.is(':visible')) {
        height -= 34;
        $LView.find('.indexed-list-inner').css('height', height - 6);
      } else {
        var $LViewH = $LView.outerHeight();
        $LView.find('.indexed-list-inner').css('height', $LViewH);
      }
      $LVIEWPORT.outerHeight(height);
      _self.resizeLViewScroll();
    },
    switchView: function (isTreeView) {
      // 隐藏或者显示左侧操作
      var $LViewPort = $('#ID_LTree').closest('#ID_LVIEWPORT');
      $LViewPort.next('.form_operate')[isTreeView ? 'show' : 'hide']();
      $('#ID_LTree')[isTreeView ? 'show' : 'hide']();
      $('#ID_LView')[isTreeView ? 'hide' : 'show']();
      UnitTree.updateLViewHeight();
      if (isTreeView) {
        // 流程办理人树形列表默认展开第一个结点
        // if(UnitTree.options.type == "TaskUsers") {
        // 	var ztree = UnitTree.getZtree();
        // 	var nodes = ztree.getNodes();
        // 	if(nodes.length > 0) {
        // 	  ztree.expandNode(nodes[0], true, false, true, true, true);
        // 	}
        // }
        return;
      } else {
        // 切换为用户视图，将中间显示的节点用户清除
        UnitTree.emptyMtree();
        this.midListDataStore = null;
      }
      setTimeout(function () {
        $('#ID_Search').trigger('click');
      }, 0);
      // UnitTree.rendererLView();
    },
    rendererLView: function () {
      var self = this;
      var $LView = $('#ID_LView');
      // 列表视图不可见时，不渲染
      if ($LView.is(':visible') === false) {
        return;
      }
      var ztree = UnitTree.getZtree();
      // 过滤用户节点
      var uNodes = ztree.getNodesByParam('type', 'U');
      self.$indexlist.indexlist('render', uNodes);
      // self.$indexlist.indexlist("setViewHeight", $LView.height());
      // self.$indexlist.off("change").on("change", "input[type=checkbox]", function(event){
      // 	var $this = $(this);
      // 	var node = $this.closest("li").data("node");
      // 	if(!node){
      // 		return;
      // 	}
      // 	var checked = $this.prop("checked");
      // 	if(checked && self.options.multiple == false) {
      // 		self.$indexlist.find("input[type=checkbox]:checked").not($this).prop("checked", false);
      // 	}
      // 	ztree.checkNode(node, checked, false, true);
      // });
    }
  };

  /**
   * options : {
   * 	group:false,
   *
   * }
   *
   */
  var IndexList = function (element, options) {
    var self = this;
    self.options = options || {};
    self.$element = $(element);
    self.$search = self.$element.find('.indexed-list-search');
    self.$bar = self.$element.find('.indexed-list-bar>.bar-inner');
    self.$alert = self.$element.find('.indexed-list-alert');
    self.$listInner = self.$element.find('.indexed-list-inner');
    self.$empty = self.$element.find('.indexed-list-empty-alert');
    self.$view = self.$element.find('.table-view .table-view-list');
    // self.$bar.on("click", "a", function(event){
    // 	var $this = $(this);
    // 	$this.addClass("active").siblings(".active").removeClass("active");
    // 	var group = $this.text();
    // 	self.scrollTo(group);
    // });
  };
  $.extend(IndexList.prototype, {
    render: function (nodes) {
      var self = this;
      var groups = [],
        other = [];
      var aCode = 'A'.charCodeAt(0);
      var zCode = 'Z'.charCodeAt(0);
      for (var i = aCode; i <= zCode; i++) {
        // 初始化分组表
        groups.push([]);
      }
      groups.push(other);
      // 按字母ABC排序（数字在前）
      if ($.isFunction(self.options.sort)) {
        nodes = nodes.sort(self.options.sort);
      }
      for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        var shortName = node.data.userNamePy; //使用人员拼音排序
        if ($.trim(shortName).length === 0) {
          continue;
        }
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
      self.$bar.empty();
      self.$view.empty();
      for (var i = 0; i < groups.length; i++) {
        var group = groups[i];
        if (group.length <= 0) {
          continue;
        }
        for (var j = 0; j < group.length; j++) {
          var node = group[j];
          var $node = $(self._tplItem(node));
          $node.data('node', node);
          self.$view.append($node);
        }
        self.$bar.append(self._tplA(group[0].tag));
      }
      // self.$view.height(self.$element.height());
    },
    updateNode: function (node) {
      var self = this;
      var tag = shortName.charAt(0).toUpperCase();
      if ('A' <= tag && tag <= 'Z') {
        node.tag = tag;
        list.push(self._tplItem(node));
      } else {
      }
    },
    setViewHeight: function (height) {
      var self = this;
      self.$view.height(height);
    },
    scrollTo: function (group) {
      var self = this;
      var $groupElement = self.$view.find('[data-tags="' + group + '"]:eq(0)');
      if (!$groupElement.length || (self.hiddenGroups && self.hiddenGroups.indexOf($groupElement) > -1)) {
        return;
      }
      self.$listInner.scrollTop($groupElement.position().top);
    },
    _updateNode: function ($node, node) {
    },
    _tplA: function (letter) {
      // letter = letter.toUpperCase();
      return '<a>' + letter + '</a>';
    },
    _tplItem: function (item) {
      var tpl = '';
      var id = 'LU-' + $.uuid++;
      tpl += '<input data-id="' + item.id + '" id="' + id + '" type="checkbox" ' + (item.checked ? 'checked="checked"' : '') + '">';
      tpl += '<label for="' + id + '" class="chkbox iconfont"></label>';
      tpl += '<i class="icon U ' + item.iconSkin + '"' + ((item.data && item.data.remark) || '') + '></i>';
      tpl += '<label for="' + id + '" class="name">' + item.name + '</label>';
      var job = item.getParentNode();
      var department = '';
      if (job) {
        department = job.getParentNode();
      }
      if (job && job.name) {
        var _jobText = job.name;
        if (_jobText.indexOf('/') > -1) {
          _jobText = _jobText.split('/')[0];
        }
        if (department && department.name) {
          _jobText = department.name + '/' + _jobText;
        }
        tpl += '<label for="' + id + '" class="job" title="' + _jobText + '">' + _jobText + '</label>';
      } else if (item.namePath.split('/').length > 1) {
        tpl += '<label for="' + id + '" class="job">' + item.namePath.split('/')[0] + '</label>';
      }
      return (
        '<li data-value="' +
        item.data.shortName +
        '" data-tags="' +
        item.tag +
        '" class="table-view-cell indexed-list-item">' +
        tpl +
        '</li>'
      );
    },
    _tplGroup: function (letter) {
      // letter = letter.toUpperCase();
      return '<li data-group="' + letter + '" class="table-view-divider indexed-list-group">' + letter + '</li>';
    }
  });
  $.fn.indexlist = function (option) {
    var $this = $(this);
    var data = $this.data('bs.indexlist');
    var options = typeof option == 'object' && option;
    if (!data) {
      $this.data('bs.indexlist', (data = new IndexList(this, options)));
      return $this;
    } else if (typeof option == 'string') {
      return data[option].apply(data, $.makeArray(arguments).slice(1));
    }
  };
  $.fn.indexlist.Constructor = IndexList;

  // 直接访问 multi/org/dialog.html 测试使用代码
  // var options = {
  // // 显示全部, all:全部，MyUnit|我的单位,具体值参考 UNIT_TREE_TYPE 变量的定义 如果显示多个用分号分隔符
  // type : "AllOrg",
  // multiple : true, // 是否支持多选;
  // };
  //
  // UnitTree.init(options);

  window.UnitTree = UnitTree;
  return UnitTree;
});
