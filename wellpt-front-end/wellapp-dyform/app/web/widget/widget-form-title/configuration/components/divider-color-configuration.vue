<template>
  <div>
    <a-form-model-item :label="label" :wrapper-col="{ style: { textAlign: 'left' } }">
      <a-select
        :value="value"
        @change="onChange"
        :dropdownMatchSelectWidth="false"
        class="design-form-colors-select"
        dropdownClassName="design-form-dropdown-style"
        :getPopupContainer="getPopupContainerByPs()"
      >
        <a-select-option v-for="item in colorArr" :key="item" :value="item" class="design-form-colors-style">
          <div class="color-item" :style="'background-color:' + item"></div>
        </a-select-option>
      </a-select>
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'DeviderColorConfiguration',
  props: {
    value: {
      type: String,
      default: ''
    },
    label: {
      type: String,
      default: '分隔线颜色'
    }
  },
  data() {
    return {
      colorArr: [
        '#C00000',
        '#FF0000',
        '#FFC000',
        '#FFFF00',
        '#92D050',
        '#00B050',
        '#00B0F0',
        '#0070C0',
        '#002060',
        '#7030A0',
        '#FFFFFF',
        '#000000'
      ]
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    getPopupContainerByPs,
    onChange(val, opt) {
      this.$emit('input', val);
    }
  },
  mounted() {},
  updated() {}
};
</script>
