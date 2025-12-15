<template>
  <div>
    <a-form-model-item label="开启真实值">
      <a-input v-model="options.openValue" />
    </a-form-model-item>
    <a-form-model-item label="开启显示文字">
      <a-input v-model="options.openText" />
    </a-form-model-item>
    <a-form-model-item label="关闭真实值">
      <a-input v-model="options.closeValue" />
    </a-form-model-item>
    <a-form-model-item label="关闭显示文字">
      <a-input v-model="options.closeText" />
    </a-form-model-item>
    <a-form-model-item label="变化时回调函数">
      <JsHookSelect :widget="widget" v-model="options.jsFunction" :designer="designer" />
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'CellDataSwitchRenderConfig',
  props: { options: Object, designer: Object, widget: Object },
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
