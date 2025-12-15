<template>
  <div class="widget-edit-table-keywordsearch">
    <a-form-model-item label="字段查询">
      <a-switch v-model="widget.configuration.fieldSearch" @change="onFieldSearchChange" />
    </a-form-model-item>
    <div v-show="widget.configuration.fieldSearch">
      <a-table
        class="pt-table widget-table-advancesearch-table"
        style="--pt-table-td-padding: var(--w-padding-2xs)"
        rowKey="uuid"
        :pagination="false"
        bordered
        size="small"
        :columns="fieldSearchGroupTableColumn"
        :data-source="widget.configuration.query.fields"
        :locale="locale"
      >
        <template slot="titleSlot" slot-scope="text, record">
          <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" :style="{ cursor: 'move' }" />
          <a-input v-model="record.label" size="small" :style="{ width: '80px' }" class="addon-padding-3xs">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" :code="record.uuid" :target="record" v-model="record.label" />
            </template>
          </a-input>
        </template>
        <template slot="dataIndexSlot" slot-scope="text, record">
          <a-select
            v-if="record.operator !== 'exists'"
            :options="columnIndexOptions"
            :style="{ width: '120px' }"
            size="small"
            v-model="record.name"
            :showSearch="true"
            :filter-option="filterSelectOption"
          ></a-select>
          <a-select
            :showSearch="true"
            :filter-option="filterSelectOption"
            :options="operatorOption"
            :style="{ width: '120px' }"
            size="small"
            v-model="record.operator"
          ></a-select>
        </template>

        <template slot="operationSlot" slot-scope="text, record, index">
          <WidgetDesignDrawer :id="'listFieldSearchConfig' + record.uuid" title="编辑" :designer="designer">
            <a-button type="link" size="small" title="编辑">
              <Icon type="pticon iconfont icon-ptkj-shezhi" />
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
          <a-button type="link" size="small" @click="delFieldSearchGroupItem(index)" title="删除">
            <Icon type="pticon iconfont icon-ptkj-shanchu" />
          </a-button>
        </template>

        <template slot="footer">
          <WidgetDesignDrawer :id="'listFieldSearchConfig' + widget.id" title="新增" :designer="designer">
            <a-button type="link" :style="{ paddingLeft: '7px' }">
              <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
              新增
            </a-button>
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
import { columnSearchOperatorOptions, searchInputTypeOptions } from '@pageAssembly/app/web/widget/commons/constant';
import SearchInputConfiguration from '@pageAssembly/app/web/widget/widget-table/configuration/search-input-configuration/index';
import SearchColumnConfiguration from './search-column-configuration.vue';
import draggable from '@framework/vue/designer/draggable';
import { filterSelectOption } from '@framework/vue/utils/function';

export default {
  name: 'FieldSearchConfiguration',
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
      newAdvanceSearchGroup: {
        queryOptions: {
          queryType: undefined,
          options: {}
        },
        label: undefined,
        name: undefined,
        defaultValue: undefined,
        operator: undefined
      },
      locale: {
        emptyText: <span>暂无数据</span>
      },
      fieldSearchGroupTableColumn: [
        { title: '标题', dataIndex: 'label', width: 120, scopedSlots: { customRender: 'titleSlot' } },
        { title: '字段操作', dataIndex: 'name', scopedSlots: { customRender: 'dataIndexSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      operatorOption: columnSearchOperatorOptions,
      searchInputTypeOptions: searchInputTypeOptions
    };
  },

  beforeCreate() {},
  components: { ...SearchInputConfiguration, SearchColumnConfiguration },
  computed: {},
  created() {
    if (!this.widget.configuration.query) {
      this.$set(this.widget.configuration, 'query', { fields: [] });
    } else if (!this.widget.configuration.query.fields) {
      this.$set(this.widget.configuration.query, 'fields', []);
    }
  },
  methods: {
    filterSelectOption,
    onConfirmOk() {
      this.widget.configuration.query.fields.push(deepClone(this.newAdvanceSearchGroup));
      this.newAdvanceSearchGroup = deepClone(this.defaultNewAdvanceSearchGroup);
      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    },
    delFieldSearchGroupItem(index) {
      this.widget.configuration.query.fields.splice(index, 1);
    },
    onFieldSearchChange() {
      this.widget.configuration.query.fieldSearch = this.widget.configuration.fieldSearch;
    }
  },
  beforeMount() {
    this.defaultNewAdvanceSearchGroup = deepClone(this.newAdvanceSearchGroup);
  },
  mounted() {
    this.tableDraggable(
      this.widget.configuration.query.fields,
      this.$el.querySelector('.widget-table-advancesearch-table tbody'),
      '.drag-column-handler'
    );
  }
};
</script>
