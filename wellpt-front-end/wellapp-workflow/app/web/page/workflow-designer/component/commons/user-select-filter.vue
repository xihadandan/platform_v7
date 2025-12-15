<template>
  <div class="org-select-component">
    <!-- 过滤条件 -->
    <div class="user-select-container">
      <div class="user-select-title">
        <div class="_title">{{ text }}过滤条件</div>
        <div class="_operation">
          <a-button type="link" size="small" icon="plus" @click="addItem">添加</a-button>
          <a-button type="link" size="small" icon="delete" @click="clearList">清空</a-button>
        </div>
      </div>
      <template v-for="(item, index) in dataSource">
        <div class="user-select-item" v-if="item.type === typesMap['filter']['value']" :key="index" @click="setItem(item, index)">
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
          <a-button type="link" class="icon-only _delete" @click.stop="delItem(item, index)">
            <Icon type="pticon iconfont icon-ptkj-shanchu" />
          </a-button>
        </div>
      </template>
    </div>

    <a-modal
      class="wf-users-select-modal"
      :mask="false"
      :maskClosable="false"
      :title="`${text}过滤`"
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
          <a-form-model-item
            label="选择过滤组织"
            prop="orgId"
            :rules="{
              required: true,
              trigger: 'change',
              validator: (rule, value, callback) => {
                currentOrgIdOrBizOrgId ? callback() : callback('请选择过滤组织');
              }
            }"
          >
            <use-org-tree-select v-model="currentOrgIdOrBizOrgId" @change="changeBizOrgId" />
          </a-form-model-item>

          <template v-if="formData.bizOrgId">
            <a-form-model-item label="人员过滤">
              <a-row>
                <a-col span="12" v-for="litem in bizOrgUserFilter" :key="litem.type">
                  <div class="checked-div">{{ litem.label }}</div>
                  <div class="checked-div" v-for="(item, index) in litem.list" :key="litem.type + index">
                    <a-checkbox :checked="selectedUserOptions.includes(item.value)" :value="item" @change="changeOptions">
                      {{ item.name }}
                    </a-checkbox>
                    <template v-if="selectedUserOptions.includes(item.value) && bizOrgFilterAssignRole.includes(item.value)">
                      <w-select
                        mode="multiple"
                        v-model="assignRoleMap[item.value]"
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
          <template v-else-if="formData.orgId || !currentOrgIdOrBizOrgId">
            <!-- 行政组织人员过滤 -->
            <a-form-model-item label="人员过滤">
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
            <template v-if="designer.flowDefinition.enabledJobDuty">
              <a-form-model-item label="职等职级过滤">
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
          </template>
        </a-form-model>
      </template>
      <template slot="footer">
        <a-button type="primary" @click="saveItem">确定</a-button>
        <a-button type="danger" @click="clearAll">清空</a-button>
        <a-button @click="cancelModal">取消</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script>
import { fetchBizOrgRolesByBizOrgId } from '../api';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { handlerFilterList, bizOrgUserFilter, bizOrgFilterAssignRole } from '../designer/constant';
import WTreeSelect from '../components/w-tree-select';
import WSelect from '../components/w-select';
import WCheckbox from '../components/w-checkbox';
import UseOrgTreeSelect from '../commons/use-org-tree-select.vue';

export default {
  name: 'UserSelectFilter',
  inject: ['designer'],
  components: {
    OrgSelect,
    WTreeSelect,
    WSelect,
    WCheckbox,
    UseOrgTreeSelect
  },
  props: {
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
    },
    typesMap: {
      type: Object,
      default: () => {}
    },
    bizOrgIdRoleMap: {
      type: Object,
      default: () => {}
    },
    jobGradeMap: {
      type: Object,
      default: () => {}
    },
    jobRankMap: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    const assignRoleMap = this.createAssignRoleMap();

    return {
      visible: false,
      formData: undefined,
      handlerFilterList,
      bizOrgUserFilter,
      currentOrgIdOrBizOrgId: '', // 当前组织id
      bizOrgRolesOptions: [], // 业务组织角色
      bizOrgFilterAssignRole, // 需要指定角色选项
      assignRoleMap // 指定角色
    };
  },
  computed: {
    // 选中的人员选项
    selectedUserOptions() {
      let selected = [];
      const userOptions = this.formData.userOptions;

      if (userOptions.length) {
        selected = userOptions.map(item => item.value);
      }
      return selected;
    }
  },
  watch: {
    'formData.bizOrgId': {
      handler(bizOrgId) {
        this.getBizOrgRoles(bizOrgId);
      }
    }
  },
  methods: {
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
    // 创建指定角色
    createAssignRoleMap() {
      let assignRoleMap = {};
      bizOrgFilterAssignRole.forEach(r => {
        assignRoleMap[r] = '';
      });
      return assignRoleMap;
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
    // 更改角色
    changeBizRole(value, option) {
      this.formData.userOptions.forEach(item => {
        if (item.value === option.value) {
          item.bizRoleId = value;
        }
      });
    },
    addItem() {
      this.currentOrgIdOrBizOrgId = '';
      this.assignRoleMap = this.createAssignRoleMap();
      this.formData = this.createItem({
        type: this.typesMap['filter']['value']
      });
      this.visible = true;
    },
    delItem(item, index) {
      this.$emit('delete', item, index);
    },
    setItem(item, index) {
      this.formData = JSON.parse(JSON.stringify(item));
      this.currentOrgIdOrBizOrgId = item.orgId || item.bizOrgId;
      this.assignRoleMap = this.createAssignRoleMap();
      item.userOptions.forEach(u => {
        this.assignRoleMap[u.value] = u.bizRoleId;
      });
      this.visible = true;
    },
    saveItem() {
      this.$emit('save', this.formData);
    },
    // 更改业务组织id
    changeBizOrgId(value, label, extra) {
      this.currentOrgIdOrBizOrgId = value;
      this.assignRoleMap = this.createAssignRoleMap();
      this.formData = this.createItem({
        type: this.typesMap['filter']['value'],
        groupId: this.formData.groupId
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
        groupId: this.formData.groupId
      });
      this.currentOrgIdOrBizOrgId = '';
      this.assignRoleMap = this.createAssignRoleMap();
    },
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
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
