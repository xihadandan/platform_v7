<template>
  <a-form-model
    class="basic-info"
    :model="situationConfig"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'left' } }"
    :colon="false"
  >
    <a-form-model-item label="名称" prop="situationName">
      <a-input v-model="situationConfig.situationName" />
    </a-form-model-item>
    <a-form-model-item label="办理时限" prop="itemWorkday">
      <a-select
        v-model="situationConfig.itemWorkday"
        show-search
        style="width: 100%"
        :filter-option="false"
        :options="timeLimitOptions"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="事项材料" prop="itemMaterialCodes">
      <a-select
        v-model="itemMaterialCodes"
        mode="multiple"
        show-search
        style="width: 100%"
        :filter-option="false"
        :options="materialOptions"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="条件设置">
      <a-input v-show="false" v-model="situationConfig.conditionName" />
      <a-form-model-item label="条件类型">
        <a-radio-group v-model="conditionConfig.type" default-value="3">
          <a-radio value="3">通过表单字段值比较</a-radio>
          <a-radio value="9">逻辑条件</a-radio>
        </a-radio-group>
      </a-form-model-item>
    </a-form-model-item>
    <a-row v-show="conditionConfig.type == '3'">
      <a-col span="2">
        <a-select v-model="conditionConfig.leftBracket" show-search style="width: 100%" :filter-option="false">
          <a-select-option value="">&nbsp;</a-select-option>
          <a-select-option value="(">(</a-select-option>
        </a-select>
      </a-col>
      <a-col span="1"></a-col>
      <a-col span="6">
        <a-select
          v-model="conditionConfig.formUuid"
          show-search
          style="width: 90%"
          :filter-option="filterSelectOption"
          @change="onFormChange"
        >
          <a-select-option v-for="d in formOptions" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
      </a-col>
      <a-col span="4">
        <a-select
          v-model="conditionConfig.fieldName"
          show-search
          style="width: 90%"
          :filter-option="filterSelectOption"
          :options="formFieldOptions"
          @change="onFieldChange"
        ></a-select>
      </a-col>
      <a-col span="4">
        <a-select
          v-model="conditionConfig.operator"
          show-search
          style="width: 90%"
          :filter-option="filterSelectOption"
          :options="operatorOptions"
          @change="onOperatorChange"
        ></a-select>
      </a-col>
      <a-col span="4">
        <a-input v-model="conditionConfig.value" />
      </a-col>
      <a-col span="1"></a-col>
      <a-col span="2">
        <a-select v-model="conditionConfig.rightBracket" show-search style="width: 100%" :filter-option="false">
          <a-select-option value="">&nbsp;</a-select-option>
          <a-select-option value=")">)</a-select-option>
        </a-select>
      </a-col>
    </a-row>
    <a-form-model-item v-show="conditionConfig.type == '9'" label="逻辑条件">
      <a-radio-group v-model="conditionConfig.connector" default-value="&">
        <a-radio value="&">并且</a-radio>
        <a-radio value="|">或者</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <p />
    <a-row>
      <a-col>
        <a-button-group>
          <a-button type="primary" @click="addConditionConfig">添加</a-button>
          <a-button @click="deleteConditionConfig">删除</a-button>
        </a-button-group>
        <a-list bordered :data-source="situationConfig.conditionConfigs">
          <a-list-item
            class="drag-btn-handler"
            :class="{ selected: item.id == selectedItem.id }"
            slot="renderItem"
            slot-scope="item"
            @click="onConditionConfigClick(item)"
          >
            {{ getItemDisplayName(item) }}
          </a-list-item>
        </a-list>
      </a-col>
    </a-row>
    <p />
    <a-form-model-item label="备注">
      <a-textarea v-model="situationConfig.remark" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { generateId, deepClone } from '../../designer/utils';
