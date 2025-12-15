<template>
  <uni-card class="w-card" :is-shadow="true" v-show="vShow">
    <template v-slot:title>
      <uni-section
        :title="widget.configuration.hideTitle ? '' : widget.configuration.title"
        :sub-title="widget.configuration.hideSubTitle ? '' : widget.configuration.subTitle"
      >
        <template v-slot:right v-if="widget.configuration.headerButton.enable">
          <text @click="onPopupButtons" style="color: var(--color-primary)">操作</text>
        </template>
      </uni-section>
    </template>
    <template v-for="(wgt, w) in widget.configuration.widgets">
      <w-widget :widget="wgt" :parent="widget" :key="wgt.id"></w-widget>
    </template>
    <w-button-action-sheet :button="widget.configuration.headerButton.buttons" ref="buttonSheet" />
  </uni-card>
</template>

<script>
import mixin from "../page-widget-mixin";
export default {
  mixins: [mixin],
  name: "w-card",
  data() {
    return {};
  },
  components: {
    "w-widget": () => import("../w-widget/w-widget.vue"),
  },
  created() {},
  mounted: function () {},
  methods: {
    onPopupButtons() {
      this.$refs.buttonSheet.show = true;
    },
  },
};
</script>
