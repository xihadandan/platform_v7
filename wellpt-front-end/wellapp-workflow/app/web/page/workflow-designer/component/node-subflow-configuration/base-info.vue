<template>
  <!-- 子流程属性-子流程 -->
  <div>
    <a-form-model-item class="form-item-vertical" prop="name" label="名称">
      <a-input v-model="formData.name" @blur="changeName">
        <template slot="addonAfter">
          <w-i18n-input :target="formData" :code="formData.id + '.taskName'" v-model="formData.name" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" prop="id" label="ID">
      <template v-if="canEditId">
        <a-input v-model="formData.id" @change="changeId" @blur="blurId" />
      </template>
      <template v-else>
        {{ formData.id }}
      </template>
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" prop="businessType" label="分发组织">
      <use-org-tree-select v-model="formData.businessType" @change="changeBusinessType" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="子流程">
      <node-subflow-new-flows :dataSource="formData.newFlows" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="前置关系">
      <relation-table :dataSource="formData.relations" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="跟进人员">
      <user-select-list
        v-model="formData.subTaskMonitors"
        types="unit/bizOrg/field/task/custom/filter"
        title="选择跟进人员"
        text="跟进人员"
      />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="跟踪汇总环节">
      <node-task-select v-model="formData.traceTask" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">
        <label>子流程查看主流程设置</label>
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">设置子流程对主流程详情的查阅权限</div>
          <a-icon type="exclamation-circle" />
        </a-tooltip>
      </template>
      <a-radio-group v-model="formData.childLookParent" size="small">
        <a-radio v-for="item in childLookParentConfig" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio>
      </a-radio-group>
      <div>
        <w-checkbox v-model="formData.parentSetChild">允许主流程更改查看权限</w-checkbox>
      </div>
    </a-form-model-item>
  </div>
</template>

<script>
import { debounce } from 'lodash';
import { childLookParentConfig, orgGranularityOptions, bizOrgGranularityOptions } from '../designer/constant';
import WCheckbox from '../components/w-checkbox';
import UseOrgTreeSelect from '../commons/use-org-tree-select.vue';
import NodeTaskSelect from '../commons/node-task-select';
import NodeSubflowNewFlows from './new-flows.vue';
import UserSelectList from '../commons/user-select-list.vue';
import RelationTable from './relation-table.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
const orgGranularityValues = orgGranularityOptions.map(g => g.value);
const bizOrgGranularityValues = bizOrgGranularityOptions.map(g => g.value);

export default {
  name: 'NodeSubflowBaseInfo',
  inject: ['designer', 'workFlowData', 'graph'],
  props: {
    graphItem: {
      type: Object,
      default: () => {}
    },
    formData: {
      type: Object,
      default: () => {}
    },
    rules: {
      type: Object,
      default: () => {}
    }
  },
  provide() {
    return {
      unChangedGranularityIds: this.unChangedGranularityIds
    };
  },
  components: {
    WCheckbox,
    UseOrgTreeSelect,
    NodeTaskSelect,
    NodeSubflowNewFlows,
    UserSelectList,
    RelationTable,
    WI18nInput
  },
  data() {
    const oldNodeDataId = this.formData.id;
    const businessType = this.formData.businessType;
    return {
      oldNodeDataId,
      childLookParentConfig,
      tasksData: [],
      canEditId: false, // id是否能编辑
      selectedCellId: null,
      businessType,
      unChangedGranularityIds: {
        value: []
      } // 未改变的粒度的子流程
    };
  },
  computed: {
    orgVersionId() {
      return this.workFlowData.property.orgVersionId || '';
    }
  },
  created() {
    this.setCanEditId();
    // 排除当前节点id, 不能动态取
    this.tasksData = this.graph.instance.subflowsData.filter(item => item.id !== this.formData.id);
    this.setSelectedCellId();
  },
  methods: {
    changeBusinessType(value, label, extra) {
      let preValue = '';
      if (extra.preValue && extra.preValue.length) {
        preValue = extra.preValue[0]['value'];
      } else {
        preValue = this.businessType;
      }

      let businessTypes = this.designer.subflowChangedBusinessType;
      const hasIndex = businessTypes.findIndex(b => b === this.formData.id);
      if (hasIndex > -1) {
        businessTypes.splice(hasIndex, 1);
      }
      this.unChangedGranularityIds.value = [];

      if (this.formData.newFlows.length) {
        let isChangeType = false;
        const oldIsBizOrg = preValue.indexOf('BO_') === 0; // 是否业务组织
        const newIsBizOrg = value.indexOf('BO_') === 0;
        if (oldIsBizOrg) {
          if (!newIsBizOrg) {
            isChangeType = true;
          }
        } else {
          // if (!extra.triggerNode.dataRef.bizOrgs) {}
          if (newIsBizOrg) {
            isChangeType = true;
          }
        }

        let ids = [];
        if (newIsBizOrg) {
          this.formData.newFlows.map(flow => {
            if (orgGranularityValues.includes(flow.granularity)) {
              ids.push(flow.id);
            }
          });
        } else {
          this.formData.newFlows.map(flow => {
            if (bizOrgGranularityValues.includes(flow.granularity)) {
              ids.push(flow.id);
            }
          });
        }
        this.unChangedGranularityIds.value = ids;
        if (ids.length) {
          businessTypes.push(this.formData.id);
        }
      }
    },
    setSelectedCellId() {
      this.selectedCellId = this.graph.instance.selectedId;
    },
    // 更改子流程名
    changeName: function (event) {
      const value = event.target.value;
      if (this.graph.instance) {
        this.graph.instance.setEdgesLablesByName(value, this.selectedCellId);
      }
    },
    changeNameOld: debounce(function (event) {
      let error = false;
      const value = event.target.value;
      if (!value) {
        error = true;
      }
      if (value && !error) {
        const taskData = this.tasksData.find(item => {
          return item.name === value;
        });
        if (taskData) {
          error = true;
        }
      }
      if (error) {
        this.formData.name = '';
        this.$message.error(this.rules['name']['msg']); // 不能和message相同的key
      }
    }, 300),
    // 更改环节id
    changeId: debounce(function (event) {
      let error = false;
      const value = event.target.value;
      if (!value) {
        error = true;
      }
      if (value && !error) {
        const taskData = this.tasksData.find(item => {
          return item.id === value;
        });
        if (taskData) {
          error = true;
        }
      }
      if (error) {
        this.formData.id = '';
        this.$message.error(this.rules['id']['msg']);
      }
    }, 300),
    blurId(event) {
      const value = event.target.value;
      if (value !== this.oldNodeDataId) {
        // 更改环节id后修改流向toID、fromID
        this.graphItem.updateNodeDataId({
          nodeDataId: value,
          oldNodeDataId: this.oldNodeDataId
        });
        this.oldNodeDataId = value;
      }
    },
    // 设置id是否能编辑
    setCanEditId() {
      const cell = this.graph.instance.getSelectedCell();
      const hasIndex = this.graph.instance.newSubflowsUnEdited.findIndex(t => t.id === cell.id);
      if (hasIndex > -1) {
        this.graph.instance.newSubflowsUnEdited.splice(hasIndex, 1);
        this.graph.instance.newSubflowsEdited.push(cell);
        this.canEditId = true;
      }
    }
  }
};
</script>
