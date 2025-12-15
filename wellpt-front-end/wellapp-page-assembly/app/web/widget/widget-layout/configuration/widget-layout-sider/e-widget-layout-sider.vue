<template>
  <EditWrapper
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
    :enableDrag="false"
    :enableDelete="false"
    :unselectable="layoutFixed && parent.main"
    class="widget-layout-sider-container"
  >
    <a-layout-sider
      :collapsible="widget.configuration.collapsible"
      v-model="collapsed"
      :theme="vSiderTheme"
      :style="{
        height: 'calc(100vh' + ' - ' + vSiderHeight + ')'
      }"
      :collapsedWidth="64"
      :width="224"
      :class="['widget-layout-sider', vBgColorType, getDropdownClassName()]"
      :trigger="null"
    >
      <!-- <template slot="trigger">
        <a-tooltip placement="right" :overlayStyle="{ zIndex: 10000 }">
          <template slot="title">
            <span>收起</span>
          </template>
          <a-icon type="menu-fold" />
        </a-tooltip>
      </template> -->
      <PerfectScrollbar
        :style="{
          height: 'calc(100vh' + ' - ' + vSiderHeight + ' - 10px)'
        }"
      >
        <div class="logo" v-if="vSiderLogo">
          <img :src="vSiderLogo" @load="getImageDimensions" :style="vSiderLogoStyle" />
        </div>
        <draggable
          :list="widget.configuration.widgets"
          v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
          @end="evt => onGridDragEnd(evt, widget.configuration.widgets)"
          @add="evt => onGridDragAdd(evt, widget.configuration.widgets)"
          @update="onGridDragUpdate"
          @paste.native="onDraggablePaste"
        >
          <transition-group
            name="fade"
            tag="div"
            class="widget-layout-sider-drop-panel"
            :style="{
              minHeight: 'calc(100vh' + ' - ' + vSiderHeight + ' - 15px)'
            }"
          >
            <template v-for="(wgt, i) in widget.configuration.widgets">
              <WDesignItem
                :widget="wgt"
                :key="wgt.id"
                :index="i"
                :widgetsOfParent="widget.configuration.widgets"
                :designer="designer"
                :parent="widget"
                :dragGroup="dragGroup"
              />
            </template>
          </transition-group>
        </draggable>
      </PerfectScrollbar>
    </a-layout-sider>
    <Icon
      v-if="widget.configuration.collapsible"
      :type="collapsed ? 'double-right' : 'double-left'"
      :class="['sider-trigger']"
      @click="changeCollapsed"
    />
  </EditWrapper>
</template>
<style lang="less">
.widget-edit-wrapper .widget-layout-sider {
  border: 1px dashed;
}

.widget-edit-wrapper .widget-layout-sider .ps {
  width: 100%;
  padding: 2px;
  position: absolute;
}
.widget-edit-wrapper.widget-layout-sider-container {
  &:hover {
    .sider-trigger {
      opacity: 1;
    }
  }
  .widget-layout-sider.dark {
    & + .sider-trigger {
      background-color: var(--w-color-dark);
      border: 1px solid var(--w-color-dark);
      color: #fff;
    }
  }
  .widget-layout-sider.primary-color {
    & + .sider-trigger {
      background-color: var(--w-primary-color);
      border: 1px solid var(--w-primary-color);
      color: #fff;
    }
  }
  .widget-layout-sider.light {
    & + .sider-trigger {
      background-color: var(--w-color-light);
    }
  }
  .widget-layout-sider.ant-layout-sider-collapsed + .sider-trigger {
    left: 64px;
  }
  .sider-trigger {
    position: absolute;
    top: 50%;
    z-index: 2;
    margin-left: -1px;
    border-radius: 0px 4px 4px 0px;
    height: 48px;
    width: 20px;
    line-height: 48px;
    text-align: center;
    margin-left: -1px;
    background-color: var(--w-fill-color-base);
    color: var(--w-text-color-dark);
    border: 1px solid var(--w-border-color-lighter);
    border-left: 0;
    font-size: 10px;
    opacity: 0;
    left: 224px;
    transition: left 0.15s linear, opacity 0.3s linear;
    cursor: pointer;
  }
}
</style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import { addWindowResizeHandler } from '@framework/vue/utils/util';
import { getDropdownClassName } from '@framework/vue/utils/function';

