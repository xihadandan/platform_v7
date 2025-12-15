/* 
使用“自定义选项”样式注意点
1、设置 treeNodeLabelProp ="label"
2、设置 replaceFields.label = 'name'
3、作用域插槽名称要和 replaceFields.title = 'name' 相同;即 v-slot:name="slotProps"
*/
import { TreeSelect } from 'ant-design-vue'
import { getComponentFromProp } from 'ant-design-vue/es/_util/props-util';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
import { getAlignFromPlacement } from 'ant-design-vue/es/vc-trigger/utils'

export default {
  name: 'WTreeSelect',
  // extends: TreeSelect,
  model: {
    prop: 'value',
    event: 'change',
  },
  props: {
    // ...TreeSelect.props,
    value: {
      type: [String, Object, Number, Array]
    },
    separator: {
      type: String,
      default: ';'
    },
    treeData: {
      type: [Array],
      default: () => []
    },
    replaceFields: {
      type: [Object],
      default: () => {
        // const defaultFields = {
        //   children: 'children',
        //   title: 'title',
        //   key: 'key',
        //   label: 'label',
        //   value: 'value'
        // };
        return {
          children: 'children',
          title: 'title',
          key: 'key',
          value: 'value'
        }
      }
    },
    treeNodeFilterProp: {
      type: String,
      default: 'title'
    },
    treeNodeLabelProp: {
      type: String,
      default: 'title'
    },
    treeCheckable: {
      type: Boolean,
      default: false // 显示checkbox, true时multiple为true表示多选
    },
    allowClear: {
      type: Boolean,
      default: true
    },
    showSearch: {
      type: Boolean,
      default: true
    },
    placeholder: {
      type: null
    },
    dropdownStyle: {
      type: Object,
      default: () => { }
    },
    dropdownClassName: {
      type: String,
      default: 'ps__child--consume'
    },
    showArrow: {
      type: Boolean,
      default: true
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
    loadData: {
      type: Function
    },
    getPopupContainer: {
      type: Function,
      default: getPopupContainerByPs()
    },
    //下拉菜单和选择器同宽
    dropdownMatchSelectWidth: {
      type: Boolean,
      default: true
    },
    showCheckedStrategy: {
      type: String,
      default: 'SHOW_CHILD'
    },
    // true时父子节点选中状态不再关联
    treeCheckStrictly: {
      type: Boolean,
      default: false
    },
    treeDefaultExpandAll: {
      type: Boolean,
      default: false
    },
    selectCheckAll: { // 全选
      type: Boolean,
      default: false
    },
    // 是否把每个选项的 label 包装到 value 中
    labelInValue: {
      type: Boolean,
      default: false
    },
    popupPlacement: {
      type: String,
      default: 'bottomLeft'
    },
    treeExpandedKeys: {
      type: Array,
    },
    nodeTitle: {
      type: null
    }
    // filterTreeNode: {
    //   type: [Boolean, Function]
    // }
  },
  computed: {
    currentValue() {
      let value = undefined
      if (this.value) {
        if (typeof this.value === 'string') {
          value = this.value.split(this.separator);
          if (this.labelInValue && !this.treeCheckable) {
            value = {
              value: this.value,
              label: this.formData[this.formDataFieldName]
            }
          } else if (this.treeCheckStrictly && this.treeCheckable) {
            let labelArr = []
            let label = this.formData[this.formDataFieldName]
            if (label) {
              labelArr = label.split(this.separator);
            }
            let valueArr = []
            value.forEach((item, i) => {
              let label = ''
              if (labelArr.length) {
                label = labelArr[i]
              }
              valueArr.push({
                value: item,
                label
              })
            });
            value = valueArr
          }
        }
      }
      return value
    }
  },
  methods: {
    onChange(value, label, extra) {
      let valueTemp
      if (this.labelInValue && !this.treeCheckable) {
        valueTemp = value
        value = value.value
      } else if (value && Array.isArray(value)) {
        if (this.treeCheckStrictly && this.treeCheckable) {
          valueTemp = JSON.parse(JSON.stringify(value))
          let valueArr = []
          value.forEach(item => {
            valueArr.push(item.value)
          });
          value = valueArr
        }
        value = value.join(this.separator)
      }
      if (Object.keys(this.formData).length && this.formDataFieldName) {
        if (this.labelInValue && !this.treeCheckable) {
          label = [valueTemp.label]
        } else if (this.treeCheckStrictly && this.treeCheckable && Array.isArray(valueTemp)) {
          if (!label) {
            label = []
          }
          valueTemp.forEach(item => {
            label.push(item.label)
          });
        }

        if (label) {
          this.formData[this.formDataFieldName] = label.join(this.separator);
        } else {
          this.formData[this.formDataFieldName] = ''
        }
      }
      if (value === undefined) {
        value = ''
      }
      this.$emit('input', value);
      this.$emit('change', value, label, extra);
    },
    onFocus() {
      const treeCom = this.$refs.antTreeSelect.$children[0]
      const ariaId = treeCom.ariaId
      this.$emit('focus');
    },
    onBlur() {
      this.$emit('blur');
    },
    onSearch() {
      this.$emit('search', ...arguments);
    },
    onTreeExpand() {
      this.$emit('treeExpand', ...arguments);
    }
  },
  render() {
    const that = this
    let placeholder = getComponentFromProp(this, 'placeholder')
    if (!placeholder) {
      placeholder = '请选择'
    }
    const dropdownStyle = {
      maxHeight: '400px',
      overflow: 'auto',
      textAlign: 'left',
      ...this.$props.dropdownStyle
    }

    const getPopupContainer = (target, dialogContext) => {
      // webpack://%5Bname%5D/node_modules/ant-design-vue/es/vc-trigger/Trigger.js  getPopupAlign

      target.__vue__.$parent.$parent.$parent.getPopupAlign = function () {
        const props = this.$props;
        const popupPlacement = that.$props.popupPlacement,
          popupAlign = props.popupAlign,
          builtinPlacements = props.builtinPlacements;

        if (popupPlacement && builtinPlacements) {
          return getAlignFromPlacement(builtinPlacements, popupPlacement, popupAlign);
        }
        return popupAlign;
      }

      // target.__vue__.$parent.$parent.$nextTick(function () {
      //   this.$parent.popupPlacement = 'topLeft'
      // })
      return this.$props.getPopupContainer(target, dialogContext)
    }

    const setScopedSlots = (arr) => {
      const $scopedSlots = this.$scopedSlots
      // es/tree-select/index.js  updateTreeData()
      if (!arr) {
        return
      }
      const replaceFields = this.$props.replaceFields
      return arr.map(item => {
        let node = {
          ...item,
          // scopedSlots: {
          //   title: 'nodeTitle'
          // },
        }
        if ($scopedSlots[replaceFields.title]) {
          node.title = $scopedSlots[replaceFields.title](item)
        }
        if (replaceFields.label) {
          node.label = item[replaceFields.label]
        }
        const children = item[replaceFields.children]

        if (children) {
          node[replaceFields.children] = setScopedSlots(children)
          return node
        }
        return node
      })
    }

    let treeData
    if (Object.keys(this.$scopedSlots).length) {
      treeData = setScopedSlots(this.$props.treeData)

      let replaceFields = this.$props.replaceFields
      delete replaceFields.title
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
        focus: this.onFocus,
        blur: this.onBlur,
        search: this.onSearch,
        treeExpand: this.onTreeExpand
      },
      ref: 'antTreeSelect',
      // scopedSlots: this.$scopedSlots
    }
    if (treeData) {
      props.props.treeData = treeData
    }
    return (
      <TreeSelect {...props} />
    )
  }
}
