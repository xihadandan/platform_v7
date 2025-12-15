import addEventListener from 'ant-design-vue/es/vc-util/Dom/addEventListener';
import * as utils from 'ant-design-vue/es/vc-slider/src/utils';
import BaseMixin from 'ant-design-vue/es/_util/BaseMixin';
import { hasProp } from 'ant-design-vue/es/_util/props-util';
import './w-slider-checkbox.less';


function noop() { }

export default {
  name: 'WSliderCheckbox',
  model: {
    prop: 'value',
    event: 'change',
  },
  mixins: [BaseMixin],
  props: {
    value: {
      type: Number,
    },
    options: {
      type: Array,
      default: () => []
    },
    railStyle: {
      type: Object,
      default: () => {
        return {
          height: '32px'
        }
      }
    },
    mode: {
      type: String,
      validator: function (value) {
        return ['1', '2', '3',].includes(value)
      }
    },
    disabled: {
      type: Boolean,
      default: false
    },
    vertical: {
      // 值为 true 时，Slider 为垂直方向
      type: Boolean,
      default: false
    },
    reverse: {
      // 反向坐标轴
      type: Boolean,
      default: false
    },
    handleStyle: {
      type: Object,
      default: () => {
        return {
          height: '32px'
        }
      }
    },
    min: {
      type: Number,
      default: 0
    },
    max: {
      type: Number,
      default: 100
    },
    step: {
      type: Number,
      default: 1
    },
    marks: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    const value = this.value !== undefined ? this.value : 0;
    this.handlesRefs = {}
    return {
      sValue: this.trimAlignValue(value),
      dragging: false,
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.document = this.$refs.sliderRef && this.$refs.sliderRef.ownerDocument;
    })
  },
  beforeDestroy() {
    this.$nextTick(() => {
      this.removeDocumentEvents();
    })
  },
  methods: {
    onChange(state) {
      const isNotControlled = !hasProp(this, 'value');
      const nextState = state.sValue > this.max ? { ...state, sValue: this.max } : state;
      if (isNotControlled) {
        this.setState(nextState);
      }

      const changedValue = nextState.sValue;
      this.$emit('change', changedValue);
    },
    onMouseMove(e) {
      if (!this.$refs.sliderRef) {
        this.onEnd();
        return;
      }
      const position = utils.getMousePosition(this.vertical, e);
      this.onMove(e, position - this.dragOffset);
    },
    onMove(e, position) {
      utils.pauseEvent(e);
      const { sValue } = this;
      const value = this.calcValueByPos(position);
      if (value === sValue) return;
      console.log('position------', value)
      this.onChange({ sValue: value });
    },
    onEnd(force) {
      const { dragging } = this;
      this.removeDocumentEvents();
      if (dragging || force) {
        this.$emit('afterChange', this.sValue);
      }
      this.setState({ dragging: false });
    },
    onMouseDown(e) {
      if (e.button !== 0) {
        return;
      }
      const isVertical = this.vertical;
      let position = utils.getMousePosition(isVertical, e);
      if (!utils.isEventFromHandle(e, this.handlesRefs)) {
        this.dragOffset = 0;
      } else {
        const handlePosition = utils.getHandleCenterPosition(isVertical, e.target);
        this.dragOffset = position - handlePosition;
        position = handlePosition;
      }
      this.removeDocumentEvents();
      this.onStart(position);
      this.addDocumentMouseEvents();
      utils.pauseEvent(e);
    },
    onStart(position) {
      this.setState({ dragging: true });
      const { sValue } = this;
      this.$emit('beforeChange', sValue);

      const value = this.calcValueByPos(position);

      this.startValue = value;
      this.startPosition = position;
      if (value === sValue) return;

      this.prevMovedHandleIndex = 0;
      this.onChange({ sValue: value });
    },
    onMouseUp() {

    },
    calcValue(offset) {
      const { vertical, min, max } = this;
      const ratio = Math.abs(Math.max(offset, 0) / this.getSliderLength());
      const value = vertical ? (1 - ratio) * (max - min) + min : ratio * (max - min) + min;
      return value;
    },
    calcValueByPos(position) {
      const sign = this.reverse ? -1 : +1;
      const sliderStart = this.getSliderStart()
      const sliderLenth = this.getSliderLength()
      if (position + 120 > sliderStart + sliderLenth) {
        position = sliderStart + sliderLenth - 120
      }
      let pixelOffset = sign * (position - sliderStart);


      const nextValue = this.trimAlignValue(this.calcValue(pixelOffset));
      return nextValue;
    },
    calcOffset(value) {
      const { min, max } = this;
      const ratio = (value - min) / (max - min);
      return ratio * 100;
    },
    trimAlignValue(v, nextProps = {}) {
      if (v === null) {
        return null;
      }
      const mergedProps = { ...this.$props, ...nextProps };
      const val = utils.ensureValueInRange(v, mergedProps);
      return utils.ensureValuePrecision(val, mergedProps);
    },
    getSliderStart() {
      const stepsRef = this.$refs.stepsRef;
      const { vertical, reverse } = this;
      const rect = stepsRef.getBoundingClientRect();
      if (vertical) {
        return reverse ? rect.bottom : rect.top;
      }
      return window.pageXOffset + (reverse ? rect.right : rect.left);
    },
    getSliderLength() {
      const stepsRef = this.$refs.stepsRef;
      if (!stepsRef) {
        return 0;
      }

      const coords = stepsRef.getBoundingClientRect();

      return this.vertical ? coords.height : coords.width;
    },
    addDocumentMouseEvents() {
      this.onMouseMoveListener = addEventListener(this.document, 'mousemove', this.onMouseMove);
      this.onMouseUpListener = addEventListener(this.document, 'mouseup', this.onEnd);
    },
    removeDocumentEvents() {
      this.onMouseMoveListener && this.onMouseMoveListener.remove();
      this.onMouseUpListener && this.onMouseUpListener.remove();
      /* eslint-enable no-unused-expressions */
    },
    saveHandle: function saveHandle(index, handle) {
      this.handlesRefs[index] = handle;
    }
  },
  render() {
    let { disabled, railStyle, handleStyle, min, max, sValue, options } = this
    const offset = this.calcOffset(sValue);

    const renderRail = () => {
      const props = {
        style: {
          width: `${120 * options.length}px`,
          ...railStyle
        }
      }
      return (
        <div class="w-slider-rail" {...props}></div>
      )
    }

    const renderSteps = () => {
      const { options } = this.$props

      return (
        <div
          class="w-slider-steps"
          ref="stepsRef"
          style="position: absolute;"
        >
          {options.map(item => {
            return (
              <div class="w-slider-option">{item.label}</div>
            )
          })}
        </div>
      )
    }

    const renderHandle = () => {
      handleStyle = {
        ...handleStyle,
        // width: '100px',
        'left': `${offset}%`,
        'right': 'auto',
        'transform': 'translateX(-50%)'
      }
      const ariaProps = {
        'aria-valuemin': min,
        'aria-valuemax': max,
        'aria-valuenow': sValue,
        'aria-disabled': !!disabled,
      }
      const handleProps = {
        attrs: {
          ...ariaProps,
        },
        directives: [
          {
            name: 'ant-ref',
            value: h => this.saveHandle(0, h),
          },
        ],
        style: handleStyle
      }
      return (
        <div class="w-slider-handle" {...handleProps}>值</div>
      )
    }

    return (
      <div
        ref="sliderRef"
        class="w-slider-radio-container"
        tabIndex="-1"
        onMousedown={disabled ? noop : this.onMouseDown}
        onMouseup={disabled ? noop : this.onMouseUp}
      >
        {renderRail()}
        {renderSteps()}
        {renderHandle()}
      </div>
    )
  }
}


/* 
 const renderSteps2 = () => {
      const { options } = this.$props
      const vNodes = options.map(item => {
        const props = {
          props: {
            value: item.value
          }
        }
        return (
          <a-radio-button {...props}>{item.label}</a-radio-button>
        )
      })

      return (
        <a-radio-group button-style="solid">
          {vNodes}
        </a-radio-group>
      )
    }

*/