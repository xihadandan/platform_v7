<template>
  <Modal :ok="onSave" :width="900" :height="600" okText="保存" @show="onShow" :cancel="onCancel" :destroyOnClose="true">
    <span slot="title">
      设置权限
      <span style="color: var(--w-text-color-light); font-size: var(--w-font-size-sm); font-weight: normal">
        | 选择权限可访问的页面内容
      </span>
    </span>
    <span>
      <slot></slot>
    </span>
    <template slot="content">
      <a-tabs default-active-key="1" class="module-privilege-resource-set pt-tabs">
        <a-tab-pane key="1" tab="功能权限" style="padding: 0">
          <a-layout style="background-color: #fff">
            <a-layout-sider theme="light" width="224px" style="padding: var(--w-padding-md) var(--w-padding-xs)">
              <PerfectScrollbar style="height: 400px; padding-right: var(--w-padding-xs); margin-right: calc(0px - var(--w-padding-xs))">
                <ResourceSider
                  ref="resSider"
                  :resources="resources"
                  @select="onSelectResource"
                  :filterResType="['page']"
                  @checkChange="onResCheckChange"
                  :loading="rsLoading"
                />
              </PerfectScrollbar>
            </a-layout-sider>
            <a-layout-content>
              <PerfectScrollbar class="layout-content-scroll">
                <template v-if="selectedFuncKey != undefined">
                  <a-skeleton active :loading="functionEleLoading">
                    <template v-if="functionDataset[selectedFuncKey] && functionDataset[selectedFuncKey].length > 0">
                      <template v-for="(ele, i) in functionDataset[selectedFuncKey]">
                        <!-- 展示组件定义类资源 -->
                        <template v-if="ele.type == 'appWidgetDefinition'">
                          <a-row type="flex" :data-id="ele.id" v-if="ele.isProtected || ele.children.length > 0" :key="'functionEle_' + i">
                            <a-col flex="100%">
                              <a-row
                                class="function-row-item"
                                type="flex"
                                style="background-color: var(--w-fill-color-light); border-bottom: 0"
                              >
                                <a-col flex="auto">{{ ele.title }}</a-col>
                                <a-col flex="40px">
                                  <a-switch
                                    v-if="ele.isProtected"
                                    :checked="pageResourceCheckedKeys[selectedFuncKey].includes(selectedFuncKey + ':' + ele.id)"
                                    @change="e => onChangeSwitch(e, ele.id)"
                                  />
                                </a-col>
                              </a-row>
                            </a-col>
                            <a-col flex="100%" v-if="ele.children && ele.children.length > 0">
                              <a-row
                                class="function-row-item"
                                type="flex"
                                v-for="(child, c) in ele.children"
                                :key="'functionEleChild_' + c"
                                :data-id="child.id"
                              >
                                <a-col flex="auto" style="padding-left: 20px">{{ child.title }}</a-col>
                                <a-col flex="40px">
                                  <a-switch
                                    :checked="pageResourceCheckedKeys[selectedFuncKey].includes(selectedFuncKey + ':' + child.id)"
                                    @change="e => onChangeSwitch(e, child.id)"
                                  />
                                </a-col>
                              </a-row>
                            </a-col>
                          </a-row>
                        </template>

                        <!-- 展示组件上的功能类资源 -->
                        <a-row class="function-row-item" type="flex" v-else>
                          <a-col flex="auto">{{ ele.title }}</a-col>
                          <a-col flex="40px">
                            <a-switch v-model="ele.checked" />
                          </a-col>
                        </a-row>
                      </template>
                    </template>
                    <a-empty v-else />
                  </a-skeleton>
                </template>
                <template v-else>
                  <a-result title="请选择页面"></a-result>
                </template>
              </PerfectScrollbar>
            </a-layout-content>
          </a-layout>
        </a-tab-pane>
        <!-- <a-tab-pane key="2" tab="数据权限"></a-tab-pane> -->
        <a-tab-pane key="3" tab="权限分配" style="padding: 20px">
          <a-alert message="为模块创建角色，用于设置一类用户的访问权限。" type="info" show-icon closable style="margin-bottom: 12px" />
          <div>
            <a-row class="function-row-item" type="flex" style="background-color: var(--w-fill-color-light); border-bottom: 0">
              <a-col flex="auto">当前权限已分配给以下角色:</a-col>
            </a-row>
            <a-spin v-if="loadingRoles" size="small" />
            <PerfectScrollbar v-else style="height: 258px; margin-bottom: 12px">
              <a-row v-for="(role, i) in roles" :key="'current_privilege_role_' + role.uuid" class="function-row-item" type="flex">
                <a-col flex="auto">
                  <span
                    style="
                      padding: 1px 3px;
                      margin-right: var(--w-margin-2xs);
                      background-color: var(--w-primary-color);
                      border-radius: 4px;
                    "
                  >
                    <Icon
                      slot="icon"
                      type="pticon iconfont icon-ptkj-jiaose"
                      style="font-size: var(--w-font-size-base); color: #fff"
                    ></Icon>
                  </span>
                  {{ role.name }}
                </a-col>
                <a-col flex="45px">
                  <a-button type="icon" size="small" title="删除" @click="onRemovePrgRole(i)">
                    <Icon type="pticon iconfont icon-ptkj-dacha"></Icon>
                  </a-button>
                </a-col>
              </a-row>
            </PerfectScrollbar>
          </div>

          <Modal title="选择角色" :ok="selectRoleOk" :cancel="cancelSelectRole" :okText="selectRoleOkText" style="display: block">
            <a-button block size="large">
              <Icon type="pticon iconfont icon-ptkj-jiahao" />
              添加角色
            </a-button>
            <template slot="content">
              <template v-if="moduleRolesSelectable.length">
                <a-checkable-tag
                  v-for="role in moduleRolesSelectable"
                  :key="'waitCk_' + role.uuid"
                  style="margin-bottom: 12px; margin-right: 12px; --w-tag-text-size: var(--w-font-size-base); --w-tag-border-radius: 4px"
                  :checked="selectRoleUuids.indexOf(role.uuid) > -1"
                  @change="checked => onSelectRole(checked, role)"
                >
                  {{ role.name }}
                </a-checkable-tag>
              </template>
              <a-empty v-else></a-empty>
            </template>
          </Modal>
        </a-tab-pane>
      </a-tabs>
    </template>
  </Modal>
