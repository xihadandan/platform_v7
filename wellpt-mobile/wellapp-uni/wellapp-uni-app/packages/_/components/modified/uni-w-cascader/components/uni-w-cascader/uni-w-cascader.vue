<template>
  <view class="uni-w-cascader">
    <view class="uni-w-cascader-input" :class="{ readonly: readonly }" @click="handleInput">
      <slot :options="options" :data="inputSelected" :error="errorMessage">
        <view class="input-value" :class="{ 'input-value-border': border }">
          <text v-if="errorMessage" class="selected-area error-text">{{ errorMessage }}</text>
          <view v-else-if="loading && !isOpened" class="selected-area">
            <uni-load-more class="load-more" :contentText="loadMore" status="loading"></uni-load-more>
          </view>
          <scroll-view v-else-if="inputSelected.length" class="selected-area" scroll-x="true">
            <view class="selected-list">
              <view class="selected-item" v-for="(item, index) in inputSelected" :key="index">
                <text class="text-color">{{ item.text }}</text
                ><text v-if="index < inputSelected.length - 1" class="input-split-line">{{ split }}</text>
              </view>
            </view>
          </scroll-view>
          <text v-else class="selected-area placeholder">{{ placeholder || $t("global.pleaseSelect", "请选择") }}</text>
          <view v-if="clearIcon && !readonly && inputSelected.length" class="icon-clear" @click.stop="clear">
            <uni-icons type="clear" color="var(--text-color-clear)" size="16" />
          </view>
          <view class="arrow-area" v-if="(!clearIcon || !inputSelected.length) && !readonly">
            <uni-icons type="right" size="16" color="var(--text-color-arrow)" />
          </view>
        </view>
      </slot>
    </view>
    <view class="uni-w-cascader-cover" v-if="isOpened" @click="handleClose"></view>
    <view class="uni-w-cascader-dialog" v-if="isOpened">
      <view class="uni-popper__arrow"></view>
      <view class="dialog-caption">
        <view class="title-area">
          <text class="dialog-title">{{ popupTitle || $t("global.pleaseSelect", "请选择") }}</text>
        </view>
        <view class="dialog-close">
          <uni-w-button type="text" @click="handleClose" icon="iconfont icon-ptkj-dacha-xiao"></uni-w-button>
        </view>
      </view>
      <view v-if="showSearch" class="search-bar">
        <uni-search-bar
          :placeholder="$t('global.searchplaceholder', '请输入搜索内容')"
          :cancelText="$t('global.cancel', '取消')"
          @confirm="search"
          v-model="searchValue"
          @input="inputSearchValue"
          @cancel="cancelSearchValue"
          @clear="clearSearchValue"
        >
        </uni-search-bar>
      </view>
      <uniDataSearchPickerview
        v-if="searchBarFocus"
        class="picker-view"
        ref="searchPickerView"
        :data="searchDataList"
        :loading="searchLoading"
        @change="onchange"
      />
      <data-picker-view
        v-show="!searchBarFocus"
        class="picker-view"
        ref="pickerView"
        v-model="dataValue"
        :localdata="localdata"
        :preload="preload"
        :collection="collection"
        :field="field"
        :orderby="orderby"
        :where="where"
        :step-searh="stepSearh"
        :self-field="selfField"
        :parent-field="parentField"
        :managed-mode="true"
        :map="map"
        :ellipsis="ellipsis"
        :changeOnSelect="changeOnSelect"
        @change="onchange"
        @datachange="ondatachange"
        @nodeclick="onnodeclick"
      >
      </data-picker-view>
    </view>
  </view>
</template>

<script>
import dataPicker from "./uni-data-pickerview/uni-data-picker.js";
import DataPickerView from "./uni-data-pickerview/uni-data-pickerview.vue";
import uniDataSearchPickerview from "./uni-data-pickerview/uni-data-search-pickerview.vue";
import { debounce } from "lodash";
import { utils } from "wellapp-uni-framework";

