<template>
  <div
    class="widget-uni-navbar"
    :style="{
      backgroundColor,
      color: backgroundColor ? '#fff' : ''
    }"
  >
    <div class="_navbar-header">
      <div class="_left-container" v-if="widget.configuration.enabledLeftContainer">
        <div class="_left-back" v-if="widget.configuration.enabledBack">
          <Icon :type="widget.configuration.backButtonIcon" :size="20" />
          <div class="back-button-name" v-if="widget.configuration.enabledBackButton">
            {{ widget.configuration.backButtonName }}
          </div>
        </div>
        <template v-if="widget.configuration.enabledLogo && widget.configuration.logoIcon">
          <img
            v-if="widget.configuration.logoIcon.startsWith('/proxy-repository/')"
            :src="widget.configuration.logoIcon"
            style="height: 20px"
          />
          <Icon v-else :type="widget.configuration.logoIcon" :size="20" />
        </template>
      </div>
      <div class="_middle-container" v-if="widget.configuration.enabledMiddleContainer">
        <template v-if="widget.configuration.enabledMiddleTitle">
          <Icon :type="widget.configuration.addonBeforeIcon" v-show="widget.configuration.addonBeforeIcon" :size="20" />
          <span class="middle-title">{{ widget.configuration.middleTitle }}</span>
          <Icon :type="widget.configuration.addonAfterIcon" v-show="widget.configuration.addonAfterIcon" :size="20" />
        </template>
        <template v-if="widget.configuration.enabledTabs">
          <div class="widget-uni-tab">
            <div class="tab-bar-wrapper">
              <template v-if="widgetTabs.configuration.uniConfiguration.tabStyleType == 'subsection'">
                <a-radio-group v-model="activeKey" button-style="solid">
                  <template v-for="(tab, i) in widgetTabs.configuration.tabs">
                    <a-radio-button :value="tab.id">{{ tab.title }}</a-radio-button>
                  </template>
                </a-radio-group>
              </template>

              <template v-else>
                <div
                  v-for="(tab, i) in widgetTabs.configuration.tabs"
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
          </div>
        </template>
      </div>
      <div class="_right-container" v-show="widget.configuration.enabledRightContainer">
        <template v-if="widget.configuration.rightButtonConfig && widget.configuration.rightButtonConfig.enable">
          <WidgetTableButtons :button="widget.configuration.rightButtonConfig" :designModeIsDisabled="true" />
        </template>
        <draggable
          v-show="widget.configuration.enabledRightWidgets"
          :list="widget.configuration.rightWidgets"
          v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
          handle=".widget-drag-handler"
          @add="e => onDragAdd(e, widget.configuration.rightWidgets)"
          :move="onDragMove"
          :class="[
            !widget.configuration.rightWidgets || widget.configuration.rightWidgets.length === 0
              ? 'widget-edit-empty'
              : 'widget-edit-content'
          ]"
        >
          <transition-group name="fade" tag="div" :style="{ minHeight: '30px' }">
            <template v-for="(wgt, i) in widget.configuration.rightWidgets">
              <WDesignItem
                :widget="wgt"
                :key="wgt.id"
                :index="i"
                :widgetsOfParent="widget.configuration.rightWidgets"
                :designer="designer"
                :dragGroup="draggableConfig.dragGroup"
                :parent="widget"
              />
            </template>
          </transition-group>
        </draggable>
        <div v-if="widget.configuration.enabledRightSelect" class="right-select-container">
          <a-select placeholder="选择" />
        </div>
      </div>
    </div>

    <!-- 标签页 -->
    <template v-if="widget.configuration.enabledTabs">
      <div v-for="(tab, i) in widgetTabs.configuration.tabs" :key="i" v-show="activeKey == tab.id">
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
            :class="[!tab.configuration.widgets || tab.configuration.widgets.length === 0 ? 'widget-edit-empty' : 'widget-edit-content']"
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
</template>

<script>
import draggable from '@framework/vue/designer/draggable';
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import WidgetUniNavBarConfiguration from './configuration/index.vue';