</template>
<style lang="less">
.module-privilege-resource-set {
  border: 1px solid var(--w-border-color-light);
  border-radius: 4px;
  .function-row-item {
    line-height: 32px;
    border-bottom: 1px solid var(--w-border-color-light);
    padding: var(--w-padding-3xs) var(--w-padding-xs);
    color: var(--w-text-color-darker);
    font-size: var(--w-font-size-base);
    &:hover {
      cursor: pointer;
      background-color: var(--w-primary-color-1);
    }
  }
  .ant-layout-content {
    border-left: 1px solid var(--w-border-color-light);
  }
  .layout-content-scroll {
    height: 440px;
    padding: var(--w-padding-md);
  }
}
</style>
<script type="text/babel">
import ResourceSider from './resource-sider.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import { compareArrayDifference } from '@framework/vue/utils/util';

export default {
  name: 'PrivilegeResourceSet',
  inject: ['resources', 'moduleRoles', 'currentModule', 'getModulePage'],
  props: {
    privilege: Object
  },
  components: {
    ResourceSider,
    Modal,
    Drawer
  },
  computed: {
    moduleRolesSelectable() {
      let r = [];
      for (let mr of this.moduleRoles) {
        if (!this.roleUuids.includes(mr.uuid)) {
          r.push(mr);
        }
      }
      return r;
    },
    selectRoleOkText() {
      return `确定 (${this.selectRoleUuids.length})`;
    },
    roleUuids() {
      let uuids = [];
      for (let r of this.roles) {
        uuids.push(r.uuid);
      }
      return uuids;
    },
    privilegeUuid() {
      return this.privilege.uuid;
    }
  },
  data() {
    return {
      roles: [],
      selectRoles: [],
      selectRoleUuids: [],
      functionDataset: {},
      pageResourceItems: {},
      selectedFuncKey: undefined,
      functionEleLoading: false,
      checkedResourceKeys: [],
      pageResourceCheckedKeys: {},
      rsLoading: true,
      loadingRoles: true
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onCancel() {
      this.roles.splice(0, this.roles.length);
      this.selectedFuncKey = undefined;
    },
    onShow() {
      this.queryPrivilegeRoles(this.privilege.uuid).then(d => {
        d.roles.forEach(r => {
          // 只筛选出属于该模块的角色
          if (r.appId == this.currentModule.id) {
            this.roles.push(r);
          }
        });
        this.originalRoles = JSON.parse(JSON.stringify(this.roles));
      });
      this.loadAllPageResources();
    },
    queryPrivilegeRoles(uuid) {
      return new Promise((resolve, reject) => {
        this.loadingRoles = true;
        $axios
          .get(`/proxy/api/security/privilege/getPrivilegeWithRoleDetails`, { params: { uuid } })
          .then(({ data }) => {
            this.loadingRoles = false;
            resolve(data.data);
          })
          .catch(error => {
            this.loadingRoles = false;
          });
      });
    },
    selectRoleOk(e) {
      this.roles.push(...this.selectRoles);
      this.cancelSelectRole();
      e(true);
    },
    cancelSelectRole() {
      this.selectRoleUuids = [];
      this.selectRoles = [];
    },
    onSelectRole(ck, role) {
      let idx = this.selectRoleUuids.indexOf(role.uuid);

      if (ck) {
        if (idx == -1) {
          this.selectRoleUuids.push(role.uuid);
          this.selectRoles.push(role);
        }
      } else {
        if (idx != -1) {
          this.selectRoleUuids.splice(idx, 1);
          this.selectRoles.splice(idx, 1);
        }
      }
    },
    onRemovePrgRole(i) {
      this.roles.splice(i, 1);
    },
    loadAllPageResources() {
      let pageUuids = [],
        pageIds = [],
        uuidIdMap = {};
      let mPage = this.getModulePage();
      this.resourceKeyItemMap = {};
      if (mPage) {
        pageUuids.push(mPage.uuid);
        pageIds.push(mPage.id);
        uuidIdMap[mPage.uuid] = mPage.id;
        this.$set(this.pageResourceCheckedKeys, mPage.id, []);
        this.resourceKeyItemMap[mPage.uuid] = mPage;
        this.resourceKeyItemMap[mPage.id] = mPage;
      }
      for (let r of this.resources) {
        if (r._RES_TYPE == 'page') {
          pageUuids.push(r.uuid);
          pageIds.push(r.id);
          uuidIdMap[r.uuid] = r.id;
          this.resourceKeyItemMap[r.uuid] = r;
          this.resourceKeyItemMap[r.id] = r;
          this.$set(this.pageResourceCheckedKeys, r.id, []);
        }

        if (r.children != undefined) {
          for (let c of r.children) {
            if (c._RES_TYPE == 'page') {
              this.$set(this.pageResourceCheckedKeys, c.id, []);
              pageUuids.push(c.uuid);
              pageIds.push(c.id);
              uuidIdMap[c.uuid] = c.id;
              this.resourceKeyItemMap[c.uuid] = c;
              this.resourceKeyItemMap[c.id] = c;
            }
          }
        }
      }
      // 加载所有页面上的资源
      this.fetchAllPageResources(pageUuids, pageIds, uuidIdMap, () => {
        this.rsLoading = false;
      });
    },
    fetchAllPageResources(uuids, ids, uuidIdMap, callback) {
      $axios
        .all([
          $axios.post('/proxy/api/webapp/page/definition/getAppPageResources', uuids),
          $axios.get(`/proxy/api/security/privilege/getPrivilegeOtherResource/${this.privilegeUuid}`, {}),
          $axios.get(`/proxy/api/security/privilege/getPrivilegeResource/${this.privilegeUuid}`, {})
        ])
        .then(
          $axios.spread((pageResResponse, prgOtherResResponse, prgResResponse) => {
            let prgOtherRes = prgOtherResResponse.data.data,
              prgRes = prgResResponse.data.data,
              defaultCheckKeys = [];
            this.originalResourceKeys = [];
            this.checkedResourceKeys = [];
            if (prgOtherRes != undefined) {
              // 页面资源选中
              for (let p of prgOtherRes) {
                defaultCheckKeys.push(p.resourceUuid);
                if (ids.includes(p.resourceUuid)) {
                  // 判断页面是否加入权限资源
                  this.checkedResourceKeys.push(p.resourceUuid);
                  this.originalResourceKeys.push(p.resourceUuid);
                }
              }
            }
            if (prgRes != undefined) {
              // 链接资源
              for (let p of prgRes) {
                // 只显示属于当前模块的资源
                if (p.moduleId == this.currentModule.id) {
                  this.checkedResourceKeys.push(p.uuid);
                  this.originalResourceKeys.push(p.uuid);
                }
              }
            }

            // 更新选中的页面资源
            this.$refs.resSider.setCheckedKeys(this.checkedResourceKeys);

            let res = pageResResponse.data.data;
            if (res != undefined) {
              for (let r of res) {
                // 使用 [ 页面ID:资源ID(对应前端的组件(元素)ID) ] 作为与权限关联的标识:
                // 1. 页面升级时候，已关联权限的资源不需要重新配置权限
                //  （ 页面升级版本只是页面设计的改变，已有权限的业务功能元素是不变的 ）
                // 2. 前端判断资源是否有权限时候可以直接使用[页面ID:组件元素的ID]进行判断
                //  ( 注意：前端复制复制页面时候，会重新生成页面ID，但是组件ID不会重新生成，也不会影响这边的权限管理 )
                if (r.isProtected && defaultCheckKeys.includes(uuidIdMap[r.appPageUuid] + ':' + r.id)) {
                  this.originalResourceKeys.push(uuidIdMap[r.appPageUuid] + ':' + r.id);
                  this.pageResourceCheckedKeys[uuidIdMap[r.appPageUuid]].push(uuidIdMap[r.appPageUuid] + ':' + r.id);
                  this.resourceKeyItemMap[uuidIdMap[r.appPageUuid] + ':' + r.id] = r;
                }
              }
            }

            callback.call(this);
          })
        );
    },
    onResCheckChange(keys) {
      this.checkedResourceKeys = [...keys];
    },
    onSelectResource(data) {
      this.selectedFuncKey = data._RES_TYPE == 'page' ? data.id : data.uuid;
      if (this.functionDataset[this.selectedFuncKey] == undefined) {
        this.$set(this.functionDataset, this.selectedFuncKey, []);
        this.functionEleLoading = true;
        this.getPageResources(data.uuid, list => {
          // 构建功能数据集
          let wgtFuncMap = {},
            elements = [];
          this.pageResourceItems[this.selectedFuncKey] = list;
          for (let res of list) {
            // 组件元素功能:
            if (res.appFunction && res.appFunction.type == 'AppWidgetFunctionElement' && res.isProtected) {
              res.appFunction.definitionJson = JSON.parse(res.appFunction.definitionJson);
              let wid = res.appFunction.definitionJson.widgetId || '-1';
              if (wgtFuncMap[wid] == undefined) {
                wgtFuncMap[wid] = [];
              }
              wgtFuncMap[wid].push({
                resourceUuid: res.uuid,
                functionUuid: res.appFunction.uuid,
                id: res.id,
                checked: false,
                title: res.appFunction.definitionJson.title
              });
              this.resourceKeyItemMap[this.selectedFuncKey + ':' + res.id] = { name: res.appFunction.definitionJson.title };
            }
          }

          for (let res of list) {
            if (res.appFunction && res.appFunction.type == 'appWidgetDefinition') {
              res.appFunction.definitionJson = JSON.parse(res.appFunction.definitionJson);
              // 组件定义功能
              if (res.isProtected || wgtFuncMap[res.id]) {
                elements.push({
                  resourceUuid: res.uuid,
                  functionUuid: res.appFunction.uuid,
                  id: res.id,
                  title: res.appFunction.definitionJson.title,
                  isProtected: res.isProtected,
                  type: 'appWidgetDefinition',
                  checked: false,
                  children: wgtFuncMap[res.id]
                });
              }
            }
          }

          this.functionDataset[this.selectedFuncKey].push(...elements);
          this.functionEleLoading = false;
        });
      } else if (this.pageResourceItems[this.selectedFuncKey]) {
        for (let res of this.pageResourceItems[this.selectedFuncKey]) {
          if (res.appFunction && res.appFunction.type == 'AppWidgetFunctionElement' && res.isProtected) {
            this.resourceKeyItemMap[this.selectedFuncKey + ':' + res.id] = { name: res.appFunction.definitionJson.title };
          }
        }
      }
    },
    getPageResources(uuid, callback) {
      $axios.get(`/proxy/api/webapp/page/definition/getAppPageResourceFunction/${uuid}`, {}).then(({ data }) => {
        if (data.code == 0) {
          if (typeof callback == 'function' && data.data) {
            callback.call(this, data.data);
          }
        }
      });
    },

    notifyMsg(type, message) {
      this.$notification[type]({
        duration: 2,
        message
      });
    },
    onChangeSwitch(ck, id) {
      let key = this.selectedFuncKey + ':' + id;
      let keys = this.pageResourceCheckedKeys[this.selectedFuncKey];
      if (keys != undefined) {
        let idx = keys.indexOf(key);
        if (ck && idx == -1) {
          keys.push(key);
        } else if (!ck && idx != -1) {
          keys.splice(idx, 1);
        }
      }
    },

    updatePrivilege() {
      let formData = {
        privilege: {
          uuid: this.privilege.uuid
        },
        privilegeResourceAdded: [],
        privilegeResourceDeleted: [],
        resourceAdded: [],
        resourceDeleted: [],
        roleAdded: [],
        roleDeleted: []
      };

      let result = compareArrayDifference(
        this.originalRoles,
        this.roles,
        (list, item) => {
          for (let i = 0, len = list.length; i < len; i++) {
            if (list[i].uuid == item.uuid) {
              return true;
            }
          }
          return false;
        },
        item => {
          return item.uuid;
        }
      );
      formData.roleAdded = result.to;
      formData.roleDeleted = result.from;
      let resourceKeys = [...this.checkedResourceKeys];
      for (let id in this.pageResourceCheckedKeys) {
        resourceKeys.push(...this.pageResourceCheckedKeys[id]);
      }
      result = compareArrayDifference(this.originalResourceKeys, resourceKeys);
      let pageIds = Object.keys(this.pageResourceCheckedKeys);
      for (let key in result) {
        let list = result[key];
        list.forEach(item => {
          if (pageIds.includes(item)) {
            // 页面ID资源
            formData[key == 'from' ? 'privilegeResourceDeleted' : 'privilegeResourceAdded'].push({
              privilegeUuid: this.privilege.uuid,
              type: 'appPageDefinition',
              resourceUuid: item,
              resourceName: this.resourceKeyItemMap[item].name
            });
          } else if (item.indexOf(':') != -1) {
            // 页面下的组织元素资源
            formData[key == 'from' ? 'privilegeResourceDeleted' : 'privilegeResourceAdded'].push({
              privilegeUuid: this.privilege.uuid,
              type: 'AppWidgetFunctionElement',
              resourceUuid: item,
              resourceName: this.resourceKeyItemMap[item].name
            });
          } else {
            // 链接资源
            formData[key == 'from' ? 'resourceDeleted' : 'resourceAdded'].push(item);
          }
        });
      }
      console.log('增量更新权限资源数据: ', formData);

      if (
        formData.privilegeResourceAdded.length ||
        formData.privilegeResourceDeleted.length ||
        formData.resourceAdded.length ||
        formData.resourceDeleted.length ||
        formData.roleAdded.length ||
        formData.roleDeleted.length
      ) {
        this.$loading();
        $axios
          .post(`/proxy/api/security/privilege/updatePrivilegeRoleResource`, [formData])
          .then(({ data }) => {
            this.$loading(false);
            if (data.data) {
              this.$message.success('保存成功');
              $axios.get(`/proxy/api/security/privilege/publishPrivilegeUpdatedEvent/${this.privilege.uuid}`);
              this.originalRoles = JSON.parse(JSON.stringify(this.roles));
              this.originalResourceKeys = JSON.parse(JSON.stringify(resourceKeys));
              if (formData.roleAdded.length || formData.roleDeleted.length) {
                this.$emit('privilegeRoleChange');
              }
            }
          })
          .catch(error => {
            this.$loading(false);
          });
      } else {
        this.$message.info('无变更内容');
      }
    },
    onSave(e) {
      this.updatePrivilege();
    }
  }
};
</script>
