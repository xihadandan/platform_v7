<template>
  <w-list-view :widget="widget" @buttonClick="buttonClick"></w-list-view>
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
    buttonClick: function (button, selection) {
      const _self = this;
      let code = button.code || "";
      switch (code) {
        // 提交环节
        case "submitTask":
          _self.submitTask(selection);
          break;
        // 删除环节
        case "deleteTask":
          _self.deleteTask(selection);
          break;
      }
    },
    submitTask: function () {
      uni.showToast({
        title: "提交环节功能待实现！",
      });
    },
    deleteTask: function () {
      uni.showToast({
        title: "删除环节功能待实现！",
      });
    },
  },
};
</script>

<style></style>
