<template>
  <div>
    <template v-for="(item, index) in tags">
      <a-tag :key="index">{{ item }}</a-tag>
    </template>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import cellRenderMixin from './cellRenderMixin';
export default {
  name: 'CellDataTagRender',
  mixins: [cellRenderMixin],
  title: '数据标签渲染器',
  props: {},
  components: {},
  computed: {
    tags() {
      if (this.text) {
        if (typeof this.text == 'string') {
          return this.text.split(';');
        }
      }
      return [];
    }
  },
  data() {
    return {
      loading: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {}
};
</script>
