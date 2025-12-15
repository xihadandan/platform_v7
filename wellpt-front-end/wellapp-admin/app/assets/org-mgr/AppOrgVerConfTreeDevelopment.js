define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var AppOrgVerConfTreeDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppOrgVerConfTreeDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var self = this;
      $('#orgVersionId').val(GetRequestParam().id);
      $('#systemUnitId').val(GetRequestParam().systemUnitId);

      var orgVersionId = $('#orgVersionId').val();
      loadTree(orgVersionId);

      $('#fold_all').click(function () {
        var zTree = $.fn.zTree.getZTreeObj('org_tree');
        var rootNode = zTree.getNodes();
        for (var i = 0; i < rootNode.length; i++) {
          var childNode = rootNode[i];
          zTree.expandNode(childNode, false);
        }
      });

      $('#unfold_all').click(function () {
        $.fn.zTree.getZTreeObj('org_tree').expandAll(true);
      });

      $('#orgFullName').wSelect2({
        serviceName: 'multiOrgVersionFacade',
        queryMethod: 'loadSelectData',
        labelField: 'orgFullName',
        valueField: 'orgVersionId',
        placeholder: '请选择',
        params: {
          unitId: $('#systemUnitId').val()
        },
        multiple: false,
        remoteSearch: false,
        width: '100%',
        height: 250
      });

      $('#orgFullName').on('change', function () {
        if ($('#orgVersionId').val()) {
          loadTree($('#orgVersionId').val());
          var orgUserTable = $('#org_ver_config_tree').parents('.ui-wBootgrid').find('.org_ver_config_table').attr('id');

          $('#' + orgUserTable).wBootstrapTable('addParam', 'versionId', $('#orgVersionId').val());
          $('#' + orgUserTable).wBootstrapTable('removeParam', 'eleIdPath');
          $('#' + orgUserTable).wBootstrapTable('refresh');
        }
      });

      // // 定义导出
      // $("#btn_iexp").on("click", function() {
      //     var path = $("#iexpPath").val();
      //     if (StringUtils.isBlank(path)) {
      //         alert("请选择组织树节点！");
      //         return false;
      //     }
      //     var type = "orgVersion";
      //     $.iexportData["export"]({
      //         uuid : path,
      //         type : type
      //     });
      // });
      // // 定义导入
      // $("#btn_iimp").on("click", function() {
      //     $.iexportData["import"]({
      //         callback : function() {
      //             refreshPage();
      //         }
      //     });
      // });

      // 导出
      $('#btn_exp').on('click', function () {
        var verId = $('#orgVersionId').val();
        var url = getBackendUrl() + '/multi/org/version/export';
        server.FileDownloadUtils.downloadTools(url, { verId: verId });
      });

      // 导入excel信息

      $('#btn_imp').click(function () {
        appModal.confirm('导入组织会清空所有旧数据，请先备份数据 “ MULTI_ORG_ ” 开头的表后再导入', function (success) {
          if (success) {
            var verId = $('#orgVersionId').val();
            var message = getHtml();
            appModal.dialog({
              message: message,
              size: 'middle',
              title: '导入组织',
              shown: function () {
                $('#import_dialog').attr('isUpload', false);
              },
              buttons: {
                confirm: {
                  label: '确定',
                  className: 'btn-primary',
                  callback: function (event) {
                    console.log(arguments);
                    var isUpload = $('#import_dialog').attr('isUpload');
                    if (isUpload == 'true') {
                      appModal.info('正在上传导入中，请不要重复操作');
                      return;
                    }

                    var file = $('#uploadfile').val();
                    if (file == '') {
                      appModal.error('请选择xls文件');
                      return;
                    }
                    if (file.indexOf('.') < 0) {
                      return;
                    }
                    var fileType = file.substring(file.lastIndexOf('.'), file.length);
                    if (fileType == '.xls' || fileType == '.xlsx') {
                      // 暂时屏蔽所有按钮，防止重复点击
                      $('#import_dialog').attr('isUpload', true);
                      appModal.showMask();
                      var url = getBackendUrl() + '/multi/org/version/import?verId=' + verId;
                      var _auth = getCookie('_auth');
                      if (_auth) {
                        url += '&' + _auth + '=' + getCookie(_auth);
                      }
                      $.ajaxFileUpload({
                        url: url, // 链接到服务器的地址
                        secureuri: false,
                        fileElementId: 'uploadfile', // 文件选择框的ID属性
                        dataType: 'text', // 服务器返回的数据格式
                        mask: true,
                        success: function (data, status) {
                          appModal.hideMask();
                          $('#import_dialog').attr('isUpload', false);
                          // QQ浏览器会多返回莫名奇妙的额外数据，所以套个text()方法来过滤脏数据
                          data = $('<div>' + data + '</div>').text();
                          if (data.indexOf('成功') > 0) {
                            appModal.alert(data);
                            self.getWidget().refresh();
                          } else {
                            appModal.alert(data);
                          }
                        },
                        error: function (data) {
                          appModal.hideMask();
                          $('#import_dialog').attr('isUpload', false);
                        }
                      });
                    } else {
                      appModal.error('请选择xls文件');
                      return;
                    }
                  }
                },
                cancel: {
                  label: '取消',
                  className: 'btn-default',
                  callback: function () {
                    var isUpload = $('#import_dialog').attr('isUpload');
                    if (isUpload == 'true') {
                      appModal.info('正在上传导入中，暂时不能取消');
                      return;
                    } else {
                      $('#import_dialog').attr('isUpload', false);
                    }
                  }
                }
              }
            });
          }
        });
      });

      function loadTree(orgVersionId) {
        var setting = {
          callback: {
            beforeClick: function (treeId, treeNode) {
              if (treeNode == null) {
                return;
              }

              var orgUserTable = $('#org_ver_config_tree').parents('.ui-wBootgrid').find('.org_ver_config_table').attr('id');

              $('#' + orgUserTable)
                .wBootstrapTable('addParam', 'eleIdPath', treeNode.data.eleIdPath)
                .wBootstrapTable('refresh');

              $('#iexpPath').val(treeNode.path);
              return true;
            }
          }
        };
        $.common.ztree.initOrgTree('#org_tree', setting, orgVersionId);
      }

      function getHtml() {
        var html = '';
        html +=
          "<div id='import_dialog' class='form-widget'>" +
          "<div class='well-form form-horizontal'>" +
          "<div class='form-group' id='tr_boss'>" +
          "<label for='bossNames' class='well-form-label control-label'>选择XLS文件</label>" +
          "<div class='well-form-control'>" +
          "<input type='file' name='upload' id='uploadfile'>" +
          "<div class='content'><a href='" +
          ctx +
          "/static/resfacade/share/组织数据导入模板_6.0.xls'>组织数据导入模板</a></div>" +
          '</div>' +
          '</div>' +
          '</div>' +
          '</div>';
        return html;
      }

      $('#org_tree').slimScroll({
        height: '600px',
        wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
      });
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppOrgVerConfTreeDevelopment;
});
