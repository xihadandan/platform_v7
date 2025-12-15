<template>
  <a-form-model
    ref="form"
    :model="formData"
    :colon="false"
    labelAlign="left"
    :label-col="{ flex: '100px' }"
    :wrapper-col="{ flex: 'auto' }"
  >
    <a-form-model-item label="选择过滤组织">
      <w-tree-select
        v-model="currentOrgIdOrBizOrgId"
        :treeData="bizOrgTreeData"
        :replaceFields="{
          children: 'bizOrgs',
          title: 'name',
          key: 'id',
          value: 'id'
        }"
        :showSearch="false"
        :treeDefaultExpandAll="true"
        @change="changeBizOrgId"
      />
    </a-form-model-item>

    <template v-if="formData.bizOrgId">
      <a-form-model-item prop="option1" label="人员过滤">
        <a-row>
          <a-col span="12" v-for="litem in bizOrgUserFilter" :key="litem.type">
            <div class="checked-div">{{ litem.label }}</div>
            <div class="checked-div" v-for="(item, index) in litem.list" :key="litem.type + index">
              <a-checkbox :checked="selectedUserOptions.includes(item.value)" :value="item" @change="changeOptions">
                {{ item.name }}
              </a-checkbox>
            </div>
          </a-col>
        </a-row>
      </a-form-model-item>
    </template>
    <template v-else-if="formData.orgId || !currentOrgIdOrBizOrgId">
      <!-- 行政组织人员过滤 -->
      <a-form-model-item prop="option1" label="人员过滤">
        <a-row>
          <a-col span="12" v-for="litem in handlerFilterList" :key="litem.type">
            <div class="checked-div">{{ litem.label }}</div>
            <div class="checked-div" v-for="(item, index) in litem.list" :key="litem.type + index">
              <a-checkbox :checked="selectedUserOptions.includes(item.value)" :value="item" @change="changeOptions">
                {{ item.name }}
              </a-checkbox>
            </div>
          </a-col>
        </a-row>
      </a-form-model-item>
      <a-form-model-item label="职等职级过滤">
        <a-form-model-item :label-col="{ flex: '120px' }">
          <template slot="label">
            <w-checkbox v-model="formData.enabledJobGrade">指定职等人员</w-checkbox>
          </template>
          <w-select
            v-model="formData.jobGrade"
            optionValueType="number"
            mode="multiple"
            :options="jobGradeOptions"
            :replaceFields="{
              title: 'jobGradeName',
              key: 'uuid',
              value: 'jobGrade'
            }"
          />
        </a-form-model-item>
        <a-form-model-item :label-col="{ flex: '120px' }">
          <template slot="label">
            <w-checkbox v-model="formData.enabledJobRank">指定职级人员</w-checkbox>
          </template>
          <w-tree-select
            v-model="formData.jobRankId"
            :treeData="jobRankTreeData"
            :treeCheckable="true"
            :replaceFields="{
              children: 'children',
              title: 'name',
              key: 'id',
              value: 'id'
            }"
          />
        </a-form-model-item>
      </a-form-model-item>
    </template>
  </a-form-model>
</template>

<script>
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { handlerFilterList, bizOrgUserFilter, userSelectType, bizOrgAssignRole } from '../designer/constant';
import WTreeSelect from '../components/w-tree-select';
import WSelect from '../components/w-select';
import WCheckbox from '../components/w-checkbox';

