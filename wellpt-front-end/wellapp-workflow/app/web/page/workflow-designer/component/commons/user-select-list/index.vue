<template>
  <div class="org-select-component">
    <div class="user-select-container">
      <div class="user-select-title">
        <div class="_title">{{ text }}</div>
        <div class="_operation">
          <a-button type="link" size="small" icon="plus" @click="addItem">添加</a-button>
          <a-button type="link" size="small" icon="delete" @click="clearList()">清空</a-button>
        </div>
      </div>
      <template v-for="(item, index) in dataList">
        <div class="user-select-item" v-if="item.type !== typesMap['filter']['value']" :key="index" @click="setItem(item, index)">
          <div class="user-select-item-head">
            <div class="_icon">
              <Icon :type="`pticon iconfont ${typesValueMap[item.type]['icon']}`" />
            </div>
            <div class="_label">
              {{ typesValueMap[item.type]['label'] }}
              <span v-if="item.orgName">-{{ item.orgName }}</span>
            </div>
          </div>
          <div class="user-select-item-content">
            <template v-if="item.argValue">
              <a-tag v-for="(arg, i) in item.argValue.split(';')" :key="i">
                {{ arg }}
              </a-tag>
            </template>
            <a-tag v-for="(uo, i) in item.userOptions" :key="i">
              {{ uo.argValue }}
            </a-tag>
          </div>

          <!-- <a-button type="link" icon="delete" class="_delete" size="small" @click="delItem" /> -->
          <!-- <i class="iconfont icon-luojizujian-shanchujishiqi _delete" @click="delItem" /> -->
          <template v-if="index">
            <a-button type="icon" class="icon-only _delete" @click.stop="delItem(item, index)">
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
            </a-button>
          </template>
        </div>
      </template>
    </div>
    <template v-if="types.indexOf('filter') > -1">
      <user-select-filter
        :dataSource="dataList"
        :jobGradeOptions="jobGradeOptions"
        :jobRankTreeData="jobRankTreeData"
        :createItem="createItem"
        :createUserOption="createUserOption"
        @delete="delItem"
        @save="saveItem"
        @clearList="clearList"
      />
    </template>
    <a-modal
      class="wf-users-select-modal"
      :mask="false"
      :maskClosable="false"
      :title="title"
      :visible="visible"
      :okText="okText"
      cancelText="取消"
      :width="800"
      :bodyStyle="{ padding: '12px 20px', height: '600px', 'overflow-y': 'auto' }"
      @cancel="cancelModal"
      ref="modal"
    >
      <template v-if="formData">
        <a-form-model
          ref="form"
          :model="formData"
          :colon="false"
          labelAlign="left"
          :label-col="{ flex: '100px' }"
          :wrapper-col="{ flex: 'auto' }"
        >
          <a-form-model-item prop="type" label="来源">
            <a-radio-group v-model="formData.type" size="small" @change="changeTypeValue">
              <a-radio-button v-for="item in sourceTypeOptions" :key="item.value" :value="item.value">
                {{ item.label }}
              </a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <template v-if="formData.type === sourceTypeOptions[0]['value']">
            <a-form-model-item prop="unit" label="组织机构" v-if="types.indexOf('unit') > -1">
              <org-select
                :orgVersionId="orgVersionId"
                :orgVersionIds="orgVersionIds"
                v-model="formData.value"
                v-if="orgVersionId"
                :orgType="['MyOrg', 'MyLeader', 'PublicGroup']"
                @change="changeOrg"
              />
            </a-form-model-item>

            <a-form-model-item prop="option1" label="人员选项">
              <a-row>
                <a-col span="12" v-for="litem in handlerList" :key="litem.type">
                  <div class="checked-div">{{ litem.label }}</div>
                  <div class="checked-div" v-for="(item, index) in litem.list" :key="litem.type + index">
                    <a-checkbox :checked="selectedUserOptions.includes(item.value)" :value="item" @change="changeOptions">
                      {{ item.name }}
                    </a-checkbox>
                  </div>
                </a-col>
              </a-row>
            </a-form-model-item>

            <a-form-model-item label="职等职级">
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
          <template v-if="formData.type === sourceTypeOptions[1]['value']">
            <a-form-model-item label="业务组织">
              <w-tree-select
                v-model="formData.bizOrgId"
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
              <template v-if="currentBizOrgUuid">
                <org-select
                  ref="bizOrg"
                  :orgUuid="currentBizOrgUuid"
                  v-model="formData.value"
                  :key="currentBizOrgUuid"
                  :orgType="['MyOrg', 'MyLeader', 'PublicGroup']"
                  @change="changeBizOrgValue"
                />
              </template>
            </a-form-model-item>
            <a-form-model-item prop="option2" label="人员选项">
              <a-row>
                <a-col span="12" v-for="litem in bizOrgUserOptions" :key="litem.type">
                  <div class="checked-div">{{ litem.label }}</div>
                  <div class="checked-div" v-for="(item, index) in litem.list" :key="litem.type + index">
                    <a-checkbox :checked="selectedUserOptions.includes(item.value)" :value="item" @change="changeOptions">
                      {{ item.name }}
                    </a-checkbox>
                    <template v-if="selectedUserOptions.includes(item.value) && bizOrgAssignRole.includes(item.value)">
                      <w-select
                        mode="multiple"
                        :options="bizOrgRolesOptions"
                        :replaceFields="{
                          title: 'name',
                          key: 'uuid',
                          value: 'id'
                        }"
                        @change="value => changeBizRole(value, item)"
                        style="width: 90%"
                      />
                    </template>
                  </div>
                </a-col>
              </a-row>
            </a-form-model-item>
          </template>
          <template v-if="formData.type === sourceTypeOptions[2]['value']">
            <a-form-model-item prop="field" label="表单字段" v-if="types.indexOf('field') > -1">
              <dyform-fields-tree-select
                v-model="formData.value"
                :formData="formData"
                formDataFieldName="argValue"
                @change="changeFields"
              />
            </a-form-model-item>
          </template>
          <template v-if="formData.type === sourceTypeOptions[3]['value']">
            <a-form-model-item prop="task" label="环节" v-if="types.indexOf('task') > -1">
              <node-task-select
                v-model="formData.value"
                :formData="formData"
                formDataFieldName="argValue"
                mode="multiple"
                @change="changeTask"
              />
            </a-form-model-item>
          </template>
          <template v-if="formData.type === sourceTypeOptions[4]['value']">
            <a-form-model-item prop="custom" label="自定义" v-if="types.indexOf('custom') > -1">
              <user-select-custom
                v-model="formData.value"
                :data="[formData]"
                :orgVersionId="orgVersionId"
                :orgVersionIds="orgVersionIds"
                @change="changCustom"
              />
            </a-form-model-item>
          </template>
        </a-form-model>
      </template>
      <template slot="footer">
        <a-button type="primary" @click="saveItem()">{{ okText }}</a-button>
        <a-button type="danger" @click="clearAll">清空</a-button>
        <a-button @click="cancelModal">取消</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import { fetchJobGradeList, fetchJobRankTree, fetchBizOrgRolesByBizOrgId } from '../api';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import {
  handlerList,
  handlerFilterList,
  userSelectType,
  bizOrgUserOptions,
  availableBizOrgOptions,
  bizOrgAssignRole
} from '../designer/constant';
import DyformFieldsTreeSelect from './dyform-fields-tree-select.vue';
import NodeTaskSelect from './node-task-select';
import UserSelectCustom from './user-select-custom.vue';
import WTreeSelect from '../components/w-tree-select';
import WSelect from '../components/w-select';
import WCheckbox from '../components/w-checkbox';
import NodeTaskUser from '../designer/NodeTaskUser';
import UserSelectFilter from './user-select-filter.vue';

