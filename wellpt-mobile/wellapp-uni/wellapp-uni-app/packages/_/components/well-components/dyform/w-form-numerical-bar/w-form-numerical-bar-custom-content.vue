<template>
  <view>
    <rich-text :nodes="contentNode"></rich-text>
  </view>
</template>
<script type="text/babel">
import { each, isObject } from "lodash";
export default {
  props: ["content", "percent", "status", "form", "formData"],
  data: function () {
    return {};
  },
  computed: {
    contentNode() {
      const regex = new RegExp(/\{\{.*?\}\}/g);
      let content = this.content;
      let contentArr = this.content.match(regex);
      // 仅对数值进行替换，样式跟随配置
      each(contentArr, (item) => {
        let params = item.replace("{{", "").replace("}}", "");
        params = params.split(".");
        let data = "";
        each(params, (citem, index) => {
          if (index == 0) {
            data = this[citem] + "";
          } else if (isObject(data)) {
            data = data[citem];
          }
        });
        content = content.replace(item, data || "");
      });
      return content;
    },
  },
  mounted() {},
  methods: {},
};
</script>
