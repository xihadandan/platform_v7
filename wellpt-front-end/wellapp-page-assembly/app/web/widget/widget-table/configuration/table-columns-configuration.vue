<template>
  <div>
    <a-table
      rowKey="id"
      :pagination="false"
      size="small"
      :bordered="false"
      :columns="columnDefine"
      :locale="locale"
      :data-source="widget.configuration.columns"
      class="pt-table widget-table-col-table no-border"
    >
      <template slot="titleSlot" slot-scope="text, record">
        <Icon
          title="拖动排序"
          type="pticon iconfont icon-ptkj-tuodong"
          class="drag-column-handler drag-handler"
          :style="{ cursor: 'move' }"
        ></Icon>
        <a-input
          v-model="record.title"
          size="small"
          :style="{ width: '100px' }"
          :title="record.title"
          @change="onChangeTitle(record)"
          class="addon-padding-3xs"
        >
          <template slot="addonAfter">
            <template v-if="record.primaryKey"><a-icon type="key" title="主键列" /></template>
            <WI18nInput
              v-show="!record.titleHidden"
              :widget="widget"
              :designer="designer"
              :code="record.id"
              :target="record"
              v-model="record.title"
            />
          </template>
        </a-input>
      </template>
      <template slot="widthSlot" slot-scope="text, record">
        <a-input-number :style="{ width: '70px' }" v-model="record.width" size="small" :min="0" />
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button
          type="link"
          size="small"
          @click="onClickVisible(record)"
          :title="
            record.hidden || (widget.configuration.enableCustomTable && record.customVisibleType === 'defaultHidden') ? '显示' : '隐藏'
          "
        >
          <Icon
            :type="
              record.hidden || (widget.configuration.enableCustomTable && record.customVisibleType === 'defaultHidden')
                ? 'pticon iconfont icon-wsbs-yincang'
                : 'pticon iconfont icon-wsbs-xianshi'
            "
          ></Icon>
        </a-button>
        <a-button type="link" size="small" @click="delColumn(index)" title="删除列">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
        <WidgetDesignDrawer :id="'tableColConfig' + record.id" title="编辑列" :designer="designer">
          <a-button type="link" size="small" title="编辑列">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          </a-button>
          <template slot="content">
            <ColumnConfiguration :columnIndexOptions="columnIndexOptions" :column="record" :widget="widget" :designer="designer" />
          </template>
        </WidgetDesignDrawer>
      </template>
      <template slot="footer">
        <WidgetDesignDrawer :id="'tableColConfig' + widget.id" title="新增列" :designer="designer">
          <a-button type="link" size="small" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            新增
          </a-button>
          <template slot="content">
            <ColumnConfiguration
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
import ColumnConfiguration from './column-configuration.vue';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'TableColumnsConfiguration',
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
      newColumn: {
        id: generateId(),
        role: [],
        sortable: false,
        renderFunction: { type: undefined, options: {} },
        exportFunction: { type: undefined, options: {} },
        dataIndex: undefined,
        title: undefined,
        primaryKey: false,
        hidden: false,
        ellipsis: true,
        customVisibleType: 'chooseVisible', // 默认为可选显示
        showTip: false,
        tipContent: undefined,
        titleAlign: 'left',
        contentAlign: 'left'
      },
      columnDefine: [
        { title: '标题', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '列宽(px)', dataIndex: 'width', width: 60, scopedSlots: { customRender: 'widthSlot' }, align: 'center' },
        { title: '操作', dataIndex: 'operation', width: 110, scopedSlots: { customRender: 'operationSlot' }, align: 'center' }
      ]
    };
  },

  beforeCreate() {},
  components: { ColumnConfiguration },
  computed: {},
  created() {},
  methods: {
    onChangeTitle(item) {
      if (item.i18n && item.i18n.zh_CN) {
        item.i18n.zh_CN[item.id] = item.title;
      }
    },
    onConfirmOk() {
      if (this.newColumn.primaryKey) {
        for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
          this.widget.configuration.columns[i].primaryKey = false;
        }
      }
      this.widget.configuration.columns.push(deepClone(this.newColumn));
      this.newColumn = deepClone(this.defaultNewColumn);
      this.newColumn.id = generateId();
      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
    },
    delColumn(index) {
      this.widget.configuration.columns.splice(index, 1);
    },
    onClickVisible(record) {
      this.$set(record, 'hidden', !record.hidden);
      if (!record.hidden && this.widget.configuration.enableCustomTable) {
        record.customVisibleType = 'chooseVisible';
      }
    }
  },
  beforeMount() {
    this.defaultNewColumn = deepClone(this.newColumn);
  },
  mounted() {
    this.tableDraggable(this.widget.configuration.columns, this.$el.querySelector('.widget-table-col-table tbody'), '.drag-column-handler');
  }
};
</script>
