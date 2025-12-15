<!-- 消息详情弹出框 -->
<template>
  <div>
    <a-modal
      dialogClass="pt-modal"
      :title="title"
      :visible="visible"
      width="900px"
      :bodyStyle="{ padding: '12px 0 0' }"
      :z-index="zIndex || 1000"
      :maskClosable="false"
      :mask="mask"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleOk"
      @cancel="handleCancel"
      :getContainer="getContainer"
    >
      <Scroll style="max-height: 530px">
        <MessageDetail v-if="visible" ref="detailRef" :data="messageData" :options="options"></MessageDetail>
      </Scroll>
      <template slot="footer">
        <MessageButton v-if="visible" ref="buttonsRef" :data="messageData" :options="options"></MessageButton>
      </template>
    </a-modal>
  </div>
</template>
<script type="text/babel">
import { each, assignIn, camelCase } from 'lodash';
import { readMsgService } from '@admin/app/web/template/message-manage/message-utils.js';
import MessageDetail from './message-detail.vue';
import MessageButton from './message-buttons.vue';
export default {
  name: 'MessageDetailModal',
  inject: ['pageContext', 'vPageState', '$event'],
  props: {
    value: { type: Boolean, default: false },
    isModal: { type: Boolean, default: true }, // 属于弹框，用于判断按钮组件是否要加【取消】按钮
    displayLocation: { type: String, default: 'message-detail' }, // 根据显示位置，判断消息格式事件配置的按钮
    type: String, // sendMsg:发消息
    readOnly: { type: Boolean, default: false }, //消息详情是否只读，默认可编辑,
    zIndex: Number,
    mask: { type: Boolean, default: true },
    msgData: {
      //消息详情
      type: Object,
      default: () => {}
    },
    modalTitle: String,
    relatedBtnInButtons: { type: Boolean, default: true },
    container: Function
  },
  components: {
    MessageDetail,
    MessageButton
  },
  data: function () {
    let messageData = this.resetMsgData(this.msgData);
    let options = {
      isModal: this.isModal, // 属于弹框，用于判断按钮组件是否要加【取消】按钮
      displayLocation: this.displayLocation, // 根据显示位置，判断消息格式事件配置的按钮
      type: this.type, // sendMsg:发消息
      readOnly: this.readOnly, //消息详情是否只读，默认可编辑
      title: this.modalTitle,
      messageData
    };
    return {
      options,
      messageData,
      visible: this.value
    };
  },
  META: {
    method: {
      showModal: '打开弹框'
    }
  },
  created() {},
  computed: {
    title() {
      return (this.options && this.options.title) || '消息详情';
    }
  },
  mounted() {},
  methods: {
    getContainer() {
      return document.body;
    },
    // 打开弹框
    showModal(params) {
      let options = {
        isModal: this.isModal, // 属于弹框，用于判断按钮组件是否要加【取消】按钮
        displayLocation: this.displayLocation, // 根据显示位置，判断消息格式事件配置的按钮
        type: this.type, // sendMsg:发消息
        readOnly: this.readOnly, //消息详情是否只读，默认可编辑
        title: this.modalTitle,
        messageData: this.resetMsgData(this.msgData),
        relatedBtnInButtons: this.relatedBtnInButtons
      };
      this.messageData = this.resetMsgData(this.msgData);
      if (params && params.$evtWidget) {
        // 从配置进入
        assignIn(options, params.eventParams && params.eventParams, {
          messageData: params.meta || {} // && params.meta.rowData
        });

        // 设置已读
        if (params.meta['ISREAD'] == 0) {
          readMsgService([params.meta['UUID']], () => {
            // 刷新当前页数据,二开
            params.$evtWidget.invokeDevelopmentMethod('refreshDataByCurrent', true);
            // 刷新左侧类别徽标数据
            params.$evtWidget.pageContext.emitEvent(`refetchMessageBoxClassifyData`);
            // 刷新主导航徽标
            window.top.$app.pageContext.emitEvent(`WidgetMenu:refreshMenuBadgeNumberByCode`, { code: 'HeaderOnlineMsg' });
            // 刷新主导航未读消息列表
            window.top.$app.pageContext.emitEvent(`WidgetMenu:refreshHeaderOnlineMsgList`);
          });
        }
      } else {
        // 头部消息进入
        assignIn(options, params);
      }
      // 消息详情
      if (options && options.messageData) {
        each(options.messageData, (item, index) => {
          this.messageData[camelCase(index)] = item;
        });
      }
      this.$set(this, 'options', options);
      this.visible = true;
      this.$nextTick(() => {
        this.options.getModalSelf = this.getSelf;
        this.options.getDetailRef = this.getDetailRef;
        // this.getDetailRef().initData();
      });
    },
    getSelf() {
      return this;
    },
    getDetailRef() {
      return this.$refs.detailRef;
    },
    handleCancel() {
      this.visible = false;
      this.$emit('input', this.visible);
    },
    handleOk() {
      this.visible = false;
      this.$emit('input', this.visible);
    },
    // 消息字段都是大写
    resetMsgData(data) {
      let _data = {};
      each(data, (item, index) => {
        _data[camelCase(index)] = item;
      });
      return _data;
    }
  },
  watch: {
    value: {
      handler(v) {
        this.visible = v;
        if (this.visible) {
          this.showModal();
        }
      }
    }
  }
};
</script>
