<template>
  <div class="org-setting">
    <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
      <template v-for="(set, i) in getCurrentSetMetas()">
        <div :key="i">
          <div class="setting-title">
            {{ set.title }}
          </div>
          <a-list item-layout="horizontal" :data-source="getOrgSettingMetaSource(set.key)">
            <a-list-item slot="renderItem" slot-scope="item, index">
              <!-- 组织节点挂载校验，允许设置可挂载的节点模型类型  -->
              <a slot="actions" v-if="set.key === 'orgMountValidateSettingList'">
                <a-popconfirm
                  placement="left"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="e => confirm(e, item, set.key)"
                  @cancel="cancel(item)"
                  overlayClassName="org-setting-popconfirm"
                >
                  <a-icon slot="icon" type="question-circle-o" style="display: none" />
                  <template slot="title">
                    <a-space>
                      <label>允许挂载的父节点</label>
                      <a-select
                        allowClear
                        style="width: 250px"
                        mode="multiple"
                        v-model="item.attrValTemp.modelIds"
                        :options="orgElementModelOption"
                      />
                      <!-- FIXME: 是否忽略职位被挂载，职位作为末级节点 -->
                    </a-space>
                  </template>

                  <a-button type="link" size="small">
                    <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                    设置
                  </a-button>
                </a-popconfirm>
              </a>

              <!-- 用户唯一性自定义规则 -->
              <a
                slot="actions"
                v-else-if="
                  item.category === 'USER_UNIQUE_RULE' && !['TEL_UNIQUE', 'USER_ID_NUMBER_UNIQUE', 'USER_NAME_UNIQUE'].includes(item.key)
                "
              >
                <a-popconfirm
                  placement="topRight"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="e => confirm(e, item)"
                  @cancel="cancel(item)"
                  :ref="'orgSettingPopconfirm_' + item.uuid"
                  overlayClassName="org-setting-popconfirm"
                >
                  <a-icon slot="icon" type="question-circle-o" style="display: none" />
                  <template slot="title">
                    <a-form-model
                      :model="item.attrValTemp"
                      :rules="selfDefineUserRules"
                      :label-col="{ span: 6 }"
                      :wrapper-col="{ span: 18 }"
                      :ref="'orgSettingForm_' + item.uuid"
                      class="pt-form"
                    >
                      <a-form-model-item label="规则名称" prop="name">
                        <a-input v-model="item.attrValTemp.name" />
                      </a-form-model-item>
                      <a-form-model-item label="校验字段" prop="fields">
                        <a-select
                          :options="userFieldOptions"
                          v-model="item.attrValTemp.fields"
                          style="width: 250px"
                          allowClear
                          mode="multiple"
                        />
                      </a-form-model-item>
                      <a-form-model-item label="规则描述">
                        <a-textarea v-model="item.remark" />
                      </a-form-model-item>
                    </a-form-model>
                  </template>

                  <a-button type="link" size="small">
                    <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                    规则设置
                  </a-button>
                </a-popconfirm>
              </a>
              <!-- 组织节点统级 -->
              <!-- <a slot="actions" v-if="item.key === 'ORG_ELEMENT_USER_COUNT'">
                <a-popconfirm
                  placement="topRight"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="e => confirm(e, item)"
                  ref="countOrgEleUserPopconfirm"
                  @cancel="cancel(item)"
                >
                  <a-icon slot="icon" type="question-circle-o" style="display: none" />
                  <template slot="title">
                    <a-form-model
                      :model="item.attrValTemp"
                      ref="countOrgElementUserForm"
                      :labelCol="{
                        span: 10,
                        style: {
                          width: '135px'
                        }
                      }"
                      :wrapperCol="{
                        span: 14
                      }"
                      :colon="false"
                      :rules="{ countModelIds: [{ required: true, message: '必选', trigger: ['change'], type: 'array' }] }"
                    >
                      <a-form-model-item prop="countModelIds">
                        <template slot="label">
                          <a-tooltip>
                            <template slot="title">组织架构中对于类型的节点将显示人员数量统计</template>
                            统计组织单元
                            <a-icon type="info-circle" />
                          </a-tooltip>
                        </template>
                        <a-select
                          allowClear
                          style="width: 250px"
                          mode="multiple"
                          v-model="item.attrValTemp.countModelIds"
                          :options="orgElementModelOption"
                        />
                      </a-form-model-item>
                     <a-form-model-item label="统计兼任人员">
                        <a-switch v-model="item.attrValTemp.countPartTime" />
                      </a-form-model-item>
                    </a-form-model>
                  </template>
                  <a-button type="link" icon="setting">设置</a-button>
                </a-popconfirm>
              </a> -->
              <a
                slot="actions"
                v-if="item.key === 'MY_ORG_FORCE_ASYNC_FETCH_THRESHOLD' || item.key === 'MY_ORG_USER_FORCE_ASYNC_FETCH_THRESHOLD'"
              >
                <a-popconfirm
                  placement="topRight"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="e => confirm(e, item)"
                  ref="myOrgForceAsyncFetchPopconfirm"
                  @cancel="cancel(item)"
                  overlayClassName="org-setting-popconfirm"
                >
                  <a-icon slot="icon" type="question-circle-o" style="display: none" />
                  <template slot="title">
                    <a-form-model layout="inline" :model="item.attrValTemp" ref="myOrgForceAsyncFetchForm" :colon="false">
                      <a-form-model-item label="阈值">
                        <a-input-number allowClear v-model="item.attrValTemp.num" :min="500" :max="5000" />
                      </a-form-model-item>
                    </a-form-model>
                  </template>
                  <a-button type="link" size="small">
                    <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                    设置
                  </a-button>
                </a-popconfirm>
              </a>
              <!-- 单位、部门分级管理 -->
              <a slot="actions" v-if="set.key === 'orgLevelMgrSettingList'">
                <a-popconfirm
                  placement="topRight"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="e => confirm(e, item)"
                  @cancel="cancel(item)"
                  overlayClassName="org-setting-popconfirm"
                >
                  <a-icon slot="icon" type="question-circle-o" style="display: none" />
                  <template slot="title">
                    <a-tabs default-active-key="orgConstructEditGrantTab" :style="{ width: '450px' }">
                      <a-tab-pane key="orgConstructEditGrantTab" tab="组织架构">
                        <Scroll :style="{ height: '400px' }">
                          <a-form-model :model="item.attrValTemp" :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }">
                            <template v-for="(orgModel, i) in orgElementModels">
                              <a-form-model-item :label="orgModel.name" :key="'orgModelGrant_' + i" v-if="orgModel.id != 'unit'">
                                <a-checkbox-group v-model="item.attrValTemp[orgModel.id + 'Grant']" :options="orgElementEditGrantOptions" />
                              </a-form-model-item>
                            </template>
                          </a-form-model>
                        </Scroll>
                      </a-tab-pane>
                      <a-tab-pane key="1" tab="用户" class="user-grant-tab">
                        <a-checkbox-group v-model="item.attrValTemp['userGrant']" :style="{ width: '100%' }">
                          <a-row>
                            <a-col :span="24">
                              <a-checkbox value="createUser">创建用户</a-checkbox>
                            </a-col>
                          </a-row>
                          <a-row>
                            <a-col :span="24">
                              <a-checkbox value="joinUser">添加用户</a-checkbox>
                            </a-col>
                          </a-row>
                          <a-row type="flex">
                            <a-col flex="auto">
                              <a-checkbox value="allowEdit">编辑</a-checkbox>
                            </a-col>
                            <a-col
                              flex="360px"
                              v-show="item.attrValTemp['userGrant'] && item.attrValTemp['userGrant'].includes('allowEdit')"
                            >
                              <a-row type="flex">
                                <a-col :flex="1">
                                  <a-checkbox value="editAccountInfo">账号信息</a-checkbox>
                                </a-col>
                                <a-col :flex="1">
                                  <a-checkbox value="editUserInfo">个人信息</a-checkbox>
                                </a-col>
                                <a-col :flex="1">
                                  <a-checkbox value="editJobInfo">工作信息</a-checkbox>
                                </a-col>
                                <a-col :flex="1">
                                  <a-checkbox value="editRole">角色权限</a-checkbox>
                                </a-col>
                              </a-row>
                            </a-col>
                          </a-row>

                          <a-row type="flex">
                            <a-col flex="90px">
                              <a-checkbox value="moveUser">调整</a-checkbox>
                            </a-col>
                            <a-col
                              flex="180px"
                              v-show="item.attrValTemp['userGrant'] && item.attrValTemp['userGrant'].includes('moveUser')"
                            >
                              <a-row type="flex">
                                <a-col :flex="1">
                                  <a-checkbox value="moveInsideDept">部门内</a-checkbox>
                                </a-col>
                                <a-col :flex="1">
                                  <a-checkbox value="moveCrossDept">跨部门</a-checkbox>
                                </a-col>
                              </a-row>
                            </a-col>
                          </a-row>

                          <a-row>
                            <a-col :span="24">
                              <a-checkbox value="jobQuit">离职</a-checkbox>
                            </a-col>
                          </a-row>

                          <a-row>
                            <a-col :span="24">
                              <a-checkbox value="lockUser">冻结</a-checkbox>
                            </a-col>
                          </a-row>

                          <a-row>
                            <a-col :span="24">
                              <a-checkbox value="deleteUser">删除</a-checkbox>
                            </a-col>
                          </a-row>
                        </a-checkbox-group>
                      </a-tab-pane>
                    </a-tabs>
                  </template>
                  <a-button type="link" size="small">
                    <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                    设置权限
                  </a-button>
                </a-popconfirm>
              </a>

              <!-- 其他参数设置 -->
              <a slot="actions" v-if="['ORG_VERSION_AUDIT_FLOW_ENABLE', 'ORG_JOB_RANK_LEVEL_ENABLE'].includes(item.key)">
                <a-popconfirm
                  placement="topRight"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="e => confirm(e, item)"
                  @cancel="cancel(item)"
                  v-if="item.key === 'ORG_VERSION_AUDIT_FLOW_ENABLE'"
                  overlayClassName="org-setting-popconfirm"
                >
                  <a-icon slot="icon" type="question-circle-o" style="display: none" />
                  <template slot="title">
                    <a-space>
                      <label>默认审批流程</label>
                      <a-select
                        allowClear
                        style="width: 250px"
                        v-model="item.attrValTemp.flowId"
                        :options="flowDefOptions"
                        :filter-option="filterOption"
                      />
                    </a-space>
                  </template>
                  <a-button type="link" size="small">
                    <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                    设置默认流程
                  </a-button>
                </a-popconfirm>
              </a>
              <a slot="actions" v-if="item.key === 'ORG_JOB_RANK_LEVEL_ENABLE'">
                <a-button type="link" size="small" @click.stop="openDutySetting">
                  <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                  体系设置
                </a-button>
              </a>

              <a slot="actions" v-if="item.allowDelete">
                <a-button type="link" size="small" @click.stop="deleteSetting(item, index)">
                  <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                  删除
                </a-button>
              </a>
              <a
                slot="actions"
                v-if="!['MY_ORG_FORCE_ASYNC_FETCH_THRESHOLD', 'MY_ORG_USER_FORCE_ASYNC_FETCH_THRESHOLD'].includes(item.key)"
              >
                <a-switch
                  v-model="item.enable"
                  :loading="item.loading"
                  checked-children="开启"
                  un-checked-children="关闭"
                  @click="checked => enableSetting(checked, item)"
                />
              </a>
              <a-list-item-meta>
                <label slot="description">{{ item.remark }}</label>
                <label slot="title">{{ item.title }}</label>
              </a-list-item-meta>
            </a-list-item>

            <!-- <template slot="loadMore" v-if="set.key === 'orgUserUniqRuleList'">
              <a-button icon="plus" block @click.stop="addRuleModalVisible = true">新建规则</a-button>
            </template> -->
          </a-list>
        </div>
      </template>
    </a-skeleton>
    <a-modal
      title="新建规则"
      :visible="addRuleModalVisible"
      @ok="addUserRule"
      @cancel="addRuleModalVisible = false"
      :width="500"
      :bodyStyle="{ height: '300px', overflowY: 'auto' }"
      :destroyOnClose="true"
      dialogClass="pt-modal"
    >
      <div class="pt-form">
        <a-form-model ref="addUserForm" :model="tempUserRule" :rules="selfDefineUserRules" :label-col="labelCol" :wrapper-col="wrapperCol">
          <a-form-model-item label="规则名称" prop="name">
            <a-input v-model="tempUserRule.name" />
          </a-form-model-item>
          <a-form-model-item label="校验字段" prop="fields">
            <a-select :options="userFieldOptions" v-model="tempUserRule.fields" style="width: 100%" allowClear mode="multiple" />
          </a-form-model-item>
          <a-form-model-item label="规则描述">
            <a-textarea v-model="tempUserRule.remark" />
          </a-form-model-item>
        </a-form-model>
      </div>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';

