<template>
  <div>
    <div style="margin-bottom: 8px">
      <div style="display: flex; align-items: flex-start">
        <a-checkable-tag
          :style="{
            margin: '0px 0px 5px 0px',
            backgroundColor: selectCheckAll ? 'var(--w-primary-color)' : 'var(--w-primary-color-1)',
            color: selectCheckAll ? '#fff' : 'var(--w-primary-color)'
          }"
          :checked="selectCheckAll"
          @change="chagneTagAll"
        >
          <a-icon
            :type="
              selectCheckAll || (selectedIds.length > 0 && selectedIds.length == selectOptions.length) ? 'check-square' : 'minus-square'
            "
            theme="filled"
          />
          全部
        </a-checkable-tag>
        <a-divider type="vertical" style="height: 26px" />
        <div style="display: flex; width: calc(100% - 300px); flex-wrap: wrap">
          <a-checkable-tag
            v-for="tag in selectOptions"
            :key="tag.value"
            :checked="selectedIds.indexOf(tag.value) > -1"
            @change="checked => changeTag(tag, checked)"
            style="margin-bottom: 5px"
          >
            {{ tag.label }}
          </a-checkable-tag>
        </div>
        <div style="display: flex; align-items: center">
          <a-button v-show="rows.length > 0" @click="onClickTranslateAll" :icon="translateAllLoading ? 'loading' : 'global'">
            翻译全部
          </a-button>
          <a-divider type="vertical" />
          <a-input-search v-model="searchText" allow-clear placeholder="国际化内容" />
          <a-button size="small" type="link" @click="resetSearchForm">重置</a-button>
        </div>
      </div>
    </div>

    <a-table
      :columns="columns"
      :data-source="rowsFiltered"
      :rowKey="getRowKey"
      :pagination="pagination"
      @change="handleTableChange"
      :loading="loading"
    >
      <template slot="nameSlot" slot-scope="text, record">
        <a-button size="small" type="link" @click.stop="handleRowName(record)">
          {{ record.name }}
        </a-button>
      </template>

      <template v-for="(langKey, i) in Object.keys(langMap)" :slot="langKey + 'Slot'" slot-scope="text, record">
        <a-input v-if="langKey == 'zh_CN'" v-model="record.i18n.zh_CN[record.code]" :readOnly="true">
          <template slot="addonAfter">
            <a-button
              size="small"
              type="link"
              @click="translateText(record, langKey)"
              :icon="record.loading[langKey] ? 'loading' : undefined"
            >
              翻译
            </a-button>
          </template>
        </a-input>
        <a-input v-else v-model="record[langKey]" @change="event => onChangeLanguageText(event, record, langKey)">
          <template slot="suffix">
            <a-icon
              style="color: var(--w-primary-color)"
              :type="record.loading[langKey] ? 'loading' : 'sync'"
              @click="translateText(record, langKey)"
              title="翻译"
            />
          </template>
        </a-input>
      </template>
    </a-table>
  </div>
</template>
<script>
const flowProperty = 'flowProperty';
const propertyI18nCodes = ['workflowName', 'defaultSubmitOpinionText', 'supplementTaskName'];
const propertyI18nKeys = ['name', 'defaultSubmitOpinionText', 'supplementTaskName'];
const propertyI18nPathCodes = {
  '/name': 'workflowName',
  '/autoSubmitRule/defaultSubmitOpinionText': 'defaultSubmitOpinionText',
  '/autoSubmitRule/supplementTaskName': 'supplementTaskName'
};
import { debounce, set } from 'lodash';
import { collectI18nEntries } from '../designer/utils';

