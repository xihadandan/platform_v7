<template>
  <ButtonConfiguration :button="button" :widget="widget" :designer="designer" ref="buttonConfiguration">
    <template slot="extraInfo">
      <a-form-model-item label="导入文件模板">
        <a-upload
          name="file"
          :showUploadList="false"
          :before-upload="e => beforeUpload(e, 10)"
          :customRequest="e => customRequest(e, afterUploadSuccess)"
        >
          <div>
            <a-button v-if="importRule.importFileTemplate.fileID" icon="file-excel" size="small" type="link" @click="downloadFile">
              {{ importRule.importFileTemplate.fileName }}
            </a-button>
            <a-button size="small" :icon="uploading ? 'loading' : 'upload'" class="favicon-upload-button">
              {{ importRule.importFileTemplate.fileID ? '替换' : '上传' }}
            </a-button>
            <template v-if="importRule.importFileTemplate.fileID">
              <a-divider type="vertical" />
              <WidgetDesignDrawer
                :closeOpenDrawer="false"
                :id="'WidgetTableBtnConfigImportSheetConfig' + button.id"
                title="工作表导入配置"
                :designer="designer"
              >
                <a-button icon="setting" size="small">配置</a-button>
                <template slot="content">
                  <SubformImportSheetConfiguration :import-rule="importRule" :widget="widget" :designer="designer" :button="button" />
                </template>
              </WidgetDesignDrawer>
            </template>
          </div>
        </a-upload>
      </a-form-model-item>

      <div v-if="importRule.importFileTemplate.fileID">
        <a-form-model-item label="导入弹窗">
          <a-radio-group v-model="importRule.importModalType" button-style="solid" size="small">
            <a-radio-button value="default">默认</a-radio-button>
            <a-radio-button value="template">自定义</a-radio-button>
          </a-radio-group>
        </a-form-model-item>

        <template v-if="importRule.importModalType === 'template'">
          <a-form-model-item>
            <template slot="label">
              <a-popover placement="left" title="模板说明">
                <template slot="content">
                  <p>支持属性:</p>
                  <p>1. fileTemplate: 导入文件模板对象</p>
                  <p>2. confirmImportFile: 执行导入文件数据函数</p>
                  <p>3. customFileUploadRequest: 本地文件上传函数</p>
                  <p><a-button size="small" type="link" @click="clickCopyImportModalTemplateCode">复制代码样例</a-button></p>
                </template>
                <div>
                  使用模板
                  <a-icon type="info-circle" />
                </div>
              </a-popover>
            </template>
            <a-select
              :options="vueTemplateOptions"
              :style="{ width: '100%' }"
              v-model="importRule.importModalTemplateName"
              :filter-option="filterOption"
              :showSearch="true"
              allowClear
            ></a-select>
          </a-form-model-item>
        </template>
        <template v-else>
          <a-form-model-item label="导入弹窗标题">
            <a-input v-model="importRule.modalTitle" placeholder="数据导入"></a-input>
          </a-form-model-item>
          <a-form-model-item label="导入弹窗宽度">
            <a-input v-model="importRule.modalWidth" placeholder="500"></a-input>
          </a-form-model-item>
          <a-form-model-item label="导入确认按钮文本">
            <a-input v-model="importRule.modalOkText" placeholder="导入"></a-input>
          </a-form-model-item>
        </template>
      </div>
    </template>
  </ButtonConfiguration>
