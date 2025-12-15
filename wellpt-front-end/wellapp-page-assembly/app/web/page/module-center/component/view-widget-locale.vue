<template>
  <div>
    <a-table :columns="columns" :data-source="rows" rowKey="code" :pagination="false">
      <template v-for="(option, i) in i18nOption" :slot="option.value + 'Slot'" slot-scope="text, record">
        <a-input
          v-model="record[option.value]"
          @change="onChangeLanguageText(record, option.value)"
          :readOnly="localeRows[option.value] && localeRows[option.value][record.code] != undefined"
        >
          <template
            slot="suffix"
            v-if="
              option.value == 'zh_CN' ||
              (option.value != 'zh_CN' && !(localeRows[option.value] && localeRows[option.value][record.code] != undefined))
            "
          >
            <a-icon
              style="color: var(--w-primary-color)"
              :type="record.loading[option.value] ? 'loading' : option.value == 'zh_CN' ? 'global' : 'sync'"
              @click="translateText(record, option)"
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
import { camelCase, debounce } from 'lodash';
export default {
  name: 'ViewWidgetLocale',
  props: {
    wtype: String
  },
  components: {},
  computed: {},
  data() {
    let columns = [
      {
        title: '编码',
        width: 150,
        dataIndex: 'code',
        ellipsis: false
      }
    ];

    return {
      i18nOption: [],
      columns,
      rows: [],
      localeRows: {},
      translating: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    let promise = [];
    this.fetchLocaleOptions().then(options => {
      this.i18nOption = options;
      for (let o of this.i18nOption) {
        promise.push(this.importLocale(o.value));
        this.columns.push({
          title: `${o.description} / ${o.value}`,
          dataIndex: o.value,
          i18nColumn: true,
          scopedSlots: {
            customRender: o.value + 'Slot'
          }
        });
      }
      let rowCodeMap = {};
      promise.push(this.fetchWidgetCodeI18ns());
      Promise.all(promise)
        .then(results => {
          let widgetCodeI18n = results[promise.length - 1];
          for (let opt of this.i18nOption) {
            if (this.localeRows[opt.value]) {
              for (let k in this.localeRows[opt.value]) {
                if (rowCodeMap[k] == undefined) {
                  rowCodeMap[k] = {
                    code: k,
                    loading: {},
                    applyTo: 'pt-app-widget'
                  };
                  for (let o of this.i18nOption) {
                    rowCodeMap[k][o.value] = undefined;
                    rowCodeMap[k].loading[o.value] = false;
                  }
                }
                rowCodeMap[k][opt.value] = this.localeRows[opt.value][k];
                rowCodeMap[k].loading[opt.value] = false;
              }
            }
          }

          for (let code in rowCodeMap) {
            if (widgetCodeI18n) {
              for (let opt of this.i18nOption) {
                let field = camelCase(opt.value);
                if (
                  (this.localeRows[opt.value] == undefined || this.localeRows[opt.value][code] == undefined) &&
                  widgetCodeI18n[code] &&
                  widgetCodeI18n[code][field]
                ) {
                  rowCodeMap[code][opt.value] = widgetCodeI18n[code][field];
                }
              }
            }

            this.rows.push(rowCodeMap[code]);
          }
        })
        .catch(err => {
          console.error(err);
        });
    });
  },
  mounted() {},
  methods: {
    fetchWidgetCodeI18ns() {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/app/codeI18n/getAppCodeI18nByApplyTo`, {
            params: {
              applyTo: 'pt-app-widget'
            }
          })
          .then(({ data }) => {
            let map = {};
            if (data.data) {
              for (let d of data.data) {
                if (d.code.startsWith(this.wtype + '.')) {
                  map[d.code] = d;
                }
              }
            }
            resolve(Object.keys(map).length > 0 ? map : undefined);
          })
          .catch(error => {});
      });
    },
    translateText: debounce(function (record, option) {
      if (option.value == 'zh_CN') {
        let promise = [];
        for (let i = 1, len = this.i18nOption.length; i < len; i++) {
          let to = this.i18nOption[i].transCode || this.i18nOption[i].value.split('_')[0];
          record.loading[this.i18nOption[i].value] = true;
          promise.push(this.invokeTranslateApi(record['zh_CN'], 'zh', to));
        }
        Promise.all(promise).then(res => {
          for (let i = 0, len = res.length; i < len; i++) {
            if (
              this.localeRows[this.i18nOption[i + 1].value] == undefined ||
              this.localeRows[this.i18nOption[i + 1].value][record.code] == undefined
            ) {
              record[this.i18nOption[i + 1].value] = res[i];
            }
            record.loading[this.i18nOption[i + 1].value] = false;
          }
        });
      } else {
        record.loading[option.value] = true;
        this.invokeTranslateApi(record['zh_CN'], 'zh', option.transCode || option.value.split('_')[0]).then(text => {
          record.loading[option.value] = false;
          record[option.value] = text;
        });
      }
    }, 300),
    invokeTranslateApi(word, from, to) {
      return new Promise((resolve, reject) => {
        this.$translate(word, from, to)
          .then(text => {
            this.translating = false;
            resolve(text.toLowerCase());
          })
          .catch(error => {});
      });
    },
    save() {
      return new Promise((resolve, reject) => {
        this.$loading();
        let rows = [];
        for (let r of this.rows) {
          for (let o of this.i18nOption) {
            rows.push({
              code: r.code,
              applyTo: r.applyTo,
              content: r[o.value],
              locale: o.value
            });
          }
        }
        this.$axios
          .post(`/proxy/api/app/codeI18n/save`, rows)
          .then(({ data }) => {
            this.$loading(false);
            this.$message.success('保存成功');
            resolve();
          })
          .catch(error => {
            this.$loading(false);
            this.$message.success('保存异常');
          });
      });
    },
    fetchLocaleOptions() {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
          .then(({ data }) => {
            if (data.code == 0) {
              let options = [];
              for (let d of data.data) {
                options.push({
                  label: d.name,
                  value: d.locale,
                  description: d.remark || d.name,
                  transCode: d.translateCode
                });
              }
              resolve(options);
            }
          })
          .catch(error => {});
      });
    },
    importLocale(lang) {
      return new Promise((resolve, reject) => {
        import(/* webpackChunkName: "locale-[request]" */ `@locale/${lang}.json`)
          .then(module => {
            console.log('加载到组件 ' + lang + ' 国际化信息包', module.default);
            this.$set(this.localeRows, lang, {});
            if (module.default[this.wtype]) {
              let keyValue = this.getNestedKeyValues(module.default[this.wtype]);
              for (let key in keyValue) {
                this.localeRows[lang][this.wtype + '.' + key] = keyValue[key];
              }
            }
            resolve();
          })
          .catch(() => {
            resolve();
          });
      });
    },
    getNestedKeyValues(obj) {
      let keyValue = {};
      function getNestedKeys(obj, parentKey = '', result) {
        for (let key in obj) {
          if (obj.hasOwnProperty(key)) {
            let newKey = parentKey === '' ? key : `${parentKey}.${key}`;
            if (typeof obj[key] === 'object' && obj[key] !== null && !Array.isArray(obj[key])) {
              getNestedKeys(obj[key], newKey, result);
            } else {
              result[newKey] = obj[key];
            }
          }
        }
        return result;
      }
      getNestedKeys(obj, '', keyValue);
      return keyValue;
    }
  }
};
</script>
