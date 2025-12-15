import { Checkbox } from 'ant-design-vue';

export default {
  name: 'WCheckbox',
  model: {
    prop: 'checked'
  },
  props: {
    ...Checkbox.props,
    checked: {
      // type: [Number, String, Boolean],
      type: null,
      required: true
    },
    checkedValue: {
      type: [String, Boolean, Number],
      default() {
        return '1';
      }
    },
    unCheckedValue: {
      type: [String, Boolean, Number],
      default() {
        return '0';
      }
    }
  },
  computed: {
    currentValue() {
      return this.checked === this.checkedValue;
    }
  },
  methods: {
    onChange(event) {
      const targetChecked = event.target.checked;
      this.$emit('input', targetChecked ? this.checkedValue : this.unCheckedValue);
      this.$emit('change', event);
    }
  },
  render() {
    const children = this.$slots.default
    let props = {
      props: {
        ...this.$props,
        checked: this.currentValue
      },
      on: {
        change: this.onChange
      }
    };
    return (
      <Checkbox {...props} >
        {children !== undefined ? children : undefined}
      </Checkbox>
    );
  }
};
