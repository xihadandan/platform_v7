define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  AppPtMgrCommons
) {
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;
  var Validation = server.Validation;
  var SelectiveDatas = server.SelectiveDatas;

  // 平台管理_产品集成_功能列表_视图组件二开
  var AppFunctionDialog = {};

  AppFunctionDialog.show = function (options) {
    var excludeDataUuids = options.excludeDataUuids || [];
    var showRight = options.showRight == undefined ? true : options.showRight;
    var callback = options.callback;
    var dlgId = UUID.createUUID();
    var title = '添加功能';
    var message = "<div id='" + dlgId + "' class='form-horizontal'></div>";
    var dlgOptions = {
      title: title,
      message: message,
      size: 'middle',
      shown: function () {
        var $container = $('#' + dlgId);
        var items = SelectiveDatas.getItems('PT_APP_FUNCTION_TYPE');
        var selectData = $.map(items, function (item) {
          return {
            text: item.label,
            id: item.value
          };
        });
        selectData.unshift({
          text: '全部',
          id: ''
        });
        formBuilder.buildSelect({
          items: selectData,
          container: $container,
          name: 'functionType',
          diaplay: 'functionTypeName',
          label: '功能类型',
          labelColSpan: '2',
          controlColSpan: '10',
          events: {
            change: function () {
              $container.find('.function-resource').html('');
              buildFunctionResourceSelect2();
            }
          }
        });
        $container.append("<div class='function-resource'></div>");
        var buildFunctionResourceSelect2 = function () {
          formBuilder.buildSelect2({
            select2: {
              serviceName: 'appFunctionMgr',
              defaultBlank: true,
              remoteSearch: true,
              params: {
                functionType: $('#functionType', $container).val(),
                excludeDataUuids: excludeDataUuids.join(';')
              }
            },
            container: $container.find('.function-resource'),
            name: 'functionUuid',
            diaplay: 'functionName',
            label: '功能',
            labelColSpan: '2',
            labelClass: 'required',
            controlColSpan: '10'
          });
        };
        buildFunctionResourceSelect2();
        if (showRight) {
          formBuilder.buildCheckbox({
            items: [
              {
                text: '需权限控制',
                id: '1'
              }
            ],
            container: $container,
            name: 'isProtected',
            diaplay: 'isProtectedName',
            label: '权限资源',
            labelColSpan: '2',
            controlColSpan: '10'
          });
        }
      },
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            var $container = $('#' + dlgId);
            var isProtected = $("input[name='isProtected']", $container).prop('checked');
            var functionUuid = $("input[name='functionUuid']", $container).val();
            if (StringUtils.isBlank(functionUuid)) {
              appModal.error('请选择功能！');
              return false;
            }
            // js 功能添加到后端功能
            if ($('#functionType', $container).val() === 'JavaScript') {
              $.ajax({
                url: '/jsexplainer/registerJavascriptFunction/' + functionUuid,
                type: 'GET',
                async: false,
                contentType: 'json',
                success: function (result) {
                  functionUuid = result.data.uuid;
                }
              });
            }

            var data = {};
            // JDS.call({
            //   service: 'appFunctionMgr.getBean',
            //   data: [functionUuid],
            //   async: false,
            //   success: function (result) {
            server.JDS.restfulGet({
              url: `/proxy/api/app/pagemanager/getFunctionBean/${functionUuid}`,
              async: false,
              success: function (result) {
                data = result.data;
              }
            });
            data.isProtected = isProtected;
            if ($.isFunction(callback)) {
              callback.call(this, data);
            }
            return true;
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default'
        }
      }
    };
    appModal.dialog(dlgOptions);
  };
  return AppFunctionDialog;
});
