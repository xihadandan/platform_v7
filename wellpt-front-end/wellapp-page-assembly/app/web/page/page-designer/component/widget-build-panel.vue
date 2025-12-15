<template>
  <div class="widget-build-container" @click="buildPanelContainerClick">
    <a-row class="widget-build-toolbar">
      <a-col :span="23" :style="{ paddingLeft: '12px' }">
        <a-button
          type="link"
          size="small"
          :disabled="disableUndo"
          :loading="designer.undoOrRedoLoading"
          :title="$t('PageDesigner.toolbar.undo', '撤销')"
          @click.stop="undo"
        >
          <!-- 撤销 -->
          <Icon type="pticon iconfont icon-luojizujian-fanhui"></Icon>
          {{ $t('PageDesigner.toolbar.undo', '撤销') }}
        </a-button>
        <a-button
          type="link"
          size="small"
          :disabled="disableRedo"
          :loading="designer.undoOrRedoLoading"
          :title="$t('PageDesigner.toolbar.redo', '恢复')"
          @click.stop="redo"
        >
          <!-- 恢复 -->
          <Icon type="pticon iconfont icon-luojizujian-fanhui" style="transform: scaleX(-1)"></Icon>
          {{ $t('PageDesigner.toolbar.redo', '恢复') }}
        </a-button>

        <a-popconfirm
          placement="bottomLeft"
          :arrowPointAtCenter="true"
          :trigger="disableClear ? '' : 'click'"
          title="确认要清空吗?"
          ok-text="清空"
          cancel-text="取消"
          @confirm="clear"
          v-if="allowClear"
        >
          <a-button type="link" size="small" :title="$t('PageDesigner.toolbar.clear', '清空')" :disabled="disableClear">
            <!-- 清空 -->
            <Icon type="pticon iconfont icon-xmch-qingkongqingchu"></Icon>
            {{ $t('PageDesigner.toolbar.clear', '清空') }}
          </a-button>
        </a-popconfirm>

        <slot name="preview-btn-slot" v-if="allowPreview">
          <a-button type="link" size="small" :title="$t('PageDesigner.toolbar.preview', '预览')" @click.stop="preview">
            <!-- 预览 -->
            <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
            {{ $t('PageDesigner.toolbar.preview', '预览') }}
          </a-button>
        </slot>

        <WidgetCodeEditor
          lang="json"
          title="页面变量JSON"
          @save="value => savePageVar(value)"
          :value="defaultPageVars"
          :zIndex="10"
          v-if="allowPageVars"
        >
          <a-button icon="code" type="link" size="small">页面变量</a-button>
        </WidgetCodeEditor>
        <WidgetDesignModal
          v-if="allowSaveAsTemplate"
          :zIndex="1000"
          :width="300"
          title="另存为模板"
          :designer="designer"
          :maxHeight="600"
          ref="saveAsTemplateModal"
        >
          <a-button type="link" size="small">
            <Icon type="pticon iconfont icon-ptkj-baocun"></Icon>
            另存为模板
          </a-button>
          <div slot="content">
            <a-form-model :colon="false">
              <a-form-model-item label="名称">
                <a-input v-model="designTemplateTitle" />
              </a-form-model-item>
            </a-form-model>
          </div>
          <template slot="footer">
            <a-button @click="onSaveAsDesignTemplate" type="primary">
              <Icon type="pticon iconfont icon-ptkj-baocun"></Icon>
              保存
            </a-button>
          </template>
        </WidgetDesignModal>
      </a-col>
      <a-col :span="1" :style="{ textAlign: 'right', paddingRight: '12px' }">
        <a-button type="link" size="small" :title="isFullscreen ? '退出全屏' : '全屏'" @click.stop="onFullscreen">
          <Icon :type="isFullscreen ? 'pticon iconfont icon-ptkj-tuichuquanping' : 'pticon iconfont icon-ptkj-quanping'"></Icon>
          <!-- {{ isFullscreen ? '退出全屏' : '全屏' }} -->
        </a-button>
      </a-col>
    </a-row>
    <!-- style="position: relative; width: calc(100% - 1px)" -->
    <div class="widget-build-drop-container" style="position: relative" v-if="!loading">
      <div v-show="designer.widgets.length === 0" class="design-empty-info">
        <widgetBuildEmptyImg></widgetBuildEmptyImg>
        从左侧拖拽组件或布局开始设计
      </div>
      <slot name="dndContainer"></slot>
      <PerfectScrollbar class="panel-scroll" :key="bKey" :options="{ suppressScrollX: true }" v-if="!customDndContainer">
        <draggable
          :list="designer.widgets"
          v-bind="{ group: 'dragGroup', ghostClass: 'ghost' }"
          handle=".widget-drag-handler"
          @start="onDragStart"
          @end="onDragEnd"
          @add="onDragAdd"
          @choose="onDragChoose"
          :move="onDragMove"
          @change="onDragChange"
          @sort="onDragSort"
          @unchoose="onDragUnchoose"
          @update="onDragUpdate"
        >
          <transition-group
            name="fade"
            tag="div"
            :class="['widget-drop-panel', designer.dragoverSide ? 'wgt-side-dagovered' : '', 'widget-drop-panel-' + designType]"
            @click.native.stop="onClickDropPanel"
            :style="{ position: 'relative', ...vDesignStyle, width: 'calc(100% - 1px)', height: 'auto' }"
          >
            <template>
              <WDesignItem
                :widget="widget"
                v-for="(widget, index) in designer.widgets"
                :key="widget.id"
                :index="index"
                :widgetsOfParent="designer.widgets"
                :designer="designer"
              />
            </template>
          </transition-group>
        </draggable>
      </PerfectScrollbar>
      <a-drawer
        :visible="showForceRender"
        placement="right"
        :get-container="false"
        :wrap-style="{ position: 'absolute' }"
        :bodyStyle="{ padding: '5px' }"
        width="100%"
        :closable="false"
      >
        <div style="text-align: right" slot="title">
          <a-button type="link" size="small" style="" icon="rollback" @click="backLastSelectedWidget">返回</a-button>
        </div>

        <PerfectScrollbar style="height: calc(100vh - 160px)">
          <WDesignItem
            v-if="showForceRender"
            :widget="designer.selectedWidget"
            :key="designer.selectedWidget.id"
            :designer="designer"
            :forceRender="true"
            :editControl="{
              enableClone: false,
              enableDrag: false,
              enableDelete: false
            }"
          />
        </PerfectScrollbar>
      </a-drawer>
    </div>
    <!-- 组件不强制渲染的情况下，对组件的预览展示 -->

    <!-- <div class="widget-build-drop-container" v-if="!forceRenderLayer">
      <div style="box-shadow: 0px 0px 10px 7px #333333ad; width: 100%; background: #fff; position: relative; z-index: 10000">
        <WDesignItem :widget="designer.selectedWidget" :key="designer.selectedWidget.id" :designer="designer" :forceRender="true" />
      </div>
    </div> -->

    <a-drawer
      title="组件定义JSON"
      placement="left"
      :closable="true"
      :mask="false"
      :visible="designer.widgetJsonDrawerVisible"
      :width="350"
      @close="designer.widgetJsonDrawerVisible = false"
    >
      <JsonViewer :value="jsonWidget" :expand-depth="3" copyable boxed sort>
        <div slot="copy">复制</div>
      </JsonViewer>
    </a-drawer>
  </div>
