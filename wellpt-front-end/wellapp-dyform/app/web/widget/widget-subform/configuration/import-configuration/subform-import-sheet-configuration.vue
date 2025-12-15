<template>
  <div>
    <a-tabs size="small" default-active-key="0_sheet" tab-position="bottom" type="card" @change="e => onTabChange(e)">
      <template slot="tabBarExtraContent">
        <a-tooltip title="严格模式开启后, 将对导入的工作表列与模板头部进行一致性匹配">严格模式</a-tooltip>
        <a-checkbox v-model="importRule.strict" />
      </template>
      <template v-for="(sheet, i) in importRule.sheetConfig">
        <a-tab-pane :key="i + '_sheet'" :tab="sheet.sheetName">
          <div style="height: calc(100vh - 225px)">
            <a-table
              :pagination="false"
              :bordered="true"
              :columns="getSheetHeaderColumn(sheet)"
              :data-source="getSheetHeaderConfigRow(sheet)"
              :scroll="{ x: 150 * sheet.header.length }"
            >
              <template slot="title">
                <div
                  style="display: flex; align-items: baseline"
                  v-if="i > 0 && widget.configuration.rowEditMode == 'form' && widget.configuration.editFormUuid != undefined"
                >
                  从表下的从表
                  <a-select
                    style="margin-left: 8px; width: 250px"
                    :options="subformOptions"
                    v-model="sheet.params.formUuid"
                    @change="(e, n) => onChangeSubformSelect(e, n, sheet)"
                  ></a-select>
                </div>
              </template>

              <template v-for="(header, h) in sheet.header" :slot="'customTitle_' + h">
                <span v-show="header.required" style="color: red">*</span>
                {{ header.title }}
              </template>
              <template v-for="(header, h) in sheet.header" :slot="'configSlot_' + h" slot-scope="text, record">
                <a-select
                  :key="i == 0 ? undefined : 'select' + h + sheet.params.formUuid"
                  :options="i == 0 ? subformColumnOptions : subSubformColumnOptions[sheet.params.formUuid]"
                  :filterSelectOption="filterSelectOption"
                  show-search
                  v-model="header.code"
                  placeholder="选择从表列"
                  style="width: 100%"
                  @change="(e, opt) => onChangeHeaderColumn(e, opt, header)"
                  allow-clear
                />
                <!-- <WidgetDesignModal
                  :id="'WidgetTableBtnConfigImportSheetFieldRuleConfig' + button.id + i + '_' + h"
                  title="字段规则"
                  :designer="designer"
                >
                  <a-button type="link" size="small">配置</a-button>
                  <template slot="content">
                    <a-form-model :label-col="{ span: 4 }" :colon="false" labelAlign="left">
                      <a-form-model-item label="正则表达式" :wrapper-col="{ span: 20 }">
                        <a-input v-model="header.regExp">
                          <JavaRegExpExamplePopover slot="addonAfter" />
                        </a-input>
                      </a-form-model-item>
                    </a-form-model>
                  </template>
                </WidgetDesignModal> -->
              </template>
            </a-table>
            <a-form-model :label-col="{ span: 12, style: { width: '150px' } }">
              <a-form-model-item label="判断数据是否重复的列" :wrapper-col="{ style: { width: 'calc(100% - 150px)' } }">
                <a-select v-model="sheet.duplicateDataHeader" style="width: 100%" allow-clear mode="multiple">
                  <template v-for="(header, e) in sheet.header">
                    <a-select-option :value="header.title" v-if="header.code != undefined">{{ header.title }}</a-select-option>
                  </template>
                </a-select>
              </a-form-model-item>
              <a-form-model-item
                v-if="sheet.duplicateDataHeader && sheet.duplicateDataHeader.length > 0"
                label="数据重复时"
                :wrapper-col="{ style: { width: 'calc(100% - 150px)' } }"
              >
                <a-radio-group v-model="sheet.duplicateStrategy" button-style="solid" size="small">
                  <a-radio-button value="ignore">跳过</a-radio-button>
                  <a-radio-button value="update">更新</a-radio-button>
                </a-radio-group>
              </a-form-model-item>

              <a-form-model-item :wrapper-col="{ style: { width: 'calc(100% - 150px)' } }" v-if="i > 0">
                <template slot="label">
                  关联父级从表
                  <a-checkbox v-model="sheet.join.enable" />
                </template>
                <div style="display: flex; align-items: center" v-if="sheet.join.enable">
                  <a-select
                    :key="'join_select' + sheet.join.formUuid"
                    :options="getCellColumnOptions(sheet.join.formUuid)"
                    :filterSelectOption="filterSelectOption"
                    show-search
                    v-model="sheet.join.joinColumn"
                    placeholder="选择字段"
                    style="width: 50%"
                    allow-clear
                  />
                  =
                  <a-select placeholder="选择列" v-model="sheet.join.joinHeader" style="width: 50%" allow-clear>
                    <template v-for="(header, e) in sheet.header">
                      <a-select-option :value="header.title">{{ header.title }}</a-select-option>
                    </template>
                  </a-select>
                </div>
              </a-form-model-item>
            </a-form-model>
          </div>
        </a-tab-pane>
      </template>
    </a-tabs>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import moment from 'moment';
