<template>
  <div class="role-permission-configuration">
    <div class="content-item">
      <div class="sub-title pt-title-vertical-line">文件库</div>
      <a-card bordered class="action-card">
        <a-checkbox-group v-model="configuration.libraryActions" style="width: 100%" :disabled="readonly">
          <a-checkbox value="viewStorage">查看容量使用</a-checkbox>
          <a-checkbox value="viewRecycleBin">查看回收站</a-checkbox>
        </a-checkbox-group>
      </a-card>
    </div>
    <div class="content-item">
      <div class="sub-title pt-title-vertical-line">文件夹</div>
      <a-card bordered class="action-card">
        编辑操作
        <a-checkbox-group v-model="configuration.editFolderActions" style="width: 100%" :disabled="readonly">
          <a-checkbox value="createFolder">新建夹</a-checkbox>
          <a-checkbox value="renameFolder">重命名夹</a-checkbox>
          <a-checkbox value="moveFolder">移动夹</a-checkbox>
          <a-checkbox value="copyFolder">复制夹</a-checkbox>
        </a-checkbox-group>
      </a-card>
      <p />
      <a-card bordered class="action-card">
        管理操作
        <a-checkbox-group v-model="configuration.manageFolderActions" style="width: 100%" :disabled="readonly">
          <a-checkbox value="viewFolderAttributes">查看夹属性</a-checkbox>
          <a-checkbox value="editFolderAttributes">编辑夹属性</a-checkbox>
          <a-checkbox value="deleteFolder">
            删除夹(
            <a-checkbox value="deleteFolderWithDocuments">有子夹时不可删除</a-checkbox>
            <a-checkbox value="deleteFolderWithoutDocuments">有文档时不可删除</a-checkbox>
            )
          </a-checkbox>
        </a-checkbox-group>
      </a-card>
    </div>
    <div class="content-item">
      <div class="sub-title pt-title-vertical-line">文档</div>
      <a-card bordered class="action-card">
        基础操作
        <a-checkbox-group v-model="configuration.readFileActions" style="width: 100%" :disabled="readonly">
          <a-checkbox value="readFile">查看文档</a-checkbox>
          <a-checkbox value="downloadFile">下载文档</a-checkbox>
          <a-checkbox value="previewFile">预览文档</a-checkbox>
          <a-checkbox value="shareFile">共享文档</a-checkbox>
          <a-checkbox value="viewFileAttributes">查看文档属性</a-checkbox>
        </a-checkbox-group>
        <p />
        数据权限
        <a-radio-group v-model="configuration.readFileRule.mode" default-value="all" style="width: 100%" :disabled="readonly">
          <a-radio value="all">全部文档</a-radio>
          <a-radio value="limit">部分文档</a-radio>
        </a-radio-group>
        <template v-if="configuration.readFileRule.mode === 'limit'">
          <a-checkbox-group v-model="configuration.readFileRule.limitUsers" style="width: 100%" :disabled="readonly">
            <a-checkbox value="creator">本人创建的文档</a-checkbox>
            <br />
            <a-checkbox value="subordinate">下属创建的文档</a-checkbox>
            <template v-if="configuration.readFileRule.limitUsers.includes('subordinate')">
              <br />
              <a-checkbox value="subordinateOfAll" style="margin-left: 10px">包含下级组织节点</a-checkbox>
            </template>
            <br />
            <a-checkbox value="deptUser">同部门人员创建的文档</a-checkbox>
            <template v-if="configuration.readFileRule.limitUsers.includes('deptUser')">
              <br />
              <a-checkbox value="deptUserOfAll" style="margin-left: 10px">包含下级组织节点</a-checkbox>
            </template>
          </a-checkbox-group>
        </template>
      </a-card>
      <p />
      <a-card bordered class="action-card">
        编辑操作
        <a-checkbox-group v-model="configuration.editFileActions" style="width: 100%" :disabled="readonly">
          <a-checkbox value="createFile">上传文件</a-checkbox>
          <a-checkbox value="createDocument">创建文档</a-checkbox>
          <a-checkbox value="renameFile">重命名文档</a-checkbox>
          <a-checkbox value="moveFile">移动文档</a-checkbox>
          <a-checkbox value="copyFile">复制文档</a-checkbox>
        </a-checkbox-group>
        <p />
        数据权限
        <a-radio-group v-model="configuration.editFileRule.mode" default-value="all" style="width: 100%" :disabled="readonly">
          <a-radio value="all">全部文档</a-radio>
          <a-radio value="limit">部分文档</a-radio>
        </a-radio-group>
        <template v-if="configuration.editFileRule.mode === 'limit'">
          <a-checkbox-group v-model="configuration.editFileRule.limitUsers" style="width: 100%" :disabled="readonly">
            <a-checkbox value="creator">本人创建的文档</a-checkbox>
            <br />
            <a-checkbox value="subordinate">下属创建的文档</a-checkbox>
            <template v-if="configuration.editFileRule.limitUsers.includes('subordinate')">
              <br />
              <a-checkbox value="subordinateOfAll" style="margin-left: 10px">包含下级组织节点</a-checkbox>
            </template>
            <br />
            <a-checkbox value="deptUser">同部门人员创建的文档</a-checkbox>
            <template v-if="configuration.editFileRule.limitUsers.includes('deptUser')">
              <br />
              <a-checkbox value="deptUserOfAll" style="margin-left: 10px">包含下级组织节点</a-checkbox>
            </template>
          </a-checkbox-group>
        </template>
      </a-card>
      <p />
      <a-card bordered class="action-card">
        管理操作
        <a-checkbox-group v-model="configuration.manageFileActions" style="width: 100%" :disabled="readonly">
          <a-checkbox value="deleteFile">删除文档</a-checkbox>
          <a-checkbox value="restoreFile">恢复文档</a-checkbox>
        </a-checkbox-group>
        <p />
        数据权限
        <a-radio-group v-model="configuration.manageFileRule.mode" default-value="all" style="width: 100%" :disabled="readonly">
          <a-radio value="all">全部文档</a-radio>
          <a-radio value="limit">部分文档</a-radio>
        </a-radio-group>
        <template v-if="configuration.manageFileRule.mode === 'limit'">
          <a-checkbox-group v-model="configuration.manageFileRule.limitUsers" style="width: 100%" :disabled="readonly">
            <a-checkbox value="creator">本人创建的文档</a-checkbox>
            <br />
            <a-checkbox value="subordinate">下属创建的文档</a-checkbox>
            <template v-if="configuration.manageFileRule.limitUsers.includes('subordinate')">
              <br />
              <a-checkbox value="subordinateOfAll" style="margin-left: 10px">包含下级组织节点</a-checkbox>
            </template>
            <br />
            <a-checkbox value="deptUser">同部门人员创建的文档</a-checkbox>
            <template v-if="configuration.mamanageFileRule.limitUsers.includes('deptUser')">
              <br />
              <a-checkbox value="deptUserOfAll" style="margin-left: 10px">包含下级组织节点</a-checkbox>
            </template>
          </a-checkbox-group>
        </template>
      </a-card>
    </div>
  </div>
