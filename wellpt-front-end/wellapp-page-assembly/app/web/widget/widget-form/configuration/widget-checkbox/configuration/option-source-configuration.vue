<template>
  <div>
    <a-form-model-item label="选项来源">
      <a-select
        :options="optionSourceTypes"
        :style="{ width: '100%' }"
        v-model="options.type"
        allowClear
        @change="onSelectOptionSrcChange"
      ></a-select>
    </a-form-model-item>
    <div v-show="options.type == 'selfDefine'" style="padding: 0px 10px">
      <a-button-group class="table-header-operation" size="small" style="margin-bottom: 8px">
        <a-button @click="addDefineOption" icon="plus">新增</a-button>
        <a-button @click="delDefineOption">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          删除
        </a-button>
        <a-button @click="moveDefineOption('forward')" icon="arrow-up">上移</a-button>
        <a-button @click="moveDefineOption" icon="arrow-down">下移</a-button>
      </a-button-group>
      <a-table
        rowKey="id"
        size="small"
        :row-selection="{ selectedRowKeys: selectedOptionRowKeys, onChange: selectConstantChange }"
        :pagination="false"
        :bordered="false"
        :columns="[
          { title: '展示文本', dataIndex: 'label', width: 120, scopedSlots: { customRender: 'labelSlot' } },
          { title: '值', dataIndex: 'value', width: 120, scopedSlots: { customRender: 'valueSlot' } }
        ]"
        :data-source="options.defineOptions"
      >
        <template slot="valueSlot" slot-scope="text, record">
          <a-input v-model="record.value" size="small" />
        </template>
        <template slot="labelSlot" slot-scope="text, record">
          <a-input v-model="record.label" size="small">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" :target="record" :code="record.id" v-model="record.label" />
            </template>
          </a-input>
        </template>
      </a-table>
    </div>
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
    <a-form-model-item label="自动国际化翻译" v-if="options.type == 'dataSource' || options.type == 'dataModel'">
      <a-switch v-model="options.autoTranslate" />
    </a-form-model-item>
    <a-form-model-item label="数据源" v-if="options.type == 'dataSource'">
      <DataStoreSelectModal v-model="options.dataSourceId" :displayModal="true" @change="changeDataSourceId" />
    </a-form-model-item>

    <div v-if="options.type == 'dataSource'">
      <a-form-model-item :label="opt.label" v-for="(opt, i) in dataSourceFieldOptions" :key="i">
        <a-select
          v-model="options[opt.value]"
          show-search
          style="width: 100%"
          :filter-option="filterOption"
          :options="columnIndexOptions"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item label="默认过滤条件" class="display-b" :label-col="{}" :wrapper-col="{}">
        <a-textarea
          placeholder="请输入过滤条件"
          v-model="options.defaultCondition"
          :rows="4"
          :style="{
            height: 'auto'
          }"
        />
      </a-form-model-item>
    </div>

    <div v-if="options.type == 'dataModel'">
      <optionSourceDataModel :designer="designer" :widget="widget" :options="options" />
    </div>
    <div v-show="options.type == 'apiLinkService'">
      <a-form-model-item label="API服务数据">
        <WidgetDesignDrawer :id="'apiLinkService' + widget.id" title="API 服务数据设置" :designer="designer" :width="875">
          <a-button type="primary" size="small">设置</a-button>
          <template slot="content">
            <ApiOperationDataset
              :widget="widget"
              :configuration="options"
              :designer="designer"
              :apiResultTransformSchema="apiResultTransformSchema"
            />
          </template>
        </WidgetDesignDrawer>
      </a-form-model-item>
    </div>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId, swapArrayElements } from '@framework/vue/utils/util';
import { optionSourceTypes } from '../../../../commons/constant';
import optionSourceDataModel from '@dyform/app/web/widget/widget-form-tag/configuration/components/optionSourceDataModel.vue';
import ApiOperationDataset from '@dyform/app/web/widget/commons/api-operation-dataset.vue';

export default {
  name: 'FormCheckboxOptionSourceConfiguration',
  inject: ['designer'],
  mixins: [],
  props: {
    widget: Object,
    options: Object,
    dataSourceFieldOptions: Array
  },
  data() {
    let apiResultTransformSchema = {
      type: 'array',
      description: '下拉选项数组',
      propertyEditable: false,
      items: {
        type: 'object',
        propertyEditable: false,
        properties: {
          label: {
            propertyEditable: false,
            type: 'string',
            description: '选项文本'
          },
          value: {
            propertyEditable: false,
            type: 'string',
            description: '选项值'
          }
        }
      }
    };
    return {
      selectedOptionRowKeys: [],
      dataDictionaryTreeData: [],
      dataSourceOptions: [],
      columnIndexOptions: [],
      selectedOptionRows: [],
      optionSourceTypes,
      apiResultTransformSchema
    };
  },

  beforeCreate() {},
  components: { optionSourceDataModel, ApiOperationDataset },
  computed: {},
  created() {},
  methods: {
    addDefineOption() {
      this.options.defineOptions.push({
        id: generateId(),
        label: undefined,
        value: undefined
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
        // this.fetchDataSourceOptions();
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
    if (this.options.type) {
      this.onSelectOptionSrcChange(this.options.type);
    }
  }
};
</script>
