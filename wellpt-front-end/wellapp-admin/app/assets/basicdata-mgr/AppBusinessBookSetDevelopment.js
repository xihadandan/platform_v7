define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'HtmlWidgetDevelopment', 'clipboard'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  HtmlWidgetDevelopment,
  ClipboardJS
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;

  //  平台应用_公共资源_业务通讯录编辑二开
  var AppBusinessBookSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppBusinessBookSetDevelopment, HtmlWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {
    },
    // 创建后回调
    create: function () {
    },
    // 初始化回调
    init: function () {
      var _self = this;
      var _uuid;
      if (GetRequestParam().uuid) {
        _uuid = GetRequestParam().uuid;
      }
      // 表单选择器
      var form_selector = '#business_book_org_user_form';

      var oldRoles = [];
      var bean = {
        uuid: null,
        code: null,
        unit: null,
        unitValue: null,
        dept: null,
        deptValue: null,
        parentUuid: null,
        type: null,
        id: null,
        name: null,
        businessCategoryUuid: null
      };

      var validator = $.common.validation.validate(form_selector, 'businessCategoryOrgEntity');

      $(form_selector).json2form(bean);

      $('#business_book_unit').wSelect2({
        serviceName: 'businessCategoryService',
        queryMethod: 'querySelectDataFromMultiOrgSystemUnitAll',
        selectionMethod: 'loadSelectDataFromMultiOrgSystemUnit',
        labelField: 'business_book_unitValue',
        valueField: 'business_book_unit',
        defaultBlank: true
      });
      var _randomid = _.random(1, 9999999999);
      $("#copyid", _self.widget.element).attr('id', 'copyid' + _randomid);
      new ClipboardJS('#copyid' + _randomid, {
        text: function (trigger) {
          return bean.id;
        }
      }).on('success', function (e) {
        appModal.info('复制成功!');
      });

      // 新增操作
      $('#business_book_btn_unit').click(function () {
        initEditOrg('1');
      });
      $('#business_book_btn_category').click(function () {
        initEditOrg('2');
      });

      var tree = null;
      var setting = {
        callback: {
          beforeClick: function (treeId, treeNode) {
            $("#business_book_id_show", _self.widget.element).hide();
            if (treeNode.id === '-1') {
              var categoryId = bean.businessCategoryUuid;
              clear();
              bean.businessCategoryUuid = categoryId;
              bean.uuid = '-1';
              initEditOrg('1');
            } else {
              JDS.call({
                service: 'businessCategoryOrgService.get',
                data: [treeNode.id],
                async: false,
                validate: true,
                success: function (result) {
                  bean = result.data;
                  $(form_selector).json2form(bean);
                  _self.showBusinessBookId(bean.id);
                  $('#business_book_unit').trigger('change');
                  if (bean.type === '1') {
                    $('.business_book_unit').show();
                  } else {
                    $('.business_book_unit').hide();
                  }
                }
              });
            }
          }
        }
      };

      function initEditOrg(type) {
        _self.showBusinessBookId();
        if (!bean.businessCategoryUuid || !bean.uuid) {
          appModal.alert('请先选择业务类别');
          return;
        }
        if (type === '1') {
          $('.business_book_unit').show();
        } else {
          $('.business_book_unit').hide();
        }

        $(form_selector).clearForm(true);
        $('#business_book_unit').trigger('change');
        $('#business_book_parentUuid').val(bean.uuid);
        if (bean.type === '1') {
          $('#business_book_parentName').val(bean.deptValue);
        } else {
          $('#business_book_parentName').val(bean.name);
        }
        $('#business_book_type').val(type);
        $('#business_book_businessCategoryUuid').val(bean.businessCategoryUuid);
      }

      function initOrgTree() {
        bean.businessCategoryUuid = _uuid;
        bean.uuid = '-1';
        bean.name = '';
        initEditOrg('1');
        JDS.call({
          service: 'businessCategoryOrgService.findAsTree',
          data: [_uuid],
          async: false,
          validate: true,
          success: function (result) {
            tree = $.fn.zTree.init($('#business_book_org_tree'), setting, result.data);
          }
        });
      }

      function clear() {
        $(form_selector).clearForm(true);
        // 清空JSON
        $.common.json.clearJson(bean);
        $("span[name='business_book_id']", _self.widget.element).text('');
      }

      function getName(bean) {
        if (bean.type === '1') {
          return bean.name;
        } else {
          return bean.name;
        }
      }

      if (_uuid) {
        initOrgTree();
      }

      $('#business_book_deptValue').click(function () {
        var unit = $('#business_book_unit').val();
        if (unit === '') {
          appModal.alert('请先选择单位！');
          return;
        }
        var initValues = $('#business_book_dept').val();
        var initLabels = $('#business_book_deptValue').val();
        $.unit2.open({
          valueField: 'business_book_dept',
          labelField: 'business_book_deptValue',
          title: '请选择部门',
          type: 'All',
          multiple: false,
          selectTypes: 'D',
          valueFormat: 'justId',
          unitId: unit,
          initValues: initValues,
          initLabels: initLabels,
          callback: function (values, labels) {
          }
        });
      });

      // 保存脚本定义信息
      $('#business_book_btn_save').click(function () {
        if (!validator.form()) {
          return false;
        }

        if ($('#business_book_type').val() === '1' && $('#business_book_unit').val() === '') {
          //  || $('#dept').val() == '' // 不校验部门
          appModal.alert('单位不能为空');
          return false;
        }

        if ($('#business_book_type').val() !== '1' && $('#business_book_name').val() === '') {
          appModal.alert('名称不能为空');
          return false;
        }

        // 清空JSON
        $.common.json.clearJson(bean);
        // 收集表单数据
        $(form_selector).form2json(bean);

        JDS.call({
          service: 'businessCategoryOrgService.save',
          data: [bean],
          async: false,
          validate: true,
          success: function (result) {
            var treeObj = $.fn.zTree.getZTreeObj('business_book_org_tree');
            if (bean.uuid) {
              var node = treeObj.getNodesByParam('id', bean.uuid, null)[0];
              node.name = getName(bean);
              treeObj.updateNode(node);
              $('#business_book_uuid').val(bean.id);
            } else {
              bean.uuid = result.data;
              $('#business_book_uuid').val(bean.uuid);
              var parentNode = treeObj.getNodesByParam('id', bean.parentUuid, null)[0];
              treeObj.addNodes(parentNode, {id: bean.uuid, name: getName(bean)});
              treeObj.selectNode(treeObj.getNodesByParam('id', bean.uuid, null)[0]);
              treeObj.setting.callback.beforeClick(treeObj.setting.treeId, treeObj.getNodesByParam("id", bean.uuid, null)[0]);//触发函数
            }
            $('#business_book_uuid').val(result.data);
            appModal.success('保存成功！');
          }
        });
      });

      // 删除
      $('#business_book_btn_delete').click(function () {
        console.log(bean);
        if (StringUtils.isBlank(bean.uuid) || bean.uuid === '-1') {
          appModal.error('请选择记录！');
          return true;
        }

        var name = getName(bean);
        appModal.confirm('确定要删除[' + name + ']吗?', function (result) {
          if (!result) return;
          JDS.call({
            service: 'businessCategoryOrgService.delete',
            data: [bean.uuid],
            async: false,
            success: function (result) {
              appModal.success('删除成功!');
              refreshZtree('business_book_org_tree', 'delete');
              clear();
            }
          });
        });
      });
    },

    showBusinessBookId: function (id) {
      if (id) {
        $("span[name='business_book_id']", this.widget.element).text(id);
        $("#business_book_id_show", this.widget.element).show();
      } else {
        $("#business_book_id_show", this.widget.element).hide();
      }
    },

    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppBusinessBookSetDevelopment;
});
