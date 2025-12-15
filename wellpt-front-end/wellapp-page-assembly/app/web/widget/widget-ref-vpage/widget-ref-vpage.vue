<template>
  <div
    class="widget-ref-vpage"
    :style="{
      pointerEvents: designMode ? 'none' : 'unset'
    }"
  >
    <WidgetVpage
      v-if="pageId != undefined"
      :pageId="pageId"
      :key="pageId + widget.id"
      @pageError="onPageError"
      :errorRenderDisabled="widget.configuration.errorCustomize"
    />
    <template v-else>
      <a-empty v-if="designMode" description="暂无选择页面" />
    </template>
    <ErrorRender
      v-if="widget.configuration.errorCustomize && errorCode != undefined && errorTemplateConfig && errorTemplateConfig[errorCode]"
      :templateConfig="errorTemplateConfig[errorCode]"
      :widget="widget"
    />
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { executeJSFormula } from '@framework/vue/utils/util';
import ErrorRender from './error-render.js';
export default {
  name: 'WidgetRefVpage',
  mixins: [widgetMixin],
  props: {},
  components: { ErrorRender },
  computed: {
    errorTemplateConfig() {
      let map = {};
      if (this.widget.configuration.errorTemplateConfig) {
        for (let item of this.widget.configuration.errorTemplateConfig) {
          map[item.code] = item;
        }
      }
      return map;
    }
  },
  data() {
    return {
      pageId: this.widget.configuration.pageSourceType == 'constant' ? this.widget.configuration.pageId : undefined,
      errorCode: undefined
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onPageError({ errorCode }) {
      this.errorCode = errorCode;
    },
    afterChangeableDependDataChanged() {
      if (this.widget.configuration.pageSourceType == 'expr' && this.widget.configuration.pageExpr != undefined) {
        executeJSFormula(this.widget.configuration.pageExpr.value, this.widgetDependentVariableDataSource()).then(result => {
          if (result != undefined) {
            this.pageId = result;
          }
        });
      }
    }
  },
  watch: {
    pageId: {
      handler(newValue, oldValue) {
        this.errorCode = undefined;
      }
    }
  }
};
</script>
