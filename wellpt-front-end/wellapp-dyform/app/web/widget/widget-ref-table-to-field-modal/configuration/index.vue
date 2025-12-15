<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" @change="onChangeTitle"></a-input>
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetRefTableToFieldDevelopment" width="205px" />
        </a-form-model-item>
        <DefaultVisibleConfiguration
          :designer="designer"
          :configuration="widget.configuration"
          :widget="widget"
          :compact="true"
          :codeRule="{
            '__DYFORM__.editable': {
              allowOperator: ['true', 'false']
            }
          }"
        ></DefaultVisibleConfiguration>
        <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="operationSetting">
          <a-collapse-panel key="operationSetting" header="按钮设置">
            <ButtonDetails :widget="widget" :designer="designer" :button="widget.configuration.button" :allowVisibleCondition="false" />
          </a-collapse-panel>
          <a-collapse-panel key="fieldMappingSetting" header="字段映射规则">
            <a-form-model-item>
              <template slot="label">
                插入从表
                <a-checkbox v-model="widget.configuration.insertToSubform" />
              </template>
              <a-select
                v-show="widget.configuration.insertToSubform"
                :style="{ width: '100%' }"
                :options="subformOptions"
                v-model="widget.configuration.insertToWidgetSubformId"
                @change="onChangeSubformSelect"
                show-search
                :filter-option="filterSelectOption"
              ></a-select>
            </a-form-model-item>

            <div v-if="widget.configuration.insertToSubform && widget.configuration.insertToWidgetSubformId != undefined">
              <a-form-model-item>
                <template slot="label">
                  当从表行数据已存在时进行更新
                  <a-checkbox v-model="widget.configuration.repeatUpdate" />
                </template>
              </a-form-model-item>
              <a-form-model-item v-if="widget.configuration.repeatUpdate">
                <template slot="label">行数据存在判断列</template>
                <a-select
                  mode="multiple"
                  :options="fieldOptions"
                  v-model="widget.configuration.repeatByCode"
                  style="width: 100%"
                ></a-select>
              </a-form-model-item>
            </div>
            <div v-else>
              <a-form-model-item>
                <template slot="label">
                  <a-popover placement="left" :mouseEnterDelay="0.5">
                    <template slot="content">
                      重新打开弹窗后, 会根据选择的映射表单字段值与表格列值进行匹配选中表格行
                      <br />
                      (请勿选择日期或者附件类字段)
                    </template>
                    <label>
                      选择表格行的依据表单映射字段
                      <a-icon type="info-circle" />
                    </label>
                  </a-popover>
                </template>
                <a-select
                  mode="multiple"
                  :options="fieldMappedOptions"
                  v-model="widget.configuration.selectByCode"
                  style="width: 100%"
                  allow-clear
                ></a-select>
              </a-form-model-item>
            </div>
            <a-table
              rowKey="id"
              size="small"
              :pagination="false"
              :bordered="false"
              :columns="mappingColumn"
              :data-source="widget.configuration.fieldMapping"
            >
              <template slot="customFieldTitle">
                {{ widget.configuration.insertToSubform && widget.configuration.insertToWidgetSubformId ? '从表列' : '表单字段' }}
              </template>
              <template slot="dataIndexSlot" slot-scope="text, record">
                <a-select :options="fieldOptions" v-model="record.field" style="width: 100%"></a-select>
              </template>
              <template slot="tableColumnSlot" slot-scope="text, record, index">
                <a-select :options="tableColumnOptions" v-model="record.tableColumn" style="width: calc(100% - 50px)"></a-select>
                <a-button size="small" type="link" @click="widget.configuration.fieldMapping.splice(index, 1)" title="删除">
                  <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                </a-button>
              </template>

              <template slot="footer">
                <div style="text-align: right">
                  <a-button size="small" type="link" @click="addFieldMapping">添加映射规则</a-button>
                </div>
              </template>
            </a-table>
          </a-collapse-panel>
          <a-collapse-panel key="modalSetting" header="弹窗设置">
            <a-form-model-item label="弹窗关闭后销毁内容">
              <a-switch v-model="widget.configuration.modal.destroyOnClose" />
            </a-form-model-item>
            <a-form-model-item label="弹窗标题">
              <a-input v-model="widget.configuration.modal.title"></a-input>
            </a-form-model-item>
            <ButtonList :widget="widget" :button="widget.configuration.modal.button" title="弹窗按钮"></ButtonList>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>

      <a-tab-pane key="t" tab="表格">
        <TableBasicConfiguration :widget="widget.configuration.WidgetTable" :designer="designer" />
      </a-tab-pane>

      <a-tab-pane key="5" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer" :codeSnippets="[]"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<style lang="less"></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import WidgetTableConfiguration from '@pageWidget/widget-table/configuration/index.vue';
import TableBasicConfiguration from '@pageWidget/widget-table/configuration/table-basic-configuration.vue';
import ButtonDetails from '@pageWidget/commons/buttons-configuration/button-details.vue';
import ButtonList from '@pageWidget/commons/buttons-configuration/index.vue';
import { filterSelectOption } from '@framework/vue/utils/function';

import { generateId } from '@framework/vue/utils/util';

