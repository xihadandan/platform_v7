<template>
  <HtmlWrapper title="装配中心">
    <a-layout class="module-assemble-center" v-if="selectedKey == undefined">
      <a-layout :hasSider="true">
        <a-layout-sider theme="light" :width="200" class="left-sider">
          <a-menu style="width: 199px" :defaultOpenKeys="['ModuleIndex']" v-model="selectedMenuKey" @select="onSelectMenu">
            <a-menu-item key="ModuleIndex">首页</a-menu-item>
            <a-menu-item key="ModuleView">模块装配</a-menu-item>
            <a-menu-item key="ModuleManagerList">模块管理</a-menu-item>
            <a-menu-item key="ModuleTemplateManager">模板中心</a-menu-item>
            <a-menu-item key="ModuleWidgetManager">组件中心</a-menu-item>
          </a-menu>
        </a-layout-sider>
        <a-layout-content>
          <PerfectScrollbar class="layout-content-scroll">
            <component :is="selectedMenuKey[0]" />
            <a-back-top :target="backTopTarget" style="right: 30px" />
          </PerfectScrollbar>
        </a-layout-content>
      </a-layout>
    </a-layout>
    <component :is="selectedKey" v-else />
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import './css/index.less';
import ModuleIndex from './component/index.vue';
import ModuleView from './component/module-view.vue';
import ModuleManagerList from './component/module-manager-list.vue';
import ModuleTemplateManager from './component/module-template-manager.vue';
import ModuleWidgetManager from './component/module-widget-manager.vue';

export default {
  name: 'ModuleAssembleCenter', // 装配首页
  props: {},
  components: { ModuleIndex, ModuleView, ModuleManagerList, ModuleTemplateManager, ModuleWidgetManager },
  computed: {},
  data() {
    return { selectedMenuKey: ['ModuleIndex'] };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    backTopTarget() {
      return this.$el.querySelector('.layout-content-scroll');
    },
    onSelectMenu({ item, key, selectedKeys }) {
      this.selectedMenuKey = selectedKeys;
    }
  }
};
</script>
