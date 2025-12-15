<template>
  <div>
    <a-form-model-item label="选项来源">
      <a-select
        :options="optionSources"
        :style="{ width: '100%' }"
        v-model="options.type"
        allowClear
        @change="onSelectOptionSrcChange"
      ></a-select>
      <div v-show="options.type == 'selfDefine'">
        <a-button-group class="table-header-operation" size="small" style="margin-bottom: 10px">
          <a-button @click="addDefineOption" icon="plus">新增</a-button>
          <a-button @click="delDefineOption" icon="delete">删除</a-button>
          <a-button @click="moveDefineOption('forward')" icon="arrow-up">上移</a-button>
          <a-button @click="moveDefineOption" icon="arrow-down">下移</a-button>
        </a-button-group>
        <a-table
          rowKey="id"
          :row-selection="{ selectedRowKeys: selectedOptionRowKeys, onChange: selectConstantChange }"
          :pagination="false"
          size="small"
          :columns="[
            { title: '展示文本', dataIndex: 'label', width: 200, scopedSlots: { customRender: 'labelSlot' } },
            { title: '值', dataIndex: 'value', scopedSlots: { customRender: 'valueSlot' } }
          ]"
          :data-source="options.defineOptions"
        >
          <template slot="valueSlot" slot-scope="text, record">
            <a-input v-model="record.value" size="small" />
          </template>
          <template slot="labelSlot" slot-scope="text, record">
            <a-input v-model="record.label" size="small">
              <template slot="addonAfter">
                <WI18nInput
                  :widget="widget"
                  :designer="designer"
                  :code="column.id + '_' + record.id"
                  v-model="record.label"
                  :target="record"
                />
              </template>
            </a-input>
          </template>
        </a-table>
      </div>
    </a-form-model-item>

    <a-form-model-item label="数据字典" v-show="options.type == 'dataDictionary'">
      <a-tree-select
        showSearch
        allowClear
        v-model="options.dataDictionaryUuid"
        style="width: 100%"
        treeNodeFilterProp="title"
        :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
        :tree-data="dataDictionaryTreeData"
        :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
      ></a-tree-select>
    </a-form-model-item>
    <div v-if="options.type == 'dataSource'">
      <a-form-model-item label="数据源">
        <DataStoreSelectModal v-model="options.dataSourceId" :displayModal="true" @change="changeDataSourceId" />
      </a-form-model-item>
      <!-- <a-form-model-item label="默认条件">
        <a-textarea v-model="options.defaultCondition" />
      </a-form-model-item> -->
      <div v-if="dataSourceField">
        <template v-if="dataSourceField.length != undefined">
          <template v-for="(field, i) in dataSourceField">
            <a-form-model-item :label="field.label" :key="'dsField_' + i">
              <a-select
                v-model="options[field.value]"
                show-search
                style="width: 100%"
                :filter-option="filterOption"
                :options="columnIndexOptions"
              ></a-select>
            </a-form-model-item>
          </template>
        </template>
        <a-form-model-item :label="dataSourceField.label" v-else>
          <a-select
            v-model="options[dataSourceField.value]"
            show-search
            style="width: 100%"
            :filter-option="filterOption"
            :options="columnIndexOptions"
          ></a-select>
        </a-form-model-item>
      </div>
    </div>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId, swapArrayElements } from '@framework/vue/utils/util';
export default {
  name: 'selectSearchConfig',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    options: Object,
    dataSourceField: Object | Array,
    allowOptionSrc: Array,
    column: Object
  },
  data() {
    let optionSources = [];
    if (this.allowOptionSrc == undefined || this.allowOptionSrc.includes('selfDefine')) {
      optionSources.push({ label: '自定义', value: 'selfDefine' });
    }
    if (this.allowOptionSrc == undefined || this.allowOptionSrc.includes('dataDictionary')) {
      optionSources.push({ label: '数据字典', value: 'dataDictionary' });
    }
    if (this.allowOptionSrc == undefined || this.allowOptionSrc.includes('dataSource')) {
      optionSources.push({ label: '数据源', value: 'dataSource' });
    }
    return {
      selectedOptionRowKeys: [],
      selectedOptionRows: [],
      dataDictionaryTreeData: [],
      dataSourceOptions: [],
      columnIndexOptions: [],
      optionSources
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (this.dataSourceField) {
      if (Array.isArray(this.dataSourceField)) {
        for (let d of this.dataSourceField) {
          if (!this.options.hasOwnProperty(d.value)) {
            this.$set(this.options, d.value, undefined);
          }
        }
      } else {
        if (!this.options.hasOwnProperty(d.value)) {
          this.$set(this.options, this.dataSourceField.value, undefined);
        }
      }
    }
  },
  methods: {
    addDefineOption() {
      if (this.options.defineOptions == undefined) {
        this.$set(this.options, 'defineOptions', []);
      }
      this.options.defineOptions.push({
        id: generateId()
      });
    },

    selectConstantChange(selectedRowKeys, selectedRows) {
      this.selectedOptionRowKeys = selectedRowKeys;
      this.selectedOptionRows = selectedRows;
    },

    delDefineOption() {
      for (let i = 0, len = this.selectedOptionRowKeys.length; i < len; i++) {
        for (let j = 0, jlen = this.options.defineOptions.length; j < jlen; j++) {
          if (this.options.defineOptions[j].id == this.selectedOptionRowKeys[i]) {
            this.options.defineOptions.splice(j, 1);
            break;
          }
        }
      }
    },
    moveDefineOption(direction) {
      let ids = [];
      for (let i = 0, len = this.selectedOptionRows.length; i < len; i++) {
        ids.push(this.selectedOptionRows[i].id);
      }
      swapArrayElements(
        ids,
        this.options.defineOptions,
        function (a, b) {
          return a == b.id;
        },
        direction
      );
    },

    onSelectOptionSrcChange(val) {
      if (val == 'dataDictionary' && this.dataDictionaryTreeData.length == 0) {
        this.fetchDataDictionaryTreeData();
      }
      if (val == 'dataSource' && this.dataSourceOptions.length == 0) {
        this.fetchDataSourceOptions();
      }
    },
    fetchDataDictionaryTreeData() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'cdDataDictionaryFacadeService',
          methodName: 'getAllDataDictionaryAsCategoryTree'
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            // data.data.selectable = false;
            _this.dataDictionaryTreeData = data.data.children;
            _this.dataDictionaryTreeData.forEach(node => {
              node.selectable = !node.nocheck;
            });
          }
        });
    },
    fetchDataSourceOptions(value) {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData'
        })
        .then(({ data }) => {
          if (data.results) {
            _this.dataSourceOptions = data.results;
          }
        });
    },

    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },

    changeDataSourceId(value) {
      this.options.dataSourceLabelColumn = null;
      this.options.dataSourceValueColumn = null;
      this.columnIndexOptions = [];
      if (value) this.fetchColumns();
    },

    fetchColumns() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'viewComponentService',
          methodName: 'getColumnsById',
          args: JSON.stringify([this.options.dataSourceId])
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.columnIndexOptions.length = 0;
            for (let i = 0, len = data.data.length; i < len; i++) {
              _this.columnIndexOptions.push({
                value: data.data[i].columnIndex,
                label: data.data[i].title
              });
            }
          }
        });
    }
  },
  mounted() {
    this.onSelectOptionSrcChange(this.options.type);
    if (this.options.type == 'dataSource' && this.options.dataSourceId) {
      this.fetchColumns();
    }
  }
};
</script>
