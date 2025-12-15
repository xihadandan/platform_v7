<template>
  <view :style="theme" class="dms-dyform-view">
    <w-dms-dyform-document-view
      v-if="!documentUniJsModule"
      :dmsId="dmsId"
      :configuration="configuration"
      :item="item"
      :extraParams="extraParams"
    ></w-dms-dyform-document-view>
    <view v-else>
      <!-- #ifdef H5 || APP-PLUS -->
      <component
        :is="documentUniJsModule"
        :dmsId="dmsId"
        :configuration="configuration"
        :item="item"
        :extraParams="extraParams"
      />
      <!-- #endif -->
      <!-- #ifdef MP -->
      <!-- #endif -->
    </view>
  </view>
</template>

<script>
import developComps from "@develop/developComps.js";

export default {
  mixins: [developComps],
  data() {
    return {
      dmsId: "",
      configuration: {},
      item: {},
      extraParams: {},
      documentUniJsModule: null,
    };
  },
  components: {
    /** INJECT COMPONENTS */
  },
  methods: {},
  onLoad(options) {
    const _self = this;
    let dmsId = _self.getPageParameter("dmsId");
    let configuration = _self.getPageParameter("configuration") || {};
    let item = _self.getPageParameter("item");
    let extraParams = _self.getPageParameter("extraParams");
    let store = configuration.store || {};
    let document = configuration.document || {};
    let documentUniJsModule = document.uniJsModule || "";

    // 数据项为空时，取配置的表单
    if (item == null) {
      item = {
        formUuid: store.formUuid,
        mFormUuid: store.mFormUuid,
        dataUuid: "",
      };
    } else if (item.formUuid == store.formUuid) {
      item.mFormUuid = store.mFormUuid;
    }

    _self.dmsId = dmsId;
    _self.configuration = configuration;
    _self.item = item;
    _self.extraParams = extraParams;
    _self.documentUniJsModule = documentUniJsModule;
  },
};
</script>

<style lang="scss" scoped>
.dms-dyform-view {
  width: 100%;
  height: 100%;
}
</style>
