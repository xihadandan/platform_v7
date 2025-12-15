<template>
  <ButtonConfiguration :button="button" :widget="widget" :designer="designer" ref="buttonConfiguration">
    <template slot="extraInfo">
      <a-form-model-item label="导出文件名">
        <a-input v-model="exportRule.exportFileName">
          <template slot="addonAfter">
            <WI18nInput
              :widget="widget"
              :designer="designer"
              :code="button.id + '_fileName'"
              v-model="exportRule.exportFileName"
              :target="exportRule"
            />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="支持可选字段导出">
        <a-switch v-model="exportRule.exportColumnSelectable" />
      </a-form-model-item>

      <a-form-model-item label="导出表格数据来源">
        <a-switch v-model="exportRule.useTableDataSource" />
      </a-form-model-item>
      <div v-if="!exportRule.useTableDataSource">
        <a-form-model-item label="数据来源">
          <a-radio-group v-model="exportRule.dataSourceType" button-style="solid" size="small">
            <a-radio-button value="dataSource">数据仓库</a-radio-button>
            <a-radio-button value="dataModel">数据模型</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="选择数据仓库" v-if="exportRule.dataSourceType == 'dataSource'">
          <DataStoreSelectModal v-model="exportRule.dataSourceId" :displayModal="true" @change="e => changeDataSourceId(exportRule)" />
        </a-form-model-item>
        <template v-else-if="exportRule.dataSourceType == 'dataModel'">
          <a-form-model-item>
            <template slot="label">
              <span
                style="cursor: pointer"
                :class="exportRule.dataModelUuid ? 'ant-btn-link' : ''"
                @click="redirectDataModelDesign(exportRule.dataModelUuid)"
                :title="exportRule.dataModelUuid ? '打开数据模型' : ''"
              >
                数据模型
                <a-icon type="environment" v-show="exportRule.dataModelUuid" style="color: inherit; line-height: 1" />
              </span>
            </template>
            <DataModelSelectModal
              v-model="exportRule.dataModelUuid"
              :dtype="['TABLE', 'VIEW']"
              ref="dataModelSelect"
              @change="e => onDataModelChange(e, exportRule)"
            />
          </a-form-model-item>
        </template>
        <a-form-model-item label="默认导出条件"><a-textarea v-model="exportRule.defaultCondition"></a-textarea></a-form-model-item>
      </div>
      <a-form-model-item label="导出范围" v-else>
        <a-radio-group v-model="exportRule.exportRange" button-style="solid" size="small">
          <a-radio-button value="all">导出全部</a-radio-button>
          <a-radio-button value="rowSelected">导出所选</a-radio-button>
          <a-radio-button value="currentPage">导出当前页</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="导出列定义">
        <WidgetDesignModal width="800px" :id="'tableColExportFormatConfig' + widget.id" title="导出列定义" :designer="designer">
          <a-button type="link" icon="setting" size="small">配置</a-button>
          <template slot="content">
            <a-table
              rowKey="dataIndex"
              :pagination="false"
              :bordered="false"
              :columns="formatterColumn"
              :locale="locale"
              :data-source="exportRule.useTableDataSource ? widget.configuration.columns : exportRule.columns"
              :scroll="{ y: 'calc(100vh - 225px)' }"
              size="small"
              class="widget-table-col-table no-border"
            >
              <template slot="exportableSlot" slot-scope="text, record, index">
                <a-switch v-model="record.exportable" size="small" />
              </template>
              <template slot="titleSlot" slot-scope="text, record">
                <a-input v-model="record.title" v-if="exportRule.useTableDataSource">
                  <template slot="addonAfter">
                    <WI18nInput :widget="widget" :designer="designer" :code="record.id" v-model="record.title" :target="record" />
                  </template>
                </a-input>
                <a-input v-model="record.title" v-else>
                  <template slot="addonAfter">
                    <WI18nInput
                      :widget="widget"
                      :designer="designer"
                      :code="button.id + '_' + record.dataIndex"
                      v-model="record.title"
                      :target="record"
                    />
                  </template>
                </a-input>
                <!-- <a-tag>{{ record.dataIndex }}</a-tag> -->
              </template>
              <template slot="formatterSlot" slot-scope="text, record">
                <a-select
                  :allowClear="true"
                  :style="{ width: '100%' }"
                  :showSearch="true"
                  :filter-option="filterSelectOption"
                  v-model="record.exportFunction.type"
                  @change="(val, vnode) => onSelectChangeRenderFunc(val, vnode, record.exportFunction)"
                >
                  <a-select-opt-group>
                    <span slot="label">
                      <a-icon type="cloud-server" />
                      服务端渲染
                    </span>
                    <a-select-option v-for="(opt, i) in columnRenderOptions" :value="opt.value" :key="opt.value">
                      {{ opt.label }}
                    </a-select-option>
                  </a-select-opt-group>
                </a-select>
                <a-form-model
                  v-if="record.exportFunction.type && renderConfNames.includes(record.exportFunction.type + 'Config')"
                  :colon="false"
                  layout="vertical"
                  style="margin-top: 5px"
                >
                  <component
                    :is="record.exportFunction.type + 'Config'"
                    :options="record.exportFunction.options"
                    :column="record"
                    :widget="widget"
                    :designer="designer"
                    :columnIndexOptions="columnIndexOptions"
                  />
                </a-form-model>
              </template>
            </a-table>
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
    </template>
  </ButtonConfiguration>