/**
 * DataPicker 级联选择
 * @description 支持单列、和多列级联选择。列数没有限制，如果屏幕显示不全，顶部tab区域会左右滚动。
 * @tutorial https://ext.dcloud.net.cn/plugin?id=3796
 * @property {String} popup-title 弹出窗口标题
 * @property {Array} localdata 本地数据，参考
 * @property {Boolean} border = [true|false] 是否有边框
 * @property {Boolean} readonly = [true|false] 是否仅读
 * @property {Boolean} preload = [true|false] 是否预加载数据
 * @value true 开启预加载数据，点击弹出窗口后显示已加载数据
 * @value false 关闭预加载数据，点击弹出窗口后开始加载数据
 * @property {Boolean} step-searh = [true|false] 是否分布查询
 * @value true 启用分布查询，仅查询当前选中节点
 * @value false 关闭分布查询，一次查询出所有数据
 * @property {String|DBFieldString} self-field 分布查询当前字段名称
 * @property {String|DBFieldString} parent-field 分布查询父字段名称
 * @property {String|DBCollectionString} collection 表名
 * @property {String|DBFieldString} field 查询字段，多个字段用 `,` 分割
 * @property {String} orderby 排序字段及正序倒叙设置
 * @property {String|JQLString} where 查询条件
 * @event {Function} popupshow 弹出的选择窗口打开时触发此事件
 * @event {Function} popuphide 弹出的选择窗口关闭时触发此事件
 */
