<template>
  <div>
    <a-form-model-item label="示例: 将数据为html代码的内容渲染为html标签">
      <div>
        {{ htmlString }}
      </div>
      <div v-html="htmlString"></div>
    </a-form-model-item>
  </div>
</template>

<script>
export default {
  name: 'CellDataHtmlRenderMock',
  props: {},
  data() {
    return {
      htmlString: '<p style="color:red;font-weight:bolder;">这是一段 HTML 代码</p>'
    };
  },
  components: {}
};
</script>
