<template>
  <a-form-model layout="vertical" :colon="false">
    <a-form-model-item v-for="(rad, i) in item" :key="'item_' + i" :label="rad.code" @click.native.stop="onClickFormItem(rad)">
      <a-input-group compact>
        <a-input-number :value="numberValue(rad.value)" @change="e => onChange(rad, e)" style="width: 80%" />
        <a-button>px</a-button>
      </a-input-group>
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ThemeHeightProp',
  props: { item: Array },
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onClickFormItem(e) {
      document.querySelector('.theme-style-panel').__vue__.height = e.value;
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
