<template>
  <div
    :style="{
      width: '100%'
    }"
    :class="[
      'module-role-td-container',
      row.roleUuid ? 'is-role-div' : '',
      row.roleUuid && rowIndex + 1 == row.moduleRoleCount ? 'last-index' : '',
      widgetTableContext.expandedRowKeys.indexOf(row.UUID) == -1 ? (row.roleUuid ? '' : 'module-role-td-unExpand') : ''
    ]"
  >
    <a-row v-if="row.ID" type="flex">
      <a-col flex="60px">
        <a-avatar
          shape="square"
          :size="40"
          :style="{
            background: moduleIcon.bgColor
              ? moduleIcon.bgColor.startsWith('--')
                ? 'var(' + moduleIcon.bgColor + ')'
                : moduleIcon.bgColor
              : 'var(--w-primary-color)',
            borderRadius: '8px'
          }"
        >
          <Icon slot="icon" :type="moduleIcon.icon || 'pticon iconfont icon-ptkj-zaitechengjian'" style="font-size: 24px" />
        </a-avatar>
      </a-col>
      <a-col flex="calc(100% - 400px)">
        <div>
          <div style="font-weight: bold; line-height: var(--w-widget-table-thead-text-size)">{{ row.NAME }}</div>
          <template v-if="row.children != undefined">
            <template v-for="(item, i) in row.children">
              <a-tag
                :key="item.roleUuid"
                class="tag-no-border"
                style="--w-tag-border-radius: var(--w-border-radius-2); margin-top: 6px; --w-tag-height: 20px"
              >
                <a-icon type="safety" />
                {{ item.roleName }}
              </a-tag>
            </template>
          </template>
        </div>
      </a-col>
      <a-col flex="140px" style="text-align: right">
        <Drawer
          title="设置"
          ref="drawer"
          :width="500"
          style="text-align: left"
          mask
          :ok="saveRoleMember"
          okText="保存"
          :cancel="cancelSaveRoleMember"
        >
          <a-button size="small" type="link" @click="startSetRoleMember">
            <Icon type="iconfont icon-ptkj-shezhi"></Icon>
            设置
          </a-button>
          <template slot="content">
            <template v-if="row.children != undefined && row.children.length > 0">
              <a-form-model :colon="false" class="pt-form">
                <template v-for="(item, i) in row.children">
                  <a-form-model-item :label="item.roleName">
                    <OrgSelect
                      v-model="item.memberKeys"
                      :orgType="['MyOrg', 'MySystemRole', 'PublicGroup']"
                      ref="orgSelect"
                      :orgTypeExtensions="[{ label: '角色', value: 'MySystemRole' }]"
                      :params="params"
                      @change="onChangeOrgSelect"
                      :enableCache="true"
                      :key="orgSelectKey"
                    ></OrgSelect>
                  </a-form-model-item>
                </template>
              </a-form-model>
            </template>
            <a-empty v-else description="模块暂无角色数据" />
          </template>
        </Drawer>
      </a-col>
    </a-row>

    <div v-if="row.roleUuid" :class="['role-div']">
      <div style="display: flex; line-height: 40px; color: var(--w-text-color-light)" v-if="rowIndex == 0">
        <div style="width: 245px; padding-left: var(--w-padding-2xs)">角色</div>
        <div style="color: #929292; padding-left: var(--w-padding-2xs)">成员</div>
      </div>
      <a-row type="flex" style="align-items: center" class="role-div-content">
        <a-col
          :title="row.roleName"
          style="line-height: 32px; padding-bottom: 4px; width: 245px; overflow: hidden; text-overflow: ellipsis; text-wrap: nowrap"
        >
          {{ row.roleName }}
        </a-col>
        <a-col flex="calc(100% - 500px)">
          <a-button size="small" type="link" icon="loading" v-if="row.loading" />
          <template v-if="row.members != undefined">
            <template v-for="(mem, u) in row.members">
              <a-tag
                v-if="!planDeleteKeys.includes(mem.id)"
                :key="'role_' + row.roleUuid + '_member_' + mem.id"
                class="primary-color"
                style="margin-bottom: 4px; --w-tag-border-radius: var(--w-border-radius-2)"
                @close="planRemoveRoleMember(mem)"
                closable
              >
                <a-icon type="user" v-if="mem.type == 'user'" />
                <a-icon type="safety" v-else-if="mem.type == 'securityRole'" />
                <Icon :type="orgElementIcon[mem.type] || 'file'" v-else style="font-size: 12px" />

                {{ mem.name }}
              </a-tag>
            </template>
          </template>
        </a-col>
        <a-col flex="140px">
          <div v-if="row.members != undefined && row.members.length > 0 && planDeleteKeys.length > 0" style="text-align: right">
            <!-- <a-button type="link" size="small" icon="edit" @click="editRowRole" v-if="!edit">编辑</a-button> -->
            <a-button type="link" :loading="saving" size="small" @click="saveDeleteRoleMember">
              <Icon type="iconfont icon-ptkj-baocun"></Icon>
              保存
            </a-button>
            <a-button
              size="small"
              type="link"
              @click="
                () => {
                  planDeleteKeys.splice(0, planDeleteKeys.length);
                }
              "
            >
              <Icon type="iconfont icon-ptkj-dacha"></Icon>
              取消
            </a-button>
          </div>
        </a-col>
      </a-row>
    </div>
  </div>
