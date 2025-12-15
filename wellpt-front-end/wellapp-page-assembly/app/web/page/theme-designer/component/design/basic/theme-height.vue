<template>
  <div
    class="theme-style-panel"
    :style="{
      '--preview-height-size': height
    }"
  >
    <a-page-header title="高度">
      <div slot="subTitle">为统一设计到开发的布局语言, 减少还原损耗, 高度采用布局一致的常用模度8倍数原则, 具备动态的韵律感</div>

      <a-card :class="[selected ? 'border-selected' : '']" @click="onSelect(config.derive)">
        <template slot="title">
          常用高度
          <Modal title="添加高度" :ok="e => editVar(e, config)" :cancel="onCancelEditVar">
            <a-button size="small" type="link" icon="plus" @click="setVarCode(config, config.code)" />
            <template slot="content">
              <EditVarPanel :item="varItem" :prefix="config.code" />
            </template>
          </Modal>
        </template>
        <a-row class="style-preview-row" type="flex">
          <a-col flex="1">
            <div class="preview-height-col">
              <div>
                <div
                  :style="{
                    height: 'var(--preview-height-size)'
                  }"
                >
                  <div>
                    <div :style="{ 'line-height': 'var(--preview-height-size)' }">
                      <span>{{ height }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </a-col>
        </a-row>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import varMixin from './varMixin.js';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
export default {
  name: 'ThemeHeight',
  mixins: [varMixin],
  props: { config: Object },
  components: { Modal },
  computed: {},
  data() {
    return { height: this.config.derive[0].value, selected: false };
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
