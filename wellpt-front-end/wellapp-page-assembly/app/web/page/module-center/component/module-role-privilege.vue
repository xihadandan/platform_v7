<template>
  <a-tabs default-active-key="role" class="pt-tabs">
    <a-tab-pane key="role" tab="角色">
      <a-input-search style="width: 300px; margin-bottom: 16px" @search="e => onSearch(e, 'roleKeyword')" allow-clear />
      <a-alert
        message="为模块创建角色，用于设置一类用户的访问权限"
        type="info"
        show-icon
        closable
        style="margin-bottom: 16px; width: calc(100% - 12px)"
      />
      <PerfectScrollbar style="height: calc(100vh - 280px); overflow-x: hidden" id="module-role-scroll">
        <a-spin tip="数据加载中" :spinning="roleLoading">
          <a-list :grid="grid" :data-source="vRoles" class="module-role-list">
            <a-list-item
              slot="renderItem"
              slot-scope="item, index"
              v-if="
                item.uuid == '-1' ||
                roleKeyword == undefined ||
                roleKeyword == '' ||
                (roleKeyword && (item.id.indexOf(roleKeyword) != -1 || item.name.indexOf(roleKeyword) != -1))
              "
            >
              <a-card :title="null" size="small" v-if="item.uuid != '-1'" class="role-item-meta-card" hoverable>
                <a-row type="flex" style="flex-wrap: nowrap; align-items: center">
                  <a-col flex="80px">
                    <a-avatar :size="48" shape="square" style="background: var(--w-primary-color); border-radius: 8px; margin-left: 10px">
                      <Icon slot="icon" type="pticon iconfont icon-ptkj-jiaose" style="font-size: 36px"></Icon>
                    </a-avatar>
                  </a-col>
                  <a-col flex="auto">
                    <div class="description" style="min-height: 80px">
                      <div>
                        <a-avatar :size="20" shape="square" style="background: var(--w-primary-color-1); border-radius: 4px">
                          <Icon
                            slot="icon"
                            type="pticon iconfont icon-ptkj-yonghuziliao"
                            style="font-size: 14px; color: var(--w-primary-color)"
                          ></Icon>
                        </a-avatar>
                        <a-input v-if="item.edit" style="width: 140px" v-model="item.rename" />
                        <span v-else class="role-name" :title="item.name">
                          {{ item.name }}
                        </span>
                        <a-tag
                          v-if="!item.edit"
                          size="small"
                          class="primary-color tag-no-border tag-role-id"
                          style="--w-tag-border-radius: 4px"
                          :title="item.id"
                        >
                          {{ item.id }}
                        </a-tag>
                      </div>
                      <div style="padding-left: 24px">
                        <a-textarea
                          v-if="item.edit"
                          v-model="item.new_remark"
                          style="width: 376px; height: 54px"
                          :autoSize="{ minRows: 1, maxRows: 2 }"
                        />
                        <span v-else class="role-description" :title="item.remark">{{ item.remark }}</span>
                      </div>
                      <div v-if="!item.edit" class="role-link">
                        <a-avatar :size="20" shape="square" style="background: var(--w-primary-color-1); border-radius: 4px">
                          <Icon
                            slot="icon"
                            type="pticon iconfont icon-ptkj-lianjiebangding"
                            style="font-size: 14px; color: var(--w-primary-color)"
                          ></Icon>
                        </a-avatar>
                        关联角色
                        <span class="role-num">{{ item.nestedRoles.length }}</span>
                        权限
                        <span class="role-num">{{ item.privileges.length }}</span>
                      </div>
                    </div>
                  </a-col>
                  <a-col
                    flex="245px"
                    :style="{
                      textAlign: 'right',
                      alignSelf: item.edit ? 'end' : 'auto'
                    }"
                  >
                    <template v-if="!item.edit">
                      <Modal
                        destroyOnClose
                        title="关联角色权限"
                        :ok="e => saveRoleMember(e, item)"
                        :maxHeight="600"
                        :width="600"
                        :okText="moduleRoleMemOkText"
                      >
                        <a-button type="link" size="small">
                          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                          关联角色权限
                        </a-button>
                        <template slot="content">
                          <ModuleRoleMember
                            :roleUuid="item.uuid"
                            ref="roleMember"
                            :roles="roles"
                            :privileges="privileges"
                            :ok="e => saveRoleMember(e, item)"
                            @change="onRoleMemberCkChanged"
                          />
                        </template>
                      </Modal>

                      <a-button type="link" size="small" @click="onEditRolePrivilege(item)">
                        <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
                        编辑
                      </a-button>
                      <a-popconfirm
                        placement="left"
                        :arrowPointAtCenter="true"
                        title="确认要删除吗?"
                        ok-text="删除"
                        cancel-text="取消"
                        @confirm="removeRolePrivilege(item, index - 1, 'role')"
                      >
                        <a-button type="link" size="small" :loading="item.loading">
                          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                          删除
                        </a-button>
                      </a-popconfirm>
                    </template>
                    <template v-else>
                      <a-button type="primary" :loading="item.loading" @click="saveRoleBasic(item)">保存</a-button>
                      <a-button @click="item.edit = false">取消</a-button>
                    </template>
                  </a-col>
                </a-row>
              </a-card>
              <a-card size="small" v-else class="role-item-meta-card" hoverable>
                <div class="add-role-block" @click="onClickAddRoleReady" v-show="!addRoleReady">
                  <a-icon type="plus" style="font-size: 24px" />
                  <div>新增角色</div>
                </div>

                <a-row type="flex" style="flex-wrap: nowrap; align-items: center" v-show="addRoleReady">
                  <a-col flex="80px">
                    <a-avatar :size="48" shape="square" style="background: var(--w-primary-color); border-radius: 8px; margin-left: 10px">
                      <Icon slot="icon" type="pticon iconfont icon-ptkj-jiaose" style="font-size: 36px"></Icon>
                    </a-avatar>
                  </a-col>
                  <a-col flex="auto">
                    <div class="description">
                      <div>
                        <a-avatar :size="20" shape="square" style="background: var(--w-primary-color-1); border-radius: 4px">
                          <Icon
                            slot="icon"
                            type="pticon iconfont icon-ptkj-yonghuziliao"
                            style="font-size: 14px; color: var(--w-primary-color)"
                          ></Icon>
                        </a-avatar>
                        <a-input v-model="newRole.name" style="max-width: 140px" />
                        <a-input style="width: 208px" :value="idSuffix()" @change="e => mergeIdSuffix(e)">
                          <span slot="addonBefore">{{ idPrefix }}</span>
                        </a-input>
                      </div>
                      <div>
                        <a-textarea v-model="newRole.remark" style="width: 376px; height: 54px" :autoSize="{ minRows: 1, maxRows: 2 }" />
                      </div>
                    </div>
                  </a-col>
                  <a-col flex="160px" style="text-align: right; align-self: end">
                    <a-button type="primary" :loading="newRole.loading" @click="saveRoleBasic(newRole)">保存</a-button>
                    <a-button @click="addRoleReady = false">取消</a-button>
                  </a-col>
                </a-row>
              </a-card>
            </a-list-item>
          </a-list>
        </a-spin>
        <a-back-top :target="backTopRole" style="right: 30px" />
      </PerfectScrollbar>
    </a-tab-pane>
    <a-tab-pane key="privilege" tab="权限" class="module-privilege-tab-pane">
      <a-input-search style="width: 300px; margin-bottom: 16px" @search="e => onSearch(e, 'privilegeKeyword')" allow-clear />
      <a-alert
        message="为一类相近的界面、功能和数据权限创建权限组，将权限赋予角色"
        type="info"
        show-icon
        closable
        style="margin-bottom: 16px; width: calc(100% - 12px)"
      />
      <PerfectScrollbar style="height: calc(100vh - 280px); overflow-x: hidden" id="module-privilege-scroll">
        <a-spin tip="数据加载中" :spinning="privilegeLoading">
          <a-list :grid="{ gutter: 16, column: 1 }" :data-source="vPrivileges" class="module-role-list">
            <a-list-item
              slot="renderItem"
              slot-scope="item, index"
              v-if="
                item.uuid == '-1' ||
                privilegeKeyword == undefined ||
                privilegeKeyword == '' ||
                (privilegeKeyword && item.name.indexOf(privilegeKeyword) != -1)
              "
            >
              <a-card
                :title="null"
                size="small"
                v-if="item.uuid != '-1'"
                class="role-item-meta-card"
                hoverable
                style="height: auto; min-height: 114px"
              >
                <a-row type="flex" style="flex-wrap: nowrap; align-items: center">
                  <a-col flex="80px">
                    <a-avatar :size="48" shape="square" style="background: var(--w-primary-color); border-radius: 8px; margin-left: 10px">
                      <Icon slot="icon" type="pticon iconfont icon-ptkj-quanxian" style="font-size: 36px"></Icon>
                    </a-avatar>
                  </a-col>
                  <a-col flex="auto">
                    <div class="description" style="min-height: 80px">
                      <div>
                        <a-input v-if="item.edit" style="width: 200px" v-model="item.rename" />
                        <span v-else class="role-name" :title="item.name" style="max-width: 200px">
                          {{ item.name }}
                        </span>
                      </div>
                      <div>
                        <a-textarea
                          v-if="item.edit"
                          v-model="item.new_remark"
                          style="width: 376px; height: 54px"
                          :autoSize="{ minRows: 1, maxRows: 2 }"
                        />
                        <span v-else class="role-description" :title="item.remark">{{ item.remark }}</span>
                      </div>
                      <div v-if="!item.edit">
                        <a-tag
                          v-for="(role, i) in item.roles"
                          :key="'privilege_role_' + role.uuid"
                          class="primary-color w-ellipsis"
                          style="max-width: 120px; --w-tag-border-radius: 4px"
                          :title="role.name"
                        >
                          <Icon slot="icon" type="pticon iconfont icon-ptkj-jiaose"></Icon>
                          {{ role.name }}
                        </a-tag>
                      </div>
                    </div>
                  </a-col>
                  <!-- <a-col flex="280px">
                    <div style="color: var(--w-text-color-light); font-size: var(--w-font-size-base); margin-bottom: 12px">
                      权限访问页面
                    </div>
                    <a-tag
                      v-for="(role, i) in item.roles"
                      :key="'privilege_role_' + role.uuid"
                      class="primary-color w-ellipsis"
                      style="max-width: 120px; --w-tag-border-radius: 4px"
                      :title="role.name"
                    >
                      <Icon slot="icon" type="pticon iconfont icon-ptkj-jiaose"></Icon>
                      {{ role.name }}
                    </a-tag>
                  </a-col> -->
                  <a-col
                    flex="220px"
                    :style="{
                      textAlign: 'right',
                      alignSelf: item.edit ? 'end' : 'auto'
                    }"
                  >
                    <template v-if="!item.edit">
                      <PrivilegeResourceSet :privilege="item" @privilegeRoleChange="onPrivilegeRoleChange">
                        <a-button type="link" size="small">
                          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                          设置
                        </a-button>
                      </PrivilegeResourceSet>
                      <a-button type="link" size="small" @click="onEditRolePrivilege(item)">
                        <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
                        编辑
                      </a-button>
                      <a-popconfirm
                        placement="left"
                        :arrowPointAtCenter="true"
                        title="确认要删除吗?"
                        ok-text="删除"
                        cancel-text="取消"
                        @confirm="removeRolePrivilege(item, index - 1, 'privilege')"
                      >
                        <a-button type="link" size="small" :loading="item.loading">
                          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                          删除
                        </a-button>
                      </a-popconfirm>
                    </template>
                    <template v-else>
                      <a-button type="primary" :loading="item.loading" @click="onSavePrivilege(item)">保存</a-button>
                      <a-button @click="item.edit = false">取消</a-button>
                    </template>
                  </a-col>
                </a-row>
              </a-card>

              <a-card size="small" v-else class="role-item-meta-card" hoverable>
                <div class="add-role-block" @click="onClickAddPrivilegeReady" v-show="!addPrivilegeReady">
                  <a-icon type="plus" style="font-size: 24px" />
                  <div>新增权限</div>
                </div>

                <a-row type="flex" style="flex-wrap: nowrap; align-items: center" v-show="addPrivilegeReady">
                  <a-col flex="80px">
                    <a-avatar :size="48" shape="square" style="background: var(--w-primary-color); border-radius: 8px; margin-left: 10px">
                      <Icon slot="icon" type="pticon iconfont icon-ptkj-quanxian" style="font-size: 36px"></Icon>
                    </a-avatar>
                  </a-col>
                  <a-col flex="auto">
                    <div class="description">
                      <div>
                        <a-input v-model="newPrivilege.name" style="width: 200px" />
                      </div>
                      <div>
                        <a-textarea
                          v-model="newPrivilege.remark"
                          style="width: 378px; height: 54px"
                          :autoSize="{ minRows: 1, maxRows: 2 }"
                        />
                      </div>
                    </div>
                  </a-col>
                  <a-col flex="160px" style="text-align: right; align-self: end">
                    <a-button type="primary" :loading="newPrivilege.loading" @click="onSavePrivilege(newPrivilege)">保存</a-button>
                    <a-button @click="addPrivilegeReady = false">取消</a-button>
                  </a-col>
                </a-row>
              </a-card>
            </a-list-item>
          </a-list>
        </a-spin>
        <a-back-top :target="backTopPrivilege" style="right: 30px" />
      </PerfectScrollbar>
    </a-tab-pane>
  </a-tabs>
