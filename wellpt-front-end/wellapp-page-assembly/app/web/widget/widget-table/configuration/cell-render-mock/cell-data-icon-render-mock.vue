<template>
  <div>
    <a-form-model-item label="示例：">
      <template v-if="cellRenderName">
        <component :is="cellRenderName" :rowIndex="rowIndex" :text="text" />
      </template>
    </a-form-model-item>
  </div>
</template>

<script>
import CellDataIconRender from '../../cell-render/cell-data-icon-render.vue';
export default {
  name: 'CellDataIconRenderMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      rowIndex: 0,
      text: 'pticon iconfont icon-a-iconfonticon-logo_wellinfo-01'
    };
  },
  components: {
    CellDataIconRender
  }
};
</script>
