/* 子流程数据下拉框 */
import { getComponentFromProp } from 'ant-design-vue/es/_util/props-util';

export default {
  name: 'NodeSubflowSelect',
  inject: ['graph'],
  props: {
    value: {
      type: [String, Array]
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
    separator: {
      type: String,
      default: ';'
    },
    selectAll: {
      type: Boolean,
      default: false
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
    isUnit: {
      type: Boolean,
      default: false
    },
    unitElement: {
      type: Function,
      default: () => {
        return {
          type: 32,
          value: '',
          argValue: null
        }
      }
    },
    filterId: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      isSelectAll: false,
    }
  },
  computed: {
    options() {
      let options = [];
      if (this.graph.instance) {
        const tasksData = this.graph.instance.subflowsData
        options = tasksData.filter(item => {
          return !this.filterId.includes(item.id)
        })
      }
      return options;
    },
    currentValue() {
      let value = undefined
      if (this.value) {
        value = this.value
      }
      if (this.mode === 'multiple' && value && typeof (value) === "string") {
        value = value.split(this.separator)
      }
      if (this.isUnit && value && Array.isArray(value)) {
        let valueArr = []
        value.forEach(item => {
          valueArr.push(item.value)
        });
        value = valueArr
      }
      return value
    }
  },
  methods: {
    onChange(value, option) {
      if (this.mode === 'multiple') {
        if (this.options.length > 0 && value.length == this.options.length) {
          this.isSelectAll = true;
        } else {
          this.isSelectAll = false;
        }
        if (value.length) {
          value = value.join(this.separator)
        } else {
          value = ''
        }
      }
      if (this.isUnit && value) {
        let valueArr = []
        if (typeof (value) === "string") {
          value = value.split(this.separator)
        }
        value.forEach(v => {
          let item = this.unitElement()
          item.value = v
          valueArr.push(item)
        });
        value = valueArr
      }

      if (Object.keys(this.formData).length && this.formDataFieldName) {
        if (option) {
          if (this.mode === 'multiple') {
            this.formData[this.formDataFieldName] = _.map(option, item => {
              let data = item.data.props;
              return data.title;
            }).join(this.separator);
          } else {
            this.formData[this.formDataFieldName] = option.componentOptions.children[0].text.trim();
          }
        } else {
          this.formData[this.formDataFieldName] = ''
        }
      }
      this.$emit('input', value)
      this.$emit('change', value, option)
    },
    onChangeAll(e, menu) {
      let checked = e.target.checked;
      let value = [], label = [];
      if (checked) {
        this.isSelectAll = true;
        value = this.options.map(item => {
          return item.id;
        })
        label = this.options.map(item => {
          return item.name;
        })
      } else {
        this.isSelectAll = false;
      }
      if (Object.keys(this.formData).length && this.formDataFieldName) {
        this.formData[this.formDataFieldName] = label.join(this.separator);
      }
      this.$emit('input', value.join(this.separator));
      this.$emit('onSelectAllchange', value, label.join(this.separator), this.options);
    }
  },
  render() {
    let placeholder = getComponentFromProp(this, 'placeholder')
    if (!placeholder) {
      placeholder = '请选择'
    }


    const dropdownRender = (menu) => {
      let data = (<div>{menu}</div>);
      if (this.mode == 'multiple' && this.selectAll) {
        const selectallProp = {
          props: {
            checked: this.isSelectAll
          },
          on: {
            change: (e) => this.onChangeAll(e, menu)
          }
        }
        data = (<div>
          <div style="padding:4px var(--w-select-item-lr-padding); cursor: pointer">
            <a-checkbox {...selectallProp}>全选</a-checkbox>
          </div>
          {menu}
        </div>)
      }
      return data;
    }

    const props = {
      props: {
        ...this.$props,
        value: this.currentValue,
        placeholder,
        dropdownRender: dropdownRender,
        ref: 'nodeTaskSelectRef'
      },
      on: { change: this.onChange },
    }

    const renderChildren = () => {
      return this.options.map(item => {
        const title = item.name
        const props = {
          props: {
            key: item.id,
            value: item.id,
            title
          }
        }
        return <a-select-option {...props}>{title}</a-select-option>
      })
    }

    return (
      <a-select {...props} >
        {renderChildren()}
      </a-select>
    )
  }
}