export default {
  name: 'OrgSetting',
  props: { parent: Object },
  inject: ['pageContext'],
  data() {
    return {
      loading: true,
      setting: {},
      parentName: '',
      tempSetting: {},
      orgSettings: [],
      orgElementModels: [],
      orgMgrPageSettingList: [],
      orgDialogChooseSettingList: [],
      orgCrossDataVisibleList: [],
      orgLevelMgrSettingList: [],
      orgUseGrantSettingList: [],
      orgVersionSettingList: [],
      orgJobRankSettingList: [],
      orgMountValidateSettingList: [],
      orgUserUniqRuleList: [],
      orgMountValidateSetMetas: [{ title: '组织节点挂载校验', key: 'orgMountValidateSettingList' }].concat(
        this._$SYSTEM_ID == 'PRD_PT' ? [{ title: '用户唯一性校验', key: 'orgUserUniqRuleList' }] : []
      ),
      orgDisplaySetMetas: [
        { title: '组织管理页面', key: 'orgMgrPageSettingList' },
        { title: '组织选择弹窗', key: 'orgDialogChooseSettingList' },
        { title: '组织通讯录', key: 'orgCrossDataVisibleList' }
      ],
      levelGrantSetSetMetas: [
        { title: '组织分级管理设置', key: 'orgLevelMgrSettingList' },
        { title: '组织使用授权设置', key: 'orgUseGrantSettingList' }
      ],
      otherOrgSetMetas: [
        { title: '组织版本化', key: 'orgVersionSettingList' },
        { title: '职务体系', key: 'orgJobRankSettingList' }
      ],
      addRuleModalVisible: false,
      tempUserRule: { name: undefined, fields: undefined, remark: undefined },
      userFieldOptions: [
        { label: '用户名', value: 'userName' },
        { label: '身份证号', value: 'idNumber' },
        { label: '移动电话', value: 'ceilPhoneNumber' },
        { label: '邮件', value: 'mail' }
      ],
      selfDefineUserRules: {
        name: { required: true, message: '规则名称必填', trigger: ['blur', 'change'] },
        fields: { required: true, message: '校验字段必选', trigger: ['blur', 'change'] }
      },
      flowDefOptions: [],
      orgElementEditGrantOptions: [
        { label: '新建', value: 'add' },
        { label: '编辑', value: 'edit' },
        { label: '删除', value: 'delete' }
      ],
      orgUserEditGrantOptions: [
        { label: '创建用户', value: 'createUser' },
        { label: '添加用户', value: 'joinUser' },
        { label: '编辑', value: 'editUser' }
      ],
      countOrgElementFormVisible: false,
      labelCol: { span: 4 },
      wrapperCol: { span: 19 }
    };
  },
  META: {
    method: {
      openAddRuleModal: '新建规则'
    }
  },
  beforeCreate() {},
  components: {},
  filters: {
    formateOrgModelIdNames: function (values, map) {
      if (!values) return '';
      let labels = [];
      let val = JSON.parse(values);
      if (val && val.modelIds.length) {
        for (let i = 0, len = val.modelIds.length; i < len; i++) {
          labels.push(map[val.modelIds[i]].name);
        }
      }
      return labels.join('、');
    }
  },
  computed: {
    orgElementModelIdMap() {
      let map = {};
      for (let i = 0, len = this.orgElementModels.length; i < len; i++) {
        map[this.orgElementModels[i].id] = this.orgElementModels[i];
      }
      return map;
    },
    orgElementModelOption() {
      let option = [];
      for (let i = 0, len = this.orgElementModels.length; i < len; i++) {
        option.push({ label: this.orgElementModels[i].name, value: this.orgElementModels[i].id });
      }
      return option;
    },
    exceptJobOrgElementModelOption() {
      let option = [];
      for (let i = 0, len = this.orgElementModels.length; i < len; i++) {
        if (this.orgElementModels[i].id != 'job') {
          option.push({ label: this.orgElementModels[i].name, value: this.orgElementModels[i].id });
        }
      }
      return option;
    }
  },
  created() {},
  methods: {
    subTitle(key) {},
    openDutySetting() {
      window.open('/web/app/pt-mgr/pt-usr-mgr/app_20211025103457.html?pageUuid=50975b43-4219-4359-9a89-09e3f1d199c3', '_blank');
    },
    getOrgSettingMetaSource(key) {
      return this[key];
    },
    generateOrgSettingMetaDataSource(params) {
      for (let pkey in params) {
        this[pkey] = [];
        let attrs = params[pkey];
        for (let i = 0, len = attrs.length; i < len; i++) {
          let set = this.setting[attrs[i].value];
          if (set) {
            let remark = set.remark;
            // if (set.attrKey == 'ORG_ELEMENT_USER_COUNT') {
            //   remark = '统计并显示' + this.formateModelIdsToNameString(JSON.parse(set.attrVal).countModelIds) + '的人数';
            // }
            this[pkey].push({
              title: attrs[i].label,
              key: set.attrKey,
              enable: set.enable,
              uuid: set.uuid,
              attrVal: set.attrVal,
              attrValTemp: set.attrVal ? JSON.parse(set.attrVal) : undefined,
              remark
            });
          }
        }
        console.log(this.parentName + '组织参数设置', this[pkey]);
      }
      this.loading = false;
    },
    getOtherOrgSettingList() {
      this.generateOrgSettingMetaDataSource({
        orgVersionSettingList: [
          { label: '版本化', value: 'ORG_VERSION_ENABLE' },
          { label: '版本发布审批', value: 'ORG_VERSION_AUDIT_FLOW_ENABLE' }
        ],
        orgJobRankSettingList: [{ label: '职务管理体系', value: 'ORG_JOB_RANK_LEVEL_ENABLE' }]
      });
    },
    getOrgLevelGrantSettingList() {
      this.generateOrgSettingMetaDataSource({
        orgLevelMgrSettingList: [
          { label: '单位分级管理', value: 'ORG_UNIT_EDIT_GRANT' },
          { label: '部门分级管理', value: 'ORG_DEPT_EDIT_GRANT' }
        ],
        orgUseGrantSettingList: [
          { label: '单位使用授权功能', value: 'ORG_UNIT_USE_GRANT_VISIBLE' },
          { label: '部门使用授权功能', value: 'ORG_DEPT_USE_GRANT_VISIBLE' }
        ]
      });
    },
    getOrgMgrPageSettingList() {
      this.generateOrgSettingMetaDataSource({
        orgMgrPageSettingList: [
          { label: '职位节点显示', value: 'ORG_MANAGE_JOB_VISIBLE' }
          // { label: '组织节点统计', value: 'ORG_ELEMENT_USER_COUNT' }
        ],
        orgDialogChooseSettingList: [
          { label: '职位节点显示', value: 'ORG_DIALOG_JOB_LEVEL_VISIBLE' },
          { label: '组织预览显示', value: 'ORG_DIALOG_SELECT_PREVIEW' },
          { label: '我的组织数据加载异步阈值', value: 'MY_ORG_FORCE_ASYNC_FETCH_THRESHOLD' },
          { label: '我的组织用户数据加载异步阈值', value: 'MY_ORG_USER_FORCE_ASYNC_FETCH_THRESHOLD' }
        ],
        orgCrossDataVisibleList: [{ label: '跨单位组织数据可见性', value: 'ORG_CROSS_DATA_VISIBLE' }]
      });
    },
    getOrgMountValidateSettingList() {
      this.orgMountValidateSettingList = [];
      for (let i = 0, len = this.orgElementModels.length; i < len; i++) {
        let model = this.orgElementModels[i],
          key = `${model.id}_MOUNT_SET`.toUpperCase(),
          set = this.setting[key],
          attrValJson = set && set.attrVal ? JSON.parse(set.attrVal) : { modelIds: [] };
        if (set) {
          this.orgMountValidateSettingList.push({
            title: model.name,
            key,
            enable: set.enable,
            uuid: set.uuid,
            attrVal: set.attrVal,
            attrValTemp: attrValJson,
            remark: '允许挂载的父节点: ' + this.formateModelIdsToNameString(attrValJson.modelIds)
          });
        }
      }

      this.orgUserUniqRuleList = [];
      if (this._$SYSTEM_ID == 'PRD_PT') {
        let rules = [
            { value: 'TEL_UNIQUE', label: '手机号' },
            { value: 'USER_ID_NUMBER_UNIQUE', label: '身份证号' },
            { value: 'USER_NAME_UNIQUE', label: '姓名' }
          ],
          fixed = [];
        for (let i = 0, len = rules.length; i < len; i++) {
          let set = this.setting[rules[i].value];
          this.orgUserUniqRuleList.push({
            title: rules[i].label,
            key: rules[i].value,
            enable: set.enable,
            uuid: set.uuid,
            remark: set.remark,
            category: set.category
          });
          fixed.push(rules[i].value);
        }
        for (let i = 0, len = this.orgSettings.length; i < len; i++) {
          let set = this.orgSettings[i],
            attrVal = set.attrVal;
          // 属于自定义规则
          if (set.category === 'USER_UNIQUE_RULE' && !fixed.includes(set.attrKey) && attrVal) {
            let attrJson = set.attrVal ? JSON.parse(set.attrVal) : { title: undefined, remark: undefined, fields: [] };
            this.orgUserUniqRuleList.push({
              custom: true,
              title: attrJson.name,
              key: set.attrKey,
              enable: set.enable,
              attrVal: set.attrVal,
              attrValTemp: attrJson,
              uuid: set.uuid,
              remark: set.remark,
              category: set.category,
              allowDelete: true
            });
          }
        }
      }

      this.loading = false;
    },
    openAddRuleModal() {
      this.$set(this, 'tempUserRule', { name: undefined, fields: undefined, remark: undefined });
      this.addRuleModalVisible = true;
    },
    addUserRule() {
      let _this = this,
        submitData = {
          attrKey: generateId(),
          attrVal: JSON.stringify(this.tempUserRule),
          remark: this.tempUserRule.remark,
          category: 'USER_UNIQUE_RULE'
        };
      this.$refs.addUserForm.validate(valid => {
        if (valid) {
          $axios.post('/proxy/api/org/elementModel/addOrgSetting', submitData).then(({ data }) => {
            if (data.code == 0 && data.data) {
              _this.$message.success('新建规则成功');
              _this.getSetting(() => {
                _this.getOrgMountValidateSettingList();
              });

              _this.addRuleModalVisible = false;
            } else {
              _this.$message.error('新建规则失败');
            }
          });
        } else {
          return false;
        }
      });
    },

    cancel(item) {
      // 还原
      item.attrValTemp = JSON.parse(typeof item.attrVal == 'string' ? item.attrVal : JSON.stringify(item.attrVal));
    },
    confirm(e, item, metaKey) {
      let key = item.key,
        _this = this;
      let commit = () => {
        let submitData = JSON.parse(JSON.stringify(this.setting[key]));
        let tempStr = JSON.stringify(item.attrValTemp);
        submitData.attrVal = tempStr;
        submitData.remark = item.remark;

        // 校验规则
        let ispass = true;
        if (this.$refs['orgSettingForm_' + item.uuid]) {
          this.$refs['orgSettingForm_' + item.uuid][0].validate((valid, msg) => {
            ispass = valid;
          });
        }
        if (!ispass) {
          // 如果校验不通过，阻止弹框关闭
          if (this.$refs['orgSettingPopconfirm_' + item.uuid]) {
            this.$refs['orgSettingPopconfirm_' + item.uuid][0].setVisible(true, e);
          }
          return false;
        }
        // 更新
        $axios.post('/proxy/api/org/elementModel/updateOrgSetting', submitData).then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.$message.success('保存成功');
            item.attrVal = JSON.parse(JSON.stringify(item.attrValTemp));
            _this.setting[key].attrVal = tempStr;
            // 更新备注：
            if (metaKey == 'orgMountValidateSettingList') {
              item.remark = '允许挂载的父节点: ' + _this.formateModelIdsToNameString(item.attrValTemp.modelIds);
            }
            // if (key == 'ORG_ELEMENT_USER_COUNT') {
            //   item.remark = '统计并显示' + _this.formateModelIdsToNameString(JSON.parse(tempStr).countModelIds) + '的人数';
            // }
            if (item.category === 'USER_UNIQUE_RULE' && item.allowDelete) {
              item.remark = item.attrValTemp.remark;
            }
            // 规则修改后刷新
            if (this.$refs['orgSettingForm_' + item.uuid]) {
              _this.getSetting(() => {
                _this.getOrgMountValidateSettingList();
              });
            }
          } else {
            _this.$message.error('保存失败');
          }
        });
      };
      if (item.key == 'ORG_ELEMENT_USER_COUNT') {
        // this.$refs.countOrgEleUserPopconfirm[0].setVisible(true);
        // this.$refs.countOrgElementUserForm[0].validate(pass => {
        //   if (pass) {
        //     this.$refs.countOrgEleUserPopconfirm[0].setVisible(false);
        //     commit.call(_this);
        //   }
        // });
      } else {
        commit.call(this);
      }
    },

    formateModelIdsToNameString(ids) {
      let labels = [];
      for (let i = 0, len = ids.length; i < len; i++) {
        if (this.orgElementModelIdMap[ids[i]]) {
          labels.push(this.orgElementModelIdMap[ids[i]].name);
        }
      }
      return labels.join('、');
    },

    deleteSetting(item, i) {
      let _this = this,
        uuid = item.uuid;
      $axios.get('/proxy/api/org/elementModel/deleteOrgSetting', { params: { uuid } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          _this.$message.success(`删除成功`);
          if (item.category === 'USER_UNIQUE_RULE') {
            _this.orgUserUniqRuleList.splice(i, 1);
          }
        } else {
          _this.$message.error(`删除失败`);
        }
      });
    },
    enableSetting(checked, item) {
      let _this = this;
      this.$set(item, 'loading', true);
      $axios.get('/proxy/api/org/elementModel/enableOrgSetting', { params: { uuid: item.uuid, enable: checked } }).then(({ data }) => {
        this.$set(item, 'loading', false);
        if (data.code == 0 && data.data) {
          _this.$message.success(`${checked ? '开启' : '关闭'}成功`);
        } else {
          item.enable = !checked;
        }
      });
    },

    fetchOrgElementModel() {
      let _this = this;
      return new Promise((resolve, reject) => {
        $axios.get('/proxy/api/org/elementModel/getAllOrgElementModels', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.orgElementModels.splice(0, _this.orgElementModels.length);
            _this.orgElementModels.push(...data.data);
            _this.getOrgMountValidateSettingList();
            resolve(_this.orgElementModels);
          }
        });
      });
    },

    getSetting(callback) {
      let _this = this;
      $axios.get('/proxy/api/org/elementModel/queryOrgSetting', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          _this.orgSettings = data.data;
          for (let i = 0, len = data.data.length; i < len; i++) {
            _this.$set(_this.setting, data.data[i].attrKey, data.data[i]);
            let attrVal = data.data[i].attrVal;
            if (attrVal) {
              attrVal = JSON.parse(attrVal);
            }
            let temp = JSON.parse(JSON.stringify(data.data[i]));
            temp.attrVal = attrVal || undefined;
            _this.$set(_this.tempSetting, data.data[i].attrKey, temp);
          }
          _this.fetchOrgElementModel().then(models => {
            if (this.setting['ORG_UNIT_EDIT_GRANT'].attrVal == undefined) {
              let obj = {};
              for (let m of models) {
                obj[`${m.id}Grant`] = [];
              }
              this.setting['ORG_UNIT_EDIT_GRANT'].attrVal = JSON.stringify(obj);
            } else {
              let attrVal = JSON.parse(this.setting['ORG_UNIT_EDIT_GRANT'].attrVal);
              for (let m of models) {
                if (attrVal[`${m.id}Grant`] == undefined) {
                  attrVal[`${m.id}Grant`] = [];
                }
              }
            }
            // if (this.setting['ORG_ELEMENT_USER_COUNT'].attrVal == undefined) {
            //   this.setting['ORG_ELEMENT_USER_COUNT'].attrVal = JSON.stringify({ countModelIds: ['unit', 'dept'] });
            // }
            if (typeof callback === 'function') callback.call(_this);
          });
        }
      });
    },

    fetchWorkflowOptions() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'flowDefineService',
          methodName: 'query',
          args: JSON.stringify([{}])
        })
        .then(({ data }) => {
          let options = [];
          if (data.code == 0 && data.data) {
            for (let i = 0, len = data.data.length; i < len; i++) {
              options.push({
                label: data.data[i].name,
                value: data.data[i].uuid
              });
            }
          }
          _this.flowDefOptions = options;
        });
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    getCurrentSetMetas() {
      // 根据 tabkey 切换不同的定义数据
      let metas = {
        组织数据校验: this.orgMountValidateSetMetas,
        组织显示设置: this.orgDisplaySetMetas,
        分级授权设置: this.levelGrantSetSetMetas,
        其他参数设置: this.otherOrgSetMetas
      };

      return metas[this.parentName] || [];
    },

    loadData() {
      this.parentName = this.parent.title;
      let init = () => {
        this.getSetting(() => {
          if (this.parentName == '组织显示设置') {
            this.getOrgMgrPageSettingList();
          } else if (this.parentName == '分级授权设置') {
            this.getOrgLevelGrantSettingList();
          } else if (this.parentName == '其他参数设置') {
            this.getOtherOrgSettingList();
          }
        });
      };
      init.call(this);

      if (this.parentName === '组织数据校验') {
        this.pageContext.handleEvent(`bzgjUwLFnflThzyubRCJWsmGOgQJUqkO:change`, activeKey => {
          this.fetchOrgElementModel();
        });
      }
      if (this.parentName == '其他参数设置') {
        this.fetchWorkflowOptions();
      }
    }
  },
  beforeMount() {
    this.loadData();
  },
  mounted() {}
};
</script>
<style lang="less" scoped>
.user-grant-tab {
  .ant-checkbox-group {
    > .ant-row,
    > .ant-row-flex {
      margin-bottom: var(--w-padding-2xs);
    }
  }
}
</style>
<style lang="less">
.org-setting-popconfirm {
  .ant-popover-message-title {
    padding-left: 0;
  }
}
</style>
