<template>
  <div>
    <a-form-model
      :model="form"
      :label-col="labelCol"
      labelAlign="left"
      :wrapper-col="wrapperCol"
      :colon="false"
      class="biz-org-config-form-model pt-form"
    >
      <a-card title="组织维度设置" :bordered="false">
        <a-alert
          message="将组织资源(如人员)按照当前业务所需维度进行分配, 划分为多个业务维度节点, 如按项目组分配人员, 则业务维度为项目"
          banner
          class="pt-alert"
          type="info"
          :showIcon="false"
          style="margin-bottom: 8px"
        />
        <a-form-model-item label="组织资源分配维度" prop="bizOrgDimensionId">
          <a-select v-model="form.bizOrgDimensionId" allow-clear>
            <a-select-option value="">不设置维度</a-select-option>
            <template v-for="(item, i) in dimensionOptions">
              <a-select-option :key="item.uuid" :value="item.id">{{ item.name }}</a-select-option>
            </template>
          </a-select>
        </a-form-model-item>
        <a-form-model-item prop="allowDimensionLevel" v-if="form.bizOrgDimensionId">
          <template slot="label">
            <a-tooltip title="允许划分的业务维度层级，例如业务维度为项目，则多级时允许业务组织中项目下添加子项目">
              分配维度层级
              <a-icon type="info-circle" />
            </a-tooltip>
          </template>
          <a-radio-group v-model="form.allowDimensionLevel" button-style="solid" size="small">
            <a-radio-button value="1">一级</a-radio-button>
            <a-radio-button value="-1">多级</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </a-card>
      <a-card title="组织数据设置" :bordered="false">
        <a-alert
          style="margin-bottom: 8px"
          message="业务组织可从关联组织中同步部门数据, 作为业务组织中的业务部门"
          banner
          class="pt-alert"
          type="info"
          :showIcon="false"
        />
        <a-form-model-item prop="enableSyncOrg">
          <template slot="label">
            <a-tooltip title="保持组织同步时，关联组织中的组织树将按照关联组织同步内容，保持自动同步至业务组织根节点或每个业务维度下">
              组织数据维护方式
              <a-icon type="info-circle" />
            </a-tooltip>
          </template>
          <a-radio-group v-model="form.enableSyncOrg" button-style="solid" size="small">
            <a-radio-button value="1">关联组织同步</a-radio-button>
            <a-radio-button value="0">人工维护</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="关联组织同步内容" prop="syncOrgOption" v-if="form.enableSyncOrg == 1">
          <a-checkbox-group v-model="form.syncOrgOption">
            <a-checkbox
              :disabled="form.syncOrgOption.includes(option.value) && form.syncOrgOption.length == 1"
              :value="option.value"
              :key="'ckopt_' + i"
              v-for="(option, i) in syncOrgCheckOptions"
            >
              {{ option.label }}
            </a-checkbox>
          </a-checkbox-group>
        </a-form-model-item>
        <div v-show="form.enableSyncOrg == 0">
          <a-form-model-item prop="allowOrgEleModel">
            <template slot="label">
              <a-tooltip title="设置业务组织中可以使用的组织单元模型，用于构建业务组织树">
                可用组织单元
                <a-icon type="info-circle" />
              </a-tooltip>
            </template>
            <template v-for="(item, i) in orgElementModelOption">
              <div :style="{ borderBottom: '1px solid #E9E9E9' }">
                <a-checkbox
                  :indeterminate="item.indeterminate"
                  v-model="item.checkAll"
                  @change="onAllowOrgElementModelCheckAllChange(item)"
                >
                  全选
                  <a-divider type="vertical" />
                  {{ item.title }}
                </a-checkbox>
              </div>

              <a-checkbox-group
                v-model="item.checked"
                :options="item.options"
                @change="e => orgElementModelCheckAllIndeterminateChange(i, e)"
              />
            </template>
          </a-form-model-item>
          <a-form-model-item prop="allowOrgLevel">
            <template slot="label">
              <a-tooltip title="业务维度下允许创建的组织层级。例如，业务按项目进行划分，则每个项目下的组织架构是扁平的一级还是多级">
                业务维度下的组织层级
                <a-icon type="info-circle" />
              </a-tooltip>
            </template>
            <a-radio-group v-model="form.allowOrgLevel" button-style="solid" size="small">
              <a-radio-button value="1">一级</a-radio-button>
              <a-radio-button value="-1">多级</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
        </div>
      </a-card>
      <a-card title="业务角色设置" :bordered="false">
        <div>
          <Drawer
            title="添加业务角色"
            :width="500"
            :ok="e => confirmEditBizRoleInfo(e, undefined)"
            okText="保存"
            :container="childDrawerContainer"
            :zIndex="1000"
            mask
            :destroyOnClose="true"
          >
            <a-button type="primary" icon="plus" size="small">添加业务角色</a-button>
            <template slot="content">
              <BizOrgRoleInfo
                :bizOrgUuid="bizOrgUuid"
                :roleInfo="{}"
                :role-list="bizOrgRoleList"
                :org-element-models="orgElementModels"
                ref="newBizOrgRoleInfo"
              />
            </template>
          </Drawer>
          <Drawer
            title="从模板添加"
            :width="500"
            :ok="confirmAppendBizOrgRoleTemplate"
            okText="添加"
            :container="childDrawerContainer"
            :zIndex="1000"
            mask
          >
            <a-button @click="fetchBizOrgRoleTemplates" size="small">从模板添加</a-button>
            <template slot="content">
              <div :class="['biz-org-role-template-drawer-content']">
                <a-spin v-if="bizOrgRoleTemplateLoading" />
                <template v-else>
                  <div v-for="(item, i) in bizOrgRoleTemplate" :key="'popover' + i">
                    <div>
                      <div
                        :class="['row', selectedBizOrgRoleTemplateKey.includes(item.uuid) ? 'selected' : undefined]"
                        @click="onSelectBizOrgRoleTemplate(item)"
                      >
                        <div class="icon-container">
                          <Icon v-if="item.icon" :type="item.icon" style="font-size: 24px" />
                          <div v-else style="font-size: 24px">{{ item.name.charAt(0) }}</div>
                        </div>
                        <div style="width: calc(100% - 64px); padding-left: 12px">
                          <div style="font-weight: bold; line-height: 24px; color: #333333">{{ item.name }}</div>
                          <div style="line-height: 24px; color: #999999">{{ item.remark }}</div>
                          <div>
                            <a-tag
                              class="pt-tag"
                              v-for="(roleTpt, r) in item.roleTemplate"
                              style="margin-bottom: 4px"
                              :key="'role_tag_' + r"
                            >
                              {{ roleTpt.name }}
                            </a-tag>
                          </div>
                        </div>
                        <div>
                          <a-checkbox :checked="selectedBizOrgRoleTemplateKey.includes(item.uuid)" />
                        </div>
                      </div>
                    </div>
                  </div>
                  <a-empty v-if="bizOrgRoleTemplate.length == 0" />
                </template>
              </div>
            </template>
          </Drawer>
        </div>
        <a-alert
          :showIcon="false"
          banner
          message="设置当前业务组织下的业务角色, 并且可设置应用于业务维度节点或者业务组织节点"
          class="pt-alert"
          type="info"
          style="margin: 8px 0px"
        />
        <a-table :columns="roleColumns" :data-source="bizOrgRoleList" rowKey="id" ref="roleTable" :pagination="false">
          <template slot="nameSlot" slot-scope="text, record">
            <div style="display: flex; align-items: center">
              <Icon type="iconfont icon-ptkj-tuodong" class="drag-btn-handler" title="拖动排序" />
              <a-input v-model="record.name" :maxLength="120" style="width: 150px; margin-left: 8px" />
            </div>
          </template>
          <template slot="applyToSlot" slot-scope="text, record">
            <a-tag class="pt-tag" v-for="(apply, a) in text" style="margin-bottom: 3px" :key="'tag_' + a">
              {{ apply == 'BIZ_DIMENSION_ELEMENT' ? '业务维度节点' : '业务组织节点' }}
              <a-icon v-if="text.length > 1" type="close" @click="text.splice(a, 1)" />
            </a-tag>
          </template>
          <!-- <template slot="configDetailSlot" slot-scope="text, record">
            <a-row>
                <a-col :span="12">
                <div style="color: #adadad; margin-bottom: 8px">成员可选类型</div>
                <template v-for="(memberType, m) in record.allowMemberType">
                  <a-tag v-if="orgElementModelMap[memberType]" style="margin-bottom: 3px">
                    {{ orgElementModelMap[memberType].name }}
                  </a-tag>
                </template>
              </a-col>
              <a-col :span="24">
                <div style="color: #adadad; margin-bottom: 8px">成员单 / 多选</div>
                <a-tag color="green">
                  {{ record.multipleSelectMember ? '多选' : '单选' }}
                </a-tag>
              </a-col>
            </a-row>
          </template> -->
          <template slot="operationSlot" slot-scope="text, record, index">
            <Drawer
              title="编辑业务角色"
              :ok="e => confirmEditBizRoleInfo(e, record)"
              :width="500"
              okText="保存"
              :container="childDrawerContainer"
              :zIndex="1000"
              mask
              :destroyOnClose="true"
            >
              <a-button type="link" size="small">
                <Icon type="iconfont icon-ptkj-bianji" />
                编辑
              </a-button>
              <template slot="content">
                <BizOrgRoleInfo
                  :roleInfo="record"
                  :bizOrgUuid="bizOrgUuid"
                  :org-element-models="orgElementModels"
                  :role-list="bizOrgRoleList"
                  ref="editBizOrgRoleInfo"
                  :ok="e => confirmEditBizRoleInfo(e, record)"
                />
              </template>
            </Drawer>
            <a-button type="link" @click="bizOrgRoleList.splice(index, 1)" size="small">
              <Icon type="iconfont icon-ptkj-shanchu" />
              删除
            </a-button>
          </template>
        </a-table>
      </a-card>
    </a-form-model>
    <div class="drawer-container" style="position: absolute"></div>
  </div>
