<template>
  <div class="role-select-panel">
    <a-row :gutter="16">
      <a-col :span="12">
        <div style="display: flex; align-items: center; justify-content: space-between; height: 56px">
          <label class="title">选择角色</label>
          <a-input-search @search="onSearchRole" allow-clear style="width: 170px" />
        </div>
        <div class="check-all-box">
          <a-checkbox :disabled="!editable" :indeterminate="indeterminate" :checked="checkAll" @change="e => onCheckAll(e)">
            全选
          </a-checkbox>
        </div>
        <div class="select-panel">
          <PerfectScrollbar :style="{ height: vHeight }">
            <div class="spin-center" v-if="loading">
              <a-spin />
            </div>
            <template v-for="role in roleSource">
              <a-checkbox
                :disabled="!editable"
                :key="role.uuid"
                :value="role.uuid"
                :checked="selectedKeys.includes(role.uuid)"
                @change="e => onChangeRoleCheck(e, role)"
                style="display: flex; align-items: center; padding: 8px 0"
              >
                <span :title="role.name" style="width: 190px" class="role-name">
                  {{ role.name }}
                </span>
              </a-checkbox>
            </template>
          </PerfectScrollbar>
        </div>
      </a-col>
      <a-col :span="12">
        <div style="display: flex; align-items: center; justify-content: space-between; height: 56px">
          <label class="title">已选角色</label>
          <a-button type="link" v-show="removeKeys.length > 0" @click="clearAllSelected">
            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
            删除
          </a-button>
        </div>
        <div class="check-all-box">
          <a-checkbox
            :disabled="!editable"
            :indeterminate="indeterminateSelected"
            :checked="checkAllSelected"
            @change="e => onCheckAllSelected(e)"
          >
            全选
          </a-checkbox>
        </div>
        <div class="user-role-check-list select-panel">
          <PerfectScrollbar :style="{ height: vHeight }">
            <template v-for="(role, i) in selectedRoles">
              <div :key="'role_checked_' + i" :title="role.name" class="selected-item">
                <a-checkbox
                  :disabled="!editable"
                  :key="role.uuid"
                  :value="role.uuid"
                  :checked="removeKeys.includes(role.uuid)"
                  @change="e => onChangeSelectd(e, role)"
                  style="display: flex; align-items: center; padding: 7px 0"
                >
                  <span class="role-name">{{ role.name }}</span>
                </a-checkbox>
                <a-icon class="close-icon" type="close" @click="removeSelectedRole(i)" />
              </div>
            </template>
          </PerfectScrollbar>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<style lang="less">