import { copyToClipboard } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function';
export default {
  name: 'SubformImportSheetConfiguration',
  inject: ['appId'],
  props: { widget: Object, designer: Object, importRule: Object, button: Object },
  components: {},
  computed: {
    subformColumnOptions() {
      let opt = [];
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        opt.push({
          label: this.widget.configuration.columns[i].title,
          value: this.widget.configuration.columns[i].dataIndex
        });
        let colWidget = this.widget.configuration.columns[i].widget;
        if (colWidget.wtype == 'WidgetFormDatePicker' && colWidget.subtype == 'Range') {
          opt.push({
            label: this.widget.configuration.columns[i].title + '(结束时间)',
            value: colWidget.configuration.endDateField
          });
        }
      }
      return opt;
    }
  },
  data() {
    let year = moment().year();
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      columnOptions: {},

      dateFormatOptions: [
        { value: 'yyyy-MM-dd HH:mm:ss', label: `${year}-01-01 00:00:00` },
        { value: 'yyyy-MM-dd HH:mm', label: `${year}-01-01 00:00` },
        { value: 'yyyy-MM-dd HH', label: `${year}-01-01 00` },
        { value: 'yyyy年MM月dd日 HH时mm分ss秒', label: `${year}年01月01日 00时00分00秒` },
        { value: 'yyyy年MM月dd日', label: `${year}年01月01日` }
      ],
      subformOptions: [],
      subSubformColumnOptions: {}
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.fetchSubformOptions();
  },
  methods: {
    getCellColumnOptions(formUuid) {
      for (let i = 0, len = this.subformOptions.length; i < len; i++) {
        if (this.subformOptions[i].parentDyform.formUuid == formUuid) {
          return this.subformOptions[i].parentDyform.columns;
        }
      }
      return [];
    },
    onChangeSubformSelect(key, node, sheet) {
      sheet.join.formUuid = undefined;
      if (key) {
        sheet.join.formUuid = node.data.props.parentDyform.formUuid;
      }
    },
    fetchSubformOptions() {
      if (this.widget.configuration.editFormUuid != undefined) {
        let cascadeGetOptions = formUuid => {
          this.fetchDyformDefinition(formUuid).then(def => {
            // console.log(JSON.parse(def.definitionJson));
            let vjson = JSON.parse(def.definitionVjson);
            let subforms = vjson.subforms;
            let parentDyformColumnOptions = [];
            vjson.fields.forEach(f => {
              parentDyformColumnOptions.push({
                label: f.configuration.name,
                value: f.configuration.code
              });
            });
            if (subforms != undefined && subforms.length > 0) {
              for (let i = 0, len = subforms.length; i < len; i++) {
                this.subformOptions.push({
                  label: subforms[i].configuration.formName,
                  value: subforms[i].configuration.formUuid,
                  parentDyform: {
                    formUuid,
                    columns: parentDyformColumnOptions
                  }
                });
                let colOptions = [];
                subforms[i].configuration.columns.forEach(col => {
                  colOptions.push({
                    label: col.title,
                    value: col.dataIndex
                  });
                });
                this.$set(this.subSubformColumnOptions, subforms[i].configuration.formUuid, colOptions);
                if (subforms[i].configuration.editFormUuid != undefined) {
                  cascadeGetOptions.call(this, subforms[i].configuration.editFormUuid);
                }
              }
            }
          });
        };
        cascadeGetOptions.call(this, this.widget.configuration.editFormUuid);
      }
    },
    fetchDyformDefinition(formUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/dyform/definition/getFormDefinitionByUuid?formUuid=${formUuid}`, {})
          .then(({ data }) => {
            resolve(data);
          })
          .catch(error => {});
      });
    },
    restSheetHeaderFieldMapping(sheet) {
      if (sheet && sheet.header) {
        let header = sheet.header;
        for (let i = 0, len = header.length; i < len; i++) {
          header[i].code = undefined;
        }
      }
    },
    getSheetHeaderColumn(sheet) {
      let cols = [],
        header = sheet.header;
      for (let i = 0, len = header.length; i < len; i++) {
        cols.push({
          // title: header[i].title,
          dataIndex: 'header_' + i,
          width: 200,
          slots: { title: 'customTitle_' + i },
          scopedSlots: { customRender: 'configSlot_' + i }
        });
      }
      return cols;
    },

    getSheetHeaderConfigRow(sheet) {
      let data = {},
        rows = [data],
        header = sheet.header;
      for (let i = 0, len = header.length; i < len; i++) {
        data['header_' + i] = header[i].config;
      }
      return rows;
    },
    filterSelectOption,
    onChangeHeaderColumn(e, opt, header) {
      if (opt) {
        header.dataType = opt.data.props.dataType || 'string';
        header.required = opt.data.props.required === true;
      }
    },

    onClickCopy(e, text) {
      let _this = this;
      copyToClipboard(text, e, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    }
  },

  watch: {
    importRule: {
      deep: true,
      handler(v) {
        console.log('配置变更', v);
      }
    }
  }
};
</script>
