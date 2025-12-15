<template>
  <a-tag
    v-if="text"
    :class="type ? 'pt-tag' : ''"
    :style="type ? getTagStyle(color) : ''"
    :color="type ? '' : color && color.startsWith('--') ? `var(${color})` : color"
  >
    {{ text }}
  </a-tag>
</template>
<script type="text/babel">
export default {
  mixins: [],
  name: 'ColorfulTag',
  title: '多彩标签组件',
  props: {
    text: String,
    color: String,
    type: {
      type: String,
      default: 'default',
      validator: function validator(val) {
        return !val || ['border', 'bg-color', 'default'].includes(val);
      }
    }
  },
  data() {
    return {};
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

        if (this.type == 'border') {
          style['--w-pt-tag-text-color'] = color;
          style['--w-pt-tag-background'] = light;
          style['--w-pt-tag-border-color'] = color;
        } else if (this.type == 'bg-color') {
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
