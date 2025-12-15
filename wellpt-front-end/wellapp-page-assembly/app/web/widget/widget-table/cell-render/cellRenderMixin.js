export default {
  inject: ['pageContext', 'widgetTableContext'],
  props: {
    row: Object,
    text: [String, Number, Object],
    rowIndex: Number,
    slotOption: Object,
    invokeJsFunction: Function,
    invokeDevelopmentMethod: Function
  },
  methods: {
    $t() {
      if (this.widgetTableContext != undefined) {
        return this.widgetTableContext.$t(...arguments);
      }
      if (this.$i18n) {
        if (arguments[0] === this.$i18n.t(...arguments)) {
          return arguments[1]
        }
      }
      return this.$i18n.t(...arguments);
    }
  }
};
