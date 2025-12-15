<template>
  <a-form-model :model="formData" :label-col="{ span: 4 }" :wrapper-col="{ span: 16 }" :rules="rules" ref="form">
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="formData.name" />
    </a-form-model-item>
    <a-form-model-item label="排序号" prop="sortOrder">
      <a-input-number v-model="formData.sortOrder" style="width: 100%" />
    </a-form-model-item>
    <a-form-model-item label="描述" prop="remark">
      <a-textarea v-model="formData.remark" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
export default {
  props: {
    formData: Object
  },
  data() {
    return {
      rules: {
        name: [{ required: true, message: '名称必填', trigger: 'blur' }]
      }
    };
  },
  methods: {
    validate() {
      return this.$refs.form.validate();
    }
  }
};
</script>

<style></style>
