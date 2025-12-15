define(['constant', 'commons', 'server', 'appContext', 'appModal', 'wSelect2', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  wSelect2,
  HtmlWidgetDevelopment
) {
  var Browser = commons.Browser;
  var JDS = server.JDS;

  var AppOrgVerConfLeaderDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgVerConfLeaderDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var orgVersionId = Browser.getQueryString('versionId');
      var systemUnitId = Browser.getQueryString('systemUnitId');
      var orgVersionFullName = Browser.getQueryString('orgVersionFullName');
      $('#systemUnitId').val(systemUnitId);
      $('#orgVersionId').val(orgVersionId);
      $('#orgVersionFullName').val(orgVersionFullName);

      $('#orgVersionFullName')
        .wSelect2({
          serviceName: 'multiOrgVersionFacade',
          queryMethod: 'loadSelectData',
          labelField: 'orgVersionFullName',
          valueField: 'orgVersionId',
          placeholder: '请选择',
          params: {
            unitId: $('#systemUnitId').val()
          },
          multiple: false,
          remoteSearch: false,
          width: '100%',
          height: 250
        })
        .on('change', function () {
          var verId = $('#orgVersionId').val();
          if (verId) {
            getTableData(verId);
          }
        });

      $('#leaderList').bootstrapTable({
        data: [],
        pagination: false,
        striped: false,
        showColumns: false,
        width: 500,
        undefinedText: '',
        clickToSelect: true,
        columns: [
          {
            field: 'uuid',
            title: 'uuid',
            visible: false
          },
          {
            field: 'jobId',
            title: '职位ID/用户ID'
          },
          {
            field: 'jobName',
            title: '职位名称/用户姓名'
          },
          {
            field: 'leaderType',
            title: '领导关系',
            formatter: function (val) {
              if (val == 0) {
                return '负责';
              } else if (val == 1) {
                return '分管';
              }
              return val;
            }
          },
          {
            field: 'underlingName',
            title: '下属'
          }
        ]
      });

      var verId = $('#orgVersionId').val();
      getTableData(verId);

      function getTableData(verId) {
        JDS.call({
          service: 'multiOrgTreeNodeService.queryAllLeaderListByVerId',
          async: false,
          data: [verId],
          version: '',
          success: function (result) {
            if (result.data) {
              for (var i = 0; i < result.data.length; i++) {
                var item = result.data[i];
                if (item) {
                  $('#leaderList').bootstrapTable('insertRow', {
                    index: 0,
                    row: item
                  });
                }
              }
            }
          }
        });
      }
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppOrgVerConfLeaderDevelopment;
});
