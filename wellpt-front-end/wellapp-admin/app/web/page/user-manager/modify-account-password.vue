<template>
  <HtmlWrapper title="修改密码">
    <div>
      <HeaderModifyPassword
        ref="modifyPassword"
        :closable="false"
        :visible="true"
        :cancelButtonProps="{
          style: {
            display: 'none'
          }
        }"
      />
    </div>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ModifyAccountPassword',
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
  methods: {}
};
</script>
