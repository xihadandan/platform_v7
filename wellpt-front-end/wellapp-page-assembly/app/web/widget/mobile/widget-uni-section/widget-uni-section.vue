<template>
  <div
    class="widget-uni-section"
    :style="{
      borderRadius: formatBorderRadius(widget.configuration.borderRadius)
    }"
  >
    <div
      class="header"
      v-if="widget.configuration.title || widget.configuration.subTitle || widget.configuration.decorationType"
      :style="{
        backgroundImage: widget.configuration.backgroundImage ? 'url(' + widget.configuration.backgroundImage + ')' : 'unset',
        backgroundRepeat: 'no-repeat',
        backgroundSize: 'cover'
      }"
    >
      <div :class="['decoration', widget.configuration.decorationType]">
        <Icon :type="widget.configuration.icon.src" />
        <template v-if="widget.configuration.decorationType == 'custom'">
          <!-- 图片 -->
          <img :src="widget.configuration.icon.src" v-if="widget.configuration.icon.src.startsWith('/')" />
          <Icon :type="widget.configuration.icon.src" v-else />
        </template>
      </div>
      <div style="width: 100%">
        <div class="title" :style="{ color: widget.configuration.titleColor }">
          {{ widget.configuration.title }}
          <span v-if="widget.configuration.titleClickable && (widget.configuration.titleClickText || widget.configuration.titleClickIcon)">
            {{ widget.configuration.titleClickText }}
            <Icon :type="widget.configuration.titleClickIcon" />
          </span>
        </div>
        <div class="subTitle" :style="{ color: widget.configuration.subTitleColor }">{{ widget.configuration.subTitle }}</div>
      </div>
    </div>

    <div v-show="widget.configuration.allowContent" class="content">
      <draggable
        :list="widget.configuration.widgets"
        v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
        handle=".widget-drag-handler"
        @add="e => onDragAdd(e, widget.configuration.widgets)"
        :move="onDragMove"
      >
        <transition-group name="fade" tag="div" :style="{ minHeight: '100px' }">
          <template v-for="(wgt, i) in widget.configuration.widgets">
            <WDesignItem
              :widget="wgt"
              :key="wgt.id"
              :index="i"
              :widgetsOfParent="widget.configuration.widgets"
              :designer="designer"
              :dragGroup="draggableConfig.dragGroup"
              :parent="widget"
            />
          </template>
        </transition-group>
      </draggable>
    </div>
  </div>
</template>
<style lang="less">
.widget-uni-section {
  background-color: transparent;
  overflow: hidden;
  margin-bottom: 12px;
  > .header {
    display: flex;
    align-items: center;
    padding: 12px 10px;
    .decoration {
      margin-right: 6px;
      background-color: #2979ff;
      &.line {
        width: 4px;
        height: 12px;
        border-radius: 10px;
      }

      &.circle {
        width: 6px;
        height: 6px;
        border-radius: 50%;
      }

      &.square {
        width: 8px;
        height: 8px;
      }
      &.custom {
        width: 40px;
        height: 40px;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 8px;
        background-color: unset;
        > img {
          width: 100%;
          height: 100%;
          object-fit: contain;
        }
      }
    }

    .title {
      color: rgb(51, 51, 51);
      display: flex;
      justify-content: space-between;
      width: 100%;
    }
    .subTitle {
      font-size: 12px;
      color: rgb(153, 153, 153);
    }
  }
}
</style>
<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';

export default {
  name: 'WidgetUniSection',
  inject: ['draggableConfig'],
  mixins: [editWgtMixin, draggable],
  props: {},
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    formatBorderRadius(borderRadius) {
      if (borderRadius != undefined) {
        if (Array.isArray(borderRadius)) {
          return borderRadius
            .map(item => {
              return item + 'px';
            })
            .join(' ');
        } else {
          return borderRadius + 'px';
        }
      }
      return undefined;
    }
  }
};
</script>
