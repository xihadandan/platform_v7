<template>
  <view class="uni-select__w-selector-content">
    <view v-if="hasOperateBar" class="flex f_y_c uni-select__selector-operate-bar">
      <uni-search-bar
        v-if="showSearch"
        class="f_g_1"
        :placeholder="$t('global.searchplaceholder', '请输入搜索内容')"
        :cancelText="$t('global.cancel', '取消')"
        @confirm="search"
        v-model="searchValue"
        @focus="focusSearch"
        @input="inputSearchValue"
        @cancel="cancelSearchValue"
        @clear="clearSearchValue"
      >
      </uni-search-bar>
      <view v-if="multiple && selectCheckAll && this.selectOptions.length" class="uni-select__selector-checkall f_s_0">
        <button class="checkall-btn" :type="checkAll ? 'primary' : 'default'" size="mini" @tap="checkAllChange">
          {{ $t("global.selectAll", "全选") }}
        </button>
      </view>
    </view>
    <view v-if="operateBarSlot" class="uni-select__selector-operate-bar">
      <slot name="operateBar"></slot>
    </view>
    <scroll-view scroll-y="true" class="uni-select__selector-scroll" @scrolltolower="onScrolltolower">
      <view class="uni-select__selector-empty" v-if="selectOptions.length === 0">
        <text>{{ emptyTips || $t("global.emptyTips", "无选项") }}</text>
      </view>
      <view v-else class="uni-select__selector-list">
        <template v-if="componentType == 'select-group'">
          <uniWDataSelectSelectorGroup :key="pindex" v-for="(pitem, pindex) in selectOptions">
            <template slot="label">{{ pitem.label }}</template>
            <view
              class="uni-select__selector-item"
              :class="{
                'uni-select__selector-item-selected': checkJudge(item[valueParam]),
                multiple: multiple,
                'current-item': item[valueParam] == currentNode[valueParam],
              }"
              v-for="(item, index) in pitem[optionsParam]"
              :key="index"
              @tap="change(item)"
            >
              <uni-w-data-checkbox
                v-if="multiple"
                :value="checkFilterHandler(item[valueParam])"
                :localdata="[{ text: '', value: item[valueParam] }]"
                checkboxStyle
                @tap="change(item)"
              ></uni-w-data-checkbox>
              <view class="uni-select__selector-item-text-view">
                <slot name="selectorItemText" :item="item">
                  <text class="uni-select__selector-text" :class="{ 'uni-select__selector__disabled': item.disable }">
                    {{ formatItemName(item) }}
                  </text>
                </slot>
              </view>
              <slot
                name="selectorItem"
                :item="item"
                :index="index"
                :selectedOptions="pitem[optionsParam]"
                :parentIndex="pindex"
              ></slot>
            </view>
          </uniWDataSelectSelectorGroup>
        </template>
        <template v-else>
          <view
            class="uni-select__selector-item"
            :class="{
              'uni-select__selector-item-selected': checkJudge(item[valueParam]),
              multiple: multiple,
              'current-item': item[valueParam] == currentNode[valueParam],
            }"
            v-for="(item, index) in selectOptions"
            :key="index"
            @tap="change(item)"
          >
            <uni-w-data-checkbox
              v-if="multiple"
              :value="checkFilterHandler(item[valueParam])"
              :localdata="[{ text: '', value: item[valueParam] }]"
              checkboxStyle
              @tap="change(item)"
            ></uni-w-data-checkbox>
            <view class="uni-select__selector-item-text-view">
              <slot name="selectorItemText" :item="item" :index="index" :selectedOptions="selectOptions">
                <text class="uni-select__selector-text" :class="{ 'uni-select__selector__disabled': item.disable }">
                  {{ formatItemName(item) }}
                </text>
              </slot>
            </view>
            <slot name="selectorItem" :item="item" :index="index" :selectedOptions="selectOptions"></slot>
          </view>
        </template>
        <slot name="selectorLoadMore" v-if="!searchValue"></slot>
        <slot name="selectorListBottom"></slot>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { debounce, every, groupBy, findIndex } from "lodash";
