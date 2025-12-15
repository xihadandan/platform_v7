<template>
  <div>
    请输入新流程的名称和ID：
    <a-form-model ref="form" :model="formData" :rules="rules" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }">
      <a-form-model-item label="流程名称" prop="flowDefName">
        <a-input v-model="formData.flowDefName" />
      </a-form-model-item>
      <a-form-model-item label="流程ID" prop="flowDefId">
        <a-input v-model="formData.flowDefId" />
      </a-form-model-item>
    </a-form-model>
  </div>
</template>

<script>
export default {
  props: {
    flowDefinition: Object
  },
  data() {
    return {
      formData: { flowDefName: this.flowDefinition.name + ' - 副本', flowDefId: this.flowDefinition.id },
      rules: {
        flowDefName: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        flowDefId: [
          { required: true, message: '不能为空', trigger: ['blur', 'change'] },
          { pattern: /^[a-zA-Z_0-9]+$/, message: '只能包含英文大小写字母、整数和下划线', trigger: ['blur', 'change'] }
        ]
      }
    };
  },
  methods: {
    collect() {
      return this.$refs.form
        .validate()
        .then(valid => {
          if (valid) {
            return this.formData;
          }
        })
        .catch(() => {});
    }
  }
};
</script>

<style></style>
