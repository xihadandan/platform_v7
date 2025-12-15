<template>
  <!-- 环节属性-流转设置-分支模式 -->
  <div>
    <a-form-model-item label="跟进人员" class="form-item-vertical">
      <user-select-list
        v-model="formData.branchTaskMonitors"
        types="unit/bizOrg/field/task/custom/filter"
        title="选择跟进人员"
        text="跟进人员"
      />
    </a-form-model-item>
    <div class="divider-title">办理进度</div>
    <a-form-model-item class="form-item-vertical" label="显示位置">
      <form-blocks-select v-model="formData.undertakeSituationPlaceHolder" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="列表标题表达式">
      <title-define-template
        :formData="formData"
        prop="undertakeSituationTitleExpression"
        title="列表标题设置"
        alert="在下方编辑列表标题表达式，可插入流程内置变量、表单字段和文本。"
      >
        <a-button type="link" size="small" slot="trigger">
          <Icon type="pticon iconfont icon-ptkj-shezhi" />
          <span>设置</span>
        </a-button>
      </title-define-template>
      <div class="title-expression-value" style="line-height: 20px">
        {{ formData.undertakeSituationTitleExpression }}
      </div>
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="按钮配置">
      <branch-buttons-table v-model="formData.undertakeSituationButtons" type="task" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="列配置">
      <branch-columns-table v-model="formData.undertakeSituationColumns" />
    </a-form-model-item>
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
      >
        <a-button type="link" size="small" slot="trigger">
          <Icon type="pticon iconfont icon-ptkj-shezhi" />
          <span>设置</span>
        </a-button>
      </title-define-template>
      <div class="title-expression-value" style="line-height: 20px">
        {{ formData.infoDistributionTitleExpression }}
      </div>
    </a-form-model-item>
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
      >
        <a-button type="link" size="small" slot="trigger">
          <Icon type="pticon iconfont icon-ptkj-shezhi" />
          <span>设置</span>
        </a-button>
      </title-define-template>
      <div class="title-expression-value" style="line-height: 20px">
        {{ formData.operationRecordTitleExpression }}
      </div>
    </a-form-model-item>
  </div>
</template>

<script>
import UserSelectList from '../commons/user-select-list.vue';
import FormBlocksSelect from '../commons/form-blocks-select.vue';
import TitleDefineTemplate from '../commons/title-define-template.vue';
import BranchButtonsTable from '../commons/branch-buttons-table.vue';
import BranchColumnsTable from '../commons/branch-columns-table.vue';
export default {
  name: 'BranchMode',
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    UserSelectList,
    FormBlocksSelect,
    TitleDefineTemplate,
    BranchButtonsTable,
    BranchColumnsTable
  },
  data() {
    return {};
  },
  computed: {
    orgVersionId() {
      return this.formData.orgVersionId || '';
    }
  },
  created() {},
  methods: {}
};
</script>
