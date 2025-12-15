<template>
  <div class="org-batch-create-user">
    <a-form-model layout="inline" :model="pwd" :rules="pwdRules" ref="pwdForm" class="top-form">
      <a-form-model-item label="初始登录密码" prop="value" style="margin-bottom: 0px" v-if="allowCustomizePwd">
        <a-input-password
          v-model="pwd.value"
          type="password"
          style="width: 400px"
          :max-length="maxLength"
          :placeholder="passwordPlaceholder"
        />
      </a-form-model-item>
      <a-form-model-item label="确认密码" prop="confirmValue" style="margin-bottom: 0px" v-if="allowCustomizePwd">
        <a-input-password v-model="pwd.confirmValue" type="password" style="width: 400px" :max-length="maxLength" />
      </a-form-model-item>
      <div style="float: right; margin-top: 4px">
        <!-- <a-button type="link" v-if="draftDate" @click="clickLoadDraftData">加载上一次保存的草稿数据 ( {{ draftDate }} )</a-button>
        <a-button icon="save" @click="saveAsDraftLocal">保存草稿</a-button> -->
        <!-- <a-button icon="close" @click="clearAll">清空</a-button>
        <a-button icon="plus" type="primary" @click="createUser" :loading="committing">创建</a-button> -->
      </div>
    </a-form-model>
    <div style="padding: 8px 0px">
      <a-button type="link" icon="plus" size="small" @click="addRow">新增</a-button>
      <a-button type="link" size="small" @click="deleteSelectedRows">
        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        删除
      </a-button>
    </div>
    <a-table
      :pagination="false"
      rowKey="uuid"
      :columns="columns"
      :data-source="users"
      :rowSelection="rowSelection"
      :expandedRowKeys.sync="expandedRowKeys"
      :scroll="{ y: 'calc(100vh - 360px)', x: 1800 }"
      class="pt-table"
    >
      <template slot="seqSlot" slot-scope="text, record, index">
        {{ index + 1 }}
        <a-icon type="close-circle" theme="filled" v-show="record.error" style="color: var(--w-danger-color)" />
        <a-icon type="check-circle" theme="filled" v-show="record.success" style="color: var(--w-success-color)" />
        <a-icon type="loading" v-show="record.loading" />
      </template>

      <template slot="userNameSlot" slot-scope="text, record">
        <a-form-model :ref="'userNameForm_' + record.uuid" layout="inline" :model="record" :rules="record._FORM_RULES_">
          <a-form-model-item prop="userName">
            <a-input v-model="record.userName" @change="e => onChangeUserName(e, record)" style="width: 100px" />
          </a-form-model-item>
          <a-form-model-item prop="loginName">
            <a-input v-model="record.loginName" style="width: 100px" />
          </a-form-model-item>
        </a-form-model>
      </template>

      <template slot="deptSlot" slot-scope="text, record">
        <a-select
          :filterOption="filterOption"
          v-model="record.orgElementId"
          :options="deptOptions"
          show-search
          allow-clear
          style="width: 100%"
          @change="(v, node) => onChangeDeptSelect(v, node, record)"
        />
      </template>
      <template slot="jobSlot" slot-scope="text, record">
        <a-select
          :filterOption="filterOption"
          :key="record.key.jobKey"
          v-model="record.jobId"
          :options="filterJobOptions(record.orgElementUuid)"
          show-search
          allow-clear
          style="width: 100%"
        />
      </template>
      <template slot="directReporterSlot" slot-scope="text, record">
        <OrgSelect
          title="选择直接汇报人"
          :orgVersionId="orgVersion.id"
          v-model="record.directReporter"
          :showBizOrgUnderOrg="false"
          :checkableTypes="['job', 'user']"
          :orgType="['MyOrg']"
          :multiSelect="true"
        />
      </template>
      <template slot="genderSlot" slot-scope="text, record">
        <a-radio-group v-model="record.gender" style="display: flex">
          <a-radio value="MALE">男</a-radio>
          <a-radio value="FEMALE">女</a-radio>
        </a-radio-group>
      </template>
      <template slot="userNoSlot" slot-scope="text, record">
        <a-input v-model="record.userNo" />
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" @click="copyRow(record, index)">
          <Icon type="pticon iconfont icon-ptkj-fuzhi"></Icon>
          复制
        </a-button>
        <a-button type="link" size="small" @click="insertRow(index)">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          插入
        </a-button>
        <a-button type="link" size="small" @click="deleteRow(index)">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          删除
        </a-button>
      </template>
      <div slot="expandedRowRender" slot-scope="record" style="padding-left: 60px">
        <a-form-model layout="inline" :model="record" :rules="record._FORM_RULES_" :ref="'extForm_' + record.uuid">
          <a-form-model-item label="手机号码" prop="ceilPhoneNumber">
            <a-input v-model="record.ceilPhoneNumber" />
          </a-form-model-item>
          <a-form-model-item label="身份证号" prop="idNumber"><a-input v-model="record.idNumber" /></a-form-model-item>
          <a-form-model-item label="邮箱" prop="mail"><a-input v-model="record.mail" /></a-form-model-item>
          <a-form-model-item label="家庭电话"><a-input v-model="record.familyPhoneNumber" /></a-form-model-item>
          <a-form-model-item label="办公电话"><a-input v-model="record.businessPhoneNumber" /></a-form-model-item>
        </a-form-model>
      </div>
    </a-table>
    <a-modal title="导入成功的用户密码" v-model="showCreateUserPwdModal" :footer="null">
      <div>
        <Scroll>
          <a-table
            rowKey="loginName"
            :columns="[
              {
                title: '姓名',
                dataIndex: 'userName',
                key: 'userName'
              },
              {
                title: '账号',
                dataIndex: 'loginName',
                key: 'loginName'
              },
              {
                title: '密码',
                dataIndex: 'password',
                key: 'password'
              }
            ]"
            :data-source="createUserPassword"
            :scroll="{ y: 600 }"
            :pagination="false"
          ></a-table>
        </Scroll>
        <a-button type="link" :block="true" @click="copyCreateUserPassword">复制内容</a-button>
      </div>
    </a-modal>
  </div>
