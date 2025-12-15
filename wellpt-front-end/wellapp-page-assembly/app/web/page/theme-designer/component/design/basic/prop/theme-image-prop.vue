<template>
  <a-upload
    :multiple="true"
    action="/proxy-repository/repository/file/mongo/saveFilesAndPushToFolder?folderID=pictureL-pict-pict-pict-pictureLibpi"
    list-type="picture"
    :default-file-list="fileList"
    @change="onFileChange"
  >
    <a-button>
      <a-icon type="upload" />
      图片上传
    </a-button>
  </a-upload>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ThemeImageProp',
  props: { item: Object },
  components: {},
  computed: {},
  data() {
    return { fileList: [...(this.item.images || [])] };
  },
  beforeCreate() {},
  created() {
    if (this.item.images == undefined) {
      this.$set(this.item, 'images', []);
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onFileChange: function (info) {
      if (info.file.status === 'done') {
        console.log(info);
        var fileInfo = info.file.response.data[0];
        this.item.images.push({
          uid: fileInfo.fileID,
          name: info.file.name,
          url: '/proxy-repository/repository/file/mongo/download?preview=true&fileID=' + fileInfo.fileID
        });
      } else if (info.file.status == 'removed') {
        // TODO: 是否要删除mongod库内的图片
        for (let i = 0, len = this.item.images.length; i < len; i++) {
          if (this.item.images[i].uid === info.file.uid) {
            this.item.images.splice(i, 1);
            break;
          }
        }
      }
    }
  }
};
</script>
