<template>
  <div>
    <a-form-model class="pt-form">
      <template v-if="type && ['icon', 'title'].indexOf(type) > -1">
        <a-form-model-item :label="type === 'title' ? '标题大小' : '图标大小'">
          <a-input-number v-model="target[param].size" :allowClear="true"></a-input-number>
          px
        </a-form-model-item>
        <ColorSelectConfiguration
          :label="type === 'title' ? '标题颜色' : '图标颜色'"
          v-model="target[param]"
          onlyValue
          colorField="color"
          radioSize="small"
        ></ColorSelectConfiguration>
      </template>
      <template v-if="type && ['title'].indexOf(type) > -1">
        <a-form-model-item label="标题超过宽度" v-if="!isItem">
          <a-radio-group size="small" v-model="target[param].titleEllipsis" button-style="solid">
            <a-radio-button value="">不省略</a-radio-button>
            <a-radio-button value="1">显示1行</a-radio-button>
            <a-radio-button value="2">显示2行</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="副标题大小">
          <a-input-number v-model="target[param].subSize" :allowClear="true"></a-input-number>
          px
        </a-form-model-item>
        <ColorSelectConfiguration
          label="副标题颜色"
          v-model="target[param]"
          onlyValue
          colorField="subColor"
          radioSize="small"
        ></ColorSelectConfiguration>
        <a-form-model-item label="副标题超过宽度" v-if="!isItem">
          <a-radio-group size="small" v-model="target[param].subTitleEllipsis" button-style="solid">
            <a-radio-button value="">不省略</a-radio-button>
            <a-radio-button value="1">显示1行</a-radio-button>
            <a-radio-button value="2">显示2行</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </template>
      <template v-else>
        <template v-if="type && ['icon'].indexOf(type) > -1">
          <a-form-model-item label="图标宽度">
            <a-input v-model="target[param].width" :allowClear="true"></a-input>
          </a-form-model-item>
          <a-form-model-item label="图标高度">
            <a-input v-model="target[param].height" :allowClear="true"></a-input>
          </a-form-model-item>
        </template>
        <BackgroundSet :backgroundStyle="target[param].backgroundStyle"></BackgroundSet>
        <borderRadiusSet :target="target[param]"></borderRadiusSet>
        <ColorSelectConfiguration
          label="边框颜色"
          v-model="target[param]"
          onlyValue
          colorField="borderColor"
          radioSize="small"
        ></ColorSelectConfiguration>
      </template>
      <a-form-model-item label="外边距">
        <a-input v-model="target[param].margin" :allowClear="true"></a-input>
      </a-form-model-item>
      <a-form-model-item label="内边距">
        <a-input v-model="target[param].padding" :allowClear="true"></a-input>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<script type="text/babel">
import ColorSelectConfiguration from '@pageAssembly/app/web/widget/commons/color-select-configuration.vue';
import BackgroundSet from '../../commons/background-set.vue';
import borderRadiusSet from '../../commons/style-prop/border-radius-set.vue';
import { assign } from 'lodash';

export default {
  name: 'StyleConfiguration',
  mixins: [],
  inject: ['pageContext'],
  props: {
    widget: Object,
    designer: Object,
    target: Object,
    param: {
      type: String,
      requried: true
    },
    type: { type: String, default: 'icon' },
    isItem: { type: Boolean, default: true }
  },
  data() {
    if (!this.target.hasOwnProperty(this.param)) {
      let target = {
        backgroundStyle: {
          backgroundColor: '', // 白底
          backgroundImage: undefined,
          backgroundImageInput: undefined,
          bgImageUseInput: false,
          backgroundRepeat: undefined,
          backgroundPosition: undefined
        },
        borderRadius: 8,
        padding: '',
        margin: '',
        size: '',
        color: '',
        borderColor: ''
      };
      if (this.type === 'icon') {
        assign(target, {
          width: '',
          height: ''
        });
      }
      if (this.type === 'title') {
        assign(target, {
          subColor: '',
          subSize: ''
        });
      }
      this.$set(this.target, this.param, target);
    }
    return {};
  },
  beforeCreate() {},
  components: { BackgroundSet, borderRadiusSet },
  computed: {},
  created() {},
  methods: {},
  beforeMount() {},
  mounted() {}
};
</script>
