<template>
  <div>
    <div v-if="editable" style="margin-bottom: 10px">
      <template v-if="orgRoleUserView">
        <Modal title="添加成员" :ok="confirmOrgRoleUserAdd" :width="500" :destroyOnClose="true" mask>
          <a-button icon="plus" type="primary">添加成员</a-button>
          <template slot="content">
            <OrgRoleAddUserForm :org-version="orgVersion" ref="orgRoleAddUserForm" :default-org-role-uuid="orgRoleUuid" />
          </template>
        </Modal>
        <a-button @click="e => removeOrgRoleMem(e)">移出成员</a-button>
      </template>
      <template v-else>
        <Modal
          v-if="userAuthority.includes('createUser')"
          title="新建用户"
          :ok="confirmSaveUserInfo"
          :width="900"
          :destroyOnClose="true"
          mask
          centered
        >
          <a-button type="primary" icon="plus">新建用户</a-button>
          <template slot="content">
            <OrgUserInfo
              :orgVersionUuid="orgVersionUuid"
              :org-version-id="orgVersion.id"
              :org-element-id="orgElementId"
              tabsWidth="140px"
              :password-rules="passwordRules"
              v-if="!passwordRuleLoading"
              :user-authority="userAuthority"
              ref="newOrgUserInfo"
            />
          </template>
        </Modal>
        <Modal v-if="userAuthority.includes('joinUser')" title="添加用户" :ok="confirmAddUserInfo" :width="500" :destroyOnClose="true" mask>
          <a-button>添加用户</a-button>
          <template slot="content">
            <OrgQuickJoinUserForm ref="orgQuickJoinUserForm" :orgVersion="orgVersion" />
          </template>
        </Modal>
        <a-button @click="removeOrgUser">移出用户</a-button>
        <Modal
          title="批量创建用户"
          v-if="userAuthority.includes('createUser')"
          :destroyOnClose="true"
          v-model="batchCreateUserVisible"
          width="calc(100% - 200px)"
          mask
          maxHeight="calc(-200px + 100vh)"
          :bodyStyle="{ height: 'calc(-200px + 100vh)' }"
          centered
        >
          <a-button>批量创建</a-button>
          <template slot="content">
            <OrgBatchCreateUser
              :orgVersion="orgVersion"
              :passwordRules="passwordRules"
              v-if="!passwordRuleLoading"
              ref="orgBatchCreateUserRef"
            />
          </template>
          <template slot="footer">
            <a-button @click="onBatchCreateUserCancel">取消</a-button>
            <a-button type="danger" @click="onBatchCreateUserClearAll">清空</a-button>
            <a-button type="primary" @click="onBatchCreateUserConfirm">创建</a-button>
          </template>
        </Modal>
        <a-button icon="search" @click="fetchNoDeptJobUser()">无部门人员</a-button>
        <a-dropdown>
          <a-button>
            <Icon type="iconfont icon-ptkj-gengduocaidan"></Icon>
            更多
          </a-button>
          <a-menu slot="overlay" @click="handleMoreOperationClick">
            <!-- <a-menu-item key="batchResetPwd">批量导入</a-menu-item>
            <a-menu-item key="batchResetPwd">批量导出</a-menu-item>
            <a-menu-item key="batchResetPwd">批量调整</a-menu-item> -->

            <a-menu-item key="batchResetPwd">批量重置密码</a-menu-item>
            <a-menu-item key="batchDelete" v-if="userAuthority.includes('deleteUser')">批量删除用户</a-menu-item>
          </a-menu>
        </a-dropdown>
      </template>
      <div style="float: right; display: inline-flex">
        <a-input
          v-model="keyword"
          placeholder="姓名 / 账号 / 编号"
          @keyup.enter="onSearch"
          allow-clear
          style="width: 320px; margin-right: 8px"
        />
        <a-button type="primary" @click="onSearch">搜索</a-button>
      </div>
    </div>

    <a-table
      :bordered="false"
      :data-source="rows"
      :columns="columns"
      rowKey="uuid"
      :loading="loading"
      :row-selection="rowSelection"
      :pagination="pagination"
      @change="onTableChange"
      class="pt-table org-version-user-table pt-empty"
      style="--w-table-thead-td-text-color: var(--w-text-color-dark)"
    >
      <template slot="userNameSlot" slot-scope="text, record">
        <a-avatar
          :src="record.avatar ? '/proxy/org/user/view/photo/' + record.avatar : null"
          style="background: linear-gradient(180deg, var(--w-primary-color-4) 0%, var(--w-primary-color) 100%)"
        >
          <span v-if="!record.avatar">
            {{ text && text.slice(0, 1) }}
          </span>
        </a-avatar>
        {{ text }}
      </template>
      <template slot="isAccountNonLockedSlot" slot-scope="text, record">
        <a-tag v-if="text == '1'" color="green" class="tag-no-border">正常</a-tag>
        <a-tag color="red" class="tag-no-border" v-else>冻结</a-tag>
      </template>
      <template slot="operationSlot" slot-scope="record, index">
        <template v-if="orgRoleUserView">
          <a-button size="small" type="link" @click="e => removeOrgRoleMem(e, record.userId)">移出成员</a-button>
        </template>
        <template v-else>
          <Modal
            :title="userInfoEditable ? '编辑' : '详情'"
            :ok="e => confirmSaveUserInfo(e, record.uuid)"
            :width="900"
            :destroyOnClose="true"
            mask
            centered
            :okButtonProps="{
              style: {
                display: userInfoEditable ? 'inline-block' : 'none'
              }
            }"
          >
            <a-button v-if="userInfoEditable" size="small" type="link">
              <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
              编辑
            </a-button>
            <a-button v-else size="small" type="link">
              <Icon type="pticon iconfont icon-szgy-zonghechaxun" />
              查看
            </a-button>
            <template slot="content">
              <OrgUserInfo
                :user-authority="userAuthority"
                :user-uuid="record.uuid"
                :org-version-id="orgVersion.id"
                :orgVersionUuid="orgVersionUuid"
                :org-element-id="orgElementId"
                :displayState="userInfoEditable ? 'edit' : 'label'"
                tabsWidth="140px"
                ref="orgUserInfo"
              />
            </template>
          </Modal>
          <a-dropdown v-if="editable">
            <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-gengduocaidan" /></a-button>
            <a-menu slot="overlay" @click="e => handleMoreMenuItemClick(e, record, index)">
              <a-menu-item key="lock" v-if="userAuthority.includes('lockUser')">
                {{ record.isAccountNonLocked == '1' ? '冻结' : '解冻' }}
              </a-menu-item>
              <a-menu-item key="delete" v-if="userAuthority.includes('deleteUser')">删除</a-menu-item>
              <a-menu-item key="resetAccountPwd">重置密码</a-menu-item>
            </a-menu>
          </a-dropdown>
        </template>
      </template>
    </a-table>
    <Pagination v-model="pagination" @change="onChangePagination"></Pagination>

    <a-modal
      title="确认重置密码"
      v-model="confirmResetPwdModalVisible"
      @ok="confirmResetPassword"
      class="pt-modal"
      :bodyStyle="{ height: '260px' }"
      mask
    >
      <div>
        <p :title="waitResetPwdUsers.map(user => user.userName)">确认要重置用户[{{ waitResetPwdUsersLabel }}]的密码?</p>
        <div v-if="allowCustomizeResetPwd">
          <a-form-model layout="inline" :model="resetPwd" :rules="resetPwd.rules" ref="resetPwdForm">
            <a-form-model-item :label="null" prop="value" style="margin-bottom: 0px">
              <a-input-password
                v-model="resetPwd.value"
                type="password"
                style="width: 400px"
                :max-length="passwordMaxLength"
                :placeholder="passwordPlaceholder"
              />
            </a-form-model-item>
          </a-form-model>
          <div class="pt-tip-block" style="margin-top: 12px">
            {{ passwordPlaceholder }}
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>
<style lang="less">
.org-version-user-table {
  height: e('calc(100vh - 225px)');
  overflow: auto;
  .ant-table-pagination {
    display: none;
  }
}
</style>
<script type="text/babel">
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import OrgUserInfo from './org-user-info.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import OrgBatchCreateUser from './org-batch-create-user.vue';
import OrgRoleAddUserForm from './org-role-add-user-form.vue';
import OrgQuickJoinUserForm from './org-quick-join-user-form.vue';
import { debounce } from 'lodash';
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import Pagination from '@admin/app/web/template/common/pagination.vue';

