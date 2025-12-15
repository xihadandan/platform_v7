<template>
  <div class="product-center-index">
    <a-row type="flex" :gutter="12">
      <a-col flex="70%">
        <div class="index-header">
          <a-page-header>
            <div slot="title">产品中心</div>
            <div slot="subTitle" class="w-ellipsis-3"></div>
          </a-page-header>
        </div>
        <a-row>
          <a-col :span="24">
            <a-card title="最近访问" :bordered="false" class="pt-card" style="height: 292px"></a-card>
            <!-- <a-card
              class="card-tabs"
              style="width: 100%; height: 298px"
              :tab-list="[
                { key: '1', tab: '最近访问' },
                { key: '2', tab: '我参与的' }
              ]"
              :active-tab-key="personCardTabKey"
              @tabChange="key => onCardTabChange(key, 'personCardTabKey')"
            ></a-card> -->
          </a-col>
        </a-row>
      </a-col>
      <a-col flex="auto">
        <a-card title="产品动态" :bordered="false" class="pt-card" style="width: 100%; height: 544px; margin-bottom: 12px"></a-card>
      </a-col>
    </a-row>
    <a-row>
      <a-col :span="24">
        <a-card class="pt-card" :bordered="false" title="产品概览" style="width: 100%; height: 267px"></a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script type="text/babel">
import '@pageAssembly/app/web/page/product-center/css/index.less';
export default {
  name: 'ProductIndex',
  props: {},
  components: {},
  computed: {},
  data() {
    return { personCardTabKey: '1', quickCreateVisible: false, text: { content: undefined } };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    startAssemble() {},
    onCardTabChange(key, prop) {
      this[prop] = key;
    }
  },
  watch: {
    text: {
      deep: true,
      handler() {
        console.log(arguments);
      }
    }
  }
};
</script>
