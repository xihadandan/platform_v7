<template>
  <view class="w-template" :class="customClassCom" style="width: 100%; height: auto">
    <RenderDevelopTemplate
      :isComponent="isComponent"
      :widget="widget"
      :parent="parent"
      ref="wTemplate"
      @templateMounted="onTemplateMounted"
    />
  </view>
</template>
<script>
import RenderDevelopTemplate from "@develop/RenderDevelopTemplate.vue";
import { pageContext } from "wellapp-uni-framework";
export default {
  mixins: [],
  props: {
    widget: {
      type: Object,
      required: false,
    },
    parent: Object,
  },
  components: { RenderDevelopTemplate },
  computed: {
    isComponent() {
      return this.widget.configuration.templateName;
    },
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  unmounted() {
    pageContext.unregisterComponent(this.widget.id);
  },

  methods: {
    onTemplateMounted({ componentInst }) {
      pageContext.registerComponent(this.widget.id, componentInst);
    },
  },
};
</script>
