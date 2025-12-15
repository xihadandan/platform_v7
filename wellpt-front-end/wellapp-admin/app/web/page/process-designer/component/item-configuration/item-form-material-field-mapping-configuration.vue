<script>
import ItemFormFieldMappingConfiguration from './item-form-field-mapping-configuration.vue';
export default {
  extends: ItemFormFieldMappingConfiguration,
  computed: {
    sourceTypeOptions() {
      return [
        { label: '业务主体', value: 'entity' },
        { label: '事项源', value: 'item', disabled: !this.itemDefinition.itemDefId },
        { label: '事项源材料', value: 'itemMaterial', disabled: !this.itemDefinition.itemDefId }
      ];
    },
    targetExcludeFields() {
      const _this = this;
      let fields = [];
      if (_this.formConfig.materialNameField) {
        fields.push(_this.formConfig.materialNameField);
      }
      if (_this.formConfig.materialCodeField) {
        fields.push(_this.formConfig.materialCodeField);
      }
      if (_this.formConfig.materialRequiredField) {
        fields.push(_this.formConfig.materialRequiredField);
      }
      if (_this.formConfig.materialFileField) {
        fields.push(_this.formConfig.materialFileField);
      }
      return fields;
    }
  },
  methods: {
    loadFormFieldOptions() {
      const _this = this;

      _this.sourceTypeChange();

      let targetFormUuid = _this.formConfig.formUuid;
      _this.loadFormDefinition(targetFormUuid).then(formDefinition => {
        // 附件从表字段
        let subforms = formDefinition.subforms || {};
        for (let subformUuid in subforms) {
          let subform = subforms[subformUuid];
          if (subform.outerId == _this.formConfig.materialSubformId) {
            _this.targetFormFieldOptions = _this.getFormFieldSelectData(subform, false, _this.targetExcludeFields);
          }
        }
      });
    },
    sourceTypeChange() {
      const _this = this;
      if (_this.fieldMapping.sourceType == 'entity') {
        let processDefinition = _this.designer.getProcessDefinition();
        let entityFormUuid = processDefinition.entityConfig && processDefinition.entityConfig.formUuid;
        _this.loadFormDefinition(entityFormUuid).then(formDefinition => {
          _this.sourceFormFieldOptions = _this.getFormFieldSelectData(formDefinition, true);
        });
      } else if (_this.fieldMapping.sourceType == 'item') {
        let itemDefId = _this.itemDefinition.itemDefId;
        let url = `/proxy/api/biz/process/definition/getFormDefinitionByItemDefId/${itemDefId}`;
        _this.loadFormDefinition(itemDefId, url).then(formDefinition => {
          _this.sourceFormFieldOptions = _this.getFormFieldSelectData(formDefinition, true);
        });
      } else {
        let itemDefId = _this.itemDefinition.itemDefId;
        _this.getItemDefinition(itemDefId).then(itemDefinition => {
          // 事项源定义材料从表
          if (itemDefinition && itemDefinition.materialSubformId) {
            let url = `/proxy/api/biz/process/definition/getFormDefinitionByItemDefId/${itemDefId}`;
            _this.loadFormDefinition(itemDefId, url).then(formDefinition => {
              // 附件从表字段
              let subforms = formDefinition.subforms || {};
              for (let subformUuid in subforms) {
                let subform = subforms[subformUuid];
                if (subform.outerId == itemDefinition.materialSubformId) {
                  _this.sourceFormFieldOptions = _this.getFormFieldSelectData(subform);
                }
              }
            });
          }
        });
      }
    },
    getItemDefinition(itemDefId) {
      return $axios.get(`/proxy/api/biz/item/definition/getById?id=${itemDefId}`).then(({ data: result }) => {
        return result.data;
      });
    }
  }
};
</script>
