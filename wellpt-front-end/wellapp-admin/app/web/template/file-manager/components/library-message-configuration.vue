<template>
  <div>
    <a-card>
      <a-row>
        <a-col :span="20">文档创建通知</a-col>
        <a-col :span="4" style="text-align: right">
          <a-switch v-model="configuration.message.enabledFileCreated" />
        </a-col>
      </a-row>
      <span class="remark-text">当有新文档创建时，系统将发送通知给指定人员</span>
      <p />
      通知接收人
      <p />
      <OrgSelect
        v-if="fileCreatedRecipient"
        v-model="configuration.message.fileCreatedRecipient"
        :orgType="['MyOrg', 'DmsRoleDefinition']"
        :orgTypeExtensions="[{ label: '库角色', value: 'DmsRoleDefinition' }]"
        :params="orgSelectParams"
      ></OrgSelect>
      <p />
      <a-button type="link " icon="plus" @click="onAddLibraryRoleClick('fileCreatedRecipient')">添加库角色</a-button>
      <a-button type="link" icon="plus" @click="onAddOrgMemberClick('fileCreatedRecipient')">添加组织成员</a-button>
    </a-card>
    <p />
    <a-card>
      <a-row>
        <a-col :span="20">文档更新通知</a-col>
        <a-col :span="4" style="text-align: right">
          <a-switch v-model="configuration.message.enabledFileUpdated" />
        </a-col>
      </a-row>
      <span class="remark-text">当文档内容有更新时，系统将发送通知给指定人员</span>
      <p />
      通知接收人
      <p />
      <OrgSelect
        v-if="fileUpdatedRecipient"
        v-model="configuration.message.fileUpdatedRecipient"
        :orgType="['MyOrg', 'DmsRoleDefinition']"
        :orgTypeExtensions="[{ label: '库角色', value: 'DmsRoleDefinition' }]"
        :params="orgSelectParams"
      ></OrgSelect>
      <p />
      <a-button type="link " icon="plus" @click="onAddLibraryRoleClick('fileUpdatedRecipient')">添加库角色</a-button>
      <a-button type="link" icon="plus" @click="onAddOrgMemberClick('fileUpdatedRecipient')">添加组织成员</a-button>
    </a-card>
    <p />
    <a-card>
      <a-row>
        <a-col :span="20">文档删除通知</a-col>
        <a-col :span="4" style="text-align: right">
          <a-switch v-model="configuration.message.enabledFileDeleted" />
        </a-col>
      </a-row>
      <span class="remark-text">当有文档被删除时，系统将发送通知给指定人员</span>
      <p />
      通知接收人
      <p />
      <OrgSelect
        v-if="fileDeletedRecipient"
        v-model="configuration.message.fileDeletedRecipient"
        :orgType="['MyOrg', 'DmsRoleDefinition']"
        :orgTypeExtensions="[{ label: '库角色', value: 'DmsRoleDefinition' }]"
        :params="orgSelectParams"
      ></OrgSelect>
      <p />
      <a-button type="link " icon="plus" @click="onAddLibraryRoleClick('fileDeletedRecipient')">添加库角色</a-button>
      <a-button type="link" icon="plus" @click="onAddOrgMemberClick('fileDeletedRecipient')">添加组织成员</a-button>
    </a-card>

    <OrgSelect
      v-show="false"
      ref="libraryRoleSelect_fileCreatedRecipient"
      title="选择库角色"
      :orgType="['DmsRoleDefinition']"
      :orgTypeExtensions="[{ label: '库角色', value: 'DmsRoleDefinition' }]"
      :params="orgSelectParams"
      @change="$event => onLibraryRoleChange($event, 'fileCreatedRecipient')"
    ></OrgSelect>
    <OrgSelect
      v-show="false"
      ref="libraryRoleSelect_fileUpdatedRecipient"
      title="选择库角色"
      :orgType="['DmsRoleDefinition']"
      :orgTypeExtensions="[{ label: '库角色', value: 'DmsRoleDefinition' }]"
      :params="orgSelectParams"
      @change="$event => onLibraryRoleChange($event, 'fileUpdatedRecipient')"
    ></OrgSelect>
    <OrgSelect
      v-show="false"
      ref="libraryRoleSelect_fileDeletedRecipient"
      title="选择库角色"
      :orgType="['DmsRoleDefinition']"
      :orgTypeExtensions="[{ label: '库角色', value: 'DmsRoleDefinition' }]"
      :params="orgSelectParams"
      @change="$event => onLibraryRoleChange($event, 'fileDeletedRecipient')"
    ></OrgSelect>
    <OrgSelect
      v-show="false"
      ref="myOrgSelect_fileCreatedRecipient"
      title="选择组织成员"
      :orgType="['MyOrg']"
      @change="$event => onOrgChange($event, 'fileCreatedRecipient')"
    ></OrgSelect>
    <OrgSelect
      v-show="false"
      ref="myOrgSelect_fileUpdatedRecipient"
      title="选择组织成员"
      :orgType="['MyOrg']"
      @change="$event => onOrgChange($event, 'fileUpdatedRecipient')"
    ></OrgSelect>
    <OrgSelect
      v-show="false"
      ref="myOrgSelect_fileDeletedRecipient"
      title="选择组织成员"
      :orgType="['MyOrg']"
      @change="$event => onOrgChange($event, 'fileDeletedRecipient')"
    ></OrgSelect>
  </div>
</template>

<script>
import OrgSelect from '@admin/app/web/lib/org-select.vue';
export default {
  props: {
    configuration: Object
  },
  components: { OrgSelect },
  data() {
    if (!this.configuration.message) {
      this.$set(this.configuration, 'message', {
        enabledFileCreated: false,
        enabledFileUpdated: false,
        enabledFileDeleted: false,
        fileCreatedRecipient: '',
        fileUpdatedRecipient: '',
        fileDeletedRecipient: ''
      });
    }
    return {
      fileCreatedRecipient: true,
      fileUpdatedRecipient: true,
      fileDeletedRecipient: true
    };
  },
  computed: {
    orgSelectParams() {
      return {
        system: this._$SYSTEM_ID,
        roleUuids: this.configuration.assignRoles.map(role => role.roleUuid)
      };
    }
  },
  methods: {
    onAddLibraryRoleClick(recipientKey) {
      this.$refs['libraryRoleSelect_' + recipientKey].openModal();
    },
    onAddOrgMemberClick(recipientKey) {
      this.$refs['myOrgSelect_' + recipientKey].openModal();
    },
    onLibraryRoleChange({ value }, recipientKey) {
      const _this = this;
      _this.onOrgChange({ value }, recipientKey);
    },
    onOrgChange({ value }, recipientKey) {
      const _this = this;
      if (value) {
        let fileRecipient = _this.configuration.message[recipientKey] || '';
        let fileRecipients = fileRecipient.split(';');
        let addedRecipients = value.split(';');
        addedRecipients.forEach(recipient => {
          if (!fileRecipients.includes(recipient)) {
            fileRecipients.push(recipient);
          }
        });
        _this.configuration.message[recipientKey] = fileRecipients.join(';');
      }

      _this[recipientKey] = false;
      _this.$nextTick(() => {
        _this[recipientKey] = true;
      });
    }
  }
};
</script>

<style lang="less" scoped>
.remark-text {
  font-size: var(--w-font-size-sm);
  color: var(--w-gray-color-9);
}
::v-deep .ant-card-body {
  background-color: #efefef;
}
::v-deep .ant-progress-inner {
  background-color: #cecece;
}
</style>
