<template>
  <a-spin :spinning="loading">
    <a-form-model ref="form" :model="formData" :rules="rules" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }" class="pt-form">
      <a-tabs default-active-key="1" v-model="activeKey" class="pt-tabs">
        <a-tab-pane key="1" tab="基本信息">
          <div class="flex">
            <a-form-model-item label="名称" prop="name" style="width: 50%">
              <a-input v-model="formData.name" />
            </a-form-model-item>
            <a-form-model-item label="ID" prop="id" style="width: 50%">
              <a-input v-model="formData.id" :disabled="!!formData.uuid" />
            </a-form-model-item>
          </div>
          <div class="flex">
            <a-form-model-item label="编号" prop="code" style="width: 50%">
              <a-input v-model="formData.code" />
            </a-form-model-item>
            <a-form-model-item label="归属" prop="appId" style="width: 50%">
              <!-- 角色不允许修改归属 -->
              <a-select
                :showSearch="true"
                v-model="formData.appId"
                :options="moduleOptions"
                :filter-option="filterOption"
                :style="{ width: '100%' }"
                disabled
              ></a-select>
            </a-form-model-item>
          </div>
          <a-form-model-item label="角色成员" prop="memberIds" :label-col="{ span: 3 }" :wrapper-col="{ span: 21 }">
            <OrgSelect v-model="formData.memberIds" ref="orgSelect" :orgType="['MyOrg', 'PublicGroup']" />
          </a-form-model-item>
          <a-form-model-item label="备注" prop="remark" :label-col="{ span: 3 }" :wrapper-col="{ span: 21 }">
            <a-textarea v-model="formData.remark" />
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="2" tab="角色权限">
          <div class="content-box flex">
            <div style="border-right: 1px solid var(--w-border-color-light)">
              <div class="flex f_x_s" style="margin-bottom: 12px">
                <div class="content-title">选择角色/权限</div>
                <a-input-search v-model="searchValue" @search="onSearchRoleOrPrivilege" allowClear style="width: 160px" />
              </div>
              <PerfectScrollbar style="height: calc(100vh - 204px); margin-right: -20px; padding-right: 12px">
                <a-tree
                  v-model="roleCheckedKeys"
                  :tree-data="[vRoleTreeData]"
                  :replaceFields="{ title: 'name', key: 'uuid', value: 'uuid' }"
                  :expandedKeys="roleExpandedKeys"
                  :autoExpandParent="autoExpandParent"
                  @expand="onRoleExpand"
                  checkable
                  class="ant-tree-directory"
                >
                  <template slot="title" slot-scope="{ name, icon }">
                    <div class="title" :title="name">
                      <Icon v-if="icon" :type="icon"></Icon>
                      <span v-if="searchValue && name.indexOf(searchValue) > -1">
                        {{ name.substr(0, name.indexOf(searchValue)) }}
                        <span style="color: #f50">{{ searchValue }}</span>
                        {{ name.substr(name.indexOf(searchValue) + searchValue.length) }}
                      </span>
                      <span v-else>{{ name }}</span>
                    </div>
                  </template>
                </a-tree>
                <a-tree
                  v-model="privilegeCheckedKeys"
                  :tree-data="[privilegeTreeData]"
                  :replaceFields="{ title: 'name', key: 'uuid', value: 'uuid' }"
                  :expandedKeys="privilegeExpandKeys"
                  :autoExpandParent="autoExpandParent"
                  @expand="onPrivilegeExpand"
                  class="ant-tree-directory"
                  checkable
                >
                  <template slot="title" slot-scope="{ name, icon }">
                    <div class="title" :title="name">
                      <Icon v-if="icon" :type="icon"></Icon>
                      <span v-if="searchValue && name.indexOf(searchValue) > -1">
                        {{ name.substr(0, name.indexOf(searchValue)) }}
                        <span style="color: #f50">{{ searchValue }}</span>
                        {{ name.substr(name.indexOf(searchValue) + searchValue.length) }}
                      </span>
                      <span v-else>{{ name }}</span>
                    </div>
                  </template>
                </a-tree>
              </PerfectScrollbar>
            </div>
            <div>
              <div style="margin-bottom: 12px">
                <div class="content-title">已选角色/权限</div>
              </div>
              <PerfectScrollbar
                style="height: calc(100vh - 204px); margin-right: -20px; padding-right: 12px"
                class="selected-role-privileges"
              >
                <a-list :data-source="selectedRoleAndPrivileges">
                  <a-list-item slot="renderItem" slot-scope="item, index">
                    <Icon v-if="item.icon" :type="item.icon"></Icon>
                    <span style="width: calc(100% - 40px); padding: 0 8px" class="w-ellipsis-1">{{ item.name }}</span>
                    <Icon class="close" type="close" @click="removeSelectedRoleOrPrivilege(item.uuid)"></Icon>
                  </a-list-item>
                </a-list>
              </PerfectScrollbar>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="3" tab="权限结果">
          <div class="content-box">
            <div style="width: 100%">
              <PerfectScrollbar style="max-height: calc(100vh - 60px); margin-right: -20px; padding-right: 12px">
                <a-tree
                  :tree-data="resultTreeData"
                  :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
                  :defaultExpandedKeys="['-1']"
                  class="ant-tree-directory"
                >
                  <template slot="title" slot-scope="{ name, icon }">
                    <div class="title" :title="name">
                      <Icon v-if="icon" :type="icon"></Icon>
                      <span>{{ name }}</span>
                    </div>
                  </template>
                </a-tree>
              </PerfectScrollbar>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="4" tab="分配信息">
          <a-table
            rowKey="id"
            :pagination="false"
            :columns="memberInfoTableColumn"
            :locale="locale"
            :data-source="memberInfos"
            :class="['widget-table-member-info-table pt-table']"
          >
            <template slot="typeSlot" slot-scope="text, record">
              <template v-if="text == 'user'">用户</template>
              <template v-else>
                {{ orgElementModel[text] != undefined ? orgElementModel[text].name : '其他' }}
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
      <div style="margin-top: 12px; text-align: center" v-if="!$widgetDrawerContext">
        <a-button type="primary" @click="save">保存</a-button>
      </div>
    </a-form-model>
  </a-spin>