</template>
<style lang="less">
.module-role-list {
  width: 99%;
  .add-role-block {
    padding-top: 24px;
    color: var(--w-text-color-light);
    text-align: center;
    font-size: var(--w-font-size-base);
    &:hover {
      color: var(--w-primary-color);
    }
  }
  .role-item-meta-card {
    cursor: pointer;
    border-radius: 4px;
    height: 114px;
    .description {
      display: grid;
      grid-gap: 8px;

      > div:nth-child(2) {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        color: rgba(0, 0, 0, 0.45);
      }

      .role-name {
        color: var(--w-text-color-dark);
        font-size: var(--w-font-size-base);
        font-weight: bold;
        max-width: 130px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        display: inline-block;
        vertical-align: middle;
      }
      .tag-role-id {
        max-width: e('calc(100% - 140px)');
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        display: inline-block;
        vertical-align: middle;
      }
      .role-description {
        font-size: var(--w-font-size-sm);
        color: var(--w-text-color-light);
        line-height: var(--w-font-size-base);
        width: 220px;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
      }
      .role-link {
        color: var(--w-text-color-dark);
        font-size: var(--w-font-size-base);

        .role-num {
          color: var(--w-primary-color);
          font-weight: bold;
        }
      }
    }
  }
}
</style>
<script type="text/babel">
import moment from 'moment';
import ModuleRoleMember from './module-role-member.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import PrivilegeResourceSet from './privilege-resource-set.vue';
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'ModuleRolePrivilege',
  inject: ['currentModule'],
  props: {},
  components: { ModuleRoleMember, Modal, PrivilegeResourceSet },
  computed: {
    vRoles() {
      let r = [{ uuid: -1 }];
      r.push(...this.roles);
      return r;
    },
    vPrivileges() {
      let r = [{ uuid: -1 }];
      r.push(...this.privileges);
      return r;
    }
  },
  data() {
    return {
      grid: { gutter: 16, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 2 },
      newRole: { name: undefined, remark: undefined, id: undefined, appId: this.currentModule.id },
      newPrivilege: { name: undefined, remark: undefined, appId: this.currentModule.id },
      addRoleReady: false,
      addPrivilegeReady: false,
      roleLoading: true,
      privilegeLoading: true,
      idPrefix: 'ROLE_',
      moduleRoleMemOkText: '确定',
      roles: [],
      privileges: [],
      roleKeyword: undefined,
      privilegeKeyword: undefined
    };
  },
  provide() {
    return {
      moduleRoles: this.roles
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getRoles();
    this.getPrivileges();
  },
  mounted() {},
  methods: {
    onPrivilegeRoleChange() {
      this.getRoles();
    },

    onSearch(value, field) {
      this[field] = value.trim();
    },
    onRoleMemberCkChanged({ roleChecked, privilegeChecked }) {
      this.moduleRoleMemOkText = `确定 (${roleChecked.length + privilegeChecked.length}个)`;
    },
    backTopPrivilege() {
      return this.$el.querySelector('#module-privilege-scroll');
    },
    backTopRole() {
      return this.$el.querySelector('#module-role-scroll');
    },
    onEditRolePrivilege(item) {
      this.$set(item, 'edit', true);
      this.$set(item, 'rename', item.name);
      this.$set(item, 'new_remark', item.remark);
    },
    idSuffix() {
      return this.newRole.id ? this.newRole.id.split(this.idPrefix)[1] : undefined;
    },
    mergeIdSuffix(e) {
      this.newRole.id = e.target.value == '' ? undefined : this.idPrefix + e.target.value;
    },
    removeRolePrivilege(item, i, url) {
      this.$set(item, 'loading', true);
      $axios.post(`/proxy/api/security/${url}/removeAll`, [item.uuid]).then(({ data }) => {
        this.$set(item, 'loading', false);
        if (data.code == 0) {
          this.$message.success('删除成功');
          if (url === 'privilege') {
            this.privileges.splice(i, 1);
          } else {
            this.roles.splice(i, 1);
          }
        }
      });
    },
    onSavePrivilege(item) {
      if ((item.uuid == undefined && !item.name) || (item.uuid && !item.rename)) {
        this.$message.error('请填写权限名称');
        return;
      }
      this.$set(item, 'loading', true);
      if (item.uuid == undefined) {
        item.code = 'PRIVILEGE_' + moment().format('yyyyMMDDHHmmss');
      }
      item.appId = this.currentModule.id;
      let _temp = { ...item };
      if (_temp.uuid) {
        _temp.name = item.rename;
        _temp.remark = item.new_remark;
      }
      _temp.otherResources = null; // 置空, 避免后端更新关联资源（这里仅保存权限的基本信息）
      $axios.post('/proxy/api/security/privilege/saveBean', _temp).then(({ data }) => {
        this.$set(item, 'loading', false);
        if (data.data) {
          this.$message.success('保存成功');
          if (item.uuid == undefined) {
            this.addPrivilegeReady = false;
            this.privileges.splice(0, 0, {
              name: this.newPrivilege.name,
              remark: this.newPrivilege.remark,
              code: item.code,
              uuid: data.data,
              recVer: 0
            });
          } else {
            item.name = item.rename;
            item.remark = item.new_remark;
            item.edit = false;
            item.recVer++;
          }
        }
      });
    },
    saveRoleMember(e, item) {
      let roleChecked = this.$refs.roleMember.roleChecked,
        roleMap = this.$refs.roleMember.roleMap,
        _this = this,
        privilegeChecked = this.$refs.roleMember.privilegeChecked;

      let nestedRoleRemoved = [],
        nestedRoleAdded = [],
        privilegeRemoved = [],
        privilegeAdded = [],
        originalPrivileges = [],
        originalNestedRoles = [];
      for (let i = 0, len = item.privileges.length; i < len; i++) {
        if (item.privileges[i].uuid == this.defaultModulePvgUuid) {
          // 跳过默认模块访问权限，该权限不允许被删除
          continue;
        }
        originalPrivileges.push(item.privileges[i].uuid);
        if (!privilegeChecked.includes(item.privileges[i].uuid)) {
          privilegeRemoved.push(item.privileges[i].uuid);
        }
      }
      for (let i = 0, len = privilegeChecked.length; i < len; i++) {
        if (!originalPrivileges.includes(privilegeChecked[i])) {
          privilegeAdded.push(privilegeChecked[i]);
        }
      }
      for (let i = 0, len = item.nestedRoles.length; i < len; i++) {
        originalNestedRoles.push(item.nestedRoles[i].uuid);
        if (!roleChecked.includes(item.nestedRoles[i].uuid)) {
          nestedRoleRemoved.push(item.nestedRoles[i].uuid);
        }
      }

      for (let i = 0, len = roleChecked.length; i < len; i++) {
        if (!originalNestedRoles.includes(roleChecked[i])) {
          nestedRoleAdded.push(roleChecked[i]);
        }
      }

      const commit = () => {
        if (nestedRoleAdded.length || nestedRoleRemoved.length || privilegeAdded.length || privilegeRemoved.length) {
          this.$loading();
          $axios
            .post(`/proxy/api/security/role/updateRoleMember`, [
              {
                role: {
                  uuid: item.uuid
                },
                nestedRoleRemoved,
                nestedRoleAdded,
                privilegeRemoved,
                privilegeAdded
              }
            ])
            .then(({ data }) => {
              this.$loading(false);
              if (data.data) {
                $axios.post(`/proxy/api/security/role/publishRoleUpdatedEvent?uuid=${item.uuid}`);
                this.$message.success('关联角色权限成功');
                e(true);
                if (nestedRoleRemoved.length > 0) {
                  for (let i = 0; i < item.nestedRoles.length; i++) {
                    if (nestedRoleRemoved.includes(item.nestedRoles[i].uuid)) {
                      item.nestedRoles.splice(i--, 1);
                    }
                  }
                }
                if (nestedRoleAdded.length > 0) {
                  nestedRoleAdded.forEach(r => {
                    item.nestedRoles.push({ uuid: r });
                  });
                }

                if (privilegeRemoved.length > 0) {
                  for (let i = 0; i < item.privileges.length; i++) {
                    if (privilegeRemoved.includes(item.privileges[i].uuid)) {
                      item.privileges.splice(i--, 1);
                    }
                  }
                }
                if (privilegeAdded.length > 0) {
                  privilegeAdded.forEach(r => {
                    item.privileges.push({ uuid: r });
                  });
                }
              } else {
                this.$message.error('关联角色权限失败');
              }
            })
            .catch(() => {
              this.$loading(false);
              this.$message.error('关联角色权限失败');
            });
        } else {
          e(true);
        }
      };
      if (nestedRoleAdded.length > 0) {
        this.$loading();
        let checks = [];
        for (let i = 0, len = nestedRoleAdded.length; i < len; i++) {
          checks.push(this.checkRoleRecursive(item.uuid, nestedRoleAdded[i]));
        }
        Promise.all(checks).then(results => {
          let recursive = [],
            uuids = [];
          for (let i = 0, len = results.length; i < len; i++) {
            if (results[i].recursive) {
              uuids.push(results[i].roleUuid);
              recursive.push(roleMap[results[i].roleUuid].name);
            }
          }
          this.$loading(false);
          if (recursive.length > 0) {
            this.$info({
              title: '提示',
              content: (
                <div>
                  {recursive.map(n => (
                    <a-tag>{n}</a-tag>
                  ))}{' '}
                  存在角色嵌套, 将被取消关联
                </div>
              ),
              onOk() {
                for (let i = 0; i < nestedRoleAdded.length; i++) {
                  if (uuids.includes(nestedRoleAdded[i])) {
                    nestedRoleAdded.splice(i--, 1);
                  }
                }

                commit.call(_this);
              }
            });
          } else {
            commit();
          }
        });
      } else {
        commit();
      }
    },
    checkRoleRecursive(roleUuid, childRoleUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/security/role/checkNestedRecursively`, { params: { roleUuid, childRoleUuid } })
          .then(({ data }) => {
            resolve({
              roleUuid: childRoleUuid,
              recursive: data.data
            });
          })
          .catch(error => {});
      });
    },
    saveRoleBasic(item) {
      if ((item.uuid && !item.rename) || (item.uuid == undefined && !item.name)) {
        this.$message.error('请填写角色名称');
        return;
      }
      this.$set(item, 'loading', true);
      let submitData = deepClone(item);
      if (item.uuid) {
        submitData.name = item.rename;
        submitData.remark = item.new_remark;
      }
      // 仅保存基础信息时候，置空关联角色、权限，避免后端接口处理为删除关联的角色/权限
      submitData.nestedRoles = null;
      submitData.privileges = null;
      if (item.uuid == undefined && this.defaultModulePvgUuid) {
        // 默认捆绑上模块访问权限
        submitData.privileges = [{ uuid: this.defaultModulePvgUuid }];
      }
      this.onSaveRole(submitData, uuid => {
        this.$set(item, 'loading', false);
        if (uuid == -1) {
          return;
        }

        // FIXME: 生成默认租户的有使用关联到该模块的系统下的角色 ?
        if (item.uuid == undefined) {
          this.addRoleReady = false;
          this.roles.splice(0, 0, {
            name: this.newRole.name,
            remark: this.newRole.remark,
            uuid,
            id: this.newRole.id,
            privileges: [],
            nestedRoles: [],
            recVer: 0
          });
        } else {
          item.edit = false;
          item.recVer++;
          item.name = item.rename;
          item.remark = item.new_remark;
          this.updatePrivilegeRoleName(item.uuid, item.rename, item.new_remark);
        }
      });
    },
    onSaveRole(item, callback) {
      if (item.name) {
        $axios
          .post('/proxy/api/security/role/saveBean', { ...item })
          .then(({ data }) => {
            if (data.data) {
              this.$message.success('保存成功');
              if (typeof callback == 'function') {
                callback.call(this, data.data);
              }
            } else if (!data.success) {
              this.$message.error(data.msg);
              callback(-1);
            }
          })
          .catch(error => {
            this.$message.error('保存失败');
            callback(-1);
          });
      }
    },
    updatePrivilegeRoleName(uuid, name, remark) {
      for (let p of this.privileges) {
        if (p.roles) {
          for (let r of p.roles) {
            if (r.uuid == uuid) {
              r.name = name;
              r.remark = remark;
            }
          }
        }
      }
    },
    onClickAddRoleReady() {
      this.addRoleReady = true;
      this.newRole.name = undefined;
      this.newRole.id = this.idPrefix + moment().format('yyyyMMDDHHmmss');
      this.newRole.remark = undefined;
      this.newRole.uuid = undefined;
    },
    onClickAddPrivilegeReady() {
      this.newPrivilege.name = undefined;
      this.newPrivilege.remark = undefined;
      this.newPrivilege.uuid = undefined;
      this.addPrivilegeReady = true;
    },

    getRoles() {
      this.roles.splice(0, this.roles.length);
      $axios.get('/proxy/api/security/role/queryAppRoles', { params: { appId: this.currentModule.id } }).then(({ data }) => {
        if (data.data) {
          for (let r of data.data) {
            // 排除默认权限
            for (let i = 0; i < r.privileges.length; i++) {
              if (r.privileges[i].systemDef == 1) {
                r.privileges.splice(i--, 1);
              }
            }
            // 排除非该模块下的角色: 模块角色可以在系统管理后台下其他模块的角色
            for (let i = 0; i < r.nestedRoles.length; i++) {
              if (r.nestedRoles[i].appId != this.currentModule.id) {
                r.nestedRoles.splice(i, 1);
                break;
              }
            }
            this.roles.push(r);
          }

          this.roleLoading = false;
        }
      });
    },
    getPrivileges() {
      this.privileges.splice(0, this.privileges.length);
      $axios.get('/proxy/api/security/privilege/queryAppRolePrivileges', { params: { appId: this.currentModule.id } }).then(({ data }) => {
        if (data.data) {
          for (let d of data.data) {
            if (d.systemDef == 0) {
              // 排除模块默认的权限数据
              this.privileges.push(d);
            } else if (d.systemDef == 1 && d.code == 'PRIVILEGE_MOD_' + this.currentModule.id.toUpperCase()) {
              // 模块访问权限
              this.defaultModulePvgUuid = d.uuid;
            }
          }
          // this.privileges.push(...data.data);
          this.privilegeLoading = false;
          if (this.defaultModulePvgUuid == undefined) {
            this.privilegeLoading = true;
            this.checkDefaultModulePrivilege().then(uuid => {
              this.privilegeLoading = false;
              this.defaultModulePvgUuid = uuid;
            });
          }
        }
      });
    },

    // 修复数据丢失使用
    checkDefaultModulePrivilege() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/security/privilege/getPrivilegeBeanByCode/PRIVILEGE_MOD_${this.currentModule.id.toUpperCase()}`)
          .then(({ data }) => {
            let prg = data.data;
            if (prg == null) {
              import('./module-detail.vue').then(({ default: ModuleDetail }) => {
                ModuleDetail.methods.createDefaultModulePrivilege(this.currentModule.id).then(uuid => {
                  this.bindModulePrivilegeToModuleRoles(uuid).then(() => {
                    resolve(uuid);
                  });
                });
              });
            }
          });
      });
    },
    bindModulePrivilegeToModuleRoles(privilegeUuid) {
      return new Promise((resolve, reject) => {
        $axios.get('/proxy/api/security/role/queryAppRoles', { params: { appId: this.currentModule.id } }).then(({ data }) => {
          if (data.data) {
            let updateRoles = [];
            for (let i = 0, len = data.data.length; i < len; i++) {
              if (data.data.systemDef != 1) {
                updateRoles.push({
                  role: { uuid: data.data[i].uuid },
                  privilegeAdded: [privilegeUuid]
                });
              }
            }
            $axios.post(`/proxy/api/security/role/updateRoleMember`, updateRoles).then(({ data }) => {
              $axios.get(`/proxy/api/security/privilege/publishPrivilegeUpdatedEvent/${privilegeUuid}`).then(() => {
                resolve();
              });
            });
          }
        });
      });
    }
  }
};
</script>
