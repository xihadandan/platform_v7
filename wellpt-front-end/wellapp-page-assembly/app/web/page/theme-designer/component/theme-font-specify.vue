<template>
  <div class="theme-specify-panel">
    <a-page-header title="字体">
      <div slot="subTitle">
        一般默认使用系统字体（微软雅黑/苹方），主字号14px，可以保证大约77%的显示器用户处于比较好的阅读体验。字阶是基于14px的字体，
        以自然常数的生长律，同时用5声音阶的单元切分而得，行高是字体以及行高都符合偶数原则，行高=字号+8。
      </div>
      <a-descriptions title="字族" layout="vertical" :colon="false" :column="4">
        <a-descriptions-item label="变量">{{ config.fontFamily.code }}</a-descriptions-item>
        <a-descriptions-item label="可用字体">
          <a-select v-model="config.fontFamily.scope" :options="fontFamilyOptions" mode="multiple" style="width: 100%" />
        </a-descriptions-item>
        <a-descriptions-item label="默认字体">
          <a-select v-model="config.fontFamily.value" :options="defaultFontFamilyOptions" style="width: 100%" />
        </a-descriptions-item>
        <a-descriptions-item label="描述">
          <a-input v-model="config.fontFamily.remark" />
        </a-descriptions-item>
        <a-descriptions-item :label="null"></a-descriptions-item>
        <a-descriptions-item label="默认字体预览" :span="3">
          <div class="font-preview-panel">
            <div>{{ config.fontFamily.value }}</div>
            <div>
              <div :style="{ [config.fontFamily.code]: config.fontFamily.value, fontFamily: 'var(' + config.fontFamily.code + ')' }">
                <a-row style="margin-bottom: 15px">
                  <a-col :span="8"><h1 style="margin: 0px">一级标题</h1></a-col>
                  <a-col :span="8"><h1 style="margin: 0px">123</h1></a-col>
                  <a-col :span="8"><h1 style="margin: 0px">Title</h1></a-col>
                </a-row>
                <a-row style="color: rgba(0, 0, 0, 0.45); font-size: small; margin-bottom: 15px">
                  <a-col :span="8"><span>注释文字</span></a-col>
                  <a-col :span="8"><span>123</span></a-col>
                  <a-col :span="8"><span>note</span></a-col>
                </a-row>
                <a-row>
                  <a-col :span="8"><div>正文</div></a-col>
                  <a-col :span="8"><div>13456789</div></a-col>
                  <a-col :span="8"><div>main body</div></a-col>
                </a-row>
              </div>
            </div>
          </div>
        </a-descriptions-item>

        <a-descriptions-item label="变量">{{ config.codeFamily.code }}</a-descriptions-item>
        <a-descriptions-item label="可用字体">
          <a-select v-model="config.codeFamily.scope" :options="fontFamilyOptions" mode="multiple" style="width: 100%" />
        </a-descriptions-item>
        <a-descriptions-item label="默认字体">
          <a-select v-model="config.codeFamily.value" :options="defaultCodeFamilyOptions" style="width: 100%" />
        </a-descriptions-item>
        <a-descriptions-item label="描述">
          <a-input v-model="config.codeFamily.remark" />
        </a-descriptions-item>
        <a-descriptions-item :label="null"></a-descriptions-item>
        <a-descriptions-item label="默认字体预览" :span="3">
          <div class="font-preview-panel">
            <div>{{ config.codeFamily.value }}</div>
            <div>
              <pre :style="{ [config.codeFamily.code]: config.codeFamily.value, fontFamily: 'var(' + config.codeFamily.code + ')' }">
