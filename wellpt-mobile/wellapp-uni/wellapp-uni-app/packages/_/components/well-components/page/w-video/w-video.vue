<template>
  <view class="w-video" :class="customClassCom">
    <video :src="src" :poster="poster" controls :autoplay="true"></video>
  </view>
</template>
<style lang="scss">
.w-video {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
<script>
import { storage } from "wellapp-uni-framework";
import mixin from "../page-widget-mixin";
export default {
  mixins: [mixin],
  props: {},
  components: {},
  computed: {},
  data() {
    return { src: undefined, poster: undefined, autoplay: false };
  },
  beforeCreate() {},
  created() {
    let video = this.widget.configuration.sources[0];
    this.src = video.src;
    if (video.videoSourceType == "dbFile") {
      this.src = storage.fillAccessResourceUrl(video.dbFileSrc.split("/proxy-repository")[1]);
    }
    if (video.poster) {
      if (video.poster.startsWith("/static/")) {
        this.poster = video.poster;
      } else if (video.poster.startsWith("/proxy-repository")) {
        this.poster = storage.fillAccessResourceUrl(video.poster.split("/proxy-repository")[1]);
      }
    }
    this.autoplay = video.autoplay === true;
  },
  beforeMount() {},
  mounted() {},
  methods: {},
};
</script>
