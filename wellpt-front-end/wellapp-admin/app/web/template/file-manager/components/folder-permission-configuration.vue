<template>
  <div>
    <a-table
      rowKey="uuid"
      :columns="roleTableColumns"
      :showHeader="false"
      :data-source="configuration.assignRoles"
      :locale="locale"
      :pagination="false"
    >
      <template slot="orgNamesSlot" slot-scope="text, record">
        <OrgSelect
          v-model="record.orgIds"
          :orgType="['MyOrg', 'MySystemRole']"
          :orgTypeExtensions="[{ label: '角色', value: 'MySystemRole' }]"
          :params="orgSelectParams"
          @change="onOrgSelectChange($event, record)"
        ></OrgSelect>
      </template>
      <template slot="operationSlot" slot-scope="text, record">
        <a-button type="link" icon="edit" @click="onEditRoleDefinitionClick(record)">查看权限</a-button>
        <a-button type="link" icon="delete" @click="onRemoveRoleClick(record)">移除</a-button>
      </template>
    </a-table>
    <a-popover v-model="addRoleModelPopoverVisible" trigger="click" placement="bottomLeft">
      <template slot="content">
        <div style="max-height: 300px; overflow: auto">
          <a-radio-group v-model="checkedRoleModelUuid">
            <a-radio v-for="(opt, i) in roleModelOptions" :key="i" :value="opt.value">
              {{ opt.label }}（{{ getModelRoleNames(opt) }}）
            </a-radio>
          </a-radio-group>
        </div>
        <p />
        <a-button type="primary" size="small" @click="onAddRoleModelOk">确定</a-button>
      </template>
      <a-button type="link" icon="plus">添加权限模型</a-button>
    </a-popover>
    <a-popover v-model="addRoleDefinitionPopoverVisible" trigger="click" placement="bottomLeft">
      <template slot="content">
        <div style="max-height: 300px; overflow: auto">
          <a-checkbox-group v-model="checkedRoleUuids">
            <a-checkbox v-for="(opt, i) in checkedRoleDefinitionOptions" :key="i" :value="opt.value">
              {{ opt.label }}
            </a-checkbox>
          </a-checkbox-group>
        </div>
        <p />
        <a-button type="primary" size="small" @click="onAddRoleDefinitionOk">确定</a-button>
      </template>
      <a-button type="link" icon="plus">添加权限组</a-button>
    </a-popover>
    <a-modal
      :title="roleDefinitionModalTitle"
      :visible="roleDefinitionModalVisible"
      width="900px"
      @ok="onEditDefinitionHandleOk"
      @cancel="() => (roleDefinitionModalVisible = false)"
    >
      <div v-if="roleConfiguration" style="max-height: 450px; overflow: auto">
        <RolePermissionConfiguration :configuration="roleConfiguration" :readonly="true"></RolePermissionConfiguration>
      </div>
    </a-modal>
  </div>
</template>

