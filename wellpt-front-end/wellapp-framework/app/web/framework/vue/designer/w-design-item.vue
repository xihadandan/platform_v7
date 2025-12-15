<template>
  <div v-if="widget.forceRender === false && forceRender !== true"></div>
  <div
    v-else
    :class="['design-row', cutting ? 'border-animate' : '', designer.terminalType]"
    :style="designItemStyle"
    @copy.stop="onCopy"
    @cut.stop="onCut"
    :id="'design-item_' + widget.id"
  >
    <template>
      <a-alert type="error" banner v-if="fieldMissed" style="float: left; width: 100%; z-index: 1000000">
        <div slot="message">
          <label>引用数据模型属性已被删除, 是否移除该组件?</label>
          <a-button size="small" type="link" @click="clickFieldMissedAlertOption(false)">否</a-button>
          <a-button size="small" type="link" @click="clickFieldMissedAlertOption(true)">是</a-button>
        </div>
      </a-alert>

      <a-result
        v-if="configurationNotFount"
        status="error"
        sub-title="请检查组件的配置数据"
        style="outline: 2px solid #409eff; position: relative"
      >
        <template slot="title">
          配置数据 404
          <a-button @click="onRemoveError" type="link" icon="close" size="small" style="position: absolute; top: 10px; right: 10px">
            移除
          </a-button>
        </template>
      </a-result>

      <template v-else>
        <draggable
          class="design-item-draggable-left"
          :list="leftWidgets"
          v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
          v-if="canPutOnSider"
          handle=".widget-drag-handler"
          @add="e => onDragAdd(e, 'left')"
        ></draggable>
        <component
          v-if="customizeMobile && designer.terminalType == 'mobile'"
          ref="component"
          :is="mWType"
          :widget="widget"
          :index="index"
          :widgetsOfParent="widgetsOfParent"
          :designer="designer"
          :parent="parent"
          @mounted="onComponentMounted"
          :key="wKey"
          :editControl="editControl"
        />
        <component
          v-else-if="customize"
          ref="component"
          :is="eWType"
          :widget="widget"
          :index="index"
          :widgetsOfParent="widgetsOfParent"
          :designer="designer"
          :parent="parent"
          @mounted="onComponentMounted"
          :key="wKey"
          :editControl="editControl"
        />

        <EditWrapper
          v-else
          :widget="widget"
          :index="index"
          :widgetsOfParent="widgetsOfParent"
          :designer="designer"
          :parent="parent"
          :showWidgetName="false"
          :widgetDisplayAsReadonly="widgetDisplayAsReadonly"
          v-bind="editControl"
          ref="editWrapper"
        >
          <component
            ref="component"
            :is="widget.wtype"
            :widget="widget"
            :index="index"
            :widgetsOfParent="widgetsOfParent"
            :designer="designer"
            :parent="parent"
            @designStateChange="e => onDesignStateChange(e)"
            @mounted="onComponentMounted"
            :key="wKey"
          />
        </EditWrapper>
        <draggable
          :list="rightWidgets"
          @add="e => onDragAdd(e, 'right')"
          v-if="canPutOnSider"
          class="design-item-draggable-right"
          v-bind="{ group: dragGroup, ghostClass: 'ghost', animation: 200 }"
        ></draggable>
      </template>
    </template>
  </div>
</template>
<style lang="less">
/*边框虚线滚动动画特效*/
.border-animate {
  padding: 1px;
  background: linear-gradient(90deg, gray 60%, transparent 60%) repeat-x left top/10px 1px,
    linear-gradient(0deg, gray 60%, transparent 60%) repeat-y right top/1px 10px,
    linear-gradient(90deg, gray 60%, transparent 60%) repeat-x right bottom/10px 1px,
    linear-gradient(0deg, gray 60%, transparent 60%) repeat-y left bottom/1px 10px;

  animation: border-animate 0.382s infinite linear;
}

@keyframes border-animate {
  0% {
    background-position: left top, right top, right bottom, left bottom;
  }

  100% {
    background-position: left 10px top, right top 10px, right 10px bottom, left bottom 10px;
  }
}
</style>
<script type="text/babel">
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import { debounce } from 'lodash';
import md5 from '@framework/vue/utils/md5';

