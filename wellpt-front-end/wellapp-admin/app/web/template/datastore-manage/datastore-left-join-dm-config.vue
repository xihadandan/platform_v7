<template>
  <div>
    <a-form-model-item label=" " :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-switch
        v-model="leftJoinConfig.enabled"
        checked-children="开启"
        un-checked-children="关闭"
        @change="onEnabledLeftJoinConfigChange"
      ></a-switch>
    </a-form-model-item>
    <template v-if="leftJoinConfig.enabled">
      <a-form-model-item label="数据模型" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-select
          v-model="leftJoinConfig.dmId"
          showSearch
          allow-clear
          :options="dataModelOptions"
          :filter-option="filterSelectOption"
          @change="onDataModelChange"
          :getPopupContainer="getPopupContainerNearestPs()"
          :dropdownClassName="getDropdownClassName()"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item label="查询列" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-select
          mode="multiple"
          v-model="leftJoinConfig.selection"
          showSearch
          allow-clear
          :options="dataModelColumnOptions"
          :filter-option="filterSelectOption"
          @change="onSelectionChange"
          :getPopupContainer="getPopupContainerNearestPs()"
          :dropdownClassName="getDropdownClassName()"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item label="连接条件" :label-col="labelCol" :wrapper-col="wrapperCol">
        <WhereConditionSet
          :columns="onColumns"
          :condition="leftJoinConfig.onConditions"
          title=""
          ref="onWhere"
          :supportVar="false"
          @change="e => onConditionChange(e, 'onConditionSql')"
        />
      </a-form-model-item>
    </template>
  </div>
</template>

