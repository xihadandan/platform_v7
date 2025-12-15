<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" class="pt-form">
    <a-tabs
      defaultActiveKey="1"
      tabPosition="left"
      class="ant-tabs-menu-style"
      :tabBarStyle="{ '--w-tab-menu-style-width': '140px', '--w-tab-menu-style-nav-container-padding': '0 24px 0 0' }"
      style="height: 460px"
    >
      <a-tab-pane key="1" tab="基本信息">
        <div v-if="!editable" style="position: absolute; width: 100%; height: 100%; z-index: 1"></div>
        <a-form-model-item label="单位" prop="unitId" v-if="type == 'unit'">
          <span v-if="uuid">{{ form.name }}</span>
          <a-select :options="unitOptions" v-model="form.unitId" @change="onChangeUnit" v-else />
        </a-form-model-item>
        <a-form-model-item label="名称" prop="name" v-show="type !== 'unit'">
          <a-input v-model="form.name" :maxLength="120">
            <template slot="addonAfter">
              <WI18nInput :target="form" code="name" v-model="form.name" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="简称" prop="shortName">
          <a-input v-model.trim="form.shortName" :maxLength="64">
            <template slot="addonAfter">
              <WI18nInput :target="form" code="short_name" v-model="form.shortName" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="ID" v-if="uuid">
          {{ form.id }}
        </a-form-model-item>
        <a-form-model-item label="编码" prop="code">
          <a-input v-model="form.code" :maxLength="64" />
        </a-form-model-item>
        <a-form-model-item v-if="enableJobRankLevel" label="归属职务" v-show="type == 'job'" prop="jobDuty.dutyId">
          <a-tree-select
            v-model="form.jobDuty.dutyId"
            show-search
            treeNodeFilterProp="title"
            :treeData="dutyTreeData"
            :treeIcon="true"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            allow-clear
          ></a-tree-select>
        </a-form-model-item>
        <a-form-model-item label="父级节点" prop="parentUuid" v-if="vOrgElementTreeData.length > 0">
          <a-tree-select
            v-model="form.parentUuid"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            allow-clear
            :tree-data="vOrgElementTreeData"
            :treeExpandedKeys.sync="treeExpandedKeys"
          ></a-tree-select>
        </a-form-model-item>
        <a-form-model-item label="备注" prop="remark">
          <a-textarea v-model="form.remark" :maxLength="300" />
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="管理信息" v-if="orgElementModelMap[type].type == 'MANAGE'">
        <Scroll style="height: 460px">
          <a-form-model-item label="负责人" :style="{ 'pointer-events': editable ? 'unset' : 'none' }">
            <OrgSelect
              title="选择负责人"
              :orgVersionId="orgVersion.id"
              v-model="form.management.director"
              :checkableTypes="['user', 'job']"
              orgType="MyOrg"
              :multiSelect="false"
              :showBizOrgUnderOrg="false"
            />
          </a-form-model-item>
          <a-form-model-item label="分管领导" :style="{ 'pointer-events': editable ? 'unset' : 'none' }">
            <OrgSelect
              title="选择分管领导"
              :orgVersionId="orgVersion.id"
              v-model="form.management.leader"
              :checkableTypes="['user', 'job']"
              orgType="MyOrg"
              :multiSelect="false"
              :showBizOrgUnderOrg="false"
            />
          </a-form-model-item>
          <div v-if="enableAuthority">
            <a-form-model-item label="分级管理" :style="{ 'pointer-events': editable ? 'unset' : 'none' }">
              <a-switch v-model="form.management.enableAuthority" />
            </a-form-model-item>
            <div v-show="form.management.enableAuthority">
              <a-form-model-item :style="{ 'pointer-events': editable ? 'unset' : 'none' }">
                <template slot="label">{{ orgElementModelMap[type].name }}管理员</template>

                <OrgSelect
                  :title="orgElementModelMap[type].name + '管理员'"
                  :orgVersionId="orgVersion.id"
                  v-model="form.management.orgManager"
                  :checkableTypes="['user']"
                  orgType="MyOrg"
                  :multiSelect="false"
                  :showBizOrgUnderOrg="false"
                />
              </a-form-model-item>
              <a-form-model-item label="管理权限">
                <a-tabs defaultActiveKey="a" type="card" size="small">
                  <a-tab-pane key="a" tab="组织架构" :style="{ 'pointer-events': editable ? 'unset' : 'none' }">
                    <!-- <template v-for="(orgModel, i) in orgElementModels">
                      <a-form-model>
                        <a-form-model-item :key="'orgModelGrant_' + i" v-if="orgModel.id != 'unit'" style="margin: 0px">
                          <template slot="label">
                            {{ orgModel.name }}
                          </template>
                          <a-checkbox-group v-model="orgAuthority[orgModel.id]" :options="getOrgElementEditGrantOptions(orgModel)" />
                        </a-form-model-item>
                      </a-form-model>
                    </template> -->
                    <a-descriptions layout="vertical" bordered size="small" :column="1" :colon="false">
                      <template v-for="(orgModel, i) in orgElementModels">
                        <a-descriptions-item :label="orgModel.name">
                          <a-checkbox-group v-model="orgAuthority[orgModel.id]" :options="getOrgElementEditGrantOptions(orgModel)" />
                        </a-descriptions-item>
                      </template>
                    </a-descriptions>
                  </a-tab-pane>
                  <a-tab-pane key="b" tab="用户" :style="{ 'pointer-events': editable ? 'unset' : 'none' }">
                    <a-checkbox-group v-model="userAuthority" :style="{ width: '100%' }">
                      <a-row>
                        <a-col :span="24">
                          <a-checkbox value="createUser" :disabled="!hasUserConfigAuthority('createUser')">创建用户</a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :span="24">
                          <a-checkbox value="joinUser" :disabled="!hasUserConfigAuthority('joinUser')">添加用户</a-checkbox>
                        </a-col>
                      </a-row>
                      <!-- <a-row>
                      <a-col :span="24">
                        <a-checkbox value="spliceUser" :disabled="!hasUserConfigAuthority('spliceUser')">移出用户</a-checkbox>
                      </a-col>
                    </a-row> -->
                      <a-row type="flex">
                        <a-col flex="auto">
                          <a-checkbox value="allowEdit" :disabled="!hasUserConfigAuthority('allowEdit')">编辑</a-checkbox>
                        </a-col>
                        <a-col flex="360px" v-show="userAuthority.includes('allowEdit')">
                          <a-row type="flex">
                            <a-col :flex="1">
                              <a-checkbox value="editAccountInfo" :disabled="!hasUserConfigAuthority('editAccountInfo')">
                                账号信息
                              </a-checkbox>
                            </a-col>
                            <a-col :flex="1">
                              <a-checkbox value="editUserInfo" :disabled="!hasUserConfigAuthority('editUserInfo')">个人信息</a-checkbox>
                            </a-col>
                            <a-col :flex="1">
                              <a-checkbox value="editJobInfo" :disabled="!hasUserConfigAuthority('editJobInfo')">工作信息</a-checkbox>
                            </a-col>
                            <a-col :flex="1">
                              <a-checkbox value="editRole" :disabled="!hasUserConfigAuthority('editRole')">角色权限</a-checkbox>
                            </a-col>
                          </a-row>
                        </a-col>
                      </a-row>

                      <!-- <a-row type="flex">
                      <a-col flex="90px">
                        <a-checkbox value="moveUser" :disabled="!hasUserConfigAuthority('moveUser')">调整</a-checkbox>
                      </a-col>
                      <a-col flex="180px" v-show="userAuthority.includes('moveUser')">
                        <a-row type="flex">
                          <a-col :flex="1">
                            <a-checkbox value="moveInsideDept" :disabled="!hasUserConfigAuthority('moveInsideDept')">部门内</a-checkbox>
                          </a-col>
                          <a-col :flex="1">
                            <a-checkbox value="moveCrossDept" :disabled="!hasUserConfigAuthority('moveCrossDept')">跨部门</a-checkbox>
                          </a-col>
                        </a-row>
                      </a-col>
                    </a-row> -->

                      <!-- <a-row>
                      <a-col :span="24">
                        <a-checkbox value="jobQuit" :disabled="!hasUserConfigAuthority('jobQuit')">离职</a-checkbox>
                      </a-col>
                    </a-row> -->

                      <a-row>
                        <a-col :span="24">
                          <a-checkbox value="lockUser" :disabled="!hasUserConfigAuthority('lockUser')">冻结</a-checkbox>
                        </a-col>
                      </a-row>

                      <a-row>
                        <a-col :span="24">
                          <a-checkbox value="deleteUser" :disabled="!hasUserConfigAuthority('deleteUser')">删除</a-checkbox>
                        </a-col>
                      </a-row>
                    </a-checkbox-group>
                  </a-tab-pane>
                </a-tabs>
              </a-form-model-item>
            </div>
          </div>
        </Scroll>
      </a-tab-pane>
      <a-tab-pane key="4" tab="角色信息">
        <RoleSelectPanel v-model="form.roleUuids" panelHeight="340px" :editable="editable" />
      </a-tab-pane>
      <a-tab-pane key="5" tab="权限信息" v-if="uuid">
        <OrgRoleResult :orgElementUuid="uuid" panelHeight="400px" />
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script type="text/babel">
import OrgRoleInfo from './authroize-role/org-role-info.vue';
import OrgRoleResult from './authroize-role/org-role-result.vue';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import RoleSelectPanel from './authroize-role/role-select-panel.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'OrgElement',
  props: {
    type: String,
    uuid: String,
    parentUuid: String,
    orgRoles: Array,
    orgElementModels: Array,
    orgSetting: Object,
    orgVersion: Object,
    orgElementTreeData: Array,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: { OrgRoleInfo, OrgRoleResult, OrgSelect, RoleSelectPanel, WI18nInput },
  inject: ['pageContext', 'vPageState', 'parentAllowedMountType', 'orgElementManagementMap'],
  data() {
    let rules = { name: { required: true, message: '名称必填', trigger: ['blur', 'change'] } };
    if (!this.uuid && this.type === 'unit') {
      rules.unitId = { required: true, message: '单位必选', trigger: ['blur', 'change'] };
    }
    return {
      form: {
        parentUuid: this.parentUuid,
        management: {},
        roleUuids: [],
        jobDuty: {}
      },
      roleMember: {},
      orgAuthority: {},
      userAuthority: [],
      orgElementRole: {},
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      rules,
      orgElementEditGrantOptions: [
        { label: '新建', value: 'add' },
        { label: '编辑', value: 'edit' },
        { label: '删除', value: 'delete' }
      ],
      unitOptions: [],
      unitIdMap: {},
      treeExpandedKeys: [],
      dutyTreeData: []
    };
  },
  computed: {
    enableAuthority() {
      return (
        (this.type == 'unit' &&
          this.orgSetting &&
          this.orgSetting['ORG_UNIT_EDIT_GRANT'] &&
          this.orgSetting['ORG_UNIT_EDIT_GRANT'].enable) ||
        (this.type == 'dept' && this.orgSetting && this.orgSetting['ORG_DEPT_EDIT_GRANT'] && this.orgSetting['ORG_DEPT_EDIT_GRANT'].enable)
      );
    },
    enableJobRankLevel() {
      return this.orgSetting && this.orgSetting['ORG_JOB_RANK_LEVEL_ENABLE'] && this.orgSetting['ORG_JOB_RANK_LEVEL_ENABLE'].enable;
    },
    orgElementModelMap() {
      let map = {};
      for (let o of this.orgElementModels) {
        map[o.id] = o;
      }
      return map;
    },
    vOrgElementTreeData() {
      let treeData = JSON.parse(JSON.stringify(this.orgElementTreeData));
      // disabled
      if (treeData.length) {
        let build = data => {
          for (let i = 0; i < data.length; i++) {
            let tar = data[i];
            if (tar.key === this.uuid) {
              // 排除当前节点
              data.splice(i, 1);
              i--;
            } else {
              tar.disabled = !(
                this.parentAllowedMountType &&
                this.parentAllowedMountType[this.type] &&
                this.parentAllowedMountType[this.type].includes(tar.data.type)
              );

              if (!tar.disabled) {
                if (tar.orgAuthority) {
                  tar.disabled = tar.orgAuthority[this.type] == undefined || !tar.orgAuthority[this.type].includes('add');
                }
              }

              tar.value = tar.key;
              delete tar.title;
              delete tar.key;
              if (tar.children) build(tar.children);
            }
          }
        };
        build(treeData);
        this.treeExpandedKeys = treeData.length > 0 ? [treeData[0].value] : [];
      }
      return treeData;
    },
    editable() {
      return this.displayState == 'edit';
    }
  },
  watch: {},
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getOrgElementDetails();
    this.getUnitOptions();
    if (this.enableJobRankLevel && this.type == 'job') {
      this.rules['jobDuty.dutyId'] = { required: true, message: '归属职务必选', trigger: ['blur', 'change'] };
      this.getDutyTreeData();
    }
    if (this.orgElementManagementMap != undefined && Object.keys(this.orgElementManagementMap).length > 0) {
      // 仅授权可管理的几个节点，则新创建的节点必须要有父级节点
      this.rules['parentUuid'] = { required: true, message: '父级节点必选', trigger: ['blur', 'change'] };
    }
  },
  mounted() {},
  methods: {
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    onChangeUnit(v, opt) {
      if (v != undefined) {
        // this.form.id = v;
        this.form.sourceId = v;
        this.form.name = this.unitIdMap[v].name;
        this.form.shortName = this.unitIdMap[v].shortName;
      }
    },
    getDutyTreeData() {
      let _this = this;
      $axios
        .post(`/proxy/api/org/organization/fetchOrgTree/OrgDutySeq`, {})
        .then(({ data }) => {
          console.log('查询职务数据返回：', data);
          if (data.data) {
            let build = d => {
              for (let i = 0, len = d.length; i < len; i++) {
                d[i].selectable = d[i].checkable;
                d[i].value = d[i].key;
                if (d[i].selectable) {
                  // 添加单选框
                  d[i].icon = ({ selected }) => {
                    return <a-radio checked={selected} />;
                  };
                }
                if (d[i].children) {
                  build(d[i].children);
                } else {
                  d[i].isLeaf = true;
                }
              }
            };
            build(data.data);
            _this.dutyTreeData = data.data;
          }
        })
        .catch(error => {
          console.log(error);
          _this.$message.error('组织服务异常');
        });
    },
    getUnitOptions() {
      let _this = this;
      $axios.get(`/proxy/api/org/unit/listUnits`, { params: { system: this._$SYSTEM_ID, tenant: null } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          for (let i = 0, len = data.data.length; i < len; i++) {
            _this.unitIdMap[data.data[i].id] = data.data[i];
            if (data.data[i].enable) {
              _this.unitOptions.push({
                label: data.data[i].name,
                value: data.data[i].id
              });
            }
          }
        }
      });
    },
    getOrgElementDetails() {
      let _this = this;
      if (this.uuid) {
        _this.$loading();
        $axios
          .get(`/proxy/api/org/organization/version/orgElementDetails/${this.uuid}`, {})
          .then(({ data }) => {
            _this.$loading(false);
            if (data.code == 0 && data.data) {
              _this.form = data.data;

              if (_this.form.management) {
                if (_this.form.management.orgAuthority) {
                  _this.orgAuthority = JSON.parse(_this.form.management.orgAuthority);
                }
                if (_this.form.management.userAuthority) {
                  _this.userAuthority = _this.form.management.userAuthority.split(';');
                }
              } else {
                _this.form.management = {};
              }
              _this.form.jobDuty = data.data.jobDuty || { dutyId: undefined };
              _this.form.roleUuids = data.data.roleUuids || [];
              if (data.data.i18ns) {
                let i18n = {};
                for (let item of data.data.i18ns) {
                  if (i18n[item.locale] == undefined) {
                    i18n[item.locale] = {};
                  }
                  i18n[item.locale][item.dataCode] = item.content;
                }
                _this.form.i18n = i18n;
              }
            }
          })
          .catch(() => {
            _this.$loading(false);
          });
      }
    },
    save() {
      let form = this.form,
        _this = this,
        orgVersion = this.orgVersion;
      this.$loading('保存中');
      return new Promise((resolve, reject) => {
        _this.$refs.form.validate(valid => {
          if (valid) {
            form.orgVersionId = orgVersion.id;
            form.orgVersionUuid = orgVersion.uuid;
            form.state = orgVersion.state;
            form.type = _this.type;
            form.management.orgAuthority = JSON.stringify(_this.orgAuthority);
            form.management.userAuthority = _this.userAuthority.join(';');
            if (form.i18n) {
              let i18ns = [];
              for (let locale in form.i18n) {
                for (let key in form.i18n[locale]) {
                  if (form.i18n[locale][key]) {
                    i18ns.push({
                      locale: locale,
                      content: form.i18n[locale][key],
                      dataCode: key
                    });
                  }
                }
              }
              form.i18ns = i18ns;
            }
            $axios
              .post('/proxy/api/org/organization/version/saveOrgElement', form)
              .then(({ data }) => {
                _this.$loading(false);
                if (data.code == 0) {
                  _this.$message.success('保存成功');
                  let node = { ...data.data };
                  resolve(node);
                } else {
                  _this.$message.error(data.msg);
                }
              })
              .catch(error => {
                _this.$message.error('服务异常');
                _this.$loading(false);
              });
          } else {
            _this.$loading(false);
          }
        });
      });
    },

    getOrgElementEditGrantOptions(orgModel) {
      const _this = this;
      let type = _this.type;
      let grantOptions = {};
      if (type == 'unit') {
        grantOptions = (_this.orgSetting && _this.orgSetting['ORG_UNIT_EDIT_GRANT'].attrValueJson) || {};
      } else if (type == 'dept') {
        grantOptions = (_this.orgSetting && _this.orgSetting['ORG_DEPT_EDIT_GRANT'].attrValueJson) || {};
      }

      let authorities = grantOptions[`${orgModel.id}Grant`] || [];
      let retOptions = [];
      _this.orgElementEditGrantOptions.forEach(grant => {
        if (authorities.includes(grant.value)) {
          retOptions.push(grant);
        } else {
          retOptions.push(Object.assign({ disabled: true }, grant));
        }
      });
      return retOptions;
    },
    hasUserConfigAuthority(authority) {
      const _this = this;
      let type = _this.type;
      let grantOptions = {};
      if (type == 'unit') {
        grantOptions = (_this.orgSetting && _this.orgSetting['ORG_UNIT_EDIT_GRANT'].attrValueJson) || {};
      } else if (type == 'dept') {
        grantOptions = (_this.orgSetting && _this.orgSetting['ORG_DEPT_EDIT_GRANT'].attrValueJson) || {};
      }

      let authorities = grantOptions.userGrant || {};
      return authorities.includes(authority);
    }
  }
};
</script>
