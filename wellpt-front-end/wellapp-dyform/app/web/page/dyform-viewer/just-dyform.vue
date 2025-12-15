<template>
  <HtmlWrapper title="表单">
    <WidgetDyform
      :formUuid="formUuid"
      ref="wDyform"
      @mounted="onDyformMounted"
      :dyformStyle="{ padding: 'var(--w-padding-xs) var(--w-padding-md)' }"
    />
  </HtmlWrapper>
</template>

<script type="text/babel">
import '@dyform/app/web/framework/vue/install';
import '@installPageWidget';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
import { getQueryString } from '@framework/vue/utils/function.js';
export default {
  name: 'JustDyform',
  props: {},
  data() {
    return {};
  },
  provide() {
    return {
      $pageJsInstance: {},
      designMode: false
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    formUuid() {
      return this.uuid;
    }
  },
  created() {},

  methods: {
    onDyformMounted() {
      // 有锚点时，hash值会被改成锚点当前id值
      if (location.hash == '#iframe' || getQueryString('iframe') == '1') {
        window.parent.postMessage('dyform mounted', window.parent.origin);
      }
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
