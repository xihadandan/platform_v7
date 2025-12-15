<template>
  <svg :class="['svg-icon', symbolId]" :style="style" v-bind="$attrs">
    <use :href="iconPath" />
  </svg>
</template>

<script>
import spritePath from '@modules/svg-sprite-icon.svg';
export default {
  name: 'SvgIcon',
  props: {
    symbolId: {
      type: String,
      required: true
    },
    size: {
      type: [String, Number],
      default: 16
    }
  },
  computed: {
    iconPath() {
      return `${spritePath}#${this.symbolId}`;
    },
    style() {
      const size = typeof this.size === 'number' ? `${this.size}px` : this.size;
      return {
        width: size,
        height: size,
        fill: 'currentColor'
      };
    }
  },
  mounted() {},
  methods: {
    getAllSymbols() {
      return new Promise((resolve, reject) => {
        $axios.get(spritePath).then(res => {
          import('@xmldom/xmldom').then(xmldom => {
            const { DOMParser } = xmldom;
            const parser = new DOMParser();
            const doc = parser.parseFromString(res.data, 'image/svg+xml');
            const symbols = doc.getElementsByTagName('symbol');
            const items = [];
            Array.from(symbols).forEach((symbol, index) => {
              const id = symbol.getAttribute('id');
              items.push({
                id
              });
            });
            resolve(items);
          });
        });
      });
    }
  }
};
</script>

<style scoped>
.svg-icon {
  vertical-align: middle;
}
</style>
