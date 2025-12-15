<template>
  <div class="org-select-component">
    <div class="user-select-container">
      <div class="user-select-title">
        <div class="_title">{{ text }}</div>
        <div class="_operation">
          <a-button type="link" size="small" icon="plus" @click="addItem">添加</a-button>
          <a-button type="link" size="small" @click="clearList()">
            <Icon type="pticon iconfont icon-ptkj-shanchu" />
            <span>清空</span>
          </a-button>
        </div>
      </div>
      <template v-for="(item, index) in dataList">
        <div class="user-select-item" v-if="item.type !== typesMap['filter']['value']" :key="index" @click="setItem(item, index)">
          <div class="user-select-item-head" v-if="item.type === typesMap['filter']['value']">
            <template v-if="item.orgId">
              <div class="_icon">
                <Icon :type="`pticon iconfont ${typesMap['unit']['icon']}`" />
              </div>
              <div class="_label">
                {{ typesMap['unit']['label'] }}
                <span v-if="item.orgName">-{{ item.orgName }}</span>
              </div>
            </template>
            <template v-else-if="item.bizOrgId">
              <div class="_icon">
                <Icon :type="`pticon iconfont ${typesMap['bizOrg']['icon']}`" />
              </div>
              <div class="_label">
                {{ typesMap['bizOrg']['label'] }}
                <span v-if="item.orgName">-{{ item.orgName }}</span>
              </div>
            </template>
          </div>
          <div class="user-select-item-head" v-else>
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
              <a-tag v-for="(arg, i) in item.argValue.split(';')" :key="`arg_${i}`">
                {{ arg }}
              </a-tag>
            </template>
            <a-tag v-for="(uo, i) in item.userOptions" :key="`uo_${i}`">
              {{ uo.argValue }}
            </a-tag>
            <template v-if="item.bizOrgId && item.bizRoleIds">
              <a-tag v-for="(role, i) in item.bizRoleIds" :key="`role_id_${i}`">
                {{ bizOrgIdRoleMap[item.bizOrgId] ? bizOrgIdRoleMap[item.bizOrgId][role]['name'] : '' }}
              </a-tag>
            </template>
            <template v-if="item.enabledJobGrade === '1' && item.jobGrade">
              <a-tag v-for="(grade, i) in item.jobGrade.split(';')" :key="`grade_${i}`">
                {{ jobGradeMap[grade] ? jobGradeMap[grade]['jobGradeName'] : '' }}
              </a-tag>
            </template>
            <template v-if="item.enabledJobRank === '1' && item.jobRankId">
              <a-tag v-for="(rank, i) in item.jobRankId.split(';')" :key="`rank_${i}`">
                {{ jobRankMap[rank] ? jobRankMap[rank]['name'] : '' }}
              </a-tag>
            </template>
          </div>

          <!-- <a-button type="link" icon="delete" class="_delete" size="small" @click="delItem" /> -->
          <!-- <i class="iconfont icon-luojizujian-shanchujishiqi _delete" @click="delItem" /> -->
          <template v-if="index">
            <a-button type="link" class="icon-only _delete" @click.stop="delItem(item, index)">
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
            </a-button>
          </template>
        </div>
      </template>
    </div>

    <user-select-filter
      v-if="types.indexOf('filter') !== -1"
      ref="userSelectFilter"
      :text="text"
      :dataSource="dataList"
      :typesMap="typesMap"
      :jobGradeOptions="jobGradeOptions"
      :jobRankTreeData="jobRankTreeData"
      :jobGradeMap="jobGradeMap"
      :jobRankMap="jobRankMap"
      :bizOrgIdRoleMap="bizOrgIdRoleMap"
      :createItem="createItem"
      :createUserOption="createUserOption"
      @delete="delItem"
      @save="saveItem"
      @clearList="clearList"
    />

    <a-modal
      class="wf-users-select-modal"
      :mask="false"
      :maskClosable="false"
      :title="title"
      :visible="visible"
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
          <a-form-model-item label="来源">
            <a-radio-group v-model="formData.type" size="small" button-style="solid" @change="changeTypeValue">
              <a-radio-button v-for="item in sourceTypeOptions" :key="item.value" :value="item.value">
                {{ item.label }}
              </a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <div v-if="sourceTypeOptions[0] && formData.type === sourceTypeOptions[0]['value']">
            <a-form-model-item
              prop="orgId"
              label="组织机构"
              :rules="{
                required: true,
                message: '请选择组织'
              }"
            >
              <use-org-select v-model="formData.orgId" :allowClear="false" @change="changeOrgId" />
            </a-form-model-item>
            <a-form-model-item v-if="currentOrgUuid" prop="value">
              <template slot="label">{{ text }}</template>
              <org-select
                :orgUuid="currentOrgUuid"
                :orgIdOptions="{ xxx: [] }"
                v-model="formData.value"
                :key="currentOrgUuid"
                :orgType="['MyOrg', 'MyLeader', 'PublicGroup']"
                @change="changeOrgValue"
              />
            </a-form-model-item>
            <a-form-model-item label="人员选项">
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
            <template v-if="designer.flowDefinition.enabledJobDuty">
              <a-form-model-item label="职等职级">
                <a-form-model-item
                  :label-col="{ flex: '120px' }"
                  ref="jobGradeFormItem"
                  prop="jobGrade"
                  :rules="{
                    trigger: 'change',
                    validator: (rule, value, callback) => {
                      formData.enabledJobGrade === '1' ? (value ? callback() : callback('请选择职等人员')) : callback();
                    }
                  }"
                >
                  <template slot="label">
                    <w-checkbox v-model="formData.enabledJobGrade" @change="changeEnabledJobGrade">指定职等人员</w-checkbox>
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
                <a-form-model-item
                  :label-col="{ flex: '120px' }"
                  ref="jobRankIdFormItem"
                  prop="jobRankId"
                  :rules="{
                    trigger: 'change',
                    validator: (rule, value, callback) => {
                      formData.enabledJobRank === '1' ? (value ? callback() : callback('请选择职级人员')) : callback();
                    }
                  }"
                >
                  <template slot="label">
                    <w-checkbox v-model="formData.enabledJobRank" @change="changeEnabledJobRank">指定职级人员</w-checkbox>
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
          </div>
          <div v-if="sourceTypeOptions[1] && formData.type === sourceTypeOptions[1]['value']">
            <a-form-model-item
              prop="bizOrgId"
              label="业务组织"
              :rules="{
                required: true,
                message: '请选择组织'
              }"
            >
              <use-org-tree-select v-model="formData.bizOrgId" :parentSelect="false" @change="changeBizOrgId" />
            </a-form-model-item>
            <a-form-model-item v-if="currentBizOrgUuid" prop="value">
              <template slot="label">{{ text }}</template>
              <org-select
                ref="bizOrg"
                :orgUuid="currentBizOrgUuid"
                v-model="formData.value"
                :key="currentBizOrgUuid"
                :orgType="['MyOrg', 'PublicGroup']"
                @change="changeBizOrgValue"
              />
            </a-form-model-item>
            <a-form-model-item label="业务人员选项">
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
                        v-model="assignRoleMap[item.value]"
                        :options="
                          bizOrgAssignRoleDimensionElement.includes(item.value)
                            ? bizOrgRolesDimensionElementOptions
                            : bizOrgRolesOrgElementOptions
                        "
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
          </div>
          <template v-if="sourceTypeOptions[2] && formData.type === sourceTypeOptions[2]['value']">
            <a-form-model-item label="表单字段">
              <dyform-fields-tree-select
                v-model="formData.value"
                :formData="formData"
                prop="value"
                formDataFieldName="argValue"
                @change="changeFields"
              />
            </a-form-model-item>
          </template>
          <template v-if="sourceTypeOptions[3] && formData.type === sourceTypeOptions[3]['value']">
            <a-form-model-item label="环节">
              <node-task-select
                v-model="formData.value"
                :formData="formData"
                formDataFieldName="argValue"
                mode="multiple"
                @change="changeTask"
              />
            </a-form-model-item>
          </template>
          <template v-if="sourceTypeOptions[4] && formData.type === sourceTypeOptions[4]['value']">
            <a-form-model-item label="自定义">
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
        <a-button type="danger" v-if="showClearAll" @click="clearAll">清空</a-button>
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
  bizOrgAssignRole,
  bizOrgAssignRoleDimensionElement,
  userSelectItemValidator
} from '../designer/constant';
import DyformFieldsTreeSelect from './dyform-fields-tree-select.vue';
import NodeTaskSelect from './node-task-select';
import UserSelectCustom from './user-select-custom.vue';
import WTreeSelect from '../components/w-tree-select';
import WSelect from '../components/w-select';
import WCheckbox from '../components/w-checkbox';
import NodeTaskUser from '../designer/NodeTaskUser';
import UseOrgTreeSelect from '../commons/use-org-tree-select.vue';
import UserSelectFilter from './user-select-filter.vue';
import UseOrgSelect from '../commons/use-org-select.vue';
import mixins from '../mixins';

