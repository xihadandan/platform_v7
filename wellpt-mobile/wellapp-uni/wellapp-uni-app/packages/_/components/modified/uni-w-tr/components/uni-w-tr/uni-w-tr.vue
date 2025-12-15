<template>
  <!-- #ifdef H5 -->
  <tr class="uni-table-tr" :data-key="keyValue" @click="onClick">
    <td v-if="isSelection && ishead" class="checkbox uni-table-td" :class="{ 'tr-table--border': border }">
      <table-checkbox
        :checked="checked"
        :indeterminate="indeterminate"
        :disabled="disabled"
        :radio="this.selection === 'radio'"
        :style="{ 'justify-content': contentAlign }"
        @checkboxSelected="checkboxSelected"
      ></table-checkbox>
    </td>
    <slot></slot>
  </tr>
  <!-- #endif -->
  <!-- #ifndef H5 -->
  <view class="uni-table-tr" @click="onClick">
    <view v-if="isSelection && ishead" class="checkbox uni-table-td" :class="{ 'tr-table--border': border }">
      <table-checkbox
        :checked="checked"
        :indeterminate="indeterminate"
        :disabled="disabled"
        :radio="this.selection === 'radio'"
        :style="{ 'justify-content': contentAlign }"
        @checkboxSelected="checkboxSelected"
      ></table-checkbox>
    </view>
    <slot></slot>
  </view>
  <!-- #endif -->
</template>

<script>
import tableCheckbox from "./table-checkbox.vue";
/**
 * Tr 表格行组件
 * @description 表格行组件 仅包含 th,td 组件
 * @tutorial https://ext.dcloud.net.cn/plugin?id=
 */
export default {
  name: "uniWTr",
  components: {
    tableCheckbox,
  },
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
    keyValue: {
      type: [String, Number],
      default: "",
    },
  },
  options: {
    // #ifdef MP-TOUTIAO
    virtualHost: false,
    // #endif
    // #ifndef MP-TOUTIAO
    virtualHost: true,
    // #endif
  },
  data() {
    return {
      value: false,
      border: false,
      selection: false,
      widthThArr: [],
      ishead: true,
      checked: false,
      indeterminate: false,
    };
  },
  computed: {
    isSelection() {
      if (this.selection === "checkbox" || this.selection === "radio") {
        return true;
      }
      return false;
    },
    contentAlign() {
      let align = "left";
      switch (this.align) {
        case "left":
          align = "flex-start";
          break;
        case "center":
          align = "center";
          break;
        case "right":
          align = "flex-end";
          break;
      }
      return align;
    },
  },
  created() {
    this.root = this.getTable();
    this.head = this.getTable("uniWThead");
    if (this.head) {
      this.ishead = false;
      this.head.init(this);
    }
    this.border = this.root.border;
    this.selection = this.root.type;
    this.align = this.root.align || "left";
    this.root.trChildren.push(this);
    const rowData = this.root.data.find((v) => v[this.root.rowKey] === this.keyValue);
    if (rowData) {
      this.rowData = rowData;
    }
    this.root.isNodata();
  },
  mounted() {
    if (this.widthThArr.length > 0) {
      const selectionWidth = this.isSelection ? 50 : 0;
      this.root.minWidth = Number(this.widthThArr.reduce((a, b) => Number(a) + Number(b))) + selectionWidth;
    }
  },
  // #ifndef VUE3
  destroyed() {
    const index = this.root.trChildren.findIndex((i) => i === this);
    this.root.trChildren.splice(index, 1);
    this.root.isNodata();
  },
  // #endif
  // #ifdef VUE3
  unmounted() {
    const index = this.root.trChildren.findIndex((i) => i === this);
    this.root.trChildren.splice(index, 1);
    this.root.isNodata();
  },
  // #endif
  methods: {
    minWidthUpdate(width) {
      this.widthThArr.push(width);
      if (this.widthThArr.length > 0) {
        const selectionWidth = this.isSelection ? 50 : 0;
        this.root.minWidth = Number(this.widthThArr.reduce((a, b) => Number(a) + Number(b))) + selectionWidth;
      }
    },
    // 选中
    checkboxSelected(e) {
      let rootData = this.root.data.find((v) => v[this.root.rowKey] === this.keyValue);
      this.checked = e.checked;
      this.root.check({
        child: rootData || this,
        check: e.checked,
        keyValue: rootData ? this.keyValue : null,
        radio: this.selection === "radio",
      });
    },
    change(e) {
      this.root.trChildren.forEach((item) => {
        if (item === this) {
          this.root.check(this, e.detail.value.length > 0 ? true : false);
        }
      });
    },
    /**
     * 获取父元素实例
     */
    getTable(name = "uniWTable") {
      let parent = this.$parent;
      let parentName = parent.$options.name;
      while (parentName !== name) {
        parent = parent.$parent;
        if (!parent) return false;
        parentName = parent.$options.name;
      }
      return parent;
    },
    onClick(e) {
      this.$emit("click", e);
    },
  },
};
</script>

<style lang="scss">
$border-color: var(--w-border-color-mobile);
$uni-primary: var(--w-primary-color);

.uni-table-tr {
  /* #ifndef APP-NVUE */
  display: table-row;
  transition: all 0.3s;
  box-sizing: border-box;
  /* #endif */
}

.checkbox {
  padding: 0 8px;
  width: 26px;
  padding-left: 12px;
  /* #ifndef APP-NVUE */
  display: table-cell;
  vertical-align: middle;
  /* #endif */
  color: #333;
  font-weight: 500;
  border-bottom: 1px $border-color solid;
  font-size: 14px;
  // text-align: center;
}

.tr-table--border {
  border-right: 1px $border-color solid;
}

/* #ifndef APP-NVUE */
.uni-table-tr {
  ::v-deep .uni-table-th {
    &.table--border:last-child {
      // border-right: none;
    }
  }

  ::v-deep .uni-table-td {
    &.table--border:last-child {
      // border-right: none;
    }
  }
}

/* #endif */
</style>
