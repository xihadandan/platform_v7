<template>
  <div class="theme-style-panel">
    <a-page-header title="阴影">
      <div slot="subTitle">平台提供了阴影变量用于不同的场景</div>

      <a-card
        :class="[selectKey == key ? 'border-selected' : '']"
        v-for="(key, k) in ['levelOne', 'levelTwo', 'levelThree']"
        :key="'levelStyle-' + key + k"
        @click="onSelect(config[key].derive, key)"
      >
        <template slot="title">
          {{ titleMap[key] }}
        </template>
        <a-row class="style-preview-row" type="flex" style="justify-content: space-around">
          <a-col
            v-for="(item, i) in config[key].derive"
            :key="'levelStylePreview_' + key + i"
            flex="150px"
            :style="{ boxShadow: item.value, height: '60px' }"
          >
            <div style="line-height: 25px; text-align: center">{{ item.code }}</div>
            <div style="line-height: 25px; text-align: center">{{ item.value }}</div>
          </a-col>
        </a-row>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ThemeShadow',
  props: { config: Object },
  components: {},
  computed: {},
  data() {
    return { selectKey: undefined, titleMap: { levelOne: '一级阴影', levelTwo: '二级阴影', levelThree: '三级阴影' } };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onSelect(e, key) {
      this.$emit('select', e);
      this.selectKey = key;
    }
  }
};
</script>