export default {
  name: 'WidgetRefTableToFieldModalConfiguration',

  mixins: [formConfigureMixin],
  props: {
    parent: Object,
    widget: Object,
    designer: Object
  },
  data() {
    return {
      mappingColumn: [
        {
          dataIndex: 'dataIndex',
          width: '50%',
          scopedSlots: { customRender: 'dataIndexSlot' },
          slots: { title: 'customFieldTitle' }
        },
        { title: '表格列', dataIndex: 'tableColumn', width: '50%', scopedSlots: { customRender: 'tableColumnSlot' } }
      ]
    };
  },

  beforeCreate() {},
  components: { TableBasicConfiguration, ButtonDetails, ButtonList },
  computed: {
    subformOptions() {
      let options = [];
      if (this.designer) {
        for (let wid in this.designer.widgetIdMap) {
          let w = this.designer.widgetIdMap[wid];
          if (w.wtype == 'WidgetSubform') {
            options.push({ label: w.title, value: w.id });
          }
        }
      }
      return options;
    },
    displayStateOptions() {
      return [{ label: '表单可编辑', value: '__DYFORM__.editable' }];
    },
    tableColumnOptions() {
      let opt = [];
      if (this.widget.configuration.WidgetTable && this.widget.configuration.WidgetTable.configuration) {
        for (let i = 0, len = this.widget.configuration.WidgetTable.configuration.columns.length; i < len; i++) {
          opt.push({
            label: this.widget.configuration.WidgetTable.configuration.columns[i].title,
            value: this.widget.configuration.WidgetTable.configuration.columns[i].dataIndex
          });
        }
      }
      return opt;
    },
    fieldOptions() {
      let opt = [];
      if (this.widget.configuration.insertToSubform && this.widget.configuration.insertToWidgetSubformId) {
        let wgt = this.designer.widgetIdMap[this.widget.configuration.insertToWidgetSubformId];
        if (wgt) {
          for (let i = 0, len = wgt.configuration.columns.length; i < len; i++) {
            opt.push({
              label: wgt.configuration.columns[i].title,
              value: wgt.configuration.columns[i].dataIndex
            });
          }
        }
      } else {
        if (this.designer.SimpleFieldInfos) {
          for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
            opt.push({
              label: this.designer.SimpleFieldInfos[i].name,
              value: this.designer.SimpleFieldInfos[i].code
            });
          }
        }
      }

      return opt;
    },
    fieldMappedOptions() {
      let opt = [],
        fieldCodeMapped = [];
      for (let map of this.widget.configuration.fieldMapping) {
        fieldCodeMapped.push(map.field);
      }
      if (this.designer.SimpleFieldInfos) {
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          if (fieldCodeMapped.includes(this.designer.SimpleFieldInfos[i].code)) {
            opt.push({
              label: this.designer.SimpleFieldInfos[i].name,
              value: this.designer.SimpleFieldInfos[i].code
            });
          }
        }
      }

      return opt;
    }
  },
  created() {},
  methods: {
    filterSelectOption,
    addFieldMapping() {
      this.widget.configuration.fieldMapping.push({
        id: generateId(),
        dataIndex: undefined,
        tableColumn: undefined
      });
    }
  },
  mounted() {
    if (this.widget.configuration.insertToSubform == undefined) {
      this.$set(this.widget.configuration, 'insertToSubform', false);
      this.$set(this.widget.configuration, 'insertToWidgetSubformId', undefined);
      this.$set(this.widget.configuration, 'repeatUpdate', false);
      this.$set(this.widget.configuration, 'repeatByCode', []);
    }
  },

  configuration() {
    let widgetTableConfig = {
      id: generateId(),
      wtype: 'WidgetTable',
      title: '关联字段数据 - 表格',
      configuration: WidgetTableConfiguration.configuration()
    };
    widgetTableConfig.configuration.rowSelectType = 'radio';
    widgetTableConfig.configuration.clickRowSelect = true;
    return {
      displayType: 'button',
      modal: {
        destroyOnClose: false,
        title: '关联数据',
        button: {
          enable: true,
          // 底部按钮配置
          buttons: [
            {
              id: generateId(),
              code: 'cancel',
              CANCEL_BUTTON: true,
              title: '取消',
              visible: true,
              unDeleted: true,
              style: { icon: undefined, textHidden: false }
            },
            {
              id: generateId(),
              OK_BUTTON: true,
              code: 'ok',
              title: '确定',
              unDeleted: true,
              visible: true,
              style: { icon: undefined, textHidden: false, type: 'primary' }
            }
          ],
          buttonGroup: {
            type: 'notGroup', // notGroup: 不分组  fixedGroup: 固定分组 dynamicGroup: 动态分组
            groups: [
              // 固定分组
              // {name:,buttonIds:[]}
            ],
            dynamicGroupName: '更多', //动态分组名称
            dynamicGroupBtnThreshold: 3 // 分组按钮数阈值，达到该数才触发分组
          }
        }
      },
      button: {
        id: generateId(),
        code: undefined,
        title: '关联数据',
        visible: true,
        displayPosition: undefined,
        style: { icon: undefined, textHidden: false }
      },
      fieldMapping: [],
      insertToSubform: false,
      insertToWidgetSubformId: undefined,
      repeatByCode: [],
      repeatUpdate: false,
      WidgetTable: widgetTableConfig,
      domEvents: [
        {
          id: 'onModalClose',
          title: '弹窗关闭时触发',
          codeSource: 'codeEditor',
          jsFunction: undefined,
          widgetEvent: [],
          customScript: undefined // 事件脚本
        }
      ]
    };
  }
};
</script>
