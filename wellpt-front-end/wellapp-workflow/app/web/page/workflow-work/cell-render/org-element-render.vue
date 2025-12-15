<template>
  <div>
    <OrgSelect v-model="text" textonly :titleDisplay="showType"></OrgSelect>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import cellRenderMixin from './cellRenderMixin';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
export default {
  name: 'OrgElementRender',
  mixins: [cellRenderMixin],
  title: '组织用户渲染器',
  props: {},
  components: { OrgSelect },
  computed: {},
  data() {
    let showType = this.slotOption.options.showType;
    return {
      showType
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
