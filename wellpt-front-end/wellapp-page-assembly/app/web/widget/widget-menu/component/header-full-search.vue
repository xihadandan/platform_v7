<template>
  <div class="header-full-search-container">
    <div class="header-full-search" @click="handleSearchPortal">
      <i class="iconfont icon-ptkj-sousuochaxun" />
      <span>搜索</span>
    </div>
    <div class="header-full-search-input" ref="box" @focusin="onFocusIn" @focusout="onFocusOut">
      <full-search-input
        v-show="showInput"
        :meta="meta"
        :setting="fullSearchDefinition"
        ref="input"
        @focus="onFocus"
        @blur="onBlur"
        @search="handleSearch"
      />
    </div>
  </div>
</template>

<script>
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import FullSearchInput from '@admin/app/web/template/full-search/full-text-search/search-input.vue';

export default {
  name: 'HeaderFullSearch',
  inject: ['pageContext', 'widgetLayoutContext', 'fullSearchDefinition'],
  props: {
    menu: Object
  },
  components: {
    FullSearchInput
  },
  data() {
    return {
      showInput: false,
      meta: {
        keyword: '',
        categoryCode: ''
      }
    };
  },
  watch: {
    showInput: {
      handler(visible) {
        const widgetMenuEl = this.$el.closest('.widget-menu');
        if (!widgetMenuEl) {
          return;
        }
        const menuItems = widgetMenuEl.querySelectorAll('li[role="menuitem"].ant-menu-item') || [];
        if (visible) {
          for (let index = 0; index < menuItems.length; index++) {
            menuItems[index].style.cssText += `;display:none`;
          }
        } else {
          for (let index = 0; index < menuItems.length; index++) {
            menuItems[index].style.cssText += `;display:inline-block`;
          }
        }
      }
    }
  },
  methods: {
    onFocusIn() {
      console.log('容器获得焦点（或内部元素获得焦点）');
      this.showInput = true;
    },
    onFocusOut(e) {
      const box = this.$refs.box;
      let next = e.relatedTarget;
      // if (!next && e.target) {
      //   if (e.target.classList.contains('ant-input-clear-icon')) {
      //     next = e.target;
      //   }
      // }

      // 如果 relatedTarget 不在容器内，说明整个容器失焦
      if (!box.contains(next)) {
        console.log('整个容器完全失去焦点');
        if (!this.meta.keyword) {
          const timer = setTimeout(() => {
            this.showInput = false;
            clearTimeout(timer);
          }, 200);
        }
      }
    },
    // 点击搜索入口
    handleSearchPortal() {
      if (!this.fullSearchDefinition) {
        return;
      }
      if (this.fullSearchDefinition.interactiveMode === 'page') {
        this.openSearchTabPage();
      } else {
        this.showInput = !this.showInput;
        if (this.showInput) {
          this.$nextTick(() => {
            this.$refs.input.focus();
          });
        }
      }
    },
    onFocus() {},
    onBlur() {},
    handleSearch() {
      this.openSearchTabPage();
      this.showInput = false;
    },
    // 打开搜索标签页
    openSearchTabPage() {
      if (!this.widgetLayoutContext) {
        return;
      }
      const containerWid = this.widgetLayoutContext.widget.configuration.content.id;
      let options = {
        actionType: 'redirectPage',
        containerWid,
        eventParams: [],
        key: '_full-search-tab-key',
        meta: this.meta,
        name: '搜索页面2',
        pageId: 'page-full-search',
        pageName: '发起工作 v1.0',
        pageType: 'page',
        pageUuid: '326718168015831040',
        targetPosition: 'widgetLayout',
        title: '搜索页面',
        trigger: 'click'
      };

      let jsFunctionOptions = {
        actionType: 'jsFunction',
        actionTypeName: '脚本代码',
        containerWid,
        eventParams: [],
        key: '_full-search-tab-key',
        meta: {
          menu: {
            data: undefined,
            id: '_full-search-tab-key',
            title: '搜索页面'
          }
        },
        targetPosition: 'widgetLayout',
        title: '搜索页面',
        trigger: 'click'
      };
      if (this.__developScript['FullSearchCardDevelopment']) {
        jsFunctionOptions.$evtWidget = {
          developJsInstance: {
            FullSearchCardDevelopment: new this.__developScript['FullSearchCardDevelopment'].default(this)
          }
        };
        jsFunctionOptions.jsFunction = 'FullSearchCardDevelopment.createFullSearch';
      }

      const widgetRenderOptions = {
        actionType: 'widgetRender',
        widgets: [
          {
            id: '_full-search-tab-key',
            wtype: 'WidgetIconLib'
          }
        ],
        meta: {
          menu: {
            data: undefined,
            id: '_full-search-tab-key',
            title: '搜索页面'
          }
        },
        eventParams: [],
        title: '搜索页面',
        key: '_full-search-tab-key',
        targetPosition: 'widgetLayout',
        containerWid
      };
      this.dispatchEventHandler(options);
    },
    dispatchEventHandler(eventHandler) {
      if (eventHandler.pageContext == undefined) {
        eventHandler.pageContext = this.pageContext;
      }

      new DispatchEvent(eventHandler).dispatch();
    }
  }
};
</script>

<style lang="less">
.header-full-search-input {
  position: fixed;
  width: 560px;
  left: 50%;
  margin-left: -280px;
  z-index: 7;
}
.header-full-search-container {
  display: inline-flex;
  width: 96px;
  line-height: 40px;
}
.header-full-search() {
  display: flex;
  justify-content: center;
  width: 100%;
  cursor: pointer;
  > i {
    margin-right: 2px;
  }
}
.menu-sys-bar {
  &.primary-color {
    .header-full-search {
      .header-full-search();
      border-radius: 8px;
      background: rgba(255, 255, 255, 0.1);

      &:hover {
        background: rgba(255, 255, 255, 0.2);
      }
    }
    .header-full-search-input {
      .ant-input-affix-wrapper:hover .ant-input:not(.ant-input-disabled),
      .ant-input:hover {
        border-color: var(--w-input-border-color);
      }
      .ant-input:focus {
        border-color: var(--w-input-border-color);
        box-shadow: var(--w-input-box-shadow-hover);
      }
    }
  }
  &.light {
    .header-full-search {
      .header-full-search();
    }
  }
}
</style>
