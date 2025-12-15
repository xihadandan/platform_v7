<template>
  <div>
    <a-button
      type="link"
      size="small"
      :class="{
        'btn-open-global': true,
        'btn-tool-active': visible
      }"
      @click="openDrawer"
    >
      <Icon type="pticon iconfont icon-ptkj-guojihua-01" />
    </a-button>

    <drawer
      v-if="containerCreated"
      v-model="visible"
      width="calc(100vw - var(--workflow-designer-configuration-width))"
      :closable="false"
      :container="getContainer"
      :wrapStyle="{
        position: 'absolute'
      }"
    >
      <div class="flow-i18n-wrapper" slot="content">
        <flow-i18n-view />
      </div>
    </drawer>
    <!-- <a-drawer
      placement="right"
      :closable="false"
      :mask="false"
      width="calc(100vw - var(--workflow-designer-configuration-width))"
      :wrapStyle="{
        position: 'absolute',
        top: 'var(--workflow-tool-bar-height)',
        height: 'calc(100vh - var(--workflow-designer-header-height) - var(--workflow-tool-bar-height))'
      }"
      :visible="visible"
      @close="closeDrawer"
      :getContainer="getContainer"
      :destroyOnClose="true"
      class="pt-drawer flow-i18n-drawer"
    >
      <div class="flow-i18n-wrapper">
        <flow-i18n-view />
      </div>
    </a-drawer> -->
  </div>
</template>

<script>
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import FlowI18nView from './flow-i18n-view.vue';
const drawerSelector = '.graph-wrapper';

export default {
  name: 'FlowI18nDrawer',
  inject: ['pageContext', 'graph'],
  data() {
    return {
      containerCreated: false,
      visible: false
    };
  },
  components: {
    FlowI18nView,
    Drawer
  },
  computed: {},
  mounted() {
    this.pageContext.handleEvent('closeAllDrawer', this.closeDrawer);
  },
  beforeDestroy() {
    this.pageContext.offEvent('closeAllDrawer');
  },
  methods: {
    closeDrawer() {
      this.visible = false;
    },
    openDrawer() {
      if (this.visible) {
        this.closeDrawer();
        return;
      }
      this.pageContext.emitEvent('closeAllDrawer');
      if (!this.containerCreated) {
        this.containerCreated = true;
      }
      this.visible = true;
    },
    getContainer() {
      return document.querySelector(drawerSelector);
    }
  }
};
</script>
