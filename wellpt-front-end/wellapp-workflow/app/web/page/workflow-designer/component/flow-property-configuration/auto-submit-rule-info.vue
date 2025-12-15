<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    :colon="false"
    labelAlign="left"
    :label-col="{ flex: '140px' }"
    :wrapper-col="{ flex: 'auto' }"
    class="form-model-flex"
  >
    <a-form-model-item label="审批去重模式">
      <a-radio-group v-model="formData.mode" size="small" button-style="solid" @change="changeMode">
        <a-radio-button v-for="item in modeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
      </a-radio-group>
      <a-tooltip placement="topRight" :arrowPointAtCenter="true">
        <template slot="title">{{ modeMaps[formData.mode]['tips'] }}</template>
        <a-icon type="exclamation-circle" style="margin-left: 7px" />
      </a-tooltip>
    </a-form-model-item>
    <a-form-model-item label="规则生效环节">
      <a-radio-group v-model="formData.effectiveTask" size="small" button-style="solid" @change="changeEffectiveTask">
        <a-radio-button v-for="item in effectiveTaskOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item v-if="formData.effectiveTask !== effectiveTaskOptions[0]['value']" prop="taskIds">
      <div slot="label"></div>
      <a-select v-model="formData.taskIds" mode="multiple" optionFilterProp="title" :options="taskOptions" />
    </a-form-model-item>
    <a-form-model-item label="规则生效人员">
      <a-radio-group v-model="formData.effectiveUser" size="small" button-style="solid" @change="changeEffectiveUser">
        <a-radio-button v-for="item in effectiveUserOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item v-if="formData.effectiveUser !== effectiveUserOptions[0]['value']" prop="users">
      <div slot="label"></div>
      <org-select-single-modal v-model="formData.users" />
    </a-form-model-item>
    <a-form-model-item>
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">当用户在以下节点作为操作用户时计入审批次数，多次即为重复</div>
          <label>
            重复审批人员判定
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>
      <template v-if="formData.mode === modeOptions[0]['value']">
        <!-- <a-checkbox-group v-model="formData.matchTypes" :options="matchTypesOptions.slice(0, 3)" class="checkbox-group-block" /> -->
        <a-checkbox-group v-model="formData.matchTypes" class="exit-conditions-group">
          <div
            v-for="(item, index) in beforeMatchTypesOptions"
            :key="index"
            :class="(index + 1) % 2 === 0 ? 'exit-condition-single-user' : ''"
          >
            <a-checkbox
              v-show="(index + 1) % 2 === 0 && formData.matchTypes.includes(beforeMatchTypesOptions[index - 1]['value'])"
              :key="item.value"
              :value="item.value"
            >
              {{ item.label }}
            </a-checkbox>
            <a-checkbox v-show="(index + 1) % 2 !== 0" :key="item.value" :value="item.value">{{ item.label }}</a-checkbox>
          </div>
        </a-checkbox-group>
      </template>
      <template v-else>
        <a-checkbox-group v-model="formData.matchTypes" :options="matchTypesOptions.slice(1, 3)" class="checkbox-group-block" />
      </template>
    </a-form-model-item>
    <a-form-model-item label="处理方式">
      <template v-if="formData.mode === modeOptions[0]['value']">
        <a-radio-group v-model="formData.handleMode" size="small" button-style="solid" @change="changeHandleMode">
          <a-radio-button v-for="item in handleModeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
        </a-radio-group>
      </template>
      <template v-else>
        {{ handleModeOptions[1]['label'] }}
      </template>
      <a-tooltip placement="topRight" :arrowPointAtCenter="true">
        <template slot="title">{{ handleModeMaps[formData.handleMode]['tips'] }}</template>
        <a-icon type="exclamation-circle" style="margin-left: 7px" />
      </a-tooltip>
    </a-form-model-item>
    <template v-if="formData.mode === modeOptions[0]['value']">
      <a-form-model-item label="自动提交意见" v-if="formData.handleMode === handleModeOptions[0]['value']">
        <a-radio-group v-model="formData.submitOpinionMode" size="small" button-style="solid">
          <a-radio-button v-for="item in submitOpinionModeOptions" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item v-if="formData.handleMode === handleModeOptions[1]['value']">
        <template slot="label">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">系统自动跳过重复审批人员时，是否在办理过程中显示人员跳过</div>
            <label>
              是否留痕
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
        <a-switch v-model="formData.keepRecord" />
      </a-form-model-item>
      <template
        v-if="formData.submitOpinionMode === submitOpinionModeOptions[1]['value'] && formData.handleMode === handleModeOptions[0]['value']"
      >
        <a-form-model-item label="缺省意见内容">
          <a-input v-model="formData.defaultSubmitOpinionText">
            <template slot="addonAfter">
              <w-i18n-input :target="formData" code="defaultSubmitOpinionText" v-model="formData.defaultSubmitOpinionText" />
            </template>
          </a-input>
        </a-form-model-item>
      </template>
    </template>
    <template v-else>
      <a-form-model-item>
        <template slot="label">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">系统自动跳过重复审批人员时，是否在办理过程中显示人员跳过</div>
            <label>
              是否留痕
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
        <a-switch v-model="formData.keepRecord" />
      </a-form-model-item>
    </template>
    <!-- 退出规则设置 -->
    <a-form-model-item>
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">
            在审批环境改变或需要用户操作时，退出{{
              `${formData.mode === modeOptions[0]['value'] ? '前置审批' : '后置审批'}`
            }}，由用户人工操作
          </div>
          <label style="font-weight: bold; vertical-align: middle; margin-right: 3px">退出规则设置</label>
          <a-icon type="exclamation-circle" />
        </a-tooltip>
      </template>
    </a-form-model-item>
    <a-form-model-item label="退出条件">
      <template v-if="formData.mode === modeOptions[0]['value']">
        <div class="exit-conditions-tips">前置审批环节之后的任一重复审批环节，满足以下任一开启的条件时，退出前置审批规则</div>
        <a-checkbox-group v-model="formData.exitConditions" class="exit-conditions-group">
          <div
            v-for="(item, index) in beforeExitConditionsOptions"
            :key="index"
            :class="index && index % 2 === 0 ? 'exit-condition-single-user' : ''"
          >
            <a-checkbox
              v-show="index && index % 2 === 0 && formData.exitConditions.includes(beforeExitConditionsOptions[index - 1]['value'])"
              :value="item.value"
            >
              {{ item.label }}
            </a-checkbox>
            <a-checkbox v-show="!index || index % 2 !== 0" :value="item.value">{{ item.label }}</a-checkbox>

            <template v-if="item.tips">
              <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                <template slot="title">{{ item.tips }}</template>
                <a-icon type="exclamation-circle" />
              </a-tooltip>
            </template>
          </div>
        </a-checkbox-group>
      </template>
      <template v-else>
        <div class="exit-conditions-tips">后置审批环节之前的任一重复审批环节，满足以下任一开启的条件时，退出后置审批规则</div>
        <a-checkbox-group v-model="formData.exitConditions" class="exit-conditions-group">
          <div
            v-for="(item, index) in afterExitConditionsOptions"
            :key="index"
            :class="(index + 1) % 2 === 0 ? 'exit-condition-single-user' : ''"
          >
            <a-checkbox
              v-show="(index + 1) % 2 === 0 && formData.exitConditions.includes(afterExitConditionsOptions[index - 1]['value'])"
              :key="item.value"
              :value="item.value"
            >
              {{ item.label }}
            </a-checkbox>
            <a-checkbox v-show="(index + 1) % 2 !== 0" :key="item.value" :value="item.value">{{ item.label }}</a-checkbox>

            <template v-if="item.tips">
              <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                <template slot="title">{{ item.tips }}</template>
                <a-icon type="exclamation-circle" />
              </a-tooltip>
            </template>
          </div>
        </a-checkbox-group>
      </template>
    </a-form-model-item>
    <template v-if="formData.mode === modeOptions[0]['value']">
      <a-form-model-item label="退出范围">
        <a-radio-group v-model="formData.exitScope" size="small" button-style="solid">
          <a-radio-button v-for="item in beforeExitScopeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
        </a-radio-group>
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <template slot="title">{{ beforeExitScopeMaps[formData.exitScope]['tips'] }}</template>
          <a-icon type="exclamation-circle" style="margin-left: 7px" />
        </a-tooltip>
      </a-form-model-item>
    </template>
    <template v-else>
      <a-form-model-item label="退出范围">
        <a-radio-group v-model="formData.exitScope" size="small" button-style="solid">
          <a-radio-button v-for="item in afterExitScopeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
        </a-radio-group>
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <template slot="title">{{ afterExitScopeMaps[formData.exitScope]['tips'] }}</template>
          <a-icon type="exclamation-circle" style="margin-left: 7px" />
        </a-tooltip>
      </a-form-model-item>
      <!-- 补审补办规则 -->
      <a-form-model-item>
        <template slot="label">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">
              因存在分支、条件分支或者其他原因，可能导致后置审批环节没有实际执行而漏审漏办，补审补办功能将检测跳过审批的人员，没有执行后置审批的将补审补办
            </div>
            <label style="font-weight: bold; vertical-align: middle; margin-right: 3px">补审补办规则</label>
            <a-icon type="exclamation-circle" />
          </a-tooltip>
        </template>
      </a-form-model-item>
      <a-form-model-item label="补审补办规则">
        <a-radio-group v-model="formData.supplementRule" class="auto-submit-supplement-rule">
          <div v-for="(item, index) in supplementRuleOptions" :key="index">
            <a-radio :key="item.value" :value="item.value">{{ item.label }}</a-radio>
            <template v-if="item.tips">
              <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                <template slot="title">{{ item.tips }}</template>
                <a-icon type="exclamation-circle" />
              </a-tooltip>
            </template>
          </div>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="补审补办方式">
        <a-radio-group v-model="formData.supplementMode" size="small" button-style="solid">
          <a-radio-button v-for="item in supplementModeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
        </a-radio-group>
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <template slot="title">{{ supplementModeMaps[formData.supplementMode]['tips'] }}</template>
          <a-icon type="exclamation-circle" style="margin-left: 7px" />
        </a-tooltip>
      </a-form-model-item>
      <template v-if="formData.supplementMode === supplementModeOptions[0]['value']">
        <a-form-model-item label="补审补办操作权限">
          <a-radio-group v-model="formData.supplementOperateRight" size="small" button-style="solid">
            <a-radio-button v-for="item in supplementOperateRightOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="补审补办表单权限">
          <a-radio-group v-model="formData.supplementViewFormMode" size="small" button-style="solid">
            <a-radio-button v-for="item in supplementViewFormModeOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </template>
      <template v-else>
        <a-form-model-item label="补审补办环节名称">
          <a-input v-model="formData.supplementTaskName">
            <template slot="addonAfter">
              <w-i18n-input :target="formData" code="supplementTaskName" v-model="formData.supplementTaskName" />
            </template>
          </a-input>
        </a-form-model-item>
      </template>
    </template>
  </a-form-model>
