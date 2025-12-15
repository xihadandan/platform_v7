<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    class="role-model-details pt-form"
    :label-col="{ span: 5 }"
    labelAlign="left"
    :wrapper-col="{ span: 19 }"
    :colon="false"
  >
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model-item label="名称" prop="name">
          <a-input v-model="formData.name" />
        </a-form-model-item>
        <a-form-model-item label="ID" prop="id">
          {{ formData.id || formData.uuid }}
        </a-form-model-item>
        <a-form-model-item label="编号" prop="code">
          <a-input v-model="formData.code" />
        </a-form-model-item>
        <a-form-model-item label="图标" prop="icon">
          <WidgetDesignModal
            title="选择图标"
            :zIndex="1000"
            :width="640"
            dialogClass="pt-modal widget-icon-lib-modal"
            :bodyStyle="{ height: '560px' }"
            :maxHeight="560"
            mask
            bodyContainer
          >
            <IconSetBadge v-model="formData.icon"></IconSetBadge>
            <template slot="content">
              <WidgetIconLib v-model="formData.icon" />
            </template>
          </WidgetDesignModal>
        </a-form-model-item>
        <a-form-model-item label="分类" prop="categoryUuid">
          <a-select v-model="formData.categoryUuid" :options="libraryCategoryOptions"></a-select>
        </a-form-model-item>
        <a-form-model-item label="文档版本化" prop="enabledVersion">
          <a-switch v-model="formData.configuration.enabledVersion" />
        </a-form-model-item>
        <a-form-model-item v-if="formData.configuration.enabledVersion" label="版本化需审批" prop="enabledVersionApprove">
          <a-switch v-model="formData.configuration.enabledVersionApprove" @change="loadWorkflowOptions" />
        </a-form-model-item>
        <a-form-model-item
          v-if="formData.configuration.enabledVersion && formData.configuration.enabledVersionApprove"
          label="文档版本审批流程"
          prop="approveFlowDefIds"
        >
          <a-select
            v-model="formData.configuration.approveFlowDefIds"
            :options="workflowOptions"
            :filter-option="filterSelectOption"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="描述" prop="remark">
          <a-textarea v-model="formData.remark" maxlength="66" />
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="内容定义">
        <FolderContentConfiguration :configuration="formData.configuration"></FolderContentConfiguration>
      </a-tab-pane>
      <a-tab-pane key="3" tab="权限配置">
        <FolderPermissionConfiguration :configuration="formData.configuration"></FolderPermissionConfiguration>
      </a-tab-pane>
      <a-tab-pane key="4" tab="存储配置">
        <LibraryStorageConfiguration :configuration="formData.configuration"></LibraryStorageConfiguration>
      </a-tab-pane>
      <a-tab-pane key="5" tab="消息通知">
        <LibraryMessageConfiguration :configuration="formData.configuration"></LibraryMessageConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';
