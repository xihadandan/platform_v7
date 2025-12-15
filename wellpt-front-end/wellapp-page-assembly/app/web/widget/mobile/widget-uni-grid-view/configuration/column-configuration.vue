<template>
  <div class="widget-edit-uni-grid-view-column">
    <a-list :key="listKey" :grid="{ gutter: 8, column: this.widget.configuration.numColumns }" :data-source="widget.configuration.columns">
      <a-list-item slot="renderItem" slot-scope="record, index">
        <a-card size="small" :bodyStyle="{ height: '230px', position: 'relative' }">
          <div class="flex f_y_c" style="margin-bottom: 10px">
            <div class="f_g_1">组合样式</div>
            <WidgetDesignModal
              title="组合样式设置"
              :width="600"
              :zIndex="1000"
              dialogClass="pt-modal"
              :bodyStyle="{ height: '560px' }"
              :maxHeight="560"
              mask
              bodyContainer
            >
              <a-button type="link" size="small" title="组合样式设置">
                <Icon type="pticon iconfont icon-ptkj-zhutifengge" />
              </a-button>
              <template slot="content">
                <WidgetUniGridViewStyleConfiguration :target="record" param="groupStyle" type=""></WidgetUniGridViewStyleConfiguration>
              </template>
            </WidgetDesignModal>
          </div>
          <div class="flex f_y_c">
            <div class="f_g_1" style="min-width: 125px; width: 100%; height: 60px; margin-bottom: 25px; position: relative">
              <ImageLibrary
                v-model="record.icon.src"
                width="100%"
                height="60px"
                :allowSelectType="['icon', 'commonImages', 'mongoImages']"
                :iconStyle="{
                  ...getIconStyle(record.iconStyle),
                  ...getIconBackGroundStyle(record.iconStyle)
                }"
              />
              <WidgetDesignModal
                title="图标样式设置"
                :width="600"
                :zIndex="1000"
                dialogClass="pt-modal"
                :bodyStyle="{ height: '560px' }"
                :maxHeight="560"
                mask
                bodyContainer
              >
                <a-button type="primary" size="small" style="position: absolute; right: 0px; bottom: 0px" title="图标样式设置">
                  <Icon type="pticon iconfont icon-ptkj-zhutifengge" />
                </a-button>
                <template slot="content">
                  <WidgetUniGridViewStyleConfiguration :target="record" param="iconStyle"></WidgetUniGridViewStyleConfiguration>
                </template>
              </WidgetDesignModal>
            </div>
          </div>
          <div class="flex f_y_c" style="margin-top: 10px">
            <div class="f_g_1" style="padding-right: 8px">
              <div>
                <a-input placeholder="标题" v-model="record.text" allow-clear style="width: 100%">
                  <template slot="addonAfter">
                    <WI18nInput :widget="widget" :designer="designer" :code="record.uuid" :target="record" v-model="record.text" />
                  </template>
                </a-input>
              </div>
              <div style="margin-top: 10px">
                <a-input placeholder="副标题" v-model="record.subtext" allow-clear style="width: 100%">
                  <template slot="addonAfter">
                    <WI18nInput
                      :widget="widget"
                      :designer="designer"
                      :code="record.uuid + '_subTitle'"
                      :target="record"
                      v-model="record.subtext"
                    />
                  </template>
                </a-input>
              </div>
            </div>
            <div>
              <WidgetDesignModal
                title="标题样式设置"
                :width="600"
                :zIndex="1000"
                dialogClass="pt-modal"
                :bodyStyle="{ height: '560px' }"
                :maxHeight="560"
                mask
                bodyContainer
              >
                <a-button type="link" size="small" title="标题样式设置">
                  <Icon type="pticon iconfont icon-ptkj-zhutifengge" />
                </a-button>
                <template slot="content">
                  <WidgetUniGridViewStyleConfiguration
                    :target="record"
                    param="titleStyle"
                    type="title"
                  ></WidgetUniGridViewStyleConfiguration>
                </template>
              </WidgetDesignModal>
            </div>
          </div>

          <template slot="actions" class="ant-card-actions">
            <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" :style="{ cursor: 'move' }" />
            <WidgetDesignDrawer
              :closeOpenDrawer="false"
              :id="'widgetUniGridViewColConfig' + record.id"
              title="事件管理"
              :designer="designer"
              width="600px"
            >
              <a-button type="link" size="small" title="事件管理">
                <Icon type="pticon iconfont icon-ptkj-shezhi" />
              </a-button>
              <template slot="content">
                <WidgetEventHandler :widget="widget" :eventModel="record.eventHandler" :designer="designer" />
              </template>
            </WidgetDesignDrawer>
            <a-icon
              :type="record.hidden === true ? 'eye-invisible' : 'eye'"
              @click="record.hidden = !record.hidden"
              :title="record.hidden ? '显示' : '隐藏'"
            />
            <a-button
              type="link"
              size="small"
              @click="widget.configuration.columns.splice(index, 1)"
              class="ant-btn-dangerous"
              title="删除"
            >
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
            </a-button>
          </template>
        </a-card>
      </a-list-item>
    </a-list>
    <div>
      <a-button icon="plus" type="default" block @click="addColumn">新增</a-button>
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, swapArrayElements } from '@framework/vue/utils/util';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import draggable from '@framework/vue/designer/draggable';
import WidgetUniGridViewStyleConfiguration from './style-configuration.vue';
import { assign } from 'lodash';

