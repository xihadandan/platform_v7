<template>
  <div>
    <a-table
      rowKey="id"
      :showHeader="false"
      :pagination="false"
      size="small"
      :bordered="false"
      :columns="columnDefine"
      :locale="locale"
      :data-source="columnData"
      class="subform-col-sort-table no-border"
    >
      <template slot="title">
        <i class="line" />
        {{ vTitle }}
      </template>
      <template slot="titleSlot" slot-scope="text, record">
        <Icon
          title="拖动排序"
          type="pticon iconfont icon-ptkj-tuodong"
          class="drag-column-handler drag-handler"
          :style="{ cursor: 'move' }"
        ></Icon>
        <span>{{ columnNameMap[record.dataIndex] }}</span>
      </template>

      <template slot="sortRuleSlot" slot-scope="text, record">
        <a-tag v-if="columnIndexes.length > 0 && record.dataIndex != undefined && !columnIndexes.includes(record.dataIndex)" color="red">
          字段不存在
        </a-tag>
        <template v-else>
          <a-button type="link" size="small" @click.stop="onClickSortRule(record)">
            {{ record.sortType === 'asc' ? '升序' : '降序' }}
          </a-button>
        </template>
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" @click="delColumn(index)" title="删除">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
        <WidgetDesignDrawer :id="'subformColConfig' + record.id" :title="vTitle" :designer="designer" :destroyOnClose="true">
          <a-button type="link" size="small" @click="setColumn(record)" title="设置">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          </a-button>
          <template slot="content">
            <ColumnSortConfiguration
              :fieldSelectOptions="fieldOptions"
              :sortColumn="record"
              :widget="widget"
              :designer="designer"
              :columnIndexOptions="columnIndexOptions"
              @change="changeField"
            />
          </template>
        </WidgetDesignDrawer>
      </template>
      <template slot="footer">
        <WidgetDesignDrawer :id="'subformColConfig' + widget.id" :title="vTitle" :designer="designer" :destroyOnClose="true">
          <a-button type="link" size="small" @click="addColumn" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            {{ '添加' + vTitle }}
          </a-button>
          <template slot="content">
            <ColumnSortConfiguration
              :fieldSelectOptions="fieldOptions"
              :sortColumn="columnTarget"
              :widget="widget"
              :columnIndexOptions="columnIndexOptions"
              :designer="designer"
              @change="changeField"
              v-if="columnData.length > 0"
            />
          </template>
        </WidgetDesignDrawer>
      </template>
    </a-table>
  </div>
</template>
<script type="text/babel">
import { generateId, swapArrayElements } from '@framework/vue/utils/util';
import ColumnSortConfiguration from './column-sort-configuration.vue';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'SubformColumnSortConfiguration',
  mixins: [draggable],
  inject: ['pageContext'],
  props: {
    widget: Object,
    designer: Object,
    columnIndexOptions: Array
  },
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      columnTarget: {},
      columnDefine: [
        { title: '标题', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '排序规则', dataIndex: 'rule', width: 72, scopedSlots: { customRender: 'sortRuleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' } }
      ],
      dataIndex: '',
      columnNameMap: {},
      columnsMap: {}
    };
  },
  components: { ColumnSortConfiguration },
  computed: {
    vTitle() {
      return '排序字段';
    },
    fieldSelectOptions() {
      let options = [];
      for (let i = 0, len = this.columnIndexOptions.length; i < len; i++) {
        options.push({
          label: this.columnIndexOptions[i].configuration.name,
          value: this.columnIndexOptions[i].configuration.code
        });
      }
      return options;
    },
    columnIndexes() {
      return this.columnIndexOptions.map(item => item.configuration.code);
    },
    fieldOptions() {
      let options = [];
      let columnNameMap = {};
      for (let i = 0, len = this.columnIndexOptions.length; i < len; i++) {
        let has = false;
        const columnSub = this.columnIndexOptions[i].configuration;
        columnNameMap[columnSub.code] = columnSub.name;
        let column = this.widget.configuration.columns.find(item => item.dataIndex == columnSub.code);
        if (column) {
          columnNameMap[columnSub.code] = column.title;
        }
        let hasIndex = this.widget.configuration.defaultSort.columns.findIndex(item => item.dataIndex == columnSub.code);
        if (hasIndex > -1 && columnSub.code !== this.dataIndex) {
          has = true;
        }
        if (!has) {
          options.push({
            label: columnSub.name,
            value: columnSub.code
          });
        }
      }
      this.columnNameMap = columnNameMap;
      this.dataIndex = '';
      return options;
    },
    columnData() {
      return this.widget.configuration.defaultSort.columns;
    }
  },
  methods: {
    changeField(value) {
      this.dataIndex = value;
      let curField = {};
      this.columnIndexOptions.forEach(item => {
        if (item.configuration.code === value) {
          curField = item;
        }
      });
      this.widget.configuration.columns.forEach(item => {
        if (item.dataIndex === value) {
          item.widget = curField;
          item.title = curField.configuration.name;
        }
      });
    },
    setColumn(column) {
      this.dataIndex = column.dataIndex;
    },
    addColumn() {
      if (this.widget.configuration.defaultSort.columns.length > 0 && this.widget.configuration.defaultSort.columns[0].id === undefined) {
        this.widget.configuration.defaultSort.columns = [];
      }
      this.widget.configuration.defaultSort.columns.push({
        id: generateId(),
        dataIndex: undefined,
        sortType: 'asc',
        sortable: {
          enable: false,
          alogrithmType: 'orderByChar',
          datePattern: undefined,
          script: null
        }
      });
      this.columnTarget = this.widget.configuration.defaultSort.columns[this.widget.configuration.defaultSort.columns.length - 1];
    },
    delColumn(index) {
      this.widget.configuration.defaultSort.columns.splice(index, 1);
    },
    onClickSortRule(record) {
      record.sortType = record.sortType === 'asc' ? 'desc' : 'asc';
    }
  },
  mounted() {
    this.tableDraggable(
      this.widget.configuration.defaultSort.columns,
      this.$el.querySelector('.subform-col-sort-table tbody'),
      '.drag-column-handler'
    );
  }
};
</script>
