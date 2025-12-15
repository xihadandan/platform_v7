<template>
  <PerfectScrollbar class="flow-selection-panel-scroll">
    <a-icon
      type="double-left"
      :class="['left-collapse', collapse ? 'collapsed' : '']"
      @click.stop="onClickCollapse"
      :title="collapse ? '点击展开' : '点击收缩'"
    />
    <div class="flow-selection-panel">
      <template v-if="collapse">
        <div class="selection-type-mini" v-for="panel in selectionType" :key="panel.type">
          <a-tooltip v-for="(item, index) in panel.list" :key="index" :title="item.title" :arrowPointAtCenter="true" placement="right">
            <div
              :class="{ 'selection-mini': true, active: curType === item.type }"
              :draggable="!!item.draggable"
              @click="handleSelectType(item.type)"
              @dragstart="onDragstart($event, item.type)"
              @dragend="onDragend($event, item.type)"
            >
              <i v-if="item.icon" :class="['iconfont', item.icon]"></i>
              <div v-else-if="item.svg" :class="`svg-icon svg-icon-${item.svg}`" v-html="svgIcons[item.svg]"></div>
            </div>
          </a-tooltip>
        </div>
      </template>
      <template v-else>
        <div class="selection-type-container" v-for="panel in selectionType" :key="panel.type">
          <div class="selection-type-title">{{ panel.title }}</div>
          <div class="selection-container">
            <div
              v-for="(item, index) in panel.list"
              :key="index"
              :class="{ 'selection-item': true, active: curType === item.type }"
              :draggable="!!item.draggable"
              @click="handleSelectType(item.type)"
              @dragstart="onDragstart($event, item.type)"
              @dragend="onDragend($event, item.type)"
            >
              <i v-if="item.icon" :class="['iconfont', item.icon]"></i>
              <div v-else-if="item.svg" :class="`svg-icon svg-icon-${item.svg}`" v-html="svgIcons[item.svg]"></div>
              <div class="selection-name">
                {{ item.title }}
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </PerfectScrollbar>
</template>

<script>
import { selectionType } from '../designer/constant';

const reqFiles = require.context('./svg', false, /\.js$/);
const re = /\.\/(.*)\.js/;
const svgIcons = reqFiles.keys().reduce((files, filePath) => {
  const fileName = filePath.match(re)[1];
  const value = reqFiles(filePath);
  files[fileName] = value.default;
  return files;
}, {});

export default {
  name: 'FlowSelectionPanel',
  inject: ['graph'],
  props: {
    graphItem: {
      type: Object,
      default: () => {}
    },
    panelW: {
      type: Number,
      default: 224
    }
  },
  data() {
    return {
      collapse: false,
      selectionType,
      svgIcons
    };
  },
  computed: {
    curType() {
      let selectedTool = '';
      if (this.graphItem) {
        selectedTool = this.graphItem.selectedTool;
      }
      return selectedTool;
    }
  },
  methods: {
    // 选择工具
    handleSelectType(type) {
      if (!this.graphItem) {
        return;
      }
      if (type === 'DEFAULT') {
        this.graphItem.removeUnselectCell();
      }
      if (type === this.curType) {
        type = '';
      }
      this.graphItem.setSelectedTool(type);
    },
    // 折叠
    onClickCollapse() {
      this.collapse = !this.collapse;
      // this.collapseOrExpand(this.collapse ? 0 : this.panelW);
    },
    // 折叠或展开
    collapseOrExpand(width = 0) {
      let style = this.$el.parentElement.parentElement.style;
      style.flex = '0 0 ' + width + 'px';
      style.width = width + 'px';
      style.minWidth = style.width;
    },
    onDragstart(e, type) {
      if (!this.graphItem) {
        return;
      }
      this.graphItem.setSelectedTool(type);
      this.graphItem.updateDragInfo({ startX: e.offsetX, startY: e.offsetY });
    },
    onDragend(e, type) {
      if (!this.graphItem) {
        return;
      }
      this.graphItem.addDragNode({
        type
      });
    }
  }
};
</script>
