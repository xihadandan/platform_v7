<template>
  <a-form-model
    class="basic-info"
    :model="timer"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'left' } }"
    :colon="false"
  >
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="timer.name" />
    </a-form-model-item>
    <a-form-model-item label="状态代码字段" prop="stateCodeField">
      <a-select
        v-model="timer.stateCodeField"
        show-search
        style="width: 100%"
        :options="formFieldOptions"
        :filter-option="filterSelectOption"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="计时状态">
      <a-tree-select
        v-model="timer.stateIds"
        style="width: 100%"
        :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
        :multiple="true"
        :tree-data="stateTreeData"
        :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
        tree-default-expand-all
        @change="onStateTreeChange"
      ></a-tree-select>
    </a-form-model-item>
    <a-form-model-item label="离开计时状态时" prop="timerOfStateChanged">
      <a-radio-group v-model="timer.timerOfStateChanged">
        <a-radio value="stop">结束计时</a-radio>
        <a-radio value="pause">暂停计时</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="计时服务配置" prop="timerConfigId">
      <a-select
        v-model="timer.timerConfigId"
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        @change="onTimerConfigChange"
      >
        <a-select-option v-for="d in timerConfigOptions" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item v-if="dyTimeLimit" label="动态时限字段" prop="timeLimitField">
      <a-select
        v-model="timer.timeLimitField"
        show-search
        style="width: 80%"
        :options="formFieldOptions"
        :filter-option="filterSelectOption"
      ></a-select>
      {{ getTimeLimitUnitName(timerConfig) }}
    </a-form-model-item>
    <a-form-model-item v-if="!dyTimeLimit && timerConfig" label="固定时限">
      <a-label>{{ timerConfig.timeLimit }}{{ getTimeLimitUnitName(timerConfig) }}</a-label>
    </a-form-model-item>
    <a-form-model-item label="工作时间方案" prop="workTimePlanId">
      <a-select
        v-model="timer.workTimePlanId"
        show-search
        style="width: 100%"
        :options="workTimePlanOptions"
        :filter-option="filterSelectOption"
      ></a-select>
    </a-form-model-item>

    <a-form-model-item label="预警处理" prop="enableAlarmDoing">
      <a-switch v-model="timer.enableAlarmDoing"></a-switch>
    </a-form-model-item>
    <a-form-model-item label="预警消息通知" v-if="timer.enableAlarmDoing">
      提前
      <a-input-number v-model="timer.alarmTimeLimit" :min="1" :max="10" :precision="0" style="width: 60px"></a-input-number>
      <a-select v-model="timer.alarmTimingMode" style="width: 100px" :options="alarmTimingModeOptions"></a-select>
      开始消息提醒，共
      <a-input-number v-model="timer.alarmCount" :min="1" :max="10" :precision="0" style="width: 60px"></a-input-number>
      次
      <a-checkbox-group v-model="timer.alarmMsgObjects" :options="dueMsgObjectsOptions" />
    </a-form-model-item>
    <a-form-model-item label=" " v-if="timer.enableAlarmDoing && timer.alarmMsgObjects && timer.alarmMsgObjects.includes('Other')">
      <OrgSelect v-model="timer.alarmMsgOtherUsers"></OrgSelect>
    </a-form-model-item>
    <a-form-model-item label="预警消息格式" v-if="timer.enableAlarmDoing && timer.alarmMsgObjects && timer.alarmMsgObjects.length">
      <a-select
        v-model="timer.alarmMsgTemplateId"
        allow-clear
        show-search
        style="width: 100%"
        :options="messageTemplateOptions"
        :filter-option="filterSelectOption"
      ></a-select>
    </a-form-model-item>

    <a-form-model-item label="到期处理" prop="enableDueDoing">
      <a-switch v-model="timer.enableDueDoing"></a-switch>
    </a-form-model-item>
    <a-form-model-item label="到期消息通知" v-if="timer.enableDueDoing">
      <a-checkbox-group v-model="timer.dueMsgObjects" :options="dueMsgObjectsOptions" />
    </a-form-model-item>
    <a-form-model-item label=" " v-if="timer.enableDueDoing && timer.dueMsgObjects && timer.dueMsgObjects.includes('Other')">
      <OrgSelect v-model="timer.dueMsgOtherUsers"></OrgSelect>
    </a-form-model-item>
    <a-form-model-item label="到期消息格式" v-if="timer.enableDueDoing && timer.dueMsgObjects && timer.dueMsgObjects.length">
      <a-select
        v-model="timer.dueMsgTemplateId"
        allow-clear
        show-search
        style="width: 100%"
        :options="messageTemplateOptions"
        :filter-option="filterSelectOption"
      ></a-select>
    </a-form-model-item>

    <a-form-model-item label="逾期处理" prop="enableOverdueDoing">
      <a-switch v-model="timer.enableOverdueDoing"></a-switch>
    </a-form-model-item>
    <a-form-model-item label="逾期消息通知" v-if="timer.enableOverdueDoing">
      <a-checkbox-group v-model="timer.overdueMsgObjects" :options="dueMsgObjectsOptions" />
    </a-form-model-item>
    <a-form-model-item label=" " v-if="timer.enableOverdueDoing && timer.overdueMsgObjects && timer.overdueMsgObjects.includes('Other')">
      <OrgSelect v-model="timer.overdueMsgOtherUsers"></OrgSelect>
    </a-form-model-item>
    <a-form-model-item label="逾期消息格式" v-if="timer.enableOverdueDoing && timer.overdueMsgObjects && timer.overdueMsgObjects.length">
      <a-select
        v-model="timer.overdueMsgTemplateId"
        allow-clear
        show-search
        style="width: 100%"
        :options="messageTemplateOptions"
        :filter-option="filterSelectOption"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="逾期时" v-if="timer.enableOverdueDoing">
      <a-select
        v-model="timer.overdueAction"
        allow-clear
        show-search
        style="width: 100%"
        :options="overdueActionOptions"
        :filter-option="filterSelectOption"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="执行groovy脚本" v-if="timer.enableOverdueDoing && timer.overdueAction == 'groovyScript'">
      <a-textarea v-model="timer.overdueGroovyScript" />
      <div class="remark" v-html="groovyEntityTimerScriptRemark"></div>
    </a-form-model-item>
    <a-form-model-item label="备注" prop="remark">
      <a-textarea v-model="timer.remark" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { groovyEntityTimerScriptRemark } from '../../designer/remarks.js';
