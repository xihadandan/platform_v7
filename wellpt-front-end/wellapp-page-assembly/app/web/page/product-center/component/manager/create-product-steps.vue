<template>
  <div>
    <a-steps :current="step">
      <a-step v-for="s in steps" :key="s.title" :title="s.title" />
    </a-steps>
    <a-card :bordered="false" size="small" style="width: 100%; height: 100%">
      <keep-alive>
        <component :is="steps[step].component" v-bind="product" />
      </keep-alive>
    </a-card>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import ProductBasicInfo from './product-basic-info.vue';
import ProductModule from './product-module.vue';
import ProductPageSetting from './product-page-setting.vue';
import ProductRolePrivilege from './product-role-privilege.vue';
import ProductSysSetting from './product-sys-setting.vue';
import ProductCreateDone from './product-create-done.vue';
export default {
  name: 'CreateProductSteps',
  props: { step: Number },
  components: { ProductBasicInfo, ProductModule, ProductPageSetting, ProductRolePrivilege, ProductSysSetting, ProductCreateDone },
  computed: {},
  data() {
    return {
      product: {
        detail: {},
        moduleIds: []
      },
      steps: [
        {
          title: '基本信息',
          content: '填写系统的基本信息',
          component: ProductBasicInfo.name
        },
        {
          title: '选择模块',
          content: '选择系统包含的模块',
          component: ProductModule.name
        },
        {
          title: '系统设置',
          content: '配置系统在前台展示的页面',
          component: ProductPageSetting.name
        },
        {
          title: '角色权限',
          content: '设置系统级的角色和角色下的权限',
          component: ProductRolePrivilege.name
        },
        {
          title: '系统管理',
          content: '为系统搭配相应的后台管理能力',
          component: ProductSysSetting.name
        },
        {
          title: '完成配置',
          component: ProductCreateDone.name
        }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: { save() {} }
};
</script>
