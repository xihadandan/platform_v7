<template>
  <view class="uni-stat__w-select" :style="selectStyle">
    <span v-if="label" class="uni-label-text hide-on-phone">{{ label + "：" }}</span>
    <view class="uni-stat-box" :class="{ 'uni-stat__actived': selectedLabel }">
      <view class="uni-select" :class="{ 'uni-select--disabled': disabled, 'border-none': !bordered }">
        <view class="uni-select__input-box" @click="toggleSelector">
          <view v-if="selectedLabel" class="uni-select__input-text">
            <template v-if="selectorItemTextSlot">
              <template v-for="option in valueOptions">
                <slot name="selectorItemText" :item="option"></slot>
              </template>
            </template>
            <template v-else>{{ textShow }}</template>
          </view>
          <view v-else class="uni-select__input-text uni-select__input-placeholder">{{ placeholder }}</view>
          <view class="clear-icon" v-if="selectedLabel && clear && !disabled" @click.stop="clearVal">
            <uni-icons type="clear" color="var(--text-color-clear)" size="16" />
          </view>
          <view v-else class="arrow-icon">
            <uni-icons :type="showSelector ? 'right' : 'right'" size="16" color="var(--text-color-arrow)" />
          </view>
        </view>
        <view class="uni-select--mask" v-if="showSelector" @click="toggleSelector" />
        <template v-if="dropType == 'picker'">
          <uni-popup ref="popup" background-color="#fff" @change="changePicker" :borderRadius="popopRadius">
            <view class="popup-content">
              <view class="uni-select-popop-title">
                <view class="left">
                  <slot name="popopTitleLeft"></slot>
                </view>
                <view class="center">
                  <text>{{ placeholder || title || $t("global.pleaseSelect", "请选择") }}</text>
                </view>
                <view class="right">
                  <slot name="popopTitleRight"></slot>
                  <uni-w-button
                    v-if="multiple"
                    type="text"
                    @click="toggleSelector"
                    icon="iconfont icon-ptkj-dacha-xiao"
                  ></uni-w-button>
                </view>
              </view>
              <uniWDataSelectSelector
                v-model="valueCom"
                :initOptions="initOptions"
                :localdata="localdata"
                :multiple="multiple"
                :showSearch="showSearch"
                :selectCheckAll="selectCheckAll"
                :tokenSeparators="tokenSeparators"
                :componentType="componentType"
                :formatItemName="formatItemName"
                :moreStatus="moreStatus"
                :currentNode="currentNode"
                :emptyTips="emptyTips"
                @change="change"
                @checkAllChange="checkAllChange"
                @scrolltolower="onScrolltolower"
              >
                <template slot="operateBar" v-if="operateBarSlots">
                  <slot name="operateBar"></slot>
                </template>
                <template v-slot:selectedLabel="{ item, index, selectedOptions, parentIndex }">
                  <slot name="selectedLabel" :item="item"></slot>
                </template>
                <template v-slot:selectorItemText="{ item, index, selectedOptions, parentIndex }">
                  <slot
                    name="selectorItemText"
                    :item="item"
                    :index="index"
                    :selectedOptions="selectedOptions"
                    :parentIndex="parentIndex"
                  ></slot>
                </template>
                <template slot="selectorLoadMore">
                  <slot name="selectorLoadMore"></slot>
                </template>
                <template slot="selectorListBottom">
                  <slot name="selectorListBottom"></slot>
                </template>
              </uniWDataSelectSelector>
            </view>
          </uni-popup>
        </template>
        <template v-else>
          <view class="uni-select__selector" :style="getOffsetByPlacement" v-if="showSelector">
            <view :class="placement == 'bottom' ? 'uni-popper__arrow_bottom' : 'uni-popper__arrow_top'"></view>
            <uniWDataSelectSelector
              v-model="valueCom"
              :initOptions="initOptions"
              :localdata="localdata"
              :multiple="multiple"
              :showSearch="showSearch"
              :selectCheckAll="selectCheckAll"
              :tokenSeparators="tokenSeparators"
              :componentType="componentType"
              :formatItemName="formatItemName"
              :moreStatus="moreStatus"
              :currentNode="currentNode"
              :emptyTips="emptyTips"
              @change="change"
              @checkAllChange="checkAllChange"
              @scrolltolower="onScrolltolower"
            >
              <template slot="operateBar" v-if="operateBarSlots">
                <slot name="operateBar"></slot>
              </template>
              <template v-slot:selectorItemText="{ item, index, selectedOptions, parentIndex }">
                <slot name="selectorItemText" :item="item"></slot>
              </template>
              <template v-slot:selectorItem="{ item, index, selectedOptions, parentIndex }">
                <slot
                  name="selectorItem"
                  :item="item"
                  :index="index"
                  :selectedOptions="selectedOptions"
                  :parentIndex="parentIndex"
                ></slot>
              </template>
              <template slot="selectorLoadMore">
                <slot name="selectorLoadMore"></slot>
              </template>
              <template slot="selectorListBottom">
                <slot name="selectorListBottom"></slot>
              </template>
            </uniWDataSelectSelector>
          </view>
        </template>
      </view>
    </view>
  </view>
