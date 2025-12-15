<template>
  <Modal title="个人中心">
    <slot></slot>
    <template slot="content">
      <a-result></a-result>
    </template>
  </Modal>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'HeaderPersonality',
  props: {},
  components: { Modal },
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
