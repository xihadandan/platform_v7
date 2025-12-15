<template>
  <a-tabs default-active-key="allProduct" class="prod-manager-list-tabs">
    <a-tab-pane key="allProduct" tab="产品管理" style="padding: 0px">
      <ProductList />
    </a-tab-pane>
    <a-tab-pane key="myResponsible" tab="我负责的" style="padding: 0px"></a-tab-pane>
    <a-tab-pane key="myManager" tab="我管理的" style="padding: 0px"></a-tab-pane>
  </a-tabs>
</template>
<script type="text/babel">
import ProductList from './product-list.vue';
import '@pageAssembly/app/web/page/product-center/css/index.less';

export default {
  name: 'ProductManager',
  props: {},
  components: { ProductList },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