</template>
<style lang="less"></style>
<script type="text/babel">
import ButtonConfiguration from './button-configuration.vue';
import RenderConfiguration from './render-configuration/index';
import { generateId, deepClone } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';
export default {
  name: 'ExportButtonConfiguration',
  props: {
    widget: Object,
    designer: Object,
    columnIndexOptions: Array,
    button: Object,
    exportRule: Object,
    columnRenderOptions: Array
  },
  components: { ButtonConfiguration },
  computed: {},
  data() {
    return {
      dataSourceOptions: [],
      renderConfNames: Object.keys(RenderConfiguration),
      formatterColumn: [
        { title: '允许导出', dataIndex: 'exportable', width: 75, scopedSlots: { customRender: 'exportableSlot' } },
        { title: '列', dataIndex: 'title', width: 'calc(50% - 75px)', scopedSlots: { customRender: 'titleSlot' } },
        { title: '数据格式化', dataIndex: 'width', width: '50%', scopedSlots: { customRender: 'formatterSlot' }, align: 'center' }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  watch: {
    'widget.configuration.columns': {
      handler(val) {
        this.initColumns();
      }
    }
  },
  beforeMount() {
    this.initColumns();
  },
  mounted() {
    this.fetchDataSourceOptions();
  },
  methods: {
    filterSelectOption,
    initColumns() {
      if (this.widget.configuration.columns.length) {
        for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
          if (this.widget.configuration.columns[i].exportable == undefined) {
            this.$set(this.widget.configuration.columns[i], 'exportable', this.widget.configuration.columns[i].hidden !== true);
          }
          if (this.widget.configuration.columns[i].exportFunction == undefined) {
            this.$set(this.widget.configuration.columns[i], 'exportFunction', { type: undefined, options: {} });
          }
        }
      }
    },
    redirectDataModelDesign(uuid) {
      if (uuid) {
        window.open(`/data-model-design/index?uuid=${uuid}`, '_blank');
      }
    },
    validate() {
      return new Promise((resolve, reject) => {
        this.$refs.buttonConfiguration.validate().then(valid => {
          if (valid) {
            resolve();
          }
        });
      });
    },
    fetchDataModelDetails(uuid) {
      return new Promise((resolve, reject) => {
        $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid } }).then(({ data, headers }) => {
          if (data.code == 0) {
            let detail = data.data,
              columns = JSON.parse(detail.columnJson);
            let columnIndexOptions = [];
            for (let col of columns) {
              if (detail.type == 'TABLE' || (detail.type == 'VIEW' && col.hidden !== true && col.return === true)) {
                columnIndexOptions.push({
                  dataIndex: col.alias || col.column,
                  title: col.title,
                  isSysDefault: col.isSysDefault
                });
              }
            }
            resolve(columnIndexOptions);
          }
        });
      });
    },
    onDataModelChange(uuid, item) {
      item.columns.splice(0, item.columns.length);
      if (uuid) {
        this.fetchDataModelDetails(uuid).then(list => {
          list.forEach(d => {
            item.columns.push({
              title: d.title,
              dataIndex: d.dataIndex,
              exportable: d.isSysDefault !== true,
              exportFunction: { type: undefined, options: {} }
            });
          });
        });
      }
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
    changeDataSourceId(item) {
      let _this = this;
      _this.fetchColumns(item.dataSourceId).then(({ data }) => {
        if (data.code == 0 && data.data) {
          item.columns.splice(0, item.columns.length);
          data.data.forEach(d => {
            item.columns.push({
              title: d.title,
              dataIndex: d.columnIndex,
              exportable: d.hidden === true,
              exportFunction: { type: undefined, options: {} }
            });
          });
        }
      });
    },
    fetchColumns(dataSourceId) {
      return $axios.post('/json/data/services', {
        serviceName: 'viewComponentService',
        methodName: 'getColumnsById',
        args: JSON.stringify([dataSourceId])
      });
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    onSelectChangeRenderFunc(val, vnode, exportFunction) {
      this.$set(exportFunction, 'options', {});
    }
  }
};
</script>
