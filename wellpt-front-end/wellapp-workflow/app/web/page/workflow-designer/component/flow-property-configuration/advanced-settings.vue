<template>
  <!-- 流程属性-高级设置 -->
  <div>
    <a-collapse
      :activeKey="['flowType', 'approvalPath', 'bakUsers', 'messageTemplates', 'records', 'printTemplate', 'opinionCheck', 'other']"
      :bordered="false"
      expandIconPosition="right"
      class="flow-collapse"
    >
      <a-collapse-panel key="flowType" class="flow-collapse-item">
        <template slot="header">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">
              等价流程：将本流程设置为完全按指定流程流转，不需要再单独设置流程图
              <br />
              自由流程：将本流程设置为完全自由流程，环节之间自由提交、自由流转，不受流向的约束
              <br />
              应用类型：输入流程的应用类型ID，可将同一类型的流程归集展示
              <!-- <br />
              流程审计：记录查看流程和表单修改过的痕迹 -->
            </div>
            <label>
              流程类型设置
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
        <a-form-model-item label="设置为等价流程" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
          <w-switch :checked="equalFlowShow" :checkedValue="true" :unCheckedValue="false" @change="changeQqualFlow" />
        </a-form-model-item>
        <template v-if="equalFlowShow">
          <div class="ant-form-item">已启用等价流程，流程将按照等价流程图流转</div>
          <a-form-model-item label="选择流程" prop="equalFlow.id">
            <w-select
              v-model="equalFlow.id"
              :options="equalFlowOptions"
              :formData="equalFlow"
              formDataFieldName="name"
              @change="changeQqualFlowId"
            />
          </a-form-model-item>
        </template>
        <a-form-model-item
          prop="isFree"
          label="设置为自由流程"
          :label-col="{ span: 9 }"
          :wrapper-col="{ span: 14, style: { textAlign: 'right' } }"
        >
          <w-switch v-model="formData.isFree" />
        </a-form-model-item>
        <div class="ant-form-item" v-if="formData.isFree === '1'">已启用自由流程，流程环节自由跳转</div>
        <a-form-model-item prop="applyId" label="应用类型">
          <a-input v-model="formData.applyId" :allowClear="true" />
        </a-form-model-item>
        <!-- <a-form-model-item prop="isAudit" label="流程审计">
          <w-switch v-model="formData.isAudit" />
        </a-form-model-item> -->
      </a-collapse-panel>
      <a-collapse-panel key="approvalPath" header="审批路径设置">
        <a-form-model-item>
          <template slot="label">
            <a-tooltip placement="topRight" :arrowPointAtCenter="true">
              <div slot="title">设置本流程按照指定的一个组织架构进行流转和审批</div>
              <label>
                使用组织
                <a-icon type="exclamation-circle" />
              </label>
            </a-tooltip>
          </template>
          <a-radio-group v-model="formData.useDefaultOrg" size="small" button-style="solid" @change="changeUseDefaultOrg">
            <template v-for="item in useOrgConfig">
              <a-tooltip placement="top" :arrowPointAtCenter="true" v-if="item.value === '1'">
                <div slot="title">{{ defaultOrgData.name }}</div>
                <a-radio-button :key="item.value" :value="item.value">
                  {{ item.label }}
                </a-radio-button>
              </a-tooltip>
              <a-radio-button v-else :key="item.value" :value="item.value">
                {{ item.label }}
              </a-radio-button>
            </template>
          </a-radio-group>
        </a-form-model-item>
        <template v-if="formData.useDefaultOrg === '0'">
          <a-form-model-item prop="orgId" label="" :wrapper-col="{ style: { textAlign: 'left' } }">
            <organization-select v-model="formData.orgId" :allowClear="true" :showSearch="true" @change="changeOrgId" />
          </a-form-model-item>
        </template>
        <a-form-model-item :label-col="{ span: 7 }" :wrapper-col="{ span: 16, style: { textAlign: 'right' } }">
          <template slot="label">
            <a-tooltip placement="topRight" :arrowPointAtCenter="true">
              <div slot="title">当前组织有业务组织时，可选业务组织进行流转</div>
              <label>
                可用业务组织
                <a-icon type="exclamation-circle" />
              </label>
            </a-tooltip>
          </template>
          <a-radio-group v-model="formData.availableBizOrg" size="small" button-style="solid" @change="changeBizOrgType">
            <a-radio-button v-for="item in availableBizOrgOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <template v-if="formData.availableBizOrg === availableBizOrgOptions[2]['value']">
          <div class="ant-form-item">
            <w-select
              v-model="formData.bizOrgId"
              mode="multiple"
              :options="bizOrgsList"
              :replaceFields="{
                title: 'name',
                key: 'id',
                value: 'id'
              }"
            />
          </div>
        </template>
        <a-form-model-item :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
          <template slot="label">
            <a-tooltip placement="topRight" :arrowPointAtCenter="true">
              <div slot="title">如果本流程需要同时使用多个组织架构，或跨组织架构审批，可开启多组织审批。</div>
              <label>
                多组织审批
                <a-icon type="exclamation-circle" />
              </label>
            </a-tooltip>
          </template>
          <w-switch v-model="formData.enableMultiOrg" />
        </a-form-model-item>
        <template v-if="formData.enableMultiOrg === '1'">
          <multi-orgs-approval v-model="formData.multiOrgs" />
        </template>
        <a-form-model-item class="form-item-vertical" label="使用组织或组织的版本变更时，流转中的流程">
          <a-radio-group v-model="formData.autoUpgradeOrgVersion" size="small" button-style="solid">
            <a-radio-button v-for="item in autoUpgradeOrgVersionConfig" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <multi-job-flow :formData="formData" />
        <auto-submit-rule-drawer :formData="formData" />
      </a-collapse-panel>
      <!-- 逾期流程代理 -->
      <a-collapse-panel key="bakUsers" :showArrow="false" class="flow-collapse-item">
        <template slot="header">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">添加本流程的AB岗人员设置，可在流程计时器中设置流程逾期时自动移交给B岗人员处理</div>
            <label>
              逾期流程代理
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
        <div class="bak-user-container">
          <div class="bak-user-item" v-for="(record, index) in formData.bakUsers" :key="index">
            <div class="bak-user-content">
              <div class="bak-user-line">
                <label>A岗</label>
                <org-select
                  class="flow-org-select"
                  :orgVersionId="orgVersionId"
                  :orgVersionIds="orgVersionIds"
                  v-model="record.valueId"
                  v-if="orgVersionId"
                  @change="data => bakUserChange('value', index, data)"
                />
              </div>
              <div class="bak-user-line">
                <label>B岗</label>
                <org-select
                  class="flow-org-select"
                  :orgVersionId="orgVersionId"
                  :orgVersionIds="orgVersionIds"
                  v-model="record.argValueId"
                  v-if="orgVersionId"
                  @change="data => bakUserChange('argValue', index, data)"
                />
              </div>
            </div>
            <div class="bak-user-del">
              <a-button type="link" size="small" @click="delBakUser(record, index)">
                <Icon type="pticon iconfont icon-ptkj-shanchu" />
              </a-button>
            </div>
          </div>
          <a-button type="link" size="small" @click="addBakUser" icon="plus">添加</a-button>
        </div>
      </a-collapse-panel>
      <!-- 消息分发 -->
      <a-collapse-panel key="messageTemplates" :showArrow="false" class="flow-collapse-item">
        <template slot="header">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">设置本流程各个办理环节需要发送的消息或通知</div>
            <label>
              消息分发
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
        <div class="message-templates-container">
          <div class="message-template-item" v-for="(record, index) in formData.messageTemplates" :key="index">
            <div class="message-template-name">{{ record.typeName }}</div>
            <div class="message-template-btn-group">
              <w-switch v-model="record.isSendMsg" size="small" />
              <a-button type="link" size="small" @click="setMessageTemplate(record, index)">
                <Icon type="pticon iconfont icon-ptkj-shezhi" />
              </a-button>
              <a-button type="link" size="small" @click="delMessageTemplate(record, index)">
                <Icon type="pticon iconfont icon-ptkj-shanchu" />
              </a-button>
            </div>
          </div>
          <a-button type="link" size="small" @click="addMessageTemplate" icon="plus">添加</a-button>
        </div>
      </a-collapse-panel>
      <!-- 信息记录 -->
      <a-collapse-panel key="records" :showArrow="false" class="flow-collapse-item">
        <template slot="header">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">添加流程流转过程中需要记录到表单的信息， 例如环节办理意见、办理人等（本流程通用）</div>
            <label>
              信息记录
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
        <div class="message-templates-container">
          <div class="common-list-empty" v-show="!formData.records.length">
            <span>暂无数据</span>
          </div>
          <div class="message-template-item" v-for="(record, index) in formData.records" :key="index">
            <div class="message-template-name">{{ record.name }}</div>
            <div class="message-template-btn-group record-btn-group">
              <a-button type="link" size="small" @click="setRecord(record, index)">
                <Icon type="pticon iconfont icon-ptkj-shezhi" />
              </a-button>
              <a-button type="link" size="small" @click="delRecord(record, index)">
                <Icon type="pticon iconfont icon-ptkj-shanchu" />
              </a-button>
            </div>
          </div>
          <a-button type="link" size="small" @click="addRecord" icon="plus">添加</a-button>
        </div>
      </a-collapse-panel>
      <!-- 流程打印模板 -->
      <a-collapse-panel key="printTemplate" :showArrow="false" class="flow-collapse-item">
        <template slot="header">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">添加本流程各环节通用的打印模板</div>
            <label>
              流程打印模板
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
        <div class="print-template-container">
          <print-template-tree-select :formData="formData" />
        </div>
      </a-collapse-panel>
      <a-collapse-panel key="opinionCheck" :showArrow="false" class="flow-collapse-item">
        <template slot="header">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">设置用户在操作提交、退回、转办、会签时签署意见内容的校验规则</div>
            <label>
              签署意见校验设置
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
        <modal
          title="签署意见校验设置"
          :container="getContainer"
          :ok="saveOpinionCheck"
          okText="保存"
          :width="750"
          wrapperClass="opinion-check-scene flow-timer-modal-wrap"
        >
          <a-button type="link" class="opinion-check-setting">
            <Icon type="pticon iconfont icon-ptkj-shezhi" />
            <span>设置</span>
          </a-button>
          <template slot="content">
            <opinion-check-rules ref="opinionCheckRef" :list="formData.opinionCheckSets" />
          </template>
        </modal>
      </a-collapse-panel>
      <a-collapse-panel key="other" header="其他">
        <a-form-model-item label="流程状态">
          <modal title="流程状态" :container="getContainer" :ok="saveFlowStates" okText="保存" wrapperClass="flow-timer-modal-wrap">
            <a-button type="link">
              <Icon type="pticon iconfont icon-ptkj-shezhi" />
              <span>设置</span>
            </a-button>
            <template slot="content">
              <flow-property-state ref="flowStatesRef" :list="formData.flowStates" />
            </template>
          </modal>
        </a-form-model-item>
        <a-form-model-item class="form-item-vertical" label="加载JS模块">
          <!-- <w-select v-model="formData.customJsModule" :options="flowDevelops" /> -->
          <custom-js-module v-model="formData.customJsModule" />
        </a-form-model-item>
        <a-form-model-item class="form-item-vertical" label="流程事件监听">
          <w-tree-select
            :selectCheckAll="true"
            class="workflow-tree-select"
            v-model="formData.listener"
            :treeData="flowListeners"
            :treeCheckable="true"
            :replaceFields="{
              children: 'children',
              title: 'name',
              key: 'id',
              value: 'id'
            }"
          />
        </a-form-model-item>
        <a-form-model-item class="form-item-vertical" label="环节事件监听">
          <task-listener-tree-select v-model="formData.globalTaskListener" :treeCheckable="true" />
        </a-form-model-item>
        <!-- 事件脚本 -->
        <a-form-model-item class="form-item-vertical" label="事件脚本">
          <event-scripts v-model="formData.eventScripts" />
        </a-form-model-item>
        <!-- 索引设置 -->
        <a-form-model-item label="索引设置">
          <a-radio-group v-model="formData.indexType" size="small" button-style="solid">
            <a-radio-button v-for="item in defineConfig" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <template v-if="formData.indexType == constDefine">
          <a-form-model-item label="索引标题">
            <div style="flex: 1">
              <div>
                <title-define-template
                  :formData="formData"
                  prop="indexTitleExpression"
                  title="索引标题设置"
                  alert="在下方编辑索引表达式，可插入流程内置变量、表单字段和文本。"
                >
                  <a-button type="link" size="small" slot="trigger">
                    <Icon type="pticon iconfont icon-ptkj-shezhi" />
                    <span>设置</span>
                  </a-button>
                </title-define-template>
              </div>
            </div>
          </a-form-model-item>
          <div class="ant-form-item">
            <div class="title-expression-value">
              {{ formData.indexTitleExpression }}
            </div>
          </div>
          <a-form-model-item label="索引内容">
            <div style="flex: 1">
              <div>
                <title-define-template
                  :formData="formData"
                  prop="indexContentExpression"
                  title="索引内容设置"
                  alert="在下方编辑索引表达式，可插入流程内置变量、表单字段和文本。"
                >
                  <a-button type="link" size="small" slot="trigger">
                    <Icon type="pticon iconfont icon-ptkj-shezhi" />
                    <span>设置</span>
                  </a-button>
                </title-define-template>
              </div>
            </div>
          </a-form-model-item>
          <div class="ant-form-item">
            <div class="title-expression-value">
              {{ formData.indexContentExpression }}
            </div>
          </div>
        </template>
      </a-collapse-panel>
    </a-collapse>
    <a-modal
      title="消息分发"
      :visible="messageTemplatesVisible"
      @ok="saveMessageTemplates"
      @cancel="messageTemplatesVisible = false"
      :width="800"
      wrapClassName="flow-timer-modal-wrap"
      :getContainer="getContainer"
      :destroyOnClose="true"
    >
      <message-templates ref="messageTemplateRef" :data="currentMessageTemplate" :orgVersionId="orgVersionId"></message-templates>
    </a-modal>
    <a-modal
      title="信息记录"
      :visible="recordInfoVisible"
      @ok="saveRecord"
      @cancel="recordInfoVisible = false"
      :width="800"
      wrapClassName="flow-timer-modal-wrap"
      :getContainer="getContainer"
      :destroyOnClose="true"
    >
      <record-info ref="recordInfoRef" :data="currentRecordInfo" :orgVersionId="orgVersionId" />
    </a-modal>
  </div>