// scripts/genAntdCss.tsx
import { extractStyle } from '@ant-design/cssinjs';
import type Entity from '@ant-design/cssinjs/lib/Cache';
import { createHash } from 'crypto';
import fs from 'fs';
import path from 'path';
              </pre>
            </div>
          </div>
        </a-descriptions-item>
      </a-descriptions>
      <div>
        <a-descriptions title="字号和字阶" :colon="false" :column="1">
          <a-descriptions-item :label="null">
            <a-descriptions :colon="false" :column="4" v-for="key in ['fontSize', 'fontWeight']" :key="key">
              <template slot="title">
                {{ key === 'fontSize' ? '字号' : '字重' }}
                <a-button
                  size="small"
                  type="link"
                  icon="plus"
                  @click="
                    addDerives(
                      key === 'fontSize' ? config.fontSize.classify[0].derive : config.fontWeight.derive,
                      key === 'fontSize' ? config.fontSize.classify[0].code : config.fontWeight.code
                    )
                  "
                />
              </template>
              <template v-if="key === 'fontSize'">
                <a-descriptions-item :span="4">
                  <a-descriptions :title="null" layout="vertical" :colon="false" :column="4">
                    <a-descriptions-item label="变量">{{ config.fontSize.classify[0].code }}</a-descriptions-item>
                    <a-descriptions-item label="值/公式">
                      <a-input-group compact>
                        <a-input-number :min="12" :value="fontSizeValue(config.fontSize.classify[0].value)" @change="onChangeFontSize" />
                        <a-button>px</a-button>
                      </a-input-group>
                    </a-descriptions-item>
                    <a-descriptions-item label="描述">基本字号</a-descriptions-item>
                    <a-descriptions-item label="预览">
                      <div
                        :style="{
                          [config.fontSize.classify[0].code]: config.fontSize.classify[0].value,
                          fontSize: 'var(' + config.fontSize.classify[0].code + ')'
                        }"
                      >
                        <span>字体大小</span>
                        <span>
                          {{ config.fontSize.classify[0].value }}
                        </span>
                      </div>
                    </a-descriptions-item>
                  </a-descriptions>
                </a-descriptions-item>
              </template>

              <template v-for="(item, i) in key === 'fontSize' ? config.fontSize.classify[0].derive : config.fontWeight.derive">
                <a-descriptions-item :label="null" :key="i">
                  <a-input
                    :value="codeSuffix(item.code, key === 'fontSize' ? config.fontSize.classify[0].code : config.fontWeight.code)"
                    @change="e => mergeCodeSuffix(e, item, key === 'fontSize' ? config.fontSize.classify[0].code : config.fontWeight.code)"
                  >
                    <template slot="addonBefore">
                      {{ (key === 'fontSize' ? config.fontSize.classify[0].code : config.fontWeight.code) + '-' }}
                    </template>
                  </a-input>
                </a-descriptions-item>
                <a-descriptions-item :label="null" :key="i">
                  <a-input v-model="item.value" />
                </a-descriptions-item>
                <a-descriptions-item :label="null" :key="i">
                  <a-input-group>
                    <a-row :gutter="8">
                      <a-col :span="20">
                        <a-input v-model="item.remark" />
                      </a-col>
                      <a-col :span="4">
                        <a-button
                          size="small"
                          type="link"
                          icon="delete"
                          @click="deleteDeriveColors(key === 'fontSize' ? config.fontSize.classify[0] : config.fontWeight, i)"
                        />
                      </a-col>
                    </a-row>
                  </a-input-group>
                </a-descriptions-item>
                <a-descriptions-item :label="null" :key="i">
                  <div v-show="item.value" :style="setFontVar(item, key)" :font-var-code="item.code" :font-var-type="key">
                    <span>{{ key === 'fontSize' ? '字体大小' : '字重预览' }}</span>
                    <span>
                      {{ fontVarComputed[item.code] }}
                    </span>
                  </div>
                </a-descriptions-item>
              </template>
            </a-descriptions>
          </a-descriptions-item>
        </a-descriptions>

        <a-descriptions title="行高" layout="vertical" :colon="false" :column="3">
          <a-descriptions-item label="变量">{{ config.lineHeight.code }}</a-descriptions-item>
          <a-descriptions-item label="值/公式">
            <a-input v-model="config.lineHeight.value" />
          </a-descriptions-item>
          <a-descriptions-item label="描述">{{ config.lineHeight.remark }}</a-descriptions-item>
          <a-descriptions-item :title="null"></a-descriptions-item>
          <a-descriptions-item label="预览" :span="2">
            <div :style="{ [config.lineHeight.code]: config.lineHeight.value }">
              <p :style="{ 'line-height': 'var(' + config.lineHeight.code + ')' }">
                三十功名尘与土，八千里路云和月。
                <br />
                莫等闲、白了少年头，空悲切。
              </p>
            </div>
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce, kebabCase } from 'lodash';