</template>

<script>
import OrgSelectSingleModal from '../commons/org-select-single-modal.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

const modeOptions = [
  {
    label: '前置审批',
    value: 'before',
    tips: '同一用户在一个流程中首次审批后，后续环节如有重复审批时，不需要再次人工审批，由系统自动处理'
  },
  {
    label: '后置审批',
    value: 'after',
    tips: '同一用户在一个流程的多个环节都是办理人时，开启后置审批，则该用户只需要审批最后一次，之前的环节由系统自动处理'
  }
];
let modeMaps = {};
modeOptions.forEach(m => {
  modeMaps[m.value] = m;
});

const effectiveTaskOptions = [
  { label: '全流程', value: 'all' },
  { label: '指定环节', value: 'include' },
  { label: '除指定环节外的其他环节', value: 'exclude' }
];

const effectiveUserOptions = [
  { label: '所有人', value: 'all' },
  { label: '指定人员', value: 'include' },
  { label: '除指定人员外的其他人员', value: 'exclude' }
];
const beforeMatchTypesOptions = [
  { label: '审批节点办理人', value: 'task' },
  { label: '含发起环节办理人', value: 'taskIncludeStart' },
  { label: '协作节点办理人(不含决策人)', value: 'collaboration' },
  { label: '含发起环节办理人', value: 'collaborationIncludeStart' }
];
const matchTypesOptions = [
  { label: '流程发起人', value: 'start' },
  { label: '审批节点办理人', value: 'task' },
  { label: '协作节点办理人(不含决策人)', value: 'collaboration' }
  // { label: '包含分支、条件分支之后环节的办理人', value: 'branch' }
];
const handleModeOptions = [
  { label: '自动审批', value: 'submit', tips: '系统代替用户自动审批并在办理过中留痕，用户为环节已办人' },
  { label: '自动跳过', value: 'skip', tips: '系统自动跳过重复审批人员，用户非环节已办人' }
];
let handleModeMaps = {};
handleModeOptions.forEach(m => {
  handleModeMaps[m.value] = m;
});
const submitOpinionModeOptions = [
  { label: '使用最后一次人工填写意见', value: 'latest' },
  { label: '使用缺省意见', value: 'default' }
];
const beforeExitConditionsOptions = [
  { label: '环节数据版本发生变更', value: 'dataChanged', tips: '关联的表单数据和用户上一次审批时不一致' },
  { label: '环节可编辑表单时', value: 'canEditForm' },
  { label: '仅一人办理或办理人全部去重时判断', value: 'singleUserOnCanEditForm' },
  { label: '环节可编辑表单且存在必填字段时', value: 'editAndRequiredField' },
  { label: '仅一人办理或办理人全部去重时判断', value: 'singleUserOnEditAndRequiredField' },
  { label: '环节需要选择流向时', value: 'chooseDirection', tips: '如果未启用判断条件，最终无用户人工操作时，顺延至由前环节办理人选择' },
  { label: '仅一人办理或办理人全部去重时判断', value: 'singleUserOnChooseDirection' },
  {
    label: '环节需要选择下一环节办理人/抄送人/督办人时',
    value: 'chooseUser',
    tips: '如果未启用判断条件，最终无用户人工操作时，顺延至由前环节办理人选择'
  },
  { label: '仅一人办理或办理人全部去重时判断', value: 'singleUserOnChooseUser' }
];
const afterExitConditionsOptions = [
  { label: '环节可编辑表单时', value: 'canEditForm' },
  { label: '仅一人办理或办理人全部去重时判断', value: 'singleUserOnCanEditForm' },
  { label: '环节可编辑表单且存在必填字段时', value: 'editAndRequiredField' },
  { label: '仅一人办理或办理人全部去重时判断', value: 'singleUserOnEditAndRequiredField' },
  { label: '环节需要选择流向时', value: 'chooseDirection', tips: '如果未启用判断条件，最终无用户人工操作时，顺延至由前环节办理人选择' },
  { label: '仅一人办理或办理人全部去重时判断', value: 'singleUserOnChooseDirection' },
  {
    label: '环节需要选择下一环节办理人/抄送人/督办人时',
    value: 'chooseUser',
    tips: '如果未启用判断条件，最终无用户人工操作时，顺延至由前环节办理人选择'
  },
  { label: '仅一人办理或办理人全部去重时判断', value: 'singleUserOnChooseUser' }
];

