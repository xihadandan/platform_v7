<template>
  <label>
    <a-spin v-if="loading">
      <a-icon slot="indicator" type="loading" style="font-size: inherit" spin />
    </a-spin>
    {{ text || emptyText }}
  </label>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'UserDisplay',
  props: {
    userId: String,
    displayDimension: {
      type: String,
      default: 'userName'
    },
    emptyText: String
  },
  components: {},
  computed: {},
  data() {
    return {
      text: '',
      loading: true
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.userId != undefined) {
      if (this.displayDimension == 'userName') {
        this.$clientCommonApi.getUserNamesByIds([this.userId]).then(map => {
          this.text = map[this.userId] || '';
          if (!this.text) {
            require('@workflow/app/web/page/workflow-designer/component/api')
              .fetchUserById({ userId: this.userId })
              .then(
                res => {
                  this.text = res.userName;
                  this.loading = false;
                },
                error => {
                  this.loading = false;
                }
              );
          } else {
            this.loading = false;
          }
        });
      }
    } else if (this._$USER != undefined) {
      if (this.displayDimension == 'userName') {
        this.text = this._$USER.userName;
      }
      this.loading = false;
    }
  },
  mounted() {},
  methods: {}
};
</script>
