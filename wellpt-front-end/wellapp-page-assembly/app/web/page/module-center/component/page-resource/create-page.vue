<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
    <a-form-model-item label="页面名称" prop="name">
      <a-input v-model="form.name" />
    </a-form-model-item>
    <a-form-model-item label="页面ID" prop="id">
      <a-input v-model="form.id" />
    </a-form-model-item>
    <template v-if="isBigscreen">
      <a-form-model-item label="大屏尺寸">
        <a-input-number v-model="form.screenWidth" :min="1024" />
        x
        <a-input-number v-model="form.screenHeight" :min="720" />
        <a-dropdown>
          <a-button icon="more" size="small" type="link">推荐尺寸</a-button>
          <a-menu slot="overlay" @click="onSelectScreenSize">
            <template v-for="(opt, i) in bigScreenSizeOptions">
              <a-menu-item :key="i + '_index'">
                <a href="javascript:;">{{ opt[0] }} x {{ opt[1] }}</a>
              </a-menu-item>
            </template>
          </a-menu>
        </a-dropdown>
      </a-form-model-item>
    </template>
    <a-form-model-item label="分组">
      <a-select :options="groupOptions" v-model="form.groupUuid" allow-clear />
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="form.remark" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import moment from 'moment';
import { debounce } from 'lodash';

export default {
  name: 'CreatePage',
  inject: ['currentModule'],
  props: {
    groupOptions: Array,
    isPc: Boolean,
    isBigscreen: {
      type: Boolean,
      default: false
    }
  },
  components: {},
  computed: {},
  data() {
    let form = { name: undefined, id: undefined, remark: undefined };
    if (this.isBigscreen) {
      form.screenHeight = 1440;
      form.screenWidth = 2560;
    }
    return {
      form,
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        name: [{ required: true, message: '页面名称必填', trigger: 'blur' }],
        id: [
          { required: true, message: 'ID必填', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.checkIdExist }
        ]
      },
      bigScreenSizeOptions: [
        [1024, 768],
        [1280, 720],
        [2560, 1440],
        [3840, 2160],
        [5120, 2880],
        [7680, 4320]
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.form.id = 'page_' + moment().format('yyyyMMDDHHmmss');
  },

  mounted() {},
  methods: {
    onSelectScreenSize(e) {
      this.form.screenHeight = this.bigScreenSizeOptions[e.item.index][1];
      this.form.screenWidth = this.bigScreenSizeOptions[e.item.index][0];
    },
    checkIdExist: debounce(function (rule, value, callback) {
      $axios
        .get(`/proxy/api/webapp/page/definition/existId`, {
          params: {
            id: value
          }
        })
        .then(({ data }) => {
          if (data.code == 0) {
            callback(data.data ? '页面ID已存在' : undefined);
          } else {
            callback('服务异常');
          }
        });
    }, 300),
    save(callback) {
      let _this = this;
      let wtype = this.isPc || this.isBigscreen ? (this.isBigscreen ? 'vBigscreen' : 'vPage') : 'vUniPage';

      let pageDef = {
        ..._this.form,
        //  title: this.form.name,
        isPc: this.isPc || this.isBigscreen ? '1' : '0',
        wtype,
        appId: this.currentModule.id,
        definitionJson: JSON.stringify({
          wtype,
          title: this.form.name,
          name: this.form.name,
          id: this.form.id,
          items: [],
          vars: {},
          js: undefined,
          style: this.isBigscreen
            ? {
                width: this.form.screenWidth,
                height: this.form.screenHeight
              }
            : undefined
        })
      };
      this.$refs.form.validate(passed => {
        if (passed) {
          _this.$loading('保存中');
          $axios
            .post('/web/design/savePageDefinition', pageDef)
            .then(({ data }) => {
              // data.data
              _this.$loading(false);
              if (data.data) {
                if (_this.form.groupUuid) {
                  $axios.get('/proxy/api/app/module/resGroup/updateMember', {
                    params: {
                      memberUuid: data.data,
                      groupUuid: _this.form.groupUuid,
                      type: 'appPageDefinition'
                    }
                  });
                }
                callback({
                  uuid: data.data,
                  id: _this.form.id,
                  name: _this.form.name,
                  wtype: pageDef.wtype,
                  version: '1.0',
                  groupUuid: _this.form.groupUuid,
                  justCreated: true
                });
                _this.form = {};
                _this.toPageDesign(data);
              }
            })
            .catch(() => {
              _this.$loading(false);
            });
        } else {
          _this.$loading(false);
        }
      });
    },
    toPageDesign(data) {
      let wtype = this.isPc || this.isBigscreen ? (this.isBigscreen ? 'vBigscreen' : 'vPage') : 'vUniPage';
      window.open(
        `/${wtype == 'vUniPage' ? 'uni-page' : wtype == 'vBigscreen' ? 'bigscreen' : 'page'}-designer/index?uuid=${data.data}`,
        '_blank'
      );
    }
  }
};
</script>
