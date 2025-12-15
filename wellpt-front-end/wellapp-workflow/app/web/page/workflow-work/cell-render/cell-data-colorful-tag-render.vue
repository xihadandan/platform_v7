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
        {{ $t('WorkflowView.colorfulTag_' + tag.key, tag.text) }}
      </a-tag>
    </template>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import cellRenderMixin from './cellRenderMixin';

export default {
  mixins: [cellRenderMixin],
  name: 'CellDataColorfulTagRender',
  title: '数据多彩标签渲染器',
  props: {},
  components: {},
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
        for (let i = 0, len = tags.length; i < len; i++) {
          if (texts.includes(tags[i].value)) {
            tags[i].tag.key = tags[i].key;
            t.push(tags[i].tag);
            if (texts.length == 1) {
              return t;
            }
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
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
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
