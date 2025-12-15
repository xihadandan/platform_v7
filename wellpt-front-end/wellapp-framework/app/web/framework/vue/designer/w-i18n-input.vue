<template>
  <span>
    <a-popover
      v-if="!htmlEditor && vCode != undefined"
      v-model="visible"
      :title="popoverTitle"
      trigger="click"
      :placement="placement"
      @visibleChange="onVisibleChange"
      :arrowPointAtCenter="true"
    >
      <template slot="content">
        <a-spin v-if="loading"></a-spin>
        <a-form :colon="false" style="width: 300px" layout="vertical" v-if="languageOptions.length > 0">
          <a-form-item
            :label="languageOptions[0].label"
            style="margin-bottom: 0px; display: block"
            :label-col="{ style: { width: '100%' } }"
            :wrapper-col="{ style: { width: '100%' } }"
            v-if="showZhCn"
          >
            <a-input
              class="translate-from-input"
              v-model="languageOptions[0].dataValue"
              :placeholder="languageOptions[0].description"
              :max-length="2000"
              @change="onChangeLanguageText"
            >
              <template slot="addonAfter" v-if="enableTranslate">
                <a-button size="small" type="link" @click="translateAll" :icon="translatingAll ? 'loading' : undefined">翻译</a-button>
              </template>
            </a-input>
          </a-form-item>
          <a-form-item
            style="margin-bottom: 0px; display: block"
            :label-col="{ style: { width: '100%' } }"
            :wrapper-col="{ style: { width: '100%' } }"
          >
            <template slot="label">
              {{ languageOptions[langIndex].description || languageOptions[langIndex].label }}
              <template v-if="displayLocaleCode">({{ languageOptions[langIndex].value }})</template>
              <a-button size="small" icon="retweet" type="link" @click.stop="nextLang" title="切换语言" />
            </template>
            <a-input
              v-model="languageOptions[langIndex].dataValue"
              :placeholder="languageOptions[langIndex].description"
              :max-length="2000"
              @change="onChangeLanguageText"
            >
              <template slot="suffix" v-if="enableTranslate">
                <a-icon :type="translating ? 'loading' : 'sync'" @click="translateText" title="翻译" />
              </template>
            </a-input>
          </a-form-item>
        </a-form>
      </template>
      <a-icon type="global" style="color: var(--w-primary-color); cursor: pointer" title="设置国际化内容" />
    </a-popover>

    <Modal v-else-if="htmlEditor && vCode != undefined" v-model="visible" width="800px" okText="确认" title="国际化内容">
      <template slot="content">
        <div style="height: 300px" v-if="languageOptions.length > 0">
          <template v-for="(opt, i) in languageOptions">
            <div v-if="i > 0" v-show="langIndex == i">
              <QuillEditor
                v-model="languageOptions[i].dataValue"
                :ref="'quillEditor' + opt.value"
                @change="quillValue => changeQuillEditor(quillValue, opt, i)"
              />
            </div>
          </template>
          <div style="text-align: right; padding-top: 5px">
            <a-space>
              <a-button size="small" type="link" icon="retweet" @click="nextLang" title="切换语言">
                {{ languageOptions[langIndex].description || languageOptions[langIndex].label }}
                <template v-if="displayLocaleCode">({{ languageOptions[langIndex].value }})</template>
              </a-button>
              <a-button
                v-if="enableTranslate"
                type="primary"
                size="small"
                @click="translateText"
                :icon="translating ? 'loading' : undefined"
              >
                翻译
              </a-button>
            </a-space>
          </div>
        </div>
      </template>

      <a-tooltip title="设置国际化内容">
        <a-button size="small" type="link" icon="global" @click="init" />
      </a-tooltip>
    </Modal>
    <Modal v-else v-model="visible" title="自定义国际化" width="800px" okText="确认" :ok="onConfirmI18nWords">
      <template slot="content">
        <div>
          <a-table
            :columns="columns"
            rowKey="uuid"
            :pagination="false"
            :data-source="rows"
            :scroll="{ y: 300 }"
            v-if="languageOptions.length > 0"
          >
            <template slot="codeSlot" slot-scope="text, record, index">
              <div style="display: flex; align-items: center">
                <Icon
                  type="iconfont icon-ptkj-shanchu"
                  :style="{ color: 'red', marginRight: '5px' }"
                  @click="rows.splice(index, 1)"
                  title="删除"
                />
                <a-input v-model="record.code" :max-length="600" />
              </div>
            </template>
            <template v-for="(lang, i) in languageOptions" :slot="lang.value + 'Slot'" slot-scope="text, record">
              <a-input v-model="record[lang.value]" :max-length="2000">
                <template slot="suffix" v-if="enableTranslate">
                  <a-icon
                    style="color: var(--w-primary-color)"
                    :type="record.loading[lang.value] ? 'loading' : lang.value == 'zh_CN' ? 'global' : 'sync'"
                    @click="rowTranslateText(record, lang)"
                    title="翻译"
                  />
                </template>
              </a-input>
            </template>
          </a-table>
          <a-button type="dashed" style="width: 100%; margin: 12px 0px" @click="addI18nWord">
            <Icon type="pticon iconfont icon-ptkj-jiahao" />
            添加国际化词汇编码
          </a-button>
        </div>
      </template>
      <a-tooltip title="设置国际化词汇">
        <a-button size="small" type="link" icon="global" @click="init" />
      </a-tooltip>
    </Modal>
  </span>
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce, get, set, each } from 'lodash';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { generateId } from '@framework/vue/utils/util';
import QuillEditor from '@pageAssembly/app/web/lib/quill-editor';