export default {
  name: "UniWCascader",
  emits: ["popupopen", "popupopened", "popupclosed", "nodeclick", "input", "change", "update:modelValue", "inputclick"],
  mixins: [dataPicker],
  components: {
    DataPickerView,
    uniDataSearchPickerview,
  },
  props: {
    options: {
      type: [Object, Array],
      default() {
        return {};
      },
    },
    popupTitle: {
      type: String,
      default: () => {
        return this.$t("global.pleaseSelect", "请选择");
      },
    },
    placeholder: {
      type: String,
      default: () => {
        return this.$t("global.pleaseSelect", "请选择");
      },
    },
    heightMobile: {
      type: String,
      default: "",
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    clearIcon: {
      type: Boolean,
      default: true,
    },
    border: {
      type: Boolean,
      default: false,
    },
    split: {
      type: String,
      default: "/",
    },
    ellipsis: {
      type: Boolean,
      default: true,
    },
    showSearch: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      isOpened: false,
      inputSelected: [],
      searchValue: "",
      searchDataList: [],
      searchLoading: false,
      searchBarFocus: false,
    };
  },
  created() {
    this.$nextTick(() => {
      this.load();
    });
  },
  watch: {
    localdata: {
      handler() {
        this.load();
      },
      deep: true,
    },
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },

    clear() {
      this._dispatchEvent([]);
    },
    onPropsChange() {
      this._treeData = [];
      this.selectedIndex = 0;

      this.load();
    },
    load() {
      if (this.readonly) {
        this._processReadonly(this.localdata, this.dataValue);
        return;
      }

      // 回显本地数据
      if (this.isLocalData) {
        this.loadData();
        this.inputSelected = this.selected.slice(0);
      } else if (this.isCloudDataList || this.isCloudDataTree) {
        // 回显 Cloud 数据
        this.loading = true;
        this.getCloudDataValue()
          .then((res) => {
            this.loading = false;
            this.inputSelected = res;
          })
          .catch((err) => {
            this.loading = false;
            this.errorMessage = err;
          });
      }
    },
    show() {
      this.$emit("popupopen");
      this.isOpened = true;
      this.searchDataList = [];
      this.searchValue = "";
      this.searchBarFocus = false;
      setTimeout(() => {
        this.$refs.pickerView.updateData({
          treeData: this._treeData,
          selected: this.selected,
          selectedIndex: this.selectedIndex,
        });
      }, 200);
      this.$emit("popupopened");
    },
    hide() {
      this.isOpened = false;
      this.$emit("popupclosed");
    },
    handleInput() {
      if (this.readonly) {
        this.$emit("inputclick");
        return;
      }
      this.show();
    },
    handleClose(e) {
      this.hide();
    },
    onnodeclick(e) {
      this.$emit("nodeclick", e);
    },
    ondatachange(e) {
      this._treeData = this.$refs.pickerView._treeData;
    },
    onchange(e, isParent) {
      this.selectIsParent = !!isParent;
      let selected = [];
      if (Array.isArray(e)) {
        e.forEach((item) => {
          if (item.value && item.hasOwnProperty("data")) {
            selected.push(item);
          }
        });
      } else {
        selected = e;
      }
      if (!isParent) {
        this.hide();
      }
      this.$nextTick(() => {
        this.inputSelected = selected;
      });
      this._dispatchEvent(selected);
    },
    _processReadonly(dataList, value) {
      var isTree = dataList.findIndex((item) => {
        return item.children;
      });
      if (isTree > -1) {
        let inputValue;
        if (Array.isArray(value)) {
          inputValue = value[value.length - 1];
          if (typeof inputValue === "object" && inputValue.value) {
            inputValue = inputValue.value;
          }
        } else {
          inputValue = value;
        }
        this.inputSelected = this._findNodePath(inputValue, this.localdata);
        return;
      }

      if (!this.hasValue) {
        this.inputSelected = [];
        return;
      }

      let result = [];
      for (let i = 0; i < value.length; i++) {
        var val = value[i];
        var item = dataList.find((v) => {
          return v.value == val;
        });
        if (item) {
          result.push(item);
        }
      }
      if (result.length) {
        this.inputSelected = result;
      }
    },
    _filterForArray(data, valueArray) {
      var result = [];
      for (let i = 0; i < valueArray.length; i++) {
        var value = valueArray[i];
        var found = data.find((item) => {
          return item.value == value;
        });
        if (found) {
          result.push(found);
        }
      }
      return result;
    },
    _dispatchEvent(selected) {
      let item = {};
      let value = new Array(selected.length);
      if (selected.length) {
        for (var i = 0; i < selected.length; i++) {
          value[i] = selected[i].value;
        }
        item = selected[selected.length - 1];
      } else {
        item.value = "";
      }
      if (this.formItem) {
        this.formItem.setValue(item.value);
      }

      this.$emit("input", value);
      this.$emit("update:modelValue", value);
      this.$emit(
        "change",
        selected.map(function (item) {
          return item.value;
        }),
        selected.map(function (item) {
          return item.data;
        })
      );
    },
    // 搜索框
    search() {
      this.searchLoading = true;
      if (this.searchValue) {
        this.searchDataList.splice(0, this.searchDataList.length);
        let toSearch = (item, index, valueArray = [], textArray = [], selectedArray = []) => {
          if (valueArray.length >= index) {
            valueArray.splice(index - 1, valueArray.length);
            selectedArray.splice(index - 1, selectedArray.length);
            textArray.splice(index - 1, textArray.length);
          }
          valueArray.push(item[this.map.value]);
          selectedArray.push({
            text: item[this.map.text],
            value: item[this.map.value],
            data: utils.deepClone(item),
          });
          let inSearch = item[this.map.text] && item[this.map.text].indexOf(this.searchValue) > -1;
          if (inSearch) {
            let labelArr = item[this.map.text].split(this.searchValue);
            textArray.push(labelArr.join('<span class="search-text">' + this.searchValue + "</span>"));
          } else {
            textArray.push(item[this.map.text]);
          }
          if (textArray.join("").indexOf(this.searchValue) > -1) {
            // 显示值里有搜索内容
            if (this.changeOnSelect || (!this.changeOnSelect && item.isleaf)) {
              // 父级可选,只要匹配都可以加入节点；父级不可选，只有叶子节点可加入节点
              this.searchDataList.push({
                text: textArray.join(this.split),
                value: valueArray.slice(0),
                data: selectedArray.slice(0),
              });
            }
          }
          if (item.children && item.children.length) {
            let _index = index + 1;
            let _valueArray = valueArray.slice(0);
            let _textArray = textArray.slice(0);
            let _selectedArray = selectedArray.slice(0);
            item.children.forEach((citem) => {
              if (this.searchDataList.length <= 50) {
                toSearch(citem, _index, _valueArray, _textArray, _selectedArray);
              }
            });
          }
        };
        this.localdata.forEach((item) => {
          if (this.searchDataList.length <= 50) {
            toSearch(item, 1);
          }
        });
        this.searchLoading = false;
        this.searchBarFocus = true;
      } else {
        // uni.$ptToastShow({
        //   title: this.$t("global.searchplaceholder", "请输入搜索内容"),
        // });
        this.cancelSearchValue();
      }
    },
    inputSearchValue: debounce(function (res) {
      this.search();
    }, 300),
    clearSearchValue(res) {},
    cancelSearchValue() {
      this.searchValue = "";
      this.searchBarFocus = false;
      this.searchDataList.splice(0, this.searchDataList.length);
    },
  },
};
</script>

