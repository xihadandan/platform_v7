<template>
  <view class="w-tip">
    <uni-icons type="info" v-if="widget.configuration.tipType === 'popup'" @click="onToast"></uni-icons>
    <u-alert
      v-else
      :type="widget.configuration.type"
      :title="widget.configuration.body"
      :show-icon="widget.configuration.showIcon"
      :closable="widget.configuration.closable"
    ></u-alert>
    <u-toast ref="uToast"></u-toast>
  </view>
</template>

<script>
import mixin from "../page-widget-mixin";
import "./index.scss";
export default {
  name: "w-tip",
  mixins: [mixin],
  data() {
    return {};
  },
  components: {},
  computed: {},
  created() {},
  methods: {
    onToast() {
      this.$refs.uToast.show({
        message: this.widget.configuration.body,
      });
    },
  },
  mounted() {},
};
</script>
