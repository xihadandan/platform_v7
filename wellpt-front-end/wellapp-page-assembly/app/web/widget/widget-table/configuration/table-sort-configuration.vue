<template>
  <div>
    <a-table
      rowKey="id"
      :pagination="false"
      size="small"
      :bordered="false"
      :columns="columnDefine"
      :locale="locale"
      :data-source="widget.configuration.defaultSort"
      style="--pt-table-td-padding: var(--w-padding-2xs)"
      class="pt-table widget-table-sort-table no-border"
    >
      <template slot="dataIndexSlot" slot-scope="text, record, index">
        <Icon
          title="拖动排序"
          type="pticon iconfont icon-ptkj-tuodong"
          class="drag-column-handler drag-handler"
          :style="{ cursor: 'move' }"
        ></Icon>
        <a-select size="small" :style="{ width: '88%' }" v-model="record.dataIndex">
          <template v-for="(item, i) in columnIndexOptions">
            <a-select-option
              :key="'sortColOption' + i"
              :value="item.value"
              v-if="item.value == record.dataIndex || !selectedDataIndex.includes(item.value)"
            >
              {{ item.label }}
            </a-select-option>
          </template>
        </a-select>
      </template>
      <template slot="sortRuleSlot" slot-scope="text, record">
        <a-button type="link" size="small" @click.stop="onClickSortRule(record)">
          {{ record.sortType === 'asc' ? '升序' : '降序' }}
        </a-button>
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" @click="delSortColumn(index)" title="删除">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
      </template>
      <template slot="footer">
        <a-button @click.stop="addSortColumn" size="small" type="link" :style="{ paddingLeft: '7px' }">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          新增
        </a-button>
      </template>
    </a-table>
  </div>
</template>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'TableSortConfiguration',
  mixins: [draggable],
  inject: ['pageContext'],
  props: {
    widget: Object,
    columnIndexOptions: Array
  },
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      newSortColumn: {
        dataIndex: undefined,
        sortType: 'asc'
      },
      columnDefine: [
        { title: '排序列', dataIndex: 'dataIndex', scopedSlots: { customRender: 'dataIndexSlot' } },
        { title: '排序方式', dataIndex: 'rule', width: 72, scopedSlots: { customRender: 'sortRuleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 44, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    selectedDataIndex() {
      let index = [];
      for (let i = 0, len = this.widget.configuration.defaultSort.length; i < len; i++) {
        if (this.widget.configuration.defaultSort[i].dataIndex) {
          index.push(this.widget.configuration.defaultSort[i].dataIndex);
        }
      }
      return index;
    }
  },
  created() {},
  methods: {
    onClickSortRule(record) {
      record.sortType = record.sortType === 'asc' ? 'desc' : 'asc';
    },
    addSortColumn() {
      this.widget.configuration.defaultSort.push({
        id: generateId(),
        dataIndex: undefined,
        sortType: 'asc'
      });
    },
    delSortColumn(index) {
      this.widget.configuration.defaultSort.splice(index, 1);
    }
  },
  beforeMount() {},
  mounted() {
    this.tableDraggable(
      this.widget.configuration.defaultSort,
      this.$el.querySelector('.widget-table-sort-table tbody'),
      '.drag-column-handler'
    );
  }
};
</script>
