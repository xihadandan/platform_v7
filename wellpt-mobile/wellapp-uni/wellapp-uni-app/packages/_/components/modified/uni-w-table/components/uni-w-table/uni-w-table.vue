<template>
  <view
    class="uni-table-scroll"
    :class="[
      border ? 'table--border' : '',
      !noData ? 'border-none' : 'noScrollX',
      isfixed ? 'fixed' : '',
      prefixClass,
      suffixClass,
    ]"
  >
    <!-- #ifdef H5 -->
    <table
      class="uni-table"
      border="0"
      cellpadding="0"
      cellspacing="0"
      :class="{ 'table--stripe': stripe }"
      :style="{ 'min-width': minWidth + 'px' }"
    >
      <slot></slot>
    </table>
    <!-- #endif -->
    <!-- #ifndef H5 -->
    <view class="uni-table" :style="{ 'min-width': minWidth + 'px' }" :class="{ 'table--stripe': stripe }">
      <slot></slot>
    </view>
    <!-- #endif -->
    <view v-if="noData" class="uni-table-loading">
      <view
        class="uni-table-text"
        :class="{ 'empty-border': border }"
        :style="{ width: tableWidth + 'px', left: scrollLeft + 'px' }"
      >
        {{ emptyText || $t("global.noData", "暂无数据") }}
      </view>
    </view>
    <view v-if="loading" class="uni-table-mask" :class="{ 'empty-border': border }">
      <view class="uni-table--loader"></view>
    </view>
  </view>
</template>

<script>
/**
 * Table 表格
 * @description 用于展示多条结构类似的数据
 * @tutorial https://ext.dcloud.net.cn/plugin?id=3270
 * @property {Boolean} 	border 				是否带有纵向边框
 * @property {Boolean} 	stripe 				是否显示斑马线
 * @property {Boolean} 	type 					是否开启多选
 * @property {String} 	emptyText 			空数据时显示的文本内容
 * @property {Boolean} 	loading 			显示加载中
 * @event {Function} 	selection-change 	开启多选时，当选择项发生变化时会触发该事件
 */