</template>
<style lang="less">
.biz-org-role-template-drawer-content {
  .row {
    display: flex;
    padding: 8px 12px;
    border: 1px solid #f4f3f3;
    margin-bottom: 12px;
    border-radius: 4px;
    align-items: center;
    &:hover {
      background-color: #f4f3f3;
    }
    &.selected {
      border: 1px solid var(--w-primary-color);
    }
    .icon-container {
      width: 40px;
      height: 40px;
      border-radius: 8px;
      background-color: var(--w-primary-color);
      color: #ffffff;
      border: 1px solid #ffffff;
      display: flex;
      align-items: center;
      justify-content: center;
      align-self: flex-start;
    }
  }
}
</style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import { generateId } from '@framework/vue/utils/util';
import BizOrgRoleInfo from './biz-org-role-info.vue';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'BizOrgConfig',
  mixins: [draggable],
  inject: ['$event', 'designMode', 'widgetDrawerContext'],
  props: {
    bizOrgUuid: String
  },
  components: { Drawer, BizOrgRoleInfo },
  computed: {},
  data() {
    return {
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
      form: {
        bizOrgDimensionId: '',
        allowDimensionLevel: '1',
        enableSyncOrg: '1',
        syncOrgOption: ['managementOrgElement', 'noManagementOrgElement'],
        allowOrgEleModel: [],
        allowOrgLevel: '1'
      },
      dimensionOptions: [],
      syncOrgCheckOptions: [
        { label: '管理职能组织节点', value: 'managementOrgElement' },
        { label: '非职能组织节点', value: 'noManagementOrgElement' },
        { label: '同步关联组织的顶级节点', value: 'includeSyncTopOrgElement' }
      ],
      roleColumns: [
        {
          title: '名称',
          dataIndex: 'name',
          key: 'name',
          width: 200,
          scopedSlots: { customRender: 'nameSlot' }
        },
        {
          title: '应用于',
          dataIndex: 'applyTo',
          key: 'applyTo',
          scopedSlots: { customRender: 'applyToSlot' }
        },
        // {
        //   title: '角色成员选项配置',
        //   dataIndex: 'configDetail',
        //   key: 'configDetail',
        //   scopedSlots: { customRender: 'configDetailSlot' }
        // },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 160,
          key: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      bizOrgRoleList: [],
      bizOrgRoleTemplate: [],
      bizOrgRoleTemplateLoading: true,
      selectedBizOrgRoleTemplateKey: [],
      orgElementModels: [],
      orgElementModelMap: {
        user: {
          name: '人员'
        }
      },
      orgElementModelOption: [
        {
          title: '管理职能类',
          checkAll: false,
          indeterminate: false,
          checked: [],
          options: []
        },
        { title: '非职能类', checkAll: false, indeterminate: false, options: [], checked: [] }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (!this.designMode) {
      if (this.$event != undefined && this.$event.meta.UUID) {
        this.bizOrgUuid = this.$event.meta.UUID;
      }
      this.fetchBizOrgDimensions().then(list => {
        this.dimensionOptions.push(...list);
      });
      this.fetchBizOrgRoles(this.bizOrgUuid).then(list => {
        for (let role of list) {
          if (role.i18ns) {
            let i18n = {};
            for (let item of role.i18ns) {
              if (i18n[item.locale] == undefined) {
                i18n[item.locale] = {};
              }
              i18n[item.locale][item.dataCode] = item.content;
            }
            role.i18n = i18n;
          }
        }
        this.bizOrgRoleList.push(...list);
      });
      this.fetchBizOrgConfig(this.bizOrgUuid).then(data => {
        if (data) {
          this.form.uuid = data.uuid;
          this.form.bizOrgDimensionId = data.bizOrgDimensionId || '';
          this.form.allowDimensionLevel = data.allowDimensionLevel + '';
          this.form.allowOrgLevel = data.allowOrgLevel + '';
          this.form.enableSyncOrg = data.enableSyncOrg ? '1' : '0';
          this.form.syncOrgOption = (data.syncOrgOption || '').split(';');
          this.form.allowOrgEleModel = (data.allowOrgEleModel || '').split(';');
        }
        this.fetchOrgElementModel().then(list => {
          this.orgElementModels = list;
          for (let item of list) {
            if (item.id == 'job') {
              continue;
            }
            this.orgElementModelMap[item.id] = item;
            if (item.type == 'MANAGE' || item.type == 'MANAGELESS') {
              let opt = this.orgElementModelOption[item.type == 'MANAGE' ? 0 : 1];
              opt.options.push({
                label: item.name,
                value: item.id
              });
              if (this.form.uuid && this.form.allowOrgEleModel.includes(item.id)) {
                opt.checked.push(item.id);
              }
            }
          }
          if (this.form.uuid) {
            this.orgElementModelCheckAllIndeterminateChange();
          }
        });
      });
    }
  },
  mounted() {
    this.tableDraggable(this.bizOrgRoleList, this.$refs.roleTable.$el.querySelector('tbody'), '.drag-btn-handler');
  },
  methods: {
    childDrawerContainer() {
      return document.body; //this.$el.closest('.ant-tabs') || this.$el.closest('.ant-drawer-body') || this.$el.closest('.ant-modal-body');
    },
    confirmEditBizRoleInfo(e, item) {
      this.$refs[item ? 'editBizOrgRoleInfo' : 'newBizOrgRoleInfo'].collectFormData().then(data => {
        let i18ns = undefined;
        if (data.i18n) {
          i18ns = [];
          for (let locale in data.i18n) {
            for (let key in data.i18n[locale]) {
              if (data.i18n[locale][key]) {
                i18ns.push({
                  locale: locale,
                  content: data.i18n[locale][key],
                  dataCode: key
                });
              }
            }
          }
        }
        if (item) {
          item.i18ns = i18ns;
          for (let key of ['name', 'applyTo', 'remark', 'allowMemberType', 'multipleSelectMember']) {
            this.$set(item, key, data[key]);
          }
        } else {
          this.bizOrgRoleList.push({
            id: data.id,
            name: data.name,
            applyTo: data.applyTo,
            remark: data.remark,
            allowMemberType: data.allowMemberType,
            multipleSelectMember: data.multipleSelectMember,
            i18ns
          });
        }
        e(true);
      });
    },
    confirmAppendBizOrgRoleTemplate(e) {
      let names = [],
        ids = [];
      for (let i = 0; i < this.bizOrgRoleList.length; i++) {
        names.push(this.bizOrgRoleList[i].name);
        ids.push(this.bizOrgRoleList[i].id);
      }
      for (let i = 0; i < this.bizOrgRoleTemplate.length; i++) {
        let roleTemplate = this.bizOrgRoleTemplate[i].roleTemplate;
        if (roleTemplate.length) {
          for (let role of roleTemplate) {
            if (!ids.includes(role.id) && this.selectedBizOrgRoleTemplateKey.includes(this.bizOrgRoleTemplate[i].uuid)) {
              let i18n = role.i18n,
                i18ns = [];

              if (i18n) {
                for (let locale in i18n) {
                  for (let key in i18n[locale]) {
                    if (i18n[locale][key]) {
                      i18ns.push({
                        locale: locale,
                        content: i18n[locale][key],
                        dataCode: key
                      });
                    }
                  }
                }
              }
              this.bizOrgRoleList.push({
                id: role.id,
                name: role.name,
                applyTo: role.applyTo,
                remark: role.remark,
                allowMemberType: role.allowMemberType,
                multipleSelectMember: role.multipleSelectMember == 1,
                i18n,
                i18ns
              });
            }
          }
        }
      }
      e(true);
    },
    save() {
      return new Promise((resolve, reject) => {
        let formData = JSON.parse(JSON.stringify(this.form));
        formData.allowOrgEleModel = formData.allowOrgEleModel.join(';');
        formData.syncOrgOption = formData.syncOrgOption.join(';');
        formData.bizOrgUuid = this.bizOrgUuid;
        formData.enableSyncOrg = formData.enableSyncOrg == 1;
        if (formData.bizOrgDimensionId == '') {
          formData.bizOrgDimensionId = undefined;
        }
        if (this.bizOrgRoleList.length) {
          formData.bizOrgRoles = [];
          let i = 0;
          for (let o of this.bizOrgRoleList) {
            let temp = JSON.parse(JSON.stringify(o));
            if (temp.allowMemberType) {
              temp.allowMemberType = temp.allowMemberType.join(';');
            }
            if (temp.applyTo) {
              temp.applyTo = temp.applyTo.join(';');
            }
            temp.seq = ++i;
            formData.bizOrgRoles.push(temp);
          }
        }
        formData.allowOrgEleModel = [];
        for (let item of this.orgElementModelOption) {
          formData.allowOrgEleModel.push(...item.checked);
        }
        formData.allowOrgEleModel = formData.allowOrgEleModel.join(';');
        $axios
          .post(`/proxy/api/org/biz/saveBizOrgConfig`, formData)
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('保存成功');
              resolve(formData);
              if (this.$event != undefined) {
                this.widgetDrawerContext.close();
              }
            }
          })
          .catch(error => {
            this.$message.error('保存失败');
          });
      });
    },
    onAllowOrgElementModelCheckAllChange(item) {
      item.checked.splice(0, item.checked.length);
      if (item.checkAll) {
        for (let o of item.options) {
          item.checked.push(o.value);
        }
      }
      item.indeterminate = item.checked.length > 0 && item.checked.length < item.options.length;
    },

    orgElementModelCheckAllIndeterminateChange() {
      for (let item of this.orgElementModelOption) {
        item.indeterminate = item.checked.length > 0 && item.checked.length < item.options.length;
        item.checkAll = item.checked.length > 0 && item.checked.length == item.options.length;
      }
    },

    fetchOrgElementModel() {
      return new Promise((resolve, reject) => {
        $axios.get('/proxy/api/org/elementModel/getAllOrgElementModels', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
          if (data.code == 0 && data.data) {
            let list = [];
            for (let d of data.data) {
              if (d.enable) {
                list.push(d);
              }
            }
            resolve(list);
          }
        });
      });
    },
    fetchBizOrgConfig(bizOrgUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/org/biz/getBizOrgConfig`, { params: { bizOrgUuid } })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    },

    fetchBizOrgDimensions() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/org/biz/getSystemBizOrgDimensions`, { params: {} })
          .then(({ data }) => {
            resolve(data.data || []);
          })
          .catch(error => {});
      });
    },
    fetchBizOrgRoles(bizOrgUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/org/biz/getBizOrgRolesByBizOrgUuid`, { params: { bizOrgUuid, fetchI18ns: true } })
          .then(({ data }) => {
            let roles = [];
            if (data.code == 0 && data.data) {
              for (let i of data.data) {
                if (i.allowMemberType) {
                  i.allowMemberType = i.allowMemberType.split(';');
                }
                i.multipleSelectMember = i.multipleSelectMember == 1;
                if (i.applyTo) {
                  i.applyTo = i.applyTo.split(';');
                }
                roles.push(i);
              }
            }
            resolve(roles);
          })
          .catch(error => {});
      });
    },
    onSelectBizOrgRoleTemplate(item) {
      let i = this.selectedBizOrgRoleTemplateKey.indexOf(item.uuid);
      if (i == -1) {
        this.selectedBizOrgRoleTemplateKey.push(item.uuid);
      } else {
        this.selectedBizOrgRoleTemplateKey.splice(i, 1);
      }
    },
    fetchBizOrgRoleTemplates() {
      this.selectedBizOrgRoleTemplateKey.splice(0, this.selectedBizOrgRoleTemplateKey.length);
      if (this.bizOrgRoleTemplateLoading) {
        $axios
          .get(`/proxy/api/org/biz/getSystemBizOrgRoleTemplates`, { params: {} })
          .then(({ data }) => {
            this.bizOrgRoleTemplateLoading = false;
            if (data.code == 0 && data.data) {
              for (let i of data.data) {
                i.roleTemplate = i.roleTemplate ? JSON.parse(i.roleTemplate) : [];
                this.bizOrgRoleTemplate.push(i);
              }
            }
          })
          .catch(error => {});
      }
    }
  },
  META: {
    method: {
      save: '保存业务组织配置'
    }
  }
};
</script>