</template>

<script>
import moment from 'moment';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { deepClone, compareArrayDifference } from '@framework/vue/utils/util';

const orgUserTypes = {
  U: '人员',
  B: '单位',
  S: '单位',
  D: '部门',
  J: '职位',
  G: '群组',
  O: '组织节点',
  DU: '职务',
  EE: '自定义'
};

const initFormData = () => {
  return {
    uuid: undefined,
    id: undefined,
    appId: undefined,
    remark: undefined,
    name: undefined,
    code: undefined,
    memberIds: []
  };
};

export default {
  components: { OrgSelect },
  inject: ['pageContext'],
  data() {
    return {
      activeKey: '1',
      formData: initFormData(),
      rules: {
        name: { required: true, message: '不能为空', trigger: 'blur' },
        id: { required: true, message: '不能为空', trigger: 'blur' },
        code: { required: true, message: '不能为空', trigger: 'blur' }
      },
      moduleOptions: [],
      roleTreeData: { name: '角色', id: '-1', icon: 'iconfont icon-ptkj-jiaose', checkable: false, children: [] },
      roleCheckedKeys: [],
      privilegeTreeData: { name: '权限', id: '-1', icon: 'iconfont icon-ptkj-quanxian', checkable: false, children: [] },
      privilegeCheckedKeys: [],
      searchValue: '',
      roleLoading: false,
      privilegeLoading: false,
      detailLoading: false,
      roleExpandedKeys: [],
      privilegeExpandKeys: [],
      originalRoleKeys: [],
      originalPrivilegeKeys: [],
      originalMemberIds: [],
      autoExpandParent: false,
      resultTreeData: [],
      memberInfos: [],
      memberInfoTableColumn: [
        { title: '名称', dataIndex: 'name', width: '70%' },
        { title: '类型', dataIndex: 'type', scopedSlots: { customRender: 'typeSlot' } }
      ],
      locale: {
        emptyText: <span>暂无数据</span>
      },
      orgElementModel: {},
      $widgetDrawerContext: null
    };
  },
  computed: {
    loading() {
      return this.roleLoading || this.privilegeLoading || this.detailLoading;
    },
    selectedRoleAndPrivileges() {
      return [
        ...this.roleTreeData.children.filter(item => this.roleCheckedKeys.includes(item.uuid)),
        ...this.privilegeTreeData.children.filter(item => this.privilegeCheckedKeys.includes(item.uuid))
      ];
    },
    // memberInfos() {
    //   // let memberInfos = [];
    //   // let memberIds = this.formData.memberIds;
    //   // let memberNames = this.formData.memberNames;
    //   // if (memberIds && memberNames) {
    //   //   let ids = memberIds.split(';');
    //   //   let names = memberNames.split(';');
    //   //   let rows = [];
    //   //   for (let i = 0, len = ids.length; i < len; i++) {
    //   //     rows.push({
    //   //       id: ids[i],
    //   //       name: names[i],
    //   //       type: orgUserTypes[ids[i].substr(0, 1)] || '未定义'
    //   //     });
    //   //   }
    //   //   memberInfos = rows;
    //   // }
    //   return this.$refs.orgSelect.valueNodes;
    // },
    vRoleTreeData() {
      let children = this.roleTreeData.children;
      let tree = { name: '角色', uuid: '-1', icon: 'iconfont icon-ptkj-jiaose', checkable: false, children: [] };
      for (let i = 0, len = children.length; i < len; i++) {
        if (this.formData.uuid == children[i].uuid) {
          // 排除当前角色
          continue;
        }
        tree.children.push(children[i]);
      }
      return tree;
    }
  },
  created() {
    this.loadModuleOptions();
    // this.fetchSystemRoles();
    // this.fetchSystemPrivileges();
    this.fetchOrgElementModels();

    let $event = this._provided && this._provided.$event;
    this.$widgetDrawerContext = this._provided && this._provided.widgetDrawerContext;
    if (this.$widgetDrawerContext) {
      if ($event && $event.meta) {
        if ($event.meta.uuid) {
          // 编辑状态不显示“保存并添加下一个”
          let buttons = [];
          this.$widgetDrawerContext.widget.configuration.footerButton.buttons.map(item => {
            let button = JSON.parse(JSON.stringify(item));
            if (button.code === 'saveAndNewNextData') {
              button.defaultVisible = false;
            }
            buttons.push(button);
          });
          this.$widgetDrawerContext.setFooterButton(buttons);
        }
      }
    }
  },
  mounted() {
    this.pageContext.handleEvent('role:deleted', () => this.clear());
    let $event = this._provided.$event;
    this.$widgetDrawerContext = this._provided && this._provided.widgetDrawerContext;
    if ($event && $event.meta) {
      if ($event.meta.uuid) {
        this.viewDetails($event);
      } else {
        this.add($event);
      }
    }
  },
  methods: {
    initFormData,
    // 保存并添加下一个
    saveAndNewNextData() {
      this.save(() => {
        this.formData = this.initFormData();
        this.add(this._provided.$event);
      });
    },
    loadRole(roleUuid) {
      this.detailLoading = true;
      $axios
        .get(`/proxy/api/security/role/getBean/${roleUuid}`)
        .then(({ data: result }) => {
          if (result.code == 0) {
            this.formData.memberIds.splice(0, this.formData.memberIds.length);
            for (let key of ['id', 'uuid', 'name', 'code', 'remark', 'appId', 'system']) {
              this.formData[key] = result.data[key];
            }
            this.getRoleMems(this.formData.uuid);
            this.loadRolePrivilegeResultTree(this.formData.uuid);
          } else {
            this.detailLoading = false;
            this.resetFormData();
            this.$message.error(result.msg || '加载失败');
          }
        })
        .catch(({ response }) => {
          this.loading = false;
          this.formData = {};
          if (response.data && response.data.msg) {
            _this.$message.error(response.data.msg);
          } else {
            _this.$message.error('服务异常！');
          }
        });
    },

    resetFormData() {
      for (let key of ['id', 'uuid', 'name', 'code', 'remark', 'appId', 'system']) {
        this.formData[key] = undefined;
      }
      if (this.formData.memberIds == undefined) {
        this.$set(this.formData, 'memberIds', []);
      } else {
        this.formData.memberIds.splice(0, this.formData.memberIds.length);
      }
      this.searchValue = '';
      this.roleExpandedKeys = [];
      this.privilegeExpandKeys = [];
    },
    fetchSystemRoles() {
      this.roleLoading = true;
      this.roleTreeData.children.splice(0, this.roleTreeData.children.length);
      $axios
        .get(`/proxy/api/security/role/getRolesInTenantSystem`, {
          params: {
            system: this._$SYSTEM_ID
          }
        })
        .then(({ data }) => {
          this.roleLoading = false;
          if (data.data) {
            data.data.forEach(item => {
              item.icon = 'user';
              if (item.systemDef != '1') {
                this.roleTreeData.children.push(item);
              }
            });
          }
        })
        .catch(error => {});
    },

    fetchSystemPrivileges() {
      this.privilegeLoading = true;
      this.privilegeTreeData.children.splice(0, this.privilegeTreeData.children.length);
      $axios
        .get(`/proxy/api/security/privilege/getPrivilegesInTenantSystem`, {
          params: {
            system: this._$SYSTEM_ID
          }
        })
        .then(({ data }) => {
          this.privilegeLoading = false;
          if (data.data) {
            data.data.forEach(item => {
              item.icon = 'security-scan';
              item.id = item.uuid;
              if (item.systemDef != '1') {
                this.privilegeTreeData.children.push(item);
              }
            });
          }
        })
        .catch(error => {});
    },

    fetchOrgElementModels() {
      $axios.get('/proxy/api/org/elementModel/getAllOrgElementModels', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          data.data.forEach(item => {
            this.$set(this.orgElementModel, item.id, item);
          });
        }
      });
    },
    loadRolePrivilegeResultTree(roleUuid) {
      let updateNodeIcon = treeNodes => {
        treeNodes.forEach(node => {
          let prefix = node.id && node.id.substr(0, 1);
          if (prefix == 'R' || node.id == -1) {
            node.icon = 'user';
          } else if (prefix == 'P') {
            node.icon = 'security-scan';
          }
          updateNodeIcon(node.children || []);
        });
      };
      $axios.get(`/proxy/api/security/role/queryPrivilegeResultAsTree/${roleUuid}`).then(({ data: result }) => {
        if (result.data) {
          this.resultTreeData = [result.data];
          updateNodeIcon(this.resultTreeData);
        }
      });
    },

    loadModuleOptions() {
      const _this = this;
      _this.moduleOptions.splice(0, _this.moduleOptions.length);

      $axios
        .all([
          $axios.get(`/proxy/api/system/getSystemInfoWithProdVersion`, {
            params: {
              system: this._$SYSTEM_ID
            }
          }),
          $axios.get('/proxy/api/app/module/listModuleUnderSystem', {
            params: {
              system: this._$SYSTEM_ID
            }
          })
        ])
        .then(
          $axios.spread((res1, res2) => {
            if (res1.data.data) {
              _this.moduleOptions.push({
                label: res1.data.data.prodVersion.product.name + `(版本${res1.data.data.prodVersion.version})`,
                value: res1.data.data.prodVersion.versionId
              });

              _this.moduleOptions.push({
                label: res1.data.data.prodVersion.product.name,
                value: res1.data.data.prodVersion.product.id
              });
            }

            if (res2.data.data) {
              _this.moduleOptions.push(...res2.data.data.map(item => ({ label: item.name, value: item.id })));
            }
          })
        )
        .catch(error => {});
    },
    filterOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        (option.componentOptions.children[0] &&
          option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0)
      );
    },
    onSearchRoleOrPrivilege(e) {
      this.roleTreeData.children.forEach(item => {
        if (item.name.indexOf(this.searchValue) != -1) {
          item.style = {};
        } else {
          item.style = { display: 'none' };
        }
      });
      this.privilegeTreeData.children.forEach(item => {
        if (item.name.indexOf(this.searchValue) != -1) {
          item.style = {};
        } else {
          item.style = { display: 'none' };
        }
      });
      if (this.searchValue) {
        this.autoExpandParent = true;
        this.roleExpandedKeys = ['-1'];
        this.privilegeExpandKeys = ['-1', '0-0'];
      } else {
        this.autoExpandParent = false;
        this.roleExpandedKeys = [];
        this.privilegeExpandKeys = [];
      }
    },
    onRoleExpand(expandedKeys) {
      this.roleExpandedKeys = expandedKeys;
    },
    onPrivilegeExpand(expandedKeys) {
      this.privilegeExpandKeys = expandedKeys;
    },
    removeSelectedRoleOrPrivilege(id) {
      let index = this.roleCheckedKeys.findIndex(key => key == id);
      if (index != -1) {
        this.roleCheckedKeys.splice(index, 1);
      }
      index = this.privilegeCheckedKeys.findIndex(key => key == id);
      if (index != -1) {
        this.privilegeCheckedKeys.splice(index, 1);
      }
    },
    add(evt) {
      this.clear();
      if (evt) {
        this.$tableWidget = evt.$evtWidget;
      }
      this.formData.id = 'ROLE_' + moment().format('yyyyMMDDHHmmss');
      this.formData.code = this.formData.id;
      this.formData.appId = this._$SYSTEM_ID;
      this.formData.system = this._$SYSTEM_ID;

      this.fetchSystemRoles();
      this.fetchSystemPrivileges();
    },
    clear() {
      this.activeKey = '1';
      this.resetFormData();
      this.roleCheckedKeys = [];
      this.privilegeCheckedKeys = [];
      this.resultTreeData = [];
    },
    viewDetails(evt) {
      this.clear();
      this.$tableWidget = evt.$evtWidget;
      for (let key of ['id', 'uuid', 'name', 'code', 'remark', 'appId', 'system']) {
        this.formData[key] = evt.meta.uuid ? evt.meta[key] : undefined;
      }
      this.fetchSystemRoles();
      this.fetchSystemPrivileges();
      if (this.formData.uuid) {
        this.loadRole(this.formData.uuid);
      }
    },
    save(callback) {
      const _this = this;
      _this.$refs.form.validate(valid => {
        if (valid) {
          let formData = {
            role: {
              uuid: _this.formData.uuid,
              id: _this.formData.id,
              code: _this.formData.code,
              appId: _this.formData.appId || this._$SYSTEM_ID,
              system: _this.formData.system,
              name: _this.formData.name,
              remark: _this.formData.remark
            },
            nestedRoleRemoved: [],
            nestedRoleAdded: [],
            privilegeRemoved: [],
            privilegeAdded: [],
            userIdRemoved: [],
            userIdAdded: [],
            orgElementRemoved: [],
            orgElementAdded: []
          };
          // 新创建的角色归属当前租户系统
          if (formData.role.uuid == undefined) {
            formData.role.system = this._$SYSTEM_ID;
          }
          let roleResult = compareArrayDifference(this.originalRoleKeys, this.roleCheckedKeys);
          formData.nestedRoleRemoved = roleResult.from;
          formData.nestedRoleAdded = roleResult.to;
          let pvgResult = compareArrayDifference(this.originalPrivilegeKeys, this.privilegeCheckedKeys);
          formData.privilegeRemoved = pvgResult.from;
          formData.privilegeAdded = pvgResult.to;

          // 成员数据
          let memberResult = compareArrayDifference(this.originalMemberIds, this.formData.memberIds);
          for (let key in memberResult) {
            memberResult[key].forEach(id => {
              if (id.startsWith('U_')) {
                formData[key == 'from' ? 'userIdRemoved' : 'userIdAdded'].push(id);
              } else {
                formData[key == 'from' ? 'orgElementRemoved' : 'orgElementAdded'].push(id);
              }
            });
          }

          console.log('保存角色数据', formData);
          _this.$loading();
          $axios
            .post(`/proxy/api/security/role/updateRoleMember`, [formData])
            .then(({ data }) => {
              _this.$loading(false);
              if (data.data) {
                _this.$message.success('保存成功！');
                if (typeof callback !== 'function') {
                  _this.clear();
                  _this.loadRole(data.data[0]);
                }
                if (_this.$tableWidget) {
                  _this.$tableWidget.refetch(true);
                }
                $axios.post(`/proxy/api/security/role/publishRoleUpdatedEvent?uuid=${data.data[0]}`);
                if (_this.$widgetDrawerContext && typeof callback !== 'function') {
                  _this.$widgetDrawerContext.close();
                }
                if (typeof callback === 'function') {
                  callback();
                }
              }
            })
            .catch(() => {
              _this.$loading(false);
            });
        } else {
          this.activeKey = '1';
        }
      });
    },

    getRoleMems(roleUuid) {
      this.roleCheckedKeys.splice(0, this.roleCheckedKeys.length);
      this.privilegeCheckedKeys.splice(0, this.privilegeCheckedKeys.length);
      this.formData.memberIds.splice(0, this.formData.memberIds.length);
      this.memberInfos.splice(0, this.memberInfos.length);
      Promise.all([
        $axios.get(`/proxy/api/security/role/getRoleMembers`, { params: { uuid: roleUuid } }),
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
        $axios.get(`/proxy/api/org/organization/orgGroup/getRoleRelaGroups`, {
          params: {
            roleUuid,
            system: this._$SYSTEM_ID
          }
        })
      ]).then(responses => {
        this.detailLoading = false;
        let { privileges, nestedRoles } = responses[0].data.data;
        this.originalPrivilegeKeys = [];
        for (let p of privileges) {
          if (p.systemDef == 1) {
            // 默认访问权限由相关功能绑定，例如产品版本内的角色绑定首页时候会捆绑默认页面权限
            continue;
          }
          this.privilegeCheckedKeys.push(p.uuid);
          this.originalPrivilegeKeys.push(p.uuid);
        }
        this.originalRoleKeys = [];
        for (let p of nestedRoles) {
          if (p.systemDef == '1' && p.id.startsWith('ROLE_VIEW_PAGE_')) {
            // 排除捆绑的默认系统角色，该类角色由相关功能绑定，例如系统首页绑定角色
            continue;
          }
          this.roleCheckedKeys.push(p.uuid);
          this.originalRoleKeys.push(p.uuid);
        }
        let memberIds = [],
          memberInfos = [];
        let orgElements = responses[1].data.data;
        if (orgElements) {
          for (let i = 0, len = orgElements.length; i < len; i++) {
            memberIds.push(orgElements[i].id);
            memberInfos.push({
              id: orgElements[i].id,
              uuid: orgElements[i].uuid,
              name: orgElements[i].name,
              type: orgElements[i].type
            });
          }
        }

        let users = responses[2].data.data;
        if (users) {
          for (let i = 0, len = users.length; i < len; i++) {
            memberIds.push(users[i].userId);
            memberInfos.push({
              id: users[i].userId,
              name: users[i].userName,
              type: 'user'
            });
          }
        }

        let groups = responses[3].data.data;
        if (groups) {
          for (let i = 0, len = groups.length; i < len; i++) {
            memberIds.push(groups[i].id);
            memberInfos.push({
              id: groups[i].id,
              name: groups[i].name,
              type: 'group'
            });
          }
        }

        this.originalMemberIds = memberIds;
        this.memberInfos.push(...memberInfos);
        this.formData.memberIds.push(...memberIds);
      });
    }
  },
  META: {
    method: {
      add: '新增角色',
      viewDetails: '查看角色详情',
      save: '保存角色',
      saveAndNewNextData: '保存并添加下一个'
    }
  }
};
</script>

<style lang="less" scoped>
.selected-role-privileges {
  .close {
    display: none;
  }

  ::v-deep .ant-list-item {
    border-bottom: 0;
    padding: 8px 0 8px 8px;
    justify-content: flex-start;
    align-items: center;
    &:hover {
      background-color: var(--w-primary-color-1);
      .close {
        display: inline-block;
        &:hover {
          color: var(--w-primary-color);
        }
      }
    }
  }
}

.content-box {
  border-radius: 4px;
  border: 1px solid var(--w-border-color-light);
  > div {
    padding: 12px 20px;
    width: 50%;
  }
  .content-title {
    font-size: var(--w-font-size-base);
    color: var(--w-text-color-dark);
    font-weight: bold;
    width: 120px;
    line-height: 32px;
  }
}
</style>
