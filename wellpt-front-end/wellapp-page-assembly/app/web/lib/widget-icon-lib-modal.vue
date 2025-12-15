<template>
  <Modal
    :title="title"
    dialogClass="pt-modal widget-icon-lib-modal"
    :mask="mask"
    :width="640"
    :bodyStyle="{ height: '560px' }"
    :maxHeight="560"
    :zIndex="zIndex"
    :container="container"
  >
    <span class="modal-icon-lib-trigger" :title="title">
      <slot>
        <a-badge>
          <a-icon
            v-if="!required && mValue"
            @click.stop="mValue = undefined"
            slot="count"
            type="close-circle"
            theme="filled"
            class="close-icon"
            :title="deleteTitle"
          />
          <a-avatar
            v-if="!mValue"
            shape="square"
            style="background-color: #ffffff; cursor: pointer; border: 1px #cccccc dashed; color: #cccccc"
            :size="32"
          >
            <Icon slot="icon" type="iconfont icon-ptkj-jiahao"></Icon>
          </a-avatar>
          <a-avatar
            v-else-if="onlyIconClass || (valueJson && !valueJson.showBackground)"
            shape="square"
            style="background-color: #ffffff; color: var(--w-text-color-dark)"
            :size="32"
          >
            <Icon slot="icon" :type="mValue" style="font-size: 20px"></Icon>
          </a-avatar>
          <Icon v-else :type="mValue" style="font-size: 20px" :size="32"></Icon>
        </a-badge>
      </slot>
    </span>
    <template slot="content">
      <WidgetIconLib v-model="mValue" :onlyIconClass="onlyIconClass" :required="required" />
    </template>
  </Modal>
</template>
<script type="text/babel">
import Modal from './modal.vue';
import WidgetIconLib from '@pageAssembly/app/web/widget/widget-icon-lib/widget-icon-lib.vue';
export default {
  name: 'WidgetIconLibModal',
  props: {
    value: String,
    required: {
      //图标必填
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '选择图标'
    },
    deleteTitle: {
      type: String,
      default: '删除图标'
    },
    mask: {
      type: Boolean,
      default: true
    },
    onlyIconClass: {
      type: Boolean,
      default: false
    },
    container: Function, // 返回一个HTMLElement元素
    zIndex: Number
  },
  data() {
    return {
      mValue: this.value
    };
  },
  watch: {
    mValue: {
      handler(v, o) {
        if (v !== o) {
          this.emitChange();
        }
      }
    },
    value(v) {
      if (v != this.mValue) {
        this.mValue = v;
      }
    }
  },
  beforeCreate() {},
  components: { Modal, WidgetIconLib },
  computed: {
    valueJson() {
      if (this.isValidJSON) {
        return JSON.parse(this.mValue);
      }
      return undefined;
    },
    isValidJSON() {
      try {
        if (!this.mValue) {
          return false;
        }
        JSON.parse(this.mValue);
        return true;
      } catch (e) {
        return false;
      }
    }
  },
  created() {},
  methods: {
    emitChange() {
      this.$emit('input', this.mValue);
      this.$emit('change', this.mValue);
      // 兼容旧API
      this.$emit('iconSelected', this.mValue);
      // console.log(this.mValue);
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
<style lang="less">
.modal-icon-lib-trigger {
  > .ant-badge {
    > .ant-btn {
      line-height: normal;
    }

    cursor: pointer;
    > .close-icon {
      color: #f5222d;
      top: 3px;
      opacity: 0;
    }
    &:hover {
      > .close-icon {
        opacity: 1;
      }
    }
  }
  > .ant-btn {
    line-height: normal;
  }
}
</style>
