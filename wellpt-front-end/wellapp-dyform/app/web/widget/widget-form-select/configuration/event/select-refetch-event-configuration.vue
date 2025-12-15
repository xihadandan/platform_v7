<template>
  <div>
    <a-form-model :colon="false">
      <a-form-model-item label="默认查询条件">
        <a-textarea
          v-model="eventHandler.wEventParams.defaultCondition"
          :auto-size="{ minRows: 2, maxRows: 6 }"
          placeholder="请输入默认查询条件"
        />
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'WidgetFormSelectRefetchOptionEventConfiguration',
  props: {
    widget: Object,
    fromWidget: Object,
    designer: Object,
    eventHandler: Object
  },
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {
    if (this.eventHandler.wEventParams == undefined) {
      this.$set(this.eventHandler, 'wEventParams', {});
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