</template>

<script>
import { deepClone } from '@framework/vue/utils/util';
const LIBRARY_ACTIONS = ['viewStorage', 'viewRecycleBin'];
const EDIT_FOLDER_ACTIONS = ['createFolder', 'renameFolder', 'moveFolder', 'copyFolder'];
const MANAGE_FOLDER_ACTIONS = [
  'viewFolderAttributes',
  'editFolderAttributes',
  'deleteFolder',
  'deleteFolderWithDocuments',
  'deleteFolderWithoutDocuments'
];
const READ_FILE_ACTIONS = ['readFile', 'downloadFile', 'previewFile', 'shareFile', 'viewFileAttributes'];
const EDIT_FILE_ACTIONS = ['createFile', 'createDocument', 'renameFile', 'moveFile', 'copyFile'];
const MANAGE_FILE_ACTIONS = ['deleteFile', 'restoreFile'];
export default {
  props: {
    configuration: Object,
    readonly: Boolean
  },
  methods: {
    definitionJson2Configuration(definitionJson, actions) {
      let configuration = {};
      if (definitionJson || actions) {
        if (definitionJson) {
          configuration = JSON.parse(definitionJson);
        } else {
          configuration = {
            actions: actions.split(','),
            readFileRule: { mode: 'all', limitUsers: [] },
            editFileRule: { mode: 'all', limitUsers: [] },
            manageFileRule: { mode: 'all', limitUsers: [] }
          };
        }
        configuration.libraryActions = configuration.actions.filter(action => LIBRARY_ACTIONS.includes(action));
        configuration.editFolderActions = configuration.actions.filter(action => EDIT_FOLDER_ACTIONS.includes(action));
        configuration.manageFolderActions = configuration.actions.filter(action => MANAGE_FOLDER_ACTIONS.includes(action));
        configuration.readFileActions = configuration.actions.filter(action => READ_FILE_ACTIONS.includes(action));
        configuration.editFileActions = configuration.actions.filter(action => EDIT_FILE_ACTIONS.includes(action));
        configuration.manageFileActions = configuration.actions.filter(action => MANAGE_FILE_ACTIONS.includes(action));
        if (configuration.administrateFileRule) {
          configuration.manageFileRule = configuration.administrateFileRule;
          delete configuration.administrateFileRule;
        }
        delete configuration.actions;
      } else {
        configuration = {
          libraryActions: [],
          editFolderActions: [],
          manageFolderActions: [],
          readFileActions: [],
          readFileRule: { mode: 'all', limitUsers: [] },
          editFileActions: [],
          editFileRule: { mode: 'all', limitUsers: [] },
          manageFileActions: [],
          manageFileRule: { mode: 'all', limitUsers: [] }
        };
      }
      return configuration;
    },
    configuration2DefinitionJson(configuration) {
      let config = deepClone(configuration);
      config.actions = [
        ...config.libraryActions,
        ...config.editFolderActions,
        ...config.manageFolderActions,
        ...config.readFileActions,
        ...config.editFileActions,
        ...config.manageFileActions
      ];
      delete config.libraryActions;
      delete config.editFolderActions;
      delete config.manageFolderActions;
      delete config.readFileActions;
      delete config.editFileActions;
      delete config.manageFileActions;
      return JSON.stringify(config);
    }
  }
};
</script>

<style lang="less" scoped>
.role-permission-configuration {
  .content-item {
    .sub-title {
      padding-left: 8px;
      font-size: var(--w-font-size-base);
      color: var(--w-text-color-darker);
      line-height: 32px;
      margin-bottom: 12px;
    }

    ::v-deep .ant-form-item-no-colon {
      margin-left: 8px;
    }
    .item-label {
      margin: 12px 8px;
      font-size: var(--w-font-size-sm);
      color: var(--w-text-color-light);
    }
    .item-tip {
      font-size: var(--w-font-size-sm);
      color: var(--w-text-color-light);
      padding: 4px 0;
      line-height: 20px;
    }
    span.item-tip {
      padding-left: 12px;
    }
  }

  .action-card {
    ::v-deep .ant-card-body {
      background-color: #efefef;
    }
  }

  .ant-checkbox-wrapper {
    display: inline;
  }
  .ant-radio-wrapper {
    display: inline;
  }
  ::v-deep .ant-checkbox-disabled + span {
    color: var(--w-checkbox-text-color);
  }
  ::v-deep .ant-radio-disabled + span {
    color: var(--w-radio-text-color);
  }
}
</style>
