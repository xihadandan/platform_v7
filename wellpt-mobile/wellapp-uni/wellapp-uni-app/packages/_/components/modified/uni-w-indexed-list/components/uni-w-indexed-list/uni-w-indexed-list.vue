<template>
  <view class="uni-indexed-list" ref="list" id="list">
    <!-- #ifdef APP-NVUE -->
    <list class="uni-indexed-list__scroll" scrollable="true" show-scrollbar="false">
      <cell v-for="(list, idx) in lists" :key="idx" :ref="'uni-indexed-list-' + idx">
        <!-- #endif -->
        <!-- #ifndef APP-NVUE -->
        <scroll-view :scroll-into-view="scrollViewId" class="uni-indexed-list__scroll" scroll-y>
          <view v-for="(list, idx) in lists" :key="idx" :id="'uni-indexed-list-' + idx">
            <!-- #endif -->
            <UniWIndexedListItem :list="list" :loaded="loaded" :idx="idx" :showSelect="showSelect" @itemClick="onClick">
              <template v-slot:prefix="{ item, index, list }">
                <slot name="prefix" :item="item" :index="index" :list="list"></slot>
              </template>
              <template v-slot:suffix="{ item, index, list }">
                <slot name="suffix" :item="item" :index="index" :list="list"></slot>
              </template>
            </UniWIndexedListItem>
            <!-- #ifndef APP-NVUE -->
          </view>
        </scroll-view>
        <!-- #endif -->
        <!-- #ifdef APP-NVUE -->
      </cell>
    </list>
    <!-- #endif -->
    <view class="uni-indexed-list__menu">
      <!-- @touchstart="touchStart"
      @touchmove.stop.prevent="touchMove"
      @touchend="touchEnd"
      @mousedown.stop="mousedown"
      @mousemove.stop.prevent="mousemove"
      @mouseleave.stop="mouseleave" -->
      <view
        v-for="(list, key) in lists"
        :key="key"
        class="uni-indexed-list__menu-item"
        :class="touchmoveIndex == key ? 'uni-indexed-list__menu--active' : ''"
        @tap.stop="selectItem(key)"
      >
        <text
          class="uni-indexed-list__menu-text"
          :class="touchmoveIndex == key ? 'uni-indexed-list__menu-text--active' : ''"
          >{{ list.key }}</text
        >
      </view>
    </view>
    <view v-if="touchmove" class="uni-indexed-list__alert-wrapper">
      <text class="uni-indexed-list__alert">{{ lists[touchmoveIndex].key }}</text>
    </view>
  </view>
</template>
<script>
import UniWIndexedListItem from "./uni-w-indexed-list-item.vue";
// #ifdef APP-NVUE
const dom = weex.requireModule("dom");
// #endif
// #ifdef APP-PLUS
function throttle(func, delay) {
  var prev = Date.now();
  return function () {
    var context = this;
    var args = arguments;
    var now = Date.now();
    if (now - prev >= delay) {
      func.apply(context, args);
      prev = Date.now();
    }
  };
}

function touchMove(e) {
  let pageY = e.touches[0].pageY;
  let index = Math.floor((pageY - this.winOffsetY) / this.itemHeight);
  if (this.touchmoveIndex === index) {
    return false;
  }
  let item = this.lists[index];
  if (item) {
    // #ifndef APP-NVUE
    this.scrollViewId = "uni-indexed-list-" + index;
    this.touchmoveIndex = index;
    // #endif
    // #ifdef APP-NVUE
    dom.scrollToElement(this.$refs["uni-indexed-list-" + index][0], {
      animated: false,
    });
    this.touchmoveIndex = index;
    // #endif
  }
}
const throttleTouchMove = throttle(touchMove, 40);
// #endif

/**
 * IndexedList 索引列表
 * @description 用于展示索引列表
 * @tutorial https://ext.dcloud.net.cn/plugin?id=375
 * @property {Boolean} showSelect = [true|false] 展示模式
 * 	@value true 展示模式
 * 	@value false 选择模式
 * @property {Object} options 索引列表需要的数据对象
 * @event {Function} click 点击列表事件 ，返回当前选择项的事件对象
 * @example <uni-indexed-list options="" showSelect="false" @click=""></uni-indexed-list>
 */
