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
  name: 'dataDictionaryRendererMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      多个用分隔符分号“;”
      配置：数据字典：Nationality（平台字典-元数据-民族）
      传入：01;02
      返回：汉族;蒙古族
      */
      text: '汉族;蒙古族'
    };
  }
};
</script>