export default {
  name: 'WDesignItem',
  inject: ['draggable', 'designTerminalType', 'widgetMeta'],
  props: {
    widget: Object, // 组件定义
    widgetsOfParent: Array, // 父组件的所有子组件
    children: Array,
    designer: Object, // 设计器实例
    parent: Object, // 父组件
    index: Number, //当前组件在父组件的子组件列表的序号
    forceRender: Boolean,
    editControl: Object,
    dragGroup: {
      type: String,
      default: 'dragGroup'
    } // 当前页面可拖拽的组名，可以通过设置不同的组名实现页面多层级的允许拖拽范围
  },
  components: {},
  provide() {
    return {
      designItemContext: this
    };
  },

  computed: {
    isDatabaseField() {
      return this.widget.configuration && this.widget.configuration.isDatabaseField;
    },
    designItemStyle() {
      let style = {};
      if (this.widget.configuration && this.widget.configuration.style && this.widget.configuration.style.width) {
        style.width = this.widget.configuration.style.width;
      }
      if (this.isDatabaseField) {
        style.position = 'relative';
        style.display = 'inline-block';
      }
      if (!this.canShow) {
        style.display = 'none';
      }
      return style;
    },
    eWType() {
      return `E${this.widget.wtype}`;
    },
    mWType() {
      return `M${this.widget.wtype}`;
    },
    widgetMd5() {
      let wKey =
        this._isMounted == undefined ||
        this.widget.configuration == undefined ||
        this.widget.wtype == 'WidgetRefrence' ||
        this.widget.wtype == 'WidgetLayout' ||
        this.widget.wtype == 'WidgetForm' ||
        // 容器不适合主动刷新，由容器自身实现实时响应容器的内容变更
        ['basicContainer', 'advanceContainer'].includes(this.widget.category)
          ? this.widget.id
          : md5(JSON.stringify(this.widget));
      // console.log('组件MD5变更: ', this.widget.title, wKey);

      return wKey;
    },
    canPutOnSider() {
      if (this.isDatabaseField && this.parent == undefined) {
        if (this.widget.line == undefined) {
          return true;
        }

        // 每行最多4个元素
        let lines = [];
        for (let i = 0, len = this.widgetsOfParent.length; i < len; i++) {
          let w = this.widgetsOfParent[i];
          if (w.line == this.widget.line) {
            lines.push(w);
          }
        }
        return lines.length < 4;
      }
      return false;
    },
    canShow() {
      if (
        ['WidgetCloneTemplatePlaceholder', 'WidgetRefFormFieldPlaceholder', 'WidgetDmColumnPlaceholder', 'WidgetRefrence'].includes(
          this.widget.wtype
        )
      ) {
        return true;
      }
      if (this.widget.configuration != undefined && this.widget.configuration.deviceVisible != undefined) {
        if (!this.widget.configuration.deviceVisible.includes(this.designer.terminalType)) {
          return false;
        }
      }

      let meta = this.widgetMeta[this.widget.wtype];
      if (meta) {
        if (this.designer.terminalType == 'mobile') {
          // 移动端组件需要保证pc端组件兼容移动端设计
          for (let s of meta.scope) {
            if (s.indexOf('mobile') != -1) {
              return true;
            }
          }
          return false;
        }
        return (
          meta.scope == 'page' ||
          meta.scope == 'dyform' ||
          (Array.isArray(meta.scope) && (meta.scope.includes('page') || meta.scope.includes('dyform')))
        );
      }
      return false;
    }
  },
  data() {
    return {
      customize: false,
      customizeMobile: false,
      configurationNotFount: false,
      wKey: this.widget.id,
      cutting: false,
      fieldMissed: false,
      leftWidgets: [],
      rightWidgets: [],
      widgetDisplayAsReadonly: this.widget.category != 'displayComponent'
    };
  },
  beforeCreate() {},
  created() {
    if (!EASY_ENV_IS_NODE) {
      this.designer.widgetIdMap[this.widget.id] = this.widget;
      this.customize = window.Vue.options.components[`${this.eWType}`] != undefined;
      this.customizeMobile = window.Vue.options.components[`${this.mWType}`] != undefined;
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

  methods: {
    onDesignStateChange(e) {
      if (e != undefined) {
        if (e.widgetDisplayAsReadonly != undefined) {
          this.widgetDisplayAsReadonly = e.widgetDisplayAsReadonly;
        }
      }
    },
    onComponentMounted() {
      if (this.watchComponentDefaultEvents) {
        this.watchComponentDefaultEvents();
      }
      this.watchComponentDefaultEvents = this.$watch(
        () => this.$refs.component.defaultEvents,
        newVal => {
          let supperDefaultEvents = this.$refs.component.supperDefaultEvents || [];
          this.$set(
            this.designer.widgetDefaultEvents,
            this.widget.id,
            supperDefaultEvents.concat(this.$refs.component.defaultEvents || [])
          );
        }
      );
    },
    onDragAdd(e, position) {
      if (!this.widget.line) {
        this.$set(this.widget, 'line', generateId());
      }
      let _item = null;
      if (e.item && e.item._underlying_vm_) {
        _item = e.item._underlying_vm_;
        if (_item.line != undefined) {
          _item.fromLine = _item.line;
        }
        this.$set(_item, 'line', this.widget.line);
      }
      let lines = [];
      for (let i = 0, len = this.widgetsOfParent.length; i < len; i++) {
        let w = this.widgetsOfParent[i];
        if (w.line == this.widget.line) {
          lines.push(w);
        }
      }
      let _width = parseFloat(100 / (lines.length + 1)).toFixed(2) + '%';
      for (let i = 0, len = lines.length; i < len; i++) {
        this.$set(lines[i].configuration.style, 'width', _width);
      }
      if (_item) {
        if (_item.configuration.style == undefined) {
          this.$set(_item.configuration, 'style', { width: _width });
        } else {
          this.$set(_item.configuration.style, 'width', _width);
        }
      }

      if (position == 'left') {
        this.leftWidgets[0].__style = { width: _width };
        this.leftWidgets[0].line = this.widget.line;
        this.widgetsOfParent.splice(this.index, 0, this.leftWidgets[0]);
      } else {
        this.rightWidgets[0].__style = { width: _width };
        this.rightWidgets[0].line = this.widget.line;
        this.widgetsOfParent.splice(this.index + 1, 0, this.rightWidgets[0]);
      }

      this.leftWidgets.splice(0, this.leftWidgets.length);
      this.rightWidgets.splice(0, this.rightWidgets.length);
    },
    onCopy(e) {
      this.$message.success('已复制');
      let copyWidget = JSON.parse(JSON.stringify(this.widget));
      copyWidget.configuration.cloneWidget = true;
      copyToClipboard(JSON.stringify(copyWidget), window.event);
    },
    onCut() {
      // if (this.designer.selectedId) {
      //   this.cutting = true;
      //   copyToClipboard(
      //     JSON.stringify({
      //       cutting: true,
      //       widget: this.widget
      //     }),
      //     window.event
      //   );
      // }
    },

    refresh: debounce(function () {
      if (this.$refs.component && this.$refs.component.autoRefresh === false) {
        return;
      }
      this.wKey = this.widgetMd5;
      // console.log('组件刷新使用key', this.widget.title, this.widgetMd5);
    }, 500),
    onRemoveError() {
      this.widgetsOfParent.splice(this.index, 1);
    },
    setStyleProperty() {
      if (this.widget.configuration.style === undefined) {
        this.$set(this.widget.configuration, 'style', {
          block: true,
          width: this.widget.__style ? this.widget.__style.width : '100%',
          height: 'auto',
          margin: [0, 0, 0, 0],
          padding: [0, 0, 0, 0]
        });
        delete this.widget.__style;
        return;
      }

      // 默认值
      if (!this.widget.configuration.style.hasOwnProperty('block')) {
        this.$set(this.widget.configuration.style, 'block', true);
      }

      if (!this.widget.configuration.style.hasOwnProperty('width')) {
        this.$set(this.widget.configuration.style, 'width', this.widget.__style ? this.widget.__style.width : '100%');
      }

      if (!this.widget.configuration.style.hasOwnProperty('height')) {
        this.$set(this.widget.configuration.style, 'height', undefined);
      }

      if (!this.widget.configuration.style.hasOwnProperty('margin')) {
        this.$set(this.widget.configuration.style, 'margin', [0, 0, 0, 0]);
      }

      if (!this.widget.configuration.style.hasOwnProperty('padding')) {
        this.$set(this.widget.configuration.style, 'padding', [0, 0, 0, 0]);
      }
      delete this.widget.__style;
    },

    updateSimpleFieldInfos() {
      // 表单类字段组件
      if (this.widget.configuration.isDatabaseField && this.widget.refDyformId == undefined) {
        if (this.widget.configuration.code) {
          if (!this.designer.hasOwnProperty('SimpleFieldInfos')) {
            // 简单的字段数据集合，用于提供其他需要使用字段编码、名称数据监听使用
            this.$set(this.designer, 'SimpleFieldInfos', []);
          }
          let notExist = false;
          if (this.designer.FieldWidgets) {
            let fieldIds = [];
            for (let i = 0, len = this.designer.FieldWidgets.length; i < len; i++) {
              fieldIds.push(this.designer.FieldWidgets[i].id);
              if (
                this.designer.FieldWidgets[i].id != this.widget.id &&
                this.designer.FieldWidgets[i].configuration.code == this.widget.configuration.code
              ) {
                // 字段编码冲突，要置空
                // this.widget.configuration.code = undefined;
                return;
              }
            }
            if (!fieldIds.includes(this.widget.id)) {
              notExist = true;
              this.designer.FieldWidgets.push(this.widget);
            }
          }
          if (notExist) {
            let fieldInfo = {
              code: this.widget.configuration.code,
              name: this.widget.configuration.name,
              id: this.widget.id,
              wtype: this.widget.wtype
            };
            this.designer.SimpleFieldInfos.push(fieldInfo);
            let relaFieldConfigures = this.widget.configuration.relaFieldConfigures;
            if (relaFieldConfigures) {
              this.$set(fieldInfo, 'relaFields', []);
              for (let i = 0, len = relaFieldConfigures.length; i < len; i++) {
                fieldInfo.relaFields.push({
                  code: relaFieldConfigures[i].code,
                  name: relaFieldConfigures[i].name || this.widget.configuration.name
                });
              }
            }
          } else {
            for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
              if (this.designer.SimpleFieldInfos[i].id == this.widget.id) {
                this.designer.SimpleFieldInfos[i].code = this.widget.configuration.code;
                this.designer.SimpleFieldInfos[i].name = this.widget.configuration.name;
                return;
              }
            }
          }
        }
      }
    },

    checkField() {
      if (
        this.designer.dataModelColumnMap &&
        this.widget.configuration.isDatabaseField &&
        this.widget.configuration.code &&
        this.widget.column
      ) {
        if (!Object.keys(this.designer.dataModelColumnMap).includes(this.widget.configuration.code)) {
          this.fieldMissed = true;
        }
      }
    },
    clickFieldMissedAlertOption(yes) {
      this.fieldMissed = false;
      if (yes === true) {
        if (!!this.widgetsOfParent) {
          this.widgetsOfParent.splice(this.index, 1);
          this.$delete(this.designer.widgetIdMap, this.widget.id);
          this.$delete(this.designer.widgetTreeMap, this.widget.id);
          this.designer.clearSelected();
        }
      } else {
        this.$delete(this.widget, 'column');
        this.widget.configuration.persistAsColumn = true;
        this.designer.emitEvent(`${this.widget.id}:dataModelColumnChanged`);
      }
    },

    deleteSelf() {
      if (!!this.widgetsOfParent) {
        let fieldWidget = this.widgetsOfParent.splice(this.index, 1);
        if (fieldWidget && fieldWidget[0]) {
          let parent = this.designer.widgetConfigurations[fieldWidget[0].id].parent;
          if (parent && parent.wtype == 'WidgetFormItem' && fieldWidget[0].configuration.syncLabel2FormItem) {
            // 如果组件名称与单元格标题同步，删除组件同步删除单元格标题
            this.designer.parentOfSelectedWidget.configuration.label = '';
            delete this.designer.parentOfSelectedWidget.configuration.i18n;
          }
        }
        this.$delete(this.designer.widgetIdMap, this.widget.id);
        this.$delete(this.designer.widgetTreeMap, this.widget.id);
        if (this.designer.selectedId == this.widget.id) {
          this.designer.clearSelected();
        }

        if (this.designer[`${this.widget.wtype}s`]) {
          for (let i = 0, len = this.designer[`${this.widget.wtype}s`].length; i < len; i++) {
            let w = this.designer[`${this.widget.wtype}s`][i];
            if (w.id == this.widget.id) {
              this.designer[`${this.widget.wtype}s`].splice(i, 1);
              break;
            }
          }
        }
      }
    }
  },

  mounted() {
    let _this = this;
    // 设置组件集合
    // if (!this.customize) {
    let key = `${this.widget.wtype}s`;
    if (!this.designer.hasOwnProperty(key)) {
      this.$set(this.designer, key, []);
    }
    for (let i = 0, len = this.designer[key].length; i < len; i++) {
      if (this.designer[key][i].id == this.widget.id) {
        this.designer[key][i] = this.widget;
        return;
      }
    }
    this.designer[key].push(this.widget);
    // }

    if (this.$refs.component) {
      if (this.designer.widgetDefaultEvents[this.widget.id] == undefined) {
        let supperDefaultEvents = this.$refs.component.supperDefaultEvents || [];
        this.$set(this.designer.widgetDefaultEvents, this.widget.id, supperDefaultEvents.concat(this.$refs.component.defaultEvents || []));
      }
    }

    this.$emit('mounted', { e: this });

    this.updateSimpleFieldInfos();

    this.checkField();

    this.designer.offEvent(`${this.widget.id}:delete`).handleEvent(`${this.widget.id}:delete`, () => {
      _this.deleteSelf();
    });

    // 处理旧数据定义，删除无用的属性数据，不要保存描述展示性内容
    delete this.widget.thumbnail;
    delete this.widget.description;
    delete this.widget.iconClass;
    delete this.widget.icon;
  },

  beforeDestroy() {},

  destroyed() {
    setTimeout(() => {
      let element = document.querySelector('#design-item_' + this.widget.id);
      if (element == null) {
        let key = `${this.widget.wtype}s`;
        if (this.designer[key]) {
          for (let i = 0, len = this.designer[key].length; i < len; i++) {
            if (this.designer[key][i].id == this.widget.id) {
              this.designer[key].splice(i, 1);
              break;
            }
          }
        }

        if (this.designer.FieldWidgets) {
          for (let i = 0, len = this.designer.FieldWidgets.length; i < len; i++) {
            if (this.designer.FieldWidgets[i].id == this.widget.id) {
              this.designer.FieldWidgets.splice(i, 1);
              this.designer.SimpleFieldInfos.splice(i, 1);
              return;
            }
          }
        }

        if (this.widget.refDyformId != undefined) {
          // 引用表单
          this.designer.emitEvent(`widget:design:destroy`, this);
        }
      }
    }, 200);
  },

  watch: {
    'widget.title': {
      immediate: true,
      handler(v) {
        if (this.designer.widgetTreeMap[this.widget.id]) {
          this.$set(this.designer.widgetTreeMap[this.widget.id], 'title', v);
          // this.designer.widgetTreeMap[this.widget.id].title = v;
        }
      }
    },
    widget: {
      deep: false,
      handler(v) {
        if (this.designer.undoOrRedo && this.designer.selectedId === v.id) {
          this.designer.setSelected(v);
        }
      }
    },
    widgetMd5: {
      handler(v, o) {
        // console.log(`widgetMd5: ${o} => ${v}`);
        if (this._isMounted == undefined || v == this.widget.id || o == this.widget.id) {
          // 跳过第一次初始化configuration的情况
          // console.log('跳过刷新');
          return;
        }
        this.refresh();
      }
    },

    'widget.configuration.code': {
      handler(v) {
        this.updateSimpleFieldInfos();
      }
    },
    'widget.configuration.isDatabaseField': {
      handler(v) {
        this.updateSimpleFieldInfos();
      }
    },

    'widget.configuration.name': {
      handler(v) {
        this.updateSimpleFieldInfos();
      }
    }
  }
};
</script>
