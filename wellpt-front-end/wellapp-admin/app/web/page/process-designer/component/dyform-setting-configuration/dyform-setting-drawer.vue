<script>
import ProcessDesignDrawer from '../process-design-drawer.vue';
export default {
  props: {
    closeOpenDrawer: { type: Boolean, default: false }
  },
  extends: ProcessDesignDrawer
};
</script>
