$(function () {
  /***************************************************************************************可编辑流水号对外调用接口****************************************************************************************************/

  var _headPart = '';
  var _lastPart = '';
  var _pointer = '';
  var _serialVal = '';
  var incremental, initialValue;
  var _formUuid;
  var _formField;
  var _automaticNumberSupplement;
  var _initSelectedValue;
  var filleNumberBtnFlg = false;
  //加载时判断可编辑框是否被选中
  $('#iseditor').focus(function () {
    document.getElementById('serialName').options.length = 1;
    // $(".hide").show();
  });

  //获取可编辑的流水号
  function getAllEditableSerialNumber(designatedId, designatedType, isOverride, formUuid, projection, initSelectedValue) {
    _initSelectedValue = initSelectedValue;
    document.getElementById('serialName').options.length = 1;
    //获取指定id可编辑的流水号
    if (designatedId != '' && designatedId != '请选择') {
      JDS.call({
        service: 'serialNumberService.getByDesignatedId',
        data: [true, designatedId],
        async: false,
        version: '',
        success: function (result) {
          $.each(result.data, function (i) {
            var $option = $('<option>', {
              value: result.data[i].name,
              'data-id': result.data[i].id
            }).text(result.data[i].name);
            $option.data('snm', result.data[i]);
            $('#serialName').append($option);
          });

          if (result.data.length == 1) {
            $('#serialName option:eq(1)').prop('selected', true);
            $('#serialName').trigger('change');
          } else if (!result.data.length) {
            //设置为不可点击
            $('#serialName').attr('disabled', 'disabled');
          }
        }
      });
    } else if (designatedType != '' && designatedType != '请选择') {
      //获取指定类型可编辑的流水号
      JDS.call({
        service: 'serialNumberService.listAllByDesignatedTypes',
        version: '',
        async: false,
        data: [true, designatedType.split(';')],
        success: function (result) {
          $.each(result.data, function (i) {
            //添加名称option选项
            var $option = $('<option>', {
              value: result.data[i].name,
              'data-id': result.data[i].id
            }).text(result.data[i].name);
            $option.data('snm', result.data[i]);
            $('#serialName').append($option);
            if (initSelectedValue && initSelectedValue.sn == result.data[i].name) {
              $('#serialName')
                .find("option[value='" + result.data[i].name + "']")
                .prop('selected', true);
              $('#serialName').trigger('change');
            }
          });

          if (result.data.length == 1) {
            $('#serialName option:eq(1)').prop('selected', true);
            $('#serialName').trigger('change');
          } else if (!result.data.length) {
            //设置为不可点击
            $('#serialName').attr('disabled', 'disabled');
          }
        }
      });
    } else {
      //获取所有可编辑的流水号
      JDS.call({
        service: 'serialNumberService.getByIsEditor',
        async: false,
        data: [true],
        success: function (result) {
          $.each(result.data, function (i) {
            //添加名称option选项
            var $option = $('<option>', {
              value: result.data[i].name,
              'data-id': result.data[i].id
            }).text(result.data[i].name);
            $option.data('snm', result.data[i]);
            $('#serialName').append($option);
          });

          if (result.data.length == 1) {
            $('#serialName option:eq(1)').prop('selected', true);
            $('#serialName').trigger('change');
          }
        }
      });
    }
  }

  //名称改变时将值赋给流水号的option选项
  function getEditableSerialNumberValue(initSelectedValue) {
    var firstLoad = true;
    $('#serialName').change(function () {
      if ($('#serialName').val() != '') {
        $("input[name='serialNum_pointer']").parents('.input-group').show();
      } else {
        $("input[name='serialNum_pointer']").parents('.input-group').hide();
      }
      changStyle($('#serialNum_head_part'));
      changStyle($('#serialNum_last_part'));
      //每次改变选项前都需要清除原来内容
      _headPart = '';
      _lastPart = '';
      document.getElementById('serialMaintain').options.length = 0;
      document.getElementById('head_last_part').options.length = 0;

      $('#serialName option:selected').each(function (i, o) {
        if ($(this).val()) {
          $('#head_last_part').removeAttr('disabled');
          $('#serialMaintain').removeAttr('disabled');
          getDifferentKey($(this).data('id'), firstLoad ? initSelectedValue : null);
        } else {
          $('#head_last_part').attr('disabled', true);
          $('#serialMaintain').attr('disabled', true);
        }
      });

      $('#head_last_part').wellSelect({
        valueField: 'head_last_part',
        remoteSearch: false
      });
      $('#serialMaintain').wellSelect({
        valueField: 'serialMaintain',
        remoteSearch: false
      });
      firstLoad = false;
    });
  }

  //判断所选择的流水号定义在维护中是否有不同的关键字
  function getDifferentKey(id, initSelectedValue) {
    JDS.call({
      service: 'serialNumberService.groupByHeadLast',
      data: [id, filleNumberBtnFlg],
      version: '',
      async: false,
      success: function (result) {
        if (result.data.length > 0) {
          $.each(result.data, function (i) {
            var head_Part = StringUtils.nvl(result.data[i][0]);
            var last_Part = StringUtils.nvl(result.data[i][1]);
            var partVal = head_Part;
            var $option = $('<option>', {
              value: partVal
            }).text(partVal);
            $option.data('head_last_part', result.data[i]);
            $('#head_last_part').append($option);
            if (
              (initSelectedValue && initSelectedValue.headPart == head_Part && initSelectedValue.lastPart == last_Part) ||
              ($('#serialNum_head_part').text() == head_Part && $('#serialNum_last_part').text() == last_Part)
            ) {
              $('#head_last_part')
                .find("option[value='" + partVal + "']")
                .prop('selected', true);
            }
          });
          if (result.data.length > 1) {
            $('#head_last_part').parent().show();
          }
          $('#head_last_part').trigger('change');
        } else {
          $('#head_last_part').parent().hide();
          JDS.call({
            service: 'serialNumberMaintainService.getById',
            data: [id],
            version: '',
            async: false,
            success: function (result) {
              if (result.data.length > 0) {
                $.each(result.data, function (i) {
                  _headPart = StringUtils.nvl(result.data[i].headPart);
                  _lastPart = StringUtils.nvl(result.data[i].lastPart);
                  _pointer = StringUtils.nvl(result.data[i].pointer);
                  serialVal = StringUtils.nvl(result.data[i].keyPart);
                  var $option = $('<option>', {
                    value: serialVal
                  }).text(headPart + pointer + lastPart);
                  $option.data('snm', result.data[i]);
                  $('#serialMaintain').append($option);
                  if (initSelectedValue && initSelectedValue.snm == serialVal) {
                    $('#serialMaintain')
                      .find("option[value='" + serialVal + "']")
                      .prop('selected', true);
                  }
                });
                if (!initSelectedValue) {
                  //如果没有指定，则使用第一个流水号
                  $('#serialMaintain option:eq(0)').prop('selected', true);
                }
              }
              $('#serialMaintain').trigger('change');
            }
          });
        }
      }
    });
  }

  //流水号的option选项改变时将值赋给流水号
  function getFinalEditableSerialNumber(isOverride) {
    $('#filleNumberBtn').on('click', function () {
      filleNumberBtnFlg = true;
      $(this).parents('tr').next().show();
      if (!$('#serialNum').val()) {
        $('#head_last_part').attr('disabled', true);
        $('#serialMaintain').attr('disabled', true);
      } else {
        $('#head_last_part').removeAttr('disabled');
        $('#serialMaintain').removeAttr('disabled');
        $('#serialName').trigger('change');
      }
    });

    $('#serialMaintain').change(function () {
      clearSerialNumberInput();
      var $option = $(this).find('option:selected');
      if ($option.data('snm')) {
        _headPart = StringUtils.nvl($option.data('snm').headPart);
        _lastPart = StringUtils.nvl($option.data('snm').lastPart);
        var newPoint = $option.data('snm').pointer;
        _pointer = newPoint;
        $('#serialNum_pointer').val(StringUtils.nvl(newPoint));
        $('#serialNum_head_part').text(StringUtils.nvl($option.data('snm').headPart));
        $('#serialNum_last_part').text(StringUtils.nvl($option.data('snm').lastPart));
        $('#serialNum').val($option.text());
        _serialVal = $option.val();
      }
    });

    $('#serialNum_pointer').blur(function () {
      var snm = $('#serialName').find('option:selected').data('snm');
      var length = snm.initialValue.length;
      var isFillPosition = snm.isFillPosition;
      var val = $(this).val().trim();
      if (isFillPosition && val != '' && val.length < len) {
        // 补位
        $(this).val((Array(len).join(0) + val).slice(-len));
        //$("#serialNum").val($("#serialNum_head_part").text() + $(this).val() + $("#serialNum_last_part").text());//更新流水号
      }
    });

    $('#head_last_part').change(function () {
      var $option = $(this).find('option:selected');
      if ($option.data('head_last_part')) {
        var head_last_part_val = $option.data('head_last_part');
        var param = {
          serialNumberId: head_last_part_val[2],
          formUuid: _formUuid,
          formField: _formField,
          automaticNumberSupplement: _automaticNumberSupplement,
          queryByHeadAndLast: true,
          headPart: head_last_part_val[0],
          lastPart: head_last_part_val[1]
        };
        document.getElementById('serialMaintain').options.length = 0;
        JDS.call({
          service: 'serialNumberService.generateSerialNumberList',
          data: [param],
          version: '',
          async: false,
          success: function (result) {
            var flg = false;
            for (var i = 0; i < result.data.length; i++) {
              var headPart = StringUtils.nvl(result.data[i].headPart);
              var lastPart = StringUtils.nvl(result.data[i].lastPart);
              var pointer = StringUtils.nvl(result.data[i].pointer);
              var serialVal = headPart + pointer + lastPart;
              var $option = $('<option>', {
                value: serialVal
              }).text(serialVal);
              $option.data('snm', result.data[i]);
              $('#serialMaintain').append($option);
              if (_initSelectedValue && _initSelectedValue.snm == serialVal) {
                $('#serialMaintain')
                  .find("option[value='" + serialVal + "']")
                  .prop('selected', true);
                $('#serialMaintain').trigger('change');
                flg = true;
              }
            }
            if (!flg) {
              if (_automaticNumberSupplement && filleNumberBtnFlg) {
                //如果没有指定，并且 自动补最小流水号 则使用第一个流水号
                $('#serialMaintain option:eq(0)').prop('selected', true);
                $('#serialMaintain').trigger('change');
              } else {
                var data = result.data[result.data.length - 1];
                _headPart = StringUtils.nvl(data.headPart);
                _lastPart = StringUtils.nvl(data.lastPart);
                var serialVal = headPart + data.pointer + lastPart;
                var serialVal = headPart + data.pointer + lastPart;
                $('#serialMaintain')
                  .find("option[value='" + serialVal + "']")
                  .prop('selected', true);
                $('#serialNum_pointer').val(StringUtils.nvl(data.pointer));
                $('#serialNum_head_part').text(StringUtils.nvl(data.headPart));
                $('#serialNum_last_part').text(StringUtils.nvl(data.lastPart));
                $('#serialNum').val(serialVal);
                _serialVal = serialVal;
              }
            }
            $('#serialMaintain').wellSelect({
              valueField: 'serialMaintain',
              remoteSearch: false
            });
          }
        });
      }
    });
  }

  function changStyle($ele) {
    if (StringUtils.isBlank($ele.text())) {
      $ele.css({
        padding: 0
      });
    } else {
      $ele.css({
        padding: '6px 12px'
      });
    }
  }

  function clearSerialNumberInput() {
    $('#serialNum').val('');
    $('#serialNum_head_part').text('');
    $('#serialNum_pointer').val('');
    $('#serialNum_last_part').text('');
  }

  //提交、取消时清除数据
  function clearAll() {
    filleNumberBtnFlg = false;
    document.getElementById('serialName').options.length = 0;
    document.getElementById('serialMaintain').options.length = 0;
    clearSerialNumberInput();
    // $(".hide").hide();
  }

  function generateDialogHtml(dialogOptions) {
    var isFillNumber = dialogOptions.serialNumberFill; //是否补号
    var automaticNumberSupplement = dialogOptions.automaticNumberSupplement; //是否自动补最小流水号
    _automaticNumberSupplement = automaticNumberSupplement;
    var $div = $('<div>', {
      id: 'dialog_form',
      style: ''
    }).append(
      $('<div>', {
        class: 'dialog_form_content dyform clearfix'
      }).append(
        $('<table>', {
          class: 'table table-condensed'
        }).append(
          $('<tr>', {
            class: 'odd'
          }).append(
            $('<td>', {
              class: 'label-td'
            }).text(dialogOptions.serialNumberTypeLabel || '流水号名称'),
            $('<td>', {
              class: 'value',
              style: isFillNumber ? 'border-right:none;' : ''
            }).append(
              $('<div>', {
                class: 'td_class',
                style: ''
              }).append(
                $('<select>', {
                  name: 'serialName',
                  id: 'serialName',
                  style: 'float:left;width:' + (isFillNumber ? '84%' : '100%') + ' '
                })
              )
            ),
            isFillNumber
              ? $('<td>', {
                  style: 'width:1%;border-left:none;'
                }).append(
                  $('<input>', {
                    type: 'button',
                    class: 'btn btn-sm btn-primary',
                    id: 'filleNumberBtn',
                    value: '补号',
                    style: 'margin-left:3px;margin-top:2px; ' + (isFillNumber ? '' : 'display:none;') + ' '
                  })
                )
              : ''
          ),
          $('<tr>', {
            class: 'odd',
            style: 'display:none;'
          }).append(
            $('<td>', {
              class: 'label-td',
              style: 'width:150px;'
            }).text(dialogOptions.serialNumberSelLabel || '可选择流水号'),
            $('<td>', {
              class: 'value',
              colspan: isFillNumber ? '2' : '1'
            }).append(
              $('<div>', {
                class: 'input-group',
                style: 'height:35px;border-radius: 4px;background:transparent;'
              })
                .append(
                  $('<div>', {
                    class: 'input-group-addon',
                    style: 'border:none;background:transparent;padding:0;text-align:left;display:none;'
                  }).append(
                    $('<select>', {
                      name: 'head_last_part',
                      id: 'head_last_part',
                      style: 'float:left;'
                    })
                  )
                )
                .append(
                  $('<div>', {
                    class: 'input-group-addon',
                    style: 'border:none;background:transparent;padding:0;text-align:left;'
                  }).append(
                    $('<select>', {
                      name: 'serialMaintain',
                      id: 'serialMaintain',
                      style: 'float:left;'
                    })
                  )
                )
            )
          ),
          $('<tr>', {
            class: 'odd'
          }).append(
            $('<td>', {
              class: 'label-td',
              style: 'width:150px;'
            }).text(dialogOptions.serialNumberLabel || '流水号'),
            $('<td>', {
              class: 'value',
              colspan: isFillNumber ? '2' : '1'
            }).append(
              $('<div>', {
                class: 'input-group',
                style: 'width:100%;height:35px;border-radius: 4px;background:transparent;'
              }).append(
                $('<div>', {
                  id: 'serialNum_head_part',
                  class: 'serilNumPosition input-group-addon',
                  style: 'border:none;background:transparent;padding:0;'
                }),
                $('<input>', {
                  name: 'serialNum_pointer',
                  id: 'serialNum_pointer',
                  class: 'serilNumPosition form-control',
                  style: 'width:100%;height: 35px;border-radius:4px;',
                  type: 'text'
                }),
                $('<div>', {
                  id: 'serialNum_last_part',
                  class: 'serilNumPosition input-group-addon',
                  style: 'border: none;background:transparent;padding:0;'
                }),
                $('<input>', {
                  name: 'serialNum',
                  id: 'serialNum',
                  style: 'display:none;',
                  type: 'text'
                })
              )
            )
          )
        )
      )
    );

    return $div[0].outerHTML;
  }

  //接收参数，可编辑流水号(指定的流水号id,指定的流水号分类，是否覆盖指针(0:强制不覆盖，1:新指针大于当前指针才覆盖；2：强制覆盖;3:表单数据创建时计算),表单uuid,存放流水号列名)
  getEditableSerialNumber = function (
    designatedId,
    designatedType,
    isOverride,
    formUuid,
    projection,
    element,
    dialogOptions,
    initSelectedValue,
    returnCallback,
    length,
    serialNumberTips
  ) {
    _formUuid = formUuid;
    _formField = projection;
    var json = new Object();
    json.content = generateDialogHtml(dialogOptions);
    json.title = dialogOptions.dialogTitle || '可编辑流水号';
    json.size = 'md';
    var buttons = new Object();
    buttons.确定 = makeSure;
    json.buttons = buttons;
    //showDialog(json);

    appModal.dialog({
      title: dialogOptions.dialogTitle || '可编辑流水号',
      size: 'middle',
      message: generateDialogHtml(dialogOptions),
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            return makeSure();
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default',
          callback: function () {
            clearAll();
          }
        }
      },
      shown: function () {
        $('#serialName').wellSelect({
          valueField: 'serialName',
          remoteSearch: false
        });
        $('#serialNum_pointer').attr('maxlength', length);
        if (isOverride == 3) {
          $('#serialNum_pointer').attr('disabled', 'disabled');
        }
        if (document.getElementById('serialName').disabled) {
          $('#serialName').prev().find('.well-select-placeholder').html('您无流水号的数据权限，请联系管理员');
        }
      }
    });

    getEditableSerialNumberValue(initSelectedValue); //名称改变时将值赋给流水号的option选项
    getFinalEditableSerialNumber(isOverride); //流水号的option选项改变时将值赋给流水号
    getAllEditableSerialNumber(designatedId, designatedType, isOverride, formUuid, projection, initSelectedValue); //获取所有可编辑

    function makeSure() {
      //将流水号加到可编辑框里
      //var serial = $("#serialNum").val();
      var newPointer = $('#serialNum_pointer').val();
      if (!StringUtils.isNotBlank(newPointer)) {
        appModal.warning('请输入流水号指针!'); //oAlert2("请输入流水号指针!", $.noop, "");
        return false;
      }
      var lastSerialNumber = _headPart + newPointer + _lastPart;
      var serialNoDefId = $('#serialName>option:selected').attr('data-id');
      var serialNameSelected = $('#serialName').val();
      var snm = $('#serialMaintain').find('option:selected').data('snm');
      var uuid = snm.uuid;
      var occupied = false;
      //检测当前流水号是否被占用
      var checkResult = isOverride != 3 && checkIsOccupied(formUuid, projection, lastSerialNumber);
      if (checkResult) {
        //oAlert2("该流水号已经被占用!", $.noop, "");
        var tipsMsg = serialNumberTips || '该流水号已经被占用!';
        appModal.alert(tipsMsg);
        return false;
      } else {
        //				oAlert2("该流水号未被占用!");
        //不覆盖
        if (isOverride == 0) {
          //					 oAlert2("流水号："+lastSerialNumber+"强制不更新");
          clearAll();
          $('#editableSerialNumber').dialog('close');
          closeDialog();

          $('#iseditor').val(lastSerialNumber);
        }
        //大于当前指针才覆盖
        if (isOverride == 1) {
          var currentPointer = _pointer - parseInt($('#serialName').find('option:selected').data('snm').incremental);
          //					 oAlert2("新指针："+newPointer);
          if (newPointer > currentPointer) {
            //						 oAlert2("流水号："+lastSerialNumber+"大于当前，更新");
            occupied = savePointer(lastSerialNumber, _headPart, _lastPart, serialNoDefId, serialNameSelected, isOverride, newPointer);
            closeDialog();
            $('#iseditor').val(lastSerialNumber);
          } else {
            //						 oAlert2("流水号："+lastSerialNumber+"小于或等于当前， 不更新",closeDialog,"");
            closeDialog();
            $('#iseditor').val(lastSerialNumber);
          }
        }
        //强制覆盖
        if (isOverride == 2) {
          //					 oAlert2("新指针newPointer："+newPointer);
          occupied = savePointer(lastSerialNumber, _headPart, _lastPart, serialNoDefId, serialNameSelected, isOverride, newPointer);
          //					 oAlert2("流水号："+lastSerialNumber+"强制更新");
          closeDialog();
          $('#iseditor').val(lastSerialNumber);
        }

        if (isOverride == 3) {
          closeDialog();
          $('#iseditor').val(lastSerialNumber);
        }
      }

      //			alert(element);
      if ($.isFunction(returnCallback)) {
        returnCallback(lastSerialNumber, {
          sn: serialNameSelected, // 流水号名称
          snm: lastSerialNumber, // 流水号维护记录
          snid: serialNoDefId, // 流水号ID
          headPart: _headPart,
          lastPart: _lastPart,
          uuid: uuid,
          occupied: occupied
        });
      }
      clearAll();
      //$("#" + element).val(lastSerialNumber);
    }

    //保存指针
    function savePointer(lastSerialNumber, headPart, lastPart, serialNoDefId, serialVal, isOverride, newPointer) {
      var snm = $('#serialMaintain').find('option:selected').data('snm');
      var occupied = false;
      JDS.call({
        service: 'serialNumberMaintainService.savePointer',
        data: [lastSerialNumber, headPart, lastPart, serialNoDefId, serialVal, isOverride, newPointer, snm.uuid],
        version: '',
        async: false,
        success: function (result) {
          if (result && result.success) {
            occupied = result.data;
          }
          //						oAlert2("保存成功！",closeDialog,"");
          clearAll();
        }
      });
      return occupied;
    }

    //检测当前流水号是否已被占用
    function checkIsOccupied(formUuid, projection, lastSerialNumber) {
      var checkResult;
      JDS.call({
        service: 'serialNumberMaintainService.checkIsOccupied',
        data: [formUuid, projection, lastSerialNumber],
        version: '',
        async: false,
        success: function (result) {
          checkResult = result.data;
        }
      });
      return checkResult;
    }
  };
  /******************************************************************************************************************************************************************************************************************/
});