export default {
  name: 'WI18nInput',
  inject: ['pageContext', 'widgetI18ns'],
  props: {
    widget: Object,
    target: Object, //IMPORTANT !!! : i18n 数据绑定到某个选项上，类似按钮、下拉选项等，删除该选项时候，也能同步删除改选项上的i18n，避免产生无用的数据
    designer: Object,
    code: String,
    value: String,
    placement: {
      type: String,
      default: 'bottomRight'
    },
    remark: String,
    htmlEditor: {
      type: Boolean,
      default: false
    },
    showZhCn: {
      type: Boolean,
      default: true
    },
    popoverTitle: {
      type: String,
      default: '国际化'
    },
    enableTranslate: {
      type: Boolean,
      default: true
    },
    displayLocaleCode: {
      type: Boolean,
      default: true
    }
  },
  components: { Modal, QuillEditor },
  computed: {
    i18nBindToObject() {
      return this.target != undefined ? this.target : this.widget.configuration;
    },
    languageOptMap() {
      let map = {};
      for (let o of this.languageOptions) {
        map[o.value] = o;
      }
      return map;
    },
    vCode() {
      return this.widget && this.widget.id && this.code ? `${this.widget.id}.${this.code}` : this.code;
    }
  },
  data() {
    let columns = [
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
      languageOptions: [],
      langIndex: 1,
      translating: false,
      translatingAll: false,
      rows: [],
      columns,
      loading: true
    };
  },
  beforeCreate() {},
  created() {
    this.translateText = debounce(this.translateText.bind(this), 300);
    this.rowTranslateText = debounce(this.rowTranslateText.bind(this), 300);
  },
  beforeMount() {
    this.initI18nCodeObjectConfig();
    if (this.languageOptions.length == 0) {
      this.fetchLocaleOptions().then(options => {
        for (let opt of options) {
          opt.dataValue = opt.value == 'zh_CN' ? this.value : undefined;
          this.columns.push({
            title: `${opt.description || opt.name}${this.displayLocaleCode ? ` / ${opt.value}` : ''}`,
            dataIndex: opt.value,
            i18nColumn: true,
            scopedSlots: {
              customRender: opt.value + 'Slot'
            }
          });
        }
        this.languageOptions.push(...options);
      });
    }
  },
  mounted() {
    // if (this.i18nBindToObject.i18n != undefined && this.i18nBindToObject.i18n.zh_CN) {
    //   if (this.i18nBindToObject.i18n.zh_CN) {
    //     this.$emit('input', this.i18nBindToObject.i18n.zh_CN[this.code]);
    //   }
    // }
  },
  methods: {
    changeQuillEditor(quillValue, opt, index) {
      this.onChangeLanguageText();
    },
    initI18nCodeObjectConfig() {
      if (this.vCode != undefined && this.value != undefined && this.value != '') {
        // 针对指定属性进行的国际化
        if (!this.i18nBindToObject.hasOwnProperty('i18n')) {
          this.$set(this.i18nBindToObject, 'i18n', {
            zh_CN: {
              [this.vCode]: this.value
            }
          });
        }
      }
    },
    // 提取 HTML 中的中文并翻译
    translateHtml(html) {
      // 正则表达式匹配中文字符
      const chineseRegex = /[\u4e00-\u9fa5]+/g;
      const chineseMatches = html.match(chineseRegex) || [];

      // 去重并翻译
      const uniqueChinese = [...new Set(chineseMatches)];
      const translationMap = new Map();
      let promise = [],
        toOption = this.languageOptions[this.langIndex];
      for (const text of uniqueChinese) {
        let p = this.invokeTranslateApi(text, 'zh', toOption.transCode || toOption.value.split('_')[0]);
        promise.push(p);
        p.then(result => {
          translationMap.set(text, result);
        });
      }
      Promise.all(promise).then(values => {
        for (const [chinese, result] of translationMap.entries()) {
          html = html.replace(new RegExp(chinese, 'g'), result);
        }
        this.languageOptions[this.langIndex].dataValue = html;
        if (this.$refs['quillEditor' + toOption.value] != undefined) {
          this.$refs['quillEditor' + toOption.value][0].setHtml(html);
        }
        this.onChangeLanguageText();
      });
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
                        description: d.remark,
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
            this.loading = false;
            resolve(JSON.parse(JSON.stringify(results)));
          }
        );
      });
    },
    onConfirmI18nWords() {
      let i18ns = {};
      for (let lang of this.languageOptions) {
        i18ns[lang.value] = {};
      }
      for (let row of this.rows) {
        for (let key in i18ns) {
          i18ns[key][row.code] = row[key];
        }
      }
      this.$set(this.i18nBindToObject, 'i18n', i18ns);
      this.visible = false;
    },
    rowTranslateText(record, lang) {
      if (lang.value == 'zh_CN') {
        let promise = [];
        for (let i = 1, len = this.languageOptions.length; i < len; i++) {
          let to = this.languageOptions[i].transCode || this.languageOptions[i].value.split('_')[0];
          record.loading[this.languageOptions[i].value] = true;
          promise.push(this.invokeTranslateApi(record['zh_CN'], 'zh', to));
        }
        Promise.all(promise).then(res => {
          for (let i = 0, len = res.length; i < len; i++) {
            record[this.languageOptions[i + 1].value] = res[i];
            // if (
            //   this.widgetI18ns &&
            //   this.widgetI18ns[this.langOption[i + 1].value] &&
            //   this.widgetI18ns[this.langOption[i + 1].value][record.widget.id]
            // ) {
            //   this.widgetI18ns[this.langOption[i + 1].value][record.widget.id][record.code] = res[i];
            // }
            record.loading[this.languageOptions[i + 1].value] = false;
          }
        });
      } else {
        record.loading[lang.value] = true;
        this.invokeTranslateApi(record['zh_CN'], 'zh', lang.transCode || lang.value.split('_')[0]).then(text => {
          record.loading[lang.value] = false;
          record[lang.value] = text;
        });
      }
    },
    extractPlaceholdersAndSplit(text) {
      // 定义正则表达式匹配占位符
      const placeholderPattern = /\$\{([^\}]+)\}/g;

      // 保存匹配到的占位符
      const placeholders = [];
      let match;
      while ((match = placeholderPattern.exec(text)) !== null) {
        placeholders.push(match[0]); // 包含 ${}
      }

      let tempText = text;

      placeholders.forEach((placeholder, index) => {
        tempText = tempText.replace(placeholder, '{ ' + index + ' }');
      });

      return {
        placeholders,
        formateText: tempText
      };
    },
    invokeTranslateApi(word, from, to) {
      // 提取占位符
      let { formateText, placeholders } = this.extractPlaceholdersAndSplit(word);
      if (placeholders.length) {
        word = formateText;
      }

      return new Promise((resolve, reject) => {
        this.$translate(word, from, to).then(ans => {
          if (placeholders.length) {
            for (let i = 0, len = placeholders.length; i < len; i++) {
              ans = ans.replace('{' + i + '}', placeholders[i]);
            }
          }
          resolve(ans);
        });
      });
    },
    addI18nWord() {
      let row = {
        uuid: generateId(),
        code: generateId(5),
        loading: {}
      };
      for (let opt of this.languageOptions) {
        row[opt.value] = undefined;
        row.loading[opt.value] = false;
      }
      this.rows.push(row);
    },
    getPopupContainer(triggerNode) {
      return triggerNode.parentNode;
    },
    init() {
      if (this.vCode != undefined) {
        this.initI18nCodeObjectConfig();
        let i18n = this.i18nBindToObject.i18n;
        if (i18n) {
          each(this.languageOptions, lang => {
            if (this.widgetI18ns != undefined) {
              let v = get(this.widgetI18ns, `${lang.value}.Widget.${this.vCode}`, undefined);
              if (v) {
                i18n[lang.value][this.vCode] = v;
              }
            }
            this.languageOptMap[lang.value].dataValue = i18n[lang.value] ? i18n[lang.value][this.vCode] : undefined;
          });
        }
        if (this.languageOptMap.zh_CN.dataValue == undefined || this.value) {
          this.languageOptMap.zh_CN.dataValue = this.value;
        }
      } else {
        // 国际化词汇组
        let i18n = this.i18nBindToObject.i18n;
        this.rows.splice(0, this.rows.length);
        if (i18n) {
          let rowCodeMap = {};
          for (let lang in i18n) {
            for (let code in i18n[lang]) {
              if (rowCodeMap[code] == undefined) {
                rowCodeMap[code] = {
                  code,
                  uuid: generateId(),
                  loading: {}
                };
              }
              rowCodeMap[code][lang] = i18n[lang][code];
              rowCodeMap[code].loading[lang] = false;
              let v = get(this.widgetI18ns, `${lang}.Widget.${this.widget.id}.${code}`, undefined);
              if (v != undefined) {
                i18n[lang][code] = v;
                rowCodeMap[code][lang] = v;
              }
            }
          }
          for (let c in rowCodeMap) {
            this.rows.push(rowCodeMap[c]);
          }
        }
      }
    },
    onVisibleChange(visible) {
      if (visible) {
        this.init();
      }
    },
    translateText() {
      if (this.languageOptions[0].dataValue) {
        if (this.htmlEditor) {
          this.translateHtml(this.languageOptions[0].dataValue);
          return;
        }
        this.translating = true;
        let fromOption = this.languageOptions[0],
          toOption = this.languageOptions[this.langIndex];
        let from = fromOption.transCode || fromOption.value.split('_')[0];
        let to = toOption.transCode || toOption.value.split('_')[0];
        this.invokeTranslateApi(fromOption.dataValue, from, to).then(result => {
          toOption.dataValue = result;
          let v = get(this.widgetI18ns, `${toOption.value}.Widget.${this.vCode}`, undefined);
          if (v != undefined) {
            set(this.widgetI18ns, `${toOption.value}.Widget.${this.vCode}`, result);
          }

          this.translating = false;
          this.onChangeLanguageText();
        });
      }
    },

    translateAll: debounce(function () {
      if (!this.languageOptions[0].dataValue) {
        return;
      }
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
          let v = get(this.widgetI18ns, `${toOption.value}.Widget.${this.vCode}`, undefined);
          if (v != undefined) {
            set(this.widgetI18ns, `${toOption.value}.Widget.${this.vCode}`, toOption.dataValue);
          }
        }
        this.translatingAll = false;
        this.onChangeLanguageText();
        if (this.designer) {
          this.designer.emitEvent(`design:i18nChanged`, { widgetId: this.widget.id });
        }
      });
    }, 300),

    onChangeLanguageText() {
      for (let o of this.languageOptions) {
        if (!this.i18nBindToObject.i18n.hasOwnProperty(o.value)) {
          this.$set(this.i18nBindToObject.i18n, o.value, {});
        }
        this.i18nBindToObject.i18n[o.value][this.vCode] = o.dataValue || undefined;
        if (o.value == 'zh_CN') {
          this.$emit('input', o.dataValue);
        }
      }
      if (this.designer) {
        this.designer.emitEvent(`design:i18nChanged`, { widgetId: this.widget.id });
      }
      this.$emit('change');
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
        this.initI18nCodeObjectConfig();
        if (this.i18nBindToObject.i18n && this.languageOptions.length > 0) {
          this.languageOptMap.zh_CN.dataValue = v;
          if (this.i18nBindToObject.i18n.zh_CN) {
            this.i18nBindToObject.i18n.zh_CN[this.vCode] = v;
          }
        }
      }
    },
    code: {
      handler(v, o) {
        this.init();
      }
    }
    // 'i18nBindToObject.i18n': {
    //   deep: true,
    //   handler(v, o) {
    //     if (v && v.zh_CN) {
    //       this.$emit('input', v.zh_CN[this.vCode]);
    //     }
    //   }
    // }
  }
};
</script>
