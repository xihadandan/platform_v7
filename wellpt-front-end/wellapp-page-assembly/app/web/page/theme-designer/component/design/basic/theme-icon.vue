<template>
  <div class="theme-style-panel">
    <a-page-header title="图标">
      <div slot="subTitle">平台图标</div>
      <WidgetIconLib :onlyIconClass="true" :selectable="false" scrollHeight="calc(100vh - 250px)" />
    </a-page-header>
  </div>
</template>
<style lang="less">
.theme-style-panel {
  .icon-ul {
    margin: 10px 0;
    list-style: none;
    i {
      font-size: 32px;
    }
    li {
      float: left;
      width: 100px;
      height: 100px;
      line-height: 1.5;
      text-align: center;
      list-style: none;
      cursor: pointer;
      color: #555;
      transition: color 0.3s ease-in-out, background-color 0.3s ease-in-out;
      position: relative;
      margin: 3px;
      border-radius: 4px;
      background-color: #fff;
      overflow: hidden;
      padding: 10px 0 0;
    }

    li:hover {
      outline: 1px solid #1890ff;
      color: #1890ff;
    }
    li.selected {
      background-color: #1890ff;
      outline: 1px solid #1890ff;
      color: #fff;
    }

    li .icon-class {
      display: block;
      text-align: center;
      transform: scale(0.83);
      font-family: Lucida Console, Consolas;
      white-space: nowrap;
    }
  }
}
</style>
<script type="text/babel">
import SvgIcon from '@framework/../widget/svg-icon.vue';
import { debounce, findIndex, some } from 'lodash';
import WidgetIconLib from '@pageAssembly/app/web/widget/widget-icon-lib/widget-icon-lib.vue';
export default {
  name: 'ThemeIcon',
  props: {},
  components: { WidgetIconLib },
  computed: {},
  data() {
    return { iconLibArray: [] };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.fetchIconfontMetadataAll();
  },
  methods: {
    fetchSvgIcon() {
      return new Promise(resolve => {
        SvgIcon.methods.getAllSymbols().then(items => {
          console.log('svg-icon-ids', items);
          let glyphs = [];
          if (items) {
            items.forEach(item => {
              glyphs.push({
                icon_id: item.id,
                icon_class: item.id,
                name: item.id.replace('svg-icon-', '')
              });
            });
          }
          resolve({
            id: 'svg-icon',
            name: 'svg图标',
            glyphs
          });
        });
      });
    },
    fetchIconfontMetadataAll() {
      let _this = this;
      Promise.all([
        this.fetchIconfontMetadata('/static/css/pt/iconfont/iconfont.json'), // 平台默认出场的图标库
        this.fetchIconfontMetadata('/static/css/app-iconfont/iconfont.json'), // 业务线补充的图标库
        this.fetchIconfontMetadata('/static/css/pt/ant-iconfont/iconfont.json'), // 平台默认出场的图标库
        this.fetchSvgIcon() // svg 图标
      ]).then(results => {
        results.forEach(result => {
          _this.iconLibArray.push(result);
          console.log('iconLibArray', _this.iconLibArray);
        });
      });
    },
    fetchIconfontMetadata(url) {
      let _this = this;
      // 获取业务线图标库：业务线图标库要定义不一样的图标设置，例如： iconfont": "app-iconfont", "css_prefix_text": "app-icon-",
      return new Promise((resolve, reject) => {
        $axios.get(url).then(res => {
          let glyphs = res.data.glyphs || [],
            font_family = res.data.font_family,
            css_prefix_text = res.data.css_prefix_text;
          for (let i = 0, len = glyphs.length; i < len; i++) {
            glyphs[i].icon_class = `${font_family} ${css_prefix_text}${glyphs[i].font_class}`;
            // 在已有的图标里找到一样的，就隐藏
            let isDel = some(_this.iconLibArray, item => {
              let hasIndex = findIndex(item.glyphs, { icon_class: glyphs[i].icon_class });
              return hasIndex > -1;
            });
            if (isDel) {
              glyphs[i].hide = true;
            }
          }
          // _this.iconLibArray.push(res.data);
          resolve(res.data);
        });
      });
    }
  }
};
</script>