import WidgetIconLib from '@pageAssembly/app/web/widget/widget-icon-lib/widget-icon-lib.vue';
import WidgetDesignModal from '@pageAssembly/app/web/widget/commons/widget-design-modal.vue';
import IconSetBadge from '@pageAssembly/app/web/widget/commons/icon-set-badge.vue';
import FolderContentConfiguration from './components/folder-content-configuration.vue';
import FolderPermissionConfiguration from './components/folder-permission-configuration.vue';
import LibraryStorageConfiguration from './components/library-storage-configuration.vue';
import LibraryMessageConfiguration from './components/library-message-configuration.vue';
export default {
  components: {
    WidgetIconLib,
    WidgetDesignModal,
    IconSetBadge,
    FolderContentConfiguration,
    FolderPermissionConfiguration,
    LibraryStorageConfiguration,
    LibraryMessageConfiguration
  },
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event || {};
    let formData = $event.meta || {};
    if (!formData.uuid) {
      formData.id = generateId('SF');
      formData.type = 0;
    }
    formData.configuration = this.definitionJson2Configuration(formData.definitionJson);
    return {
      uuid: formData.uuid,
      formData,
      rules: {
        name: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        ['configuration.dataTypes']: { required: true, message: '不能为空', trigger: ['blur', 'change'] }
      },
      libraryCategoryOptions: [],
      workflowOptions: []
    };
  },
  created() {
    if (this.uuid) {
      this.loadFileLibrary().then(() => {
        if (
          this.formData.configuration &&
          this.formData.configuration.enabledVersion &&
          this.formData.configuration.enabledVersionApprove
        ) {
          this.loadWorkflowOptions();
        }
      });
    }
    this.loadLibraryCategoryOptions();
  },
  methods: {
    filterSelectOption,
    loadFileLibrary() {
      return $axios
        .get('/proxy/api/dms/folder/config/get?uuid=' + this.uuid)
        .then(({ data: result }) => {
          if (result.data) {
            let formData = result.data;
            if (formData.i18ns) {
              let i18n = {};
              for (let item of formData.i18ns) {
                if (i18n[item.locale] == undefined) {
                  i18n[item.locale] = {};
                }
                i18n[item.locale][item.code] = item.content;
              }
              formData.i18n = i18n;
            }
            this.formData = formData;
          } else {
            this.$message.error(result.msg || '服务异常！');
          }
        })
        .catch(({ response }) => {
          this.$message.error((response.data && response.data.msg) || '服务异常！');
        });
    },
    loadLibraryCategoryOptions() {
      $axios.get('/proxy/api/dms/library/category/list').then(({ data: result }) => {
        if (result.data) {
          this.libraryCategoryOptions = result.data.map(item => ({ label: item.name, value: item.uuid }));
        }
      });
    },
    loadWorkflowOptions() {
      if (this.workflowOptions.length) {
        return;
      }
      $axios
        .post('/json/data/services', {
          serviceName: 'flowDefineService',
          methodName: 'query',
          args: JSON.stringify([{}])
        })
        .then(({ data: result }) => {
          if (result.data) {
            this.workflowOptions = result.data.map(item => ({ label: item.name, value: item.id }));
          }
        });
    },
    definitionJson2Configuration(definitionJson) {
      let configuration = {};
      if (definitionJson) {
        configuration = JSON.parse(definitionJson);
      } else {
        configuration = {
          dataTypes: ['FILE'],
          isInheritFolderRole: '0',
          fileAcceptMode: 'unlimited',
          fileAccept: [],
          assignRoles: [],
          storage: {
            quota: 'unlimited',
            limitSize: 50,
            limitUnit: 'GB',
            fileQuota: 'unlimited',
            fileSizeLimit: 100,
            fileSizeLimitUnit: 'MB',
            recycleBinRetention: 'unlimited',
            recycleBinRetentionTimeLimit: 30,
            recycleBinRetentionTimeUnit: 'day'
          },
          message: {
            enabledFileCreated: false,
            enabledFileUpdated: false,
            enabledFileDeleted: false,
            fileCreatedRecipient: '',
            fileUpdatedRecipient: '',
            fileDeletedRecipient: ''
          }
        };
      }
      return configuration;
    },
    save($evt) {
      const _this = this;
      _this.$loading();
      let item = _this.formData;
      if (item.i18n) {
        let i18ns = [];
        for (let locale in item.i18n) {
          for (let key in item.i18n[locale]) {
            if (item.i18n[locale][key]) {
              i18ns.push({
                locale: locale,
                content: item.i18n[locale][key],
                code: key
              });
            }
          }
        }
        item.i18ns = i18ns;
      }

      _this.$refs.form.validate(result => {
        if (result) {
          $axios
            .post('/proxy/api/dms/folder/config/save', _this.formData)
            .then(({ data: result }) => {
              if (result.code == 0) {
                _this.$message.success('保存成功！');
                $evt.$evtWidget && $evt.$evtWidget.closeDrawer && $evt.$evtWidget.closeDrawer();
                _this.$event && _this.$event.$evtWidget && _this.$event.$evtWidget.refetch && _this.$event.$evtWidget.refetch(true);
              } else {
                _this.$message.error(result.msg || '服务异常！');
              }
              _this.$loading(false);
            })
            .catch(({ response }) => {
              _this.$loading(false);
              _this.$message.error((response.data && response.data.msg) || '服务异常！');
            });
        } else {
          _this.$loading(false);
        }
      });
    }
  },
  META: {
    method: {
      save: '保存文件库'
    }
  }
};
</script>

<style lang="less" scoped></style>
