<template>
  <div :style="vContainerStyle">
    <video :id="vid" class="video-js"></video>
  </div>
</template>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import '@pageAssembly/app/public/js/video.js/video-js.min.css';

export default {
  name: 'WidgetVideo',
  mixins: [widgetMixin],
  data() {
    return {
      vid: this.widget.id + '_' + new Date().getTime(),
      videoOptions: {
        width: undefined,
        height: undefined,
        language: 'zh-CN'
      },
      wKey: this.widget.id
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vAspectRadioSizeOptions() {
      let configuration = this.widget.configuration,
        options = {};
      if (configuration.aspectRatio.length == 2 && configuration.aspectRatio[0] != undefined && configuration.aspectRatio[1] != undefined) {
        options.aspectRatio = `${configuration.aspectRatio[0]}:${configuration.aspectRatio[1]}`;
      }
      if (configuration.style != undefined) {
        if (configuration.style.width != undefined) {
          options.width = configuration.style.width;
        }
        if (configuration.style.height != undefined) {
          options.height = configuration.style.height;
        }
      }
      return options;
    },
    vContainerStyle() {
      let style = {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
      };
      return style;
    }
  },
  created() {},
  methods: {
    initVideoOptions() {
      let configuration = this.widget.configuration;
      this.videoOptions.autoplay = this.widget.configuration.autoplay; // 自动播放
      this.videoOptions.controls = true;
      this.videoOptions.poster = this.widget.configuration.poster;
      if (configuration.sources != undefined && configuration.sources.length > 0) {
        let sources = [];
        for (let item of this.widget.configuration.sources) {
          sources.push({
            type: item.type,
            src: item.videoSourceType == 'dbFile' ? item.dbFileSrc : item.src
          });
        }
        this.videoOptions.sources = sources;
      }
      Object.assign(this.videoOptions, this.vAspectRadioSizeOptions);
    },
    createVideoPlayer() {
      if (this.$el.querySelector(`#${this.vid}`) == null) {
        let video = document.createElement('video');
        video.setAttribute('id', this.vid);
        video.setAttribute('class', 'video-js');
        this.$el.appendChild(video);
      }

      this.initVideoOptions();
      import('@pageAssembly/app/public/js/video.js/lang/zh-CN.json').then(j => {
        this.videoOptions.languages = {
          'zh-CN': j
        };
      });
      import('@pageAssembly/app/public/js/video.js/video.min.js').then(videojs => {
        this.videoPlayer = videojs.default(this.vid, this.videoOptions);
      });
    }
  },
  beforeMount() {},
  mounted() {
    this.createVideoPlayer();
  },
  watch: {
    vAspectRadioSizeOptions: {
      deep: true,
      handler() {
        if (this.videoPlayer) {
          this.videoPlayer.dispose(); // 销毁播放器实例
          this.createVideoPlayer();
        }
      }
    }
  }
};
</script>
