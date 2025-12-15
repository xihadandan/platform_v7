<template>
  <div>
    {{ text }}
    <span v-if="refTitle" class="widget-hyperlink" :title="'引自：' + refTitle">
      <Icon type="link"></Icon>
    </span>
  </div>
</template>

<script type="text/babel">
// 数据仓库列表，标题渲染
export default {
  name: 'DatabaseTableTitleRender',
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
      refTitle: ''
    };
  },
  watch: {},
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
  },
  mounted() {
    if (this.row.isRef === 1) {
      this.getModuleDetail();
    }
  },
  methods: {
    getModuleDetail() {
      $axios
        .post('/json/data/services', {
          argTypes: [],
          serviceName: 'appModuleMgr',
          methodName: 'getModuleDetail',
          args: JSON.stringify([this.row.moduleId]),
          validate: false,
          version: ''
        })
        .then(({ data }) => {
          if (data.code == 0) {
            this.refTitle = '';
            if (data.data) {
              this.refTitle = (data.data.appSystemBean ? data.data.appSystemBean.name + '/' : '') + data.data.name;
            }
          }
        });
    }
  }
};
</script>
<style lang="less" scoped></style>
