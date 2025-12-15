<template>
  <div>
    <a-form-model-item label="备选项来源">
      <a-select :options="optionSourceTypes" v-model="options.type" @change="changeOptionSource" />
      <!-- <a-radio-group
        size="small"
        v-model="options.type"
        @change="
          e => {
            changeOptionSource(e.target.value);
          }
        "
      >
        <a-radio-button :value="item.value" v-for="(item, i) in optionSourceTypes" :key="i">{{ item.label }}</a-radio-button>
      </a-radio-group> -->
    </a-form-model-item>
    <!-- 常量 -->
    <template v-if="options.type === 'selfDefine'">
      <div class="wcfg-select-item">
        <a-button-group class="table-header-operation" size="small">
          <a-button @click="addDefineOption" icon="plus">新增</a-button>
          <a-button @click="delDefineOption">
            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
            删除
          </a-button>
          <a-button @click="moveDefineOption('forward')" icon="arrow-up">上移</a-button>
          <a-button @click="moveDefineOption" icon="arrow-down">下移</a-button>
        </a-button-group>
        <a-table
          size="small"
          rowKey="id"
          :row-selection="{ selectedRowKeys: selectedOptionRowKeys, onChange: selectConstantChange }"
          :pagination="false"
          bordered
          :columns="selfDefineColumns"
          :data-source="options.defineOptions"
        >
          <template slot="valueSlot" slot-scope="text, record">
            <a-input addon-before="组名" v-model="record.group" size="small" v-show="options.type == 'select-group'"></a-input>
            <a-input addon-before="展示文本" v-model="record.label" size="small">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" :target="record" :code="record.id" v-model="record.label" />
              </template>
            </a-input>

            <a-input addon-before="值" v-model="record.value" size="small" />
          </template>
        </a-table>
      </div>
    </template>
    <!-- 数据字典 -->
    <template v-if="options.type == 'dataDictionary'">
      <div class="wcfg-select-item">
        <div>数据字典选择</div>
        <a-tree-select
          class="wcfg-select-dict"
          showSearch
          allowClear
          v-model="options.dataDictionaryUuid"
          style="width: 100%"
          treeNodeFilterProp="title"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="dataDictionaryTreeData"
          :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
        ></a-tree-select>
      </div>
    </template>
    <a-form-model-item label="自动国际化翻译" v-if="options.type == 'dataSource' || options.type == 'dataModel'">
      <a-switch v-model="options.autoTranslate" />
    </a-form-model-item>
    <!-- 数据仓库 -->
    <div v-if="options.type == 'dataSource'">
      <a-form-model-item label="数据仓库选择" :label-col="{ span: 8 }" :wrapper-col="{ span: 15, style: { textAlign: 'right' } }">
        <DataStoreSelectModal v-model="options.dataSourceId" :displayModal="true" @change="changeDataSourceId" />
      </a-form-model-item>

      <a-form-model-item
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 15, style: { textAlign: 'right' } }"
        :label="opt.label"
        v-for="(opt, i) in dataSourceFieldOptions"
        :key="i"
      >
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

    <a-form-model-item label="页面变量" v-show="options.type == 'pageVar'">
      <a-select :options="pageVarOptions" allowClear v-model="options.bindPageVar" />
    </a-form-model-item>

    <div v-show="options.type == 'dataModel'">
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

    <!-- <a-form-model-item label="展示字段" v-show="options.type == 'dataSource'">
      <a-select
        v-model="options.dataSourceLabelColumn"
        show-search
        size="small"
        style="width: 100%"
        :filter-option="filterOption"
        :options="columnIndexOptions"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="值字段" v-show="options.type == 'dataSource'">
      <a-select
        v-model="options.dataSourceValueColumn"
        show-search
        size="small"
        style="width: 100%"
        :filter-option="filterOption"
        :options="columnIndexOptions"
      ></a-select>
    </a-form-model-item> -->
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId, swapArrayElements } from '@framework/vue/utils/util';
import { optionSourceTypes as optionSources } from '../../../../commons/constant';
import optionSourceDataModel from '@dyform/app/web/widget/widget-form-tag/configuration/components/optionSourceDataModel.vue';
import ApiOperationDataset from '@dyform/app/web/widget/commons/api-operation-dataset.vue';

export default {
  name: 'FormSelectOptionSourceConfiguration',
  mixins: [],
  props: {
    designer: Object,
    widget: Object,
    options: Object,
    dataSourceFieldOptions: Array,
    optionSources: Array
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
    let optionSourceTypes;
    if (this.optionSources) {
      optionSourceTypes = this.optionSources;
    } else {
      optionSourceTypes = optionSources;
    }
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
  computed: {
    selfDefineColumns() {
      return [{ title: '选项', dataIndex: 'value', width: 120, scopedSlots: { customRender: 'valueSlot' } }];
    },
    pageVarOptions() {
      // 页面变量路径
      let paths = this.designer.pageVarKeyPaths(),
        options = [];
      for (let i = 0, len = paths.length; i < len; i++) {
        options.push({ label: paths[i], value: paths[i] });
      }
      return options;
    }
  },
  created() {
    this.fetchData(this.options.type);
    this.fetchColumns();
  },

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
    // 改变备选项来源 常量、数据字典、数据仓库
    changeOptionSource(val) {
      this.widget.configuration.optionDataAutoSet = false;
      this.fetchData(val);
    },
    fetchData(type) {
      if (type == 'dataDictionary' && this.dataDictionaryTreeData.length == 0) {
        this.fetchDataDictionaryTreeData();
      }
      if (type == 'dataSource' && this.dataSourceOptions.length == 0) {
        // this.fetchDataSourceOptions();
      }
    },
    // 获取数据字典
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
    // 获取数据仓库
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

    changeDataSourceId(value, opt) {
      this.options.dataSourceLabelColumn = null;
      this.options.dataSourceValueColumn = null;
      this.columnIndexOptions = [];
      if (value) this.fetchColumns();
    },

    fetchColumns() {
      let _this = this;
      if (this.options.dataSourceId) {
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
              _this.$emit('columnIndexOptionChange', _this.columnIndexOptions);
            }
          });
      }
    }
  }
};
</script>
