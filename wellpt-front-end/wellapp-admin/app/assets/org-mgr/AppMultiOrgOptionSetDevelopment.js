define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppMultiOrgOptionSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppMultiOrgOptionSetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _self = this;
      var _uuid = GetRequestParam().uuid;

      var form_selector = '#org_option_form';
      var formBean = {
        uuid: null,
        id: null,
        name: null,
        code: null,
        remark: null,
        systemUnitId: null,
        isShow: 1,
        isEnable: 1
      };

      $(form_selector).json2form(formBean);

      var validator = $.common.validation.validate(form_selector, 'multiOrgOption');

      if (_uuid) {
        showOrgOptionInfo();
        $('#org_option_id').attr('readonly', 'readonly');
      } else {
        showOptionStyle();
      }
      $('input[name="isEnable"]').on('change', function (event) {
        var checkedVal = $('input[name="isEnable"]:checked').val();
        if (checkedVal === '0') {
          $('#org_option_isShow1').closest('.form-group').hide();
        } else {
          $('#org_option_isShow1').closest('.form-group').show();
        }
      });

      // 显示样式
      function showOptionStyle(id) {
        if ($.trim(id).length <= 0) {
          $('#style-tree').closest('.form-group').hide();
          return;
        }
        $.ajax({
          url: ctx + '/api/org/option/getOptionStyle',
          type: 'get',
          data: {
            id: id
          },
          success: function (result) {
            if (typeof result.data === 'string') {
              if (result.data.indexOf('tree') > -1) {
                $('#style-tree').show();
              }
              if (result.data.indexOf('list') > -1) {
                $('#style-list').show();
              }
            }
          }
        });
        $('#style-tree').closest('.form-group').show();
      }

      // 根据组织UUID获取信息
      function showOrgOptionInfo() {
        $.ajax({
          url: ctx + '/api/org/multi/getOrgOption',
          type: 'get',
          data: {
            uuid: _uuid
          },
          success: function (result) {
            formBean = result.data;
            $(form_selector).json2form(formBean);
            showOptionStyle(formBean.id);
            $('input[name="isEnable"]:checked').trigger('change');
          }
        });
      }

      // 定义保存按钮的事件
      $('#btn_options_save')
        .off()
        .on('click', function () {
          if (!validator.form()) {
            return false;
          }
          $(form_selector).form2json(formBean);
          formBean.systemUnitId = formBean.systemUnitId || SpringSecurityUtils.getCurrentUserUnitId();
          var url = '/api/org/multi/' + (formBean.uuid ? 'modifyOrgOption' : 'addOrgOption');
          $.ajax({
            type: 'POST',
            url: url,
            dataType: 'json',
            data: formBean,
            success: function (data) {
              appModal.success('保存成功！');
              appContext.getNavTabWidget().closeTab();
            }
          });
        });

      // 添加提示
      function addPopoverTips() {
        var $showTip = $('<span class="icon iconfont icon-ptkj-xinxiwenxintishi" aria-hidden="true" title="使用说明"></span>');
        var $enableTip = $('<span class="icon iconfont icon-ptkj-xinxiwenxintishi" aria-hidden="true" title="使用说明"></span>');
        $('label[for="org_option_isShow0"]').after($showTip);
        $('label[for="org_option_isEnable0"]').after($enableTip);
        $showTip.popover({
          html: true,
          placement: 'bottom',
          container: 'body',
          trigger: 'hover',
          template:
            '<div class="popover" role="tooltip" style="max-width:720px;"><div class="arrow"></div><div class="popover-content"></div></div>',
          content: function () {
            var content =
              '<div id="u25056_text" class="text " style="top: 20px; transform-origin: 308px 24px 0px;"><p><span>1、当组织选择框的程序，无type参数时，“默认显示”选中的组织项才会显示</span></p><p><span>2、当组织选择框的程序，从页面配置项中读取type参数时，“默认显示”选中的组织项，在配置项中默认选中，如表单组织选择框控件的“可选的组织选择项”</span></p></div>';
            return content;
          }
        });
        $enableTip.popover({
          html: true,
          placement: 'bottom',
          container: 'body',
          trigger: 'hover',
          template:
            '<div class="popover" role="tooltip" style="max-width:720px;"><div class="arrow"></div><div class="popover-content"></div></div>',
          content: function () {
            var content =
              '<div id="u25049_text" class="text " style="top: 17px; transform-origin: 308px 56px 0px;"><p><span>1、禁用时，所有组织选择框都不显示该组织选择项</span></p><p><span>2、启用时，组织选择框中显示的组织选择项，通过2种方式控制</span></p><p><span>（1）通过程序type参数指定</span></p><p><span>• 示例：如type:"Role;MyDept"，则该程序控制的组织选择框中，只显示组织选择项“角色”、“我的部门”，且组织选择项的状态必须都为启用</span></p><p><span>• 特别注意：程序type参数值可能从页面配置项中读取，如表单组织选择框控件的“可选的组织选择项”</span></p><p><span>（2）通过字段“默认显示”控制：程序无type参数时，显示“默认显示”选中的组织选择项</span></p></div>';
            return content;
          }
        });
      }

      addPopoverTips();
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppMultiOrgOptionSetDevelopment;
});
