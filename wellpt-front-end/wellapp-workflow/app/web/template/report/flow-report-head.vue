<template>
  <div class="flow-report-head">
    <div class="flow-report-head-top">
      <div class="_title">{{ title }}</div>
      <a-button type="link" @click="toggleShowFilter">
        <Icon type="iconfont icon-ptkj-shaixuan"></Icon>
        <span>{{ showFilter ? '收起' : '筛选' }}</span>
      </a-button>
    </div>
    <template v-if="showFilter">
      <div class="flow-report-head-filter">
        <slot name="filter">
          <a-form-model :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }" :model="formData" :colon="false">
            <a-row>
              <a-col :span="12">
                <a-form-model-item label="发起人范围" prop="startUserRange">
                  <OrgSelect v-model="formData.startUserRange" />
                </a-form-model-item>
              </a-col>
              <a-col :span="12">
                <a-form-model-item label="发起时间范围" prop="startTimeRange">
                  <a-range-picker :format="defaultPattern" :valueFormat="defaultPattern" v-model="formData.startTimeRange" />
                </a-form-model-item>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span="12">
                <a-form-model-item label="流程范围" prop="flowRange">
                  <FlowSelect v-model="formData.flowRange" style="width: 100%"></FlowSelect>
                </a-form-model-item>
              </a-col>
              <a-col :span="12">
                <a-form-model-item label="包含已停用流程数据" prop="includeDisabledFlow">
                  <a-switch v-model="formData.includeDisabledFlow" />
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-form-model>
        </slot>
        <a-space class="flow-report-head-handle">
          <a-button type="primary" @click="onSearchClick">查询</a-button>
          <a-button type="default" @click="onResetClick">重置</a-button>
        </a-space>
      </div>
    </template>
  </div>
</template>

<script>
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';

export default {
  name: 'FlowReportHead',
  props: {
    title: {
      type: String,
      default: ''
    },
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    OrgSelect,
    FlowSelect
  },
  data() {
    return {
      showFilter: false,
      defaultPattern: 'yyyy-MM-DD'
    };
  },
  methods: {
    onSearchClick() {
      this.$emit('onSearchClick');
    },
    onResetClick() {
      this.$emit('onResetClick');
    },
    toggleShowFilter() {
      this.showFilter = !this.showFilter;
      this.$emit('toggleShowFilter', this.showFilter);
    }
  }
};
</script>

<style lang="less">
.flow-report-head {
  min-height: 46px;
  padding: 16px 20px;
  border-radius: 2px;
  margin-bottom: 12px;
  background-color: var(--w-color-white);
  .flow-report-head-top {
    display: flex;
    justify-content: space-between;
    align-items: center;

    ._title {
      font-weight: bold;
      color: var(--w-text-color-dark);
    }
  }
  .flow-report-head-filter {
    position: relative;
    .flow-report-head-handle {
      position: absolute;
      right: 0;
      bottom: 0;
    }
  }
}
</style>
