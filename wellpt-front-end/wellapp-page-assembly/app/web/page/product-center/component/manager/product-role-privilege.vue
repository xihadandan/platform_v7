<template>
  <div class="product-role-container">
    <a-input-search style="width: 300px; margin-bottom: 20px" allowClear v-model.trim="roleKeywordSearch" />
    <a-alert
      message="定义产品的角色，为这些角色分配功能模块的访问权限和首页"
      type="info"
      closable
      show-icon
      style="margin-bottom: 20px; width: calc(100% - 12px)"
    />
    <PerfectScrollbar style="height: calc(100vh - 400px)">
      <a-list :grid="grid" :data-source="vRoles" class="product-role-list">
        <a-list-item slot="renderItem" slot-scope="item, index">
          <a-card :title="null" size="small" v-if="item.uuid != '-1'" class="role-item-meta-card" hoverable>
            <a-row type="flex" style="flex-wrap: nowrap; align-items: center">
              <a-col flex="80px" v-if="!item.edit">
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
                    <a-input v-if="item.edit" style="width: 200px" v-model="item.new_name" />
                    <span v-else class="role-name">
                      {{ item.name }}
                      <!-- <a-tag>{{ item.id }}</a-tag> -->
                    </span>
                  </div>
                  <div style="padding-left: 24px">
                    <a-textarea
                      v-if="item.edit"
                      v-model="item.new_remark"
                      style="width: 90%; height: 54px"
                      :autoSize="{ minRows: 1, maxRows: 2 }"
                    />
                    <span v-else class="role-description">{{ item.remark }}</span>
                  </div>
                  <div v-if="!item.edit" class="role-link">
                    <a-avatar :size="20" shape="square" style="background: var(--w-primary-color-1); border-radius: 4px">
                      <Icon
                        slot="icon"
                        type="pticon iconfont icon-ptkj-lianjiebangding"
                        style="font-size: 14px; color: var(--w-primary-color)"
                      ></Icon>
                    </a-avatar>
                    首页
                    <span class="role-num">{{ item.privileges.length }}</span>
                    角色
                    <span class="role-num">{{ item.nestedRoles.length }}</span>
                  </div>
                </div>
              </a-col>
              <a-col
                flex="225px"
                :style="{
                  textAlign: 'right',
                  alignSelf: item.edit ? 'end' : 'auto'
                }"
              >
                <template v-if="!item.edit">
                  <Modal
                    title="关联角色权限"
                    :ok="e => saveRoleMember(e, item)"
                    :maxHeight="614"
                    :width="900"
                    destroyOnClose
                    :bodyStyle="{ padding: 'var(--w-padding-xs) var(--w-padding-md)' }"
                  >
                    <a-button type="link" size="small">
                      <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                      设置
                    </a-button>
                    <template slot="content">
                      <ProductRoleMember
                        :role-list="roles"
                        :modules="modules"
                        :versionId="versionId"
                        :role="item"
                        ref="roleMember"
                        :version-uuid="uuid"
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
                    <a-input v-model="newRole.name" style="width: 209px" />
                    <!-- <a-input style="width: 208px" :value="idSuffix()" @change="e => mergeIdSuffix(e)">
                    <span slot="addonBefore">{{ idPrefix }}</span>
                  </a-input> -->
                  </div>
                  <div style="padding-left: 24px">
                    <a-textarea v-model="newRole.remark" style="width: 90%; height: 54px" :autoSize="{ minRows: 1, maxRows: 2 }" />
                  </div>
                </div>
              </a-col>
              <a-col flex="160px" style="text-align: right; align-self: end">
                <a-button type="primary" :loading="newRole.loading" @click="saveRoleBasic(newRole)" style="margin-right: 8px">
                  保存
                </a-button>
                <a-button @click="addRoleReady = false">取消</a-button>
              </a-col>
            </a-row>
          </a-card>
        </a-list-item>
      </a-list>
    </PerfectScrollbar>
  </div>
