<template>
  <w-panel :widget="widget">
    <template slot="menu">
      <uni-icons type="plusempty" size="22" @click="menuItemClick"></uni-icons>
    </template>
  </w-panel>
</template>

<script>
export default {
  props: {
    widget: {
      type: Object,
      required: true,
    },
  },
  methods: {
    menuItemClick: function () {
      uni.navigateTo({
        url: "/uni_modules/w-app/pages/workflow/start_new_work",
      });
    },
  },
};
</script>
