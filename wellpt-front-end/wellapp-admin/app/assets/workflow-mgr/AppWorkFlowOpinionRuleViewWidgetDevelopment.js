define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  HtmlWidgetDevelopment
) {
  var StringBuilder = commons.StringBuilder;
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;
  // 流程签署意见_校验规则管理_编辑组件二开
  var AppWorkFlowOpinionRuleViewWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppWorkFlowOpinionRuleViewWidgetDevelopment, HtmlWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {
    },
    // 创建后回调
    create: function () {
      var _self = this;

      //初始化保存按钮
      _self._init_button();
    },
    // 初始化回调
    init: function () {
      var _self = this;

      var params = this.getWidgetParams();

      $('.satisfyCondition')
        .wellSelect({
          data: [
            {id: 'SC01', text: '全部'},
            {id: 'SC02', text: '任何'}
          ],
          searchable: false,
          remoteSearch: false
        })
        .wellSelect('val', 'SC01');
      var _uuid;
      var _rowData;
      if (params.uuid) {
        //编辑
        _rowData = params;
        _uuid = _rowData.uuid;

        // 编辑
        ac_id = 'btn_list_view_edit';
        _self._rendersOpinionRule(_uuid, ac_id);
        $('#opinionRuleItem').append(_self._addItemButtonHtml());
        _self._opinionRuleItemEvent();
      } else {
        //新增
        $('#opinionRuleItem').append(_self._addItemButtonHtml());
        _self._addItem();
        _self._opinionRuleItemEvent();
      }
      _self._initCss();
    },

    refresh: function () {
      var _self = this;
      _self.init();
    },
    //初始化保存按钮
    _init_button: function () {
      var _self = this;
      $('#page_20210602111015').attr('style', 'background: #FFFF;');
      var footer_html = _self._footer_html();
      $('#page_20210602111015').closest('.modal-dialog .modal-content ').find('.modal-footer').remove();
      $('#page_20210602111015').closest('.modal-dialog .modal-content ').append(footer_html);
      var bean = {
        uuid: null,
        opinionRuleName: null,
        satisfyCondition: null,
        cueWords: null,
        isAlertAutoClose: 1,
        opinionRuleItems: null
      };
      // 表单选择器
      var form_selector = '#opinion_rule_form';
      var validator = $.common.validation.validate(form_selector, 'opinionRuleEntity', function (options) {
        options.messages.opinionRuleName = {
          required: '不能为空'
        };
        options.messages.cueWords = {
          required: '不能为空'
        };
      });

      $(form_selector).json2form(bean);
      // 保存意见规则
      $('#page_20210602111015')
        .closest('.modal-dialog .modal-content ')
        .find('.btn_opinion_role_save')
        .click(function () {
          if (!validator.form()) {
            return false;
          }
          $(form_selector).form2json(bean);

          var opinionRuleItems = [];
          $.each($('#opinionRuleItem .work-flow-settings >li'), function (i, dom) {
            var opinionRuleItem = {itemName: '', itemCondition: '', itemValue: ''};
            opinionRuleItem.itemName = $(dom).find('.itemName').val();
            opinionRuleItem.itemCondition = $(dom).find('.itemCondition').val();
            opinionRuleItem.itemValue = $(dom).find('.itemValue').val();
            opinionRuleItems.push(opinionRuleItem);
          });
          bean.opinionRuleItems = JSON.stringify(opinionRuleItems);

          if (bean.isAlertAutoClose) {
            bean.isAlertAutoClose = 1;
          } else {
            bean.isAlertAutoClose = 0;
          }
          JDS.restfulPost({
            url: ctx + '/api/opinion/rule/saveOpinionRule',
            data: bean,
            validate: true,
            async: false,
            success: function (result) {
              appModal.success('保存成功！');

              // var _thisFrame = window.frameElement != null;
              // var targetWindow = _thisFrame ? window.parent : window.opener;
              // if (targetWindow)

              appContext.pageContainer.trigger('AppWorkFlowOpinionRuleList.Refresh');
            }
          });
        });
    },
    _footer_html: function () {
      var html =
        '<div class="modal-footer">' +
        '<button type="button" btnid="btn_opinion_rule_save_clone" name="btn_opinion_rule_save" class="well-btn w-btn-primary  btn_opinion_role_save">保存</button>' +
        '<button data-bb-handler="cancel" type="button" class="btn btn-default js-dialog-cancel btn_opinion_role_cancel">取消</button></div>';
      return html;
    },
    //渲染已添加的校验项
    _rendersOpinionRule: function (dataUuid, ac_id) {
      var _self = this;

      JDS.restfulGet({
        url: ctx + '/api/opinion/rule/getOpinionRuleDetail',
        data: {uuid: dataUuid},
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          var data = result.data;
          bean = data;
          // 表单选择器
          var form_selector = '#opinion_rule_form';
          $(form_selector).json2form(data);
          var validator = $.common.validation.validate(form_selector, 'opinionRuleEntity', function (options) {
            options.messages.opinionRuleName = {
              required: '不能为空'
            };
          });
          validator.form();
          $('.satisfyCondition').wellSelect('val', data.satisfyCondition);
          $.each(data.opinionRuleItemEntitys, function (i, val) {
            _self._addItem(val, dataUuid, ac_id);
          });
        }
      });
    },
    //添加校验项
    _addItem: function (val, dataUuid, ac_id) {
      var _self = this;
      $('#opinionRuleItem .work-flow-settings').append(_self._itemHtml());
      var dom = $('#opinionRuleItem .work-flow-settings').children('li:last-child');
      //校验项输入框 只能输入数字 事件
      $(dom)
        .find('.itemValue')
        .on('change', function () {
          var itemNameValue = $(this).parent().parent().find('.itemName').val();
          if (itemNameValue == '意见长度') {
            var itemValue = $(this).val();
            var patrn = /^(-)?\d+(\.\d+)?$/;
            if (itemValue != '' && patrn.exec(itemValue) == null) {
              appModal.error('校验项为意见长度时，只能输入数字');
              $(this).val('');
            } else {
            }
          }
        });

      if (ac_id == undefined || ac_id != 'open_view') {
        //删除校验项事件
        $(dom).on('click', '.btn-remove', function () {
          $(this).parent().parent().remove();
        });
      }

      var itemName = '';
      var itemCondition_defaultValue = '';
      if (ac_id == undefined || ac_id == 'btn_list_view_add') {
        //新增
        itemName = '意见内容';
        itemCondition_defaultValue = 'IC01';
      } else {
        //编辑赋值 校验项
        itemName = val.itemName;
        $(dom).find('.itemValue').val(val.itemValue);
        itemCondition_defaultValue = val.itemCondition;
      }
      $(dom)
        .find('.itemName')
        .wellSelect({
          data: [
            {id: '意见内容', text: '意见内容'},
            {id: '意见长度', text: '意见长度'}
          ],
          searchable: false,
          remoteSearch: false
        })
        .on('change', function () {
          var itemName = $(this).val();

          if (ac_id == undefined || ac_id == 'btn_list_view_add') {
          } else if (ac_id == 'btn_list_view_edit') {
            // 编辑
            //查看编辑
          } else {
            //查看
            $(dom).find('.itemName').wellSelect('readonly', true);
            $(dom).find('.itemCondition').wellSelect('readonly', true);
            $(dom).find('.itemValue').attr('disabled', 'disabled');
          }
          if (itemName == '意见内容') {
            var option = {
              data: [
                {id: 'IC01', text: '等于'},
                {id: 'IC02', text: '不等于'},
                {id: 'IC07', text: '包含'},
                {id: 'IC08', text: '不包含'}
              ],
              searchable: false,
              remoteSearch: false
            };
            $(dom).find('.itemCondition').wellSelect('reRenderOption', option);
          } else {
            var option = {
              data: [
                {id: 'IC01', text: '等于'},
                {id: 'IC02', text: '不等于'},
                {id: 'IC03', text: '大于'},
                {id: 'IC04', text: '大于等于'},
                {id: 'IC05', text: '小于'},
                {id: 'IC06', text: '小于等于'}
              ],
              searchable: false,
              remoteSearch: false
            };
            $(dom).find('.itemCondition').wellSelect('reRenderOption', option);
          }
        });
      $(dom)
        .find('.itemCondition')
        .wellSelect({
          data: [
            {id: 'IC01', text: '等于'},
            {id: 'IC02', text: '不等于'},
            {id: 'IC07', text: '包含'},
            {id: 'IC08', text: '不包含'}
          ],
          searchable: false,
          remoteSearch: false
        })
        .wellSelect('val', itemCondition_defaultValue);
      $(dom).find('.itemName').wellSelect('val', itemName);
    },
    //校验项事件
    _opinionRuleItemEvent: function () {
      var _self = this;
      //添加校验项事件
      $('#addOpinionRuleItem').on('click', function () {
        _self._addItem();
      });
    },
    //添加校验项hmtl
    _addItemButtonHtml: function () {
      var html =
        '<div class="mb20 well-btn w-btn-primary well-btn-sm w-noLine-btn" id="addOpinionRuleItem"><i class="iconfont icon-ptkj-jiahao"></i>添加校验项</div>\n';
      return html;
    },
    //校验项html
    _itemHtml: function () {
      var items =
        '  <li data-name="" data-index="0">' +
        '  <div class="name">' +
        '  <div class="item_div1" >' +
        '     <input type="text" name="item_name" class="itemName form-control"/>' +
        '   </div>' +
        '  <div class="item_div1" >' +
        '     <input type="text" name="item_condition" class="itemCondition form-control" />' +
        '   </div>' +
        '  <div class="item_div2" >' +
        '     <input type="text" name="item_value" class="itemValue" maxlength="10" />' +
        '   </div>' +
        '  </div>' +
        '   <div class="btn-group">' +
        '     <div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
        '     <i class="iconfont icon-ptkj-shanchu"></i>' +
        '   </div>' +
        '  </div>' +
        '  </li>';
      return items;
    },
    _isIEBrowser: function () {
      return !!(navigator.userAgent.indexOf('MSIE ') > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./));
    },
    //初始化css文件
    _initCss: function () {
      var css = '#opinion_rule_form td {border: 0px;}';
      css += '#opinion_rule_form td.label-td {background: #FFFFFF;}';
      css += '#opinion_rule_form .item_span {margin-right: 10px;}';
      css += '#opinion_rule_form .item_span1 {margin-left: 10px;}';
      css += '#opinion_rule_form .item_div1 {display: inline-block; width: 30%;}';
      css += '#opinion_rule_form .item_div2 {display: inline-block; width: 35%;}';
      css += '#opinion_rule_form label.error {color: #e33033;font-size: 14px;font-weight: normal;min-width: 150px;}';
      $('html > head').append($('<style type = "text/css">' + css + '</style>'));
    }
  });
  return AppWorkFlowOpinionRuleViewWidgetDevelopment;
});
