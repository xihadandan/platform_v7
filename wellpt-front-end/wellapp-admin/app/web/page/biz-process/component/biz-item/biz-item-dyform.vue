<template>
  <a-card class="preview-card" size="small" :bordered="false" bodyStyle="padding:0">
    <template slot="title">
      <span class="title">{{ formDefinition.name }}</span>
      <label v-if="formDefinition.version" style="color: var(--w-text-color-light); font-size: var(--w-font-size-base)">
        v{{ formDefinition.version }}
      </label>
    </template>
    <template slot="extra">
      <a-button v-if="existsEntityForm" @click="onClickEditDyform" size="small" type="link">
        <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
        编辑表单
      </a-button>
    </template>
    <a-empty v-if="emptyDesign" style="padding-top: 240px">
      <template #description>
        暂无设计内容
        <a-button type="link" size="small" @click="onClickEditDyform">前往配置</a-button>
      </template>
    </a-empty>
    <PerfectScrollbar v-show="!emptyDesign" style="height: calc(100vh - 120px)">
      <div style="width: 100%; height: 100%; display: block; position: fixed; z-index: 1"></div>
      <iframe
        :key="pageKey"
        id="pagePreviewIframe"
        :src="'/dyform-viewer/just-dyform?formUuid=' + formDefinition.uuid + '&iframe=1#iframe'"
        :style="{ minHeight: 'calc(100vh - 130px)', border: 'none', width: '100%' }"
      ></iframe>
    </PerfectScrollbar>
  </a-card>
</template>

<script>
export default {
  props: {
    formDefinition: Object
  },
  inject: ['pageContext'],
  data() {
    return {
      pageKey: 'biz_process_item_dyform_preview',
      emptyDesign: false
    };
  },
  mounted() {
    window.addEventListener('message', this.frameListener, false);
  },
  beforeDestroy() {
    window.removeEventListener('message', this.frameListener);
  },
  methods: {
    frameListener(event) {
      let _this = this;
      if (event.origin !== location.origin) {
        return;
      }
      if (event.data == 'dyform mounted') {
        let iframeEle = document.querySelector('#pagePreviewIframe');
        _this.emptyDesign = iframeEle.contentWindow.$app.$refs.wDyform.widgets.length == 0;

        // 配置监听方法的属性值
        // 定义一个监听器
        let observer = new MutationObserver(mutations => {
          for (let item of mutations) {
            if (item.type === 'childList') {
              const scrollHeight = iframeEle.contentWindow.document.body.scrollHeight;
              iframeEle.style.height = `${scrollHeight}px`;
              break;
            }
          }
        });

        observer.observe(iframeEle.contentWindow.document, {
          attributes: true,
          childList: true,
          subtree: true
        });
      }
    },
    onClickEditDyform() {
      let _this = this;
      this.pageContext.handleCrossTabEvent(`dyform:design:change:${_this.formDefinition.uuid}`, formDefinition => {
        // _this.formDefinition = formDefinition;
        Object.assign(_this.formDefinition, formDefinition);
        // _this.pageContext.emitEvent('loadItemFormDefinition', formDefinition);
        // 不同页面临时存储对象不一样
        let iframeEle = document.querySelector('#pagePreviewIframe');
        if (iframeEle && iframeEle.contentWindow && iframeEle.contentWindow.$app && iframeEle.contentWindow.$app.$tempStorage) {
          iframeEle.contentWindow.$app.$tempStorage.removeItem(_this.formDefinition.uuid);
        }
        _this.$tempStorage.removeItem(_this.formDefinition.uuid, () => {
          _this.refresh(true);
        });
      });
      window.open(`/dyform-designer/index?uuid=${_this.formDefinition.uuid}`, '_blank');
    },
    refresh(force) {
      if (force) {
        this.pageKey = 'biz_process_item_dyform_preview_' + new Date().getTime();
      }
    }
  }
};
</script>

<style></style>