const beforeExitScopeOptions = [
  { label: '单次退出前置审批', value: 'single', tips: '仅在触发退出条件的环节退出前置审批' },
  { label: '全流程退出前置审批', value: 'all', tips: '在触发退出条件后，直接退出前置审批，后续环节都不再去重' }
];
let beforeExitScopeMaps = {};
beforeExitScopeOptions.forEach(m => {
  beforeExitScopeMaps[m.value] = m;
});
const afterExitScopeOptions = [
  { label: '单次退出后置审批', value: 'single', tips: '仅在触发退出条件的环节退出后置审批' },
  { label: '全流程退出后置审批', value: 'all', tips: '在触发退出条件后，直接退出后置审批，不再去重' }
];
let afterExitScopeMaps = {};
afterExitScopeOptions.forEach(m => {
  afterExitScopeMaps[m.value] = m;
});
const supplementRuleOptions = [
  { label: '存在未审批过的人员时补审补办', value: '1' },
  { label: '存在未审批人员，且有环节被跳过时补审补办', value: '2', tips: '审批人跳过的环节有其他人进行了审批时，则不补审补办' }
];
const supplementModeOptions = [
  { label: '按最后跳过环节补审补办', value: 'task', tips: '流程将在结束前跳转至未审批人员最后跳过的节点，办理后结束流程' },
  {
    label: '按人员补审补办',
    value: 'user',
    tips: '流程将在结束前虚拟一个补审补办节点，未审批的人员将在此节点一同补审补办，只有提交和查阅表单权限'
  }
];
let supplementModeMaps = {};
supplementModeOptions.forEach(m => {
  supplementModeMaps[m.value] = m;
});
const supplementOperateRightOptions = [
  { label: '只有提交权限', value: 'submit' },
  { label: '同补审环节权限', value: 'default' }
];
const supplementViewFormModeOptions = [
  { label: '只有阅读权限', value: 'readonly' },
  { label: '同补审环节权限', value: 'default' }
];

