<template>
  <span>
    <a-popover
      v-model="visible"
      title="国际化"
      trigger="click"
      :placement="placement"
      @visibleChange="onVisibleChange"
      :arrowPointAtCenter="true"
    >
      <template slot="content">
        <a-form :colon="false" style="width: 300px" layout="vertical" v-if="languageOptions.length != 0">
          <a-form-item
            :label="languageOptions[0].label"
            style="margin-bottom: 0px; display: block"
            :label-col="{ style: { width: '100%' } }"
            :wrapper-col="{ style: { width: '100%' } }"
          >
            <a-input v-model="languageOptions[0].dataValue" :placeholder="languageOptions[0].description" @change="onChangeLanguageText">
              <template slot="suffix">
                <a-icon :type="translatingAll ? 'loading' : 'global'" @click="translateAll" />
              </template>
            </a-input>
          </a-form-item>
          <a-form-item
            style="margin-bottom: 0px; display: block"
            :label-col="{ style: { width: '100%' } }"
            :wrapper-col="{ style: { width: '100%' } }"
          >
            <template slot="label">
              {{ languageOptions[langIndex].label }} ({{ languageOptions[langIndex].value }})
              <a-button size="small" icon="retweet" type="link" @click.stop="nextLang" />
            </template>
            <a-input v-model="languageOptions[langIndex].dataValue" :placeholder="languageOptions[langIndex].description">
              <template slot="suffix">
                <a-icon :type="translating ? 'loading' : 'sync'" @click="translateText" />
              </template>
            </a-input>
          </a-form-item>
          <div style="text-align: right; padding: 5px; display: flex; justify-content: space-between; align-items: center">
            <a-alert
              banner
              :message="saveResultStatus == 'success' ? '保存成功' : '保存失败'"
              :type="saveResultStatus"
              show-icon
              v-if="saveResultStatus != undefined"
            />
            <span v-else></span>
            <a-button size="small" type="primary" :loading="saving" icon="save" @click="onSaveAppCodeI18n">保存</a-button>
          </div>
        </a-form>
      </template>
      <a-icon type="global" />
    </a-popover>
  </span>
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce, camelCase } from 'lodash';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'WAppCodeI18nInput',
  inject: ['pageContext'],
  props: {
    applyTo: {
      type: String,
      required: true
    },
    code: String,
    value: String,
    placement: {
      type: String,
      default: 'topRight'
    },
    remark: String
  },
  components: { Modal },
  computed: {},
  data() {
    let languageOptions = [],
      columns = [
        {
          title: '编码',
          width: 150,
          dataIndex: 'code',
          scopedSlots: {
            customRender: 'codeSlot'
          }
        }
      ];

    return {
      visible: false,
      languageOptions,
      langIndex: 1,
      translating: false,
      translatingAll: false,
      saving: false,
      saveIcon: 'save',
      saveResultStatus: undefined,
      rows: [],
      columns
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onSaveAppCodeI18n() {
      return new Promise((resolve, reject) => {
        this.saving = true;
        for (let r of this.rows) {
          for (let o of this.i18nOption) {
            r[camelCase(o.value)] = r[o.value];
          }
        }
        let formData = { code: this.code, applyTo: this.applyTo, remark: this.remark };
        for (let o of this.languageOptions) {
          formData[camelCase(o.value)] = o.dataValue;
        }

        this.$axios
          .post(`/proxy/api/app/codeI18n/save`, [formData])
          .then(({ data }) => {
            this.saving = false;
            this.saveResultStatus = data.code == 0 ? 'success' : 'error';
            setTimeout(() => {
              this.saveResultStatus = undefined;
            }, 3000);
            resolve();
          })
          .catch(error => {
            this.saving = false;
          });
      });
    },
    fetchLocaleOptions() {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
          .then(({ data }) => {
            if (data.code == 0) {
              for (let d of data.data) {
                this.languageOptions.push({
                  label: d.name,
                  value: d.locale,
                  description: d.remark || d.name,
                  transCode: d.translateCode,
                  dataValue: d.locale == 'zh_CN' ? this.value : undefined
                });
              }
            }
            resolve();
          })
          .catch(error => {});
      });
    },

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

    getPopupContainer(triggerNode) {
      return triggerNode.parentNode;
    },
    init() {
      this.fetchLocaleOptions().then(() => {
        for (let opt of this.languageOptions) {
          this.columns.push({
            title: `${opt.description} / ${opt.value}`,
            dataIndex: opt.value,
            i18nColumn: true,
            scopedSlots: {
              customRender: opt.value + 'Slot'
            }
          });
        }
      });
    },
    onVisibleChange(visible) {
      if (visible) {
        this.init();
      }
    },
    translateText: debounce(function () {
      if (this.languageOptions[0].dataValue) {
        this.translating = true;
        let fromOption = this.languageOptions[0],
          toOption = this.languageOptions[this.langIndex];
        let from = fromOption.transCode || fromOption.value.split('_')[0];
        let to = toOption.transCode || toOption.value.split('_')[0];
        this.invokeTranslateApi(fromOption.dataValue, from, to).then(result => {
          toOption.dataValue = result;
          this.translating = false;
          this.onChangeLanguageText();
        });
      }
    }, 300),

    translateAll: debounce(function () {
      let promise = [];
      this.translatingAll = true;
      for (let i = 1; i < this.languageOptions.length; i++) {
        let toOption = this.languageOptions[i];
        let fromOption = this.languageOptions[0];
        let from = fromOption.transCode || fromOption.value.split('_')[0];
        let to = toOption.transCode || toOption.value.split('_')[0];
        promise.push(this.invokeTranslateApi(fromOption.dataValue, from, to));
      }
      Promise.all(promise).then(result => {
        for (let i = 1; i < this.languageOptions.length; i++) {
          let toOption = this.languageOptions[i];
          toOption.dataValue = result[i - 1];
        }
        this.translatingAll = false;
      });
    }, 300),

    onChangeLanguageText() {
      if (this.languageOptions.length > 0) {
        this.$emit('input', this.languageOptions[0].dataValue);
      }
    },
    nextLang() {
      this.langIndex++;
      if (this.langIndex == this.languageOptions.length) {
        this.langIndex = 1;
      }
    }
  },
  watch: {
    value: {
      handler(v, o) {
        if (this.languageOptions.length > 0) {
          this.languageOptions[0].dataValue = v;
        }
      }
    }
  }
};
</script>
