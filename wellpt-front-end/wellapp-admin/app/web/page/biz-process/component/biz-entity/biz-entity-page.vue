<template>
  <a-card size="small" :bordered="false">
    <template slot="title">
      {{ entity.pageName || defaultTitle }}
      <label v-if="entity.pageName && entity.pageVersion" style="color: rgba(0, 0, 0, 0.45)">v{{ entity.pageVersion }}</label>
    </template>
    <template v-if="existsEntityPage" slot="extra">
      <a-button @click="onClickViewPage" size="small" type="link">
        <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
        预览页面
      </a-button>
      <a-button @click="onClickEditPage" size="small" type="link">
        <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
        编辑页面
      </a-button>
      <a-button @click="onClickMenu('ChoosePage', '选择已存在表格')" size="small" type="link">
        <Icon type="pticon iconfont icon-luojizujian-xuanzebiaoji"></Icon>
        变更页面
      </a-button>
    </template>
    <template v-if="!existsEntityPage">
      <a-row type="flex" class="block-operations">
        <a-col>
          <a-empty :description="defaultTitle + '不存在'" class="pt-empty pt-data-empty" style="margin-top: 50px"></a-empty>
        </a-col>
        <a-col flex="160px" class="action" @click.native.stop="onClickMenu('CreatePage', '新建' + defaultTitle)">
          <a-row type="flex">
            <a-col>
              <a-icon type="html5" />
            </a-col>
            <a-col>
              <h1>新建{{ defaultTitle }}</h1>
            </a-col>
          </a-row>
        </a-col>
        <a-col flex="160px" class="action" @click.native.stop="onClickMenu('ChoosePage', '选择已存在表格')">
          <a-row type="flex">
            <a-col>
              <a-icon type="select" />
            </a-col>
            <a-col><h1>选择已存在表格</h1></a-col>
          </a-row>
        </a-col>
      </a-row>
    </template>
    <template v-else>
      <a-empty v-if="emptyDesign" style="padding-top: 240px">
        <template #description>
          暂无设计内容
          <a-button type="link" size="small" @click="openPageDesigner">前往配置</a-button>
        </template>
      </a-empty>
      <PerfectScrollbar v-show="!emptyDesign" style="height: calc(100vh - 120px)">
        <div style="width: 100%; height: 100%; display: block; position: fixed; z-index: 1"></div>
        <iframe
          :key="pageKey"
          id="pagePreviewIframe"
          :src="'/webpage/' + entity.pageId + '/' + entity.pageUuid + '#iframe'"
          :style="{ minHeight: 'calc(100vh - 130px)', border: 'none', width: '100%' }"
        ></iframe>
      </PerfectScrollbar>
    </template>
    <a-modal :title="choosePageTitle" :visible="choosePageVisible" @ok="handleChoosePageOk" @cancel="e => (choosePageVisible = false)">
      <a-select v-model="choosePageUuid" show-search @search="handlePageSearch" style="width: 100%" :filter-option="filterSelectOption">
        <a-select-option v-for="d in pageOptions" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-modal>
  </a-card>
</template>

