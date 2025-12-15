<template>
  <div>
    <a-form-model-item :label="label">
      <a-select
        v-model="widget.configuration.dividerStyle[code]"
        :style="{ width: '100%' }"
        @change="onChange"
        :getPopupContainer="getPopupContainerByPs()"
      >
        <a-select-option v-for="item in options" :key="item.code" :value="item.code" class="design-form-divider-style">
          <div style="width: 80px; text-align: left">{{ item.name }}</div>
          <div class="hr-style hr-solid" :style="'border-top-width: ' + item.code"></div>
        </a-select-option>
      </a-select>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'DeviderWidthConfiguration',
  props: {
    widget: Object,
    code: {
      type: String,
      default: 'width'
    },
    label: {
      type: String,
      default: '分隔线线宽'
    }
  },
  data() {
    return {
      options: [
        {
          code: '1px',
          name: '细'
        },
        {
          code: '2px',
          name: '较细'
        },
        {
          code: '3px',
          name: '中等'
        },
        {
          code: '4px',
          name: '微粗'
        },
        {
          code: '5px',
          name: '粗'
        }
      ]
    };
  },

  beforeCreate() {},
  components: {},
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
