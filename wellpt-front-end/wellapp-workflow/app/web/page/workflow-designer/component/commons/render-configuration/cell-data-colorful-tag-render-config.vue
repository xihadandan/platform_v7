<template>
  <div>
    <a-form-model-item label="标签风格" v-if="options.tagType">
      <a-radio-group v-model="options.tagType" @change="onChangeTagType" button-style="solid">
        <a-radio-button v-for="(item, i) in tagStyle" :value="item.type" :key="'tag' + i">
          {{ item.name }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="标签定义">
      <a-table
        :columns="columns"
        :showHeader="false"
        :data-source="options.tags"
        :pagination="false"
        size="small"
        :locale="{
          emptyText: '暂无数据'
        }"
      >
        <template slot="valueSlot" slot-scope="text, record">
          <a-input v-model="record.value" allow-clear placeholder="值" />
        </template>
        <template slot="tagSlot" slot-scope="text, record, index">
          <a-input v-model="record.tag.text" allow-clear style="width: 130px; margin-right: 8px" placeholder="文本">
            <template slot="addonAfter">
              <WI18nInput :target="record" :code="'colorfulTag_' + record.key" v-model="record.tag.text" />
            </template>
          </a-input>
          <a-select v-model="record.tag.color" style="width: 130px" placeholder="选择颜色">
            <a-select-option v-for="(item, i) in tagSelectOptions" :value="item.value" :key="'tag' + i">
              <a-tag :class="{ 'pt-tag': true }" :style="getTagStyle(item.value)">
                {{ item.label }}
              </a-tag>
            </a-select-option>
          </a-select>
        </template>
        <template slot="operationSlot" slot-scope="text, record, index">
          <a-button size="small" type="link" @click="options.tags.splice(index, 1)">
            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          </a-button>
        </template>
        <template slot="footer">
          <a-button size="small" type="link" block @click="onAddTag">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            新增
          </a-button>
        </template>
      </a-table>
    </a-form-model-item>
    <a-form-model-item>
      <template slot="label">
        值分割符号
        <a-checkbox v-model="options.split" />
      </template>
      <a-input v-if="options.split" style="width: 100px" v-model="options.splitSymbol" allow-clear />
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'CellDataColorfulTagRenderConfig',
  props: { options: Object },
  components: { WI18nInput },
  computed: {},
  data() {
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
      columns: [
        {
          title: '值',
          dataIndex: 'value',
          width: 140,
          scopedSlots: { customRender: 'valueSlot' }
        },
        {
          title: '标签',
          dataIndex: 'tag',
          scopedSlots: { customRender: 'tagSlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          width: 70,
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
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
  created() {
    if (this.options.tags == undefined) {
      this.$set(this.options, 'tags', []);
    }
    if (!this.options.tagType) {
      this.$set(this.options, 'tagType', 'default');
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onAddTag() {
      this.options.tags.push({
        key: generateId(),
        value: undefined,
        tag: {
          color: undefined,
          text: undefined
        }
      });
    },
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

        if (this.options.tagType == 'border') {
          style['--w-pt-tag-text-color'] = color;
          style['--w-pt-tag-background'] = light;
          style['--w-pt-tag-border-color'] = border;
        } else if (this.options.tagType == 'bg-color') {
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
  }
};
</script>