export default {
  name: "uniWTable",
  options: {
    virtualHost: true,
  },
  emits: ["selection-change"],
  props: {
    data: {
      type: Array,
      default() {
        return [];
      },
    },
    // 是否有竖线
    border: {
      type: Boolean,
      default: false,
    },
    // 是否显示斑马线
    stripe: {
      type: Boolean,
      default: false,
    },
    // 多选
    type: {
      type: String,
      default: "",
    },
    // 没有更多数据
    emptyText: {
      type: String,
      default: "",
    },
    loading: {
      type: Boolean,
      default: false,
    },
    rowKey: {
      type: String,
      default: "",
    },
    fixed: {
      type: Object,
    },
    height: {
      type: Number,
      default: 0,
    },
    // 初始选中值
    defaultSelectedRowKeys: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    let backKeysData = this.defaultSelectedRowKeys || [];
    return {
      noData: true,
      tableWidth: 500,
      minWidth: 0,
      scrollLeft: 0,
      multiTableHeads: [],
      // 定义tr的实例数组
      trChildren: [],
      thChildren: [],
      theadChildren: null,
      backData: [],
      backKeysData,
      backIndexData: [],
    };
  },
  computed: {
    // 固定前几列，最多选择和序号列
    prefixClass() {
      if (this.fixed && this.fixed.prefix) {
        let pre = [];
        for (let i = 1; i <= this.fixed.prefix; i++) {
          pre.push("prefix-" + i);
        }
        return pre.join(" ");
      }
      return "";
    },
    suffixClass() {
      if (this.fixed && this.fixed.suffix) {
        return "suffix-" + this.fixed.suffix;
      }
      return "";
    },
    isfixed() {
      // 表格最小宽度小于表格宽度，就不设置固定列
      if (this.minWidth < this.tableWidth) {
        return false;
      }
      return this.prefixClass || this.suffixClass;
    },
  },
  watch: {
    loading(val) {},
    data(newVal) {
      let theadChildren = this.theadChildren;
      let rowspan = 1;
      if (this.theadChildren) {
        rowspan = this.theadChildren.rowspan;
      }

      // this.trChildren.length - rowspan
      this.noData = false;
      // this.noData = newVal.length === 0
    },
  },
  created() {},
  mounted() {
    this.$nextTick(() => {
      this.getTableRectInfo();
    });
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    isNodata() {
      let theadChildren = this.theadChildren;
      let rowspan = 1;
      if (this.theadChildren) {
        rowspan = this.theadChildren.rowspan;
      }
      this.noData = this.trChildren.length - rowspan <= 0;
    },
    /**
     * 选中所有
     */
    selectionAll() {
      let startIndex = 1;
      let theadChildren = this.theadChildren;
      if (!this.theadChildren) {
        theadChildren = this.trChildren[0];
      } else {
        startIndex = theadChildren.rowspan; // - 1;
      }
      let isHaveData = this.data && this.data.length > 0;
      theadChildren.checked = true;
      theadChildren.indeterminate = false;
      this.trChildren.forEach((item, index) => {
        if (!item.disabled) {
          item.checked = true;
          if (isHaveData && item.keyValue) {
            const row = this.data.find((v) => v[this.rowKey] === item.keyValue);
            if (!this.backData.find((v) => v[this.rowKey] === row[this.rowKey])) {
              this.backData.push(row);
              this.backKeysData.push(row[this.rowKey]);
            }
          }
          if (index > startIndex - 1 && this.backIndexData.indexOf(index - startIndex) === -1) {
            this.backIndexData.push(index - startIndex);
          }
        }
      });
      // this.backData = JSON.parse(JSON.stringify(this.data))
      this.$emit("selection-change", {
        detail: {
          value: this.backData,
          index: this.backIndexData,
          keys: this.backKeysData,
        },
      });
    },
    /**
     * 用于多选表格，切换某一行的选中状态，如果使用了第二个参数，则是设置这一行选中与否（selected 为 true 则选中）
     */
    toggleRowSelection(row, selected, valueIgnore) {
      // if (!this.theadChildren) return
      row = [].concat(row);

      this.trChildren.forEach((item, index) => {
        // if (item.keyValue) {

        const select = row.findIndex((v) => {
          //
          if (typeof v === "number") {
            return v === index - 1;
          } else {
            return v[this.rowKey] === item.keyValue;
          }
        });
        let ischeck = item.checked;
        if (select !== -1) {
          if (typeof selected === "boolean") {
            item.checked = selected;
          } else {
            item.checked = !item.checked;
          }
          if (ischeck !== item.checked) {
            this.check({
              child: item.rowData || item,
              check: item.checked,
              keyValue: item.rowData ? item.keyValue : null,
              emit: true,
              valueIgnore,
            });
          }
        } else {
          item.checked = false;
          if (ischeck !== item.checked) {
            this.check({
              child: item.rowData || item,
              check: item.checked,
              keyValue: item.rowData ? item.keyValue : null,
              emit: true,
              valueIgnore,
            });
          }
        }
        // }
      });
      this.$emit("selection-change", {
        detail: {
          value: this.backData,
          index: this.backIndexData,
          keys: this.backKeysData,
        },
      });
    },

    /**
     * 用于多选表格，清空用户的选择
     */
    clearSelection() {
      let theadChildren = this.theadChildren;
      if (!this.theadChildren) {
        theadChildren = this.trChildren[0];
      }
      // if (!this.theadChildren) return
      theadChildren.checked = false;
      theadChildren.indeterminate = false;
      this.trChildren.forEach((item) => {
        // if (item.keyValue) {
        item.checked = false;
        // }
        if (item.keyValue) {
          const index = this.backData.findIndex((v) => v[this.rowKey] === item.keyValue);
          this.backData.splice(index, 1);
          this.backKeysData.splice(index, 1);
        }
      });
      this.backIndexData = [];
      this.$emit("selection-change", {
        detail: {
          value: this.backData,
          index: this.backIndexData,
          keys: this.backKeysData,
        },
      });
    },
    /**
     * 用于多选表格，切换所有行的选中状态
     */
    toggleAllSelection() {
      let list = [];
      let startIndex = 1;
      let theadChildren = this.theadChildren;
      if (!this.theadChildren) {
        theadChildren = this.trChildren[0];
      } else {
        startIndex = theadChildren.rowspan - 1;
      }
      this.trChildren.forEach((item, index) => {
        if (!item.disabled) {
          if (index > startIndex - 1) {
            list.push(index - startIndex);
          }
        }
      });
      this.toggleRowSelection(list);
    },

    /**
     * 选中\取消选中
     * @param {Object} child
     * @param {Boolean} check
     * @param {String|Number} keyValue tr的keyValue值
     * @param {Boolean} emit 是否触发选中值变化事件
     * @param {Boolean} valueIgnore 忽略选中值计算
     * @param {Boolean} radio 单选
     */
    check(option) {
      if (option.radio) {
        // 单选
        if (!option.valueIgnore) {
          let childDomIndex = this.trChildren.findIndex((item, index) => option.child === item);
          if (childDomIndex < 0) {
            childDomIndex = this.data.findIndex((v) => v[this.rowKey] === option.keyValue) + 1;
          }
          this.backIndexData.forEach((item) => {
            if (this.trChildren[item + 1]) {
              this.trChildren[item + 1].checked = false;
            }
          });
          this.backData.splice(0, this.backData.length);
          this.backKeysData.splice(0, this.backKeysData.length);
          this.backIndexData.splice(0, this.backIndexData.length);
          if (option.check) {
            if (option.keyValue) {
              this.backData.push(option.child);
              this.backKeysData.push(option.child[this.rowKey]);
            }
            this.backIndexData.push(childDomIndex - 1);
          }
        }
      } else {
        let theadChildren = this.theadChildren;
        if (!this.theadChildren) {
          theadChildren = this.trChildren[0];
        }

        let childDomIndex = this.trChildren.findIndex((item, index) => option.child === item);
        if (childDomIndex < 0) {
          childDomIndex = this.data.findIndex((v) => v[this.rowKey] === option.keyValue) + 1;
        }
        const dataLen = this.trChildren.filter((v) => !v.disabled && v.keyValue).length;
        if (childDomIndex === 0) {
          // option.check ? this.selectionAll() : this.clearSelection();
          return;
        }

        if (!option.valueIgnore) {
          if (option.check) {
            if (option.keyValue) {
              this.backData.push(option.child);
              this.backKeysData.push(option.child[this.rowKey]);
            }
            this.backIndexData.push(childDomIndex - 1);
          } else {
            const index = this.backData.findIndex((v) => v[this.rowKey] === option.keyValue);
            const idx = this.backIndexData.findIndex((item) => item === childDomIndex - 1);
            if (option.keyValue) {
              this.backData.splice(index, 1);
              this.backKeysData.splice(index, 1);
            }
            this.backIndexData.splice(idx, 1);
          }
        }

        const domCheckAll = this.trChildren.find((item, index) => index > 0 && !item.checked && !item.disabled);
        if (!domCheckAll) {
          theadChildren.indeterminate = false;
          theadChildren.checked = true;
        } else {
          theadChildren.indeterminate = true;
          theadChildren.checked = false;
        }

        if (this.backIndexData.length === 0) {
          theadChildren.indeterminate = false;
        }
      }

      if (!option.emit) {
        this.$emit("selection-change", {
          detail: {
            value: this.backData,
            index: this.backIndexData,
            keys: this.backKeysData,
          },
        });
      }
    },
    /**
     * 更新tr的check值
     * 适用场景，切换分页后，显示数据行选中状态改变
     */
    updateTrCheck() {
      let list = [];
      let startIndex = 1;
      let theadChildren = this.theadChildren;
      if (!this.theadChildren) {
        theadChildren = this.trChildren[0];
      } else {
        startIndex = theadChildren.rowspan;
      }
      this.trChildren.forEach((item, index) => {
        if (!item.disabled) {
          if (index > startIndex - 1) {
            if (this.backKeysData.indexOf(item.keyValue) > -1) {
              list.push(index - startIndex);
            }
          }
        }
      });
      this.backIndexData = list;
      if (this.backIndexData.length === 0) {
        theadChildren.indeterminate = false;
      }
      this.toggleRowSelection(list, true, true);
    },
    defaultSelectedChange(init) {
      if (init) {
        this.backKeysData = this.defaultSelectedRowKeys || [];
      }
      if (this.backData.length !== this.backKeysData.length) {
        this.backData.splice(0, this.backData.length);
        this.backKeysData.forEach((item) => {
          let index = this.data.findIndex((d) => d[this.rowKey] == item);
          if (index > -1) {
            this.backData.push(this.data[index]);
          }
        });
      }
      this.$nextTick(() => {
        this.updateTrCheck();
      });
    },
    getTableRectInfo() {
      let tableLeft = 0;
      const views = uni.createSelectorQuery().in(this);
      views
        .select(".uni-table-scroll")
        .boundingClientRect((data) => {
          if (data) {
            this.tableWidth = data.width;
            tableLeft = data.left;
          }
        })
        .exec();
      views
        .select(".uni-table")
        .boundingClientRect((data) => {
          if (data) {
            this.scrollLeft = -data.left + tableLeft;
          }
        })
        .exec();
    },
  },
};
</script>

