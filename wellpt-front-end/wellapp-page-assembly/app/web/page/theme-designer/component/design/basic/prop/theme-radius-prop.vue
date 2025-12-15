<template>
  <a-form-model layout="vertical" :colon="false">
    <a-form-model-item v-for="(rad, i) in item" :key="'item_' + i" :label="rad.code">
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
  name: 'ThemeRadiusProp',
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
    numberValue(v) {
      return parseInt(v);
    },
    onChange(rad, e) {
      rad.value = e + 'px';
    }
  }
};
</script>
