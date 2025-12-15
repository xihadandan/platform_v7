<template>
  <HtmlWrapper title="主题预览">
    <a-layout class="widget-design-layout">
      <a-layout-header class="widget-design-header">
        <a-row>
          <a-col :span="12">
            <div style="display: inline-flex">
              <a-select style="width: 150px" v-model="fontSizeIndex" @change="onChangeFontSize">
                <a-select-option v-for="(font, i) in themeFontSize" :value="i" :key="'fontsize_' + i">
                  {{ font.label }}
                </a-select-option>
              </a-select>
              <div style="display: inline-flex; align-items: center">
                <div
                  v-for="(color, i) in themeColors"
                  :key="'themecolor_' + i"
                  @click.stop="changeThemeColor(color, i)"
                  :style="{
                    cursor: 'pointer',
                    borderRadius: '2px',
                    outline: '1px solid #fff',
                    marginLeft: '10px',
                    width: '25px',
                    height: '25px',
                    backgroundColor: color,
                    display: 'flex',
                    'align-items': 'center',
                    'justify-content': 'center'
                  }"
                >
                  <a-icon type="check" v-show="themeColor == color" />
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12" :style="{ textAlign: 'right' }">
            <a-button icon="save">保存</a-button>
            <a-button icon="save">本地暂存</a-button>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout :hasSider="true">
        <a-layout-sider theme="light" :width="348">
          <a-menu style="width: 256px" :default-selected-keys="['1']" :open-keys.sync="openKeys" mode="inline">
            <a-sub-menu key="sub1">
              <span slot="title">
                <a-icon type="mail" />
                <span>人事管理</span>
              </span>
              <a-menu-item-group key="g1">
                <template slot="title">
                  <a-icon type="qq" />
                  <span>在职人员</span>
                </template>
                <a-menu-item key="1">人员列表</a-menu-item>
                <a-menu-item key="2">报表统计</a-menu-item>
              </a-menu-item-group>
              <a-menu-item-group key="g2" title="离职人员">
                <a-menu-item key="3">离职人员</a-menu-item>
                <a-menu-item key="4">报表统计</a-menu-item>
              </a-menu-item-group>
            </a-sub-menu>
            <a-sub-menu key="sub2">
              <span slot="title">
                <a-icon type="appstore" />
                <span>会议室管理</span>
              </span>
              <a-menu-item key="5">会议申请</a-menu-item>
              <a-menu-item key="6">会议查看</a-menu-item>
              <a-sub-menu key="sub3" title="会议报告">
                <a-menu-item key="7">今日报告</a-menu-item>
                <a-menu-item key="8">部门报告</a-menu-item>
              </a-sub-menu>
            </a-sub-menu>
          </a-menu>
        </a-layout-sider>
        <a-layout-content>
          <a-form-model ref="ruleForm" :model="form" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
            <a-form-model-item ref="name" label="名称" prop="name">
              <a-input v-model="form.name" />
            </a-form-model-item>
            <a-form-model-item label="地区选择" prop="region">
              <a-select v-model="form.region" placeholder="请选择地区">
                <a-select-option value="厦门">厦门</a-select-option>
                <a-select-option value="福州">福州</a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="时间" required prop="date1">
              <a-date-picker v-model="form.date1" show-time type="date" placeholder="选择日期" style="width: 100%" />
            </a-form-model-item>
            <a-form-model-item label="是否" prop="delivery">
              <a-switch v-model="form.delivery" />
            </a-form-model-item>
            <a-form-model-item label="类型选择" prop="type">
              <a-checkbox-group v-model="form.type">
                <a-checkbox value="1" name="type">类型一</a-checkbox>
                <a-checkbox value="2" name="type">类型二</a-checkbox>
                <a-checkbox value="3" name="type">类型三</a-checkbox>
              </a-checkbox-group>
            </a-form-model-item>
            <a-form-model-item label="资源" prop="resource">
              <a-radio-group v-model="form.resource">
                <a-radio value="1">资源一</a-radio>
                <a-radio value="2">资源二</a-radio>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="备注" prop="desc">
              <a-input v-model="form.desc" type="textarea" />
            </a-form-model-item>
            <a-form-model-item :wrapper-col="{ span: 14, offset: 4 }">
              <a-button type="primary">创建</a-button>
              <a-button style="margin-left: 10px">重置</a-button>
            </a-form-model-item>
          </a-form-model>
        </a-layout-content>
        <a-layout-sider theme="light" :width="300"></a-layout-sider>
      </a-layout>
    </a-layout>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ThemePreview',
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      fontSizeIndex: 0,
      themeColor: undefined,
      current: ['mail'],
      openKeys: ['sub1'],
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      other: '',
      form: {
        name: '',
        region: undefined,
        date1: undefined,
        delivery: false,
        type: [],
        resource: '',
        desc: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入名称', trigger: 'blur' },
          { min: 3, max: 5, message: '长度3~5', trigger: 'blur' }
        ],
        region: [{ required: true, message: '请选择地区', trigger: 'change' }],
        date1: [{ required: true, message: '请选择日期', trigger: 'change' }],
        type: [
          {
            type: 'array',
            required: true,
            message: '请选择类型',
            trigger: 'change'
          }
        ],
        resource: [{ required: true, message: '请选择资源', trigger: 'change' }],
        desc: [{ required: true, message: '请输入备注', trigger: 'blur' }]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.themeColor = this.defaultThemeColor;
  },
  mounted() {},
  methods: {
    changeThemeColor(color, i) {
      let classes = document.body.classList.value.split(' ');
      this.themeColor = color;
      for (let i = 0, len = classes.length; i < len; i++) {
        if (classes[i].startsWith('primary-color-')) {
          classes.splice(i, 1);
          break;
        }
      }
      classes.push(`primary-color-${i + 1}`);
      document.body.setAttribute('class', classes.join(' '));
    },
    onChangeFontSize() {
      let classes = document.body.classList.value.split(' ');
      if (this.fontSizeIndex == 0) {
        // 移除字体样式
        for (let i = 0, len = classes.length; i < len; i++) {
          if (classes[i].startsWith('font-size-')) {
            classes.splice(i, 1);
            break;
          }
        }
      } else {
        classes.push(`font-size-${this.fontSizeIndex + 1}`);
      }
      document.body.setAttribute('class', classes.join(' '));
    }
  }
};
</script>
