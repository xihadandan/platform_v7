<template>
  <a-select
    show-search
    :filter-option="filterOption"
    :style="{ width: width }"
    :size="size"
    allowClear
    :showSearch="true"
    :mode="multiSelect ? 'multiple' : 'default'"
    v-model="selectValue"
    @change="onChange"
    :options="roleOptions"
  >
    <a-spin v-if="fetching" slot="notFoundContent" size="small" />
  </a-select>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'RoleSelect',
  mixins: [],
  props: {
    value: {
      type: [Object, Array]
    },
    width: {
      type: String,
      default: '100%'
    },
    size: {
      type: String,
      default: 'default'
    },
    multiSelect: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      fetching: false,
      roleOptions: [],
      roles: {},
      selectValue: this.value === undefined || (!Array.isArray(this.value) && Object.keys(this.value).length == 0) ? undefined : this.value
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    getRoleTree() {
      this.fetching = true;
      $axios.get(`/proxy/api/security/role/getRoleTree`, {}).then(({ data }) => {
        if (data.code == 0) {
          this.fetching = false;
          // 处理后端返回的结果树
          let option = [];
          let cascadeAppendChild = children => {
            for (let i = 0, len = children.length; i < len; i++) {
              let child = children[i];
              if (child.type == 'R') {
                option.push({
                  label: child.name,
                  value: child.data
                });
              }
              if (child.children && child.children.length > 0) {
                cascadeAppendChild(child.children);
              }
            }
          };
          cascadeAppendChild(data.data.children);

          this.roleOptions = option;
        }
      });
    },

    onChange(value, option) {
      this.$emit('input', value);
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    }
  },
  beforeMount() {
    this.getRoleTree();
  },
  mounted() {}
};
</script>