export default {
  name: 'ThemeFontSpecify',
  props: {
    config: Object
  },
  components: {},
  computed: {
    defaultCodeFamilyOptions() {
      let options = [];
      for (let i = 0, len = this.config.codeFamily.scope.length; i < len; i++) {
        options.push({
          label: this.config.codeFamily.scope[i],
          value: this.config.codeFamily.scope[i]
        });
      }
      return options;
    },
    defaultFontFamilyOptions() {
      let options = [];
      for (let i = 0, len = this.config.fontFamily.scope.length; i < len; i++) {
        options.push({
          label: this.config.fontFamily.scope[i],
          value: this.config.fontFamily.scope[i]
        });
      }
      return options;
    }
  },

  data() {
    let fontFamily = [
      'Microsoft YaHei',
      '-apple-system',
      'BlinkMacSystemFont',
      'Segoe UI',
      'PingFang SC',
      'Hiragino Sans GB',
      'Helvetica Neue',
      'Helvetica',
      'Arial',
      'sans-serif',
      'Apple Color Emoji',
      'Segoe UI Emoji',
      'Segoe UI Symbol',
      '宋体',
      '新宋体',
      '楷体',
      '仿宋',
      '黑体'
    ];
    let fontFamilyOptions = [];
    for (let i = 0, len = fontFamily.length; i < len; i++) {
      fontFamilyOptions.push({ label: fontFamily[i], value: fontFamily[i] });
    }
    return { fontFamilyOptions, fontVarComputed: {} };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.computeFontVarValue('fontSize', 'fontWeight');
  },
  methods: {
    mergeCodeSuffix(e, item, prefix) {
      item.code = prefix + '-' + e.target.value;
    },
    codeSuffix(code, prefix) {
      return code.split(prefix + '-')[1];
    },
    fontSizeValue(v) {
      return parseInt(v);
    },
    onChangeFontSize(e) {
      this.config.fontSize.classify[0].value = e + 'px';
    },
    computeFontVarValue: debounce(function () {
      for (let type of arguments) {
        let elements = document.querySelectorAll('[font-var-code][font-var-type=' + type + ']');
        for (let i = 0, len = elements.length; i < len; i++) {
          let code = elements[i].getAttribute('font-var-code');
          this.$set(this.fontVarComputed, code, getComputedStyle(elements[i])[type]);
        }
      }
    }, 300),

    setFontVar(item, styleProp) {
      return {
        [this.config.fontSize.classify[0].code]: this.config.fontSize.classify[0].value,
        [item.code]: item.value,
        [kebabCase(styleProp)]: 'var(' + item.code + ')'
      };
    },
    getCssComputedColorPropValue(key) {
      return EASY_ENV_IS_BROWSER ? getComputedStyle(this.$el).getPropertyValue(key) : undefined;
    },

    deleteDeriveColors(tar, index) {
      if (index == undefined) {
        tar.derive = [];
      } else {
        tar.derive.splice(index, 1);
      }
    },
    addDerives(derive, prefix) {
      derive.push({
        code: prefix + '-' + (derive.length + 1),
        value: undefined,
        remark: undefined
      });
    }
  },
  watch: {
    'config.fontSize': {
      deep: true,
      handler() {
        this.computeFontVarValue('fontSize');
      }
    },
    'config.fontWeight': {
      deep: true,
      handler() {
        this.computeFontVarValue('fontWeight');
      }
    }
  }
};
</script>