</template>

<script>
import {
  defaultMessageTemplate,
  useOrgConfig,
  autoUpgradeOrgVersionConfig,
  defineConfig,
  constDefault,
  constDefine,
  availableBizOrgOptions
} from '../designer/constant';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import WSwitch from '../components/w-switch.js';
import WSelect from '../components/w-select';
import WTreeSelect from '../components/w-tree-select.js';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import OpinionCheckRules from './opinion-check-rules.vue';
import FlowPropertyState from './flow-property-state.vue';
import OrganizationSelect from '../commons/organization-select.js';
import PrintTemplateTreeSelect from '../commons/print-template-tree-select.vue';
import TaskListenerTreeSelect from '../commons/task-listener-tree-select.vue';
import TitleDefineTemplate from '../commons/title-define-template.vue';
import MessageTemplates from './message-templates.vue';
import RecordInfo from '../commons/record-info.vue';
import EventScripts from '../commons/event-scripts.vue';
import CustomJsModule from '../commons/custom-js-module.vue';
import MultiOrgsApproval from './multi-orgs-approval.vue';
import MultiJobFlow from '../commons/multi-job-flow.vue';
import AutoSubmitRuleDrawer from './auto-submit-rule-drawer.vue';
import NodeTaskUser from '../designer/NodeTaskUser';
import mixins from '../mixins';

