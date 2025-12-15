<template>
  <dyform-field-base class="w-serial-number" :dyformField="dyformField">
    <template slot="editable-input">
      <uni-easyinput
        v-if="isEditableSerialNumber"
        v-model="formData[fieldDefinition.name]"
        type="text"
        placeholder="请选择"
      />
      <label v-else>{{ formData[fieldDefinition.name] }}</label>
    </template>
  </dyform-field-base>
</template>

<script>
import dyformFieldBase from "./field-base.vue";
const _ = require("lodash");
export default {
  components: { dyformFieldBase },
  props: {
    dyformField: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      items: [],
      fieldDefinition: this.dyformField.getDefinition(),
      formData: this.dyformField.getFormData(),
    };
  },
  created() {
    var _self = this;
    var value = _self.dyformField.getValue();
    if (_.isEmpty(_.trim(value))) {
      if (!_self.isEditableSerialNumber) {
        _self.generateSerialNumber();
      }
    }
  },
  methods: {
    generateSerialNumber: function () {
      var _self = this;
      var id = _self.fieldDefinition.designatedId || _self.fieldDefinition.serialNumberDefinitionId;
      var isOccupied = _self.fieldDefinition.isSaveDb == "0";
      var formUuid = _self.dyformField.getFormScope().getDyform().getFormUuid();
      var dataUuid = _self.dyformField.getFormScope().getDyform().getDataUuid();
      var fieldName = _self.fieldDefinition.name;
      var serialNumber = "";
      if (_.isEmpty(_.trim(id))) {
        uni.showToast({ title: "流水号ID为空" });
        return "";
      }
      var service = "serialNumberService.generateSerialNumber";
      if (_self.fieldDefinition.serialNumberDefinitionId) {
        service = "snSerialNumberFacadeService.generateSerialNumber";
      }
      uni.request({
        service: service,
        data: [
          {
            serialNumberId: id,
            occupied: isOccupied,
            formUuid: formUuid,
            dataUuid: dataUuid,
            formField: fieldName,
            renderParams: {},
          },
        ],
        success: function (result) {
          serialNumber = result.data.data;
          if (!_.isEmpty(_.trim(serialNumber))) {
            if (serialNumber.serialNo) {
              _self.dyformField.setValue(serialNumber.serialNo);
            } else {
              _self.dyformField.setValue(serialNumber);
            }
          }
        },
      });
    },
  },
  computed: {
    isRequired: function () {
      return this.dyformField.isRequired();
    },
    // 是否可编辑流水号
    isEditableSerialNumber: function () {
      return this.fieldDefinition.inputMode == "7";
    },
  },
};
</script>

<style scoped>
.w-serial-number {
}
.uni-list-cell {
  justify-content: flex-start;
}
.textarea-label {
  margin-left: 30px;
}
</style>