export default {
  name: 'EWidgetLayoutSider',
  mixins: [editWgtMixin, draggable],
  inject: ['layoutFixed'],
  props: {
    backgroundColorType: String
  },
  data() {
    return {
      scrollerHeight: '300px',
      collapsed: this.widget.configuration.defaultCollapsed === true,
      logoNaturalWidth: 1,
      logoNaturalHeight: 1,
      collapsedLoading: false
    };
  },
  provide() {
    return {
      backgroundColorType: () => {
        return this.vBgColorType;
      }
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vSiderHeight() {
      let num = 120;
      if (this.parent.configuration.layoutType == 'siderTopMiddleBottom') {
        return num + 'px';
      }
      if (this.parent.configuration.header.configuration.visible) {
        num += 60;
      }
      if (this.parent.configuration.footer.configuration.visible) {
        num += 40;
      }
      return num + 'px';
    },
    vSiderLogo() {
      if (this.parent.configuration.logoPosition == 'sider') {
        let bgColorType = this.vBgColorType;
        if (bgColorType == 'primary-color') {
          // 左导航情况下的主题色logo, 按深色logo处理
          bgColorType = 'dark';
        }
        let bgTypeLogo = this.widget.configuration[`${bgColorType}BgColorTypeLogo`];

        if (bgTypeLogo !== undefined) {
          return bgTypeLogo;
        }
        return this.parent.configuration.header.configuration.logo;
      }
      return undefined;
    },
    vSiderLogoStyle() {
      let vSiderLogoStyle = {};
      if (this.collapsed) {
        vSiderLogoStyle = { maxWidth: '100%' };
      } else {
        if (this.logoNaturalWidth / this.logoNaturalHeight < 1.2 && this.logoNaturalWidth / this.logoNaturalHeight > 0.8) {
          vSiderLogoStyle = { maxWidth: '120px' };
        } else if (this.logoNaturalWidth > this.logoNaturalHeight) {
          vSiderLogoStyle = { maxWidth: '100%' };
        } else {
          vSiderLogoStyle = { maxHeight: 'calc(224px - 40px)' };
        }
      }
      return vSiderLogoStyle;
    },
    vBgColorType() {
      return this.backgroundColorType || this.widget.configuration.backgroundColorType;
    },
    vSiderTheme() {
      return this.vBgColorType == 'primary-color' ? 'light' : this.vBgColorType;
    }
  },
  created() {},
  methods: {
    getDropdownClassName,
    getWidgetDefinitionElements(widget) {
      // 由布局组件提供
      return [];
    },
    onGridDragEnd(evt, subWidgets) {
      //
    },

    onGridDragEnd(evt, subWidgets) {
      //
    },

    onGridDragAdd(evt, subWidgets) {
      const newIndex = evt.newIndex;
      if (!!subWidgets[newIndex]) {
        this.designer.setSelected(subWidgets[newIndex]);
      }

      // this.designer.emitEvent('field-selected', this.widget);
    },

    onGridDragUpdate() {},
    updateHeight() {
      //TODO
    },
    getImageDimensions(event) {
      const img = event.target;
      this.logoNaturalWidth = img.naturalWidth;
      this.logoNaturalHeight = img.naturalHeight;
    },
    changeCollapsed() {
      if (!this.collapsedLoading) {
        this.collapsed = !this.collapsed;
        this.collapsedLoading = true;
      } else {
        this.$nextTick(() => {
          this.collapsedLoading = false;
        });
      }
    }
  },
  mounted() {
    this.scrollerHeight = window.innerHeight - 233 + 'px';
    addWindowResizeHandler(() => {
      this.$nextTick(() => {
        this.scrollerHeight = window.innerHeight - 233 + 'px';
      });
    });

    if (this.layoutFixed && this.parent.main) {
      this.designer.unselectableWidgetIds.push(this.widget.id);
    }
  }
};
</script>
