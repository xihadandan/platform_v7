<template>
  <div>
    <a-form-model-item label="字体">
      <a-select v-model="widget.configuration[type].fontFamily" :style="{ width: '100%' }" :getPopupContainer="getPopupContainerByPs()">
        <a-select-option v-for="item in fontFamily" :key="item.code" :value="item.value">
          <div :style="'font-family:' + item.value">{{ item.name }}</div>
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="字体大小">
      <a-input-number v-model="widget.configuration[type].fontSize" :min="10" :defaultValue="28" />
      <span style="padding-left: 10px">px</span>
    </a-form-model-item>
    <a-form-model-item label="字体样式" class="item-lh">
      <a-checkbox-group v-model="widget.configuration[type].fontStyle" :options="fontStyle" />
    </a-form-model-item>
    <ColorSelectConfiguration
      label="字体颜色"
      v-model="widget.configuration[type]"
      onlyValue
      colorField="color"
      radioSize="small"
    ></ColorSelectConfiguration>
    <ColorSelectConfiguration
      label="字体背景颜色"
      v-model="widget.configuration[type]"
      onlyValue
      colorField="bgcolor"
      radioSize="small"
    ></ColorSelectConfiguration>
    <a-form-model-item label="对齐方式" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-radio-group size="small" v-model="widget.configuration[type].align" button-style="solid">
        <a-radio-button value="left" title="左对齐"><a-icon type="align-left" /></a-radio-button>
        <a-radio-button value="center" title="居中对齐"><a-icon type="align-center" /></a-radio-button>
        <a-radio-button value="right" title="右对齐"><a-icon type="align-right" /></a-radio-button>
      </a-radio-group>
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
import DividerColorConfiguration from './divider-color-configuration.vue';
export default {
  name: 'TitleStyleConfiguration',
  props: {
    widget: Object,
    type: {
      type: String,
      default: 'style'
    }
  },
  data() {
    return {
      fontFamily: [
        { name: '宋体', value: '宋体', code: 'songti' },
        { name: '黑体', value: '黑体', code: 'heiti' },
        { name: '仿宋_GB2312', value: 'fangsong', code: 'fangsong' },
        { name: '楷体', value: '楷体', code: 'kaiti' },
        { name: '隶书', value: '隶书', code: 'lishu' },
        { name: '幼圆', value: '幼圆', code: 'youyuan' },
        { name: '微软雅黑', value: 'Microsoft YaHei', code: 'weiruanyahei' }
      ],
      fontStyle: [
        { label: '加粗', value: 'bold' },
        { label: '斜体', value: 'italic' },
        { label: '下划线', value: 'underline' }
      ]
    };
  },

  beforeCreate() {},
  components: { DividerColorConfiguration },
  computed: {},
  created() {},
  methods: {
    getPopupContainerByPs,
    onChange(val, opt) {}
  },
  mounted() {},
  updated() {}
};
</script>
