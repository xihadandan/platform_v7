<template>
  <div>
    <a-form-model-item label="示例：">
      <span v-html="text"></span>
    </a-form-model-item>
  </div>
</template>

<script>
export default {
  name: 'WebmailIconDataStoreRendererMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    /* 
    获取字段UUID的值
    */
    return {
      text: `
      <div class=\"mail_icon mail_readed\" title=\"邮件已读\"></div><div class=\"mail_icon mail_attachment\" title=\"邮件附件\"></div>
      `
    };
  }
};
</script>

<style lang="less">
.mail_readed {
  width: 18px;
  height: 16px;
  display: inline-block;
  overflow: hidden;
  cursor: pointer;
  margin-right: 3px;
  vertical-align: text-bottom;
  background: url(/static/themes/images/mail/mail_icon.png) -48px -16px no-repeat;
}
.mail_attachment {
  width: 16px;
  height: 16px;
  display: inline-block;
  overflow: hidden;
  cursor: pointer;
  margin-right: 3px;
  vertical-align: text-bottom;
  background: url(/static/themes/images/mail/mail_icon.png) -65px 2px no-repeat;
  margin-top: 3px;
}
</style>
