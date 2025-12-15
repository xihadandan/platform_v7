<template>
  <div>
    <a-form-model-item label="标签风格" v-if="options[field.tagType]">
      <a-radio-group v-model="options[field.tagType]" @change="onChangeTagType" button-style="solid" size="small">
        <a-radio-button v-for="(item, i) in tagStyle" :value="item.type" :key="'tag' + i">
          {{ item.name }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="标签文本">
      <a-input v-model="options[field.text]" allow-clear placeholder="文本">
        <template slot="addonAfter" v-if="isI18n">
          <WI18nInput :target="value" :code="'colorfulTag_' + options[field.key]" v-model="options[field.text]" :widget="value" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="标签定义">
      <ColorPicker
        v-model="options[field.color]"
        :allowClear="true"
        v-if="options[field.colorType]"
        style="width: 150px"
        class="colorPickerInput"
      >
        <a-tag slot="prefix" :class="{ 'pt-tag': true }" :style="getTagStyle(options[field.color])">
          {{ options[field.text] || '标签' }}
        </a-tag>
      </ColorPicker>
      <a-select v-model="options[field.color]" style="width: 150px" placeholder="选择颜色" v-else>
        <a-select-option v-for="(item, i) in tagSelectOptions" :value="item.value" :key="'tag' + i">
          <a-tag :class="{ 'pt-tag': true }" :style="getTagStyle(item.value)">
            {{ options[field.text] || '标签' }}
          </a-tag>
          {{ item.label }}
        </a-select-option>
      </a-select>
      <a-tooltip placement="right">
        <template slot="title">切换颜色选择</template>
        <a-checkbox v-model="options[field.colorType]"></a-checkbox>
      </a-tooltip>
    </a-form-model-item>
  </div>
</template>
<style lang="less">
.colorPickerInput {
  .ant-input {
    padding-left: 56px !important;
  }
}
</style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'ColorfulTagConfiguration',
  props: {
    value: {
      type: Object,
      default: () => {
        return {
          key: generateId(),
          colorType: false,
          text: '',
          tagType: 'default',
          color: undefined
        };
      }
    },
    replace: {
      type: Object,
      default: () => {}
    },
    isI18n: {
      type: Boolean,
      default: true
    }
  },
  components: {},
  computed: {},
  data() {
    let field = Object.assign({ key: 'key', colorType: 'colorType', text: 'text', tagType: 'tagType', color: 'color' }, this.replace);
    let options = this.value || {
      [this.field.key]: generateId(),
      [this.field.colorType]: false,
      [this.field.text]: '',
      [this.field.tagType]: 'default',
      [this.field.color]: undefined
    };
    let tagOptions = [
      {
        label: '主题色',
        value: '--w-primary-color'
      },
      {
        label: '主题成功色',
        value: '--w-success-color'
      },
      {
        label: '主题错误色',
        value: '--w-danger-color'
      },
      {
        label: '主题警告色',
        value: '--w-warning-color'
      },
      {
        label: '主题提示色',
        value: '--w-info-color'
      },
      {
        label: '主题链接色',
        value: '--w-link-color'
      },
      {
        label: '红色',
        value: '#FF0000'
      },
      {
        label: '玫红色',
        value: '#DC416B'
      },
      {
        label: '粉色',
        value: '#FFC0CB'
      },
      {
        label: '橙色',
        value: '#FFA500'
      },
      {
        label: '青色',
        value: '#00FFFF'
      },
      {
        label: '绿色',
        value: '#008000'
      },
      {
        label: '蓝色',
        value: '#0000FF'
      },
      {
        label: '紫色',
        value: '#6732F3'
      },
      {
        label: '灰色',
        value: '#666666'
      }
    ];
    return {
      options,
      field,
      tagStyle: [
        {
          type: 'default',
          name: '默认'
        },
        {
          type: 'border',
          name: '边框'
        },
        {
          type: 'bg-color',
          name: '背景色'
        }
      ],
      tagOptions,
      tagSelectOptions: deepClone(tagOptions)
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

        if (this.options[this.field.tagType] == 'border') {
          style['--w-pt-tag-text-color'] = color;
          style['--w-pt-tag-background'] = light;
          style['--w-pt-tag-border-color'] = border;
        } else if (this.options[this.field.tagType] == 'bg-color') {
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
    },
    onChangeTagType() {
      this.tagSelectOptions.splice(0, this.tagSelectOptions.length);
      this.$nextTick(() => {
        this.tagSelectOptions = deepClone(this.tagOptions);
      });
    }
  },
  watch: {
    options: {
      deep: true,
      handler(val) {
        this.$emit('input', val);
      }
    }
  }
};
</script>
