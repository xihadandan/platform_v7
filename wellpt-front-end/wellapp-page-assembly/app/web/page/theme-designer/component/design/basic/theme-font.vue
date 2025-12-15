<template>
  <div class="theme-style-panel">
    <a-page-header title="字体">
      <div slot="subTitle">
        一般默认使用系统字体（微软雅黑/苹方），主字号14px，可以保证大约77%的显示器用户处于比较好的阅读体验。字阶——基于14px的字体，
        以自然常数的生长律，同时用5声音阶的单元切分而得；行高——字体以及行高都符合偶数原则，行高=字号+8。
      </div>

      <a-card title="字族">
        <a-descriptions :colon="false" :bordered="false" layout="vertical" :column="1">
          <a-descriptions-item label="显示常规文本">
            <div
              :class="['font-preview-panel full-line', selectKey == 'fontFamily' ? 'border-selected' : '']"
              @click="onSelect(config.fontFamily, 'fontFamily')"
            >
              <div>
                font-family
                <a-select v-model="config.fontFamily.value" size="small">
                  <a-select-option v-for="(sc, i) in config.fontFamily.scope" :key="'fontFamilyS_' + i" :value="sc">
                    {{ sc }}
                  </a-select-option>
                </a-select>
              </div>
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

          <a-descriptions-item label="显示代码">
            <div
              :class="['font-preview-panel full-line', selectKey == 'codeFamily' ? 'border-selected' : '']"
              @click="onSelect(config.codeFamily, 'codeFamily')"
            >
              <div>
                code-family
                <a-select v-model="config.codeFamily.value" size="small">
                  <a-select-option v-for="(sc, i) in config.codeFamily.scope" :key="'fontFamilySC_' + i" :value="sc">
                    {{ sc }}
                  </a-select-option>
                </a-select>
              </div>
              <div>
                <pre
                  :style="{
                    padding: '20px 0px 0px 40px',
                    [config.codeFamily.code]: config.codeFamily.value,
                    fontFamily: 'var(' + config.codeFamily.code + ')'
                  }"
                >
// scripts/genAntdCss.tsx
import { extractStyle } from '@ant-design/cssinjs';
import type Entity from '@ant-design/cssinjs/lib/Cache';
import { createHash } from 'crypto';
import fs from 'fs';
import path from 'path';
              </pre
                >
              </div>
            </div>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>

      <a-card>
        <template slot="title">
          字号和字阶
          <Modal title="添加字阶" :ok="addNewFontSizeOk" :cancel="cancelAddNewFontSize">
            <a-button size="small" type="link" icon="plus" />
            <template slot="content">
              <a-form-model :label-col="labelCol" :wrapper-col="wrapperCol">
                <a-form-model-item label="名称">
                  <a-input v-model="newFontSize.title" />
                </a-form-model-item>
                <a-form-model-item label="描述">
                  <a-textarea v-model="newFontSize.remark" />
                </a-form-model-item>
              </a-form-model>
            </template>
          </Modal>
        </template>
        <a-card
          size="small"
          v-for="(fontClassify, c) in config.fontSize.classify"
          :key="'fontSizeClassify_' + c"
          @click="onSelect(fontClassify, 'fontSize')"
        >
          <template slot="title">
            {{ fontClassify.title }}
            <code>{{ fontClassify.value }}</code>
          </template>
          <a-row :class="['style-preview-row']" type="flex" v-for="(font, i) in fontClassify.derive" :key="'fontSizePreview' + i">
            <a-col flex="200px">
              <div>
                <div>{{ font.code }}</div>
              </div>
            </a-col>
            <a-col flex="200px">
              {{ fontVarComputed['fontSize_' + i + font.code] }}
            </a-col>
            <a-col flex="200px" :style="{ [fontClassify.code]: fontClassify.value }">
              <div
                :style="{ [font.code]: font.value, fontSize: 'var(' + font.code + ')' }"
                :font-var-code="font.code"
                font-var-type="fontSize"
                :prefix="'fontSize_' + i"
              >
                字体大小
              </div>
            </a-col>
          </a-row>
        </a-card>
      </a-card>

      <a-card title="字重" @click.native.stop="onSelect(config.fontWeight, 'fontWeight')">
        <a-card size="small">
          <a-row :class="['style-preview-row']" type="flex" v-for="(weight, i) in config.fontWeight.derive" :key="'fontWeightPreview_' + i">
            <a-col flex="200px">
              {{ weight.code }}
            </a-col>
            <a-col flex="200px">
              {{ weight.value }}
            </a-col>
            <a-col flex="200px">
              <div :style="{ [weight.code]: weight.value, fontWeight: 'var(' + weight.code + ')' }">字重预览</div>
            </a-col>
          </a-row>
        </a-card>
      </a-card>

      <a-card title="行高" @click.native.stop="onSelect(config.lineHeight, 'lineHeight')">
        <a-card size="small">
          <template slot="title">
            {{ config.lineHeight.code }}
            <code>{{ config.lineHeight.value }}</code>
          </template>
          <a-row :class="['style-preview-row']" type="flex">
            <a-col flex="500px">
              <div :style="{ [config.lineHeight.code]: config.lineHeight.value, [config.fontSize.code]: config.fontSize.value }">
                <p :style="{ 'line-height': 'var(' + config.lineHeight.code + ')' }">
                  三十功名尘与土，八千里路云和月。
                  <br />
                  莫等闲、白了少年头，空悲切。
                </p>
              </div>
            </a-col>
          </a-row>
        </a-card>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { debounce } from 'lodash';
export default {
  name: 'ThemeFont',
  props: { config: Object },
  components: { Modal },
  computed: {},
  data() {
    return {
      selectKey: undefined,
      fontVarComputed: {},
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      newFontSize: {
        title: undefined,
        value: undefined,
        remark: undefined,
        derive: []
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.computeFontVarValue();
    console.log(this.config);
  },
  methods: {
    addNewFontSizeOk(e) {
      if (this.newFontSize.title) {
        e(true);

        this.newFontSize.code = this.config.fontSize.classify[0].code;
        this.newFontSize.value = this.config.fontSize.classify[0].value;
        this.newFontSize.derive = JSON.parse(JSON.stringify(this.config.fontSize.classify[0].derive));
        this.config.fontSize.classify.push(JSON.parse(JSON.stringify(this.newFontSize)));
        this.cancelAddNewFontSize();
      }
    },
    cancelAddNewFontSize() {
      this.newFontSize = { title: undefined, code: undefined, value: undefined, remark: undefined, derive: [] };
    },
    computeFontVarValue: debounce(function (rule, value, callback) {
      let elements = document.querySelectorAll('[font-var-code]');
      for (let i = 0, len = elements.length; i < len; i++) {
        let code = elements[i].getAttribute('font-var-code'),
          type = elements[i].getAttribute('font-var-type'),
          prefix = elements[i].getAttribute('prefix') || '';
        this.$set(this.fontVarComputed, prefix + code, getComputedStyle(elements[i])[type]);
      }
    }, 500),

    onSelect(e, key) {
      this.$emit('select', e, key);
      this.selectKey = key;
    }
  },

  watch: {
    'config.fontSize': {
      deep: true,
      handler() {
        this.computeFontVarValue();
      }
    }
  }
};
</script>
