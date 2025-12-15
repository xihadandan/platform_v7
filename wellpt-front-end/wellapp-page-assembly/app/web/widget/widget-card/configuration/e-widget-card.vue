<template>
  <EditWrapper :widget="widget" :index="index" :widgetsOfParent="widgetsOfParent" :designer="designer" :parent="parent">
    <a-card
      class="widget-card"
      :size="widget.configuration.size"
      :bordered="widget.configuration.bordered"
      :bodyStyle="{ height: bodyHeight, minHeight: widget.configuration.height == 'auto' ? '100px' : 'unset', ...mobileBodyStyle }"
    >
      <template v-if="!hideHeader">
        <template slot="title" v-if="!widget.configuration.hideTitle">
          <Icon :type="widget.configuration.titleIcon" :size="widget.configuration.size" />
          {{ widget.configuration.title }}
          <div v-if="!widget.configuration.hideSubTitle && widget.configuration.subTitle" style="color: rgba(0, 0, 0, 0.45)">
            {{ widget.configuration.subTitle }}
          </div>
        </template>
        <template slot="extra">
          <keyword-search :widget="widget" :configuration="widget.configuration" />
          <template v-if="widget.configuration.headerButton && widget.configuration.headerButton.enable">
            <WidgetTableButtons :button="widget.configuration.headerButton" :key="wButtonKey" />
          </template>
        </template>
      </template>
      <div>
        <draggable
          :list="widget.configuration.widgets"
          v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
          handle=".widget-drag-handler"
          @add="e => onDragAdd(e, widget.configuration.widgets)"
          @paste.native="onDraggablePaste"
          :move="onDragMove"
          :class="[
            !widget.configuration.widgets || widget.configuration.widgets.length === 0 ? 'widget-edit-empty' : 'widget-edit-content'
          ]"
        >
          <transition-group
            name="fade"
            tag="div"
            class="widget-card-drop-panel"
            :style="
              widget.configuration.height == 'auto'
                ? {
                    minHeight: '100px'
                  }
                : {}
            "
          >
            <template v-for="(wgt, i) in widget.configuration.widgets">
              <WDesignItem
                :widget="wgt"
                :key="wgt.id"
                :index="i"
                :widgetsOfParent="widget.configuration.widgets"
                :designer="designer"
                :dragGroup="dragGroup"
                :parent="widget"
              />
            </template>
          </transition-group>
        </draggable>
      </div>
    </a-card>
  </EditWrapper>
</template>

<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import md5 from '@framework/vue/utils/md5';
import KeywordSearch from '../components/keyword-search.vue';

export default {
  name: 'EWidgetCard',
  mixins: [editWgtMixin, draggable],
  inject: ['vProvideStyle'],
  data() {
    return {};
  },
  components: {
    KeywordSearch
  },
  computed: {
    wButtonKey() {
      if (this.widget.configuration.headerButton != undefined) {
        return md5(JSON.stringify(this.widget.configuration.headerButton));
      }
      return this.widget.id + '_headerButtons';
    },
    bodyHeight() {
      if (this.widget.configuration.height == 'auto' || this.widget.configuration.height == undefined) {
        return 'auto';
      }
      let padding = this.widget.configuration.style.padding[0] + this.widget.configuration.style.padding[2];
      return `calc( ${this.widget.configuration.height} - ${padding}px )`;
    },
    hideHeader() {
      if (this.designer.terminalType == 'mobile') {
        if (this.widget.configuration.uniConfiguration.hideCard) {
          return true;
        } else if (this.widget.configuration.uniConfiguration.hideCardHeader) {
          return true;
        }
      }
      return false;
    },
    mobileBodyStyle() {
      let style = {};
      if (this.designer.terminalType == 'mobile') {
        if (this.widget.configuration.uniConfiguration.hideCard || this.widget.configuration.uniConfiguration.hideCardBody) {
          return { padding: '1px' };
        }
      }
      return style;
    },
    // 相关方法暴露为事件，提供外部调用
    defaultEvents() {
      return [{ id: 'refetchBadge', title: '刷新徽章数量' }];
    }
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', {
        hideCard: false,
        hideCardHeader: false,
        hideCardBody: false
      });
    }
  }
};
</script>

<style lang="less" scoped>
.widget-card.ant-card-large {
  ::v-deep .ant-card-head {
    min-height: 60px;
    padding: 0 36px;
    font-size: 18px;
  }

  ::v-deep .ant-card-body {
    padding: 36px;
  }
}
</style>