export default {
  name: 'FlowPropertyAdvancedSettings',
  inject: ['designer', 'pageContext', 'graph'],
  mixins: [mixins],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    const equalFlow = JSON.parse(JSON.stringify(this.formData.equalFlow));
    let equalFlowShow = false; // 等价流程
    if (this.formData.equalFlow.id) {
      equalFlowShow = true;
      this.getFlowSchemeService();
    }
    // 逾期流程代理
    if (this.formData.bakUsers.length) {
      this.formData.bakUsers.forEach(item => {
        item.valueId = item.value ? item.value.split('|')[1] : '';
        item.argValueId = item.argValue ? item.argValue.split('|')[1] : '';
      });
    }
    // 流程事件监听
    let flowListenersId = [];
    if (this.formData.listener) {
      flowListenersId = this.formData.listener.split(';');
    }
    return {
      equalFlow,
      equalFlowShow,
      equalFlowOptions: [],
      defaultMessageTemplate,
      useOrgConfig,
      autoUpgradeOrgVersionConfig,
      flowDevelops: [],
      flowListenersId,
      flowListeners: [],
      defineConfig,
      constDefault,
      constDefine,
      availableBizOrgOptions,
      messageTemplatesVisible: false,
      currentMessageTemplate: {},
      currentMessageTemplateIndex: -1,
      recordInfoVisible: false,
      currentRecordInfo: {},
      currentRecordInfoIndex: -1,
      bizOrgsList: []
    };
  },
  components: {
    WSwitch,
    WSelect,
    WTreeSelect,
    OrgSelect,
    Modal,
    OpinionCheckRules,
    FlowPropertyState,
    OrganizationSelect,
    PrintTemplateTreeSelect,
    TaskListenerTreeSelect,
    TitleDefineTemplate,
    MessageTemplates,
    RecordInfo,
    EventScripts,
    CustomJsModule,
    MultiOrgsApproval,
    MultiJobFlow,
    AutoSubmitRuleDrawer
  },
  created() {
    // this.getJavaScriptDevelops();
    if (!this.formData.hasOwnProperty('availableBizOrg') || !this.formData.availableBizOrg) {
      this.$set(this.formData, 'availableBizOrg', 'all');
    }
    if (!this.formData.hasOwnProperty('bizOrgId') || !this.formData.bizOrgId) {
      this.$set(this.formData, 'bizOrgId', '');
    }
    if (!this.formData.hasOwnProperty('multiOrgs') || !this.formData.multiOrgs) {
      this.$set(this.formData, 'multiOrgs', []);
    }
    this.getFlowListeners();
    this.bizOrgsList = this.designer.getBizOrgsListByOrgId(this.useOrgId);
  },
  methods: {
    // 更改使用组织
    changeUseDefaultOrg(event) {
      const value = event.target.value;
      if (value === '1') {
        // 使用默认组织
        this.pageContext.emitEvent('getDefaultOrgBySystem');
        this.formData.bizOrgId = '';
      }
      this.changeBizOrgType();
    },
    changeOrgId(orgId) {
      this.pageContext.emitEvent('getOrgVersionId', {
        orgId,
        callback: orgVersionId => {
          this.formData.orgVersionId = orgVersionId;
        }
      });

      this.formData.bizOrgId = '';
      this.changeBizOrgType();
    },
    setTaskUser() {
      // this.pageContext.emitEvent('setTaskUser', {
      //   orgId
      // });

      if (this.graph.instance) {
        const cells = this.graph.instance.getNodes();
        for (let index = 0; index < cells.length; index++) {
          const cell = cells[index];
          const cellData = cell.data;
          const shape = cell.shape;
          if (cellData.isSetUser === '1') {
            // 指定办理人
            const taskUser = new NodeTaskUser({
              orgId: this.useOrgId
            });
            // cellData.users[0] = taskUser;
            this.$set(cellData.users, 0, taskUser); // 触发数组watch
          }
        }
      }
    },
    changeBizOrgType() {
      if (this.formData.availableBizOrg === this.availableBizOrgOptions[0]['value']) {
        this.formData.bizOrgId = '';
      } else if (this.formData.availableBizOrg === this.availableBizOrgOptions[2]['value']) {
        this.bizOrgsList = this.designer.getBizOrgsListByOrgId(this.useOrgId);
      }
    },
    // 添加逾期流程代理
    addBakUser() {
      this.formData.bakUsers.push({
        type: 16,
        value: '',
        argValue: ''
      });
    },
    delBakUser(data, index) {
      this.formData.bakUsers.splice(index, 1);
    },
    bakUserChange(param, index, data) {
      if (data.value) {
        this.formData.bakUsers[index][param] = data.label + '|' + data.value;
      } else {
        this.formData.bakUsers[index][param] = '';
      }
    },
    // 消息分发
    saveMessageTemplates() {
      this.$refs.messageTemplateRef.save(({ valid, error, data }) => {
        if (valid) {
          if (this.currentMessageTemplateIndex == -1) {
            this.formData.messageTemplates.push(data);
          } else {
            this.formData.messageTemplates.splice(this.currentMessageTemplateIndex, 1, data);
          }
          this.messageTemplatesVisible = false;
        }
      });
    },
    addMessageTemplate() {
      this.currentMessageTemplate = {
        type: defaultMessageTemplate[0]['type'],
        typeName: defaultMessageTemplate[0]['typeName']
      };
      this.currentMessageTemplateIndex = -1;
      this.messageTemplatesVisible = true;
    },
    setMessageTemplate(data, index) {
      this.currentMessageTemplateIndex = index;
      this.currentMessageTemplate = data;
      this.messageTemplatesVisible = true;
    },
    delMessageTemplate(data, index) {
      this.formData.messageTemplates.splice(index, 1);
    },
    // 信息记录
    saveRecord() {
      this.$refs.recordInfoRef.save(({ valid, error, data }) => {
        if (valid) {
          if (this.currentRecordInfoIndex == -1) {
            this.formData.records.push(data);
          } else {
            this.formData.records.splice(this.currentRecordInfoIndex, 1, data);
          }
          this.recordInfoVisible = false;
        }
      });
    },
    addRecord() {
      this.currentRecordInfo = {};
      this.currentRecordInfoIndex = -1;
      this.recordInfoVisible = true;
    },
    setRecord(data, index) {
      this.currentRecordInfoIndex = index;
      this.currentRecordInfo = data;
      this.recordInfoVisible = true;
    },
    delRecord(data, index) {
      this.formData.records.splice(index, 1);
    },

    // 签署意见校验设置
    saveOpinionCheck(callback) {
      this.$refs.opinionCheckRef.save(({ valid, error, data }) => {
        callback(valid);
        if (valid) {
          this.formData.opinionCheckSets = data;
        }
      });
    },
    // 流程状态
    saveFlowStates(callback) {
      this.$refs.flowStatesRef.save(({ valid, error, data }) => {
        callback(valid);
        if (valid) {
          this.formData.flowStates = data;
        }
      });
    },
    changeQqualFlow(equalFlowShow) {
      if (equalFlowShow) {
        const equalFlowEnable = () => {
          this.pageContext.emitEvent('equalFlowShow', { equalFlowShow });
          this.equalFlowShow = equalFlowShow;
          if (this.equalFlow.id) {
            this.formData.equalFlow.id = this.equalFlow.id;
            this.formData.equalFlow.name = this.equalFlow.name;
          }
          this.getFlowSchemeService();
        };
        const cells = this.graph.instance.getCells();
        if (cells.length) {
          this.$confirm({
            title: '确认',
            content: '启用等价流程将清空现有流程图，是否启用？',
            onOk: () => {
              equalFlowEnable();
            }
          });
        } else {
          equalFlowEnable();
        }
      } else {
        this.pageContext.emitEvent('equalFlowShow', { equalFlowShow });
        this.formData.equalFlow.id = '';
        this.formData.equalFlow.name = '';
        this.equalFlowShow = equalFlowShow;
      }
    },
    getFlowSchemeService(searchValue) {
      const params = {
        searchValue,
        serviceName: 'flowSchemeService',
        queryMethod: '',
        pageSize: 20
      };
      this.$axios
        .post('/common/select2/query', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.results) {
              const data = res.data.results;
              data.splice(0, 1);
              this.equalFlowOptions = data;
            }
          }
        });
    },
    changeQqualFlowId(value, option) {
      this.formData.equalFlow.id = this.equalFlow.id;
      this.formData.equalFlow.name = this.equalFlow.name;
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    // 获取流程JS二开
    getJavaScriptDevelops() {
      const params = {
        serviceName: 'appJavaScriptModuleMgr',
        queryMethod: '',
        pageSize: 1000,
        pageNo: 1,
        dependencyFilter: 'WorkView'
      };
      this.$axios
        .post('/common/select2/query', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.results) {
              const data = res.data.results;
              this.flowDevelops = data;
            }
          }
        });
    },
    // 获取流程事件监听
    getFlowListeners() {
      const params = {
        args: JSON.stringify([-1]),
        serviceName: 'flowSchemeService',
        methodName: 'getFlowListeners'
      };
      this.$axios
        .post('/json/data/services', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.flowListeners = data;
            }
          }
        });
    }
  }
};
</script>
