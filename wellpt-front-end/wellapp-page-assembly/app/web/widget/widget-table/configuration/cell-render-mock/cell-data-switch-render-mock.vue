<template>
  <div>
    <a-form-model-item label="示例：">
      <template v-if="cellRenderName">
        <component :is="cellRenderName" :slotOption="slotOption" :rowIndex="rowIndex" :text="text" />
      </template>
    </a-form-model-item>
  </div>
</template>

<script>
import CellDataSwitchRender from '../../cell-render/cell-data-switch-render.vue';
export default {
  name: 'CellDataSwitchRenderMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      rowIndex: 0,
      slotOption: {
        options: {
          closeText: '关闭',
          closeValue: '0',
          openText: '开启',
          openValue: '1'
        },
        serverRender: false
      },
      text: '1'
    };
  },
  components: {
    CellDataSwitchRender
  }
};
</script>
