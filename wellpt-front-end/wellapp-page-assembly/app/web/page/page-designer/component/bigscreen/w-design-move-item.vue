<template>
  <div
    :id="'design-move-item-' + widget.id"
    :style="vContainerStyle"
    :class="['design-move-item', selected ? 'selected' : undefined]"
    @click.stop="onClickDesignItem"
  >
    <div class="operation-bar">
      <a-button size="small" icon="profile" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
      <a-button size="small" icon="delete" type="danger" title="删除" @click.stop="removeWidget"></a-button>
    </div>
    <div class="move-reference-line-h" v-show="designer.draggingWidgetId == widget.id">
      <label>
        {{ widget.configuration.style.left }}
      </label>
    </div>
    <div class="move-reference-line-v" v-show="designer.draggingWidgetId == widget.id">
      <label>
        {{ widget.configuration.style.top }}
      </label>
    </div>
    <div style="width: 100%; height: 100%; overflow: hidden">
      <component
        ref="component"
        :is="eWType"
        :widget="widget"
        :index="index"
        :widgetsOfParent="widgetsOfParent"
        :designer="designer"
        :parent="parent"
        :key="wKey"
      />
    </div>
  </div>
</template>
<style lang="less">
.design-move-item {
  .move-reference-line-h,
  .move-reference-line-v {
    position: absolute;
    z-index: 3;
    border-top: 1px dashed var(--w-primary-color);
    > label {
      color: #fff;
      font-size: 11px;
      position: absolute;
      font-weight: bold;
    }
  }
  .move-reference-line-h {
    width: 9000px;
    left: -9000px;
    > label {
      right: 12px;
      top: 8px;
    }
  }
  .move-reference-line-v {
    border-top: unset;
    border-left: 1px dashed var(--w-primary-color);
    top: -9000px;
    height: 9000px;
    > label {
      bottom: 8px;
      left: 12px;
    }
  }
  .operation-bar {
    position: absolute;
    z-index: 1;
    right: 0px;
    display: none;
    cursor: pointer;
    > * {
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
        line-height: 22px;
        border: 1px solid var(--w-button-border-color);

        &:hover {
          color: var(--w-button-font-color-hover);
          border-color: var(--w-button-border-color-hover);
        }
      }
    }
  }
  &:hover {
    .operation-bar {
      display: block;
    }
  }
}
</style>
<script type="text/babel">
import '../../../../assets/css/design-move-item.less';
import { debounce } from 'lodash';
import md5 from '@framework/vue/utils/md5';
export default {
  name: 'WDesignMoveItem',
  props: {
    widget: Object, // 组件定义
    widgetsOfParent: Array, // 父组件的所有子组件
    children: Array,
    designer: Object, // 设计器实例
    parent: Object, // 父组件
    index: Number //当前组件在父组件的子组件列表的序号
  },
  components: {},
  computed: {
    eWType() {
      return this.widget.useScope == 'bigScreen' ? this.widget.wtype : this.customize ? `E${this.widget.wtype}` : this.widget.wtype;
    },
    selected() {
      return !!this.designer && this.widget.id === this.designer.selectedId;
    },
    vContainerStyle() {
      let style = {
        zIndex: this.widget.wtype != 'WidgetBorderStyle' ? 1 : 0
      };
      if (this.widget.configuration.style != undefined) {
        let widgetStyle = this.widget.configuration.style;
        style.transform = `translate(${widgetStyle.left}px, ${widgetStyle.top}px)`;
        if (widgetStyle.width != undefined) {
          style.width = typeof widgetStyle.width == 'number' ? `${widgetStyle.width}px` : widgetStyle.width;
        }
        if (widgetStyle.height != undefined) {
          style.height = typeof widgetStyle.height == 'number' ? `${widgetStyle.height}px` : widgetStyle.height;
        }
        if (widgetStyle.zIndex != undefined) {
          style.zIndex = widgetStyle.zIndex;
        }
      }
      return style;
    },
    widgetMd5() {
      if (
        this._isMounted == undefined ||
        this.widget.configuration == undefined ||
        ['WidgetBorderStyle', 'advanceContainer'].includes(this.widget.wtype)
      ) {
        return this.widget.id;
      }
      let clone = JSON.parse(JSON.stringify(this.widget));
      delete clone.configuration.style;
      return md5(JSON.stringify(clone));
    }
  },
  data() {
    return {
      wKey: this.widget.id
    };
  },
  beforeCreate() {},
  created() {
    if (!EASY_ENV_IS_NODE) {
      this.designer.widgetIdMap[this.widget.id] = this.widget;
      this.customize = window.Vue.options.components[`E${this.widget.wtype}`] != undefined;
      let cfgType = `${this.widget.wtype}Configuration`,
        vConfig = window.Vue.options.components[cfgType];
      if (vConfig == undefined) {
        console.warn('未发现配置文件');
      } else {
        this.vConfigurationOptions = vConfig.options;
        if (this.widget.configuration == undefined) {
          // 初始化配置数据
          let configuration = vConfig.options.configuration;
          if (typeof configuration == 'function') {
            // this.widget.configuration = configuration();
            this.$set(this.widget, 'configuration', configuration.call(this, this.widget));
          }
        }
      }
      if (this.widget.configuration == undefined) {
        this.configurationNotFount = true;
        return;
      }

      this.setStyleProperty();

      // 记录兄弟节点
      if (this.parent && this.parent.id) {
        this.designer.widgetsOfParent[this.parent.id] = this.widgetsOfParent;
      }
      if (this.widget.wtype !== 'WidgetDmColumnPlaceholder') {
        this.designer.toTree(this.widget, [], this.parent, this.index);
      }
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    refresh: debounce(function () {
      this.wKey = this.widgetMd5;
    }, 500),
    removeWidget() {
      this.widgetsOfParent.splice(this.index, 1);
      this.designer.emitEvent(`widget:deleted`, this.widget.id);
    },
    showWidgetJsonDetail() {
      this.designer.widgetJsonDrawerVisible = true;
    },
    setStyleProperty() {
      if (this.widget.configuration.style === undefined) {
        this.$set(this.widget.configuration, 'style', {
          width: this.widget.__style ? this.widget.__style.width : 300,
          height: 200,
          margin: [0, 0, 0, 0],
          padding: [0, 0, 0, 0]
        });
        delete this.widget.__style;
        return;
      }

      if (!this.widget.configuration.style.hasOwnProperty('width')) {
        this.$set(this.widget.configuration.style, 'width', this.widget.__style ? this.widget.__style.width : 100);
      }

      if (!this.widget.configuration.style.hasOwnProperty('height')) {
        this.$set(this.widget.configuration.style, 'height', 100);
      }

      if (!this.widget.configuration.style.hasOwnProperty('margin')) {
        this.$set(this.widget.configuration.style, 'margin', [0, 0, 0, 0]);
      }

      if (!this.widget.configuration.style.hasOwnProperty('padding')) {
        this.$set(this.widget.configuration.style, 'padding', [0, 0, 0, 0]);
      }
    },
    onClickDesignItem(e) {
      this.designer.setSelected(this.widget, this.parent);
    }
  },
  watch: {
    widgetMd5: {
      handler(v, o) {
        if (this._isMounted == undefined || v == this.widget.id || o == this.widget.id) {
          // 跳过第一次初始化configuration的情况
          return;
        }
        this.refresh();
      }
    }
  }
};
</script>
