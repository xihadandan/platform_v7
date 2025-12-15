<template>
  <div>
    <a-form-model-item label="示例：">
      <template v-if="cellRenderName">
        {{ text }}
      </template>
    </a-form-model-item>
  </div>
</template>

<script>
import moment from 'moment';
export default {
  name: 'dateRendererMock',
  props: {
    cellRenderName: {
      type: String,
      default: ''
    },
    options: {
      type: Object
    }
  },
  data() {
    const defaultPattern = 'YYYY-MM-DD HH:mm:ss';
    const text = moment().format(defaultPattern);
    return {
      defaultPattern,
      text
    };
  },
  watch: {
    options: {
      deep: true,
      immediate: true,
      handler(options) {}
    }
  }
};
</script>
