<template>
  <div>
    <a-form-model>
      <a-form-model-item label="动态节点">
        <a-switch v-model="node.dynamic" @change="onDynamicChange" />
      </a-form-model-item>
      <a-form-model-item label="默认展开">
        <a-switch v-model="node.defaultExpand" />
      </a-form-model-item>
      <a-form-model-item label="是否可被选中">
        <a-switch v-model="node.selectable" />
      </a-form-model-item>

      <div v-show="node.dynamic">
        <a-form-model-item label="数据类型">
          <a-radio-group v-model="node.buildType" button-style="solid" size="small" @change="onChangeBuildType(true)">
            <a-radio-button value="dataSource">数据仓库</a-radio-button>
            <a-radio-button value="dataModel">数据模型</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="数据源" v-if="node.buildType == 'dataSource'">
          <DataStoreSelectModal v-model="node.dataSourceId" :displayModal="true" @change="changeDataSourceId(true)" />
        </a-form-model-item>
        <a-form-model-item label="数据模型" v-show="node.buildType == 'dataModel'">
          <DataModelSelectModal
            v-model="node.dataModelUuid"
            ref="dataModelSelect"
            :dtype="['TABLE', 'VIEW']"
            @change="onChangeDataModelSelect(true)"
          />
        </a-form-model-item>
        <a-form-model-item label="默认条件">
          <a-textarea v-model="node.defaultCondition" :auto-size="{ minRows: 2, maxRows: 6 }" />
        </a-form-model-item>
        <a-form-model-item label="排序字段">
          <a-select
            v-model="node.sortField"
            :options="columnOptions"
            show-search
            placeholder="请选择树节点排序的字段"
            style="width: 100%"
            :filter-option="filterOption"
            allow-clear
            :getPopupContainer="getPopupContainerNearestPs()"
          />
        </a-form-model-item>
        <a-form-model-item label="排序方式">
          <a-radio-group v-model="node.sortType" button-style="solid" default-value="asc" size="small">
            <a-radio-button value="asc">升序</a-radio-button>
            <a-radio-button value="desc">降序</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <!-- 通过以下字段配置构建整个树 -->
        <a-form-model-item label="标题字段">
          <a-select
            v-model="node.titleField"
            :options="columnOptions"
            show-search
            placeholder="请选择展示为树节点标题的字段"
            style="width: 100%"
            :filter-option="filterOption"
            :getPopupContainer="getPopupContainerNearestPs()"
          />
        </a-form-model-item>
        <a-form-model-item label="数据值字段">
          <a-select
            v-model="node.valueField"
            :options="columnOptions"
            show-search
            placeholder="请选择展示为树节点数据值的字段"
            style="width: 100%"
            :filter-option="filterOption"
            :getPopupContainer="getPopupContainerNearestPs()"
          />
        </a-form-model-item>
        <a-form-model-item label="父级字段">
          <a-select
            v-model="node.parentField"
            :options="columnOptions"
            show-search
            placeholder="请选择树节点父级数据值的字段"
            style="width: 100%"
            :filter-option="filterOption"
            allow-clear
            :getPopupContainer="getPopupContainerNearestPs()"
          />
        </a-form-model-item>
        <a-form-model-item label="图标字段">
          <a-select
            v-model="node.iconField"
            :options="columnOptions"
            show-search
            placeholder="请选择树节点图标的字段"
            style="width: 100%"
            :filter-option="filterOption"
            allow-clear
            :getPopupContainer="getPopupContainerNearestPs()"
          />
        </a-form-model-item>
      </div>
      <div v-show="!node.dynamic">
        <a-form-model-item label="节点ID">
          {{ node.key }}
        </a-form-model-item>
        <a-form-model-item label="节点名称">
          <a-input v-model="node.title">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :target="node" :designer="designer" :code="node.key" v-model="node.title" />
            </template>
          </a-input>
        </a-form-model-item>
      </div>

      <a-form-model-item label="节点图标" v-show="widget.configuration.showIcon">
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
          <IconSetBadge v-model="node.icon"></IconSetBadge>
          <template slot="content">
            <WidgetIconLib v-model="node.icon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
      <a-form-model-item label="子节点拖动排序">
        <a-switch v-model="node.supportDragSort" />
      </a-form-model-item>
      <BadgeConfiguration :designer="designer" :widget="widget" :configuration="node" conditionTip>
        <template slot="defaultCondition">
          <div>
            <label style="font-weight: bold; line-height: 32px">二、支持节点数据</label>
            <ol>
              <li v-for="(item, i) in defaultConditionVar" style="margin-bottom: 8px">
                <a-tag class="primary-color">{{ item.value }}</a-tag>
                : {{ item.label }}
              </li>
            </ol>
            <p>
              例如: SQL 编写为
              <a-tag>id = '${treeNode.key}'</a-tag>
            </p>
          </div>
        </template>
      </BadgeConfiguration>
      <a-form-model-item label="节点操作">
        <a-table :pagination="false" rowKey="id" :dataSource="node.operations" :columns="operationCols" :showHeader="false" size="small">
          <template slot="nameSlot" slot-scope="text, record">
            <a-input v-model="record.name">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :target="record" :designer="designer" :code="record.id" v-model="record.name" />
              </template>
            </a-input>
          </template>
          <template slot="moreSlot" slot-scope="text, record, index">
            <a-button type="link" size="small" @click="node.operations.splice(index, 1)" title="删除">
              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
            </a-button>
            <WidgetDesignModal id="treeNodeOperations" title="设置" :designer="designer">
              <a-button type="link" size="small" title="设置"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
              <template slot="content">
                <TreeNodeOperation :widget="widget" :designer="designer" :operation="record" />
              </template>
            </WidgetDesignModal>
          </template>
          <template slot="footer">
            <a-button type="link" @click="addNodeOperation">添加操作</a-button>
          </template>
        </a-table>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import TreeNodeOperation from './tree-node-operation.vue';
