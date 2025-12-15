<template>
  <view @click.stop="() => {}" style="width: 100%">
    <template v-if="text">
      <uni-w-simple-list-upload :fileIds="files" ref="filePicker" :editable="false" :headerButtons="headerButtons">
      </uni-w-simple-list-upload>
    </template>
    <view v-else v-html="slotOption.defaultContentIfNull" />
  </view>
</template>
<style lang="less"></style>
<script type="text/babel">
import cellRenderMixin from "./cellRenderMixin";
export default {
  name: "CellDataFileRender",
  mixins: [cellRenderMixin],
  title: "数据文件渲染器",
  scope: ["pc", "mobile"],
  props: {},
  components: {},
  computed: {
    files() {
      if (this.text) {
        return this.text.split(";").map((fileID) => {
          return {
            fileID,
          };
        });
      }
      return [];
    },
  },
  data() {
    return {
      loading: false,
      headerButtons: [
        {
          buttonName: this.$t("WidgetFormFileUpload.button.onClickAllDownload", "全部下载"),
          title: this.$t("WidgetFormFileUpload.button.onClickAllDownload", "全部下载"),
          type: "primary",
          size: "default",
          block: true,
          code: "onClickAllDownload",
          btnShowType: "show",
          icon: "pticon iconfont icon-ptkj-xiazai",
        },
      ],
    };
  },
  filters: {},
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {},
};
</script>
