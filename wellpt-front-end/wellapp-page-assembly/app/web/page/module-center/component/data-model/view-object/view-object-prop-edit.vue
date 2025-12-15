<template>
  <a-form-model :model="form" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :rules="rules" ref="form">
    <a-form-model-item label="源属性" prop="location">
      <a-input v-model="form.location" />
      <a-select v-model="form.location" style="width: 100%">
        <!-- sourcePropOptions -->
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="显示名称" prop="title">
      <a-input v-model="form.id" v-if="form.uuid == undefined" />
      <span v-else>{{ form.id }}</span>
    </a-form-model-item>
    <a-form-model-item label="编码">
      <a-input v-model="form.alias" />
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="form.remark" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ViewObjectPropEdit',
  inject: ['getViewObjectDesign'],
  props: {
    detail: Object
  },
  components: {},
  computed: {},
  data() {
    let form = {};
    if (this.detail != undefined) {
      form = { ...this.detail };
    }
    return { form, rules: {}, sourcePropOptions: [] };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    let design = this.getViewObjectDesign();
    if (design != undefined) {
      let columns = design.rootColumns;
    }
  },
  mounted() {},
  methods: {}
};
</script>
