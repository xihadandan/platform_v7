<template>
  <ButtonConfiguration :button="button" :widget="widget" :designer="designer" ref="buttonConfiguration">
    <template slot="extraInfo">
      <a-form-model-item label="导出文件名">
        <a-input v-model="exportRule.exportFileName">
          <template slot="addonAfter">
            <WI18nInput
              :widget="widget"
              :designer="designer"
              :code="button.id + '_fileName'"
              v-model="exportRule.exportFileName"
              :target="exportRule"
            />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="支持可选字段导出">
        <a-switch v-model="exportRule.exportColumnSelectable" />
      </a-form-model-item>
      <a-form-model-item label="导出范围">
        <a-radio-group v-model="exportRule.exportRange" button-style="solid" size="small">
          <a-radio-button value="all">导出全部</a-radio-button>
          <a-radio-button value="rowSelected">导出所选</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="导出列配置">
        <WidgetDesignModal title="导出列配置" :destroyOnClose="true">
          <a-button type="link" icon="setting" size="small" @click="onClickOpenExportColConfig">配置</a-button>
          <template slot="content">
            <a-table :columns="exportTableColumns" :dataSource="widget.configuration.columns" :pagination="false">
              <template slot="titleSlot" slot-scope="text, record">
                <a-space direction="vertical" style="width: 100%">
                  <div style="font-weight: bold">
                    <a-icon
                      :type="collapsedKeys.includes(record.dataIndex) ? 'plus-square' : 'minus-square'"
                      :style="{
                        opacity: exportRule.exportColumnConfig[record.dataIndex].subExportColumns.length > 0 ? 1 : 0
                      }"
                      @click="e => onClickExpandOrCollapse(e, record)"
                    />
                    {{ text }}
                    <a-checkbox
                      v-if="exportRule.exportColumnConfig[record.dataIndex].dataType == 'file'"
                      style="float: right"
                      v-model="exportRule.exportColumnConfig[record.dataIndex].exportAttachment"
                    >
                      导出文件
                    </a-checkbox>
                  </div>

                  <template v-if="exportRule.exportColumnConfig[record.dataIndex].subExportColumns.length > 0">
                    <template v-for="(sColumn, s) in exportRule.exportColumnConfig[record.dataIndex].subExportColumns">
                      <a-input
                        v-show="!collapsedKeys.includes(record.dataIndex)"
                        v-model="sColumn.title"
                        :key="s"
                        style="width: 200px; margin-left: 25px"
                      />
                    </template>
                  </template>
                </a-space>
              </template>
              <template slot="exportableSlot" slot-scope="text, record">
                <a-space direction="vertical">
                  <a-switch v-model="exportRule.exportColumnConfig[record.dataIndex].exportable" />
                  <template v-if="exportRule.exportColumnConfig[record.dataIndex].subExportColumns.length > 0">
                    <template v-for="(sColumn, s) in exportRule.exportColumnConfig[record.dataIndex].subExportColumns">
                      <a-switch v-show="!collapsedKeys.includes(record.dataIndex)" v-model="sColumn.exportable" />
                    </template>
                  </template>
                </a-space>
              </template>
            </a-table>
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
    </template>
  </ButtonConfiguration>
</template>
<style lang="less"></style>
<script type="text/babel">
import ButtonConfiguration from '../button-configuration.vue';
import { getOptions } from '../../../widget-form-date-picker/configuration/components/date-pattern-options';
import { find } from 'lodash';
export default {
  name: 'SubformExportButtonConfiguration',
  props: { widget: Object, designer: Object, columnIndexOptions: Array, button: Object, exportRule: Object },
  components: { ButtonConfiguration },
  computed: {},
  data() {
    return {
      collapsedKeys: [],
      exportTableColumns: [
        { title: '列', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '可导出', width: 80, key: 'exportableSwitch', scopedSlots: { customRender: 'exportableSlot' } }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onClickOpenExportColConfig() {
      // 导出配置初始化
      if (this.widget.configuration.columns != undefined) {
        let dataIndexes = [];
        for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
          let column = this.widget.configuration.columns[i];
          dataIndexes.push(column.dataIndex);
          if (this.exportRule.exportColumnConfig == undefined) {
            this.$set(this.exportRule, 'exportColumnConfig', {});
          }
          if (this.exportRule.exportColumnConfig[column.dataIndex] == undefined) {
            let columnWidget = column.widget;
            let dataType = 'string';
            let subExportColumns = [];
            let format = undefined;
            if (columnWidget) {
              if (columnWidget.wtype == 'WidgetFormInputNumber') {
                dataType = 'number';
              } else if (columnWidget.wtype == 'WidgetFormFileUpload') {
                dataType = 'file';
              } else if (columnWidget.wtype == 'WidgetFormDatePicker') {
                dataType = 'date';
                let datePatternJson = columnWidget.configuration.datePatternJson;
                if (!datePatternJson.contentFormat) {
                  let datePatternOptions = getOptions();
                  format = find(datePatternOptions[columnWidget.configuration.datePatternType], {
                    key0: datePatternJson.key0
                  }).contentFormat.replace(/D/g, 'd');
                }

                if (columnWidget.subtype == 'Range') {
                  dataType = 'string';
                  // 日期范围
                  subExportColumns.push({
                    dataType: 'date',
                    dataIndex: columnWidget.configuration.code,
                    exportable: false,
                    title: column.title + '(开始)'
                  });
                  subExportColumns.push({
                    dataType: 'date',
                    dataIndex: columnWidget.configuration.endDateField,
                    exportable: false,
                    title: column.title + '(结束)'
                  });
                }
              } else if (['WidgetFormSelect', 'WidgetFormRadio', 'WidgetFormCheckbox', 'WidgetFormTag'].includes(columnWidget.wtype)) {
                // 选项型的组件，把真实值字段导出
                if (
                  'WidgetFormTag' !== columnWidget.wtype ||
                  ('WidgetFormTag' === columnWidget.wtype && columnWidget.configuration.tagEditMode == 'select')
                ) {
                  subExportColumns.push({
                    dataType: 'string',
                    dataIndex: columnWidget.configuration.code,
                    exportable: false,
                    title: column.title + '(真实值)'
                  });
                }
              }
            }
            this.$set(this.exportRule.exportColumnConfig, column.dataIndex, {
              exportable: column.defaultDisplayState !== 'hidden',
              exportAttachment: columnWidget.wtype == 'WidgetFormFileUpload' ? true : undefined,
              format,
              subExportColumns,
              dataType // number / date / string
            });
          }
        }
        for (let key in this.exportRule.exportColumnConfig) {
          if (!dataIndexes.includes(key)) {
            delete this.exportRule.exportColumnConfig[key];
          }
        }
      }
    },
    onClickExpandOrCollapse(e, record) {
      let item = this.exportRule.exportColumnConfig[record.dataIndex];
      if (item.subExportColumns.length > 0) {
        let i = this.collapsedKeys.indexOf(record.dataIndex);
        if (i == -1) {
          this.collapsedKeys.push(record.dataIndex);
        } else {
          this.collapsedKeys.splice(i, 1);
        }
      }
    }
  }
};
</script>
