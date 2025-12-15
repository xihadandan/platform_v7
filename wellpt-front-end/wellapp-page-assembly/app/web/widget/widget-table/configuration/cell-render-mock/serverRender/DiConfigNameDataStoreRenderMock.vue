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
  name: 'DiConfigNameDataStoreRenderMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      text: '名称'
    };
  }
};
</script>
