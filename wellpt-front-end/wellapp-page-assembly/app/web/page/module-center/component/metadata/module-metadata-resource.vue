<template>
  <a-card title="元数据" class="module-metadata-resource-card pt-card" :bordered="false">
    <a-menu class="menu" mode="vertical" @click="onMenuClick">
      <a-menu-item class="menu-overview flex" key="1" :style="setColorParams('--w-primary-color')">
        <div class="f_s_0 icon-div">
          <Icon type="pticon iconfont icon-ptkj-qiehuanshitu"></Icon>
        </div>
        <div class="f_g_1 title">概况</div>
        <div v-if="false"></div>
      </a-menu-item>
      <a-menu-item v-for="item in menus" :key="item.key" class="flex" :style="setColorParams(item.color)">
        <div class="f_s_0 icon-div">
          <Icon :type="item.icon"></Icon>
        </div>
        <div class="f_g_1 title">{{ item.title }}</div>
        <div v-if="item.count !== undefined" class="f_s_0 count-div">{{ item.count }}</div>
      </a-menu-item>
    </a-menu>
  </a-card>
</template>

<script>
import { upperFirst } from 'lodash';
export default {
  name: 'ModuleMetadataResource',
  inject: ['currentModule', 'pageContext'],
  data() {
    return {
      menus: [
        {
          title: '数据字典',
          key: 'dataDictionary',
          icon: 'pticon iconfont icon-wsbs-woyaoxue',
          compName: 'dataDictionary',
          count: 0,
          color: '--w-warning-color'
        },
        {
          title: '消息格式',
          key: 'messageTemplate',
          compName: 'MetadataPreview',
          icon: 'pticon iconfont icon-xmch-wodexiaoxi',
          count: 0,
          color: '--w-success-color'
        },
        {
          title: '计时规则',
          key: 'timerConfig',
          compName: 'MetadataPreview',
          icon: 'pticon iconfont icon-naozhong-01',
          count: 0,
          color: '--w-warning-color'
        },
        {
          title: '打印模板',
          key: 'printTemplate',
          compName: 'MetadataPreview',
          icon: 'pticon iconfont icon-wsbs-dayin',
          count: 0,
          color: '--w-warning-color'
        },
        {
          title: '流水号规则',
          key: 'serialNumber',
          compName: 'MetadataPreview',
          count: 0,
          icon: 'pticon iconfont icon-wsbs-yanzhengma',
          color: '--w-success-color'
        }
      ]
    };
  },
  created() {
    this.loadCount();
  },
  methods: {
    setColorParams(color) {
      return {
        '--metadata-icon-color': `var(${color})`,
        '--metadata-icon-bg-color': `var(${color}-1)`,
        '--metadata-icon-color-selected': `#ffffff`,
        '--metadata-icon-bg-color-selected': `var(${color})`,
        '--metadata-item-bg-selected': `var(${color}-1)`,
        '--metadata-item-border-color': `var(${color}-2)`,
        '--metadata-item-border-color-selected': `var(${color})`,
        '--metadata-item-count-color': `var(${color})`
      };
    },
    loadCount() {
      // 字典数量
      this.pageContext.handleEvent('dataDictionary:loadCount', () => {
        $axios.get(`/proxy/api/datadict/count?moduleId=${this.currentModule.id}`).then(({ data }) => {
          this.menus.find(item => item.key == 'dataDictionary').count = data.data;
        });
      });
      this.pageContext.emitEvent('dataDictionary:loadCount');
    },
    onMenuClick({ key }) {
      if (key != 1) {
        let menu = this.menus.find(item => item.key == key);
        this.$emit('select', upperFirst(menu.compName), menu);
      }
    }
  }
};
</script>

<style lang="less">
.module-metadata-resource-card {
  .menu {
    .ant-menu-item {
      padding: 0;
      cursor: pointer;
      border: 1px solid var(--metadata-item-border-color);
      height: 48px;
      line-height: 48px;
      margin-bottom: 12px;
      &.ant-menu-item-selected {
        background-color: var(--metadata-item-bg-selected);
        --metadata-icon-color: var(--metadata-icon-color-selected) !important;
        --metadata-icon-bg-color: var(--metadata-icon-bg-color-selected) !important;
        --metadata-item-border-color: var(--metadata-item-border-color-selected) !important;
      }
      &:hover {
        background-color: var(--metadata-item-bg-selected) !important;
        border-bottom-color: var(--metadata-item-border-color);
      }

      .icon-div {
        > i {
          font-size: 24px;
        }
        color: var(--metadata-icon-color);
        background-color: var(--metadata-icon-bg-color);
        width: 48px;
        text-align: center;
        font-weight: normal;
      }
      .title {
        color: var(--w-text-color-darker);
        font-size: var(--w-font-size-base);
        padding: 0 12px;
      }
      .count-div {
        font-size: 20px;
        color: var(--metadata-item-count-color);
        font-weight: bold;
        width: 48px;
        text-align: center;
        border-left: 1px solid var(--metadata-item-border-color);
      }
    }
  }
}
</style>
