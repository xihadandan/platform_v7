<template>
  <div
    :class="['widget-edit-wrapper', selected ? 'selected' : '']"
    @click.stop="selectWidget(widget)"
    @mouseenter="onMouseenter"
    @mouseleave="onMouseleave"
  >
    <span class="widget-title" v-show="widgetNameVisible">{{ vTitle }}</span>
    <span class="widget-title widget-unselect-title" v-show="unselectWidgetNameVisible">{{ vTitle }}</span>
    <div class="widget-operation-buttons" v-show="selected" :style="{ opacity: buttonOpacity }">
      <slot name="extra-buttons"></slot>
      <template v-if="widget.block === false && wrapperWidth < 100">
        <a-button size="small" :class="dragHandleClass" icon="select" v-if="enableDrag && draggable !== false" title="拖动"></a-button>
        <a-dropdown>
          <a-button icon="ellipsis" size="small" title="更多操作" @click.stop="() => {}" />
          <a-menu slot="overlay" size="small" @click="e => handleMoreOperationClick(e)">
            <a-menu-item key="widgetMoveUp" v-show="canMoveUp">
              <a href="javascript:;">向上移动</a>
            </a-menu-item>
            <a-menu-item key="widgetMoveDown" v-show="canMoveDown">
              <a href="javascript:;">向下移动</a>
            </a-menu-item>
            <a-menu-item key="showWidgetJsonDetail">
              <a href="javascript:;">查看JSON</a>
            </a-menu-item>
            <a-menu-item key="cloneWidget" v-show="enableClone">
              <a href="javascript:;">复制</a>
            </a-menu-item>

            <a-menu-item key="deleteSelf" v-if="enableDelete">
              <a href="javascript:;">删除</a>
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </template>
      <template v-else>
        <a-button size="small" icon="arrow-up" v-show="canMoveUp" title="向上移动" @click.stop="widgetMoveUp(widget)"></a-button>
        <a-button size="small" icon="arrow-down" v-show="canMoveDown" title="向下移动" @click.stop="widgetMoveDown(widget)"></a-button>
        <a-button
          size="small"
          icon="vertical-align-top"
          v-show="hasParent"
          title="选中父组件"
          @click.stop="selectWidget(parent)"
        ></a-button>
        <a-button size="small" icon="profile" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
        <Icon
          style="background: #fff"
          :class="[dragHandleClass, 'drag-icon']"
          type="pticon iconfont icon-ptkj-tuodong"
          v-if="enableDrag && draggable !== false"
          title="拖动"
        ></Icon>
        <a-button size="small" icon="copy" @click.stop="cloneWidget" title="复制" v-if="enableClone"></a-button>

        <a-button size="small" type="danger" style="padding: 0" title="删除" @click.stop="deleteSelf(widget)" v-if="enableDelete">
          <Icon type="pticon iconfont icon-ptkj-shanchu" />
        </a-button>
      </template>
    </div>
    <!-- 定义选中边框效果，避免在容器的单元格(无内边距）等布局内从而选中效果看不到 -->
    <div class="design-selected-border top"></div>
    <div class="design-selected-border right"></div>
    <div class="design-selected-border bottom"></div>
    <div class="design-selected-border left"></div>

    <div :class="['widget-edit-container', selected ? 'selected' : '']" :style="{ display: editContainerDisplay }">
      <!-- 只读情况，通过加一层遮罩让鼠标无法点击内部的组件 -->
      <div style="width: 100%; height: 100%; display: block; position: absolute; z-index: 2" v-if="widgetDisplayAsReadonly"></div>
      <div :class="['edit-wrapper-slot', widget.wtype]">
        <slot></slot>
      </div>
    </div>
  </div>
</template>
<style lang="less">
.widget-edit-wrapper {
  /* padding-bottom: 2px; */
  position: relative;
  // margin: 0px 0px 5px 0px;
  > .design-selected-border {
    position: absolute;
    z-index: 3;
    border: 1px solid var(--w-primary-color);
    display: none;
    &.top {
      width: 100%;
      height: 1px;
      top: 0;
    }
    &.right {
      width: 1px;
      height: 100%;
      right: 0;
    }
    &.bottom {
      width: 100%;
      height: 1px;
      bottom: 0;
    }
    &.left {
      width: 1px;
      height: 100%;
      left: 0;
    }
  }
  &.selected {
    > .design-selected-border {
      display: block;
    }
  }

  .edit-wrapper-slot.WidgetTemplate:empty::after {
    content: '自定义模板';
    display: block;
    width: 100%;
    // background-color: var(--w-fill-color-light);
    border: 1px dashed var(--w-border-color-base);
    text-align: center;
    line-height: 50px;
    color: var(--w-text-color-light);
  }

  .edit-wrapper-slot {
    &.WidgetSubform,
    &.WidgetCard > .widget-card > .ant-card-head {
      // 设计区不可操作
      position: relative;
      &::before {
        content: '';
        position: absolute;
        width: 100%;
        height: 100%;
        z-index: 1;
      }
    }
  }
}

.widget-edit-wrapper.inline-block {
  display: inline-block;
  /* min-width: 180px;
  width: auto; */
}
.widget-edit-operation {
  position: absolute;
  width: 100%;
  z-index: 2;
}
.widget-edit-wrapper .widget-title {
  position: absolute;
  background-color: var(--w-primary-color);
  color: #fff;
  padding: 2px;
  border-radius: 2px;
  z-index: 3;
  left: 0;
  line-height: normal;
  white-space: nowrap;
}

.widget-edit-wrapper .widget-unselect-title {
  background-color: #646464;
  color: #ffffff;
  left: 1px;
  top: 5px;
  font-size: smaller;
  opacity: 0.5;
  white-space: nowrap;
}

.widget-edit-wrapper .widget-edit-container {
  min-height: 15px;
  // padding: 2px;
}
.widget-edit-wrapper .widget-operation-buttons {
  position: absolute;
  z-index: 3;
  right: 0;
  line-height: normal;
  transition: opacity 0.3s linear;
}
.widget-edit-wrapper .widget-operation-buttons > * {
  float: left;
  border-radius: 0px;
  width: 20px;
  height: 20px;
  font-size: 12px;
  line-height: 18px;
  > .ant-select-selection {
    border-radius: 0px;
    height: 20px;
    min-height: unset;
    > .ant-select-selection__rendered {
      line-height: 20px;
    }
  }
  &.drag-icon {
    text-align: center;
    line-height: 20px;
    border: 1px solid var(--w-button-border-color);
    color: var(--w-button-font-color);

    &:hover {
      color: var(--w-button-font-color-hover);
      border-color: var(--w-button-border-color-hover);
      cursor: move;
    }
  }
}

.widget-edit-wrapper.inline-block > .widget-operation-buttons {
  display: inline-flex;
  flex-wrap: nowrap;
  .ant-btn-icon-only.ant-btn-sm {
    font-size: 10px;
    width: 20px;
    height: 23px;
    border-radius: 0px;
  }
}
.widget-edit-wrapper.hovered {
  outline: 3px solid #e8e8e8;
}
.widget-edit-wrapper > .widget-edit-container.selected {
  // outline: 2px solid var(--w-primary-color) !important;
  // box-shadow: 4px 4px 5px var(--w-primary-color) !important;
  position: sticky;
  z-index: 2;
}

.widget-edit-wrapper .error {
  outline: 2px solid #f5222d;
  // box-shadow: 4px 4px 5px #f5222d;
}

.widget-edit-wrapper.selected > .widget-operation-buttons {
  z-index: 100;
}
</style>
<script type="text/babel">
import { addElementResizeDetector, deepClone, generateId, copyToClipboard } from '../utils/util';
import { debounce } from 'lodash';
import md5 from '@framework/vue/utils/md5';

export default {
  name: 'EditWrapper',
  inject: ['draggable'],
  mixins: [],
  props: {
    widget: Object, // 组件定义
    widgetsOfParent: Array, // 父组件的所有子组件
    children: Array,
    designer: Object, // 设计器实例
    parent: Object, // 父组件
    index: Number, //当前组件在父组件的子组件列表的序号
    dragGroup: {
      type: String,
      default: 'dragGroup'
    }, // 当前页面可拖拽的组名，可以通过设置不同的组名实现页面多层级的允许拖拽范围
    title: String,
    widgetDisplayAsReadonly: {
      type: Boolean,
      default: false
    },
    enableDrag: {
      type: Boolean,
      default: true
    },
    enableDelete: {
      type: Boolean,
      default: true
    },
    enableClone: {
      type: Boolean,
      default: true
    },
    dragHandleClass: {
      type: String,
      default: 'widget-drag-handler'
    },
    showWidgetName: {
      type: Boolean,
      default: true
    },
    showUnselectWidgetName: {
      type: Boolean,
      default: false
    },
    unselectable: {
      type: Boolean,
      default: false
    },
    styleSet: Object
  },
  data() {
    return {
      defaultTitle: this.title || this.widget.title,
      buttonOpacity: 1,
      hovered: false,
      wrapperWidth: 0,
      dragover: false,
      rect: {},
      dragoverPosition: undefined,
      wKey: 'EDIT-' + this.widget.id
    };
  },
  computed: {
    selected() {
      return !!this.designer && this.widget.id === this.designer.selectedId;
      //&& !this.designer.dragging;
    },
    error() {
      return !!this.designer && this.designer.errorWidgetIds.includes(this.widget.id);
    },

    hasParent() {
      return this.parent != null;
    },

    widgetNameVisible() {
      return this.showWidgetName && !!this.designer && this.widget.id === this.designer.selectedId;
    },
    unselectWidgetNameVisible() {
      return this.showUnselectWidgetName && !!this.designer && this.widget.id != this.designer.selectedId;
    },
    canMoveUp() {
      if (this.parent && this.parent.wtype == 'WidgetPositionLayout') {
        return false;
      }
      return this.index != 0 && this.index != undefined;
    },
    canMoveDown() {
      if (this.parent && this.parent.wtype == 'WidgetPositionLayout') {
        return false;
      }
      return this.widgetsOfParent && this.index != this.widgetsOfParent.length - 1;
    },

    editContainerDisplay() {
      return this.widget.wtype === 'GridCol' ? 'initial' : 'block';
    },
    vTitle() {
      let title = this.title || this.widget.title || this.defaultTitle;
      if (this.designer.widgetTreeMap[this.widget.id]) {
        // 修改组件树的标题
        this.designer.widgetTreeMap[this.widget.id].title = title;
      }
      return title;
    }
  },
  created() {
    this.widgetPropValidate(this.widget);
    this.designer.widgetIdMap[this.widget.id] = this.widget;
    // 挂载组件树
    this.designer.toTree(this.widget, this.children, this.parent, this.index);
  },

  methods: {
    handleMoreOperationClick(e) {
      if (typeof this[e.key] === 'function') {
        this[e.key]();
      }
    },

    onMouseenter() {
      this.buttonOpacity = 1;
    },
    onMouseleave() {
      this.buttonOpacity = 0;
    },
    selectWidget(widget, parent) {
      if (this.unselectable || this.designer.unselectableWidgetIds.includes(widget.id)) {
        return;
      }

      this.designer.setSelected(widget || this.widget, parent || this.parent);
    },

    showWidgetJsonDetail() {
      this.designer.widgetJsonDrawerVisible = true;
    },
    cloneWidget() {
      this.$message.success('已复制');
      let copyWidget = JSON.parse(JSON.stringify(this.widget));
      copyWidget.configuration.cloneWidget = true;
      // 如果是锁定状态下组件，去除锁定的column属性
      if (copyWidget.hasOwnProperty('column')) {
        delete copyWidget.column;
      }
      copyToClipboard(JSON.stringify(copyWidget), window.event);
    },
    widgetMoveUp() {
      this.designer.widgetMoveUp(this.widgetsOfParent, this.index, this.parent);
    },

    widgetMoveDown() {
      this.designer.widgetMoveDown(this.widgetsOfParent, this.index, this.parent);
    },

    deleteSelf() {
      if (!!this.widgetsOfParent) {
        // let nextSelected = null;
        // if (this.widgetsOfParent.length === 1) {
        //   if (!!this.parent) {
        //     nextSelected = this.parent;
        //   }
        // } else if (this.widgetsOfParent.length === 1 + this.index) {
        //   nextSelected = this.widgetsOfParent[this.index - 1];
        // } else {
        //   nextSelected = this.widgetsOfParent[this.index + 1];
        // }

        let fieldWidget = this.widgetsOfParent.splice(this.index, 1);
        if (fieldWidget && fieldWidget[0]) {
          let parent = this.designer.widgetConfigurations[fieldWidget[0].id].parent;
          if (parent && parent.wtype == 'WidgetFormItem' && fieldWidget[0].configuration.syncLabel2FormItem) {
            // 如果组件名称与单元格标题同步，删除组件同步删除单元格标题
            this.designer.parentOfSelectedWidget.configuration.label = '';
            delete this.designer.parentOfSelectedWidget.configuration.i18n;
          }
        }
        this.designer.deleteWidget(this.widget.id);
        // this.$delete(this.designer.widgetIdMap, this.widget.id);
        // this.$delete(this.designer.widgetTreeMap, this.widget.id);

        // delete this.designer.widgetIdMap[this.widget.id];
        // delete this.designer.widgetTreeMap[this.widget.id];
        this.designer.clearSelected();
        // this.$emit('onDeleted', true);
        // if (!!nextSelected) {
        //   this.designer.setSelected(nextSelected);
        // }

        // 重新等分该行组件宽度
        if (this.widget.line) {
          let lines = [];
          for (let i = 0, len = this.widgetsOfParent.length; i < len; i++) {
            let w = this.widgetsOfParent[i];
            if (w.line == this.widget.line && w.id != this.widget.id) {
              lines.push(w);
            }
          }
          let _width = parseFloat(100 / lines.length).toFixed(2) + '%';
          for (let i = 0, len = lines.length; i < len; i++) {
            this.$set(lines[i].configuration.style, 'width', _width);
          }
          if (lines.length == 1) {
            delete this.widget.line;
          }
        }
        // if (this.widget.inline && this.widget.lineId) {
        //   let lineElements = document.querySelectorAll(`[line=${this.widget.lineId}]`);
        //   let width = lineElements.length === 2 ? 100 : ((1 / (lineElements.length - 1)) * 100).toFixed(2);

        //   for (let i = 0, len = lineElements.length; i < len; i++) {
        //     if (lineElements[i].id != this.widget.id) {
        //       let wgt = this.designer.widgetIdMap[lineElements[i].id];
        //       if (width === 100) {
        //         // 只剩一个的情况 下，要去掉lineId
        //         delete wgt.lineId;
        //       }
        //       lineElements[i].style.width = width + '%';
        //       wgt.configuration.style.width = width + '%';
        //     }
        //   }
        // }
      }
    },
    widgetPropValidate(widget) {
      let unvalidateProps = [];
      if (!widget.hasOwnProperty('id')) {
        unvalidateProps.push('id');
      }
      if (!widget.hasOwnProperty('configuration')) {
        unvalidateProps.push('configuration');
      }
      if (!widget.hasOwnProperty('title')) {
        unvalidateProps.push('title');
      }
      if (unvalidateProps.length) {
        console.error(`组件参数${JSON.stringify(unvalidateProps)}丢失！`);
      }
    },

    refreshWidget: debounce(function (v) {
      this.wKey = md5(JSON.stringify(this.widget));
    }, 500)
  },
  beforeMount() {
    // 临时样式变量
    if (this.widget.tempStyle) {
      if (this.widget.configuration.style === undefined) {
        this.$set(this.widget.configuration, 'style', { ...this.widget.tempStyle });
      } else {
        let style = deepClone(this.widget.configuration.style);
        Object.assign(style, this.widget.tempStyle);
        this.$set(this.widget.configuration, 'style', style);
      }
      delete this.widget.tempStyle;
    }
  },

  mounted() {
    let _this = this;
    this.rect = this.$el.getBoundingClientRect(); // 元素位置
    addElementResizeDetector(this.$el, () => {
      _this.$nextTick(function () {
        _this.wrapperWidth = _this.$el.clientWidth;
        _this.rect = _this.$el.getBoundingClientRect(); // 元素位置
      });
    });
    // 编辑组件挂载成功，触发事件
    this.designer.emitEvent(`EWidget:${this.widget.id}:mounted`, { widget: this.widget, parent: this.parent });
  },
  updated() {
    this.rect = this.$el.getBoundingClientRect(); // 元素位置
  },

  watch: {
    children: {
      // 监听子元素长度变化
      deep: false,
      handler(v) {
        if (v != undefined) {
          this.designer.toTree(this.widget, v, this.parent, this.index);
        }
      }
    },
    // widgetsOfParent: {
    //   deep: false,
    //   handler(v) {
    //     this.updateWidgetTreeChildren(v, this.parent ? this.parent.id : -1);
    //   }
    // },
    widget: {
      deep: false,
      handler(v) {
        if (this.designer.undoOrRedo && this.designer.selectedId === v.id) {
          this.designer.setSelected(v);
        }
      }
    }
  }
};
</script>
