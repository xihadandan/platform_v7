<template>
  <a-form-model
    class="basic-info"
    :model="formConfig"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="formConfig.name" />
    </a-form-model-item>
    <slot name="processFormInfo">
      <ProcessFormInfo ref="processFormInfo" :formConfig="formConfig" :closeOpenDrawer="false"></ProcessFormInfo>
    </slot>
  </a-form-model>
</template>

<script>
import ProcessFormInfo from './process-form-info.vue';
export default {
  inject: ['designer'],
  props: {
    templateUuid: String,
    templateType: {
      type: String,
      default: '10'
    },
    formData: {
      type: Object,
      default() {
        return {
          name: '',
          processDefUuid: this.designer.processDefinition.uuid,
          type: this.templateType,
          definitionJson: ''
        };
      }
    },
    formConfig: {
      type: Object,
      default() {
        return {
          name: '',
          formUuid: '',
          entityNameField: '',
          entityIdField: '',
          processNodePlaceHolder: ''
        };
      }
    }
  },
  components: { ProcessFormInfo },
  data() {
    if (this.formData.name) {
      this.formConfig.name = formData.name;
    }
    return {
      rules: {
        name: [{ required: true, message: '模板名称不能为空！', trigger: 'blur' }]
      }
    };
  },
  mounted() {
    this.$nextTick(() => {
      if (this.templateUuid) {
        $axios.get(`/proxy/api/biz/definition/template/get/${this.templateUuid}`).then(({ data }) => {
          if (data.data && data.data.definitionJson) {
            this.formData = Object.assign(this.formData, data.data);
            // this.formConfig = Object.assign(this.formConfig, JSON.parse(data.data.definitionJson));
            let definitionJson = JSON.parse(data.data.definitionJson);
            for (let key in definitionJson) {
              this.$set(this.formConfig, key, definitionJson[key]);
            }
            this.formConfig.name = this.formData.name;
            if (this.$refs.processFormInfo) {
              this.$refs.processFormInfo.handleFormChange(this.formConfig.formUuid);
            }
          }
        });
      }
    });
  },
  methods: {
    save(successCallback) {
      this.$refs.basicForm.validate().then(valid => {
        if (valid) {
          this.formData.name = this.formConfig.name;
          this.formData.definitionJson = JSON.stringify(this.formConfig);
          $axios.post('/proxy/api/biz/definition/template/save', this.formData).then(result => {
            successCallback.call(this, result);
          });
        }
      });
    }
  }
};
</script>

<style></style>
