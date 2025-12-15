<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="formData" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :rules="rules" ref="form">
      <a-form-model-item label="分类名称" prop="name">
        <a-input v-model="formData.name" />
      </a-form-model-item>
      <a-form-model-item label="编号" prop="code">
        <a-input v-model="formData.code" />
      </a-form-model-item>
      <a-form-model-item label="图标">
        <Modal title="选择图标" :zIndex="1000">
          <a-button type="link" size="small">
            <Icon :type="formData.icon || 'plus-square'" />
          </a-button>
          <template slot="content">
            <WidgetIconLib v-model="formData.icon" />
          </template>
        </Modal>
        <ColorPicker v-model="formData.iconColor"></ColorPicker>
      </a-form-model-item>
      <a-form-model-item label="备注" prop="remark">
        <a-textarea v-model="formData.remark" />
      </a-form-model-item>
    </a-form-model>
  </a-skeleton>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import WidgetIconLib from '@pageAssembly/app/web/widget/widget-icon-lib/widget-icon-lib.vue';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
export default {
  components: { Modal, WidgetIconLib, ColorPicker },
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event || {};
    let formData = $event.meta || {};
    let uuid = formData.key;
    if (formData.title == '流程分类') {
      uuid = '';
    }
    return {
      loading: !!uuid,
      uuid,
      formData,
      rules: {
        name: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        code: { required: true, message: '不能为空', trigger: ['blur', 'change'] }
      }
    };
  },
  created() {
    if (this.uuid) {
      this.loadFormData();
    }
  },
  methods: {
    loadFormData() {
      $axios.get(`/api/workflow/category/get?uuid=${this.uuid}`).then(({ data: result }) => {
        if (result.data) {
          this.loading = false;
          this.formData = result.data;
        }
      });
    },
    save() {
      const _this = this;
      _this.$refs.form.validate().then(valid => {
        if (valid) {
          $axios
            .post('/api/workflow/category/save', _this.formData)
            .then(({ data: result }) => {
              if (result.success) {
                _this.$message.success('保存成功！');
                _this.pageContext.emitEvent('KgYNrJvbmgnlUwXzeegVckhzjDjrgVKC:closeModal');
                _this.pageContext.emitEvent('omhnjCXbgLMLFfkVlnSJVXPJuAHPDTSj:refetch');
                _this.pageContext.emitEvent('gAIyNpFRleXGWDnXVmohGLuafyCIiwsM:refetch');
              } else {
                _this.$message.error(result.msg);
              }
            })
            .catch(({ response }) => {
              _this.$message.error(response.data.msg);
            });
        }
      });
    }
  },
  META: {
    method: {
      save: '保存流程分类'
    }
  }
};
</script>

<style></style>
