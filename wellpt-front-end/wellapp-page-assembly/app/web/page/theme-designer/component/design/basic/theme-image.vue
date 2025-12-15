<template>
  <div class="theme-style-panel">
    <a-page-header title="图片">
      <div slot="subTitle">平台图片资源</div>
      <a-card title="图片库" class="image-card-panel">
        <a-skeleton active :loading="loading"></a-skeleton>
        <div class="image-collection">
          <template v-for="(img, i) in images">
            <div class="image-wrapper">
              <img :src="img" />
              <label :title="img | urlFormat">{{ img | urlFormat }}</label>
            </div>
          </template>
        </div>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ThemeImage',
  props: { config: Object },
  components: {},
  computed: {},
  filters: {
    urlFormat(v) {
      if (v.startsWith('/static')) {
        return v.split('/static')[1];
      }
      return v;
    }
  },
  data() {
    return { images: [], loading: true };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchImages();
  },
  mounted() {
    this.$emit('select', this.config);
  },
  methods: {
    fetchImages() {
      $axios.get('/web/resource/getProjectImagFolders', {}).then(({ data }) => {
        console.log(data);
        let cascadeGetImag = item => {
          if (item.data && item.data.__imgs && item.data.__imgs.length > 0) {
            for (let i of item.data.__imgs) {
              let url = i.id;
              if (i.folderType == 2) {
                // 项目工程里的图片文件
                url = '/static/images' + url.split('\\').join('/');
              } else {
                url = '/proxy-repository/repository/file/mongo/downloadBody/' + url;
              }
              this.images.push(url);
            }
          }
          if (item.children && item.children.length > 0) {
            for (let child of item.children) {
              cascadeGetImag.call(this, child);
            }
          }
        };
        for (let item of data) {
          cascadeGetImag.call(this, item);
        }
        this.loading = false;
      });
    }
  }
};
</script>
