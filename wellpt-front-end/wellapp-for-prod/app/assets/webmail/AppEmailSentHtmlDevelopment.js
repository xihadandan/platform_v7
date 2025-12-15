define(['constant', 'commons', 'server', 'appContext', 'appModal', 'wSelect2', 'niceScroll', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  wSelect2,
  niceScroll,
  HtmlWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  var inputName = '';
  var AppEmailSentHtmlDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppEmailSentHtmlDevelopment, HtmlWidgetDevelopment, {
    prepare: function () {
      this.mailboxUuid = commons.Browser.getQueryString('mailboxUuid');
      this.editAgain = commons.Browser.getQueryString('editAgain', 'false') === 'true';
      this.transfer = commons.Browser.getQueryString('transfer', 'false') === 'true';
      this.reply = commons.Browser.getQueryString('reply', 'false') === 'true';
      this.replyAll = commons.Browser.getQueryString('replyAll', 'false') === 'true';
      this.bean = {
        mailboxUuid: this.mailboxUuid, // 邮箱数据UUID
        fromMailAddress: null, // 发送邮件地址
        toUserName: null, // 收件人名称
        toMailAddress: null, // 收件人地址
        ccUserName: null, // 抄送人名称
        ccMailAddress: null, // 抄送人地址
        bccUserName: null, // 密送人名称
        bccMailAddress: null, // 密送人地址
        subject: null, // 主题
        content: null, // 内容
        repoFileNames: null, // MONGODB附件名称
        repoFileUuids: null, // MONGODB附件UUID
        replyToUserName: null, // 设置回复人名称
        replyToMailAddress: null, // 设置回复人地址
        readReceiptStatus: null, // 重要
        priority: null //  请求阅读回执
      };

      this.quickSendMailFrame = window.frameElement != null; //是否发送邮件是以iframe嵌套到弹窗内
      this.ctlId = 'webmail_file_upload';
      this.fileupload = new WellFileUpload(this.ctlId);
      this.fileElSelector = '#webmail_file_upload_el';
      this.formSelector = '#webmail_form';
      this.mailIframeuid = commons.Browser.getQueryString('uid');

      this.templateHasSignature = false;
    },

    isEmail: function (v) {
      return /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/.test(v);
    },

    init: function () {
      if (this.quickSendMailFrame) {
        //弹窗内，隐藏面板头部以及按钮
        $('.panel-heading,#mail_button_group').hide();
      }
      var _this = this;
      _this.fileUpload_add_count = 0;
      this.loadCssLink();

      this.loadButtons();

      this.loadMailConfig();

      var $container = this.widget.element;

      _this.bindEvent();

      _this.loadRecentContactPersonList();

      _this.ckeditorLoadFinished = false;
      _this.initCkeditorPlugin();

      _this.loadSystemMailPaperOptions();

      if (this.quickSendMailFrame) {
        window.parent.mailEvent[this.mailIframeuid].ready($(window.document.body));
      }

      if (this.bean.mailboxUuid) {
        _this.fileupload.setBtnShowType('2');
        _this.loadMailDetail();
      } else {
        _this.fileupload.init(false, $(_this.fileElSelector), false, true, []);
        // 上传成功
        _this.fileupload.setBtnShowType('2');
        _this.fileupload.beforeFileUploadCallback = function (e, data) {
          if (_this.fileUpload_add_count < 0) {
            _this.fileUpload_add_count = 0;
          }
          _this.fileUpload_add_count++;
          $('#mail_button_group button', $container).prop('disabled', true);
          return true;
        };
        _this.fileupload.deleteOkCallback = function (data) {
          _this.fileUpload_add_count--;
          if (_this.fileUpload_add_count <= 0) {
            $('#mail_button_group button', $container).prop('disabled', false);
          }
        };
        _this.fileupload.uploadOkCallback = function (fileInfo) {
          _this.fileUpload_add_count--;
          if (_this.fileUpload_add_count <= 0) {
            $('#mail_button_group button', $container).prop('disabled', false);
          }
          var subjectVal = $('#subject', _this.formSelector).val();
          if (subjectVal == '' && fileInfo && fileInfo.fileName) {
            var fileName = fileInfo.fileName;
            if (fileName.indexOf('.') > 0) {
              fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            }
            $('#subject', _this.formSelector).val(fileName);
          }
        };
        // CKEDITOR.replace("content");
      }
    },

    loadMailDetail: function () {
      var _this = this;
      var $container = this.widget.element;
      var service = 'wmWebmailOutboxService.get';
      if (this.transfer) {
        service = 'wmWebmailOutboxService.getForTransfer';
      }
      if (this.reply) {
        service = 'wmWebmailOutboxService.getForReply';
      }
      if (this.replyAll) {
        service = 'wmWebmailOutboxService.getForReplyAll';
      }
      // 获取数据
      JDS.call({
        service: service,
        data: [_this.bean.mailboxUuid],
        version: '',
        success: function (result) {
          // 获取数据后处理
          _this.bean = result.data;
          window.document.title = _this.bean.subject || '写邮件';
          // 初使化邮件数据
          $('#webmail_form', $container).json2form(_this.bean);
          $('#content').val(''); //正文内容由设置信纸、模板步骤进行添加
          var dbFiles = _this.bean.repoFiles;
          _this.fileupload.init(false, $(_this.fileElSelector), false, true, dbFiles);
          _this.fileupload.beforeFileUploadCallback = function (e, data) {
            if (_this.fileUpload_add_count < 0) {
              _this.fileUpload_add_count = 0;
            }
            _this.fileUpload_add_count++;
            $('#mail_button_group button', $container).prop('disabled', true);
            return true;
          };
          _this.fileupload.deleteOkCallback = function (data) {
            _this.fileUpload_add_count--;
            if (_this.fileUpload_add_count <= 0) {
              $('#mail_button_group button', $container).prop('disabled', false);
            }
          };
          _this.fileupload.uploadOkCallback = function (fileInfo) {
            _this.fileUpload_add_count--;
            if (_this.fileUpload_add_count <= 0) {
              $('#mail_button_group button', $container).prop('disabled', false);
            }
          };
          $('#toUserNameTagInput,#ccUserNameTagInput,#bccUserNameTagInput', $container).each(function () {
            var id = $(this).attr('id');
            var prop = id.replace('TagInput', '');
            //判断是否要用智能路径展示
            var smartProp = prop.indexOf('UserName') > 0 ? prop.split('UserName')[0] + 'SmartUserName' : undefined;
            if (_this.bean[prop]) {
              var textParts = _this.bean[prop].split(';');
              if (_this.bean[smartProp]) {
                textParts = _this.bean[smartProp].split(';');
              }
              var valueParts = _this.bean[id.replace('UserNameTagInput', 'MailAddress')].split(';');
              for (var i = 0, len = textParts.length; i < len; i++) {
                var email = _this.isEmail(valueParts[i]);
                var canEdit = textParts[i] == valueParts[i] && email;
                $(this).data('validateInput', false);
                $(this).tagEditor('addTag', textParts[i], true, canEdit, {
                  value: valueParts[i]
                });
                $(this).data('validateInput', true);
              }
            }
          });
          if (StringUtils.isNotBlank(_this.bean.ccMailAddress)) {
            setTimeout(function () {
              $('.add-cc', $container).trigger('click');
            }, 0);
          }
          if (StringUtils.isNotBlank(_this.bean.bccMailAddress)) {
            setTimeout(function () {
              $('.add-bcc', $container).trigger('click');
            }, 0);
          }
          if (StringUtils.isNotBlank(_this.bean.priority)) {
            $("input[name='priority']", $container).prop('checked', _this.bean.priority == '1' ? true : false);
          }
          if (StringUtils.isNotBlank(_this.bean.readReceiptStatus)) {
            $("input[name='readReceiptStatus']", $container).prop('checked', _this.bean.readReceiptStatus == '1' ? true : false);
          }
        },
        async: false
      });
    },
    bindEvent: function () {
      var _this = this;
      var $container = this.widget.element;
      //邮件地址输入框初始化
      $('#toUserNameTagInput,#ccUserNameTagInput,#bccUserNameTagInput', $container).tagEditor({
        placeholder: '发给多人时地址请以分号隔开',
        forceLowercase: false,
        removeDuplicates: false,
        beforeClick: function ($this) {
          $('.user-name-list').hide();
        },
        onKeyUp: function ($this, val) {
          if ($.trim(val) == '') {
            $this.next().hide();
            return false;
          }
          inputName = val;
          $.ajax({
            url: ctx + '/proxy/api/org/facade/getCurrentUnitUserListByUserNameKey',
            type: 'get',
            dataType: 'json',
            contentType: 'application/json',
            data: {
              userNameKey: val
            },
            success: function (result) {
              if (result.code == 0 && result.data.length > 0) {
                var html = '';
                $.each(result.data, function (index, item) {
                  var text = item.userName + '(' + (item.jobPathName || '无职位信息') + ')';
                  html +=
                    "<li class='user-name-item' title='" +
                    text +
                    "' data-name='" +
                    item.userName +
                    "' data-id='" +
                    item.userId +
                    "'>" +
                    text +
                    '</li>';
                });
                $this.next().html(html).show();
                $this.next().getNiceScroll().resize();
                $this.next().getNiceScroll()[0].setScrollTop(0);
              } else {
                $this.next().hide();
              }
            }
          });
        },
        beforeTagSave: function (field, editor, tags, tag, val, $li, position) {
          var $el = position && position == 'before' ? $li.parents('li').prev() : $li.parents('li');
          var data = $el.data('value');
          if ((!data || data == val) && !_this.isEmail(val)) {
            $el.data('value', '');
            $el.find('.tag-editor-tag').removeAttr('disable-edit');
            $el.addClass('tag-editor-error').attr('title', '该地址格式有误，请点击修改');
          } else {
            $el.removeClass('tag-editor-error').removeAttr('title');
          }

          // appModal.toast({
          //   message: '请输入正确的邮箱地址',
          //   type: 'error',
          //   timer: 3000
          // });
          // return false;
        }
      });

      $('.user-name-list').niceScroll({
        cursorcolor: '#ccc', //滚动条的颜色
        cursoropacitymax: 0.9, //滚动条的透明度，从0-1
        touchbehavior: false, //使光标拖动滚动像在台式电脑触摸设备 true滚动条拖动不可用
        cursorwidth: '4px', //滚动条的宽度  单位默认px
        cursorborder: '0', // 游标边框css定义
        cursorborderradius: '3px', //滚动条两头的圆角
        autohidemode: true, //是否隐藏滚动条  true的时候默认不显示滚动条，当鼠标经过的时候显示滚动条
        zindex: '1001', //给滚动条设置z-index值
        railvalign: 'defaul'
      });

      $('.user-name-list')
        .off('click', '.user-name-item')
        .on('click', '.user-name-item', function (e) {
          e.stopPropagation();
          var mailAddress = $(this).data('id');
          var name = $(this).data('name');
          var $focusMailInput1 = $(this).parents('td').first().find('input');

          var existTagDatas = $focusMailInput1.tagEditor('getTagDatas');
          var existTags = $focusMailInput1.tagEditor('getTags')[0].tags;
          var exist = null;
          var replaceName = -1;
          if (existTagDatas.length) {
            for (var i = 0, len = existTagDatas.length; i < len; i++) {
              if (existTagDatas[i] == null && existTags[i] == inputName) {
                replaceName = i;
              }
              if (existTagDatas[i]) {
                var values = existTagDatas[i].value.split('/');
                if (
                  (values.length > 0 && values[values.length - 1] == mailAddress) ||
                  existTagDatas[i].value == mailAddress ||
                  existTags.indexOf(mailAddress) != -1
                ) {
                  exist = existTags[i];
                  break;
                }
              }
            }
          }

          if (exist != null) {
            appModal.info('不允许添加重复联系人');
            return false;
          }

          var email = _this.isEmail(mailAddress);
          var canEdit = email && mailAddress == name;
          $focusMailInput1.data('validateInput', false);
          $focusMailInput1.tagEditor(
            'addTag',
            name,
            true,
            canEdit,
            {
              value: mailAddress
            },
            function () {
              if (replaceName > -1) {
                $focusMailInput1
                  .next()
                  .find('.tag-editor-tag:eq(' + replaceName + ')')
                  .closest('li')
                  .find('.tag-editor-delete')
                  .click();
                inputName = null;
              }
            }
          );
          $focusMailInput1.data('validateInput', true);
        });

      $(document).on('click', function (e) {
        e.stopPropagation();
        if ($('.user-name-list:visible').length > 0 && e.target.className != 'user-name-item') {
          $('.user-name-list:visible').hide();
        }
      });

      // 保存
      $('.btn_save', $container).on(
        'click',
        _.debounce(
          function () {
            _this.onSave();
          },
          500,
          {
            leading: true,
            trailing: false
          }
        )
      );

      // 发送
      $('.btn_sent', $container).on(
        'click',
        _.debounce(
          function () {
            _this.sendMail();
          },
          500,
          {
            leading: true,
            trailing: false
          }
        )
      );

      // 发送
      $('.btn_timing_send', $container).on(
        'click',
        _.debounce(
          function () {
            _this.timingSend();
          },
          500,
          {
            leading: true,
            trailing: false
          }
        )
      );

      // 删除
      $('.btn_delete', $container).one(
        'click',
        _.debounce(
          function () {
            _this.onDelete();
          },
          500,
          {
            leading: true,
            trailing: false
          }
        )
      );

      // 转发
      $('.btn_transfer', $container).on('click', function () {
        _this.onTransfer();
      });

      // 回复
      $('.btn_reply', $container).on('click', function () {
        _this.onReply();
      });
      // 回复全部
      $('.btn_reply_all', $container).on('click', function () {
        _this.onReplyAll();
      });

      // 添加删除抄送
      $('.add-cc', $container).on('click', function () {
        $(this).hide();
        $('.row-cc').show();
        $('.del-cc').show();
        $('.mail_recent_contact_td').trigger('rowspanChange', [1]);
      });
      $('.del-cc', $container).on('click', function () {
        $(this).hide();
        _this.bean.ccUserName = '';
        _this.bean.ccMailAddress = '';
        // $('input[name=ccUserName]').val('');
        // $('input[name=ccMailAddress]').val('');
        $('.row-cc').hide();
        $('.add-cc').show();
        $('.mail_recent_contact_td').trigger('rowspanChange', [-1]);
      });

      // 添加删除密送
      $('.add-bcc', $container).on('click', function () {
        $(this).hide();
        $('.row-bcc').show();
        $('.del-bcc').show();
        $('.mail_recent_contact_td').trigger('rowspanChange', [1]);
      });
      $('.del-bcc', $container).on('click', function () {
        $(this).hide();
        // $('input[name=bccUserName]').val('');
        // $('input[name=bccMailAddress]').val('');
        _this.bean.bccUserName = '';
        _this.bean.bccMailAddress = '';
        $('.row-bcc').hide();
        $('.add-bcc').show();
        $('.mail_recent_contact_td').trigger('rowspanChange', [-1]);
      });

      var $focusMailInput = null;
      $('.tag-editor', $container).one('focus', 'input', function () {
        $focusMailInput = $(this).closest('ul').prev('input');
      });

      $('.tag-editor', $container).on('click', function () {
        $focusMailInput = $(this).prev('input');
      });

      //隐藏或展示右侧列表按钮事件
      $('#showOrHideRList', $container).on('click', function () {
        var isShowClass = $(this).hasClass('mail_recent_contact_right_icon');
        if (isShowClass) {
          $(this).removeClass('mail_recent_contact_right_icon').addClass('mail_recent_contact_left_icon');
          $('.mail_recent_contact_td').hide();
          $(this).attr('title', '展示右侧列表');
        } else {
          $(this).removeClass('mail_recent_contact_left_icon').addClass('mail_recent_contact_right_icon');
          $('.mail_recent_contact_td').show();
          $(this).attr('title', '隐藏右侧列表');
        }
      });

      //列表表格行占用数变更
      $('.mail_recent_contact_td', $container).on('rowspanChange', function (e, num) {
        var rowspan = parseInt($(this).attr('rowspan')) + num;
        $(this).attr('rowspan', rowspan);
        $(this).prev('td').attr('rowspan', rowspan);
      });

      //情况最近联系人列表
      $('#deleteRecentContact', $container).on('click', function () {
        appModal.confirm('确定要清空最近联系人吗？', function () {
          JDS.call({
            service: 'wmMailRecentContactFacadeService.deleteCurrentUserRecentContact',
            data: [],
            version: '',
            success: function (result) {
              if (result.success) {
                $('#recentContactUl', $container).empty();
              }
            }
          });
        });
      });

      $('.mail_recent_contact_td', $container).on('click', '#recentContactUl li', function () {
        if (!$('.row-cc', $container).is(':visible') && !$('.row-bcc').is(':visible')) {
          $focusMailInput = $('.tag-editor:eq(0)').closest('ul').prev('input');
        }
        if ($focusMailInput) {
          var mailAddress = $(this).attr('contacterMailAddress');
          var existTagDatas = $focusMailInput.tagEditor('getTagDatas');
          var existTags = $focusMailInput.tagEditor('getTags')[0].tags;
          var exist = null;
          if (existTagDatas.length) {
            for (var i = 0, len = existTagDatas.length; i < len; i++) {
              if (existTagDatas[i]) {
                var values = existTagDatas[i].value.split('/');
                if (
                  (values.length > 0 && values[values.length - 1] == mailAddress) ||
                  existTagDatas[i].value == mailAddress ||
                  existTags.indexOf(mailAddress) != -1
                ) {
                  exist = existTags[i];
                  break;
                }
              }
            }
          }
          if (exist != null) {
            appModal.info('不允许添加重复联系人');
            return false;
          }

          var email = _this.isEmail(mailAddress);
          var canEdit = email && mailAddress == $(this).text();
          $focusMailInput.data('validateInput', false);
          $focusMailInput.tagEditor('addTag', $(this).text(), true, canEdit, {
            value: mailAddress
          });
          $focusMailInput.data('validateInput', true);
        } else {
          appModal.info('请先选中收件、抄送或者密送输入框');
        }
      });

      $('.mail-table', $container).on('click', function () {
        if (window.event && !$(window.event.target).is('.tag-editor') && !$(window.event.target).is('#recentContactUl li')) {
          $focusMailInput = null;
        }
      });

      //签名选择框变动事件
      $('#mailSignatureSelect', $container).on('change', function () {
        var uuid = $(this).val();
        var nodeEle = CKEDITOR.instances.content.document.getBody().find('.well_sign_div');
        if (uuid == 0) {
          //不使用签名
          if (nodeEle.count() != 0) {
            nodeEle.getItem(0).hide();
          }
        } else {
          if (nodeEle.count() != 0) {
            nodeEle.getItem(0).show();
          }
          _this.setMailSignatureContent(uuid, false);
        }
      });

      $('.addOrgUser', $container).on('click', function () {
        var $this = $(this);
        var $input = $this.closest('tr').find('input');
        var datas = $input.tagEditor('getTagDatas');
        var tags = $input.tagEditor('getTags')[0].tags;
        var initValues = [],
          initLabels = [],
          tagIndexs = [];
        if (datas.length > 0) {
          for (var i = 0, len = datas.length; i < len; i++) {
            if (datas[i] && !_this.isEmail(datas[i].value)) {
              //非邮件地址的，添加到组织弹出框进行初始化
              initValues.push(datas[i].value);
              initLabels.push(tags[i]);
              tagIndexs.push(i);
            }
          }
        }

        $.unit2.open({
          targetWindow: window,
          valueField: '',
          labelField: '',
          initValues: initValues.join(';'), //初始化真实值，数组格式,
          initLabels: initLabels.join(';'), //初始化显示值，数组格式,与initValues配套使用
          title: $(this).attr('title'),
          type: _this.allowOrgOptionIds ? _this.allowOrgOptionIds.join(';') : 'MyUnit;MyDept;MyLeader;MyUnderling;DutyGroup;AllUnits',
          multiple: true,
          inputReadOnly: false,
          valueFormat: 'all',
          userIconClass: 'mail_user_plus',
          selectTypes: 'all',
          moreOptList: _this.allowOrgOptions || [
            {
              id: 'MailContactBook',
              name: '通讯录'
            }
          ],
          callback: function (values, labels) {
            var curInputId = $input.attr('id');
            var allValues = _this.getAllTagValues(curInputId, values);
            var allSmartNamePath = _this.getSmartName(allValues, labels);
            _this.setAllTags(allSmartNamePath, curInputId, values);
          }
        });
      });

      $('#mailRightNavAccordion').on('shown.bs.collapse', function (e) {
        if (_this.ckeditorLoadFinished) {
          _this.recalculateActiveRightNavHeight(e.target);
        }
      });
    },
    removeTags: function (ele) {
      var tags = ele.tagEditor('getTags')[0].tags;
      for (i = 0; i < tags.length; i++) {
        ele.tagEditor('removeTag', tags[i]);
      }
    },
    setAllTags: function (allSmartNamePath, curTag, curTagValues) {
      var _this = this;
      var tags = ['toUserNameTagInput', 'ccUserNameTagInput', 'bccUserNameTagInput'];
      for (var i = 0; i < tags.length; i++) {
        var $ele = $('#' + tags[i], _this.widget.element);
        var oldValues = _this.getTagsValue($ele);
        _this.removeTags($ele);
        if (tags[i] === curTag) {
          for (var m = 0; m < oldValues.length; m++) {
            if (oldValues[m] && _this.isEmail(oldValues[m].value)) {
              curTagValues.push(oldValues[m].value);
            }
          }
          _this.setInputTags(allSmartNamePath, $ele, curTagValues);
        } else {
          var _values = [];
          for (var m = 0; m < oldValues.length; m++) {
            if (oldValues[m]) {
              _values.push(oldValues[m].value);
            }
          }
          _this.setInputTags(allSmartNamePath, $ele, _values);
        }
      }
    },
    setInputTags: function (allSmartNamePath, ele, values) {
      var _this = this;
      console.log(ele, ele.attr('id'));
      console.log(allSmartNamePath);
      console.log(values, '-----------');
      ele.data('validateInput', false);
      for (var i = 0; i < values.length; i++) {
        var label = '';
        if (_this.isEmail(values[i])) {
          label = values[i];
        } else if (values[i].indexOf('/') > -1) {
          label = allSmartNamePath[values[i].split('/').pop()];
        } else {
          label = allSmartNamePath[values[i]];
        }
        console.log('values:', values[i], 'label:', label);
        ele.tagEditor('addTag', label, true, false, {
          value: values[i]
        });
      }
    },
    getTagsValue: function (ele) {
      return ele.tagEditor('getTagDatas');
    },
    getAllTagValues: function (curTag, curTagValues) {
      var _this = this;
      var tags = ['toUserNameTagInput', 'ccUserNameTagInput', 'bccUserNameTagInput'];
      var allValues = [];
      for (var i = 0; i < tags.length; i++) {
        if (tags[i] === curTag) {
          allValues = _this.addTagValueToAllValues(allValues, curTagValues);
        } else {
          allValues = _this.addTagValueToAllValues(allValues, _this.getTagInputValue($('#' + tags[i], _this.widget.element)));
        }
      }
      return allValues;
    },
    addTagValueToAllValues: function (allValues, tagValues) {
      for (var i = 0; i < tagValues.length; i++) {
        if (allValues.indexOf(tagValues[i]) < 0) {
          allValues.push(tagValues[i]);
        }
      }
      return allValues;
    },
    getTagInputValue: function (ele) {
      var _this = this;
      var tagsValue = [];
      var datas = ele.tagEditor('getTagDatas');
      for (var i = 0; i < datas.length; i++) {
        if (datas[i] && !_this.isEmail(datas[i].value)) {
          tagsValue.push(datas[i].value);
        }
      }
      return tagsValue;
    },
    getSmartName: function (ids, labels) {
      var self = this;
      var res = {};
      if (ids instanceof String) {
        ids = ids.split(',');
      }
      server.JDS.restfulPost({
        url: '/proxy/api/org/tree/dialog/smartName',
        async: false,
        contentType: 'application/json',
        dataType: 'json',
        data: {
          nodeIds: ids,
          nodeNames: labels,
          nameDisplayMethod: '1'
        },
        success: function (result) {
          for (var i in result.data) {
            var _data = result.data[i];
            if (i[0] === 'U') {
              res[i] = _data.name;
            } else {
              res[i] = _data.smartNamePath || _data.name;
            }
          }
        }
      });
      return res;
    },

    loadCssLink: function () {
      // $('head').append(
      //   $('<link>', {
      //     href: staticPrefix + '/js/pt/css/webmail/wm_webmail.css',
      //     rel: 'stylesheet'
      //   }),
      //   $('<link>', {
      //     href: staticPrefix + '/js/pt/css/webmail/wm_webmail_v2.css',
      //     rel: 'stylesheet'
      //   }),
      //   $('<link>', {
      //     href: staticPrefix + '/js/jquery.tag-editor/jquery.tag-editor.css',
      //     rel: 'stylesheet'
      //   })
      // );
    },

    loadMailConfig: function () {
      var _this = this;
      $.ajax({
        url: ctx + '/webmail/getMailConfig',
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        async: true,
        success: function (res) {
          if (res) {
            // 设置发件人
            if (res.senterMailAddresses && res.senterMailAddresses.length) {
              for (var i = 0, len = res.senterMailAddresses.length; i < len; i++) {
                var options = {
                  value: res.senterMailAddresses[i].mailAddress
                };
                if (res.senterMailAddresses[i].isDefault) {
                  options.selected = 'selected';
                }
                $('#fromMailAddress').append($('<option>', options).text(res.senterMailAddresses[i].userName));
              }
            }

            if (res.attachmentSizeLimit) {
              _this.fileupload.setFileMaxSize(res.attachmentSizeLimit);
            }

            // 设置组织选择项
            _this.allowOrgOptionIds = null;
            _this.allowOrgOptions = res.allowOrgOptionList;
            if (res.allowOrgOptionList && res.allowOrgOptionList.length) {
              var ids = [];
              for (var i = 0, len = res.allowOrgOptionList.length; i < len; i++) {
                ids.push(res.allowOrgOptionList[i].id);
              }
              _this.allowOrgOptionIds = ids;
            }
          }
        }
      });
    },

    buttons: {
      btn_save: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-ptkj-baocun btn_save',
        name: '保存'
      },
      btn_sent: {
        class: 'well-btn w-btn-success btn-minier smaller iconfont icon-ptkj-tijiaofabufasong btn_sent',
        name: '发送'
      },
      btn_timing_send: {
        class: 'well-btn w-btn-success btn-minier smaller iconfont icon-xmch-daibangongzuo btn_timing_send',
        name: '定时发送'
      },
      btn_delete: {
        class: 'well-btn w-btn-danger btn-minier smaller iconfont icon-ptkj-shanchu btn_delete',
        name: '删除'
      },
      btn_transfer: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-oa-zhuanban btn_transfer',
        name: '转发'
      },
      btn_reply: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-ptkj-xinjianzhengwen btn_reply',
        name: '回复'
      },
      btn_reply_all: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-ptkj-xinjianzhengwen btn_reply_all',
        name: '回复全部'
      },
      btn_revoke: {
        class: 'well-btn w-btn-primary btn-minier smaller iconfont icon-oa-chehui btn_revoke',
        name: '撤回'
      }
    },

    loadButtons: function () {
      var boxname = commons.Browser.getQueryString('boxname');
      var _buttons = [];
      var buttons = this.buttons;
      if (!this.mailboxUuid || boxname === 'DRAFT' || this.editAgain || this.transfer || this.reply || this.replyAll) {
        _buttons.push(buttons.btn_save, buttons.btn_sent, buttons.btn_timing_send);
      }
      if (boxname === 'DRAFT') {
        _buttons.push(buttons.btn_delete);
      }
      for (var i = 0; i < _buttons.length; i++) {
        $('#mail_button_group').append(
          $('<button>', {
            class: _buttons[i].class,
            name: _buttons[i].name
          }).text('  ' + _buttons[i].name)
        );
      }
    },

    //禁止双击按钮
    forbidButtonDoubleClick: function () {
      var $container = this.widget.element;
      $('#mail_button_group button', $container).prop('disabled', true);
      window.setTimeout(function () {
        $('#mail_button_group button', $container).prop('disabled', false);
      }, 300);
    },

    sendMail: function (_this) {
      if (!_this) {
        _this = this;
      }
      _this.forbidButtonDoubleClick();

      // 获取邮件数据
      _this.collectMailData();
      // 发送人地址不能为空
      if (StringUtils.isBlank(_this.bean.fromMailAddress)) {
        appModal.alert('发件人不能为空，请联系管理员！');
        return;
      }

      if (StringUtils.isBlank(_this.bean.toMailAddress)) {
        appModal.alert('收件地址不能为空!');
        return;
      }

      if (_this.quickSendMailFrame) {
        if (!window.parent.mailEvent[_this.mailIframeuid].beforeSend($(window.document.body), _this.bean)) {
          return;
        }
      }
      _this.errorUsers(_this.sendMail, _this.callService);
    },
    callService: function (_this) {
      appModal.showMask('邮件发送中...');
      JDS.restfulPost({
        url: ctx + '/proxy/api/mail/manager/send',
        data: _this.bean,
        success: function (result) {
          if (result.code == 0) {
            var mailboxUuid = result.data.mailboxUuid || '';
            $('#mail_button_group button').prop('disabled', true);
            appModal.hideMask();
            var $dialog = appModal.dialog({
              message: '您的邮件已发送，您可在发件箱中查看收件状态！',
              title: '邮件已发送',
              width: '300',
              callback: function () {
                _this.refreshParentWindowBadgeNum();
                _this.refreshParentWindowEmailList();
              },
              shown: function () {
                $('.bootbox-close-button', $dialog).on('click', function () {
                  window.close();
                });
                $dialog.find('.modal-dialog').css({
                  'min-width': '400px'
                });
                $dialog.find('.bootbox-body').css({
                  display: 'flex',
                  'align-items': 'center',
                  'justify-content': 'center'
                });
              },
              buttons: {
                check: {
                  label: '查看收件状态',
                  className: 'well-btn w-btn-primary',
                  callback: function () {
                    window.open(
                      ctx +
                        '/web/app/pt-app/pt-webmail/pt-webmail-openmail.html?pageUuid=9a6037a5-52fe-4d82-86c0-1d068a6c0b51&mailboxUuid=' +
                        mailboxUuid,
                      '_self'
                    );
                  }
                },
                close: {
                  label: '关闭',
                  className: 'btn btn-default',
                  callback: function () {
                    window.open('about:blank', '_self').close();
                  }
                }
              }
            });
          } else {
            appModal.hideMask();
            var response = JSON.parse(jqXHR.responseText);
            // if (response.msg.indexOf('账号不存在') != -1) {
            //     response.msg = response.msg.replace(/</g, "&lt;");
            //     response.msg = response.msg.replace(/>/g, "&gt;");
            // }
            // // 提交失败
            // appModal.error("发送失败!</br>" + response.msg);
            appModal.alert(response.msg);
          }
        }
      });
    },

    errorUsers: function (callbackMail, callService) {
      var _this = this;
      var errorUsers = $('.tag-editor-error:visible');
      if (errorUsers.length > 0) {
        var errorHtml = '<div>以下地址格式有误，无法成功接收邮件：</div>';
        $.each(errorUsers, function (index, item) {
          var text = $(item).find('.tag-editor-tag').text();
          errorHtml += '<div>' + text + '</div>';
        });
        appModal.dialog({
          title: '',
          message: errorHtml,
          buttons: {
            send: {
              label: '忽略并发送',
              className: 'well-btn w-btn-primary',
              callback: function () {
                errorUsers.each(function (index, item) {
                  $(item).find('.tag-editor-delete').click();
                  $(item).remove();
                });
                callbackMail(_this);
              }
            },
            edit: {
              label: '返回编辑',
              className: 'btn btn-default',
              callback: function () {
                setTimeout(function () {
                  $(errorUsers[0]).parent().trigger('click');
                }, 100);
              }
            }
          },
          callback: function () {
            setTimeout(function () {
              $(errorUsers[0]).parent().trigger('click');
            }, 100);
          }
        });
      } else {
        continueSend();
      }

      function continueSend() {
        if (StringUtils.isBlank(_this.bean.subject)) {
          appModal.confirm('邮件没有填写主题，确定继续发送？', function (r) {
            if (r) {
              _this.bean.subject = '(无主题)';
              callService(_this);
            }
          });
        } else {
          callService(_this);
        }
      }
    },
    // 获取表单数据
    collectMailData: function () {
      var _this = this;
      $('#webmail_form', this.widget.element).form2json(this.bean);
      this.bean.content = CKEDITOR.instances.content.getData();
      this.bean.fromUserName = $('#fromMailAddress option:selected').text();
      var files = WellFileUpload.getFiles(this.ctlId);
      if (files != null) {
        var fileNames = [];
        var fileUuids = [];
        $.each(files, function () {
          fileNames.push(this.fileName);
          fileUuids.push(this.fileID);
        });
        this.bean.repoFileNames = fileNames.join(';');
        this.bean.repoFileUuids = fileUuids.join(';');
      }

      //收件人 todo
      $('#toUserNameTagInput,#ccUserNameTagInput,#bccUserNameTagInput', this.widget.element).each(function () {
        if ($(this).parents('tr:visible').length == 0) {
          return false;
        }
        var datas = $(this).tagEditor('getTagDatas');
        var tags = $(this).tagEditor('getTags')[0].tags;
        var text = [];
        var value = [];
        var id = $(this).attr('id');
        if (datas.length) {
          for (var i = 0, len = datas.length; i < len; i++) {
            text.push(tags[i]);
            var _value = datas[i] ? datas[i].value : tags[i];
            value.push(_value);
          }
        }
        _this.bean[id.replace('TagInput', '')] = text.join(';');
        _this.bean[id.replace('UserNameTagInput', 'MailAddress')] = value.join(';');
      });

      _this.bean['priority'] = $("input[name='priority']", this.widget.element).prop('checked') ? '1' : '3';
      _this.bean['readReceiptStatus'] = $("input[name='readReceiptStatus']", this.widget.element).prop('checked') ? '1' : '0';
      if (_this.editAgain) {
        _this.bean['mailboxUuid'] = '';
        _this.bean['sendTime'] = null;
      }
    },

    //刷新父窗口页面的徽章数量
    refreshParentWindowBadgeNum: function () {
      var targetWindow = this.quickSendMailFrame ? window.parent : window.opener;
      if (targetWindow) targetWindow.appContext.pageContainer.trigger('AppEmail.Change');
    },

    //刷新父窗口页面的右侧邮件列表
    refreshParentWindowEmailList: function () {
      var targetWindow = this.quickSendMailFrame ? window.parent : window.opener;
      if (targetWindow) targetWindow.appContext.pageContainer.trigger('AppEmailList.Refresh');
    },

    // 删除事件处理
    onDelete: function () {
      var _this = this;
      appModal.confirm('确定要删除吗?', function () {
        JDS.restfulPost({
          url: ctx + '/proxy/api/mail/manager/delete',
          data: {
            mailboxUuids: [_this.bean.mailboxUuid]
          },
          success: function (result) {
            if (result.code == 0) {
              // 删除成功
              appModal.success('删除成功!', function () {
                _this.refreshParentWindowBadgeNum();
                _this.refreshParentWindowEmailList();
                window.location = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html';
              });
            } else {
              // 删除失败
              appModal.error('删除失败');
            }
          }
        });
      });
    },

    // 保存事件处理
    onSave: function () {
      var _this = this;
      this.forbidButtonDoubleClick();
      appModal.showMask('邮件保存中...');
      // 获取邮件数据
      this.collectMailData();
      JDS.call({
        service: 'wmWebmailOutboxService.saveDraft',
        data: [_this.bean],
        version: '',
        success: function (result) {
          $('#mail_button_group button').prop('disabled', true);
          appModal.hideMask();
          appModal.success('保存成功!', function () {
            var url = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html';
            var mailboxUuid = result.data.uuid;
            _this.refreshParentWindowBadgeNum();
            if (!_this.quickSendMailFrame) {
              _this.refreshParentWindowEmailList();
            }
            window.location = url + '?mailboxUuid=' + mailboxUuid + '&boxname=DRAFT';
          });
        },
        error: function (jqXHR) {
          appModal.hideMask();
          var msg = '';
          if (jqXHR.responseJSON && jqXHR.responseJSON.msg) {
            msg = jqXHR.responseJSON.msg;
          }
          appModal.alert('保存失败!</br>' + msg);
        }
      });
    },

    timingSend: function (_this) {
      if (!_this) {
        _this = this;
      }
      _this.forbidButtonDoubleClick();

      // 获取邮件数据
      _this.collectMailData();
      // 发送人地址不能为空
      if (StringUtils.isBlank(_this.bean.fromMailAddress)) {
        appModal.alert('发件人不能为空，请联系管理员！');
        return;
      }

      if (StringUtils.isBlank(_this.bean.toMailAddress)) {
        appModal.alert('收件地址不能为空!');
        return;
      }
      _this.errorUsers(_this.timingSend, _this.dialogTimeSend);
    },
    dialogTimeSend: function (_this) {
      if (!_this) {
        _this = this;
      }
      var title = '定时发送';
      var dlgId = 'dlgTimingSend';
      var message = "</br></br><div id='" + dlgId + "'></div>";

      function nextHour() {
        var nowDate = new Date();
        nowDate.setHours(nowDate.getHours() + 1);
        return nowDate.format('yyyy-MM-dd HH:mm');
      }

      var dlgOptions = {
        title: title,
        message: message,
        templateId: '',
        size: 'middle',
        shown: function () {
          formBuilder.buildDatetimepicker({
            container: $('#' + dlgId),
            label: '选择定时发送的时间',
            labelColSpan: 5,
            controlColSpan: 5,
            name: 'sendTime',
            isRequired: true,
            value: nextHour(),
            events: 'change',
            controlOption: {
              change: function () {
                $('#div_sendTime .error').hide();
                var sendTime = $('#sendTime', $('#' + dlgId)).val();
                if (StringUtils.isBlank(sendTime)) {
                  $('#div_sendTime .error').show();
                }
              }
            },
            timePicker: {
              format: 'datetime|yyyy-MM-dd HH:mm'
            }
          });
          $('#div_sendTime').append('<label class="error" style="text-align: left;display:none;">不能为空！</label>');
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              $('#div_sendTime .error').hide();
              var sendTime = $('#sendTime', $('#' + dlgId)).val();
              if (StringUtils.isBlank(sendTime)) {
                $('#div_sendTime .error').show();
                return false;
              }
              if (new Date(sendTime) <= new Date()) {
                appModal.error('您设置的定时时间已过期！');
                return false;
              }
              //保存草稿 定时发送
              _this.bean.sendTime = sendTime + ':00';
              JDS.call({
                service: 'wmWebmailService.timingSend',
                data: [_this.bean],
                async: false,
                success: function (result) {
                  appModal.success('保存成功！', function () {
                    var url = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-openmail.html';
                    var mailboxUuid = result.data.mailboxUuid;
                    _this.refreshParentWindowBadgeNum();
                    if (!_this.quickSendMailFrame) {
                      _this.refreshParentWindowEmailList();
                    }
                    window.location = url + '?mailboxUuid=' + mailboxUuid + '&boxname=DRAFT';
                  });
                },
                error: function (jqXHR) {
                  appModal.hideMask();
                  var msg = '';
                  if (jqXHR.responseJSON && jqXHR.responseJSON.msg) {
                    msg = jqXHR.responseJSON.msg;
                  }
                  appModal.alert('保存失败!</br>' + msg);
                }
              });

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
    },

    onTransfer: function () {
      window.location = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?mailboxUuid=' + _this.bean.mailboxUuid + '&transfer=true';
    },

    onReply: function () {
      window.location = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?mailboxUuid=' + _this.bean.mailboxUuid + '&reply=true';
    },

    onReplyAll: function () {
      window.location = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?mailboxUuid=' + _this.bean.mailboxUuid + '&replyAll=true';
    },

    loadRecentContactPersonList: function () {
      var _this = this;
      JDS.call({
        service: 'wmMailRecentContactFacadeService.queryCurrentUserRecentContacts',
        data: [],
        version: '',
        success: function (result) {
          if (result.data) {
            for (var i = 0, len = result.data.length; i < len; i++) {
              var contact = result.data[i];
              var $li = $('<li>', {
                contacterMailAddress: contact.contacterMailAddress
              }).text(contact.contacterName);
              $('#recentContactUl', _this.widget.element).append($li);
            }
          }
        }
      });
    },

    setMailSignatureContent: function (uuid, appendBr) {
      var _this = this;
      JDS.call({
        service: 'wmMailSignatureFacadeService.getSignatureDetail',
        data: [uuid],
        version: '',
        success: function (result) {
          if (result.data) {
            var paperDivNodes = CKEDITOR.instances.content.document.getBody().find('.well_mail_paper');
            var signDivNodes = CKEDITOR.instances.content.document.getBody().find('#well_signature');
            if (signDivNodes.count() != 0) {
              var signNode = signDivNodes.getItem(0);
              signNode.setHtml(result.data.signatureContent);
              signNode.setAttribute('sign_uuid', uuid);
              return;
            }
            var $signDiv = $('<div>', {
              class: 'well_sign_div',
              style: 'margin-top:20px;'
            });
            var $splitLine = $('<div>', {
              style: 'color:#909090;font-family:Arial Narrow;font-size:12px;'
            }).text('---------------------');
            var $contentDiv = $('<div>', {
              id: 'well_signature',
              sign_uuid: result.data.uuid
            }).html(result.data.signatureContent);
            $signDiv.append($splitLine, $contentDiv);
            var $br = '</br></br></br>';
            if (paperDivNodes.count() == 0) {
              CKEDITOR.instances.content.document.getBody().appendHtml((appendBr ? $br : '') + $signDiv[0].outerHTML);
            } else {
              paperDivNodes.getItem(0).appendHtml((appendBr ? $br : '') + $signDiv[0].outerHTML);
            }
          }
        },
        async: false
      });
    },

    loadMailSignatureSelectOptions: function () {
      var _this = this;
      if (_this.editAgain) {
        var signdivs = CKEDITOR.instances.content.document.getBody().find('div[sign_uuid]');
        if (signdivs.count() > 0) {
          signdivs.getItem(0).setAttribute('id', 'well_signature');
          signdivs.getItem(0).getParent().setAttribute('class', 'well_sign_div');
          signdivs.getItem(0).getParent().getParent().setAttribute('class', 'well_mail_paper');
        }
      }
      JDS.call({
        service: 'wmMailSignatureFacadeService.queryCurrentUserMailSignatures',
        data: [],
        version: '',
        success: function (result) {
          if (result.data) {
            for (var i = 0, len = result.data.length; i < len; i++) {
              var selected = result.data[i].isDefault;
              //如果是草稿的邮件、或者之前的渲染模板的时候带有签名，则不进行默认签名下拉选择框的默认选择
              if (_this.bean.status == 0 || _this.templateHasSignature || _this.editAgain) {
                var signDivNodes = CKEDITOR.instances.content.document.getBody().find('#well_signature');
                if (signDivNodes.count() > 0 && signDivNodes.getItem(0).isVisible()) {
                  selected = signDivNodes.getItem(0).getAttribute('sign_uuid') == result.data[i].uuid || selected;
                } else {
                  if (_this.bean.status == 0 || _this.editAgain) {
                    //草稿情况下，之前保存的时候没有使用签名，则也不能加载默认签名
                    selected = false;
                  }
                }
              }

              $('#mailSignatureSelect').append(
                $('<option>', {
                  text: result.data[i].signatureName,
                  value: result.data[i].uuid,
                  selected: selected
                })
              );

              //写信时候（包括回复、转发），且有默认签名，无模板签名的情况下，加载签名详情
              if (!_this.bean.mailboxUuid && result.data[i].isDefault && !_this.templateHasSignature && !_this.editAgain) {
                _this.setMailSignatureContent(result.data[i].uuid, true);
              }
            }
          }
        },
        async: false
      });
    },

    setMailPaper: function () {
      var _this = this;
      JDS.call({
        service: 'wmMailPaperFacadeService.queryCurrentUserDefaultPaper',
        data: [],
        version: '',
        success: function (result) {
          _this.renderMailPaperToCkeditor(result.data);
          //填充模板
          _this.loadMailTemplate(result.data != null);
          //设置签名
          _this.loadMailSignatureSelectOptions();

          if (_this.reply || _this.replyAll || _this.transfer) {
            //回复/转发的时候要附带原始邮件
            CKEDITOR.instances.content.document.getBody().appendHtml(_this.bean.content);
          }
        }
      });
    },

    renderMailPaperToCkeditor: function (paper) {
      this.mailPaper = paper;
      var paperDivNodes = CKEDITOR.instances.content.document.getBody().find('.well_mail_paper');
      if (paperDivNodes.count() == 0) {
        var $div = $("<div class='well_mail_paper'>").html('<p></br></p>');
        CKEDITOR.instances.content.document.getBody().setHtml($div[0].outerHTML);
        CKEDITOR.instances.content.document.getBody().setStyle('margin', '0px');
        paperDivNodes = CKEDITOR.instances.content.document.getBody().find('.well_mail_paper');
      }
      var divItem = paperDivNodes.getItem(0);
      divItem.setAttribute('style', '');
      if (paper) {
        divItem.setStyle('min-height', '550px');
        divItem.setStyle('min-height', '550px');
        divItem.setStyle('background-image', 'url(' + paper.backgroundImgUrl + ')');
        divItem.setStyle('padding', '100px 55px 200px');
        if (paper.backgroundColor) {
          divItem.setStyle('background-color', paper.backgroundColor);
        }
        if (paper.backgroundPosition) {
          divItem.setStyle('background-position', paper.backgroundPosition.toLowerCase().replace('_', ' '));
        }
        if (paper.backgroundRepeat) {
          divItem.setStyle('background-repeat', paper.backgroundRepeat.toLowerCase().replace('_', '-'));
        }
      }
    },

    loadMailTemplate: function (hasMailPaper) {
      var _this = this;
      JDS.call({
        service: 'wmMailTemplateFacadeService.getCurrentUserDefaultTemplate',
        data: [],
        version: '',
        success: function (result) {
          if (result.data) {
            // if (!hasMailPaper) {//没有信纸的情况下，直接把模板内容添加到ck的body节点下
            //     CKEDITOR.instances.content.document.getBody().appendHtml(result.data.contentRendered);
            // } else {//在信纸div的节点下添加模板内容
            //     var paperDivNodes = CKEDITOR.instances.content.document.getBody().find('.well_mail_paper');
            //     paperDivNodes.getItem(0).appendHtml(result.data.contentRendered);
            // }

            var paperDivNodes = CKEDITOR.instances.content.document.getBody().find('.well_mail_paper');
            paperDivNodes.getItem(0).appendHtml(result.data.contentRendered);

            //加载的信纸是否有带签名，如果带有签名，则接下来的签名设置步骤就不进行默认签名设置了
            _this.templateHasSignature = result.data.contentRendered.indexOf('well_signature') != -1;
          }
        },
        async: false
      });
    },

    initCkeditorPlugin: function () {
      var _this = this;
      var customCkeditorPath = staticPrefix + '/dyform/explain/ckeditor'; // 自定义ckeditor相关配置的路径
      CKEDITOR.plugins.basePath = customCkeditorPath + '/plugins/'; // 自定义ckeditor的插件路径
      CKEDITOR.replace('content', {
        allowedContent: true,
        enterMode: CKEDITOR.ENTER_P,
        font_names:
          '宋体/宋体;黑体/黑体;仿宋/仿宋_GB2312;楷体/楷体_GB2312;隶书/隶书;幼圆/幼圆;微软雅黑/微软雅黑;' + CKEDITOR.config.font_names,
        toolbarStartupExpanded: true,
        toolbarCanCollapse: true,
        customConfig: customCkeditorPath + '/dyform_config.js',
        height: '100%',
        toolbar: [
          [
            'Bold',
            'Italic',
            'Underline',
            'NumberedList',
            'BulletedList',
            '-',
            'Outdent',
            'Indent',
            'JustifyLeft',
            'JustifyCenter',
            'JustifyRight',
            'JustifyBlock',
            'Undo',
            'Redo',
            'changeMode',
            'Maximize'
          ],
          ['Font', 'FontSize', 'TextColor', 'BGColor', 'Blockquote', 'Link', 'Image', 'Table', 'Smiley', 'Source']
        ],

        pasteFromWordRemoveStyles: true,
        forcePasteAsPlainText: false,
        pasteFromWordKeepsStructure: false,
        pasteFromWordRemoveFontStyles: false,
        pasteFromWordPromptCleanup: true, // 是否提示保留word样式
        pasteFromWordNumberedHeadingToList: true,

        on: {
          paste: function (evt) {
            handleCkeditorPaste(evt);
          },
          instanceReady: function (ev) {
            // Output paragraphs as <p>Text</p>.
            this.dataProcessor.writer.setRules('p', {
              indent: true,
              breakBeforeOpen: false,
              breakAfterOpen: false,
              breakBeforeClose: false,
              breakAfterClose: false
            });

            if (!_this.bean.mailboxUuid) {
              //写新邮件，加载信纸
              _this.setMailPaper();
            } else {
              //草稿
              CKEDITOR.instances.content.setData(_this.bean.content);
              if (_this.bean.content != null && _this.bean.content.indexOf('well_mail_paper') != -1) {
                CKEDITOR.instances.content.document.getBody().setStyle('margin', '0px');
              }
              //加载签名
              _this.loadMailSignatureSelectOptions();
            }

            var _path = CKEDITOR.plugins.registered.changeMode.path;
            var _name = 'content';
            var iconDown = _path + 'images/iconDown.png';
            var cke_toolbar_lastChild = $('#cke_' + _name + ' .cke_toolbar:last-child');
            var cke_button__changemode_icon = $('#cke_' + _name + ' .cke_button__changemode_icon');
            cke_toolbar_lastChild.hide();
            cke_button__changemode_icon.css('backgroundImage', 'url(' + iconDown + ')');
          },
          loaded: function (ev) {
            //根据浏览器高度，自适应调整正文框的高度
            var height = $(window).height() - $('#content').parent().offset().top - 180;
            $('.cke_contents').css('height', height);
            _this.ckeditorLoadFinished = true;
            _this.recalculateActiveRightNavHeight($('.mail-right-nav-bar-content:eq(0)'));
          },
          focus: function () {
            $('.user-name-list').hide();
          }
        }
      });
    },

    loadSystemMailPaperOptions: function () {
      var _this = this;
      JDS.call({
        service: 'wmMailPaperFacadeService.querySystemMailPapers',
        data: [],
        version: '',
        success: function (result) {
          if (result.data) {
            for (var i = 0, len = result.data.length; i < len; i++) {
              var paper = result.data[i];
              if (staticPrefix) {
                paper.backgroundImgUrl =
                  paper.backgroundImgUrl.indexOf('/resources/pt/images') > -1
                    ? paper.backgroundImgUrl.split('/resources/pt/images')[1]
                    : paper.backgroundImgUrl;
              }
              paper.backgroundImgUrl = staticPrefix + paper.backgroundImgUrl; //兼容旧版地址
              var $img = $('<img>', {
                class: 'mail-paper-img',
                src: paper.backgroundImgUrl,
                title: '点击使用或者取消使用信纸'
              });
              $img.data('data', paper);
              $('#mailPaperContainer', _this.widget.element).append($img);
            }

            $('img', $('#mailPaperContainer', _this.widget.element)).on('click', function () {
              var $activeImg = $('#mailPaperContainer .active', _this.widget.element);
              if ($activeImg.length && $activeImg.attr('src') == $(this).attr('src')) {
                $('#mailPaperContainer .active', _this.widget.element).removeClass('active');
                _this.renderMailPaperToCkeditor(null);
                return false;
              }
              $('#mailPaperContainer .active', _this.widget.element).removeClass('active');
              _this.renderMailPaperToCkeditor($(this).data('data'));
              $(this).addClass('active');
            });
          }
        },
        error: function (jqXHR) {
          appModal.info('保存更改信纸失败');
        },
        async: true
      });
    },
    recalculateActiveRightNavHeight: function ($activeNav) {
      var maxHeight = $('.mail_recent_contact_td', this.widget.element).height() - 90;

      $($activeNav)
        .css({
          overflow: 'auto'
        })
        .animate(
          {
            height: maxHeight
          },
          'fast'
        );
    }
  });
  return AppEmailSentHtmlDevelopment;
});
