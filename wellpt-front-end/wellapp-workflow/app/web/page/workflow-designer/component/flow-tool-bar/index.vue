<template>
  <div class="graph-tool-bar">
    <div class="tool-bar-left">
      <a-button type="link" size="small" @click="handleUndo">
        <Icon type="pticon iconfont icon-luojizujian-fanhui" />
        撤销
      </a-button>
      <a-button type="link" size="small" @click="handleRedo">
        <Icon type="pticon iconfont icon-luojizujian-fanhui" style="transform: scaleX(-1)" />
        恢复
      </a-button>
      <a-button type="link" size="small" @click="handleClear">
        <Icon type="pticon iconfont icon-xmch-qingkongqingchu" />
        清空
      </a-button>
      <!-- <a-button type="link" size="small" icon="delete" @click="handleDelete"> -->
      <a-button type="link" size="small" @click="handleDelete">
        <Icon type="pticon iconfont icon-ptkj-shanchu" />
        删除
      </a-button>
      <a-button type="link" size="small" @click="handleCopy">
        <Icon type="pticon iconfont icon-ptkj-fuzhi" />
        复制
      </a-button>
      <a-button type="link" size="small" @click="handlePaste">
        <Icon type="pticon iconfont icon-ptkj-niantie" />
        粘贴
      </a-button>
      <a-button type="link" size="small" @click="handleToggleGrid">
        <Icon type="pticon iconfont icon-ptkj-wangge" />
        网格
      </a-button>
      <a-button type="link" size="small" @click="changeFullScreen">
        <Icon type="pticon iconfont icon-ptkj-tuichuquanping" v-if="fullScreen" />
        <Icon type="pticon iconfont icon-ptkj-quanping" v-else />
        {{ fullScreen ? '退出全屏' : '全屏' }}
      </a-button>
      <a-button type="link" size="small" style="color: var(--w-gray-color-5)">|</a-button>
      <a-button type="link" size="small" @click="handleCheckWorkFlow">
        <Icon type="pticon iconfont icon-ptkj-cuowujiancha" />
        错误检查
      </a-button>
      <a-button type="link" size="small" :loading="downloadLoading" @click="downloadFlowChart">
        <Icon type="pticon iconfont icon-ptkj-xiazai" />
        下载流程图
      </a-button>
    </div>
    <div class="tool-bar-right">
      <!-- 国际化 -->
      <a-tooltip title="查看国际化" placement="top" :arrowPointAtCenter="true" :align="{ offset: [0, 0] }">
        <flow-i18n-drawer />
      </a-tooltip>
      <!-- 环节搜索 -->
      <a-popover trigger="click" placement="bottomRight" :arrowPointAtCenter="true" overlayClassName="tool-search-popover">
        <node-search-select slot="content" v-model="searchKeyword" :graphItem="graphItem" />
        <a-tooltip title="搜索环节" placement="top" :arrowPointAtCenter="true" :align="{ offset: [0, 0] }">
          <a-button type="link" size="small">
            <Icon type="pticon iconfont icon-ptkj-sousuochaxun" />
          </a-button>
        </a-tooltip>
      </a-popover>
      <!-- 画布缩放 -->
      <a-tooltip title="缩小画布" placement="top" :arrowPointAtCenter="true" :align="{ offset: [0, 0] }">
        <a-button type="link" size="small" class="btn-scale-reduce" @click="handleShrink">
          <Icon type="pticon iconfont icon-a-icshuxingshezhijianshao" />
        </a-button>
      </a-tooltip>
      <a-select
        v-model="currentScale"
        :options="scaleOptions"
        :allowClear="false"
        :dropdownMatchSelectWidth="false"
        @change="changeScale"
        dropdownClassName="tool-scale-select-dropdown"
      >
        <div slot="dropdownRender" slot-scope="menu">
          <v-nodes :vnodes="menu" />
          <a-divider style="margin: 4px 0 8px" />
          <div class="ant-select-dropdown-menu-item" @click="handleAutoOrRestore('auto')">
            <Icon type="pticon iconfont icon-shiyinghuabu-01" />
            <span>适应画布</span>
          </div>
          <div class="ant-select-dropdown-menu-item" @click="handleAutoOrRestore('restore')">
            <Icon type="pticon iconfont icon-huanyuanhuabu-01" />
            <span>还原画布</span>
          </div>
        </div>
      </a-select>
      <a-tooltip title="放大画布" placement="top" :arrowPointAtCenter="true" :align="{ offset: [0, 0] }">
        <a-button type="link" size="small" class="btn-scale-add" @click="handleLarge">
          <Icon type="pticon iconfont icon-a-icshuxingshezhitianjia" />
        </a-button>
      </a-tooltip>
      <!-- 画布定位 -->
      <a-popover trigger="click" placement="bottom" :arrowPointAtCenter="true" overlayClassName="tool-position-popover">
        <div class="tool-position-container" slot="content">
          <div
            v-for="(item, index) in positionOptions"
            :key="index"
            :class="{ 'position-item': true, 'position-item-active': currentPosition === item.position }"
            @click="handlePosition(item)"
          >
            <div
              v-if="item.svg"
              :class="`svg-icon svg-icon-${item.svg}`"
              :style="`transform: rotate(${item.deg})`"
              v-html="svgIcons[item.svg]"
            ></div>
            <!-- <div v-else-if="item.deg" class="iconfont icon-zuo-01" :style="`transform: rotate(${item.deg})`"></div> -->
            <div v-else class="position-item-center"></div>
          </div>
        </div>
        <a-tooltip title="画布定位" placement="top" :arrowPointAtCenter="true" :align="{ offset: [0, 0] }">
          <a-button type="link" size="small" class="btn-svg-position">
            <Icon type="pticon iconfont icon-a-bujuzujianmaodian" />
          </a-button>
        </a-tooltip>
      </a-popover>
      <!-- 小地图 -->
      <a-tooltip title="显示或隐藏画布概览" placement="top" :arrowPointAtCenter="true" :align="{ offset: [0, 0] }">
        <a-button
          type="link"
          size="small"
          :class="{
            'tool-mini-map-btn': true,
            'btn-tool-active': showMinimap
          }"
          @click="handleMinimap"
        >
          <Icon type="pticon iconfont icon-xiaoditu-01" />
        </a-button>
      </a-tooltip>
      <!-- 环节树 -->
      <a-tooltip title="环节树" placement="top" :arrowPointAtCenter="true" :align="{ offset: [0, 0] }">
        <tool-node-tree-drawer :graphItem="graphItem" />
      </a-tooltip>
    </div>
    <div
      v-show="showMinimap"
      class="minimap-container"
      :style="{
        width: `${minimapWidth}px`,
        height: `${minimapHeight}px`
      }"
    >
      <div id="minimap"></div>
    </div>
  </div>
