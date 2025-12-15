// TOGGLER CUSTOMIZATION SETTINGS
var Layout = {};
Layout.layout = function (option) {
  var westDefaultSize = 550;
  var updateWestPaneSize = true;
  var toggleButtons = '<div class="btnToggler"></div>' + '<div class="btnReset"></div>' + '<div class="btnExpand"></div>';
  var options = $.extend(
    true,
    {
      resizerDragOpacity: 1,
      resizerDblClickToggle: false,
      resizeWhileDragging: true,
      onresize: resizeJqGrid,
      fxName: 'slide',
      west__size: westDefaultSize,
      spacing_open: 8,
      spacing_closed: 8,
      west__togglerLength_closed: 75,
      west__togglerLength_open: 75,
      west__togglerContent_closed: toggleButtons,
      west__togglerContent_open: toggleButtons,
      layout_selector: 'body'
    },
    option
  );
  westDefaultSize = options.west__size;
  // CREATE THE LAYOUT
  var layout = $(options.layout_selector).layout(options);

  // SET OBJECT POINTERS FOR CONVENIENCE
  var $westToggler = layout.togglers.west;

  // UN-BIND DEFAULT TOGGLER FUNCTIONALITY
  $westToggler.unbind('click');
  $('.ui-layout-resizer').unbind('click');
  $('.ui-layout-toggler').unbind('click');
  $westToggler.unbind('dbclick');
  $('.ui-layout-resizer').unbind('dbclick');
  $('.ui-layout-toggler').unbind('dbclick');

  // BIND CUSTOM WEST METHODS
  $westToggler.find('.btnToggler').one('click', toggleWest);
  $westToggler.find('.btnReset').click(resetWest);
  $westToggler.find('.btnExpand').one('click', expandWest);

  // 初始化隐藏
  // $westToggler.find(".btnReset").hide();

  // ADD TOOLTIPS TO CUSTOM BUTTONS
  $('.btnToggler').attr('title', '向左');
  $('.btnReset').attr('title', '拖动');
  $('.btnExpand').attr('title', '向右');

  // CUSTOM WEST METHODS
  function toggleWest(evt) {
    layout.togglers.west.find('.btnExpand').unbind('click', expandWest);
    layout.togglers.west.find('.btnExpand').one('click', resetWest);

    layout.togglers.west.find('.btnToggler').hide();
    layout.togglers.west.find('.btnReset').hide();
    layout.togglers.west.find('.btnExpand').show();

    layout.close('west');
    evt.stopPropagation();
  }

  function expandWest(evt) {
    updateWestPaneSize = false;
    layout.togglers.west.find('.btnToggler').unbind('click', toggleWest);
    layout.togglers.west.find('.btnToggler').one('click', resetWest);

    layout.togglers.west.find('.btnToggler').show();
    layout.togglers.west.find('.btnReset').hide();
    layout.togglers.west.find('.btnExpand').hide();

    layout.sizePane('west', '100%');
    layout.open('west');
    evt.stopPropagation();
  }

  function resetWest(evt) {
    updateWestPaneSize = true;
    layout.togglers.west.find('.btnToggler').unbind('click', toggleWest);
    layout.togglers.west.find('.btnExpand').unbind('click', expandWest);
    layout.togglers.west.find('.btnToggler').one('click', toggleWest);
    layout.togglers.west.find('.btnExpand').one('click', expandWest);

    layout.togglers.west.find('.btnToggler').show();
    layout.togglers.west.find('.btnReset').show();
    layout.togglers.west.find('.btnExpand').show();

    sizePane('west', westDefaultSize);
  }

  // GENERIC HELPER FUNCTION
  function sizePane(pane, size) {
    layout.sizePane(pane, size);
    layout.open(pane); // open pane if not already
  }

  window.sizePane = sizePane;

  // JQuery layout布局变化时，更新jqGrid高度与宽度
  function resizeJqGrid(position, pane, paneState) {
    // 容器直接带jqGrid列表的panel直接隐藏滚动条
    $('.ui-layout-pane>div>.ui-jqgrid').each(function () {
      $(this).parent().parent().css('overflow', 'hidden');
    });
    $('.ui-layout-resizer').unbind('click');
    $('.ui-layout-toggler').unbind('click');
    if ('west' === position && updateWestPaneSize) {
      westDefaultSize = pane.width() + 2;
    }
    if ('center' === position && options.center_onresize != undefined) {
      options.center_onresize.call(this, position, pane, paneState);
      return;
    }
    if (paneState.isClosed == undefined || paneState.isClosed != false) {
      return;
    }
    if ((grid = $('.ui-jqgrid-btable:visible'))) {
      grid.each(function (index) {
        // 如果找到中间区域列表且处理事件不为空直接返回，不再处理
        if (this.id === options.center_list_id && options.center_onresize) {
          return;
        }
        //必须是先取ui-layout-center, 如果没有的话，再取ui-layout-west
        var $paneDiv = $(this).parents('.ui-layout-center');
        if ($paneDiv.length == 0) {
          $paneDiv = $(this).parents('.ui-layout-west');
        }

        //				if( index == 1 ){
        //					console.log($paneDiv.width());
        //					console.log($paneDiv);
        //					console.log(pane);
        //				}
        //
        var paneWidth = $paneDiv.width() - (index == 0 ? 22 : 80); // pane.width() - 22;
        var paneHeight = $paneDiv.height() - (index == 0 ? 104 : 230);
        if ($('div.btn-group-top').height() > 50) {
          paneHeight = paneHeight - 29;
        }
        $(this).setGridWidth(paneWidth);
        $(this).setGridHeight(paneHeight);
      });
    }
  }

  sizePane('west', westDefaultSize);
};
$(function () {
  // 后台管理公共区

  if (jQuery.validator) {
    // 手机号码验证
    jQuery.validator.addMethod(
      'mobilePhone',
      function (value, element) {
        var mobile = /(\+\d+)?1[3458]\d{9}$/;
        return this.optional(element) || mobile.test(value);
      },
      '不是有效的手机号码!'
    );
    // 电话号码验证
    jQuery.validator.addMethod(
      'telephone',
      function (value, element) {
        var tel = /(\+\d+)?(\d{3,4}\-?)?\d{7,8}$/;
        return this.optional(element) || tel.test(value);
      },
      '不是有效的电话号码!'
    );
    jQuery.validator.addMethod(
      'idcardNumber',
      function (value, element) {
        var tel = /^[1-9]\\d{13,16}[a-zA-Z0-9]{1}$/;
        return this.optional(element) || isIdCardNo(value);
      },
      '不是有效的身份证号!'
    );

    // 身份证号码的验证规则
    function isIdCardNo(num) {
      // if (isNaN(num)) {alert("输入的不是数字！"); return false;}
      var len = num.length,
        re;
      if (len == 15) re = new RegExp(/^(\d{6})()?(\d{2})(\d{2})(\d{2})(\d{2})(\w)$/);
      else if (len == 18) re = new RegExp(/^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/);
      else {
        // alert("输入的数字位数不对。");
        return false;
      }
      var a = num.match(re);
      if (a != null) {
        if (len == 15) {
          var D = new Date('19' + a[3] + '/' + a[4] + '/' + a[5]);
          var B = D.getYear() == a[3] && D.getMonth() + 1 == a[4] && D.getDate() == a[5];
        } else {
          var D = new Date(a[3] + '/' + a[4] + '/' + a[5]);
          var B = D.getFullYear() == a[3] && D.getMonth() + 1 == a[4] && D.getDate() == a[5];
        }
        if (!B) {
          // alert("输入的身份证号 "+ a[0] +" 里出生日期不对。");
          return false;
        }
      }
      if (!re.test(num)) {
        // alert("身份证最后一位只能是数字和字母。");
        return false;
      }
      return true;
    }
  }

  $.common = $.common || {};
  $.common.jqGrid = $.common.jqGrid || {};
  $.common.jqGrid.defaultPostData = {
    queryPrefix: 'query',
    queryOr: false,
    query_EQS_systemUnitId: SpringSecurityUtils.getCurrentUserUnitId()
  };
  $.common.jqGrid.settings = {
    mtype: 'POST',
    datatype: 'json',
    rowNum: 20,
    rownumbers: true,
    rowList: [10, 20, 50, 100, 200],
    rowId: 'uuid',
    pager: '#pager',
    recordpos: 'right',
    viewrecords: true,
    sortable: true,
    sortname: 'code',
    sortorder: 'asc',
    multiselect: true,
    multiboxonly: true,
    autowidth: true,
    height: 450,
    scrollOffset: 0,
    jsonReader: {
      root: 'dataList',
      total: 'totalPages',
      page: 'currentPage',
      records: 'totalRows',
      repeatitems: false
    },
    loadComplete: function (data) {
      // 取消默认选中第一行 zyguo
      /*
       * if (data.dataList.length > 0) { var rowids =
       * $(this).data("lastSelection"); var dataIds =
       * $(this).getDataIDs(); if (rowids && rowids.length > 0 &&
       * isJqGridExistsSelectedData(rowids, dataIds)) {
       * $(this).setSelection(rowids[0]); } else {
       * $(this).setSelection(dataIds[0]); } }
       */
    }
  };

  // 判断jqGrid是否存在选中的数据
  function isJqGridExistsSelectedData(selectedIds, dataIds) {
    for (var i = 0; i < selectedIds.length; i++) {
      var selectedId = selectedIds[i];
      for (var j = 0; j < dataIds.length; j++) {
        if (selectedId === dataIds[j]) {
          return true;
        }
      }
    }
    return false;
  }

  // 模块分类格式化器
  $.common.jqGrid.formatter = $.common.jqGrid.formatter || {};
  $.common.jqGrid.formatter.moduleCategory = function (cellvalue, options, rowObject) {
    var cellshow = cellvalue;
    var categoryData = SelectiveDatas.getItems('MODULE_CATEGORY');
    for (var i = 0; i < categoryData.length; i++) {
      if (categoryData[i].value == cellvalue) {
        cellshow = categoryData[i].label;
        break;
      }
    }
    return cellshow;
  };
  $.common.jqGrid.formatter.systemUnitName = function (value, row, rowData) {
    var name = SystemUnit.getNameById(value);
    if (name) {
      return name;
    }
    return value;
  };
  $.common.jqGrid.formatter.userType = function (value, row, rowData) {
    if ('0' == value) {
      return '普通用户';
    } else if ('1' == value) {
      return '单位管理员';
    } else {
      return value;
    }
  };

  // 验证
  $.common.validation = $.common.validation || {};
  $.common.validation.validate = function (form, beanName, callback) {
    var options = {};
    $.ajax({
      type: 'GET',
      url: ctx + '/common/validation/metadata?beanName=' + beanName,
      contentType: 'application/json',
      dataType: 'json',
      async: false,
      success: function (success, statusText, jqXHR) {
        options = success;
      },
      error: function (jqXHR, statusText, error) {
      }
    });
    if (options.rules) {
      if (beanName == 'sysParamItem' && options.rules.value) {
        options.rules.value.maxlength = 2550;
      }
      if (callback != null) {
        callback.call(this, options);
      }
      $.each(options.rules, function (p) {
        if (this.required === true && !$("label[for='" + p + "']").hasClass('required')) {
          $("label[for='" + p + "']").after('<font color="red">*</font>');
        }
        if (this.remote != null) {
          var checkType = this.remote;
          var remoteRule = {
            url: ctx + '/common/validate/check/exists',
            type: 'POST',
            data: {
              uuid: function () {
                return $('#uuid').val();
              },
              checkType: checkType,
              fieldName: p,
              fieldValue: function () {
                return $.trim($('#' + p).val());
              }
            }
          };
          this.remote = remoteRule;
        }
      });

      return $(form).validate(options);
    }
  };

  // ID生成
  $.common.idGenerator = $.common.idGenerator || {};
  $.common.idGenerator.generate = function (selector, prefix) {
    var id = prefix;
    JDS.call({
      service: 'idGeneratorService.getBySysDate',
      async: false,
      version: '',
      success: function (result) {
        prefix += result.data;
      }
    });
    $(selector).val(prefix);
    $(selector).focus();
    $(selector).trigger('focusin');
    return prefix;
  };
  var categoryData = null;
  /* jqGrid的分类的格式化器 */
  $.common.jqGrid.formatter.moduleCategory = function (cellvalue, options, rowObject) {
    var cellshow = cellvalue;
    if (!categoryData) {
      $.ajax({
        type: 'POST',
        url: ctx + '/common/select2/query',
        dataType: 'json',
        contentType: 'application/json',
        async: false,
        data: JSON.stringify({
          serviceName: 'dataDictionaryService',
          type: 'MODULE_CATEGORY',
          pageSize: 1000,
          pageNo: 1
        }),
        success: function (result) {
          categoryData = result.results;
        }
      });
    }
    for (i = 0; i < categoryData.length; i++) {
      if (categoryData[i].id == cellvalue) {
        cellshow = categoryData[i].text;
        break;
      }
    }
    return cellshow;
  };

  $.common.ztree = $.common.ztree || {};
  $.common.ztree.initOrgTree = function (eleId, setting, orgVersionId) {
    $.ajax({
      url: ctx + '/api/org/multi/getOrgAsTreeByVersionId',
      type: 'get',
      data: {
        orgVersionId: orgVersionId
      },
      success: function (result) {
        var treeNodes = result.data;
        var zTree = $.fn.zTree.init($(eleId), setting, treeNodes);
        var nodes = zTree.getNodes();
        // 默认展开第一个节点
        if (nodes.length > 0) {
          var node = nodes[0];
          zTree.expandNode(node, true, false, false, true);
        }
      }
    });

  };

  $.common.ztree.initRoleTree = function (treeEleId, selectEleId) {
    var setting = {
      check: {
        enable: true
      },
      callback: {
        onCheck: function (event, treeId, treeNode) {
          if (treeNode) {
            var id = treeNode.id;
            var name = treeNode.name;
            if (treeNode.checked) {
              var option = "<option value='" + id + "'>" + name + '</option>';
              $('#' + selectEleId).append(option);
            } else {
              $('#' + selectEleId + " option[value='" + id + "']").remove();
            }
          }
        },
        onNodeCreated: function (event, treeId, treeNode) {
          if ('R' == treeNode.type) {
            // 角色的节点,才显示勾选
            treeNode.nocheck = false;
          } else {
            treeNode.nocheck = true;
          }
          var ztree = $.fn.zTree.getZTreeObj(treeId);
          ztree.updateNode(treeNode);
        }
      },
      view: {
        fontCss: function (treeId, treeNode) {
          if (treeNode.type == 'R') {
            return treeNode.data != 'S0000000000'
              ? {
                color: 'red'
              }
              : {};
          }
        }
      }
    };
    $.ajax({
      url: ctx + '/api/security/role/getRoleTree',
      type: 'get',
      data: {},
      success: function (result) {
        var zTree = $.fn.zTree.init($('#' + treeEleId), setting, result.data);
        var nodes = zTree.getNodes();
        // 默认展开第一个节点
        if (nodes.length > 0) {
          var node = nodes[0];
          zTree.expandNode(node, true, false, false, true);
        }
      }
    });

  };

  $.common.ztree.initOpinionTree = function (treeEleId, setting, params) {
    JDS.call({
      service: 'flowOpinionCategoryService.getFlowOpinionCategoryTreeByBusinessAppDataDic',
      data: [params],
      version: '',
      success: function (result) {
        var zTree = $.fn.zTree.init($('#' + treeEleId), setting, result.data);
        var nodes = zTree.getNodes();
        // 默认展开第一个节点
        if (nodes.length > 0) {
          var node = nodes[0];
          zTree.expandNode(node, true, false, false, true);
        }
      }
    });
  };

  $.common.treePath = $.common.treePath || {};
  // 获取离的最近的单位ID
  $.common.treePath.getDeptId = function (path) {
    var p = path.split('/');
    for (var i = p.length - 1; i >= 0; i--) {
      if (p[i].substr(0, 1) == 'D') {
        return p[i];
      }
    }
    return null;
  };
  // 获取离的最近的职位ID
  $.common.treePath.getJobId = function (path) {
    var p = path.split('/');
    for (var i = p.length - 2; i >= 0; i--) {
      if (p[i].substr(0, 1) == 'J') {
        return p[i];
      }
    }
    return null;
  };
  // 获取根节点ID
  $.common.treePath.getRootNodeId = function (path) {
    var p = path.split('/');
    return p[0];
  };
  // 获取元素ID
  $.common.treePath.getEleId = function (path) {
    var p = path.split('/');
    return p[p.length - 1];
  };

  $.common.systemUnit = $.common.systemUnit || {};
  $.common.systemUnit.init = function (valueField, labelField, includePt) {
    $('#' + valueField).wSelect2({
      serviceName: 'multiOrgSystemUnitService',
      queryMethod: 'queryUnitListForSelect2',
      labelField: labelField,
      valueField: valueField,
      placeholder: '请选择',
      params: {
        includePt: true == includePt ? 1 : 0
      },
      multiple: false,
      remoteSearch: false,
      width: '100%',
      height: 250
    });
  };

  $.common.idPrev = $.common.idPrev || {};
  $.common.idPrev.all = {
    D: '部门',
    J: '职位',
    DU: '职务',
    B: '业务单位',
    O: '组织节点',
    S: '系统单位',
    U: '用户',
    G: '群组',
    V: '系统单位' // 在界面展示体现的是系统单位，实际上是系统单位的组织版本
  };
  $.common.idPrev.getName = function (type) {
    return $.common.idPrev.all[type];
  };
});
