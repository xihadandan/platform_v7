<template>
  <div
    class="theme-style-panel"
    :style="{
      '--preview-padding-space-size': padding,
      '--preview-margin-space-size': margin
    }"
  >
    <a-page-header title="间距">
      <div slot="subTitle">
        间距 网格是构成页面栅格系统的最小单位。尽量保持单位是偶数, 这样在页面放大或者放大或者缩小时还能保持清晰。 Well UI 网格的基数为 4,
        符合偶数的思路同时能够匹配多数主流的显示设备。 间距规则以4的倍数为原则, 推荐使用间距: 4、8、12、16、20、24、32
      </div>

      <a-card
        v-for="(key, k) in ['paddingConfig', 'marginConfig']"
        :key="'spaceStyle-' + key + k"
        :class="[selectKey == key ? 'border-selected' : '']"
        @click="onSelect(config[key].derive, key)"
      >
        <template slot="title">
          {{ key == 'paddingConfig' ? '内边距' : '外边距' }}
          <Modal
            :title="'添加' + (key == 'paddingConfig' ? '内边距' : '外边距')"
            :ok="e => editVar(e, config[key])"
            :cancel="onCancelEditVar"
          >
            <a-button size="small" type="link" icon="plus" @click="setVarCode(config[key], config[key].code)" />
            <template slot="content">
              <EditVarPanel :item="varItem" :prefix="config[key].code" />
            </template>
          </Modal>
        </template>
        <a-row class="style-preview-row" type="flex">
          <a-col v-if="key === 'paddingConfig'">
            <div
              :style="{
                backgroundColor: '#e9e9e9',
                padding: 'var(--preview-padding-space-size)',
                outline: '1px solid #c6c6c6 '
              }"
            >
              <div style="width: 400px; height: 100px; background-color: #fff; text-align: center; line-height: 100px">内容区</div>
            </div>
          </a-col>
          <a-col v-else>
            <div
              :style="{
                backgroundColor: '#e9e9e9',
                padding: 'var(--preview-margin-space-size)'
              }"
            >
              <div style="background-color: #fff">
                <div
                  :style="{
                    outline: '1px dashed #c8c8c8',
                    width: '400px',
                    height: '100px',
                    'background-colo': '#fff',
                    'text-align': 'center',
                    'line-height': '100px'
                  }"
                >
                  内容区
                </div>
                <div
                  :style="{
                    'margin-top': '20px',
                    outline: '1px dashed #c8c8c8',
                    width: '400px',
                    height: '100px',
                    'background-color': '#fff',
                    'text-align': 'center',
                    'line-height': '100px'
                  }"
                >
                  内容区
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
  name: 'ThemeSpace',
  props: { config: Object },
  mixins: [varMixin],
  components: { Modal },
  computed: {},
  data() {
    return { selectKey: undefined, margin: this.config.marginConfig.derive[0].value, padding: this.config.paddingConfig.derive[0].value };
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
