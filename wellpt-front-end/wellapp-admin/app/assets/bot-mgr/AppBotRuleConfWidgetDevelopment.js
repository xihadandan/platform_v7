define(['constant', 'commons', 'server', 'appContext', 'appModal', 'wSelect2', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  wSelect2,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppBotRuleConfWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppBotRuleConfWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var $container = this.widget.element;
      var _self = this;
      _self.bean = {
        uuid: null,
        ruleName: null,
        id: null,
        isPersist: null
      };
      _self.ruleValueType = {
        0: 'freemarker表达式',
        1: 'groovy脚本'
      };
      _self.configMapData = {};
      $.common.systemUnit.init('systemUnitId', 'systemUnitName');

      _self.initTable('fieldMappingTable');
      _self.initTable('relaTable');
      _self.initTable('reverseWriteMappingTable');
      _self.initTable('autoFieldMappingTable');

      var uuid = GetRequestParam().uuid || _self.getWidgetParams()['uuid'];
      _self.readonly = GetRequestParam().readonly;
      if (uuid) {
        _self.loadRuleConfigDetail(uuid);
        _self.isDebug();
      } else {
        $.common.idGenerator.generate('#id', 'BTO_');
        $('#id').prop('readonly', '');
        $('#bot_rule_btn_debug').hide();
        _self.initCodeEditor(null);
      }

      $('#transferType').wSelect2({
        defaultBlank: true,
        data: [
          {
            id: 0,
            text: '单据转单据'
          },
          {
            id: 1,
            text: '报文转单据'
          }
        ],
        remoteSearch: false,
        width: '100%',
        height: 250
      });

      $('#transferType').on('change', function () {
        $('.sourceObjTr').hide();
        if ($(this).val() == '0') {
          $('.autoFieldMapping', $container).show();
          $('.sourceObjTr').show();
          //显示字段映射的源字段列
          $('#fieldMappingTable').bootstrapTable('showColumn', 'sourceFieldData').trigger('refresh');
          $('#tab_head li:eq(2),#tab_head li:eq(3)').show();
        } else {
          //隐藏字段映射的源字段列
          $('#fieldMappingTable').bootstrapTable('hideColumn', 'sourceFieldData').trigger('refresh');
          $('#tab_head li:eq(2),#tab_head li:eq(3)').hide();
          $('.autoFieldMapping', $container).hide();
        }
      });

      $('#targetObjName').wSelect2({
        serviceName: 'dyFormFacade',
        queryMethod: 'queryAllPforms',
        selectionMethod: 'getSelectedFormDefinition',
        labelField: 'targetObjName',
        valueField: 'targetObjId',
        params: {
          includeSubform: true
        },
        width: '100%',
        defaultBlank: true
      });

      $('#targetObjName').on('change', function () {
        if ($(this).attr('last_value') == $(this).val()) {
          return;
        }
        var selectData = $(this).select2('data');
        if (selectData) {
          $(this).attr('last_value', $(this).val());
          var fieldData = _self.getFieldData(selectData.id);
          $(this).data('fieldData', fieldData);
          _self.reloadTargetFieldSeleOption('#fieldMappingTable .targetObjFieldSelect', fieldData, false);
          _self.reloadTargetFieldSeleOption('#reverseWriteMappingTable .targetObjFieldSelect', fieldData, true);
        }
        _self.getSameField();
      });

      $('#sourceObjId').wSelect2({
        serviceName: 'dyFormFacade',
        queryMethod: 'queryAllPforms',
        selectionMethod: 'getSelectedFormDefinition',
        labelField: 'sourceObjName',
        valueField: 'sourceObjId',
        width: '100%',
        multiple: true,
        defaultBlank: true,
        remoteSearch: true,
        disorderValue: true
      });

      $('#sourceObjId').on('change', function () {
        _self.reloadSourceObjSelect($('#sourceObjId').val().split(';'), $('#sourceObjName').val().split(';'));
        _self.getSameField();
      });

      $('#relaObjName').wSelect2({
        serviceName: 'dyFormFacade',
        queryMethod: 'queryAllPforms',
        selectionMethod: 'getSelectedFormDefinition',
        labelField: 'relaObjName',
        valueField: 'relaObjId',
        width: '100%',
        defaultBlank: true
      });

      $('#relaObjName').on('change', function () {
        if ($(this).attr('last_value') == $(this).val()) {
          return;
        }
        var selectData = $(this).select2('data');
        if (selectData) {
          $(this).attr('last_value', $(this).val());
          var fieldData = _self.getFieldData(selectData.id, ['uuid']);
          $(this).data('fieldData', fieldData);
          _self.reloadTargetFieldSeleOption('#relaTable .targetObjFieldSelect', fieldData, false);
        }
      });

      $('#transListenerCodeType :input').on('click', function () {
        $("#transScriptContainer div[id^='bot_']").hide();
        $('#bot_' + $(this).attr('id')).show();
      });

      $('.title .switch-wrap', $container)
        .off()
        .on('click', function () {
          if ($(this).hasClass('active')) {
            $(this).removeClass('active');
            $('.autoFieldMappingTable', $container).hide();
            $('#autoMapSameColumn', $container).val('0');
          } else {
            $(this).addClass('active');
            $('.autoFieldMappingTable', $container).show();
            $('#autoMapSameColumn', $container).val('1');
          }
        });

      $container.on('input propertychange', '.keyword-search-wrap input', function () {
        var $this = $(this);
        if ($this.val()) {
          $this.siblings('.close-icon').show();
        } else {
          $this.siblings('.close-icon').hide();
        }
      });

      $container.on('click', '.close-icon', function (e) {
        e.stopPropagation();
        $(this).hide().siblings('input').val('');
        _self._onSearch();
      });

      $container.find(".autoFieldMappingTable button[name='query']").on('click', function () {
        _self._onSearch();
      });

      $container.find('input').on('keypress', function (event) {
        if (event.keyCode === 13) {
          _self._onSearch();
        }
      });

      $('#bot_rule_btn_save').on('click', function () {
        _self.save();
      });

      $('#bot_rule_btn_debug').on('click', function () {
        _self.debugRule();
      });

      //源单据选择，触发源单据字段的数据下拉数据
      $('.subtable').on('change', '.sourceObjSelect', function () {
        var $fieldSelect = $(this).next('select');
        if ($(this).attr('last_value') == $(this).val()) {
          return;
        }
        $(this).attr('last_value', $(this).val());
        if ($(this).val()) {
          var fieldData = _self.getFieldData($(this).val());
          var initField = $fieldSelect.attr('init-field');
          if (fieldData) {
            $fieldSelect.empty();
            $fieldSelect.append($('<option>'));
            for (var f in fieldData) {
              $fieldSelect.append(
                $('<option>', {
                  value: f,
                  selected: f == initField
                }).text(fieldData[f].displayName)
              );
            }
          }
          $(this).removeAttr('init-field');
          $fieldSelect.show();
        } else {
          $fieldSelect.hide();
          $fieldSelect.empty();
        }
      });

      $('#fieldMappingTable,#relaTable,#reverseWriteMappingTable', $container).on('change', '.tableFieldSelect', function () {
        //更新表格数据
        var _field = $(this).attr('field');
        var _index = parseInt($(this).attr('data-index'));
        $(this)
          .parents('table')
          .bootstrapTable('updateCell', {
            index: _index,
            field: _field,
            value: $(this).val(),
            reinit: false
          });
        $(this)
          .parents('table')
          .bootstrapTable('updateCell', {
            index: _index,
            field: _field + 'Name',
            value: $(this).find('option:selected').text(),
            reinit: false
          });
      });

      $('.btn_subtable_add').on('click', function (e) {
        var tableid = _self.getIds($(this).attr('id'));
        var data = {
          id: commons.UUID.createUUID(),
          sourceFieldData: ''
        };
        if (tableid == 'reverseWriteMappingTable') {
          data.isReverseWrite = true;
        }
        $('#' + tableid).bootstrapTable('insertRow', {
          index: 0,
          row: data
        });
        $('#' + tableid + ' .sourceObjSelect').trigger('change');
      });

      $('.btn_subtable_del').on('click', function (e) {
        var tableid = _self.getIds($(this).attr('id'));
        var datas = $('#' + tableid).bootstrapTable('getSelections');
        var ids = [];
        for (var i = 0; i < datas.length; i++) {
          ids.push(datas[i].id);
        }
        if (ids.length === 0) {
          appModal.error('请选择记录！');
          return;
        }
        $('#' + tableid).bootstrapTable('remove', {
          field: 'id',
          values: ids
        });
        $('#' + tableid + ' .sourceObjSelect').trigger('change');
      });

      if (_self.readonly) {
        $container.find('#bot_rule_btn_save').closest('.row').remove();
        setTimeout(function () {
          $container.find('input').attr('disabled', true);
          $container.find('textarea').attr('disabled', true);
          $container.find('select').attr('disabled', true);
          $('#targetObjName').wellSelect('readonly', true);
          $('#transferType').wellSelect('readonly', true);
          $('#sourceObjId').wellSelect('readonly', true);
          $('#relaObjName').wellSelect('readonly', true);
          $container.find('#btn_fieldMappingTable_add').parent().remove();
          $container.find('#btn_relaTable_add').parent().remove();
          $container.find('#btn_reverseWriteMappingTable_add').parent().remove();
        }, 1000);
      }

      $('#autoFieldMappingTable', $container)
        .off('click', '.switch-wrap')
        .on('click', '.switch-wrap', function () {
          if ($(this).hasClass('active')) {
            $(this).removeClass('active');
          } else {
            $(this).addClass('active');
          }
        });
    },

    getIds: function (id) {
      var newId = id.substring(4, id.length - 4);
      return newId;
    },

    collectionObjMappingData: function () {
      var data = [];
      var _tableData1 = $('#fieldMappingTable').bootstrapTable('getData');
      for (var i = 0, len = _tableData1.length; i < len; i++) {
        _tableData1[i].sql = i;
      }
      data = data.concat(_tableData1);
      //反写规则收集
      var _tableData2 = $('#reverseWriteMappingTable').bootstrapTable('getData');
      for (var i = 0, len = _tableData2.length; i < len; i++) {
        _tableData2[i].sql = i;
      }
      data = data.concat(_tableData2);
      for (var i = 0, len = data.length; i < len; i++) {
        if (!data[i].sourceObjField || !data[i].targetObjField) {
          appModal.error('字段必选');
          throw new Error('字段必填');
        }
      }
      return data;
    },

    collectObjRelaData: function () {
      var data = {
        relaObjId: $('#relaObjId').val(),
        relaObjName: $('#relaObjName').val(),
        uuid: $('#relaUuid').val()
      };
      if (data.relaObjId) {
        data.relaMappingDtos = $('#relaTable').bootstrapTable('getData');
      }
      return data.relaObjId ? data : null;
    },

    initCodeEditor: function (data) {
      var _this = this;
      $('#bot_scriptBeforeTrans,#bot_scriptAfterTrans').remove();
      $('#transScriptContainer').append(
        $('<div>', {
          id: 'bot_scriptBeforeTrans',
          style: 'display:none;'
        }),
        $('<div>', {
          id: 'bot_scriptAfterTrans',
          style: 'display:none;'
        })
      );

      $('#transScriptContainer div').each(function () {
        var id = $(this).attr('id');
        var $ace = $.fn.aceBinder({
          container: '#' + id,
          mode: 'groovy',
          iframeId: 'bottransframe' + id,
          id: id,
          value: data ? data[id] : '',
          readOnly: _this.readonly,
          valueChange: function (v) {
            $('#transListenerCodeType :checked').data('scriptCode', v);
          },
          codeHis: {
            enable: true,
            relaBusizUuid: $('#id').val(),
            codeType: id
          },
          varSnippets: 'bot.' + id
        });
      });
    },

    loadRuleConfigDetail: function (id) {
      var _self = this;
      JDS.call({
        service: 'botRuleConfFacadeService.getBotRuleConfigDetail',
        data: [id],
        validate: true,
        version: '',
        success: function (result) {
          if (result.data) {
            _self.bean = result.data;
            $('#rule_conf_form').json2form(result.data);
            $('#id').prop('readonly', true);
            $('#transferType').trigger('change');
            $('#targetObjName').trigger('change');

            var srcObjIds = [];
            var srcObjNames = [];
            $('#fieldMappingTable').bootstrapTable('removeAll');

            if (!!result.data.sourceObjId) {
              $('#sourceObjId').val(result.data.sourceObjId);
            } else {
              for (var i = 0, len = result.data.objMappingDtos.length; i < len; i++) {
                var map = result.data.objMappingDtos[i];
                if (srcObjIds.indexOf(map.sourceObjId) == -1) {
                  srcObjIds.push(map.sourceObjId);
                  srcObjNames.push(map.sourceObjName);
                }
              }
              $('#sourceObjId').val(srcObjIds.join(';'));
            }

            $('#sourceObjName').val(srcObjNames.join(';'));
            console.log('JDS->sourceObjId->change');
            $('#sourceObjId').trigger('change');

            if (_self.bean.autoMapSameColumn) {
              $('.title .switch-wrap').trigger('click');
            }

            for (var i = 0, len = result.data.objMappingDtos.length; i < len; i++) {
              var map = result.data.objMappingDtos[i];
              var $mappingTable = map.isReverseWrite ? $('#reverseWriteMappingTable') : $('#fieldMappingTable');
              if (map.renderValueType != null) {
                _self.configMapData[map.uuid] = {
                  id: map.renderValueType,
                  value: map.renderValueExpression
                };
              }
              map.id = commons.UUID.createUUID();
              $mappingTable.bootstrapTable('insertRow', {
                index: 0,
                row: map
              });
              $(".ruleEdit[map-uuid='" + map.uuid + "']").data('objMap', map);
            }
            //关联关系
            if (result.data.relaDto) {
              $('#relaTable').bootstrapTable('removeAll');
              $('#relaObjId').val(result.data.relaDto.relaObjId);
              $('#relaObjName').val(result.data.relaDto.relaObjName);
              $('#relaObjName').trigger('change');
              $('#relaUuid').val(result.data.relaDto.uuid);
              for (var i = 0, rLen = result.data.relaDto.relaMappingDtos.length; i < rLen; i++) {
                var relaMap = result.data.relaDto.relaMappingDtos[i];
                relaMap.id = commons.UUID.createUUID();
                $('#relaTable').bootstrapTable('insertRow', {
                  index: 0,
                  row: relaMap
                });
              }
            }
            //转换侦听
            if (result.data.scriptAfterTrans != null) {
              $('#scriptAfterTrans').data('scriptCode', result.data.scriptAfterTrans);
            }
            if (result.data.scriptBeforeTrans != null) {
              $('#scriptBeforeTrans').data('scriptCode', result.data.scriptBeforeTrans);
            }

            _self.initCodeEditor({
              bot_scriptBeforeTrans: result.data.scriptBeforeTrans,
              bot_scriptAfterTrans: result.data.scriptAfterTrans
            });

            $('#scriptBeforeTrans').trigger('click');
            $('.sourceObjSelect').trigger('change');
          }
        }
      });
    },

    save: function () {
      var bean = this.bean;
      $('#rule_conf_form').form2json(bean);
      bean.relaDto = this.collectObjRelaData(); //关联关系维护
      bean.objMappingDtos = this.collectionObjMappingData(); //字段映射
      bean.transferType = $('#transferType').select2('data').id;
      bean.targetObjId = $('#targetObjName').select2('data').id;
      bean.targetObjName = $('#targetObjName').select2('data').text;
      bean.sourceObjId = $('#sourceObjId').val();
      //转换侦听代码
      $('#transListenerCodeType :input').each(function () {
        bean[$(this).attr('id')] = $(this).data('scriptCode');
      });
      bean.systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
      //字段匹配映射
      bean.autoMapSameColumn = $('#autoMapSameColumn').val() == '1' ? true : false;
      bean.ignoreMappings = [];
      if (bean.autoMapSameColumn) {
        var autoFieldMappingTable = $('#autoFieldMappingTable').bootstrapTable('getData');
        var $switch = $('#autoFieldMappingTable').find("tr td[data-field='isMapping']").find('.switch-wrap');
        $.each(autoFieldMappingTable, function (index, item) {
          if (!$($switch[index]).hasClass('active')) {
            bean.ignoreMappings.push({
              sourceObjField: item.name,
              targetObjField: item.name,
              sourceObjId: item.sourceObjId,
              targetObjId: item.targetObjId
            });
          }
        });
      }

      appModal.confirm('是否保存', function (result) {
        if (result) {
          JDS.call({
            service: 'botRuleConfFacadeService.saveBotRuleConfig',
            data: [bean],
            validate: true,
            version: '',
            success: function (result) {
              $('body').trigger('ace_$saveCodeHis'); //触发代码变更历史保存
              appModal.success({
                message: '保存成功！',
                callback: function () {
                  if ($('#dialog_page_20200115092455').length == 0) {
                    appContext.getNavTabWidget().closeTab();
                  }
                }
              });
            }
          });
        }
      });
    },

    debugRule: function () {
      if (uuid) {
        var $div = $('<div>', {
          id: 'debugDialog'
        }).append(
          $('<textarea>', {
            id: 'jsonArea',
            style: 'width:100%;height:400px;padding:0;border:none;box-shadow: none;'
          })
        );

        appModal.dialog({
          title: '输入json格式的数据',
          size: 'large',
          message: $div,
          buttons: {
            confirm: {
              label: '确定',
              className: 'well-btn w-btn-primary',
              callback: function () {
                var param = {
                  ruleId: $('#id').val(),
                  froms: $('#sourceObjId').val()
                };
                $.extend(param, JSON.parse($('#jsonArea').val()));
                JDS.call({
                  service: 'botFacadeService.startBot',
                  data: [param],
                  version: '',
                  success: function (result) {
                    console.log('调试结果', result);
                  }
                });
                return false;
              }
            },
            cancel: {
              label: '取消',
              className: 'btn btn-default'
            }
          },
          shown: function (e) {
            $('#jsonArea').val('');
          }
        });
      }
    },

    reloadTargetFieldSeleOption: function (select, optionData, allowBlank) {
      $(select).each(function () {
        $(this).empty();
        if (allowBlank) {
          $(this).append($('<option>'));
        }
        var fieldData = optionData;
        if (fieldData) {
          for (var f in fieldData) {
            $(this).append(
              $('<option>', {
                value: f
              }).text(fieldData[f].displayName)
            );
          }
        }
      });
    },

    reloadSourceObjSelect: function (sourceObjUuids, sourceObjNames) {
      var self = this;
      $('.sourceObjSelect option').each(function () {
        if (sourceObjUuids.indexOf($(this).val()) == -1) {
          //不存在的删除
          var selected = false;
          var $select = $(this).parent();
          if ($(this).prop('selected')) {
            selected = true;
          }
          $(this).remove();
          if (selected) {
            $select.trigger('change');
          }
        }
      });
      for (var i = 0, len = sourceObjUuids.length; i < len; i++) {
        $('.sourceObjSelect').each(function () {
          if ($(this).find("option[value='" + sourceObjUuids[i] + "']").length == 0) {
            $(this).append(
              $('<option>', {
                value: sourceObjUuids[i]
              }).text(sourceObjNames[i])
            );
            $(this).trigger('change');
          }
        });
        self.getFieldData(sourceObjUuids[i]);
      }
    },

    ruleEditDialog: function ($a) {
      var _self = this;
      var message = _self.initHtml();
      appModal.dialog({
        title: '编辑计算规则',
        size: 'large',
        message: message,
        buttons: {
          confirm: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var selectData = $('#valueCalSelect').select2('data');
              if (selectData && selectData.text) {
                $a.text(selectData.text);
                var $container = $('.codeContainer:eq(' + selectData.id + ')');
                var _index = parseInt($a.attr('data-index'));
                $a.parents('table').bootstrapTable('updateCell', {
                  index: _index,
                  field: 'renderValueType',
                  value: selectData.id,
                  reinit: false
                });
                $a.parents('table').bootstrapTable('updateCell', {
                  index: _index,
                  field: 'renderValueExpression',
                  value: $container.data('coder').getValue(),
                  reinit: false
                });
              } else {
                $a.text('编辑规则');
                var $container = $('.codeContainer:eq(' + selectData.id + ')');
                var _index = parseInt($a.attr('data-index'));
                $a.parents('table').bootstrapTable('updateCell', {
                  index: _index,
                  field: 'renderValueType',
                  value: '',
                  reinit: false
                });
                $a.parents('table').bootstrapTable('updateCell', {
                  index: _index,
                  field: 'renderValueExpression',
                  value: '',
                  reinit: false
                });
              }
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn btn-default'
          }
        },
        shown: function (e) {
          $('#valueCalSelect').wSelect2({
            defaultBlank: true,
            data: [
              {
                id: 0,
                text: 'freemarker表达式'
              },
              {
                id: 1,
                text: 'groovy脚本'
              }
            ],
            remoteSearch: false,
            width: '100%',
            height: 250
          });

          $('.codeContainer').remove();
          $('#scriptContainer').append(
            $('<div>', {
              id: 'freemarkerCode',
              class: 'codeContainer',
              'ace-mode': 'ftl',
              style: 'display:none;'
            }),
            $('<div>', {
              id: 'groovyCode',
              class: 'codeContainer',
              'ace-mode': 'groovy',
              style: 'display:none;'
            })
          );

          $('#valueCalSelect').on('change', function () {
            $('.codeContainer').hide();
            $('.rule_tip').hide();
            if ($(this).val()) {
              var $container = $('.codeContainer:eq(' + $(this).val() + ')');
              $container.show();
              $('.rule_tip:eq(' + $(this).val() + ')').show();
            }
          });

          $('.codeContainer').hide();
          $('.rule_tip').hide();
          var isFieldMapping = $a.parents('table').is('#fieldMappingTable');
          var relaBusizUuid = '';
          var codeType = isFieldMapping ? 'botFieldMapping.' : 'botFieldReverseWrite.';
          var $tr = $a.parents('tr');
          if (isFieldMapping) {
            relaBusizUuid += $tr.find('.sourceObjSelect :selected').val() + '.';
            relaBusizUuid += $tr.find('.sourceObjFieldSelect :selected').val() + '->';
            relaBusizUuid += $tr.find('.targetObjFieldSelect :selected').val();
          } else {
            relaBusizUuid += $tr.find('.sourceObjSelect :selected').val() + '.';
            relaBusizUuid += $tr.find('.sourceObjFieldSelect :selected').val() + '->';
            relaBusizUuid += $tr.find('.targetObjFieldSelect :selected').val();
          }
          var _data = $a.parents('table').bootstrapTable('getData')[parseInt($a.parents('tr').attr('data-index'))];
          var renderValueType = _data.renderValueType;
          var renderValueExpression = _data.renderValueExpression;
          $('#ruleEditContainer select').find('option:eq(0)').prop('selected', true);
          $('#valueCalSelect')
            .val(renderValueType != null ? renderValueType : '')
            .trigger('change');
          $('.codeContainer').each(function () {
            if (!$(this).data('coder')) {
              //初始化代码编辑插件
              var containerId = $(this).attr('id');
              var coder = $.fn.aceBinder({
                container: '#' + containerId,
                id: containerId + 'Editor',
                value: renderValueExpression != null ? renderValueExpression : '',
                iframeId: 'ruleFrame' + new Date().getTime(),
                readOnly: _self.readonly,
                codeHis: {
                  enable: true,
                  relaBusizUuid: relaBusizUuid,
                  codeType: codeType + containerId
                },
                varSnippets: 'bot.fieldCalValue'
              });

              $(this).data('coder', coder);
            } else {
              $(this).data('coder').setValue('');
            }
          });
          if (renderValueType != null) {
            var $container = $('.codeContainer:eq(' + renderValueType + ')');
            $container.show();
            var coderPlugin = $container.data('coder');
            coderPlugin.setValue(renderValueExpression);
          }
        }
      });
    },

    targetFieldMapHtml: function (fieldData, rowObject, index, field) {
      var id = new Date().getTime();
      var $targetFieldContainer = $('<div>', {
        id: 'targetFieldDataDiv' + id
      });
      var $targetObjFieldSelect = $('<select>', {
        class: 'targetObjFieldSelect tableFieldSelect',
        field: field || 'targetObjField',
        'data-index': index
      });
      $targetFieldContainer.append($targetObjFieldSelect);
      if (fieldData) {
        var selected = [];
        var optionHtml = '<option></option>';
        for (var f in fieldData) {
          if (selected.indexOf(f) == -1) {
            optionHtml +=
              "<option value='" +
              f +
              "' " +
              (rowObject && rowObject.targetObjField == f ? 'selected' : '') +
              ' >' +
              fieldData[f].displayName +
              '</option>';
          }
        }
        $targetObjFieldSelect.html(optionHtml);
      }
      return $targetFieldContainer[0].outerHTML;
    },

    sourceFieldMapHtml: function (selectData, rowObject, index) {
      var id = new Date().getTime();
      var $sourceContainer = $('<div>', {
        id: 'sourceFieldDataDiv_' + id
      });
      var $sourceObjSelect = $('<select>', {
        class: 'sourceObjSelect tableFieldSelect',
        style: 'width:45%;vertical-align: middle;',
        field: 'sourceObjId',
        'data-index': index
      });
      if (selectData.length > 0) {
        var optionHtml = '';
        for (var i = 0, len = selectData.length; i < len; i++) {
          optionHtml +=
            "<option value='" +
            selectData[i].id +
            "' " +
            (rowObject && rowObject.sourceObjId == selectData[i].id ? 'selected' : '') +
            ' >' +
            selectData[i].text +
            '</option>';
        }
      }
      $sourceObjSelect.html(optionHtml);
      var $fieldSelect = $('<select>', {
        class: 'sourceObjFieldSelect tableFieldSelect',
        style: 'width:45%;vertical-align: middle;margin-left:10px;',
        field: 'sourceObjField',
        'data-index': index
      });
      $fieldSelect.append($('<option>').text(''));
      if (rowObject) {
        $fieldSelect.attr('init-field', rowObject.sourceObjField);
      }
      $sourceContainer.append($sourceObjSelect, $fieldSelect);
      return $sourceContainer[0].outerHTML;
    },
    getFieldData: function (uuid, excludeFields) {
      var _self = this;
      var fieldData = [];
      if (!uuid) {
        return fieldData;
      }
      _self.cacheFormField = _self.cacheFormField || {};
      if (_self.cacheFormField[uuid]) {
        return _self.cacheFormField[uuid];
      }
      $.ajax({
        url: ctx + '/pt/dyform/definition/getFormDefinition',
        async: false,
        type: 'POST',
        data: {
          formUuid: uuid,
          justDataAndDef: false
        },
        dataType: 'json',
        success: function (data) {
          if (data.fields) {
            data.fields.uuid = {
              displayName: 'UUID'
            };
            data.fields.form_uuid = {
              displayName: '表单定义UUID'
            };
            if (data.formType === 'MST') {
              //子表单
              data.fields.MAINFORM_DATA_UUID = {
                displayName: '主表单数据UUID'
              };

              data.fields.MAINFORM_FORM_UUID = {
                displayName: '主表单定义UUID'
              };
            }
            fieldData = data.fields;
            if (excludeFields) {
              for (var f in fieldData) {
                if (excludeFields.indexOf(f) != -1) {
                  delete fieldData[f];
                }
              }
            }
            _self.cacheFormField[uuid] = fieldData;
          }
        }
      });
      return fieldData;
    },
    _onSearch: function () {
      var _self = this;
      if (_self.sameFields) {
        var index = 0;
        var keyword = $('.keyword-search-wrap input').val();
        $('#autoFieldMappingTable').bootstrapTable('removeAll');
        for (var k = 0; k < _self.sameFields.length; k++) {
          // 将数据插入到表格中
          var sameFields = _self.sameFields[k];
          if (
            sameFields.sourceObjName.indexOf(keyword) > -1 ||
            sameFields.sourceObjField.indexOf(keyword) > -1 ||
            sameFields.targetObjField.indexOf(keyword) > -1 ||
            sameFields.name.indexOf(keyword) > -1
          ) {
            index += 1;
            sameFields.index = index.toString();
            $('#autoFieldMappingTable').bootstrapTable('insertRow', {
              index: k,
              row: sameFields
            });
          }
        }
      }
    },

    getSameField: function () {
      var _self = this;
      var targetFields = $('#targetObjName').data('fieldData');
      var targetObjId = $('#targetObjId').val();
      var sourceObjIds = $('#sourceObjId').val().split(';');
      var sourceObjNames = $('#sourceObjName').val().split(';');
      var sameFields = [];
      var ignoreMappings = _self.bean.ignoreMappings;
      if (!targetFields) {
        // 目标单据未选择
        return;
      }
      $.each(sourceObjIds, function (index, item) {
        var sourceField = _self.cacheFormField[item];
        var sourceObjName = sourceObjNames[index];
        for (var i in sourceField) {
          var isMapping = '1';
          if (!sourceField[i].name) {
            continue;
          }
          // var sIndex = _.findIndex(sameFields, function (o) {
          //   // 判断是否有多个相同的字段
          //   return o.name == i;
          // });
          var targetField = targetFields[i]; // 判断是否源字段和目标字段是否相同

          if (targetField) {
            // if (sIndex > -1) {
            //   sameFields.splice(sIndex, 1);
            // }
            if (ignoreMappings && ignoreMappings.length > 0) {
              // 编辑详情是否有手动取消匹配的字段
              var mIndex = _.findIndex(ignoreMappings, function (o) {
                return o.sourceObjField == sourceField[i].name && o.sourceObjId == item;
              });
              if (mIndex > -1) {
                isMapping = '0';
              }
            }
            sameFields.push({
              sourceObjName: sourceObjName,
              name: i,
              sourceObjField: sourceField[i].displayName,
              targetObjField: targetField.displayName,
              sourceObjId: item,
              targetObjId: targetObjId,
              isMapping: isMapping
            });
          }
        }
      });
      _self.sameFields = sameFields;
      $('#autoFieldMappingTable').bootstrapTable('removeAll');
      for (var k = 0; k < sameFields.length; k++) {
        // 将数据插入到表格中
        sameFields[k].index = (k + 1).toString();
        $('#autoFieldMappingTable').bootstrapTable('insertRow', {
          index: k,
          row: sameFields[k]
        });
      }
    },
    getColumn: function (id) {
      var _self = this;
      var columns = [];
      if (id == 'fieldMappingTable') {
        columns = [
          {
            field: 'checked',
            checkbox: true
          },

          {
            field: 'uuid',
            title: 'uuid',
            visible: false
          },
          {
            field: 'sourceFieldData',
            title: '源字段',
            formatter: function (value, row, index) {
              return _self.sourceFieldMapHtml($('#sourceObjId').select2('data'), row, index);
            }
          },
          {
            field: 'targetFieldData',
            title: '目标字段',
            formatter: function (value, row, index) {
              return _self.targetFieldMapHtml($('#targetObjName').data('fieldData'), row, index);
            }
          },
          {
            field: 'valueExpressionData',
            title: '值规则',
            formatter: function (value, row, index) {
              var text = _self.ruleValueType[row.renderValueType] || '编辑规则';
              return $('<a>', {
                class: 'ruleEdit',
                href: 'javascript:void(0);',
                'data-index': index
              }).text(text)[0].outerHTML;
            }
          },
          {
            field: 'renderValueExpression',
            title: '值规则',
            visible: false
          },
          {
            field: 'renderValueType',
            title: '值规则类型',
            visible: false
          },
          {
            field: 'sourceObjId',
            title: '源单据',
            visible: false
          },
          {
            field: 'sourceObjField',
            title: '源单据字段',
            visible: false
          },
          {
            field: 'targetObjField',
            title: '目标单据字段',
            visible: false
          }
        ];
      } else if (id == 'relaTable') {
        // private String sourceObjFieldName;
        // // 关联关系字段
        // private String relaObjField;

        // // 规则关联关系UUID
        // private String ruleObjRelaUuid;

        //
        // private String relaObjFieldName;
        columns = [
          {
            field: 'checked',
            checkbox: true
          },
          {
            field: 'uuid',
            title: 'uuid',
            visible: false
          },
          {
            field: 'sourceFieldData',
            title: '单据字段',
            formatter: function (value, row, index) {
              var selectData = $('#sourceObjId').select2('data');
              selectData.push($('#targetObjName').select2('data'));
              return _self.sourceFieldMapHtml(selectData, row, index);
            }
          },
          {
            field: 'relaFieldData',
            title: '关系维护字段',
            formatter: function (value, row, index) {
              if (row) {
                row.targetObjField = row.relaObjField;
              }
              return _self.targetFieldMapHtml($('#relaObjName').data('fieldData'), row, index, 'relaObjField');
            }
          },
          {
            field: 'sourceObjId',
            title: '源单据',
            visible: false
          },
          {
            field: 'sourceObjField',
            title: '源单据字段',
            visible: false
          },
          {
            field: 'relaObjField',
            title: '关系字段',
            visible: false
          }
        ];
      } else if (id == 'reverseWriteMappingTable') {
        columns = [
          {
            field: 'checked',
            checkbox: true
          },
          {
            field: 'uuid',
            title: 'uuid',
            visible: false
          },
          {
            field: 'targetFieldData',
            title: '目标字段',
            width: 300,
            formatter: function (value, row, index) {
              return _self.targetFieldMapHtml($('#targetObjName').data('fieldData'), row, index);
            }
          },
          {
            field: 'sourceFieldData',
            title: '源字段',
            formatter: function (value, row, index) {
              return _self.sourceFieldMapHtml($('#sourceObjId').select2('data'), row, index);
            }
          },
          {
            field: 'valueExpressionData',
            title: '值规则',
            width: 200,
            formatter: function (value, row, index) {
              var text = _self.ruleValueType[row.renderValueType] || '编辑规则';
              return $('<a>', {
                class: 'ruleEdit',
                href: 'javascript:void(0);',
                'data-index': index
              }).text(text)[0].outerHTML;
            }
          },
          {
            field: 'renderValueExpression',
            title: '值规则',
            visible: false
          },
          {
            field: 'renderValueType',
            title: '值规则类型',
            visible: false
          },
          {
            field: 'sourceObjId',
            title: '源单据',
            visible: false
          },
          {
            field: 'sourceObjField',
            title: '源单据字段',
            visible: false
          },
          {
            field: 'targetObjField',
            title: '目标单据字段',
            visible: false
          }
        ];
      } else if (id == 'autoFieldMappingTable') {
        columns = [
          {
            field: 'index',
            title: '序号',
            width: '50',
            align: 'center'
          },
          {
            field: 'sourceObjName',
            title: '源单据'
          },
          {
            field: 'sourceObjId',
            title: '源单据uuid',
            visible: false
          },
          {
            field: 'sourceObjField',
            title: '源字段'
          },
          {
            field: 'targetObjField',
            title: '目标字段'
          },
          {
            field: 'targetObjId',
            title: '目标单据uuid',
            visible: false
          },
          {
            field: 'name',
            title: '字段编码'
          },
          {
            field: 'isMapping',
            title: '是否映射',
            formatter: function (value, row, index) {
              var className = value == '1' ? 'active' : '';
              var html = '<div class="switch-wrap ' + className + '">' + '<span class="switch-radio"></span>' + '</div>';
              return html;
            }
          }
        ];
      }
      if (id != 'autoFieldMappingTable') {
        columns.push({
          field: 'id',
          title: 'id',
          visible: false
        });
      }

      return columns;
    },
    initTable: function (id) {
      var _self = this;
      var columns = _self.getColumn(id);
      $('#' + id)
        .bootstrapTable('destroy')
        .bootstrapTable({
          data: [],
          striped: false,
          showColumns: false,
          idField: 'id',
          uniqueId: 'id',
          sortable: false,
          sortName: id != 'autoFieldMappingTable' ? 'name' : '',
          sortOrder: 'asc',
          pageNumber: '20',
          undefinedText: '',
          columns: columns,
          onClickCell: function (field, value, row, $element) {
            if (field == 'valueExpressionData') {
              _self.ruleEditDialog($element.find('.ruleEdit'));
            }
          }
        });
    },
    isDebug: function () {
      $.ajax({
        dataType: 'json',
        type: 'POST',
        url: ctx + '/bot/ruleConfig/isDebug',
        success: function (res) {
          res ? $('#bot_rule_btn_debug').show() : $('#bot_rule_btn_debug').hide();
        }
      });
    },

    initHtml: function () {
      var html = '';
      html +=
        '<div id="ruleEditContainer">' +
        '<div class="well-form form-horizontal" id="transListenerCodeType">' +
        '<div class="form-group">' +
        '<label for="scriptBeforeTrans" class="well-form-label control-label">计算方式</label>' +
        '<div class="well-form-control">' +
        '<input type="text" id="valueCalSelect" name="valueCalSelect" class="form-control">' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div id="scriptContainer">' +
        '<div id="freemaerkerCode" class="codeContainer" ace-mode="ftl" style="display: none;"></div>' +
        '<div id="groovyCode" class="codeContainer" ace-mode="groovy" style="display: none;"></div>' +
        '</div>' +
        '<div style="background-color: #dff0d8;padding: 5px;display: none;" class="rule_tip">' +
        'freemarker支持的内置变量：' +
        '<ul>' +
        '<li style="padding-bottom: 10px;">' +
        'sourceValue ：表示源字段值，freemarker表达式可通过${"${"}sourceValue}访问' +
        '</li>' +
        '<li style="padding-bottom: 10px;">' +
        'targetValue ：表示目标字段值，该值仅在字段反写规则内可取到值。freemarker表达式可通过${"${"}targetValue}访问' +
        '</li>' +
        '<li style="padding-bottom: 10px;">' +
        'formData ：表示所有源数据。如果源数据是表单，则表示表单数据，' +
        '可以通过formData.表单ID.表单字段的方式取值，例如：${"${"}formData.uf_oa_book.book_name}；如果源数据是JSON报文对象，则通过data访问json数据对象，可以通过' +
        'data.报文字段的方式取值，例如JSON报文为{"name":"test","code":100}，取值name通过${"${"}data.name}' +
        '</li>' +
        '<li style="padding-bottom: 10px;">' +
        'targetFormData ：表单单据数据，表示目标单据数据，' +
        '可以通过targetFormData.表单ID.表单字段的方式取值，例如：targetFormData.uf_oa_book.book_name' +
        '</li>' +
        '<li style="padding-bottom: 10px;">' +
        '其他系统默认的变量：<br>' +
        'currentUserName ：当前用户的名称<br>' +
        'currentLoginName ：当前用户的登录名<br>' +
        'currentUserId ：当前用户ID<br>' +
        'currentUserUnitId ：当前用户归属的组织ID<br>' +
        'currentUserUnitName ：当前用户归属的组织名称<br>' +
        'currentUserDepartmentId ：当前用户归属的部门ID<br>' +
        'currentUserDepartmentName ：当前用户归属的部门名称 <br>' +
        'sysdate ：当前时间。${"${"}sysdate?datetime}获取当前完整的时间格式 <br>' +
        '</li>' +
        '</ul>' +
        '</div>' +
        '<div style="background-color: #dff0d8;padding: 5px;display: none;" class="rule_tip">' +
        'groovy支持的内置变量：' +
        '<ul>' +
        '<li style="padding-bottom: 10px;">' +
        'sourceValue ：表示源字段值，groovy脚本直接可以在脚本内使用sourceValue变量' +
        '</li>' +
        '<li style="padding-bottom: 10px;">' +
        'targetValue ：表示目标字段值，groovy脚本直接可以在脚本内使用targetValue变量' +
        '</li>' +
        '<li style="padding-bottom: 10px;">' +
        'data ：表示所有源数据。如果源数据是表单，则表示表单数据，' +
        '可以通过data.表单ID.表单字段的方式取值，例如：data.uf_oa_book.book_name；如果源数据是JSON报文对象，则表示JSON对象，可以通过' +
        'data.报文字段的方式取值，例如JSON报文为{"name":"test","code":100}，取值name通过data.name' +
        '</li>' +
        '<li style="padding-bottom: 10px;">' +
        'targetFormData ：表单单据数据，表示目标单据数据，' +
        '可以通过targetFormData.表单ID.表单字段的方式取值，例如：targetFormData.uf_oa_book.book_name' +
        '</li>' +
        '<li style="padding-bottom: 10px;">' +
        'jsonBody' +
        '：表示源数据是JSON报文对象，可以通过jsonBody.报文字段的方式取值，例如JSON报文为{"name":"test","code":100}，取值name通过jsonBody.name' +
        '</li>' +
        '<li style="padding-bottom: 10px;">' +
        '其他系统默认的变量：<br>' +
        'currentUserName ：当前用户的名称<br>' +
        'currentLoginName ：当前用户的登录名<br>' +
        'currentUserId ：当前用户ID<br>' +
        'currentUserUnitId ：当前用户归属的组织ID<br>' +
        'currentUserUnitName ：当前用户归属的组织名称<br>' +
        'currentUserDepartmentId ：当前用户归属的部门ID<br>' +
        'currentUserDepartmentName ：当前用户归属的部门名称 <br>' +
        'sysdate ：当前时间 <br>' +
        '</li>' +
        '</ul>' +
        '</div>' +
        '</div>';
      return html;
    }
  });
  return AppBotRuleConfWidgetDevelopment;
});