export default {
  name: 'OrgVersionUserTable',
  inject: ['pageContext', 'orgElementManagementMap', 'getOrgElementTreeNodeMap'],
  props: {
    orgVersion: Object,
    orgElementId: String,
    orgRoleUuid: String,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: { Modal, OrgUserInfo, Drawer, OrgBatchCreateUser, OrgRoleAddUserForm, OrgQuickJoinUserForm, Pagination },
  computed: {
    userAuthority() {
      let auth = ['createUser', 'joinUser', 'deleteUser', 'lockUser', 'editAccountInfo', 'editUserInfo', 'editJobInfo', 'editRole'];
      if (this.orgElementManagementMap != undefined && Object.keys(this.orgElementManagementMap).length > 0) {
        if (this.orgElementId != undefined) {
          let mag = this.orgElementManagementMap[this.orgElementId];
          if (mag == undefined) {
            let treeNodeMap = this.getOrgElementTreeNodeMap();
            for (let k in treeNodeMap) {
              if (treeNodeMap[k].id == this.orgElementId) {
                // 往上找父级的权限
                let parentKey = treeNodeMap[treeNodeMap[k].key].parentKey;
                while (parentKey != undefined) {
                  let parent = treeNodeMap[parentKey];
                  if (parent) {
                    mag = this.orgElementManagementMap[parent.key];
                    if (mag) {
                      break;
                    } else {
                      parentKey = parent.parentKey;
                    }
                  } else {
                    break;
                  }
                }
                break;
              }
            }
          }
          if (mag !== undefined) {
            return mag.userAuthority || [];
          }
        } else {
          // 全部视图时候需要取合集
          let allAuth = [];
          for (let i in this.orgElementManagementMap) {
            allAuth = allAuth.concat(this.orgElementManagementMap[i].userAuthority || []);
          }
          return Array.from(new Set(allAuth));
        }
      }
      return auth;
    },

    orgVersionUuid() {
      return this.orgVersion.uuid;
    },
    rowSelection() {
      return {
        type: 'checkbox',
        selectedRowKeys: this.selectedRowKeys,
        selectedRows: this.selectedRows,
        onChange: this.selectRowChange,
        columnWidth: 60
      };
    },
    orgRoleUserView() {
      return this.orgRoleUuid != undefined;
    },
    columns() {
      let columns = [
        {
          title: '用户姓名',
          dataIndex: 'userName',
          width: 250,
          scopedSlots: { customRender: 'userNameSlot' }
        },
        {
          title: '账号',
          width: 150,
          dataIndex: 'loginName'
        },
        {
          title: '编号',
          dataIndex: 'userNo',
          width: 100
        },
        {
          title: '部门/职位',
          dataIndex: 'deptJobName'
        },
        {
          title: '状态',
          dataIndex: 'isAccountNonLocked',
          scopedSlots: { customRender: 'isAccountNonLockedSlot' },
          width: 100
        },
        {
          title: '操作',
          width: 150,
          scopedSlots: { customRender: 'operationSlot' }
        }
      ];
      if (this.orgRoleUserView) {
        return [columns[0], columns[3], columns[5]];
      }
      return columns;
    },
    userInfoEditable() {
      return (
        this.editable &&
        (this.userAuthority.includes('editAccountInfo') ||
          this.userAuthority.includes('editUserInfo') ||
          this.userAuthority.includes('editJobInfo') ||
          this.userAuthority.includes('editRole'))
      );
    },
    editable() {
      return this.displayState == 'edit';
    },
    waitResetPwdUsersLabel() {
      let label = [];
      let max = 10;
      let length = this.waitResetPwdUsers.length > max ? max : this.waitResetPwdUsers.length;
      for (let i = 0; i < length; i++) {
        label.push(this.waitResetPwdUsers[i].userName);
      }
      return this.waitResetPwdUsers.length > max ? label.join('、') + '等' + this.waitResetPwdUsers.length + '人' : label.join('、');
    }
  },
  data() {
    return {
      loading: true,
      rows: [],
      queryNoDeptNoJob: undefined,
      selectedRowKeys: [],
      selectedRows: [],
      keyword: undefined,
      pagination: {
        hideOnSinglePage: true,
        current: 1, //当前页
        pageSize: 10, //每页条数
        total: 0 //总条数
      },
      passwordRules: {},
      passwordRuleLoading: true,
      confirmResetPwdModalVisible: false,
      allowCustomizeResetPwd: false,
      passwordMaxLength: 20,
      passwordMinLength: 6,
      passwordPlaceholder: '',
      resetPwd: {
        value: undefined,
        rules: {
          value: [
            {
              required: true,
              message: '请输入密码',
              trigger: 'blur'
            }
          ]
        }
      },
      waitResetPwdUsers: [],
      batchCreateUserVisible: false,
      orgSelectParams: {}
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchUser();
    this.fetchUserAcctPasswordRules();
    if (this.orgElementManagementMap != undefined && Object.keys(this.orgElementManagementMap).length > 0) {
      // 只展示管理节点下的组织节点
      this.orgSelectParams.includeKeys = Object.keys(this.orgElementManagementMap).filter(function (key) {
        return key.indexOf('_') != -1;
      });
    }
  },
  mounted() {
    let _this = this;
    this.pageContext.handleEvent(`OrgVersionUserTableRefetch`, () => {
      _this.fetchUser();
    });
  },
  methods: {
    onSearch() {
      this.pagination.current = 1;
      this.fetchUser();
    },
    onTableChange(pagination, filters, sorter) {
      this.sorter = sorter;
      if (typeof this.pagination !== 'boolean' && pagination) {
        this.pagination.current = pagination.current;
        this.pagination.pageSize = pagination.pageSize;
      }
      this.fetchUser();
    },
    confirmOrgRoleUserAdd(e) {
      this.$refs.orgRoleAddUserForm.save().then(() => {
        e(true);
        this.$message.success('添加成员成功');
        this.pageContext.emitEvent('refreshUserCountStatics');
        this.fetchUser();
      });
    },
    removeOrgRoleMem(e, userId) {
      let userIds = [];
      if (userId) {
        userIds.push(userId);
      } else {
        if (this.selectedRows) {
          for (let r of this.selectedRows) {
            userIds.push(r.userId);
          }
        }
      }
      if (userIds.length) {
        $axios
          .post(`/proxy/api/org/organization/version/removeOrgRoleMember/${this.orgVersionUuid}`, userIds)
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('移出成员成功');
              this.pageContext.emitEvent('refreshUserCountStatics');
              this.fetchUser();
            } else {
              this.$message.error('移出成员失败');
            }
          })
          .catch(error => {
            this.$message.error('移出成员失败');
          });
      }
    },
    selectRowChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },
    clearSelectRows() {
      this.selectedRowKeys.splice(0, this.selectedRowKeys.length);
      this.selectedRows.splice(0, this.selectedRows.length);
    },
    fetchNoDeptJobUser() {
      this.queryNoDeptNoJob = '1';
      this.pagination.current = 1;
      this.fetchUser();
    },
    confirmAddUserInfo(e) {
      this.$refs.orgQuickJoinUserForm.save().then(() => {
        e(true);
        this.fetchUser();
      });
    },
    removeOrgUser() {
      let userIds = [];
      if (this.selectedRows) {
        for (let r of this.selectedRows) {
          userIds.push(r.userId);
        }
      }
      let _this = this;
      if (userIds.length) {
        this.$confirm({
          title: `确认要移出用户吗?`,
          content: undefined,
          onOk() {
            $axios
              .post(`/proxy/api/org/organization/removeOrgUser`, {
                userIds,
                orgElementId: _this.orgElementId,
                orgVersionUuid: _this.orgVersionUuid
              })
              .then(({ data }) => {
                if (data.code == 0) {
                  _this.$message.success('移出成员成功');
                  _this.pageContext.emitEvent('refreshUserCountStatics');
                  _this.fetchUser();
                  _this.clearSelectRows();
                }
              })
              .catch(error => {});
          },
          onCancel() {}
        });
      }
    },
    confirmSaveUserInfo(e, uuid) {
      if (this.editable) {
        this.$refs[uuid ? 'orgUserInfo' : 'newOrgUserInfo'].save().then(() => {
          e(true);
          this.fetchUser();
          this.pageContext.emitEvent('refreshUserCountStatics');
        });
      } else {
        e(true);
      }
    },
    refresh() {
      this.queryNoDeptNoJob = undefined;
      this.pagination.current = 1;
      this.fetchUser();
    },
    handleMoreOperationClick(e) {
      if (e.key == 'batchDelete') {
        this.batchDeleteAccount();
      } else if (e.key == 'batchResetPwd') {
        this.batchResetPwd();
      }
    },
    batchResetPwd() {
      const _this = this;
      if (_this.selectedRowKeys.length) {
        _this.confirmResetPwdModalVisible = true;
        _this.waitResetPwdUsers.splice(0, _this.waitResetPwdUsers.length);
        _this.waitResetPwdUsers.push(..._this.selectedRows);
      } else {
        _this.$message.error('请选择用户！');
      }
    },
    confirmResetPassword() {
      let _this = this;
      let _continue = () => {
        _this.$loading();
        let uuids = [],
          password =
            _this.allowCustomizeResetPwd && _this.resetPwd.value != undefined && _this.resetPwd.value != ''
              ? _this.resetPwd.value
              : undefined;
        for (let w of this.waitResetPwdUsers) {
          uuids.push(w.uuid);
          w.password = password;
          if (!_this.allowCustomizeResetPwd) {
            if (password == undefined) {
              password = {};
            }
            password[w.uuid] = _this.createRandomPassword();
            w.password = password[w.uuid];
          }
        }
        $axios
          .post('/proxy/api/user/batchResetPassword', { uuids, password })
          .then(({ data }) => {
            _this.$loading(false);
            if (data.code == 0) {
              _this.clearSelectRows();
              _this.confirmResetPwdModalVisible = false;
              let columns = [
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
                  key: 'password',
                  scopedSlots: { customRender: 'passwordSlot' }
                }
              ];
              _this.$success({
                title: '重置密码成功',
                width: 650,
                content: (
                  <div>
                    <a-table
                      size="small"
                      rowKey="loginName"
                      columns={columns}
                      data-source={_this.waitResetPwdUsers}
                      scroll={{ y: 400 }}
                      pagination={false}
                      scopedSlots={{
                        passwordSlot: (text, record) => <a-tag onClick={e => _this.copyTextToClipboard(text, e)}>{text}</a-tag>
                      }}
                    ></a-table>
                    <a-button size="small" type="link" block="true" onClick={_this.copyRestPwdUser}>
                      复制内容
                    </a-button>
                  </div>
                )
              });
              _this.resetPwd.value = undefined;
            }
          })
          .catch(error => {
            _this.$loading(false);
            _this.$message.error('重置密码失败');
          });
      };
      if (this.$refs.resetPwdForm) {
        this.$refs.resetPwdForm.validate(p => {
          if (p) {
            _continue();
          }
        });
      } else {
        _continue();
      }
    },
    copyRestPwdUser(e) {
      let _this = this;
      let str = [];
      for (let item of this.waitResetPwdUsers) {
        str.push(item.userName + '\t' + item.loginName + '\t' + item.password);
      }
      copyToClipboard(str.join('\n'), e, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    },
    copyTextToClipboard(text, e) {
      let _this = this;
      copyToClipboard(text, e, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    },
    createRandomPassword() {
      const length = Math.floor(Math.random() * (this.passwordMaxLength - this.passwordMinLength + 1)) + this.passwordMinLength;
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
    batchDeleteAccount() {
      const _this = this;
      if (_this.selectedRowKeys.length) {
        _this.$confirm({
          title: `确认框`,
          content: `确认要删除用户[${this.selectedRows.map(user => user.userName)}]?`,
          onOk() {
            _this.$loading();
            $axios
              .post('/proxy/api/user/batchDeleteUser', { uuids: _this.selectedRowKeys })
              .then(({ data }) => {
                _this.$loading(false);
                if (data.code == 0) {
                  //TODO: 删除结果提示，可能存在部分用户删除失败
                  _this.$message.success('批量删除用户成功');
                  _this.pageContext.emitEvent('refreshUserCountStatics');
                  _this.fetchUser();
                  _this.clearSelectRows();
                }
              })
              .catch(error => {
                _this.$loading(false);
                _this.$message.error('批量删除用户成功');
              });
          },
          onCancel() {}
        });
      } else {
        _this.$message.error('请选择用户！');
      }
    },
    handleMoreMenuItemClick(e, item, index) {
      if (e.key == 'lock') {
        this.lockAccount(item);
      } else if (e.key == 'delete') {
        this.deleteAccount(item, index);
      } else if (e.key == 'resetAccountPwd') {
        this.resetAccountPwd(item);
      }
    },
    deleteAccount(item, index) {
      let _this = this;
      this.$confirm({
        title: `确认要删除用户 [ ${item.userName} ] 吗?`,
        content: undefined,
        onOk() {
          $axios
            .get('/proxy/api/user/delete', { params: { uuid: item.uuid } })
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('删除成功');
                _this.pageContext.emitEvent('refreshUserCountStatics');
                _this.fetchUser();
              }
            })
            .catch(error => {
              console.error(error);
              _this.$message.error('删除失败');
            });
        },
        onCancel() {}
      });
    },
    resetAccountPwd(item) {
      this.confirmResetPwdModalVisible = true;
      this.waitResetPwdUsers.splice(0, this.waitResetPwdUsers.length);
      this.waitResetPwdUsers.push(item);
    },
    lockAccount(item) {
      $axios
        .get('/proxy/api/user/lockUser', { params: { uuid: item.uuid, locked: item.isAccountNonLocked == '1' } })
        .then(({ data }) => {
          if (data.code == 0) {
            item.isAccountNonLocked = item.isAccountNonLocked == '1' ? '0' : '1';
            this.$message.success(`${item.isAccountNonLocked == '1' ? '解冻' : '冻结'}成功`);
          }
        })
        .catch(error => {
          this.$message.success(`${item.isAccountNonLocked == '1' ? '解冻' : '冻结'}失败`);
        });
    },
    fetchUser() {
      let _this = this;
      this.loading = true;
      new DataSourceBase({
        onDataChange: function (data, count, params) {
          _this.rows = data.data;
          _this.pagination.total = count;
          _this.pagination.current = data.pagination.currentPage;
          _this.pagination.totalPages = data.pagination.totalPages;
          _this.loading = false;
        },
        receiver: this,
        dataStoreId: 'CD_DS_20221201173948',
        params: this.queryNoDeptNoJob
          ? {
              orgVersionUuid: this.orgVersionUuid,
              queryNoDeptNoJob: this.queryNoDeptNoJob,
              keyword: this.keyword
            }
          : {
              orgVersionUuid: this.orgVersionUuid,
              orgElementId: this.orgElementId,
              orgRoleUuid: this.orgRoleUuid,
              keyword: this.keyword
            },
        pageSize: this.pagination.pageSize || 20
      }).load({
        pagination: {
          currentPage: this.pagination.current,
          pageSize: this.pagination.pageSize
        }
      });
    },

    fetchUserAcctPasswordRules() {
      let _this = this;
      this.$axios
        .get(`/proxy/api/user/getAcctPasswordRules`, {
          params: {}
        })
        .then(({ data }) => {
          this.passwordRuleLoading = false;
          if (data.code == 0 && data.data) {
            for (let key in data.data) {
              this.$set(this.passwordRules, key, data.data[key]);
            }
            if (Object.keys(this.passwordRules).length != 0) {
              this.allowCustomizeResetPwd = this.passwordRules.sysInitPasswordSource !== 'random';
              this.passwordMaxLength = parseInt(this.passwordRules.maxLength);
              this.passwordMinLength = parseInt(this.passwordRules.minLength);
            }
            let newPwdRule = { trigger: ['blur', 'change'] };
            if (this.allowCustomizeResetPwd) {
              let requireCnt = parseInt(this.passwordRules.charTypeNumRequire);
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
              this.resetPwd.rules.value.push(newPwdRule);
            }
          }
        })
        .catch(error => {
          this.passwordRuleLoading = false;
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
    onChangePagination() {
      this.onTableChange();
    },
    onBatchCreateUserClearAll() {
      this.$refs.orgBatchCreateUserRef.clearAll();
    },
    onBatchCreateUserConfirm() {
      this.$refs.orgBatchCreateUserRef.createUser();
    },
    onBatchCreateUserCancel() {
      this.batchCreateUserVisible = false;
    }
  },
  watch: {}
};
</script>
