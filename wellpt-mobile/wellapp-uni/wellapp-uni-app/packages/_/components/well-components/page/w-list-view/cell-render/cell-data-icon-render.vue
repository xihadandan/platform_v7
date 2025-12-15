<template>
  <view>
    <w-icon v-if="isJson" :iconConfig="text" :size="20"></w-icon>
    <w-icon v-else :icon="text" :size="16"></w-icon>
  </view>
</template>
<style lang="scss"></style>
<script type="text/babel">
import cellRenderMixin from "./cellRenderMixin";
import { utils } from "wellapp-uni-framework";
export default {
  name: "CellDataIconRender",
  mixins: [cellRenderMixin],
  title: "图标渲染器",
  props: {},
  components: {},
  computed: {
    isJson() {
      return this.text && utils.isValidJSON(this.text);
    },
  },
  data() {
    return {
      loading: false,
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {},
};
</script>
