<template>
  <div>
    <a-form-model-item label="拆分的分隔符">
      <a-input v-model.trim="options.separator" allow-clear />
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'SplitFormatterOption',
  props: {
    options: Object
  },
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {
    if (!this.options.hasOwnProperty('separator')) {
      this.$set(this.options, 'separator', undefined);
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
