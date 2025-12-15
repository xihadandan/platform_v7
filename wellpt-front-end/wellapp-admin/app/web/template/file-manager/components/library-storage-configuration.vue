<template>
  <div>
    <a-card>
      存储配额
      <p />
      <div>
        <a-row>
          <a-col :span="12">已使用 0 {{ configuration.storage.limitUnit }}</a-col>
          <a-col :span="12" style="text-align: right">
            <span v-if="configuration.storage.quota === 'unlimited'">总配额 不限制</span>
            <span v-else>总配额 {{ configuration.storage.limitSize }} {{ configuration.storage.limitUnit }}</span>
          </a-col>
        </a-row>
        <a-progress :percent="0" :showInfo="false" />
      </div>
      <p />
      <a-radio-group v-model="configuration.storage.quota" button-style="solid">
        <a-radio-button value="unlimited">不限制</a-radio-button>
        <a-radio-button value="limited">限制</a-radio-button>
      </a-radio-group>
      <span v-if="configuration.storage.quota === 'limited'" style="margin-left: 10px">
        <a-input-number v-model="configuration.storage.limitSize" :min="1"></a-input-number>
        <a-select v-model="configuration.storage.limitUnit" style="width: 70px">
          <a-select-option value="MB">MB</a-select-option>
          <a-select-option value="GB">GB</a-select-option>
        </a-select>
      </span>
    </a-card>
    <p />
    <a-card>
      单文件大小限制
      <p />
      <a-radio-group v-model="configuration.storage.fileQuota" button-style="solid">
        <a-radio-button value="unlimited">不限制</a-radio-button>
        <a-radio-button value="limited">限制</a-radio-button>
      </a-radio-group>
      <span v-if="configuration.storage.fileQuota === 'limited'" style="margin-left: 10px">
        <a-input-number v-model="configuration.storage.fileSizeLimit" :min="1"></a-input-number>
        <a-select v-model="configuration.storage.fileSizeLimitUnit" style="width: 70px">
          <a-select-option value="KB">KB</a-select-option>
          <a-select-option value="MB">MB</a-select-option>
          <a-select-option value="GB">GB</a-select-option>
        </a-select>
        超出此大小的文件将无法上传
      </span>
    </a-card>
    <p />
    <a-card>
      回收站文档保留时长
      <p />
      <a-radio-group v-model="configuration.storage.recycleBinRetention" button-style="solid">
        <a-radio-button value="unlimited">不限制</a-radio-button>
        <a-radio-button value="limited">限制</a-radio-button>
      </a-radio-group>
      <span v-if="configuration.storage.recycleBinRetention === 'limited'" style="margin-left: 10px">
        <a-input-number v-model="configuration.storage.recycleBinRetentionTimeLimit" :min="1"></a-input-number>
        <a-select v-model="configuration.storage.recycleBinRetentionTimeUnit" style="width: 70px">
          <a-select-option value="day">天</a-select-option>
          <a-select-option value="month">月</a-select-option>
          <a-select-option value="year">年</a-select-option>
        </a-select>
        超出此时限的已删除文档将被彻底删除，无法恢复
      </span>
    </a-card>
  </div>
</template>

<script>
export default {
  props: {
    configuration: Object
  },
  data() {
    if (!this.configuration.storage) {
      this.$set(this.configuration, 'storage', {
        quota: 'unlimited',
        limitSize: 50,
        limitUnit: 'GB',
        fileQuota: 'unlimited',
        fileSizeLimit: 100,
        fileSizeLimitUnit: 'MB',
        recycleBinRetention: 'unlimited',
        recycleBinRetentionTimeLimit: 30,
        recycleBinRetentionTimeUnit: 'day'
      });
    }
    return {};
  }
};
</script>

<style lang="less" scoped>
::v-deep .ant-card-body {
  background-color: #efefef;
}
::v-deep .ant-progress-inner {
  background-color: #cecece;
}
</style>
