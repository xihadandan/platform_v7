<template>
  <div class="module-center-index">
    <a-row>
      <a-col :span="24" class="index-header">
        <a-page-header title="装配中心">
          <div slot="title">装配中心</div>
          <div slot="subTitle" class="w-ellipsis-3">装配中心介绍文案</div>
          <a-button type="primary" @click="quickCreateVisible = true">开始装配</a-button>
        </a-page-header>
        <QuickCreateSelect v-model="quickCreateVisible" />
      </a-col>
    </a-row>
    <a-row type="flex" :gutter="16">
      <a-col flex="auto">
        <a-row>
          <a-col :span="24">
            <a-card
              class="card-tabs"
              :bordered="false"
              style="width: 100%; height: 252px"
              :tab-list="[
                { key: '1', tab: '最近访问' },
                { key: '2', tab: '我参与的' }
              ]"
              :active-tab-key="personCardTabKey"
              @tabChange="key => onCardTabChange(key, 'personCardTabKey')"
            ></a-card>
          </a-col>
          <a-col :span="24" style="margin-top: 16px">
            <a-card class="pt-card" :bordered="false" title="项目概览" style="width: 100%; height: 243px"></a-card>
          </a-col>
        </a-row>
      </a-col>
      <a-col flex="400px">
        <a-card class="pt-card" :bordered="false" title="团队动态" style="width: 100%; height: 512px"></a-card>
      </a-col>
    </a-row>
  </div>
</template>
<style lang="less">
.module-center-index {
  background: var(--w-widget-page-layout-bg-color);
  width: 100%;
  overflow-x: hidden;
  .index-header {
    height: 300px;
    padding: 40px 20px;
    background: url(/static/image/module-center-banner.png) no-repeat;
    background-size: 100% 100%;
    border-radius: 2px;
    border: 2px solid #fff;
    position: relative;
    .ant-page-header-content {
      padding-top: 36px;
    }
    .ant-page-header-heading-title {
      color: #333333;
      font-size: 36px;
      margin-bottom: 12px;
    }
    .ant-page-header-heading-sub-title {
      width: 512px;
      clear: both;
      font-size: 16px;
      color: var(--w-text-color-base);
    }
  }
  .ant-row {
    margin-bottom: 12px;
  }
  .pt-card {
    --w-card-head-title-weight: bold;
  }
}
</style>
<script type="text/babel">
import QuickCreateSelect from './module-quick-create-select.vue';

export default {
  name: 'ModuleIndex',
  props: {},
  components: { QuickCreateSelect },
  computed: {},
  data() {
    return { personCardTabKey: '1', quickCreateVisible: false };
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
  }
};
</script>
