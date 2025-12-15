<template>
  <!-- #ifdef H5 -->
  <td
    class="uni-table-td"
    :rowspan="rowspan"
    :colspan="colspan"
    :class="{ 'table--border': border, ellipsis: ellipsis }"
    :style="{ width: width + 'px', 'text-align': align }"
  >
    <slot></slot>
  </td>
  <!-- #endif -->
  <!-- #ifndef H5 -->
  <view
    class="uni-table-td"
    :class="{ 'table--border': border, ellipsis: ellipsis }"
    :style="{ width: width + 'px', 'text-align': align }"
  >
    <slot></slot>
  </view>
  <!-- #endif -->
</template>

<script>
/**
 * Td 单元格
 * @description 表格中的标准单元格组件
 * @tutorial https://ext.dcloud.net.cn/plugin?id=3270
 * @property {Number} 	align = [left|center|right]	单元格对齐方式
 */
export default {
  name: "uniWTd",
  options: {
    virtualHost: true,
  },
  props: {
    width: {
      type: [String, Number],
      default: "",
    },
    align: {
      type: String,
      default: "left",
    },
    rowspan: {
      type: [Number, String],
      default: 1,
    },
    colspan: {
      type: [Number, String],
      default: 1,
    },
    ellipsis: Boolean,
  },
  data() {
    return {
      border: false,
    };
  },
  created() {
    this.root = this.getTable();
    this.border = this.root.border;
  },
  methods: {
    /**
     * 获取父元素实例
     */
    getTable() {
      let parent = this.$parent;
      let parentName = parent.$options.name;
      while (parentName !== "uniWTable") {
        parent = parent.$parent;
        if (!parent) return false;
        parentName = parent.$options.name;
      }
      return parent;
    },
  },
};
</script>

<style lang="scss">
$border-color: #f2f2f2;

.uni-table-td {
  display: table-cell;
  padding: 8px 10px;
  font-size: var(--w-font-size-base);
  border-bottom: 1px $border-color solid;
  font-weight: 400;
  color: var(--w-text-color-mobile);
  line-height: 23px;
  box-sizing: border-box;

  &.ellipsis {
    word-break: break-all;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}

.table--border {
  border-right: 1px $border-color solid;
}
</style>
