import './w-select.less';
import { getComponentFromProp } from 'ant-design-vue/es/_util/props-util';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
import { getAlignFromPlacement } from 'ant-design-vue/es/vc-trigger/utils'

export default {
  name: 'WSelect',
  props: {
    value: {
      type: [String, Number]
    },
    options: {
      type: [Array],
      default: () => []
    },
    mode: {
      type: String,
      validator: function (value) {
        return ['default', 'multiple', 'tags', 'combobox'].includes(value)
      }
    },
    placeholder: {
      type: null // 基础的类型检查 (`null` 和 `undefined` 会通过任何类型验证)
    },
    addonBefore: {
      type: null
    },
    addonAfter: {
      type: null
    },
    dropdownClassName: {
      type: String,
      default: 'ps__child--consume'
    },
    showArrow: {
      type: Boolean,
      default: true
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
    replaceFields: {
      type: [Object],
      default: () => {
        return {
          title: 'text',
          key: 'id',
          value: 'id'
        }
      }
    },
    formData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    formDataFieldName: {
      type: String,
      default: ''
    },
    dropdownStyle: {
      type: Object,
      default: () => { }
    },
    getPopupContainer: {
      type: Function,
      default: getPopupContainerByPs()
    },
    disabled: {
      type: Boolean,
      default: false
    },
    popupPlacement: {
      type: String,
      default: 'bottomLeft'
    },
    dropdownMatchSelectWidth: {
      type: Boolean,
      default: true
    },
    optionValueType: {
      // type this.options[0][this.replaceFields.value] options中value的数据类型
      type: String,
      default: 'string'
    }
  },
  computed: {
    replaceFieldsComputed() {
      let replaceFields = this.defaultFields
      if (this.isRenderChildren) {
        replaceFields = { ...this.replaceFields }
      }
      return replaceFields
    },
    optionsComputed() {
      return this.options || [];
    },
    currentValue() {
      let value = undefined
      if (this.value) {
        value = this.value
      }
      if (this.mode === 'multiple' && value) {
        value = value.split(this.separator)
      }
      if (this.optionValueType === 'number' && value) {
        let valueNumer
        if (Array.isArray(value)) {
          valueNumer = value.map(v => {
            if (v) {
              return Number(v)
            }
          })
        } else {
          valueNumer = Number(value)
        }
        value = valueNumer
      }
      this.setFieldName(value);
      return value
    }
  },
  watch: {
    optionsComputed() {
      this.setFieldName(this.currentValue);
    }
  },
  data() {
    return {
      isRenderChildren: true,
      defaultFields: {
        title: 'label',
        key: 'value',
        value: 'value'
      }
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
      if (value === undefined) {
        value = ''
      } else {
        if (this.optionValueType === 'number') {
          value = value + ''
        }
      }

      this.$emit('input', value)
      this.$emit('change', value, option)
    },
    onDropdownVisibleChange(open) {
      this.$emit('dropdownVisibleChange', open)
    },
    setFieldName: function (value) {
      if (Object.keys(this.formData).length && this.formDataFieldName) {
        if (value) {
          const replaceFields = this.replaceFieldsComputed
          let fieldNameArr = [];
          value.split(this.separator).forEach(item => {
            let hasIndex = this.optionsComputed.findIndex(oitem => {
              return oitem[replaceFields.value] == item;
            });
            if (hasIndex > -1) {
              fieldNameArr.push(this.optionsComputed[hasIndex][replaceFields.title]);
            }
          })
          this.formData[this.formDataFieldName] = fieldNameArr.join(this.separator);
        } else {
          this.formData[this.formDataFieldName] = ''
        }
        // console.log(this.formData[this.formDataFieldName]);
      }
    }
  },
  render() {
    const that = this
    let placeholder = getComponentFromProp(this, 'placeholder')
    if (!placeholder) {
      placeholder = '请选择'
    }

    const { options } = this.$props
    if (options.length) {
      if (options[0][this.defaultFields['value']] !== undefined &&
        options[0][this.defaultFields['title']] !== undefined
      ) {
        this.isRenderChildren = false
      } else {
        this.isRenderChildren = true
      }
    }
    const dropdownStyle = {
      maxHeight: '400px',
      overflow: 'auto',
      textAlign: 'left',
      ...this.$props.dropdownStyle
    }

    const getPopupContainer = (target, dialogContext) => {

      target.__vue__.$parent.getPopupAlign = function () {
        const props = this.$props;
        const popupPlacement = that.$props.popupPlacement,
          popupAlign = props.popupAlign,
          builtinPlacements = props.builtinPlacements;

        if (popupPlacement && builtinPlacements) {
          return getAlignFromPlacement(builtinPlacements, popupPlacement, popupAlign);
        }
        return popupAlign;
      }

      return this.$props.getPopupContainer(target, dialogContext)
    }

    const props = {
      props: {
        ...this.$props,
        value: this.currentValue,
        placeholder,
        dropdownStyle,
        getPopupContainer
      },
      on: {
        change: this.onChange,
        dropdownVisibleChange: this.onDropdownVisibleChange
      },
    }

    if (this.isRenderChildren) {
      delete props.props.options
    }

    const renderChildren = () => {
      const replaceFields = this.replaceFieldsComputed
      return this.optionsComputed.map(item => {
        const title = item[replaceFields.title];
        const key = item[replaceFields.key];
        const value = item[replaceFields.value];
        const props = {
          props: {
            key,
            value,
            title
          }
        }
        return <a-select-option {...props}>{title}</a-select-option>
      })
    }

    const addonBefore = getComponentFromProp(this, 'addonBefore')
    const addonAfter = getComponentFromProp(this, 'addonAfter')

    if (addonBefore || addonAfter) {
      const addonBeforeNode = (
        addonBefore ? <span class="w-select-group-addon">{addonBefore}</span> : null
      )
      const addonAfterNode = (
        addonAfter ? <span class="w-select-group-addon">{addonAfter}</span> : null
      )
      return (
        <div class="w-select-group">
          {addonBeforeNode}
          <a-select {...props} >
            {this.isRenderChildren ? renderChildren() : undefined}
          </a-select>
          {addonAfterNode}
        </div>
      )
    } else {
      return (
        <a-select {...props} >
          {this.isRenderChildren ? renderChildren() : undefined}
        </a-select>
      )
    }

  }
}
