<template>
  <div>
    <a-form-model-item label="示例：">
      <template v-if="cellRenderName">
        <component :is="cellRenderName" :text="text" />
      </template>
    </a-form-model-item>
  </div>
</template>

<script>
import CellDataOrgElementRender from '../../cell-render/cell-data-org-element-render.vue';
export default {
  name: 'CellDataOrgElementRenderMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      text: this._$USER.userId
    };
  },
  components: {
    CellDataOrgElementRender
  }
};
</script>
