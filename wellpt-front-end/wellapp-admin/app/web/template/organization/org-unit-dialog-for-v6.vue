<template>
  <OrgSelect
    v-if="orgOptions"
    v-model="orgOptions.value"
    v-bind="orgOptions"
    ref="orgSelect"
    :orgType="['MyOrg', 'MyLeader', 'PublicGroup']"
    @change="onChange"
  />
</template>

<script>
import OrgSelect from '@admin/app/web/lib/org-select.vue';
export default {
  components: {
    OrgSelect
  },
  data() {
    return {
      orgOptions: null
    };
  },
  created() {
    let isDesignMode = !!this._provided.designMode;
    if (isDesignMode) {
      return;
    }

    let systemId = sessionStorage.getItem('system_id');
    if (systemId && !$axios.defaults.headers.common['system_id']) {
      window.SYSTEM_ID = systemId;
      $axios.defaults.headers.common['system_id'] = systemId;
    }
    Vue && (Vue.prototype._$SYSTEM_ID = systemId);

    if (!window.UnitTree) {
      this.createUniTree();
    }
    if (!window.appModal) {
      this.createAppModal();
    }
  },
  methods: {
    createUniTree() {
      const _this = this;
      _this.selectedData = [];
      window.UnitTree = {
        init(unitTreeOptions) {
          console.log('unitTreeOptions', unitTreeOptions);
          let idValues = [];
          let values = unitTreeOptions.data || [];
          values.forEach(item => {
            idValues.push(item.id);
          });
          _this.orgOptions = {
            value: idValues.join(';'),
            valueField: unitTreeOptions.valueField,
            labelField: unitTreeOptions.labelField,
            title: unitTreeOptions.title,
            type: unitTreeOptions.type,
            multiSelect: unitTreeOptions.multiple,
            isPathValue: unitTreeOptions.valueFormat != 'justId',
            selectTypes: unitTreeOptions.selectTypes,
            separator: unitTreeOptions.separator
          };
          if (unitTreeOptions.orgVersionId) {
            _this.orgOptions.orgVersionId = unitTreeOptions.orgVersionId;
          }
          _this.openOrgSelect();
        },
        getSelectedData() {
          let okBtn = document.querySelector('.ant-modal-footer .ant-btn-primary');
          okBtn.click();
          return _this.selectedData;
        }
      };
    },
    createAppModal() {
      window.appModal = {
        showSimpleLoading() {},
        hideSimpleLoading() {}
      };
    },
    openOrgSelect() {
      this.$nextTick(() => {
        this.$refs.orgSelect.openModal();
        this.orgOptions.value && this.$refs.orgSelect.setValue(this.orgOptions.value.split(';'));
        setTimeout(() => {
          let antModal = document.querySelector('.ant-modal');
          let antModalFooter = document.querySelector('.ant-modal-footer');
          antModal.style.width = '100%';
          antModal.style.height = '100%';
          antModal.style.top = '-53px';
          antModalFooter.style.display = 'none';
        }, 1);
      });
    },
    onChange({ value, label, nodes }) {
      const _this = this;
      nodes.forEach(node => {
        _this.selectedData.push({
          id: node.key,
          name: node.title,
          idPath: node.keyPath,
          namePath: node.titlePath,
          orgVersionId: node.version
        });
      });
    }
  }
};
</script>

<style></style>
