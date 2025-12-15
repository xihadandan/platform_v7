<template>
  <div>
    <a-form-model-item label="加减按钮样式" class="item-lh"></a-form-model-item>
    <a-form-model-item>
      <a-radio-group v-model="widget.configuration.stepBtnType" style="width: 100%">
        <a-radio value="LR">
          <span>左右&nbsp;</span>
          <span class="input-number-LR">
            <div class="minus"><a-icon type="minus" /></div>
            <a-input-number style="width: 60px" disabled />
            <div class="plus"><a-icon type="plus" /></div>
          </span>
        </a-radio>
        <a-radio value="TB">
          <span>上下&nbsp;</span>
          <span class="input-number-TB">
            <a-input-number style="width: 60px" disabled />
          </span>
        </a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="加减步长">
      <a-input-number v-model="widget.configuration.step" :min="0.1" :precision="1" />
    </a-form-model-item>
  </div>
</template>
<style lang="less">
.designer-configuration-form {
  .input-number-LR {
    position: relative;
    display: inline-block;
    .ant-input-number {
      padding-left: 12px;
      padding-right: 12px;
    }
    .ant-input-number-handler-wrap {
      display: none;
    }
    .minus,
    .plus {
      position: absolute;
      height: 30px;
      line-height: 28px;
      width: 15px;
      text-align: center;
      font-size: 8px;
      top: 1px;
      z-index: 1;
      i {
        color: rgba(0, 0, 0, 0.45);
        font-size: 10px;
      }

      &:hover {
        i {
          color: #1890ff;
        }
      }
    }
    .minus {
      left: 0;
      border-right: 1px solid #e5e5e5;
    }
    .plus {
      right: 0;
      border-left: 1px solid #e5e5e5;
    }
    &.small {
      .minus,
      .plus {
        height: 22px;
        line-height: 20px;
      }
    }
    &.large {
      .minus,
      .plus {
        height: 38px;
        line-height: 36px;
      }
    }
  }
  .input-number-TB {
    .ant-input-number-handler-wrap {
      opacity: 1 !important;
      display: block !important;
    }
  }

  .input-number-LR,
  .input-number-TB {
    .ant-input-number-disabled {
      color: #ffffff;
      background-color: #ffffff;
    }
  }
}
</style>
<script type="text/babel">
export default {
  name: 'numberEditSetting',
  props: {
    widget: Object,
    designer: Object,
    value: String
  },
  data() {
    return {};
  },
  beforeCreate() {},
  components: {},
  watch: {},
  computed: {},
  created() {},
  methods: {},
  mounted() {}
};
</script>
