<template>
  <a-layout-header
    :class="['widget-layout-header', vBgColorType, selected ? 'selected' : '']"
    :style="vHeaderStyle"
    @click.native.stop="selectWidget(widget)"
    v-if="widget.configuration.visible"
  >
    <span class="widget-title" v-show="selected">{{ widget.title }}</span>
    <div class="widget-operation-buttons" v-show="selected">
      <a-button size="small" icon="vertical-align-top" v-show="hasParent" title="选中父组件" @click.stop="selectWidget(parent)"></a-button>
      <a-button size="small" icon="profile" type="danger" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
    </div>
    <div class="header-main">
      <div class="logo" v-if="vHeaderLogo != undefined">
        <img :src="vHeaderLogo" :style="vHeaderImgStyle" />
      </div>
      <h1 class="title" v-if="widget.configuration.title">
        {{ widget.configuration.title }}
      </h1>
      <draggable
        :list="headerWidgets"
        v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
        @end="evt => onGridDragEnd(evt, headerWidgets)"
        @add="evt => onGridDragAdd(evt, headerWidgets)"
        @update="onGridDragUpdate"
        style="flex: 1"
        @paste.native="onDraggablePaste"
      >
        <transition-group name="fade" tag="div" class="grid-col-drop-panel">
          <template v-for="(wgt, i) in headerWidgets">
            <WDesignItem
              :widget="wgt"
              :key="wgt.id"
              :index="i"
              :widgetsOfParent="headerWidgets"
              :designer="designer"
              :parent="widget"
              :dragGroup="dragGroup"
            />
          </template>
        </transition-group>
      </draggable>
    </div>
  </a-layout-header>
</template>
<style lang="less" scoped>
.widget-edit-wrapper .widget-layout-header {
  padding: 2px;
  border: 1px dashed;
}
.widget-edit-wrapper .grid-col-drop-panel {
  height: 100%;
}
.widget-layout-header.selected .widget-title {
  top: 0px;
  line-height: normal;
}

.widget-layout-header.selected .widget-operation-buttons {
  line-height: 0px;
  top: 0px;
}
</style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import { deepClone, generateId } from '@framework/vue/utils/util';

export default {
  name: 'EWidgetLayoutHeader',
  mixins: [editWgtMixin, draggable],
  inject: ['layoutFixed'],
  props: {
    backgroundColorType: String
  },
  data() {
    let headerWidgets = this.widget.configuration.widgets,
      menuBarWidgets = [];
    if (this.parent.main) {
      if (this.widget.configuration.showHeaderMenuBar) {
        // 系统首页情况下的菜单栏展示
        // 显示菜单栏，则把菜单导航的产品菜单拆分到菜单栏内显示，系统导航顶部展示
        let widgetMenu = deepClone(this.widget.configuration.widgets[0]);
        widgetMenu.configuration.mode = 'horizontal';
        widgetMenu.configuration.enableSysMenu = true;
        widgetMenu.configuration.menus = []; // 置空导航菜单，仅保留系统级导航
        headerWidgets = [widgetMenu];
        // 创建导航菜单
        let navWidget = deepClone(this.widget.configuration.widgets[0]);
        navWidget.id = generateId();
        navWidget.configuration.sysFunctionMenus = []; // 置空系统级导航，保留导航菜单
        navWidget.configuration.mode = 'horizontal';
        navWidget.configuration.enableSysMenu = false;
        menuBarWidgets.push(navWidget);
      }
      // 从侧边栏获取导航组件的系统级导航生成到头部展示
      let widgetMenu = this.parent.configuration.sider.configuration.widgets[0];
      if (widgetMenu && widgetMenu.main) {
        let headerSysMenu = deepClone(widgetMenu);
        headerSysMenu.id = generateId();
        headerSysMenu.configuration.menus = [];
        headerSysMenu.configuration.enableSysMenu = true;
        headerSysMenu.configuration.mode = 'horizontal';
        headerWidgets = [headerSysMenu];
      }
    }

    return { headerWidgets, menuBarWidgets };
  },
  provide() {
    return {
      backgroundColorType: this.vSubBgColorType
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vHeaderImgStyle() {
      // 初始根据图片比例计算最小宽度，避免图片加载前后导致后面的dom元素移动现象
      let minWidth = 'fit-content';
      let bgColorType = this.vBgColorType;
      if (bgColorType == 'primary-color') {
        // 头部的主题色logo，按暗色logo处理
        bgColorType = 'dark';
      }
      let bgTypeLogo = this.widget.configuration[`${bgColorType}BgColorTypeLogo`];
      if (bgTypeLogo !== undefined) {
        // 存在具体背景色的logo
        if (this.widget.configuration[`${bgColorType}BgColorTypeLogoAspectRatio`] != undefined) {
          minWidth = Math.ceil(36 * this.widget.configuration[`${bgColorType}BgColorTypeLogoAspectRatio`]) + 'px';
        }
      } else if (this.widget.configuration.logoAspectRatio != undefined) {
        minWidth = Math.ceil(36 * this.widget.configuration.logoAspectRatio) + 'px';
      }
      return {
        minWidth
      };
    },
    vHeaderLogo() {
      if (this.parent.configuration.logoPosition == 'header') {
        let bgColorType = this.vBgColorType;
        if (bgColorType == 'primary-color') {
          // 头部的主题色logo，按暗色logo处理
          bgColorType = 'dark';
        }
        let bgTypeLogo = this.widget.configuration[`${bgColorType}BgColorTypeLogo`];
        if (bgTypeLogo !== undefined) {
          return bgTypeLogo;
        }
        return this.widget.configuration.logo;
      }
      return undefined;
    },
    vHeaderStyle() {
      let style = {};
      // if (this.widget.configuration.fixed) {
      //   style.position = 'fixed';
      //   style.top = '0px';
      //   if (this.parent.configuration.layoutType == 'topMiddleBottom') {
      //     // 没有侧边栏
      //     style.width = '100%';
      //   }
      // }
      if (this.widget.configuration.bgImage) {
        style.backgroundImage = `url(${this.widget.configuration.bgImage})`;
        style.border = 'unset';
      }
      return style;
    },
    vSubBgColorType() {
      return this.widget.configuration.bgImage ? 'transparent' : this.vBgColorType;
    },
    vBgColorType() {
      return this.backgroundColorType || this.widget.configuration.backgroundColorType;
    }
  },
  created() {},
  methods: {
    selectWidget(wgt) {
      if (this.layoutFixed && this.parent.main) {
        return;
      }
      this.designer.setSelected(wgt);
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
    getWidgetDefinitionElements(widget) {
      // 由布局组件提供
      return [];
    }
  },
  mounted() {
    if (this.layoutFixed && this.parent.main) {
      this.designer.unselectableWidgetIds.push(this.widget.id);
    }
  }
};
</script>
