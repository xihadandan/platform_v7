<template>
  <a-modal
    :title="title"
    class="myMsgSettingModal"
    :visible="visible"
    width="700px"
    :bodyStyle="{ padding: '24px 10px' }"
    :maskClosable="false"
    @cancel="handleCancel"
  >
    <div style="height: 530px; overflow: auto">
      <MessageSetting ref="settingRef"></MessageSetting>
    </div>
    <template slot="footer">
      <a-button type="primary" @click="handleOk">保存</a-button>
      <a-button @click="handleReset">恢复默认</a-button>
      <a-button @click="handleCancel">取消</a-button>
    </template>
  </a-modal>
</template>
<script type="text/babel">
import './msg.less';
export default {
  name: 'MessageOnlineSettingModal',
  props: {
    show: {
      type: Boolean,
      default: false
    }
  },
  components: { MessageSetting: () => import('./message-online-setting.vue') },
  data() {
    return {
      visible: false,
      title: '消息通知设置'
    };
  },
  watch: {
    show(v) {
      this.visible = v;
    }
  },
  created() {},
  methods: {
    handleCancel() {
      this.visible = false;
      this.$emit('close');
    },
    handleOk() {
      this.$refs.settingRef.save(() => {
        this.handleCancel();
      });
    },
    handleReset() {
      this.$refs.settingRef.reset(() => {
        // this.handleCancel();
      });
    }
  }
};
</script>
