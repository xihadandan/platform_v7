<template>
  <!-- 被联动字段选项 -->
  <a-select
    class="wcfg-select-dict"
    placeholder="请选择"
    :options="inputFieldOptions"
    :style="{ width: '100%' }"
    v-model="widget.configuration.relateKey"
    @change="changeRelateKey"
    :getPopupContainer="getPopupContainerByPs()"
  />
</template>

<script>
import formConfigureMixin from '../../../mixin/formConfigure.mixin';
export default {
  name: 'SelectRelateField',
  inject: ['designer'],
  mixins: [formConfigureMixin],
  props: {
    widget: Object
  },
  data() {
    return {};
  },
  methods: {
    // 改变联动项
    changeRelateKey(val) {
      const relate = this.inputFieldOptions.find(item => {
        return item.value === val;
      });
      this.$set(this.widget.configuration, 'relateKeyLabel', relate.label);
    }
  }
};
</script>