<style lang="scss" scoped>
$border-color: var(--w-border-color-mobile);

.uni-table-scroll {
  width: 100%;

  overflow-x: auto;
  &.noScrollX {
    overflow-x: hidden;
  }
  /* #ifndef APP-NVUE */
  /* #endif */

  &.fixed {
    ::v-deep .uni-table {
      table-layout: fixed;
      width: 100%; /* 固定宽度 */
      .uni-table-tr {
        .uni-table-td {
          background-color: #fff;
        }
        .uni-table-th {
          background-color: #fafafa;
        }
      }
    }

    // 固定序号和选中列，宽度各为50
    &.prefix-1 {
      ::v-deep .uni-table {
        .uni-table-tr {
          .uni-table-th:nth-child(1),
          .uni-table-td:nth-child(1) {
            position: sticky;
            left: 0; /* 首行永远固定在左侧 */
            z-index: 2;

            box-shadow: 2px 0 5px 0px rgba(0, 0, 0, 0.05);
          }
        }
      }
      &.prefix-2 {
        ::v-deep .uni-table {
          .uni-table-tr {
            .uni-table-th:nth-child(2),
            .uni-table-td:nth-child(2) {
              position: sticky;
              left: 50px; /* 首行永远固定在左侧 */
              z-index: 2;

              box-shadow: 2px 0 5px 0px rgba(0, 0, 0, 0.05);
            }
            .uni-table-th:nth-child(1),
            .uni-table-td:nth-child(1) {
              box-shadow: unset;
            }
          }
        }
      }
    }

    // 固定操作列
    &.suffix-1 {
      ::v-deep .uni-table {
        .uni-table-tr {
          .uni-table-th:last-child,
          .uni-table-td:last-child {
            position: sticky;
            right: 0; /* 首行永远固定在右侧 */
            z-index: 2;

            box-shadow: -2px 0 5px 0 rgba(0, 0, 0, 0.05);
          }
        }
      }
    }
  }
}

