import { getComponentFromProp } from 'ant-design-vue/es/_util/props-util';

export default {
  name: 'OrganizationSelect',
  inject: ['designer'],
  props: {
    value: {
      type: String
    },
    mode: {
      type: String,
    },
    placeholder: {
      type: null // 基础的类型检查 (`null` 和 `undefined` 会通过任何类型验证)
    },
    allowClear: {
      type: Boolean,
      default: true
    },
    showSearch: {
      type: Boolean,
      default: true
    },
    optionFilterProp: {
      type: String,
      default: 'title'
    },
    separator: {
      type: String,
      default: ';'
    },
    dropdownClassName: {
      type: String,
      default: 'ps__child--consume'
    },
    getPopupContainer: {
      type: Function,
      default: (triggerNode) => {
        return triggerNode.parentNode
      }
    },
  },
  computed: {
    organizationList() {
      return this.designer.organizationList || [];
    },
    currentValue() {
      let value = undefined
      if (this.value) {
        value = this.value
      }
      if (this.mode === 'multiple' && value) {
        value = value.split(this.separator)
      }
      return value
    }
  },
  methods: {
    onChange(value, option) {
      if (this.mode === 'multiple') {
        if (value.length) {
          value = value.join(this.separator)
        } else {
          value = undefined
        }
      }
      this.$emit('input', value)
      this.$emit('change', value, option)
    }
  },
  render() {
    let placeholder = getComponentFromProp(this, 'placeholder')
    if (!placeholder) {
      placeholder = '请选择组织'
    }
    const props = {
      props: {
        ...this.$props,
        value: this.currentValue,
        placeholder
      },
      on: { change: this.onChange },
    }

    const renderChildren = () => {
      return this.organizationList.map(item => {
        const props = {
          props: {
            key: item.uuid,
            value: item.id,
            title: item.text,
            disabled: item.disabled || false
          }
        }
        return <a-select-option {...props}>{item.text}</a-select-option>
      })
    }
    return (
      <a-select {...props} >
        {renderChildren()}
      </a-select>
    )
  }
}