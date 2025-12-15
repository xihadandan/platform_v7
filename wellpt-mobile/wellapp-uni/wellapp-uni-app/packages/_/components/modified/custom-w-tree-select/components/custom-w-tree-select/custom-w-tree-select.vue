<template>
  <view class="custom-tree-select-content">
    <view
      :class="[
        'select-list',
        { disabled },
        { active: selectList.length, 'border-none': !bordered },
        mutiple ? 'mutiple' : '',
      ]"
      @click.stop="open"
    >
      <view class="left">
        <view v-if="selectList.length" :class="['select-items']">
          <view v-if="!mutiple">{{
            pathMode ? selectedListBaseinfo[0].path : selectedListBaseinfo[0][dataLabel]
          }}</view>
          <template v-else>
            <view class="select-item" v-for="item in selectedListBaseinfo" :key="item[dataValue]">
              <view class="name">
                <text>{{ pathMode ? item.path : item[dataLabel] }}</text>
              </view>
              <view v-if="!disabled && !item.disabled" class="close" @click.stop="removeSelectedItem(item)">
                <uni-icons type="closeempty" size="14" color="var(--w-text-color-light)"></uni-icons>
              </view>
            </view>
          </template>
        </view>
        <view v-else class="custom-tree-select-content__placeholder">
          <text>{{ placeholder }}</text>
        </view>
      </view>
      <view class="right" v-if="!disabled">
        <uni-icons
          v-if="!selectList.length || !clearable"
          type="right"
          size="16"
          color="var(--text-color-arrow)"
        ></uni-icons>
        <uni-icons
          v-if="selectList.length && clearable"
          type="clear"
          size="16"
          color="var(--text-color-clear)"
          @click.native.stop="clear"
        ></uni-icons>
      </view>
    </view>
    <uni-popup
      v-if="showPopup"
      ref="popup"
      :animation="animation"
      :is-mask-click="isMaskClick"
      :mask-background-color="maskBackgroundColor"
      :background-color="backgroundColor"
      :safe-area="safeArea"
      :borderRadius="popopRadius"
      type="bottom"
      @change="change"
      @maskClick="maskClick"
    >
      <view class="popup-content" :style="{ height: contentHeight || defaultContentHeight }">
        <view class="title">
          <view v-if="mutiple && canSelectAll && filterTreeData.length" class="left" @click.stop="handleSelectAll">
            <text>{{ isSelectedAll ? $t("global.cancelSelectAll", "取消全选") : $t("global.selectAll", "全选") }}</text>
          </view>
          <view class="center">
            <text>{{ placeholder || title || $t("global.pleaseSelect", "请选择") }}</text>
          </view>
          <view class="right" :style="{ color: confirmTextColor }" @click.stop="close">
            <text>{{ confirmText || $t("global.complete", "完成") }}</text>
          </view>
        </view>
        <view v-if="search" class="search-box">
          <uni-search-bar
            style="padding: 0; width: 100%"
            @confirm="handleSearch(false)"
            :placeholder="$t('global.searchplaceholder', '请输入搜索内容')"
            :cancelText="$t('global.cancel', '取消')"
            v-model="searchStr"
            @input="handleSearch(false)"
            @cancel="handleSearch(true)"
            @clear="handleSearch(true)"
          >
          </uni-search-bar>
        </view>
        <view v-if="operateBarSlot" class="search-box">
          <slot name="operateBar"></slot>
        </view>
        <!-- 有数据、有展开收起配置、非搜索状态 -->
        <view v-if="filterTreeData.length && collapse && !searchStr" class="tree_select_collapse">
          <uni-w-button type="text" @click="onCollapseChange(false)" icon="iconfont icon-ptkj-zhankai2">{{
            $t("global.expand", "展开")
          }}</uni-w-button
          ><text>|</text
          ><uni-w-button type="text" @click="onCollapseChange(true)" icon="iconfont icon-ptkj-shouqi2">{{
            $t("global.collapse", "收起")
          }}</uni-w-button>
        </view>
        <view v-if="treeData.length" class="select-content">
          <scroll-view class="scroll-view-box" :scroll-top="scrollTop" scroll-y="true" @touchmove.stop>
            <view v-if="!filterTreeData.length" class="no-data center">
              <text>{{ $t("global.noData", "暂无数据") }}</text>
            </view>
            <data-select-item
              v-for="(ditem, dindex) in filterTreeData"
              :key="ditem[dataValue]"
              :node="ditem"
              :dataLabel="dataLabel"
              :dataValue="dataValue"
              :dataChildren="dataChildren"
              :choseParent="choseParent"
              :showLine="showLine"
              :linkage="linkage"
              :load="load"
              :lazyLoadChildren="lazyLoadChildren"
              :mutiple="mutiple"
              :parentNodes="filterTreeData"
              :index="dindex"
              :currentNode="currentNode"
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
            <view class="sentry" />
          </scroll-view>
        </view>
        <view v-else class="no-data center">
          <text>{{ $t("global.noData", "暂无数据") }}</text>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