</template>
<style lang="less"></style>
<script type="text/babel">
import SubformImportSheetConfiguration from './subform-import-sheet-configuration.vue';
import { copyToClipboard } from '@framework/vue/utils/util';
import { customFileUploadRequest } from '@framework/vue/utils/function';
import ButtonConfiguration from '../button-configuration.vue';
export default {
  name: 'SubformImportButtonConfiguration',
  props: { widget: Object, designer: Object, columnIndexOptions: Array, button: Object, importRule: Object, columnRenderOptions: Array },
  components: { SubformImportSheetConfiguration, ButtonConfiguration },
  computed: {},
  data() {
    return {
      uploading: false,
      vueTemplateOptions: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    // 获取vue模板实例
    let regExp = /wellapp[\w|-]+\/app\/web\/template\/.+\.vue$/;
    for (let key in window.Vue.options.components) {
      let comp = window.Vue.options.components[key];
      let META = comp.META;
      if (META) {
        this.vueTemplateOptions.push({
          label: META.fileName.replace('./', ''),
          value: key
        });
        continue;
      } else if (comp.options && comp.options.__file && regExp.test(comp.options.__file)) {
        this.vueTemplateOptions.push({
          label: comp.options.__file.substr(comp.options.__file.lastIndexOf('/') + 1),
          value: key
        });
      }
    }
  },
  mounted() {
    import('@pageAssembly/app/web/widget/widget-table/configuration/code-snippets').then(m => {
      this.codeSnippets = m.default;
    });
  },
  methods: {
    validate() {
      return new Promise((resolve, reject) => {
        this.$refs.buttonConfiguration.validate().then(valid => {
          if (valid) {
            resolve();
          }
        });
      });
    },
    downloadFile() {
      window.open(this.importRule.importFileTemplate.url, '_blank');
    },
    afterUploadSuccess(options, dbFile) {
      this.uploading = false;

      // 读取文件
      import('@framework/vue/lib/xlsx/xlsx.mjs').then(XLSX => {
        const reader = new FileReader();
        reader.onload = e => {
          const data = new Uint8Array(e.target.result);
          let workbook = undefined;
          try {
            workbook = XLSX.read(data, { type: 'array' });
          } catch (e) {
            this.$message.error('文件解析异常, 该文件已被加密或格式内容有误, 请检查后再导入');
            this.importRule.importFileTemplate.fileName = undefined;
            this.importRule.importFileTemplate.fileID = undefined;
            this.importRule.importFileTemplate.url = undefined;
            return;
          }

          let sheetNames = workbook.SheetNames,
            sheetIndex = 0;

          let resolveHeaderPromises = [];
          for (let i = 0, len = workbook.SheetNames.length; i < len; i++) {
            const worksheet = workbook.Sheets[workbook.SheetNames[i]];
            resolveHeaderPromises.push(this.getXlsxSheetHeader(worksheet, XLSX, workbook.SheetNames[i]));
          }
          Promise.all(resolveHeaderPromises)
            .then(headers => {
              if (this.importRule.sheetConfig.length > 0 && this.importRule.sheetConfig.length > sheetNames.length) {
                this.importRule.sheetConfig.splice(sheetNames.length);
              }
              if (this.importRule.sheetConfig.length > 0) {
                for (let i = 0; i < this.importRule.sheetConfig.length; i++) {
                  let sheetConfig = this.importRule.sheetConfig[i];
                  if (headers[i] == undefined) {
                    continue;
                  }
                  // 更新配置
                  sheetConfig.sheetName = sheetNames[i];
                  // 更新头部
                  const jsonData = headers[i].header;
                  sheetConfig.headerRowIndex = headers[i].index;
                  let oldHeader = JSON.parse(JSON.stringify(sheetConfig.header));
                  sheetConfig.header.forEach(h => {
                    oldHeader[h.title] = JSON.parse(JSON.stringify(h));
                  });
                  for (let j = 0, jlen = jsonData.length; j < jlen; j++) {
                    if (sheetConfig.header[j]) {
                      sheetConfig.header[j].title = jsonData[j];
                      if (oldHeader[jsonData[j]] == undefined) {
                        sheetConfig.header[j].code = undefined;
                        sheetConfig.header[j].dataType = 'string';
                        sheetConfig.header[j].required = false;
                        sheetConfig.header[j].regExp = undefined;
                        sheetConfig.header[j].transformValueOption.splice(0, sheetConfig.header[j].transformValueOption.length);
                        sheetConfig.header[j].transformValueDataDictCode = undefined;
                        sheetConfig.header[j].transformValueType = undefined;
                      }
                    } else {
                      if (oldHeader[jsonData[j]]) {
                        oldHeader[jsonData[j]].index = j;
                      }
                      sheetConfig.header.push(
                        oldHeader[jsonData[j]]
                          ? oldHeader[jsonData[j]]
                          : {
                              title: jsonData[j],
                              index: j,
                              code: undefined,
                              dataType: 'string', // string , number , date
                              required: false,
                              regExp: undefined,
                              transformValueOption: [],
                              transformValueDataDictCode: undefined,
                              transformValueType: undefined
                            }
                      );
                    }
                  }
                }
                sheetIndex = this.importRule.sheetConfig.length;
              }

              for (let i = sheetIndex, len = workbook.SheetNames.length; i < len; i++) {
                const jsonData = headers[i].header;
                let header = [];
                for (let j = 0, jlen = jsonData.length; j < jlen; j++) {
                  header.push({
                    title: jsonData[j],
                    index: j,
                    code: undefined,
                    dataType: 'string', // string , number , date
                    required: false,
                    regExp: undefined,
                    transformValueOption: [],
                    transformValueDataDictCode: undefined,
                    transformValueType: undefined
                  });
                }
                this.importRule.sheetConfig.push({
                  sheetName: workbook.SheetNames[i],
                  sheetIndex: i,
                  table: undefined,
                  headerRowIndex: headers[i].index,
                  duplicateDataHeader: [],
                  join: {
                    enable: false,
                    table: undefined,
                    formUuid: undefined,
                    joinColumn: undefined,
                    joinHeader: undefined
                  },
                  params: {
                    formUuid: i == 0 ? this.widget.configuration.formUuid : undefined
                  },
                  duplicateStrategy: 'ignore',
                  header
                });
              }

              this.importRule.importFileTemplate.fileID = dbFile.fileID;
              this.importRule.importFileTemplate.fileName = options.file.name;
              this.importRule.importFileTemplate.url = `/proxy-repository/repository/file/mongo/download?fileID=${dbFile.fileID}`;
            })
            .catch(e => {
              console.error(e);
            });
        };
        reader.onerror = e => {
          reject(e);
        };
        reader.readAsArrayBuffer(options.file);
      });
    },
    getXlsxSheetHeader(worksheet, XLSX, sheetName) {
      let _this = this;
      return new Promise((resolve, reject) => {
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
        if (jsonData.length > 1) {
          // 模板存在多个表头，需要用户选择使用那个表头
          let xlsxTemplateHtml = XLSX.utils.sheet_to_html(worksheet),
            headerIndex = undefined;

          this.$confirm({
            title: `请选择工作表[${sheetName}]中数据列对应的表头`,
            okText: '确定',
            content: h => {
              return h('div', {
                domProps: { innerHTML: xlsxTemplateHtml },
                class: 'xlsx-template-html-container',
                on: {
                  click: function (e) {
                    console.log('div', e);
                    let trs = e.target.closest('tbody').querySelectorAll('tr');
                    trs.forEach(tr => {
                      tr.setAttribute('class', '');
                    });
                    let tr = e.target.closest('tr');
                    tr.setAttribute('class', 'selected');
                    headerIndex = tr.rowIndex;
                  }
                }
              });
            },
            onOk(close) {
              if (headerIndex != undefined) {
                resolve({ header: jsonData[headerIndex], index: headerIndex + 1 });
                close();
              } else {
                _this.$message.error('请选择使用的数据列表头');
              }
            },
            onCancel(close) {
              close();
              reject();
            }
          });
        } else if (jsonData.length == 1) {
          resolve({ header: jsonData[0], index: 1 });
        } else {
          resolve(undefined);
        }
      });
    },
    customRequest(options, afterUpload) {
      this.uploading = true;
      this.importRule.importFileTemplate.fileName = undefined;
      this.importRule.importFileTemplate.fileID = undefined;
      this.importRule.importFileTemplate.url = undefined;
      customFileUploadRequest(options).then(dbFile => {
        this.afterUploadSuccess(options, dbFile);
      });
    },
    beforeUpload(file, limitSize) {
      return new Promise((resolve, reject) => {
        if (
          [
            'application/vnd.ms-excel',
            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            'application/wps-office.xlsx',
            'application/wps-office.xls'
          ].includes(file.type)
        ) {
          resolve(file);
        } else {
          this.$message.error('只允许上传 xls 或者 xlsx 文件格式');
          reject();
        }
      });
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    clickCopyImportModalTemplateCode(e) {
      let _this = this;
      copyToClipboard(this.codeSnippets.tableImportModalSnippet, e, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    }
  }
};
</script>
