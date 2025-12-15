<template>
  <!-- 子流程表单 -->
  <PerfectScrollbar :style="{ height: '480px' }" ref="scroll">
    <a-form-model
      ref="form"
      :model="formData"
      :rules="rules"
      :colon="false"
      labelAlign="left"
      :label-col="{ span: 5 }"
      :wrapper-col="{ span: 19, style: { textAlign: 'right' } }"
    >
      <a-form-model-item prop="value" label="流程名称">
        <flow-select-multiple-views
          v-model="formData.value"
          formDataFieldName="name"
          :formData="formData"
          :labelInValue="false"
          viewMode="modal"
          @change="changeFlowId"
        />
      </a-form-model-item>
      <a-form-model-item prop="label" label="流程标签">
        <a-input v-model="formData.label">
          <template slot="addonAfter">
            <w-i18n-input :target="formData" :code="'flowLabel_' + formData.id" v-model="formData.label" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="分发条件">
        <condition-setting v-model="formData.conditions" text="分发条件" :types="['1', '3', '5', '4']" :orgVersionId="orgVersionId" />
      </a-form-model-item>
      <a-form-model-item label="分发粒度">
        <template v-if="distributeIsBizOrg">
          <!-- 分发组织为业务组织 -->
          <a-radio-group v-model="formData.granularity" size="small" button-style="solid">
            <a-radio-button v-for="item in bizOrgGranularityOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
          </a-radio-group>
          <template v-if="formData.granularity === bizOrgGranularityOptions[0]['value']">
            <div>分发至业务维度节点或部门下的目标业务角色成员</div>
            <w-select
              v-model="formData.bizRoleId"
              mode="multiple"
              :options="bizOrgRolesOptions"
              :replaceFields="{
                title: 'name',
                key: 'uuid',
                value: 'id'
              }"
            />
          </template>
          <div class="subflow-granularity-tips">{{ bizOrgGranularityMap[formData.granularity]['desc'] }}</div>
        </template>
        <template v-else>
          <!-- 分发组织为行政组织 -->
          <a-radio-group v-model="formData.granularity" size="small" button-style="solid">
            <a-radio-button v-for="item in orgGranularityOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
          </a-radio-group>
          <div class="subflow-granularity-tips">{{ orgGranularityMap[formData.granularity]['desc'] }}</div>
        </template>
      </a-form-model-item>
      <a-form-model-item label="流程实例">
        <div>
          <w-select v-model="formData.createWay" formDataFieldName="createWayName" :formData="formData" :options="createWayOptions">
            <template slot="addonBefore">来源</template>
          </w-select>
        </div>
        <template v-if="formData.createWay === createWayOptions[1]['value']">
          <w-select
            v-model="formData.interfaceValue"
            formDataFieldName="interfaceName"
            :formData="formData"
            :options="interfaceOptions"
            :replaceFields="replaceFields"
            addonBefore="接口"
          />
        </template>
        <div class="title-expression-value" v-else>
          <a-row>
            <a-col :span="12">
              <a-form-model-item label="表单" :label-col="{ style: { textAlign: 'right' } }">
                <a-select
                  v-model="createInstanceFormId"
                  placeholder="请选择"
                  @change="changeFormId"
                  :getPopupContainer="getPopupContainerByPs()"
                  :dropdownClassName="getDropdownClassName()"
                >
                  <a-select-opt-group v-for="(opt, i) in formOptions" :key="i" :label="opt.label">
                    <a-select-option v-for="item in opt.children" :key="item.value" :value="item.value" :title="item.label">
                      {{ item.label }}
                    </a-select-option>
                  </a-select-opt-group>
                </a-select>
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item label="字段" :label-col="{ style: { textAlign: 'right' } }">
                <w-select
                  v-model="formData.taskUsers"
                  formDataFieldName="taskUsersName"
                  :formData="formData"
                  :options="fieldOptions"
                  :replaceFields="{
                    title: 'name',
                    key: 'code',
                    value: 'code'
                  }"
                />
              </a-form-model-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="12">
              <a-form-model-item label="创建方式" :label-col="{ style: { textAlign: 'right' } }">
                <w-select
                  v-model="formData.createInstanceWay"
                  formDataFieldName="createInstanceWayName"
                  :formData="formData"
                  :options="createInstanceWayOptions"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item label="" :label-col="{ style: { textAlign: 'right' } }" v-if="showCreateInstanceBatch">
                <w-checkbox v-model="formData.createInstanceBatch" style="margin-left: 27px">按从表行分批次生成实例</w-checkbox>
              </a-form-model-item>
            </a-col>
          </a-row>
        </div>
        <div>
          <w-checkbox v-model="formData.isMajor">主办</w-checkbox>
          <w-checkbox v-model="formData.isWait">等待流程</w-checkbox>
          <w-checkbox v-model="formData.isShare">共享流程</w-checkbox>
          <w-checkbox v-model="formData.notifyDoing">办结通知其他子流程</w-checkbox>
        </div>
      </a-form-model-item>
      <a-form-model-item label="流程标题">
        <div>
          <a-radio-group v-model="formData.title" size="small" @change="changeTitleMode">
            <a-radio v-for="item in titleExpressionConfig" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio>
          </a-radio-group>
        </div>
        <div v-if="formData.title === constCustom">
          <title-define-template
            :isSubflow="true"
            :formData="{ titleExpression: customTitleExpression }"
            prop="titleExpression"
            title="流程标题设置"
            alert="在下方编辑流程标题表达式，可插入流程内置变量、表单字段和文本。"
            @change="changeTitle"
          >
            <a-button type="link" size="small" icon="setting" slot="trigger">设置</a-button>
          </title-define-template>
          <span style="margin-left: -6px">自定义标题为空时，等同于默认</span>
        </div>
        <div class="title-expression-value" style="line-height: 20px">
          <template v-if="formData.title === constDefault">
            {{ subflowDefTitleExpression }}
          </template>
          <template v-else>
            {{ customTitleExpression }}
          </template>
        </div>
      </a-form-model-item>
      <a-form-model-item label="提交环节">
        <w-select
          v-model="formData.toTaskId"
          formDataFieldName="toTaskName"
          :formData="formData"
          :options="toTaskOptions"
          :replaceFields="replaceFields"
        />
      </a-form-model-item>
      <a-form-model-item label="拷贝信息">
        <new-flow-bot-rule v-model="formData.copyBotRuleId" name="copyBotRuleName" :formData="formData" :options="botRuleOptions" />
      </a-form-model-item>
      <a-form-model-item label="实时同步">
        <new-flow-bot-rule v-model="formData.syncBotRuleId" name="syncBotRuleName" :formData="formData" :options="botRuleOptions" />
      </a-form-model-item>
      <a-form-model-item label="信息反馈父流程">
        <w-checkbox v-model="formData.returnWithOver">办结时反馈</w-checkbox>
        <w-checkbox v-model="formData.returnWithDirection">指定反馈流向</w-checkbox>
      </a-form-model-item>
      <a-form-model-item label="反馈流向" v-if="formData.returnWithDirection === '1'">
        <w-select
          v-model="formData.returnDirectionId"
          formDataFieldName="returnDirectionName"
          :formData="formData"
          :options="returnDirectionOptions"
        />
      </a-form-model-item>
      <a-form-model-item label="反馈信息">
        <new-flow-bot-rule v-model="formData.returnBotRuleId" name="returnBotRuleName" :formData="formData" :options="botRuleOptions" />
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          <label>办理信息展示</label>
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">
              设置子流程实例中，办理进度、信息分发和操作记录等表格的展示位置。注意：只有主流程和子流程表单相同时，同主流程显示位置才生效。
            </div>
            <a-icon type="exclamation-circle" />
          </a-tooltip>
        </template>
      </a-form-model-item>
      <a-form-model-item label="办理进度">
        <a-radio-group
          v-model="undertakeSituationMode"
          size="small"
          @change="event => changePlaceHolderMode(event, 'undertakeSituationPlaceHolder')"
        >
          <a-radio v-for="item in placeHolderConfig" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio>
        </a-radio-group>
        <div v-if="undertakeSituationMode === '2'" class="sub-flow-new-progress">
          <w-select
            v-model="formData.undertakeSituationPlaceHolder"
            :options="formBlockOptions"
            :replaceFields="replaceFields"
            :getPopupContainer="getPopupContainer"
          />
        </div>
      </a-form-model-item>
      <a-form-model-item label="信息分布">
        <a-radio-group
          v-model="infoDistributionMode"
          size="small"
          @change="event => changePlaceHolderMode(event, 'infoDistributionPlaceHolder')"
        >
          <a-radio v-for="item in placeHolderConfig" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio>
        </a-radio-group>
        <div v-if="infoDistributionMode === '2'" class="sub-flow-new-progress">
          <w-select
            v-model="formData.infoDistributionPlaceHolder"
            :options="formBlockOptions"
            :replaceFields="replaceFields"
            :getPopupContainer="getPopupContainer"
          />
        </div>
      </a-form-model-item>
      <a-form-model-item label="操作记录">
        <a-radio-group
          v-model="operationRecordMode"
          size="small"
          @change="event => changePlaceHolderMode(event, 'operationRecordPlaceHolder')"
        >
          <a-radio v-for="item in placeHolderConfig" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio>
        </a-radio-group>
        <div v-if="operationRecordMode === '2'" class="sub-flow-new-progress">
          <w-select
            v-model="formData.operationRecordPlaceHolder"
            :options="formBlockOptions"
            :replaceFields="replaceFields"
            :getPopupContainer="getPopupContainer"
            @dropdownVisibleChange="updateScroll"
          />
        </div>
      </a-form-model-item>
    </a-form-model>
  </PerfectScrollbar>
