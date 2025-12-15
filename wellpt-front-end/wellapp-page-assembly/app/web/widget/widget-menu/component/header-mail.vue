<template>
  <a-dropdown placement="bottomRight" :trigger="['click']" v-model="visible" @visibleChange="onVisibleChange">
    <slot></slot>
    <div slot="overlay">
      <a-card style="width: 300px" size="small">
        <a-button size="small" icon="setting" slot="extra" />
        <PerfectScrollbar style="height: 600px"></PerfectScrollbar>
      </a-card>
    </div>
  </a-dropdown>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'HeaderMail',
  props: {},
  components: {},
  computed: {},
  data() {
    return { visible: false };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onVisibleChange(visible) {
      if (!this.visible) {
        this.visible = visible;
      }
    }
  }
};
</script>
