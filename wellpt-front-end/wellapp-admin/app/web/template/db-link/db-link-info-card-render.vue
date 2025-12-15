<template>
  <div style="height: 120px; padding: 12px">
    <div style="display: flex">
      <div style="background-color: #f7f7f7; border-radius: 4px; padding: 8px">
        <DbIcons :type="row.DB_TYPE" :width="54" :height="54" />
      </div>
      <div style="width: calc(100% - 70px); padding-left: 12px">
        <label style="font-weight: bolder">{{ row.NAME }}</label>
        <div style="min-height: 32px">{{ row.REMARK }}</div>
      </div>
    </div>

    <div style="text-align: right">
      <slot name="actions" />
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import DbIcons from './icons/db-icons.vue';
export default {
  name: 'DbLinkInfoCardRender',
  props: {
    row: Object
  },
  components: { DbIcons },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
