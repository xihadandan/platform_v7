<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form"
    class="message-detail-form">
    <a-form-model-item label="收件人" prop="recipient">
      <template v-if="readOnly">
        <a-input :disabled="readOnly" v-model="form.recipientName"></a-input>
      </template>
      <OrgSelect v-else v-model="form.recipient" :disable="readOnly" @change="onOrgSelectChange" />
    </a-form-model-item>
    <a-form-model-item label="主题" prop="subject">
      <a-input v-model="form.subject" allow-clear :disabled="readOnly"
        style="width: calc(100% - 98px); margin-right: 10px" />
      <span style="color: var(--w-form-item-label-color);padding-right: 8px;">重要: </span><a-switch v-model="markFlag"
        :disabled="readOnly" />
    </a-form-model-item>
    <a-form-model-item label="内容" prop="body">
      <QuillEditor v-model="form.body" ref="quillEditor" @change="bodyChange" @blur="bodyChange" :hiddenButtons="[]"
        :disable="readOnly" />
    </a-form-model-item>
    <a-form-model-item label="附件" v-show="!readOnly || attach">
      <UploadSimpleComponent :fileIds="attach" @change="attendChange" ref="fjRef" :uploadBtn="{
        type: 'link',
        hasIcon: true,
        title: '上传附件'
      }" :editable="!readOnly" :separator=/[,;]/g></UploadSimpleComponent>
    </a-form-model-item>
    <a-form-model-item label=" " :colon="false" v-if="!options.relatedBtnInButtons">
      <div style="text-align: right">
        <a-button type="link" @click="relatedHandle">{{ relatedTitle }}</a-button>
      </div>
    </a-form-model-item>
  </a-form-model>
</template>
<script type="text/babel">
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import QuillEditor from '@pageAssembly/app/web/lib/quill-editor';
import UploadSimpleComponent from '../common/upload-simple-component';
import { deepClone } from '@framework/vue/utils/util';
export default {
  name: 'MessageDetail',
  props: {
    data: {
      // 消息内容
      type: Object,
      defalut: () => {
        return {};
      }
    },
    options: {
      // 相关参数
      type: Object,
      defalut: () => {
        return {};
      }
    }
  },
  components: {
    OrgSelect,
    QuillEditor,
    UploadSimpleComponent
  },
  data() {
    return {
      form: {},
      labelCol: { span: 3 },
      wrapperCol: { span: 20 },
      rules: {
        recipient: { required: true, message: '收件人必填', trigger: ['blur', 'change'] },
        subject: { required: true, message: '主题必填', trigger: ['blur', 'change'] },
        body: { required: true, message: '消息内容必填', trigger: ['blur', 'change'] }
      },
      markFlag: false,
      attach: ''
    };
  },
  computed: {
    messageParm() {
      let messageParm = this.form.MESSAGE_PARM || this.form.messageParm;
      return messageParm ? JSON.parse(messageParm) : undefined;
    },
    readOnly() {
      return this.options && this.options.readOnly;
    },
    relatedTitle() {
      if (this.messageParm) {
        return this.messageParm.relatedTitle;
      } else {
        return this.form.relatedTitle || this.form.RELATED_TITLE || '';
      }
    },
    relatedUrl() {
      let url = '';
      if (this.messageParm) {
        url = this.messageParm.relatedUrl;
      } else {
        url = this.form.relatedUrl || this.form.RELATED_URL || '';
      }
      if (url && url.indexOf('http') != 0 && this._$SYSTEM_ID) {
        url = '/sys/' + this._$SYSTEM_ID + '/_' + url;
      }
      return url;
    },
    relatedShow() {
      return this.relatedUrl && this.relatedTitle;
    }
  },
  mounted() {
    this.initData();
  },
  methods: {
    // 消息数据初始化
    initData() {
      this.form = deepClone(this.data);
      if (this.form.body) {
        this.$refs.quillEditor.setHtml(this.form.body);
      } else {
        this.$refs.quillEditor.setHtml('');
      }
      this.markFlag = this.form.markFlag == '1' || this.form.MARK_FLAG == '1';
      if (this.messageParm) {
        this.attach = this.messageParm.attach;
      } else if (this.form.attach) {
        this.attach = this.form.attach;
      }

      if (this.form.messageOutboxUuid == undefined) {
        // 发件箱获取附件
        this.fetchMessageAttach(this.form.uuid, 'messageAttach');
      }

      if (this.attach) {
        this.$refs.fjRef.setValue(this.attach);
      }
    },
    fetchMessageAttach(folderID, purpose) {
      this.$axios.get(`/proxy-repository/repository/file/mongo/getNonioFilesFromFolder`, {
        params: {
          folderID, purpose
        }
      }).then(({ data }) => {
         if (data && data.data) {
          let attach = [];
          for (let a of data.data) {
            attach.push(a.fileID);
          }
          this.attach = attach.join(',');
          this.$refs.fjRef.setValue(this.attach);

        }
      }).catch(error => { })
    },
    bodyChange(value) {
      this.$set(this.form, 'body', value || this.$refs.quillEditor.getHtml());
    },
    attendChange(fileList) {
      this.attach = _.map(fileList, item => {
        return item.dbFile.fileID;
      });
    },
    onOrgSelectChange({ value, label, nodes }) {
      this.form.recipientName = label;
    },
    relatedHandle() {
      window.open(this.relatedUrl);
    },
    collectForm(callback) {
      this.bodyChange();
      this.$refs.form.validate(valid => {
        if (valid) {
          this.beforeSaveReq(callback);
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    beforeSaveReq(callback) {
      let bean = {
        body: this.form.body.replace('%', '%'),
        markflag: this.markFlag ? '1' : '0',
        messageAttach: (typeof this.attach == 'string' && this.attach ? [...this.attach.split(/,|;/)] : this.attach) || [],
        showUser: this.form.recipientName,
        subject: this.form.subject.replace('%', '%'),
        type: ['ON_LINE'],
        userId: this.form.recipient
      };
      console.log(bean);
      if (typeof callback == 'function') {
        callback(bean);
      }
    }
  }
};
</script>
<style lang="less" scoped>
.message-detail-form {
  --w-form-item-label-color: black;

  .ant-form-item {
    margin-bottom: 20px;

    .org-select-component {
      vertical-align: middle;
      display: inline-block;
      width: 100%;
    }

    .simpleUpload_component {
      margin-top: 4px;
      --w-upload-list-padding: 8px;

      ::v-deep .widget-file-upload-list .file-list-item {
        padding: 4px 8px;

      }
    }
  }
}
</style>
