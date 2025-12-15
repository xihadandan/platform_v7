<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="formData" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" ref="form">
      <a-tabs>
        <a-tab-pane key="basicInfo" :tab="$t('WidgetFileManager.attribute.baseInfo', '基本信息')">
          <a-form-model-item :label="$t('WidgetFileManager.attribute.name', '名称')" prop="name">
            {{ formData.name }}
          </a-form-model-item>
          <a-form-model-item :label="$t('WidgetFileManager.attribute.type', '类型')" prop="contentType">
            {{ formData.contentTypeName }}
          </a-form-model-item>
          <a-form-model-item :label="$t('WidgetFileManager.attribute.location', '位置')" prop="location">
            {{ formData.location }}
          </a-form-model-item>
          <a-form-model-item
            :label="$t('WidgetFileManager.attribute.size', '大小')"
            v-if="formData.contentType != 'application/folder'"
            prop="size"
          >
            {{ formData.sizeString }}({{ formData.size }}{{ $t('WidgetFileManager.attribute.byte', '字节') }})
          </a-form-model-item>
          <a-form-model-item :label="$t('WidgetFileManager.attribute.createTime', '创建时间')" prop="createTime">
            {{ formData.createTime }}
          </a-form-model-item>
          <a-form-model-item :label="$t('WidgetFileManager.attribute.modifyTime', '修改时间')" prop="modifyTime">
            {{ formData.modifyTime }}
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane
          v-if="fileManager.isFolder(file) && formData.folderConfiguration"
          key="privilegeInfo"
          :tab="$t('WidgetFileManager.attribute.permissionsConfig', '权限配置')"
        >
          <a-form-model-item
            :label="$t('WidgetFileManager.attribute.permissionsConfig', '权限配置')"
            :prop="$t('WidgetFileManager.attribute.permissionsConfig', '权限配置')"
          >
            <a-radio-group
              v-model="formData.folderConfiguration.isInheritFolderRole"
              :disabled="!editFolderAttributes"
              button-style="solid"
            >
              <a-radio-button :value="1">{{ $t('WidgetFileManager.attribute.inherit', '继承上级夹') }}</a-radio-button>
              <a-radio-button :value="0">{{ $t('WidgetFileManager.attribute.define', '自定义') }}</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <template v-if="formData.folderConfiguration.isInheritFolderRole == 0">
            <a-form-model-item :label="$t('WidgetFileManager.attribute.manager', '管理者')">
              <a-row>
                <a-col span="22">
                  <OrgSelect
                    v-model="formData.folderConfiguration.administrator"
                    :orgType="['MyOrg', 'MySystemRole']"
                    ref="orgSelect"
                    :orgTypeExtensions="[{ label: $t('WidgetFileManager.attribute.role', '角色'), value: 'MySystemRole' }]"
                    :params="orgSelectParams"
                    :disable="!editFolderAttributes"
                  ></OrgSelect>
                </a-col>
                <a-col span="2"></a-col>
              </a-row>
            </a-form-model-item>
            <a-form-model-item :label="$t('WidgetFileManager.attribute.editor', '编辑者')">
              <a-row>
                <a-col span="22">
                  <OrgSelect
                    v-model="formData.folderConfiguration.editor"
                    :orgType="['MyOrg', 'MySystemRole']"
                    ref="orgSelect"
                    :orgTypeExtensions="[{ label: $t('WidgetFileManager.attribute.role', '角色'), value: 'MySystemRole' }]"
                    :params="orgSelectParams"
                    :disable="!editFolderAttributes"
                  ></OrgSelect>
                </a-col>
                <a-col span="2">
                  &nbsp;
                  <Icon
                    :title="$t('WidgetFileManager.attribute.fileOperationPermissionsSetting', '文件操作权限设置')"
                    type="iconfont icon-ptkj-shezhi"
                    v-if="editFolderAttributes"
                    @click="e => editRoleAction('editor', $t('WidgetFileManager.attribute.editor', '编辑者'))"
                  ></Icon>
                </a-col>
              </a-row>
            </a-form-model-item>
            <a-form-model-item :label="$t('WidgetFileManager.attribute.viewer', '阅读者')">
              <a-row>
                <a-col span="22">
                  <OrgSelect
                    v-model="formData.folderConfiguration.reader"
                    :orgType="['MyOrg', 'MySystemRole']"
                    ref="orgSelect"
                    :orgTypeExtensions="[{ label: $t('WidgetFileManager.attribute.role', '角色'), value: 'MySystemRole' }]"
                    :params="orgSelectParams"
                    :disable="!editFolderAttributes"
                  ></OrgSelect>
                </a-col>
                <a-col span="2">
                  &nbsp;
                  <Icon
                    :title="$t('WidgetFileManager.attribute.fileOperationPermissionsSetting', '文件操作权限设置')"
                    type="iconfont icon-ptkj-shezhi"
                    v-if="editFolderAttributes"
                    @click="e => editRoleAction('reader', $t('WidgetFileManager.attribute.viewer', '阅读者'))"
                  ></Icon>
                </a-col>
              </a-row>
            </a-form-model-item>
          </template>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
    <a-modal
      :title="$t('WidgetFileManager.attribute.fileOperationPermissions', '文件操作权限') + '——' + editRoleName"
      width="900px"
      :visible="roleActionModalVisible"
      @ok="onEditRoleActionOk"
      @cancel="roleActionModalVisible = false"
    >
      <a-checkbox-group v-model="roleActions" style="width: 100%">
        <template v-for="(item, index) in actionOptions">
          <a-row v-if="index % 3 == 0" :key="index">
            <a-col span="8">
              <a-checkbox :value="actionOptions[index].value">
                {{ $t('WidgetFileManager.fileActions.' + actionOptions[index].value, actionOptions[index].label) }}
              </a-checkbox>
            </a-col>
            <a-col span="8">
              <a-checkbox v-if="actionOptions[index + 1]" :value="actionOptions[index + 1].value">
                {{ $t('WidgetFileManager.fileActions.' + actionOptions[index + 1].value, actionOptions[index + 1].label) }}
              </a-checkbox>
            </a-col>
            <a-col span="8">
              <a-checkbox v-if="actionOptions[index + 2]" :value="actionOptions[index + 2].value">
                {{ $t('WidgetFileManager.fileActions.' + actionOptions[index + 2].value, actionOptions[index + 2].label) }}
              </a-checkbox>
            </a-col>
          </a-row>
        </template>
      </a-checkbox-group>
      <template slot="footer">
        <a-button key="back" @click="roleActionModalVisible = false">
          {{ $t('WidgetFileManager.attribute.cancelButtonText', '取消') }}
        </a-button>
        <a-button key="restore" @click="onRestoreRoleAction">{{ $t('WidgetFileManager.attribute.resetButtonText', '恢复默认') }}</a-button>
        <a-button key="submit" type="primary" @click="onEditRoleActionOk">
          {{ $t('WidgetFileManager.attribute.okButtonText', '确定') }}
        </a-button>
      </template>
    </a-modal>
  </a-skeleton>
