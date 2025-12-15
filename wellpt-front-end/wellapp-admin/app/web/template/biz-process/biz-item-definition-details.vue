<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="formData" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }" :rules="rules" ref="form">
      <a-form-model-item label="名称" prop="name">
        <a-input v-model="formData.name" />
      </a-form-model-item>
      <a-form-model-item label="ID" prop="id">
        <a-input v-model="formData.id" :disabled="uuid != null" />
      </a-form-model-item>
      <a-form-model-item label="编码" prop="code">
        <a-input v-model="formData.code" />
      </a-form-model-item>
      <a-form-model-item prop="businessId">
        <template slot="label">
          <a-space>
            所属业务
            <a-popover>
              <template slot="content">
                用于关联相同业务的业务流程定义，关联后当前业务
                <br />
                事项定义表单的数据才能在对应业务流程定义中引用
              </template>
              <a-icon type="info-circle" />
            </a-popover>
          </a-space>
        </template>
        <a-tree-select
          v-model="formData.businessId"
          style="width: 100%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="businessTreeData"
          placeholder="请选择"
          tree-default-expand-all
          :disabled="!businessIdEditable"
        ></a-tree-select>
      </a-form-model-item>
      <a-form-model-item prop="formId">
        <template slot="label">
          <a-space>
            源事项定义表单
            <a-popover>
              <template slot="content">将从该表单的存储中读取事项定义</template>
              <a-icon type="info-circle" />
            </a-popover>
          </a-space>
        </template>
        <a-select
          v-model="formData.formId"
          show-search
          style="width: calc(100% - 150px)"
          :filter-option="filterOption"
          @change="handleFormChange"
        >
          <a-select-option v-for="d in formOptions" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
        <a-button type="link" style="width: 150px" @click="addItemDyform">新增源事项定义表单</a-button>
      </a-form-model-item>
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="基本信息映射">
          <a-form-model-item label="事项名称字段" prop="itemNameField">
            <a-select v-model="formData.itemNameField" show-search :filter-option="filterOption" :options="formFieldOptions"></a-select>
          </a-form-model-item>
          <a-form-model-item label="事项编码字段" prop="itemCodeField">
            <a-select v-model="formData.itemCodeField" show-search :filter-option="filterOption" :options="formFieldOptions"></a-select>
          </a-form-model-item>
          <a-form-model-item label="事项所有者字段" prop="itemOwnerField">
            <a-select v-model="formData.itemOwnerField" show-search :filter-option="filterOption" :options="formFieldOptions"></a-select>
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane v-if="formData.type == '20'" key="2" tab="包含事项从表映射" force-render>
          <a-form-model-item label="包含事项从表" prop="includeItemSubformId">
            <a-select
              v-model="formData.includeItemSubformId"
              show-search
              :filter-option="filterOption"
              :options="subformOptions"
              @change="handleIncludeItemSubformChange"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="包含事项名称字段" prop="includeItemNameField">
            <a-select
              v-model="formData.includeItemNameField"
              show-search
              :filter-option="filterOption"
              :options="includeItemFieldOptions"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="包含事项编码字段" prop="includeItemCodeField">
            <a-select
              v-model="formData.includeItemCodeField"
              show-search
              :filter-option="filterOption"
              :options="includeItemFieldOptions"
            ></a-select>
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="3" tab="时限配置从表映射" force-render>
          <a-form-model-item label="时限配置从表" prop="timeLimitSubformId">
            <a-select
              v-model="formData.timeLimitSubformId"
              show-search
              :filter-option="filterOption"
              :options="subformOptions"
              @change="handleTimeLimitSubformChange"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="时限字段" prop="timeLimitField">
            <a-select
              v-model="formData.timeLimitField"
              show-search
              :filter-option="filterOption"
              :options="timeLimitFieldOptions"
            ></a-select>
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="4" tab="材料配置从表映射" force-render>
          <a-form-model-item label="材料配置从表" prop="materialSubformId">
            <a-select
              v-model="formData.materialSubformId"
              show-search
              :filter-option="filterOption"
              :options="subformOptions"
              @change="handleMaterialSubformChange"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="材料名称字段" prop="materialNameField">
            <a-select
              v-model="formData.materialNameField"
              show-search
              :filter-option="filterOption"
              :options="materialFieldOptions"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="材料编码字段" prop="materialCodeField">
            <a-select
              v-model="formData.materialCodeField"
              show-search
              :filter-option="filterOption"
              :options="materialFieldOptions"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="材料是否必填字段" prop="materialRequiredField">
            <a-select
              v-model="formData.materialRequiredField"
              show-search
              :filter-option="filterOption"
              :options="materialFieldOptions"
            ></a-select>
          </a-form-model-item>
        </a-tab-pane>
      </a-tabs>
      <a-form-model-item label="备注">
        <a-input v-model="formData.remark" type="textarea" />
      </a-form-model-item>
    </a-form-model>
  </a-skeleton>
