<template>
  <div>
    <a-form-model-item label="块级组件" size="small" v-if="editBlock">
      <a-switch v-model="widget.configuration.style.block" @change="onBlockChange" />
    </a-form-model-item>
    <div v-show="vAllowSetWidth || vAllowSetHeight">
      <a-form-model-item label="宽度" v-if="vAllowSetWidth">
        <a-radio-group
          size="small"
          v-show="widthPersent"
          v-model="widget.configuration.style.width"
          button-style="solid"
          @change="onWidthRadioChange"
        >
          <a-radio-button value="25.00%">1/4</a-radio-button>
          <a-radio-button value="33.33%">1/3</a-radio-button>
          <a-radio-button value="50.00%">1/2</a-radio-button>
          <a-radio-button value="66.67%">2/3</a-radio-button>
          <a-radio-button value="75.00%">3/4</a-radio-button>
          <a-radio-button value="100%">1</a-radio-button>
        </a-radio-group>
        <div class="input-number-suffix-wrapper">
          <a-input-number
            :style="{ width: '75px', marginLeft: '5px' }"
            v-model="widthPxValue"
            size="small"
            :min="0"
            :max="2000"
            @change="onInputWidthChange"
          />
          <i>px</i>
        </div>
      </a-form-model-item>
      <a-form-model-item v-if="vAllowSetHeight">
        <template slot="label">
          <a-tooltip placement="top">
            <template slot="title">
              <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                <li>高度使用百分比（%），请确保其父级有设置高度</li>
                <li>100vh为可视区域（Viewport）高度的100%</li>
              </ul>
            </template>
            高度
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-tooltip>
        </template>
        <a-row type="flex">
          <a-col flex="auto" style="padding-right: 5px">
            <a-input-number size="small" v-model="height" @change="onInputHeight" v-show="heightUnit != 'auto'" />
          </a-col>
          <a-col flex="70px">
            <a-select size="small" v-model="heightUnit" @change="onInputHeight" style="width: 100%">
              <a-select-option value="px">px</a-select-option>
              <a-select-option value="%">%</a-select-option>
              <a-select-option value="vh">vh</a-select-option>
              <a-select-option value="auto">自动</a-select-option>
            </a-select>
          </a-col>
        </a-row>
        <!-- <a-input v-model.number="height" @change="onInputHeight" v-show="heightUnit != 'auto'">
        <a-select slot="addonAfter" v-model="heightUnit" @change="onInputHeight">
          <a-select-option value="px"><div style="width: 30px">px</div></a-select-option>
          <a-select-option value="%"><div style="width: 30px">%</div></a-select-option>
          <a-select-option value="auto"><div style="width: 30px">自动</div></a-select-option>
        </a-select>
      </a-input>
      <a-select v-show="heightUnit == 'auto'" v-model="heightUnit" @change="onInputHeight">
        <a-select-option value="px"><div style="width: 30px">px</div></a-select-option>
        <a-select-option value="%"><div style="width: 30px">%</div></a-select-option>
        <a-select-option value="auto"><div style="width: 30px">自动</div></a-select-option>
      </a-select> -->
      </a-form-model-item>
    </div>

    <a-form-model-item
      label="边距"
      class="vertical display-b"
      v-if="setMarginPadding === true || setMarginPadding[0] || setMarginPadding[1]"
    >
      <div class="layout-box-container">
        <div class="margin-top-div" v-show="setMarginPadding === true || setMarginPadding[0]">
          <span class="next-input next-medium next-noborder">
            <input placeholder="0" v-model.number="widget.configuration.style.margin[0]" autocomplete="off" height="100%" maxlength="3" />
          </span>
        </div>
        <div class="margin-right-div" v-show="setMarginPadding === true || setMarginPadding[0]">
          <span class="next-input next-medium next-noborder">
            <input placeholder="0" v-model.number="widget.configuration.style.margin[1]" autocomplete="off" height="100%" maxlength="3" />
          </span>
        </div>
        <div class="margin-bottom-div" v-show="setMarginPadding === true || setMarginPadding[0]">
          <span class="help-txt">外边距</span>
          <span class="next-input next-medium next-noborder">
            <input placeholder="0" v-model.number="widget.configuration.style.margin[2]" autocomplete="off" height="100%" maxlength="3" />
          </span>
        </div>
        <div class="margin-left-div" v-show="setMarginPadding === true || setMarginPadding[0]">
          <span class="next-input next-medium next-noborder">
            <input placeholder="0" v-model.number="widget.configuration.style.margin[3]" autocomplete="off" height="100%" maxlength="3" />
          </span>
        </div>
        <div :class="paddingPrefx + 'top-div'" v-show="setMarginPadding === true || setMarginPadding[1]">
          <span class="next-input next-medium next-noborder">
            <input placeholder="0" autocomplete="off" height="100%" maxlength="3" v-model.number="widget.configuration.style.padding[0]" />
          </span>
        </div>
        <div :class="paddingPrefx + 'right-div'" v-show="setMarginPadding === true || setMarginPadding[1]">
          <span class="next-input next-medium next-noborder">
            <input placeholder="0" autocomplete="off" height="100%" maxlength="3" v-model.number="widget.configuration.style.padding[1]" />
          </span>
        </div>
        <div :class="paddingPrefx + 'bottom-div'" v-show="setMarginPadding === true || setMarginPadding[1]">
          <span class="help-txt">内边距</span>
          <span class="next-input next-medium next-noborder">
            <input placeholder="0" autocomplete="off" height="100%" maxlength="3" v-model.number="widget.configuration.style.padding[2]" />
          </span>
        </div>
        <div :class="paddingPrefx + 'left-div'" v-show="setMarginPadding === true || setMarginPadding[1]">
          <span class="next-input next-medium next-noborder">
            <input placeholder="0" autocomplete="off" height="100%" maxlength="3" v-model.number="widget.configuration.style.padding[3]" />
          </span>
        </div>
      </div>
    </a-form-model-item>
  </div>
