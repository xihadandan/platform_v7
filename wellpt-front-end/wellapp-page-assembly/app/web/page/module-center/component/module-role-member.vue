<template>
  <a-spin tip="数据加载中" :spinning="loading">
    <a-row class="module-role-member">
      <a-col :span="12">
        <div>
          <div class="sub-title">菜单</div>
          <a-radio-group v-model="activeKey" button-style="solid" class="select-pane-type">
            <a-radio-button value="p">权限</a-radio-button>
            <a-radio-button value="r">角色</a-radio-button>
          </a-radio-group>
          <div v-show="activeKey == 'p'">
            <div style="margin-bottom: 12px">
              <a-input-search allow-clear v-model.trim="searchPrivileges" />
            </div>
            <div class="checkbox-row" v-show="privileges.length">
              <a-checkbox
                :indeterminate="privilegeIndeterminate"
                :checked="privilegeCheckAll"
                @change="e => onCheckAllChange(e, 'privilegeChecked', 'privilegeIndeterminate', 'privilegeCheckAll')"
              >
                全选
              </a-checkbox>
            </div>
            <PerfectScrollbar style="height: 350px">
              <a-checkbox-group
                class="role-checkbox-group"
                v-model="privilegeChecked"
                @change="onCheckChange('privilegeIndeterminate', 'privilegeChecked', 'privilegeCheckAll', allPrivilegeKeys)"
              >
                <a-row
                  v-for="(privilege, i) in privileges"
                  :key="'priv_ck_' + privilege.uuid"
                  class="checkbox-row"
                  v-show="searchPrivileges == undefined || privilege.name.indexOf(searchPrivileges) != -1"
                >
                  <a-col :span="24">
                    <a-checkbox :value="privilege.uuid">
                      <div class="checkbox-row-item" :title="privilege.name">
                        <div class="title">
                          <Icon type="pticon iconfont icon-ptkj-quanxian" />
                          {{ privilege.name }}
                        </div>
                        <label class="remark" :title="privilege.remark">
                          {{ privilege.remark }}
                        </label>
                      </div>
                    </a-checkbox>
                  </a-col>
                </a-row>
              </a-checkbox-group>
            </PerfectScrollbar>
          </div>
          <div v-show="activeKey == 'r'">
            <div style="margin-bottom: 12px">
              <a-input-search allow-clear v-model.trim="searchRole" />
            </div>
            <div class="checkbox-row" v-show="roles.length">
              <a-checkbox
                :indeterminate="roleIndeterminate"
                :checked="roleCheckAll"
                @change="e => onCheckAllChange(e, 'roleChecked', 'roleIndeterminate', 'roleCheckAll')"
              >
                全选
              </a-checkbox>
            </div>
            <PerfectScrollbar style="height: 350px">
              <a-checkbox-group
                class="role-checkbox-group"
                v-model="roleChecked"
                @change="onCheckChange('roleIndeterminate', 'roleChecked', 'roleCheckAll', allRoleKeys)"
              >
                <template v-for="(role, i) in roles">
                  <a-row
                    :key="'role_ck_' + role.uuid"
                    class="checkbox-row"
                    v-if="role.uuid != roleUuid"
                    v-show="
                      searchRole == undefined ||
                      role.name.indexOf(searchRole) != -1 ||
                      (role.remark != undefined && role.remark.indexOf(searchRole) != -1)
                    "
                  >
                    <a-col :span="24">
                      <a-checkbox :value="role.uuid">
                        <div class="checkbox-row-item" :title="role.name">
                          <div class="title">
                            <Icon type="pticon iconfont icon-ptkj-jiaose" />
                            {{ role.name }}
                          </div>
                          <label class="remark" :title="role.remark">
                            {{ role.remark }}
                          </label>
                        </div>
                      </a-checkbox>
                    </a-col>
                  </a-row>
                </template>
              </a-checkbox-group>
            </PerfectScrollbar>
          </div>
        </div>
      </a-col>
      <a-col :span="12">
        <div>
          <div class="sub-title">
            已选
            <span style="float: right">
              <a-input-search v-model.trim="searchSelected" style="width: 130px" allow-clear />
            </span>
          </div>
          <PerfectScrollbar style="height: 482px">
            <template v-for="(opt, i) in checkedOptions">
              <div
                class="checkbox-row selected"
                :key="'opt_' + i"
                v-show="searchSelected == undefined || opt.name.indexOf(searchSelected) != -1"
              >
                <div class="title" :title="opt.name">
                  <Icon :type="opt.type == 'page' ? 'pticon iconfont icon-szgy-zhuye' : 'pticon iconfont icon-ptkj-jiaose'" />
                  <span>{{ opt.name }}</span>
                </div>
                <a-tag
                  color="blue"
                  v-if="opt.module != undefined"
                  :style="{ position: 'absolute', right: '20px', top: '50%', marginTop: '-12px' }"
                >
                  {{ opt.module }}
                </a-tag>
                <a-button
                  size="small"
                  type="link"
                  icon="close"
                  @click="removeChecked(opt, i)"
                  :style="{ position: 'absolute', right: '4px', top: '50%', marginTop: '-12px' }"
                ></a-button>
              </div>
            </template>
            <a-empty v-if="checkedOptions.length == 0">
              <span slot="description" class="empty-description">暂无数据</span>
            </a-empty>
          </PerfectScrollbar>
        </div>
      </a-col>
    </a-row>
  </a-spin>
