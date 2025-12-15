<template>
  <uni-row class="w-row">
    <template v-for="(col, i) in widget.configuration.cols">
      <uni-col :span="24" :key="'col_' + col.id">
        <template v-for="(wgt, w) in col.configuration.widgets">
          <w-widget :widget="wgt" :parent="widget" :key="wgt.id"></w-widget>
        </template>
      </uni-col>
    </template>
  </uni-row>
</template>

<script>
import mixin from "../page-widget-mixin";
export default {
  mixins: [mixin],
  name: "w-grid",
  data() {
    return {};
  },
  components: {
    "w-widget": () => import("../w-widget/w-widget.vue"),
  },
  created() {},
  mounted: function () {},
  methods: {},
};
</script>
