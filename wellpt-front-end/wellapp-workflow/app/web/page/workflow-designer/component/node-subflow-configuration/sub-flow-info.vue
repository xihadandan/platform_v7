<template>
  <!-- 子流程属性-办理信息 -->
  <div>
    <!-- 办理进度 start -->
    <div class="divider-title">办理进度</div>
    <a-form-model-item class="form-item-vertical" label="显示位置">
      <form-blocks-select v-model="formData.undertakeSituationPlaceHolder" />
      <w-checkbox v-model="formData.expandList">列表默认展开</w-checkbox>
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="列表标题表达式">
      <title-define-template
        :formData="formData"
        prop="undertakeSituationTitleExpression"
        title="列表标题设置"
        alert="在下方编辑流程标题表达式，可插入流程内置变量、表单字段和文本。"
        @change="arg => changeTitle(arg, 'undertakeSituationTitleExpression')"
      >
        <a-button type="link" size="small" icon="setting" slot="trigger">设置</a-button>
      </title-define-template>
      <div class="title-expression-value" style="line-height: 20px">
        {{ formData.undertakeSituationTitleExpression }}
      </div>
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="按钮配置">
      <branch-buttons-table v-model="formData.undertakeSituationButtons" type="subflow" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="列配置">
      <branch-columns-table :subFormData="formData" v-model="formData.undertakeSituationColumns" type="subflow" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="排序配置">
      <a-radio-group v-model="formData.sortRule" size="small">
        <a-radio v-for="item in sortRuleConfig" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio>
      </a-radio-group>
      <template v-if="formData.sortRule === constDefault">
        <div style="font-size: 12px; color: var(--w-gray-color-7)">先按子流程的配置顺序，再按子流程的办理状态未办结、已办结排序</div>
      </template>
      <template v-else>
        <branch-columns-sort-table v-model="formData.undertakeSituationOrders" :columns="formData.undertakeSituationColumns" />
      </template>
    </a-form-model-item>
    <!-- 办理进度 end -->

    <!-- 信息分发 start -->
    <div class="divider-title">信息分发</div>
    <a-form-model-item class="form-item-vertical" label="显示位置">
      <form-blocks-select v-model="formData.infoDistributionPlaceHolder" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="列表标题表达式">
      <title-define-template
        :formData="formData"
        prop="infoDistributionTitleExpression"
        title="列表标题设置"
        alert="在下方编辑流程标题表达式，可插入流程内置变量、表单字段和文本。"
        @change="arg => changeTitle(arg, 'infoDistributionTitleExpression')"
      >
        <a-button type="link" size="small" icon="setting" slot="trigger">设置</a-button>
      </title-define-template>
      <div class="title-expression-value" style="line-height: 20px">
        {{ formData.infoDistributionTitleExpression }}
      </div>
    </a-form-model-item>
    <!-- 信息分发 end -->

    <!-- 操作记录 start -->
    <div class="divider-title">操作记录</div>
    <a-form-model-item class="form-item-vertical" label="显示位置">
      <form-blocks-select v-model="formData.operationRecordPlaceHolder" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="列表标题表达式">
      <title-define-template
        :formData="formData"
        prop="operationRecordTitleExpression"
        title="列表标题设置"
        alert="在下方编辑流程标题表达式，可插入流程内置变量、表单字段和文本。"
        @change="arg => changeTitle(arg, 'operationRecordTitleExpression')"
      >
        <a-button type="link" size="small" icon="setting" slot="trigger">设置</a-button>
      </title-define-template>
      <div class="title-expression-value" style="line-height: 20px">
        {{ formData.operationRecordTitleExpression }}
      </div>
    </a-form-model-item>
    <!-- 操作记录 end -->
  </div>
</template>

<script>
import { constDefault, constCustom, sortRuleConfig, fixedField, fixedSortField, btnSource, sortSource } from '../designer/constant';
import WCheckbox from '../components/w-checkbox';
import WSelect from '../components/w-select';
import FormBlocksSelect from '../commons/form-blocks-select.vue';
import TitleDefineTemplate from '../commons/title-define-template.vue';
import BranchButtonsTable from '../commons/branch-buttons-table.vue';
import BranchColumnsTable from '../commons/branch-columns-table.vue';
import BranchColumnsSortTable from '../commons/branch-columns-sort-table.vue';

export default {
  name: 'SubflowInfo',
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WCheckbox,
    WSelect,
    FormBlocksSelect,
    TitleDefineTemplate,
    BranchButtonsTable,
    BranchColumnsTable,
    BranchColumnsSortTable
  },
  data() {
    return {
      constDefault,
      constCustom,
      sortRuleConfig,
      fixedField,
      fixedSortField,
      btnSource,
      sortSource,
      flowFields: []
    };
  },
  created() {
    this.fetchSortFields().then(res => {
      res.forEach(item => {
        const curIndex = this.fixedField.findIndex(f => f.id === item.id);
        if (curIndex === -1) {
          this.flowFields.push(item);
        }
      });
    });
  },
  methods: {
    getSortFields() {},
    fetchSortFields() {
      return new Promise((resolve, reject) => {
        this.$axios.get('/api/workflow/work/sortFields').then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              resolve(data);
            }
          }
        });
      });
    },
    // 更改流程标题
    changeTitle(arg, fieldKey) {
      this.formData[fieldKey] = arg.value;
    }
  }
};
</script>
