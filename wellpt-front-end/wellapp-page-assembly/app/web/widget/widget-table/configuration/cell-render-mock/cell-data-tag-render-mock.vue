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
import CellDataTagRender from '../../cell-render/cell-data-tag-render.vue';
export default {
  name: 'CellDataTagRenderMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      rowIndex: 0,
      text: '标签1;标签2'
    };
  },
  components: {
    CellDataTagRender
  }
};
</script>
