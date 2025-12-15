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
  name: 'systemUnitNameMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      传入系统单位idsystemUnitId：S0000000000
      返回系统单位名称：基础平台
      */
      text: '基础平台'
    };
  }
};
</script>