import { getPopupContainerNearestPs } from '@framework/vue/utils/function.js';
export default {
  name: 'TreeNodeConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    node: Object
  },
  data() {
    return {
      columnOptions: [],
      dataSourceOptions: [],
      operationCols: [
        { dataIndex: 'name', title: '名称', scopedSlots: { customRender: 'nameSlot' } },
        { dataIndex: 'more', title: '更多', scopedSlots: { customRender: 'moreSlot' }, width: '90px' }
      ],
      defaultConditionVar: [
        { label: '节点key', value: 'key' },
        { label: '父级key', value: 'parentKey' },
        { label: '标题', value: 'title' }
        // { label: '节点详细数据', value: '_originalData' }
      ]
    };
  },

  beforeCreate() {},
  components: { TreeNodeOperation },
  computed: {},
  created() {
    if (this.node.key === undefined) {
      // 新增节点
      this.$set(this.node, 'dynamic', false);
    }
  },
  methods: {
    getPopupContainerNearestPs,
    onChangeBuildType(isChange) {
      if (this.node.buildType == 'dataSource') {
        if (this.node.dataSourceId) {
          this.fetchColumns();
          if (isChange) {
            this.clearNodeSelData();
          }
        }
      }

      if (this.node.buildType == 'dataModel') {
        if (this.node.dataModelUuid) {
          this.onChangeDataModelSelect(isChange);
        }
      }
    },
    onDynamicChange() {
      if (this.node.dynamic) {
        this.node.title = '动态节点';
        this.fetchDataSourceOptions();
      }
    },
    addNodeOperation() {
      this.node.operations.push({
        id: generateId(),
        name: undefined,
        icon: undefined,
        eventHandler: { eventParams: [] }
      });
    },
    changeDataSourceId(isChange) {
      this.fetchColumns();
      if (isChange) {
        this.clearNodeSelData();
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
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    onChangeDataModelSelect(isChange) {
      if (this.node.dataModelUuid == undefined) {
        this.columnOptions.splice(0, this.columnOptions.length);
      } else {
        this.$refs.dataModelSelect.fetchDataModelColumns(this.node.dataModelUuid).then(list => {
          if (list) {
            this.columnOptions.splice(0, this.columnOptions.length);
            this.columnOptions.push(...list);
          }
        });
      }
      if (isChange) {
        this.clearNodeSelData();
      }
    },
    fetchColumns() {
      if (this.node && this.node.dataSourceId) {
        let _this = this;
        _this.columnOptions = [];
        $axios
          .post('/json/data/services', {
            serviceName: 'viewComponentService',
            methodName: 'getColumnsById',
            args: JSON.stringify([this.node.dataSourceId])
          })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              for (let i = 0, len = data.data.length; i < len; i++) {
                _this.columnOptions.push({ label: data.data[i].title, value: data.data[i].columnIndex });
              }
            }
          });
      }
    },
    clearNodeSelData() {
      this.$set(this.node, 'sortField', undefined);
      this.$set(this.node, 'titleField', undefined);
      this.$set(this.node, 'valueField', undefined);
      this.$set(this.node, 'parentField', undefined);
      this.$set(this.node, 'iconField', undefined);
    }
  },
  mounted() {
    if (this.node.selectable == undefined) {
      this.$set(this.node, 'selectable', true);
    }
    if (this.node.dynamic) {
      if (this.node.dataSourceId) {
        if (this.node.buildType == undefined) {
          this.$set(this.node, 'buildType', 'dataSource');
        }
        this.fetchDataSourceOptions();
      }
      if (this.node.buildType != 'dataModel') {
        this.fetchColumns();
      } else {
        this.onChangeDataModelSelect();
      }
    }
  }
};
</script>
