<template>
  <div>
    <div style="margin-bottom: 8px">
      <!-- <a-alert banner type="info" style="position: absolute; top: 10px; left: 50%; transform: translateX(-50%)">
        <div slot="message">
          设计内容发生变更, 请刷新获取最新国际化配置数据
          <a-button type="link" size="small">刷新</a-button>
        </div>
      </a-alert> -->
      <div style="display: flex; align-items: flex-start">
        <!-- <a-checkbox :indeterminate="selectWgtIndeterminate" :checked="selectWgtCheckAll" @change="onCheckAllSelectWgtChange">
          选择组件
        </a-checkbox> -->
        <a-checkable-tag
          :style="{
            margin: '0px 0px 5px 0px',
            backgroundColor: selectWgtCheckAll ? 'var(--w-primary-color)' : 'var(--w-primary-color-1)',
            color: selectWgtCheckAll ? '#fff' : 'var(--w-primary-color)'
          }"
          :checked="selectWgtCheckAll"
          @change="onCheckAllSelectWgtChange"
        >
          <a-icon
            :type="
              selectWgtCheckAll || (selectedWgtIds.length > 0 && selectedWgtIds.length == selectWgtOptions.length)
                ? 'check-square'
                : 'minus-square'
            "
            theme="filled"
          />
          全部
        </a-checkable-tag>
        <a-divider type="vertical" style="height: 26px" />
        <div style="display: flex; width: calc(100% - 300px); flex-wrap: wrap">
          <template v-for="tag in selectWgtOptions">
            <a-checkable-tag
              style="margin-bottom: 5px"
              :key="tag.value"
              :checked="selectedWgtIds.indexOf(tag.value) > -1"
              @change="checked => handleWgtSelectChange(tag, checked)"
            >
              {{ tag.widget.title || tag.widget.name }}
            </a-checkable-tag>
          </template>
        </div>
        <div style="display: flex; align-items: center">
          <a-button v-show="rows.length > 0" @click="onClickTranslateAll" :icon="translateAllLoading ? 'loading' : 'global'">
            翻译全部
          </a-button>
          <a-divider type="vertical" />
          <a-input-search v-model="searchText" allow-clear placeholder="编码 / 国际化内容" />
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
      <template slot="widgetSlot" slot-scope="text, record">
        <a-button size="small" type="link" @click.stop="selectWidget(record.widget.id)">
          {{ record.widget.title || record.widget.name }}
        </a-button>
      </template>

      <template v-for="(langKey, i) in Object.keys(langMap)" :slot="langKey + 'Slot'" slot-scope="text, record">
        <a-input
          v-if="langKey == 'zh_CN'"
          v-model="record.i18n.zh_CN[record.code]"
          @change="onChangeLanguageText(record, langKey)"
          :readOnly="true"
        >
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
        <a-input v-else v-model="record[langKey]" @change="onChangeLanguageText(record, langKey)">
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
<style lang="less"></style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { debounce, get, set } from 'lodash';
export default {
  name: 'WidgetI18nView',
  inject: ['widgetI18ns'],
  props: {
    designer: Object
  },
  components: { Drawer, Modal },
  computed: {
    rowsFiltered() {
      let rows = [];
      for (let r of this.rows) {
        if (
          (this.selectedWgtIds.length == 0 || this.selectedWgtIds.includes(r.widget.id)) &&
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
  data() {
    let columns = [
      {
        title: '组件',
        width: 150,
        dataIndex: 'widget',
        scopedSlots: {
          customRender: 'widgetSlot'
        },
        ellipsis: true
      }
      // {
      //   title: '编码',
      //   width: 150,
      //   dataIndex: 'code',
      //   ellipsis: true
      // }
    ];

    return {
      loading: true,
      languageOptions: [],
      pagination: { current: 1, pageSize: 8, hideOnSinglePage: true },
      rows: [],
      columns,
      selectWgtOptions: [],
      selectedWgtIds: [],
      selectWgtIndeterminate: false,
      selectWgtCheckAll: true,
      searchText: undefined,
      onlyTranslateEmpty: true,
      translateAllLoading: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchLocaleOptions().then(options => {
      this.languageOptions.push(...options);
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
  mounted() {
    // this.designer.offEvent('design:i18nChanged').handleEvent(`design:i18nChanged`, data => {
    //   let { widgetId, code } = data;
    //   for (let i = 0, len = this.rows.length; i < len; i++) {
    //     if (this.rows[i].widget.id == widgetId && this.rows[i].code == code) {
    //       let codeI18n = this.rows[i].widget.configuration.i18n[code];
    //       for (let lang in codeI18n) {
    //         this.rows[i][lang] = codeI18n[lang];
    //       }
    //       break;
    //     }
    //   }
    // });
  },
  methods: {
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
    getAndUpdateDesignerWidgetI18ns(widgets, wI18ns) {
      let i18ns = {},
        i18nList = [];
      function findI18nObjects(json, widget) {
        function traverse(obj, belongToWidget) {
          if (typeof obj !== 'object' || obj === null) return;
          if (obj.id && obj.wtype && obj.configuration) {
            belongToWidget = obj;
          }
          if (Array.isArray(obj)) {
            for (let item of obj) {
              traverse(item, belongToWidget);
            }
          } else {
            for (let key in obj) {
              if (key === 'i18n' && obj[key].zh_CN != undefined) {
                let i18n = obj[key];
                for (let lang in i18n) {
                  if (wI18ns && wI18ns[lang] && wI18ns[lang]) {
                    for (let k in i18n[lang]) {
                      let v = get(wI18ns, `${lang}.Widget.${k}`, undefined);
                      if (v != undefined) {
                        i18n[lang][k] = v;
                      }
                    }
                  }
                }
                i18nList.push({
                  i18n,
                  widget: belongToWidget
                });

                for (let langCode in i18n) {
                  if (i18ns[langCode] == undefined) {
                    i18ns[langCode] = { Widget: {} };
                  }
                  if (i18ns[langCode].Widget[belongToWidget.id] == undefined) {
                    i18ns[langCode].Widget[belongToWidget.id] = {};
                  }
                  for (let c in i18n[langCode]) {
                    i18ns[langCode].Widget[belongToWidget.id][c] = i18n[langCode][c];
                  }
                }
              } else {
                traverse(obj[key], belongToWidget);
              }
            }
          }
        }
        traverse(json, widget);
      }
      for (let wgt of widgets) {
        findI18nObjects(wgt, wgt);
      }
      console.log('获取到 i18ns ', i18ns, i18nList);
      return i18nList;
    },
    initRows() {
      this.selectWgtOptions.splice(0, this.selectWgtOptions.length);
      this.selectedWgtIds.splice(0, this.selectedWgtIds.length);
      this.rows.splice(0, this.rows.length);
      let wgtIds = [];
      let i18ns = this.getAndUpdateDesignerWidgetI18ns(this.designer.widgets, this.widgetI18ns);
      if (i18ns.length > 0) {
        for (let i = 0, len = i18ns.length; i < len; i++) {
          let { i18n, widget } = i18ns[i];
          if (!wgtIds.includes(widget.id)) {
            wgtIds.push(widget.id);
            this.selectWgtOptions.push({
              value: widget.id,
              label: widget.title,
              widget
            });
          }
          let rowCodeMap = {};
          for (let lang in i18n) {
            for (let code in i18n[lang]) {
              if (rowCodeMap[code] == undefined) {
                rowCodeMap[code] = {
                  widget: widget,
                  code,
                  i18n,
                  loading: {}
                };
              }

              for (let opt of this.languageOptions) {
                rowCodeMap[code][opt.value] = i18n[opt.value] != undefined ? i18n[opt.value][code] : undefined;
                rowCodeMap[code].loading[opt.value] = false;
              }
            }
          }
          for (let key in rowCodeMap) {
            this.rows.push(rowCodeMap[key]);
          }
        }
      }
    },
    matchLangText(item) {
      for (let o of this.languageOptions) {
        if (item[o.value] && item[o.value].toLowerCase().indexOf(this.searchText.toLowerCase()) > -1) {
          return true;
        }
      }
      return false;
    },
    resetSearchForm() {
      this.searchText = undefined;
      this.selectedWgtIds.splice(0, this.selectedWgtIds.length);
      this.selectWgtCheckAll = true;
      // this.initRows();
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
            set(this.widgetI18ns, `${this.languageOptions[i + 1].value}.Widget.${record.code}`, res[i - 1]);
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
          set(this.widgetI18ns, `${this.languageOptions[i + 1].value}.Widget.${record.code}`, text);
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
    handleTableChange(pagination) {
      this.pagination.current = pagination.current;
    },
    onClickTranslateAll() {
      let _this = this;
      this.$confirm({
        title: '确定要翻译全部吗?',
        content: h => (
          <div>
            <a-checkbox checked={_this.onlyTranslateEmpty} onChange={e => (_this.onlyTranslateEmpty = e.target.checked)}>
              仅翻译未进行翻译的文字内容
            </a-checkbox>
          </div>
        ),
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
                          set(_this.widgetI18ns, `${toMap[to]}.Widget.${record.code}`, map[record['zh_CN']]);
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
    },

    onChangeLanguageText(item, langKey) {
      this.$set(item.i18n[langKey], item.code, item[langKey]);
      set(this.widgetI18ns, `${langKey}.Widget.${item.code}`, item[langKey]);
    },
    getRowKey(item) {
      return item.widget.id + item.code;
    },
    selectWidget(id) {
      this.designer.selectedByID(id);
      // document.querySelector('#design-main').style.zIndex = 1000;
    },
    onCheckAllSelectWgtChange(e) {
      if (!this.selectWgtCheckAll) {
        this.selectWgtCheckAll = true;
        this.selectedWgtIds.splice(0, this.selectedWgtIds.length);
      } else {
        this.selectWgtCheckAll = true;
      }
    },
    handleWgtSelectChange(item) {
      let i = this.selectedWgtIds.indexOf(item.value);
      if (i == -1) {
        this.selectedWgtIds.push(item.value);
      } else {
        this.selectedWgtIds.splice(i, 1);
      }
      this.selectWgtCheckAll = this.selectedWgtIds.length == 0;
    }
  }
};
</script>
