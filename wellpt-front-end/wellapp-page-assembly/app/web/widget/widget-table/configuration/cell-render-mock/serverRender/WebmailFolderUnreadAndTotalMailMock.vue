<template>
  <div>
    <a-form-model-item label="示例：">
      <template v-if="cellRenderName">
        {{ text }}
      </template>
    </a-form-model-item>
  </div>
</template>

<script>
export default {
  name: 'WebmailFolderUnreadAndTotalMailMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      渲染器会读取当前用户id、行数据中字段code值
      code可选值
      "INBOX";//收件箱
      "OUTBOX";//发件箱
      "DRAFT";//草稿箱
      "RECYCLE";//回收站

      返回：未读数 / 总数
      */
      text: '7 / 10'
    };
  }
};
</script>
