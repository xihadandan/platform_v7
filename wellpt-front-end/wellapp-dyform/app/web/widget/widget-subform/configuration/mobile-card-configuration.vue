<template>
  <a-form-model>
    <a-form-model-item label="标题列">
      <a-select
        v-model="widget.configuration.uniConfiguration.cardDisplay.titleColumn"
        :options="fieldOptions"
        :style="{ width: '100%' }"
        allow-clear
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="扩展展示信息列">
      <a-select
        mode="multiple"
        v-model="widget.configuration.uniConfiguration.cardDisplay.extendInfoColumn"
        :options="fieldOptions"
        :style="{ width: '100%' }"
        allow-clear
      ></a-select>
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'MobileCardConfiguration',
  props: {
    widget: Object,
    designer: Object,
    fieldOptions: Array
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
