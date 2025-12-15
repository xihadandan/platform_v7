<template>
  <a-form-model ref="form" :model="formData" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
    <a-form-model-item :label="label" prop="name">
      <a-input v-model="formData.name" :maxLength="200" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { trim } from 'lodash';
export default {
  props: {
    label: {
      type: String,
      default: '夹名称'
    },
    initName: String,
    parentFolderUuid: String,
    file: Object
  },
  data() {
    const _this = this;
    let checkTheSameName = (rule, value, callback) => {
      if (_this.parentFolderUuid) {
        $axios
          .post('/json/data/services', {
            serviceName: 'dmsFileManagerService',
            methodName: 'checkTheSameNameForCreateFolder',
            args: JSON.stringify([trim(value), _this.parentFolderUuid])
          })
          .then(({ data: result }) => {
            if (result.data) {
              callback(new Error(_this.$t('WidgetFileManager.message.repeatFolderName', '重复的文件夹名称，请输入名称！')));
            } else {
              callback();
            }
          })
          .catch(() => {
            callback();
          });
      } else if (_this.file && _this.file.contentType == 'application/folder') {
        $axios
          .post('/json/data/services', {
            serviceName: 'dmsFileManagerService',
            methodName: 'existsTheSameFolderNameWidthFolderUuid',
            args: JSON.stringify([trim(value), _this.file.uuid])
          })
          .then(({ data: result }) => {
            if (result.data) {
              let errorTip = _this.$t('WidgetFileManager.message.repeatFolderName', '重复的文件夹名称，请输入名称！');
              callback(new Error(errorTip));
            } else {
              callback();
            }
          })
          .catch(() => {
            callback();
          });
      } else {
        callback();
      }
    };
    return {
      labelCol: { span: 5 },
      wrapperCol: { span: 18 },
      formData: {
        name: _this.initName || ''
      },
      rules: {
        name: [
          { required: true, message: this.$t('WidgetFormInput.validateMessage.required', '必填'), trigger: 'change' },
          { validator: checkTheSameName, trigger: 'change' }
        ]
      }
    };
  },
  methods: {
    collect() {
      return this.$refs.form.validate().then(() => {
        return {
          name: trim(this.formData.name)
        };
      });
    }
  }
};
</script>

<style></style>
