<template>
  <div>
    <a-form-model-item :label="label">
      <a-select
        v-model="widget.configuration.dividerStyle[code]"
        :style="{ width: '100%' }"
        defaultValue="solid"
        @change="onChangeStyle"
        :getPopupContainer="getPopupContainerByPs()"
      >
        <a-select-option v-for="item in options" :key="item.code" :value="item.code" class="design-form-divider-style">
          <div style="width: 80px; text-align: left">{{ item.name }}</div>
          <div :class="'hr-style hr-' + item.code"></div>
        </a-select-option>
      </a-select>
    </a-form-model-item>
  </div>
</template>
<script type="text/babel">
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'DeviderTypeConfiguration',
  props: {
    widget: Object,
    code: {
      type: String,
      default: 'type'
    },
    label: {
      type: String,
      default: '分隔线类型'
    }
  },
  data() {
    return {
      options: [
        {
          code: 'solid',
          name: '实线',
          style: 'border-top:{width} solid {color};'
        },
        {
          code: 'dashed-wide',
          name: '虚线(宽)',
          style:
            'height: {width};background: repeating-linear-gradient(to right, {color} 0, {color} 20px, transparent 0, transparent 25px);'
        },
        {
          code: 'dashed-narrow',
          name: '虚线(窄)',
          style:
            'height: {width};background: repeating-linear-gradient(to right, {color} 0, {color} 12px, transparent 0, transparent 15px);'
        },
        {
          code: 'dotted',
          name: '点线',
          style: 'border-top:{width} dotted {color};'
        },
        {
          code: 'dotted-dashed',
          name: '点虚线',
          style:
            'height: {width};background: repeating-linear-gradient(to right, {color} 0px, {color} 20px, transparent 0, transparent 25px, {color} 0px, {color} 27px, transparent 0, transparent 32px);'
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
    onChangeStyle(val, opt) {
      this.widget.configuration.dividerStyle[this.code + '_style'] = this.options.find(function (item) {
        return item.code == opt.key;
      }).style;
    }
  },
  mounted() {},
  updated() {}
};
</script>