export default {
  name: 'FlowI18nView',
  inject: ['workFlowData', 'graph'],
  data() {
    let columns = [
      {
        title: '组件',
        width: 150,
        dataIndex: 'name',
        scopedSlots: {
          customRender: 'nameSlot'
        },
        ellipsis: true
      }
    ];
    return {
      loading: true,
      languageOptions: [],
      pagination: { current: 1, pageSize: 8, hideOnSinglePage: true },
      rows: [],
      columns,
      selectOptions: [],
      selectedIds: [],
      selectCheckAll: true,
      searchText: undefined,
      onlyTranslateEmpty: true,
      translateAllLoading: false,
      selectedTypes: [],
      cellsData: null,
      propertyRows: [],
      nodeRowsMap: {},
      edgeRowsMap: {},
      gatewayRowsMap: {}
    };
  },
  computed: {
    rowsFiltered() {
      let rows = [];

      let filterSource = [];
      if (this.selectedTypes.length) {
        for (const type of this.selectedTypes) {
          if (type === flowProperty) {
            // 流程属性
            for (const item of this.propertyRows) {
              filterSource.push(item);
            }
          } else {
            for (const key in this[`${type}RowsMap`]) {
              if (this.selectedIds.includes(key)) {
                for (const item of this[`${type}RowsMap`][key]) {
                  filterSource.push(item);
                }
              }
            }
          }
        }
      } else {
        filterSource = this.rows;
      }
      for (let r of filterSource) {
        if (
          (this.selectedIds.length == 0 || this.selectedIds.includes(r.rootId)) &&
          (this.searchText == undefined ||
            this.searchText == '' ||
            r.code.toLowerCase().indexOf(this.searchText.toLowerCase()) > -1 ||
            this.matchLangText(r))
        ) {
          rows.push(r);
        }
      }
      return rows;
    },
    langMap() {
      let map = {};
      for (let o of this.languageOptions) {
        map[o.value] = o;
      }
      return map;
    }
  },
  created() {
    this.getCellsData();
  },
  beforeMount() {
    this.fetchLocaleOptions().then(options => {
      this.languageOptions.push(...options);
      this.getSelectOptions();
      this.initRows();
      this.loading = false;
      for (let o of options) {
        this.columns.push({
          title: `${o.description || o.label} / ${o.value}`,
          dataIndex: o.value,
          i18nColumn: true,
          scopedSlots: {
            customRender: o.value + 'Slot'
          }
        });
      }
    });
  },
  methods: {
    getCellsData() {
      let cellsData = {};
      const cellsDataKey = ['node', 'edge', 'gateway'];
      cellsDataKey.map(key => {
        cellsData[key] = [];
      });
      if (this.graph.instance) {
        const cells = this.graph.instance.getCells();
        for (let index = 0; index < cells.length; index++) {
          const cell = cells[index];
          const cellData = cell.data;
          const shape = cell.shape;
          if (shape === 'NodeTask' || shape === 'NodeSubflow' || shape === 'NodeRobot' || shape === 'NodeCollab') {
            cellsData[cellsDataKey[0]].push(cellData);
          } else if (shape === 'EdgeDirection') {
            cellsData[cellsDataKey[1]].push(cellData);
          } else if (shape === 'NodeCondition') {
            cellsData[cellsDataKey[2]].push(cellData);
          }
        }
      }
      this.cellsData = cellsData;
    },
    getSelectOptions() {
      const { property } = this.workFlowData;
      let selectOptions = [
        {
          type: flowProperty,
          label: property.name,
          value: property.id
        }
      ];
      if (!this.cellsData) {
        return selectOptions;
      }
      for (const key in this.cellsData) {
        for (const item of this.cellsData[key]) {
          selectOptions.push({
            type: key,
            label: item.name,
            value: item.id
          });
        }
      }

      this.selectOptions = selectOptions;
    },
    initRows() {
      const { node: nodes, edge: edges, gateway: gateways } = this.cellsData;
      const { property } = this.workFlowData;
      let rows = [];

      const editRow = row => {
        let obj = row,
          code = row.code;
        for (let opt of this.languageOptions) {
          const langCode = opt.value;
          if (!obj.i18n[langCode]) {
            obj.i18n[langCode] = {};
          }
          if (!obj.i18n[langCode][code]) {
            obj.i18n[langCode][code] = undefined;
          }
          row[langCode] = obj.i18n[langCode][code];
          row.loading[langCode] = false;
        }
      };

      // const propertyRows = this.getI18nRows(property, flowProperty);
      const propertyRows = collectI18nEntries(property, {
        type: flowProperty + '-' + property.id,
        fn: editRow
      });
      propertyRows.forEach(item => {
        rows.push(item);
      });
      this.propertyRows = propertyRows;

      /* 节点 */
      for (const item of nodes) {
        // const i18nRows = this.getI18nRows(item, 'node');
        const i18nRows = collectI18nEntries(item, {
          type: 'node' + '-' + item.id,
          fn: editRow
        });
        this.nodeRowsMap[item.id] = i18nRows;
        i18nRows.forEach(item => {
          rows.push(item);
        });
      }

      /* 边 */
      for (const item of edges) {
        // const i18nRows = this.getI18nRows(item, 'edge');
        const i18nRows = collectI18nEntries(item, {
          type: 'edge' + '-' + item.id,
          fn: editRow
        });
        this.edgeRowsMap[item.id] = i18nRows;
        i18nRows.forEach(item => {
          rows.push(item);
        });
      }

      /* 判断点 */
      for (const item of gateways) {
        // const i18nRows = this.getI18nRows(item, 'gateway');
        const i18nRows = collectI18nEntries(item, {
          type: 'gateway' + '-' + item.id,
          fn: editRow
        });
        this.gatewayRowsMap[item.id] = i18nRows;
        i18nRows.forEach(item => {
          rows.push(item);
        });
      }

      this.rows = rows;
    },
    getI18nRows(data, type, rootId = data.id) {
      let rows = [];
      const traverse = (obj, _key) => {
        if (typeof obj !== 'object' || obj === null) return;
        if (Array.isArray(obj)) {
          for (let item of obj) {
            traverse(item, _key);
          }
        } else {
          for (let key in obj) {
            if (key === 'i18n' && obj[key] !== undefined && obj[key]['zh_CN'] != undefined) {
              let i18n = obj[key];
              let code;
              for (const key in i18n['zh_CN']) {
                if ((type === flowProperty && propertyI18nCodes.includes(key)) || type !== flowProperty) {
                  code = key;
                  let name = i18n['zh_CN'][code];

                  let row = {
                    rootId,
                    type,
                    id: obj.id,
                    uuid: obj.uuid,
                    name,
                    code,
                    i18n: obj.i18n,
                    data: obj,
                    loading: {},
                    _key: `${type}.${rootId}` + '_' + `${_key ? _key + '.' + code : code}`
                  };

                  for (let opt of this.languageOptions) {
                    const langCode = opt.value;
                    if (!obj.i18n[langCode]) {
                      obj.i18n[langCode] = {};
                    }
                    if (!obj.i18n[langCode][code]) {
                      obj.i18n[langCode][code] = undefined;
                    }
                    row[langCode] = obj.i18n[langCode][code];
                    row.loading[langCode] = false;
                  }

                  rows.push(row);
                }
              }
            } else {
              traverse(obj[key], key);
            }
          }
        }
      };

      traverse(data);
      return rows;
    },
    fetchLocaleOptions() {
      return new Promise((resolve, reject) => {
        this.$tempStorage.getCache(
          'allLocaleOptions',
          () => {
            return new Promise((resolve, reject) => {
              this.$axios
                .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
                .then(({ data }) => {
                  let options = [];
                  if (data.code == 0) {
                    for (let d of data.data) {
                      options.push({
                        label: d.name,
                        value: d.locale,
                        description: d.remark || d.name,
                        transCode: d.translateCode
                      });
                    }
                  }
                  resolve(options);
                })
                .catch(error => {});
            });
          },
          results => {
            resolve(results);
          }
        );
      });
    },
    handleTableChange(pagination) {
      this.pagination.current = pagination.current;
    },
    getRowKey(item) {
      return item.key;
    },
    matchLangText(item) {
      for (let o of this.languageOptions) {
        if (item[o.value] && item[o.value].toLowerCase().indexOf(this.searchText.toLowerCase()) > -1) {
          return true;
        }
      }
      return false;
    },
    onChangeLanguageText(event, item, langKey) {
      const value = event.target.value;
      this.$set(item.i18n[langKey], item.code, value);
    },
    chagneTagAll(event) {
      if (!this.selectCheckAll) {
        this.selectCheckAll = true;
        this.selectedIds.splice(0, this.selectedIds.length);
        this.selectedTypes = [];
      } else {
        this.selectCheckAll = true;
      }
      this.pagination.current = 1;
    },
    changeTag(item) {
      let i = this.selectedIds.indexOf(item.value);
      if (i == -1) {
        this.selectedIds.push(item.value);
      } else {
        this.selectedIds.splice(i, 1);
      }
      if (this.selectedIds.length) {
        //   let selectedTypes = this.selectedTypes.concat(item.type);
        // const unique = selectedTypes.reduce((accumulator, currentValue) => {
        //   if (accumulator.indexOf(currentValue) === -1) {
        //     accumulator.push(currentValue);
        //   }
        //   return accumulator;
        // }, []);
        // this.selectedTypes = unique;
      } else {
        this.selectedTypes = [];
      }
      this.pagination.current = 1;
      this.selectCheckAll = this.selectedIds.length == 0;
    },
    resetSearchForm() {
      this.searchText = undefined;
      this.selectedIds.splice(0, this.selectedIds.length);
      this.selectedTypes = [];
      this.selectCheckAll = true;
    },
    handleRowName(item) {
      if (item.type === flowProperty) {
        this.$root.$refs.configurationPanel.tabKey = flowProperty;
      } else {
        this.$root.$refs.configurationPanel.tabKey = 'cellProperty';
        this.graph.instance.resetSelection(item.rootId);
      }
    },

    translateText: debounce(function (record, lang) {
      if (lang == 'zh_CN') {
        let promise = [];
        record.loading[lang] = true;
        for (let i = 1, len = this.languageOptions.length; i < len; i++) {
          let to = this.languageOptions[i].transCode || this.languageOptions[i].value.split('_')[0];
          record.loading[this.languageOptions[i].value] = true;
          promise.push(this.invokeTranslateApi(record['zh_CN'], 'zh', to));
        }
        Promise.all(promise).then(res => {
          console.log(res);
          record.loading[lang] = false;
          for (let i = 0, len = res.length; i < len; i++) {
            record[this.languageOptions[i + 1].value] = res[i];
            if (!record.i18n.hasOwnProperty(this.languageOptions[i + 1].value)) {
              this.$set(record.i18n, this.languageOptions[i + 1].value, { [record.code]: res[i] });
            } else {
              this.$set(record.i18n[this.languageOptions[i + 1].value], record.code, res[i]);
            }
            record.loading[this.languageOptions[i + 1].value] = false;
          }
        });
      } else {
        let langOpt = this.langMap[lang];
        record.loading[lang] = true;
        this.invokeTranslateApi(record['zh_CN'], 'zh', langOpt.transCode || langOpt.value.split('_')[0]).then(text => {
          record.loading[lang] = false;
          record[lang] = text;
          if (!record.i18n.hasOwnProperty(lang)) {
            this.$set(record.i18n, lang, { [record.code]: text });
          } else {
            this.$set(record.i18n[lang], record.code, text);
          }
        });
      }
    }, 300),
    invokeTranslateApi(word, from, to) {
      return new Promise((resolve, reject) => {
        this.$translate(word, from, to)
          .then(result => {
            this.translating = false;
            resolve(result);
          })
          .catch(error => {
            reject();
          });
      });
    },
    // 翻译全部
    onClickTranslateAll() {
      let _this = this;
      this.$confirm({
        title: '确定要翻译全部吗?',
        content: () => {
          const changeOnlyTranslateEmpty = event => {
            _this.onlyTranslateEmpty = event.target.checked;
          };
          return (
            <div>
              <a-checkbox checked={_this.onlyTranslateEmpty} onChange={changeOnlyTranslateEmpty}>
                仅翻译未进行翻译的文字内容
              </a-checkbox>
            </div>
          );
        },
        onOk() {
          console.log(_this.rows);
          let waitTranslate = {},
            toMap = {};
          for (let i = 1, len = _this.languageOptions.length; i < len; i++) {
            let to = _this.languageOptions[i].transCode || _this.languageOptions[i].value.split('_')[0];
            for (let record of _this.rows) {
              if (
                (_this.onlyTranslateEmpty &&
                  (record[_this.languageOptions[i].value] == undefined || record[_this.languageOptions[i].value] == '')) ||
                !_this.onlyTranslateEmpty
              ) {
                if (waitTranslate[to] == undefined) {
                  waitTranslate[to] = [];
                  toMap[to] = _this.languageOptions[i].value;
                }
                waitTranslate[to].push(record['zh_CN']);
              }
            }
          }

          console.log('待翻译内容', waitTranslate);
          let p = [];
          for (let to in waitTranslate) {
            _this.translateAllLoading = true;
            p.push(
              new Promise((resolve, reject) => {
                _this
                  .invokeTranslateApi(waitTranslate[to], 'zh', to)
                  .then(map => {
                    for (let record of _this.rows) {
                      if (
                        (_this.onlyTranslateEmpty && (record[toMap[to]] == undefined || record[toMap[to]] == '')) ||
                        !_this.onlyTranslateEmpty
                      ) {
                        if (map[record['zh_CN']]) {
                          _this.$set(record, toMap[to], map[record['zh_CN']]);
                          if (!record.i18n.hasOwnProperty(toMap[to])) {
                            _this.$set(record.i18n, toMap[to], { [record.code]: map[record['zh_CN']] });
                          } else {
                            _this.$set(record.i18n[toMap[to]], record.code, map[record['zh_CN']]);
                          }
                        }
                      }
                    }
                    resolve();
                  })
                  .catch(() => {
                    reject();
                  });
              })
            );
          }
          Promise.all(p)
            .then(() => {
              _this.translateAllLoading = false;
            })
            .catch(() => {
              _this.translateAllLoading = false;
            });
        },
        onCancel() {}
      });
    }
  }
};
</script>
