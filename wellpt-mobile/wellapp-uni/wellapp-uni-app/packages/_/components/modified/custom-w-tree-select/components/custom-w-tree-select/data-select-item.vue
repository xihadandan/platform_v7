<template>
  <view
    class="custom-tree-select-content"
    :class="{
      'show-line': showLine && node[dataChildren] && node[dataChildren].length && node.showChildren,
    }"
    :style="{ marginLeft: `${level ? 14 : 0}px` }"
  >
    <view
      v-if="node.visible"
      class="custom-tree-select-item"
      :class="{ 'current-item': node[dataValue] == currentNode[dataValue] }"
    >
      <view class="item-content">
        <view class="left">
          <view
            v-if="node[dataChildren] && node[dataChildren].length"
            :class="['right-icon', { active: node.showChildren }]"
            @click.stop="nameClick(node)"
          >
            <uni-icons type="forward" size="14" color="#333"></uni-icons>
          </view>
          <view v-if="loadingArr.includes(node[dataValue])" class="loading-icon-box">
            <uni-icons class="loading-icon" type="spinner-cycle" size="14" color="#333"></uni-icons>
          </view>
          <slot name="nodeLeft" :node="node"></slot>
          <view class="name" :style="node.disabled ? 'color: #999' : ''" @click.stop="nodeClick(node)">
            <slot name="nodeLable" :node="node">{{ node[dataLabel] }}</slot>
          </view>
          <slot name="nodeRight" :item="node" :sindex="index" :selectedOptions="parentNodes"></slot>
        </view>
        <view
          v-if="
            choseParent ||
            (!choseParent && !node[dataChildren]) ||
            (!choseParent && node[dataChildren] && !node[dataChildren].length)
          "
          :class="['check-box', { disabled: node.disabled }, { 'radio-box': !mutiple }, { checked: node.checked }]"
          @click.stop="nodeClick(node)"
        >
          <view v-if="!node.checked && node.partChecked && linkage" class="part-checked"></view>
          <w-icon
            v-if="node.checked"
            :icon="mutiple ? 'iconfont icon-ptkj-duoxuan-xuanzhong' : 'iconfont icon-ptkj-danxuan-xuanzhong'"
            :size="16"
            :color="node.disabled ? '#333' : 'var(--w-primary-color)'"
          >
          </w-icon>
        </view>
      </view>
    </view>
    <view v-if="node.showChildren && node[dataChildren] && node[dataChildren].length">
      <data-select-item
        v-for="(ditem, dindex) in listData"
        :key="ditem[dataValue]"
        :node="ditem"
        :dataLabel="dataLabel"
        :dataValue="dataValue"
        :dataChildren="dataChildren"
        :choseParent="choseParent"
        :showLine="showLine"
        :linkage="linkage"
        :level="level + 1"
        :load="load"
        :lazyLoadChildren="lazyLoadChildren"
        :mutiple="mutiple"
        :parentNodes="listData"
        :currentNode="currentNode"
        :index="dindex"
      >
        <template v-slot:nodeLeft="{ node }">
          <slot name="nodeLeft" :node="node"></slot>
        </template>
        <template v-slot:nodeLable="{ node }">
          <slot name="nodeLable" :node="node"></slot>
        </template>
        <template v-slot:nodeRight="{ item, sindex, selectedOptions }">
          <slot name="nodeRight" :item="item" :sindex="sindex" :selectedOptions="selectedOptions"></slot>
        </template>
      </data-select-item>
    </view>
  </view>
</template>

