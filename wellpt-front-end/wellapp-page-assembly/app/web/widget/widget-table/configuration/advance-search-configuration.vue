<template>
  <div class="widget-edit-table-keywordsearch">
    <a-form-model-item label="高级搜索">
      <a-switch v-model="widget.configuration.search.advanceSearchEnable" />
    </a-form-model-item>
    <div v-show="widget.configuration.search.advanceSearchEnable">
      <a-form-model-item
        label="默认展开高级搜索"
        v-show="widget.configuration.search.columnSearchGroupEnable || widget.configuration.search.keywordSearchEnable"
      >
        <a-switch v-model="widget.configuration.search.defaultExpandAdvanceSearch" />
      </a-form-model-item>
      <a-form-model-item label="每行列数">
        <a-input-number :min="1" v-model="widget.configuration.search.advanceSearchPerRowColNumber" />
      </a-form-model-item>

      <a-table
        rowKey="id"
        :pagination="false"
        bordered
        size="small"
        :columns="columnAdvSearchGroupTableColumn"
        :data-source="widget.configuration.search.columnAdvanceSearchGroup"
        :locale="locale"
        class="pt-table widget-table-advancesearch-table no-border"
        style="--pt-table-td-padding: var(--w-padding-2xs)"
      >
        <template slot="titleSlot" slot-scope="text, record">
          <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" :style="{ cursor: 'move' }"></Icon>
          <a-input v-model="record.title" size="small" :style="{ width: '80px' }" class="addon-padding-3xs">
            <template slot="addonAfter">
              <WI18nInput :target="record" :code="record.id" v-model="record.title" :widget="widget" />
            </template>
          </a-input>
        </template>
        <template slot="dataIndexSlot" slot-scope="text, record">
          <a-select
            v-if="record.operator !== 'exists'"
            :options="columnIndexOptions"
            :style="{ width: '125px' }"
            size="small"
            v-model="record.dataIndex"
            allow-clear
            @change="onChangeColSelect(record)"
          ></a-select>
          <a-select
            :options="filterColumnOperatorOptions(record)"
            :style="{ width: '100%' }"
            :key="record.id + record.dataIndex"
            size="small"
            allow-clear
            v-model="record.operator"
          ></a-select>
        </template>

        <template slot="operationSlot" slot-scope="text, record, index">
          <WidgetDesignDrawer :id="'tableColConfig' + record.id" title="编辑" :designer="designer">
            <a-button type="link" size="small" title="编辑"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
            <template slot="content">
              <SearchColumnConfiguration
                :widget="widget"
                :designer="designer"
                :column="record"
                :columnIndexOptions="columnIndexOptions"
                :enableDefaultValue="true"
                :enableQueryTyepSet="true"
              />
            </template>
          </WidgetDesignDrawer>
          <a-button type="link" size="small" @click="delColumnAdvanceSearchGroupItem(index)" title="删除">
            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          </a-button>
        </template>

        <template slot="footer">
          <WidgetDesignDrawer :id="'tableColAdvanceSearchConfig' + widget.id" title="新增列" :designer="designer">
            <a-button type="link" icon="plus" :style="{ paddingLeft: '7px' }">新增</a-button>
            <template slot="content">
              <SearchColumnConfiguration
                :widget="widget"
                :designer="designer"
                :column="newAdvanceSearchGroup"
                :columnIndexOptions="columnIndexOptions"
                :enableDefaultValue="true"
                :enableQueryTyepSet="true"
              />
            </template>
            <template slot="footer">
              <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
            </template>
          </WidgetDesignDrawer>
        </template>
      </a-table>
    </div>
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import { columnSearchOperatorOptions, searchInputTypeOptions } from '../../commons/constant';
import SearchInputConfiguration from './search-input-configuration/index';
import SearchColumnConfiguration from './search-column-configuration.vue';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'AdvanceSearchConfiguration',
  inject: ['pageContext'],
  mixins: [draggable],
  props: {
    widget: Object,
    columnIndexOptions: Array,
    designer: Object
  },
  data() {
    return {
      options: {},
      advanceSearchInputTypeConfVisible: false,
      newAdvanceSearchGroup: {
        searchInputType: {
          type: undefined,
          options: {}
        },
        title: undefined,
        dataIndex: undefined,
        defaultValue: undefined,
        operator: undefined
      },
      locale: {
        emptyText: <span>暂无数据</span>
      },
      columnAdvSearchGroupTableColumn: [
        { title: '标题', dataIndex: 'title', width: 120, scopedSlots: { customRender: 'titleSlot' } },
        { title: '字段操作', dataIndex: 'dataIndex', scopedSlots: { customRender: 'dataIndexSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }

        // { title: '查询类型', dataIndex: 'searchInputType', width: 120, scopedSlots: { customRender: 'searchInputTypeSlot' } },
        // { title: '默认值', dataIndex: 'defaultValue', width: 120, scopedSlots: { customRender: 'defaultValueSlot' } },
        // { title: '操作符', dataIndex: 'operator', width: 200, scopedSlots: { customRender: 'operatorSlot' } }
      ],
      columnSearchOperatorOptions,
      searchInputTypeOptions: searchInputTypeOptions
    };
  },

  beforeCreate() {},
  components: { ...SearchInputConfiguration, SearchColumnConfiguration },
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
    onConfirmOk() {
      this.widget.configuration.search.columnAdvanceSearchGroup.push(deepClone(this.newAdvanceSearchGroup));
      this.newAdvanceSearchGroup = deepClone(this.defaultNewAdvanceSearchGroup);
      this.newAdvanceSearchGroup.id = generateId();
      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    },
    delColumnAdvanceSearchGroupItem(index) {
      this.widget.configuration.search.columnAdvanceSearchGroup.splice(index, 1);
    }
  },
  beforeMount() {
    this.defaultNewAdvanceSearchGroup = deepClone(this.newAdvanceSearchGroup);
    this.newAdvanceSearchGroup.id = generateId();
  },
  mounted() {
    this.tableDraggable(
      this.widget.configuration.search.columnAdvanceSearchGroup,
      this.$el.querySelector('.widget-table-advancesearch-table tbody'),
      '.drag-column-handler'
    );
  }
};
</script>
