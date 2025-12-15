import EditVarPanel from '../../edit-var-panel.vue';
export default {
  components: { EditVarPanel },
  data() {
    return {
      varItem: {
        code: undefined,
        value: undefined,
        remark: undefined
      }
    }
  },
  props: {
  },
  computed: {
  },
  methods: {
    editVar(callback, target) {
      if (this.varItem.code) {
        target.derive.push({
          ...this.varItem
        });
        callback(true);
        this.onCancelEditVarPanel();
      }
    },

    setVarCode(target, prefix, isEdit) {
      this.varItem.code = prefix + '-' + (target.derive.length + 1);
      if (!isEdit) {
        this.varItem.value = '';
      }
    },

    onCancelEditVar() {
      this.varItem.code = undefined;
      this.varItem.value = undefined;
      this.varItem.remark = undefined;
    },
  },

};
