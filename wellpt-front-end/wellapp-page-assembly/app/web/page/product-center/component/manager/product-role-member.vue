<template>
  <a-row class="product-role-member" :gutter="0">
    <a-col :span="activeKey == 'role' ? 8 : 16">
      <div>
        <div class="sub-title">菜单</div>
        <a-radio-group v-model="activeKey" button-style="solid" class="select-pane-type">
          <a-radio-button value="index">
            <!-- <Icon :type="pageLoading ? 'loading' : 'pticon iconfont icon-szgy-zhuye'" /> -->
            首页
          </a-radio-button>
          <a-radio-button value="role">
            <!-- <Icon :type="roleLoading ? 'loading' : 'pticon iconfont icon-ptkj-jiaose'" /> -->
            角色
          </a-radio-button>
        </a-radio-group>
        <div class="product-role-member-index-list" v-show="activeKey == 'index'">
          <div style="margin-bottom: 12px">
            <a-input-search allow-clear v-model.trim="searchPage" />
          </div>
          <div class="checkbox-row" v-show="pages.length">
            <a-checkbox :indeterminate="pageIndeterminate" :checked="pageCheckAll" @change="e => onCheckAllPage(e)">全选</a-checkbox>
          </div>
          <PerfectScrollbar style="height: 256px">
            <a-checkbox-group class="role-checkbox-group" v-model="pageChecked" @change="onPageCheckChange">
              <template v-for="page in pages">
                <div
                  :key="'page_ck_' + page.uuid"
                  class="checkbox-row"
                  v-show="searchPage == undefined || page.name.indexOf(searchPage) != -1"
                >
                  <a-checkbox :value="page.id">
                    <div class="checkbox-row-item">
                      <div class="title" :title="page.name" style="max-width: 200px">
                        <Icon type="pticon iconfont icon-szgy-zhuye" />
                        {{ page.name }}
                      </div>
                      <div class="remark" style="max-width: 300px" :title="page.remark">
                        {{ page.remark }}
                      </div>
                    </div>
                  </a-checkbox>
                </div>
              </template>
            </a-checkbox-group>
          </PerfectScrollbar>
        </div>
        <div class="product-role-member-role-list" v-show="activeKey == 'role'">
          <div style="margin-bottom: 12px">
            <a-input-search allow-clear v-model.trim="searchRole" />
          </div>
          <div class="checkbox-row" v-if="Object.keys(vRoleMap).length">
            <a-checkbox :indeterminate="roleIndeterminate" :checked="roleCheckAll" @change="e => onCheckAllRole(e)">全选</a-checkbox>
          </div>
          <PerfectScrollbar style="height: 256px">
            <template v-for="mod in vRoleMap">
              <div class="checkbox-row" :key="mod.id">
                <a-checkbox :indeterminate="mod.indeterminate" :checked="mod.checkAll" @change="e => onCheckAllModRoleChange(e, mod)">
                  <a-icon type="folder-open" style="color: #e6a62d" theme="filled" />
                  {{ mod.title }}
                </a-checkbox>
              </div>
              <a-checkbox-group
                class="role-checkbox-group"
                :key="mod.id"
                v-model="mod.checked"
                @change="onCheckModRoleChange(mod)"
                v-show="mod.children.length"
              >
                <template v-for="role in mod.children">
                  <div
                    :key="'role_ck_' + role.uuid"
                    class="checkbox-row"
                    style="padding-left: 20px"
                    @click.stop="clickToPreviewRoleTree(role)"
                    v-show="
                      searchRole == undefined ||
                      role.name.indexOf(searchRole) != -1 ||
                      (role.remark != undefined && role.remark.indexOf(searchRole) != -1)
                    "
                  >
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
                  </div>
                </template>
              </a-checkbox-group>
            </template>
          </PerfectScrollbar>
        </div>
      </div>
    </a-col>
    <a-col :span="8" v-if="activeKey == 'role'">
      <div>
        <div class="sub-title">
          角色预览
          <label class="role-name-preview" v-if="roleNamePreview != undefined" :style="{}">- {{ roleNamePreview }}</label>
        </div>
        <div v-show="rolePrgTreeLoading" class="spin-center">
          <a-spin />
        </div>
        <PerfectScrollbar style="height: 388px">
          <DraggableTreeList
            :key="rolePrgTreeKey"
            v-model="rolePrivilegePreviewTree"
            :draggable="false"
            :dragButton="false"
            titleField="name"
            expandIcon="plus"
            defaultExpandAll
          >
            <template slot="title" slot-scope="scope">
              <Icon v-if="scope.item.type == 'PRIVILEGE'" type="pticon iconfont icon-ptkj-quanxian" />
              <Icon v-else-if="scope.item.type == 'ROLE'" type="pticon iconfont icon-ptkj-jiaose" />
              <Icon v-else-if="scope.item.type == 'appPageDefinition'" type="pticon iconfont icon-ptkj-yemian" />
              <a-icon v-else type="file" />
              <span>{{ scope.item.name }}</span>
            </template>
          </DraggableTreeList>
          <a-empty v-if="rolePrivilegePreviewTree.length == 0 && !rolePrgTreeLoading">
            <span slot="description" class="empty-description">暂无数据</span>
          </a-empty>
        </PerfectScrollbar>
      </div>
    </a-col>
    <a-col :span="8">
      <div>
        <div class="sub-title">
          已选
          <span style="float: right">
            <a-input-search v-model.trim="searchSelected" style="width: 130px" allow-clear />
          </span>
        </div>
        <PerfectScrollbar style="height: 388px">
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
</template>
<style lang="less">
.product-role-member {
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
      > .ant-checkbox-wrapper {
        width: 100%;
      }

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
import { orderBy, debounce } from 'lodash';
import DraggableTreeList from '@pageAssembly/app/web/widget/commons/draggable-tree-list';
import { compareArrayDifference } from '@framework/vue/utils/util';
import ProductPageList from './product-page-list.vue';

export default {
  name: 'ProductRoleMember',
  props: {
    role: Object,
    roleList: Array,
    modules: Array,
    versionUuid: String,
    versionId: String
  },
  components: { DraggableTreeList },
  computed: {
    checkedOptions() {
      let opt = [];
      if (this.pages != undefined) {
        for (let p of this.pages) {
          if (this.pageChecked.includes(p.id)) {
            opt.push({
              name: p.name,
              key: p.id,
              type: 'page'
            });
          }
        }
      }
      if (this.roleMap) {
        for (let k in this.roleMap) {
          let mod = this.roleMap[k];
          for (let r of mod.children) {
            if (mod.checked.includes(r.uuid)) {
              opt.push({
                name: r.name,
                key: r.uuid,
                module: mod.title,
                pid: k,
                type: 'role'
              });
            }
          }
        }
      }
      return opt;
    },
    allPageKeys() {
      let keys = [];
      if (this.pages != undefined) {
        for (let r of this.pages) {
          keys.push(r.id);
        }
      }
      return keys;
    },
    moduleMap() {
      let map = {};
      for (let m of this.modules) {
        map[m.id] = m;
      }
      return map;
    },
    vRoleMap() {
      if (this.searchRole == undefined) {
        return this.roleMap;
      }
      let map = {};
      for (let key in this.roleMap) {
        let role = this.roleMap[key].children;
        if (role.length) {
          let rs = [];
          let has = false;
          for (let i = 0, len = role.length; i < len; i++) {
            if (
              role[i].name.indexOf(this.searchRole) != -1 ||
              (role[i].remark != undefined && role[i].remark.indexOf(this.searchRole) != -1)
            ) {
              has = true;
              break;
            }
          }
          if (has) {
            map[key] = this.roleMap[key];
          }
        }
      }
      return map;
    }
  },
  data() {
    return {
      activeKey: 'index',
      searchPage: undefined,
      searchRole: undefined,
      searchSelected: undefined,
      pages: [],
      pageIndeterminate: false,
      pageChecked: [],
      pageCheckAll: false,
      roleIndeterminate: false,
      roleCheckAll: false,
      roleChecked: [],
      roleMap: {},
      currentRoleCheckedKey: undefined,
      rolePrivilegePreviewTree: [],
      rolePrgTreeKey: 'rolePrgPreviewTree_0',
      roleLoading: true,
      pageLoading: true,
      rolePrgTreeLoading: false,
      roleNamePreview: undefined,
      originalRoleKeys: [],
      originalPrivilegeKeys: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getModuleRoles();
    this.getPagePrivileges();
  },
  mounted() {},
  methods: {
    collectRoleMember() {
      // 模块角色作为内嵌角色
      let roleUuids = [];
      for (let key in this.roleMap) {
        let r = this.roleMap[key];
        roleUuids = roleUuids.concat(r.checked);
      }
      let privileges = [];
      for (let p of this.pageChecked) {
        if (this.pageAsPrg['PRIVILEGE_PAGE_' + p]) {
          privileges.push(this.pageAsPrg['PRIVILEGE_PAGE_' + p].uuid);
        }
      }

      let roleResult = compareArrayDifference(this.originalRoleKeys, roleUuids);
      let pvgResult = compareArrayDifference(this.originalPrivilegeKeys, privileges);
      return {
        nestedRoleRemoved: roleResult.from,
        nestedRoleAdded: roleResult.to,
        privilegeRemoved: pvgResult.from,
        privilegeAdded: pvgResult.to
      };
    },
    getPagePrivileges() {
      this.pageAsPrg = {};
      $axios
        .all([
          $axios.get(`/proxy/api/security/privilege/queryAppRolePrivileges`, { params: { appId: this.versionId } }),
          $axios.get(`/proxy/api/app/prod/version/pages`, { params: { prodVersionUuid: this.versionUuid } })
        ])
        .then(
          $axios.spread((res1, res2) => {
            for (let d of res1.data.data) {
              this.pageAsPrg[d.code] = d;
            }

            let pid = [],
              pmap = {};
            this.originalPrivilegeKeys.splice(0, this.originalPrivilegeKeys.length);
            if (this.role != undefined && this.role.privileges) {
              for (let p of this.role.privileges) {
                if (p.systemDef == 1 && p.code.indexOf('PRIVILEGE_PAGE_') == 0) {
                  pid.push(p.code);
                  pmap[p.code] = p;
                }
              }
            }

            // 仅展示最新版本的数据
            let list = orderBy(
              res2.data.data,
              [
                o => {
                  return parseFloat(o.version);
                }
              ],
              ['desc']
            );

            this.pages = [];
            let pageid = [];
            for (let d of list) {
              if (pageid.includes(d.id)) {
                continue;
              }
              pageid.push(d.id);
              this.pages.push(d);
              if (pid.includes('PRIVILEGE_PAGE_' + d.id)) {
                this.pageChecked.push(d.id);
                this.originalPrivilegeKeys.push(pmap['PRIVILEGE_PAGE_' + d.id].uuid);
              }
              if (this.pageAsPrg['PRIVILEGE_PAGE_' + d.id] == undefined) {
                // 数据修正作用：正常逻辑是创建首页时候会默认创建相关角色、权限
                // 创建首页访问权限
                ProductPageList.methods.createDefaultPagePrivilege(d.id, this.versionId).then(({ data }) => {
                  this.pageAsPrg['PRIVILEGE_PAGE_' + d.id] = { uuid: data.data };
                });
              }
            }
            this.pageCheckAll = this.pageChecked.length > 0 && this.pageChecked.length == this.pages.length;
            this.pageIndeterminate = this.pageChecked.length > 0 && this.pageChecked.length < this.pages.length;
            this.pageLoading = false;
          })
        )
        .catch(error => {});
    },

    createDefaultPagePrivilege(pageId, appId) {
      return new Promise((resolve, reject) => {
        $axios
          .post('/proxy/api/security/privilege/savePrivilegeResource', {
            appId,
            name: '页面访问权限',
            code: 'PRIVILEGE_PAGE_' + pageId,
            enabled: true,
            systemDef: 1,
            otherResources: [{ type: 'appPageDefinition', resourceUuid: pageId }]
          })
          .then(({ data }) => {
            $axios.post(`/proxy/api/security/role/updateRoleMember`, [
              {
                role: {
                  id: 'ROLE_VIEW_PAGE_' + pageId,
                  code: 'ROLE_VIEW_PAGE_' + pageId,
                  name: '页面访问角色',
                  appId
                },
                privilegeAdded: [data.data]
              }
            ]);
            resolve(data.data);
          });
      });
    },
    getModuleRoles() {
      let mid = [],
        map = {};
      for (let i of this.modules) {
        mid.push(i.moduleId);
        map[i.moduleId] = i;
      }
      if (mid.length) {
        $axios.post('/proxy/api/security/role/queryAppRolesByAppIds', mid).then(({ data }) => {
          this.originalRoleKeys.splice(0, this.originalRoleKeys.length);
          if (data.data) {
            let pid = [];
            if (this.role != undefined && this.role.nestedRoles) {
              for (let p of this.role.nestedRoles) {
                pid.push(p.uuid);
              }
            }

            // 当前产品版本下的角色
            if (this.roleList && this.roleList.length > 0) {
              this.$set(this.roleMap, this.versionId, {
                indeterminate: false,
                checkAll: false,
                checked: [],
                title: '当前版本',
                children: []
              });
              this.roleList.forEach(r => {
                if (r.uuid == this.role.uuid) {
                  return true;
                }
                if (pid.includes(r.uuid)) {
                  this.roleMap[r.appId].checked.push(r.uuid);
                  this.originalRoleKeys.push(r.uuid);
                }
                this.roleMap[r.appId].children.push(r);
              });
            }

            for (let r of data.data) {
              if (this.roleMap[r.appId] == undefined) {
                this.$set(this.roleMap, r.appId, {
                  indeterminate: false,
                  checkAll: false,
                  checked: [],
                  title: map[r.appId].name,
                  children: []
                });
              }
              if (pid.includes(r.uuid)) {
                this.roleMap[r.appId].checked.push(r.uuid);
                this.originalRoleKeys.push(r.uuid);
              }
              this.roleMap[r.appId].children.push(r);
            }

            for (let key in this.roleMap) {
              this.roleMap[key].checkAll =
                this.roleMap[key].children.length > 0 && this.roleMap[key].children.length == this.roleMap[key].checked.length;
              this.roleMap[key].indeterminate =
                this.roleMap[key].checked.length > 0 && this.roleMap[key].children.length > this.roleMap[key].checked.length;
            }

            this.roleLoading = false;
          }
        });
      }
    },
    onCheckAllModRoleChange(e, item) {
      item.checkAll = e.target.checked;
      item.indeterminate = false;
      item.checked = [];
      if (item.checkAll) {
        for (let c of item.children) {
          item.checked.push(c.uuid);
        }
      }
      this.changeModCheckState();
    },
    fetchRolePrgTreeData: debounce(function ({ uuid, name }) {
      this.rolePrgTreeLoading = true;
      this.roleNamePreview = name;
      $axios
        .get(`/proxy/api/security/role/getRolePrivilegeResourceTreeByUuid/${uuid}`, { params: {} })
        .then(({ data }) => {
          this.rolePrgTreeLoading = false;
          if (data.code == 0 && data.data) {
            for (let i = 0; i < data.data.length; i++) {
              if (data.data[i].data.systemDef == 1) {
                data.data.splice(i--, 1);
              }
            }
            this.rolePrivilegePreviewTree = data.data;
            this.rolePrgTreeKey = 'rolePrgPreviewTree_' + new Date().getTime();
          }
        })
        .catch(error => {});
    }, 500),
    clickToPreviewRoleTree(role) {
      this.currentRoleCheckedKey = role.uuid;
      this.fetchRolePrgTreeData(role);
    },
    changeModCheckState(item) {
      if (item) {
        item.indeterminate = !!item.checked.length && item.checked.length < item.children.length;
        item.checkAll = item.checked.length === item.children.length;
      }

      let checkAllCnt = 0,
        checkedCnt = 0;
      for (let key in this.roleMap) {
        let r = this.roleMap[key];
        if (r.indeterminate) {
          checkedCnt++;
        }
        if (r.checkAll) {
          checkAllCnt++;
          checkedCnt++;
        }
      }
      this.roleCheckAll = checkAllCnt > 0 && checkAllCnt == Object.keys(this.roleMap).length;
      this.roleIndeterminate = this.roleCheckAll ? false : checkedCnt > 0;
    },

    onCheckModRoleChange(item) {
      this.changeModCheckState(item);
      this.currentRoleCheckedKey = item.uuid;
    },
    onCheckAllPage(e) {
      this.pageCheckAll = e.target.checked;
      this.pageIndeterminate = false;
      this.pageChecked = e.target.checked ? this.allPageKeys : [];
    },
    onPageCheckChange() {
      this.pageIndeterminate = this.pageChecked.length > 0 && this.pageChecked.length < this.allPageKeys.length;
      this.pageCheckAll = this.pageChecked.length == this.allPageKeys.length;
    },
    onCheckAllRole(e) {
      this.roleCheckAll = e.target.checked;
      for (let key in this.roleMap) {
        let mod = this.roleMap[key];
        mod.checked = [];
        mod.checkAll = false;
        mod.indeterminate = false;
        if (this.roleCheckAll) {
          mod.checkAll = true;
          mod.indeterminate = false;
          for (let role of mod.children) {
            mod.checked.push(role.uuid);
          }
        }
      }
      this.changeModCheckState();
    },

    removeChecked(item) {
      if (item.type == 'page') {
        let idx = this.pageChecked.indexOf(item.key);
        if (idx != -1) {
          this.pageChecked.splice(idx, 1);
        }
        this.onPageCheckChange();
      } else if (item.type == 'role') {
        let mod = this.roleMap[item.pid],
          checked = mod.checked;
        let idx = checked.indexOf(item.key);
        if (idx != -1) {
          checked.splice(idx, 1);
          mod.indeterminate = !!mod.checked.length && mod.checked.length < mod.children.length;
          mod.checkAll = mod.checked.length === mod.children.length;
          this.changeModCheckState(mod);
        }
      }
    }
  }
};
</script>
