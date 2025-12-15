import './index.less';
import { InputNumber } from 'ant-design-vue';
import { getComponentFromProp } from 'ant-design-vue/es/_util/props-util';

export default {
  name: 'WInputNumber',
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    ...InputNumber.props,
    addonBefore: {
      type: null
    },
    addonAfter: {
      type: null
    },
    prefix: {
      type: null
    },
    suffix: {
      type: null
    },
    hiddenHandler: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    currentValue() {
      return this.value;
    }
  },
  methods: {
    onChange(value) {
      this.$emit('change', ...arguments);
    },
    onPressEnter(event) {
      this.$emit('pressEnter', ...arguments);
    }
  },
  render() {
    const addonBefore = getComponentFromProp(this, 'addonBefore')
    const addonAfter = getComponentFromProp(this, 'addonAfter')

    const prefix = getComponentFromProp(this, 'prefix')
    const suffix = getComponentFromProp(this, 'suffix')

    let className = {
      'w-input-number': true
    }
    if (this.$props.hiddenHandler) {
      className['w-input-number-hidden-handler'] = true
    }
    const props = {
      props: {
        ...this.$props,
        value: this.currentValue,
      },
      on: {
        change: this.onChange,
        pressEnter: this.onPressEnter
      },
      'class': className
    }


    const addonBeforeNode = (
      addonBefore ? <span class="w-input-number-group-addon">{addonBefore}</span> : null
    )
    const addonAfterNode = (
      addonAfter ? <span class="w-input-number-group-addon">{addonAfter}</span> : null
    )

    const prefixNode = (
      prefix ? <span class="w-input-number-prefix">{prefix}</span> : null
    )
    const suffixNode = (
      suffix ? <span class="w-input-number-suffix">{suffix}</span> : null
    )

    let element = (
      <InputNumber {...props} />
    )

    if (prefixNode || suffixNode) {
      element = (
        <span class="w-input-number-affix-wrapper">
          {prefixNode}
          {element}
          {suffixNode}
        </span>
      )
    }

    if (addonBeforeNode || addonAfterNode) {
      element = (
        <span class="w-input-number-group-wrapper">
          <span class="w-input-number-wrapper w-input-number-group">
            {addonBeforeNode}
            {element}
            {addonAfterNode}
          </span>
        </span>
      )
    }

    return (
      element
    )
  }
}