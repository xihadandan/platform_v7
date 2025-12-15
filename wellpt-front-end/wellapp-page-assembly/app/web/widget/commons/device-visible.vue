<template>
  <div v-if="designer.isUniAppDesign === false">
    <a-form-model-item>
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <span slot="title">可指定至少一项设备进行显示</span>

          <label>
            <span>显示设备</span>
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>

      <a-checkbox-group
        v-if="widget.configuration.deviceVisible != undefined"
        v-model="widget.configuration.deviceVisible"
        :options="ckOptions"
      />
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import { debounce } from 'lodash';

export default {
  name: 'DeviceVisible',
  __ANT_NEW_FORM_ITEM: true,
  inject: ['designer'],
  props: {
    widget: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {
    ckOptions() {
      return [
        {
          label: 'PC 端',
          value: 'pc',
          disabled: this.widget.configuration.deviceVisible.length == 1 && this.widget.configuration.deviceVisible.includes('pc')
        },
        {
          label: '移动端',
          value: 'mobile',
          disabled: this.widget.configuration.deviceVisible.length == 1 && this.widget.configuration.deviceVisible.includes('mobile')
        }
      ];
    }
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty('deviceVisible')) {
      this.$set(this.widget.configuration, 'deviceVisible', ['pc', 'mobile']);
    }
  },
  methods: {},
  mounted() {}
};
</script>
