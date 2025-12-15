<template>
  <Modal v-model="visible" :title="title" okText="保存" :ok="onSave">
    <template slot="content">
      <ModuleDetail :detail="{ uuid }" ref="moduleDetail" />
    </template>
  </Modal>
</template>
<style lang="less"></style>
<script type="text/babel">
import ModuleDetail from './module-detail.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'ModuleDetailModal',
  model: {
    modalVisible: Boolean
  },
  props: {
    uuid: String,
    title: String,
    afterSave: Function,
    modalVisible: { type: Boolean, default: false }
  },
  components: { ModuleDetail, Modal },
  computed: {},
  data() {
    return { visible: this.modalVisible };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onSave() {
      this.$refs.moduleDetail.onSave(data => {
        if (typeof this.afterSave == 'function') {
          this.visible = false;
          this.afterSave(data);
        }
      });
    }
  }
};
</script>