<script>
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import RolePermissionConfiguration from './role-permission-configuration.vue';
import { generateId, deepClone } from '@framework/vue/utils/util';
export default {
  props: {
    configuration: Object
  },
  components: { OrgSelect, RolePermissionConfiguration },
  data() {
    this.configuration.assignRoles = this.configuration.assignRoles || [];
    this.configuration.assignRoles.forEach(item => {
      if (!item.uuid) {
        item.uuid = generateId('SF');
      }
    });
    return {
      roleTableColumns: [
        {
          title: '权限组',
          dataIndex: 'roleName',
          width: 120
        },
        {
          title: '人员',
          dataIndex: 'orgNames',
          width: 120,
          scopedSlots: { customRender: 'orgNamesSlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 120,
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      locale: {
        emptyText: <span>暂无数据</span>
      },
      addRoleModelPopoverVisible: false,
      roleModelOptions: [],
      checkedRoleModelUuid: undefined,
      addRoleDefinitionPopoverVisible: false,
      roleDefinitionOptions: [],
      checkedRoleUuids: [],
      orgSelectParams: {
        system: this._$SYSTEM_ID
      },
      roleDefinitionModalTitle: '权限配置',
      roleDefinitionModalVisible: false,
      roleConfiguration: undefined
    };
  },
  computed: {
    checkedRoleDefinitionOptions() {
      let selectedRoleUuids = this.configuration.assignRoles.map(item => item.roleUuid);
      return this.roleDefinitionOptions.filter(item => !selectedRoleUuids.includes(item.value));
    }
  },
  created() {
    this.loadRoleDefinitions();
    this.loadRoleModels();
  },
  methods: {
    loadRoleDefinitions() {
      const _this = this;
      return $axios.get('/proxy/api/dms/role/list').then(({ data: result }) => {
        if (result.data) {
          _this.roleDefinitionOptions = result.data.map(item => {
            return { label: item.name, value: item.uuid };
          });
        }
      });
    },
    loadRoleModels() {
      const _this = this;
      return $axios.get('/proxy/api/dms/role/model/list').then(({ data: result }) => {
        if (result.data) {
          _this.roleModelOptions = result.data.map(item => {
            return { label: item.name, value: item.uuid, roleUuids: (item.roleUuids && item.roleUuids.split(';')) || [] };
          });
        }
      });
    },
    getModelRoleNames(model) {
      let roleNames = model.roleUuids.map(uuid => {
        let role = this.roleDefinitionOptions.find(item => item.value === uuid);
        return role && role.label;
      });
      return roleNames.join(',');
    },
    onAddRoleModelOk() {
      const _this = this;
      if (!_this.checkedRoleModelUuid) {
        _this.$message.error('请选择权限模型');
        return;
      }

      let roleModel = _this.roleModelOptions.find(item => item.value === _this.checkedRoleModelUuid);
      let roleUuids = [...roleModel.roleUuids];
      let existsAssignRoles = _this.configuration.assignRoles.filter(item => roleUuids.includes(item.roleUuid));
      if (existsAssignRoles.length > 0) {
        existsAssignRoles.forEach(item => {
          let index = roleUuids.indexOf(item.roleUuid);
          roleUuids.splice(index, 1);
        });
        _this.$message.warn(`该权限模型下的权限组[${existsAssignRoles.map(item => item.roleName)}]已存在`);
      }

      let roleDefinitions = _this.roleDefinitionOptions.filter(item => roleUuids.includes(item.value));
      roleDefinitions.forEach(item => {
        _this.configuration.assignRoles.push({
          uuid: generateId('SF'),
          roleUuid: item.value,
          roleName: item.label,
          orgNames: undefined,
          orgIds: undefined
        });
      });

      _this.checkedRoleModelUuid = undefined;
      _this.addRoleModelPopoverVisible = false;
    },
    onAddRoleDefinitionOk() {
      const _this = this;
      if (_this.checkedRoleUuids.length === 0) {
        _this.$message.error('请选择权限组');
        return;
      }

      let roleDefinitions = _this.roleDefinitionOptions.filter(item => _this.checkedRoleUuids.includes(item.value));
      roleDefinitions.forEach(item => {
        _this.configuration.assignRoles.push({
          uuid: generateId('SF'),
          roleUuid: item.value,
          roleName: item.label,
          orgNames: undefined,
          orgIds: undefined
        });
      });
      _this.checkedRoleUuids.length = 0;
      _this.addRoleDefinitionPopoverVisible = false;
    },
    onEditRoleDefinitionClick(record) {
      const _this = this;
      let roleUuid = record.roleUuid;
      $axios.get(`/proxy/api/dms/role/get?uuid=${roleUuid}`).then(({ data: result }) => {
        if (result.data) {
          let roleDefinition = result.data;
          _this.roleDefinitionModalTitle = `${roleDefinition.name}——权限配置`;
          if (roleDefinition.definitionJson || roleDefinition.actions) {
            _this.roleConfiguration = RolePermissionConfiguration.methods.definitionJson2Configuration(
              roleDefinition.definitionJson,
              roleDefinition.actions
            );
          }
          _this.roleDefinition = roleDefinition;
        }
      });
      _this.roleDefinitionModalVisible = true;
    },
    onEditDefinitionHandleOk() {
      const _this = this;
      _this.roleConfiguration = undefined;
    },
    onOrgSelectChange({ value, label }, record) {
      record.orgNames = label;
    },
    onRemoveRoleClick(record) {
      const _this = this;
      let index = _this.configuration.assignRoles.findIndex(item => item.roleUuid == record.roleUuid);
      if (index !== -1) {
        _this.configuration.assignRoles.splice(index, 1);
      }
    }
  }
};
</script>

<style lang="less" scoped>
::v-deep .ant-radio-wrapper {
  display: block;
}
::v-deep .ant-checkbox-wrapper {
  display: block;
}
</style>
