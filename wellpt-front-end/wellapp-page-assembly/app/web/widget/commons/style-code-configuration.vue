<template>
  <WidgetCodeEditor v-model="configuration.styleContent" width="auto" height="600px" lang="css" :hideError="true"></WidgetCodeEditor>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'StyleCodeConfiguration',
  props: {
    configuration: Object
  },
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
