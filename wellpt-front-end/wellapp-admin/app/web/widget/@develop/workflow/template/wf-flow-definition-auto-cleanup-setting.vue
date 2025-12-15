<template>
  <a-form-model ref="form" :model="formData" :rules="rules" :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }" :colon="false">
    <a-form-model-item prop="enabled">
      <template slot="label">
        <a-space>
          定时清除
          <a-popover>
            <template slot="content">开启后系统将自动清除已删除一段时间的流程（已有流程实例的流程无法自动清除）</template>
            <a-icon type="info-circle"></a-icon>
          </a-popover>
        </a-space>
      </template>
      <a-switch v-model="formData.enabled" checked-children="开启" un-checked-children="关闭" />
    </a-form-model-item>
    <a-form-model-item v-show="formData.enabled" label="自动清除周期" prop="retentionDays">
      系统自动清除
      <a-input-number v-model="formData.retentionDays" />
      天前删除的流程定义。
    </a-form-model-item>
  </a-form-model>
</template>

<script>
export default {
  data() {
    return {
      formData: { enabled: false, retentionDays: 90 },
      rules: {
        retentionDays: { required: true, message: '不能为空', trigger: ['blur', 'change'] }
      }
    };
  },
  created() {
    $axios.post('/proxy/api/workflow/definition/cleanup/config/get').then(({ data: result }) => {
      if (result.success) {
        this.formData = result.data;
      }
    });
  },
  methods: {
    save() {
      return this.$refs.form.validate().then(valid => {
        if (valid) {
          return $axios.post('/proxy/api/workflow/definition/cleanup/config/save', this.formData).then(({ data: result }) => {
            this.$message.success('保存成功！');
            return result.success;
          });
        }
      });
    }
  }
};
</script>

<style></style>
