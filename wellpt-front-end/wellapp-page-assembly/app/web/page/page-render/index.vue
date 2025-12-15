<template>
  <HtmlWrapper :title="title">
    <!-- 临时处理，后续提供错误页 -->
    <Error :error-code="errorCode" v-if="errorCode" />
    <WidgetVpage
      v-else="errorCode != undefined"
      :widget="widget"
      :pageUuid="pageUuid"
      :pageId="pageId"
      :_unauthorizedResource="unauthorizedResource"
      :widgetI18ns="widgetI18ns"
      :errorCode="errorCode"
      @rendered="onPageRendered"
      ref="page"
      :key="pageKey"
    >
      <UserPreferenceCustomize ref="userPreferenceCustomize" />
    </WidgetVpage>
  </HtmlWrapper>
</template>
<style lang="less" scoped></style>
<script type="text/babel">
import '@installPageWidget';
// import '@dyform/app/web/framework/vue/install';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
import '@framework/vue/lib/linkSys.js';
import UserPreferenceCustomize from '@pageAssembly/app/web/lib/user-preference-customize.vue';
import { generateId } from '@framework/vue/utils/util';
import { getQueryString } from '@framework/vue/utils/function.js';

export default {
  name: 'PageRender',
  props: {},
  data() {
    return { pageKey: generateId() };
  },
  provide() {
    return {
      designMode: false,
      ENVIRONMENT: this.ENVIRONMENT || {},
      unauthorizedResource: [],
      previewMode: false,
      layoutFixed: this.layoutFixed,
      fullSearchDefinition: this.fullSearchDefinition,
      tenantProdVersionUuid: this.tenantProdVersionUuid
    };
  },

  beforeCreate() {},
  components: { UserPreferenceCustomize },
  computed: {},
  created() {},

  methods: {
    onPageRendered() {
      // 有锚点时，hash值会被改成锚点当前id值
      if (location.hash == '#iframe' || getQueryString('iframe') == '1') {
        window.parent.postMessage('vPage Rendered', window.parent.origin);
      }
    },
    refresh({ pageId, pageUuid, widget, unauthorizedResource }) {
      this.pageKey = generateId();
      if (pageId != undefined) {
        this.pageId = pageId;
        this.widget = undefined;
      } else if (pageUuid != undefined) {
        this.pageUuid = pageUuid;
        this.widget = undefined;
      } else if (widget != undefined) {
        this.widget = widget;
      }
      if (unauthorizedResource != undefined) {
        this.unauthorizedResource.splice(0, this.unauthorizedResource.length);
        this.unauthorizedResource.push(...unauthorizedResource);
      }
    }
  },
  beforeMount() {
    if (location.hash == '#iframe') {
      if (window.parent.location.pathname.indexOf('/module/assemble') != -1) {
        this._provided.previewMode = true;
      }
    }
  },
  mounted() {}
};
</script>
