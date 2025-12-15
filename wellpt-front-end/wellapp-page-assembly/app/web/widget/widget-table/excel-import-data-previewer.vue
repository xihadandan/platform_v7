<template>
  <span :style="{ display: elementHide ? 'none' : 'inline-block' }">
    <span v-on:click="onTrigger">
      <slot></slot>
    </span>
    <a-drawer
      :title="title"
      placement="top"
      :visible="visible"
      height="calc(100vh)"
      @close="onClose"
      :z-index="9999999"
      :afterVisibleChange="afterVisibleChange"
    >
      <div class="excel-file-data-display">
        <a-tabs type="card" default-active-key="1" tab-position="bottom" v-model="activeTabKey" @change="onTabChange" ref="sheetTabs">
          <template v-for="(sheet, index) in sheets">
            <a-tab-pane :key="sheet.name" :tab="sheet.name" :forceRender="true">
              <Scroll style="height: calc(100vh - 140px)">
                <template v-if="!sheet.loading">
                  <div
                    v-show="sheet.html != undefined && sheet.html != ''"
                    v-html="sheet.html"
                    :class="['excel-table-container', filterErrorRecord ? 'error-filtered' : '']"
                    @click="onClickSheet"
                    :id="'sheet_' + index"
                    ref="sheet"
                  ></div>
                  <a-empty v-show="sheet.html == undefined || sheet.html == ''" />
                </template>
                <div class="spin-center" v-else>
                  <a-spin />
                </div>
              </Scroll>
            </a-tab-pane>
          </template>
          <template slot="tabBarExtraContent" v-if="importSheetResults.length > 0">
            <a-checkbox v-model="filterErrorRecord">仅查看导入错误的记录</a-checkbox>
            <a-divider type="vertical" />
            <a-button type="danger" @click="onClickExportErrorSheetData">导出错误数据</a-button>
          </template>
        </a-tabs>
      </div>
    </a-drawer>
  </span>