import { utils } from "wellapp-uni-framework";
import uniWDataSelectSelectorGroup from "./uni-w-data-select-selector-group.vue";
export default {
  name: "uniWDataSelectSelector",
  mixins: [uniCloud.mixinDatacom || {}],
  components: { uniWDataSelectSelectorGroup },
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
    emptyTips: {
      type: String,
      default: "",
    },
    // 格式化输出 用法 field="_id as value, version as text, uni_platform as label" format="{label} - {text}"
    format: {
      type: String,
      default: "",
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
    formatItemName: {
      type: Function,
    },
    groupColumn: String,
    // 是否存在更多加载数据
    moreStatus: {
      type: Boolean,
      default: false,
    },
    // 当前选中的节点
    currentNode: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    let operateBarSlot = this.$slots.operateBar != undefined;
    return {
      operateBarSlot,
      selectOptions: [],
      searchValue: "",
      checkAll: false,
      valueLabelMap: {},
      selectedLabels: [],
      focus: false,
    };
  },
  created() {
    this.debounceGet = this.debounce(() => {
      this.search();
    }, 300);
  },
  mounted() {
    if (this.multiple && this.selectCheckAll) {
      this.checkAllJudge();
    }
  },
  computed: {
    valueCom() {
      // #ifdef VUE3
      return this.modelValue;
      // #endif
      // #ifndef VUE3
      return this.value;
      // #endif
    },
    hasOperateBar() {
      return (this.showSearch || (this.multiple && this.selectCheckAll)) && (this.selectOptions.length || this.focus);
    },
  },
  filters: {
    checkFilter(itemVal, value) {
      if (value && typeof value == "object") {
        return value.indexOf(itemVal) > -1;
      }
      return false;
    },
  },
  watch: {
    localdata: {
      immediate: true,
      handler(val, old) {
        if (Array.isArray(val) && old !== val) {
          this.selectOptions = val;
        }
      },
    },
    valueCom(val, old) {
      if (this.multiple && this.selectCheckAll) {
        this.checkAllJudge();
      }
    },
    selectOptions: {
      immediate: true,
      handler(val) {
        if (val.length) {
        }
      },
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
    change(item) {
      this.$emit("change", item);
    },
    // 获取当前加载的数据
    getLoadData() {
      return this.selectOptions;
    },

    // 搜索框
    search() {
      if (this.componentType == "select-group") {
        if (this.searchValue) {
          let selectOptions = [];
          for (let i = 0, len = this.localdata.length; i < len; i++) {
            for (let j = 0, jlen = this.localdata[i][this.optionsParam].length; j < jlen; j++) {
              let label = this.formatItemName(this.localdata[i][this.optionsParam][j]);
              if (label && label.toString().indexOf(this.searchValue) != -1) {
                let hasIndex = findIndex(selectOptions, (opt) => {
                  return opt.label == this.localdata[i].label;
                });
                if (hasIndex == -1) {
                  let item = { label: this.localdata[i].label };
                  item[this.optionsParam] = [];
                  selectOptions.push(item);
                }
                hasIndex = findIndex(selectOptions, (opt) => {
                  return (opt.label = this.localdata[i].label);
                });
                selectOptions[hasIndex][this.optionsParam].push(this.localdata[i][this.optionsParam][j]);
              }
            }
          }
          this.selectOptions = utils.deepClone(selectOptions);
        } else {
          this.selectOptions = utils.deepClone(this.localdata);
        }
      } else {
        let options =
          this.moreStatus && this.searchValue ? utils.deepClone(this.initOptions) : utils.deepClone(this.localdata);
        let selectOptions = options.filter((o) => {
          let label = this.formatItemName(o);
          return label && label.toString().indexOf(this.searchValue) != -1;
        });
        this.selectOptions = utils.deepClone(selectOptions);
      }
    },
    focusSearch() {
      this.focus = true;
    },
    inputSearchValue: debounce(function (res) {
      this.search();
    }, 300),
    clearSearchValue(res) {
      this.search();
    },
    cancelSearchValue(res) {
      this.searchValue = "";
      this.search();
      this.focus = false;
    },
    // 全选切换
    checkAllChange() {
      let value = utils.deepClone(this.valueCom || []);
      if (this.checkAll) {
        // 当前全选，点击应该取消全选
        for (let i = 0, len = this.selectOptions.length; i < len; i++) {
          if (this.componentType == "select-group") {
            for (let j = 0, jlen = this.selectOptions[i][this.optionsParam].length; j < jlen; j++) {
              let index = value.indexOf(this.selectOptions[i][this.optionsParam][j][this.valueParam]);
              if (index > -1) {
                // 存在则移除
                value.splice(index, 1);
              }
            }
          } else {
            let index = value.indexOf(this.selectOptions[i][this.valueParam]);
            if (index > -1) {
              // 存在则移除
              value.splice(index, 1);
            }
          }
        }
      } else {
        // 当前非全选，点击应该全选
        if (this.componentType == "select-group") {
          for (let i = 0, len = this.selectOptions.length; i < len; i++) {
            for (let j = 0, jlen = this.selectOptions[i][this.optionsParam].length; j < jlen; j++) {
              let index = value.indexOf(this.selectOptions[i][this.optionsParam][j][this.valueParam]);
              if (index == -1) {
                // 不存在就添加
                value.push(this.selectOptions[i][this.optionsParam][j][this.valueParam]);
              }
            }
          }
        } else {
          for (let i = 0, len = this.selectOptions.length; i < len; i++) {
            let index = value.indexOf(this.selectOptions[i][this.valueParam]);
            if (index == -1) {
              // 不存在就添加
              value.push(this.selectOptions[i][this.valueParam]);
            }
          }
        }
      }
      this.$emit("checkAllChange", value);
    },
    // 选中判断
    checkJudge(value) {
      if (this.valueCom && typeof this.valueCom == "object") {
        return this.valueCom.indexOf(value) > -1;
      }
      return value === this.valueCom;
    },
    // 全选按钮判断
    checkAllJudge() {
      let checkAll = false;
      if (this.componentType == "select-group") {
        for (let i = 0, len = this.selectOptions.length; i < len; i++) {
          let options = this.selectOptions[i][this.optionsParam];
          if (options) {
            checkAll = every(options, (item) => {
              return this.valueCom.indexOf(item[this.valueParam]) > -1;
            });
          }
        }
        this.checkAll = checkAll;
      } else {
        if (this.selectOptions.length) {
          this.checkAll = every(this.selectOptions, (item) => {
            return this.valueCom.indexOf(item[this.valueParam]) > -1;
          });
        }
      }
    },
    onScrolltolower() {
      if (this.searchValue) {
        return false;
      }
      this.$emit("scrolltolower");
    },
    checkFilterHandler(itemVal) {
      return this.valueCom.indexOf(itemVal) > -1 ? itemVal : "";
    },
  },
};
</script>

<style lang="scss">
$uni-base-color: var(--w-text-color-base);
$uni-main-color: var(--w-text-color-mobile);
$uni-secondary-color: var(--w-text-color-light);
$uni-border-3: var(--w-border-color-mobile);

.uni-select__w-selector-content {
  .uni-select__selector {
    /* #ifndef APP-NVUE */
    box-sizing: border-box;
    /* #endif */
    position: absolute;
    left: 0;
    width: 100%;
    background-color: #ffffff;
    border: 1px solid $uni-border-3;
    border-radius: 6px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    z-index: 3;
    padding: 4px 0;
  }

  .uni-select__selector-scroll {
    /* #ifndef APP-NVUE */
    max-height: 300px;
    box-sizing: border-box;
    /* #endif */
  }

  /* #ifdef H5 */
  @media (min-width: 768px) {
    .uni-select__selector-scroll {
      max-height: 600px;
    }
  }

  /* #endif */

  .uni-select__selector-empty,
  .uni-select__selector-item {
    /* #ifndef APP-NVUE */
    display: flex;
    cursor: pointer;
    align-items: center;
    /* #endif */
    line-height: 24px;
    font-size: var(--w-font-size-lg);
    text-align: left;
    border-bottom: solid 1px $uni-border-3;
    padding: 8px 10px;
    color: var(--w-text-color-mobile);

    // &.uni-select__selector-item-selected:not(.multiple) {
    //   background-color: #fafafa;
    // }
    // &.current-item {
    //   background-color: #f9f9f9;
    // }
    &.uni-select__selector-item-selected {
      color: var(--w-primary-color);
      font-size: var(--w-font-size-lg);
      font-weight: bold;
    }

    ::v-deep .uni-data-checklist {
      flex: 0;

      uni-radio-group uni-label,
      uni-checkbox-group uni-label {
        padding-right: 0;
      }
      .checklist-group .checklist-box {
        margin: 4px 0;
      }
    }
  }

  // .uni-select__selector-item:hover {
  //   background-color: #f9f9f9;
  // }

  .uni-select__selector-empty:last-child,
  .uni-select__selector-item:last-child {
    /* #ifndef APP-NVUE */
    border-bottom: none;
    /* #endif */
  }

  .uni-select__selector-item-text-view {
    .uni-select__selector-text {
      word-break: break-all;
      text-align: left;
      display: -webkit-box;
      overflow: hidden;
      white-space: normal !important;
      text-overflow: ellipsis;
      word-wrap: break-word;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
    }
  }

  .uni-select__selector__disabled {
    opacity: 0.4;
    cursor: default;
  }

  .uni-select__selector-operate-bar {
    padding: 10px;
    .uni-searchbar {
      padding: 0;
    }
    .uni-searchbar + .uni-select__selector-checkall {
      padding-left: 10px;
    }
    .checkall-btn {
      line-height: 2;
      vertical-align: middle;
    }

    & + .uni-select__selector-scroll {
      // padding-top: 56px;
    }
    & + .uni-select__selector-operate-bar {
      padding-top: 0;
    }
  }
}
.popup-content {
  .uni-select__w-selector-content {
    .uni-select__selector-item-text-view {
      display: flex;
      justify-content: var(--uni-select-selector-item-text-view-justify-content);
      width: 100%;
      .uni-select__selector-text {
        word-break: break-all;
        text-align: center;
      }
    }
  }
  .uni-select__selector-empty,
  .uni-select__selector-item {
    line-height: 20px;
    padding: 12px 10px;
  }
}
</style>
