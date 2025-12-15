define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  HtmlWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;

  //  平台应用_公共资源_脚本定义编辑二开
  var AppCdScriptDefinitionSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppCdScriptDefinitionSetDevelopment, HtmlWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},
    // 创建后回调
    create: function () {},
    // 初始化回调
    init: function () {
      var _self = this;
      var _id;
      if (GetRequestParam().id) {
        _id = GetRequestParam().id;
      }
      // 表单选择器
      var form_selector = '#excute_sql_definiton_form';
      $(form_selector).clearForm(true);

      var formBean = {
        uuid: null,
        name: null,
        id: null,
        sqlContent: null
      };

      var validator = $.common.validation.validate(form_selector, 'executeSqlDefinitionEntity', function (options) {
        options.ignore = '';
      });

      if (!$ace) {
        var $ace = $.fn.aceBinder({
          container: '#excute_sql_definiton_sqlContainer',
          iframeId: 'excute_sql_definiton_sqlContainerIframe',
          mode: 'sql'
        });
      }

      $(form_selector).json2form(formBean);

      // 根据id获取数据
      function getSqlDefinitionById(id, success) {
        JDS.call({
          service: 'execSqlDefinitionFacadeService.getDtoById',
          data: [id],
          success: success
        });
      }

      if (_id) {
        getSqlDefinitionById(_id, function (result) {
          formBean = result.data;
          $(form_selector).json2form(formBean);
          // ID只读
          $('#excute_sql_definiton_id').prop('readonly', 'readonly');
          $ace.setValue(formBean.sqlContent);
        });
      } else {
        // 生成ID
        $.common.idGenerator.generate('#excute_sql_definiton_id', 'EXEC_SQL_');
        // ID可编辑
        $('#excute_sql_definiton_id').prop('readonly', '');
        $ace.setValue('');
      }

      // 保存脚本定义信息
      $('#excute_sql_definiton_btn_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        $(form_selector).form2json(formBean);
        formBean.sqlContent = $ace.getValue();

        JDS.call({
          service: 'execSqlDefinitionFacadeService.saveSql',
          data: [formBean],
          success: function (result) {
            appModal.success('保存成功!');
          }
        });
      });
    },

    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppCdScriptDefinitionSetDevelopment;
});
