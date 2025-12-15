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
  name: 'favoriteRendererMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      /* 
      传入userId：U_283626575096709120
      返回用户名：总裁
      */
      text: '收藏渲染器'
    };
  }
};
</script>
