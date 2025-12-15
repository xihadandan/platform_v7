export default {
  subformColumnRenderTemplate: `
  <template>
  <div>
    <slot>
      <!-- 默认插槽渲染默认的字段组件内容 -->
    </slot>
    <a-button type="link">点击</a-button>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'TestRenderColumnExample',
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
  `
}
