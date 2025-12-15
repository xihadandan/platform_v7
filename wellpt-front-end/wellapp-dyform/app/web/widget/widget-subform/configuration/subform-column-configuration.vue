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
      class="subform-col-table no-border"
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
        <span :class="record.required ? 'required-star-after' : ''">{{ text }}</span>
      </template>

      <template slot="widthSlot" slot-scope="text, record">
        <!-- <a-tag v-show="text" color="blue">{{ text }}px</a-tag> -->
        <a-tag v-if="columnIndexes.length > 0 && record.dataIndex != undefined && !columnIndexes.includes(record.dataIndex)" color="red">
          字段不存在
        </a-tag>
        <template v-else>
          {{ text ? text + 'px' : '' }}
          <Icon type="pticon iconfont icon-wsbs-yincang" v-show="record.defaultDisplayState === 'hidden'" />
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
            <ColumnConfiguration
              :fieldSelectOptions="fieldOptions"
              :column="record"
              :widget="widget"
              :designer="designer"
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
            <ColumnConfiguration
              :fieldSelectOptions="fieldOptions"
              :column="columnTarget"
              :widget="widget"
              :designer="designer"
              @change="changeField"
              v-if="columnData.length > 0"
            />
          </template>
        </WidgetDesignDrawer>
      </template>
    </a-table>

    <!-- <a-form-model-item label="行标题列" v-if="designer.terminalType == 'mobile'">
      <a-select v-model="widget.configuration.uniConfiguration.rowTitleColumn" style="width: 100%" :options="fieldSelectOptions" />
    </a-form-model-item> -->
  </div>
</template>
<script type="text/babel">
import { generateId, swapArrayElements } from '@framework/vue/utils/util';
import ColumnConfiguration from './column-configuration.vue';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'SubformColumnConfiguration',
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
        { title: '宽度', dataIndex: 'width', width: 100, scopedSlots: { customRender: 'widthSlot' }, align: 'center' },
        { title: '字段', dataIndex: 'operation', width: 80, scopedSlots: { customRender: 'operationSlot' }, align: 'center' }
      ],
      dataIndex: ''
    };
  },
  components: { ColumnConfiguration },
  computed: {
    vTitle() {
      if (this.designer.terminalType == 'mobile') {
        return '字段';
      }
      return this.widget.configuration.layout == 'table' ? '表格列' : '字段';
    },
    fieldSelectOptions() {
      if (this.widget.configuration.columns.length && this.widget.configuration.columns[0].id == undefined) {
        return [];
      }
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
      for (let i = 0, len = this.columnIndexOptions.length; i < len; i++) {
        let has = false;
        const columnSub = this.columnIndexOptions[i].configuration;
        for (let index = 0; index < this.widget.configuration.columns.length; index++) {
          const column = this.widget.configuration.columns[index];
          if (column.dataIndex == columnSub.code) {
            has = true;
          }
        }
        if (columnSub.code == this.dataIndex) {
          has = false;
        }
        if (!has) {
          options.push({
            label: columnSub.name,
            value: columnSub.code
          });
        }
      }
      this.dataIndex = '';
      return options;
    },
    columnData() {
      return this.widget.configuration.columns;
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
      if (this.widget.configuration.columns.length > 0 && this.widget.configuration.columns[0].id === undefined) {
        this.widget.configuration.columns = [];
      }
      this.widget.configuration.columns.push({
        id: generateId(),
        title: '',
        dataIndex: undefined,
        defaultDisplayState: 'edit',
        sortable: {
          enable: false,
          alogrithmType: 'orderByChar',
          datePattern: undefined,
          script: null
        },
        width: null,
        required: false,
        titleAlign: 'left',
        contentAlign: 'left',
        ellipsis: true
        // widget:
      });
      this.columnTarget = this.widget.configuration.columns[this.widget.configuration.columns.length - 1];
    },
    delColumn(index) {
      this.widget.configuration.columns.splice(index, 1);
    }
  },
  mounted() {
    this.tableDraggable(this.widget.configuration.columns, this.$el.querySelector('.subform-col-table tbody'), '.drag-column-handler');
  }
};
</script>
