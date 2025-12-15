<template>
  <div class="designer-configuration-form">
    <a-form-model
      ref="form"
      :model="widget.configuration"
      :rules="rules"
      labelAlign="left"
      :wrapper-col="{ style: { textAlign: 'right' } }"
    >
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" :isReadonly="true" />
          <a-form-model-item label="默认状态" class="item-lh">
            <a-radio-group
              size="small"
              v-model="widget.configuration.defaultDisplayState"
              button-style="solid"
              @change="onChangeDefaultDisplayState"
            >
              <a-radio-button value="edit">可编辑</a-radio-button>
              <a-radio-button value="unedit">不可编辑</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>

<style lang="less"></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';

export default {
  name: 'WidgetRefFormFieldPlaceholderConfiguration',

  mixins: [formConfigureMixin],
  props: {
    parent: Object,
    widget: Object,
    designer: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    onChangeDefaultDisplayState() {
      this.designer.emitEvent('refDyformWidgetConfigurationChange', this.widget);
    }
  },
  mounted() {}
};
</script>