<script>
import { getPopupContainerNearestPs, getDropdownClassName } from '@framework/vue/utils/function.js';
import WhereConditionSet from '@pageAssembly/app/web/page/module-center/component/data-model/view-object/design-component/where-condition-set.vue';
export default {
  props: {
    datastore: Object,
    item: Object,
    params: Object
  },
  components: { WhereConditionSet },
  data() {
    let leftJoinConfig = this.initLeftJoinConfig(this.params);
    return {
      leftJoinConfig,
      dataModelOptions: [],
      dataModelColumnOptions: [],
      dataModelColumns: [],
      labelCol: { span: 4, style: 'text-align:right;padding-right:4px;' },
      wrapperCol: { span: 19 }
    };
  },
  computed: {
    onColumns() {
      let _this = this;
      let columnDefinitionBeans = _this.datastore.columnDefinitionBeans || [];
      let columns = [];
      columnDefinitionBeans.forEach(item => {
        columns.push({
          title: item.title,
          column: item.columnIndex,
          location: item.columnName,
          dataType: _this.convertDataTypeOfDataStore(item.dataType)
        });
      });

      let dataModelColumns = _this.dataModelColumns || [];
      dataModelColumns.forEach(item => {
        columns.push({
          title: _this.leftJoinConfig.dmName + '.' + item.title,
          column: item.column,
          location: 'dm.' + (item.alias || item.column),
          dataType: item.dataType
        });
      });
      return columns;
    }
  },
  watch: {
    params: {
      deep: false,
      handler(newVal, oldVal) {
        this.leftJoinConfig = this.initLeftJoinConfig(newVal);
      }
    }
  },
  created() {
    const _this = this;
    let leftJoinConfig = _this.leftJoinConfig;

    if (leftJoinConfig.enabled) {
      _this.loadDataModels().then(() => {
        if (leftJoinConfig.dmId) {
          _this.onDataModelChange();
        }
      });
    }
  },
  methods: {
    getPopupContainerNearestPs,
    getDropdownClassName,
    initLeftJoinConfig(params) {
      let leftJoinConfig = params.leftJoinConfig || {
        enabled: false,
        dmName: '',
        dmId: '',
        tableName: '',
        tableAlias: 'dm',
        tableSql: null,
        selection: [],
        selectionColumns: [],
        onConditions: [],
        onConditionSql: '',
        sqlParameter: undefined
      };
      return leftJoinConfig;
    },
    convertDataTypeOfDataStore(dataType) {
      let retDataType = dataType;
      switch (dataType) {
        case 'String':
          retDataType = 'varchar';
          break;
        case 'Date':
          retDataType = 'timestamp';
          break;
        case 'Integer':
        case 'Long':
        case 'Double':
        case 'Float':
        case 'Short':
          retDataType = 'number';
          break;
      }
      return retDataType;
    },
    convertDataTypeOfDataModel(dataType) {
      let retDataType = dataType;
      switch (dataType) {
        case 'varchar':
          retDataType = 'String';
          break;
        case 'timestamp':
          retDataType = 'Date';
          break;
        case 'number':
          retDataType = 'Double';
          break;
      }
      return retDataType;
    },
    filterSelectOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    },
    onEnabledLeftJoinConfigChange() {
      const _this = this;
      if (_this.dataModelLoaded) {
        return;
      }
      _this.loadDataModels();
    },
    loadDataModels() {
      const _this = this;
      _this.dataModelLoaded = true;
      _this.dataModelMap = {};
      return $axios
        .post(`/proxy/api/dm/getDataModelsByType`, {
          type: ['TABLE', 'VIEW'],
          module: this.datastore.moduleId != undefined ? [this.datastore.moduleId] : undefined
        })
        .then(({ data }) => {
          if (data.code == 0) {
            let models = data.data;
            if (models) {
              for (let m of models) {
                if (_this.dataModelMap[m.id]) {
                  continue;
                }
                _this.dataModelOptions.push({ label: m.name, value: m.id });
                _this.dataModelMap[m.id] = m;
              }
            }
          }
        });
    },
    onDataModelChange() {
      const _this = this;
      let dmId = _this.leftJoinConfig.dmId;
      let dataModel = _this.dataModelMap[dmId];
      if (!dataModel) {
        console.warn('data model is null');
        return;
      }
      _this.leftJoinConfig.dmName = dataModel.name;
      _this.leftJoinConfig.dmType = dataModel.type;
      _this.leftJoinConfig.tableName = 'UF_' + dmId;
      // 获取数据模型
      $axios
        .get(`/proxy/api/dm/getDetails`, { params: { id: dmId } })
        .then(({ data: result }) => {
          if (result.data) {
            _this.leftJoinConfig.tableSql = result.data.sql;
            if (result.data.columnJson) {
              let dataModelColumns = JSON.parse(result.data.columnJson);
              dataModelColumns = dataModelColumns.filter(item => !item.hasOwnProperty('return') || item.return);
              // _this.dataModelColumns = dataModelColumns;
              _this.dataModelColumnOptions = dataModelColumns.map(item => ({ label: item.title, value: item.alias || item.column }));
              // dataModelColumns.forEach(item => (item.location = 'dm.' + item.column));
              _this.dataModelColumns = dataModelColumns;
            }
          } else {
            _this.leftJoinConfig.tableSql = null;
            _this.dataModelColumns = [];
            _this.dataModelColumnOptions = [];
          }
        })
        .catch(() => {
          _this.leftJoinConfig.tableSql = null;
          _this.dataModelColumns = [];
          _this.dataModelColumnOptions = [];
        });
    },
    onSelectionChange() {
      const _this = this;
      let selection = _this.leftJoinConfig.selection || [];
      let selectionColumns = _this.dataModelColumns.filter(item => selection.includes(item.alias) || selection.includes(item.column));
      _this.leftJoinConfig.selectionColumns = selectionColumns.map(item => {
        return {
          columnIndex: 'DM_' + (item.alias || item.column), // 列别名添加DM_前缀
          columnName: 'dm.' + (item.alias || item.column),
          dataType: _this.convertDataTypeOfDataModel(item.dataType),
          columnType: item.dataType && item.dataType.toUpperCase(),
          title: item.title
        };
      });
    },
    onConditionChange({ sql, sqlParameter }, key) {
      if (sql != undefined && sql.__proto__.name == 'Error') {
        this.hasError;
        return;
      }
      this.leftJoinConfig[key] = key === 'onConditionSql' ? 'ON ' + sql : sql;
      this.leftJoinConfig.sqlParameter = sqlParameter;
    },
    collectData() {
      const _this = this;
      let leftJoinConfig = _this.leftJoinConfig;

      if (!leftJoinConfig.enabled) {
        return Promise.resolve({ leftJoinConfig });
      }

      if (!leftJoinConfig.dmId) {
        _this.$message.error('请选择数据模型！');
        return Promise.reject({ leftJoinConfig });
      }

      if (!leftJoinConfig.selection.length) {
        _this.$message.error('查询列不能为空！');
        return Promise.reject({ leftJoinConfig });
      } else if (!leftJoinConfig.onConditions.length) {
        _this.$message.error('连接条件不能为空！');
        return Promise.reject({ leftJoinConfig });
      } else if (this.hasError) {
        _this.$message.error('连接条件配置错误！');
        return Promise.reject({ leftJoinConfig });
      }

      try {
        let on = _this.$refs.onWhere.collect();
        if (on.sql) {
          leftJoinConfig.onConditionSql = 'ON ' + on.sql;
        } else {
          this.$message.error('请添加连接条件');
          return Promise.reject({ leftJoinConfig });
        }
      } catch (error) {
        _this.$message.error('连接条件配置错误！');
        return Promise.reject({ leftJoinConfig });
      }

      return Promise.resolve({ leftJoinConfig });
    }
  }
};
</script>

<style></style>
