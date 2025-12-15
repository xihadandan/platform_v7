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
  var $container_exp = undefined;
  var _self = undefined;
  var numTest = true; //验证导出数据拆分是否为整数
  var currentUserUUID = SpringSecurityUtils.getCurrentUserUnitId(); //当前用户UUID
  var dataType_first_id = '864001'; //数据类别，组织数据项对应的id值

  // 平台应用_数据导入导出管理_数据导出页二开
  var AppDataExportWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppDataExportWidgetDevelopment, HtmlWidgetDevelopment, {
    // 初始化回调
    init: function () {
      _self = this;
      $container_exp = this.widget.element;

      //导出数据拆分，默认值
      $('#dataSize_exp').val('10,000');
      numTest = true;

      _self.initDataType(); //初始化数据类型
      _self.initFolder(); //初始化默认文件地址
      _self.getAttributionData(); //初始化数据归属
      _self.bindEvent();
    },
    //导出
    saveData: function () {
      //导出到，必填
      var dataFolder = $('#dataFolder_exp').val();
      if (dataFolder) {
        appCommonJS.valiPath('#dataFolder_exp', dataFolder, '请输入有效的磁盘路径！'); //需要调接口，会延后
      } else {
        appCommonJS.valiInput('#dataFolder_exp', dataFolder, '请输入导出的数据路径！');
      }

      //数据类型，必填
      var dataType = _self.getDataTypeValue();

      //数据归属，必填
      var dataAttribution = $('#dataAttribution_exp').val();
      appCommonJS.valiInput('#dataAttribution_exp', dataAttribution, '请选择数据归属！');
      dataAttribution = dataAttribution.replace(/;/g, ',');
      var dataAttributionName = _.map($('#dataAttribution_exp').wellSelect('data'), function (item) {
        return item.text;
      });
      dataAttributionName = dataAttributionName.join(',');

      //导出数据拆分，正整数
      appCommonJS.valiInput('#dataSize_exp', numTest, '请输入正整数！');

      setTimeout(function () {
        // console.log("errorlen")
        var errorlen = $('#data_export_form').find('.error:visible').length;
        //存在错误提示
        if (errorlen > 0) {
          return false;
        }
        var dataSize = $('#dataSize_exp').val();
        if (dataSize) {
          dataSize = dataSize.replace(/,/g, '');
        }
        var message = '即将导出' + dataType.names + '，是否确定？';
        var option = {
          title: '确认提示',
          size: 'small',
          message: message,
          callback: function (callue) {
            if (callue) {
              var params = {
                dataType: dataType.names,
                dataTypeJson: JSON.stringify(dataType.dataJson), //"{'org_data':{'version':'1','user':'1','group':'1','type':'1','duty':'1','rank':'1'},'flow_data':'1','email_data':{'email':{'receivce':'1','send':'1','draft':'0','recovery':'0'},'folder':'1','contact_group':'1','contact':'1','tag':'1'}}",
                systemUnitIds: dataAttribution,
                systemUnitNames: dataAttributionName,
                exportPath: dataFolder,
                batchQuantity: dataSize
              };
              _self.saveDataReq(params);
            }
          }
        };
        appModal.confirm(option);
      }, 500);
    },
    //获取导出数据类型选中的值
    getDataTypeValue: function (id) {
      var data = []; //记录选中的子类
      var dataName = []; //记录选中的类型名
      var dataNameJson = {};
      var dataJson = {};
      var $index = 0; //当前id类型index
      var haserror = false;
      _.each(appCommonJS.dataTypeListExp, function (item, index) {
        dataJson[item.id] = {};
        data[index] = [];
        if ($('#' + item.id + '_exp').attr('checked')) {
          _.each(item.children, function (citem) {
            if (item.id == 'flow_data') {
              //流程数据
              if ($('#' + citem.id + '_exp').attr('checked')) {
                if (dataName.indexOf(item.text) == -1) {
                  dataName.push(item.text); //类型名
                  dataNameJson[item.text] = [];
                }
                dataJson[item.id] = 1;
                data[index].push(item.id);
                dataNameJson[item.text].push(item.children[0].text);
              } else {
                appCommonJS.valiInput('#dataType_exp', false, '请选择导出流程数据！', true);
                haserror = true;
              }
            } else if (citem.id != 'email') {
              if ($('#' + citem.id + '_exp').attr('checked')) {
                dataJson[item.id][citem.id] = 1;
                if (dataName.indexOf(item.text) == -1) {
                  dataName.push(item.text); //类型名
                  dataNameJson[item.text] = [];
                }
                data[index].push(citem.id);
                dataNameJson[item.text].push(citem.text);
              } else {
                dataJson[item.id][citem.id] = 0;
              }
              if (id == citem.id) {
                $index = index;
              }
            } else {
              //邮件有子类
              dataJson[item.id][citem.id] = {};
              var hasCheck = false;
              _.each(citem.children, function (gitem) {
                if ($('#' + citem.id + '_exp').attr('checked')) {
                  if ($('#' + gitem.id + '_exp').attr('checked')) {
                    dataJson[item.id][citem.id][gitem.id] = 1;
                    if (dataName.indexOf(item.text) == -1) {
                      dataName.push(item.text); //类型名
                      dataNameJson[item.text] = [];
                    }
                    data[index].push(gitem.id);
                    if (!hasCheck) {
                      dataNameJson[item.text][citem.text] = [];
                      hasCheck = true;
                    }
                    dataNameJson[item.text][citem.text].push(gitem.text);
                  } else {
                    dataJson[item.id][citem.id][gitem.id] = 0;
                  }
                  if (id == gitem.id) {
                    $index = index;
                  }
                } else {
                  dataJson[item.id][citem.id][gitem.id] = 0;
                }
                if (id == citem.id) {
                  $index = index;
                }
              });
            }
          });
        } else {
          if (item.id == 'flow_data') {
            //流程数据
            dataJson[item.id] = 0;
          }
          _.each(item.children, function (citem) {
            if (citem.id != 'email') {
              dataJson[item.id][citem.id] = 0;
            } else {
              dataJson[item.id][citem.id] = {};
              _.each(citem.children, function (gitem) {
                dataJson[item.id][citem.id][gitem.id] = 0;
              });
            }
          });
        }
      });
      data = _.flatten(data);
      if (!haserror) {
        appCommonJS.valiInput('#dataType_exp', data.length, '请选择导出数据类型！', true);
      }
      //数据类型子类显示值
      var dataNameJsonStr = '';
      _.each(dataName, function (item, index) {
        dataNameJsonStr += item + '（';
        if (item == '邮件') {
          var emails = dataNameJson[item]['邮件'];
          if (emails && emails.length) {
            dataNameJsonStr += '邮件（';
            dataNameJsonStr += dataNameJson[item]['邮件'].join('，');
            dataNameJsonStr += '）';
            dataNameJson[item] = dataNameJson[item].slice();
            if (dataNameJson[item].length) {
              dataNameJsonStr += '，';
            }
          }
          if (dataNameJson[item].length) {
            dataNameJsonStr += dataNameJson[item].join('，');
          }
        } else {
          dataNameJsonStr += dataNameJson[item].join('，');
        }
        dataNameJsonStr += '）';
        if (index != dataName.length - 1) {
          dataNameJsonStr += '<br/>';
        }
      });
      return {
        ids: data,
        names: dataName.join(','),
        dataJson: dataJson
      };
    },
    //导出接口请求
    saveDataReq: function (params) {
      var data = {
        url: '/api/dataIO/exportData',
        params: JSON.stringify(params),
        type: 'POST'
      };
      appCommonJS
        .ajaxFunction(data)
        .then(function (res) {
          if (res.code == 0) {
            appModal.success('开始导出数据');
            _self.clearAll();
          } else {
            appModal.error(res.msg || '操作失败');
          }
        })
        .catch(function (err) {
          // console.error(err.msg);
        });
    },
    //初始化 导出数据类型，监听数据类型变化事件
    initDataType: function () {
      var dataTypeList = appCommonJS.dataTypeListExp;
      $('#dataType_exp').empty();
      _.each(dataTypeList, function (item, index) {
        var $div = $('<div>', {
          id: item.id + '_div_exp'
        });
        $('#dataType_exp').append($div);
        _self.initCheckbox($div, item);
        $div.find('input').addClass('parent-item');
        var $child = $('<div>', {
          id: item.id + '_child_exp',
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
      $('#dataType_exp').append(
        '<label class="error iconfont icon-ptkj-wentiziduantishi" style="text-align: left;display: none;"></label>'
      );
      //父级点击事件
      $('.parent-item', '#dataType_exp', $container_exp)
        .off()
        .on('change', function () {
          var $input = $(this);
          var checked = $input.attr('checked');
          var id = $input.attr('id').replace('_exp', '');
          if (checked) {
            //父级选中则显示子项
            $('#' + id + '_child_exp').show();
            //全选子项
            $('#' + id + '_child_exp')
              .find('input')
              .each(function () {
                var $cid = $(this).attr('id');
                if ($cid != 'draft_exp' && $cid != 'recovery_exp') {
                  $(this).attr('checked', 'checked');
                }
              });
          } else {
            $('#' + id + '_child_exp').hide();
          }
          _self.getDataTypeValue();
        });
      $('input', '.child-item', '#dataType_exp', $container_exp)
        .off()
        .on('change', function (e) {
          var id = $(this).attr('id');
          e.stopPropagation();
          _self.getDataTypeValue(id);
        });
    },
    //初始化 设置默认导出文件地址
    initFolder: function () {
      var data = {
        url: '/api/dataIO/defaultFilePath',
        params: {},
        type: 'GET'
      };
      appCommonJS
        .ajaxFunction(data)
        .then(function (res) {
          if (res.code == 0) {
            $('#dataFolder_exp').val(res.data);
          } else {
            appModal.error(res.msg || '操作失败');
          }
        })
        .catch(function (err) {
          // console.error(err.msg);
        });
    },
    //组装导出数据类型复选框
    initCheckbox: function ($element, data) {
      var $input = $('<input type="checkbox" name="' + data.id + '" id="' + data.id + '_exp" value="' + data.id + '"/>');
      var $label = $('<label for="' + data.id + '_exp" data-name="' + data.text + '">&nbsp;' + data.text + '</label>');
      if (data.disabled) {
        //默认选中且不可改
        $input.attr('disabled', true);
        $label.css('color', '#999');
        $input.attr('checked', 'checked');
      }
      $element.append($input).append($label);
    },
    //获取数据归属数据并初始化数据归属下拉框（多选）
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
            $('#dataAttribution_exp', $container_exp)
              .wellSelect('destroy')
              .wellSelect({
                multiple: true, //是否开启多选
                data: resdata,
                valueField: 'dataAttribution_exp',
                labelField: 'dataAttributionName_exp'
              })
              .on('change', function () {
                var data = $(this).val();
                appCommonJS.valiInput('#dataAttribution_exp', data, '请选择数据归属！');
              });
          } else {
            appModal.error(res.msg || '操作失败');
          }
        })
        .catch(function (err) {
          // console.error(err.msg);
        });
    },
    /* 设置到可编辑元素中 */
    setValue2EditableElem: function ($editableElem) {
      if ($editableElem == null) {
        return;
      }
      var value = $editableElem.val();
      if (!value) {
        numTest = true;
        appCommonJS.valiInput('#dataSize_exp', numTest, '请输入正整数！');
        return false;
      }
      if (typeof value === 'string' && value.indexOf(',') > -1) {
        value = value.replace(/,/g, '');
      }
      if (false === $editableElem.is(':focus')) {
        if (value && !_self.test(value)) {
          return;
        }
        value = appCommonJS.getDisplayValue(value);
      }
      if ($editableElem.val() === value) {
        return;
      }
      $($editableElem).val(value);
    },
    //验证输入是否为正整数
    test(num) {
      var reg = /^((?!0)\d{1,9})$/; //最长9位数字，可修改
      if (!num.match(reg)) {
        numTest = false;
        appCommonJS.valiInput('#dataSize_exp', numTest, '请输入正整数！');
        return false;
      } else {
        numTest = true;
        appCommonJS.valiInput('#dataSize_exp', numTest, '请输入正整数！');
        return true;
      }
    },
    //绑定事件
    bindEvent: function () {
      $container_exp
        .on('focus', '#dataSize_exp', function () {
          //设置千位分隔符
          _self.setValue2EditableElem($(this));
        })
        .on('blur', '#dataSize_exp', function () {
          //设置千位分隔符
          _self.setValue2EditableElem($(this));
        })
        .on('click', '.info-close', function () {
          //隐藏顶部提醒
          $('.data-import-export-mod .alert').hide();
        })
        .on('click', '#btn_more_exp', function () {
          //更多说明
          console.log('更多说明');
        })
        .on('blur', '#dataFolder_exp', function () {
          setTimeout(function () {
            //验证导出存放路径
            var dataFolder = $('#dataFolder_exp').val();
            if (dataFolder) {
              appCommonJS.valiPath('#dataFolder_exp', dataFolder, '请输入有效的磁盘路径！');
            } else {
              appCommonJS.valiInput('#dataFolder_exp', dataFolder, '请输入导出的数据路径！');
            }
          }, 0);
        })
        .on('mousedown', '#btn_save_exp', function (e) {
          //导出
          _self.saveData();
        });
    },
    clearAll: function () {
      _self.initDataType(); //初始化数据类型
      _self.initFolder(); //初始化默认文件地址
      $('#dataAttribution_exp').wSelect2('val', '');
      //导出数据拆分，默认值
      $('#dataSize_exp').val('10,000');
      numTest = true;
      $('#data_export_form').find('.error').hide(); //隐藏错误提示
    }
  });
  return AppDataExportWidgetDevelopment;
});
