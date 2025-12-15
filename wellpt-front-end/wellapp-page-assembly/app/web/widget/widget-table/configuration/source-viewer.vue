<template>
  <div class="source-viewer">
    <!-- 只有非受控模式才显示下拉 -->
    <div v-if="rawCode" style="margin-top: 20px">
      <pre class="code-block"> {{ rawCode }}</pre>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SourceViewer',
  props: {
    sources: {
      type: Object
    },
    currentFile: {
      type: String,
      default: null
    },
    options: Object
  },
  data() {
    return {
      rawCode: null
    };
  },
  watch: {
    options: {
      deep: true,
      handler(val) {
        if (this.currentFile === 'vueTemplateDataRender' && val.templateFrom == 'projectCode') {
          if (val.templateName) {
            if (window.Vue.options.components[val.templateName]) {
              const mate = window.Vue.options.components[val.templateName].META;
              window.__wtemplateSourceCtxMap[mate.dirPath](mate.filePath).then(module => {
                this.rawCode = module.default;
              });
            }
          } else {
            this.rawCode = null;
          }
        }
      }
    },
    currentFile: {
      immediate: true,
      handler(val) {
        if (val) {
          if (val === 'vueTemplateDataRender') {
            this.rawCode = null;
          } else {
            if (this.sources[val]) {
              this.rawCode = this.sources[val];
            } else {
              this.rawCode = null;
            }
          }
        }
      }
    }
  },
  methods: {
    onSelectChange(val) {
      this.$emit('update:currentFile', val);
    }
  }
};
</script>

<style scoped>
.code-block {
  background: #f4f4f4;
  padding: 10px;
  border-radius: 6px;
  white-space: pre-wrap;
  overflow-x: auto;
}
</style>
