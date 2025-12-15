define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'ztree',
  'layDate',
  'bootstrapTable',
  'bootstrapTable_editable',
  'multiOrg',
  'HtmlWidgetDevelopment'
], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ztree,
  laydate,
  bootstrapTable,
  bootstrapTable_editable,
  multiOrg,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppWorkHandoverWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppWorkHandoverWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _self = this;
      var $element = this.widget.element;
      var uuid = this.getWidgetParams().uuid || GetRequestParam().uuid;
      $element.parents('.modal-body').first().next().hide();
      $element.parents('.bootbox').first().addClass('handover');
      // 查看
      if (uuid) {
        $('.handover-add', $element).hide();
        $('.handover-edit', $element).show();
        $.ajax({
          url: ctx + '/api/wh/handover/getWorkHandoverByUuid',
          type: 'get',
          dataType: 'json',
          data: { handoverUuid: uuid },
          success: function (res) {
            if (res.code == 0) {
              var data = res.data;
              var statusName = data.workHandoverStatus == '1' ? '未执行' : data.workHandoverStatus == '2' ? '执行中' : '已完成';
              var className = 'exc' + data.workHandoverStatus;
              var $statusHtml = "<span class='exc " + className + "'>" + statusName + '</span>';
              $element
                .parents('.bootbox')
                .find('.modal-title')
                .html(data.handoverPersonName + '的工作交接')
                .append($statusHtml);
              fillEditData(data, $element);
              if (data.workHandoverStatus == 1) {
                fillAddData(data, $element);
              } else {
                $('#editBtn', $element).hide();
              }
            }
          }
        });
      }

      // 接收人组织弹窗
      $('#receiverNameDialog', $element)
        .off()
        .on('click', function () {
          $.unit2.open({
            valueField: 'receiverId',
            labelField: 'receiverNameDialog',
            title: '选择接收人',
            type: 'MyUnit',
            multiple: false,
            selectTypes: 'U',
            valueFormat: 'justId',
            nameDisplayMethod: '1'
          });
        });
      // 交接人组织弹窗
      $('#handoverPersonNameDialog', $element)
        .off()
        .on('click', function () {
          getPerson('MyUnit', '选择交接人');
        });
      // 禁用人员组织弹窗
      $('.disabled-user', $element)
        .off()
        .on('click', function () {
          getPerson('DisablePersons', '选择交接人(已禁用/冻结)');
        });
      // 工作类型
      $('#handoverWorkTypeName', $element).wellSelect({
        data: [
          {
            id: 'flow',
            text: '流程'
          }
        ],
        labelField: 'handoverWorkTypeName',
        valueField: 'handoverWorkType',
        searchable: false,
        showEmpty: false
      });

      $('.workflow-item', $element)
        .off('change', 'input')
        .on('change', 'input', function () {
          var id = $(this).attr('id');
          if (id.indexOf('1') == -1) {
            var isChecked = $(this).prop('checked');
            $(this)
              .parent()
              .find('#' + id + '1')
              .next()
              [isChecked ? 'show' : 'hide']();
            $(this)
              .parent()
              .find('#' + id + '1')
              .prop('checked', false);
          }
        });

      // 流程定义、流程分类
      $('#handoverContentsName', $element).wCommonComboTree({
        service: 'workflowDelegationSettiongsService.getContentAsTreeAsync',
        serviceParams: ['-1'],
        serviceVersion: '',
        valueField: 'id',
        value: $('#handoverContentsId', $element).val(),
        width: '690px',
        separator: ';',
        searchEnable: true,
        multiSelect: true, // 是否多选
        parentSelect: true, // 父节点选择有效，默认无效
        onAfterSetValue: function (comboTree, value) {
          $('#handoverContentsId', $element).val(value.options.value);
          console.log(value.options.valueNodes);
          var valueNodes = value.options.valueNodes;
          var html = '';
          $.each(valueNodes, function (index, item) {
            var className = item.data.indexOf('FLOW_CATEGORY_') == 0 ? 'icon-ptkj-wenjianjia-kai' : 'icon-ptkj-wenjian';
            html += "<span class='flowClass'><i class='iconfont " + className + "'></i>" + item.name + '</span>';
          });
          $('#handoverContentsName', $element).html(html);
        }
      });

      // 上一步
      $('#prevStepBtn', $element)
        .off()
        .on('click', function () {
          changeStepStatus($(this), $element, false);
          $('#nextStepBtn', $element).show();
        });

      // 下一步
      $('#nextStepBtn', $element)
        .off()
        .on('click', function () {
          var nextStepBtnDom = $(this);
          // 校验交接内容是否填写
          var bean = collectData($element);
          if (bean.handoverPersonId == '') {
            appModal.error('交接人不能为空！');
            return false;
          }
          if (bean.receiverId == '') {
            appModal.error('接收人不能为空！');
            return false;
          }
          if (bean.handoverPersonId == bean.receiverId) {
            appModal.error('接收人不能和交接人相同！');
            return false;
          }
          if (bean.handoverWorkType == '') {
            appModal.error('工作类型不能为空！');
            return false;
          }
          if (bean.whWorkTypeToHandoverCountItemDtos.length == 0) {
            appModal.error('交接内容不能为空！');
            return false;
          }
          if (bean.handoverContentsId.length == 0) {
            appModal.error('交接内容（ 流程分类和流程定义）不能为空！');
            return false;
          }

          JDS.restfulGet({
            url: ctx + '/proxy/api/wh/handover/checkWorkFlowTaskDelegation',
            data: { handoverPersonId: bean.handoverPersonId },
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
              if (result.code == 0) {
                if (result.data === 0) {
                  $('.handover-result', $element).hide();
                  changeStepStatus(nextStepBtnDom, $element, true);
                  nextStepBtnDom.hide();
                } else {
                  appModal.error('存在工作委托关系（委托他人或受托），请先终止委托关系！');
                  return false;
                }
              }
            }
          });
        });

      // 确定交接
      $('#handoverBtn', $element)
        .off()
        .on('click', function () {
          changeStepStatus($(this), $element, false);
          $('#cancelBtn', $element).text('关闭');
          var bean = collectData($element);
          bean.handoverWorkTimeSetting = $("input[name='handoverWorkTimeSetting']:checked", $element).val();
          bean.noticeHandoverPersonFlag = $('#noticeHandoverPersonFlag', $element).hasClass('active') ? 1 : 0;

          $.ajax({
            url: ctx + '/api/wh/handover/saveWorkHandover',
            type: 'post',
            dataType: 'json',
            data: bean,
            success: function (res) {
              if (res.code == 0) {
                $('#widgetRefreshEventDiv').trigger('click');
                $('.step-tips', $element).html(res.data);
              }
            }
          });
        });

      // 取消/关闭
      $('#cancelBtn', $element)
        .off()
        .on('click', function () {
          appModal.hide();
        });

      //关闭
      $('#closeBtn', $element)
        .off()
        .on('click', function () {
          appModal.hide();
        });
      $('#editBtn', $element)
        .off()
        .on('click', function () {
          $('.handover-add', $element).show();
          $('.handover-edit', $element).hide();
        });

      // 查询交接内容
      $('#queryBtn', $element)
        .off()
        .on('click', function () {
          $('.handover-add', $element).find('#queryHandover').show();
          $('.handover-result', $element).show();
          var bean = collectData($element);
          server.JDS.restfulPost({
            url: '/proxy/api/wh/handover/getFlowDatasRecords',
            data: bean,
            success: function (result) {
              if (result.code == 0) {
                var html = '';
                $.each(result.data, function (index, item) {
                  html += "<div class='result-item'>" + "<span class='result-item-title'>" + item.handoverContentTypeName;
                  if (item.completedFlowFlag === 1) {
                    html += '<span>含已办结流程</span>';
                  }
                  html += '</span>' + "<span class='result-item-value'>" + item.count + '</span>' + '</div>';
                });
                $('.handover-add', $element).find('.result-list').html(html);
                $('.handover-add', $element).find('#queryHandover').hide();
              }
            }
          });
        });

      // 通知接收人开关
      $('.switch-wrap', $element)
        .off()
        .on('click', function () {
          var isActive = $(this).hasClass('active');
          $(this)[isActive ? 'removeClass' : 'addClass']('active');
          $('.switch-tips')[isActive ? 'hide' : 'show']();
        });

      // 组织弹窗
      function getPerson(type, title) {
        $.unit2.open({
          valueField: 'handoverPersonId',
          labelField: 'handoverPersonNameDialog',
          title: title,
          type: type,
          multiple: false,
          selectTypes: 'U',
          valueFormat: 'justId',
          nameDisplayMethod: '1'
        });
      }

      // 切换上一步、下一步、确认交接按钮的交互
      function changeStepStatus($this, $element, isShow) {
        var val = $this.data('value');
        $('.stepBody', $element).hide();
        $('.stepBody' + val, $element).show();
        $('button[data-value]', $element)[isShow ? 'show' : 'hide']();
        if (val == '1') {
          $element
            .find('.handover-steps >div:eq(0)')
            .find('.step')
            .addClass('doing')
            .removeClass('over iconfont icon-ptkj-dagou')
            .text('1');
          $element.find('.handover-steps >div:eq(1)').find('.step').removeClass('doing').text('2');
        } else {
          $element
            .find('.handover-steps >div:eq(' + (val - 1) + ')')
            .find('.step')
            .addClass('doing');
          $element
            .find('.handover-steps >div:lt(' + (val - 1) + ')')
            .find('.step')
            .removeClass('doing')
            .addClass('over iconfont icon-ptkj-dagou')
            .text('');
        }
      }

      // 收集新增的数据
      function collectData($element) {
        var bean = {};
        bean.uuid = $('#uuid', $element).val();
        bean.handoverPersonId = $('#handoverPersonId', $element).val();
        bean.handoverPersonName = $('#handoverPersonNameDialog', $element).val();
        bean.receiverId = $('#receiverId', $element).val();
        bean.receiverName = $('#receiverNameDialog', $element).val();
        bean.handoverWorkType = $('#handoverWorkType', $element).val();
        bean.handoverWorkTypeName = $('#handoverWorkTypeName', $element).val();
        bean.handoverContentsName = $('#handoverContentsName', $element).val();
        bean.handoverContentsId = $('#handoverContentsId', $element).val();
        bean.whWorkTypeToHandoverCountItemDtos = [];
        var $workflowItem = $('.workflow-item', $element);
        $.each($workflowItem, function (index, item) {
          //有勾选才赋值
          if ($(item).find('input:eq(0)').prop('checked')) {
            var type = $(item).find('input:eq(0)').attr('id');
            var typeName = $(item).find('label:eq(0)').text();
            var flag = $('#' + type + '1', $element).length > 0 ? $('#' + type + '1', $element).prop('checked') : false;
            bean.whWorkTypeToHandoverCountItemDtos.push({
              completedFlowFlag: flag ? 1 : 0,
              handoverContentType: type,
              handoverContentTypeName: typeName
            });
          }
        });
        return bean;
      }

      // 编辑页面数据填充
      function fillAddData(data, $element) {
        $('#uuid', $element).val(data.uuid);
        $('#handoverPersonId', $element).val(data.handoverPersonId);
        $('#handoverPersonNameDialog', $element).val(data.handoverPersonName);
        $('#receiverId', $element).val(data.receiverId);
        $('#receiverNameDialog', $element).val(data.receiverName);
        $('#handoverWorkTypeName', $element).wellSelect('val', data.handoverWorkType);
        var handoverContentsId = data.handoverContentsId.split(';');
        var handoverContentsName = data.handoverContentsName.split(';');
        $('#handoverContentsId', $element).val(handoverContentsId);
        $('#handoverContentsName', $element).val(data.handoverContentsName);
        if (handoverContentsId.length > 0) {
          var html = '';
          $.each(handoverContentsId, function (index, item) {
            var className = item.indexOf('FLOW_CATEGORY_') == 0 ? 'icon-ptkj-wenjianjia-kai' : 'icon-ptkj-wenjian';
            html += "<span class='flowClass'><i class='iconfont " + className + "'></i>" + handoverContentsName[index] + '</span>';
          });
          $('#handoverContentsName', $element).html(html);
        }
        $('#todo', $element).prop('checked', '');
        $('#done', $element).prop('checked', '');
        $.each(data.whWorkTypeToHandoverCountItemDtoList, function (index, item) {
          $('#' + item.handoverContentType, $element).prop('checked', true);
          $('#' + item.handoverContentType + '1', $element)
            .next()
            .show();
          if (item.completedFlowFlag === 1) {
            $('#' + item.handoverContentType + '1', $element).prop('checked', true);
          }
        });

        $("input[name='handoverWorkTimeSetting'][value='" + data.excType + "']", $element).prop('checked', true);
        if (data.noticeHandoverPersonFlag == 0) {
          $('#noticeHandoverPersonFlag', $element).removeClass('active');
        }
      }

      function fillEditData(data, $element) {
        var $info = $('.edit-info', $element);
        $.each($info, function (index, item) {
          var id = $(item).attr('id');
          var field = id.split('edit-')[1];
          if (field == 'workHandoverStatus' && data.workHandoverStatus == '3') {
            // 交接结果
            $('#' + id, $element)
              .parent()
              .show();
            var dom = '';
            $.each(data.workTypeToHandoverCountDtoList, function (index, item) {
              dom += "<div class='result-item'>" + "<span class='result-item-title'>" + item.handoverContentTypeName;
              if (item.completedFlowFlag == 1) {
                dom += '<span>含已办结流程</span>';
              }
              dom += '</span>' + "<span class='result-item-value'>" + item.count + '</span>' + '</div>';
            });
            $('#' + id).html(dom);
          } else if (field == 'handoverWorkFlow') {
            // 交接内容
            var contents = [];
            $.each(data.whWorkTypeToHandoverCountItemDtoList, function (index, item) {
              var text = item.completedFlowFlag == 1 ? '（含已办结）' : '';
              contents.push(item.handoverContentTypeName + text);
            });
            $('#' + id).html(contents.join(';'));
          } else if (field == 'handoverContentsId') {
            // 交接内容
            var html = '';
            var handoverContentsId = data.handoverContentsId.split(';');
            var handoverContentsName = data.handoverContentsName.split(';');
            $.each(handoverContentsId, function (cIndex, cItem) {
              var className = cItem.indexOf('FLOW_CATEGORY_') == 0 ? 'icon-ptkj-wenjianjia-kai' : 'icon-ptkj-wenjian';
              html += "<span class='flowClass'><i class='iconfont " + className + "'></i>" + handoverContentsName[cIndex] + '</span>';
            });
            $('#' + id).html(html);
          } else if (field != 'workHandoverStatus') {
            // 其他
            $('#' + id).html(data[field]);
          }
        });
      }
    },

    refresh: function () {
      this.init();
    }
  });
  return AppWorkHandoverWidgetDevelopment;
});
