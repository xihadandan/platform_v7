<template>
  <div>
    <a-form-model-item label="加减按钮样式" class="item-lh"></a-form-model-item>
    <a-form-model-item>
      <a-radio-group v-model="widget.configuration.stepBtnType" style="width: 100%">
        <a-radio value="LR" style="margin-right: 0">
          <span>左右&nbsp;</span>
          <span class="input-number-LR">
            <div class="minus"><a-icon type="minus" /></div>
            <a-input-number style="width: 60px" disabled />
            <div class="plus"><a-icon type="plus" /></div>
          </span>
        </a-radio>
        <a-radio value="TB" style="margin-right: 0">
          <span>上下&nbsp;</span>
          <span class="input-number-TB">
            <a-input-number style="width: 60px" disabled />
          </span>
        </a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="加减步长">
      <a-input-number v-model="widget.configuration.step" :min="0.1" :precision="1" />
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'numberEditSetting',
  props: {
    widget: Object,
    designer: Object,
    value: String
  },
  data() {
    return {};
  },
  beforeCreate() {},
  components: {},
  watch: {},
  computed: {},
  created() {},
  methods: {},
  mounted() {}
};
</script>
