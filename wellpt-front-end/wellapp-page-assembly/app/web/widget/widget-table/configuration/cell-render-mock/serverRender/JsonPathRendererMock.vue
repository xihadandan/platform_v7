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
      https://github.com/json-path/JsonPath
      http://127.0.0.1:7001/static/js/jsonpath/jsonpath.html
      wellapp-page-assembly\app\assets\js\design\common\design_commons.js renderOptionView
      */
      text: 'JsonPath模板渲染器'
    };
  }
};
</script>
