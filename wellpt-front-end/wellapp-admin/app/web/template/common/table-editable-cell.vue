<template>
  <div @click.stop="cellClick" class="table-editable-cell">
    <div v-if="editable">
      <slot>
        <a-form-model-item ref="formItem" :prop="prop" :rules="cell.rules">
          <component :is="cell.type" v-model="model[prop]" v-bind="cell.attrs" @blur="onBlur"></component>
        </a-form-model-item>
      </slot>
    </div>
    <span v-if="!editable" class="label" :style="displayStyle">
      <a :title="displayValue">{{ displayValue }}</a>
    </span>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
export default {
  props: {
    id: String || Number,
    model: {
      type: Object,
      default() {
        return {};
      }
    },
    prop: String,
    cell: {
      type: Object,
      default() {
        return {
          type: 'a-input',
          rules: undefined,
          valid: true,
          editable: false,
          attrs: {
            value: undefined,
            options: []
          }
        };
      }
    },
    cellMap: {
      type: Object,
      default() {
        return {};
      }
    },
    displayStyle: {
      type: Object,
      default() {
        return {
          minWidth: '100px',
          minHeight: '20px'
        };
      }
    }
  },
  data() {
    return {
      cellId: this.id || generateId('SF'),
      editable: this.cell.editable || false
    };
  },
  computed: {
    displayValue() {
      let value = this.model[this.prop];
      let options = this.cell.attrs.options;
      if (!options || options.length == 0) {
        return value;
      }
      if (!value) {
        return value;
      }
      let labels = [];
      let values = Array.isArray(value) ? value : value.split(';');
      options.forEach(item => {
        if (values.includes(item.value)) {
          labels.push(item.label);
        }
      });
      return labels.join(';');
    }
  },
  created() {
    this.cellMap[this.cellId] = this;
  },
  methods: {
    cellClick() {
      this.editable = true;
      this.updateCellMapEditable();
      this.$nextTick(() => {
        if (this.$children[0] && this.$children[0].$el && this.$children[0].$el.focus) {
          this.$children[0].$el.focus();
          let inputEl = this.$children[0].$el.querySelector('input,textarea');
          if (inputEl) {
            inputEl.focus();
          }
          this.$children[0].open = true;
        }
      });
    },
    updateCellMapEditable(editable = this.editable) {
      if (!this.cellMap) {
        return;
      }

      for (let key in this.cellMap) {
        if (key != this.cellId) {
          this.cellMap[key].editable = false;
        }
      }
      this.cellMap[this.cellId].editable = editable;
    },
    onBlur() {
      if (this.cell.onBlur) {
        this.cell.onBlur.call(this);
      } else {
        this.submit();
      }
    },
    submit() {
      if (this.$refs.formItem) {
        this.$refs.formItem.validate('blur');
        this.$refs.formItem.validate('change');
      }
      this.$nextTick(() => {
        if (this.cell.valid !== false) {
          this.editable = false;
        }
      });
    }
  }
};
</script>

<style lang="less" scoped>
.table-editable-cell {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  .label a {
    border-bottom: dashed 1px #08c;
  }
}
</style>
