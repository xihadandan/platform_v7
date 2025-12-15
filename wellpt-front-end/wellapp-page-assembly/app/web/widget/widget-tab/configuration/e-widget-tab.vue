<template>
  <EditWrapper
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :children="widget.configuration.tabs"
  >
    <div class="widget-tabs-container">
      <a-tabs
        v-if="designer.terminalType == 'pc'"
        hide-add
        :activeKey="activeKey"
        :tab-position="widget.configuration.tabPosition"
        :type="widget.configuration.tabStyleType"
        @change="onChange"
        :style="{ height: vHeight, minHeight: '100px' }"
        :size="widget.configuration.size"
        class="widget-tab"
      >
        <a-tab-pane v-for="(t, i) in widget.configuration.tabs" :key="t.id" :closable="t.configuration.closable" :forceRender="designMode">
          <template slot="tab">
            <div :class="['flex', widget.configuration.tabStyleType === 'editable-card' ? 'ant-dropdown-trigger' : '']">
              <div class="widget-tab-title f_g_1">
                <Icon :type="t.configuration.icon" v-if="t.configuration.icon" :size="iconSize" />
                {{ t.title }}
              </div>
            </div>
          </template>
          <template v-if="t.configuration.eventHandler.pageId">
            <!-- 页面加载 -->
            <div style="pointer-events: none; cursor: not-allowed !important">
              <WidgetVpage :pageId="t.configuration.eventHandler.pageId" :key="t.configuration.eventHandler.pageId" />
            </div>
          </template>
          <template v-else>
            <!-- 组件加载 -->
            <draggable
              :list="t.configuration.widgets"
              v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
              handle=".widget-drag-handler"
              @add="e => onDragAdd(e, t.configuration.widgets)"
              :move="onDragMove"
              @paste.native="onDraggablePaste"
              :class="[!t.configuration.widgets || t.configuration.widgets.length === 0 ? 'widget-edit-empty' : 'widget-edit-content']"
            >
              <transition-group
                :id="'tab-container' + t.id"
                name="fade"
                tag="div"
                :style="{ minHeight: '150px' }"
                :class="['widget-tab-item', tabItemSelected(t.id) ? 'tab-selected' : '']"
              >
                <template v-for="(wgt, w) in t.configuration.widgets">
                  <WDesignItem
                    :widget="wgt"
                    :key="wgt.id"
                    :index="w"
                    :widgetsOfParent="t.configuration.widgets"
                    :designer="designer"
                    :parent="t"
                    :dragGroup="dragGroup"
                  />
                </template>
              </transition-group>
            </draggable>
          </template>
        </a-tab-pane>

        <template slot="tabBarExtraContent">
          <div style="padding: 0px 8px; pointer-events: none" v-if="currentTab != undefined">
            <keyword-search :widget="currentTab" :configuration="currentTab.configuration" />
            <template v-if="currentTab.configuration && currentTab.configuration.tabButton && currentTab.configuration.tabButton.enable">
              <WidgetTableButtons :key="tabButtonKey" :button="currentTab.configuration.tabButton" :developJsInstance="developJsInstance" />
            </template>
          </div>
        </template>
      </a-tabs>
      <div v-else class="widget-uni-tab">
        <div class="tab-bar-wrapper">
          <template v-if="widget.configuration.uniConfiguration.tabStyleType == 'subsection'">
            <a-radio-group v-model="activeKey" button-style="solid">
              <template v-for="(tab, i) in widget.configuration.tabs">
                <a-radio-button :value="tab.id">{{ tab.title }}</a-radio-button>
              </template>
            </a-radio-group>
          </template>

          <template v-else>
            <div
              v-for="(tab, i) in widget.configuration.tabs"
              :key="'tab_' + i"
              :class="['text-tab', activeKey == tab.id ? 'actived' : '']"
              @click="activeKey = tab.id"
            >
              <span>
                {{ tab.title }}
              </span>
            </div>
          </template>
        </div>

        <template v-for="(tab, i) in widget.configuration.tabs">
          <div v-show="activeKey == tab.id">
            <template v-if="tab.configuration.eventHandler.pageId">
              <WidgetVpage :pageId="tab.configuration.eventHandler.pageId" :key="tab.configuration.eventHandler.pageId" />
            </template>
            <template v-else>
              <draggable
                :list="tab.configuration.widgets"
                v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
                handle=".widget-drag-handler"
                @add="e => onDragAdd(e, tab.configuration.widgets)"
                :move="onDragMove"
              >
                <transition-group name="fade" tag="div" :style="{ minHeight: '100px' }">
                  <template v-for="(wgt, i) in tab.configuration.widgets">
                    <WDesignItem
                      :widget="wgt"
                      :key="wgt.id"
                      :index="i"
                      :widgetsOfParent="tab.configuration.widgets"
                      :designer="designer"
                      :dragGroup="draggableConfig.dragGroup"
                      :parent="widget"
                    />
                  </template>
                </transition-group>
              </draggable>
            </template>
          </div>
        </template>
      </div>
    </div>
  </EditWrapper>
