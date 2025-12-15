<template>
  <div>
    <a-form-model>
      <a-form-model-item label="数据仓库查询">
        <DataStoreSelectModal v-model="dataSourceId" :displayModal="true" @change="changeDataSourceId" />
      </a-form-model-item>
    </a-form-model>

    <a-table
      rowKey="id"
      :pagination="false"
      :bordered="false"
      size="small"
      :columns="columns"
      :data-source="columnDataSource"
      class="column-table"
    >
      <template slot="title">
        <i class="line" />
        列定义
      </template>
      <template slot="titleSlot" slot-scope="text, record">
        <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" :style="{ cursor: 'move' }" title="拖动调整顺序" />
        <a-input size="small" v-model="record.title" style="width: 200px">
          <a-tooltip placement="right" slot="suffix" :title="record.dataIndex">
            <a-icon type="code" title="字段编码" />
          </a-tooltip>
        </a-input>
      </template>
      <template slot="widthSlot" slot-scope="text, record">
        <a-input-number size="small" v-model="record.width" />
      </template>
      <template slot="customHiddenTitle">
        <a-checkbox :indeterminate="colVisibleIndeterminate" :checked="checkAllColVisible" @change="onCheckAllVisibleColumn" />
        是否显示
      </template>
      <template slot="hiddenSlot" slot-scope="text, record">
        <a-switch size="small" :checked="!record.hidden" @change="e => changeHidden(record, e)" />
      </template>
      <template slot="primaryKeySlot" slot-scope="text, record">
        <a-switch size="small" :checked="record.primaryKey" @change="checked => onPrimaryKeyChange(checked, record)" />
      </template>
    </a-table>
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'DatastoreConfiguration',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      dataIndexVisible: false,
      dataSourceOptions: [],
      dataSourceId: this.widget.configuration.dataSourceId,
      dataSourceName: this.widget.configuration.dataSourceName,
      columnDataSource: [],
      columns: [
        { title: '标题', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '列宽', dataIndex: 'width', width: 100, scopedSlots: { customRender: 'widthSlot' }, align: 'center' },
        {
          dataIndex: 'hidden',
          width: 100,
          scopedSlots: { customRender: 'hiddenSlot' },
          align: 'center',
          slots: { title: 'customHiddenTitle' }
        },
        { title: '是否主键', dataIndex: 'primaryKey', width: 100, scopedSlots: { customRender: 'primaryKeySlot' }, align: 'center' }
      ],
      colVisibleIndeterminate: false,
      checkAllColVisible: false
    };
  },
  computed: {
    colVisibleCount() {
      let cnt = 0;
      this.columnDataSource.forEach(col => {
        cnt += col.hidden === true ? 0 : 1;
      });
      return cnt;
    }
  },
  created() {
    if (this.dataSourceId) {
      this.updateColumnDataSourceIfRequired();
    }
  },
  mounted() {
    if (this.dataSourceId != null) {
      this.columnDataSource = deepClone(this.widget.configuration.columns);
    }
    this.fetchDataSourceOptions();
    this.tableDraggable(this.columnDataSource, this.$el.querySelector('.column-table tbody'), '.drag-column-handler');
    this.changeColumnHidden();
  },
  methods: {
    changeHidden(item, e) {
      item.hidden = !item.hidden;
      this.changeColumnHidden();
    },
    changeColumnHidden() {
      this.$nextTick(() => {
        this.checkAllColVisible = this.colVisibleCount > 0 && this.colVisibleCount == this.columnDataSource.length;
        this.colVisibleIndeterminate =
          this.columnDataSource.length > 0 && this.colVisibleCount > 0 && this.colVisibleCount < this.columnDataSource.length;
      });
    },
    onCheckAllVisibleColumn(e) {
      this.columnDataSource.forEach(col => {
        col.hidden = !e.target.checked;
      });
      this.changeColumnHidden();
    },
    reset() {
      this.dataSourceId = undefined;
      this.columnDataSource.splice(0, this.columnDataSource.length);
      this.changeColumnHidden();
    },
    onPrimaryKeyChange(checked, record) {
      if (checked) {
        for (let i = 0, len = this.columnDataSource.length; i < len; i++) {
          this.columnDataSource[i].primaryKey = false;
        }
      }
      record.primaryKey = checked;
    },
    setPrimaryKey(record) {
      for (let i = 0, len = this.columnDataSource.length; i < len; i++) {
        this.columnDataSource[i].primaryKey = false;
      }
      record.primaryKey = true;
    },

    changeDataSourceId(value, opt) {
      let _this = this;
      _this.dataSourceName = opt.text ? opt.text : opt.componentOptions ? opt.componentOptions.children[0].text.trim() : undefined;
      _this.fetchColumns().then(({ data }) => {
        if (data.code == 0 && data.data) {
          _this.columnDataSource.length = 0;
          for (let i = 0, len = data.data.length; i < len; i++) {
            _this.columnDataSource.push(_this.dataSourceColumn2TableDefaultColumn(data.data[i]));
          }
          _this.changeColumnHidden();
        }
      });
    },
    dataSourceColumn2TableDefaultColumn(column) {
      let col = {
        id: generateId(),
        title: column.title,
        dataIndex: column.columnIndex,
        primaryKey: false,
        ellipsis: true,
        sortable: false,
        hidden: column.hidden === true,
        keywordQuery: false,
        width: undefined,
        renderFunction: { type: undefined, options: {} },
        exportFunction: { type: undefined, options: {} },
        customVisibleType: 'chooseVisible',
        showTip: false,
        tipContent: undefined,
        titleAlign: 'left',
        contentAlign: 'left'
      };
      if (col.dataIndex.toLowerCase() === 'uuid') {
        // 默认设置uuid为主键列
        col.primaryKey = true;
      }
      return col;
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
    fetchColumns() {
      return $axios.post('/json/data/services', {
        serviceName: 'viewComponentService',
        methodName: 'getColumnsById',
        args: JSON.stringify([this.dataSourceId])
      });
    },
    updateColumnDataSourceIfRequired() {
      let _this = this;
      _this.fetchColumns().then(({ data }) => {
        let columns = data.data;
        if (data.code == 0 && columns) {
          // 添加列
          let addedColumns = [];
          columns.forEach(column => {
            let index = _this.columnDataSource.findIndex(source => source.dataIndex == column.columnIndex);
            if (index == -1) {
              addedColumns.push(column);
            }
          });
          if (addedColumns.length > 0) {
            addedColumns.reverse();
            let addedColumnTitles = [];
            addedColumns.forEach(addedColumn => {
              addedColumnTitles.push(addedColumn.title);
              _this.columnDataSource.unshift(_this.dataSourceColumn2TableDefaultColumn(addedColumn));
            });
            _this.$message.info(`自动加载数据仓库添加的列[${addedColumnTitles}]`);
          }

          // 删除列
          let deletedColumns = [];
          _this.columnDataSource.forEach(source => {
            let index = columns.findIndex(column => source.dataIndex == column.columnIndex);
            if (index == -1) {
              deletedColumns.push(source);
            }
          });
          if (deletedColumns.length > 0) {
            let deleteColumnTitles = [];
            deletedColumns.forEach(deletedColumn => {
              deleteColumnTitles.push(deletedColumn.title);
              let index = _this.columnDataSource.findIndex(source => source.dataIndex == deletedColumn.dataIndex);
              _this.columnDataSource.splice(index, 1);
            });
            _this.$message.info(`自动删除数据仓库不存在的列[${deleteColumnTitles}]`);
          }
        }
      });
    },
    onConfirmOk() {
      let primaryKey = false;
      let columnIndexOptions = [];
      for (let i = 0, len = this.columnDataSource.length; i < len; i++) {
        if (this.columnDataSource[i].primaryKey) {
          primaryKey = true;
        }
        columnIndexOptions.push({
          label: this.columnDataSource[i].title,
          value: this.columnDataSource[i].dataIndex,
          dataType: { NUMBER: 'number', 'TIMESTAMP(6)': 'date', VARCHAR2: 'string' }[this.columnDataSource[i].columnType] || 'string'
        });
      }
      if (!primaryKey) {
        this.$message.error('请设置主键列');
        return false;
      }
      this.widget.configuration.columns.splice(0, this.widget.configuration.columns.length);
      this.widget.configuration.columns.push(...this.columnDataSource);
      // this.widget.configuration.columns = this.columnDataSource;
      this.widget.configuration.dataSourceId = this.dataSourceId;
      this.widget.configuration.dataSourceName = this.dataSourceName;
      this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
      return {
        columnIndexOptions
      };
    }
  }
};
</script>
