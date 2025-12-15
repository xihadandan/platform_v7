<template>
  <HtmlWrapper title="产品中心">
    <a-layout class="product-center" v-if="selectedKey == undefined">
      <a-layout :hasSider="true">
        <a-layout-sider theme="light" :width="200" class="left-sider">
          <a-menu style="width: 199px" v-model="selectedMenuKey" @select="onSelectMenu">
            <a-menu-item key="ProductIndex">首页</a-menu-item>
            <a-menu-item key="ProductManager">产品管理</a-menu-item>
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
import ProductIndex from './component/product-index.vue';
import ProductManager from './component/manager/product-manager.vue';
export default {
  name: 'ProductAssembleCenter',
  props: {},
  components: { ProductIndex, ProductManager },
  computed: {},
  data() {
    return { selectedMenuKey: ['ProductIndex'] };
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