export default {
  name: 'UserSelectList',
  inject: ['designer', 'workFlowData'],
  mixins: [mixins],
  components: {
    OrgSelect,
    DyformFieldsTreeSelect,
    NodeTaskSelect,
    UserSelectCustom,
    WTreeSelect,
    WSelect,
    WCheckbox,
    UseOrgTreeSelect,
    UserSelectFilter,
    UseOrgSelect
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
      // default: 'unit'
      default: 'unit/bizOrg/field/task/custom/filter'
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
    const rules = {
      orgId: { required: true, message: '请选择组织' },
      bizOrgId: { required: true, message: '请选择组织' }
    };
    let sourceTypeOptions = [];
    this.types.split('/').forEach(type => {
      if (typesMap[type] && type !== 'filter') {
        sourceTypeOptions.push(typesMap[type]);
      }
    });
    // let sourceTypeOptions = userSelectType.slice(0, userSelectType.length - 1);

    const currentTypeValue = sourceTypeOptions[0] && sourceTypeOptions[0]['value'];
    const assignRoleMap = this.createAssignRoleMap();
    return {
      visible: false,
      formData: undefined,
      rules,
      dataList: [],
      handlerList,
      handlerFilterList,
      sourceTypeOptions, // 来源类型选项
      typesMap, // 以字符串为key，unit:{}
      typesValueMap, // 以type值(Number)为key，1:{}
      bizOrgUserOptions,
      availableBizOrgOptions,
      tempData: {}, // {unit:{groupId:'',type:1}} 临时数据
      currentTypeValue, // 当前来源值 Number类型
      currentOrgUuid: '',
      currentOrgVersionId: '',
      currentBizOrgUuid: '', // 当前业务组织uuid
      jobGradeOptions: [], // 职等选项
      jobRankTreeData: [], // 职级树
      jobGradeMap: {},
      jobRankMap: {},
      bizOrgRolesOptions: [], // 业务组织角色
      bizOrgAssignRole, // 需要指定角色选项
      bizOrgAssignRoleDimensionElement,
      assignRoleMap,
      createItem: (args = {}) => {
        let item = new NodeTaskUser(args);
        item.groupId = args.groupId || generateId();
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
    showClearAll() {
      let show = true;
      if (this.formData) {
        const hasIndex = this.dataList.findIndex(item => item.groupId === this.formData.groupId);
        if (hasIndex === 0) {
          show = false;
        }
      }
      return show;
    },
    // 选中的人员选项
    selectedUserOptions() {
      let selected = [];
      const userOptions = this.formData.userOptions;

      if (userOptions && userOptions.length) {
        selected = userOptions.map(item => item.value);
      }
      return selected;
    },
    // 应用于“业务维度节点”的角色
    bizOrgRolesDimensionElementOptions() {
      return this.bizOrgRolesOptions.filter(item => item.applyTo.indexOf('BIZ_DIMENSION_ELEMENT') !== -1);
    },
    // 应用于“业务组织节点”的角色
    bizOrgRolesOrgElementOptions() {
      return this.bizOrgRolesOptions.filter(item => item.applyTo.indexOf('ORG_ELEMENT') !== -1);
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
    this.getJobGradeOtions();
    this.getJobRankTree();
    if (!this.value.length) {
      let item = this.createItem({ orgId: this.useOrgId });
      this.dataList = [item];
    } else {
      this.setDataList(this.value);
    }
    this.emitValue();
  },
  methods: {
    // 创建指定角色
    createAssignRoleMap() {
      let assignRoleMap = {};
      bizOrgAssignRole.forEach(r => {
        assignRoleMap[r] = '';
      });
      return assignRoleMap;
    },
    // 获取职等选项
    getJobGradeOtions() {
      fetchJobGradeList().then(res => {
        let jobGradeMap = {};
        this.jobGradeOptions = res.map(item => {
          if (!item.jobGradeName) {
            item.jobGradeName = item.jobGrade + '';
          }
          jobGradeMap[item.jobGrade] = item;
          return item;
        });
        this.jobGradeMap = jobGradeMap;
      });
    },
    // 获取职级树
    getJobRankTree() {
      fetchJobRankTree().then(data => {
        let jobRankMap = {};
        data.forEach(item => {
          jobRankMap[item.id] = item;
          if (item.parent) {
            item.selectable = false;
            // item.disableCheckbox = true;
            // item.disabled = true;
          }
          if (item.children) {
            item.children.forEach(citem => {
              jobRankMap[citem.id] = citem;
              if (citem.parent) {
                citem.selectable = false;
              }
            });
          }
        });

        this.jobRankTreeData = data;
        this.jobRankMap = jobRankMap;
      });
    },
    userSelectValidator(rule, value, callback) {
      let valid = true;
      if (this.formData.type === '1') {
        if (this.formData.userOptions.length) {
          callback();
        }
      }
      if (valid) {
        callback();
      } else {
        callback('不能为空');
      }
    },
    changeEnabledJobGrade() {
      if (this.formData.enabledJobGrade === '1') {
        this.$refs.jobGradeFormItem.onFieldChange();
      } else {
        this.$refs.jobGradeFormItem.clearValidate();
      }
    },
    changeEnabledJobRank() {
      if (this.formData.enabledJobRank === '1') {
        this.$refs.jobRankIdFormItem.onFieldChange();
      } else {
        this.$refs.jobRankIdFormItem.clearValidate();
      }
    },
    changeOrgId(value, option) {
      this.tempData = this.createTempData({
        groupId: this.formData.groupId
      });
      const type = this.typesValueMap[this.currentTypeValue]['type'];
      this.formData = this.tempData[type];

      this.currentOrgUuid = '';
      this.formData.value = '';
      this.formData.orgId = value;

      if (value) {
        const info = this.designer.getOrgInfoById(value);
        if (info) {
          this.currentOrgUuid = info.uuid;
        }
      }
    },
    changeOrgValue({ value, label, nodes }) {
      this.formData.argValue = label;
      this.formData.valuePath = nodes.map(node => node.keyPath).join(';');
    },
    // 更改业务组织id
    changeBizOrgId(value, label, extra) {
      this.assignRoleMap = this.createAssignRoleMap();
      this.tempData = this.createTempData({
        groupId: this.formData.groupId
      });
      const type = this.typesValueMap[this.currentTypeValue]['type'];
      this.formData = this.tempData[type];

      this.currentBizOrgUuid = undefined;
      this.formData.value = null;

      if (value) {
        this.formData.bizOrgId = value;
        this.currentBizOrgUuid = extra.triggerNode.dataRef.uuid;
        this.$nextTick(() => {
          this.$refs.bizOrg.currentBizOrgUuid = extra.triggerNode.dataRef.uuid;
          this.$refs.bizOrg.orgSelectTreeLoading = false;
        });
      }
    },
    // 更改业务组织下的办理人
    changeBizOrgValue({ value, label, nodes }) {
      this.formData.argValue = label;
      if (value === '') {
        this.formData.value = null;
      }
    },
    setDataList(list) {
      const value = JSON.parse(JSON.stringify(list));
      let data = [];
      for (let index = 0; index < value.length; index++) {
        let item = value[index];
        if (!item.groupId) {
          // groupId分组ID没值，会按旧版配置解析
          item.groupId = generateId();
          item.userOptions = [];

          const userItem = this.createItem({
            type: item.type
          });
          item = { ...userItem, ...item };
          let findItem;
          for (let index = 0; index < this.handlerList.length; index++) {
            // 旧数据人员选项type = 8 要修改成1，“人员过滤”新旧数据都是8
            findItem = this.handlerList[index]['list'].find(option => option.value === item.value);
            if (findItem) {
              break;
            }
          }
          if (findItem) {
            item.value = '';
            item.argValue = '';
            item.type = 1;
            let option = this.createUserOption();
            option.value = findItem.value;
            option.argValue = findItem.name;
            item.userOptions.push(option);
          }

          if (item.type === this.sourceTypeOptions[0]['value'] || item.type === this.typesMap['filter']['value']) {
            if (!item.orgId) {
              item.orgId = this.useOrgId;
            }
          }
        }

        if (item.argValue && item.argValue.indexOf('|')) {
          // 兼容旧数据
          const argValueArr = item.argValue.split('|');
          item.argValue = argValueArr[0];
        }
        let info;
        if (item.orgId) {
          info = this.designer.getOrgInfoById(item.orgId);
        } else if (item.bizOrgId) {
          info = this.designer.getBizOrgById(item.bizOrgId);
          let bizRoleIds = [];
          item.userOptions.forEach(u => {
            if (u.bizRoleId) {
              bizRoleIds = bizRoleIds.concat(u.bizRoleId.split(';'));
            }
          });
          item.bizRoleIds = bizRoleIds;
        }
        if (info) {
          item.orgName = info.name;
        }

        data.push(item);
      }
      this.dataList = data;
    },
    // 创建缓存数据
    createTempData({ groupId } = {}) {
      let data = {};
      if (!groupId) {
        groupId = generateId();
      }
      this.types.split('/').forEach(type => {
        const typeValue = this.typesMap[type]['value'];
        let item = this.createItem({
          type: typeValue,
          groupId
        });
        if (type === this.sourceTypeOptions[0]['type']) {
          item.orgId = this.useOrgId;
        }
        data[type] = item;
      });
      return data;
    },
    addItem() {
      this.currentTypeValue = this.sourceTypeOptions[0]['value'];
      this.tempData = this.createTempData();
      const type = this.typesValueMap[this.currentTypeValue]['type'];
      this.formData = this.tempData[type];

      this.currentBizOrgUuid = undefined;
      const info = this.designer.getOrgInfoById(this.useOrgId);
      if (info) {
        this.currentOrgUuid = info.uuid;
      }

      this.assignRoleMap = this.createAssignRoleMap();

      this.visible = true;
    },
    delItem(item, index) {
      const hasIndex = this.dataList.findIndex(d => d.groupId === item.groupId);
      if (hasIndex > -1) {
        this.dataList.splice(hasIndex, 1);
        this.emitValue();
      }
    },
    setItem(item, index) {
      const record = JSON.parse(JSON.stringify(item));
      this.currentTypeValue = record.type;
      this.tempData = this.createTempData({
        groupId: record.groupId
      });
      const type = this.typesValueMap[this.currentTypeValue]['type'];
      this.formData = record;
      this.tempData[type] = record;

      this.currentBizOrgUuid = undefined;
      this.currentOrgUuid = '';
      if (record.orgId) {
        const info = this.designer.getOrgInfoById(record.orgId);
        if (info) {
          this.currentOrgUuid = info.uuid;
        }
      } else if (record.bizOrgId) {
        const info = this.designer.getBizOrgById(record.bizOrgId);
        if (this.formData.value === '') {
          this.formData.value = null;
        }
        if (info) {
          this.currentBizOrgUuid = info.uuid;
          this.$nextTick(() => {
            this.$refs.bizOrg.currentBizOrgUuid = info.uuid;
            this.$refs.bizOrg.orgSelectTreeLoading = false;
          });
        }
      }

      this.assignRoleMap = this.createAssignRoleMap();
      if (item.userOptions) {
        item.userOptions.forEach(u => {
          this.assignRoleMap[u.value] = u.bizRoleId;
        });
      }

      this.visible = true;
    },
    saveItem(record) {
      const saveFuc = item => {
        if (userSelectItemValidator(item)) {
          const hasIndex = this.dataList.findIndex(d => d.groupId === item.groupId);
          if (hasIndex === -1) {
            this.dataList.push(item);
          } else {
            this.dataList.splice(hasIndex, 1, item);
          }
          this.emitValue();
          if (item.type === 8) {
            currentRef.visible = false;
          } else {
            this.visible = false;
          }
        } else {
          let error = '出错了';
          const errorMap = {
            1: `请设置${this.text}、人员选项其中一个`,
            32: `请设置${this.text}、业务人员选项其中一个`,
            8: `请设置人员过滤`
          };
          error = errorMap[item.type];
          if (item.type === 1) {
            if (this.designer.flowDefinition.enabledJobDuty) {
              error = `请设置${this.text}、人员选项、职等职级其中一个`;
            }
          } else if (item.type === 8) {
            if (this.designer.flowDefinition.enabledJobDuty) {
              error = `${error}、职等职级其中一个`;
            }
          }
          this.$message.error(error);
        }
      };
      let currentRef;
      if (record) {
        currentRef = this.$refs.userSelectFilter;
      } else {
        record = this.tempData[this.typesValueMap[this.formData.type]['type']];
        currentRef = this.$refs.form;
      }
      if (currentRef) {
        currentRef.validate(args => {
          if (typeof args === 'boolean') {
            const valid = args;
            if (valid) {
              saveFuc(record);
            }
          } else {
            const { valid, error } = args;
            if (valid) {
              saveFuc(record);
            }
          }
        });
      }
    },
    emitValue() {
      let selectedNodes = {};
      this.types.split('/').forEach(type => {
        const typeValue = this.typesMap[type]['value'];
        selectedNodes[type] = {
          type: typeValue,
          data: []
        };
      });
      const value = this.dataList.map(i => {
        let { orgName, bizRoleIds, ...item } = i; // 删除多个属性id、orgName
        const type = this.typesValueMap[item.type]['type'];
        selectedNodes[type]['data'].push(item);
        return item;
      });

      this.$emit('input', value);
      this.$emit('change', { value, selectedNodes });
      this.formData = undefined;
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
      this.currentTypeValue = typeValue;

      this.tempData = this.createTempData({
        groupId: this.formData.groupId
      });
      const type = this.typesValueMap[this.currentTypeValue]['type'];
      this.formData = this.tempData[type];

      this.currentOrgUuid = this.useOrgData.uuid;

      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      });
    },
    clearAll() {
      this.formData = this.createItem({
        type: this.currentTypeValue,
        groupId: this.formData.groupId
      });

      const type = this.typesValueMap[this.currentTypeValue]['type'];
      this.tempData[type] = this.formData;
      this.assignRoleMap = this.createAssignRoleMap();
    },
    cancelModal() {
      this.visible = false;
      this.formData = undefined;
      this.$emit('cancel');
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
        const { type, groupId, orgId, bizOrgId } = this.dataList[0];
        this.dataList = this.dataList.filter((item, index) => {
          return item.type === this.typesMap['filter']['value'];
        });
        let newItem = this.createItem({
          groupId,
          type
        });
        newItem.orgId = orgId;
        newItem.bizOrgId = bizOrgId;
        this.dataList.unshift(newItem);
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
