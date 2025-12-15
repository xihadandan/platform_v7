<template>
  <a-form-model :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules">
    <a-form-model-item label="变量名">
      <a-input :value="codeSuffix()" @change="e => mergeCodeSuffix(e)">
        <span slot="addonBefore">{{ prefix + '-' }}</span>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="值 / 公式">
      <a-input v-model="item.value" />
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="item.remark" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'EditColorClassifyPanel',
  props: {
    item: Object,
    prefix: String
  },
  components: {},
  computed: {},
  data() {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        suffix: [{ required: true, message: '变量名必填', trigger: 'blur' }]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    codeSuffix() {
      return this.item.code ? this.item.code.split(this.prefix + '-')[1] : undefined;
    },
    mergeCodeSuffix(e) {
      this.item.code = this.prefix + '-' + e.target.value;
    }
  }
};
</script>