</template>
<style lang="less">
.excel-table-container {
  &.error-filtered {
    tr.success {
      display: none !important;
    }
  }
  table {
    table-layout: fixed;
    width: 100%;
    border-collapse: collapse;
    font-size: 14px;
  }
  tr {
    cursor: pointer;
    &.header {
      font-weight: bold;
      background-color: #fafafa !important;
    }
    &.error {
      background-color: #fff1f0 !important;
    }
  }
  tr:hover {
    background-color: var(--w-primary-color-1);
  }

  th,
  td {
    &.import-result-td {
      width: 220px;
    }
    padding: 4px;
    text-align: left;
    &[colspan] {
      text-align: center;
    }
    border: 1px solid #e0e0e0;
    &.success {
      color: #52c41a;
    }
    &.error {
      color: #f5222d;
    }
    .success {
      color: #52c41a;
    }
    .error {
      color: #f5222d;
    }
  }
}
</style>
<script type="text/babel">
export default {
  name: 'ExcelImportDataPreviewer',
  props: {
    file: Object,
    importResult: Object,
    sheetRules: Array
  },
  components: {},
  computed: {},
  data() {
    return {
      loading: true,
      filterErrorRecord: false,
      sheets: [],
      importSheetResults: [],
      visible: false,
      activeTabKey: undefined,
      elementHide: this.$slots.default == undefined,
      vModel: this.$listeners.input != undefined,
      title: this.file != undefined ? this.file.name || '数据' : '数据'
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onClickExportErrorSheetData() {
      let workbook = this.workbook,
        XLSX = this.XLSX,
        sheetResults = [];
      for (let item of this.importSheetResults) {
        sheetResults[item.sheetIndex] = item;
      }
      const newSheet = [];
      for (let i = 0, len = workbook.SheetNames.length; i < len; i++) {
        // 1. 原始数据转二维数组
        let originalWorksheet = workbook.Sheets[workbook.SheetNames[i]];
        let jsonData = XLSX.utils.sheet_to_json(originalWorksheet, { header: 1 });
        let output = undefined;
        if (sheetResults[i] != undefined) {
          let errorData = [];
          let headerRowIndex = this.sheetRules[i] ? this.sheetRules[i].headerRowIndex || 1 : 1;
          // 有错误数据
          for (let item of sheetResults[i].results) {
            if (!item.ok) {
              errorData.push(jsonData[item.rowIndex - 1]);
            }
          }
          // 添加表头数据
          let header = jsonData.splice(0, headerRowIndex);
          output = header.concat(errorData);
        } else {
          output = [];
        }
        newSheet.push(XLSX.utils.aoa_to_sheet(output));
      }
      const newWorkbook = XLSX.utils.book_new();
      for (let i = 0, len = workbook.SheetNames.length; i < len; i++) {
        XLSX.utils.book_append_sheet(newWorkbook, newSheet[i], workbook.SheetNames[i]);
      }
      this.XLSX.writeFile(newWorkbook, `导出错误数据_${new Date().format('yyyyMMDDHHmmss')}.xlsx`);
    },
    updateImportResult(importSheetResults) {
      if (importSheetResults != undefined) {
        this.importSheetResults.splice(0, this.importSheetResults.length);
        this.importSheetResults.push(...importSheetResults);
      }
      if (this.importSheetResults.length > 0 && this.visible) {
        let sheetResults = [],
          sheetRowImportResult = {};
        for (let item of this.importSheetResults) {
          sheetResults[item.sheetIndex] = item;
          let rowResult = [];
          for (let i of item.results) {
            rowResult[i.rowIndex] = i;
          }
          sheetRowImportResult[item.sheetIndex] = rowResult;
        }
        let appendImportMessage = i => {
          let s = this.$refs.sheetTabs.$el.querySelector('#sheet_' + i);
          let allTrs = s.querySelectorAll('tr'),
            rowResult = sheetRowImportResult[i],
            headerRowIndex = this.sheetRules[i].headerRowIndex || 1;
          let count = { success: 0, error: 0 };
          for (let r of rowResult) {
            if (r != undefined) {
              count[r.ok ? 'success' : 'error']++;
            }
          }
          for (let t = 0; t < allTrs.length; t++) {
            let td = allTrs[t].querySelector('.import-result-td'),
              append = td == null;
            if (append) {
              td = document.createElement('td');
            }
            if (t == 0) {
              if (append) {
                td.innerHTML = `导入结果: <span class="success">成功 (${count.success})</span> / <span class="error">错误 (${count.error})</span>`;
                td.setAttribute('rowspan', headerRowIndex);
                td.setAttribute('class', 'import-result-td');
                allTrs[t].appendChild(td);
              }
            } else if (t > headerRowIndex - 1) {
              // 数据行
              let ret = rowResult[t + 1];
              td.innerText = ret.msg || '导入处理成功';
              td.setAttribute('class', ret.ok ? 'import-result-td success' : 'import-result-td error');
              allTrs[t].setAttribute('class', ret.ok ? 'success' : 'error');
              if (append) {
                allTrs[t].appendChild(td);
              }
            }
          }
          this.sheets[i].errorMessageAppended = true;
        };
        for (let i = 0, len = this.sheets.length; i < len; i++) {
          if (this.sheets[i].errorMessageAppended == undefined) {
            if (this.sheets[i].html) {
              appendImportMessage(i);
            } else {
              // 待渲染
              this.renderSheetHtml(this.sheets[i].name).then(() => {
                appendImportMessage(i);
              });
            }
          }
        }
      }
    },
    afterVisibleChange() {
      if (this.visible) {
        this.updateImportResult();
      }
    },
    onClickSheet(e) {
      console.log('点击单元格', e);
    },
    onClose() {
      this.visible = false;
      if (this.vModel) {
        this.$emit('input', false);
      }
    },
    onTrigger() {
      this.visible = true;
      if (this.workbook == undefined) {
        this.dataRender();
      } else if (this.sheets.length > 0) {
        this.activeTabKey = this.sheets[0].name;
      }
      if (this.vModel) {
        this.$emit('input', true);
      }
    },
    onTabChange(activeTabKey) {
      this.$nextTick(() => {
        this.renderSheetHtml(activeTabKey);
      });
    },
    renderSheetHtml(sheetName) {
      return new Promise((resolve, reject) => {
        if (this.workbook != undefined) {
          for (let i = 0, len = this.workbook.SheetNames.length; i < len; i++) {
            if (sheetName == this.workbook.SheetNames[i] && this.sheets[i].loading) {
              try {
                this.sheets[i].loading = false;
                this.sheets[i].html = this.XLSX.utils.sheet_to_html(this.workbook.Sheets[this.sheets[i].name]);
                // 更新表头样式
                this.$nextTick(() => {
                  let s = this.$refs.sheetTabs.$el.querySelector('#sheet_' + i);
                  let headerRowIndex = this.sheetRules[i].headerRowIndex || 1;
                  let trs = s.querySelectorAll('tr');
                  for (let i = 0; i < trs.length; i++) {
                    if (i <= headerRowIndex - 1) {
                      trs[i].setAttribute('class', 'header');
                    } else if (i > headerRowIndex - 1) {
                      break;
                    }
                  }
                });
              } catch (error) {
                // 可能是空的无法转换
              }

              break;
            }
          }
        }
        resolve();
      });
    },

    dataRender() {
      import('@framework/vue/lib/xlsx/xlsx.mjs').then(XLSX => {
        this.XLSX = XLSX;
        window.XLSX = XLSX;
        const reader = new FileReader();
        reader.onload = e => {
          const data = new Uint8Array(e.target.result);
          let workbook = undefined;
          this.loading = false;
          try {
            workbook = XLSX.read(data, { type: 'array' });
            window.WORKBOOK = workbook;
          } catch (e) {
            this.$message.error('文件解析异常, 该文件已被加密或格式内容有误');
            return;
          }
          this.workbook = workbook;
          for (let i = 0, len = workbook.SheetNames.length; i < len; i++) {
            this.sheets.push({
              name: workbook.SheetNames[i],
              loading: true,
              html: undefined
            });
            if (i == 0) {
              this.renderSheetHtml(workbook.SheetNames[i]);
            }
          }
          this.activeTabKey = this.sheets[0].name;
        };
        reader.onerror = e => {
          reject(e);
        };
        reader.readAsArrayBuffer(this.file);
      });
    }
  }
};
</script>
