<template>
  <!-- 业务组织和业务角色 -->
  <div>
    <a-form-model-item class="form-item-vertical" label="业务组织">
      <organization-select v-model="formData.businessType" @change="changeOrg" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="业务角色">
      <a-select
        v-model="businessRole"
        :allowClear="true"
        :options="ruleOptions"
        @change="changeRole"
        placeholder="请选择"
        :getPopupContainer="getPopupContainerByPs()"
        :dropdownClassName="getDropdownClassName()"
      />
    </a-form-model-item>
  </div>
</template>

<script>
import OrganizationSelect from './organization-select';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'BusinessTypeRole',
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    OrganizationSelect
  },
  data() {
    let businessRole = undefined;
    if (this.formData.businessRole) {
      businessRole = this.formData.businessRole;
    }
    return {
      businessRole,
      ruleOptions: []
    };
  },
  created() {
    const businessType = this.formData.businessType;
    if (businessType) {
      this.getBusinessRoles(businessType);
    }
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    changeOrg(value, option) {
      this.getBusinessRoles(value);
      this.formData.businessTypeName = option.data.props.title;
    },
    getBusinessRoles(businessType) {
      this.$axios
        .get('/api/workflow/definition/getBusinessRoles', {
          params: { businessType }
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.ruleOptions = data;
            }
          }
        });
    },
    changeRole(value, option) {
      this.formData.businessRole = value;
      if (option) {
        this.formData.businessRoleName = option.componentOptions.children[0].text.trim();
      }
    }
  }
};
</script>
