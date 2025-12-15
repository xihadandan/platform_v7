<template>
  <dyform-field-base class="w-dialog" :dyformField="dyformField">
    <template slot="editable-input">
      <view @click="openDialog">
        <uni-easyinput
          v-model="formData[fieldDefinition.name]"
          :disabled="true"
          type="text"
          placeholder="请选择"
          @focus="openDialog"
        />
      </view>
    </template>
    <template slot="displayAsLabel">
      <uni-forms-item :label="fieldLabel" :required="isRequired">
        <label>{{ formData[fieldDefinition.name] }}</label>
      </uni-forms-item>
    </template>
    <template slot="disabled">
      <uni-forms-item :label="fieldLabel" :required="isRequired">
        <label>{{ formData[fieldDefinition.name] }}</label>
      </uni-forms-item>
    </template>
  </dyform-field-base>
</template>

<script>
const _ = require("lodash");
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
    openDialog: function () {
      var _self = this;
      _self.setPageParameter("options", {
        fieldDefinition: _self.fieldDefinition,
        ok: function (selection) {
          // console.log(JSON.stringify(selection));
          // console.log(JSON.stringify(_self.fieldDefinition));
          _self.setMappingFieldValue(selection);
        },
      });
      uni.navigateTo({
        url: "/packages/_/pages/dyform/popup_dialog",
      });
    },
    setMappingFieldValue: function (selection) {
      var _self = this;
      var fieldDefinition = _self.fieldDefinition;
      var relationDataDefiantion = fieldDefinition.relationDataDefiantion;
      if (_.isEmpty(relationDataDefiantion)) {
        return;
      }
      var relationFieldMappingstrArray = relationDataDefiantion.split("|");
      for (var i = 0; i < relationFieldMappingstrArray.length; i++) {
        var relationColumn = JSON.parse(relationFieldMappingstrArray[i]);
        if (relationColumn.formField && relationColumn.sqlField) {
          var values = [];
          _.each(selection, function (data) {
            values.push(data[relationColumn.sqlField]);
          });
          // _self.formData[relationColumn.formField] = values.join(";");
          _self.$set(_self.formData, relationColumn.formField, values.join(";"));
        }
      }
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
.field {
}
.uni-list-cell {
  justify-content: flex-start;
}
.textarea-label {
  margin-left: 30px;
}
</style>
