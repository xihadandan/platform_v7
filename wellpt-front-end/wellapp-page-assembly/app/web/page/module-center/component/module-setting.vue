<template>
  <a-tabs
    default-active-key="basic"
    tabPosition="left"
    id="module-setting"
    class="ant-tabs-menu-style"
    :tabBarStyle="{ '--w-tab-menu-style-width': '224px' }"
  >
    <a-tab-pane key="basic">
      <template slot="tab">
        <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
        基本设置
      </template>
      <a-card title="基本设置" :bordered="false" style="--w-card-head-title-weight: bold">
        <ModuleDetail :detail="moduleDetail" allow-save />
      </a-card>
    </a-tab-pane>
    <a-tab-pane key="nav" v-if="false">
      <template slot="tab">
        <Icon type="pticon iconfont icon-ptkj-mokuaiqukuai"></Icon>
        模块导航
      </template>
      <ModuleIndexNav />
    </a-tab-pane>
    <a-tab-pane key="auth">
      <template slot="tab">
        <Icon type="pticon iconfont icon-ptkj-quanxian"></Icon>
        角色权限
      </template>
      <ModuleRolePrivilege />
    </a-tab-pane>
    <!-- <a-tab-pane key="request">
      <template slot="tab">
        <Icon type="pticon iconfont icon-xmch-faqifang"></Icon>访问设置
      </template>
    </a-tab-pane> -->
    <a-tab-pane key="functionWidget">
      <template slot="tab">
        <Icon type="pticon iconfont icon-ptkj-zujian"></Icon>
        功能组件
      </template>
      <ModuleFunctionWidgetList :appId="moduleDetail.id" />
    </a-tab-pane>
    <a-tab-pane key="fullSearch">
      <span slot="tab">
        <Icon type="pticon iconfont icon-ptkj-sousuochaxun" />
        全文检索
      </span>
      <!--  <module-full-search-category :moduleDetail="moduleDetail" @select="onSelect" /> -->
      <module-full-search />
    </a-tab-pane>
  </a-tabs>
</template>
<style lang="less"></style>
<script type="text/babel">
import ModuleDetail from './module-detail.vue';
import ModuleRolePrivilege from './module-role-privilege.vue';
import ModuleIndexNav from './module-nav/module-index-nav.vue';
import ModuleFunctionWidgetList from './module-function-widget-list.vue';
import ModuleFullSearch from './full-search/module-full-search.vue';
export default {
  name: 'ModuleSetting',
  props: {
    moduleDetail: Object
  },
  components: {
    ModuleDetail,
    ModuleRolePrivilege,
    ModuleIndexNav,
    ModuleFunctionWidgetList,
    ModuleFullSearch
  },
  computed: {},
  data() {
    return {
      basicForm: {},
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        name: [{ required: true, message: '模块名称必填', trigger: 'blur' }]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
