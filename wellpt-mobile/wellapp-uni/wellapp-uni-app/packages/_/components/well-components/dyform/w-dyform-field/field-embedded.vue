<template>
  <dyform-field-base class="w-embedded" :dyformField="dyformField">
    <template slot="editable">
      <uni-forms-item :label="fieldLabel">
        <button
          v-if="formData[fieldDefinition.name] != null && formData[fieldDefinition.name] != ''"
          type="default"
          @tap="viewDetail"
        >
          查看详情
        </button>
      </uni-forms-item>
    </template>
    <template slot="displayAsLabel">
      <uni-forms-item :label="fieldLabel">
        <button
          v-if="formData[fieldDefinition.name] != null && formData[fieldDefinition.name] != ''"
          type="default"
          @tap="viewDetail"
        >
          查看详情
        </button>
      </uni-forms-item>
    </template>
    <template slot="disabled">
      <uni-forms-item :label="fieldLabel">
        <button
          v-if="formData[fieldDefinition.name] != null && formData[fieldDefinition.name] != ''"
          type="default"
          @tap="viewDetail"
        >
          查看详情
        </button>
      </uni-forms-item>
    </template>
  </dyform-field-base>
</template>

<script>
import dyformFieldBase from "./field-base.vue";
import mixin from "./field-mixin";
export default {
  mixins: [mixin],
  components: { dyformFieldBase },
  data() {
    return {};
  },
  created() {},
  methods: {
    viewDetail: function () {
      var _self = this;
      var url = _self.dyformField.getValue();
      var title = _self.fieldLabel;
      uni.navigateTo({
        url: "/packages/_/pages/web-view/web-view?url=" + url + "&title=" + title,
      });
    },
  },
  computed: {
    isRequired: function () {
      return this.dyformField.isRequired();
    },
  },
};
</script>

<style scoped>
.w-embedded {
}
</style>
