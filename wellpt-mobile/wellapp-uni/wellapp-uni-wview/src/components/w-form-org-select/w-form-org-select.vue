<template>
  <uni-forms-item
    v-if="!hidden"
    :label="itemLabel"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :class="widgetClass"
  >
    <w-org-select
      :orgUuid="widget.configuration.orgUuid"
      :isPathValue="widget.configuration.isPathValue"
      :readonly="readonly"
      :textonly="displayAsLabel"
      :displayStyle="displayAsLabel ? 'Label' : widget.configuration.inputDisplayStyle"
      :separator="widget.configuration.separator"
      :title="widget.configuration.modalTitle"
      :multiSelect="widget.configuration.multiSelect"
      :titleDisplay="widget.configuration.choosenTitleDisplay"
      :checkableTypes="widget.configuration.checkableTypes"
      :orgType="enableOrgSelectTypes"
      v-model="formData[widget.configuration.code]"
      @change="onChange"
      ref="orgSelect"
    />
  </uni-forms-item>
</template>
<style lang="scss"></style>
<script>
import wOrgSelect from "../w-org-select/w-org-select.vue";
import formElement from "../w-dyform/form-element.mixin";

export default {
  mixins: [formElement],
  props: {},
  components: { wOrgSelect },
  computed: {
    enableOrgSelectTypes() {
      let opt = [];
      for (let i = 0, len = this.widget.configuration.orgSelectTypes.length; i < len; i++) {
        if (this.widget.configuration.orgSelectTypes[i].enable) {
          opt.push(this.widget.configuration.orgSelectTypes[i]);
        }
      }
      return opt;
    },
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onChange({ value, label, nodes }) {
      console.log("组织弹出框选择变更：", arguments);
      if (this.widget.configuration.displayValueField) {
        // 设置关联显示值字段
        this.form.setFieldValue(this.widget.configuration.displayValueField, label);
      }
      this.emitChange();
    },
  },
};
</script>
