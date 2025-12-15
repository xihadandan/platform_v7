import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
export default {
  name: 'EditFormElementMixin',
  mixins: [editWgtMixin],
  inject: ['form'],
  data() {
    return {
    };
  },
  provide() {
    return this.form == undefined ? { // 避免未在表单区内，导致注入报错
      form: {}
    } : {};
  },
  beforeCreate() { },
  components: {},
  computed: {
    rules() {
      return { required: this.widget.configuration === undefined ? false : this.widget.configuration.required }
    }
  },
  created() {
    // if (this.form === undefined) {
    //   this.$message.error('组件必须在表单区内');
    //   this.widgetsOfParent.splice(this.index, 1);
    //   this.designer.clearSelected();
    //   throw new Error(0)
    // }

  },
  mounted() { },
  methods: {
  },



};