</template>

<script>
export default {
  props: {
    businessIdEditable: {
      type: Boolean,
      default: true
    }
  },
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event || {};
    let eventParams = $event.eventParams || {};
    let type = eventParams.type || '10';
    let businessId = eventParams.businessId;
    let uuid = $event && $event.meta != undefined ? $event.meta.uuid : undefined;
    return {
      loading: $event && $event.meta && $event.meta.uuid != undefined,
      uuid,
      formData: {
        type,
        businessId,
        id: (!uuid && 'BIZ_ITEM_' + new Date().format('yyyyMMDDHHmmss')) || undefined
      },
      rules: {
        name: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        id: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        businessId: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        formId: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        itemNameField: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        itemCodeField: { required: true, message: '不能为空', trigger: ['blur', 'change'] }
      },
      businessTreeData: [],
      formOptions: [],
      formFieldOptions: [],
      subformOptions: [],
      includeItemFieldOptions: [],
      mutexItemFieldOptions: [],
      relateItemFieldOptions: [],
      timeLimitFieldOptions: [],
      materialFieldOptions: []
    };
  },
  beforeMount() {
    if (this.uuid) {
      this.getItemDefinitionDetails(this.uuid);
    }
  },
  mounted() {
    this.loadBusinessTreeData();
    this.handleFormSearch();
  },
  methods: {
    refresh() {
      if (this.formData.formId) {
        this.handleFormChange(this.formData.formId);
      }
    },
    loadBusinessTreeData() {
      let _this = this;
      let convertATreeSelectData = function (nodes) {
        nodes.forEach(node => {
          node.label = node.name;
          node.value = (node.data && node.data.id) || node.id;
          node.selectable = node.type == 'business';
          if (node.children) {
            convertATreeSelectData(node.children);
          }
        });
        return nodes;
      };
      this.$axios
        .post('/json/data/services', {
          serviceName: 'bizBusinessFacadeService',
          methodName: 'getBusinessTree'
        })
        .then(({ data }) => {
          if (data.data) {
            _this.businessTreeData = convertATreeSelectData([data.data]);
          }
        });
    },
    handleFormSearch(value = '') {
      let _this = this;
      _this.$axios
        .post('/common/select2/query', {
          serviceName: 'bizItemDefinitionFacadeService',
          queryMethod: 'listDyFormDefinitionSelectData',
          searchValue: value,
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data }) => {
          if (data.results) {
            _this.formOptions = data.results;
          }
        });
    },
    handleFormChange(formId) {
      const _this = this;
      if (!formId) {
        _this.formFieldOptions = [];
        _this.subformOptions = [];
        return;
      }

      $axios.get(`/proxy/api/biz/item/definition/getFormDefinitionByFormId/${formId}`).then(({ data }) => {
        if (data.data) {
          let formDefinition = JSON.parse(data.data);

          // 表单字段
          _this.formFieldOptions = _this.getFieldSelectData(formDefinition);

          // 表单从表
          let subformMap = new Map();
          let subformSelectData = [];
          let subforms = formDefinition.subforms || {};
          for (let key in subforms) {
            let subform = subforms[key];
            subformSelectData.push({ value: subform.outerId, label: subform.displayName || subform.name });
            subformMap.set(subform.outerId, subform);
          }
          _this.subformOptions = subformSelectData;
          _this.subformMap = subformMap;

          // 触发从表字段变更事件
          if (this.formData.includeItemSubformId) {
            this.handleIncludeItemSubformChange(this.formData.includeItemSubformId);
          }
          if (this.formData.mutexItemSubformId) {
            this.handleMutexItemSubformChange(this.formData.mutexItemSubformId);
          }
          if (this.formData.relateItemSubformId) {
            this.handleRelateItemSubformChange(this.formData.relateItemSubformId);
          }
          if (this.formData.timeLimitSubformId) {
            this.handleTimeLimitSubformChange(this.formData.timeLimitSubformId);
          }
          if (this.formData.materialSubformId) {
            this.handleMaterialSubformChange(this.formData.materialSubformId);
          }
        }
      });
    },
    addItemDyform() {
      let _this = this;
      _this.pageContext.handleCrossTabEvent(`dyform:design:create`, formDefinition => {
        if (!_this.formData.formId && formDefinition) {
          _this.formOptions.push({ id: formDefinition.id, text: formDefinition.name });
          _this.formData.formId = formDefinition.id;
          _this.handleFormChange(_this.formData.formId);
        }
      });
      window.open(`/dyform-designer/index`, '_blank');
    },
    getFieldSelectData(formDefinition) {
      let fieldSelectData = [];
      if (!formDefinition) {
        return fieldSelectData;
      }
      let fields = formDefinition.fields || {};
      for (let key in fields) {
        let field = fields[key];
        fieldSelectData.push({ value: field.name, label: field.displayName });
      }
      return fieldSelectData;
    },
    filterOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    },
    handleIncludeItemSubformChange(subformId) {
      const _this = this;
      if (!subformId) {
        _this.includeItemFieldOptions = [];
        return;
      }

      let subform = _this.subformMap.get(subformId);
      _this.includeItemFieldOptions = _this.getFieldSelectData(subform);
    },
    handleMutexItemSubformChange(subformId) {
      const _this = this;
      if (!subformId) {
        _this.mutexItemFieldOptions = [];
        return;
      }

      let subform = _this.subformMap.get(subformId);
      _this.mutexItemFieldOptions = _this.getFieldSelectData(subform);
    },
    handleRelateItemSubformChange(subformId) {
      const _this = this;
      if (!subformId) {
        _this.relateItemFieldOptions = [];
        return;
      }

      let subform = _this.subformMap.get(subformId);
      _this.relateItemFieldOptions = _this.getFieldSelectData(subform);
    },
    handleTimeLimitSubformChange(subformId) {
      const _this = this;
      if (!subformId) {
        _this.timeLimitFieldOptions = [];
        return;
      }

      let subform = _this.subformMap.get(subformId);
      _this.timeLimitFieldOptions = _this.getFieldSelectData(subform);
    },
    handleMaterialSubformChange(subformId) {
      const _this = this;
      if (!subformId) {
        _this.materialFieldOptions = [];
        return;
      }

      let subform = _this.subformMap.get(subformId);
      _this.materialFieldOptions = _this.getFieldSelectData(subform);
    },
    getItemDefinitionDetails() {
      $axios.get(`/proxy/api/biz/item/definition/get?uuid=${this.uuid}`).then(({ data }) => {
        if (data.data) {
          this.formData = data.data;
          this.refresh();
        }
        this.loading = false;
      });
    },
    save(event) {
      // 保存数据
      let _this = this;
      _this.$refs.form.validate(valid => {
        if (valid) {
          $axios
            .post('/proxy/api/biz/item/definition/save', _this.formData)
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('保存成功');
                // 刷新表格
                _this.pageContext.emitEvent('CGpnfIUpijyboXQOviBohcvtEQWdPbHU:refetch');
                event.$evtWidget.closeModal();
              }
            })
            .catch(error => {
              if (error.response && error.response.data && error.response.data.msg) {
                _this.$message.error(error.response.data.msg);
              } else {
                _this.$message.error('服务异常');
              }
            });
        }
      });
    }
  },
  META: {
    method: {
      save: '保存事项定义'
    }
  }
};
</script>

<style></style>
