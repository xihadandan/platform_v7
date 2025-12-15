<template>
  <div>
    <a-form-model :label-col="{ span: 12, style: { width: '150px' } }">
      <a-form-model-item :wrapper-col="{ style: { width: 'calc(100% - 150px)' } }">
        <template slot="label">
          自定义导入服务
          <a-checkbox :checked="importRule.importService !== defaultImportService" @change="onChangeImportServiceChecked" />
        </template>
        <template v-if="importRule.importService !== defaultImportService">
          <a-select
            v-model="importRule.importService"
            :options="listenerServiceOptions"
            style="width: calc(100% - 30px)"
            @change="onChangeImportService"
          />
          <a-popover placement="bottomRight" :arrowPointAtCenter="true">
            <template slot="content">
              <div>
                提供继承于
                <code>com.wellsoft.context.util.excel.AbstractEasyExcelImportListener</code>
                的后端服务类
                <a-button size="small" type="link" @click="clickCopyImportListenerClassCode">复制代码样例</a-button>
              </div>
            </template>
            <a-icon style="margin-left: 5px" type="info-circle" theme="twoTone" />
          </a-popover>
        </template>
      </a-form-model-item>
      <template v-if="importRule.importService !== defaultImportService">
        <a-form-model-item :wrapper-col="{ style: { width: 'calc(100% - 150px)' } }">
          <template slot="label">记录导入日志</template>
          <a-switch v-model="importRule.importLog" />
        </a-form-model-item>
        <a-form-model-item :wrapper-col="{ style: { width: 'calc(100% - 150px)' } }">
          <template slot="label">
            <a-tooltip title="严格模式开启后, 将对导入的工作表列与模板头部进行一致性匹配">严格模式</a-tooltip>
          </template>
          <a-switch v-model="importRule.strict" />
        </a-form-model-item>
      </template>
    </a-form-model>

    <a-tabs
      v-show="importRule.importService == defaultImportService"
      size="small"
      default-active-key="0_sheet"
      tab-position="bottom"
      type="card"
      @change="e => onTabChange(e)"
    >
      <template slot="tabBarExtraContent">
        记录导入日志
        <a-checkbox v-model="importRule.importLog" />
        <a-divider type="vertical" />
        <a-tooltip title="严格模式开启后, 将对导入的工作表列与模板头部进行一致性匹配">严格模式</a-tooltip>
        <a-checkbox v-model="importRule.strict" />
      </template>
      <template v-for="(sheet, i) in importRule.sheetConfig">
        <a-tab-pane :key="i + '_sheet'" :tab="sheet.sheetName">
          <div style="height: calc(100vh - 270px)">
            <a-table
              :pagination="false"
              :bordered="true"
              :columns="getSheetHeaderColumn(sheet)"
              :data-source="getSheetHeaderConfigRow(sheet)"
              :scroll="{ x: 150 * sheet.header.length }"
            >
              <template slot="title">
                <div style="display: flex; align-items: baseline; justify-content: space-between">
                  <div>
                    <a-radio-group v-model="sheet.params.importToType" button-style="solid" size="small">
                      <a-radio-button value="dyform">表单</a-radio-button>
                      <a-radio-button value="dataModel">数据模型</a-radio-button>
                    </a-radio-group>
                    字段与工作表列映射
                  </div>
                  <div>
                    <DataModelSelectModal
                      style="width: 250px"
                      v-model="sheet.params.dataModelUuid"
                      dtype="TABLE"
                      ref="dataModelSelect"
                      v-if="sheet.params.importToType == 'dataModel'"
                      @change="(e, dataModel) => onDataModelChange(e, dataModel, sheet)"
                    />

                    <a-select
                      v-else
                      show-search
                      style="width: 250px"
                      :options="dyformDefinitionOption"
                      v-model="sheet.params.formUuid"
                      @change="(e, opt) => onChangeDyformSelect(e, opt, sheet)"
                      :filter-option="filterOption"
                    ></a-select>
                  </div>
                </div>
              </template>
              <template v-for="(header, h) in sheet.header" :slot="'customTitle_' + h">
                <span v-show="header.required" style="color: red">*</span>
                {{ header.title }}
              </template>
              <template v-for="(header, h) in sheet.header" :slot="'configSlot_' + h" slot-scope="text, record">
                <a-select
                  :options="getCellColumnOptions(sheet)"
                  :filterOption="filterOption"
                  show-search
                  v-model="header.code"
                  placeholder="选择字段"
                  style="width: calc(100% - 50px)"
                  @change="(e, opt) => onChangeHeaderColumn(e, opt, header)"
                  allow-clear
                />
                <WidgetDesignModal
                  :id="'WidgetTableBtnConfigImportSheetFieldRuleConfig' + button.id + i + '_' + h"
                  title="字段规则"
                  :designer="designer"
                  :zIndex="1000"
                >
                  <a-button type="link" size="small">配置</a-button>
                  <template slot="content">
                    <a-form-model :label-col="{ span: 4 }" :colon="false" labelAlign="left">
                      <a-form-model-item label="数据类型" :wrapper-col="{ span: 20 }">
                        <a-radio-group v-model="header.dataType" button-style="solid" size="small">
                          <a-radio-button value="string">字符</a-radio-button>
                          <a-radio-button value="number">数字</a-radio-button>
                          <a-radio-button value="date">日期</a-radio-button>
                        </a-radio-group>
                        <a-input v-if="header.dataType == 'date'" v-model="header.format" placeholder="日期格式" allow-clear>
                          <a-popover title="常用日期格式" slot="addonAfter">
                            <template slot="content">
                              <a-row
                                type="flex"
                                v-for="(opt, i) in dateFormatOptions"
                                :key="'df_' + i"
                                style="flex-wrap: nowrap; margin-bottom: 8px; width: 480px"
                              >
                                <a-col flex="150px">{{ opt.label }}</a-col>
                                <a-col flex="auto">
                                  <a-tag @click="e => onClickCopy(e, opt.value)">{{ opt.value }}</a-tag>
                                </a-col>
                              </a-row>
                            </template>
                            <a-icon type="info-circle" theme="filled" />
                          </a-popover>
                        </a-input>
                      </a-form-model-item>
                      <a-form-model-item label="必填" :wrapper-col="{ span: 20 }">
                        <a-switch v-model="header.required" />
                      </a-form-model-item>
                      <a-form-model-item label="正则表达式" :wrapper-col="{ span: 20 }">
                        <a-input v-model="header.regExp">
                          <JavaRegExpExamplePopover slot="addonAfter" />
                        </a-input>
                      </a-form-model-item>
                      <a-form-model-item label="值转换" :wrapper-col="{ span: 20 }">
                        <a-radio-group v-model="header.transformValueType" button-style="solid" size="small">
                          <a-radio-button :value="undefined">无</a-radio-button>
                          <a-radio-button value="dataDict">数据字典</a-radio-button>
                          <a-radio-button value="define">自定义</a-radio-button>
                        </a-radio-group>
                        <template v-if="header.transformValueType == 'define'">
                          <a-table
                            size="small"
                            :pagination="false"
                            bordered
                            :columns="[{ title: '转换', dataIndex: 'value', scopedSlots: { customRender: 'valueSlot' } }]"
                            :data-source="header.transformValueOption"
                          >
                            <template slot="valueSlot" slot-scope="text, record, index">
                              <div style="display: flex; align-items: center">
                                <div>
                                  <a-input addon-before="文本" v-model="record.label" size="small" />
                                  <a-input addon-before="数值" v-model="record.value" size="small" />
                                </div>
                                <a-button
                                  size="small"
                                  type="link"
                                  icon="delete"
                                  @click="header.transformValueOption.splice(index, 1)"
                                ></a-button>
                              </div>
                            </template>
                            <template slot="footer">
                              <div>
                                <a-button
                                  @click="header.transformValueOption.push({ label: undefined, value: undefined })"
                                  icon="plus"
                                  size="small"
                                  type="link"
                                  :block="true"
                                >
                                  新增
                                </a-button>
                              </div>
                            </template>
                          </a-table>
                        </template>
                        <a-tree-select
                          v-else-if="header.transformValueType == 'dataDict'"
                          showSearch
                          allowClear
                          v-model="header.transformValueDataDictCode"
                          style="width: 100%"
                          treeNodeFilterProp="title"
                          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                          :tree-data="dataDictionaryTreeData"
                          :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
                        ></a-tree-select>
                      </a-form-model-item>
                    </a-form-model>
                  </template>
                </WidgetDesignModal>
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

              <a-form-model-item :wrapper-col="{ style: { width: 'calc(100% - 150px)' } }">
                <template slot="label">
                  关联父级表
                  <a-checkbox v-model="sheet.join.enable" />
                </template>
                <a-select
                  v-if="sheet.join.enable"
                  show-search
                  style="width: 100%"
                  :options="dyformDefinitionOption"
                  v-model="sheet.join.formUuid"
                  @change="(e, opt) => onChangeDyformSelect(e, opt, sheet.join)"
                  :filter-option="filterOption"
                ></a-select>
              </a-form-model-item>

              <template v-if="sheet.join.enable && sheet.join.table">
                <a-form-model-item :wrapper-col="{ style: { width: 'calc(100% - 150px)' } }">
                  <template slot="label">关联字段与工作表列</template>
                  <div style="display: flex; align-items: center">
                    <a-select
                      :options="getCellColumnOptions(sheet.join.formUuid)"
                      :filterOption="filterOption"
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
              </template>
            </a-form-model>
          </div>
        </a-tab-pane>
      </template>
    </a-tabs>
    <!-- <a-form-model>
      <a-form-model-item label="工作表">

      </a-form-model-item>
    </a-form-model> -->
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import moment from 'moment';
import { copyToClipboard } from '@framework/vue/utils/util';

