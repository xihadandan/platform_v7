<template>
  <a-form-model-item
    :style="itemStyle"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="widgetClass"
  >
    <a-avatar
      v-if="designMode"
      shape="square"
      style="background-color: #ffffff; cursor: pointer; border: 1px #cccccc dashed; color: #cccccc"
      :size="32"
    >
      <Icon slot="icon" type="iconfont icon-ptkj-jiahao"></Icon>
    </a-avatar>
    <template v-else>
      <a-avatar :style="iconStyle" :size="32" v-if="!editable">
        <Icon slot="icon" :type="iconClass" />
      </a-avatar>
      <WidgetIconLibModal
        :title="itemTitle"
        v-else
        v-model="formData[widget.configuration.code]"
        :zIndex="1000"
        :onlyIconClass="onlyIconClass"
      >
        <a-badge class="icon-set-badge">
          <a-icon
            v-if="allowClear && formData[widget.configuration.code]"
            @click.stop="formData[widget.configuration.code] = undefined"
            slot="count"
            type="close-circle"
            theme="filled"
            class="close-icon"
            :deleteTitle="$t('WidgetIconLib.delete', '删除')"
          />
          <a-avatar
            v-if="!iconClass"
            shape="square"
            style="background-color: #ffffff; cursor: pointer; border: 1px #cccccc dashed; color: #cccccc"
            :size="32"
          >
            <Icon slot="icon" type="iconfont icon-ptkj-jiahao"></Icon>
          </a-avatar>
          <a-avatar v-else :style="iconStyle" :size="32">
            <Icon slot="icon" :type="iconClass" style="font-size: 20px" />
          </a-avatar>
        </a-badge>
      </WidgetIconLibModal>
    </template>
  </a-form-model-item>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formMixin from '../mixin/form-common.mixin';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
export default {
  extends: FormElement,
  name: 'WidgetFormSetIcon',
  mixins: [widgetMixin, formMixin],
  components: {
    WidgetIconLibModal
  },
  data() {
    return {
      onlyIconClass: this.widget.configuration.onlyIconClass,
      allowClear: this.widget.configuration.allowClear
    };
  },
  computed: {
    itemTitle() {
      return this.$t(this.widget.id, this.widget.configuration.name);
    },
    iconClass() {
      if (this.isValidJSON) {
        const config = JSON.parse(this.formData[this.widget.configuration.code]);
        return config.iconClass;
      } else if (this.formData[this.widget.configuration.code] && this.formData[this.widget.configuration.code].indexOf('cssStyle') > -1) {
        return undefined;
      }

      return this.formData[this.widget.configuration.code];
    },
    iconStyle() {
      let style = { borderRadius: '20%', backgroundColor: '#ffffff', color: 'var(--w-text-color-dark)' };
      const cssStyle = {};
      if (this.isValidJSON) {
        const config = JSON.parse(this.formData[this.widget.configuration.code]);
        if (config.iconClass) {
          Object.assign(cssStyle, config.cssStyle);
        }
      }
      if (this.iconClass) {
        return Object.assign(style, cssStyle);
      }
      return Object.assign(style, { backgroundColor: '#f7f7f7' });
    },
    isValidJSON() {
      try {
        if (!this.formData[this.widget.configuration.code]) {
          return false;
        }
        JSON.parse(this.formData[this.widget.configuration.code]);
        return true;
      } catch (e) {
        return false;
      }
    }
  },
  mounted() {},
  methods: {
    setValue(val) {
      this.$set(this.formData, this.widget.configuration.code, val);
      this.emitChange();
    },
    displayValue() {
      return this.formData[this.widget.configuration.code];
    }
  }
};
</script>
<style lang="less" scoped>
.icon-set-badge {
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
</style>