.role-select-panel {
  .title {
    font-size: var(--w-font-size-lg);
    color: var(--w-text-color-dark);
    font-weight: bold;
  }
  .check-all-box {
    border-radius: 4px 4px 0 0;
    border: 1px solid #e8e8e8;
    border-bottom: 0;
    padding: var(--w-padding-2xs) var(--w-padding-xs);
  }
  .select-panel {
    display: flex;
    flex-direction: column;
    padding-left: var(--w-padding-xs);
    overflow-y: auto;
    border: 1px solid #e8e8e8;
    border-radius: 0 0 4px 4px;
  }
  .role-name {
    display: inline-block;
    width: 160px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    color: #000000;
    vertical-align: text-bottom;
    line-height: 23px;
  }
  .user-role-check-list {
    .selected-item {
      margin-right: var(--w-padding-xs);
      cursor: pointer;
      font-size: var(--w-font-size-base);
      color: var(--w-text-color-dark);
      line-height: 24px;
      border-radius: 4px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .close-icon {
        opacity: 0;
      }
      &:hover {
        // background-color: var(--w-primary-color-1);
        .close-icon {
          opacity: 1;
        }
      }
    }
  }
}
</style>
<script type="text/babel">
export default {
  name: 'RoleSelectPanel',
  props: {
    value: Array,
    panelHeight: String,
    editable: {
      type: Boolean,
      default: true
    }
  },
  components: {},
  computed: {
    vHeight() {
      return this.panelHeight != undefined ? this.panelHeight : 'calc(100vh - 220px)';
    }
  },
  data() {
    return {
      loading: true,
      searchRoleKeyword: undefined,
      allRoleSource: [],
      roleSource: [],
      selectedKeys: this.value ? [...this.value] : [],
      selectedRoles: [],
      indeterminate: false,
      checkAll: false,
      indeterminateSelected: false, //已选的全选状态
      checkAllSelected: false, //已选的全选状态
      removeKeys: [] // 已选列的选中项
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchRoleSource();
  },
  mounted() {},
  methods: {
    clearAllSelected() {
      if (this.removeKeys.length == 0) {
        this.message('请选择要移除的角色');
        return false;
      }
      for (let i = 0; i < this.removeKeys.length; i++) {
        let j = this.selectedKeys.indexOf(this.removeKeys[i]);
        this.selectedKeys.splice(j, 1);
        this.selectedRoles.splice(j, 1);
      }
      this.removeKeys.splice(0, this.removeKeys.length);
      // this.selectedKeys.splice(0, this.selectedKeys.length);
      // this.selectedRoles.splice(0, this.selectedRoles.length);
      this.emitSelectChange();
    },
    onChangeRoleCheck(e, role) {
      let i = this.selectedKeys.indexOf(role.uuid);
      if (i == -1) {
        this.selectedKeys.push(role.uuid);
        this.selectedRoles.push(role);
      } else {
        this.selectedKeys.splice(i, 1);
        this.selectedRoles.splice(i, 1);

        let j = this.removeKeys.indexOf(role.uuid);
        if (j > -1) {
          this.removeKeys.splice(j, 1);
        }
      }
      this.emitSelectChange();
    },
    emitSelectChange() {
      this.setCheckAllSelectedStatus();
      this.setCheckAllStatus();
      this.$emit('input', this.selectedKeys);
      this.$emit('change', {
        selectedKeys: this.selectedKeys,
        selectedRoles: this.selectedRoles
      });
    },
    onSearchRole(v) {
      this.searchRoleKeyword = v;
      this.roleSource = this.allRoleSource.filter(role => this.isShowRole(role));
      this.setCheckAllStatus();
    },
    removeSelectedRole(i) {
      let j = this.removeKeys.indexOf(this.selectedKeys[i]);
      if (j > -1) {
        this.removeKeys.splice(j, 1);
      }
      this.selectedKeys.splice(i, 1);
      this.selectedRoles.splice(i, 1);
      this.emitSelectChange();
    },
    fetchRoleSource() {
      $axios
        .get(`/proxy/api/security/role/getRolesInTenantSystem`, {
          params: {
            system: this._$SYSTEM_ID,
            tenant: this._$USER.tenantId
          }
        })
        .then(({ data }) => {
          for (let i = 0; i < data.data.length; i++) {
            if (data.data[i].systemDef == 1) {
              data.data.splice(i--, 1);
            }
          }
          this.allRoleSource = data.data;
          this.roleSource = data.data;
          this.loading = false;
          if (this.selectedKeys.length) {
            let roleMap = {};
            for (let r of this.roleSource) {
              roleMap[r.uuid] = r;
            }
            for (let i = 0; i < this.selectedKeys.length; i++) {
              if (roleMap[this.selectedKeys[i]]) {
                this.selectedRoles.push(roleMap[this.selectedKeys[i]]);
              } else {
                this.selectedKeys.splice(i, 1);
                this.selectedRoles.splice(i--, 1);
              }
            }
          }
          this.setCheckAllStatus();
        })
        .catch(error => {});
    },
    isShowRole(role) {
      return (
        this.searchRoleKeyword == undefined ||
        this.searchRoleKeyword == '' ||
        (role.name && role.name.indexOf(this.searchRoleKeyword.trim()) != -1)
      );
    },
    // 设置角色全选复选框状态
    setCheckAllStatus() {
      let selectedData = [];
      for (let i = 0; i < this.roleSource.length; i++) {
        if (this.selectedKeys.includes(this.roleSource[i].uuid)) {
          selectedData.push(this.roleSource[i]);
        }
      }
      this.checkAll = this.roleSource.length > 0 && this.roleSource.length == selectedData.length;
      this.indeterminate = this.roleSource.length > 0 && selectedData.length > 0 && this.roleSource.length > selectedData.length;
    },
    onCheckAll(e) {
      this.checkAll = e.target.checked;
      if (this.checkAll) {
        // 全选
        for (let i = 0; i < this.roleSource.length; i++) {
          let role = this.roleSource[i];
          if (!this.selectedKeys.includes(role.uuid)) {
            this.selectedKeys.push(role.uuid);
            this.selectedRoles.push(role);
          }
        }
      } else {
        // 全不选
        for (let i = 0; i < this.roleSource.length; i++) {
          let role = this.roleSource[i];
          let j = this.selectedKeys.indexOf(role.uuid);
          if (j > -1) {
            this.selectedKeys.splice(j, 1);
            this.selectedRoles.splice(j, 1);
          }
        }
        this.removeKeys.splice(0, this.removeKeys.length);
      }
      this.emitSelectChange();
    },
    // 设置已选列全选复选框状态
    setCheckAllSelectedStatus() {
      this.checkAllSelected = this.selectedRoles.length > 0 && this.selectedRoles.length == this.removeKeys.length;
      this.indeterminateSelected =
        this.selectedRoles.length > 0 && this.removeKeys.length > 0 && this.selectedRoles.length > this.removeKeys.length;
    },
    onCheckAllSelected(e) {
      this.checkAllSelected = e.target.checked;
      if (this.checkAllSelected) {
        // 全选
        this.removeKeys.splice(0, this.removeKeys.length);
        for (let i = 0; i < this.selectedKeys.length; i++) {
          this.removeKeys.push(this.selectedKeys[i]);
        }
      } else {
        this.removeKeys.splice(0, this.removeKeys.length);
      }
      this.setCheckAllSelectedStatus();
    },
    onChangeSelectd(e, role) {
      let i = this.removeKeys.indexOf(role.uuid);
      if (i == -1) {
        this.removeKeys.push(role.uuid);
      } else {
        this.removeKeys.splice(i, 1);
      }
      this.setCheckAllSelectedStatus();
    }
  }
};
</script>
