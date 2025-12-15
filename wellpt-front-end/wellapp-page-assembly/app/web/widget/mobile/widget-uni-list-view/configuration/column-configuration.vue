<template>
  <div class="widget-edit-uni-list-view-column">
    <a-button-group class="table-header-operation">
      <a-button @click="addColumn">
        <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
        新增
      </a-button>
      <a-button @click="delColumn">
        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        删除
      </a-button>
      <a-button @click="moveUpColumn" icon="arrow-up">上移</a-button>
      <a-button @click="moveDownColumn" icon="arrow-down">下移</a-button>
    </a-button-group>
    <a-tooltip>
      <template slot="title">
        <ul style="padding-inline-start: 20px; margin-block-end: 0px">
          <li>卡片风格时，若不配置属性标题或属性标题为备注时，则均展示在内容区内</li>
          <li>卡片风格时，若属性标题为右侧文本时，显示在标题右侧</li>
          <li>卡片风格时，在非内容区域不显示【显示标题】</li>
          <li>列表风格时，不配置属性标题的字段，将不显示</li>
          <li>存在多个相同属性标题的（除卡片的备注外），以最后一个为准</li>
        </ul>
      </template>
      <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
    </a-tooltip>
    <a-table
      class="pt-table widget-uni-list-columns-table"
      style="--pt-table-td-padding: var(--w-padding-2xs)"
      rowKey="uuid"
      :row-selection="{ selectedRowKeys: selectedColumnRowKeys, onChange: selectColumnChange }"
      :pagination="false"
      bordered
      :columns="columnDefine"
      :data-source="columnData"
      :scroll="{ y: 600 }"
    >
      <template slot="nameSlot" slot-scope="text, record">
        <Icon
          v-if="configuration.displayStyle == '2'"
          type="pticon iconfont icon-ptkj-tuodong"
          class="drag-column-handler"
          :style="{ cursor: 'move', verticalAlign: 'sub' }"
          title="拖动排序"
        ></Icon>
        <a-select
          :options="columnTemplateOptions"
          :style="{ width: configuration.displayStyle == '2' ? 'calc(100% - 24px)' : '100%' }"
          :defaultValue="text"
          allowClear
          @change="$evt => editColumnPropText($evt, record, 'name')"
        ></a-select>
      </template>
      <template slot="iconSlot" slot-scope="text, record">
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
          <IconSetBadge v-model="record.icon"></IconSetBadge>
          <template slot="content">
            <WidgetIconLib v-model="record.icon" />
          </template>
        </WidgetDesignModal>
      </template>
      <template slot="titleSlot" slot-scope="text, record">
        <a-input v-model="record.title" allowClear class="addon-padding-3xs">
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :designer="designer" :code="record.uuid" :target="record" v-model="record.title" />
          </template>
        </a-input>
      </template>
      <template slot="mapColumnSlot" slot-scope="text, record, index">
        <a-select
          :options="recordColumnIndexOptions(index)"
          :style="{ width: '100%' }"
          :defaultValue="text"
          :showSearch="true"
          :filter-option="filterSelectOption"
          allowClear
          @change="$evt => editColumnPropText($evt, record, 'mapColumn')"
        ></a-select>
      </template>
      <template slot="rendererSlot" slot-scope="text, record">
        <a-select
          :allowClear="true"
          :style="{
            width:
              record.renderer.rendererType &&
              renderConfNames.includes(record.renderer.rendererType + 'Config') &&
              record.renderer.rendererType !== 'CellDataFileRender'
                ? 'calc(100% - 36px)'
                : '100%'
          }"
          :showSearch="true"
          :filter-option="filterSelectOption"
          v-model="record.renderer.rendererType"
          @change="(val, vnode) => onSelectChangeRenderFunc(val, vnode, record.renderer)"
        >
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="desktop" />
              客户端渲染
            </span>

            <a-select-option v-for="(opt, i) in clientRenderOptions" :value="opt.value" :key="opt.value">
              {{ opt.label }}
            </a-select-option>
          </a-select-opt-group>
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="cloud-server" />
              服务端渲染
            </span>

            <a-select-option v-for="(opt, i) in serverRenderOptions" :value="opt.value" :key="opt.value">
              {{ opt.label }}
            </a-select-option>
          </a-select-opt-group>
        </a-select>
        <WidgetDesignModal
          title="渲染器配置"
          :width="640"
          v-if="
            record.renderer.rendererType &&
            renderConfNames.includes(record.renderer.rendererType + 'Config') &&
            record.renderer.rendererType !== 'CellDataFileRender'
          "
        >
          <a-button type="link" size="small" title="渲染器配置">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          </a-button>
          <template slot="content">
            <a-form-model :colon="false">
              <component
                v-if="record.renderer.rendererType && renderConfNames.includes(record.renderer.rendererType + 'Config')"
                :is="record.renderer.rendererType + 'Config'"
                :options="record.renderer.options"
                :column="record"
                :widget="widget"
                :designer="designer"
                :columnIndexOptions="columnIndexOptions"
              />
            </a-form-model>
          </template>
        </WidgetDesignModal>
      </template>
      <template slot="sortOrderSlot" slot-scope="text, record">
        <a-select
          :options="sortOrderOptions"
          :style="{ width: '100%' }"
          :defaultValue="text"
          @change="$evt => editColumnPropText($evt, record, 'sortOrder')"
        ></a-select>
      </template>
      <template slot="ellipsisSlot" slot-scope="text, record">
        <a-input-number v-model="record.ellipsis" style="width: 100%" :min="1" :max="3"></a-input-number>
      </template>
    </a-table>
  </div>
