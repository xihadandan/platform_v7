<template>
  <span v-show="vShow">
    <a-button
      :code="widget.configuration.button.code || widget.configuration.button.id"
      :shape="widget.configuration.button.style.shape"
      :type="widget.configuration.button.style.type"
      :size="size"
      @click.stop="showModal"
      :title="widget.configuration.button.style.textHidden == true ? widget.configuration.button.title : ''"
    >
      <Icon :type="widget.configuration.button.style.icon" v-if="widget.configuration.button.style.icon" />
      {{ widget.configuration.button.style.textHidden == true ? '' : widget.configuration.button.title }}
    </a-button>
    <a-modal
      :destroyOnClose="widget.configuration.modal.destroyOnClose"
      width="calc(100% - 400px)"
      :maskClosable="false"
      v-model="modalVisible"
      :title="widget.configuration.modal.title"
    >
      <Scroll style="max-height: calc(100vh - 450px)">
        <WidgetTable
          :widget="widget.configuration.WidgetTable"
          :parent="parent"
          :designer="designer"
          ref="widgetTable"
          @onSelectRowChanged="onTableSelectRowChanged"
          @mounted="onWidgetTableMounted"
          @tbodyRendered="onWidgetTableBodyRendered"
        />
      </Scroll>

      <template slot="okText">{{ $t('WidgetRefTableToFieldModal.ok', '确定') }} ({{ selectRowCnt }})</template>
      <template slot="footer">
        <WidgetTableButtons
          :button="widget.configuration.modal.button"
          :developJsInstance="developJsInstance"
          :meta="getButtonMeta"
          :eventWidget="getSelf"
          @button-click="$evt => onButtonClick($evt)"
          ref="footerButton"
        ></WidgetTableButtons>
      </template>
    </a-modal>
  </span>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';