<script>
export default {
  props: {
    entity: Object,
    defaultTitle: {
      type: String,
      default: '业务主体信息'
    }
  },
  inject: ['assemble', 'filterSelectOption', 'pageContext'],
  data() {
    return {
      pageKey: 'biz_process_entity_page_preview',
      emptyDesign: false,
      pageOptions: [],
      choosePageUuid: '',
      choosePageTitle: '',
      choosePageVisible: false
    };
  },
  computed: {
    existsEntityPage() {
      return this.entity.pageUuid;
    }
  },
  mounted() {
    window.addEventListener('message', this.frameListener, false);
  },
  beforeDestroy() {
    window.removeEventListener('message', this.frameListener);
  },
  methods: {
    frameListener(event) {
      let _this = this;
      if (event.origin !== location.origin) {
        return;
      }
      if (event.data == 'vPage Rendered') {
        let iframeEle = document.querySelector('#pagePreviewIframe');
        _this.emptyDesign = iframeEle.contentWindow.$app.$refs.page.items.length == 0;

        // 配置监听方法的属性值
        // 定义一个监听器
        let observer = new MutationObserver(mutations => {
          for (let item of mutations) {
            if (item.type === 'childList') {
              const scrollHeight = iframeEle.contentWindow.document.body.scrollHeight;
              iframeEle.style.height = `${scrollHeight}px`;
              break;
            }
          }
        });

        iframeEle.contentWindow.addEventListener('DOMContentLoaded', function (e) {
          try {
            if (iframeEle.contentWindow.document.readyState === 'interactive') {
              iframeEle.style.height = '0px';
              setTimeout(function () {
                iframeEle.style.height = `${iframeEle.contentWindow.document.body.scrollHeight}px`;
              }, 100);
              observer.observe(iframeEle.contentWindow.document, {
                attributes: true,
                childList: true,
                subtree: true
              });
            }
          } catch (err) {}
        });
      }
    },
    onClickMenu(key, title) {
      if (key == 'CreatePage') {
        this.onClickAddPage();
      } else if (key == 'ChoosePage') {
        this.choosePageTitle = title;
        this.choosePageVisible = true;
        if (this.pageOptions.length == 0) {
          this.handlePageSearch();
        }
      }
    },
    onClickAddPage() {
      let _this = this;
      _this.pageContext.handleCrossTabEvent(`page:design:create`, pageDefinition => {
        if (!_this.entity.pageUuid) {
          _this.entity.pageUuid = pageDefinition.uuid;
          _this.entity.pageId = pageDefinition.id;
          _this.entity.pageName = pageDefinition.name;
          _this.entity.pageVersion = pageDefinition.version || '1.0';
          _this.assemble.save();
        }
      });
      window.open(`/page-designer/index`, '_blank');
    },
    onClickEditPage() {
      let _this = this;
      this.pageContext.handleCrossTabEvent(`page:design:change:${this.entity.pageUuid}`, pageDefinition => {
        _this.entity.pageUuid = pageDefinition.uuid;
        _this.entity.pageId = pageDefinition.id;
        _this.entity.pageName = pageDefinition.name;
        _this.entity.pageVersion = pageDefinition.version;
        _this.assemble.save();
        _this.refresh(true);
      });
      window.open(`/page-designer/index?uuid=${this.entity.pageUuid}`, '_blank');
    },
    refresh(force) {
      if (force) {
        this.pageKey = 'biz_process_entity_page_preview_' + new Date().getTime();
      }
    },
    handlePageSearch(value = '') {
      const _this = this;
      _this.$axios
        .post('/common/select2/query', {
          serviceName: 'appPageDefinitionMgr',
          queryMethod: 'loadSelectData',
          searchValue: value,
          pageSize: 10,
          pageNo: 1
        })
        .then(({ data }) => {
          if (data.results) {
            _this.pageOptions = data.results;
          }
        });
    },
    handleChoosePageOk() {
      const _this = this;
      if (!_this.choosePageUuid) {
        _this.$message.error('请选择' + this.defaultTitle + '页面！');
        return;
      }
      _this.handlePageChange(_this.choosePageUuid).then(pageDefinition => {
        if (pageDefinition) {
          _this.entity.pageUuid = pageDefinition.uuid;
          _this.entity.pageId = pageDefinition.id;
          _this.entity.pageName = pageDefinition.name;
          _this.entity.pageVersion = pageDefinition.version;
          _this.assemble.save();
          _this.choosePageVisible = false;
        }
      });
    },
    handlePageChange(pageUuid) {
      return $axios
        .post('/json/data/services', {
          serviceName: 'appPageDefinitionMgr',
          methodName: 'getBean',
          args: JSON.stringify([pageUuid])
        })
        .then(({ data: result }) => {
          if (result.data) {
            return result.data;
          }
        });
    },
    onClickViewPage() {
      window.open(`/webpage/${this.entity.pageId}/${this.entity.pageUuid}`, '_blank');
    },
    openPageDesigner() {
      this.onClickEditPage();
    }
  }
};
</script>

<style></style>