export default {
  name: "UniWIndexedList",
  components: {
    UniWIndexedListItem,
  },
  emits: ["click"],
  props: {
    options: {
      type: Array,
      default() {
        return [];
      },
    },
    showSelect: {
      type: Boolean,
      default: false,
    },
    keyParam: {
      type: String,
      default: "key",
    },
    multiSelect: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      lists: [],
      winHeight: 0,
      itemHeight: 0,
      winOffsetY: 0,
      touchmove: false,
      touchmoveIndex: -1,
      scrollViewId: "",
      touchmovable: true,
      loaded: false,
      isPC: false,
      selectLists: [],
    };
  },
  watch: {
    options: {
      handler: function () {
        this.setList();
      },
      deep: true,
    },
  },
  mounted() {
    // #ifdef H5
    this.isPC = this.IsPC();
    // #endif
    setTimeout(() => {
      this.setList();
    }, 50);
    setTimeout(() => {
      this.loaded = true;
    }, 300);
  },
  methods: {
    setList() {
      let index = 0;
      this.lists = [];
      this.options.forEach((value, index) => {
        if (value.data.length === 0) {
          return;
        }
        let indexBefore = index;
        let items = value.data.map((item) => {
          let obj = {};
          obj["key"] = value[this.keyParam];
          obj["itemIndex"] = index;
          index++;
          if (typeof item !== "string") {
            for (let k in item) {
              obj[k] = item[k];
            }
            obj.checked = item.checked ? item.checked : false;
          } else {
            obj["name"] = item;
          }
          // console.log(item);
          return obj;
        });
        this.lists.push({
          title: value[this.keyParam],
          key: value[this.keyParam],
          items: items,
          itemIndex: indexBefore,
        });
      });
      // #ifndef APP-NVUE
      uni
        .createSelectorQuery()
        .in(this)
        .select("#list")
        .boundingClientRect()
        .exec((ret) => {
          this.winOffsetY = ret[0].top;
          this.winHeight = ret[0].height;
          this.itemHeight = this.winHeight / this.lists.length;
        });
      // #endif
      // #ifdef APP-NVUE
      dom.getComponentRect(this.$refs["list"], (res) => {
        this.winOffsetY = res.size.top;
        this.winHeight = res.size.height;
        this.itemHeight = this.winHeight / this.lists.length;
      });
      // #endif
    },
    touchStart(e) {
      this.touchmove = true;
      let pageY = this.isPC ? e.pageY : e.touches[0].pageY;
      let index = Math.floor((pageY - this.winOffsetY) / this.itemHeight);
      let item = this.lists[index];
      if (item) {
        this.scrollViewId = "uni-indexed-list-" + index;
        this.touchmoveIndex = index;
        // #ifdef APP-NVUE
        dom.scrollToElement(this.$refs["uni-indexed-list-" + index][0], {
          animated: false,
        });
        // #endif
      }
    },
    touchMove(e) {
      // #ifndef APP-PLUS
      let pageY = this.isPC ? e.pageY : e.touches[0].pageY;
      let index = Math.floor((pageY - this.winOffsetY) / this.itemHeight);
      if (this.touchmoveIndex === index) {
        return false;
      }
      let item = this.lists[index];
      if (item) {
        this.scrollViewId = "uni-indexed-list-" + index;
        this.touchmoveIndex = index;
      }
      // #endif
      // #ifdef APP-PLUS
      throttleTouchMove.call(this, e);
      // #endif
    },
    touchEnd() {
      this.touchmove = false;
      // this.touchmoveIndex = -1
    },

    /**
     * 兼容 PC @tian
     */

    mousedown(e) {
      if (!this.isPC) return;
      this.touchStart(e);
    },
    mousemove(e) {
      if (!this.isPC) return;
      this.touchMove(e);
    },
    mouseleave(e) {
      if (!this.isPC) return;
      this.touchEnd(e);
    },

    // #ifdef H5
    IsPC() {
      var userAgentInfo = navigator.userAgent;
      var Agents = ["Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod"];
      var flag = true;
      for (let v = 0; v < Agents.length - 1; v++) {
        if (userAgentInfo.indexOf(Agents[v]) > 0) {
          flag = false;
          break;
        }
      }
      return flag;
    },
    // #endif

    onClick(e) {
      let { idx, index, item } = e;
      let obj = {};
      for (let key in this.lists[idx].items[index]) {
        obj[key] = this.lists[idx].items[index][key];
      }
      let select = [];
      if (this.showSelect) {
        this.lists[idx].items[index].checked = !this.lists[idx].items[index].checked;
        this.lists.forEach((value, vidx) => {
          value.items.forEach((vitem, vindex) => {
            // 单选时，如果当前项被选中，其他项取消选中
            if (!this.multiSelect) {
              if (this.lists[idx].items[index].checked) {
                if (vitem.id != item.id && vitem.checked) {
                  this.lists[vidx].items[vindex].checked = false;
                  vitem.checked = false;
                }
              }
            }
            if (vitem.checked) {
              obj = {};
              for (let key in this.lists[vidx].items[vindex]) {
                obj[key] = this.lists[vidx].items[vindex][key];
              }
              select.push(obj);
            }
          });
        });
        this.selectLists = select;
        this.$emit("click", {
          item: item,
          select: select,
        });
      }
    },
    /**
     * 改变是否选中
     * @param item 当前项
     * @param checked 如果有值则改成该值（true|false），未设置则改成相反值
     * @param listsItemsKeyParam lists项的key字段名
     * @param itemKeyParam 当前项对应的key字段名
     */
    setChecked(item, checked, listsItemsKeyParam = "id", itemKeyParam = "key") {
      for (let idx = 0; idx < this.lists.length; idx++) {
        if (this.lists[idx].key == item[this.keyParam]) {
          let items = this.lists[idx].items;
          for (let index = 0; index < items.length; index++) {
            if (items[index][listsItemsKeyParam] == item[itemKeyParam]) {
              if (checked === undefined) {
                this.lists[idx].items[index].checked = !this.lists[idx].items[index].checked;
              } else {
                this.lists[idx].items[index].checked = checked;
              }
              break;
            }
          }
          break;
        }
      }
    },
    selectItem(index) {
      let item = this.lists[index];
      if (item) {
        this.scrollViewId = "uni-indexed-list-" + index;
        this.touchmoveIndex = index;
        // #ifdef APP-NVUE
        dom.scrollToElement(this.$refs["uni-indexed-list-" + index][0], {
          animated: false,
        });
        // #endif
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.uni-indexed-list {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: row;
}

.uni-indexed-list__scroll {
  flex: 1;
}

.uni-indexed-list__menu {
  width: 24px;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: column;
}

.uni-indexed-list__menu-item {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex: 1;
  align-items: center;
  justify-content: center;
  /* #ifdef H5 */
  cursor: pointer;
  /* #endif */
}

.uni-indexed-list__menu-text {
  font-size: 12px;
  text-align: center;
  color: #aaa;
}

.uni-indexed-list__menu--active {
  // background-color: rgb(200, 200, 200);
}

.uni-indexed-list__menu--active {
}

.uni-indexed-list__menu-text--active {
  border-radius: 16px;
  width: 16px;
  height: 16px;
  line-height: 16px;
  background-color: var(--w-primary-color);
  color: #fff;
}

.uni-indexed-list__alert-wrapper {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: row;
  align-items: center;
  justify-content: center;
}

.uni-indexed-list__alert {
  width: 80px;
  height: 80px;
  border-radius: 80px;
  text-align: center;
  line-height: 80px;
  font-size: 35px;
  color: #fff;
  background-color: rgba(0, 0, 0, 0.5);
}
</style>