import draggable from '@framework/vue/designer/draggable';
export default {
  mixins: [draggable],
  props: {
    itemDefinition: Object,
    situationConfig: {
      type: Object,
      default() {
        return {
          id: generateId(),
          conditionConfigs: []
        };
      }
    }
  },
  inject: ['designer', 'filterSelectOption', 'getCacheData'],
  data() {
    let itemMaterialCodes = [];
    if (this.situationConfig.itemMaterialCodes) {
      itemMaterialCodes = this.situationConfig.itemMaterialCodes.split(';');
    }
    if (!this.situationConfig.conditionConfigs) {
      // this.situationConfig.conditionConfigs = [];
      this.$set(this.situationConfig, 'conditionConfigs', []);
    }
    return {
      timeLimitOptions: [],
      materialOptions: [],
      itemMaterialCodes,
      rules: {
        situationName: [{ required: true, message: '名称不能为空！', trigger: 'blur' }]
      },
      conditionConfig: {
        id: '',
        type: '3',
        connector: '&'
      },
      formOptions: [],
      formFieldOptions: [],
      operatorOptions: [
        {
          value: '>',
          label: '大于'
        },
        {
          value: '>=',
          label: '大于等于'
        },
        {
          value: '<',
          label: '小于'
        },
        {
          value: '<=',
          label: '小于等于'
        },
        {
          value: '==',
          label: '等于'
        },
        {
          value: '!=',
          label: '不等于'
        },
        {
          value: 'like',
          label: '包含'
        },
        {
          value: 'notlike',
          label: '不包含'
        }
      ],
      selectedItem: {}
    };
  },
  watch: {
    itemMaterialCodes: function (newVal) {
      this.situationConfig.itemMaterialCodes = newVal.join(';');
    }
  },
  created() {
    const _this = this;
    let itemDefId = _this.itemDefinition.itemDefId;
    let itemCode = _this.itemDefinition.itemCode;
    $axios
      .get(`/proxy/api/biz/item/definition/listTimeLimitAndMaterialByItemCode?id=${itemDefId}&itemCode=${itemCode}`)
      .then(({ data }) => {
        if (data.data) {
          let timeLimitOptions = [];
          let materialOptions = [];
          let materials = data.data.materials || [];
          let timeLimits = data.data.timeLimits || [];
          materials.forEach(material => {
            materialOptions.push({ value: material.materialCode, label: material.materialName });
          });
          timeLimits.forEach(timeLimit => {
            timeLimitOptions.push({
              value: timeLimit.timeLimit,
              label: _this.itemDefinition.timeLimitType == '1' ? `${timeLimit.timeLimit}工作日` : `${timeLimit.timeLimit}自然日`
            });
          });
          _this.materialOptions = materialOptions;
          _this.timeLimitOptions = timeLimitOptions;
        }
      });

    _this.handleFormSearch();
  },
  mounted() {
    this.tableDraggable(this.situationConfig.conditionConfigs, this.$el.querySelector('.ant-list-items'), '.drag-btn-handler');
  },
  methods: {
    getItemDisplayName(item) {
      let leftBracket = item.leftBracket || '';
      let formName = item.formName || '';
      let fieldDisplayName = item.fieldDisplayName || '';
      let operatorName = item.operatorName || '';
      let value = item.value || '';
      let rightBracket = item.rightBracket || '';
      if (item.type == '3') {
        return leftBracket + formName + ':' + fieldDisplayName + ' ' + operatorName + ' ' + value + rightBracket;
      } else {
        return item.connector == '&' ? '并且' : '或者';
      }
    },
    handleFormSearch(value = '') {
      let _this = this;
      _this
        .getCacheData('formOptions', (resolve, reject) => {
          _this.$axios
            .post('/common/select2/query', {
              serviceName: 'bizProcessDefinitionFacadeService',
              queryMethod: 'listDyFormDefinitionSelectData',
              searchValue: value,
              pageSize: 1000,
              pageNo: 1
            })
            .then(({ data }) => {
              if (data.results) {
                resolve(data.results);
                // _this.formOptions = data.results;
              }
            })
            .catch(err => reject(err));
        })
        .then(formOptions => {
          _this.formOptions = formOptions;
        });
    },
    onFormChange() {
      let _this = this;
      _this
        .getCacheData(`formDefinition_${_this.conditionConfig.formUuid}`, (resolve, reject) => {
          _this.$axios
            .get(`/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${_this.conditionConfig.formUuid}`)
            .then(({ data }) => {
              if (!data.data) {
                console.error('form definition is null', formUuid);
                reject({});
              } else {
                let formDefinition = JSON.parse(data.data);
                resolve(formDefinition);
              }
            })
            .catch(err => reject({}));
        })
        .then(formDefinition => {
          _this.conditionConfig.formName = formDefinition.name;

          let fieldSelectData = [{ value: 'uuid', label: 'UUID' }];
          let fields = formDefinition.fields || {};
          // 字段
          for (let fieldName in fields) {
            let field = fields[fieldName];
            fieldSelectData.push({ value: field.name, label: field.displayName });
          }
          _this.formFieldOptions = fieldSelectData;
        });
    },
    onFieldChange() {
      let fieldOption = this.formFieldOptions.find(item => item.value == this.conditionConfig.fieldName);
      this.conditionConfig.fieldDisplayName = fieldOption ? fieldOption.label : '';
    },
    onOperatorChange() {
      let operatorOption = this.operatorOptions.find(item => item.value == this.conditionConfig.operator);
      this.conditionConfig.operatorName = operatorOption ? operatorOption.label : '';
    },
    addConditionConfig() {
      let config = deepClone(this.conditionConfig);
      config.id = generateId();
      this.situationConfig.conditionConfigs.push(config);
    },
    deleteConditionConfig() {
      if (this.selectedItem) {
        let index = this.situationConfig.conditionConfigs.findIndex(item => item.id == this.selectedItem.id);
        if (index != -1) {
          this.situationConfig.conditionConfigs.splice(index, 1);
          this.selectedItem = {};
        }
      } else {
        this.$message.warning('请选择要删除的记录！');
      }
    },
    onConditionConfigClick(item) {
      this.selectedItem = item;
      this.conditionConfig = item;
      if (item.formUuid) {
        this.onFormChange();
      }
    },
    collect() {
      return this.$refs.basicForm
        .validate()
        .then(valid => {
          if (valid) {
            return this.situationConfig;
          }
          return null;
        })
        .catch(valid => {
          console.log('valid ', valid);
        });
    }
  }
};
</script>

<style lang="less" scoped>
.selected {
  background-color: #eee;
}
</style>
