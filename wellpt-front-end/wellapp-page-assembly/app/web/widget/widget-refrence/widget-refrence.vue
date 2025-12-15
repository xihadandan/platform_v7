<template>
  <div :class="[widgetClass, 'widget-refrence']">
    <a-skeleton active v-if="fetching" />
    <template v-for="(wgt, windex) in refWidgets">
      <component :key="wgt.id" :is="wgt.wtype" :widget="wgt" :index="windex" :widgetsOfParent="refWidgets" :parent="parent"></component>
    </template>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { set, camelCase } from 'lodash';
export default {
  name: 'WidgetRefrence',
  mixins: [widgetMixin],
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      fetching: true,
      refWidgets: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    let widgetUuid = this.widget.configuration.refWidgetUuid;
    if (widgetUuid) {
      this.fetching = true;
      this.fetchRefWidgetDefinition(widgetUuid).then(def => {
        this.fetching = false;
        let items = JSON.parse(def.definitionJson).items;
        this.refWidgets.splice(0, this.refWidgets.length);
        this.refWidgets.push(...items);
        if (def.i18ns && def.i18ns.length > 0) {
          let message = { Widget: {} };
          for (let i of def.i18ns) {
            set(message.Widget, i.code, i.content);
          }
          this.$i18n.mergeLocaleMessage(this.$i18n.locale, message);
        }
        this.$nextTick(() => {
          this.$emit('fetched', def);
        });
      });
    }
  },
  mounted() {},
  methods: {
    fetchRefWidgetDefinition(uuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/user/widgetDef/getDetail`, { params: { uuid } })
          .then(({ data }) => {
            if (data.data) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    }
  }
};
</script>