export default {
  name: 'UserSelectFilter',
  inject: ['designer', 'workFlowData'],
  components: {
    OrgSelect,
    WTreeSelect,
    WSelect,
    WCheckbox
  },
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    title: {
      type: String,
      default: '选择人员'
    },
    text: {
      type: String,
      default: '办理人'
    },
    value: {
      type: Array
    },
    dataSource: {
      type: Array,
      default: () => []
    },
    jobGradeOptions: {
      type: Array,
      default: () => []
    },
    jobRankTreeData: {
      type: Array,
      default: () => []
    },
    createItem: {
      type: Function
    },
    createUserOption: {
      type: Function
    }
  },
  data() {
    let typesMap = {},
      typesValueMap = {};
    userSelectType.forEach(item => {
      typesMap[item['type']] = item;
      typesValueMap[item['value']] = item;
    });

    return {
      visible: false,
      handlerFilterList,
      bizOrgUserFilter,
      bizOrgAssignRole,
      typesMap, // 以字符串为key，unit:{}
      typesValueMap, // 以type值(Number)为key，1:{}
      bizOrgTreeData: [], // 组织树
      currentOrgIdOrBizOrgId: ''
    };
  },
  computed: {
    // 默认行政组织
    defaultOrgData() {
      let orgData = {};
      if (this.designer.defaultOrgData) {
        orgData = this.designer.defaultOrgData;
      }
      return orgData;
    },
    // 选中的人员选项
    selectedUserOptions() {
      let selected = [];
      const userOptions = this.formData.userOptions;

      if (userOptions.length) {
        selected = userOptions.map(item => item.value);
      }
      return selected;
    },
    userOrgId() {
      let orgId = this.defaultOrgData.id;
      const property = this.workFlowData.property;
      if (property.useDefaultOrg === '0') {
        orgId = property.orgId;
      }
      return orgId;
    }
  },
  methods: {
    addItem() {
      this.currentOrgIdOrBizOrgId = '';
      this.getBizOrgTreeData();
      this.formData = this.createItem({
        type: this.typesMap['filter']['value']
      });
      this.visible = true;
    },
    delItem(item, index) {
      this.$emit('delete', item, index);
    },
    setItem(item, index) {
      this.getBizOrgTreeData();
      this.formData = JSON.parse(JSON.stringify(item));
      this.currentOrgIdOrBizOrgId = item.orgId || item.bizOrgId;
      this.visible = true;
    },
    saveItem() {
      this.visible = false;
      this.$emit('save', this.formData);
    },
    // 获取业务组织树
    getBizOrgTreeData() {
      let treeData = [];
      const orgId = this.userOrgId;
      const property = this.workFlowData.property;

      const getTreeNode = ({ orgId, bizOrgId, availableBizOrg }) => {
        let node = this.designer.getBizOrgTreeNode(orgId, bizOrgId);
        return node;
      };

      if (property.multiOrgs) {
        property.multiOrgs.forEach(item => {
          const node = getTreeNode(item);
          if (node) {
            treeData.push(node);
          }
        });
      } else if (orgId) {
        const node = getTreeNode(property);
        if (node) {
          treeData.push(node);
        }
      }
      this.bizOrgTreeData = treeData;
    },
    // 更改业务组织id
    changeBizOrgId(value, label, extra) {
      this.formData = this.createItem({
        type: this.typesMap['filter']['value'],
        id: this.formData.id
      });
      if (value) {
        const data = extra.triggerNode.dataRef;
        if (data.bizOrgs) {
          // 行政组织
          this.formData.orgId = value;
        } else {
          this.formData.bizOrgId = value;
        }
      }
    },
    // 选项变化事件
    changeOptions(event) {
      let item = event.target.value;

      const userOptions = this.formData.userOptions;
      const hasIndex = userOptions.findIndex(u => u.value === item.value);

      if (event.target.checked) {
        if (hasIndex == -1) {
          let option = this.createUserOption();

          option.value = item.value;
          option.argValue = item.name;

          userOptions.push(option);
        }
      } else {
        if (hasIndex > -1) {
          userOptions.splice(hasIndex, 1);
        }
      }
    },
    clearAll() {
      this.formData = this.createItem({
        type: this.typesMap['filter']['value'],
        id: this.formData.id
      });
      this.currentOrgIdOrBizOrgId = '';
    },
    cancelModal() {
      this.visible = false;
    },
    clearList() {
      this.$emit('clearList', this.typesMap['filter']['value']);
    }
  }
};
</script>