const partCheckedSet = new Set();
import { paging } from "./utils";
import { utils } from "wellapp-uni-framework";
import { map } from "lodash";
import dataSelectItem from "./data-select-item.vue";
export default {
  name: "custom-w-tree-select",
  components: {
    dataSelectItem,
  },
  model: {
    prop: "value",
    event: ["input"],
  },
  props: {
    canSelectAll: {
      type: Boolean,
      default: false,
    },
    safeArea: {
      type: Boolean,
      default: true,
    },
    search: {
      type: Boolean,
      default: false,
    },
    clearResetSearch: {
      type: Boolean,
      default: false,
    },
    animation: {
      type: Boolean,
      default: true,
    },
    "is-mask-click": {
      type: Boolean,
      default: true,
    },
    "mask-background-color": {
      type: String,
      default: "rgba(0,0,0,0.1)",
    },
    "background-color": {
      type: String,
      default: "#ffffff",
    },
    "safe-area": {
      type: Boolean,
      default: true,
    },
    choseParent: {
      type: Boolean,
      default: true,
    },
    placeholder: {
      type: String,
      default: "",
    },
    confirmText: {
      type: String,
      default: "",
    },
    confirmTextColor: {
      type: String,
      default: "var(--w-primary-color)",
    },
    contentHeight: {
      type: String,
    },
    disabledList: {
      type: Array,
      default: () => [],
    },
    listData: {
      type: Array,
      default: () => [],
    },
    dataLabel: {
      type: String,
      default: "name",
    },
    dataValue: {
      type: String,
      default: "id",
    },
    dataChildren: {
      type: String,
      default: "children",
    },
    linkage: {
      type: Boolean,
      default: false,
    },
    clearable: {
      type: Boolean,
      default: false,
    },
    mutiple: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    showChildren: {
      type: Boolean,
      default: false,
    },
    // 边框
    bordered: {
      type: Boolean,
      default: false,
    },
    // 带连接线的树
    showLine: {
      type: Boolean,
      default: false,
    },
    pathMode: {
      type: Boolean,
      default: false,
    },
    pathHyphen: {
      type: String,
      default: "/",
    },
    load: {
      type: Function,
      default: function () {},
    },
    lazyLoadChildren: {
      type: Boolean,
      default: false,
    },
    value: {
      type: [Array, String],
      default: () => [],
    },
    // 可展开折叠树
    collapse: {
      type: Boolean,
      default: false,
    },
    // 分隔符
    tokenSeparators: {
      type: String,
      default: ";",
    },
    // 当前选中的节点
    currentNode: {
      type: Object,
      default: () => ({}),
    },
    // 展开的节点
    defaultExpandNodes: {
      type: Array,
      default: () => [],
    },
    title: String,
    popopRadius: {
      type: String,
      default: "16px 16px 0 0",
    },
  },
  data() {
    let operateBarSlot = this.$slots.operateBar != undefined;
    let showChildrenNodes = utils.deepClone(this.defaultExpandNodes);
    return {
      operateBarSlot,
      defaultContentHeight: "500px",
      treeData: [],
      filterTreeData: [],
      clearTimerList: [],
      selectedListBaseinfo: [],
      showPopup: false,
      clickOpenTimer: null,
      isSelectedAll: false,
      scrollTop: 0,
      searchStr: "",
      showChildrenNodes,
    };
  },
  computed: {
    selectList() {
      return this.value || [];
    },
  },
  watch: {
    listData: {
      deep: true,
      immediate: true,
      handler(newVal) {
        if (newVal) {
          partCheckedSet.clear();
          this.treeData = this.initData(newVal);

          if (this.value) {
            this.changeStatus(this.treeData, this.value, true);
            this.filterTreeData.length && this.changeStatus(this.filterTreeData, this.value);
          }
          if (this.showPopup) {
            this.resetClearTimerList();
            this.renderTree(this.treeData);
          }
        }
      },
    },
    value: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.changeStatus(this.treeData, this.value, true);
          this.filterTreeData.length && this.changeStatus(this.filterTreeData, this.value);
        }
      },
    },
    showPopup(val) {
      this.$emit("showPickerPopup",val);
      if (!val) {
        this.$emit("clearCurrentNode");
      }
    },
  },
  mounted() {
    this.getContentHeight(uni.getSystemInfoSync());
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    // 搜索完成返回顶部
    goTop() {
      this.scrollTop = 10;
      this.$nextTick(() => {
        this.scrollTop = 0;
      });
    },
    // 获取对应数据
    getReflectNode(node, arr) {
      const array = [...arr];
      while (array.length) {
        const item = array.shift();
        if (item[this.dataValue] === node[this.dataValue]) {
          return item;
        }
        if (item[this.dataChildren] && item[this.dataChildren].length) {
          array.push(...item[this.dataChildren]);
        }
      }
      return {};
    },
    getContentHeight({ screenHeight }) {
      this.defaultContentHeight = `${Math.floor(screenHeight * 0.7)}px`;
    },
    // 处理搜索
    handleSearch(isClear = false) {
      this.resetClearTimerList();
      if (isClear) {
        // 点击清空按钮并且设置清空按钮会重置搜索
        if (this.clearResetSearch) {
          this.renderTree(this.treeData);
        }
      } else {
        this.renderTree(this.searchValue(this.searchStr, this.treeData));
      }
      this.goTop();
      // uni.hideKeyboard();
    },
    // 具体搜索方法
    searchValue(str, arr) {
      const res = [];
      arr.forEach((item) => {
        if (item.visible) {
          if (item[this.dataLabel].toString().toLowerCase().indexOf(str.toLowerCase()) > -1) {
            res.push(item);
          } else {
            if (item[this.dataChildren] && item[this.dataChildren].length) {
              const data = this.searchValue(str, item[this.dataChildren]);
              if (data && data.length) {
                if (str && !item.showChildren && item[this.dataChildren] && item[this.dataChildren].length) {
                  item.showChildren = true;
                }
                res.push({
                  ...item,
                  [this.dataChildren]: data,
                });
              }
            }
          }
        }
      });
      return res;
    },
    // 懒加载
    renderTree(arr) {
      const pagingArr = paging(arr);
      this.filterTreeData.splice(0, this.filterTreeData.length, ...((pagingArr && pagingArr[0]) || []));
      this.lazyRenderList(pagingArr, 1);
    },
    // 懒加载具体逻辑
    lazyRenderList(arr, startIndex) {
      for (let i = startIndex; i < arr.length; i++) {
        let timer = null;
        timer = setTimeout(() => {
          this.filterTreeData.push(...arr[i]);
        }, i * 500);
        this.clearTimerList.push(() => clearTimeout(timer));
      }
    },
    // 中断懒加载
    resetClearTimerList() {
      const list = [...this.clearTimerList];
      this.clearTimerList = [];
      list.forEach((fn) => fn());
    },
    // 打开弹窗
    open() {
      // disaled模式下禁止打开弹窗
      if (this.disabled) return;
      this.showPopup = true;
      this.$nextTick(() => {
        this.$refs.popup.open();
        this.renderTree(this.treeData);
      });
    },
    // 关闭弹窗
    close() {
      this.$refs.popup.close();
    },
    // 弹窗状态变化 包括点击回显框和遮罩
    change(data) {
      if (data.show) {
        uni.$on("custom-tree-select-node-click", this.handleNodeClick);
        uni.$on("custom-tree-select-name-click", this.handleHideChildren);
        uni.$on("custom-tree-select-load", this.handleLoadNode);
      } else {
        uni.$off("custom-tree-select-node-click", this.handleNodeClick);
        uni.$off("custom-tree-select-name-click", this.handleHideChildren);
        uni.$off("custom-tree-select-load", this.handleLoadNode);
        this.resetClearTimerList();
        this.searchStr = "";
        if (this.animation) {
          setTimeout(() => {
            this.showPopup = false;
          }, 200);
        } else {
          this.showPopup = false;
        }
      }
      this.$emit("change", data);
    },
    // 点击遮罩
    maskClick() {
      this.$emit("maskClick");
    },
    // 初始化数据
    initData(arr, parentVisible = undefined, pathArr = [], isCollapse) {
      if (!Array.isArray(arr)) return [];
      const res = [];

      for (let i = 0; i < arr.length; i++) {
        const curPathArr = [...pathArr, arr[i][this.dataLabel]];
        const obj = {
          ...arr[i],
          [this.dataLabel]: arr[i][this.dataLabel],
          [this.dataValue]: arr[i][this.dataValue],
          path: curPathArr.join(this.pathHyphen),
        };

        obj.checked = this.selectList.includes(arr[i][this.dataValue]);

        obj.disabled = false;
        if (Boolean(arr[i].disabled) || this.disabledList.includes(obj[this.dataValue])) {
          obj.disabled = true;
        }

        //半选
        obj.partChecked = Boolean(arr[i].partChecked === undefined ? false : arr[i].partChecked);
        obj.partChecked && obj.partCheckedSet.add(obj[this.dataValue]);
        !obj.partChecked && (this.isSelectedAll = false);

        const parentVisibleState = parentVisible === undefined ? true : parentVisible;
        const curVisibleState = arr[i].visible === undefined ? true : Boolean(arr[i].visible);
        if (parentVisibleState === curVisibleState) {
          obj.visible = parentVisibleState;
        } else if (!parentVisibleState || !curVisibleState) {
          obj.visible = false;
        } else {
          obj.visible = true;
        }

        if (arr[i] && this.showChildrenNodes.indexOf(arr[i][this.dataValue]) > -1) {
          arr[i].showChildren = true;
        }

        obj.showChildren =
          "showChildren" in arr[i] && arr[i].showChildren != undefined ? arr[i].showChildren : this.showChildren;
        if (isCollapse !== undefined) {
          obj.showChildren = !isCollapse; // true为折叠，false为展开
          this.onMarkShowChildrenNodes(obj, obj.showChildren);
        }
        if (arr[i][this.dataChildren] && arr[i][this.dataChildren].length) {
          const childrenVal = this.initData(arr[i][this.dataChildren], obj.visible, curPathArr, isCollapse);
          obj[this.dataChildren] = childrenVal;
          if (!obj.checked && childrenVal.some((item) => item.checked || item.partChecked)) {
            obj.partChecked = true;
            partCheckedSet.add(obj[this.dataValue]);
          }
        }

        res.push(obj);
      }

      return res;
    },
    // 获取某个节点后面所有元素
    getChildren(node) {
      if (!(node[this.dataChildren] && node[this.dataChildren].length)) return [];
      const res = node[this.dataChildren].reduce((pre, val) => {
        if (val.visible) {
          return [...pre, val];
        }
        return pre;
      }, []);
      for (let i = 0; i < node[this.dataChildren].length; i++) {
        res.push(...this.getChildren(node[this.dataChildren][i]));
      }
      return res;
    },
    // 获取某个节点所有祖先元素
    getParentNode(target, arr) {
      let res = [];

      for (let i = 0; i < arr.length; i++) {
        if (arr[i][this.dataValue] === target[this.dataValue]) {
          return true;
        }

        if (arr[i][this.dataChildren] && arr[i][this.dataChildren].length) {
          const childRes = this.getParentNode(target, arr[i][this.dataChildren]);
          if (typeof childRes === "boolean" && childRes) {
            res = [arr[i]];
          } else if (Array.isArray(childRes) && childRes.length) {
            res = [...childRes, arr[i]];
          }
        }
      }

      return res;
    },
    // 点击checkbox
    handleNodeClick(data, status) {
      const node = this.getReflectNode(data, this.treeData);
      node.checked = typeof status === "boolean" ? status : !node.checked;
      node.partChecked = false;
      partCheckedSet.delete(node[this.dataValue]);
      // 如果是单选不考虑其他情况
      if (!this.mutiple) {
        let emitData = [];
        if (node.checked) {
          emitData = [node[this.dataValue]];
        }
        this.emit(emitData);
      } else {
        // 多选情况
        if (!this.linkage) {
          // 不需要联动
          let emitData = null;
          if (node.checked) {
            emitData = Array.from(new Set([...this.selectList, node[this.dataValue]]));
          } else {
            emitData = this.selectList.filter((id) => id !== node[this.dataValue]);
          }
          this.emit(emitData);
        } else {
          // 需要联动
          let emitData = [...this.selectList];
          const parentNodes = this.getParentNode(node, this.treeData);
          const childrenVal = this.getChildren(node).filter((item) => !item.disabled);
          if (node.checked) {
            // 选中
            emitData = Array.from(new Set([...emitData, node[this.dataValue]]));
            if (childrenVal.length) {
              emitData = Array.from(new Set([...emitData, ...childrenVal.map((item) => item[this.dataValue])]));
              // 孩子节点全部选中并且清除半选状态
              childrenVal.forEach((childNode) => {
                childNode.partChecked = false;
                partCheckedSet.delete(childNode[this.dataValue]);
              });
            }
            if (parentNodes.length) {
              let flag = false;
              // 有父元素 如果父元素下所有子元素全部选中，选中父元素
              while (parentNodes.length) {
                const item = parentNodes.shift();
                if (!item.disabled) {
                  if (flag) {
                    // 前一个没选中并且为半选那么之后的全为半选
                    item.partChecked = true;
                    partCheckedSet.add(item[this.dataValue]);
                  } else {
                    const allChecked = item[this.dataChildren]
                      .filter((node) => node.visible && !node.disabled)
                      .every((node) => node.checked);
                    if (allChecked) {
                      item.checked = true;
                      item.partChecked = false;
                      partCheckedSet.delete(item[this.dataValue]);
                      emitData = Array.from(new Set([...emitData, item[this.dataValue]]));
                    } else {
                      item.partChecked = true;
                      partCheckedSet.add(item[this.dataValue]);
                      flag = true;
                    }
                  }
                }
              }
            }
          } else {
            // 取消选中
            emitData = emitData.filter((id) => id !== node[this.dataValue]);
            if (childrenVal.length) {
              // 取消选中全部子节点
              childrenVal.forEach((childNode) => {
                emitData = emitData.filter((id) => id !== childNode[this.dataValue]);
              });
            }
            if (parentNodes.length) {
              parentNodes.forEach((parentNode) => {
                if (emitData.includes(parentNode[this.dataValue])) {
                  parentNode.checked = false;
                }
                emitData = emitData.filter((id) => id !== parentNode[this.dataValue]);
                const hasChecked = parentNode[this.dataChildren]
                  .filter((node) => node.visible && !node.disabled)
                  .some((node) => node.checked || node.partChecked);

                parentNode.partChecked = hasChecked;
                if (hasChecked) {
                  partCheckedSet.add(parentNode[this.dataValue]);
                } else {
                  partCheckedSet.delete(parentNode[this.dataValue]);
                }
              });
            }
          }
          this.emit(emitData);
        }
      }
    },
    emit(value) {
      let label = map(this.selectedListBaseinfo, (item) => {
        return this.pathMode ? item.path : item[this.dataLabel];
      });
      this.$emit("input", value);
      this.$emit("update:modelValue", value);
      this.$emit("changeValue", value, label, this.selectedListBaseinfo);
    },
    // 点击名称折叠或展开
    handleHideChildren(node) {
      const status = !node.showChildren;
      this.getReflectNode(node, this.treeData).showChildren = status;
      this.getReflectNode(node, this.filterTreeData).showChildren = status;
      this.onMarkShowChildrenNodes(node);
    },
    // 根据 dataValue 找节点
    changeStatus(list, ids, needEmit = false) {
      const arr = [...list];
      let flag = true;
      needEmit && (this.selectedListBaseinfo = []);

      while (arr.length) {
        const item = arr.shift();

        if (ids.includes(item[this.dataValue])) {
          this.$set(item, "checked", true);
          needEmit && this.selectedListBaseinfo.push(item);
          // 数据被选中清除半选状态
          item.partChecked = false;
          partCheckedSet.delete(item[this.dataValue]);
        } else {
          this.$set(item, "checked", false);
          if (!this.choseParent && item[this.dataChildren] && item[this.dataChildren].length) {
            // 父级不可选时，不标记false
          } else if (item.visible && !item.disabled) {
            flag = false;
          }
          if (partCheckedSet.has(item[this.dataValue])) {
            this.$set(item, "partChecked", true);
          } else {
            this.$set(item, "partChecked", false);
          }
        }

        if (item[this.dataChildren] && item[this.dataChildren].length) {
          arr.push(...item[this.dataChildren]);
        }
      }
      this.isSelectedAll = flag;
      needEmit && this.$emit("selectChange", [...this.selectedListBaseinfo]);
    },
    // 移除选项
    removeSelectedItem(node) {
      this.isSelectedAll = false;
      if (this.linkage) {
        this.handleNodeClick(node, false);
        this.$emit("removeSelect", node);
      } else {
        const emitData = this.selectList.filter((item) => item !== node[this.dataValue]);
        this.$emit("removeSelect", node);
        this.emit(emitData);
      }
    },
    // 全部选中
    handleSelectAll() {
      this.isSelectedAll = !this.isSelectedAll;
      if (this.isSelectedAll) {
        if (!this.mutiple) {
          uni.showToast({
            title: this.$t("global.radioModeNoSelectAll", "单选模式下不能全选"),
            icon: "none",
            duration: 1000,
          });
          return;
        }
        let emitData = [];
        this.treeData.forEach((item) => {
          if (item.visible || (item.disabled && item.checked)) {
            // 父级可选或者没有子级
            if (
              this.choseParent ||
              !item[this.dataChildren] ||
              (item[this.dataChildren] && !item[this.dataChildren].length)
            ) {
              emitData = Array.from(new Set([...emitData, item[this.dataValue]]));
            }
            if (item[this.dataChildren] && item[this.dataChildren].length) {
              emitData = Array.from(
                new Set([
                  ...emitData,
                  ...this.getChildren(item)
                    .filter((citem) => {
                      // 父级不可选且是父级时，不在范围内
                      if (!this.choseParent && citem[this.dataChildren] && citem[this.dataChildren].length) {
                        return false;
                      }
                      return !citem.disabled || (citem.disabled && citem.checked);
                    })
                    .map((item) => item[this.dataValue]),
                ])
              );
            }
          }
        });
        this.emit(emitData);
      } else {
        this.clear();
      }
    },
    // 清空选项
    clear() {
      if (this.disabled) return;
      const emitData = [];
      partCheckedSet.clear();
      this.selectedListBaseinfo.forEach((node) => {
        if (node.visible && node.checked && node.disabled) {
          emitData.push(node[this.dataValue]);
        }
      });
      this.emit(emitData);
    },
    // 异步加载节点
    handleLoadNode({ source, target }) {
      // #ifdef MP-WEIXIN
      const node = this.getReflectNode(source, this.treeData);
      this.$set(node, this.dataChildren, this.initData(target, source.visible, source.path.split(this.pathHyphen)));
      this.$nextTick(() => {
        this.handleHideChildren(node);
      });
      // #endif

      // #ifndef MP-WEIXIN
      this.$set(source, this.dataChildren, this.initData(target, source.visible, source.path.split(this.pathHyphen)));
      this.$nextTick(() => {
        this.handleHideChildren(source);
      });
      // #endif
    },
    // 收起/展开
    onCollapseChange(isCollapse) {
      this.treeData = this.initData(utils.deepClone(this.listData), undefined, undefined, isCollapse);
      this.resetClearTimerList();
      this.renderTree(this.treeData);
    },
    // 记录展开子列表的父节点值
    onMarkShowChildrenNodes(node, isShow) {
      if (node[this.dataValue] && node[this.dataChildren] && node[this.dataChildren].length) {
        let hasIndex = this.showChildrenNodes.indexOf(node[this.dataValue]);
        if (hasIndex == -1) {
          if (isShow === undefined || isShow) {
            this.showChildrenNodes.push(node[this.dataValue]);
          }
        } else {
          if (isShow === undefined || !isShow) {
            this.showChildrenNodes.splice(hasIndex, 1);
          }
        }
      }
    },
  },
};
</script>

