<template>
  <div :class="['no-form-item-widget', widgetClass]">
    <img :src="src" :style="style" v-if="src" />
    <div v-else :style="style">
      <svg
        width="100%"
        height="100%"
        viewBox="0 0 128 128"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
      >
        <desc>Created with Pixso.</desc>
        <defs />
        <path
          id="path"
          d="M113 89.7716C113 89.0225 112.68 88.4532 112.04 88.0636L90.484 74.9276C89.4938 74.3239 88.6311 74.4706 87.896 75.3676L78.396 86.9436C77.4547 87.8873 76.384 88.0327 75.44 87.0916L43.836 55.5836C42.9363 54.6881 42.0163 54.6668 41.076 55.5196L17.656 76.8516C17.2179 77.2477 16.9993 77.741 17 78.3316L17 111.448L113 111.448L113 89.7716Z"
          fill-rule="nonzero"
          fill="#bfbfbf"
        />
        <path
          id="path"
          d="M88 55C88 57.2091 88.7811 59.0948 90.3431 60.6569C91.9052 62.2189 93.7909 63 96 63C98.2091 63 100.095 62.2189 101.657 60.6569C103.219 59.0948 104 57.2091 104 55C104 52.7909 103.219 50.9052 101.657 49.3431C100.095 47.7811 98.2091 47 96 47C93.7909 47 91.9052 47.7811 90.3431 49.3431C88.7811 50.9052 88 52.7909 88 55Z"
          fill-rule="nonzero"
          fill="#bfbfbf"
        />
        <path
          id="path"
          d="M0 0L0 128L128 128L128 0L0 0ZM119.467 117.333L8.53333 117.333L8.53333 10.6667L119.467 10.6667L119.467 117.333Z"
          fill-rule="nonzero"
          fill="#bfbfbf"
        />
      </svg>
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
export default {
  name: 'WidgetImage',
  mixins: [widgetMixin],
  props: {},
  components: {},
  computed: {
    style() {
      let width = this.widget.configuration.style.width,
        height = this.widget.configuration.style.height;
      let vStyle = {
        width: typeof width == 'number' ? width + 'px' : width,
        height: typeof height == 'number' ? height + 'px' : height
      };

      if (this.widget.configuration.src == undefined) {
        vStyle.width = '150px';
        vStyle.height = '150px';
      } else {
        vStyle.objectFit = this.widget.configuration.objectFit;
      }


      return vStyle;
    },
    src() {
      return this.widget.configuration.src;
    },
    autoRefresh() {
      return false;
    }
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
