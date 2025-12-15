<template>
  <div>
    <a-form-model-item label="颜色主题">
      <a-select v-model="themeCode" @change="handleChange">
        <a-select-option v-for="(theme, i) in colorsTheme" :key="'theme_' + i" :value="i">
          {{ theme.name }}
          <i
            class="anticon color-preview-block"
            :style="{ color: color, fonstSize: '16px' }"
            v-for="(color, j) in theme.colors"
            :key="'color_' + j"
          >
            <svg viewBox="0 0 1024 1024" width="1em" height="1em" fill="currentColor" aria-hidden="true" focusable="false" class="">
              <path d="M864 64H160C107 64 64 107 64 160v704c0 53 43 96 96 96h704c53 0 96-43 96-96V160c0-53-43-96-96-96z"></path>
            </svg>
          </i>
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <div class="color-palette-list" style="text-align: right">
      <div v-for="(clr, i) in widget.configuration.color" :key="'clr_' + clr + '_' + i" style="margin-bottom: 8px; padding: 0px 20px">
        <a-row type="flex">
          <a-col flex="auto"><ColorPicker v-model="widget.configuration.color[i]" width="100%" /></a-col>
          <a-col flex="80px">
            <a-button size="small" type="link" @click="widget.configuration.color.splice(i, 1)" title="删除">
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
            </a-button>
            <a-button type="link" class="drag-btn-handler icon-only" @click.stop="() => {}" title="拖动排序" style="cursor: move">
              <Icon type="pticon iconfont icon-ptkj-tuodong" />
            </a-button>
          </a-col>
        </a-row>
      </div>
    </div>
    <a-button size="small" type="link" icon="plus" :block="true" @click="addColor">添加颜色</a-button>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import { deepClone } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';

const colorsTheme = {
  default: {
    name: '默认',
    code: 'default',
    colors: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc']
  },
  officeClassic: {
    name: 'Office配色',
    code: 'officeClassic',
    colors: ['#4F81BD', '#C0504D', '#9BBB59', '#8064A2', '#4BACC6', '#F79646', '#D6A3DC', '#F2C2B7']
  },
  modernAnalytics: {
    name: '数据分析风格',
    code: 'modernAnalytics',
    colors: ['#4472C4', '#ED7D31', '#70AD47', '#FFC000', '#5B9BD5', '#7030A0', '#A5A5A5', '#E46C0A']
  },
  highContrast: {
    name: '高对比度配色',
    code: 'highContrast',
    colors: ['#003366', '#CC3300', '#339933', '#990099', '#FFCC00', '#0066CC', '#FF6600', '#663399']
  },
  pastelHarmony: {
    name: '柔和协调色系',
    code: 'pastelHarmony',
    colors: ['#B8CCE4', '#F8CBAD', '#D99795', '#C3D69B', '#B2A1C7', '#92CDDC', '#FAC08F', '#FFF2CC']
  },
  gradientScales_singleHue: {
    name: '单色调渐变',
    code: 'gradientScales_singleHue',
    colors: ['#F7FBFF', '#DEEBF7', '#C6DBEF', '#9ECAE1', '#6BAED6', '#4292C6', '#2171B5', '#084594']
  },
  pastelHarmony_diverging: {
    name: '多色调渐变',
    code: 'pastelHarmony_diverging',
    colors: ['#B2182B', '#D6604D', '#F4A582', '#FDDBC7', '#D1E5F0', '#92C5DE', '#4393C3', '#2166AC']
  }
};

export default {
  name: 'ColorConfiguration',
  props: { widget: Object, designer: Object },
  mixins: [draggable],
  components: { ColorPicker },
  computed: {},
  data() {
    return {
      colorsTheme,
      themeCode: ''
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.bindDrag();
  },
  methods: {
    bindDrag() {
      this.tableDraggable(this.widget.configuration.color, this.$el.querySelector('.color-palette-list'), '.drag-btn-handler', {
        afterOnEnd: () => {
          // this.$set(this.widget.configuration, 'color', this.widget.configuration.color);
          // console.log(this.widget.configuration.color);
        }
      });
    },
    addColor() {
      this.widget.configuration.color.push(
        this.widget.configuration.color != undefined && this.widget.configuration.color.length > 0
          ? this.widget.configuration.color[this.widget.configuration.color.length - 1]
          : '#5470c6'
      );
    },
    handleChange(value, options) {
      this.widget.configuration.color.splice(0, this.widget.configuration.color.length);
      colorsTheme[this.themeCode].colors.forEach(item => {
        this.widget.configuration.color.push(item);
      });
    }
  }
};
</script>
