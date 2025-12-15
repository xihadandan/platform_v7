export default {
  inject: ['pageContext'],
  props: {
    row: Object,
    text: [String, Number, Object],
    rowIndex: Number,
    slotOption: Object,
    invokeJsFunction: Function,
    invokeDevelopmentMethod: Function
  },
  methods: {
  }
};
