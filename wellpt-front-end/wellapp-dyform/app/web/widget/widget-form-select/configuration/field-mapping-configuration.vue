<template>
  <!-- 字段映射 -->
  <div class="widget-edit-table-column">
    <a-button-group class="table-header-operation">
      <a-button @click="addColumn" icon="plus">新增</a-button>
      <a-button @click="delColumn" icon="delete">删除</a-button>
      <a-button @click="moveColumn('forward')" icon="arrow-up">上移</a-button>
      <a-button @click="moveColumn" icon="arrow-down">下移</a-button>
    </a-button-group>
    <a-table
      rowKey="id"
      :row-selection="{ selectedRowKeys: selectedColumnRowKeys, onChange: selectColumnChange }"
      :pagination="false"
      bordered
      :columns="columnDefine"
      :data-source="widget.configuration.options.dataSourceDataMapping"
      :scroll="{ y: 600 }"
    >
      <template slot="sourceFieldSlot" slot-scope="text, record">
        <a-select
          :options="columnIndexOptions"
          :style="{ width: '100%' }"
          size="small"
          :defaultValue="text"
          v-model="record.sourceField"
        ></a-select>
      </template>
      <template slot="targetFieldSlot" slot-scope="text, record">
        <a-select
          :options="formFieldOptions"
          :style="{ width: '100%' }"
          size="small"
          :defaultValue="text"
          v-model="record.targetField"
        ></a-select>
      </template>
    </a-table>
  </div>
</template>
<style>
.widget-edit-table-column .table-header-operation {
  margin-bottom: 8px;
}

.widget-edit-table-column .table-header-operation {
  margin-bottom: 8px;
}
.widget-edit-table-column .table-footer-operation {
  margin-top: 8px;
  float: right;
}
</style>
<script type="text/babel">
import { generateId, swapArrayElements } from '@framework/vue/utils/util';
export default {
  name: 'WidgetFormSelectFieldMappingConfiguration',
  mixins: [],
  inject: ['pageContext'],
  props: {
    designer: Object,
    widget: Object,
    columnIndexOptions: Array
  },
  data() {
    return {
      options: {},
      renderOptions: [],
      columnRenderFuncConfVisible: false,
      currentColumnRenderFuncConf: {},
      selectedColumnRowKeys: [],
      selectedColumnRows: [],
      columnDefine: [
        { title: '数据源字段', dataIndex: 'sourceField', width: 150, scopedSlots: { customRender: 'sourceFieldSlot' } },
        { title: '表单字段', dataIndex: 'targetField', width: 150, scopedSlots: { customRender: 'targetFieldSlot' } }
      ]
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    formFieldOptions() {
      let opt = [];
      if (this.designer.FieldWidgets && this.designer.FieldWidgets.length) {
        for (let k = 0, len = this.designer.FieldWidgets.length; k < len; k++) {
          let field = this.designer.FieldWidgets[k];
          if (field.configuration.code && field.id != this.widget.id) {
            opt.push({
              label: field.configuration.name || field.configuration.code,
              value: field.configuration.code
            });
          }
        }
      }
      return opt;
    }
  },
  created() {},
  methods: {
    addColumn() {
      this.widget.configuration.options.dataSourceDataMapping.push({
        id: generateId(),
        sourceField: '',
        targetField: ''
      });
    },
    delColumn() {
      if (this.selectedColumnRowKeys.length) {
        for (let i = 0, len = this.selectedColumnRowKeys.length; i < len; i++) {
          for (let j = 0, jlen = this.widget.configuration.options.dataSourceDataMapping.length; j < jlen; j++) {
            if (this.widget.configuration.options.dataSourceDataMapping[j].id == this.selectedColumnRowKeys[i]) {
              this.widget.configuration.options.dataSourceDataMapping.splice(j, 1);
              break;
            }
          }
        }
      }
    },
    moveColumn(direction) {
      let ids = [];
      for (let i = 0, len = this.selectedColumnRows.length; i < len; i++) {
        ids.push(this.selectedColumnRows[i].id);
      }
      swapArrayElements(
        ids,
        this.widget.configuration.options.dataSourceDataMapping,
        function (a, b) {
          return a == b.id;
        },
        direction
      );
    },
    selectColumnChange(selectedRowKeys, selectedRows) {
      this.selectedColumnRowKeys = selectedRowKeys;
      this.selectedColumnRows = selectedRows;
    }
  },
  mounted() {}
};
</script>
