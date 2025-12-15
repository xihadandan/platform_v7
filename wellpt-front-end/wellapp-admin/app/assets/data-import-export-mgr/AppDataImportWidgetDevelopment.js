define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'layDate',
  'HtmlWidgetDevelopment',
  'AppDataImportExportCommon'
], function (constant, commons, server, appContext, appModal, laydate, HtmlWidgetDevelopment, appCommonJS) {
  var $container_imp = undefined;
  var _self = undefined;
  var currentUserUUID = SpringSecurityUtils.getCurrentUserUnitId(); //当前用户UUID
  var dataType_first_id = 'org_data'; //数据类别，组织数据项对应的id值
  var dataType_user_id = 'user'; //数据类别，用户项对应的id值
  var dataType_zzjg_id = 'version'; //数据类别，组织架构项对应的id值

  // 平台应用_数据导入导出管理_数据导入页二开
  var AppDataImportWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppDataImportWidgetDevelopment, HtmlWidgetDevelopment, {
    // 初始化回调
    init: function () {
      _self = this;
      $container_imp = this.widget.element;

      //源数据UUID字段,默认值
      $('#dataSuuid_imp').val('sUUID');

      _self.initDataType(); //初始化数据类型
      _self.getAttributionData(); //初始化数据归属下拉框
      _self.getOrganizationVersionData(); //初始化组织版本下拉框
      _self.getPwdRules(); //获取密码规则
      _self.bindEvent();
    },
    //导入
    saveData: function () {
      //导入到，必填
      var dataFolder = $('#dataFolder_imp').val();
      if (dataFolder) {
        appCommonJS.valiPath('#dataFolder_imp', dataFolder, '请输入有效的磁盘路径！'); //需要调接口，会延后
      } else {
        appCommonJS.valiInput('#dataFolder_imp', dataFolder, '请输入导入的数据路径！');
      }

      //数据类型，必填
      var dataType = _self.getDataTypeValue();

      //源数据UUID字段，必填
      var dataSuuid = $('#dataSuuid_imp').val();
      appCommonJS.valiInput('#dataSuuid_imp', dataSuuid, '请输入源数据UUID字段！');

      //数据归属，必填
      var dataAttribution = $('#dataAttribution_imp').val();
      appCommonJS.valiInput('#dataAttribution_imp', dataAttribution, '请选择数据归属！');
      var dataAttributionName = $('#dataAttributionName_imp').val();

      //验证并获取组织选择和密码验证
      var ovAndPassword = _self.dataTypeChildItemChange(true);

      setTimeout(function () {
        var errorlen = $('#data_import_form').find('.error:visible').length;
        //存在错误提示
        if (errorlen > 0) {
          return false;
        }
        var repeatStrategy = $('input[name="dataRepeat_imp"]:checked').val();
        var errorStrategy = $('input[name="dataAbnormal_imp"]:checked').val();

        var message = '即将导入' + dataType.names + '，是否确定？';
        var option = {
          title: '确认提示',
          size: 'small',
          message: message,
          callback: function (callue) {
            if (callue) {
              var params = {
                dataType: dataType.names,
                dataTypeJson: JSON.stringify(dataType.dataJson),
                importUnitId: dataAttribution,
                importUnitName: dataAttributionName,
                importPath: dataFolder,
                repeatStrategy: repeatStrategy,
                errorStrategy: errorStrategy,
                versionId: ovAndPassword.dataOV,
                versionName: ovAndPassword.dataOVName,
                settingPwd: ovAndPassword.dataPW,
                sourceUuid: dataSuuid
              };

              appModal.showMask('读取数据包...');
              _self.saveDataReq(params);
            }
          }
        };
        appModal.confirm(option);
      }, 300);
    },
    //获取导入数据类型选中的值
    getDataTypeValue: function () {
      var data = [];
      var dataName = [];
      var dataJson = {};
      _.each(appCommonJS.dataTypeListImp, function (item, index) {
        if ($('#' + item.id + '_imp').attr('checked')) {
          dataJson[item.id] = {};
          dataName.push(item.text); //类型名
          _.each(item.children, function (citem) {
            if (item.id == 'flow_data') {
              //流程数据
              if ($('#' + citem.id + '_imp').attr('checked')) {
                dataJson[item.id] = 1;
                data.push(item.id);
              } else {
                appCommonJS.valiInput('#dataType_imp', false, '请选择导入流程数据！', true);
                haserror = true;
              }
            } else if (citem.id != 'email') {
              if ($('#' + citem.id + '_imp').attr('checked')) {
                dataJson[item.id][citem.id] = 1;
                if (dataName.indexOf(item.text) == -1) {
                  dataName.push(item.text); //类型名
                }
                data.push(citem.id);
              } else {
                dataJson[item.id][citem.id] = 0;
              }
            } else {
              //邮件有子类
              dataJson[item.id][citem.id] = {};
              _.each(citem.children, function (gitem) {
                if ($('#' + citem.id + '_imp').attr('checked')) {
                  if ($('#' + gitem.id + '_imp').attr('checked')) {
                    dataJson[item.id][citem.id][gitem.id] = 1;
                    data.push(citem.id);
                  } else {
                    dataJson[item.id][citem.id][gitem.id] = 0;
                  }
                } else {
                  dataJson[item.id][citem.id][gitem.id] = 0;
                }
              });
            }
          });
        }
      });
      data = _.flatten(data);
      appCommonJS.valiInput('#dataType_imp', data.length, '请选择导入数据类型！', true);
      return {
        ids: data,
        names: dataName.join(','),
        dataJson: dataJson
      };
    },
    //导入接口请求
    saveDataReq: function (params) {
      var data = {
        url: '/api/dataIO/importData',
        params: JSON.stringify(params),
        type: 'POST'
      };
      appCommonJS
        .ajaxFunction(data)
        .then(function (res) {
          appModal.hideMask();
          if (res.code == 0 && res.success) {
            appModal.success('开始导入数据');
            _self.clearAll();
          } else {
            appModal.error(res.msg || '操作失败');
          }
        })
        .catch(function (err) {
          // console.error(err.msg);
        });
    },
    //初始化 导入数据类型，监听数据类型变化事件
    initDataType: function () {
      $('#dataType_imp').empty();
      var dataTypeList = appCommonJS.dataTypeListImp;
      _.each(dataTypeList, function (item, index) {
        var $div = $('<div>', {
          id: item.id + '_div_imp',
          style: 'margin-top: 8px;margin-bottom: 5px;'
        });
        $('#dataType_imp').append($div);
        _self.initRadio($div, item);
        $div.find('input').addClass('parent-item');
        var $child = $('<div>', {
          id: item.id + '_child_imp',
          class: 'child-item'
        });
        _.each(item.children, function (citem, cindex) {
          if (cindex == 0) {
            $div.append($child);
          }
          _self.initCheckbox($child, citem);
          _.each(citem.children, function (ccitem, ccindex) {
            if (ccindex == 0) {
              $child.append("<label style=''>(</label>");
            }
            _self.initCheckbox($child, ccitem);
            if (ccindex == citem.children.length - 1) {
              $child.append("<label style=''>)</label>");
            }
          });
        });
      });
      $('#dataType_imp').append(
        '<label class="error iconfont icon-ptkj-wentiziduantishi" style="text-align: left;display: none;"></label>'
      );
      //父级点击事件
      $("[name='dataType']", '#dataType_imp', $container_imp)
        .off()
        .on('change', function () {
          var id = $("input[name='dataType']:checked").val(); //获取选中的值
          _.each(appCommonJS.dataTypeListImp, function (item) {
            if (item.id == id) {
              $('#' + item.id + '_child_imp').show();
              $('#' + item.id + '_child_imp')
                .find('input')
                .each(function () {
                  $(this).attr('checked', 'checked');
                });
            } else {
              $('#' + item.id + '_child_imp').hide();
              $('#' + item.id + '_child_imp')
                .find('input')
                .each(function () {
                  $(this).removeAttr('checked');
                });
            }
            _self.getDataTypeValue();
            _self.dataTypeChildItemChange();
          });
        });
      $('.child-item', '#dataType_imp', $container_imp)
        .off()
        .on('change', function (e) {
          e.stopPropagation();
          var id = $(this).attr('id');
          if (id == dataType_first_id + '_child_imp') {
            //主要监听组织数据的用户和组织架构是否变化
            _self.dataTypeChildItemChange();
          }
          _self.getDataTypeValue();
        });
    },
    //用户复选框变化  组织架构复选框变化
    dataTypeChildItemChange: function (isvali) {
      var checked_user = $('#dataType_imp #' + dataType_user_id + '_imp').attr('checked');
      var checked_zzjg = $('#dataType_imp #' + dataType_zzjg_id + '_imp').attr('checked');
      var dataOrganizationVersion = '';
      var dataOrganizationVersionName = '';
      var dataPassword = '';
      if (checked_user) {
        //用户选中时，密码显示
        $('.dataPassword_imp').show(); //设置登录密码
        dataPassword = $('#dataPassword_imp').val();
        if (isvali) {
          _self.checkUserPwd();
        }
      } else {
        $('.dataPassword_imp').hide(); //设置登录密码
        appCommonJS.valiInput('#dataPassword_imp', true);
      }
      if (checked_user || checked_zzjg) {
        //用户或组织架构选中时，组织版本显示
        $('.dataOrganizationVersion_imp').show(); //选择组织版本
        dataOrganizationVersion = $('#dataOrganizationVersion_imp').val();
        dataOrganizationVersionName = $('#dataOrganizationVersionName_imp').val();
        if (isvali) {
          appCommonJS.valiInput('#dataOrganizationVersion_imp', dataOrganizationVersion, '请选择组织版本！');
        }
      } else if (!checked_user && !checked_zzjg) {
        $('.dataOrganizationVersion_imp').hide(); //选择组织版本
        appCommonJS.valiInput('#dataOrganizationVersion_imp', true);
      }
      return {
        dataOV: dataOrganizationVersion, //组织版本值
        dataOVName: dataOrganizationVersionName,
        dataPW: dataPassword //登录密码
      };
    },
    //组装导入数据类型单选框
    initRadio: function ($element, data) {
      var $input = $('<input type="radio" name="dataType" id="' + data.id + '_imp" value="' + data.id + '"/>');
      var $label = $('<label for="' + data.id + '_imp" data-name="' + data.text + '">&nbsp;' + data.text + '</label>');
      $element.append($input).append($label);
    },
    //组装导入数据类型复选框
    initCheckbox: function ($element, data) {
      var $input = $('<input type="checkbox" name="' + data.id + '" id="' + data.id + '_imp" value="' + data.id + '"/>');
      var $label = $('<label for="' + data.id + '_imp" data-name="' + data.text + '">&nbsp;' + data.text + '</label>');
      if (data.disabled) {
        //默认选中且不可改
        $input.attr('disabled', true);
        $label.css('color', '#999');
        $input.attr('checked', 'checked');
      }
      $element.append($input).append($label);
    },
    //获取数据归属数据并初始化数据归属下拉框（单选）
    getAttributionData: function () {
      var data = {
        url: '/api/dataIO/querySystemUnitList',
        params: {},
        type: 'GET'
      };
      appCommonJS
        .ajaxFunction(data)
        .then(function (res) {
          if (res.code == 0) {
            var resdata = _.map(res.data, function (item) {
              return {
                id: item.id,
                text: item.name
              };
            });
            $('#dataAttribution_imp', $container_imp)
              .wellSelect('destroy')
              .wellSelect({
                data: resdata,
                valueField: 'dataAttribution_imp',
                labelField: 'dataAttributionName_imp'
              })
              .on('change', function () {
                var data = $(this).val();
                appCommonJS.valiInput('#dataAttribution_imp', data, '请选择数据归属！');
                $('#dataOrganizationVersion_imp').val('');
                _self.getOrganizationVersionData(data);
              });
          } else {
            appModal.error(res.msg || '操作失败');
          }
        })
        .catch(function (err) {
          // console.error(err.msg);
        });
    },
    //获取数据归属数据选项下的组织版本并初始化下拉框（单选）
    getOrganizationVersionData: function (dataAttribution) {
      if (dataAttribution) {
        var data = {
          url: '/api/org/version/getVersionBySystemUnitId',
          params: {
            systemUnitId: dataAttribution
          },
          type: 'GET'
        };
        appCommonJS
          .ajaxFunction(data)
          .then(function (res) {
            if (res.code == 0) {
              var resdata = _.map(res.data, function (item) {
                return {
                  id: item.id,
                  text: item.fullName
                };
              });
              $('#dataOrganizationVersion_imp', $container_imp)
                .wellSelect('destroy')
                .wellSelect({
                  data: resdata,
                  valueField: 'dataOrganizationVersion_imp',
                  labelField: 'dataOrganizationVersionName_imp'
                })
                .on('change', function () {
                  var data = $(this).val();
                  appCommonJS.valiInput('#dataOrganizationVersion_imp', data, '请选择组织版本！');
                });
            } else {
              appModal.error(res.msg || '操作失败');
            }
          })
          .catch(function (err) {
            // console.error(err.msg);
          });
      } else {
        $('#dataOrganizationVersion_imp', $container_imp).wellSelect('destroy').wellSelect({
          data: [],
          valueField: 'dataOrganizationVersion_imp',
          labelField: 'dataOrganizationVersionName_imp'
        });
      }
    },
    //获取密码规则
    getPwdRules: function () {
      $.ajax({
        type: 'GET',
        url: ctx + '/api/pwd/setting/getMultiOrgPwdSetting',
        dataType: 'json',
        success: function (result) {
          _self.rules = result.data;
          var latter = _self.rules.letterAsk == 'LA02' ? '至少包含2种' : _self.rules.letterAsk == 'LA01' ? '至少包含1种' : '包含3种';
          var minLength = _self.rules.minLength || 4;
          var maxLength = _self.rules.maxLength || 20;
          var letterLimited = _self.rules.letterLimited == 'LL01' ? '(必须要有大写、小写)' : '';
          _self.placeholder = '字母' + letterLimited + '、数字、特殊字符中' + latter + '，' + minLength + '~' + maxLength + '位';
          $('#dataPassword_imp', $container_imp).attr('placeholder', _self.placeholder);
        }
      });
    },
    //验证密码规则
    checkUserPwd: function () {
      var isSave = true;
      var latter = _self.rules.letterAsk;
      var minLength = _self.rules.minLength || 4;
      var maxLength = _self.rules.maxLength || 20;
      var latterRegLower = /[a-z]+/;
      var latterRegupper = /[A-Z]+/;
      var latters = /[a-zA-Z]+/;
      var numReg = /[0-9]+/;
      var others = /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]+/im;

      var password = $('#dataPassword_imp').val();

      if (minLength > password.length || maxLength < password.length) {
        isSave = false;
      } else if (/[\u4E00-\u9FA5]+/.test(password)) {
        isSave = false;
      } else {
        var i = 0;
        if (latterRegLower.test(password) || latterRegupper.test(password)) {
          i++;
        }

        if (numReg.test(password)) {
          i++;
        }
        if (others.test(password)) {
          i++;
        }
        if (i - (latter == 'LA02' ? '2' : latter == 'LA01' ? '1' : '3') < 0) {
          isSave = false;
        } else if (
          _self.rules.letterLimited == 'LL01' &&
          latters.test(password) &&
          (!latterRegLower.test(password) || !latterRegupper.test(password))
        ) {
          isSave = false;
        }
      }

      appCommonJS.valiInput('#dataPassword_imp', isSave, '不符合密码格式：' + _self.placeholder);
      return password;
    },
    //绑定事件
    bindEvent: function () {
      $container_imp
        .on('click', '.info-close', function () {
          //隐藏顶部提醒
          $('.data-import-export-mod .alert').hide();
        })
        .on('click', '#btn_more', function () {
          //更多说明
          console.log('更多说明');
        })
        .on('mousedown', '#btn_save_imp', function () {
          //导入
          _self.saveData();
        })
        .on('click', '.password-eye-btn', function (e) {
          //密码显示隐藏
          e.stopPropagation();
          var $this = $(this);
          var $span = $this.find('span');
          var _iconClass = $span.attr('class');
          var $input = $this.siblings('input');
          if ($this.hasClass('close')) {
            $input.attr('type', 'text');
            $this.removeClass('close').addClass('open');
            $span.removeClass(_iconClass).addClass('iconfont icon-wsbs-yincang');
          } else {
            $input.attr('type', 'password');
            $this.removeClass('open').addClass('close');
            $span.removeClass(_iconClass).addClass('iconfont icon-wsbs-xianshi');
          }
        })
        .on('blur', '#dataPassword_imp', function () {
          //验证密码
          _self.checkUserPwd();
        })
        .on('blur', '#dataFolder_imp', function () {
          //验证导入存放路径
          var dataFolder = $('#dataFolder_imp').val();
          if (dataFolder) {
            appCommonJS.valiPath('#dataFolder_imp', dataFolder, '请输入有效的磁盘路径！');
          } else {
            appCommonJS.valiInput('#dataFolder_imp', dataFolder, '请输入导入的数据路径！');
          }
        })
        .on('blur', '#dataSuuid_imp', function () {
          //验证源数据UUID字段
          var dataSuuid = $('#dataSuuid_imp').val();
          appCommonJS.valiInput('#dataSuuid_imp', dataSuuid, '请输入源数据UUID字段！');
        });
    },
    clearAll: function () {
      //源数据UUID字段,默认值
      $('#dataSuuid_imp').val('sUUID');
      _self.initDataType(); //初始化数据类型
      $('#dataFolder_imp').val(''); //数据路径
      $('#dataPassword_imp').val(''); //密码
      $('#dataOrganizationVersion_imp').wSelect2('val', ''); //组织版本
      $('#dataAttribution_imp').wSelect2('val', ''); //数据归属
      $('input[name="dataRepeat_imp"]')[0].click(); //重复
      $('input[name="dataAbnormal_imp"]')[0].click(); //异常
      _self.dataTypeChildItemChange();
      $('#data_import_form').find('.error').hide(); //隐藏错误提示
    }
  });
  return AppDataImportWidgetDevelopment;
});
