export default {
  name: 'EditFormElementConfigureMixin',
  inject: ['pageContext'],
  data() {
    return {
    };
  },

  beforeCreate() { },
  components: {},
  computed: {
    formVarOptions() {
      let opt = [];
      let id = this.designer.selectedId;
      let $wgt = this.pageContext.getVueWidgetById(id);
      if ($wgt) {
        let form = $wgt.form;
        if (form) {
          for (let k in form) {
            if (k && k == $wgt.formCodeWidgetConfigures[k].configuration.code) {
              opt.push({ label: $wgt.formCodeWidgetConfigures[k].configuration.title, value: k });
            }
          }
        }
      }

      return opt;
    }
  },
  created() {


  },
  mounted() {


  },
  methods: {
    onChangeTitleHidden() {
      this.$set(this.widget.configuration, 'titleHidden', !this.widget.configuration.titleHidden);
    }
  },
}