export default {
  name: 'ImportSheetConfiguration',
  inject: ['appId'],
  props: { widget: Object, designer: Object, importRule: Object, button: Object },
  components: {},
  computed: {},
  data() {
    let year = moment().year();
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      columnOptions: {},
      dyformDefinitionOption: [],
      dataDictionaryTreeData: [],
      defaultImportService: 'com.wellsoft.pt.dms.ext.excel.listener.DyformDataExcelImportListener',
      dateFormatOptions: [
        { value: 'yyyy-MM-dd HH:mm:ss', label: `${year}-01-01 00:00:00` },
        { value: 'yyyy-MM-dd HH:mm', label: `${year}-01-01 00:00` },
        { value: 'yyyy-MM-dd HH', label: `${year}-01-01 00` },
        { value: 'yyyy年MM月dd日 HH时mm分ss秒', label: `${year}年01月01日 00时00分00秒` },
        { value: 'yyyy年MM月dd日', label: `${year}年01月01日` }
      ],
      listenerServiceOptions: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchDyformDefinition();
    this.fetchDataDictionaryTreeData();
    if (this.importRule.sheetConfig.length > 0) {
      let { dataModelUuid, importToType, formUuid } = this.importRule.sheetConfig[0].params;
      if (dataModelUuid) {
        this.onDataModelChange(dataModelUuid);
      }
      if (formUuid) {
        this.onChangeDyformSelect(formUuid);
      }
      if (this.importRule.sheetConfig[0].join.formUuid) {
        this.onChangeDyformSelect(this.importRule.sheetConfig[0].join.formUuid);
      }
    }
    this.fetchImportListenerServiceOptions();
  },
  mounted() {
    import('./code-snippets').then(m => {
      this.codeSnippets = m.default;
    });
  },
  methods: {
    onChangeImportServiceChecked() {
      if (this.importRule.importService == this.defaultImportService) {
        this.importRule.importService = undefined;
        this.importRule.importCode = undefined;
      } else {
        this.importRule.importService = this.defaultImportService;
        this.importRule.importCode = this.importRule.sheetConfig[0].table;
      }
    },
    fetchDataDictionaryTreeData() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'cdDataDictionaryFacadeService',
          methodName: 'getAllDataDictionaryAsCategoryTree'
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            // data.data.selectable = false;
            _this.dataDictionaryTreeData = data.data.children;
            _this.dataDictionaryTreeData.forEach(node => {
              node.selectable = !node.nocheck;
            });
          }
        });
    },
    onTabChange(e) {
      let sheet = this.importRule.sheetConfig[parseInt(e)];
      let { dataModelUuid, importToType, formUuid } = sheet.params;
      if (dataModelUuid) {
        this.onDataModelChange(dataModelUuid);
      }
      if (formUuid) {
        this.onChangeDyformSelect(formUuid);
      }
      if (sheet.join && sheet.join.formUuid) {
        this.onChangeDyformSelect(sheet.join.formUuid);
      }
    },
    fetchDyformDefinition() {
      $axios
        .get(`/proxy/api/app/module/queryRelaModuleIds`, {
          params: {
            moduleId: this.appId
          }
        })
        .then(({ data }) => {
          let ids = data.data || [];
          ids.push(this.appId);
          $axios
            .post(`/proxy/api/dyform/definition/queryFormDefinitionNoJsonByModuleIds`, ids)
            .then(({ data }) => {
              let options = [];
              if (data.code == 0 && data.data) {
                for (let i = 0, len = data.data.length; i < len; i++) {
                  options.push({
                    label: data.data[i].name + ` (v${data.data[i].version})`,
                    value: data.data[i].uuid,
                    table: data.data[i].tableName
                  });
                }
              }
              this.dyformDefinitionOption = options;
            })
            .catch(error => {});
        });
    },
    fetchDyformDefinitionFieldOptions(formUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/dyform/definition/getFormDefinitionByUuid?formUuid=${formUuid}`, {})
          .then(({ data }) => {
            let fields = JSON.parse(data.definitionJson).fields;
            let options = [];
            for (let f in fields) {
              let dataType = 'string';
              if (fields[f].dbDataType == '2') {
                dataType = 'date';
              } else if (['13', '131', '132', '14', '15', '12', '17'].includes(fields[f].dbDataType)) {
                dataType = 'number';
              }
              options.push({
                label: fields[f].displayName,
                dataType,
                required: fields[f].configuration ? fields[f].configuration.required === true : false,
                value: f
              });
            }
            resolve(options);
          })
          .catch(error => {});
      });
    },
    onChangeDyformSelect(uuid, opt, sheet) {
      if (opt) {
        this.restSheetHeaderFieldMapping(sheet);
        sheet.table = opt.data.props.table;
        if (sheet.sheetIndex == 0) {
          this.importRule.importCode = sheet.table;
        }
      }
      if (uuid) {
        if (this.columnOptions[uuid] == undefined) {
          this.$set(this.columnOptions, uuid, []);
          this.fetchDyformDefinitionFieldOptions(uuid).then(data => {
            this.columnOptions[uuid].push(...data);
          });
        }
      }
    },
    fetchDataModelDetails(uuid) {
      return new Promise((resolve, reject) => {
        $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid } }).then(({ data, headers }) => {
          if (data.code == 0) {
            let detail = data.data,
              columns = JSON.parse(detail.columnJson);
            let columnIndexOptions = [];
            for (let col of columns) {
              if (detail.type == 'TABLE') {
                let dataType = col.dataType;
                if (dataType == 'varchar') {
                  dataType = 'string';
                } else if (dataType == 'timestamp') {
                  dataType = 'date';
                } else if (dataType == 'clob') {
                  dataType = 'string';
                }
                if (col.dataType)
                  columnIndexOptions.push({
                    dataIndex: col.alias || col.column,
                    title: col.title,
                    required: col.notNull === true,
                    dataType,
                    isSysDefault: col.isSysDefault
                  });
              }
            }
            resolve(columnIndexOptions);
          }
        });
      });
    },
    onDataModelChange(uuid, dataModel, sheet) {
      if (dataModel) {
        this.restSheetHeaderFieldMapping(sheet);
        sheet.table = `UF_${dataModel.id}`;
        if (sheet.sheetIndex == 0) {
          this.importRule.importCode = sheet.table;
        }
      }
      if (uuid) {
        if (this.columnOptions[uuid] == undefined) {
          this.$set(this.columnOptions, uuid, []);
          this.fetchDataModelDetails(uuid).then(list => {
            list.forEach(d => {
              this.columnOptions[uuid].push({
                label: d.title,
                value: d.dataIndex,
                dataType: d.dataType
              });
            });
          });
        }
      }
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
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    onChangeHeaderColumn(e, opt, header) {
      if (opt) {
        header.dataType = opt.data.props.dataType || 'string';
        header.required = opt.data.props.required === true;
      }
    },
    getCellColumnOptions(sheet) {
      return (
        this.columnOptions[
          typeof sheet == 'string' ? sheet : sheet.params.importToType == 'dataModel' ? sheet.params.dataModelUuid : sheet.params.formUuid
        ] || []
      );
    },

    onClickCopy(e, text) {
      let _this = this;
      copyToClipboard(text, e, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    },
    onChangeImportService(e) {
      this.importRule.importCode = e.substr(e.lastIndexOf('.') + 1);
    },
    fetchImportListenerServiceOptions() {
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'excelImportListenerHolder',
          queryMethod: 'getAllExcelImportListenerClass'
        })
        .then(({ data }) => {
          if (data.results) {
            data.results.forEach(r => {
              if (r.id != this.defaultImportService) {
                this.listenerServiceOptions.push({
                  label: r.text,
                  value: r.id
                });
              }
            });
          }
        });
    },

    clickCopyImportListenerClassCode(e) {
      let _this = this;
      copyToClipboard(this.codeSnippets.javaImportListenerSnippet, e, function (success) {
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