<style lang="scss">
.uni-w-cascader {
  flex: 1;
  position: relative;
  font-size: 14px;

  .error-text {
    color: var(--w-danger-color);
  }

  .input-value {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    align-items: center;
    flex-wrap: nowrap;
    font-size: 14px;
    /* line-height: 35px; */
    overflow: hidden;
    height: 35px;
    /* #ifndef APP-NVUE */
    box-sizing: border-box;
    /* #endif */

    .icon-clear,
    .arrow-area {
      padding-left: 5px;
    }
  }

  .input-value-border {
    border: 1px solid var(--w-border-color-mobile);
    border-radius: 4px;
    padding: 0 var(--w-padding-2xs);
    .selected-area {
      justify-content: flex-start !important;
    }
  }

  .selected-area {
    flex: 1;
    overflow: hidden;
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
  }

  .load-more {
    /* #ifndef APP-NVUE */
    margin-right: auto;
    /* #endif */
    /* #ifdef APP-NVUE */
    width: 40px;
    /* #endif */
  }

  .selected-list {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    flex-wrap: nowrap;
    /* padding: 0 5px; */
  }

  .selected-item {
    flex-direction: row;
    /* padding: 0 1px; */
    /* #ifndef APP-NVUE */
    white-space: nowrap;
    /* #endif */
  }

  .text-color {
    color: var(--w-text-color-mobile);
  }

  .placeholder {
    color: var(--text-color-placeholder);
    font-size: var(--w-font-size-base);
  }

  .input-split-line {
    opacity: 0.5;
  }

  .arrow-area {
    position: relative;
    width: 20px;
    /* #ifndef APP-NVUE */
    margin-left: auto;
    display: flex;
    /* #endif */
    justify-content: center;
  }

  .uni-w-cascader-cover {
    position: fixed;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.4);
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: column;
    z-index: 100;
  }

  .uni-w-cascader-dialog {
    position: fixed;
    left: 0;
    /* #ifndef APP-NVUE */
    top: 20%;
    /* #endif */
    /* #ifdef APP-NVUE */
    top: 200px;
    /* #endif */
    right: 0;
    bottom: 0;
    background-color: #ffffff;
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: column;
    z-index: 102;
    overflow: hidden;
    /* #ifdef APP-NVUE */
    width: 750rpx;
    /* #endif */
  }

  .dialog-caption {
    position: relative;
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    /* border-bottom: 1px solid #f0f0f0; */
  }

  .title-area {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    align-items: center;
    /* #ifndef APP-NVUE */
    margin: auto;
    /* #endif */
    padding: 0 10px;
  }

  .dialog-title {
    /* font-weight: bold; */
    line-height: 44px;
    font-size: var(--w-font-size-lg);
    color: var(--w-text-color-mobile);
    font-weight: bold;
  }

  .dialog-close {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    align-items: center;
    padding: 0 10px;
  }

  .picker-view {
    flex: 1;
    overflow: hidden;
  }

  .icon-clear {
    display: flex;
    align-items: center;
  }

  /* #ifdef H5 */
  @media all and (min-width: 768px) {
    .uni-w-cascader-cover {
      background-color: transparent;
    }

    .uni-w-cascader-dialog {
      position: absolute;
      top: 55px;
      height: auto;
      min-height: 400px;
      max-height: 50vh;
      background-color: #fff;
      border: 1px solid var(--w-border-color-mobile);
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      border-radius: 4px;
      overflow: unset;
    }

    .dialog-caption {
      display: none;
    }

    .icon-clear {
      /* margin-right: 5px; */
    }
  }

  /* #endif */

  /* picker 弹出层通用的指示小三角, todo：扩展至上下左右方向定位 */
  /* #ifndef APP-NVUE */
  .uni-popper__arrow,
  .uni-popper__arrow::after {
    position: absolute;
    display: block;
    width: 0;
    height: 0;
    border-color: transparent;
    border-style: solid;
    border-width: 6px;
  }

  .uni-popper__arrow {
    filter: drop-shadow(0 2px 12px rgba(0, 0, 0, 0.03));
    top: -6px;
    left: 10%;
    margin-right: 3px;
    border-top-width: 0;
    border-bottom-color: var(--w-border-color-mobile);
  }

  .uni-popper__arrow::after {
    content: " ";
    top: 1px;
    margin-left: -6px;
    border-top-width: 0;
    border-bottom-color: #fff;
  }

  /* #endif */
}
</style>