</template>
<style lang="less">
.widget-edit-wrapper {
  .widget-tab-item {
    &.selected {
      margin-left: 2px;
      margin-right: 2px;
    }
    > .design-row {
      > .widget-edit-wrapper {
        > .widget-edit-container > div > div {
          // padding: 0px;
        }
      }
    }
  }

  .widget-edit-container {
    .ant-tabs-content {
      > .ant-tabs-tabpane {
        padding: var(--w-padding-xs) var(--w-padding-md);
      }
      .unselectable-page-cover {
        width: e('calc(100% - var(--w-padding-md) * 2)');
        height: e('calc(100% - var(--w-padding-xs) * 2 - 55px)');
        &.selected {
          outline: 2px dashed var(--primary-color);
        }

        display: block;
        position: absolute;
        z-index: 2;
        cursor: not-allowed;
      }
      .unselectable-page-cover.left,
      .unselectable-page-cover.right {
        width: e('calc(100% - var(--w-padding-md) * 2 - 76px)');
        height: e('calc(100% - var(--w-padding-xs) * 2)');
        top: var(--w-padding-xs);
        right: var(--w-padding-md);
      }

      .unselectable-page-cover.right {
        right: unset;
      }
      .unselectable-page-cover.bottom {
        top: var(--w-padding-xs);
      }
    }
  }

  .widget-uni-tab {
    > .tab-bar-wrapper {
      --w-radio-button-solid-border-radius: 5px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 5px;
      .ant-radio-group {
        width: 100%;
        display: flex;
      }
      .ant-radio-button-wrapper {
        color: var(--w-primary-color);
        border: 1px solid var(--w-primary-color);
        flex: 1;
        text-align: center;
      }
      .ant-radio-button-wrapper-checked {
        color: #fff;
      }

      .text-tab {
        line-height: 20px;
        font-size: 14px;
        cursor: pointer;
        flex: 1;
        text-align: center;
        > span {
          padding-bottom: 4px;
          border-bottom: 2px solid transparent;
        }
        &.actived {
          color: var(--w-primary-color);
          > span {
            border-bottom: 2px solid var(--w-primary-color);
          }
        }
      }
    }
  }
}
</style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import { generateId } from '@framework/vue/utils/util';
import md5 from '@framework/vue/utils/md5';
import KeywordSearch from '../../widget-card/components/keyword-search.vue';

export default {
  name: 'EWidgetTab',
  mixins: [editWgtMixin, draggable],
  inject: ['vProvideStyle', 'designMode'],
  provide() {
    return {};
  },
  data() {
    return {
      activeKey: this.widget.configuration.tabs && this.widget.configuration.tabs.length > 0 ? this.widget.configuration.tabs[0].id : null
    };
  },
  components: {
    KeywordSearch
  },
  computed: {
    current() {
      for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
        if (this.widget.configuration.tabs[i].id == this.activeKey) {
          return i;
        }
      }
      return 0;
    },
    tabKeys() {
      let k = [];
      for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
        k.push(this.widget.configuration.tabs[i].id);
      }
      return k;
    },
    tabButtonKey() {
      let key = this.activeKey;
      if (this.currentTab && this.currentTab.configuration.tabButton) {
        key += '-' + md5(JSON.stringify(this.currentTab.configuration.tabButton));
      }
      return key;
    },
    currentTab() {
      for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
        if (this.widget.configuration.tabs[i].id == this.activeKey) {
          return this.widget.configuration.tabs[i];
        }
      }
    },
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      return [
        { id: 'refetchBadge', title: '刷新徽章数量' },
        {
          id: 'setTabVisible',
          title: '设置页签是否显示',
          eventParams: [
            {
              paramKey: 'id',
              remark: '通过页签ID',
              valueSource: {
                inputType: 'select',
                options: (() => {
                  let scope = [];
                  if (this.widget.configuration.tabs) {
                    for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
                      scope.push({
                        value: this.widget.configuration.tabs[i].id,
                        label: this.widget.configuration.tabs[i].title
                      });
                    }
                  }
                  return scope;
                })()
              }
            },
            {
              paramKey: 'title',
              remark: '通过页签名称',
              valueSource: {
                inputType: 'select',
                options: (() => {
                  let scope = [];
                  if (this.widget.configuration.tabs) {
                    for (let i = 0, len = this.widget.configuration.tabs.length; i < len; i++) {
                      scope.push({
                        value: this.widget.configuration.tabs[i].title,
                        label: this.widget.configuration.tabs[i].title
                      });
                    }
                  }
                  return scope;
                })()
              }
            },
            {
              paramKey: 'visible',
              remark: '显示或者隐藏',
              valueSource: {
                inputType: 'select',
                options: [
                  { label: '必填', value: 'true' },
                  { label: '非必填', value: 'false' }
                ]
              }
            }
          ]
        }
      ];
    },
    vHeight() {
      if (this.widget.configuration.height == 'auto' || this.widget.configuration.height == undefined) {
        return 'auto';
      }
      let padding =
        this.widget.configuration.style != undefined && this.widget.configuration.style.padding != undefined
          ? this.widget.configuration.style.padding[0] + this.widget.configuration.style.padding[2]
          : 0;
      return `calc( ${this.widget.configuration.height} - ${padding}px )`;
    },
    iconSize() {
      return { small: 20, default: 20, large: 24 }[this.widget.configuration.size];
    }
  },
  watch: {
    tabKeys: {
      handler() {
        // 删除了当前激活的标签页，则默认选择第一个
        if (!this.tabKeys.includes(this.activeKey) && this.tabKeys.length > 0) {
          this.activeKey = this.tabKeys[0];
        }
      }
    },
    'widget.configuration.defaultActiveKey': {
      handler(v) {
        if (v != null) {
          this.activeKey = v;
        }
      }
    }
  },
  methods: {
    onChange(activeKey) {
      this.activeKey = activeKey;
      this.pageContext.emitEvent(`${this.widget.id}:change`, activeKey);
    },
    tabItemSelected(id) {
      if (this.designer.selectedId == id) {
        this.activeKey = id;
        return true;
      }
      return false;
    }
  }
};
</script>
