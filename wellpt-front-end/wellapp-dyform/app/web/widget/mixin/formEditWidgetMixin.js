import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import { debounce } from 'lodash';

export default {
  extends: editWgtMixin,
  components: {},
  inject: ['dyform', 'pageContext'],
  computed: {

  },
  created() { },
  methods: {


    updateSimpleFieldInfos: debounce(function (v) {
      if (this.widget.configuration.isDatabaseField && this.widget.configuration.code) {
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          if (this.designer.SimpleFieldInfos[i].id == this.widget.id) {
            this.designer.SimpleFieldInfos[i].code = this.widget.configuration.code;
            this.designer.SimpleFieldInfos[i].name = this.widget.configuration.name;
            return;
          }
        }
      }
    }, 2000)
  },


  watch: {

  }
};