</template>
<style lang="less">
.widget-table {
  .ant-table-row-level-1 {
    --w-widget-table-td-padding: 0;
    --w-widget-table-td-margin: 0 0 0 40px;
    --w-widget-table-td-border-color: var(--w-gray-color-2);
    --w-table-tr-background-hover: transparent;
    .is-role-div {
      padding: 0 var(--w-padding-xs);
      border-left: 1px solid var(--w-border-color-light);
      border-right: 1px solid var(--w-border-color-light);
      background: var(--w-gray-color-2);
      &.last-index {
        border-bottom: 1px solid var(--w-border-color-light);
        border-bottom-left-radius: var(--w-border-radius-2);
        border-bottom-right-radius: var(--w-border-radius-2);
        margin-bottom: 14px;
        .role-div {
          margin-bottom: var(--w-margin-2xs);
        }
      }
    }
    .is-role-div .role-div {
      border-bottom: 1px solid var(--w-border-color-light);
      .role-div-content {
        padding: var(--w-padding-3xs) var(--w-padding-2xs) 0;
        &:hover {
          background: var(--w-primary-color-1);
        }
      }
    }
  }
  .ant-table-row-level-0 + .ant-table-row-level-1 {
    .is-role-div {
      border-top-left-radius: var(--w-border-radius-2);
      border-top-right-radius: var(--w-border-radius-2);
      border-top: 1px solid var(--w-border-color-light);
    }
  }
  .ant-table-row-level-0 .module-role-td-unExpand,
  .ant-table-row-level-1 .is-role-div.last-index {
    position: relative;
    &::before {
      content: '';
      display: block;
      width: e('calc(100% + 37px)');
      height: 1px;
      background: var(--w-border-color-light);
      position: absolute;
      bottom: 0;
      bottom: -14px;
      left: -38px;
    }
  }
}
</style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import OrgSelect from '@admin/app/web/lib/org-select.vue';

