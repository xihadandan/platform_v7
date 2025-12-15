<template>
  <div class="widget-build-container" @click="buildPanelContainerClick">
    <a-row class="widget-build-toolbar" style="padding-left: 12px">
      <a-col :span="20">
        <!-- 撤销 -->
        <a-button type="link" size="small" :title="$t('PageDesigner.toolbar.undo', '撤销')" @click.stop="undo" :disabled="disableUndo">
          <Icon type="pticon iconfont icon-luojizujian-fanhui"></Icon>
          {{ $t('PageDesigner.toolbar.undo', '撤销') }}
        </a-button>
        <!-- 重做 -->
        <a-button type="link" size="small" :title="$t('PageDesigner.toolbar.redo', '恢复')" @click.stop="redo" :disabled="disableRedo">
          <Icon type="pticon iconfont icon-luojizujian-fanhui" style="transform: scaleX(-1)"></Icon>
          {{ $t('PageDesigner.toolbar.redo', '恢复') }}
        </a-button>
        <!-- 重置 -->
        <a-button type="link" size="small" title="重置" @click.stop="reset" :disabled="disableReset">
          <Icon type="pticon iconfont icon-ptkj-nishizhenxuanzhuan"></Icon>
          重置
        </a-button>
      </a-col>
      <a-col :span="4" :style="{ textAlign: 'right', paddingRight: '12px' }">
        <a-button type="link" size="small" :title="isFullscreen ? '退出全屏' : '全屏'" @click.stop="onFullscreen">
          <Icon :type="isFullscreen ? 'pticon iconfont icon-ptkj-tuichuquanping' : 'pticon iconfont icon-ptkj-quanping'"></Icon>
          <!-- {{ isFullscreen ? '退出全屏' : '全屏' }} -->
        </a-button>
      </a-col>
    </a-row>
    <div id="item-flow-desinger-container" @dragover.stop="onDragover"></div>
  </div>
</template>

<script>
import { invokeVueMethod } from '../designer/utils.js';
export default {
  name: 'ItemFlowBuildPanel',
  props: {
    designer: Object
  },
  data() {
    return { isFullscreen: false };
  },
  computed: {
    disableUndo() {
      return !this.designer.canUndo;
    },
    disableRedo() {
      return !this.designer.canRedo;
    },
    disableReset() {
      return !this.designer.currentItemFlow;
    }
  },
  mounted() {
    document.addEventListener('fullscreenchange', () => {
      if (document.fullscreenElement) {
        this.isFullscreen = true;
      } else {
        this.isFullscreen = false;
      }
    });
  },
  methods: {
    onDragover(e) {
      const _this = this;
      let cellId = _this.findParentCellId(e.target, 'g');
      _this.designer.updateDragInfo({ x: e.offsetX, y: e.offsetY, dragoverCellId: cellId });
    },
    findParentCellId(element, tagName) {
      let tagElement = element;
      while (tagElement != null && tagElement.tagName != tagName) {
        tagElement = tagElement.parentNode;
      }

      let cellId = '';
      if (tagElement != null) {
        cellId = tagElement.getAttribute('data-cell-id');
      }
      return cellId;
    },
    buildPanelContainerClick() {},
    undo() {
      this.designer.undo();
    },
    redo() {
      this.designer.redo();
    },
    reset() {
      const _this = this;
      this.$confirm({
        title: '确认框',
        content: '重置会删除事项配置外的所有配置信息，确认要重置吗?',
        okText: '确定',
        cancelText: '取消',
        onOk() {
          _this.designer.reset();
        }
      });
    },
    remove() {
      let selectedNode = this.designer.getSelectedNode();
      if (selectedNode == null) {
        this.$message.error('请选择要删除的节点！');
      }
      this.designer.removeNode(selectedNode);
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
    }
  }
};
</script>

<style lang="less" scoped>
.widget-build-container {
  height: 100%;
}
#item-flow-desinger-container {
  height: 100%;
}
</style>
