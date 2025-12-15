<template>
  <a-skeleton active :loading="false" :paragraph="{ rows: 10 }">
    <a-form-model class="pt-form" :model="formData" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :rules="rules" ref="form">
      <a-tabs class="pt-tabs">
        <a-tab-pane key="basicInfo" tab="基本信息">
          <a-form-model-item label="名称" prop="name">
            <a-input v-model="formData.name">
              <template slot="addonAfter">
                <w-i18n-input :target="formData" code="name" v-model="formData.name" />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="编号" prop="code">
            <a-input v-model="formData.code" />
          </a-form-model-item>
          <a-form-model-item label="备注" prop="remark">
            <a-textarea v-model="formData.remark" />
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="contentInfo" tab="内容定义">
          <template v-if="formData.configuration">
            <a-form-model-item label="类型">
              <a-radio-group v-model="formData.configuration.dataType">
                <a-radio v-for="item in dataTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio>
              </a-radio-group>
            </a-form-model-item>
            <template v-if="dyformConfigurable">
              <a-form-model-item label="动态表单">
                <a-select
                  v-model="formData.configuration.formUuid"
                  showSearch
                  allow-clear
                  :options="formOptions"
                  :filter-option="filterSelectOption"
                  @search="onFormSearch"
                  @change="onFormChange"
                ></a-select>
              </a-form-model-item>
              <a-form-model-item label="文档标题字段">
                <a-select
                  v-model="formData.configuration.fileNameField"
                  showSearch
                  allow-clear
                  :options="formFieldOptions"
                  :filter-option="filterSelectOption"
                  @change="value => updateFormFieldConfigName(value, 'fileNameFieldName')"
                ></a-select>
              </a-form-model-item>
              <a-form-model-item label="文档状态字段">
                <a-select
                  v-model="formData.configuration.fileStatusField"
                  showSearch
                  allow-clear
                  :options="formFieldOptions"
                  :filter-option="filterSelectOption"
                  @change="value => updateFormFieldConfigName(value, 'fileStatusFieldName')"
                ></a-select>
              </a-form-model-item>
              <a-form-model-item label="阅读人员字段">
                <a-select
                  v-model="formData.configuration.readFileField"
                  showSearch
                  allow-clear
                  :options="formFieldOptions"
                  :filter-option="filterSelectOption"
                  @change="value => updateFormFieldConfigName(value, 'readFileFieldName')"
                ></a-select>
              </a-form-model-item>
              <a-form-model-item label="编辑人员字段">
                <a-select
                  v-model="formData.configuration.editFileField"
                  showSearch
                  allow-clear
                  :options="formFieldOptions"
                  :filter-option="filterSelectOption"
                  @change="value => updateFormFieldConfigName(value, 'editFileFieldName')"
                ></a-select>
              </a-form-model-item>
              <!-- 显示表单、打印模板由表单设置处理 -->
              <!-- <a-form-model-item label="显示表单">
                <a-select
                  v-model="formData.configuration.displayFormUuid"
                  showSearch
                  allow-clear
                  :options="displayFormOptions"
                  :filter-option="filterSelectOption"
                  @search="onDisplayFormSearch"
                ></a-select>
              </a-form-model-item>
              <a-form-model-item label="打印模板">dmsFolderConfigurationFacadeService.getPrintTemplateTreeByUser</a-form-model-item> -->
            </template>
            <a-form-model-item label="全文检索">
              <a-switch v-model="formData.configuration.enabledFulltextIndex" checked-children="启用" un-checked-children="禁用" />
            </a-form-model-item>
          </template>
        </a-tab-pane>
        <a-tab-pane key="privilegeInfo" tab="权限配置">
          <template v-if="formData.configuration">
            <a-form-model-item label="管理者">
              <a-row>
                <a-col span="22">
                  <OrgSelect
                    v-model="formData.configuration.administrator"
                    :orgType="['MyOrg', 'MySystemRole']"
                    ref="orgSelect"
                    :orgTypeExtensions="[{ label: '角色', value: 'MySystemRole' }]"
                    :params="orgSelectParams"
                  ></OrgSelect>
                </a-col>
                <a-col span="2">
                  &nbsp;
                  <Icon type="iconfont icon-ptkj-shezhi" @click="e => editRoleAction('administrator', '管理者')"></Icon>
                </a-col>
              </a-row>
            </a-form-model-item>
            <a-form-model-item label="编辑者">
              <a-row>
                <a-col span="22">
                  <OrgSelect
                    v-model="formData.configuration.editor"
                    :orgType="['MyOrg', 'MySystemRole']"
                    ref="orgSelect"
                    :orgTypeExtensions="[{ label: '角色', value: 'MySystemRole' }]"
                    :params="orgSelectParams"
                  ></OrgSelect>
                </a-col>
                <a-col span="2">
                  &nbsp;
                  <Icon type="iconfont icon-ptkj-shezhi" @click="e => editRoleAction('editor', '编辑者')"></Icon>
                </a-col>
              </a-row>
            </a-form-model-item>
            <a-form-model-item label="阅读者">
              <a-row>
                <a-col span="22">
                  <OrgSelect
                    v-model="formData.configuration.reader"
                    :orgType="['MyOrg', 'MySystemRole']"
                    ref="orgSelect"
                    :orgTypeExtensions="[{ label: '角色', value: 'MySystemRole' }]"
                    :params="orgSelectParams"
                  ></OrgSelect>
                </a-col>
                <a-col span="2">
                  &nbsp;
                  <Icon type="iconfont icon-ptkj-shezhi" @click="e => editRoleAction('reader', '阅读者')"></Icon>
                </a-col>
              </a-row>
            </a-form-model-item>
          </template>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
    <a-modal
      :title="'文件操作权限——' + editRoleName"
      width="700px"
      :visible="roleActionModalVisible"
      @ok="onEditRoleActionOk"
      @cancel="roleActionModalVisible = false"
      class="pt-modal"
    >
      <a-checkbox-group v-model="roleActions" style="width: 100%">
        <template v-for="(item, index) in actionOptions">
          <a-row v-if="index % 2 == 0" :key="index" style="margin-bottom: 8px">
            <a-col span="12">
              <a-checkbox :value="actionOptions[index].value">{{ actionOptions[index].label }}</a-checkbox>
            </a-col>
            <a-col span="12">
              <a-checkbox v-if="actionOptions[index + 1]" :value="actionOptions[index + 1].value">
                {{ actionOptions[index + 1].label }}
              </a-checkbox>
            </a-col>
          </a-row>
        </template>
      </a-checkbox-group>
      <template slot="footer">
        <a-button key="back" @click="roleActionModalVisible = false">取消</a-button>
        <a-button key="restore" @click="onRestoreRoleAction">恢复默认</a-button>
        <a-button key="submit" type="primary" @click="onEditRoleActionOk">确定</a-button>
      </template>
    </a-modal>
  </a-skeleton>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import {
  DEFAULT_ADMIN_ACTIONS,
  DEFAULT_EDITOR_ACTIONS,
  DEFAULT_READER_ACTIONS,
  FILE_ACTION_OPTIONS
} from '@pageAssembly/app/web/widget/widget-file-manager/constant.js';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  components: {
    OrgSelect,
    WI18nInput
  },
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event || {};
    let formData = $event.meta || {};
    if (!formData.uuid) {
      formData = {
        configuration: {
          dataType: 'FILE',
          isInheritFolderRole: 0,
          administratorActions: [...DEFAULT_ADMIN_ACTIONS],
          editorActions: [...DEFAULT_EDITOR_ACTIONS],
          readerActions: [...DEFAULT_READER_ACTIONS]
        },
        type: 0
      };
    }
    return {
      loading: formData.uuid != undefined,
      uuid: formData.uuid,
      formData,
      rules: {
        name: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        code: { required: true, message: '不能为空', trigger: ['blur', 'change'] }
      },
      dataTypeOptions: [
        {
          label: '文件实体',
          value: 'FILE'
        },
        {
          label: '动态表单',
          value: 'DYFORM'
        },
        {
          label: '混合模式',
          value: 'MIXTURE'
        }
      ],
      formOptions: [],
      formFieldOptions: [],
      displayFormOptions: [],
      orgSelectParams: {
        system: this._$SYSTEM_ID
      },
      actionOptions: FILE_ACTION_OPTIONS,
      roleActions: [],
      editRoleName: null,
      roleActionModalVisible: false
    };
  },
  computed: {
    dyformConfigurable() {
      let dataType = this.formData && this.formData.configuration.dataType;
      return ['DYFORM', 'MIXTURE'].includes(dataType);
    }
  },
  created() {
    const _this = this;
    if (_this.uuid) {
      _this.loadFileLibrary(_this.uuid);
    }
    _this.loadFormOptions();
    _this.loadDisplayFormOptions();
  },
  methods: {
    filterSelectOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    },
    loadFileLibrary(uuid) {
      const _this = this;
      _this.loading = true;
      $axios
        .post('/json/data/services', {
          serviceName: 'dmsFolderMgr',
          methodName: 'getBean',
          args: JSON.stringify([uuid])
        })
        .then(({ data: result }) => {
          if (result.data) {
            _this.formData = result.data;
            if (this.formData.i18ns) {
              let i18n = {};
              for (let item of this.formData.i18ns) {
                if (i18n[item.locale] == undefined) {
                  i18n[item.locale] = {};
                }
                i18n[item.locale][item.code] = item.content;
              }
              this.formData.i18n = i18n;
            }
            if (_this.formData.configuration && _this.formData.configuration.formUuid) {
              _this.onFormChange(false);
            }
          } else {
            _this.$message.error(result.msg || '服务异常！');
          }
        })
        .catch(({ response }) => {
          _this.$message.error((response.data && response.data.msg) || '服务异常！');
        })
        .finally(() => {
          _this.loading = false;
        });
    },
    loadFormOptions(searchValue = '') {
      const _this = this;
      $axios
        .post('/common/select2/query', {
          serviceName: 'dyFormFacade',
          queryMethod: 'queryAllPforms',
          searchValue,
          pageSize: 20,
          includeSuperAdmin: true
        })
        .then(({ data: result }) => {
          if (result.results) {
            _this.formOptions = result.results.map(item => ({ label: item.text, value: item.id }));
            // 选择的表单选项
            if (
              !searchValue &&
              _this.formData.configuration &&
              _this.formData.configuration.formUuid &&
              _this.formOptions.find(item => item.value == _this.formData.configuration.formUuid) == null
            ) {
              _this.formOptions = [{ label: _this.formData.configuration.formName, value: _this.formData.configuration.formUuid }].concat(
                _this.formOptions
              );
            }
          }
        });
    },
    onFormSearch(searchValue) {
      this.loadFormOptions(searchValue);
    },
    onFormChange(value) {
      const _this = this;
      let formUuid = _this.formData.configuration.formUuid;
      if (value) {
        let formOption = _this.formOptions.find(item => item.value == formUuid);
        _this.formData.configuration.formName = (formOption && formOption.label) || '';
      }

      if (!formUuid) {
        return;
      }

      _this.loadFormDefinition(formUuid);
    },
    updateFormFieldConfigName(field, nameProp) {
      const _this = this;
      let fieldOption = _this.formFieldOptions.find(item => item.value == field);
      _this.formData.configuration[nameProp] = (fieldOption && fieldOption.label) || '';
    },
    loadDisplayFormOptions(searchValue = '') {
      const _this = this;
      $axios
        .post('/common/select2/query', {
          serviceName: 'dyFormFacade',
          queryMethod: 'queryAllVforms',
          searchValue,
          pageSize: 20,
          includeSuperAdmin: true
        })
        .then(({ data: result }) => {
          if (result.results) {
            _this.formOptions = result.results.map(item => ({ label: item.text, value: item.id }));
          }
        });
    },
    onDisplayFormSearch(searchValue) {
      this.loadDisplayFormOptions(searchValue);
    },
    loadFormDefinition(formUuid) {
      const _this = this;
      $axios
        .post(`/proxy/api/dyform/definition/getFormDefinitionByUuid?formUuid=${formUuid}`, {})
        .then(({ data: result }) => {
          if (result.definitionJson) {
            let formDefinition = JSON.parse(result.definitionJson);
            let fields = formDefinition.fields || {};
            let fieldOptions = [];
            // 字段
            for (let fieldName in fields) {
              let field = fields[fieldName];
              fieldOptions.push({ value: field.name, label: field.displayName });
            }
            _this.formFieldOptions = fieldOptions;
          }
        })
        .catch(error => {});
    },
    editRoleAction(roleType, roleName) {
      const _this = this;
      _this.editRoleType = roleType;
      _this.editRoleName = roleName;
      let roleActions = _this.formData.configuration[roleType + 'Actions'] || [];
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
      _this.$message.info('已恢复！');
    },
    onEditRoleActionOk() {
      const _this = this;
      _this.formData.configuration[_this.editRoleType + 'Actions'] = _this.roleActions;
      _this.roleActionModalVisible = false;
    },
    save($evt) {
      const _this = this;
      if (_this.loading) {
        return;
      }
      _this.loading = true;
      _this.$loading();
      let item = this.formData;
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
            .post('/json/data/services', {
              serviceName: 'dmsFolderMgr',
              methodName: 'saveBean',
              args: JSON.stringify([_this.formData])
            })
            .then(({ data: result }) => {
              if (result.code == 0) {
                _this.$message.success('保存成功！');
                $evt.$evtWidget.closeModal && $evt.$evtWidget.closeModal();
                _this.$event && _this.$event.$evtWidget && _this.$event.$evtWidget.refetch && _this.$event.$evtWidget.refetch(true);
              } else {
                _this.$message.error(result.msg || '服务异常！');
              }
              setTimeout(() => {
                _this.loading = false;
                _this.$loading(false);
              }, 200);
            })
            .catch(({ response }) => {
              setTimeout(() => {
                _this.loading = false;
                _this.$loading(false);
              }, 200);
              _this.$message.error((response.data && response.data.msg) || '服务异常！');
            });
        } else {
          _this.loading = false;
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
