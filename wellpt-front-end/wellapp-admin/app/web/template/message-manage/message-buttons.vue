<template>
  <div>
    <WidgetTableButtons
      :button="rowEndButton"
      :meta="form"
      :size="options.size"
      :eventWidget="options.getTableSelf || options.getModalSelf"
      @button-click="clickHandle"
      :developJsInstance="options.developJsInstance"
    ></WidgetTableButtons>
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import { messageDetailModal, presetEventsInfo } from '@admin/app/web/template/message-manage/message-utils.js';
import { assignIn, map, each, groupBy } from 'lodash';
export default {
  name: 'MessageButtons',
  inject: ['pageContext'],
  props: {
    data: {
      type: Object,
      defalut: () => {
        return {};
      }
    },
    options: {
      type: Object,
      defalut: () => {
        return {};
      }
    },
    disabled: {
      type: Boolean,
      defalut: false
    }
  },
  components: {},
  data() {
    let form = deepClone(this.data);
    if (this.data.MESSAGE_PARM || this.data.messageParm) {
      let messageParm = this.data.MESSAGE_PARM || this.data.messageParm;
      form = JSON.parse(messageParm);
      form.messageParm = messageParm;
    }
    return {
      form
    };
  },
  provide: {},
  computed: {
    messageParm() {
      let messageParm = this.form.MESSAGE_PARM || this.form.messageParm;
      return messageParm ? JSON.parse(messageParm) : undefined;
    },
    callbackJson() {
      let callbackJson = this.messageParm && this.messageParm.callbackJson && JSON.parse(this.messageParm.callbackJson);
      if (!callbackJson) {
        callbackJson = {};
      }
      if (typeof callbackJson === 'object' && !callbackJson.adjusted) {
        callbackJson.adjusted = true;
        callbackJson.events = [];
        callbackJson.events.push(presetEventsInfo.forward);
        callbackJson.events.push(presetEventsInfo.delete);
      }
      return callbackJson;
    },
    relatedTitle() {
      if (this.messageParm) {
        return this.messageParm.relatedTitle;
      } else {
        return this.form.relatedTitle || this.form.RELATED_TITLE || '';
      }
    },
    jumpRelatedUrl() {
      let url = '';
      if (this.messageParm) {
        url = this.messageParm.relatedUrl;
      } else {
        url = this.form.relatedUrl || this.form.RELATED_URL || '';
      }
      if (url && url.indexOf('http') != 0 && this._$SYSTEM_ID) {
        url = '/sys/' + this._$SYSTEM_ID + '/_' + url;
      }
      return url;
    },
    relatedUrl() {
      let url = '';
      if (this.messageParm) {
        url = this.messageParm.relatedUrl;
      } else {
        url = this.form.relatedUrl || this.form.RELATED_URL || '';
      }
      return url;
    },
    type() {
      return this.options.type;
    },
    rowEndButton() {
      let rowEndButton = {
        buttons: [],
        buttonGroup: {
          type: 'fixedGroup',
          groups: [],
          style: { textHidden: false, type: 'default', rightDownIconVisible: false },
          dynamicGroupName: '更多',
          dynamicGroupBtnThreshold: 3
        }
      };
      // relatedUrl跳转按钮显示在按钮组里
      if (this.options.relatedBtnInButtons && this.jumpRelatedUrl) {
        rowEndButton.buttons.push(
          this.newBtn({
            code: 'related_btn',
            title: this.relatedTitle,
            style: { type: 'link', icon: 'pticon iconfont icon-ptkj-liucheng' }
          })
        );
      }
      if (this.type == 'sendMsg') {
        rowEndButton.buttons.push(
          this.newBtn({
            code: 'sendMsg_btn',
            title: '发送',
            style: { type: 'primary' }
          })
        );
      } else if (this.callbackJson) {
        each(this.callbackJson.events, item => {
          if (item.id) {
            if (item.displayLocation.indexOf(this.options.displayLocation) > -1) {
              rowEndButton.buttons.push(item);
            }
          } else if (item.type == 'preset') {
            if (item.displayLocation.indexOf(this.options.displayLocation) > -1) {
              // 6.2配置，除内置按扭外，其他不处理
              rowEndButton.buttons.push(
                this.newBtn({
                  code: item.code,
                  title: item.text,
                  style: { type: (item.btnLib && item.btnLib.type) || '' }
                })
              );
            }
          }
        });
        each(groupBy(rowEndButton.buttons, 'group'), (item, index) => {
          if (index && index !== 'undefined') {
            rowEndButton.buttonGroup.groups.push({
              id: generateId(),
              name: index,
              buttonIds: map(item, 'id')
            });
          }
        });
      }
      if (this.options.isModal) {
        rowEndButton.buttons.push(
          this.newBtn({
            code: 'cancel_btn',
            title: '取消'
          })
        );
      }

      return rowEndButton;
    },
    pageContext1() {
      return this.pageContext || this.options.pageContext || undefined;
    },
    pageType() {
      if (this.options.getTableSelf && this.options.getTableSelf() && this.options.getTableSelf().pageType) {
        return this.options.getTableSelf().pageType;
      }
      return '';
    }
  },
  mounted() {
    // console.log(this.form);
  },
  methods: {
    newBtn(button) {
      return {
        id: generateId(),
        code: button.code,
        title: button.title,
        visible: true,
        role: [],
        style: assignIn({ textHidden: false }, button.style),
        switch: { defaultChecked: true, checkedCondition: { operator: 'true' } },
        defaultVisible: true,
        defaultVisibleVar: { operator: 'true', enable: false },
        visibleToggle: {}
      };
    },
    clickHandle(button) {
      if (button.code && typeof this[button.code] == 'function') {
        this[button.code]();
      } else {
      }
    },
    related_btn() {
      window.open(this.jumpRelatedUrl);
    },
    // 弹框，发送消息
    sendMsg_btn() {
      if (this.options.getDetailRef()) {
        this.options.getDetailRef().collectForm(bean => {
          $axios.post('/message/content/submitmessage', bean).then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('发送成功');
              this.options.getModalSelf().handleCancel();
              this.refreshList('refetchMessageOutBoxManangeTable');
            } else {
              this.$message.error('发送失败');
            }
          });
        });
      }
    },
    //关闭弹框
    cancel_btn() {
      this.options.getModalSelf().handleCancel();
    },
    notificationClose() {
      this.options.notification.close(this.options.key);
    },
    // 获取回复消息内容
    getReplybody() {
      var replybody =
        '<br/><p></p>--------------' +
        this.form.sentTime +
        '  ' +
        this.form.senderName +
        ' 在源消息中写道------</p><p>主题:' +
        this.form.subject +
        '</p><p>内容:</p>' +
        this.form.body;
      return replybody;
    },
    // 转发
    btnForwardMsg() {
      let forwardMsg = {
        body: this.getReplybody(),
        subject: 'Fw:' + this.form.subject,
        relatedUrl: this.relatedUrl,
        relatedTitle: this.relatedTitle,
        attach: this.messageParm && this.messageParm.attach
      };
      if (this.options.displayLocation == 'message-detail') {
        this.cancel_btn();
        setTimeout(() => {
          this.options.getModalSelf().showModal({
            messageData: forwardMsg,
            title: '转发消息',
            type: 'sendMsg'
          });
        }, 0);
      } else if (this.options.displayLocation == 'message-list') {
        this.$parent.$refs.messgageDetailRef.showModal({
          messageData: forwardMsg,
          title: '转发消息',
          type: 'sendMsg'
        });
      } else if (this.options.displayLocation == 'message-modal') {
        this.$parent.options.notificationBoxSelf.$refs.messgageDetailRef.showModal({
          messageData: forwardMsg,
          title: '转发消息',
          type: 'sendMsg'
        });
      }
      if (this.options.isModal) {
        // this.cancel_btn();
      } else if (this.options.notification) {
        this.notificationClose();
      }
      this.$emit('close');
    },
    // 删除
    btnDelMsg() {
      let _this = this;
      let url = '/message/content/deleteInboxMessage';
      if (this.pageType == 'outBox') {
        url = '/message/content/deleteOutboxMessage';
      }
      this.$confirm({
        title: '确认要删除吗?',
        onOk() {
          $axios.post(url, { uuids: _this.data.uuid }).then(({ data }) => {
            if (data.code == 0) {
              _this.$message.success('删除成功');
              _this.refreshList();
              if (_this.options.isModal) {
                _this.cancel_btn();
              }
              _this.$emit('refresh');
              _this.$emit('close');
            }
          });
        },
        onCancel() {}
      });
    },
    // 回复
    btnReplyMsg() {
      let replyMsg = {
        body: this.getReplybody(),
        subject: 'Re:' + this.form.subject,
        relatedUrl: this.relatedUrl,
        relatedTitle: this.relatedTitle,
        attach: this.messageParm && this.messageParm.attach,
        recipient: this.form.sender,
        recipientName: this.form.senderName
      };
      if (this.options.displayLocation == 'message-detail') {
        this.cancel_btn();
        setTimeout(() => {
          this.options.getModalSelf().showModal({
            messageData: replyMsg,
            title: '回复消息',
            type: 'sendMsg'
          });
        }, 0);
      } else if (this.options.displayLocation == 'message-list') {
        this.$parent.$refs.messgageDetailRef.showModal({
          messageData: replyMsg,
          title: '回复消息',
          type: 'sendMsg'
        });
      } else if (this.options.displayLocation == 'message-modal') {
        this.$parent.options.notificationBoxSelf.$refs.messgageDetailRef.showModal({
          messageData: replyMsg,
          title: '回复消息',
          type: 'sendMsg'
        });
      }
      // messageDetailModal({
      //   messageData: replyMsg,
      //   title: '回复消息',
      //   type: 'sendMsg',
      //   pageContext: this.pageContext1
      // });
      if (this.options.isModal) {
        // this.cancel_btn();
      } else if (this.options.notification) {
        this.notificationClose();
      }
      this.$emit('close');
    },
    // 刷新列表
    refreshList(eventName) {
      if (this.options.getTableSelf) {
        // 从列表进入
        let widget = this.options.getTableSelf();
        let options = widget.getDataSourceProvider().options;
        widget.refetch && widget.refetch(options);
        if (this.pageType != 'outBox' && widget.$developJsInstance.messageInBoxManageTableDevelopment) {
          widget.$developJsInstance.messageInBoxManageTableDevelopment.refreshMessageData();
        }
      } else if (this.pageContext1) {
        this.pageContext1.emitEvent(eventName || `refetchMessageInBoxManangeTable`, { hasParamRefetch: true });
      }
    }
  }
};
</script>