export default {
  name: 'UserSelectList',
  inject: ['designer', 'workFlowData'],
  components: {
    OrgSelect,
    DyformFieldsTreeSelect,
    NodeTaskSelect,
    UserSelectCustom,
    WTreeSelect,
    WSelect,
    WCheckbox,
    UserSelectFilter
  },
  props: {
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
    types: {
      // unit组织1/field字段2/option选项8/direction/task环节
      type: String,
      default: 'unit'
    },
    setKey: {
      type: String,
      default: ''
    },
    cellData: {
      type: Object,
      default: () => {}
    },
    separator: {
      // 分隔符
      type: String,
      default: ';'
    },
    showOpenIcon: {
      type: Boolean,
      default: true
    },
    enableCache: {
      type: Boolean,
      default: false
    },
    okText: {
      type: String,
      default: '确定'
    },
    textLength: {
      type: [Boolean, Number],
      default: false
    }
  },
  data() {
    let typesMap = {},
      typesValueMap = {};
    userSelectType.forEach(item => {
      typesMap[item['type']] = item;
      typesValueMap[item['value']] = item;
    });
    const sourceTypeOptions = userSelectType.slice(0, userSelectType.length - 1);
    const currentTypeValue = sourceTypeOptions[0]['value'];

    return {
      visible: false,
      formData: undefined,
      dataList: [],
      handlerList,
      handlerFilterList,
      sourceTypeOptions, // 来源类型选项
      typesMap, // 以字符串为key，unit:{}
      typesValueMap, // 以type值(Number)为key，1:{}
      bizOrgUserOptions,
      bizOrgTreeData: [], // 业务组织树，行政组织不允许选择
      availableBizOrgOptions,
      tempData: {}, // {unit:{groupId:'',type:1}} 临时数据
      currentTypeValue, // 当前来源值 Number类型
      currentBizOrgVersionId: '',
      currentBizOrgUuid: '', // 当前业务组织uuid
      jobGradeOptions: [], // 职等选项
      jobRankTreeData: [], // 职级树
      bizOrgRolesOptions: [], // 业务组织角色
      bizOrgAssignRole,
      createItem: (args = {}) => {
        let item = new NodeTaskUser(args);
        item.id = args.id || generateId();
        return item;
      },
      createUserOption: () => {
        return {
          value: '', // '选项值',
          argValue: '', // '选项名称',
          bizRoleId: '' // '业务组织角色ID，多个以分号隔开',
        };
      }
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
    },
    orgVersionIds() {
      return this.workFlowData.property.orgVersionIds || [];
    },
    orgVersionId() {
      return this.workFlowData.property.orgVersionId;
    }
  },
  watch: {
    value: {
      handler(value) {
        this.setDataList(value);
      }
    },
    'formData.bizOrgId': {
      handler(bizOrgId) {
        this.getBizOrgRoles(bizOrgId);
      }
    }
  },
  created() {
    if (!this.value.length) {
      let item = this.createItem({ orgId: this.userOrgId });
      this.dataList = [item];
      this.emitValue();
    } else {
      this.setDataList(this.value);
    }
    this.getJobGradeOtions();
    this.getJobRankTree();
  },
  methods: {
    // 获取职等选项
    getJobGradeOtions() {
      fetchJobGradeList().then(res => {
        this.jobGradeOptions = res;
      });
    },
    // 获取职级树
    getJobRankTree() {
      fetchJobRankTree().then(data => {
        data.forEach(item => {
          if (item.parent) {
            item.selectable = false;
          }
          if (item.children) {
            item.children.forEach(citem => {
              if (citem.parent) {
                citem.selectable = false;
              }
            });
          }
        });

        this.jobRankTreeData = data;
      });
    },
    setDataList(list) {
      const value = JSON.parse(JSON.stringify(list));
      let data = [];
      for (let index = 0; index < value.length; index++) {
        let item = value[index];
        item.id = generateId();
        let info;
        if (item.orgId) {
          info = this.designer.getOrgInfoById(item.orgId);
        } else if (item.bizOrgId) {
          info = this.designer.getBizOrgById(item.bizOrgId);
        }
        if (info) {
          item.orgName = info.name;
        }

        data.push(item);
      }
      this.dataList = data;
    },
    // 创建缓存数据
    createTempData({ id } = {}) {
      let data = {};
      if (!id) {
        id = generateId();
      }
      this.types.split('/').forEach(type => {
        const typeValue = this.typesMap[type]['value'];
        let item = this.createItem({
          type: typeValue,
          id
        });
        data[type] = item;
      });
      return data;
    },
    addItem() {
      this.currentTypeValue = this.sourceTypeOptions[0]['value'];
      this.getBizOrgTreeData();
      this.tempData = this.createTempData();
      const type = this.typesValueMap[this.currentTypeValue]['type'];
      this.formData = this.tempData[type];
      this.visible = true;
    },
    delItem(item, index) {
      const hasIndex = this.dataList.findIndex(d => d.id === item.id);
      if (hasIndex > -1) {
        this.dataList.splice(hasIndex, 1);
        this.emitValue();
      }
    },
    setItem(item, index) {
      const record = JSON.parse(JSON.stringify(item));
      this.currentTypeValue = record.type;
      this.getBizOrgTreeData();
      this.tempData = this.createTempData({
        id: record.id
      });
      const type = this.typesValueMap[this.currentTypeValue]['type'];
      this.formData = record;

      this.tempData[type] = record;
      const info = this.designer.getBizOrgById(record.bizOrgId);
      if (info) {
        this.currentBizOrgUuid = info.uuid;
      }
      this.visible = true;
    },
    saveItem(item) {
      if (!item) {
        item = this.tempData[this.typesValueMap[this.formData.type]['type']];
      }
      const hasIndex = this.dataList.findIndex(d => d.id === item.id);
      if (hasIndex === -1) {
        this.dataList.push(item);
      } else {
        this.dataList.splice(hasIndex, 1, item);
      }
      this.emitValue();
      this.visible = false;
    },
    emitValue() {
      let value = [];
      this.dataList.forEach(i => {
        let { id, orgName, ...item } = i; // 删除多个属性id、orgName
        value.push(item);
      });
      this.$emit('input', value);
    },
    // 获取业务组织树
    getBizOrgTreeData() {
      let treeData = [];
      const orgId = this.userOrgId;
      const property = this.workFlowData.property;

      const getTreeNode = ({ orgId, availableBizOrg, bizOrgId }) => {
        let node;
        if (availableBizOrg === this.availableBizOrgOptions[1]['value']) {
          node = this.designer.getBizOrgTreeNode(orgId, null, false);
        } else if (availableBizOrg === this.availableBizOrgOptions[2]['value']) {
          if (bizOrgId) {
            node = this.designer.getBizOrgTreeNode(orgId, bizOrgId, false);
          }
        }
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
    // 获取业务组织角色
    getBizOrgRoles(bizOrgId) {
      if (!bizOrgId) {
        this.bizOrgRolesOptions = [];
        return;
      }
      fetchBizOrgRolesByBizOrgId({ bizOrgId }).then(res => {
        this.bizOrgRolesOptions = res;
      });
    },
    // 更改业务组织id
    changeBizOrgId(value, label, extra) {
      if (!value) {
        this.currentBizOrgUuid = '';
        return;
      }
      if (this.currentBizOrgUuid !== extra.triggerNode.dataRef.uuid) {
        this.currentBizOrgUuid = extra.triggerNode.dataRef.uuid;
        this.$nextTick(() => {
          this.$refs.bizOrg.currentBizOrgUuid = this.currentBizOrgUuid;
        });
      }
    },
    // 选项变化事件
    changeOptions(event) {
      let item = event.target.value;

      const typeValue = this.formData.type;
      const type = this.typesValueMap[typeValue]['type'];
      const userOptions = this.tempData[type]['userOptions'];

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
    // 更改角色
    changeBizRole(value, option) {
      this.formData.userOptions.forEach(item => {
        if (item.value === option.value) {
          item.bizRoleId = value;
        }
      });
    },
    // 更改来源值
    changeTypeValue(event) {
      const typeValue = event.target.value;

      if (this.currentTypeValue !== typeValue) {
        const type = this.typesValueMap[typeValue]['type'];
        this.formData = this.tempData[type];
        this.formData.type = typeValue;
        this.currentTypeValue = typeValue;
      }
    },
    clearAll() {
      this.formData = this.createItem({
        type: this.currentTypeValue,
        id: this.formData.id
      });

      const type = this.typesValueMap[this.currentTypeValue]['type'];
      this.tempData[type] = this.formData;
    },
    cancelModal() {
      this.visible = false;
      this.$emit('cancel');
    },
    // 更改业务组织下的办理人
    changeBizOrgValue({ value, label, nodes }) {
      this.formData.argValue = label;
    },
    // 组织机构变化事件
    changeOrg({ value, label, nodes }) {
      let names = nodes.map(item => item.title);
      this.formData.argValue = names.join(';');
    },
    // 更改自定义
    changCustom({ value, label }) {
      this.formData.value = value;
      this.formData.argValue = label;
    },
    clearList(type) {
      if (type) {
        this.dataList = this.dataList.filter(item => item.type !== type);
      } else {
        this.dataList.splice(1, this.dataList.length - 1);
      }

      this.emitValue();
    },
    // 表单字段
    changeFields({ value, label, extra }) {},
    // 环节字段
    changeTask(value, option) {}
  }
};
</script>
<style lang="less">
.wf-users-select-modal {
  .ant-row {
    margin-bottom: 4px;
    display: flex;
    .ant-form-item-control-wrapper {
      flex: 1 1 auto;
    }

    .checked-div {
      line-height: var(--w-line-height);
      margin-top: 10px;
      &:first-child {
        color: var(--w-gray-color-9);
      }
    }
  }
  .org-select-component {
    padding-top: 4px;
  }
}
.org-select-component {
  .user-select-input {
    .ant-tag {
      white-space: normal;
    }
  }
}
</style>