</template>

<script>
import {
  createWayOptions,
  constDefault,
  constCustom,
  titleExpressionConfig,
  createInstanceWayOptions,
  placeHolderConfig,
  subflowDefTitleExpression,
  orgGranularityOptions,
  bizOrgGranularityOptions
} from '../designer/constant';
import {
  fetchFlowTasksById,
  fetchSubtaskDispatcherCustomInterfaces,
  fetchBotRuleConfFacadeService,
  fetchBizOrgRolesByBizOrgId,
  fetchAllFlowAsCategoryTree
} from '../api/index';
import WCheckbox from '../components/w-checkbox';
import WSelect from '../components/w-select';
import WTreeSelect from '../components/w-tree-select';
import NewFlowBotRule from './new-flow-bot-rule.vue';
import TitleDefineTemplate from '../commons/title-define-template.vue';
import ConditionSetting from './condition-setting.vue';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import FlowSelectMultipleViews from '../commons/flow-select-multiple-views.vue';

export default {
  name: 'NodeSubflowFlowInfo',
  inject: ['designer', 'workFlowData', 'subflowData', 'unChangedGranularityIds'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    if (!this.formData.hasOwnProperty('granularity')) {
      this.$set(this.formData, 'granularity', '');
    }
    if (!this.formData.hasOwnProperty('bizRoleId')) {
      this.$set(this.formData, 'bizRoleId', '');
    }
    let createInstanceFormId = this.formData.createInstanceFormId ? this.formData.createInstanceFormId : undefined;
    let customTitleExpression = '';
    if (this.formData.titleExpression !== subflowDefTitleExpression) {
      customTitleExpression = this.formData.titleExpression;
    }
    // 办理进度
    if (this.formData.undertakeSituationPlaceHolderValue) {
      // 兼容旧数据
      this.formData.undertakeSituationPlaceHolder = this.formData.undertakeSituationPlaceHolderValue;
    }
    if (this.formData.undertakeSituationPlaceHolder === '1') {
      this.formData.undertakeSituationPlaceHolder = '';
    }
    let undertakeSituationMode = '1';
    if (this.formData.undertakeSituationPlaceHolder) {
      undertakeSituationMode = '2';
    }
    // 信息分布
    if (this.formData.infoDistributionPlaceHolderValue) {
      this.formData.infoDistributionPlaceHolder = this.formData.infoDistributionPlaceHolderValue;
    }
    if (this.formData.infoDistributionPlaceHolder === '1') {
      this.formData.infoDistributionPlaceHolder = '';
    }
    let infoDistributionMode = '1';
    if (this.formData.infoDistributionPlaceHolder) {
      infoDistributionMode = '2';
    }
    // 操作记录
    if (this.formData.operationRecordPlaceHolderValue) {
      this.formData.operationRecordPlaceHolder = this.formData.operationRecordPlaceHolderValue;
    }
    if (this.formData.operationRecordPlaceHolder === '1') {
      this.formData.operationRecordPlaceHolder = '';
    }
    let operationRecordMode = '1';
    if (this.formData.operationRecordPlaceHolder) {
      operationRecordMode = '2';
    }

    let orgGranularityMap = {},
      bizOrgGranularityMap = {};
    orgGranularityOptions.forEach(item => {
      orgGranularityMap[item.value] = item;
    });
    bizOrgGranularityOptions.forEach(item => {
      bizOrgGranularityMap[item.value] = item;
    });
    let distributeIsBizOrg = false; // 分发组织是否业务组织
    const hasIndex = this.unChangedGranularityIds.value.findIndex(id => id === this.formData.id);
    if (this.formData.granularity && hasIndex === -1) {
      if (bizOrgGranularityMap[this.formData.granularity]) {
        distributeIsBizOrg = true;
      }
    } else {
      if (this.subflowData.businessType.indexOf('BO_') === 0) {
        distributeIsBizOrg = true;
      }
      if (distributeIsBizOrg) {
        this.formData.granularity = bizOrgGranularityOptions[0]['value'];
      } else {
        this.formData.granularity = orgGranularityOptions[2]['value'];
      }
    }

    return {
      rules: {
        value: { required: true, message: '请选择流程名称' }
      },
      replaceFields: {
        title: 'name',
        key: 'id',
        value: 'id'
      },
      createInstanceFormId,
      constCustom,
      constDefault,
      titleExpressionConfig,
      createInstanceWayOptions,
      createWayOptions,
      interfaceOptions: [],
      botRuleOptions: [],
      returnDirectionOptions: [],
      formOptionGroup: [],
      flowTreeData: [],
      toTaskOptions: [],
      formBlockOptions: [],
      placeHolderConfig,
      subflowDefTitleExpression,
      customTitleExpression,
      undertakeSituationMode,
      infoDistributionMode,
      operationRecordMode,
      distributeIsBizOrg,
      orgGranularityOptions,
      orgGranularityMap,
      bizOrgGranularityMap,
      bizOrgGranularityOptions,
      bizOrgRolesOptions: []
    };
  },
  components: {
    WCheckbox,
    WSelect,
    WTreeSelect,
    NewFlowBotRule,
    TitleDefineTemplate,
    ConditionSetting,
    WI18nInput,
    FlowSelectMultipleViews
  },
  computed: {
    orgVersionId() {
      return this.workFlowData.property.orgVersionId || '';
    },
    showCreateInstanceBatch() {
      let show = false;
      if (this.formData.createInstanceFormId && this.formData.createInstanceFormId !== this.designer.formFieldDefinition.uuid) {
        show = true;
      }
      return show;
    },
    //流程实例-表单选项
    formOptions() {
      let optionGroup = [
        {
          label: '主表',
          children: []
        }
      ];
      if (this.designer.formDefinition) {
        const data = this.designer.formFieldDefinition;
        optionGroup[0]['children'] = [
          {
            label: data.name,
            value: data.uuid
          }
        ];
      }
      if (Object.keys(this.designer.subformFieldMap)) {
        const subformFieldMap = this.designer.subformFieldMap;
        optionGroup.push({
          label: '从表',
          children: []
        });
        let subform = [];
        for (const key in subformFieldMap) {
          const item = subformFieldMap[key];
          subform.push({
            label: item.name,
            value: item.uuid
          });
        }
        optionGroup[1]['children'] = subform;
      }
      return optionGroup;
    },
    // 流程实例-字段选项
    fieldOptions() {
      let fieldOptions = [];
      if (this.formData.createInstanceFormId) {
        if (this.formData.createInstanceFormId === this.designer.formFieldDefinition.uuid) {
          fieldOptions = this.designer.formFieldDefinition.fields;
        } else {
          if (this.designer.subformFieldMap[this.formData.createInstanceFormId]) {
            fieldOptions = this.designer.subformFieldMap[this.formData.createInstanceFormId]['fields'];
          }
        }
      }
      return fieldOptions;
    }
  },
  created() {
    // this.getFlowTreeDataSync();
    this.getSubtaskDispatcherCustomInterfaces();
    this.getBotRuleConfFacadeService();
    if (this.formData.value) {
      this.changeFlowId(this.formData.value);
    }
    if (this.distributeIsBizOrg) {
      this.getBizOrgRoles();
    }
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    // 改变显示位置
    changePlaceHolderMode(event, valueKey) {
      const value = event.target.value;
      if (value === this.placeHolderConfig[0]['value']) {
        this.formData[valueKey] = '';
      }
    },
    // 获取业务组织角色
    getBizOrgRoles(bizOrgId = this.subflowData.businessType) {
      if (!bizOrgId) {
        this.bizOrgRolesOptions = [];
        return;
      }
      fetchBizOrgRolesByBizOrgId({ bizOrgId }).then(res => {
        this.bizOrgRolesOptions = res;
      });
    },
    // 同步方式获取流程树
    getFlowTreeDataSync() {
      fetchAllFlowAsCategoryTree().then(res => {
        const formatTreeData = arr => {
          if (!arr) {
            return [];
          }
          return arr.map(item => {
            const children = item['children'];

            let node = {
              title: item.name,
              key: item.id,
              label: item.name,
              value: item.id,
              isLeaf: children && children.length ? false : true,
              selectable: children && children.length ? false : true,
              sourceData: item
            };

            if (children) {
              node['children'] = formatTreeData(children);
              return node;
            }
            return node;
          });
        };

        this.flowTreeData = formatTreeData(res);
      });
    },
    getFlowTreeData() {
      this.getFlowTree().then(res => {
        this.flowTreeData = this.formatTreeData(res);
      });
    },
    // 改变流程标题模式
    changeTitleMode(event) {
      const mode = event.target.value;
      if (mode === constCustom) {
        this.formData.titleExpression = this.customTitleExpression ? this.customTitleExpression : this.subflowDefTitleExpression;
      } else {
        this.formData.titleExpression = this.subflowDefTitleExpression;
      }
    },
    // 获取流程树
    getFlowTree(nodeId = -1) {
      const formId = this.designer.formFieldDefinition.uuid;
      const params = {
        args: JSON.stringify([nodeId, formId]),
        methodName: 'getFlowTree',
        serviceName: 'flowSchemeService'
      };

      return new Promise((resolve, reject) => {
        this.$axios
          .post('/json/data/services', {
            ...params
          })
          .then(res => {
            if (res.status === 200) {
              if (res.data && res.data.code === 0) {
                const data = res.data.data;
                resolve(data);
              }
            }
          });
      });
    },
    // 加载流程树
    onLoadFlowTreeData(treeNode) {
      const { id } = treeNode.dataRef;
      return new Promise((resolve, reject) => {
        this.getFlowTree(id).then(res => {
          treeNode.dataRef.children = this.formatTreeData(res);
          resolve();
        });
      });
    },
    formatTreeData(data) {
      data.forEach(item => {
        if (item.isParent) {
          item.isLeaf = false;
        } else {
          item.isLeaf = true;
        }
      });
      return data;
    },
    // 流程实例-来源-接口定义
    getSubtaskDispatcherCustomInterfaces() {
      fetchSubtaskDispatcherCustomInterfaces().then(res => {
        this.interfaceOptions = res;
      });
    },
    // 更改流程实例-接口
    changeInterface(value, option) {
      this.formData.interfaceValue = value;
      this.formData.interfaceName = option.componentOptions.children[0].text.trim();
    },
    // 获取单据转换
    getBotRuleConfFacadeService() {
      fetchBotRuleConfFacadeService().then(res => {
        this.botRuleOptions = res;
      });
    },
    // 根据流程id获取流程流向
    getFlowDirection(flowDefId) {
      const params = {
        serviceName: 'flowSchemeService',
        queryMethod: 'loadDirectionSelectData',
        pageSize: 1000,
        pageNo: 1,
        flowDefId
      };

      this.$axios
        .post('/common/select2/query', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.results) {
              const data = res.data.results;
              this.returnDirectionOptions = data;
            }
          }
        });
    },
    // 更改流程标题
    changeTitle(arg) {
      this.customTitleExpression = arg.value;
      if (arg.value) {
        this.formData.titleExpression = arg.value;
      } else {
        this.formData.titleExpression = this.subflowDefTitleExpression;
      }
    },
    // 更改流程实例表单
    changeFormId(value) {
      this.formData.createInstanceFormId = value;
      this.formData.taskUsers = '';
    },
    // 更改流程
    changeFlowId(flowDefId, label, extra) {
      if (extra && extra.triggerNode) {
        const dataRef = extra.triggerNode.dataRef;
        if (!dataRef.isLeaf) {
          this.formData.value = '';
          this.formData.name = '';
          return;
        }
      }
      this.getSubmitTaskOptions(flowDefId);
      this.getFlowDirection(flowDefId);
      this.getFormBlocksByFlowDefId(flowDefId).then(res => {
        this.formBlockOptions = res;
      });
    },
    // 获取提交环节
    getSubmitTaskOptions(flowDefId, draft = true, autoSubmit = true) {
      let hideAutoSubmit = false;
      let taskOptions = [];
      this.isAutoSubmitForkTask(flowDefId).then(res => {
        if (autoSubmit) {
          hideAutoSubmit = res;
        }
        if (draft) {
          taskOptions.push({
            id: 'DRAFT',
            name: '草稿'
          });
        }
        if (autoSubmit && !hideAutoSubmit) {
          taskOptions.push({
            id: 'AUTO_SUBMIT',
            name: '自动提交'
          });
        }
        fetchFlowTasksById(flowDefId).then(res => {
          this.toTaskOptions = taskOptions.concat(res);
        });
      });
    },
    // 是否隐藏自动提交，当选择的子流程定义，发起环节有多个流出流向，或者流出流向存在判断节点时，自动提交选项隐藏
    isAutoSubmitForkTask(flowDefId) {
      const params = {
        flowDefId
      };
      return new Promise((resolve, reject) => {
        this.$axios
          .get('/api/workflow/definition/isAutoSubmitForkTask', {
            params
          })
          .then(res => {
            if (res.status === 200) {
              if (res.data && res.data.code === 0) {
                const data = res.data.data;
                resolve(data);
              }
            }
          });
      });
    },
    // 根据流程id获取表单布局
    getFormBlocksByFlowDefId(flowDefId) {
      const params = {
        flowDefId
      };
      return new Promise((resolve, reject) => {
        this.$axios
          .get('/api/workflow/definition/getFormBlocksByFlowDefId', {
            params
          })
          .then(res => {
            if (res.status === 200) {
              if (res.data && res.data.code === 0) {
                const data = res.data.data;
                resolve(data);
              }
            }
          });
      });
    },
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    },
    updateScroll() {
      this.$nextTick(() => {
        const timer = setTimeout(() => {
          clearTimeout(timer);
          this.$refs.scroll.update();
        }, 300);
      });
    },
    getPopupContainer(triggerNode) {
      return triggerNode.closest('.ant-form');
    },
    // 获取表单配置
    getFormDefinition() {
      const params = {
        formUuid: this.workFlowData.property.formID,
        justDataAndDef: false
      };
      this.$axios.post('/pt/dyform/definition/getFormDefinition', { ...params }).then(res => {
        if (res.status === 200) {
          const data = res.data;
          let optionGroup = [{ label: '主表', children: [{ label: data.name, value: data.uuid }] }];
          if (data.subforms && Object.keys(data.subforms).length) {
            optionGroup.push({
              label: '从表',
              children: []
            });
            for (const key in data.subforms) {
              const item = data.subforms[key];
              optionGroup[1]['children'].push({
                label: item.name,
                value: item.formUuid
              });
            }
          }
          this.formOptionGroup = optionGroup;
        }
      });
    }
  }
};
</script>