<style lang="scss" scoped>
$primary-color: var(--w-primary-color);
$col-sm: 2px;
$col-base: 8px;
$col-lg: 12px;
$row-sm: 4px;
$row-base: 8px;
$row-lg: 16px;
$radius-sm: 2px;
$radius-base: 4px;
$uni-base-color: var(--w-text-color-base);
$uni-main-color: var(--w-text-color-mobile);
$uni-secondary-color: var(--w-text-color-light);
$uni-border: var(--w-border-color-mobile);

.custom-tree-select-content {
  width: 100%;
  .select-list {
    padding-left: $row-base;
    padding-right: $row-base;
    min-height: 35px;
    border: 1px solid $uni-border;
    border-radius: $radius-base;
    display: flex;
    justify-content: space-between;
    align-items: center;

    &.active {
      padding: calc(#{$col-sm} / 2) $row-base calc(#{$col-sm} / 2) $row-base;
    }

    &:not(.mutiple) {
      .right {
        margin-left: 4px;
      }
    }

    .left {
      flex: 1;

      .select-items {
        display: flex;
        flex-wrap: wrap;
      }

      .select-item {
        margin: $row-sm;
        padding: $col-sm $row-base;
        max-width: auto;
        height: auto;
        background-color: #fafafa;
        border-radius: $radius-base;
        color: $uni-main-color;
        display: flex;
        align-items: center;

        .name {
          flex: 1;
          padding-right: $row-base;
          font-size: 14px;
        }

        .close {
          width: 18px;
          height: 18px;
          display: flex;
          justify-content: center;
          align-items: center;
          overflow: hidden;
        }
      }

      .custom-tree-select-content__placeholder {
        color: var(--text-color-placeholder);
        font-size: var(--w-font-size-base);
      }
    }

    .right {
      // margin-right: $row-sm;
      display: flex;
      justify-content: flex-end;
      align-items: center;
    }

    &.disabled {
      background-color: var(--bg-disable-color);

      .left {
        .select-item {
          color: var(--text-color-disable);
          .name {
            padding: 0;
          }
        }
      }
    }

    &.border-none {
      border: none;
      &:not(.disabled),
      &:not(.active) {
        padding: 0px;
      }
      .select-item {
        padding: $row-sm $row-base;
      }
    }
  }

  .popup-content {
    flex: 1;
    display: flex;
    flex-direction: column;

    .title {
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

    .search-box {
      margin: $col-base $row-base 0;
      background-color: #fff;
      display: flex;
      align-items: center;

      .search-btn {
        margin-left: $row-base;
        height: 35px;
        line-height: 35px;
      }
    }

    .tree_select_collapse {
      margin: $col-base $row-base 0;
      uni-text {
        padding: 0 $row-sm;
        color: var(--w-text-color-lighter);
      }
    }

    .select-content {
      margin: $col-base 0; //$row-base
      flex: 1;
      overflow: hidden;
      position: relative;
    }

    .scroll-view-box {
      touch-action: none;
      flex: 1;
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      ::v-deep .uni-scroll-view-content {
        width: calc(100% - 20px);
        padding: 0 10px;
      }
    }

    .sentry {
      height: 10px; // 48px;
    }
  }

  .no-data {
    width: auto;
    color: var(--w-text-color-light);
    font-size: 14px;
  }

  .no-data.center {
    text-align: center;
  }
}
</style>
