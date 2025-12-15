<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="主标题">
            <a-input-group compact>
              <a-input v-model="widget.configuration.title" allow-clear style="width: 200px">
                <template slot="addonAfter">
                  <WI18nInput :widget="widget" :designer="designer" code="title" v-model="widget.configuration.title" />
                </template>
              </a-input>
              <ColorPicker v-model="widget.configuration.titleColor" style="width: 30px" />
            </a-input-group>
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              <span>主标题点击事件</span>
              <a-checkbox v-model="widget.configuration.titleClickable" />
            </template>
            <WidgetDesignDrawer
              v-if="widget.configuration.titleClickable"
              :id="'widgetUniSectionTitleClickConfig' + widget.id"
              title="主标题点击事件"
              :designer="designer"
              width="500px"
            >
              <a-button type="link" icon="setting" size="small">配置</a-button>
              <template slot="content">
                <div>
                  <a-form-model>
                    <a-form-model-item label="点击文本">
                      <a-input v-model="widget.configuration.titleClickText">
                        <template slot="addonAfter">
                          <WI18nInput
                            :widget="widget"
                            :designer="designer"
                            code="titleClick"
                            v-model="widget.configuration.titleClickText"
                          />
                        </template>
                      </a-input>
                    </a-form-model-item>
                    <a-form-model-item label="点击图标">
                      <ImageLibrary
                        :iconStyle="{
                          fontSize: '32px'
                        }"
                        v-model="widget.configuration.titleClickIcon"
                        width="100px"
                        height="64px"
                        :allowSelectType="['icon']"
                      />
                    </a-form-model-item>
                  </a-form-model>
                  <WidgetEventHandler :widget="widget" :eventModel="widget.configuration.titleClickEventHandler" :designer="designer" />
                </div>
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
          <a-form-model-item label="副标题">
            <a-input-group compact>
              <a-input v-model="widget.configuration.subTitle" allow-clear style="width: 200px">
                <template slot="addonAfter">
                  <WI18nInput :widget="widget" :designer="designer" code="subTitle" v-model="widget.configuration.subTitle" />
                </template>
              </a-input>
              <ColorPicker v-model="widget.configuration.subTitleColor" style="width: 30px" />
            </a-input-group>
          </a-form-model-item>

          <a-form-model-item label="标题装饰">
            <a-radio-group v-model="widget.configuration.decorationType" button-style="solid" size="small">
              <a-radio-button value="custom">自定义</a-radio-button>
              <a-radio-button value="line">竖线</a-radio-button>
              <a-radio-button value="circle">圆点</a-radio-button>
              <a-radio-button value="square">方形</a-radio-button>
              <a-radio-button :value="undefined">无</a-radio-button>
            </a-radio-group>
            <ImageLibrary
              v-if="widget.configuration.decorationType == 'custom'"
              v-model="widget.configuration.icon.src"
              width="100%"
              height="64px"
              :allowSelectType="['commonImages', 'mongoImages', 'icon']"
            />
          </a-form-model-item>
          <a-form-model-item label="标题背景图">
            <ImageLibrary
              v-model="widget.configuration.backgroundImage"
              width="100%"
              height="64px"
              :allowSelectType="['commonImages', 'mongoImages']"
            />
          </a-form-model-item>
          <BorderRadiusSet :target="widget.configuration" />

          <a-form-model-item label="内容区">
            <a-switch v-model="widget.configuration.allowContent" />
          </a-form-model-item>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import BorderRadiusSet from '../../commons/style-prop/border-radius-set.vue';
export default {
  name: 'WidgetUniSectionConfiguration',
  props: { widget: Object, designer: Object },
  components: { BorderRadiusSet },
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {},
  configuration() {
    return {
      title: '主标题',
      titleColor: '#333333',
      subTitleColor: '#999999',
      subTitle: '副标题',
      allowContent: false,
      borderRadius: 0,
      backgroundImage: undefined,
      decorationType: 'line', // line , circle , square
      widgets: [],
      titleClickIcon: 'uniicons uniui-arrowright',
      titleClickable: false,
      titleClickText: undefined,
      titleClickEventHandler: {},
      icon: {
        src: undefined,
        fontSize: 24,
        color: undefined
      }
    };
  }
};
</script>