</template>
<script type="text/babel">
import { debounce, isNumber } from 'lodash';
export default {
  name: 'StyleConfiguration',
  props: {
    widget: Object,
    editBlock: {
      type: Boolean,
      default: true
    },
    setWidthHeight: {
      // 可调整宽、高
      type: [Boolean, Array], // true ：宽高都可调，false 不可设置宽高，[true,false]: 宽可设置、高不可设置，[false,true]: 宽不可设置、高可设置
      default: true,
      validator: function (v) {
        return typeof v === 'boolean' || (Array.isArray(v) && v.length == 2 && typeof v[0] == 'boolean' && typeof v[1] == 'boolean');
      }
    },
    widthPersent: {
      type: Boolean,
      default: true
    },
    setMarginPadding: {
      // 可调整外边距、内间距
      type: [Boolean, Array],
      default: true,
      validator: function (v) {
        return typeof v === 'boolean' || (Array.isArray(v) && v.length == 2 && typeof v[0] == 'boolean' && typeof v[1] == 'boolean');
      }
    }
  },
  data() {
    let height = undefined,
      heightUnit = 'px';
    if (this.widget.configuration.height) {
      if (this.widget.configuration.height === 'auto') {
        heightUnit = 'auto';
        height = 'auto';
      } else if (isNumber(this.widget.configuration.height)) {
        height = this.widget.configuration.height;
      } else {
        height = parseInt(this.widget.configuration.height);
        heightUnit = this.widget.configuration.height.replace(/[0-9]/g, '');
      }
    } else {
      heightUnit = 'auto';
    }
    return { widthPxValue: undefined, widthChanged: '25.00%', height, heightUnit };
  },

  beforeCreate() {},
  components: {},
  computed: {
    paddingPrefx() {
      return this.vMarginSet ? 'padding-' : 'margin-';
    },
    vMarginSet() {
      return typeof this.setMarginPadding === 'boolean' ? this.setMarginPadding : this.setMarginPadding[0];
    },
    vAllowSetHeight() {
      return typeof this.setWidthHeight === 'boolean' ? this.setWidthHeight : this.setWidthHeight[1];
    },
    vAllowSetWidth() {
      return typeof this.setWidthHeight === 'boolean' ? this.setWidthHeight : this.setWidthHeight[0];
    }
  },
  created() {},

  // }

  // if (this.widget.tempStyle && this.widget.tempStyle.width && this.widget.configuration.style) {
  //   this.widget.configuration.style.width = this.widget.tempStyle.width;
  // }},
  methods: {
    onInputHeight(e) {
      if (this.heightUnit == 'auto') {
        this.widget.configuration.height = this.heightUnit;
      } else {
        if (this.height) {
          this.widget.configuration.height = this.height == 'auto' ? null : this.height + this.heightUnit;
          if (this.height == 'auto' && e != 'auto') {
            this.height = null;
          }
        } else {
          this.widget.configuration.height = null;
        }
        // px、vh 默认值
        if (!this.height) {
          if (this.heightUnit == 'px' && this.widget.configuration.style.defaultHeightPX) {
            this.height = this.widget.configuration.style.defaultHeightPX;
          } else if (this.heightUnit == 'vh' && this.widget.configuration.style.defaultHeightVH) {
            this.height = this.widget.configuration.style.defaultHeightVH;
          }
          if (this.height) {
            this.widget.configuration.height = this.height + this.heightUnit;
          }
        }
      }
      this.widget.configuration.style.height = this.widget.configuration.height;
    },
    onBlockChange() {
      if (!this.widget.configuration.style.block) {
        if (this.widthChanged) {
          this.widget.configuration.style.width = this.widthChanged;
        }
      } else {
        this.widthChanged = this.widget.configuration.style.width;
        this.widget.configuration.style.width = '100%';
      }
    },
    onInputWidthChange: debounce(function (v) {
      this.widget.configuration.style.width = this.widthPxValue;
    }, 500),

    onWidthRadioChange() {
      this.widthPxValue = undefined;
      this.widthChanged = this.widget.configuration.style.width;
    }
  },
  beforeMount() {
    if (typeof this.widget.configuration.style.width === 'number') {
      this.widthPxValue = this.widget.configuration.style.width;
    }
  },
  mounted() {}
};
</script>
