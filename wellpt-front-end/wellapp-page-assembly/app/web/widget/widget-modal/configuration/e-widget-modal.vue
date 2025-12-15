<template>
  <div class="edit-widget-modal-container">
    <div class="widget-edit-wrapper" v-show="selected && visible" :style="`position: absolute; top: ${top}; right: 2px; width: 100px`">
      <div class="widget-operation-buttons">
        <a-button size="small" icon="profile" title="查看JSON" @click.stop="showWidgetJsonDetail"></a-button>
        <a-button size="small" icon="eye-invisible" title="隐藏" @click.stop="onModalCancel"></a-button>
        <a-button size="small" icon="delete" type="danger" @click.stop="deleteSelf" title="删除"></a-button>
      </div>
    </div>
    <KeepAlive>
      <a-modal
        ref="modal"
        :getContainer="getContainer"
        :visible="visible"
        :maskClosable="false"
        :mask="false"
        :zIndex="10"
        :width="vWidth"
        :forceRender="true"
        :footer="widget.configuration.footerHidden ? null : undefined"
        :dialogStyle="{ top: '35px' }"
        :closable="widget.configuration.closable || widget.configuration.closable === undefined"
        :bodyStyle="bodyStyle"
        :dialogClass="[
          'pt-modal widget-modal footer-align-' + buttonAlign,
          widget.configuration.footerHidden ? 'no-footer' : '',
          widget.configuration.closable || widget.configuration.closable === undefined ? '' : 'unclosable'
        ]"
        :wrapClassName="selected ? 'edit-widget-modal-wrap selected' : 'edit-widget-modal-wrap'"
        @click.native.stop="evt => selectModal(evt)"
      >
        <div slot="title" v-if="showTitle" class="flex f_y_s f_x_s">
          <div v-if="!widget.configuration.hiddenTitle">
            <span v-html="widget.configuration.title || '&nbsp;'"></span>
          </div>
          <div v-else>&nbsp;</div>
          <div v-if="widget.configuration.switchFullscreen">
            <a-button type="text">
              <Icon type="pticon iconfont icon-ptkj-quanping"></Icon>
            </a-button>
          </div>
        </div>
        <template slot="footer">
          <WidgetTableButtons :button="widget.configuration.footerButton" :key="vButtonKey" />
        </template>
        <draggable
          v-if="!widget.configuration.contentFromPage"
          :list="widget.configuration.widgets"
          v-bind="{ group: draggableConfig.dragGroup, ghostClass: 'ghost', animation: 200 }"
          @add="evt => onGridDragAdd(evt, widget.configuration.widgets)"
          @paste.native="onDraggablePaste"
          :class="[
            !widget.configuration.widgets || widget.configuration.widgets.length === 0 ? 'widget-edit-empty' : 'widget-edit-content'
          ]"
        >
          <transition-group name="fade" tag="div" class="widget-modal-drop-panel">
            <template v-for="(wgt, i) in widget.configuration.widgets">
              <WDesignItem
                :widget="wgt"
                :key="wgt.id"
                :index="i"
                :widgetsOfParent="widget.configuration.widgets"
                :designer="designer"
                :parent="widget"
              />
            </template>
          </transition-group>
        </draggable>
        <div v-else :key="contentKey">
          <template
            v-if="
              widget.configuration.contentFromPage &&
              widget.configuration.eventHandler &&
              (widget.configuration.eventHandler.pageId != undefined || widget.configuration.eventHandler.url != undefined)
            "
          >
            <WidgetVpage
              v-if="widget.configuration.eventHandler.pageType == 'page' && widget.configuration.eventHandler.pageId != undefined"
              :pageId="widget.configuration.eventHandler.pageId"
              :parent="widget"
            />
            <iframe
              :id="widget.id"
              v-else-if="widget.configuration.eventHandler.pageType == 'url' && widget.configuration.eventHandler.url != undefined"
              :src="widget.configuration.eventHandler.url"
              :style="{ border: 'none', width: '100%' }"
            ></iframe>
          </template>
        </div>
      </a-modal>
    </KeepAlive>
  </div>
