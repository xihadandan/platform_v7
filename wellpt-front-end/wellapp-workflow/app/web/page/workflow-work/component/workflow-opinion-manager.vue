<template>
  <a-config-provider :locale="locale">
    <a-modal
      :title="$t('WorkflowWork.opinionManager.modalTitle', '意见管理')"
      width="900px"
      dialogClass="opinion-manager pt-modal"
      :visible="visible"
      :maskClosable="false"
      @ok="onOk"
      @cancel="onCancel"
    >
      <a-tabs class="opinion-tabs pt-tabs" default-active-key="1">
        <a-tab-pane class="recent-tab-panel" key="1" :tab="$t('WorkflowWork.opinionManager.recentTabTitle', '最近使用')">
          <a-alert
            v-if="showRecentOpinionTip"
            :message="$t('WorkflowWork.opinionManager.message.recentOpinionTip', '自动保存您最近签署的意见，可以删除。')"
            type="info"
            show-icon
            closable
          />
          <Scroll :style="{ height: '349px', marginRight: 'calc(0px - var(--w-padding-md))', paddingRight: 'var(--w-padding-md)' }">
            <a-list :data-source="recents">
              <a-list-item
                slot="renderItem"
                slot-scope="item, index"
                :title="item.content"
                @click="onOpinionItemClick($event, item)"
                @mouseenter="onOpinionItemMouseEnter($event, item)"
                @mouseleave="onOpinionItemMouseLeave($event, item)"
              >
                <span>{{ item.content }}</span>
                <a-button
                  v-show="item.showRemoveBtn"
                  type="link"
                  icon="close"
                  size="small"
                  @click.stop="onRemoveRecentItemClick($event, item, index)"
                />
              </a-list-item>
            </a-list>
          </Scroll>
        </a-tab-pane>
        <a-tab-pane class="common-tab-panel" key="2" :tab="$t('WorkflowWork.opinionManager.commonTabTitle', '常用意见')">
          <a-row>
            <a-col span="12">
              <div class="flex f_nowrap f_x_s" style="margin-bottom: 12px">
                <div class="common-tab-title">{{ $t('WorkflowWork.opinionManager.commonTabSubTitle', '从意见库选择') }}</div>
                <div>
                  <a-input-search
                    class="public-opinion-search"
                    v-model="publicOpinionQueryText"
                    :placeholder="$t('WorkflowWork.searchPlaceholder', '搜索')"
                    @search="onPublicOpinionSearch"
                  />
                </div>
              </div>
              <Scroll
                :style="{
                  height: '308px',
                  marginBottom: '12px',
                  marginRight: 'calc(0px - var(--w-padding-md))',
                  paddingRight: 'var(--w-padding-md)'
                }"
              >
                <a-list class="public-opinion-list" :data-source="publicOpinions">
                  <a-list-item
                    slot="renderItem"
                    slot-scope="item, index"
                    :title="item.content"
                    @click="onPublicOpinionItemClick($event, item)"
                  >
                    <span :class="{ disabled: hasCommonOpinion(item) }">{{ item.content }}</span>
                  </a-list-item>
                </a-list>
              </Scroll>
              <div class="need-input">
                {{ $t('WorkflowWork.opinionManager.message.inputOpinionYourself', '如意见库没有合适的，您可以') }}
                <Modal
                  :title="$t('WorkflowWork.opinionManager.operation.manualInput', '手动输入')"
                  :width="360"
                  :okText="$t('WorkflowWork.opinionManager.operation.add', '添加')"
                  :ok="onAddCommonOpinionClick"
                  :zIndex="1001"
                  :centered="true"
                  :mask="true"
                >
                  <div slot="content">
                    <a-input
                      v-model="userOpinionText"
                      type="textarea"
                      rows="5"
                      :placeholder="$t('WorkflowWork.opinionManager.inputCommonOpinionPlaceholder', '输入常用意见')"
                    />
                  </div>
                  <a-button type="link" size="small" style="--w-button-padding-lr: 0">
                    {{ $t('WorkflowWork.opinionManager.operation.manualInput', '手动输入') }}
                    <Icon type="pticon iconfont icon-ptkj-zhuce-toubu"></Icon>
                  </a-button>
                </Modal>
              </div>
            </a-col>
            <a-col span="12">
              <div class="common-tab-title">{{ $t('WorkflowWork.opinionManager.myCommonTabSubTitle', '我的常用意见') }}</div>
              <Scroll
                :style="{
                  height: '356px',
                  marginRight: 'calc(0px - var(--w-padding-md))',
                  paddingRight: 'var(--w-padding-md)'
                }"
              >
                <a-list class="my-common-opinion-list" :data-source="commonOpinionCategory.opinions">
                  <a-list-item
                    slot="renderItem"
                    slot-scope="item, index"
                    :title="item.content"
                    @click="onOpinionItemClick($event, item)"
                    @mouseenter="onOpinionItemMouseEnter($event, item)"
                    @mouseleave="onOpinionItemMouseLeave($event, item)"
                  >
                    <span v-if="item.showEditInput">
                      <a-input type="text" v-model="item.content" focus @blur="onCommonOpinionInputBlur($event, item)"></a-input>
                    </span>
                    <span v-else>{{ item.content }}</span>
                    <a-space>
                      <a-button-group>
                        <a-button
                          v-show="item.showRemoveBtn"
                          type="link"
                          size="small"
                          :title="$t('WorkflowWork.opinionManager.operation.edit', '编辑')"
                          @click.stop="onEditCommonItemClick($event, item, index)"
                        >
                          <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
                        </a-button>
                        <a-button
                          v-show="item.showRemoveBtn"
                          type="link"
                          size="small"
                          :title="$t('WorkflowWork.opinionManager.operation.moveUp', '上移')"
                          @click.stop="onUpCommonItemClick($event, item, index)"
                        >
                          <Icon type="pticon iconfont icon-ptkj-shangyi"></Icon>
                        </a-button>
                        <a-button
                          v-show="item.showRemoveBtn"
                          type="link"
                          size="small"
                          :title="$t('WorkflowWork.opinionManager.operation.moveDown', '下移')"
                          @click.stop="onDownCommonItemClick($event, item, index)"
                        >
                          <Icon type="pticon iconfont icon-ptkj-xiayi"></Icon>
                        </a-button>
                        <a-button
                          v-show="item.showRemoveBtn"
                          type="link"
                          icon="close"
                          size="small"
                          :title="$t('WorkflowWork.opinionManager.operation.delete', '删除')"
                          @click.stop="onRemoveCommonItemClick($event, item, index)"
                        />
                      </a-button-group>
                    </a-space>
                  </a-list-item>
                </a-list>
              </Scroll>
            </a-col>
          </a-row>
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </a-config-provider>
</template>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { trim as stringTrim, isEmpty, isFunction, each as forEach } from 'lodash';
export default {
  name: 'WorkflowOpinionManager',
  inject: ['locale'],
  props: {
    recents: Array,
    commonOpinionCategory: {
      type: Object,
      required: true,
      default: () => {
        return { opinions: [] };
      }
    },
    publics: Array
  },
  components: { Modal },
  data() {
    return {
      visible: false,
      showRecentOpinionTip: true,
      userOpinionText: '',
      deletedCommonOpinions: [], // 删除的个人常用意见
      commonOpinionsJson: JSON.stringify(this.commonOpinionCategory.opinions),
      publicOpinionQueryText: '',
      publicOpinions: this.publics,
      publicOpinionsJson: JSON.stringify(this.publics)
    };
  },
  created() {},
  methods: {
    open() {
      this.visible = true;
    },
    close() {
      this.visible = false;
    },
    onOk() {
      const _self = this;
      $axios
        .post('/api/workflow/opinion/saveFlowOpinionCategories', {
          opinionCategories: [_self.commonOpinionCategory],
          deletedCategoryUuids: []
        })
        .then(result => {
          console.log(result);
          _self.close();
        });
    },
    onCancel() {
      const _self = this;
      // if (_self.deletedCommonOpinions.length > 0) {
      // 还原个人常用意见
      _self.commonOpinionCategory.opinions = JSON.parse(_self.commonOpinionsJson);
      // }
      _self.close();
    },
    onOpinionItemClick() {},
    onOpinionItemMouseEnter(event, item) {
      this.$set(item, 'showRemoveBtn', true);
    },
    onOpinionItemMouseLeave(event, item) {
      this.$set(item, 'showRemoveBtn', false);
    },
    // 删除最近使用意见
    onRemoveRecentItemClick(event, item, index) {
      const _self = this;
      $axios.delete('/proxy/api/workflow/opinion/deleteRecentOpinion', { params: { content: item.content } }).then(result => {
        if (result.data.code == 0) {
          _self.$message.success(_self.$t('WorkflowWork.opinionManager.message.deleteSuccess', '删除成功'));
          _self.$emit('recentOpinionDeleted', item);
        } else {
          console.error(result);
        }
      });
    },
    // 搜索公共意见库
    onPublicOpinionSearch: function () {
      const _self = this;
      if (isEmpty(_self.publicOpinionQueryText)) {
        _self.publicOpinions = JSON.parse(_self.publicOpinionsJson);
        return;
      }
      let list = [];
      forEach(_self.publicOpinions, function (item) {
        if (item.content && item.content.indexOf(stringTrim(_self.publicOpinionQueryText)) != -1) {
          list.push(item);
        }
      });
      _self.publicOpinions = list;
    },
    // 意见库点击处理
    onPublicOpinionItemClick: function (event, item) {
      const _self = this;
      if (!_self.hasCommonOpinion(item)) {
        _self.addCommonOpinion({
          code: item.code,
          content: item.content
        });
      }
    },
    // 是否存在指定的个人常用意见
    hasCommonOpinion: function (item) {
      const _self = this;
      let opinions = _self.commonOpinionCategory.opinions;
      for (let index = 0; index < opinions.length; index++) {
        if (opinions[index].code == item.code) {
          return true;
        }
      }
      return false;
    },
    addCommonOpinion: function (item) {
      this.commonOpinionCategory.opinions.push(item);
    },
    // 添加个人常用意见
    onAddCommonOpinionClick: function () {
      const _self = this;
      let commonOpinionText = _self.userOpinionText;
      _self.commonOpinionCategory.opinions.push({ code: commonOpinionText, content: commonOpinionText });
      _self.userOpinionText = '';
    },
    // 编辑个人常用意见
    onEditCommonItemClick: function (event, item) {
      this.$set(item, 'showEditInput', true);
    },
    // 保存个人常用意见
    onCommonOpinionInputBlur: function (event, item) {
      this.$set(item, 'showEditInput', false);
    },
    // 上移个人常用意见
    onUpCommonItemClick: function (event, item) {
      const _self = this;
      let opinions = _self.commonOpinionCategory.opinions;
      // 通过新的数组对象避免卡顿
      let list = [].concat(opinions);
      for (let index = 0; index < list.length; index++) {
        if (list[index] == item) {
          if (index > 0) {
            let tmp = list[index - 1];
            list[index] = tmp;
            list[index - 1] = item;
          }
          break;
        }
      }
      _self.commonOpinionCategory.opinions = list;
    },
    // 下移个人常用意见
    onDownCommonItemClick: function (event, item) {
      const _self = this;
      let opinions = _self.commonOpinionCategory.opinions;
      // 通过新的数组对象避免卡顿
      let list = [].concat(opinions);
      for (let index = 0; index < list.length; index++) {
        if (list[index] == item) {
          if (index < list.length - 1) {
            let tmp = list[index + 1];
            list[index] = tmp;
            list[index + 1] = item;
          }
          break;
        }
      }
      _self.commonOpinionCategory.opinions = list;
    },
    // 删除个人常用意见
    onRemoveCommonItemClick: function (event, item) {
      const _self = this;
      let opinions = _self.commonOpinionCategory.opinions;
      forEach(opinions, function (object, index) {
        if (object == item) {
          if (!isEmpty(item.uuid)) {
            _self.deletedCommonOpinions.push(item);
          }
          opinions.splice(index, 1);
        }
      });
    }
  }
};
</script>
<style scoped lang="less">
.opinion-manager {
  .opinion-tabs {
    --w-tab-font-size: var(--w-font-size-base);
    border: 1px solid var(--w-border-color-light);
    border-radius: 4px;

    ::v-deep .ant-list-item {
      line-height: 32px;
      padding: 8px;
      font-size: var(--w-font-size-base);
      color: var(--w-text-color-dark);
      border-color: var(--w-border-color-light);
      word-break: break-all;
      > span {
        width: e('calc(100% - 80px)');
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
      }

      .disabled {
        color: var(--w-text-color-lighter);
      }

      &:hover {
        color: var(--w-primary-color);
        background-color: var(--w-primary-color-1);
      }
    }
    .common-tab-panel {
      --w-pt-tabs-tabpane-padding: 0;
      > .ant-row > .ant-col {
        padding: var(--w-padding-xs) var(--w-padding-md);
        &:last-child {
          border-left: 1px solid var(--w-border-color-light);
        }

        .common-tab-title {
          font-size: var(--w-font-size-base);
          color: var(--w-text-color-dark);
          font-weight: bold;
          line-height: 32px;
          white-space: nowrap;
          padding-right: 15px;
        }
        .public-opinion-search {
          // width: 240px;
        }
        .need-input {
          text-align: center;
          color: var(--w-text-color-base);
          font-size: var(--w-font-size-base);
          line-height: 24px;
        }
      }
    }
  }
}
</style>
