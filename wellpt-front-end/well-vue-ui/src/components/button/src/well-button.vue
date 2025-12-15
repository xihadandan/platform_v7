
<script type='babel'>
import { Button } from "ant-design-vue";
export default {
  extends: Button,
  name: "WellButton",
  methods: {
    handleClick: function handleClick(event) {
      var sLoading = this.$data.sLoading;
      if (sLoading) {
        return;
      }
      this.$emit("click", event);
    },
  },
};
</script>