export default {
  name: 'WidgetUniNavBar',
  inject: ['draggableConfig'],
  mixins: [editWgtMixin, draggable],
  data() {
    return {
      activeKey: '',
      widgetTabs: this.widget.configuration.widgetTabs
    };
  },
  computed: {
    backgroundColor() {
      let color = '',
        backgroundColor = this.widget.configuration.backgroundColor;
      if (backgroundColor) {
        color = this.getColorValue(backgroundColor);
      }
      return color;
    },
    tabKeys() {
      let k = [];
      if (this.widgetTabs) {
        for (let i = 0, len = this.widgetTabs.configuration.tabs.length; i < len; i++) {
          k.push(this.widgetTabs.configuration.tabs[i].id);
        }
      }
      return k;
    },
    defaultEvents() {
      const configuration = this.widget.configuration;
      let events = [];
      if (configuration.widgetSelect) {
        const selectWgtId = configuration.widgetSelect.id;
        let selectEvents = [
          {
            id: `${selectWgtId}:change`,
            title: '监听下拉框值变更',
            codeSnippet: `
            /**
             * 监听下拉框值变更
             */
            this.pageContext.handleEvent({{事件编码}}, ({value,optionItem}) => {
            
            });
            `
          }
          // {
          //   id: `${selectWgtId}:setValue`,
          //   title: '下拉框设值',
          //   codeSnippet: `
          //   /**
          //    * 下拉框设值
          //    */
          //   this.pageContext.emitEvent({{事件编码}});
          //   `
          // }
        ];
        events = [...selectEvents];
      }
      return events;
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
    }
  },
  created() {
    this.activeKey = this.getActiveKey();
    this.addProperty();
  },
  mounted() {
    if (this.widget.configuration.enabledTabs && this.widgetTabs) {
      this.$watch('widgetTabs.configuration', (newValue, oldValue) => {}, {
        deep: true
      });
    }
  },
  methods: {
    addProperty() {
      if (this.widget.configuration.rightButtonConfig === undefined) {
        this.$set(this.widget.configuration, 'rightButtonConfig', WidgetUniNavBarConfiguration.createButtonConfig());
      }
      if (this.widget.configuration.widgetTabs === undefined) {
        this.$set(this.widget.configuration, 'widgetTabs', WidgetUniNavBarConfiguration.generateWidgetTabConfiguration());
      }
      if (this.widget.configuration.widgetSelect === undefined) {
        this.$set(this.widget.configuration, 'widgetSelect', WidgetUniNavBarConfiguration.generateWidgetSelectConfiguration());
      }
    },
    getActiveKey() {
      let active = null;
      if (this.widgetTabs && this.widgetTabs.configuration.tabs && this.widgetTabs.configuration.tabs.length > 0) {
        active = this.widgetTabs.configuration.defaultActiveKey;
        // active = this.widgetTabs.configuration.tabs[0].id;
      }
      return active;
    },
    getColorValue(color) {
      if (color) {
        return color.startsWith('#') ? color : `var(${color})`;
      }
      return '';
    }
  }
};
</script>

<style lang="less">
/* 处理tab点击不到 */
.widget-edit-container {
  &:has(.widget-uni-navbar) {
    > div {
      &:first-child {
        display: none !important;
      }
    }
  }
}
.widget-uni-navbar {
  ._navbar-header {
    position: relative;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 12px;
    height: 44px;
  }
  ._left-container {
    display: flex;
    align-items: center;
    > ._left-back {
      display: flex;
      align-items: center;
    }
    .back-button-name {
      line-height: 20px;
      font-size: 14px;
      margin-right: 2px;
    }
  }
  ._middle-container {
    position: absolute;
    left: 78px;
    right: 78px;
    z-index: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    > .middle-title {
      padding: 0 4px;
    }
  }
  ._right-container {
    width: 78px;
    flex: 1;
    display: flex;
    justify-content: flex-end;
    > .widget-edit-empty {
      min-height: initial;
      min-width: 64px;
      &::after {
        content: '拖拽组件';
      }
    }
  }

  .widget-uni-tab {
    max-width: 100%;
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
        line-height: 34px;
        font-size: 14px;
        cursor: pointer;
        flex: 1;
        text-align: center;
        margin: 0 12px;
        white-space: nowrap;
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

  .right-select-container {
    position: relative;
    width: 55px;
    &::before {
      content: '';
      position: absolute;
      z-index: 3;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
    }
    > .ant-select {
      width: 100%;
      .ant-select-selection {
        border-color: transparent;
      }
      .ant-select-selection__rendered {
        margin-left: 0;
        margin-right: 13px;
      }
    }
  }
}
</style>
