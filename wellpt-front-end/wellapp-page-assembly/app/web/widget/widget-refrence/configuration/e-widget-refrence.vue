<template>
  <EditWrapper
    :showWidgetName="true"
    :title="title"
    :widget="widget"
    :index="index"
    :widgetsOfParent="widgetsOfParent"
    :designer="designer"
    :parent="parent"
  >
    <a-skeleton active v-if="fetching" />

    <WidgetRefrence
      :widget="widget"
      :index="index"
      :widgetsOfParent="widgetsOfParent"
      :designer="designer"
      :parent="parent"
      @fetched="onFetched"
    />
  </EditWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';

export default {
  name: 'EWidgetRefrence',
  mixins: [editWgtMixin],
  props: {},
  components: {},
  computed: {},
  data() {
    return { title: '功能组件', fetching: true };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onFetched(d) {
      this.fetching = false;
      this.title = '功能组件' + (d.title ? ': ' + d.title : '');
    }
  }
};
</script>