<script>
import dataSelectItem from "./data-select-item.vue";
import { paging } from "./utils";
export default {
  name: "data-select-item",
  components: {
    "data-select-item": dataSelectItem,
  },
  props: {
    node: {
      type: Object,
      default: () => ({}),
    },
    choseParent: {
      type: Boolean,
      default: true,
    },
    dataLabel: {
      type: String,
      default: "name",
    },
    dataValue: {
      type: String,
      default: "value",
    },
    dataChildren: {
      type: String,
      default: "children",
    },
    // 带连接线的树
    showLine: {
      type: Boolean,
      default: false,
    },
    linkage: {
      type: Boolean,
      default: false,
    },
    level: {
      type: Number,
      default: 0,
    },
    load: {
      type: Function,
      default: function () {},
    },
    lazyLoadChildren: {
      type: Boolean,
      default: false,
    },
    mutiple: {
      type: Boolean,
      default: false,
    },
    parentNodes: {
      type: Array,
      default: () => [],
    },
    index: {
      type: Number,
      default: 0,
    },
    // 当前选中的节点
    currentNode: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    let nodeLableSlots = this.$slots.nodeLable != undefined;
    return {
      listData: [],
      clearTimerList: [],
      loadingArr: [],
      nodeLableSlots,
    };
  },
  computed: {
    watchData() {
      const { node, dataChildren } = this;

      return {
        node,
        dataChildren,
      };
    },
  },
  watch: {
    watchData: {
      immediate: true,
      handler(newVal) {
        const { node, dataChildren } = newVal;
        if (node.showChildren && node[dataChildren] && node[dataChildren].length) {
          this.resetClearTimerList();
          this.renderTree(node[dataChildren]);
        }
      },
    },
  },
  methods: {
    // 懒加载
    renderTree(arr) {
      const pagingArr = paging(arr);
      this.listData.splice(0, this.listData.length, ...((pagingArr && pagingArr[0]) || []));
      this.lazyRenderList(pagingArr, 1);
    },
    // 懒加载具体逻辑
    lazyRenderList(arr, startIndex) {
      for (let i = startIndex; i < arr.length; i++) {
        let timer = null;
        timer = setTimeout(() => {
          this.listData.push(...arr[i]);
        }, i * 500);
        this.clearTimerList.push(() => clearTimeout(timer));
      }
    },
    // 中断懒加载
    resetClearTimerList() {
      const list = [...this.clearTimerList];
      this.clearTimerList.splice(0, this.clearTimerList.length);
      list.forEach((item) => item());
    },
    async nameClick(node) {
      if (node[this.dataChildren] && !node[this.dataChildren].length && this.lazyLoadChildren) {
        this.loadingArr.push(node[this.dataValue]);
        try {
          const res = await this.load(node);
          if (Array.isArray(res)) {
            uni.$emit("custom-tree-select-load", {
              source: node,
              target: res,
            });
          }
        } finally {
          this.loadingArr = [];
        }
      } else {
        if (!node.showChildren && node[this.dataChildren] && node[this.dataChildren].length) {
          // 打开
          this.renderTree(node[this.dataChildren]);
        } else {
          // 关闭
          this.resetClearTimerList();
          this.listData.splice(0, this.listData.length);
        }
        uni.$emit("custom-tree-select-name-click", node);
      }
    },
    nodeClick(node) {
      if (!node.disabled) {
        uni.$emit("custom-tree-select-node-click", node);
      }
    },
  },
  options: {
    styleIsolation: "shared",
  },
};
</script>

<style lang="scss" scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

$primary-color: var(--w-primary-color);
$col-sm: 4px;
$col-base: 8px;
$col-lg: 12px;
$row-sm: 5px;
$row-base: 10px;
$row-lg: 15px;
$radius-sm: 3px;
$radius-base: 6px;
$border-color: var(--w-border-color-mobile);
$line-height: 32px;

.custom-tree-select-content {
  position: relative;

  &.border {
    border-left: 1px solid $border-color;
  }

  &.show-line {
    &::before {
      content: "";
      position: absolute;
      width: 1px;
      height: calc(100% - $line-height);
      background-color: #c8c7cc;
      left: 7px;
      top: $line-height;
    }
  }

  ::v-deep .uni-checkbox-input {
    margin: 0 !important;
  }

  .custom-tree-select-item {
    &.current-item {
      background-color: #f9f9f9;
    }
  }

  .item-content {
    // margin: 0 0 $col-lg;
    padding: $col-base 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;

    &::after {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      bottom: 0;
      width: 3px;
      background-color: #fff;
      transform: translateX(-2px);
      z-index: 1;
    }

    .left {
      flex: 1;
      display: flex;
      align-items: center;

      .right-icon {
        transition: 0.15s ease;

        &.active {
          transform: rotate(90deg);
        }
      }

      .smallcircle-filled {
        width: 14px;
        height: 13.6px;
        display: flex;
        align-items: center;

        .smallcircle-filled-icon {
          transform-origin: center;
          transform: scale(0.55);
        }
      }

      .loading-icon-box {
        margin-right: $row-sm;
        width: 14px;
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;

        .loading-icon {
          transform-origin: center;
          animation: rotating infinite 0.2s ease;
        }
      }

      .name {
        flex: 1;
        padding: 0px 5px;
        display: -webkit-box;
        overflow: hidden;
        white-space: normal !important;
        text-overflow: ellipsis;
        word-wrap: break-word;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
        word-break: break-all;
        font-size: 16px;
      }
    }
  }
}

.check-box {
  width: 16px;
  height: 16px;
  border: 1px solid var(--w-border-color-darker);
  border-radius: $radius-sm;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;

  &.disabled {
    background-color: rgb(225, 225, 225);
  }

  &.radio-box {
    border-radius: 50%;
    &.checked:not(.disabled) {
      border: 1px solid transparent;
    }
  }

  .part-checked {
    width: 60%;
    height: 2px;
    background-color: $primary-color;
  }
  .uniui-checkbox-input-checked {
    &::before {
      font: normal normal normal 14px / 1 uni;
      content: "\EA08";
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -48%) scale(0.73);
      -webkit-transform: translate(-50%, -48%) scale(0.73);
    }
  }
}

@keyframes rotating {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
