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
  name: 'DyformNameDataStoreRenderMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      传入formUuids： 多个用";|,"隔开
      返回表单名：多个用", "隔开
      */
      text: '表单名称'
    };
  }
};
</script>
