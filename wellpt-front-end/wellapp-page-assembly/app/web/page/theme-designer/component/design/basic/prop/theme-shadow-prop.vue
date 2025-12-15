<template>
  <a-collapse :default-active-key="item[0].code" :bordered="false" :accordion="true" expandIconPosition="right">
    <a-collapse-panel :header="shadow.code" v-for="(shadow, i) in item" :key="shadow.code">
      <a-descriptions layout="vertical" :column="2" :colon="false">
        <a-descriptions-item label="X轴偏移">
          <a-input-group compact>
            <a-input-number size="small" :value="getOffsetValue(shadow.value, 0)" @change="e => onChangeOffset(e, shadow, 0)" />
            <a-button size="small">px</a-button>
          </a-input-group>
        </a-descriptions-item>
        <a-descriptions-item label="Y轴偏移">
          <a-input-group compact>
            <a-input-number size="small" :value="getOffsetValue(shadow.value, 1)" @change="e => onChangeOffset(e, shadow, 1)" />
            <a-button size="small">px</a-button>
          </a-input-group>
        </a-descriptions-item>
        <a-descriptions-item label="模糊度">
          <a-input-group compact>
            <a-input-number size="small" :value="getOffsetValue(shadow.value, 2)" @change="e => onChangeOffset(e, shadow, 2)" />
            <a-button size="small">px</a-button>
          </a-input-group>
        </a-descriptions-item>
        <a-descriptions-item label="扩展值">
          <a-input-group compact>
            <a-input-number size="small" :value="getOffsetValue(shadow.value, 3)" @change="e => onChangeOffset(e, shadow, 3)" />
            <a-button size="small">px</a-button>
          </a-input-group>
        </a-descriptions-item>
        <a-descriptions-item label="阴影颜色" :span="2">
          <ColorPicker :value="getOffsetValue(shadow.value, 4)" @input="e => onChangeOffset(e, shadow, 4)" />
        </a-descriptions-item>
      </a-descriptions>
    </a-collapse-panel>
  </a-collapse>
</template>
<style lang="less"></style>
<script type="text/babel">
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
export default {
  name: 'ThemeShadowProp',
  props: { item: Array },
  components: { ColorPicker },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    getOffsetValue(value, offset) {
      let parts = value.split(' ');
      if (offset < 4) {
        let int = parseInt(parts[offset]);
        return offset == 3 ? (int == 0 ? undefined : int) : int;
      }
      return parts[offset];
    },
    onChangeOffset(value, item, offset) {
      let parts = item.value.split(' ');
      parts[offset] = offset != 4 ? value + 'px' : value;
      item.value = parts.join(' ');
    },
    onClickFormItem(e) {
      document
        .querySelector('.theme-style-panel')
        .style.setProperty(e.code.startsWith('--w-padding') ? '--preview-padding-space-size' : '--preview-margin-space-size', e.value);
    },
    numberValue(v) {
      return parseInt(v);
    },
    onChange(rad, e) {
      rad.value = e + 'px';
      this.onClickFormItem(rad);
    }
  }
};
</script>
