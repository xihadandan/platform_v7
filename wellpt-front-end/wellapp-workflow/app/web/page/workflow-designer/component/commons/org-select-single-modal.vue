<template>
  <org-select :value="currentValue" :orgUuid="currentOrgUuid" :orgIdOptions="orgIdOptions" @change="changeOrgValue" />
</template>

<script>
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import mixins from '../mixins';

export default {
  name: 'OrgSelectSingleModal',
  mixins: [mixins],
  props: {
    value: {
      type: [String, Array]
    },
    orgUuid: {
      type: String
    },
    createItem: {
      type: Function,
      default: () => {
        return {
          value: '',
          argValue: '',
          type: '1'
        };
      }
    },
    orgIdOptions: {
      type: Object,
      default: () => {
        return {
          xxx: []
        };
      }
    }
  },
  components: {
    OrgSelect
  },
  computed: {
    currentValue() {
      let value;
      if (this.value) {
        if (typeof this.value === 'string') {
          value = this.value;
        } else if (Array.isArray(this.value) && this.value.length) {
          const valueArr = this.value.map(item => {
            return item.value || item;
          });

          value = valueArr.join(';');
        }
      }
      return value;
    },
    currentOrgUuid() {
      let uuid;
      if (this.orgUuid) {
        uuid = this.orgUuid;
      } else if (this.useOrgData) {
        uuid = this.useOrgData.uuid;
      }
      return uuid;
    }
  },
  methods: {
    changeOrgValue({ value, label, nodes }) {
      const userList = nodes.map(item => {
        let userItem = this.createItem();
        userItem.value = item.key;
        userItem.argValue = item.title;
        return userItem;
      });
      this.$emit('input', userList);
      this.$emit('change', { value, label, nodes, userList });
    }
  }
};
</script>