export default {
  name: 'WidgetRefTableToFieldModal',
  mixins: [widgetMixin],
  inject: ['dyform'],
  components: {},
  computed: {
    _vShowByData() {
      return { ...this.dyform.formData, __DYFORM__: { editable: this.dyform.displayState == 'edit' } };
    },
    defaultEvents() {
      return [{ id: 'openSelectModal', title: '打开关联数据选择' }];
    },
    tableColumnDataIndexMap() {
      let colMap = {};
      for (let i = 0, len = this.widget.configuration.WidgetTable.configuration.columns.length; i < len; i++) {
        colMap[this.widget.configuration.WidgetTable.configuration.columns[i].dataIndex] =
          this.widget.configuration.WidgetTable.configuration.columns[i];
      }
      return colMap;
    }
  },
  data() {
    return { modalVisible: false, selectRowCnt: 0, okButton: {}, cancelSelectKeys: [], selectKeys: [] };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.widget.configuration.modal.button) {
      for (let i = 0, len = this.widget.configuration.modal.button.buttons.length; i < len; i++) {
        if (this.widget.configuration.modal.button.buttons[i].OK_BUTTON) {
          this.okButton = this.widget.configuration.modal.button.buttons[i];
          break;
        }
      }
    }
  },
  mounted() {},
  methods: {
    onWidgetTableBodyRendered({ rows }) {
      let { fieldMapping } = this.widget.configuration;
      let formData = this.dyform.formData;
      if (fieldMapping.length && rows.length > 0) {
        let rowMatch = [];
        let insertSubform = this.widget.configuration.insertToSubform && this.widget.configuration.insertToWidgetSubformId != undefined;
        if (!insertSubform) {
          // 通过字段映射反向匹配选中行
          let selectByCode = this.widget.configuration.selectByCode || [];
          for (let i = 0, len = rows.length; i < len; i++) {
            let matched = 0;
            for (let j = 0, jlen = fieldMapping.length; j < jlen; j++) {
              let m = fieldMapping[j];
              if (m.field && m.tableColumn && selectByCode.includes(m.field)) {
                if (typeof formData[m.field] == 'string' && formData[m.field].split(';').includes(rows[i][m.tableColumn])) {
                  matched++;
                }
              }
            }
            if (matched == selectByCode.length) {
              rowMatch.push(i);
            }
          }

          if (rowMatch.length) {
            this.$refs.widgetTable.selectRowByIndex(rowMatch);
          }
        } else {
          let repeatByCode = this.widget.configuration.repeatByCode,
            matchRowFields = [];
          for (let i = 0, len = fieldMapping.length; i < len; i++) {
            if (repeatByCode.includes(fieldMapping[i].field)) {
              matchRowFields.push(fieldMapping[i]);
            }
          }
          let $subform = this.dyform.$subform[this.widget.configuration.insertToWidgetSubformId];
          for (let i = 0, len = rows.length; i < len; i++) {
            let math = false;
            if (this.cancelSelectKeys.includes(rows[i][this.$refs.widgetTable.primaryColumnKey])) {
              continue;
            }
            if (this.selectKeys.includes(rows[i][this.$refs.widgetTable.primaryColumnKey])) {
              match = true;
              rowMatch.push(i);
              continue;
            }
            for (let key in $subform.rowForms) {
              let rowFormData = $subform.rowForms[key].formData,
                eql = 0;
              for (let j = 0, jlen = matchRowFields.length; j < jlen; j++) {
                let m = matchRowFields[j];
                if (m.field && m.tableColumn) {
                  if (typeof rowFormData[m.field] == 'string' && rowFormData[m.field].split(';').includes(rows[i][m.tableColumn])) {
                    eql++;
                  }
                }
              }
              if (eql == matchRowFields.length) {
                math = true;
                break;
              }
            }
            if (math) {
              rowMatch.push(i);
            }
          }
          if (rowMatch.length) {
            this.$refs.widgetTable.selectRowByIndex(rowMatch, true);
          }
        }
      }
    },
    getButtonMeta() {
      return { rows: this.$refs.widgetTable ? this.$refs.widgetTable.getSelectedRows() : [] };
    },
    getSelf() {
      return this;
    },
    onTableSelectRowChanged(e) {
      this.selectRowCnt = e.selectedRows.length;

      if (this.selectKeys.length > 0) {
        // 记录取消选择行的值
        for (let k of this.selectKeys) {
          if (!e.selectedRowKeys.includes(k)) {
            this.cancelSelectKeys.push(k);
          } else if (this.cancelSelectKeys.includes(k)) {
            this.cancelSelectKeys.splice(this.cancelSelectKeys.indexOf(k), 1);
          }
        }
      }
      this.selectKeys.splice(0, this.selectKeys.length, ...e.selectedRowKeys);

      let buttonMap = this.$refs.footerButton.buttonMap;
      if (buttonMap && buttonMap[this.okButton.id]) {
        buttonMap[this.okButton.id].title = `${this.okButton.title} (${this.selectRowCnt})`;
      }
    },
    onButtonClick(e) {
      if (e.OK_BUTTON) {
        this.onConfirmModal();
      } else if (e.CANCEL_BUTTON) {
        this.onCancelModal();
      }
    },
    openSelectModal() {
      this.modalVisible = true;
    },
    onConfirmModal() {
      // 获取表格内所选的数据
      const selectedRows = this.$refs.widgetTable.getSelectedRows();
      // 字段映射
      let { fieldMapping } = this.widget.configuration;
      if (fieldMapping.length && selectedRows.length > 0) {
        let insertSubform = this.widget.configuration.insertToSubform && this.widget.configuration.insertToWidgetSubformId != undefined,
          rows = [],
          originalSubformRows = [];
        if (insertSubform) {
          originalSubformRows = this.dyform.getSubformRows(this.widget.configuration.insertToWidgetSubformId);
          let $subform = this.dyform.$subform[this.widget.configuration.insertToWidgetSubformId];
          let colWgtMap = {};
          if ($subform && $subform.widget) {
            $subform.widget.configuration.columns.forEach(c => {
              colWgtMap[c.widget.configuration.code] = c.widget;
            });
          }
          selectedRows.forEach(r => {
            let row = {};
            for (let i = 0, len = fieldMapping.length; i < len; i++) {
              let { field, tableColumn } = fieldMapping[i];
              if (field && tableColumn) {
                if (colWgtMap[field] && colWgtMap[field].wtype == 'WidgetFormFileUpload') {
                  if (r[tableColumn + '$RenderValue'] != undefined && r[tableColumn + '$RenderValue'].length > 0) {
                    // 通过数据文件渲染器渲染的文件列表
                    let values = [];
                    r[tableColumn + '$RenderValue'].forEach(f => {
                      values.push(f);
                    });
                    row[field] = values;
                  }
                } else if (colWgtMap[field].wtype == 'WidgetFormDatePicker' && colWgtMap[field].subtype == 'Range') {
                  // 日期范围
                  let dateStr = r[tableColumn];
                  if (dateStr) {
                    if (dateStr.indexOf('~') != -1) {
                      let dateArr = dateStr.split('~');
                      row[field] = dateArr[1].trim();
                      row[colWgtMap[field].configuration.endDateField] = dateArr[1].trim();
                    } else if (this.tableColumnDataIndexMap[tableColumn].renderFunction.options.isRange) {
                      row[colWgtMap[field].configuration.endDateField] =
                        r[this.tableColumnDataIndexMap[tableColumn].renderFunction.options.endTimeParams];
                      row[field] = dateStr;
                    }
                  }
                } else {
                  row[field] = r[tableColumn];
                }
              }
            }
            let update = false;
            if (
              originalSubformRows != undefined &&
              originalSubformRows.length > 0 &&
              this.widget.configuration.repeatUpdate &&
              this.widget.configuration.repeatByCode != undefined &&
              this.widget.configuration.repeatByCode.length > 0
            ) {
              for (let i = 0; i < originalSubformRows.length; i++) {
                let subformRow = originalSubformRows[i],
                  repeatByCode = this.widget.configuration.repeatByCode,
                  equalCount = 0;
                for (let j = 0; j < repeatByCode.length; j++) {
                  if (subformRow[repeatByCode[j]] == row[repeatByCode[j]]) {
                    equalCount++;
                  }
                }
                if (equalCount == repeatByCode.length) {
                  // 更新原始行
                  let rowForm = this.dyform.getSubformRowForm(this.widget.configuration.insertToWidgetSubformId, i);
                  for (let k in row) {
                    subformRow[k] = row[k];
                    if (rowForm) {
                      rowForm.setFieldValue(k, row[k]);
                    }
                  }
                  update = true;
                }
              }
            }
            if (!update) {
              rows.push(row);
            }
          });

          if (rows.length) {
            // 插入从表
            this.dyform.addSubformRows(this.widget.configuration.insertToWidgetSubformId, rows);
          }
        } else {
          for (let i = 0, len = fieldMapping.length; i < len; i++) {
            let { field, tableColumn } = fieldMapping[i];
            let wField = this.dyform.getField(field);
            if (field && tableColumn && wField) {
              let wtype = wField.widget.wtype;
              if (wtype == 'WidgetFormFileUpload') {
                // 附件组件设置，需要从表格上获取渲染的文件列表
                let values = [];
                selectedRows.forEach(r => {
                  if (r[tableColumn + '$RenderValue'] != undefined && r[tableColumn + '$RenderValue'].length > 0) {
                    // 通过数据文件渲染器渲染的文件列表
                    r[tableColumn + '$RenderValue'].forEach(f => {
                      values.push(f);
                    });
                  }
                });
                this.dyform.setFieldValue(field, values);
              } else if (wtype == 'WidgetFormDatePicker' && wField.widget.subtype == 'Range') {
                // 日期范围
                let lastRow = selectedRows[selectedRows.length - 1];
                let dateStr = lastRow[tableColumn];
                if (dateStr) {
                  if (dateStr.indexOf('~') != -1) {
                    let dateArr = dateStr.split('~');
                    this.dyform.setFieldValue(wField.widget.configuration.endDateField, dateArr[1].trim());
                    this.dyform.setFieldValue(field, dateArr[0].trim());
                  } else if (this.tableColumnDataIndexMap[tableColumn].renderFunction.options.isRange) {
                    this.dyform.setFieldValue(
                      wField.widget.configuration.endDateField,
                      lastRow[this.tableColumnDataIndexMap[tableColumn].renderFunction.options.endTimeParams]
                    );
                    this.dyform.setFieldValue(field, dateStr);
                  }
                }
              } else {
                let values = [];
                selectedRows.forEach(r => {
                  if (r[tableColumn] != undefined && r[tableColumn] != '') {
                    values.push(r[tableColumn]);
                  }
                });
                this.dyform.setFieldValue(field, values.join(';'));
              }
            }
          }
        }
        this.modalVisible = false;
      }
    },
    onCancelModal() {
      this.modalVisible = false;
      this.triggerDomEvent('onModalClose', this.dyform.formData, {
        selectedRows: this.$refs.widgetTable.getSelectedRows()
      });
    },

    showModal() {
      this.modalVisible = true;
    }
  },
  watch: {
    modalVisible: {
      handler(v) {
        if (this.widget.configuration.modal.destroyOnClose) {
          // 记录取消/选择的行
          this.cancelSelectKeys.splice(0, this.cancelSelectKeys.length);
          this.selectKeys.splice(0, this.selectKeys.length);
        }
      }
    }
  }
};
</script>
