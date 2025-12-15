<template>
  <div>
    <a-switch v-model="status" @change="statusChange" />
  </div>
</template>

<script type="text/babel">
// 版式文档服务配置列表，状态渲染
export default {
  name: 'LayoutdocumentserviceTableStatusRender',
  props: {
    text: String,
    row: {
      type: Object,
      default: {} // edit、label
    }
  },
  components: {},
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      status: this.text == '1'
    };
  },
  watch: {
    text(v) {
      debugger;
      this.status = v == '1';
    }
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
  },
  mounted() {},
  methods: {
    statusChange() {
      let _this = this;
      _this.$confirm({
        title: '系统仅允许存在一个"启用"的服务，继续保存将修改其他"启用"的服务的状态为"禁用"，是否确定保存？',
        onOk() {
          $axios
            .post('/api/basicdata/layoutDocumentServiceConf/changeLayoutDocumentConfigStatus', {
              uuid: _this.row.UUID,
              status: _this.status ? '1' : '0'
            })
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('修改成功');
                _this.pageContext.getVueWidgetById('QDctflXBiEofrDqKmPvvZbyljuBIifgq').refetch();
              }
            });
        },
        onCancel() {
          _this.status = !_this.status;
        }
      });
    }
  }
};
</script>
<style lang="less" scoped></style>