</template>
<style lang="less">
.widget-edit-uni-list-view-column .table-header-operation {
  margin-bottom: 8px;
}

.widget-edit-uni-list-view-column .table-header-operation {
  margin-bottom: 8px;
}
.widget-edit-uni-list-view-column .table-footer-operation {
  margin-top: 8px;
  float: right;
}
.designer_form_model_modal {
  .ant-row {
    margin-bottom: 4px;
    display: flex;
    .ant-form-item-control-wrapper {
      flex: 1 1 auto;
    }
  }
}
</style>
<script type="text/babel">
import RenderConfiguration from '@pageAssembly/app/web/widget/widget-table/configuration/render-configuration/index';
import { generateId } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';
import TableRenderMixin from '@pageAssembly/app/web/widget/widget-table/table.renderMixin';
import CellRender from '@pageAssembly/app/web/widget/widget-table/cell-render/index';
import draggable from '@framework/vue/designer/draggable';
export default {
  name: 'WidgetUniListViewColumnConfiguration',
  mixins: [draggable],
  inject: ['pageContext'],
  components: { ...RenderConfiguration },
  props: {
    widget: Object,
    configuration: Object,
    designer: Object,
    columnIndexOptions: Array,
    columnTemplateOptions: {
      type: Array,
      default: () => {
        return [
          { label: '标题', value: 'title' },
          { label: '小标题', value: 'subtitle' },
          { label: '备注', value: 'note' },
          { label: '右侧文本', value: 'rightText' },
          { label: '底部左侧', value: 'bottomLeft' },
          { label: '底部右侧', value: 'bottomRight' }
        ];
      }
    }
  },
  data() {
    console.log(CellRender);
    let clientRenderOptions = [];
    for (let k in CellRender) {
      if (CellRender[k].scope && CellRender[k].scope.includes('mobile')) {
        clientRenderOptions.push({
          label: CellRender[k].title,
          value: k,
          isClient: true
        });
      }
    }
    TableRenderMixin.methods.getRenderMethodOptions().forEach(item => {
      if (item.scope && item.scope.includes('mobile')) {
        clientRenderOptions.push({
          label: item.label,
          value: item.value
        });
      }
    });
    return {
      renderConfNames: Object.keys(RenderConfiguration),
      RenderConfiguration,
      options: {},
      serverRenderOptions: [],
      columnRenderFuncConfVisible: false,
      currentColumnRenderFuncConf: {},
      selectedColumnRowKeys: [],
      sortOrderOptions: [
        { label: '无排序', value: '' },
        { label: '升序', value: 'asc' },
        { label: '降序', value: 'desc' }
      ],
      clientRenderOptions
    };
  },

  beforeCreate() {},
  computed: {
    columnDefine() {
      if (this.configuration.displayStyle == '2') {
        return [
          {
            title: '属性标题',
            dataIndex: 'name',
            width: 150,
            scopedSlots: { customRender: 'nameSlot' }
          },
          { title: '图标', dataIndex: 'icon', width: 50, scopedSlots: { customRender: 'iconSlot' } },
          { title: '显示标题', dataIndex: 'title', width: 160, scopedSlots: { customRender: 'titleSlot' } },
          { title: '映射字段', dataIndex: 'mapColumn', width: 150, scopedSlots: { customRender: 'mapColumnSlot' } },
          { title: '渲染器', dataIndex: 'renderer', scopedSlots: { customRender: 'rendererSlot' } },
          { title: '排序', dataIndex: 'sortOrder', width: 100, scopedSlots: { customRender: 'sortOrderSlot' } },
          { title: '超出省略', dataIndex: 'ellipsis', width: 80, scopedSlots: { customRender: 'ellipsisSlot' } }
        ];
      } else {
        // 数据展示风格为列表时，不显示标题设置
        return [
          { title: '属性标题', dataIndex: 'name', width: 150, scopedSlots: { customRender: 'nameSlot' } },
          { title: '图标', dataIndex: 'icon', width: 50, scopedSlots: { customRender: 'iconSlot' } },
          // { title: '显示标题', dataIndex: 'title', width: 200, scopedSlots: { customRender: 'titleSlot' } },
          { title: '映射字段', dataIndex: 'mapColumn', width: 150, scopedSlots: { customRender: 'mapColumnSlot' } },
          { title: '渲染器', dataIndex: 'renderer', scopedSlots: { customRender: 'rendererSlot' } },
          { title: '排序', dataIndex: 'sortOrder', width: 100, scopedSlots: { customRender: 'sortOrderSlot' } },
          { title: '超出省略', dataIndex: 'ellipsis', width: 80, scopedSlots: { customRender: 'ellipsisSlot' } }
        ];
      }
    },
    columnData() {
      return this.configuration.templateProperties;
    },
    columnTemplateTitleMap() {
      let map = {};
      for (let i = 0, len = this.columnTemplateOptions.length; i < len; i++) {
        map[this.columnTemplateOptions[i].value] = this.columnTemplateOptions[i].label;
      }
      return map;
    },
    columnIndexTitleMap() {
      let map = {};
      for (let i = 0, len = this.columnIndexOptions.length; i < len; i++) {
        map[this.columnIndexOptions[i].value] = this.columnIndexOptions[i].label;
      }
      return map;
    }
  },
  created() {
    this.getRenderFunctionOptions();
  },
  methods: {
    filterSelectOption,
    // 除当前行映射字段外，其他行字段不显示
    recordColumnIndexOptions(index) {
      return this.columnIndexOptions.filter(item => {
        let flag = true;
        let hasIndex = this.columnData.findIndex(record => record.mapColumn == item.value);
        if (hasIndex > -1 && hasIndex != index) {
          flag = false;
        }
        return flag;
      });
    },
    addColumn() {
      this.configuration.templateProperties.push({
        uuid: generateId(),
        title: '',
        name: '',
        mapColumn: '',
        mapColumnName: '',
        renderer: {},
        ellipsis: undefined,
        sortOrder: ''
      });
    },
    delColumn() {
      if (this.selectedColumnRowKeys.length) {
        for (let i = 0, len = this.selectedColumnRowKeys.length; i < len; i++) {
          for (let j = 0, jlen = this.configuration.templateProperties.length; j < jlen; j++) {
            if (this.configuration.templateProperties[j].uuid == this.selectedColumnRowKeys[i]) {
              this.configuration.templateProperties.splice(j, 1);
              break;
            }
          }
        }
      }
    },
    moveUpColumn() {},
    moveDownColumn() {},
    editColumnPropText($evt, record, field) {
      if (field == 'name') {
        record.title = this.columnTemplateTitleMap[$evt];
      } else if (field == 'mapColumn') {
        record.mapColumnName = this.columnIndexTitleMap[$evt];
      }
      record[field] = $evt;
    },
    selectColumnChange(selectedRowKeys, selectedRows) {
      this.selectedColumnRowKeys = selectedRowKeys;
    },
    onSelectChangeRenderFunc(val, vnode, renderFunction) {
      this.$set(renderFunction, 'options', {});
      if (this.clientRenderOptions.findIndex(item => item.value == val) > -1) {
        this.$set(renderFunction, 'isClient', true);
      } else {
        this.$set(renderFunction, 'isClient', false);
      }
    },
    getRenderFunctionOptions() {
      var _this = this;
      if (this.serverRenderOptions.length == 0) {
        $axios
          .post('/common/select2/query', {
            serviceName: 'viewComponentService',
            queryMethod: 'loadRendererSelectData',
            pageNo: 1,
            type: 2,
            pageSize: 10000
          })
          .then(({ data }) => {
            if (data.results && data.results.length) {
              for (let i = 0, len = data.results.length; i < len; i++) {
                _this.serverRenderOptions.push({
                  value: data.results[i].id,
                  label: data.results[i].text
                });
              }
            }
          });
      }
    },
    draggableMount() {
      this.tableDraggable(
        this.configuration.templateProperties,
        this.$el.querySelector('.widget-uni-list-columns-table tbody'),
        '.drag-column-handler'
      );
    }
  },
  mounted() {
    this.draggableMount();
  },
  watch: {
    columnData: {
      deep: true,
      handler(v) {
        // console.log(JSON.stringify(v));
      }
    }
  }
};
</script>
