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
import CellDataColorfulTagRender from '../../cell-render/cell-data-colorful-tag-render.vue';
export default {
  name: 'CellDataColorfulTagRenderMock',
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
          split: true,
          splitSymbol: ';',
          tagType: 'border',
          tags: [
            {
              key: 'UQNWpdLxDwrARjPeAJdYmpKKMShLHNFE',
              tag: {
                color: '--w-primary-color',
                key: 'UQNWpdLxDwrARjPeAJdYmpKKMShLHNFE',
                text: '主题色'
              },
              value: '1'
            },
            {
              key: 'TkDQvWmSccVUMRQgrlMBCDiJHEhggcbX',
              tag: {
                color: '--w-success-color',
                key: 'TkDQvWmSccVUMRQgrlMBCDiJHEhggcbX',
                text: '成功色'
              },
              value: '2'
            }
          ]
        },
        serverRender: false
      },
      text: '1;2'
    };
  },
  components: {
    CellDataColorfulTagRender
  }
};
</script>
