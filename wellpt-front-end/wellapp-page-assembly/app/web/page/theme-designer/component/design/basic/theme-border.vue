<template>
  <div class="theme-style-panel">
    <a-page-header title="边框">
      <div slot="subTitle">边框不随主题变化而变化，默认设置为常量</div>

      <a-card
        :class="[selectKey == key ? 'border-selected' : '']"
        v-for="(key, k) in ['borderStyle', 'borderWidth']"
        :key="'borderStyle-' + key + k"
        @click="onSelect(config[key], key)"
      >
        <template slot="title">
          {{ key == 'borderStyle' ? '边框样式' : '边框尺寸' }}
          <Modal
            :title="'添加' + (key == 'borderStyle' ? '边框样式' : '边框尺寸')"
            :ok="e => editVar(e, config[key])"
            :cancel="onCancelEditVar"
          >
            <a-button size="small" type="link" icon="plus" @click="setVarCode(config[key], config[key].code)" />
            <template slot="content">
              <EditVarPanel :item="varItem" :prefix="config[key].code" />
            </template>
          </Modal>
        </template>
        <a-row class="style-preview-row" type="flex" v-for="(bor, i) in config[key].derive" :key="'borderStylePreview_' + key + i">
          <a-col flex="200px">
            <div>
              <div>{{ bor.code }}</div>
            </div>
          </a-col>
          <a-col flex="150px" style="padding-right: 20px">
            {{ bor.value }}
          </a-col>
          <a-col flex="200px">
            <div
              :style="{
                'border-bottom-style': key == 'borderWidth' ? 'solid' : bor.value,
                width: '100%',
                height: '0px',
                'border-bottom-width':
                  key === 'borderWidth'
                    ? bor.value.indexOf('px') > -1
                      ? bor.value
                      : bor.value + 'px'
                    : bor.value == 'double'
                    ? '3px'
                    : '1px'
              }"
            ></div>
          </a-col>
          <a-col flex="100px">
            <a-button
              type="link"
              icon="delete"
              v-if="(key === 'borderStyle' && i > 0) || (key == 'borderWidth' && i > 3)"
              @click="deleteDerive(key === 'borderStyle' ? config.borderStyle : config.borderWidth, i)"
            ></a-button>
          </a-col>
        </a-row>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import varMixin from './varMixin.js';
export default {
  name: 'ThemeBorder',
  props: { config: Object },
  mixins: [varMixin],
  components: { Modal },
  computed: {},
  data() {
    return {
      selectKey: undefined
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onSelect(e, key) {
      this.$emit('select', e);
      this.selectKey = key;
    },
    deleteDerive(tar, index) {
      if (index == undefined) {
        tar.derive = [];
      } else {
        tar.derive.splice(index, 1);
      }
    }
  }
};
</script>
