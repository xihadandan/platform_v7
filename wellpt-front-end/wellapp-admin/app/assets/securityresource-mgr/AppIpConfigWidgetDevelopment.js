define(['constant', 'commons', 'server', 'appContext', 'appModal', 'multiOrg', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  multiOrg,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppIpConfigWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppIpConfigWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      showTable();
      initData();

      var $element = this.widget.$element;

      //开关按钮
      $('.switch-button', $element)
        .off()
        .on('click', function () {
          if ($(this).hasClass('active')) {
            // 关闭
            $(this).removeClass('active');
            $(this).prev().val('0');
          } else {
            $(this).addClass('active');
            $(this).prev().val('1');
          }
        });

      var beans = [];

      $('.btn-add-rows')
        .off()
        .on('click', function () {
          var bean = {
            uuid: null,
            applyTo: $(this).data('apply'),
            userIds: null,
            usernames: null,
            ipAddress1: null,
            ipAddress2: null,
            domainAddress1: null,
            domainAddress2: null,
            id: new Date().getTime()
          };
          var id = $(this).attr('id');
          var newId = getId(id);
          if (newId == 'ip_sms_verify_list') {
            $('#sms_valid_period').show();
          }
          addTableRow($('#' + newId), bean);
        });

      $('.btn-del-rows')
        .off()
        .on('click', function () {
          var id = $(this).attr('id');
          var newId = getId(id);
          var field = newId == 'domain_login_limit_list' ? 'domainAddress1' : 'ipAddress1';
          delTableRow($('#' + newId), field);
        });

      // 允许登录本系统的用户
      $('#usernames')
        .off()
        .on('click', function () {
          $.unit2.open({
            labelField: 'usernames',
            valueField: 'userIds',
            title: '选择用户',
            selectTypes: 'U',
            type: 'MyUnit'
          });
        });

      // 保存用户信息
      $('#ip_config_btn_save')
        .off()
        .on('click', function () {
          var data1 = $('#login_verify_code_list').bootstrapTable('getData'),
            data2 = $('#ip_login_limit_list').bootstrapTable('getData'),
            data3 = $('#ip_sms_verify_list').bootstrapTable('getData'),
            data4 = $('#domain_login_limit_list').bootstrapTable('getData');

          var newBean = data1.concat(data2, data3, data4);
          for (var i = 0; i < newBean.length; i++) {
            var bean = newBean[i];
            if (bean.applyTo == '3') {
              var validPeriod = $('#validPeriod').val();
              bean.validPeriod = validPeriod != null ? $.trim(validPeriod) : validPeriod;
            }
            bean.rowStatus = 'edited';
            beans.push(bean);
          }
          // 登录安全
          $.ajax({
            url: '/web/login/config/saveLoginSecurityConfig',
            type: 'POST',
            data: JSON.stringify({ isAllowMultiDeviceLogin: $('#isAllowMultiDeviceLogin').val() == '1' }),
            dataType: 'json',
            contentType: 'application/json'
          });
          // 系统访问用户
          var object = {};
          object.usernames = $('#usernames').val();
          object.userIds = $('#userIds').val();
          JDS.call({
            service: 'systemAccessFacadeService.saveAllFromMap',
            data: [object],
            async: false,
            version: '',
            success: function (result) {}
          });
          // IP配置
          JDS.call({
            service: 'ipSecurityConfigFacadeService.saveAllBean',
            data: [beans],
            version: '',
            success: function (result) {
              appModal.success('保存成功！', function () {
                location.reload();
              });
            }
          });
        });

      function getId(id) {
        var newId = id.substring(4, id.length - 3) + 'list';
        return newId;
      }

      function showTable() {
        var datas = [
          {
            column: [
              {
                checkbox: true
              },
              {
                field: 'uuid',
                title: 'uuid',
                visible: false
              },
              {
                field: 'applyTo',
                title: 'applyTo',
                visible: false
              },
              {
                field: 'ipAddress1',
                title: '需要校验的IP地址(段)',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'ipAddress2',
                title: '不校验的IP地址(段)',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              }
            ],
            ele: '#login_verify_code_list'
          },
          {
            column: [
              {
                checkbox: true
              },
              {
                field: 'uuid',
                title: 'uuid',
                visible: false
              },
              {
                field: 'applyTo',
                title: 'applyTo',
                visible: false
              },
              {
                field: 'userIds',
                title: 'userIds',
                visible: false
              },
              {
                field: 'usernames',
                title: '用户',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'ipAddress1',
                title: '允许登录的IP地址(段)',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'ipAddress2',
                title: '禁止登录的IP地址(段)',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              }
            ],
            ele: '#ip_login_limit_list'
          },
          {
            column: [
              {
                checkbox: true
              },
              {
                field: 'uuid',
                title: 'uuid',
                visible: false
              },
              {
                field: 'applyTo',
                title: 'applyTo',
                visible: false
              },
              {
                field: 'userIds',
                title: 'userIds',
                visible: false
              },
              {
                field: 'usernames',
                title: '用户',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'ipAddress1',
                title: '需要二次验证的IP地址(段)',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'ipAddress2',
                title: '不二次验证的IP地址(段)',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              }
            ],
            ele: '#ip_sms_verify_list'
          },
          {
            column: [
              {
                checkbox: true
              },
              {
                field: 'uuid',
                title: 'uuid',
                visible: false
              },
              {
                field: 'applyTo',
                title: 'applyTo',
                visible: false
              },
              {
                field: 'userIds',
                title: 'userIds',
                visible: false
              },
              {
                field: 'usernames',
                title: '用户',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'domainAddress1',
                title: '允许登录的域',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'domainAddress2',
                title: '禁止登录的域',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              }
            ],
            ele: '#domain_login_limit_list'
          }
        ];
        for (var i = 0; i < datas.length; i++) {
          initTable(datas[i].ele, datas[i].column);
        }
      }

      function initTable(ele, columns) {
        $(ele).bootstrapTable({
          data: [],
          idField: 'uuid',
          striped: false,
          showColumns: false,
          uniqueId: 'uuid',
          width: 500,
          undefinedText: '',
          columns: columns,
          onClickCell: function (field, value, row, $element) {
            if (field == 'usernames') {
              $('#userName').val(value);
              $('#userId').val(row.userIds);
              $.unit2.open({
                labelField: 'userName',
                valueField: 'userId',
                title: '选择用户',
                selectTypes: 'U',
                type: 'MyUnit',
                close: function (e) {
                  console.log(1, $('#userName').val(), $('#userId').val());
                  value = $('#userName').val();
                  if (value.length === 0) {
                    $element.find('a').addClass('editable-empty');
                  } else {
                    $element.find('a').removeClass('editable-empty');
                  }
                  $element.find('a').attr('title', value);
                  $element.find('a').html(value);
                  row.userIds = $('#userId').val();
                  row.usernames = $('#userName').val();
                }
              });
            }
          }
        });
      }

      function addTableRow(ele, data) {
        // 插入行数据
        ele.bootstrapTable('insertRow', { index: 0, row: data });
      }

      function delTableRow(ele, fields) {
        // 删除行数据
        var rowids = ele.bootstrapTable('getSelections');
        if (rowids.length == 0) {
          appModal.error('请选择记录!');
          return;
        }
        appModal.confirm('确定删除记录？', function () {
          var fieldsVal = [];
          for (var i = 0; i < rowids.length; i++) {
            rowids[i].id ? (fields = 'id') : (fields = 'uuid');
            rowids[i].rowStatus = 'deleted';
            fieldsVal.push(rowids[i][fields]);
            beans.push(rowids[i]);
          }

          ele.bootstrapTable('remove', {
            field: fields,
            values: fieldsVal
          });
          if (ele.attr('id') == 'ip_sms_verify_list') {
            var datas = $('#ip_sms_verify_list').bootstrapTable('getData');
            if (datas.length <= 0) {
              $('#sms_valid_period').hide();
            }
          }
        });
      }

      function initData() {
        JDS.call({
          service: 'systemAccessFacadeService.getAllAsMap',
          data: [],
          version: '',
          success: function (result) {
            var data = result.data;
            $('#usernames').val(data.usernames);
            $('#userIds').val(data.userIds);
          }
        });

        $('#sms_valid_period').hide();

        JDS.call({
          service: 'ipSecurityConfigFacadeService.getAllBean',
          data: [],
          version: '',
          success: function (result) {
            $(result.data).each(function () {
              var data = {
                uuid: this.uuid,
                applyTo: this.applyTo,
                userIds: this.userIds,
                usernames: this.usernames,
                ipAddress1: this.ipAddress1,
                ipAddress2: this.ipAddress2,
                domainAddress1: this.domainAddress1,
                domainAddress2: this.domainAddress2
              };
              var ele = '';
              if (this.applyTo === '1') {
                ele = $('#login_verify_code_list');
              } else if (this.applyTo === '2') {
                ele = $('#ip_login_limit_list');
              } else if (this.applyTo === '4') {
                ele = $('#domain_login_limit_list');
              } else if (this.applyTo === '3') {
                ele = $('#ip_sms_verify_list');

                if (this.validPeriod != null) {
                  $('#validPeriod').val(this.validPeriod);
                }

                $('#sms_valid_period').show();
              }
              addTableRow(ele, data);
            });
          }
        });

        $.get('/web/login/config/getLoginSecurityConfig', {}, function (res) {
          if (res.data) {
            $('#isAllowMultiDeviceLogin', $element).val(res.data.isAllowMultiDeviceLogin ? '1' : '0');
            if (!res.data.isAllowMultiDeviceLogin) {
              $('#isAllowMultiDeviceLoginSwitch', $element).removeClass('active');
            }
          }
        });
      }
    }
  });
  return AppIpConfigWidgetDevelopment;
});
