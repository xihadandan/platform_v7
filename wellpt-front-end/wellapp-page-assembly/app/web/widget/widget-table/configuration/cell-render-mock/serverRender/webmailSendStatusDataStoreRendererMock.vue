<template>
  <div>
    <a-form-model-item label="示例：">
      <span v-html="text"></span>
    </a-form-model-item>
  </div>
</template>

<script>
export default {
  name: 'webmailSendStatusDataStoreRendererMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      读取行数据中字段"UUID"的值得到
      revokeStatus、sendStatus都是int
      revokeStatus!= null && revokeStatus!= 0 已撤回
      发送状态（0：待解析收件地址(待发送)，1：投递成功(已发送)，2：投递失败(发送失败)）
      */
      text: `
      <span class="mail_icon mail_send_success" style="color:#0fbb0f" title="投递成功">
      </span>
       当前列的值
      `
    };
  }
};
</script>

<style lang="less">
.mail_send_success {
  width: 16px;
  height: 16px;
  display: inline-block;
  float: none;
  background: url(/static/themes/images/mail/success.png) no-repeat;
  margin-top: 5px;
  overflow: hidden;
  cursor: pointer;
  margin-right: 3px;
  vertical-align: text-bottom;
}
</style>
