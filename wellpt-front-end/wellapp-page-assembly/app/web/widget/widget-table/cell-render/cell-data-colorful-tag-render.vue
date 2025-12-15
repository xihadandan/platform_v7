<template>
  <div v-if="tags != undefined">
    <template v-for="(tag, i) in tags">
      <a-tag
        :class="tagType ? 'pt-tag' : ''"
        :style="tagType ? getTagStyle(tag.color) : ''"
        :color="tagType ? '' : tag.color.startsWith('--') ? `var(${tag.color})` : tag.color"
        :key="'tag_' + rowIndex + '_' + i"
        style="margin-bottom: 3px"
      >
        {{ $t('colorfulTag_' + tag.key, tag.text) }}
      </a-tag>
    </template>
  </div>
</template>
<script type="text/babel">
import cellRenderMixin from './cellRenderMixin';
import { groupBy } from 'lodash';

export default {
  mixins: [cellRenderMixin],
  name: 'CellDataColorfulTagRender',
  title: '数据多彩标签渲染器',
  scope: ['pc', 'mobile'],
  computed: {
    tags() {
      if (this.text == undefined) {
        return undefined;
      }
      let { tags, split, splitSymbol } = this.slotOption.options;
      if (tags) {
        let texts = [this.text];
        if (split && splitSymbol) {
          texts = this.text.split(splitSymbol);
        }
        let t = [];
        let valueTags = groupBy(tags, tag => !!(tag.tag && tag.tag.textType));
        if (valueTags[false] && valueTags[false].length > 0) {
          // 根据值判断，显示文本
          for (let i = 0, len = valueTags[false].length; i < len; i++) {
            let falseTag = valueTags[false][i];
            if (texts.includes(falseTag.value)) {
              let index = texts.indexOf(falseTag.value);
              let tag = {
                key: falseTag.key,
                text: this.$t('colorfulTag_' + falseTag.key, falseTag.tag.text),
                color: falseTag.tag.color
              };
              t.push(tag);
              texts.splice(index, 1);
              if (texts.length == 0) {
                return t;
              }
            }
          }
        }
        if (valueTags[true] && valueTags[true].length > 0) {
          // 使用当前text值, 取第一个
          for (let j = 0, len = texts.length; j < len; j++) {
            let tag = {
              key: valueTags[true][0].key + '_' + j,
              text: texts[j],
              color: valueTags[true][0].tag.color
            };
            t.push(tag);
          }
        }
        return t;
      }
      return undefined;
    }
  },
  data() {
    let tagType = this.slotOption.options.tagType;
    return {
      tagType
    };
  },
  methods: {
    getTagStyle(color) {
      if (color) {
        let style = {};
        let light = '';
        let border = '';
        if (color.startsWith('--')) {
          // 使用主题色
          light = `var(${color}-1)`;
          border = `var(${color}-3)`;
          color = `var(${color})`;
        } else if (color.startsWith('#')) {
          // 使用hex
          light = `rgba(${this.hexToRgb(color)},0.05)`;
          border = `rgba(${this.hexToRgb(color)},0.2)`;
        } else {
          light = `var(--w-fill-color-light)`;
          border = `var(--w-border-color-dark)`;
        }

        if (this.tagType == 'border') {
          style['--w-pt-tag-text-color'] = color;
          style['--w-pt-tag-background'] = light;
          style['--w-pt-tag-border-color'] = color;
        } else if (this.tagType == 'bg-color') {
          style['--w-pt-tag-text-color'] = `#FFFFFF`;
          style['--w-pt-tag-background'] = color;
        } else {
          style['--w-pt-tag-text-color'] = color;
          style['--w-pt-tag-background'] = light;
        }
        return style;
      }
      return '';
    },
    hexToRgb(hex) {
      const r = parseInt(hex.slice(1, 3), 16);
      const g = parseInt(hex.slice(3, 5), 16);
      const b = parseInt(hex.slice(5, 7), 16);
      return `${r},${g},${b}`;
    }
  }
};
</script>
