<template>
  <div class="widget-edit-table-keywordsearch">
    <a-form-model-item label="关键字搜索">
      <a-switch v-model="widget.configuration.search.keywordSearchEnable" />
    </a-form-model-item>
    <a-form-model-item label="关键字搜索字段" v-show="widget.configuration.search.keywordSearchEnable">
      <a-select
        v-model="widget.configuration.search.keywordSearchColumns"
        :options="columnIndexOptions"
        :style="{ width: '100%' }"
        mode="multiple"
        show-search
        :filter-option="filterSelectOption"
      />
    </a-form-model-item>

    <a-form-model-item label="组合关键字搜索">
      <a-switch v-model="widget.configuration.search.columnSearchGroupEnable" />
    </a-form-model-item>

    <a-table
      v-show="widget.configuration.search.columnSearchGroupEnable"
      rowKey="id"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="columnSearchGroupTableColumn"
      :data-source="widget.configuration.search.columnSearchGroup"
      :locale="locale"
      class="widget-table-columnsearch-table no-border"
    >
      <template slot="titleSlot" slot-scope="text, record">
        <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" :style="{ cursor: 'move' }"></Icon>
        <a-input v-model="record.title" size="small" :style="{ width: '80px' }">
          <template slot="addonAfter">
            <WI18nInput :target="record" :code="record.id" v-model="record.title" :widget="widget" />
          </template>
        </a-input>
      </template>
      <template slot="dataIndexSlot" slot-scope="text, record">
        <a-select
          :options="columnIndexOptions"
          :style="{ width: '100%' }"
          size="small"
          v-model="record.dataIndex"
          allow-clear
          @change="onChangeColSelect(record)"
        />
        <a-select
          v-model="record.operator"
          :key="record.id + record.dataIndex"
          allow-clear
          :options="filterColumnOperatorOptions(record)"
          :style="{ width: '100%' }"
          size="small"
        />
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" @click="delColumnSearchGroupItem(index)" title="删除">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>

        <WidgetDesignDrawer :id="'tableKeywordSearchColConfig' + record.id" title="编辑组合字段查询" :designer="designer">
          <a-button type="link" size="small" title="编辑组合字段查询"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
          <template slot="content">
            <SearchColumnConfiguration
              :columnIndexOptions="columnIndexOptions"
              :column="record"
              :widget="widget"
              :designer="designer"
              ref="columnConf"
            />
          </template>
        </WidgetDesignDrawer>
      </template>
      <template slot="footer">
        <WidgetDesignDrawer :id="'tableKeywordSearchColConfig' + widget.id" title="新增组合字段查询" :designer="designer">
          <a-button size="small" type="link" icon="plus" :style="{ paddingLeft: '7px' }">新增</a-button>
          <template slot="content">
            <SearchColumnConfiguration
              :columnIndexOptions="columnIndexOptions"
              :column="newColumn"
              :widget="widget"
              :designer="designer"
              ref="columnConf"
            />
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
          </template>
        </WidgetDesignDrawer>
      </template>
    </a-table>
  </div>
</template>

<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';
import { columnSearchOperatorOptions } from '../../commons/constant';
import draggable from '@framework/vue/designer/draggable';
import SearchColumnConfiguration from './search-column-configuration.vue';

export default {
  name: 'WidgetTableKeywordSearchConfiguration',
  mixins: [draggable],
  props: {
    widget: Object,
    columnIndexOptions: Array,
    designer: Object
  },
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      newColumn: {
        id: generateId(),
        title: undefined,
        dataIndex: undefined,
        operator: undefined
      },
      options: {},
      selectedColumnSearchGroupRowKeys: [],
      selectedColumnSearchGroupRows: [],
      columnSearchGroupTableColumn: [
        { title: '标题', dataIndex: 'title', width: 120, scopedSlots: { customRender: 'titleSlot' } },
        { title: '字段操作', dataIndex: 'dataIndex', scopedSlots: { customRender: 'dataIndexSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      columnSearchOperatorOptions
    };
  },

  beforeCreate() {},
  components: { SearchColumnConfiguration },
  computed: {
    columnIndexOptionMap() {
      let map = {};
      for (let c of this.columnIndexOptions) {
        map[c.value] = c;
      }
      return map;
    }
  },
  created() {},
  methods: {
    onChangeColSelect(column) {
      if (column.dataIndex && column.operator) {
        let options = this.filterColumnOperatorOptions(column);
        if (options.length > 0) {
          for (let o of options) {
            if (o.value == column.operator) {
              return;
            }
          }
        }
        column.operator = undefined;
      }
    },
    filterColumnOperatorOptions(column) {
      if (column.dataIndex) {
        let col = this.columnIndexOptionMap[column.dataIndex];
        if (col) {
          if (col.dataType) {
            let matches = {
              string: ['eq', 'like', 'nlike', 'in', 'is null', 'is not null'],
              number: ['lt', 'le', 'gt', 'ge', 'eq', 'between', 'like', 'nlike'],
              date: ['lt', 'le', 'gt', 'ge', 'eq', 'between', 'like', 'nlike']
            };
            return this.columnSearchOperatorOptions.filter(item => {
              return matches[col.dataType] ? matches[col.dataType].includes(item.value) : true;
            });
          }
        }
      }
      return this.columnSearchOperatorOptions;
    },
    filterSelectOption,
    onConfirmOk() {
      this.widget.configuration.search.columnSearchGroup.push(deepClone(this.newColumn));
      this.newColumn = { id: generateId(), title: undefined, dataIndex: undefined, operator: undefined };
      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    },

    delColumnSearchGroupItem(index) {
      this.widget.configuration.search.columnSearchGroup.splice(index, 1);
    }
  },
  mounted() {
    this.tableDraggable(
      this.widget.configuration.columns,
      this.$el.querySelector('.widget-table-columnsearch-table tbody'),
      '.drag-column-handler'
    );
  }
};
</script>
