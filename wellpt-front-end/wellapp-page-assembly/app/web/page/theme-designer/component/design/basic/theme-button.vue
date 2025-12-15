<template>
  <div class="theme-style-panel theme-btn-panel" :style="basicStyleCssVars">
    <a-page-header title="按钮" class="btn_has_space btn_has_b_space">
      <a-card title="多个尺寸和间距">
        <a-button type="primary" size="large" :auto-insert-space-in-button="false">按钮</a-button>
        <a-button type="primary" icon="plus" size="large" :auto-insert-space-in-button="false">按钮</a-button>
        <a-button type="primary" icon="icon" size="large" :auto-insert-space-in-button="false">按钮</a-button>
        <a-button type="primary" :auto-insert-space-in-button="false">按钮</a-button>
        <a-button type="primary" icon="plus" :auto-insert-space-in-button="false">按钮</a-button>
        <a-button type="primary" size="small" :auto-insert-space-in-button="false">按钮</a-button>
        <a-button type="primary" icon="plus" size="small" :auto-insert-space-in-button="false">按钮</a-button>
      </a-card>
      <a-card title="主按钮(type='primary')">
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button type="primary">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button type="primary" class="theme-btn-hover">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button type="primary" class="theme-btn-active">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button type="primary" disabled>按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button type="primary" loading>按钮</a-button>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card title="次按钮(type='default'或不填)">
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button>按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button class="theme-btn-hover">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button class="theme-btn-active">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button disabled>按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button loading>按钮</a-button>
          </a-descriptions-item>
        </a-descriptions>
        <div>危险按钮（class="ant-btn-dangerous"）</div>
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button class="ant-btn-dangerous">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button class="theme-btn-hover ant-btn-dangerous">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button class="theme-btn-active ant-btn-dangerous">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button disabled class="ant-btn-dangerous">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button loading class="ant-btn-dangerous">按钮</a-button>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card title="链接按钮(type='link')">
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button type="link">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button type="link" class="theme-btn-hover">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button type="link" class="theme-btn-active">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button type="link" disabled>按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button loading type="link">按钮</a-button>
          </a-descriptions-item>
        </a-descriptions>
        <div>危险按钮（class="ant-btn-dangerous"）</div>
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button class="ant-btn-dangerous" type="link">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button class="theme-btn-hover ant-btn-dangerous" type="link">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button class="theme-btn-active ant-btn-dangerous" type="link">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button disabled class="ant-btn-dangerous" type="link">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button loading class="ant-btn-dangerous" type="link">按钮</a-button>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card title="线性按钮(type='line')">
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button type="line">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button type="line" class="theme-btn-hover">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button type="line" class="theme-btn-active">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button type="line" disabled>按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button type="line" loading>按钮</a-button>
          </a-descriptions-item>
        </a-descriptions>
        <div>危险按钮（class="ant-btn-dangerous"）</div>
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button class="ant-btn-dangerous" type="line">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button class="theme-btn-hover ant-btn-dangerous" type="line">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button class="theme-btn-active ant-btn-dangerous" type="line">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button disabled class="ant-btn-dangerous" type="line">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button loading class="ant-btn-dangerous" type="line">按钮</a-button>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card title="危险按钮(type='danger')">
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button type="danger">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button type="danger" class="theme-btn-hover">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button type="danger" class="theme-btn-active">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button type="danger" disabled>按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button type="danger" loading>按钮</a-button>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card title="图标+文本按钮(type='text')">
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button type="text" icon="plus">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button type="text" class="theme-btn-hover" icon="plus">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button type="text" icon="plus" class="theme-btn-active">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button type="text" icon="plus" disabled>按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button type="text" icon="plus" loading>按钮</a-button>
          </a-descriptions-item>
        </a-descriptions>
        <div>危险按钮（class="ant-btn-dangerous"）</div>
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button class="ant-btn-dangerous" icon="plus" type="text">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button class="theme-btn-hover ant-btn-dangerous" icon="plus" type="text">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button class="theme-btn-active ant-btn-dangerous" icon="plus" type="text">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button disabled class="ant-btn-dangerous" icon="plus" type="text">按钮</a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button loading class="ant-btn-dangerous" icon="plus" type="text">按钮</a-button>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card title="图标按钮(type='icon')，如果内部只有一个图标的按钮要呈正方形（class='icon-only'）">
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button type="icon" class="icon-only"><Icon type="pticon iconfont icon-ptkj-jiahao"></Icon></a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button type="icon" class="theme-btn-hover icon-only"><Icon type="pticon iconfont icon-ptkj-jiahao"></Icon></a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button type="icon" icon="plus" class="theme-btn-active"></a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button type="icon" icon="plus" disabled></a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button type="icon" icon="plus" loading></a-button>
          </a-descriptions-item>
        </a-descriptions>
        <div>危险按钮（class="ant-btn-dangerous"）</div>
        <a-descriptions layout="vertical" :colon="false" :column="5">
          <a-descriptions-item label="默认">
            <a-button class="ant-btn-dangerous" icon="plus" type="icon"></a-button>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-button class="theme-btn-hover ant-btn-dangerous" icon="plus" type="icon"></a-button>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a-button class="theme-btn-active ant-btn-dangerous" icon="plus" type="icon"></a-button>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-button disabled class="ant-btn-dangerous" icon="plus" type="icon"></a-button>
          </a-descriptions-item>
          <a-descriptions-item label="加载">
            <a-button loading class="ant-btn-dangerous" icon="plus" type="icon"></a-button>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card title="按钮组合（a-button-group）">
        <a-button-group>
          <a-button type="primary">L</a-button>
          <a-button>M</a-button>
          <a-button>M</a-button>
          <a-button type="dashed">R</a-button>
        </a-button-group>
      </a-card>
      <a-card title="幽灵按钮" :style="{ background: 'var(--w-primary-color)' }">
        <a-button type="primary" ghost>Primary</a-button>
        <a-button ghost>Default</a-button>
        <a-button type="link" ghost>Link</a-button>
        <a-button type="text" ghost>text</a-button>
        <a-button type="icon" class="icon-only" ghost><Icon type="pticon iconfont icon-ptkj-jiahao"></Icon></a-button>
        <a-button type="line" ghost>line</a-button>
        <br />
        <a-button type="primary" ghost loading>Primary</a-button>
        <a-button ghost loading>Default</a-button>
        <a-button ghost disabled>disabled</a-button>
        <br />
        <a-button type="danger" ghost>Danger</a-button>
        <a-button class="ant-btn-dangerous" ghost>Danger</a-button>
        <a-button type="link" class="ant-btn-dangerous" ghost>Link</a-button>
        <a-button type="icon" class="ant-btn-dangerous icon-only" ghost><Icon type="pticon iconfont icon-ptkj-jiahao"></Icon></a-button>
        <a-button type="line" class="ant-btn-dangerous" ghost>line</a-button>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less">
.theme-btn-panel {
  .ant-btn {
    // margin-right: 8px;
    margin-bottom: 8px;
  }
  .ant-btn-group {
    .ant-btn {
      margin-right: 0px;
      margin-bottom: 0px;
    }
  }
}
</style>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import varMixin from './varMixin.js';
export default {
  name: 'ThemeButton',
  props: { config: Object },
  mixins: [varMixin, themePropMixin],
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
  methods: {}
};
</script>
