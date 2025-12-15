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
  name: 'percentageDataStoreRendererMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      百分比渲染器
      为空时返回“0.0%”
      Double doubleValues = Double.valueOf(value.toString());
      return (doubleValues * 100) + "%";
      传入：0.8
      返回：80.0%
      */
      text: '80.0%'
    };
  }
};
</script>
