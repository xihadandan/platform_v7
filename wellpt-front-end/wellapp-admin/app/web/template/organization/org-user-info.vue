<template>
  <div>
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
    <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" :colon="false" class="pt-form">
      <a-tabs
        default-active-key="acctInfo"
        tabPosition="left"
        class="ant-tabs-menu-style"
        v-model="activeKey"
        style="height: 500px"
        :tabBarStyle="{ '--w-tab-menu-style-width': tabsWidth ? tabsWidth : '', '--w-tab-menu-style-nav-container-padding': '0 24px 0 0' }"
      >
        <a-tab-pane key="acctInfo" tab="账号信息">
          <div :style="{ height: '400px', paddingRight: '10px', 'pointer-events': !editable ? 'none' : 'unset' }">
            <a-form-model-item label="账号" prop="loginName">
              <a-input v-model="form.loginName" v-if="!uuid" />
              <label v-else>{{ form.loginName }}</label>
            </a-form-model-item>
            <a-form-model-item label="登录密码" prop="password" v-if="!uuid">
              <a-input-password
                v-model.trim="form.password"
                autocomplete="new-password"
                :max-length="passwordMaxLength"
                :placeholder="passwordPlaceholder"
              />
            </a-form-model-item>
            <a-form-model-item label="确认密码" prop="confirmPassword" v-if="!uuid">
              <a-input-password v-model.trim="form.confirmPassword" />
            </a-form-model-item>
          </div>
        </a-tab-pane>
        <a-tab-pane key="userInfo" tab="个人信息" forceRender>
          <Scroll @mounted="scroll => updateScrollContentHeight(scroll, 'userInfo')" :style="{ height: '400px', paddingRight: '10px' }">
            <div :style="{ 'pointer-events': (uuid && !userAuthority.includes('editUserInfo')) || !editable ? 'none' : 'unset' }">
              <div :style="{ textAlign: 'center', padding: '15px', position: 'relative' }">
                <span class="user-avatar">
                  <!-- <a-badge>
                <a-icon type="close-square" theme="filled" slot="count" style="color: #f5222d" />

              </a-badge> -->
                  <a-avatar
                    :size="120"
                    :icon="form.avatar ? null : 'user'"
                    :src="form.avatar ? '/proxy/org/user/view/photo/' + form.avatar : null"
                    title="点击头像进行上传或者替换"
                    style="cursor: pointer"
                    @click.stop="onClickUploadAvatar"
                  />
                  <div class="user-info-avatar-operation" v-show="form.avatar != undefined">
                    <a-button size="small" @click.stop="onClickUploadAvatar" type="primary" title="替换头像">替换</a-button>
                    <a-button size="small" @click.stop="form.avatar = undefined" type="danger" title="删除头像">删除</a-button>
                  </div>
                </span>

                <a-upload
                  list-type="picture-card"
                  class="avatar-uploader"
                  :show-upload-list="false"
                  v-show="false"
                  :customRequest="customImageRequest"
                  accept="image/png, image/jpeg"
                >
                  <a-button ref="uploadButton"></a-button>
                </a-upload>
              </div>
              <a-form-model-item label="姓名" prop="userName">
                <a-input v-model.trim="form.userName" @change="onChangeUserName">
                  <template slot="addonAfter">
                    <WI18nInput
                      :target="form"
                      code="userName"
                      v-model="form.userName"
                      :showZhCn="false"
                      popoverTitle="请输入国际化姓名"
                      :displayLocaleCode="false"
                      @change="onChangeI18nUserName"
                    />
                  </template>
                </a-input>
              </a-form-model-item>
              <!-- <a-form-model-item label="拼音" prop="pinYin" >
              <a-input v-model.trim="form.pinYin" />
            </a-form-model-item> -->
              <!-- <a-form-model-item label="英文名" prop="enName">
              <a-input v-model="form.enName" />
            </a-form-model-item> -->
              <a-form-model-item label="性别" prop="gender">
                <a-radio-group v-model="form.gender" button-style="solid">
                  <a-radio-button value="MALE">男</a-radio-button>
                  <a-radio-button value="FEMALE">女</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="工作属地">
                <a-input v-model="form.workLocation" />
              </a-form-model-item>
              <a-form-model-item label="身份证号" prop="idNumber">
                <a-input v-model="form.idNumber"></a-input>
              </a-form-model-item>
              <a-form-model-item label="手机号码" prop="ceilPhoneNumber">
                <a-input v-model="form.ceilPhoneNumber"></a-input>
              </a-form-model-item>
              <a-form-model-item label="家庭电话">
                <a-input v-model="form.familyPhoneNumber"></a-input>
              </a-form-model-item>
              <a-form-model-item label="办公电话">
                <a-input v-model="form.businessPhoneNumber"></a-input>
              </a-form-model-item>
              <a-form-model-item label="邮箱" prop="mail">
                <a-input v-model="form.mail"></a-input>
              </a-form-model-item>
              <a-form-model-item label="备注">
                <a-textarea v-model="form.remark"></a-textarea>
              </a-form-model-item>
            </div>
          </Scroll>
        </a-tab-pane>
        <a-tab-pane key="workInfo" tab="工作信息">
          <Scroll @mounted="scroll => updateScrollContentHeight(scroll, 'workInfo')" :style="{ height: '400px', paddingRight: '10px' }">
            <div>
              <a-form-model-item
                label="员工编号"
                :style="{ 'pointer-events': (uuid && !userAuthority.includes('editJobInfo')) || !editable ? 'none' : 'unset' }"
              >
                <a-input v-model="form.userNo" />
              </a-form-model-item>
              <a-form-model-item label="组织信息">
                <div
                  style="display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 7px"
                  v-for="(record, i) in orgUsers"
                  :key="'orgUserInfo_' + record._uuid"
                  :style="{ 'pointer-events': (uuid && !userAuthority.includes('editJobInfo')) || !editable ? 'none' : 'unset' }"
                >
                  <div style="width: 150px">
                    <div v-if="i == 0">
                      <span style="color: red">*</span>
                      部门
                    </div>
                    <OrgSelect
                      title="选择部门"
                      :showBizOrgUnderOrg="false"
                      :multiSelect="false"
                      titleDisplay="titlePath"
                      ref="orgSelect"
                      v-model="record.deptId"
                      :orgVersionId="orgVersionId"
                      displayStyle="Label"
                      :uncheckableTypes="['job', 'user']"
                      :showOpenIcon="false"
                      :orgType="['MyOrg']"
                      :params="orgSelectParams"
                      @change="e => onChangeDeptSelect(record)"
                    />
                    <!-- <a-tree-select
                      v-model="record.deptId"
                      style="width: 100%"
                      :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                      allow-clear
                      :showSearch="true"
                      :tree-data="vOrgElementTreeData"
                      treeNodeFilterProp="title"
                      :replaceFields="{ children: 'children', title: 'title', key: 'key_', value: 'id' }"
                    ></a-tree-select> -->
                  </div>
                  <div style="width: 150px">
                    <div v-if="i == 0">职位</div>
                    <a-select
                      :key="record.deptId + '_select_job'"
                      :options="underJobOptions(record.deptId)"
                      :style="{ width: '100%' }"
                      v-model="record.jobId"
                      allow-clear
                      @change="onChangeJobSelect(record)"
                    />
                  </div>
                  <div style="width: 150px">
                    <div v-if="i == 0">直接汇报人</div>
                    <OrgSelect
                      title="选择职位或者人员"
                      ref="orgSelect"
                      :showBizOrgUnderOrg="false"
                      v-model="record.reportTos"
                      :orgVersionId="orgVersionId"
                      displayStyle="Label"
                      :checkableTypes="['job', 'user']"
                      :showOpenIcon="false"
                      :orgType="['MyOrg']"
                    />
                  </div>
                  <div style="width: 110px">
                    <div v-if="i == 0" style="height: 40px"></div>
                    <a-button size="small" type="link" @click="orgUsers.splice(i, 1)">
                      <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                    </a-button>
                    <a-button
                      size="small"
                      type="link"
                      v-show="record.jobId != undefined && record.type == 'SECONDARY_JOB_USER'"
                      @click="setAsPrimaryJob(record, i)"
                    >
                      设为主职
                    </a-button>
                    <a-tag color="blue" v-if="record.type == 'PRIMARY_JOB_USER'">主职位</a-tag>
                  </div>
                </div>

                <a-button
                  :style="{ 'pointer-events': (uuid && !userAuthority.includes('editJobInfo')) || !editable ? 'none' : 'unset' }"
                  type="link"
                  icon="plus"
                  @click.stop="addOrgInfo"
                  size="small"
                >
                  添加
                </a-button>

                <a-popover v-if="bizOrgUserInfos.length > 0">
                  <template slot="content">
                    <div style="width: 600px; display: flex">
                      <div style="width: 200px; line-height: 32px; color: #999">业务组织</div>
                      <div style="width: 200px; line-height: 32px; color: #999">归属于</div>
                      <div style="width: 200px; line-height: 32px; color: #999">角色</div>
                    </div>
                    <div v-for="(item, i) in bizOrgUserInfos" style="width: 600px; display: flex">
                      <div style="width: 200px; line-height: 32px; font-weight: bolder" v-if="item.bizOrg">
                        <a-button type="link" size="small" @click="onClickToBizOrgDesign(item.bizOrg.uuid)">
                          {{ item.bizOrg.name }}
                        </a-button>
                      </div>
                      <div style="width: 200px; line-height: 32px">
                        {{ item.bizOrgElementPath }}
                      </div>
                      <div style="width: 200px; line-height: 32px">
                        <a-tag>
                          {{ item.bizOrgRoleName }}
                        </a-tag>
                      </div>
                    </div>
                  </template>
                  <a-button type="link">查看业务组织信息</a-button>
                </a-popover>
              </a-form-model-item>
            </div>
          </Scroll>
        </a-tab-pane>
        <a-tab-pane key="roleInfo" tab="角色信息">
          <div style="margin-bottom: 12px" v-if="editable || (userAuthority.includes('editRole') && uuid) || uuid == undefined">
            <Drawer
              title="配置用户角色"
              :ok="confirmSaveUserRole"
              :cancel="e => e && e.pointerType != ''"
              class="user-role-drawer"
              mask
              :okText="confirmRoleOkText"
            >
              <a-button icon="plus">配置用户角色</a-button>
              <template slot="content">
                <div style="width: 540px">
                  <!-- <a-row style="margin-bottom: 10px">
                      <a-col :span="12">
                        <a-input-search @search="onSearchRole" allow-clear />
                      </a-col>
                    </a-row> -->
                  <RoleSelectPanel v-model="userRoleCheckedKeys" @change="changeRoleSelect" />
                </div>
              </template>
            </Drawer>
          </div>
          <a-table
            :scroll="{ y: 400 }"
            row-key="_uuid"
            :data-source="userRoles"
            :pagination="false"
            :columns="roleColumns"
            class="pt-table"
            bordered
          >
            <template slot="fromSlot" slot-scope="text, record">
              <template v-if="record.appId && roleFromMap[record.appId]">
                <a target="_blank" :href="roleFromMap[record.appId].url" v-if="roleFromMap[record.appId].url">
                  {{ roleFromMap[record.appId].title }}
                </a>
                <template v-else>
                  {{ roleFromMap[record.appId].title }}
                </template>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="authInfo" tab="权限信息" v-if="form.userId">
          <OrgRoleResult v-if="!loading" :user-id="form.userId" :org-version-uuid="orgVersionUuid" panelHeight="400px" />
        </a-tab-pane>
        <!-- <a-tab-pane key="reportInfo" tab="汇报关系"></a-tab-pane> -->
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style lang="less" scoped>
.user-info-avatar-operation {
  opacity: 0;
  position: absolute;
  bottom: 60px;
  left: 50%;
  transform: translate(-50%, 0);
}
.user-avatar:hover {
  .user-info-avatar-operation {
    opacity: 1;
  }
}
.org-select-component {
  vertical-align: middle;
  display: inline-block;
  width: 100%;
}
</style>
<script type="text/babel">
import { debounce } from 'lodash';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import RoleSelectPanel from './authroize-role/role-select-panel.vue';
import OrgRoleResult from './authroize-role/org-role-result.vue';
import { generateId } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
// import { pinyin } from 'pinyin-pro';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'OrgUserInfo',
  props: {
    userUuid: String,
    orgVersionUuid: String,
    orgVersionId: String,
    orgElementId: String,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    },
    tabsWidth: String,
    passwordRules: Object,
    userAuthority: Array
  },
  components: { Drawer, RoleSelectPanel, OrgRoleResult, OrgSelect, WI18nInput },
  inject: ['pageContext', 'getOrgElementTreeNodeMap', 'getOrgElementTreeData', 'orgSetting', 'orgElementManagementMap'],
  data() {
    let rules = {
      userName: [{ required: true, message: '姓名必填', trigger: ['blur'] }],
      mail: [{ pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/, message: '请输入正确的邮件格式', trigger: ['blur'] }],
      businessPhoneNumber: {
        pattern: /^\d{4}-\d{7}$|^0\d{2,3}-\d{7,8}$/,
        message: '请输入正确的办公电话格式',
        trigger: ['blur']
      },
      familyPhoneNumber: {
        pattern: /^\d{4}-\d{7}$|^0\d{2,3}-\d{7,8}$/,
        message: '请输入正确的家庭电话格式',
        trigger: ['blur']
      },
      ceilPhoneNumber: [
        {
          pattern: /^(1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[2-8]|8[0-9]|9[0-35-9])\d{8})$/,
          message: '请输入正确的手机号码',
          trigger: ['blur']
        }
      ],
      idNumber: [
        {
          pattern: /^[1-9]\d{5}(19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[Xx\d]$/,
          message: '请输入正确的身份号码',
          trigger: ['blur']
        }
      ]
    };
    let uuid = this.userUuid;
    if (!uuid) {
      rules.loginName = [
        { required: true, message: '账号必填', trigger: ['blur'] },
        {
          pattern: /^[a-zA-Z0-9_]{2,20}$/,
          message: '账号只能是大小写字母，数字，_ 组成，长度为2-20个字符',
          trigger: ['blur', 'change']
        },
        { trigger: ['blur', 'change'], validator: this.checkLoginNameUnique }
      ];
      rules.password = [{ required: true, message: '登录密码必填', trigger: ['blur'] }];
      rules.confirmPassword = [
        { required: true, message: '请确认密码', trigger: ['blur'] },
        { trigger: ['blur'], validator: this.passwordDoubleCheck }
      ];
    }

    return {
      loading: uuid != undefined,
      uuid,
      activeKey: 'acctInfo',
      labelCol: {
        span: 5,
        style: {
          width: '80px'
        }
      },
      jobIds: [],
      userRoleCheckedKeys: [],
      userRoleChecked: [],
      userRoles: [],
      roleSource: [],
      wrapperCol: { span: 19 },
      form: { avatar: undefined },
      rules,
      extAttrs: ['idNumber', 'familyPhoneNumber', 'businessPhoneNumber', 'workLocation'],
      roleColumns: [
        {
          title: '名称',
          dataIndex: 'name',
          width: 200
        },
        { title: 'ID', dataIndex: 'id', width: 220 },
        { title: '来源', dataIndex: 'from', scopedSlots: { customRender: 'fromSlot' } }
      ],
      roleFromMap: {},
      orgUsers: [],
      orgUserColumns: [
        { title: '部门', dataIndex: 'deptId', scopedSlots: { customRender: 'deptSlot' }, width: 150 },
        { title: '职位', dataIndex: 'jobId', scopedSlots: { customRender: 'jobSlot' }, width: 150 },
        { title: '直接汇报人', dataIndex: 'reportTos', scopedSlots: { customRender: 'reportSlot' } },
        { dataIndex: 'operation', scopedSlots: { customRender: 'operationSlot' }, width: 150 }
      ],
      initPwd: false,
      scrollContentHeights: {
        userInfo: '100%',
        workInfo: '100%'
      },
      bizOrgUserInfos: [],
      languageOptions: [],
      collapseI18nUserName: true,
      passwordMaxLength: 20,
      passwordMinLength: 4,
      passwordPlaceholder: '',
      orgSelectParams: {}
    };
  },
  computed: {
    confirmRoleOkText() {
      return `确定 ( ${this.userRoleCheckedKeys.length} )`;
    },
    vOrgElementTreeData() {
      return this.getOrgElementTreeData();
    },
    jobOptions() {
      let treeKeyNodeMap = this.getOrgElementTreeNodeMap(),
        jobOptions = [];
      for (let key in treeKeyNodeMap) {
        let n = treeKeyNodeMap[key],
          label = n.label;
        if (n.data.type == 'job') {
          if (n.parentKey) {
            let p = treeKeyNodeMap[n.parentKey];
            label = p.label + '/' + label;
          }
          jobOptions.push({ label, value: n.data.id });
        }
      }
      return jobOptions;
    },
    jobIdValues() {
      let v = [];
      for (let i = 0, len = this.jobIds.length; i < len; i++) {
        if (this.jobIds[i].value) {
          v.push(this.jobIds[i].value);
        }
      }
      return v;
    },

    editable() {
      return this.displayState == 'edit';
    }
  },
  watch: {},
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
    this.getUserInfo();
    this.fetchRoleSource();
    if (this.uuid == undefined && this.orgElementId) {
      // 节点下新增用户，自动工作信息里的组织信息内容
      let map = this.getOrgElementTreeNodeMap();
      for (let key in map) {
        if (map[key].id == this.orgElementId) {
          if (map[key].userAuthority && !map[key].userAuthority.includes('joinUser')) {
            break;
          }
          let _orgUser = {
            _uuid: generateId(),
            jobId: undefined,
            deptId: undefined,
            reportTos: []
          };
          this.orgUsers.push(_orgUser);
          if (this.orgElementId.startsWith('J_')) {
            _orgUser.jobId = this.orgElementId;
            let parent = map[map[key].parentKey];
            if (parent) {
              _orgUser.deptId = parent.id;
            }
            _orgUser.type = 'PRIMARY_JOB_USER';
          } else {
            _orgUser.deptId = this.orgElementId;
            _orgUser.type = 'MEMBER_USER';
          }
          break;
        }
      }
    }
    this.setNewPasswordRules();
    this.setUserUniqRules();
    if (this.orgElementManagementMap != undefined && Object.keys(this.orgElementManagementMap).length > 0) {
      // 只展示管理节点下的组织节点
      this.orgSelectParams.includeKeys = Object.keys(this.orgElementManagementMap).filter(function (key) {
        return (
          key.indexOf('_') != -1 &&
          _this.orgElementManagementMap[key].userAuthority &&
          _this.orgElementManagementMap[key].userAuthority.includes('joinUser')
        );
      });
      this.orgSelectParams.excludeKeys = Object.keys(this.orgElementManagementMap).filter(function (key) {
        if (
          key.indexOf('_') != -1 &&
          _this.orgElementManagementMap[key].userAuthority &&
          !_this.orgElementManagementMap[key].userAuthority.includes('joinUser')
        ) {
          return true;
        }
        return false;
      });
    }
  },
  mounted() {},
  methods: {
    setUserUniqRules() {
      let _this = this;
      if (this.orgSetting != undefined) {
        let fieldNames = {
          ceilPhoneNumber: '手机号码',
          userName: '姓名',
          idNumber: '身份证号码',
          mail: '邮箱'
        };
        for (let p in this.orgSetting) {
          if (this.orgSetting[p].category == 'USER_UNIQUE_RULE' && this.orgSetting[p].enable) {
            let fields = [];
            if (['USER_ID_NUMBER_UNIQUE', 'USER_NAME_UNIQUE', 'TEL_UNIQUE'].includes(p)) {
              fields.push({ USER_ID_NUMBER_UNIQUE: 'idNumber', USER_NAME_UNIQUE: 'userName', TEL_UNIQUE: 'ceilPhoneNumber' }[p]);
            } else {
              // 组合字段
              if (
                this.orgSetting[p].attrValueJson &&
                this.orgSetting[p].attrValueJson.fields &&
                this.orgSetting[p].attrValueJson.fields.length > 0
              ) {
                fields = this.orgSetting[p].attrValueJson.fields;
              }
            }

            if (fields.length > 0) {
              let errorMsg = fields.length == 1 ? `${fieldNames[fields[0]]}已被使用` : ``;
              if (fields.length > 1) {
                let names = [];
                for (let f of fields) {
                  names.push(fieldNames[f]);
                }
                errorMsg = `${names.join('、')}已被使用`;
              }
              for (let f of fields) {
                this.rules[f].push({
                  trigger: ['change', 'blur'],
                  validator: debounce(function (rule, value, callback) {
                    let u = {};
                    for (let prop of fields) {
                      if (_this.form[prop] == undefined || _this.form[prop] === '') {
                        callback(undefined);
                        return;
                      }
                      u[prop] = _this.form[prop];
                    }
                    if (_this.form.uuid != undefined) {
                      u.uuid = _this.form.uuid;
                    }
                    _this.checkUserExist(u).then(exist => {
                      // 用户姓名、身份证必须组合唯一
                      callback(exist ? errorMsg : undefined);
                    });
                  }, 500)
                });
              }
            }
          }
        }
      }
    },
    setNewPasswordRules() {
      if (this.uuid == undefined) {
        let _this = this;
        let isEmptyRule = Object.keys(this.passwordRules).length == 0;
        let newPwdRule = { trigger: ['blur', 'change'] };
        if (!isEmptyRule) {
          let requireCnt = parseInt(this.passwordRules.charTypeNumRequire);
          this.passwordMaxLength = parseInt(this.passwordRules.maxLength);
          this.passwordMinLength = parseInt(this.passwordRules.minLength);
          let mustContainsUpperLowerCaseChar = this.passwordRules.mustContainsUpperLowerCaseChar === 'true';
          let placeholder =
            this.passwordMinLength != this.passwordMaxLength
              ? `${this.passwordMinLength}-${this.passwordMaxLength}位长度密码`
              : `${this.passwordMaxLength}位长度密码`;
          placeholder += ', 要求字母、数字、特殊字符中';
          placeholder += requireCnt == 1 ? '至少包含一种' : requireCnt == 2 ? '至少包含两种' : '包含三种';
          if (mustContainsUpperLowerCaseChar) {
            placeholder += ', 且密码包含字母时必须同时包含大小写字母';
          }
          this.passwordPlaceholder = placeholder;
          newPwdRule.validator = debounce(function (rule, value, callback) {
            _this.passwordPatternValidate(rule, value, callback);
          }, 500);
        } else {
          newPwdRule.pattern = /^[a-zA-Z0-9~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]{4,20}$/;
          newPwdRule.message = '密码只能是字母、数字、特殊字符中至少包含1种，4~20位';
          this.passwordPlaceholder = '至少包含字母、数字、特殊字符, 4-20位';
        }
        this.rules.password.push(newPwdRule);
      }
    },

    passwordPatternValidate(rule, value, callback) {
      let requireCnt = parseInt(this.passwordRules.charTypeNumRequire),
        mustContainsUpperLowerCaseChar = this.passwordRules.mustContainsUpperLowerCaseChar === 'true',
        minLength = parseInt(this.passwordRules.minLength),
        maxLength = parseInt(this.passwordRules.maxLength);
      let rst =
        value.length >= minLength &&
        value.length <= maxLength &&
        this.isValidPasswordFlexible(value, requireCnt, mustContainsUpperLowerCaseChar);

      callback(!rst ? this.passwordPlaceholder : undefined);
    },
    isValidPasswordFlexible(password, minRequirements, requireBothCases) {
      if (!password || password.length === 0 || minRequirements < 1 || minRequirements > 3) {
        return false;
      }

      // 检查各种字符类型
      const hasLowerCase = /[a-z]/.test(password);
      const hasUpperCase = /[A-Z]/.test(password);
      const hasDigit = /\d/.test(password);
      const hasSpecial = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password);

      // 处理字母要求
      let hasLetter = false;
      if (hasLowerCase || hasUpperCase) {
        // 存在字母的情况下
        if (requireBothCases) {
          hasLetter = hasLowerCase && hasUpperCase; // 必须同时有大小写
        } else {
          hasLetter = hasLowerCase || hasUpperCase; // 有任意一种即可
        }
      }

      // 计算满足的条件数量（字母算作一个条件）
      let metConditions = 0;
      if (hasLetter) metConditions++;
      if (hasDigit) metConditions++;
      if (hasSpecial) metConditions++;

      // 检查是否满足最小要求数量
      return metConditions >= minRequirements;
    },
    onChangeI18nUserName() {
      if (this.form.i18n && this.form.i18n.en_US) {
        this.form.enName = this.form.i18n.en_US.user_name;
      }
    },
    fetchLocaleOptions() {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
          .then(({ data }) => {
            if (data.code == 0) {
              for (let d of data.data) {
                this.languageOptions.push({
                  label: d.name,
                  value: d.locale,
                  description: d.remark || d.name,
                  transCode: d.translateCode
                });
              }
            }
            resolve();
          })
          .catch(error => {});
      });
    },
    onChangeUserName() {
      // if (this.form.userName) {
      //   this.$set(this.form, 'pinYin', pinyin(this.form.userName, { mode: 'surname', toneType: 'none', separator: ' ', surname: 'head' }));
      // }
    },
    onClickToBizOrgDesign(uuid) {
      window.open(`/biz-org/manager?uuid=${uuid}`, '_blank');
    },
    fetchBizOrgUserInfo(userId) {
      this.$axios
        .get(`/proxy/api/org/biz/getUserBizOrgElementRoles`, {
          params: {
            userId
          }
        })
        .then(({ data }) => {
          if (data.code == 0) {
            console.log('业务组织用户信息', data.data);
            if (data.data) {
              for (let d of data.data) {
                this.bizOrgUserInfos.push({
                  bizOrg: d.bizOrg,
                  bizOrgElementPath: d.bizOrgElementPath.cnPath,
                  bizOrgRoleName: d.bizOrgRole ? d.bizOrgRole.name : ''
                });
              }
            }
          }
        })
        .catch(error => {});
    },
    onChangeDeptSelect(item) {
      item.jobId = undefined;
    },
    onChangeJobSelect(item) {
      if (item.jobId != undefined && item.type == 'MEMBER_USER') {
        item.type = 'SECONDARY_JOB_USER';
      }
      if (item.jobId == undefined) {
        item.type = 'MEMBER_USER';
      } else if (this.orgUsers.length == 1) {
        item.type = 'PRIMARY_JOB_USER';
      }
    },
    setAsPrimaryJob(item, i) {
      item.type = 'PRIMARY_JOB_USER';
      for (let i = 0, len = this.orgUsers.length; i < len; i++) {
        if (this.orgUsers[i].type == 'PRIMARY_JOB_USER' && this.orgUsers[i]._uuid != item._uuid) {
          this.orgUsers[i].type = 'SECONDARY_JOB_USER';
          break;
        }
      }
      // 交换主职位到第一行
      if (i > 0) {
        let t = this.orgUsers.splice(i, 1)[0];
        this.orgUsers.splice(0, 0, t);
      }
    },
    underJobOptions(deptId) {
      let treeKeyNodeMap = this.getOrgElementTreeNodeMap(),
        jobOptions = [];
      if (deptId == undefined) {
        return [];
      }
      for (let key in treeKeyNodeMap) {
        let n = treeKeyNodeMap[key];
        //   label = n.label;
        if (n.id == deptId) {
          let children = n.children;
          if (children && children.length) {
            for (let i = 0, len = children.length; i < len; i++) {
              if (children[i].id.startsWith('J_')) {
                jobOptions.push({
                  label: children[i].label,
                  value: children[i].id
                });
              }
            }
          } else {
            // 由于权限未展示在右侧组织树, 从父级往下找
            for (let k in treeKeyNodeMap) {
              let child = treeKeyNodeMap[k];
              if (child.parentKey == n.key && child.id.startsWith('J_')) {
                jobOptions.push({
                  label: child.label,
                  value: child.id
                });
              }
            }
          }
          break;
        }
      }

      return jobOptions;
    },
    addOrgInfo() {
      this.orgUsers.push({
        _uuid: generateId(),
        deptId: undefined,
        jobId: undefined,
        type: 'MEMBER_USER',
        reportTos: []
      });
    },
    changeRoleSelect(e) {
      this.userRoleChecked.splice(0, this.userRoleChecked.length);
      this.userRoleChecked.push(...e.selectedRoles);
    },
    confirmSaveUserRole(e) {
      this.userRoles.splice(0, this.userRoles.length);
      this.userRoles.push(...this.userRoleChecked);
      this.form.roleUuids = [...this.userRoleCheckedKeys];
      let appIds = new Set();
      for (let i = 0, len = this.userRoles.length; i < len; i++) {
        appIds.add(this.userRoles[i].appId);
      }
      this.fetchRoleAppInfo(Array.from(appIds));
      e(true);
    },

    save() {
      let _this = this;
      return new Promise((resolve, reject) => {
        this.$loading('保存中');
        this.$refs.form.validate((valid, msg) => {
          if (valid) {
            _this.form.orgVersionUuid = _this.orgVersionUuid;
            _this.form.otherJobIds = [];
            _this.form.mainJobId = null;
            _this.form.orgUsers = [];
            _this.jobIds.forEach(j => {
              if (j.value) {
                if (j.isMainJob) {
                  _this.form.mainJobId = j.value;
                } else {
                  _this.form.otherJobIds.push(j.value);
                }
              }
            });

            if (_this.form.i18n) {
              let i18ns = [];
              for (let locale in _this.form.i18n) {
                if (_this.form.i18n[locale].userName) {
                  i18ns.push({
                    locale: locale,
                    userName: _this.form.i18n[locale].userName
                  });
                }
              }
              _this.form.userNameI18ns = i18ns;
            }

            // 扩展信息
            _this.form.userInfoExts = [];
            for (let attrKey of _this.extAttrs) {
              _this.form.userInfoExts.push({
                attrKey,
                attrValue: _this.form[attrKey]
              });
            }

            // 组织信息
            let orgUserAdded = {};
            if (this.orgUsers.length) {
              let reports = {};
              for (let i = 0, len = this.orgUsers.length; i < len; i++) {
                let o = this.orgUsers[i];
                if (o.type == 'MEMBER_USER') {
                  if (!orgUserAdded[o.deptId]) {
                    _this.form.orgUsers.push({
                      orgElementId: o.deptId,
                      type: 'MEMBER_USER'
                    });
                    reports[o.deptId] = o.reportTos;
                    orgUserAdded[o.deptId] = true;
                  } else {
                    // 合并重复行的直接汇报人
                    reports[o.deptId] = Array.from(new Set((reports[o.deptId] || []).concat(o.reportTos || [])));
                  }
                } else {
                  // 不再重复挂载用户到职位所属部门
                  // if (!orgUserAdded[o.deptId]) {
                  //   _this.form.orgUsers.push({
                  //     orgElementId: o.deptId,
                  //     type: 'MEMBER_USER'
                  //   });
                  // }
                  if (!orgUserAdded[o.jobId]) {
                    _this.form.orgUsers.push({
                      orgElementId: o.jobId,
                      type: o.type
                    });

                    if (orgUserAdded[o.deptId]) {
                      // 移除职位的归属部门
                      for (let j = 0; j < _this.form.orgUsers.length; j++) {
                        if (_this.form.orgUsers[j].orgElementId == o.deptId) {
                          _this.form.orgUsers.splice(j, 1);
                          o.reportTos = Array.from(new Set([].concat(o.reportTos || []).concat(reports[o.deptId] || [])));
                          break;
                        }
                      }
                    }
                    reports[o.jobId] = o.reportTos;
                    orgUserAdded[o.jobId] = true;
                    orgUserAdded[o.deptId] = true;
                  } else {
                    // 合并重复行的直接汇报人
                    reports[o.jobId] = Array.from(new Set(reports[o.jobId].concat(o.reportTos)));
                  }
                }
              }
              _this.form.orgReportTos = reports;
            }
            let formData = JSON.parse(JSON.stringify(_this.form));
            if (_this.systemDefRoleKeys) {
              if (formData.roleUuids == undefined) {
                formData.roleUuids = [];
              }
              formData.roleUuids.push(..._this.systemDefRoleKeys);
            }
            $axios
              .post('/proxy/api/user/org/save', formData)
              .then(({ data }) => {
                _this.$loading(false);
                if (data.code == 0) {
                  _this.$message.success(_this.form.uuid ? '修改用户成功' : '新建用户成功');
                  _this.pageContext.emitEvent('refetchUserCountStatics');
                  //FIXME:
                  resolve();
                }
              })
              .catch(error => {
                _this.$message.error('服务异常');
                _this.$loading(false);
              });
          } else {
            this.$loading(false);
            if (msg.loginName || msg.password || msg.confirmPassword) {
              this.activeKey = 'acctInfo';
              return;
            }
            if (msg.userName) {
              this.activeKey = 'userInfo';
            }
          }
        });
      });
    },
    // 自定义图片上传请求
    customImageRequest(options) {
      if (options.file.type.indexOf('image/') !== 0) {
        this.$message.error('上传文件非图片格式');
        return;
      }

      this.upload(options.file);
    },

    upload(file) {
      let _this = this,
        fileSize = file.size,
        fileName = file.name;
      let formData = new FormData();
      formData.set('frontUUID', file.uid);
      formData.set('md5', file.md5);
      formData.set('localFileSourceIcon', '');
      formData.set('size', fileSize);

      let headers = {
        'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
        'Content-Type': 'multipart/form-data'
      };
      formData.set('file', file);
      $axios
        .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
          headers: headers
        })
        .then(({ data }) => {
          console.log(data);
          if (data.data) {
            _this.form.avatar = data.data[0].fileID;
          }
        })
        .catch(function (error) {
          console.error('上传文件失败, 异常: ', error);
        });
    },

    onClickUploadAvatar() {
      this.$refs.uploadButton.$el.click();
    },
    onSetMainJob(v, i) {
      if (v) {
        this.jobIds.splice(i, 1);
        this.jobIds.forEach(j => {
          j.isMainJob = false;
        });

        this.jobIds.splice(0, 0, { value: v, isMainJob: true });
      }
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    addJob() {
      this.jobIds.push({ value: undefined, isMainJob: false });
    },
    getUserInfo() {
      let _this = this;
      if (this.uuid) {
        $axios
          .get(`/proxy/api/user/org/getUserDetails`, {
            params: {
              userUuid: this.uuid,
              orgVersionUuid: this.orgVersionUuid,
              fetchI18nName: true
            }
          })
          .then(({ data }) => {
            _this.loading = false;
            if (data.code == 0 && data.data) {
              _this.form = data.data;
              // 扩展信息
              if (data.data.userInfoExts) {
                for (let extAttr of data.data.userInfoExts) {
                  if (_this.extAttrs.includes(extAttr.attrKey)) {
                    _this.$set(_this.form, extAttr.attrKey, extAttr.attrValue);
                  }
                }
              }

              // 组织用户信息
              if (_this.form.orgUsers && _this.form.orgUsers.length) {
                let map = {};
                for (let i = 0, len = _this.form.orgUsers.length; i < len; i++) {
                  let o = _this.form.orgUsers[i];
                  if (o.orgElementId) {
                    if (map[o.type] == undefined) {
                      map[o.type] = [];
                    }
                    map[o.type].push(o);
                  }
                }
                // 职位
                let deptIds = [];
                for (let t of ['PRIMARY_JOB_USER', 'SECONDARY_JOB_USER', 'MEMBER_USER']) {
                  if (map[t]) {
                    let list = map[t];
                    for (let i = 0, len = list.length; i < len; i++) {
                      let l = list[i];
                      if (l.orgElementId) {
                        let id = l.orgElementId,
                          userPath = l.userPath,
                          parts = userPath.split('/'),
                          jobId = t != 'MEMBER_USER' ? l.orgElementId : undefined,
                          deptId = t == 'MEMBER_USER' ? id : parts[parts.length - 3];
                        if (t === 'MEMBER_USER' && deptIds.includes(deptId)) {
                          // 作为成员用户，职位已经添加了部门关系，则忽略
                          continue;
                        }
                        let reportTos = _this.form.orgReportTos ? _this.form.orgReportTos[t === 'MEMBER_USER' ? deptId : jobId] || [] : [];
                        deptIds.push(deptId);
                        _this.orgUsers.push({
                          _uuid: generateId(),
                          deptId,
                          jobId,
                          type: t,
                          reportTos
                        });
                      }
                    }
                  }
                }
              }

              this.fetchUserRoleInfo(_this.form.roleUuids);
              this.fetchBizOrgUserInfo(_this.form.userId);

              if (data.data.userNameI18ns) {
                let i18n = {};
                for (let item of data.data.userNameI18ns) {
                  if (i18n[item.locale] == undefined) {
                    i18n[item.locale] = {};
                  }
                  i18n[item.locale].userName = item.userName;
                }
                _this.form.i18n = i18n;
              }
            }
          });
      }
    },
    fetchUserRoleInfo(roleUuids) {
      if (roleUuids.length) {
        $axios
          .post(`/proxy/api/security/role/getRolesByUuids`, roleUuids)
          .then(({ data }) => {
            let appIds = new Set();
            this.systemDefRoleKeys = [];
            for (let i = 0, len = data.data.length; i < len; i++) {
              if (data.data[i].systemDef == 1) {
                // 默认角色由相关功能负责绑定或者解绑
                this.systemDefRoleKeys.push(data.data[i].uuid);
                continue;
              }
              this.userRoles.push(data.data[i]);
              this.userRoleCheckedKeys.push(data.data[i].uuid);
              this.userRoleChecked.push(data.data[i]);
              appIds.add(data.data[i].appId);
            }
            if (appIds.size) {
              this.fetchRoleAppInfo(Array.from(appIds));
            }
          })
          .catch(error => {});
      }
    },
    fetchRoleAppInfo(appIds) {
      let ids = [];
      for (let i = 0, len = appIds.length; i < len; i++) {
        if (!this.roleFromMap[appIds[i]]) {
          ids.push(appIds[i]);
        }
      }

      $axios
        .post(`/proxy/api/app/prod/version/listById`, ids)
        .then(({ data }) => {
          if (data.data) {
            for (let d of data.data) {
              this.$set(this.roleFromMap, d.versionId, {
                title: `产品: ${d.prodName} (${d.version})`,
                url: `/product/assemble/${d.prodId}`
              });
            }
          }
        })
        .catch(error => {});
      $axios
        .post(`/proxy/api/app/module/listById`, ids)
        .then(({ data }) => {
          if (data.data) {
            for (let d of data.data) {
              this.$set(this.roleFromMap, d.id, {
                title: `模块: ${d.name}`,
                url: `/module/assemble/${d.uuid}`
              });
            }
          }
        })
        .catch(error => {});
    },
    fetchRoleSource() {
      $axios
        .post(`/proxy/api/security/role/queryAppRolesByAppIds`, [])
        .then(({ data }) => {
          this.roleSource = data.data;
        })
        .catch(error => {});
    },
    passwordDoubleCheck(rule, value, callback) {
      if (this.form.password && value === this.form.password) {
        callback();
      } else {
        callback('确认密码错误');
      }
    },
    checkUserExist(user) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/user/checkUserExist`, user)
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    },
    checkLoginNameUnique: debounce(function (rule, value, callback) {
      $axios.get(`/proxy/api/user/checkLoginNameOccupied/${value}`, {}).then(({ data }) => {
        if (data.code == 0) {
          callback(data.data ? '账号已使用' : undefined);
        } else {
          callback('服务异常');
        }
      });
    }, 500),
    updateScrollContentHeight(scroll, key) {
      let perfectScrollbarRef = scroll.$refs.perfectScrollbarRef;
      if (perfectScrollbarRef && perfectScrollbarRef.ps && perfectScrollbarRef.ps.contentHeight) {
        this.scrollContentHeights[key] = perfectScrollbarRef.ps.contentHeight + 'px';
      }
    }
  }
};
</script>
