<template>
  <div>
    <div class="ant-row ant-form-item">
      <config-sub-title>备选项配置</config-sub-title>
    </div>
    <a-form-model-item
      style="flex-wrap: wrap"
      label="备选项来源"
      :wrapper-col="{ style: {} }"
      :label-col="{ style: { minWidth: 'unset' } }"
    >
      <a-select :options="optionSourceTypes" v-model="options.type" @change="changeOptionSource"></a-select>
      <!-- <a-radio-group size="small" v-model="options.type" @change="e => changeOptionSource(e.target.value)">
        <a-radio-button :value="item.value" v-for="(item, i) in optionSourceTypes" :key="i">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group> -->
    </a-form-model-item>

    <!-- 常量 -->
    <template v-if="options.type === 'selfDefine'">
      <option-source-self-define :designer="designer" :widget="widget" :options="widget.configuration.options" />
    </template>
    <!-- 数据字典 -->
    <template v-if="options.type == 'dataDictionary'">
      <option-source-dict :designer="designer" :widget="widget" :options="widget.configuration.options" />
    </template>
    <!-- 数据仓库 -->
    <div v-if="options.type == 'dataSource'">
      <option-source-data-store
        :designer="designer"
        :widget="widget"
        :options="widget.configuration.options"
        :dataSourceFieldOptions="dataSourceFieldOptions"
        @columnIndexOptionChange="columnIndexOptionChange"
      />
    </div>

    <div v-if="options.type == 'dataModel'">
      <option-source-data-model
        :designer="designer"
        :widget="widget"
        :options="widget.configuration.options"
        :fieldOptions="dataSourceFieldOptions"
        @columnIndexOptionChange="columnIndexOptionChange"
      />
    </div>
    <div v-if="options.type == 'apiLinkService'">
      <a-form-model-item label="API服务数据">
        <WidgetDesignDrawer :id="'apiLinkService' + widget.id" title="API 服务数据设置" :designer="designer" :width="875">
          <a-button type="primary" size="small">设置</a-button>
          <template slot="content">
            <ApiOperationDataset
              :widget="widget"
              :configuration="options"
              :designer="designer"
              :apiResultTransformSchema="apiResultTransformSchema"
            >
              <template slot="dataTransformFunctionPrefix">
                <a-alert type="info" message="入参 response 为api返回结果, 需要返回以下数据结构样例" style="margin-bottom: 8px">
                  <template slot="description">
                    <pre>
[
  {
    "label": "选项1","value":"选项值1","group":"分组",extend_label:"扩展选项文本"
  }

  // 更多选项对象
  ...
] </pre
                    >
                    <p>可通过返回 promise 对象, 用于实现异步逻辑</p>
                  </template>
                </a-alert>
              </template>
            </ApiOperationDataset>
          </template>
        </WidgetDesignDrawer>
      </a-form-model-item>
    </div>
    <a-form-model-item label="自动国际化翻译" v-if="options.type == 'dataSource' || options.type == 'apiLinkService'">
      <a-switch v-model="options.autoTranslate" />
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId, swapArrayElements } from '@framework/vue/utils/util';
import { optionSourceTypes } from '../../commons/constant';
import ApiOperationDataset from '../../commons/api-operation-dataset.vue';
export default {
  name: 'OptionSourceConfiguration',
  mixins: [],
  props: {
    designer: Object,
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
    if (this.widget.wtype == 'WidgetFormSelect') {
      apiResultTransformSchema.items.properties.group = {
        propertyEditable: false,
        type: 'string',
        description: '选项分组'
      };

      apiResultTransformSchema.items.properties.extend_label = {
        propertyEditable: false,
        type: 'string',
        description: '扩展显示文本'
      };
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
  components: { ApiOperationDataset },
  computed: {
    selfDefineColumns() {
      return [{ title: '选项', dataIndex: 'value', width: 120, scopedSlots: { customRender: 'valueSlot' } }];
    }
  },
  created() {},
  methods: {
    // 改变备选项来源 常量、数据字典、数据仓库
    changeOptionSource(val) {
      this.widget.configuration.optionDataAutoSet = false;
    },
    columnIndexOptionChange(columnIndexOptions) {
      this.$emit('columnIndexOptionChange', columnIndexOptions);
    },
    addDefineOption() {
      this.options.defineOptions.push({
        id: generateId(),
        label: '',
        value: ''
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
    fetchData(type) {
      if (type == 'dataDictionary' && this.dataDictionaryTreeData.length == 0) {
        // 组件OptionSourceDict自己取数据
        // this.fetchDataDictionaryTreeData();
      }
      if (type == 'dataSource' && this.dataSourceOptions.length == 0) {
        this.fetchDataSourceOptions();
      }
    },
    // 获取数据字典
    // fetchDataDictionaryTreeData() {
    //   let _this = this;
    //   $axios
    //     .post('/json/data/services', {
    //       serviceName: 'cdDataDictionaryFacadeService',
    //       methodName: 'getAllDataDictionaryAsCategoryTree'
    //     })
    //     .then(({ data }) => {
    //       if (data.code == 0 && data.data) {
    //         // data.data.selectable = false;
    //         _this.dataDictionaryTreeData = data.data.children;
    //         _this.dataDictionaryTreeData.forEach(node => {
    //           node.selectable = !node.nocheck;
    //         });
    //       }
    //     });
    // },
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
      console.log(input, option);
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },

    changeDataSourceId(value) {
      this.options.dataSourceLabelColumn = null;
      this.options.dataSourceValueColumn = null;
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
      // this.columnIndexOptions.length = 0;
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
              _this.columnIndexOptions.splice(0, _this.columnIndexOptions.length);
              // _this.columnIndexOptions.length = 0;
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
