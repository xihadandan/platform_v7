<template>
  <div :style="{ height: opinionFileSetting.enabled ? '280px' : '140px', overflow: 'auto' }">
    <a-form-model ref="form" :model="formData" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol" :colon="false">
      <a-form-model-item :label="label" prop="opinionText" style="margin-bottom: 0">
        <a-input v-model="formData.opinionText" type="textarea" rows="5" :maxLength="1000" />
      </a-form-model-item>
      <a-form-model-item v-if="opinionFileSetting.enabled" style="margin-bottom: 0">
        <template slot="label">&nbsp;</template>
        <WorkflowOpinionFile
          class="opinion-file"
          :workView="workView"
          :fileList="fileList"
          :opinionFileSetting="opinionFileSetting"
          :remoteDelete="false"
          @change="onFileChange"
          :listStyle="{ maxHeight: '122px', height: '122px' }"
        ></WorkflowOpinionFile>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>

<script>
import { trim } from 'lodash';
import WorkflowOpinionFile from '@workflow/app/web/page/workflow-work/component/workflow-opinion-file.vue';
export default {
  props: {
    label: {
      type: String,
      default: '签署意见'
    },
    required: {
      type: Boolean,
      default: false
    }
  },
  components: { WorkflowOpinionFile },
  computed: {
    workView() {
      return {
        $widget: this,
        loading: {}
      };
    }
  },
  data() {
    let labelCol = { span: 3, style: { '--w-form-item-label-color': 'var(--w-text-color-darker)' } };
    let wrapperCol = { span: 21 };
    if (this.$i18n && this.$i18n.locale !== 'zh_CN') {
      labelCol = { span: 6, style: { '--w-form-item-label-color': 'var(--w-text-color-darker)' } };
      wrapperCol = { span: 18 };
    }
    return {
      labelCol,
      wrapperCol,
      formData: {
        opinionText: '',
        opinionFiles: []
      },
      fileList: [],
      rules: { opinionText: [{ required: this.required, message: `${this.label}必填`, trigger: 'change' }] },
      opinionFileSetting: {
        enabled: false
      }
    };
  },
  created() {
    $axios.get('/proxy/api/workflow/setting/getByKey?key=OPINION_FILE').then(({ data: result }) => {
      if (result.data && result.data.attrVal) {
        this.opinionFileSetting = JSON.parse(result.data.attrVal);
      }
    });
  },
  methods: {
    onFileChange(fileList) {
      this.fileList = fileList;
      this.formData.opinionFiles = fileList
        .filter(file => file.status == 'done')
        .map(file => ({
          uuid: file.fileID,
          name: file.name,
          fileName: file.name,
          fileID: file.fileID,
          status: file.status
        }));
    },
    collectOption() {
      return this.$refs.form.validate().then(() => {
        return {
          label: '',
          value: '',
          text: trim(this.formData.opinionText),
          files: this.formData.opinionFiles
        };
      });
    }
  }
};
</script>

<style></style>
