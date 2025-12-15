<template>
  <div v-if="serverRender" class="ps">
    <slot></slot>
  </div>
  <PerfectScrollbar ref="perfectScrollbarRef" :options="options" v-else>
    <slot></slot>
  </PerfectScrollbar>
</template>
<style lang="less">
// .ps__rail-y {
//   z-index: 2;
// }
</style>
<script type="text/babel">
import { some } from 'lodash';
// PerfectScrollbar 服务端渲染异常导致页面问题，故增加该滚动组件进行适配
export default {
  name: 'Scroll',
  inject: ['pageContext'],
  props: {
    //当设置为true时，无论内容宽度如何，X轴上的滚动条都将不可用。
    suppressScrollX: {
      type: Boolean,
      default: false
    },
    //当设置为true时，无论内容宽度如何，Y轴上的滚动条都将不可用。
    suppressScrollY: {
      type: Boolean,
      default: false
    },
    wheelPropagation: {
      type: Boolean,
      default: true
    },
    stopScrollMatcher: [Function]
  },
  components: {},
  computed: {
    options() {
      return {
        suppressScrollX: this.suppressScrollX,
        suppressScrollY: this.suppressScrollY
      };
    }
  },
  data() {
    return {
      serverRender: EASY_ENV_IS_NODE,
      dropdownScrollList: [
        'ant-select-dropdown-menu',
        '.ant-select-tree-dropdown',
        '.ant-select-dropdown-content ',
        '.ant-dropdown',
        '.ant-mentions-dropdown',
        '.ant-dropdown-menu',
        '.ant-table-body',
        '.ant-select-dropdown'
      ],
      ctlOpenDropDownList: ['.ant-dropdown-open', '.ant-select-open', '.ant-popover', '.ant-select-dropdown']
    };
  },
  watch: {},
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    let _this = this;
    const scrollContainer = this.$refs.perfectScrollbarRef;
    function isScrollContainer(el) {
      const style = window.getComputedStyle(el);
      let scrollAbleElement = /auto|scroll/.test(style.overflowY) || /auto|scroll/.test(style.overflow);
      if (scrollAbleElement) {
        if (_this.stopScrollMatcher != undefined) {
          if (_this.stopScrollMatcher(el)) {
            return true;
          }
        } else {
          for (let exp of _this.dropdownScrollList) {
            if (el.classList.contains(exp.substring(1)) || el.closest(exp) != null) {
              return true;
            }
          }
        }
      }
      return false;
    }
    scrollContainer.$el.addEventListener(
      'wheel',
      function (e) {
        let el = e.target;
        while (el && el !== this) {
          if (isScrollContainer(el)) {
            e.stopPropagation();
            return;
          }
          el = el.parentElement;
        }
      },
      { passive: false, capture: true }
    ); // 使用 capture 阶段更早拦截
    // add mouse wheel event listener
    // scrollContainer &&
    //   scrollContainer.$el.addEventListener('wheel', e => {
    //     // e.preventDefault();
    //     // 滚动区域内存在搜索展开下拉框
    //     let hasOpen = some(_this.ctlOpenDropDownList, item => {
    //       return e.target.closest(item);
    //     });
    //     if (hasOpen) {
    //       // 如果当前指在滚动区域
    //       some(_this.dropdownScrollList, item => {
    //         const $target = e.target.closest(item);
    //         if ($target) {
    //           let overflow = e.target.closest(item).style.overflow;
    //           let overflowY = e.target.closest(item).style.overflowY;
    //           let isConsume = (overflow && overflow != 'hidden') || (overflowY && overflowY != 'hidden');
    //           if (isConsume && !e.target.classList.contains('ps__child--consume')) {
    //             // 加上样式可以阻止外层滚动事件
    //             e.target.classList.add('ps__child--consume');
    //           }
    //           return isConsume;
    //         }
    //         return false;
    //       });
    //     }
    //   });
    // 监听滚动条更新
    this.pageContext &&
      this.pageContext.handleEvent('perfectScrollbarToResize', ({ top }) => {
        if (scrollContainer && scrollContainer.ps) {
          if (top) {
            scrollContainer.ps.element.scrollTop = top;
          } else {
            scrollContainer.ps.update();
          }
        }
      });
    this.$emit('mounted', this);
  },
  methods: {}
};
</script>
