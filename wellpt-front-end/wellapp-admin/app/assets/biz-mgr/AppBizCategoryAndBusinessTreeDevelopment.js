define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;
  var AppBizCategoryAndBusinessTreeDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppBizCategoryAndBusinessTreeDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _self = this;
      var $element = _self.getWidgetElement();
      // 加载业务树
      _self.loadTree();

      // 绑定事件
      _self.bindEvents();

      // 隐藏操作按钮
      if ($element.hasClass('hide-buttons')) {
        $element.find('.btn-group').hide();
      }

      // 显示滚动条
      if (!$element.hasClass('hide-scroller')) {
        $('#biz_tree', $element).slimScroll({
          height: '600px',
          wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
        });
      }
    },
    refresh: function () {
      var _self = this;
      _self.init();
    },
    loadTree: function () {
      var _self = this;
      var $element = _self.getWidgetElement();
      var setting = {
        callback: {
          beforeClick: function (treeId, treeNode) {
            if (treeNode == null) {
              return;
            }

            var listViewTableId = $('#biz_category_and_business_tree', $element)
              .closest('.ui-wBootgrid')
              .find('.ui-wBootstrapTable')
              .attr('id');
            if (treeNode.id == '-1') {
              $('#' + listViewTableId)
                .wBootstrapTable('clearParams')
                .wBootstrapTable('refresh');
            } else if (treeNode.type == 'category') {
              $('#' + listViewTableId)
                .wBootstrapTable('removeParam', 'businessId')
                .wBootstrapTable('addParam', 'categoryUuid', treeNode.id)
                .wBootstrapTable('refresh');
            } else if (treeNode.type == 'business') {
              $('#' + listViewTableId)
                .wBootstrapTable('removeParam', 'categoryUuid')
                .wBootstrapTable('addParam', 'businessId', treeNode.data.id)
                .wBootstrapTable('refresh');
            }
            return true;
          },
          onDblClick: function (event, treeId, treeNode) {
            if ($element.hasClass('hide-buttons') || treeNode == null) {
              return;
            }
            // 业务分类
            if (treeNode.type == 'category') {
              _self.showCategoryDialog(treeNode.data);
            } else if (treeNode.type == 'business') {
              // 业务
              _self.showBusinessDialog(treeNode.data);
            }
          }
        },
        view: {
          selectedMulti: false
        }
      };

      $.ajax({
        url: ctx + '/proxy/api/biz/category/getCategoryAndBusinessTree',
        type: 'get',
        success: function (result) {
          var treeNodes = result.data;
          var zTree = $.fn.zTree.init($('#biz_tree', $element), setting, treeNodes);
          _self.zTree = zTree;
          var nodes = zTree.getNodes();
          // 默认展开第一个节点
          if (nodes.length > 0) {
            var node = nodes[0];
            zTree.expandNode(node, true, false, false, true);
          }
        }
      });
    },
    bindEvents: function () {
      var _self = this;
      var $container = $(_self.getWidgetElement());
      // 新增分类
      $('#btn_category_add', $container).on('click', function () {
        _self.showCategoryDialog({});
      });

      // 删除分类
      $('#btn_category_delete', $container).on('click', function () {
        _self.deleteCategory();
      });

      // 新增业务
      $('#btn_business_add', $container).on('click', function () {
        _self.showBusinessDialog({});
      });

      // 删除业务
      $('#btn_business_delete', $container).on('click', function () {
        _self.deleteBusiness();
      });
    },
    showCategoryDialog: function (category) {
      var _self = this;
      var categoryBean = category || _self.getDefaultCategory();
      var dlgId = UUID.createUUID();
      var dlgSelector = '#' + dlgId;
      var message = "<div id='" + dlgId + "'></div>";
      var options = {
        title: StringUtils.isBlank(categoryBean.uuid) ? '新增' : '编辑' + '业务分类',
        size: 'large',
        message: message,
        shown: function () {
          formBuilder.buildInput({
            label: '名称',
            name: 'name',
            value: categoryBean.name,
            labelClass: 'required',
            placeholder: '',
            container: dlgSelector
          });
          formBuilder.buildInput({
            label: 'ID',
            name: 'id',
            value: categoryBean.id,
            labelClass: 'required',
            placeholder: '',
            container: dlgSelector
          });
          formBuilder.buildInput({
            label: '编号',
            name: 'code',
            value: categoryBean.code,
            placeholder: '',
            container: dlgSelector
          });
          formBuilder.buildTextarea({
            label: '备注',
            name: 'remark',
            value: categoryBean.remark,
            placeholder: '',
            rows: 6,
            container: dlgSelector
          });
        },
        buttons: {
          canfirm: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var name = $('#name', dlgSelector).val();
              var id = $('#id', dlgSelector).val();
              var code = $('#code', dlgSelector).val();
              var remark = $('#remark', dlgSelector).val();
              if (StringUtils.isBlank(name)) {
                appModal.error('名称不能为空！');
                return false;
              }
              if (StringUtils.isBlank(id)) {
                appModal.error('ID不能为空！');
                return false;
              }
              categoryBean.name = name;
              categoryBean.id = id;
              categoryBean.code = code;
              categoryBean.remark = remark;
              JDS.restfulPost({
                url: '/proxy/api/biz/category/save',
                data: categoryBean,
                success: function (result) {
                  appModal.success('保存成功！');
                  // 刷新树
                  _self.loadTree();
                }
              });
            }
          },
          cancel: {
            label: '取消',
            className: 'btn btn-default'
          }
        }
      };
      appModal.dialog(options);
    },
    getDefaultCategory: function () {
      return { name: '', id: '', code: '', remark: '' };
    },
    deleteCategory: function () {
      var _self = this;
      var categoryUuids = [];
      if (_self.zTree) {
        var selectedNodes = _self.zTree.getSelectedNodes();
        for (var i = 0; i < selectedNodes.length; i++) {
          if (selectedNodes[i].type != 'category') {
            appModal.error('请选择分类节点！');
            return;
          }
          categoryUuids.push(selectedNodes[i].id);
        }
      }
      if (categoryUuids.length == 0) {
        appModal.error('请选择分类节点！');
        return;
      }
      appModal.confirm('确定删除业务分类吗？', function (result) {
        if (result) {
          JDS.restfulPost({
            url: '/proxy/api/biz/category/deleteAll',
            traditional: true,
            data: {
              uuids: categoryUuids
            },
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
              appModal.success('删除成功！');
              // 刷新树
              _self.loadTree();
            }
          });
        }
      });
    },
    showBusinessDialog: function (business) {
      var _self = this;
      if (business != null && StringUtils.isBlank(business.uuid)) {
        if (!_self.zTree) {
          appModal.error('请选择业务分类！');
          return;
        }
        var selectedNodes = _self.zTree.getSelectedNodes();
        if (selectedNodes.length == 0) {
          appModal.error('请选择业务分类！');
          return;
        }
        var categoryUuid = '';
        for (var i = 0; i < selectedNodes.length; i++) {
          if (selectedNodes[i].type == 'category') {
            categoryUuid = selectedNodes[i].id;
            break;
          }
        }
        if (StringUtils.isBlank(categoryUuid)) {
          appModal.error('请选择业务分类！');
          return;
        }
        business.categoryUuid = categoryUuid;
      }

      var businessBean = business || _self.getDefaultBusiness();
      var dlgId = UUID.createUUID();
      var dlgSelector = '#' + dlgId;
      var message = "<div id='" + dlgId + "'></div>";
      var options = {
        title: StringUtils.isBlank(businessBean.uuid) ? '新增' : '编辑' + '业务',
        size: 'large',
        message: message,
        shown: function () {
          formBuilder.buildInput({
            label: '名称',
            name: 'name',
            value: businessBean.name,
            labelClass: 'required',
            placeholder: '',
            container: dlgSelector
          });
          formBuilder.buildInput({
            label: 'ID',
            name: 'id',
            value: businessBean.id,
            labelClass: 'required',
            placeholder: '',
            container: dlgSelector
          });
          formBuilder.buildInput({
            label: '编号',
            name: 'code',
            value: businessBean.code,
            placeholder: '',
            container: dlgSelector
          });
          formBuilder.buildTextarea({
            label: '备注',
            name: 'remark',
            value: businessBean.remark,
            placeholder: '',
            rows: 6,
            container: dlgSelector
          });
        },
        buttons: {
          canfirm: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var name = $('#name', dlgSelector).val();
              var id = $('#id', dlgSelector).val();
              var code = $('#code', dlgSelector).val();
              var remark = $('#remark', dlgSelector).val();
              if (StringUtils.isBlank(name)) {
                appModal.error('名称不能为空！');
                return false;
              }
              if (StringUtils.isBlank(id)) {
                appModal.error('ID不能为空！');
                return false;
              }
              businessBean.name = name;
              businessBean.id = id;
              businessBean.code = code;
              businessBean.remark = remark;
              JDS.restfulPost({
                url: '/proxy/api/biz/business/save',
                data: businessBean,
                success: function (result) {
                  appModal.success('保存成功！');
                  // 刷新树
                  _self.loadTree();
                }
              });
            }
          },
          cancel: {
            label: '取消',
            className: 'btn btn-default'
          }
        }
      };
      appModal.dialog(options);
    },
    getDefaultBusiness: function () {
      return {
        name: '',
        id: '',
        code: '',
        categoryUuid: '',
        remark: ''
      };
    },
    deleteBusiness: function () {
      var _self = this;
      var businessUuids = [];
      if (_self.zTree) {
        var selectedNodes = _self.zTree.getSelectedNodes();
        for (var i = 0; i < selectedNodes.length; i++) {
          if (selectedNodes[i].type != 'business') {
            appModal.error('请选择业务节点！');
            return;
          }
          businessUuids.push(selectedNodes[i].id);
        }
      }
      if (businessUuids.length == 0) {
        appModal.error('请选择业务节点！');
        return;
      }
      appModal.confirm('确定删除业务吗？', function (result) {
        if (result) {
          JDS.restfulPost({
            url: '/proxy/api/biz/business/deleteAll',
            traditional: true,
            data: {
              uuids: businessUuids
            },
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
              appModal.success('删除成功！');
              // 刷新树
              _self.loadTree();
            }
          });
        }
      });
    }
  });
  return AppBizCategoryAndBusinessTreeDevelopment;
});
