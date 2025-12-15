<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    :colon="false"
    labelAlign="left"
    :label-col="{ span: 6 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
  >
    <a-tabs v-model="tabKey" size="small" class="flex-card-tabs flow-flex-card-tabs">
      <a-tab-pane key="0" tab="基本属性">
        <a-form-model-item class="form-item-vertical" label="名称">
          <a-input v-model="formData.name" />
        </a-form-model-item>
        <a-form-model-item class="form-item-vertical" prop="layout" label="布局">
          <a-checkbox-group v-model="formData.layout" :options="swimlaneLayoutOptions" @change="changeLayout" />
        </a-form-model-item>
        <template v-if="formData.layout.length === 1 && formData.layout.includes(swimlaneLayoutOptions[0]['value'])">
          <a-form-model-item class="form-item-vertical" label="纵向泳道高度">
            <div class="ant-input-affix-wrapper input-number-affix-wrapper">
              <a-input-number v-model="formData.columnHeight" :min="1" @change="changeColumnHeight" />
              <span class="ant-input-suffix">px</span>
            </div>
          </a-form-model-item>
        </template>
        <template v-if="formData.layout.length === 1 && formData.layout.includes(swimlaneLayoutOptions[1]['value'])">
          <a-form-model-item class="form-item-vertical" label="横向泳道宽度">
            <div class="ant-input-affix-wrapper input-number-affix-wrapper">
              <a-input-number v-model="formData.rowWidth" :min="1" />
              <span class="ant-input-suffix">px</span>
            </div>
          </a-form-model-item>
        </template>
        <a-form-model-item v-show="formData.layout.includes(swimlaneLayoutOptions[0]['value'])" class="form-item-vertical" label="纵向泳道">
          <a-table
            class="timers-table no-border swimlane-columns-table"
            size="small"
            rowKey="dataIndex"
            :pagination="false"
            :columns="verticalColumns"
            :dataSource="formData.columns"
            :rowClassName="record => 'table-row-' + record.dataIndex"
          >
            <template slot="titleSlot" slot-scope="text, record">
              <a-input v-model="record.title">
                <template slot="addonBefore">
                  <a-icon type="menu" class="drag-handler" :style="{ cursor: 'move' }" />
                </template>
              </a-input>
            </template>
            <template slot="widthSlot" slot-scope="text, record">
              <!-- <a-input-number
                v-model="record.width"
                :min="1"
                :formatter="value => `${value}px`"
                :parser="value => value.replace('px', '')"
              /> -->
              <div class="ant-input-affix-wrapper input-number-affix-wrapper">
                <a-input-number v-model="record.width" :min="1" />
                <span class="ant-input-suffix">px</span>
              </div>
            </template>
            <template slot="operationSlot" slot-scope="text, record, index">
              <a-button type="link" size="small" icon="delete" @click="delItem(index, record)" />
            </template>
            <template slot="footer">
              <div class="timers-table-footer">
                <span>
                  <a-button type="link" @click="addItem" icon="plus">添加</a-button>
                </span>
              </div>
            </template>
          </a-table>
        </a-form-model-item>
        <a-form-model-item v-show="formData.layout.includes(swimlaneLayoutOptions[1]['value'])" class="form-item-vertical" label="横向泳道">
          <a-table
            class="timers-table no-border swimlane-rows-table"
            size="small"
            rowKey="id"
            :pagination="false"
            :columns="horizontalColumns"
            :dataSource="formData.rows"
          >
            <template slot="titleSlot" slot-scope="text, record">
              <a-input v-model="record.selection">
                <template slot="addonBefore">
                  <a-icon type="menu" class="drag-handler" :style="{ cursor: 'move' }" />
                </template>
              </a-input>
            </template>
            <template slot="heightSlot" slot-scope="text, record">
              <div class="ant-input-affix-wrapper input-number-affix-wrapper">
                <a-input-number v-model="record.height" :min="1" />
                <span class="ant-input-suffix">px</span>
              </div>
            </template>
            <template slot="operationSlot" slot-scope="text, record, index">
              <a-button type="link" size="small" icon="delete" @click="delRowItem(index, record)" />
            </template>
            <template slot="footer">
              <div class="timers-table-footer">
                <span>
                  <a-button type="link" @click="addRowItem" icon="plus">添加</a-button>
                </span>
              </div>
            </template>
          </a-table>
        </a-form-model-item>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import { sGetNewName } from '../designer/utils';
