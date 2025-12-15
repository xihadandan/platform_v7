<template>
  <div>
    <a-form-model-item label="启用标题">
      <a-switch v-model="configuration.enabledTitle" />
    </a-form-model-item>
    <template v-if="configuration.enabledTitle">
      <!-- <a-button type="link" @click="handleOpen">
        <Icon type="pticon iconfont icon-ptkj-shezhi" />
      </a-button> -->
      <a-form-model-item label="图标">
        <widget-icon-lib-modal v-model="configuration.titleIcon" />
      </a-form-model-item>
    </template>
  </div>
</template>

<script>
import WidgetIconLibModal from '../../../lib/widget-icon-lib-modal.vue';

export default {
  name: 'TitleButton',
  props: {
    configuration: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WidgetIconLibModal
  },
  methods: {
    handleOpen() {}
  }
};
</script>