</template>
<style lang="less">
.module-role-member {
  border-radius: 4px;
  border: 1px solid var(--w-border-color-light);
  > .ant-col {
    padding: var(--w-padding-xs) var(--w-padding-md);
    border-left: 1px solid var(--w-border-color-light);

    .ps {
      margin-right: e('calc(0px - var(--w-padding-sm))');
      padding-right: var(--w-padding-xs);
    }
    &:first-child {
      border-left: 0;
    }

    .sub-title {
      font-weight: bold;
      font-size: var(--w-font-size-lg);
      line-height: 32px;
      color: var(--w-text-color-dark);
      margin-bottom: 12px;
    }

    .select-pane-type {
      display: flex;
      margin-bottom: 12px;
      > label {
        flex: 1;
        text-align: center;
      }
    }

    .checkbox-row {
      padding: 8px 12px;
      line-height: 24px;
      --w-checkbox-text-color-checked: var(--w-primary-color);
      --w-checkbox-text-color: #000000;
      cursor: pointer;
      border-radius: 4px;

      &:hover {
        background: var(--w-primary-color-1);
      }

      .checkbox-row-item {
        display: inline-flex;
        width: e('calc(100% - 32px)');
        align-items: center;
        justify-content: space-between;
      }

      .title {
        text-overflow: ellipsis;
        max-width: 115px;
        overflow: hidden;
        white-space: nowrap;
      }

      .remark {
        color: var(--w-text-color-light);
        font-size: var(--w-font-size-sm);
        display: inline-block;
        max-width: 100px;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        vertical-align: sub;
        padding-left: 8px;
      }

      &.selected {
        color: #000000;
        position: relative;
        .ant-btn-link {
          --w-button-font-color: #000000;
        }
      }
    }
    .role-checkbox-group {
      width: 100%;
    }

    .role-name-preview {
      font-weight: normal;
      color: var(--w-text-color-light);
      font-size: var(--w-font-size-base);
      display: inline-block;
      max-width: 160px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
      vertical-align: bottom;
    }

    .empty-description {
      color: var(--w-text-color-light);
      font-size: var(--w-font-size-base);
      line-height: 32px;
    }
  }
}
</style>
<script type="text/babel">
export default {
  name: 'ModuleRoleMember',
  props: {
    roleUuid: String,
    roles: Array,
    privileges: Array
  },
  components: {},
  computed: {
    checkedOptions() {
      let options = [];
      for (let r of this.roles) {
        if (this.roleChecked.includes(r.uuid)) {
          r.type = 'ROLE';
          options.push(r);
        }
      }
      for (let r of this.privileges) {
        if (this.privilegeChecked.includes(r.uuid)) {
          r.type = 'PRIVILEGE';
          options.push(r);
        }
      }
      return options;
    },

    allRoleKeys() {
      let keys = [];
      if (this.roles != undefined) {
        for (let r of this.roles) {
          if (r.uuid != this.roleUuid) {
            keys.push(r.uuid);
          }
        }
      }
      return keys;
    },
    roleMap() {
      let map = {};
      if (this.roles != undefined) {
        for (let r of this.roles) {
          map[r.uuid] = r;
        }
      }
      return map;
    },

    allPrivilegeKeys() {
      let keys = [];
      if (this.privileges != undefined) {
        for (let r of this.privileges) {
          keys.push(r.uuid);
        }
      }
      return keys;
    }
  },
  data() {
    return {
      roleChecked: [],
      privilegeChecked: [],
      roleIndeterminate: false,
      privilegeIndeterminate: false,
      roleCheckAll: false,
      privilegeCheckAll: false,
      loading: true,
      activeKey: 'p',
      searchPrivileges: '',
      searchRole: '',
      searchSelected: ''
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getRoleMems();
  },
  mounted() {
    this.updateScrollbar();
  },
  methods: {
    updateScrollbar() {
      let _this = this;
      setTimeout(() => {
        let $ps = _this.$el.querySelectorAll('.ps');
        for (let i = 0, len = $ps.length; i < len; i++) {
          $ps[i].__vue__.update();
        }
      }, 0);
    },
    onCheckChange(indKey, checkedKey, checkedAllKey, allKeys) {
      this[indKey] = !!this[checkedKey].length && this[checkedKey].length < allKeys.length;
      this[checkedAllKey] = this[checkedKey].length === allKeys.length;
      this.$emit('change', { roleChecked: this.roleChecked, privilegeChecked: this.privilegeChecked });
    },
    getRoleMems() {
      $axios.get(`/proxy/api/security/role/getRoleMembers`, { params: { uuid: this.roleUuid } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          let { privileges, nestedRoles } = data.data;
          for (let p of privileges) {
            if (this.allPrivilegeKeys.includes(p.uuid)) {
              this.privilegeChecked.push(p.uuid);
            }
          }

          for (let p of nestedRoles) {
            if (this.allRoleKeys.includes(p.uuid)) {
              this.roleChecked.push(p.uuid);
            }
          }

          this.$emit('change', { roleChecked: this.roleChecked, privilegeChecked: this.privilegeChecked });
          this.loading = false;
        }
      });
    },
    onCheckAllChange(e, checkedKey, indKey, checkedAllKey) {
      this[checkedKey] = e.target.checked ? (checkedKey == 'roleChecked' ? this.allRoleKeys : this.allPrivilegeKeys) : [];
      this[indKey] = false;
      this[checkedAllKey] = e.target.checked;
      this.$emit('change', { roleChecked: this.roleChecked, privilegeChecked: this.privilegeChecked });
    },
    removeChecked(opt, i) {
      let idx = this.roleChecked.indexOf(opt.uuid);
      if (idx != -1) {
        this.roleChecked.splice(idx, 1);
        this.onCheckChange('roleIndeterminate', 'roleChecked', 'roleCheckAll', this.allRoleKeys);
      } else {
        idx = this.privilegeChecked.indexOf(opt.uuid);
        if (idx != -1) {
          this.privilegeChecked.splice(idx, 1);
          this.onCheckChange('privilegeIndeterminate', 'privilegeChecked', 'privilegeCheckAll', this.allPrivilegeKeys);
        }
      }
    }
  }
};
</script>
