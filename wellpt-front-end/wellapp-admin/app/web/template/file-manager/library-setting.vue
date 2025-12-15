<template>
  <a-form-model
    :model="formData"
    class="library-setting pt-form"
    :label-col="{ span: 6 }"
    labelAlign="left"
    :wrapper-col="{ span: 18 }"
    :colon="false"
  >
    <div class="content-item">
      <div class="sub-title pt-title-vertical-line">消息格式</div>
      <a-form-model-item label="文档创建通知">
        <a-select
          v-model="formData.messageTemplate.fileCreated"
          show-search
          style="width: 310px"
          :options="messageTemplateOptions"
          :filter-option="filterSelectOption"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item label="文档更新通知">
        <a-select
          v-model="formData.messageTemplate.fileUpdated"
          show-search
          style="width: 310px"
          :options="messageTemplateOptions"
          :filter-option="filterSelectOption"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item label="文档删除通知">
        <a-select
          v-model="formData.messageTemplate.fileDeleted"
          show-search
          style="width: 310px"
          :options="messageTemplateOptions"
          :filter-option="filterSelectOption"
        ></a-select>
      </a-form-model-item>
    </div>
    <div class="content-item">
      <div class="sub-title pt-title-vertical-line">个人库设置</div>
      <a-form-model-item label="个人库模板">
        <a-select
          v-model="formData.personalDocument.libraryTemplateId"
          show-search
          style="width: 310px"
          :options="libraryTemplateOptions"
          :filter-option="filterSelectOption"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item label="个人库存储配额调整">
        <a-radio-group v-model="formData.personalDocument.storageQuota" button-style="solid">
          <a-radio-button value="unlimited">不限制</a-radio-button>
          <a-radio-button value="limited">限制</a-radio-button>
        </a-radio-group>
        <span v-if="formData.personalDocument.storageQuota === 'limited'" style="margin-left: 10px">
          <a-input-number v-model="formData.personalDocument.storageLimitSize" :min="1"></a-input-number>
          <a-select v-model="formData.personalDocument.storageLimitUnit" style="width: 70px">
            <a-select-option value="MB">MB</a-select-option>
            <a-select-option value="GB">GB</a-select-option>
          </a-select>
        </span>
      </a-form-model-item>
      <a-form-model-item label="删除用户时自动删除个人库">
        <a-switch v-model="formData.personalDocument.autoDeleteAfterUserDeleted"></a-switch>
      </a-form-model-item>
      <a-form-model-item label="删除的个人库保留时长">
        <a-input-number v-model="formData.personalDocument.retainDays" :min="1"></a-input-number>
        天
      </a-form-model-item>
    </div>
    <div class="btn-container">
      <a-button type="primary" @click="saveLibrarySetting">保存</a-button>
    </div>
  </a-form-model>
</template>

<script>
import { filterSelectOption } from '@framework/vue/utils/function';

export default {
  data() {
    return {
      setting: {},
      formData: {
        messageTemplate: {
          fileCreated: '',
          fileUpdated: '',
          fileDeleted: ''
        },
        personalDocument: {
          libraryTemplateId: '',
          storageQuota: 'unlimited', // unlimited、limited
          storageLimitSize: 5,
          storageLimitUnit: 'GB',
          autoDeleteAfterUserDeleted: true,
          retainDays: 30
        }
      },
      messageTemplateOptions: [],
      libraryTemplateOptions: []
    };
  },
  beforeMount() {
    if (!this.designMode) {
      this.loadLibrarySetting();
      this.loadLibraryTemplateOptions();
      this.loadMessageTemplateOptions();
    }
  },
  methods: {
    filterSelectOption,
    loadLibrarySetting() {
      $axios.get('/proxy/api/dms/library/setting/get').then(({ data: result }) => {
        if (result.data && result.data.definitionJson) {
          this.setting = result.data;
          this.formData = JSON.parse(result.data.definitionJson);
        }
      });
    },
    loadLibraryTemplateOptions() {},
    loadMessageTemplateOptions() {
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'messageTemplateApiFacade',
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data: result }) => {
          if (result.results) {
            this.messageTemplateOptions = result.results.map(item => ({ value: item.id, label: item.text }));
          }
        });
    },
    saveLibrarySetting() {
      this.setting.definitionJson = JSON.stringify(this.formData);
      $axios
        .post('/proxy/api/dms/library/setting/save', this.setting)
        .then(({ data: result }) => {
          if (result.data) {
            this.loadLibrarySetting();
            this.$message.success('保存成功');
          } else {
            this.$message.error('保存失败');
          }
        })
        .catch(error => {
          this.$message.error('保存失败');
        });
    }
  }
};
</script>

<style lang="less" scoped>
.library-setting {
  .content-item {
    .sub-title {
      padding-left: 8px;
      font-size: var(--w-font-size-base);
      color: var(--w-text-color-darker);
      line-height: 32px;
      margin-bottom: 12px;
    }

    ::v-deep .ant-form-item-no-colon {
      margin-left: 8px;
    }
    .item-label {
      margin: 12px 8px;
      font-size: var(--w-font-size-sm);
      color: var(--w-text-color-light);
    }
    .item-tip {
      font-size: var(--w-font-size-sm);
      color: var(--w-text-color-light);
      padding: 4px 0;
      line-height: 20px;
    }
    span.item-tip {
      padding-left: 12px;
    }
  }

  .btn-container {
    position: fixed;
    border-top: 1px solid var(--w-border-color-light);
    width: 100%;
    height: 52px;
    line-height: 52px;
    left: 5%;
    bottom: 0;
    text-align: center;
    z-index: 1;
    background-color: var(--w-bg-color-body);
  }
}
</style>