export default {
  name: 'WidgetUniGridViewColumnConfiguration',
  mixins: [draggable],
  inject: ['pageContext'],
  components: { ImageLibrary, WidgetUniGridViewStyleConfiguration },
  props: {
    widget: Object,
    pageOptions: Array,
    designer: Object
  },
  data() {
    return {
      listKey: new Date().getTime()
    };
  },

  beforeCreate() {},
  computed: {},
  created() {},
  methods: {
    addColumn() {
      this.widget.configuration.columns.push({
        uuid: generateId(),
        text: '',
        hidden: false,
        eventHandler: {
          actionType: undefined,
          pageType: 'page',
          pageId: undefined,
          url: undefined,
          eventParams: []
        },
        icon: {
          src: undefined
        },
        groupStyle: {
          backgroundStyle: {
            backgroundColor: '', // 白底
            backgroundImage: undefined,
            backgroundImageInput: undefined,
            bgImageUseInput: false,
            backgroundRepeat: undefined,
            backgroundPosition: undefined,
            backgroundSize: undefined
          },
          borderRadius: undefined,
          padding: '',
          margin: '',
          size: '',
          color: '',
          borderColor: ''
        },
        iconStyle: {
          backgroundStyle: {
            backgroundColor: '', // 白底
            backgroundImage: undefined,
            backgroundImageInput: undefined,
            bgImageUseInput: false,
            backgroundRepeat: undefined,
            backgroundPosition: undefined,
            backgroundSize: undefined
          },
          borderRadius: undefined,
          padding: '',
          margin: '',
          size: '',
          color: '',
          borderColor: '',
          width: '',
          height: ''
        },
        titleStyle: {
          padding: '',
          size: '',
          color: '',
          subColor: '',
          subSize: ''
        }
      });
    },
    getBackGroundStyle(data) {
      let style = {};
      if (data) {
        let { backgroundColor, backgroundImage, backgroundImageInput, bgImageUseInput, backgroundPosition, backgroundRepeat } =
          data.backgroundStyle;

        if (backgroundColor) {
          style.backgroundColor = this.getColorValue(backgroundColor);
        }
        let bgImgStyle = bgImageUseInput ? backgroundImageInput : backgroundImage;
        if (bgImgStyle) {
          let isUrl =
            bgImgStyle.startsWith('data:') ||
            bgImgStyle.startsWith('http') ||
            bgImgStyle.startsWith('/') ||
            bgImgStyle.startsWith('../') ||
            bgImgStyle.startsWith('./');
          style.backgroundImage = isUrl ? `url("${bgImgStyle}")` : bgImgStyle;
        }
        if (backgroundPosition) {
          style.backgroundPosition = backgroundPosition;
        }
        if (backgroundRepeat) {
          style.backgroundRepeat = backgroundRepeat;
        }
      }
      return style;
    },
    getIconBackGroundStyle(data) {
      let param = 'iconStyle';
      let style = {};
      if (this.widget.configuration[param] && this.widget.configuration[param].backgroundStyle) {
        style = this.getBackGroundStyle(this.widget.configuration[param]);
      }
      if (data && data.backgroundStyle) {
        assign(style, this.getBackGroundStyle(data));
      }
      return style;
    },
    getIconStyle(data) {
      let param = 'iconStyle';
      let style = {};
      if (this.widget.configuration[param]) {
        style = this.getTeamStyle(this.widget.configuration[param]);
      }
      if (data) {
        assign(style, this.getTeamStyle(data));
      }
      return style;
    },
    getTeamStyle(data) {
      let style = {};
      style.padding = data.padding || '8px';
      style.borderRadius = data.borderRadius || '8px';
      if (data.size) {
        style.fontSize = data.size + 'px';
      }
      if (data.color) {
        style.color = this.getColorValue(data.color);
      }
      return style;
    },
    getColorValue(color) {
      if (color) {
        return color.startsWith('#') ? color : `var(${color})`;
      }
      return '';
    }
  },
  mounted() {
    let _this = this;
    let draggableInit = () => {
      this.tableDraggable(
        this.widget.configuration.columns,
        this.$el.querySelector('.widget-edit-uni-grid-view-column .ant-row'),
        '.drag-column-handler',
        {
          afterOnEnd: () => {
            _this.listKey = new Date().getTime();
            _this.$nextTick(() => {
              draggableInit();
            });
          }
        }
      );
    };
    draggableInit();
  }
};
</script>