</template>
<style lang="less">
.edit-widget-modal-container .widget-operation-buttons {
  position: absolute;
  right: 0px;
  z-index: 100;
  top: 1px;
  button {
    border-radius: 0px;
    float: left;
  }
}
#design-main .edit-widget-modal-wrap {
  position: absolute;
  top: 52px;
  left: 8px;
  right: 8px;
  bottom: 8px;
  min-height: e('calc(100vh - 200px)');
  background-color: rgba(0, 0, 0, 0.2);
  margin: 0 2px 2px 2px;
}

.edit-widget-modal-wrap.selected {
  outline: 2px solid var(--w-primary-color) !important;
}
.edit-widget-modal-wrap .widget-modal-drop-panel {
  padding: 2px;
  min-height: 100px;
  max-height: 420px;
  overflow-y: auto;
}
</style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import draggable from '@framework/vue/designer/draggable';
import { generateId } from '@framework/vue/utils/util';
import md5 from '@framework/vue/utils/md5';

export default {
  name: 'EWidgetModal',
  mixins: [editWgtMixin, draggable],

  data() {
    return {
      vButtonKey: this.widget.id + 'Button',
      visible: false,
      buttonAlign: 'right',
      dragGroupModal: {
        name: 'dragGroupModal',
        put: function () {
          let i = ['WidgetModal', 'WidgetLayout'].indexOf(arguments[2]._underlying_vm_.wtype);
          // if (i != -1) {
          //   this.$message.info('无法拖入该组件');
          // }
          return i === -1;
        }
      },
      top: '4px'
    };
  },
  provide() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {
    contentKey() {
      if (this.widget.configuration.eventHandler) {
        return md5(JSON.stringify(this.widget.configuration.eventHandler));
      }
      return generateId();
    },
    vWidth() {
      if (this.widget.configuration.fullscreen && !this.widget.configuration.switchFullscreen) {
        return '100%';
      }
      if (this.widget.configuration.size == 'selfDefine') {
        return this.widget.configuration.width || 600;
      }
      let screenWidth = window.screen.width;
      // 常见屏幕： 1920 、1440 、 1366 、1280
      if (this.widget.configuration.size === 'small') {
        if (screenWidth <= 1280) {
          return 520;
        } else if (screenWidth > 1280 && screenWidth <= 1366) {
          return 580;
        } else if (screenWidth > 1366 && screenWidth <= 1440) {
          return 640;
        } else if (screenWidth > 1440 && screenWidth <= 1920) {
          return 700;
        }
        return 760;
      } else if (this.widget.configuration.size === 'middle') {
        if (screenWidth <= 1280) {
          return 580;
        } else if (screenWidth > 1280 && screenWidth <= 1366) {
          return 640;
        } else if (screenWidth > 1366 && screenWidth <= 1440) {
          return 700;
        } else if (screenWidth > 1440 && screenWidth <= 1920) {
          return 760;
        }
        return 820;
      } else if (this.widget.configuration.size === 'large') {
        if (screenWidth <= 1280) {
          return 760;
        } else if (screenWidth > 1280 && screenWidth <= 1366) {
          return 820;
        } else if (screenWidth > 1366 && screenWidth <= 1440) {
          return 880;
        } else if (screenWidth > 1440 && screenWidth <= 1920) {
          return 940;
        }
        return 1000;
      }
      return 520;
    },
    vTitle() {
      return this.widget.configuration.title || ' ';
    },
    defaultEvents() {
      return [
        {
          id: 'showModal',
          title: '打开弹窗',
          eventParams: [
            {
              paramKey: 'title',
              remark: '标题'
            },
            {
              paramKey: 'content',
              remark: '内容或者html字符串'
            },
            {
              paramKey: 'fullscreen',
              remark: '是否全屏打开',
              valueSource: {
                inputType: 'select',
                options: [
                  { label: '是', value: true },
                  { label: '否', value: false }
                ]
              }
            }
          ],
          codeSnippet: `
          // 打开弹窗
          this.pageContext.emitEvent({{事件编码}});
          `
        },
        {
          id: 'closeModal',
          title: '关闭弹窗',
          codeSnippet: `
          // 关闭弹窗
          this.pageContext.emitEvent({{事件编码}});
          `
        },
        {
          id: 'updateModalTitle',
          title: '更新弹窗标题',
          codeSnippet: `
          // 更新弹窗标题
          this.pageContext.emitEvent({{事件编码}} , title );
          `
        },
        {
          id: 'refresh',
          title: '刷新弹窗内容',
          codeSnippet: `
          // 刷新弹窗内容
          this.pageContext.emitEvent({{事件编码}});
          `
        }
      ];
    },
    // 弹框标题，关闭按钮，全屏按钮只要有一个，就显示
    showTitle() {
      return !this.widget.configuration.hiddenTitle || this.widget.configuration.closable || this.widget.configuration.switchFullscreen;
    }
  },
  created() {
    // 挂载组件树
    this.designer.toTree(this.widget, this.widget.configuration.widgets, this.parent, this.index);
  },
  methods: {
    deleteSelf() {
      this.onModalCancel();
      this.widgetsOfParent.splice(this.index, 1);
      delete this.designer.widgetIdMap[this.widget.id];
      this.designer.widgetTree.splice(this.index, 1);
      delete this.designer.widgetTreeMap[this.widget.id];
    },
    showWidgetJsonDetail() {
      this.designer.widgetJsonDrawerVisible = true;
    },

    getContainer() {
      return document.querySelector('#design-main');
    },
    onModalCancel() {
      this.visible = false;
      this.draggableConfig.dragGroup = 'dragGroup';
      this.designer.setSelected(null);
    },

    selectModal(evt) {
      if (
        ['ant-modal-title', 'ant-modal-header', 'widget-modal-drop-panel', 'ant-modal-body'].indexOf(evt.target.className) != -1 ||
        (evt.target.className && evt.target.className.indexOf('edit-widget-modal-wrap') != -1)
      ) {
        // 点击遮罩、弹窗标题栏处、中间拖拽区域，选择弹窗组件，其他位置不触发
        this.selectWidget();
      }
    },
    onModalOk() {},
    onGridDragAdd(evt, subWidgets) {},
    isSubWidgetSelected() {
      let current = this.designer.widgetTreeMap[this.designer.selectedId],
        parentKey = null;
      if (current) {
        parentKey = current.parentKey;
        while (parentKey && parentKey != this.widget.id) {
          current = this.designer.widgetTreeMap[parentKey];
          if (current && current.parentKey == this.widget.id) {
            return true;
          } else {
            parentKey = current.parentKey;
          }
        }
      }

      return parentKey == this.widget.id;
    }
  },
  mounted() {
    this.visible = this.widget.id == this.designer.selectedId || this.isSubWidgetSelected();
  },

  watch: {
    'designer.selectedId': {
      handler(v) {
        // 判断当前的选择组件是其子组件
        if (v == this.widget.id || this.isSubWidgetSelected()) {
          this.visible = true;
          this.draggableConfig.dragGroup = 'widgetModalGroup';
          // 过滤到弹窗组件的拖拽行为
          this.draggableConfig.filter = `.${this.widget.wtype}`;
          let idx = this.draggableConfig.filterWtype.indexOf(this.widget.wtype);
          if (idx == -1) {
            this.draggableConfig.filterWtype.push(this.widget.wtype);
          }
          this.top = document.querySelector('.panel-scroll').scrollTop + 4 + 'px';
        }
      }
    },
    'widget.configuration.footerButton': {
      deep: true,
      handler(v) {
        this.vButtonKey = new Date().getTime();
      }
    }
  }
};
</script>