</template>
<style lang="less">
.product-role-list {
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
        width: e('calc(100% - 40px)');
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
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import moment from 'moment';
import ProductRoleMember from './product-role-member.vue';
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'ProductRolePrivilege',
  inject: ['pageContext'],
  props: {
    versionId: String,
    uuid: String,
    modules: Array
  },
  components: { Modal, ProductRoleMember },
  computed: {
    vRoles() {
      let roles = [{ uuid: -1 }];
      for (let i = 0, len = this.roles.length; i < len; i++) {
        let r = this.roles[i];
        if (this.roleKeywordSearch) {
          if (r.name.indexOf(this.roleKeywordSearch) != -1 || (r.remark && r.remark.indexOf(this.roleKeywordSearch) != -1)) {
            roles.push(r);
          }
        } else {
          roles.push(r);
        }
      }

      return roles;
    }
  },
  data() {
    return {
      grid: { gutter: 16, xs: 1, sm: 1, md: 1, lg: 1, xl: 2, xxl: 2 },
      newRole: { name: undefined, remark: undefined, id: undefined, appId: this.versionId },
      addRoleReady: false,
      roleLoading: true,
      idPrefix: 'ROLE_',
      roleKeywordSearch: undefined,
      roles: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getRoles();
    this.pageContext.handleEvent('product:fetch:roles', () => {
      this.getRoles();
    });
  },
  mounted() {},
  methods: {
    onEditRolePrivilege(item) {
      this.$set(item, 'new_name', item.name);
      this.$set(item, 'new_remark', item.remark);
      this.$set(item, 'edit', true);
    },
    removeRolePrivilege(item, i, url) {
      this.$set(item, 'loading', true);
      $axios.post(`/proxy/api/security/${url}/removeAll`, [item.uuid]).then(({ data }) => {
        this.$set(item, 'loading', false);
        if (data.code == 0) {
          this.$message.success('删除成功');
          this.roles.splice(i, 1);
        }
      });
    },
    onClickAddRoleReady() {
      this.addRoleReady = true;
      this.newRole.name = undefined;
      this.newRole.id = this.idPrefix + moment().format('yyyyMMDDHHmmss');
      this.newRole.remark = undefined;
      this.newRole.uuid = undefined;
    },
    idSuffix() {
      return this.newRole.id ? this.newRole.id.split(this.idPrefix)[1] : undefined;
    },
    mergeIdSuffix(e) {
      this.newRole.id = e.target.value == '' ? undefined : this.idPrefix + e.target.value;
    },
    saveRoleMember(e, item) {
      let { nestedRoleRemoved, nestedRoleAdded, privilegeRemoved, privilegeAdded } = this.$refs.roleMember.collectRoleMember();
      this.onSaveRole({ role: { uuid: item.uuid }, nestedRoleAdded, nestedRoleRemoved, privilegeRemoved, privilegeAdded }, () => {
        this.syncGetRoleMember(item);
        $axios
          .post(`/proxy/api/security/role/publishRoleUpdatedEvent?uuid=${item.uuid}`, {})
          .then(({ data }) => {})
          .catch(error => {});
        e(true);
      });
    },
    saveRoleBasic(item) {
      if ((item.uuid && !item.new_name) || (!item.uuid && !item.name)) {
        this.$message.error('请填写角色名称');
        return false;
      }
      this.$set(item, 'loading', true);
      this.onSaveRole(
        {
          role: {
            uuid: item.uuid,
            name: item.new_name || item.name,
            id: item.id,
            code: item.id,
            remark: item.new_remark || item.remark,
            appId: this.versionId
          }
        },
        uuid => {
          this.$set(item, 'loading', false);
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
            this.$set(item, 'name', item.new_name);
            this.$set(item, 'remark', item.new_remark);
            item.recVer++;
          }
        }
      );
    },
    onSaveRole(formData, callback) {
      $axios.post(`/proxy/api/security/role/updateRoleMember`, [formData]).then(({ data }) => {
        if (data.data) {
          this.$message.success('保存成功！');

          callback.call(this, data.data[0]);
        }
      });
    },

    syncGetRoleMember(role) {
      $axios
        .get(`/proxy/api/security/role/getRoleMembers`, { params: { uuid: role.uuid } })
        .then(({ data }) => {
          if (data.data) {
            role.privileges.splice(0, role.privileges.length);
            role.nestedRoles.splice(0, role.nestedRoles.length);
            let { privileges, nestedRoles } = data.data;
            privileges.forEach(item => {
              if (item.appId == this.versionId) {
                role.privileges.push(item);
              }
            });
            let mids = [this.versionId];
            this.modules.forEach(item => {
              mids.push(item.moduleId);
            });
            nestedRoles.forEach(item => {
              if (mids.includes(item.appId)) {
                role.nestedRoles.push(item);
              }
            });
          }
        })
        .catch(error => {});
    },

    getRoles() {
      this.roles.splice(0, this.roles.length);
      $axios.get('/proxy/api/security/role/queryAppRoles', { params: { appId: this.versionId } }).then(({ data }) => {
        if (data.data) {
          for (let r of data.data) {
            if (r.systemDef == 1) {
              continue;
            }
            let { privileges, nestedRoles } = r;
            // 仅展示与该产品版本有关的角色权限
            for (let i = 0; i < privileges.length; i++) {
              if (privileges[i].appId != this.versionId) {
                privileges.splice(i--, 1);
              }
            }
            let mids = [this.versionId];
            this.modules.forEach(item => {
              mids.push(item.moduleId);
            });
            for (let i = 0; i < nestedRoles.length; i++) {
              if (!mids.includes(nestedRoles[i].appId)) {
                nestedRoles.splice(i--, 1);
              }
            }
            this.roles.push(r);
          }
          this.roleLoading = false;
        }
      });
    }
  }
};
</script>
