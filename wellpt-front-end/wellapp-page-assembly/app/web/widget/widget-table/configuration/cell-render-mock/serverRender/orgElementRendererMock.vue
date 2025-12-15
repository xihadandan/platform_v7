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
  name: 'orgElementRendererMock',
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
      传入: S_160335352144330752/D_160398824609677312/J_160398882998583296 （eleIdPath）
      返回：厦门住房公积金中心/监督部门/督察 （eleNamePath）

      传入：U_168308282438451200（userId）
      返回：七七（userName用户名）
      */
      text: '厦门住房公积金中心/监督部门/督察;七七'
    };
  }
};
</script>
