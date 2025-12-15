<template>
  <div>
    <a-checkbox-group class="rights-checkbox-group" v-model="checkedValue" @change="onChange">
      <div class="list-vertical rights" v-for="(optionItem, index) in formatOptions" :key="index">
        <a-checkbox v-for="item in optionItem" :key="item.value" :value="item.value">
          {{ item.name }}
        </a-checkbox>
      </div>
    </a-checkbox-group>
  </div>
</template>

<script>
export default {
  name: 'PermissionCheckbox',
  props: {
    value: {
      type: Array,
      default: () => []
    },
    options: {
      type: Array,
      default: () => []
    },
    columns: {
      // 默认3个一列
      type: Number,
      default: 3
    }
  },
  data() {
    let checkedValue = [];
    return {
      checkedValue
    };
  },
  watch: {
    value: {
      deep: true,
      handler(value) {
        this.checkedValue = this.getCheckedValue(value);
      }
    }
  },
  computed: {
    formatOptions() {
      let options = [];
      if (this.options && this.options.length) {
        return this.options.flatMap((item, index) => (index % this.columns ? [] : [this.options.slice(index, index + this.columns)]));
      }
      return options;
    }
  },
  created() {
    this.checkedValue = this.getCheckedValue(this.value);
  },
  methods: {
    // 获取选中值
    getCheckedValue(value) {
      let checkedValue = [];
      if (value && value.length) {
        value.forEach(item => {
          checkedValue.push(item.value);
        });
      }
      return checkedValue;
    },
    onChange(checkedValue) {
      console.log(checkedValue);
      let formatValue = [];
      checkedValue.forEach(value => {
        formatValue.push({
          type: 32,
          value,
          argValue: null
        });
      });
      this.$emit('input', formatValue);
      this.$emit('change', formatValue);
    }
  }
};
</script>
