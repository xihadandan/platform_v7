<template>
  <div>
    <a-form-model-item label="选择代码">
      <JsHookSelect :designer="designer" :widget="widget" v-model="options.jsFunction" />
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'executeDevJsFunctionConfig',
  mixins: [],
  props: {
    designer: Object,
    widget: Object,
    options: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {},
  mounted() {}
};
</script>
