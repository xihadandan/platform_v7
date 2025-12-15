<template>
  <a-card :style="{ width: width, height: height }" size="small">
    <template slot="title">
      <a-icon type="database" />
      {{ title }}
      <label class="sub-title">{{ subTitle }}</label>
    </template>
    <template slot="extra">
      <a-button type="link" icon="setting"></a-button>
      <a-button type="link" icon="delete"></a-button>
    </template>
    <div v-show="!collapsed">
      <p v-for="(item, i) in items">{{ item.title }} ------ {{ item.code }}</p>
    </div>
    <a-button block type="link" @click="onClickCollapse">
      <a-icon type="double-right" :rotate="collapsed ? 90 : -90"></a-icon>
    </a-button>
  </a-card>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'CustomVueNode',
  inject: ['getNode'],
  mixins: [],
  data() {
    return {
      title: undefined,
      subTitle: undefined,
      items: [],
      collapsed: true,
      width: '300px',
      height: '100px'
    };
  },
  watch: {},
  beforeCreate() {},
  computed: {},
  created() {},
  methods: {
    onClickCollapse() {
      this.collapsed = !this.collapsed;
      const node = this.getNode();
      if (!this.collapsed) {
        this.height = 100 + this.items.length * 35 + 'px';
        node.setSize({ width: parseInt(this.width), height: parseInt(this.height) });
      } else {
        this.height = '100px';
        node.setSize({ width: parseInt(this.width), height: parseInt(this.height) });
      }
    }
  },
  beforeMount() {},
  mounted() {
    const node = this.getNode();
    console.log(node);
    const { title, table, items } = node.getData();
    this.title = title;
    this.subTitle = table;
    this.items = items;
    node.setSize({ width: parseInt(this.width), height: parseInt(this.height) });

    node.on('change:data', data => {
      console.log('data is changed ', data);
    });
  },
  destroyed() {}
};
</script>