</template>

<script>
/**
 * DataChecklist 数据选择器
 * @description 通过数据渲染的下拉框组件
 * @tutorial https://uniapp.dcloud.io/component/uniui/uni-data-select
 * @property {String} value 默认值
 * @property {Array} localdata 本地数据 ，格式 [{text:'',value:''}]
 * @property {Boolean} clear 是否可以清空已选项
 * @property {Boolean} emptyText 没有数据时显示的文字 ，本地数据无效
 * @property {String} label 左侧标题
 * @property {String} placeholder 输入框的提示文字
 * @property {Boolean} disabled 是否禁用
 * @property {String} placement 弹出位置
 * 	@value top   		顶部弹出
 * 	@value bottom		底部弹出（default)
 * @event {Function} change  选中发生变化触发
 */
import { debounce, every } from "lodash";
import { utils } from "wellapp-uni-framework";
import uniWDataSelectSelector from "./uni-w-data-select-selector.vue";
export default {
  name: "uni-w-data-select",
  mixins: [uniCloud.mixinDatacom || {}],
  components: {
    uniWDataSelectSelector,
  },
  props: {
    // 最原始选项数据
    initOptions: {
      type: Array,
      default() {
        return [];
      },
    },
    localdata: {
      type: Array,
      default() {
        return [];
      },
    },
    value: {
      type: [String, Number, Array],
      default: "",
    },
    modelValue: {
      type: [String, Number, Array],
      default: "",
    },
    label: {
      type: String,
      default: "",
    },
    placeholder: {
      type: String,
      default: "",
    },
    emptyTips: {
      type: String,
      default: "",
    },
    clear: {
      type: Boolean,
      default: true,
    },
    defItem: {
      type: Number,
      default: 0,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    // 格式化输出 用法 field="_id as value, version as text, uni_platform as label" format="{label} - {text}"
    format: {
      type: String,
      default: "",
    },
    placement: {
      type: String,
      default: "bottom",
    },
    // 默认单选，
    multiple: {
      type: Boolean,
      default: false,
    },
    // 是否全选
    selectCheckAll: {
      type: Boolean,
      default: false,
    },
    // 显示搜索
    showSearch: {
      type: Boolean,
      default: false,
    },
    // 分隔符
    tokenSeparators: {
      type: String,
      default: ";",
    },
    textParam: {
      type: String,
      default: "text",
    },
    valueParam: {
      type: String,
      default: "value",
    },
    optionsParam: {
      type: String,
      default: "options",
    },
    componentType: {
      type: String,
      default: "select",
    },
    dropType: {
      type: String,
      default: "dropdown",
    },
    groupColumn: String,
    // 是否存在更多加载数据
    moreStatus: {
      type: Boolean,
      default: false,
    },
    // 显示边框
    bordered: {
      type: Boolean,
      default: false,
    },
    // 当前选中的节点
    currentNode: {
      type: Object,
      default: () => ({}),
    },
    popopRadius: {
      type: String,
      default: "16px 16px 0 0",
    },
    title: String,
    selectorItemTextSlot: Boolean, // 是否有用选项的slot
    alignLeft: Boolean, // 是否左对齐
  },
  data() {
    let operateBarSlots = this.$slots.operateBar != undefined;
    return {
      operateBarSlots,
      showSelector: false,
      selectOptions: [],
      checkAll: false,
      valueLabelMap: {},
      selectedLabels: [],
      selectOptionsMap: [],
      valueOptions: [], // 选中的项
    };
  },
  created() {
    this.debounceGet = this.debounce(() => {
      this.query();
    }, 300);
  },
  mounted() {},
  computed: {
    valueCom() {
      // #ifdef VUE3
      let value = this.modelValue;
      // #endif
      // #ifndef VUE3
      let value = this.value;
      // #endif

      if (this.multiple && !Array.isArray(value)) {
        value = value ? value.split(this.tokenSeparators) : [];
      }
      return value;
    },
    textShow() {
      // 长文本显示
      let text = this.selectedLabel;
      if (text.length > 10) {
        return text.slice(0, 25) + "...";
      }
      return text;
    },
    getOffsetByPlacement() {
      switch (this.placement) {
        case "top":
          return "bottom:calc(100% + 12px);";
        case "bottom":
          return "top:calc(100% + 12px);";
      }
    },
    // 选中label字符串
    selectedLabel() {
      let selectedLabel = this.selectedLabels.join(this.tokenSeparators);
      return selectedLabel;
    },
    selectStyle() {
      let style = {};
      if (this.alignLeft) {
        style["--uni-select-selector-item-text-view-justify-content"] = "flex-start";
      }
      return style;
    },
  },
  filters: {},
  watch: {
    initOptions: {
      immediate: true,
      handler(val, old) {
        if (val.length) {
          this.setValueLabelMap(val);
        }
      },
    },
    localdata: {
      immediate: true,
      handler(val, old) {
        if (Array.isArray(val) && old !== val) {
          this.selectOptions = val;
        }
      },
    },
    valueCom(val, old) {
      this.setSelectedLabels();
    },
    selectOptions: {
      immediate: true,
      handler(val) {
        if (val.length) {
          this.setValueLabelMap(val);
          setTimeout(() => {
            this.setSelectedLabels();
          }, 300);
        }
      },
    },
    showSelector(val) {
      if (!val) {
        this.$emit("clearCurrentNode");
      }
    },
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    debounce(fn, time = 100) {
      let timer = null;
      return function (...args) {
        if (timer) clearTimeout(timer);
        timer = setTimeout(() => {
          fn.apply(this, args);
        }, time);
      };
    },

    /**
     * @param {[String, Number]} value
     * 判断用户给的 value 是否同时为禁用状态
     */
    isDisabled(value) {
      let isDisabled = false;

      this.selectOptions.forEach((item) => {
        if (item[this.valueParam] === value) {
          isDisabled = item.disable;
        }
      });

      return isDisabled;
    },

    clearVal() {
      this.emit("");
    },
    change(item) {
      if (!item.disable) {
        if (!this.multiple) {
          this.showSelector = false;
          if (this.dropType == "picker") {
            this.$refs.popup.close();
          }
          this.emit(item[this.valueParam]);
        } else {
          this.multipleChange(item);
        }
      }
    },
    // 多选时数据变化
    multipleChange(item) {
      let value = utils.deepClone(this.valueCom || []);
      let index = value.indexOf(item[this.valueParam]);
      if (index > -1) {
        // 存在则移除
        value.splice(index, 1);
      } else {
        // 不存在就添加
        value.push(item[this.valueParam]);
      }
      this.emit(value);
    },
    checkAllChange(value) {
      this.emit(value);
    },
    emit(val) {
      this.$emit("input", val);
      this.$emit("update:modelValue", val);
      this.$emit("change", val);
    },
    toggleSelector() {
      if (this.disabled) {
        return;
      }
      this.showSelector = !this.showSelector;
      if (this.dropType == "picker") {
        if (this.showSelector) {
          this.$refs.popup.open("bottom");
        } else {
          this.$refs.popup.close();
        }
      }
    },
    changePicker(e) {
      this.showSelector = e.show;
    },
    formatItemName(item) {
      // let { text, value, channel_code } = item;
      let text = item[this.textParam];
      // channel_code = channel_code ? `(${channel_code})` : "";

      if (this.format) {
        // 格式化输出
        let str = "";
        str = this.format;
        for (let key in item) {
          str = str.replace(new RegExp(`{${key}}`, "g"), item[key]);
        }
        return str;
      } else {
        return text ? text : ``;
      }
    },
    // 获取当前加载的数据
    getLoadData() {
      return this.selectOptions;
    },
    // 设置所有显示值
    setValueLabelMap(options) {
      for (let i = 0, len = options.length; i < len; i++) {
        const value = options[i][this.valueParam];
        const label = this.formatItemName(options[i]);
        if (value && label) {
          this.valueLabelMap[value] = label;
          this.selectOptionsMap[value] = options[i];
        }
        if (options[i][this.optionsParam]) {
          this.setValueLabelMap(options[i][this.optionsParam]);
        }
      }
    },
    setSelectedLabels() {
      this.selectedLabels.splice(0, this.selectedLabels.length);
      this.valueOptions.splice(0, this.valueOptions.length);
      let values = utils.deepClone(this.valueCom);
      if (values == undefined) {
        return [];
      }
      if (typeof values == "string") {
        values = values.split(this.tokenSeparators);
      }
      for (let i = 0, len = values.length; i < len; i++) {
        if (this.valueLabelMap[values[i]] != undefined) {
          this.selectedLabels.push(this.valueLabelMap[values[i]]);
        }
        if (this.selectOptionsMap[values[i]] != undefined) {
          this.valueOptions.push(this.selectOptionsMap[values[i]]);
        }
      }
      this.$emit("getLabel", this.selectedLabels);
      return this.selectedLabels;
    },
    onScrolltolower() {
      this.$emit("scrolltolower");
    },
  },
};
</script>

<style lang="scss">
$uni-base-color: var(--w-text-color-base);
$uni-main-color: var(--w-text-color-mobile);
$uni-secondary-color: var(--w-text-color-light);
$uni-border-3: var(--w-border-color-mobile);

/* #ifndef APP-NVUE */
@media screen and (max-width: 500px) {
  .hide-on-phone {
    display: none;
  }
}

/* #endif */
.uni-stat__w-select {
  display: flex;
  align-items: center;
  // padding: 15px;
  /* #ifdef H5 */
  cursor: pointer;
  /* #endif */
  width: 100%;
  flex: 1;
  box-sizing: border-box;
  --uni-select-selector-item-text-view-justify-content: center;

  .uni-stat-box {
    width: 100%;
    flex: 1;
  }

  .uni-stat__actived {
    width: 100%;
    flex: 1;
    // outline: 1px solid #2979ff;
  }

  .uni-label-text {
    font-size: 14px;
    font-weight: bold;
    color: $uni-main-color;
    margin: auto 0;
    margin-right: 5px;
  }

  .uni-select {
    font-size: var(--w-font-size-base);
    border: 1px solid $uni-border-3;
    box-sizing: border-box;
    border-radius: 4px;
    padding: 0 var(--w-padding-2xs);
    position: relative;
    /* #ifndef APP-NVUE */
    display: flex;
    user-select: none;
    /* #endif */
    flex-direction: row;
    align-items: center;
    border-bottom: solid 1px $uni-border-3;
    width: 100%;
    flex: 1;
    height: 35px;

    &--disabled {
      background-color: var(--bg-disable-color);
      cursor: not-allowed;

      .uni-select__input-text {
        color: var(--text-color-disable);
      }
    }

    &.border-none {
      border: none;

      &:not(.uni-select--disabled) {
        padding: 0px;
      }
    }
  }

  .uni-select__label {
    font-size: 16px;
    // line-height: 22px;
    height: 35px;
    padding-right: 10px;
    color: $uni-secondary-color;
  }

  .uni-select__input-box {
    height: 35px;
    position: relative;
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex: 1;
    flex-direction: row;
    align-items: center;

    .clear-icon,
    .arrow-icon {
      padding-left: 5px;
    }
  }

  .uni-select__input {
    flex: 1;
    font-size: 14px;
    height: 22px;
    line-height: 22px;
  }

  .uni-select__input-plac {
    font-size: 14px;
    color: $uni-secondary-color;
  }

  .uni-select__selector__disabled {
    opacity: 0.4;
    cursor: default;
  }

  /* picker 弹出层通用的指示小三角 */
  .uni-popper__arrow_bottom,
  .uni-popper__arrow_bottom::after,
  .uni-popper__arrow_top,
  .uni-popper__arrow_top::after {
    position: absolute;
    display: block;
    width: 0;
    height: 0;
    border-color: transparent;
    border-style: solid;
    border-width: 6px;
  }

  .uni-popper__arrow_bottom {
    filter: drop-shadow(0 2px 12px rgba(0, 0, 0, 0.03));
    top: -6px;
    left: 10%;
    margin-right: 3px;
    border-top-width: 0;
    border-bottom-color: #ebeef5;
  }

  .uni-popper__arrow_bottom::after {
    content: " ";
    top: 1px;
    margin-left: -6px;
    border-top-width: 0;
    border-bottom-color: #fff;
  }

  .uni-popper__arrow_top {
    filter: drop-shadow(0 2px 12px rgba(0, 0, 0, 0.03));
    bottom: -6px;
    left: 10%;
    margin-right: 3px;
    border-bottom-width: 0;
    border-top-color: #ebeef5;
  }

  .uni-popper__arrow_top::after {
    content: " ";
    bottom: 1px;
    margin-left: -6px;
    border-bottom-width: 0;
    border-top-color: #fff;
  }

  .uni-select__input-text {
    // width: 280px;
    width: 100%;
    color: $uni-main-color;
    display: -webkit-box;
    overflow: hidden;
    white-space: normal !important;
    text-overflow: ellipsis;
    word-wrap: break-word;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    word-break: break-all;
  }

  .uni-select__input-placeholder {
    color: var(--text-color-placeholder);
    font-size: var(--w-font-size-base);
  }

  .uni-select--mask {
    position: fixed;
    top: 0;
    bottom: 0;
    right: 0;
    left: 0;
    z-index: 2;
  }

  .uni-select__selector {
    /* #ifndef APP-NVUE */
    box-sizing: border-box;
    /* #endif */
    position: absolute;
    left: 0;
    width: 100%;
    background-color: #ffffff;
    border: 1px solid var(--w-border-color-mobile);
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    z-index: 3;
    padding: 4px 0;
  }

  .popup-content {
    .uni-select-popop-title {
      padding: 14px 5rem 8px;
      font-size: var(--w-font-size-base);
      display: flex;
      align-items: center;
      justify-content: space-between;
      position: relative;

      .left {
        position: absolute;
        left: 10px;
      }

      .center {
        flex: 1;
        text-align: center;
        font-size: var(--w-font-size-lg);
        color: $uni-main-color;
        font-weight: bold;
      }

      .right {
        position: absolute;
        right: 10px;
      }
    }
  }
}
</style>
