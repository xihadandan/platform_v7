<template>
  <HtmlWrapper :title="title">
    <WidgetDyformSetting :widget="widget" :initFormData="initFormData" @titleChanged="changeTitle" />
  </HtmlWrapper>
</template>
<style lang="less">
body {
  #app {
    > .widget-dyform-setting {
      .widget-dyform-setting-content {
        margin: var(--w-margin-xs) var(--w-margin-md) var(--w-margin-xs);
      }
      .widget-dyform-setting-head + .widget-dyform-setting-content {
        margin-top: 0;
      }
    }
  }
}
</style>
<script type="text/babel">
// import { addWindowResizeHandler } from '@framework/vue/utils/util';
import '@dyform/app/web/framework/vue/install';
import '@installPageWidget';
import '@installWorkflowWidget';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件

export default {
  name: 'DyformViewer',
  data() {
    return {
      title: '表单'
      // scrollerHeight: '900px'
    };
  },
  provide() {
    return { designMode: false };
  },
  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    changeTitle(title) {
      this.title = title ? title : '表单';
    }
  },
  mounted() {
    // this.scrollerHeight = window.innerHeight - 65 + 'px';
    // addWindowResizeHandler(() => {
    //   this.$nextTick(() => {
    //     this.scrollerHeight = window.innerHeight - 65 + 'px';
    //   });
    // });
  }
};
</script>
