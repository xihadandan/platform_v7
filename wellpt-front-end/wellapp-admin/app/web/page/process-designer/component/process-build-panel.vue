<template>
  <div class="widget-build-container" @click="buildPanelContainerClick">
    <a-row class="widget-build-toolbar" style="padding-left: 12px">
      <a-col :span="20">
        <!-- 加阶段  -->
        <a-button type="link" size="small" title="加阶段" @click.stop="addNode" :disabled="disableAddNode">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          加阶段
        </a-button>
        <!-- 加事项  -->
        <a-button type="link" size="small" title="加事项" @click.stop="addItem" :disabled="disableAddItem">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          加事项
        </a-button>
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
        <!-- 清空 -->
        <a-button type="link" size="small" :title="$t('PageDesigner.toolbar.clear', '清空')" @click.stop="clear" :disabled="disableClear">
          <Icon type="pticon iconfont icon-xmch-qingkongqingchu"></Icon>
          {{ $t('PageDesigner.toolbar.clear', '清空') }}
        </a-button>
        <!-- 删除 -->
        <a-button type="link" size="small" title="删除" @click.stop="remove">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          删除
        </a-button>
        <!-- 布局切换 -->
        <a-button type="link" size="small" title="布局切换" @click.stop="switchLayout">
          <Icon type="pticon iconfont icon-hengshuqiehuan-01"></Icon>
          布局切换
        </a-button>
        <!-- 剪切 -->
        <!-- <a-button type="link" size="small" icon="scissor" title="剪切" @click.stop="cut">剪切</a-button> -->
        <!-- 粘贴 -->
        <!-- <a-button type="link" size="small" icon="snippets" title="粘贴" @click.stop="paste">粘贴</a-button> -->
      </a-col>
      <a-col :span="4" :style="{ textAlign: 'right', paddingRight: '12px' }">
        <a-button type="link" size="small" :title="isFullscreen ? '退出全屏' : '全屏'" @click.stop="onFullscreen">
          <Icon :type="isFullscreen ? 'pticon iconfont icon-ptkj-tuichuquanping' : 'pticon iconfont icon-ptkj-quanping'"></Icon>
          <!-- {{ isFullscreen ? '退出全屏' : '全屏' }} -->
        </a-button>
      </a-col>
    </a-row>
    <div v-show="designer.isEmpty" class="design-empty-info" style="width: 660px; margin-top: -260px">
      <ProcessBuildEmptyImg></ProcessBuildEmptyImg>
      <ProcessBuildEmpty :designer="designer"></ProcessBuildEmpty>
    </div>
    <div id="desinger-container" v-show="!designer.isEmpty"></div>
  </div>
</template>

<script>
import { invokeVueMethod } from '../designer/utils.js';
import ProcessBuildEmptyImg from './process-build-empty-img.vue';
import ProcessBuildEmpty from './process-build-empty.vue';
export default {
  name: 'ProcessBuildPanel',
  props: {
    designer: Object
  },
  components: { ProcessBuildEmptyImg, ProcessBuildEmpty },
  data() {
    return { isFullscreen: false };
  },
  computed: {
    disableAddNode() {
      return this.designer.selectedNodeType != 'node-group' && this.designer.selectedNodeType != 'node';
    },
    disableAddItem() {
      return this.designer.selectedNodeType != 'item-group' && this.designer.selectedNodeType != 'item';
    },
    disableUndo() {
      return !this.designer.canUndo;
    },
    disableRedo() {
      return !this.designer.canRedo;
    },
    disableClear() {
      return this.designer.isEmpty;
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
    buildPanelContainerClick() {},
    addNode() {
      let selectedNode = this.designer.getSelectedNode();
      let nodeData = selectedNode.getData();
      if (nodeData.type == 'node-group') {
        let children = this.designer.getNodeChildren(selectedNode);
        if (children.length > 0) {
          selectedNode = children[children.length - 1];
        }
      }
      invokeVueMethod(selectedNode.vm, 'onAddNodeOrItemClick', 'right');
    },
    addItem() {
      let selectedNode = this.designer.getSelectedNode();
      let nodeData = selectedNode.getData();
      if (nodeData.type == 'item-group') {
        let children = this.designer.getNodeChildren(selectedNode);
        if (children.length > 0) {
          selectedNode = children[children.length - 1];
        }
      }
      invokeVueMethod(selectedNode.vm, 'onAddItemClick');
    },
    undo() {
      this.designer.undo();
    },
    redo() {
      this.designer.redo();
    },
    clear() {
      this.designer.clear();
    },
    switchLayout() {
      this.designer.switchLayout();
    },
    remove() {
      let selectedNode = this.designer.getSelectedNode();
      if (selectedNode == null) {
        this.$message.error('请选择要删除的节点！');
      }
      let refInfo = this.designer.getNodeRefInfo(selectedNode);
      if (refInfo.ref && !refInfo.directRef) {
        this.$message.error('引用阶段下的子阶段/事项不能删除！');
        return;
      }
      this.designer.removeNode(selectedNode);
    },
    cut() {},
    paste() {},
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
#desinger-container {
  height: 100%;
}
</style>
