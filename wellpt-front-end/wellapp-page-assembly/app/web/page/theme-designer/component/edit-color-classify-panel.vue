<template>
  <a-form-model :model="classify" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules">
    <a-form-model-item label="名称" prop="title">
      <a-input v-model="classify.title" />
    </a-form-model-item>
    <a-form-model-item label="变量前缀" prop="suffix">
      <a-input v-if="isNew" v-model="classify.suffix" @change="e => mergeCodeSuffix(e)">
        <span slot="addonBefore">{{ commonPrefix }}</span>
      </a-input>
      <span v-else>{{ classify.code }}</span>
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="classify.remark" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'EditColorClassifyPanel',
  props: {
    classify: Object
  },
  components: {},
  computed: {},
  data() {
    let commonPrefix = '--w-';
    if (this.classify.code != undefined) {
      this.classify.suffix = this.classify.code.split(commonPrefix)[1];
    }
    return {
      commonPrefix,
      isNew: this.classify.code == undefined,
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        title: [{ required: true, message: '名称必填', trigger: 'blur' }],
        suffix: [{ required: true, message: '变量前缀必填', trigger: 'blur' }]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    mergeCodeSuffix(e) {
      this.classify.code = this.commonPrefix + e.target.value;
    }
  }
};
</script>
