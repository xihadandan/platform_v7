<template>
  <div class="widget-edit-uni-swiper-slider">
    <a-list :key="listKey" :grid="{ gutter: 8, column: 2 }" :data-source="widget.configuration.sliderItems">
      <a-list-item slot="renderItem" slot-scope="record, index">
        <a-card
          size="small"
          :bodyStyle="{ height: '200px', display: 'flex', alignItems: 'center', flexDirection: 'column', position: 'relative' }"
        >
          <div style="width: 100%; height: 125px; margin-bottom: 25px; position: relative">
            <ImageLibrary
              v-model="record.src"
              width="100%"
              height="125px"
              :allowSelectType="['commonImages', 'mongoImages', 'writeImage']"
            />
          </div>

          <a-input placeholder="标题" v-model="record.text" allow-clear style="width: 100%">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" :code="record.id" :target="record" v-model="record.text" />
            </template>
          </a-input>

          <template slot="actions" class="ant-card-actions">
            <Icon title="拖动排序" type="pticon iconfont icon-ptkj-tuodong" class="drag-column-handler" />
            <WidgetDesignDrawer
              :closeOpenDrawer="false"
              :id="'widgetUniSwiperImgConfig' + record.id"
              title="事件管理"
              :designer="designer"
              width="600px"
            >
              <a-button type="link" size="small" title="事件管理">
                <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
              </a-button>
              <template slot="content">
                <WidgetEventHandler :widget="widget" :eventModel="record.eventHandler" :designer="designer" />
              </template>
            </WidgetDesignDrawer>
            <a-button type="link" size="small" @click="record.hidden = !record.hidden" :title="record.hidden ? '显示' : '隐藏'">
              <Icon :type="record.hidden === true ? 'pticon iconfont icon-wsbs-yincang' : 'pticon iconfont icon-wsbs-xianshi'"></Icon>
            </a-button>
            <a-button type="link" size="small" @click="widget.configuration.sliderItems.splice(index, 1)" title="删除">
              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
            </a-button>
          </template>
        </a-card>
      </a-list-item>
    </a-list>
    <div>
      <a-button icon="plus" type="default" block @click="addSliderItem">新增</a-button>
    </div>
  </div>
</template>

<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'SwiperSliderConfiguration',
  mixins: [draggable],
  inject: ['pageContext'],
  props: {
    widget: Object
  },
  data() {
    return { listKey: new Date().getTime() };
  },

  beforeCreate() {},
  computed: {},
  created() {},
  methods: {
    addSliderItem() {
      this.widget.configuration.sliderItems.push({
        uuid: generateId(),
        text: undefined,
        src: undefined,
        hidden: false,
        eventHandler: {}
      });
    }
  },
  mounted() {
    let _this = this;
    let draggableInit = () => {
      this.tableDraggable(
        this.widget.configuration.sliderItems,
        this.$el.querySelector('.widget-edit-uni-swiper-slider .ant-row'),
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
