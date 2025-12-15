<template>
  <a-form-model-item>
    <template slot="label">
      圆角
      <a-tooltip title="设置四个角圆角">
        <a-checkbox v-model="borderRadiusSetFourCorner" @change="onChangeBorderRadiusSetFourCorner" />
      </a-tooltip>
    </template>
    <a-row v-if="!borderRadiusSetFourCorner">
      <a-col :span="12">
        <a-slider v-model="target.borderRadius" :min="0" :max="24" />
      </a-col>
      <a-col :span="12">
        <a-input-number v-model="target.borderRadius" :min="0" :max="24" style="margin-left: 12px" :allowClear="true" />
      </a-col>
    </a-row>
    <div v-else>
      <div style="display: flex; justify-content: flex-end">
        <div>
          <a-tooltip title="左上角" placement="top">
            <a-input-number v-model="target.borderRadius[0]" :min="0" :max="10" />
          </a-tooltip>
        </div>
        <div style="margin-left: 8px">
          <a-tooltip title="右上角" placement="top">
            <a-input-number v-model="target.borderRadius[1]" :min="0" :max="10" />
          </a-tooltip>
        </div>
      </div>
      <div style="display: flex; justify-content: flex-end">
        <div>
          <a-tooltip title="左下角" placement="top">
            <a-input-number v-model="target.borderRadius[3]" :min="0" :max="10" />
          </a-tooltip>
        </div>
        <div style="margin-left: 8px">
          <a-tooltip title="右下角" placement="top">
            <a-input-number v-model="target.borderRadius[2]" :min="0" :max="10" />
          </a-tooltip>
        </div>
      </div>
    </div>
  </a-form-model-item>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'BorderRadiusSet',
  props: {
    target: Object
  },
  components: {},
  computed: {},
  data() {
    return { borderRadiusSetFourCorner: Array.isArray(this.target.borderRadius) };
  },
  beforeCreate() {},
  created() {
    if (!this.target.hasOwnProperty('borderRadius')) {
      this.$set(this.target, 'borderRadius', 0);
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeBorderRadiusSetFourCorner() {
      if (this.borderRadiusSetFourCorner) {
        this.$set(
          this.target,
          'borderRadius',
          Array.from({ length: 4 }, () => {
            return this.target.borderRadius || 0;
          })
        );
      } else {
        this.$set(this.target, 'borderRadius', this.target.borderRadius[0] || 0);
      }
    }
  }
};
</script>
