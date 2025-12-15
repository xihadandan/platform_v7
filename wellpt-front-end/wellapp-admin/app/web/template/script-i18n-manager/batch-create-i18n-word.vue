<template>
  <div>
    <a-table v-if="!loading" :columns="columns" rowKey="id" :pagination="false" :data-source="rows" :scroll="{ y: 300 }">
      <template slot="codeSlot" slot-scope="text, record, index">
        <div style="display: flex; align-items: center">
          <a-icon type="minus-circle" theme="filled" :style="{ color: 'red', marginRight: '5px' }" @click="rows.splice(index, 1)" />
          <a-input v-model="record.code" allow-clear />
        </div>
      </template>
      <template slot="applyToSlot" slot-scope="text, record, index">
        <a-input v-model="record.applyTo" allow-clear />
      </template>
      <template slot="remarkSlot" slot-scope="text, record, index">
        <a-input v-model="record.remark" allow-clear />
      </template>

      <template v-for="(langKey, i) in lang" :slot="langKey + 'Slot'" slot-scope="text, record">
        <a-input v-model="record[langKey]">
          <template slot="suffix">
            <a-icon
              style="color: var(--w-primary-color)"
              :type="record.loading[langKey] ? 'loading' : langKey == 'zh_CN' ? 'global' : 'sync'"
              @click="rowTranslateText(record, langKey)"
              title="翻译"
            />
          </template>
        </a-input>
      </template>
    </a-table>
    <a-button type="dashed" style="width: 100%; margin: 12px 0px" @click="addI18nWord">
      <a-icon type="plus" />
      添加国际化词汇编码
    </a-button>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce, camelCase } from 'lodash';
import { generateId } from '@framework/vue/utils/util';

export default {
  name: 'BatchCreateI18nWord',
  inject: ['currentWindow', 'pageContext'],
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      languageOptions: [],
      columns: [],
      rows: [],
      loading: true,
      lang: [],
      moduleOptions: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchI18nOptions().then(() => {
      this.columns.splice(0, this.columns.length);
      this.lang.splice(0, this.lang.length);
      this.columns.push(
        ...[
          {
            title: '编码',
            width: 150,
            dataIndex: 'code',
            scopedSlots: {
              customRender: 'codeSlot'
            }
          },
          {
            title: '应用于',
            width: 150,
            dataIndex: 'applyTo',
            scopedSlots: {
              customRender: 'applyToSlot'
            }
          }
          // {
          //   title: '备注',
          //   width: 150,
          //   dataIndex: 'remark',
          //   scopedSlots: {
          //     customRender: 'remarkSlot'
          //   }
          // }
        ]
      );
      for (let opt of this.languageOptions) {
        this.lang.push(opt.value);
        this.columns.push({
          title: `${opt.description}`,
          dataIndex: opt.value,
          i18nColumn: true,
          scopedSlots: {
            customRender: opt.value + 'Slot'
          }
        });
      }
      this.loading = false;
    });
  },
  mounted() {},
  methods: {
    addI18nWord() {
      let row = {
        id: generateId(),
        code: generateId(8),
        module: undefined,
        loading: {}
      };
      for (let opt of this.languageOptions) {
        row[opt.value] = undefined;
        row.loading[opt.value] = false;
      }
      this.rows.push(row);
    },
    saveRows() {
      this.$loading('保存中');
      let promise = [];
      for (let r of this.rows) {
        this.$set(r, 'error', undefined);
        promise.push(this.validateRow(r));
      }
      Promise.all(promise).then(results => {
        let rows = [];
        for (let r of results) {
          if (!r.valid) {
            this.$loading(false);
            return;
          }
        }
        for (let r of this.rows) {
          for (let langKey of this.lang) {
            rows.push({
              code: r.code,
              applyTo: r.applyTo,
              content: r[langKey],
              locale: langKey
            });
          }
        }
        this.$axios
          .post(`/proxy/api/app/codeI18n/save`, rows)
          .then(({ data }) => {
            this.$loading(false);
            this.currentWindow.close();
            this.pageContext.emitEvent('csgmMfYjSikYEzqXUPUkenJtUASgunLL:refetch');
          })
          .catch(error => {
            this.$loading(false);
          });
      });
    },
    validateRow(row) {
      return new Promise((resolve, reject) => {
        if (row.code == undefined && row.module == undefined) {
          resolve({
            row,
            valid: false,
            msg: '编码、应用于不能为空'
          });
          this.$set(row, 'error', '编码、应用于不能为空');
          return;
        }
        this.$axios
          .get(`/proxy/api/app/codeI18n/getAppCodeI18nByCodeAndApplyTo`, {
            params: {
              code: row.code,
              applyTo: row.applyTo
            }
          })
          .then(({ data }) => {
            let valid = data.data == null || data.data.length == 0;
            resolve({
              row,
              valid,
              msg: !valid ? '该编码应用于已存在' : undefined
            });
            this.$set(row, 'error', '编码与应用于必须要唯一');
            return;
          })
          .catch(error => {});
      });
    },

    rowTranslateText: debounce(function (record, lang) {
      if (lang == 'zh_CN') {
        let promise = [];
        for (let i = 1, len = this.languageOptions.length; i < len; i++) {
          let to = this.languageOptions[i].transCode || this.languageOptions[i].value.split('_')[0];
          record.loading[this.languageOptions[i].value] = true;
          promise.push(this.invokeTranslateApi(record['zh_CN'], 'zh', to));
        }
        Promise.all(promise).then(res => {
          console.log(res);
          for (let i = 0, len = res.length; i < len; i++) {
            record[this.languageOptions[i + 1].value] = res[i];
            record.loading[this.languageOptions[i + 1].value] = false;
          }
        });
      } else {
        let langOpt = this.langMap[lang];
        record.loading[lang] = true;
        this.invokeTranslateApi(record['zh_CN'], 'zh', langOpt.transCode || langOpt.value.split('_')[0]).then(text => {
          record.loading[lang] = false;
          record[lang] = text;
        });
      }
    }, 300),
    invokeTranslateApi(word, from, to) {
      return new Promise((resolve, reject) => {
        this.$translate(word, from, to)
          .then(text => {
            resolve(text.toLowerCase());
          })
          .catch(error => {});
      });
    },
    fetchI18nOptions() {
      return new Promise((resolve, reject) => {
        this.languageOptions.splice(0, this.languageOptions.length);
        this.$axios
          .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
          .then(({ data }) => {
            if (data.code == 0) {
              for (let d of data.data) {
                this.languageOptions.push({
                  label: d.name,
                  value: d.locale,
                  description: d.remark || d.name,
                  transCode: d.translateCode
                });
              }
            }
            resolve();
          })
          .catch(error => {});
      });
    }
  },
  META: {
    method: {
      saveRows: '保存'
    }
  }
};
</script>
