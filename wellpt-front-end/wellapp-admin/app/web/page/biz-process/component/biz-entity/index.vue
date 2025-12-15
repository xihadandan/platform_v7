<template>
  <a-card size="small" title="业务主体" :bordered="false">
    <a-menu mode="vertical" @click="onMenuClick">
      <a-menu-item key="1" :title="formState.title">业务主体表单</a-menu-item>
      <a-menu-item key="2">业务主体信息</a-menu-item>
    </a-menu>
  </a-card>
</template>

<script>
import BizEntityDyform from './biz-entity-dyform.vue';
import BizEntityPage from './biz-entity-page.vue';
export default {
  props: {
    entity: Object
  },
  components: { BizEntityDyform, BizEntityPage },
  inject: ['assemble'],
  computed: {
    formState() {
      return this.assemble.getBizFormState();
    }
  },
  methods: {
    onMenuClick({ key }) {
      let content = { component: undefined, metadata: undefined };
      if (key == '1') {
        content = {
          component: BizEntityDyform,
          metadata: { entity: this.entity }
        };
      } else if (key == '2') {
        content = {
          component: BizEntityPage,
          metadata: { entity: this.entity }
        };
      }
      this.assemble.showContent(content);
    }
  }
};
</script>

<style></style>