</template>
<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import { generateId, deepClone, queryString } from '@framework/vue/utils/util';
import md5 from '@framework/vue/utils/md5';
import JsonViewer from 'vue-json-viewer/ssr';
import 'vue-json-viewer/style.css';
import { debounce, sortBy } from 'lodash';
import { toPng } from 'html-to-image';
import WidgetDesignModal from '@pageWidget/commons/widget-design-modal.vue';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';
import widgetBuildEmptyImg from './widget-build-empty.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';

export default {
  name: 'WidgetBuildPanel',
  props: {
    designer: Object,
    pageId: String,
    allowPageVars: {
      type: Boolean,
      default: false
    },

    allowSaveAsTemplate: {
      type: Boolean,
      default: false
    },
    allowClear: {
      type: Boolean,
      default: true
    },
    allowPreview: {
      type: Boolean,
      default: true
    },
    designType: {
      type: String,
      default: 'page'
    },
    designStyle: Object,
    loading: {
      type: Boolean,
      default: false
    },
    customDndContainer: {
      type: Boolean,
      default: false
    }
  },
  mixins: [draggable],
  inject: ['pageContext', 'layoutFixed', 'draggableConfig', 'designWidgetTypes'],
  provide() {
    return {
      containerStyle: {
        height: 'calc(100vh - 100px)'
      },
      // $containerHeight: 'calc(100vh - 100px)',
      $event: undefined,
      $pageJsInstance: undefined,
      designMode: true,
      draggable: true,
      vPageState: {},
      namespace: undefined, // 以页面UUID作为命名空间
      vProvideStyle: !EASY_ENV_IS_NODE ? Vue.observable({ height: 'auto' }) : {}
    };
  },
  data() {
    return {
      hasDefaultSlot: this.$slots.default !== undefined || this.customDndContainer,
      defaultPageVars: null,
      widgetTree: [],
      widgetIdMap: {},
      visible: false,
      isFullscreen: false,
      designTemplateTitle: undefined,
      thumbnailDataURL: undefined,
      showForceRender: false,
      treeMd5: 'designWidgetTree',
      bKey: 'widgetBuildPanel' + new Date().getTime()
    };
  },

  beforeCreate() {},
  components: { JsonViewer, WidgetDesignModal, WidgetCodeEditor, widgetBuildEmptyImg, Drawer },
  computed: {
    // forceRenderLayer() {
    //   return this.designer.selectedId !== undefined && this.designer.selectedWidget.forceRender === false;
    // },
    vDesignStyle() {
      let style = {};
      if (this.designStyle) {
        let {
          enableBackground,
          backgroundColor,
          backgroundImage,
          backgroundImageInput,
          bgImageUseInput,
          backgroundPosition,
          backgroundRepeat
        } = this.designStyle;

        style.enableBackground = !!enableBackground;
        if (enableBackground) {
          if (backgroundColor) {
            style.backgroundColor = backgroundColor;
          }
          let bgImgStyle = bgImageUseInput ? backgroundImageInput : backgroundImage;
          if (bgImgStyle) {
            let isUrl =
              bgImgStyle.startsWith('data:') ||
              bgImgStyle.startsWith('http') ||
              bgImgStyle.startsWith('/') ||
              bgImgStyle.startsWith('../') ||
              bgImgStyle.startsWith('./');
            style.backgroundImage = isUrl ? `url("${bgImgStyle}")` : bgImgStyle;
          }
          if (backgroundPosition) {
            style.backgroundPosition = backgroundPosition;
          }
          if (backgroundRepeat) {
            style.backgroundRepeat = backgroundRepeat;
          }
        }
      }
      return style;
    },
    dropPanelStyle() {
      // if (this.designer.widgets.length === 0) {
      //   return {
      //     'background-image': 'url(' + this.emptySvgUrl + ');',
      //     'background-size': '250px 250px;',
      //     'background-repeat': 'no-repeat;',
      //     'background-position': '50% 35%;'
      //   };
      // }
      return {};
    },
    jsonWidget() {
      return this.designer.selectedWidget;
    },

    disableUndo() {
      return !this.designer.buildHistory.canUndo;
    },
    disableRedo() {
      return !this.designer.buildHistory.canRedo;
    },
    disableClear() {
      return this.designer.widgets.length === 0;
    }
  },
  created() {},

  methods: {
    getDrawerContainer() {
      return this.$el.querySelector('.widget-build-drop-container');
    },
    refresh() {
      this.bKey = 'build_' + new Date().getTime();
    },
    // onPasteWidget() {
    //   console.log(arguments);
    //   if (this.designer.cutWidgetInfo.widget != undefined) {
    //     this.designer.widgets.push(this.designer.cutWidgetInfo.widget);
    //     if (this.designer.cutWidgetInfo.widgetsOfParent) {
    //       this.designer.cutWidgetInfo.widgetsOfParent.splice(this.designer.cutWidgetInfo.index, 1);
    //     }
    //     this.designer.cutWidgetInfo.widget = undefined;
    //   }
    // },
    backLastSelectedWidget() {
      this.showForceRender = false;
      this.designer.selectedByID(this.designer.selectedIdHistory[this.designer.selectedIdHistory.length - 2]);
    },

    onSaveAsDesignTemplate() {
      let _this = this;
      if (this.designer.widgets.length == 0) {
        this.$message.info('无设计内容，无法另存为模板!');
        return;
      }
      _this.$loading('另存为模板中...');
      _this.designer.clearSelected();
      let containerPs = document.querySelector('.widget-build-drop-container > .ps');
      let height = containerPs.style.height;
      containerPs.style.height = '100%';
      toPng(document.querySelector('.widget-build-drop-container'))
        .then(function (dataUrl) {
          $axios
            .post(`/proxy/api/user/widgetDef/saveUserDefWidget`, {
              widgetId: _this.designType + '_user_template',
              definitionJson: JSON.stringify({
                title: _this.designTemplateTitle || ' 模板' + new Date().getTime(),
                json: JSON.stringify(_this.designer.widgets),
                wids: Object.keys(_this.designer.widgetIdMap),
                thumbnail: dataUrl
              }),
              type: 'WIDGET_AS_TEMPLATE'
            })
            .then(({ data }) => {
              _this.$loading(false);
              if (data.code == 0) {
                _this.$message.success('模板保存成功');
                _this.pageContext.emitEvent('afterSaveTemplateSuccess');
                _this.$refs.saveAsTemplateModal.visible = false;
                _this.designTemplateTitle = undefined;
                containerPs.style.height = height;
              }
            })
            .catch(() => {
              _this.$loading(false);
            });
        })
        .catch(function (error) {
          console.error('oops, something went wrong!', error);
        });
    },
    onClickDropPanel(e) {
      this.designer.clearSelected();
    },
    onFullscreen() {
      let element = document.documentElement;
      if (document.fullscreenElement == null) {
        if (element.requestFullscreen) {
          element.requestFullscreen();
        } else if (element.mozRequestFullScreen) {
          element.mozRequestFullScreen();
        } else if (element.webkitRequestFullscreen) {
          element.webkitRequestFullscreen();
        } else if (element.msRequestFullscreen) {
          element.msRequestFullscreen();
        }
      } else {
        document.exitFullscreen();
      }
    },

    clear() {
      this.designer.clear();
    },
    undo() {
      this.designer.undo();
    },
    redo() {
      this.designer.redo();
    },
    preview() {
      let urlParams = queryString(location.search.substr(1)),
        _temp = urlParams._temp || generateId();
      this.designer.tempLocalStorageKey = _temp;

      window.localStorage.setItem(
        `${_temp}`,
        JSON.stringify({
          id: this.pageId,
          widgets: this.designer.widgets,
          pageVars: JSON.parse(this.defaultPageVars),
          pageJsModule: this.designer.pageJsModule,
          pageStyle: this.vDesignStyle
        })
      );
      window.localStorage.setItem(`${_temp}_widgetMap`, JSON.stringify(this.designer.widgetIdMap));
      this.previewWindow = window.open('/page-designer/preview/' + _temp, _temp);
      if (!urlParams._temp) {
        history.pushState({}, '页面设计', `${location.pathname}${location.search}${location.search ? '&' : '?'}_temp=${_temp}`);
      }
    },

    buildPanelContainerClick() {
      this.designer.widgetSelectDrawerVisible = false;
    },
    resolveWidgetType(widget) {
      return this.designer != null ? `E${widget.wtype}` : widget.wtype;
    },
    setPageVars(val) {
      this.defaultPageVars = typeof val === 'string' ? val : JSON.stringify(val, null, '\t');
    },
    savePageVar(val) {
      this.defaultPageVars = val;
      this.designer.pageVars = JSON.parse(val);
    },

    reloadPreviewWindow: debounce(function (v, autoRefresh = false) {
      window.localStorage.setItem(
        `${this.designer.tempLocalStorageKey}`,
        JSON.stringify({ widgets: v, pageVars: JSON.parse(this.defaultPageVars), pageJsModule: this.designer.pageJsModule })
      );
      if (autoRefresh) {
        this.previewWindow.location.reload();
      }
    }, 1000),
    emitHistoryChange: debounce(function () {
      if (this.designer.undoOrRedo || this.designer.undoOrRedo === undefined) {
        if (this.designer.undoOrRedo === undefined) {
          setTimeout(() => {
            this.designer.buildHistory.initIndex = 0;
          }, 0);
        }
        // 撤销 或者 恢复操作引起的变更
        this.designer.undoOrRedo = false;
        this.designer.emitHistoryChange();
      }
    }, 500),

    pastWidget(e) {
      let txt = e.clipboardData.getData('text');
      let _this = this;
      if (txt) {
        try {
          let wgt = JSON.parse(txt),
            ids = [];

          if (!this.designWidgetTypes.has(wgt.wtype)) {
            return;
          }

          if (wgt.wtype != undefined && wgt.id && wgt.configuration) {
            // 找到组件ID ，进行替换
            let findIds = w => {
              if (typeof w == 'object' && Object.prototype.toString.call(w).toLowerCase() == '[object object]' && !w.length) {
                if (w.id && w.wtype && w.configuration) {
                  ids.push(w.id);
                  // 遍历对象配置
                  for (let field in w.configuration) {
                    findIds(w.configuration[field]);
                  }
                }
              } else if (Array.isArray(w)) {
                for (let i = 0, len = w.length; i < len; i++) {
                  findIds(w[i]);
                }
              }
            };
            findIds(wgt);
            // 替换掉ID
            for (let i = 0, len = ids.length; i < len; i++) {
              txt = txt.replaceAll(ids[i], generateId());
            }
            wgt = JSON.parse(txt);
            // 如果是锁定状态下组件，去除锁定的column属性
            if (wgt.hasOwnProperty('column')) {
              delete wgt.column;
            }
            _this.designer.undoOrRedo = true;
            // 添加到顶级
            _this.designer.widgets.push(wgt);
            e.clipboardData.clearData();
          }
        } catch (error) {
          console.log(error);
        }
      }
    }
  },
  beforeMount() {
    // this._provided.vProvideStyle.height = this.$el.querySelector('.ps').getBoundingClientRect().height - 10;
  },
  mounted() {
    // 禁止firefox浏览器拖动触发搜索功能
    document.body.ondrop = function (e) {
      e.preventDefault();
      e.stopPropagation();
    };
    // 监听全屏状态变化
    document.addEventListener('fullscreenchange', e => {
      this.isFullscreen = document.fullscreenElement != null;
    });
    let _this = this;
    document.addEventListener('paste', e => {
      _this.pastWidget(e);
    });

    document.onkeyup = e => {
      if (this.designer.selectedId && e.code == 'Delete' && e.target.tagName != 'INPUT' && e.target.tagName != 'TEXTAREA') {
        let designItem = document.querySelector(`#design-item_${this.designer.selectedId}`);
        if (designItem) {
          designItem.__vue__.deleteSelf();
        }
      }
    };
  },

  watch: {
    'designer.selectedId': {
      handler(v) {
        if (this.designer.selectedId && this.designer.selectedWidget && this.designer.selectedWidget.forceRender === false) {
          this.showForceRender = true;
        } else {
          this.showForceRender = false;
        }
        if (v == undefined) {
          this.draggableConfig.dragGroup = 'dragGroup';
          this.draggableConfig.filterWtype = [];
        }
      }
    },
    // 'designer.widgetTreeMap': {
    //   deep: true,
    //   handler(v) {
    //     console.log('组件集合变更');
    //     this.rebuildTreeNode(v);
    //   }
    // },
    // 组件定义变更触发变更历史

    'designer.widgets': {
      deep: true,
      handler(v, o) {
        // this.rebuildTreeNode();
        if (v.length == 0 && o.length == 0) {
          // 初始化的情况
          return;
        }

        this.emitHistoryChange();

        // 刷新预览页面
        if (this.previewWindow != undefined) {
          this.reloadPreviewWindow(v);
        }
      }
    }
  }
};
</script>
