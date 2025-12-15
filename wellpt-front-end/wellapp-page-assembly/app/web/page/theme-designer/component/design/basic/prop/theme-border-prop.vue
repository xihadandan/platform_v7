<template>
  <a-form-model layout="vertical" :colon="false">
    <a-form-model-item v-for="(border, i) in item.derive" :key="'item_' + i" :label="border.code">
      <a-select :options="borderStyleOptions" style="width: 100%" v-model="border.value" v-if="isBorderStyle"></a-select>

      <a-input-group compact v-else>
        <a-input-number :value="numberValue(border.value)" @change="e => onChange(border, e)" style="width: 80%" />
        <a-button>px</a-button>
      </a-input-group>
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ThemeBorderProp',
  props: {
    item: Object
  },
  components: {},
  computed: {},
  data() {
    return {
      isBorderStyle: this.item.code === '--w-border-style',
      borderStyleOptions: [
        { label: '实线', value: 'solid' },
        { label: '虚线', value: 'dashed' },
        { label: '点线', value: 'dotted' },
        { label: '双线', value: 'double' }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    numberValue(v) {
      return parseInt(v);
    },
    onChange(rad, e) {
      rad.value = e + 'px';
    }
  }
};
</script>
