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
  name: 'WebmailUseCapacityDataStoreRenderMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      默认返回0B
      返回B/K/M/G 表示占用空间大小的字符串

      */
      text: '7M'
    };
  }
};
</script>