</template>

<script>
import { deepClone } from '@framework/vue/utils/util';
import { isEmpty } from 'lodash';
import { DEFAULT_ADMIN_ACTIONS, DEFAULT_EDITOR_ACTIONS, DEFAULT_READER_ACTIONS, FILE_ACTION_OPTIONS } from './constant.js';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
export default {
  props: {
    fileManager: Object,
    file: Object
  },
  components: { OrgSelect },
  data() {
    return {
      loading: false,
      formData: {},
      editFolderAttributes: false,
      orgSelectParams: {
        system: this._$SYSTEM_ID
      },
      actionOptions: FILE_ACTION_OPTIONS,
      roleActions: [],
      editRoleName: null,
      roleActionModalVisible: false
    };
  },
  created() {
    const _this = this;
    _this.loadAttributes();
    if (_this.fileManager.isFolder(_this.file)) {
      _this.fileManager.getFolderActions(_this.file.uuid).then(fileActions => {
        _this.editFolderAttributes = fileActions.isAllowEditFolderAttributes();
      });
    }
  },
  methods: {
    loadAttributes() {
      const _this = this;
      _this.loading = true;
      $axios
        .post('/json/data/services', {
          serviceName: 'dmsFileManagerService',
          methodName: 'getAttributes',
          args: JSON.stringify([_this.file])
        })
        .then(({ data: result }) => {
          if (result.data) {
            _this.formData = result.data;
            _this.initData = deepClone(result.data);
          }
          _this.loading = false;
        })
        .catch(({ response }) => {
          _this.loading = false;
          _this.$widget.$message.error(
            (response && response.data && response.data.msg) || _this.$t('WidgetFileManager.message.serverError', '服务异常！')
          );
        });
    },
    editRoleAction(roleType, roleName) {
      const _this = this;
      _this.editRoleType = roleType;
      _this.editRoleName = roleName;
      let roleActions = _this.formData.folderConfiguration[roleType + 'Actions'] || [];
      if (isEmpty(roleActions)) {
        if (_this.editRoleType == 'administrator') {
          roleActions = [...DEFAULT_ADMIN_ACTIONS];
        } else if (_this.editRoleType == 'editor') {
          roleActions = [...DEFAULT_EDITOR_ACTIONS];
        } else if (_this.editRoleType == 'reader') {
          roleActions = [...DEFAULT_READER_ACTIONS];
        }
      }
      _this.roleActions = roleActions;
      _this.roleActionModalVisible = true;
    },
    onRestoreRoleAction() {
      const _this = this;
      let roleActions = [];
      if (_this.editRoleType == 'administrator') {
        roleActions = [...DEFAULT_ADMIN_ACTIONS];
      } else if (_this.editRoleType == 'editor') {
        roleActions = [...DEFAULT_EDITOR_ACTIONS];
      } else if (_this.editRoleType == 'reader') {
        roleActions = [...DEFAULT_READER_ACTIONS];
      }
      _this.roleActions = roleActions;
      _this.$message.info(_this.$t('WidgetFileManager.message.reseted', '已恢复！'));
    },
    onEditRoleActionOk() {
      const _this = this;
      _this.formData.folderConfiguration[_this.editRoleType + 'Actions'] = _this.roleActions;
      _this.roleActionModalVisible = false;
    },
    isChanged() {
      return JSON.stringify(this.initData) != JSON.stringify(this.formData);
    },
    collect() {
      const _this = this;
      if (!_this.editFolderAttributes) {
        return Promise.resolve();
      }
      if (!_this.isChanged()) {
        return Promise.resolve();
      }

      let folderConfiguration = _this.formData.folderConfiguration;
      if (folderConfiguration.isInheritFolderRole) {
        return Promise.resolve(folderConfiguration);
      }

      if (isEmpty(folderConfiguration.administrator)) {
        _this.$message.error(_this.$t('WidgetFileManager.message.requiredFolderManager', '文件夹管理者不能为空！'));
        return Promise.reject();
      }
      if (folderConfiguration.editor && isEmpty(folderConfiguration.editorActions)) {
        folderConfiguration.editorActions = [...DEFAULT_EDITOR_ACTIONS];
        // _this.$message.error('编辑者没有配置文件操作权限！');
        // return Promise.reject();
      }
      if (folderConfiguration.reader && isEmpty(folderConfiguration.readerActions)) {
        folderConfiguration.readerActions = [...DEFAULT_READER_ACTIONS];
        // _this.$message.error('阅读者没有配置文件操作权限！');
        // return Promise.reject();
      }

      // 设置管理者默认权限
      folderConfiguration.administratorActions = [...DEFAULT_ADMIN_ACTIONS];

      return Promise.resolve(folderConfiguration);
    }
  }
};
</script>

<style></style>