</template>
<style lang="less" scoped>
.org-batch-create-user {
  ::v-deep .ant-table .has-error .ant-form-explain {
    position: absolute;
    width: 180px;
  }
  ::v-deep .ant-table .ant-form-inline .ant-form-item-with-help {
    margin-bottom: 30px;
  }

  .top-form {
    padding: var(--w-padding-2xs);
    background: var(--w-gray-color-2);
    border-radius: 4px;
  }
  .pt-table {
    .ant-table-tbody > tr > td {
      padding: var(--w-padding-2xs) var(--w-padding-md) var(--w-padding-2xs) var(--w-padding-xs);
    }
  }
}
</style>
<script type="text/babel">
import { pinyin } from 'pinyin-pro';
import { debounce, clone } from 'lodash';
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';

export default {
  name: 'OrgBatchCreateUser',
  inject: ['getOrgElementTreeNodeMap', 'pageContext', 'orgSetting'],
  props: { orgVersion: Object, passwordRules: Object },
  components: { OrgSelect },
  computed: {
    deptOptions() {
      let nodeMap = this.getOrgElementTreeNodeMap(),
        options = [];
      for (let key in nodeMap) {
        let node = nodeMap[key];
        if (node.id.indexOf('D_') == 0 && node.hidden !== true) {
          if (node.userAuthority && !node.userAuthority.includes('createUser')) {
            continue;
          }
          let n = {
              value: node.id,
              key: node.key
            },
            labels = [node.title];
          if (node.parentKey && nodeMap[node.parentKey]) {
            labels.push(nodeMap[node.parentKey].title);
          }
          n.label = labels.reverse().join('/');
          options.push(n);
        }
      }
      return options;
    }
  },
  data() {
    return {
      users: [],
      maxLength: 20,
      minLength: 4,
      passwordPlaceholder: '',
      allowCustomizePwd: false,
      pwd: {
        value: undefined,
        confirmValue: undefined
      },
      pwdRules: {
        value: [
          {
            required: true,
            message: '请输入密码',
            trigger: 'blur'
          }
        ],
        confirmValue: [
          {
            required: true,
            message: '请输入密码',
            trigger: 'blur'
          },
          {
            validator: this.checkPwdSame,
            trigger: 'blur'
          }
        ]
      },
      extRules: {
        userName: [{ required: true, message: '姓名必填', trigger: ['blur'] }],
        loginName: [
          { required: true, message: '账号必填', trigger: ['blur'] },
          {
            pattern: /^[a-zA-Z0-9_]{2,20}$/,
            message: '账号只能是大小写字母,数字,_组成,长度为2-20个字符',
            trigger: ['blur', 'change']
          },
          { trigger: ['blur'], validator: this.checkLoginNameUnique }
        ],
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
            pattern: /^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/,
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
      },
      columns: [
        {
          title: '序号',
          width: 70,
          scopedSlots: { customRender: 'seqSlot' }
        },
        { dataIndex: 'userName', title: '姓名 / 账号', width: 300, scopedSlots: { customRender: 'userNameSlot' } },
        // { dataIndex: 'loginName', title: '账号', width: 150, scopedSlots: { customRender: 'loginNameSlot' } },
        { dataIndex: 'dept', title: '所在部门', width: 250, scopedSlots: { customRender: 'deptSlot' } },
        { dataIndex: 'job', title: '所在职位', width: 250, scopedSlots: { customRender: 'jobSlot' } },
        { dataIndex: 'directReporter', title: '直接汇报人', scopedSlots: { customRender: 'directReporterSlot' } },
        { dataIndex: 'gender', width: 130, title: '性别', scopedSlots: { customRender: 'genderSlot' } },
        { dataIndex: 'userNo', width: 100, title: '员工编号', scopedSlots: { customRender: 'userNoSlot' } },
        { title: '操作', width: 240, scopedSlots: { customRender: 'operationSlot' } }
      ],
      rowSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange: this.onSelectRowChange
      },
      expandedRowKeys: [],
      draftDate: undefined,
      commitRows: [],
      committing: false,
      createUserPassword: [],
      showCreateUserPwdModal: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.checkLastDraftDataExist();
    this.setNewPasswordRules();
  },
  methods: {
    setNewPasswordRules() {
      let _this = this;
      let isEmptyRule = Object.keys(this.passwordRules).length == 0;
      let newPwdRule = { trigger: ['blur', 'change'] };
      if (!isEmptyRule) {
        this.allowCustomizePwd = this.passwordRules.sysInitPasswordSource === 'customized';
        let requireCnt = parseInt(this.passwordRules.charTypeNumRequire);
        this.maxLength = parseInt(this.passwordRules.maxLength);
        this.minLength = parseInt(this.passwordRules.minLength);
        let mustContainsUpperLowerCaseChar = this.passwordRules.mustContainsUpperLowerCaseChar === 'true';
        let placeholder =
          this.minLength != this.maxLength ? `${this.minLength}-${this.maxLength}位长度密码` : `${this.maxLength}位长度密码`;
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
        newPwdRule.pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[^]{4,20}$/;
        newPwdRule.message = '密码必须包含字母、数字、特殊字符, 4-20位';
        this.passwordPlaceholder = '至少包含字母、数字、特殊字符, 4-20位';
      }

      this.pwdRules.value.push(newPwdRule);
    },
    setUserUniqRules(row) {
      let _this = this;
      let rules = clone(this.extRules);
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
                rules[f].push({
                  trigger: ['change', 'blur'],
                  validator: function (rule, value, callback) {
                    console.log(this);
                    let u = {};
                    for (let prop of fields) {
                      if (row[prop] == undefined || row[prop] === '') {
                        callback(undefined);
                        return;
                      }
                      u[prop] = row[prop];
                    }

                    _this.checkUserExist(u, fields, row.uuid).then(exist => {
                      // 用户姓名、身份证必须组合唯一
                      callback(exist === false ? undefined : typeof exist === 'string' ? exist : errorMsg);
                    });
                  }
                });
              }
            }
          }
        }
      }
      row._FORM_RULES_ = rules;
    },
    checkUserExist(user, fields, uuid) {
      return new Promise((resolve, reject) => {
        for (let i = 0, len = this.users.length; i < len; i++) {
          let u = this.users[i];
          if (u.uuid != uuid) {
            // 判断是否属性相同
            let same = true;
            // ,
            //   nameMap = {
            //     ceilPhoneNumber: '手机号码',
            //     userName: '姓名',
            //     idNumber: '身份证号码',
            //     mail: '邮箱'
            //   },
            //   names = []
            for (let f of fields) {
              if (u[f] !== user[f]) {
                same = false;
                break;
              }
              // names.push(nameMap[f]);
            }
            if (same) {
              resolve(true); //`${names.join('、')}已被使用在第${i + 1}行`);
              return;
            }
          }
        }
        $axios
          .post(`/proxy/api/user/checkUserExist`, user)
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
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
    checkLastDraftDataExist() {
      let str = window.localStorage.getItem(`batchCreateOrgUser_${this.orgVersion.id}`);
      if (str) {
        let data = JSON.parse(str);
        this.draftDate = data.date;
      }
    },
    clearAll() {
      this.users.splice(0, this.users.length);
      this.expandedRowKeys.splice(0, this.expandedRowKeys.length);
      this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
      this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
      this.$refs.pwdForm.resetFields();
    },
    checkLoginNameUnique(rule, value, callback) {
      let cnt = 0;
      for (let i = 0, len = this.users.length; i < len; i++) {
        if (this.users[i].loginName == value) {
          cnt++;
          if (cnt > 1) {
            callback('账号重复');
            break;
          }
        }
      }

      $axios.get(`/proxy/api/user/checkLoginNameOccupied/${value}`, {}).then(({ data }) => {
        if (data.code == 0) {
          callback(data.data ? '账号已使用' : undefined);
        } else {
          callback('服务异常');
        }
      });
    },
    createUser() {
      let _this = this;
      _this.createUserPassword.splice(0, _this.createUserPassword.length);
      this.$refs.pwdForm.validate(p => {
        if (!_this.allowCustomizePwd || p) {
          if (_this.users.length) {
            _this.$loading();
            _this.committing = true;
            _this.commitRows.splice(0, this.commitRows.length);

            for (let i = 0, len = _this.users.length; i < len; i++) {
              _this.commitRows.push(i);
              _this
                .validateRow(i)
                .then(index => {
                  _this.commitRow(index);
                })
                .catch(i => {
                  _this.commitRowDone(i);
                });
            }
          }
        }
      });
    },
    validateRow(i) {
      let _this = this;
      let user = this.users[i];
      return new Promise((resolve, reject) => {
        _this.$refs['userNameForm_' + user.uuid].validate(pass => {
          _this.$set(user, 'error', !pass);
          _this.$set(user, 'success', false);
          if (!pass) {
            _this.commitRowDone(i);
          } else {
            if (_this.$refs['extForm_' + user.uuid]) {
              _this.$refs['extForm_' + user.uuid].validate(_pass => {
                if (pass && _pass) {
                  resolve(i);
                } else {
                  _this.commitRowDone(i);
                  _this.$set(user, 'error', !_pass);
                  if (!_pass) {
                    let idx = _this.expandedRowKeys.indexOf(user.uuid);
                    if (idx == -1) {
                      _this.expandedRowKeys.push(user.uuid);
                    }
                  }
                }
              });
            } else {
              resolve(i);
            }
          }
        });
      });
    },
    commitRowDone(i) {
      let index = this.commitRows.indexOf(i);
      if (index !== -1) {
        this.commitRows.splice(index, 1);
      }
      this.$set(this.users[i], 'loading', false);
    },
    commitRow(i) {
      return new Promise((resolve, reject) => {
        let user = this.users[i];
        let form = {
          orgVersionUuid: this.orgVersion.uuid,
          orgUsers: [],
          orgReportTos: {},
          userName: user.userName,
          loginName: user.loginName,
          gender: user.gender,
          ceilPhoneNumber: user.ceilPhoneNumber,
          mail: user.mail,
          userNo: user.userNo,
          userInfoExts: []
        };
        if (this.passwordRules.sysInitPasswordSource == 'random') {
          form.password = this.createRandomPassword();
        } else {
          form.password = this.pwd.confirmValue;
        }
        for (let attrKey of ['idNumber', 'familyPhoneNumber', 'businessPhoneNumber']) {
          if (user[attrKey] != undefined) {
            form.userInfoExts.push({
              attrKey,
              attrValue: user[attrKey]
            });
          }
        }
        if (user.jobId != undefined) {
          form.orgUsers.push({
            type: 'SECONDARY_JOB_USER',
            orgElementId: user.jobId
          });
          if (user.directReporter != undefined) {
            form.orgReportTos[user.jobId] = user.directReporter;
          }
        } else if (user.orgElementId != undefined) {
          form.orgUsers.push({
            type: 'MEMBER_USER',
            orgElementId: user.orgElementId
          });
          if (user.directReporter != undefined) {
            form.orgReportTos[user.orgElementId] = user.directReporter;
          }
        }

        $axios
          .post('/proxy/api/user/org/save', form)
          .then(({ data }) => {
            this.commitRowDone(i);
            if (data.code == 0) {
              this.$set(user, 'loading', false);
              this.$set(user, 'success', true);
              this.createUserPassword.push({ loginName: user.loginName, userName: user.userName, password: form.password });
              resolve();
            } else {
              this.$set(user, 'error', true);
              console.error(`序号: ${i + 1} , 批量创建用户 [${form.userName}] 失败: `, data);
              reject();
            }
          })
          .catch(error => {
            this.commitRowDone(i);
            this.$set(user, 'error', true);
            console.error(`序号: ${i + 1} , 批量创建用户 [${form.userName}] 失败: `, error);
            reject();
          });
      });
    },
    clickLoadDraftData() {
      let str = window.localStorage.getItem(`batchCreateOrgUser_${this.orgVersion.id}`);
      if (str) {
        let data = JSON.parse(str);
        this.users = data.users;
        this.pwd.value = data.pwd.value;
        this.pwd.confirmValue = data.pwd.confirmValue;
        this.draftDate = undefined;
        window.localStorage.removeItem(`batchCreateOrgUser_${this.orgVersion.id}`);
      }
    },
    createRandomPassword() {
      const length = Math.floor(Math.random() * (this.maxLength - this.minLength + 1)) + this.minLength;
      // 定义字符集
      const lowercase = 'abcdefghijklmnopqrstuvwxyz';
      const uppercase = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
      const numbers = '0123456789';
      const symbols = '!@#$%^&*()_+~`|}{[]\\:;?><,./-=';

      // 确保密码包含所有类型字符
      let password = [
        lowercase[Math.floor(Math.random() * lowercase.length)],
        uppercase[Math.floor(Math.random() * uppercase.length)],
        numbers[Math.floor(Math.random() * numbers.length)],
        symbols[Math.floor(Math.random() * symbols.length)]
      ];

      // 合并所有字符集
      const allChars = lowercase + uppercase + numbers + symbols;

      // 填充剩余长度
      for (let i = 4; i < length; i++) {
        password.push(allChars[Math.floor(Math.random() * allChars.length)]);
      }

      // 打乱顺序
      return password.sort(() => Math.random() - 0.5).join('');
    },
    saveAsDraftLocal() {
      window.localStorage.setItem(
        `batchCreateOrgUser_${this.orgVersion.id}`,
        JSON.stringify({
          pwd: this.pwd,
          users: this.users,
          date: new Date().format('yyyy-MM-DD HH:mm:ss')
        })
      );
      this.$message.success('本地保存成功');
    },
    onSelectRowChange(selectedRowKeys, selectedRows) {
      this.rowSelection.selectedRowKeys = selectedRowKeys;
      this.rowSelection.selectedRows = selectedRows;
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    onChangeDeptSelect(value, node, item) {
      item.orgElementUuid = node.key;
      item.key.jobKey = generateId();
      item.jobId = undefined;
    },
    filterJobOptions(id) {
      if (!id) {
        return [];
      }
      let nodeMap = this.getOrgElementTreeNodeMap(),
        options = [],
        node = nodeMap[id];

      let findJob = n => {
        if (n.children) {
          for (let i = 0, len = n.children.length; i < len; i++) {
            if (n.children[i].id.indexOf('J_') == 0) {
              options.push({
                label: n.title + '/' + n.children[i].title,
                value: n.children[i].id
              });
              findJob(n.children[i]);
            }
          }
        }
      };
      findJob(node);

      return options;
    },
    copyRow(item, index) {
      this.users.splice(index + 1, 0, {
        userName: item.userName,
        loginName: item.loginName,
        gender: item.gender,
        uuid: generateId(),
        orgElementId: item.orgElementId,
        orgElementUuid: item.orgElementUuid,
        jobId: item.jobId,
        userNo: item.userNo,
        directReporter: item.directReporter,
        idNumber: item.idNumber,
        ceilPhoneNumber: item.ceilPhoneNumber,
        familyPhoneNumber: item.familyPhoneNumber,
        mail: item.mail,
        businessPhoneNumber: item.businessPhoneNumber,
        key: {
          jobKey: generateId()
        }
      });
    },
    deleteRow(index) {
      this.users.splice(index, 1);
    },
    insertRow(index) {
      this.users.splice(index + 1, 0, {
        userName: undefined,
        loginName: undefined,
        gender: 'MALE',
        uuid: generateId(),
        orgElementId: undefined,
        jobId: undefined,
        directReporter: [],
        key: {
          jobKey: generateId()
        }
      });
    },
    deleteSelectedRows() {
      for (let i = 0; i < this.users.length; i++) {
        if (this.rowSelection.selectedRowKeys.includes(this.users[i].uuid)) {
          this.users.splice(i--, 1);
        }
      }
      this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
      this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
    },
    addRow() {
      let u = {
        userName: undefined,
        loginName: undefined,
        gender: 'MALE',
        uuid: generateId(),
        orgElementId: undefined,
        jobId: undefined,
        directReporter: [],
        key: {
          jobKey: generateId()
        }
      };
      this.setUserUniqRules(u);
      this.users.push(u);
    },
    onChangeUserName: debounce(function (e, item) {
      // if (item.loginName == undefined) {
      item.loginName = pinyin(item.userName, { mode: 'surname', toneType: 'none', separator: '' });
      // }
    }, 300),
    checkPwdSame(rule, value, callback) {
      if (this.pwd.value != this.pwd.confirmValue) {
        callback('密码不一致');
      } else {
        callback();
      }
    },
    copyCreateUserPassword(e) {
      let _this = this;
      let str = [];
      for (let item of this.createUserPassword) {
        str.push(item.userName + '\t' + item.loginName + '\t' + item.password);
      }
      copyToClipboard(str.join('\n'), e, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    }
  },
  watch: {
    commitRows: {
      deep: true,
      handler(v, o) {
        if (this.committing && v.length == 0) {
          // 提交结束了
          this.committing = false;
          this.pageContext.emitEvent('refreshUserCountStatics');
          this.pageContext.emitEvent('OrgVersionUserTableRefetch');
          this.$loading(false);
          if (!this.allowCustomizePwd && this.createUserPassword.length > 0) {
            this.showCreateUserPwdModal = true;
          }
        }
      }
    }
  }
};
</script>
