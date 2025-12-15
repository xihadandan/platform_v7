<template>
  <div
    :class="{
      'widget-icon-lib': true,
      'icon-lib-only-class': onlyIconClass ? true : false
    }"
  >
    <div class="header flex">
      <template v-if="onlyIconClass">
        <a-badge v-if="selectable">
          <a-avatar
            :class="{ iconClassSelected: true, hasIcon: iconClassSelected }"
            shape="square"
            alt="图标"
            :title="iconClassSelected ? $t('WidgetIconLib.copy', '复制') + ':' + iconData.name : ''"
            @click.stop="copy"
          >
            <Icon v-slot="icon" :type="iconClassSelected || 'iconfont icon-ptkj-jiahao'" style="font-size: 20px"></Icon>
          </a-avatar>
          <template v-if="!required" slot="count">
            <a-icon
              v-if="iconClassSelected"
              type="close-circle"
              theme="filled"
              style="color: #f5222d"
              @click.stop="deleteIcon"
              :title="$t('WidgetIconLib.delete', '删除')"
            />
          </template>
        </a-badge>
        <a-input-search @change="onSearchChange" allowClear style="width: calc(100% - 44px); margin-left: 12px" />
      </template>
      <template v-else>
        <a-input-search @change="onSearchChange" allowClear style="width: 100%" />
      </template>
    </div>

    <Scroll
      :style="`
        height: ${scrollHeight ? scrollHeight : onlyIconClass ? 'calc(100% - 76px)' : 'calc(100% - 56px - 65px)'};
        margin-top: var(--w-padding-xs);
        margin-right: var(--w-widget-icon-lib-scroll-margin-right);
        padding-right: var(--w-widget-icon-lib-scroll-padding-right);
      `"
    >
      <ul class="icon-ul">
        <template v-for="iconLib in iconLibArray">
          <template v-for="icon in iconLib.glyphs">
            <li
              v-if="!icon.hide"
              :key="icon.icon_id"
              v-show="filterIds.includes(icon.icon_id)"
              @click.stop="onClickIcon(icon.icon_class)"
              :class="iconClassSelected === icon.icon_class ? 'selected' : ''"
              :title="icon.name"
            >
              <!-- <i :class="icon.icon_class" /> -->
              <Icon
                :type="icon.icon_class"
                :size="36"
                :style="{
                  marginBottom: icon.icon_class.startsWith('svg-icon-') ? '9px' : 'unset'
                }"
              />
              <div class="icon-name w-ellipsis-2 w-user-select-none">{{ icon.name }}</div>
            </li>
          </template>
        </template>
      </ul>
      <a-empty v-if="filterIds.length == 0 && search" description="查无图标" />
    </Scroll>
    <template v-if="!onlyIconClass">
      <div class="icon-lib-handle">
        <span class="_label">{{ $t('WidgetIconLib.style', '风格') }}</span>
        <div class="icon-style-container">
          <div
            :class="{ 'icon-style-item': true, 'icon-style-active': currentStyle === item.value }"
            v-for="(item, index) in styleOptions"
            :key="index"
            @click="selectStyle(item)"
          >
            <div class="icon-style-content" v-if="index === 1" :style="filledStyle">
              <!--  <Icon :type="iconClassSelected" /> -->
              <Icon type="iconfont icon-a-iconfonticon-logo_wellinfo-01" />
            </div>
            <div class="icon-style-content" v-else-if="index === 2" :style="twoToneStyle">
              <Icon type="iconfont icon-a-iconfonticon-logo_wellinfo-01" />
            </div>
            <div class="icon-style-content" v-else :style="outlinedStyle">
              <Icon type="iconfont icon-a-iconfonticon-logo_wellinfo-01" />
            </div>
          </div>
        </div>
        <a-divider class="icon-lib-divider" :dashed="true" type="vertical" />
        <span class="_label">{{ $t('WidgetIconLib.shape', '形状') }}</span>
        <div class="icon-shape-container">
          <div
            :class="{ 'icon-shape-item': true, 'icon-shape-active': currentShape === item.value }"
            v-for="(item, index) in shapeOptions"
            :key="index"
            @click="selectShape(item)"
          />
        </div>
        <a-divider class="icon-lib-divider" :dashed="true" type="vertical" />
        <template v-if="colorConfigOptions">
          <span class="_label">{{ $t('WidgetIconLib.color', '颜色') }}</span>
          <ColorPicker v-model="currentColor" :width="120" :allowClear="true" @ok="changeColorPicker">
            <template slot="addonAfter"></template>
          </ColorPicker>
          <StyleColorTreeSelect
            ref="colorTreeSelect"
            :showPreview="false"
            :dropdownStyle="{
              width: 'auto',
              maxWidth: '400px'
            }"
            :popupContainer="getPopupContainer"
            :placeholder="$t('WidgetIconLib.themesColor', '内置颜色变量')"
            :colorConfig="colorConfigOptions"
            :autoDisplay="true"
            style="width: 102px"
            v-model="currentColorVar"
            @change="changeColorTreeSelect"
          />
        </template>
        <a-divider class="icon-lib-divider" :dashed="true" type="vertical" />
        <a-badge>
          <a-avatar v-if="!iconClassSelected" shape="square" style="background-color: #f7f7f7" :size="36"></a-avatar>
          <span
            v-else
            @click="copy"
            style="display: inline-block"
            :title="iconClassSelected ? $t('WidgetIconLib.copy', '复制') + ':' + iconData.name : ''"
          >
            <Icon :type="iconPreview" style="font-size: 24px; cursor: pointer" ref="icon" :size="36" />
          </span>

          <template v-if="!required" slot="count">
            <Icon
              v-if="iconClassSelected"
              type="close-circle"
              theme="filled"
              style="color: #f5222d"
              @click.stop="deleteIcon"
              :title="$t('WidgetIconLib.delete', '删除')"
            />
          </template>
        </a-badge>
      </div>
    </template>
  </div>
</template>
<style lang="less">
.widget-icon-lib {
  &.icon-lib-only-class {
    .iconClassSelected {
      background-color: transparent;
      border-radius: 4px;
      border: 1px solid var(--w-border-color-light);
      color: var(--w-text-color-light);

      &.hasIcon {
        border: 1px solid var(--w-primary-color);
        color: var(--w-primary-color);
        cursor: pointer;
      }
    }
  }
  height: 100%;
  .header {
    padding-top: var(--w-padding-xs);
  }
  .icon-lib-handle {
    display: flex;
    align-items: center;
    min-height: 65px;
    border-top: 1px solid rgba(0, 0, 0, 0.1);

    .color-picker-input {
      input.ant-input {
        border-top-right-radius: 0;
        border-bottom-right-radius: 0;
        border-right-color: transparent;
        &:focus,
        &:hover {
          border-right-color: var(--w-input-border-color-focus);
        }
      }
    }
  }
  ._label {
    margin-right: 8px;
    white-space: nowrap;
  }
  .style-color-select {
    .ant-select-selection__rendered {
      margin-left: 7px;
      margin-right: 20px;
    }
    .ant-select-selection__placeholder {
      font-size: 12px;
      right: 0;
    }
    .ant-select-arrow {
      right: 7px;
    }
  }
  .icon-lib-divider {
    height: 44px;
  }
  .icon-shape-container {
    display: flex;
    .icon-shape-item {
      position: relative;
      width: 32px;
      height: 32px;
      cursor: pointer;
      &::before {
        position: absolute;
        content: '';
        display: block;
        width: 16px;
        height: 16px;
        border: solid 2px #333;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
      }
      &:first-child {
        &::before {
          border-radius: 2px;
        }
      }
      &:last-child {
        margin-left: 8px;
        &::before {
          border-radius: 50%;
        }
      }
      &.icon-shape-active {
        border: solid 2px var(--w-primary-color);
        border-radius: 4px;
      }
    }
  }
  .icon-style-container {
    display: flex;
    .icon-style-item {
      position: relative;
      width: 32px;
      height: 32px;
      cursor: pointer;
      &:first-child,
      &:nth-child(2) {
        margin-right: 8px;
      }
      &.icon-style-active {
        border: solid 2px var(--w-primary-color);
        border-radius: 4px;
      }
    }
    .icon-style-content {
      position: absolute;
      width: 20px;
      height: 20px;
      text-align: center;
      line-height: 20px;
      border-radius: 4px;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      > i {
        font-size: 16px;
      }
    }
  }
  .icon-ul {
    list-style: none;
    padding-inline-start: 0px;
    i {
      font-size: 36px;
    }
    li {
      float: left;
      width: 88px;
      height: 88px;
      text-align: center;
      list-style: none;
      cursor: pointer;
      color: var(--w-text-color-dark);
      transition: color 0.3s ease-in-out, background-color 0.3s ease-in-out;
      position: relative;
      margin: 6px;
      border-radius: 4px;
      border: 1px solid transparent;
      background-color: #fff;
      overflow: hidden;
      // padding: 10px 0 0;
      .icon-name {
        font-size: 12px;
        padding: 0 var(--w-padding-2xs);
        line-height: 1.2;
      }
    }

    li:hover {
      border-color: var(--w-primary-color);
      color: var(--w-primary-color);
    }
    li.selected {
      background-color: var(--w-primary-color);
      color: #ffffff;
      border-color: var(--w-primary-color);
    }

    li .icon-class {
      display: block;
      text-align: center;
      transform: scale(0.83);
      font-family: Lucida Console, Consolas;
      white-space: nowrap;
    }
    &::after {
      display: table;
      content: '';
      clear: both;
    }
  }
}
</style>
<script type="text/babel">
import { getAlignFromPlacement } from 'ant-design-vue/es/vc-trigger/utils';
import getPlacements from 'ant-design-vue/es/tooltip/placements';
import StyleColorTreeSelect from '@pageAssembly/app/web/page/theme-designer/component/design/lib/style-color-tree-select.vue';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import { debounce, findIndex, some } from 'lodash';
import { copyToClipboard } from '@framework/vue/utils/util';
import SvgIcon from '@framework/../widget/svg-icon.vue';

export default {
  name: 'WidgetIconLib',
  inject: ['widgetContext'],
  props: {
    value: String,
    colorConfig: Object,
    required: {
      //图标必填
      type: Boolean,
      default: false
    },
    popupPlacement: {
      type: String,
      default: 'topRight'
    },
    onlyIconClass: {
      type: Boolean,
      default: false
    },
    scrollHeight: {
      type: String
    },
    selectable: {
      type: Boolean,
      default: true
    }
  },
  components: {
    StyleColorTreeSelect,
    ColorPicker,
    SvgIcon
  },
  data() {
    const styleOptions = [
      { label: '描线', value: 'outlined' },
      { label: '实心', value: 'filled' },
      { label: '双色', value: 'twoTone' }
    ];
    const shapeOptions = [
      { label: '矩形', value: 'rect', style: { borderRadius: '20%' } },
      { label: '圆形', value: 'circle', style: { borderRadius: '50%' } }
    ];
    let shapeMap = {};
    shapeOptions.forEach(item => {
      shapeMap[item.value] = item;
    });
    return {
      iconLibArray: [],
      /*表示通过v-model绑定*/ vmodel: this.$listeners.input != undefined,
      iconClassSelected: this.value || undefined,
      search: undefined,
      colorConfigJson: undefined,
      styleOptions,
      shapeOptions,
      shapeMap,
      currentStyle: styleOptions[0]['value'],
      currentShape: shapeOptions[0]['value'],
      currentColor: '',
      currentColorVar: undefined,
      iconPreviewStyle: {}
    };
  },
  watch: {
    value: {
      handler(v, o) {
        if (o && v === undefined && this.vmodel) {
          // 被删除了
          this.setIconConfig(v);
        }
      }
    }
  },
  computed: {
    iconPreview() {
      let icon = '';
      if (this.iconClassSelected) {
        const cssStyle = {
          ...this.selectedStyle,
          ...this.shapeMap[this.currentShape]['style']
        };
        const config = {
          iconClass: this.iconClassSelected,
          shape: this.currentShape,
          style: this.currentStyle,
          color: this.currentColor,
          colorVar: this.currentColorVar,
          showBackground: this.currentStyle !== this.styleOptions[0]['value'],
          cssStyle
        };
        if (!config.color) {
          delete config.color;
        }
        if (!config.colorVar) {
          delete config.colorVar;
        }
        this.iconPreviewStyle = Object.assign({ fontSize: '24px', backgroundColor: '#ffffff' }, cssStyle);
        console.log(config);
        icon = JSON.stringify(config);
      }
      return icon;
    },
    filledStyle() {
      let style;
      if (this.selectedColor) {
        style = {
          color: '#fff',
          'background-color': this.selectedColor
        };
      }
      return style;
    },
    twoToneStyle() {
      let style;
      if (this.selectedColor) {
        const color = this.selectedColor;
        style = {
          color,
          'background-color': `color-mix(in srgb, ${color} 5%, white)`
        };
      }
      return style;
    },
    outlinedStyle() {
      let style;
      if (this.selectedColor) {
        style = {
          color: this.selectedColor
        };
      }
      return style;
    },
    selectedColor() {
      let color;
      if (this.currentColor) {
        color = this.currentColor;
      } else if (this.currentColorVar) {
        color = `var(${this.currentColorVar})`;
      }
      return color;
    },
    selectedStyle() {
      return this[`${this.currentStyle}Style`];
    },
    colorConfigOptions() {
      if (this.colorConfig) {
        return this.colorConfig;
      }
      return this.colorConfigJson;
    },
    filterIds() {
      let ids = [];
      for (let i = 0, len = this.iconLibArray.length; i < len; i++) {
        let glyphs = this.iconLibArray[i].glyphs || [];
        for (let j = 0, jlen = glyphs.length; j < jlen; j++) {
          let id = glyphs[j].icon_id,
            iconClass = glyphs[j].font_class || glyphs[j].icon_class,
            name = glyphs[j].name;
          if (
            this.search == null ||
            iconClass.toLowerCase().indexOf(this.search.toLowerCase()) != -1 ||
            name.toLowerCase().indexOf(this.search.toLowerCase()) != -1
          ) {
            ids.push(id);
          }
        }
      }
      return ids;
    },
    iconData() {
      let data = {};
      if (this.iconClassSelected) {
        for (let i = 0, len = this.iconLibArray.length; i < len; i++) {
          let glyphs = this.iconLibArray[i].glyphs || [];
          for (let j = 0, jlen = glyphs.length; j < jlen; j++) {
            if (glyphs[j].icon_class == this.iconClassSelected) {
              data = glyphs[j];
            }
          }
        }
      }
      return data;
    }
  },
  created() {
    if (!this.defaultColorVar) {
      this.fetchThemeSpecify();
    }
  },
  beforeMount() {
    this.fetchIconfontMetadataAll();
    // this.fetchIconfontMetadata('/static/css/pt/iconfont/iconfont.json'); // 平台默认出场的图标库
    // this.fetchIconfontMetadata('/static/css/pt/ant-iconfont/iconfont.json'); // 平台默认出场的图标库
    // this.fetchIconfontMetadata('/static/css/app-iconfont/iconfont.json'); // 业务线补充的图标库
  },
  methods: {
    fetchSvgIcon() {
      return new Promise(resolve => {
        SvgIcon.methods.getAllSymbols().then(items => {
          console.log('svg-icon-ids', items);
          let glyphs = [];
          if (items) {
            items.forEach(item => {
              glyphs.push({
                icon_id: item.id,
                icon_class: item.id,
                name: item.id.replace('svg-icon-', '')
              });
            });
          }
          resolve({
            id: 'svg-icon',
            name: 'svg图标',
            glyphs
          });
        });
      });
    },
    isValidJSON(text) {
      try {
        if (!text) {
          return false;
        }
        JSON.parse(text);
        return true;
      } catch (e) {
        return false;
      }
    },
    setIconConfig(value = this.value) {
      if (!value) {
        this.iconClassSelected = undefined;
        this.currentStyle = this.styleOptions[0]['value'];
        this.currentShape = this.shapeOptions[0]['value'];
        this.currentColor = '';
        this.currentColorVar = this.defaultColorVar;
        return;
      }

      if (this.isValidJSON(value)) {
        const config = JSON.parse(value);
        this.iconClassSelected = config.iconClass;
        this.currentShape = config.shape;
        this.currentStyle = config.style;
        this.currentColor = config.color;
        this.currentColorVar = config.colorVar;
      } else {
        // this.currentColorVar = this.defaultColorVar;
      }
    },
    getPopupContainer(target, dialogContext) {
      const that = this;
      target.__vue__.$parent.$parent.$parent.getPopupAlign = function () {
        const props = this.$props;
        const popupPlacement = that.$props.popupPlacement,
          popupAlign = props.popupAlign,
          builtinPlacements = getPlacements({
            arrowPointAtCenter: false,
            autoAdjustOverflow: true,
            verticalArrowShift: 8
          });

        if (popupPlacement && builtinPlacements) {
          return getAlignFromPlacement(builtinPlacements, popupPlacement, popupAlign);
        }
        return popupAlign;
      };
      return target.closest('.style-color-select');
    },
    // 选择风格
    selectStyle(item) {
      this.currentStyle = item.value;
      this.emitChange();
    },
    // 选择形状
    selectShape(item) {
      this.currentShape = item.value;
      this.emitChange();
    },
    // 更改颜色值
    changeColorPicker(value) {
      this.currentColorVar = undefined;
      this.emitChange();
    },
    // 更改内置颜色变量
    changeColorTreeSelect(value, colorValue) {
      this.currentColor = colorValue;
      this.emitChange();
    },
    onSearchChange: debounce(function (v) {
      this.search = arguments[0].target.value || null;
    }, 500),
    deleteIcon() {
      this.iconClassSelected = undefined;
      this.emitChange();
    },
    onClickIcon(icon) {
      if (this.selectable) {
        this.iconClassSelected = icon;
        this.emitChange();
      }
    },
    emitChange() {
      let value = this.iconPreview;
      if (this.onlyIconClass) {
        value = this.iconClassSelected;
      }
      if (this.vmodel) {
        this.$emit('input', value);
      }
      this.$emit('change', value);
      // 兼容旧API
      this.$emit('iconSelected', value);
    },
    getIconClassSelected() {
      return this.iconClassSelected;
    },
    fetchIconfontMetadataAll() {
      let _this = this;
      Promise.all([
        this.fetchIconfontMetadata('/static/css/pt/iconfont/iconfont.json'), // 平台默认出场的图标库
        this.fetchIconfontMetadata('/static/css/app-iconfont/iconfont.json'), // 业务线补充的图标库
        this.fetchIconfontMetadata('/static/css/pt/ant-iconfont/iconfont.json'), // 平台默认出场的图标库
        this.fetchSvgIcon() // svg 图标
      ]).then(results => {
        results.forEach(result => {
          _this.iconLibArray.push(result);
          console.log('iconLibArray', _this.iconLibArray);
        });
      });
    },
    fetchIconfontMetadata(url) {
      let _this = this;
      // 获取业务线图标库：业务线图标库要定义不一样的图标设置，例如： iconfont": "app-iconfont", "css_prefix_text": "app-icon-",
      return new Promise((resolve, reject) => {
        $axios.get(url).then(res => {
          let glyphs = res.data.glyphs || [],
            font_family = res.data.font_family,
            css_prefix_text = res.data.css_prefix_text;
          for (let i = 0, len = glyphs.length; i < len; i++) {
            glyphs[i].icon_class = `${font_family} ${css_prefix_text}${glyphs[i].font_class}`;
            // 在已有的图标里找到一样的，就隐藏
            let isDel = some(_this.iconLibArray, item => {
              let hasIndex = findIndex(item.glyphs, { icon_class: glyphs[i].icon_class });
              return hasIndex > -1;
            });
            if (isDel) {
              glyphs[i].hide = true;
            }
          }
          // _this.iconLibArray.push(res.data);
          resolve(res.data);
        });
      });
    },
    copy(e) {
      if (this.iconClassSelected) {
        copyToClipboard(this.iconClassSelected, e, success => {
          if (success) {
            this.$message.success({
              content: this.$t('WidgetIconLib.copySuccess', '已复制')
            });
          }
        });
      }
    },
    fetchThemeSpecify() {
      $axios
        .get(`/proxy/api/theme/specify/getEnabled`, { params: {} })
        .then(({ data }) => {
          console.log('获取主题规范', data);
          if (data.code == 0) {
            let specifyDefJson = JSON.parse(data.data.defJson);
            this.colorConfigJson = specifyDefJson.colorConfig;
            this.defaultColorVar = specifyDefJson.colorConfig.themeColor.classify[0]['code'];
            this.setIconConfig();
          }
        })
        .catch(error => {});
    },
    $t() {
      if (this.widgetContext != undefined) {
        return this.widgetContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    }
  }
};
</script>