import constant, { swimlaneRules as rules, getCustomRules, swimlaneLayoutOptions } from '../designer/constant';
import mixins from '../mixins';
export default {
  name: 'NodeSwimlaneConfiguration',
  mixins: [mixins],
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    graphItem: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    const rulesCustom = getCustomRules(rules);
    const selectedCell = this.graphItem.getSelectedCell();

    const verticalColumns = [
      { title: '泳道标题', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
      { title: '宽度', dataIndex: 'width', scopedSlots: { customRender: 'widthSlot' }, width: 120 },
      { title: '', dataIndex: 'operation', scopedSlots: { customRender: 'operationSlot' }, width: 40, align: 'center' }
    ];
    const horizontalColumns = JSON.parse(JSON.stringify(verticalColumns));
    horizontalColumns[1] = {
      title: '高度',
      dataIndex: 'height',
      scopedSlots: { customRender: 'heightSlot' },
      width: 120
    };

    return {
      tabKey: '0',
      rules: rulesCustom,
      selectedCell,
      swimlaneLayoutOptions,
      verticalColumns,
      horizontalColumns
    };
  },
  mounted() {
    this.$emit('mounted', this);
    this.draggable(this.formData.columns, this.$el.querySelector('.swimlane-columns-table .ant-table-tbody'), '.drag-handler');
    this.draggable(this.formData.rows, this.$el.querySelector('.swimlane-rows-table .ant-table-tbody'), '.drag-handler');
  },
  methods: {
    changeLayout(checkedValue) {
      if (checkedValue.length > 1 && checkedValue.includes(this.swimlaneLayoutOptions[1]['value'])) {
        // this.formData.columns.unshift({ dataIndex: 'selection', width: constant.SwimlaneSelectionWidth });
        // this.formData.rows.map(item => {
        //   item['selection'] = '';
        // });
      } else if (!checkedValue.includes(this.swimlaneLayoutOptions[1]['value'])) {
        // this.formData.columns.
      }
    },
    changeColumnHeight(value) {
      // this.setSwimlaneStyleByTagName('td', 'height', value);
    },
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    },
    addItem() {
      const dataIndex = generateId();
      const name = sGetNewName({
        type: 'SWIMLANE',
        cells: this.formData.columns
      });
      this.formData.columns.push({
        title: name,
        dataIndex,
        width: constant.SwimlaneColumnWidth
      });
      this.formData.rows.map(item => {
        item[dataIndex] = '';
      });
    },
    delItem(index, record) {
      this.formData.columns.splice(index, 1);
      this.formData.rows.map(item => {
        delete item[record['dataIndex']];
      });
    },
    addRowItem() {
      const name = sGetNewName({
        type: 'SWIMLANE',
        cells: this.formData.rows
      });
      const rowItem = JSON.parse(JSON.stringify(this.formData.rows[0]));
      rowItem.id = Date.now();
      rowItem.selection = name;
      rowItem.height = constant.SwimlaneRowHeight;
      this.formData.rows.push(rowItem);
    },
    delRowItem(index, record) {
      this.formData.rows.splice(index, 1);
    },
    setSwimlanceSize() {
      const columnsCount = this.formData.columns.length - 1;
      const NodeSwimlaneWidth = columnsCount * constant.SwimlaneColumnWidth + constant.SwimlaneSelectionWidth;

      const rowsCount = this.formData.rows.length - 1;
      const NodeSwimlaneHeight = rowsCount * constant.SwimlaneRowHeight + constant.SwimlaneSelectionHeight;
      this.selectedCell.setSize(NodeSwimlaneWidth, NodeSwimlaneHeight);
    }
  }
};
</script>
