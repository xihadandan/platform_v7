<template>
  <div class="theme-style-panel">
    <a-page-header title="圆角">
      <div slot="subTitle">为了统一使用规范，定义了三种圆角规格，应用在具体的组件中，圆角变量遵循偶数原则</div>

      <a-card :class="[selected ? 'border-selected' : '']" @click="onSelect(config.derive)">
        <template slot="title">
          圆角尺寸

          <Modal title="添加圆角尺寸" :ok="e => editVar(e, config)" :cancel="onCancelEditVar">
            <a-button size="small" type="link" icon="plus" @click="setVarCode(config, config.code)" />
            <template slot="content">
              <EditVarPanel :item="varItem" :prefix="config.code" />
            </template>
          </Modal>
        </template>
        <template v-for="(item, i) in config.derive">
          <a-row class="style-preview-row" type="flex" :key="'borderRadiusStylePreview_' + i">
            <a-col flex="200px">
              <div>
                <div>{{ item.code }}</div>
              </div>
            </a-col>
            <a-col flex="100px">{{ item.value }}</a-col>
            <a-col flex="200px">
              <div style="width: 30px; height: 30px; overflow: hidden">
                <div
                  :style="{
                    [item.code]: item.value,
                    height: '80px',
                    width: '80px',
                    border: '1px solid #000',
                    'border-radius': 'var(' + item.code + ')'
                  }"
                ></div>
              </div>
            </a-col>
          </a-row>
        </template>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import varMixin from './varMixin.js';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'ThemeRadius',
  props: { config: Object },
  mixins: [varMixin],
  components: { Modal },
  computed: {},
  data() {
    return { selected: false };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onSelect(e, key) {
      this.$emit('select', e);
      this.selected = true;
    }
  }
};
</script>