export default {
  props: {
    entityDefinition: Object,
    timer: {
      type: Object,
      default() {
        return {
          name: '',
          id: `timer_${generateId('SF')}`,
          timerConfigId: '',
          timeLimitField: '',
          workTimePlanId: '',
          stateIds: [],
          stateCodes: [],
          timerOfStateChanged: 'stop',
          enableAlarmDoing: false,
          alarmTimeLimit: 1,
          alarmTimingMode: '3',
          alarmCount: 1,
          alarmMsgObjects: [],
          alarmMsgOtherUsers: '',
          alarmMsgTemplateId: '',
          enableDueDoing: false,
          dueMsgObjects: [],
          dueMsgOtherUsers: '',
          dueMsgTemplateId: '',
          enableOverdueDoing: false,
          overdueMsgObjects: [],
          overdueMsgOtherUsers: '',
          overdueMsgTemplateId: '',
          overdueAction: '',
          overdueGroovyScript: '',
          remark: ''
        };
      }
    }
  },
  components: { OrgSelect },
  inject: ['designer', 'getCacheData', 'filterSelectOption'],
  data() {
    return {
      groovyEntityTimerScriptRemark,
      rules: {
        name: [{ required: true, message: '名称不能为空！', trigger: 'blur' }]
      },
      timerConfigOptions: [],
      formFieldOptions: [],
      workTimePlanOptions: [],
      dyTimeLimit: false,
      timerConfig: null,
      alarmTimingModeOptions: [
        {
          value: '3',
          label: '工作日'
        },
        {
          value: '2',
          label: '工作小时'
        },
        {
          value: '1',
          label: '工作分钟'
        },
        {
          value: '86400',
          label: '天'
        },
        {
          value: '3600',
          label: '小时'
        },
        {
          value: '60',
          label: '分钟'
        }
      ],
      dueMsgObjectsOptions: [
        { label: '业务主体创建人', value: 'EntityCreator' },
        { label: '其他人员', value: 'Other' }
      ],
      messageTemplateOptions: [],
      overdueActionOptions: [{ label: '执行groovy脚本', value: 'groovyScript' }]
    };
  },
  computed: {
    stateTreeData() {
      let processDefinition = this.designer.getProcessDefinition();
      return (processDefinition.stateTree && [processDefinition.stateTree]) || [];
    }
  },
  created() {
    this.loadTimerConfigOptions();
    this.loadFormFieldOptions();
    this.loadWorkTimePlanOptions();
    this.loadMessageTemplateOptions();
    if (this.timer.timerConfigId) {
      this.onTimerConfigChange(this.timer.timerConfigId);
    }
  },
  methods: {
    loadTimerConfigOptions() {
      $axios.get(`/proxy/api/ts/timer/config/selectdata?excludedCategoryId=flowTiming`).then(({ data: result }) => {
        if (result.data) {
          this.timerConfigOptions = result.data;
        }
      });
    },
    onTimerConfigChange(value) {
      $axios.get(`/proxy/api/ts/timer/config/getById?id=${value}`).then(({ data: result }) => {
        let timerConfig = result.data;
        if (timerConfig) {
          this.dyTimeLimit = timerConfig.timeLimitType == '30' || timerConfig.timeLimitType == '40';
        }
        this.timerConfig = timerConfig;
      });
    },
    loadFormFieldOptions() {
      const _this = this;

      let entityFormUuid = _this.entityDefinition.formUuid;
      if (entityFormUuid) {
        _this.loadFormDefinition(entityFormUuid).then(formDefinition => {
          _this.formFieldOptions = _this.getFormFieldSelectData(formDefinition, false);
        });
      }
    },
    getFormFieldSelectData(formDefinition, includeUuid = false, excludeFields = []) {
      let fieldSelectData = includeUuid ? [{ value: 'uuid', label: 'UUID' }] : [];
      let fields = formDefinition.fields || {};
      // 字段
      for (let fieldName in fields) {
        let field = fields[fieldName];
        if (excludeFields.length && excludeFields.includes(field.name)) {
          continue;
        }
        fieldSelectData.push({ value: field.name, label: field.displayName });
      }
      return fieldSelectData;
    },
    loadFormDefinition(formUuid, defaultUrl) {
      const _this = this;
      return _this.getCacheData(`formDefinition_${formUuid}`, (resolve, reject) => {
        if (!formUuid) {
          resolve({});
          return;
        }
        let url = defaultUrl ? defaultUrl : `/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`;
        _this.$axios.get(url).then(({ data }) => {
          if (!data.data) {
            console.error('form definition is null', formUuid);
            resolve({});
          } else {
            let formDefinition = JSON.parse(data.data);
            resolve(formDefinition);
          }
        });
      });
    },
    loadWorkTimePlanOptions() {
      $axios.post('/proxy/api/ts/work/time/plan/getAllBySystemUnitIdsLikeName').then(({ data: result }) => {
        if (result.data) {
          this.workTimePlanOptions = result.data.map(item => ({
            label: `${item.name}(V${item.version})`,
            value: item.id
          }));
        }
      });
    },
    loadMessageTemplateOptions() {
      $axios.get('/proxy/api/workflow/definition/getMessageTemplates').then(({ data: result }) => {
        if (result.data) {
          this.messageTemplateOptions = result.data.map(item => ({
            label: item.name,
            value: item.id
          }));
        }
      });
    },
    getTimeLimitUnitName(timerConfig) {
      let timeLimitUnitName = '';
      let timeLimitUnit = timerConfig.timeLimitUnit;
      if (timeLimitUnit == '3') {
        timeLimitUnitName = '分钟';
      } else if (timeLimitUnit == '2') {
        timeLimitUnitName = '小时';
      } else {
        timeLimitUnitName = '天';
      }
      return timeLimitUnitName;
    },
    collect() {
      return this.$refs.basicForm
        .validate()
        .then(valid => {
          if (valid) {
            return this.timer;
          }
          return null;
        })
        .catch(valid => {
          console.log('valid ', valid);
          return false;
        });
    },
    onStateTreeChange(value, node, extra) {
      this.timer.stateCodes = value
        .map(nodeId => {
          let nodeData = this.getTreeNodeDataByNodeId(nodeId);
          return nodeData && nodeData.data && nodeData.data.code;
        })
        .filter(stateCode => stateCode != null);
    },
    getTreeNodeDataByNodeId(nodeId) {
      const _this = this;
      let treeData = _this.stateTreeData;
      let treeNode = null;
      let findTreeNode = nodes => {
        if (treeNode) {
          return;
        }
        nodes.forEach(node => {
          if (node.id == nodeId) {
            treeNode = node;
            return;
          }
          findTreeNode(node.children || []);
        });
      };
      findTreeNode(treeData);
      return treeNode;
    }
  }
};
</script>

<style lang="less" scoped>
.remark {
  line-height: 1.5;
  font-size: 14px;
}
</style>
