<template>
  <a-form-model-item label="" :wrapper-col="{ span: 24 }">
    <div class="color-palette-list">
      <div v-for="(item, index) in colors" :key="'color_' + item" class="flex f_x_s color-palette-item">
        <div>
          <div class="color-widget-block" :style="{ background: item }"></div>
          {{ item }}
        </div>
        <div class="oprate">
          <a-button type="link" class="icon-only" @click="delColor(index)" title="删除">
            <Icon type="pticon iconfont icon-ptkj-shanchu" />
          </a-button>
          <span class="drag-btn-handler ant-btn ant-btn-link icon-only" @click.stop="() => {}" title="拖动排序">
            <Icon type="pticon iconfont icon-ptkj-tuodong" />
          </span>
        </div>
      </div>
    </div>
    <div>
      <ColorPicker displayType="custom" @input="onColorPickOk">
        <a-button icon="plus" />
      </ColorPicker>
    </div>
  </a-form-model-item>
</template>

<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';

export default {
  name: 'ColorPaletteConfiguration',
  mixins: [draggable],
  components: {
    ColorPicker
  },
  props: {
    designer: Object,
    widget: Object,
    colors: Array
  },
  data() {
    return {
      newColor: '',
      status: false
    };
  },
  created() {},
  methods: {
    delColor(index) {
      this.colors.splice(index, 1);
    },
    onColorPickOk(color) {
      this.colors.push(color);
    }
  },
  mounted() {
    this.tableDraggable(this.colors, this.$el.querySelector('.color-palette-list'), '.drag-btn-handler');
  }
};
</script>

<style lang="less" scoped>
.color-palette-list {
  .color-palette-item {
    border: 1px solid var(--w-border-color-base);
    border-radius: var(--w-border-radius-2);
    padding-right: var(--w-padding-3xs);
    line-height: 32px;
    margin-bottom: var(--w-margin-xs);

    .color-widget-block {
      vertical-align: middle;
    }

    .oprate {
      cursor: pointer;
    }
  }
}
</style>
