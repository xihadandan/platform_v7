<template>
  <a-form-model-item>
    <template slot="label">
      {{ label }}
      <a-tooltip :title="设置四个值">
        <a-checkbox v-model="borderRadiusSetFourCorner" @change="onChangeBorderRadiusSetFourCorner" />
      </a-tooltip>
    </template>
    <a-row v-if="!borderRadiusSetFourCorner">
      <a-col :span="12" v-if="!hideSilder">
        <a-slider v-model="target[field]" :min="min" :max="max" />
      </a-col>
      <a-col :span="!hideSilder ? 12 : 24">
        <a-input-number v-model="target[field]" :min="min" :max="max" style="margin-left: 12px" />
        px
      </a-col>
    </a-row>
    <div v-else>
      <div style="display: flex; justify-content: flex-end">
        <div>
          <a-tooltip :title="labelMap[0]" placement="top">
            <a-input-number v-model="target[field][0]" :min="min" :max="max" />
          </a-tooltip>
          px
        </div>
        <div style="margin-left: 8px">
          <a-tooltip :title="labelMap[1]" placement="top">
            <a-input-number v-model="target[field][1]" :min="min" :max="max" />
          </a-tooltip>
          px
        </div>
      </div>
      <div style="display: flex; justify-content: flex-end">
        <div>
          <a-tooltip :title="labelMap[3]" placement="top">
            <a-input-number v-model="target[field][3]" :min="min" :max="max" />
          </a-tooltip>
          px
        </div>
        <div style="margin-left: 8px">
          <a-tooltip :title="labelMap[2]" placement="top">
            <a-input-number v-model="target[field][2]" :min="min" :max="max" />
          </a-tooltip>
          px
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
    target: Object,
    field: {
      type: String,
      default: 'borderRadius'
    },
    label: {
      type: String,
      default: '圆角'
    },
    min: {
      type: Number,
      default: 0
    },
    max: {
      type: Number,
      default: 24
    },
    hideSilder: Boolean
  },
  components: {},
  computed: {
    labelMap() {
      if (this.field === 'borderRadius') {
        return ['左上角', '右上角', '右下角', '左下角'];
      }
      return ['上边', '右边', '下边', '左边'];
    }
  },
  data() {
    return { borderRadiusSetFourCorner: Array.isArray(this.target[this.field]) };
  },
  beforeCreate() {},
  created() {
    if (!this.target.hasOwnProperty(this.field)) {
      this.$set(this.target, this.field, undefined);
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeBorderRadiusSetFourCorner() {
      if (this.borderRadiusSetFourCorner) {
        this.$set(
          this.target,
          this.field,
          Array.from({ length: 4 }, () => {
            return this.target[this.field] || 0;
          })
        );
      } else {
        this.$set(this.target, this.field, this.target[this.field][0] || 0);
      }
    }
  }
};
</script>
