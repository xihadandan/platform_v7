<template>
  <view>
    <pre>{{ jsonString }}</pre>
  </view>
</template>

<script>
export default {
  data() {
    return { jsonString: "" };
  },
  onLoad: function (options) {},
  onShow: function () {},
  created() {
    let jsModules = require.context("/src/development", true, /\w+\.js$/);

    let map = {};
    jsModules.keys().forEach(function (fileName) {
      let js = fileName
        .split("/")
        .pop()
        .replace(/\.\w+$/, "");
      map[js] = jsModules(fileName).default.prototype.META;
    });
    this.jsonString = JSON.stringify(map, null, 2);
  },
  methods: {},
};
</script>