export default {
  name: 'AutoSubmitRuleInfo',
  inject: ['graph'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    OrgSelectSingleModal,
    WI18nInput
  },
  data() {
    return {
      rules: {
        users: {
          trigger: 'change',
          validator: (rule, value, callback) => {
            value && value.length ? callback() : callback('请选择人员');
          }
        },
        taskIds: {
          trigger: 'change',
          validator: (rule, value, callback) => {
            value && value.length ? callback() : callback('请选择环节');
          }
        }
      },
      modeOptions,
      modeMaps,
      effectiveTaskOptions,
      effectiveUserOptions,
      beforeMatchTypesOptions,
      matchTypesOptions,
      handleModeOptions,
      handleModeMaps,
      submitOpinionModeOptions,
      beforeExitConditionsOptions,
      afterExitConditionsOptions,
      beforeExitScopeOptions,
      beforeExitScopeMaps,
      afterExitScopeOptions,
      afterExitScopeMaps,
      supplementRuleOptions,
      supplementModeOptions,
      supplementModeMaps,
      supplementOperateRightOptions,
      supplementViewFormModeOptions
    };
  },
  computed: {
    // 环节选项
    taskOptions() {
      let options = [];
      if (this.graph.instance) {
        const tasksData = this.graph.instance.tasksData;
        options = tasksData.map(item => {
          return {
            value: item.id,
            label: item.name,
            title: item.name
          };
        });
      }
      return options;
    }
  },
  methods: {
    // 更改处理方式
    changeHandleMode(event) {
      const value = event.target.value;
      if (value === this.handleModeOptions[1]['value']) {
        this.formData.keepRecord = true;
      }
    },
    changeMode(event) {
      const mode = event.target.value;
      if (this.formData.mode === this.modeOptions[1]['value']) {
        this.formData.handleMode = this.handleModeOptions[1]['value'];
      }
      this.$emit('changeMode', mode);
    },
    // 更改生效环节
    changeEffectiveTask() {
      this.formData.taskIds = [];
    },
    // 更改生效人员
    changeEffectiveUser() {
      this.formData.users = [];
    },
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    }
  }
};
</script>
