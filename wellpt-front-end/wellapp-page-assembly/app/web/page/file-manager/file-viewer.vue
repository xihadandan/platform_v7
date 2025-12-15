<template>
  <HtmlWrapper :title="title">
    <template v-if="file && isDyform && !dyformComponentLoading">
      <a-layout class="widget-dyform-setting">
        <a-layout-header class="widget-dyform-setting-head flex" ref="wHeader">
          <h1 class="f_g_1 w-ellipsis">
            {{ title }}
          </h1>
        </a-layout-header>
        <a-layout-content :class="['widget-dyform-setting-content top']" ref="wContent">
          <Scroll :style="{ height: 'calc(100vh - 100px)' }">
            <div v-if="dyformComponentLoading" class="spin-center">
              <a-spin />
            </div>
            <WidgetDyform
              :displayState="'label'"
              :formUuid="file.dataDefUuid"
              :dataUuid="file.dataUuid"
              :dyformStyle="{
                padding: 'var(--w-padding-md)'
              }"
            />
          </Scroll>
        </a-layout-content>
      </a-layout>
    </template>
  </HtmlWrapper>
</template>

<script>
import FileManager from '@pageAssembly/app/web/widget/widget-file-manager/FileManager.js';
export default {
  name: 'FileViewer',
  data() {
    let file = this.fileData;
    return {
      fileManager: EASY_ENV_IS_BROWSER ? new FileManager(this, '_self') : null,
      isDyform: false,
      dyformComponentLoading: true,
      file
    };
  },
  computed: {
    title() {
      return (this.file && this.file.fileName) || this.$t('FileViewer.viewFile', '文件查看');
    }
  },
  beforeCreate() {
    if (EASY_ENV_IS_BROWSER) {
      import('@dyform/app/web/framework/vue/install').then(m => {
        this.dyformComponentLoading = false;
      });
    }
  },
  beforeMount() {
    if (this.fileData.fileUuid) {
      this.loadFile();
    }
  },
  methods: {
    loadFile() {
      $axios.get('/proxy/api/dms/file/get?uuid=' + this.fileData.fileUuid).then(({ data: result }) => {
        this.file = result.data;
        if (this.file) {
          if (this.fileManager.isDyform(this.file)) {
            this.isDyform = true;
          } else {
            this.fileManager.viewFile(this.file);
          }
        }
      });
    }
  }
};
</script>

<style></style>