export default {
  name: 'ModuleRoleTdRender',
  inject: ['widgetTableContext'],
  props: {
    row: Object,
    rowIndex: Number
  },
  components: { Drawer, OrgSelect },
  computed: {
    orgElementIcon() {
      return this.widgetTableContext.orgElementIcon;
    },
    roleMemberMap() {
      let map = {};
      if (this.row.members) {
        for (let i = 0, len = this.row.members.length; i < len; i++) {
          map[this.row.members[i].id] = this.row.members[i];
        }
      }
      return map;
    },
    moduleIcon() {
      return this.iconDataToJson(this.row.ICON);
    }
  },
  data() {
    return {
      params: {
        system: this._$SYSTEM_ID
      },
      memberSelectKeyMap: {},
      planEditRoleMembers: [],
      planAddKeys: [],
      planDeleteKeys: [],

      saving: false,
      orgSelectKey: this.row.UUID + '_orgSelect',
      tempMemberKeys: {}
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    if (this.row.ID) {
      this.$set(this.row, 'children', []);
      this.fetchModuleRoles();
      if (this.widgetTableContext.orgElementIcon == undefined) {
        this.widgetTableContext.orgElementIcon = {};
        this.fetchOrgElementModel();
      }
    } else if (this.row.roleUuid) {
      // 角色行
      this.fetchRoleMember(this.row.roleUuid).then(responses => {
        if (responses) {
          let orgElementResponse = responses[0];
          let orgElements = orgElementResponse.data.data;
          if (orgElements) {
            for (let i = 0, len = orgElements.length; i < len; i++) {
              this.row.memberKeys.push(orgElements[i].id);
              this.row.members.push({
                id: orgElements[i].id,
                uuid: orgElements[i].uuid,
                name: orgElements[i].name,
                type: orgElements[i].type
              });
            }
          }
          let userResponse = responses[1];
          let users = userResponse.data.data;
          if (users) {
            for (let i = 0, len = users.length; i < len; i++) {
              this.row.memberKeys.push(users[i].userId);
              this.row.members.push({
                id: users[i].userId,
                name: users[i].userName,
                type: 'user'
              });
            }
          }
          let roleResponse = responses[2];
          let roles = roleResponse.data.data;
          if (roles) {
            for (let i = 0, len = roles.length; i < len; i++) {
              this.row.memberKeys.push(roles[i].uuid);
              this.row.members.push({
                id: roles[i].uuid,
                name: roles[i].name,
                type: 'securityRole'
              });
            }
          }
          let groups = responses[3].data.data;
          if (groups) {
            for (let i = 0, len = groups.length; i < len; i++) {
              this.row.memberKeys.push(groups[i].id);
              this.row.members.push({
                id: groups[i].id,
                uuid: groups[i].uuid,
                name: groups[i].name,
                type: 'group'
              });
            }
          }
          this.row.loading = false;
        }
      });
    }
    if (!this.widgetTableContext.expandIcon) {
      this.widgetTableContext.expandIcon = this.customExpandIcon;
    }
  },
  methods: {
    startSetRoleMember() {
      for (let i = 0, len = this.row.children.length; i < len; i++) {
        this.tempMemberKeys[this.row.children[i].roleUuid] = [...this.row.children[i].memberKeys];
      }
    },
    saveDeleteRoleMember() {
      if (this.planDeleteKeys.length && !this.saving) {
        let userIdRemoved = [],
          orgElementRemoved = [],
          nestedRoleRemoved = [];
        this.planDeleteKeys.forEach(k => {
          let n = this.roleMemberMap[k];
          if (n.type == 'user') {
            userIdRemoved.push(k);
          } else if (n.type == 'securityRole') {
            nestedRoleRemoved.push(k);
          } else {
            orgElementRemoved.push(n.data && n.data.uuid ? n.data.uuid : k);
          }
        });
        this.saving = true;
        $axios
          .post(`/proxy/api/security/role/updateRoleMember`, [
            {
              role: {
                uuid: this.row.roleUuid
              },
              userIdRemoved,
              orgElementRemoved,
              nestedRoleRemoved
            }
          ])
          .then(({ data }) => {
            this.saving = false;
            if (data.data) {
              this.$message.success('保存成功');
              // 更新
              for (let i = 0; i < this.row.members.length; i++) {
                if (this.planDeleteKeys.includes(this.row.members[i].id)) {
                  this.row.members.splice(i--, 1);
                }
              }
              for (let i = 0; i < this.row.memberKeys.length; i++) {
                if (this.planDeleteKeys.includes(this.row.memberKeys[i])) {
                  this.row.memberKeys.splice(i--, 1);
                }
              }
              this.planDeleteKeys.splice(0, this.planDeleteKeys.length);
            } else {
              this.$message.error('保存异常');
            }
          })
          .catch(() => {
            this.saving = false;
            this.$message.error('保存异常');
          });
      }
    },
    planRemoveRoleMember(item) {
      this.planDeleteKeys.push(item.id);
    },

    fetchOrgElementModel() {
      $axios.get('/proxy/api/org/elementModel/getAllOrgElementModels', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          for (let i = 0, len = data.data.length; i < len; i++) {
            if (data.data[i].enable) {
              this.widgetTableContext.orgElementIcon[data.data[i].id] = data.data[i].icon;
            }
          }
        }
      });
    },
    cancelSaveRoleMember() {
      for (let i = 0, len = this.row.children.length; i < len; i++) {
        this.row.children[i].memberKeys.splice(0, this.row.children[i].memberKeys.length);
        this.row.children[i].memberKeys.push(...this.tempMemberKeys[this.row.children[i].roleUuid]);
      }

      this.$nextTick(() => {
        this.tempMemberKeys = {};
      });
    },
    saveRoleMember(e) {
      let moduleRoles = this.row.children;

      let updates = [],
        roleMemberMap = {};
      for (let i = 0, len = moduleRoles.length; i < len; i++) {
        let role = moduleRoles[i];
        roleMemberMap[role.roleUuid] = role;
        let members = role.members,
          memberKeys = role.memberKeys;
        let userIdAdded = [],
          userIdRemoved = [],
          orgElementRemoved = [],
          orgElementAdded = [],
          nestedRoleRemoved = [],
          nestedRoleAdded = [];
        // 比较出增量变更的数据
        let originalKeys = [];
        members.forEach(m => {
          originalKeys.push(m.id);
          if (!memberKeys.includes(m.id)) {
            // 被删除的元素
            if (m.type == 'user') {
              userIdRemoved.push(m.id);
            } else if (m.type == 'securityRole') {
              nestedRoleRemoved.push(m.id);
            } else {
              orgElementRemoved.push(m.id);
            }
          }
        });
        memberKeys.forEach(k => {
          if (!originalKeys.includes(k)) {
            let n = this.memberSelectKeyMap[k];
            if (n.type == 'user') {
              userIdAdded.push(k);
            } else if (n.type == 'securityRole') {
              nestedRoleAdded.push(k);
            } else if (n.type == 'group') {
              orgElementAdded.push(k);
            } else {
              orgElementAdded.push(n.data.uuid); // 组织单元实例要通过uuid与角色进行关联
            }
          }
        });
        if (
          userIdAdded.length ||
          userIdRemoved.length ||
          orgElementAdded.length ||
          orgElementRemoved.length ||
          nestedRoleAdded.length ||
          nestedRoleRemoved.length
        ) {
          updates.push({
            role: {
              uuid: role.roleUuid
            },
            userIdAdded,
            userIdRemoved,
            orgElementAdded,
            orgElementRemoved,
            nestedRoleAdded,
            nestedRoleRemoved
          });
        }
      }

      if (updates.length > 0) {
        $axios
          .post(`/proxy/api/security/role/updateRoleMember`, updates)
          .then(({ data }) => {
            if (data.data) {
              e(true);
              this.$message.success('保存模块角色成员成功');
              // 更新数据到成员列表
              for (let i = 0, len = updates.length; i < len; i++) {
                let role = roleMemberMap[updates[i].role.uuid];
                if (role) {
                  let { userIdAdded, userIdRemoved, orgElementAdded, orgElementRemoved, nestedRoleAdded, nestedRoleRemoved } = updates[i];
                  let removed = [...userIdRemoved, ...orgElementRemoved, ...nestedRoleRemoved];
                  if (removed.length) {
                    for (let i = 0; i < role.members.length; i++) {
                      if (removed.includes(role.members[i].id)) {
                        role.members.splice(i--, 1);
                      }
                    }
                  }
                  let added = [...userIdAdded, ...orgElementAdded, ...nestedRoleAdded];
                  if (added.length) {
                    added.forEach(key => {
                      let n = this.memberSelectKeyMap[key];
                      role.members.push({
                        id: n.key,
                        uuid: n.data ? n.data.uuid : undefined,
                        name: n.title,
                        type: n.type
                      });
                    });
                  }
                }
              }
              this.$refs.drawer.visible = false;
            } else {
              this.$message.error('保存模块角色成员异常');
            }
          })
          .catch(error => {});
      } else {
        this.$message.success('保存模块角色成员成功');
        this.$refs.drawer.visible = false;
      }
    },
    onChangeOrgSelect({ labels, nodes, value }) {
      console.log(arguments);
      nodes.forEach(n => {
        this.memberSelectKeyMap[n.key] = n;
        if (n.data && n.data.uuid) {
          this.memberSelectKeyMap[n.data.uuid] = n;
        }
      });
    },
    fetchRoleMember(roleUuid) {
      return Promise.all([
        $axios.get(`/proxy/api/org/organization/version/getOrgElementsRelaRole`, {
          params: {
            roleUuid,
            system: this._$SYSTEM_ID
          }
        }),

        $axios.get(`/proxy/api/org/organization/user/getRoleRelaUsers`, {
          params: {
            roleUuid,
            system: this._$SYSTEM_ID
          }
        }),
        $axios.get(`/proxy/api/security/role/getRoleNestedRoles`, { params: { uuid: roleUuid } }),
        $axios.get(`/proxy/api/org/organization/orgGroup/getRoleRelaGroups`, {
          params: {
            roleUuid,
            system: this._$SYSTEM_ID
          }
        })
      ]);
    },
    fetchModuleRoles() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/security/role/queryAppRoles`, { params: { appId: this.row.ID } })
          .then(({ data }) => {
            if (data.data) {
              for (let i = 0, len = data.data.length; i < len; i++) {
                this.row.children.push({
                  moduleUuid: this.row.UUID,
                  roleUuid: data.data[i].uuid,
                  roleName: data.data[i].name,
                  loading: true,
                  members: [],
                  memberKeys: [],
                  moduleRoleCount: data.data.length
                });
              }
              if (data.data.length > 0) {
                this.widgetTableContext.expandedRowKeys.push(this.row.UUID);
              }
            }
          })
          .catch(error => {});
      });
    },
    iconDataToJson(data) {
      if (!data) {
        data = {
          icon: '',
          bgColor: ''
        };
      } else {
        try {
          let iconJson = JSON.parse(data);
          if (iconJson) {
            data = iconJson;
          }
        } catch (e) {
          if (typeof data == 'string') {
            let iconJson = {
              icon: data,
              bgColor: ''
            };
            data = iconJson;
          }
          return data;
        }
      }
      return data;
    },
    customExpandIcon(props) {
      return props.record.children ? (
        <span onClick={e => props.onExpand(props.record, e)} style="padding-right:var(--w-padding-xs);color:var(--w-text-color-dark)">
          {props.expanded ? (
            <i
              class="ant-iconfont caret-down"
              style="font-size:var(--w-font-size-base);transform: rotate(180deg);transition: transform 0.3s;display: inline-block;"
            />
          ) : (
            <i
              class="ant-iconfont caret-down"
              style="font-size:var(--w-font-size-base);transition: transform 0.3s;display: inline-block;"
            />
          )}
        </span>
      ) : undefined;
    }
  }
};
</script>