</template>

<script>
import { MinimapWidth, MinimapHeight } from '../designer/constant';
// import { MiniMap } from '@antv/x6-plugin-minimap';
import { getCurrentDate } from '../designer/utils';
import ToolNodeTreeDrawer from './node-tree-drawer.vue';
import NodeSearchSelect from './node-search-select.vue';
import FlowI18nDrawer from './flow-i18n-drawer.vue';

const reqFiles = require.context('./svg', false, /\.js$/);
const re = /\.\/(.*)\.js/;
const svgIcons = reqFiles.keys().reduce((files, filePath) => {
  const fileName = filePath.match(re)[1];
  const value = reqFiles(filePath);
  files[fileName] = value.default;
  return files;
}, {});

export default {
  name: 'FlowToolBar',
  props: {
    graphItem: {
      type: Object,
      default: () => {}
    },
    workflowName: {
      type: String,
      default: '流程设计'
    }
  },
  components: {
    ToolNodeTreeDrawer,
    NodeSearchSelect,
    FlowI18nDrawer,
    VNodes: {
      functional: true,
      render: (h, ctx) => {
        return ctx.props.vnodes;
      }
    }
  },
  data() {
    return {
      svgIcons,
      minimapWidth: MinimapWidth,
      minimapHeight: MinimapHeight,
      fullScreen: false,
      downloadLoading: false,
      searchKeyword: '', // 搜索关键词
      currentScale: 1,
      scaleOptions: [
        { label: '200%', value: 2 },
        { label: '150%', value: 1.5 },
        { label: '100%', value: 1 },
        { label: '80%', value: 0.8 },
        { label: '50%', value: 0.5 },
        { label: '20%', value: 0.2 }
      ],
      currentPosition: '',
      positionOptions: [
        { deg: '0deg', svg: 'topleft', position: 'top-left' },
        { deg: '90deg', svg: 'left', position: 'top' },
        { deg: '90deg', svg: 'topleft', position: 'top-right' },
        { deg: '0deg', svg: 'left', position: 'left' },
        { deg: '', position: 'center' },
        { deg: '180deg', svg: 'left', position: 'right' },
        { deg: '270deg', svg: 'topleft', position: 'bottom-left' },
        { deg: '270deg', svg: 'left', position: 'bottom' },
        { deg: '180deg', svg: 'topleft', position: 'bottom-right' }
      ],
      showMinimap: false
    };
  },
  mounted() {
    document.addEventListener('fullscreenchange', this.fullscreenchangeHandler);
  },
  beforeDestroy() {
    document.removeEventListener('fullscreenchange', this.fullscreenchangeHandler);
  },
  methods: {
    // 画布定位
    handlePosition(item) {
      this.currentPosition = item.position;
      if (item.position === this.positionOptions[4]['position']) {
      }
      // centerPoint
      this.graphItem.positionContent(item.position, { padding: 20 });
    },
    handleMinimap() {
      this.showMinimap = !this.showMinimap;
      if (!this.graphItem.getPlugin('minimap')) {
        // const { MiniMap } = require('@antv/x6-plugin-minimap');
        // this.graphItem.graph.use(
        //   new MiniMap({
        //     container: document.getElementById('minimap'),
        //     width: MinimapWidth,
        //     height: MinimapHeight
        //   })
        // );
        // const { default: MiniMap } = require('../graph/PluginMiniMap');
        this.graphItem.useMiniMap();
      }
    },
    changeScale(value) {
      this.graphItem.zoomTo(value);
    },
    // 画布自适应、还原画布
    handleAutoOrRestore(value) {
      if (value === 'auto') {
        this.graphItem.zoomToFit({ padding: 20 });
        this.setCurrentScale();
      } else if (value === 'restore') {
        this.graphItem.zoomTo(1);
        this.currentScale = this.scaleOptions[2]['value'];
      }
    },
    setCurrentScale() {
      let currentScale = this.graphItem.zoom();
      currentScale = parseInt(currentScale * 100) + '%';
      const findScale = this.scaleOptions.find(item => item.label === currentScale);
      if (findScale) {
        currentScale = findScale.value;
      }
      this.currentScale = currentScale;
    },
    // 放大
    handleLarge() {
      this.graphItem.zoom(0.1);
      this.setCurrentScale();
    },
    // 缩小
    handleShrink() {
      this.graphItem.zoom(-0.1);
      this.setCurrentScale();
    },
    // 撤销
    handleUndo() {
      if (this.graphItem.canUndo()) {
        this.graphItem.undo();
      }
    },
    // 恢复
    handleRedo() {
      if (this.graphItem.graph.canRedo()) {
        this.graphItem.graph.redo();
      }
    },
    // 清空
    handleClear() {
      if (this.graphItem && !this.graphItem.cellsEmpty) {
        this.$confirm({
          title: '提示',
          content: '确认要清空吗?',
          okText: '清空',
          onOk: () => {
            this.graphItem.clearCells();
          }
        });
      }
    },
    // 删除
    handleDelete() {
      const cells = this.graphItem.getSelectedCells();
      if (cells.length) {
        this.graphItem.removeCells(cells);
      }
    },
    // 复制
    handleCopy() {
      const { graph } = this.graphItem;
      let cells = this.graphItem.getSelectedCells();
      cells = cells.filter(cell => cell.shape !== 'EdgeDirection'); // 边不复制
      if (cells.length) {
        graph.copy(cells);
      }
    },
    // 粘贴
    handlePaste() {
      const { graph } = this.graphItem;
      if (!graph.isClipboardEmpty()) {
        const cells = graph.paste({ offset: 32 });
        this.graphItem.batchUpdateCellAndDataId(cells);
        graph.cleanSelection();
      }
    },
    // 网格切换
    handleToggleGrid() {
      const { graph } = this.graphItem;
      const { grid } = graph.options;
      if (grid.visible) {
        graph.hideGrid();
      } else {
        graph.showGrid();
      }
    },
    // 下载流程图
    downloadFlowChart() {
      this.downloadLoading = true;
      this.$loading('生成中');

      const ele = document.querySelector('#graph-container');
      const copyEle = ele.cloneNode(true); // 包括子节点
      copyEle.id = 'graph-container-copy';
      copyEle.style.cssText += `;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 2;
      `;
      ele.parentNode.insertBefore(copyEle, ele);

      const { graph } = this.graphItem;
      const { width, height } = graph.options;
      const { tx, ty } = graph.translate();
      const zoom = graph.zoom();
      const area = graph.getContentArea();
      graph.resize(area.width, area.height);
      graph.zoomToFit({ padding: 20 });

      const picName = this.workflowName + ' ' + getCurrentDate('yyyy-MM-DD HH_mm_ss');
      require('../designer/tools')
        .downloadFlow({ picName })
        .then(
          () => {
            graph.resize(width, height);
            graph.zoomTo(zoom);
            graph.translate(tx, ty);
            copyEle.remove();

            this.downloadLoading = false;
            this.$loading(false);
          },
          error => {
            this.downloadLoading = false;
            this.$loading(false);
          }
        );
    },
    handleCheckWorkFlow() {
      this.$emit('checkWorkFlow');
    },
    // 退出全屏
    exitFullscreen() {
      if (document.exitFullscreen) {
        document.exitFullscreen();
      } else if (document.webkitExitFullscreen) {
        /* Safari */
        document.webkitExitFullscreen();
      } else if (document.mozCancelFullScreen) {
        /* Firefox */
        document.mozCancelFullScreen();
      } else if (document.msExitFullscreen) {
        /* IE/Edge */
        document.msExitFullscreen();
      }
    },
    // 启动全屏
    launchFullscreen() {
      const element = document.documentElement;
      if (element.requestFullscreen) {
        element.requestFullscreen();
      } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
      } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen();
      } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen();
      }
    },
    changeFullScreen() {
      if (this.fullScreen) {
        this.exitFullscreen();
      } else {
        this.launchFullscreen();
      }
    },
    fullscreenchangeHandler(event) {
      this.fullScreen = !this.fullScreen;
    }
  }
};
</script>
