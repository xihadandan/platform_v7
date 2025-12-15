<template>
  <div class="widget-edit-table-keywordsearch">
    <a-form-model-item label="高级搜索">
      <a-switch v-model="widget.configuration.search.advanceSearchEnable" />
    </a-form-model-item>
    <div v-show="widget.configuration.search.advanceSearchEnable">
      <template v-if="designer.terminalType !== 'mobile'">
        <a-form-model-item
          label="默认展开高级搜索"
          v-show="widget.configuration.search.columnSearchGroupEnable || widget.configuration.search.keywordSearchEnable"
        >
          <a-switch v-model="widget.configuration.search.defaultExpandAdvanceSearch" />
        </a-form-model-item>
        <a-form-model-item label="标题宽度">
          <a-input-number v-model="widget.configuration.search.advanceSearchLabelColWidth" />
        </a-form-model-item>
        <a-form-model-item label="控件区域宽度">
          <a-input-number v-model="widget.configuration.search.advanceSearchWrapperColWidth" />
        </a-form-model-item>
        <a-form-model-item label="标题对齐方式">
          <a-radio-group v-model="widget.configuration.search.advanceSearchLabelAlign" button-style="solid" size="small">
            <a-radio-button value="left">左对齐</a-radio-button>
            <a-radio-button value="right">右对齐</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </template>

      <!-- <a-form-model-item label="每行列数">
        <a-input-number :min="1" v-model="widget.configuration.search.advanceSearchPerRowColNumber" />
      </a-form-model-item> -->

      <a-table
        rowKey="id"
        :pagination="false"
        bordered
        size="small"
        :columns="columnAdvSearchGroupTableColumn"
        :data-source="widget.configuration.search.columnAdvanceSearchGroup"
        :locale="locale"
        class="widget-table-advancesearch-table no-border"
      >
        <template slot="titleSlot" slot-scope="text, record">
          <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" :style="{ cursor: 'move' }" />
          <a-input v-model="record.title" size="small" :style="{ width: '190px' }">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" :code="record.id" v-model="record.title" :target="record" />
            </template>
          </a-input>
        </template>

        <template slot="operationSlot" slot-scope="text, record, index">
          <WidgetDesignDrawer :id="'tableColConfig' + record.id" title="编辑" :designer="designer">
            <a-button type="link" size="small" title="编辑">
              <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            </a-button>
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
import SearchColumnConfiguration from './search-column-configuration.vue';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'WidgetSubformAdvanceSearchConfiguration',
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
        id: generateId(),
        searchInputType: {
          type: undefined,
          options: {}
        },
        title: undefined,
        dataIndex: undefined,
        separator: '至',
        defaultValue: undefined,
        comparator: undefined
      },
      locale: {
        emptyText: <span>暂无数据</span>
      },
      columnAdvSearchGroupTableColumn: [
        { title: '标题', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        // { title: '字段操作', dataIndex: 'dataIndex', scopedSlots: { customRender: 'dataIndexSlot' } },
        { title: '操作', dataIndex: 'operation', width: 90, scopedSlots: { customRender: 'operationSlot' } }

        // { title: '查询类型', dataIndex: 'searchInputType', width: 120, scopedSlots: { customRender: 'searchInputTypeSlot' } },
        // { title: '默认值', dataIndex: 'defaultValue', width: 120, scopedSlots: { customRender: 'defaultValueSlot' } },
        // { title: '操作符', dataIndex: 'operator', width: 200, scopedSlots: { customRender: 'operatorSlot' } }
      ]
    };
  },

  beforeCreate() {},
  components: { SearchColumnConfiguration },
  computed: {},
  created() {},
  methods: {
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
  },
  mounted() {
    this.tableDraggable(
      this.widget.configuration.columns,
      this.$el.querySelector('.widget-table-advancesearch-table tbody'),
      '.drag-column-handler'
    );
  }
};
</script>
