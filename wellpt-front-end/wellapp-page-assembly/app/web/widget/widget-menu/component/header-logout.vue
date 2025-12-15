<template>
  <span @click="logout">
    <slot></slot>
  </span>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'HeaderLogout',
  props: {},
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    logout() {
      window.location.href = `/logout${this._$SYSTEM_ID ? '?system=' + this._$SYSTEM_ID : ''}`;
    }
  }
};
</script>
