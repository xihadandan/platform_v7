import { getComponentFromProp } from 'ant-design-vue/es/_util/props-util';
import { Switch } from 'ant-design-vue'

export default {
  name: 'WSwitch',
  model: {
    prop: 'checked',
    event: 'change'
  },
  props: {
    ...Switch.props,
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
    value() {
      if (typeof this.checkedValue === 'string' && this.checkedValue.indexOf(';') !== -1) {
        if (this.checkedValue.indexOf(this.checked) !== -1) {
          return true
        } else {
          return false
        }
      }
      return this.checked === this.checkedValue;
    }
  },
  methods: {
    onChange(checked) {
      if (typeof this.checkedValue === 'string' && this.checkedValue.indexOf(';') !== -1) {
        let value = undefined
        if (checked) {
          const checkedValue = this.checkedValue.split(';')
          value = checkedValue[0]
        } else {
          value = this.unCheckedValue
        }
        this.$emit('change', value)
      } else {
        this.$emit('change', checked ? this.checkedValue : this.unCheckedValue);
      }
    }
  },
  render() {
    let switchProps = {
      props: {
        ...this.$props,
        checked: this.value,
        checkedChildren: getComponentFromProp(this, 'checkedChildren'),
        unCheckedChildren: getComponentFromProp(this, 'unCheckedChildren'),
      },
      on: {
        change: this.onChange
      }
    };
    return <Switch {...switchProps} />;
  }
};