.uni-table {
  position: relative;
  width: 100%;
  border-radius: 5px;
  // box-shadow: 0px 0px 3px 1px rgba(0, 0, 0, 0.1);
  background-color: #fff;

  box-sizing: border-box;
  display: table;
  overflow-x: auto;
  ::v-deep .uni-table-tr:nth-child(n + 2) {
    &:hover {
      background-color: #f5f7fa;
    }
  }
  ::v-deep .uni-table-thead {
    .uni-table-tr {
      // background-color: #f5f7fa;
      &:hover {
        background-color: #fafafa;
      }
    }
  }
  /* #ifndef APP-NVUE */
  /* #endif */
}

.table--border {
  border: 1px $border-color solid;
  border-right: none;
}

.border-none {
  border-bottom: none;
  /* #ifndef APP-NVUE */
  /* #endif */
}

.table--stripe {
  ::v-deep .uni-table-tr:nth-child(2n + 3) {
    background-color: #fafafa;
  }
  /* #ifndef APP-NVUE */
  /* #endif */
}

/* 表格加载、无数据样式 */
.uni-table-loading {
  position: relative;
  /* #ifndef APP-NVUE */
  display: table-row;
  /* #endif */
  height: 50px;
  line-height: 50px;
  overflow: hidden;
  box-sizing: border-box;
}
.empty-border {
  border-right: 1px $border-color solid;
}
.uni-table-text {
  position: absolute;
  right: 0;
  left: 0;
  text-align: center;
  font-size: 14px;
  color: #999;
}

.uni-table-mask {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 99;
  /* #ifndef APP-NVUE */
  display: flex;
  margin: auto;
  transition: all 0.5s;
  /* #endif */
  justify-content: center;
  align-items: center;
}

.uni-table--loader {
  width: 30px;
  height: 30px;
  border: 2px solid #aaa;
  // border-bottom-color: transparent;
  border-radius: 50%;
  /* #ifndef APP-NVUE */
  animation: 2s uni-table--loader linear infinite;
  /* #endif */
  position: relative;
}

@keyframes uni-table--loader {
  0% {
    transform: rotate(360deg);
  }

  10% {
    border-left-color: transparent;
  }

  20% {
    border-bottom-color: transparent;
  }

  30% {
    border-right-color: transparent;
  }

  40% {
    border-top-color: transparent;
  }

  50% {
    transform: rotate(0deg);
  }

  60% {
    border-top-color: transparent;
  }

  70% {
    border-left-color: transparent;
  }

  80% {
    border-bottom-color: transparent;
  }

  90% {
    border-right-color: transparent;
  }

  100% {
    transform: rotate(-360deg);
  }
}
</style>
