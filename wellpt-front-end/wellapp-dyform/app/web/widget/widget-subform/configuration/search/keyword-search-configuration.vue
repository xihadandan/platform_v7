<template>
  <div class="widget-edit-table-keywordsearch">
    <a-form-model-item label="关键字搜索">
      <a-switch v-model="widget.configuration.search.keywordSearchEnable" />
    </a-form-model-item>
    <a-form-model-item label="关键字搜索字段" v-show="widget.configuration.search.keywordSearchEnable">
      <a-select
        v-model="widget.configuration.search.keywordSearchColumns"
        :options="fieldSelectOptions"
        :style="{ width: '100%' }"
        mode="multiple"
        show-search
        :filter-option="filterSelectOption"
      />
    </a-form-model-item>
    <a-form-model-item label="关键字输入框提示语" v-show="widget.configuration.search.keywordSearchEnable">
      <a-input v-model.trim="widget.configuration.search.keywordSearchPlaceholder">
        <template slot="addonAfter">
          <WI18nInput
            :widget="widget"
            :designer="designer"
            code="keywordSearchPlaceholder"
            v-model="widget.configuration.search.keywordSearchPlaceholder"
            :target="widget.configuration.search"
          />
        </template>
      </a-input>
    </a-form-model-item>

    <!-- <a-form-model-item label="组合关键字搜索">
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
        <a-icon type="menu" class="drag-column-handler" :style="{ cursor: 'move' }" />
        <a-input v-model="record.title" size="small" :style="{ width: '70px' }" />
      </template>
      <template slot="dataIndexSlot" slot-scope="text, record">
        <a-select :options="columnIndexOptions" :style="{ width: '100%' }" size="small" v-model="record.dataIndex" />
        <a-select v-model="record.operator" :options="operatorOption" :style="{ width: '100%' }" size="small" />
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-icon type="delete" @click="delColumnSearchGroupItem(index)" />
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
    </a-table> -->
  </div>
</template>

<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';
import draggable from '@framework/vue/designer/draggable';
import SearchColumnConfiguration from './search-column-configuration.vue';

export default {
  name: 'WidgetSubformKeywordSearchConfiguration',
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
        title: undefined,
        dataIndex: undefined,
        operator: undefined
      },
      options: {},
      selectedColumnSearchGroupRowKeys: [],
      selectedColumnSearchGroupRows: [],
      columnSearchGroupTableColumn: [
        { title: '标题', dataIndex: 'title', width: 116, scopedSlots: { customRender: 'titleSlot' } },
        { title: '字段操作', dataIndex: 'dataIndex', scopedSlots: { customRender: 'dataIndexSlot' } },
        { title: '操作', dataIndex: 'operation', width: 44, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },

  beforeCreate() {},
  components: { SearchColumnConfiguration },
  computed: {
    fieldSelectOptions() {
      let fieldSelectOptions = [];
      for (let i = 0, len = this.columnIndexOptions.length; i < len; i++) {
        if (!['WidgetFormJobSelect', 'WidgetFormColor'].includes(this.columnIndexOptions[i].wtype)) {
          fieldSelectOptions.push({
            label: this.columnIndexOptions[i].configuration.name,
            value: this.columnIndexOptions[i].configuration.code
          });
        }
      }
      return fieldSelectOptions;
    }
  },
  created() {},
  methods: {
    filterSelectOption,
    onConfirmOk() {
      this.newColumn.id = generateId();
      this.widget.configuration.search.columnSearchGroup.push(deepClone(this.newColumn));
      this.newColumn = {
        title: undefined,
        dataIndex: undefined,
        operator: undefined
      };
      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    },

    delColumnSearchGroupItem(index) {
      this.widget.configuration.search.columnSearchGroup.splice(index, 1);
    }
  },
  mounted() {}
};
</script>
