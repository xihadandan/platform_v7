<template>
  <div class="action-setting">
    <a-row type="flex">
      <a-col flex="240px">
        <div class="content-nav" :style="{ height: scrollHeight + 'px' }">
          <a-menu @click="onContentNavClick" :defaultSelectedKeys="['buttons']">
            <a-menu-item key="buttons">流程操作管理</a-menu-item>
            <a-menu-item key="rights">操作应用权限</a-menu-item>
            <a-menu-item key="group">操作分组设置</a-menu-item>
          </a-menu>
        </div>
      </a-col>
      <a-col flex="auto" class="content-body">
        <PerfectScrollbar
          :style="{
            height: scrollHeight - 60 + 'px'
          }"
        >
          <ActionButtons v-show="selectedNavKey == 'buttons'" :setting="setting"></ActionButtons>
          <ActionRights ref="rights" v-show="selectedNavKey == 'rights'" :setting="setting"></ActionRights>
          <ActionGroup v-show="selectedNavKey == 'group'" :setting="setting"></ActionGroup>
        </PerfectScrollbar>
      </a-col>
    </a-row>
    <div class="btn-container">
      <a-button @click="restoreActionSetting">恢复默认</a-button>
      <a-button type="primary" @click="saveActionSetting">保存</a-button>
    </div>
  </div>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
import { isEmpty } from 'lodash';
import ActionButtons from './components/action-buttons.vue';
import ActionRights from './components/action-rights.vue';
import ActionGroup from './components/action-group.vue';

const DEFAULT_GROUP = {
  type: 'dynamicGroup',
  dynamicGroupName: '更多',
  dynamicGroupBtnThreshold: 5,
  groups: [],
  style: {},
  mobile: {
    zh_CN: 2,
    otherLocale: 1
  }
};

export default {
  components: { ActionButtons, ActionRights, ActionGroup },
  data() {
    let buttons = this.initButtons();
    return {
      setting: {
        buttons,
        rights: [],
        group: DEFAULT_GROUP
      },
      selectedNavKey: 'buttons',
      scrollHeight: 700
    };
  },
  computed: {},
  created() {
    this.loadActionSetting();
  },
  mounted() {
    this.getVpageHeight();
  },
  methods: {
    initButtons() {
      let buttons = [
        {
          title: '保存',
          code: 'B004001',
          sortOrder: 80,
          applyTo: ['start', 'todo'],
          style: { type: 'default', icon: 'iconfont icon-ptkj-baocun' }
        },
        {
          title: '提交',
          code: 'B004002',
          sortOrder: 10,
          applyTo: ['start', 'todo'],
          style: { type: 'default', icon: 'iconfont icon-ptkj-tijiaofabufasong', type: 'primary' }
        },
        {
          title: '退回',
          code: 'B004003',
          sortOrder: 20,
          applyTo: ['todo'],
          style: { type: 'default', icon: 'iconfont icon-luojizujian-fanhui' }
        },
        {
          title: '退回前办理人',
          code: 'B004004',
          sortOrder: 30,
          applyTo: ['todo'],
          style: { type: 'default', icon: 'iconfont icon-luojizujian-fanhui' }
        },
        {
          title: '撤回',
          code: 'B004005',
          sortOrder: 40,
          applyTo: ['done'],
          style: { type: 'default', icon: 'iconfont icon-oa-zhijietuihui' }
        },
        { title: '转办', code: 'B004006', sortOrder: 50, applyTo: ['todo'], style: { type: 'default', icon: 'iconfont icon-oa-zhuanban' } },
        { title: '会签', code: 'B004007', sortOrder: 60, applyTo: ['todo'], style: { type: 'default', icon: 'iconfont icon-oa-huiqian' } },
        {
          title: '加签',
          code: 'B004042',
          sortOrder: 70,
          applyTo: ['todo'],
          style: { type: 'default', icon: 'iconfont icon-oa-qianshuyijian' }
        },
        {
          title: '关注',
          code: 'B004008',
          sortOrder: 90,
          multistate: true,
          states: [
            { title: '关注', code: 'B004008', style: { type: 'default', icon: 'iconfont icon-oa-guanzhu' } },
            { title: '取消关注', code: 'B004012', style: { type: 'default', icon: 'iconfont iconfont icon-oa-quxiaoguanzhu' } }
          ],
          applyTo: ['todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer']
        },
        {
          title: '套打',
          code: 'B004009',
          sortOrder: 110,
          applyTo: ['start', 'todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'],
          style: { type: 'default', icon: 'iconfont icon-ptkj-daiqujian' }
        },
        {
          title: '抄送',
          code: 'B004010',
          sortOrder: 80,
          applyTo: ['todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'],
          style: { type: 'default', icon: 'iconfont icon-oa-chaosongwode' }
        },
        {
          title: '签署意见',
          code: 'B004011',
          sortOrder: 120,
          applyTo: ['start', 'todo', 'done', 'monitor'],
          style: { type: 'default', icon: 'iconfont icon-ptkj-zhuce-toubu' }
        },
        // { title: '取消关注', code: 'B004012', sortOrder: 100, applyTo: ['todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'] },
        {
          title: '查看办理过程',
          code: 'B004013',
          sortOrder: 130,
          applyTo: ['todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'],
          style: { type: 'default', icon: 'iconfont icon-oa-banliguocheng' }
        },
        {
          title: '催办',
          code: 'B004014',
          sortOrder: 150,
          applyTo: ['todo', 'done', 'supervise', 'monitor'],
          style: { type: 'default', icon: 'iconfont icon-xmch-shoudaodecuibanxiaoxi' }
        },
        {
          title: '特送个人',
          code: 'B004015',
          sortOrder: 160,
          applyTo: ['monitor'],
          style: { type: 'default', icon: 'iconfont icon-ptkj_tesonggeren' }
        },
        {
          title: '特送环节',
          code: 'B004016',
          sortOrder: 170,
          applyTo: ['monitor'],
          style: { type: 'default', icon: 'iconfont icon-ptkj_tesonghuanjie' }
        },
        {
          title: '挂起',
          code: 'B004017',
          sortOrder: 130,
          multistate: true,
          states: [
            { title: '挂起', code: 'B004017', style: { type: 'default', icon: 'iconfont icon-oa-guaqi' } },
            { title: '恢复', code: 'B004018', style: { type: 'default', icon: 'iconfont icon-luojizujian-huifujishi' } }
          ],
          applyTo: ['todo', 'supervise', 'monitor']
        },
        // { title: '恢复', code: 'B004018', sortOrder: 140, applyTo: ['supervise', 'monitor'],style:{icon:"iconfont icon-luojizujian-huifujishi"} },
        {
          title: '关闭',
          code: 'B004019',
          sortOrder: 290,
          applyTo: ['start', 'todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'],
          style: { type: 'default', icon: 'ant-iconfont close' }
        },
        // { title: '提交时自动套打', code: 'B004020', sortOrder: 200, applyTo: ['supervise', 'monitor'] },
        // { title: '标记未阅', code: 'B004021', sortOrder: 210, applyTo: ['copyTo'] },
        // {
        //   title: '标记已阅',
        //   code: 'B004022',
        //   sortOrder: 220,
        //   multistate: true,
        //   states: [
        //     { title: '标记已阅', code: 'B004022' },
        //     { title: '标记未阅', code: 'B004021' }
        //   ],
        //   applyTo: ['copyTo']
        // },
        {
          title: '完成',
          code: 'B004044',
          sortOrder: 15,
          applyTo: ['todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'],
          style: { type: 'default', icon: 'iconfont icon-wsbs-dagouwancheng' }
        },
        {
          title: '删除',
          code: 'B004023',
          sortOrder: 180,
          multistate: true,
          states: [
            { title: '删除', code: 'B004023', style: { type: 'default', icon: 'iconfont icon-ptkj-shanchu' } },
            { title: '恢复', code: 'B004046', style: { type: 'default', icon: 'iconfont icon-luojizujian-huifujishi' } }
          ],
          applyTo: ['todo']
        },
        {
          title: '管理员删除',
          code: 'B004024',
          sortOrder: 190,
          applyTo: ['supervise', 'monitor'],
          style: { type: 'default', icon: 'iconfont icon-ptkj_guanliyuanshanchu' }
        },
        // { title: '可编辑文档', code: 'B004025', sortOrder: 240 },
        // { title: '必须签署意见', code: 'B004026', sortOrder: 260 },
        // { title: '转办必填意见', code: 'B004029', sortOrder: 290 },
        // { title: '会签必填意见', code: 'B004030', sortOrder: 300 },
        // { title: '加签必填意见', code: 'B004043', sortOrder: 300 },
        // { title: '退回必填意见', code: 'B004031', sortOrder: 310 },
        // { title: '特送个人必填意见', code: 'B004032', sortOrder: 320 },
        // { title: '特送环节必填意见', code: 'B004033', sortOrder: 330 },
        // { title: '催办环节必填意见', code: 'B004034', sortOrder: 340 },
        {
          title: '退回主流程',
          code: 'B004035',
          sortOrder: 350,
          applyTo: ['todo'],
          style: { type: 'default', icon: 'iconfont icon-ptkj_tuihuizhuliucheng' }
        },
        {
          title: '查看阅读记录',
          code: 'B004037',
          sortOrder: 370,
          applyTo: ['todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'],
          style: { type: 'default', icon: 'ant-iconfont profile' }
        },
        // { title: '查看流程数据快照', code: 'B004038', sortOrder: 380 },
        // { title: '撤回必填意见', code: 'B004039', sortOrder: 390 },
        {
          title: '进入连续签批',
          code: 'B004040',
          sortOrder: 400,
          applyTo: ['todo'],
          style: { type: 'default', icon: 'iconfont icon-ptkj_lianxuqianpi' }
        },
        {
          title: '查看流程图',
          code: 'B004041',
          sortOrder: 410,
          applyTo: ['start', 'todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'],
          style: { type: 'default', icon: 'iconfont icon-ptkj-suoyinshezhi' }
        },
        {
          title: '发起群聊',
          code: 'B004050',
          sortOrder: 450,
          applyTo: ['todo'],
          style: { type: 'default', icon: 'wechat' }
        },
        {
          title: '查看主流程',
          code: 'B004095',
          sortOrder: 950,
          applyTo: ['todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'],
          style: { type: 'default', icon: 'iconfont icon-luojizujian-faqiliucheng' }
        },
        {
          title: '打印表单',
          code: 'B004096',
          sortOrder: 960,
          applyTo: ['start', 'todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'],
          style: { type: 'default', icon: 'iconfont icon-wsbs-dayin' }
        }
        // { title: '版式文档处理', code: 'B004097', sortOrder: 970, applyTo: ['todo', 'done', 'supervise', 'monitor', 'copyTo', 'viewer'] }
      ];
      buttons.forEach(button => {
        button.id = generateId();
        button.buildIn = true;
        button.name = button.title;
        button.style = button.style || {};
        button.multistate = button.multistate || false;
        button.defaultVisible = true;
        button.defaultVisibleVar = {};
        button.states &&
          button.states.forEach(state => {
            if (state.code == button.code) {
              state.id = button.id;
            } else {
              state.id = generateId();
            }
            state.buildIn = true;
            state.style = state.style || {};
            state.defaultVisible = true;
            state.defaultVisibleVar = {};
          });
        if (this.setting) {
          const findItem = this.setting.buttons.find(f => f.code === button.code);
          if (findItem && findItem.i18n) {
            button.i18n = JSON.parse(JSON.stringify(findItem.i18n));
          }
        }
      });
      return buttons;
    },
    // 内置按钮调整时合并
    updateButtons(buildInBtns) {
      const _this = this;
      let buttons = _this.setting.buttons;
      let addBtns = [];
      let deleteBtns = [];
      buttons.forEach(btn => {
        let buildInBtn = buildInBtns.find(right => btn.code == right.code);
        if (!buildInBtn && btn.buildIn) {
          deleteBtns.push(btn);
        }
      });
      buildInBtns.forEach(buildInBtn => {
        let btn = buttons.find(btn => btn.code == buildInBtn.code);
        if (!btn) {
          addBtns.push(buildInBtn);
        }
      });

      // 添加的按钮
      addBtns.forEach(btn => {
        buttons.push(btn);
      });

      // 删除的按钮
      deleteBtns.forEach(deleteBtn => {
        let deleteIndex = buttons.findIndex(btn => deleteBtn.code == btn.code);
        buttons.splice(deleteIndex, 1);
      });

      if (addBtns.length) {
        _this.$message.info(`自动加载新增的内置按钮[${addBtns.map(btn => btn.title)}]，未保存！`);
      }
      if (deleteBtns.length) {
        _this.$message.info(`自动去掉删除的内置按钮[${deleteBtns.map(btn => btn.title)}]，未保存！`);
      }
      if (addBtns.length || deleteBtns.length) {
        _this.setting.buttons = [...buttons.filter(btn => btn.buildIn), ...buttons.filter(btn => !btn.buildIn)];
      }
    },
    onContentNavClick({ key }) {
      this.selectedNavKey = key;
    },
    loadActionSetting() {
      $axios.get('/proxy/api/workflow/setting/getByKey?key=ACTION').then(({ data: result }) => {
        if (result.data && result.data.attrVal) {
          let buildInBtns = deepClone(this.setting.buttons);
          let setting = JSON.parse(result.data.attrVal);
          this.setting.buttons = setting.buttons;
          this.setting.rights = setting.rights;
          this.setting.group = setting.group;

          // 移动端动态分组个数
          if (!this.setting.group.hasOwnProperty('mobile')) {
            this.$set(this.setting.group, 'mobile', {
              zh_CN: 2,
              otherLocale: 1
            });
          }
          this.updateButtons(buildInBtns);
        }
      });
    },
    saveActionSetting() {
      const _this = this;
      // 按钮名称非空判断
      let buttons = _this.setting.buttons;
      let emptyTitleBtns = buttons.filter(button => isEmpty(button.title));
      if (emptyTitleBtns.length) {
        _this.$message.error('按钮名称不能为空！');
        return;
      }

      // 按钮编码非空判断
      let emptyCodeBtns = buttons.filter(button => isEmpty(button.code));
      if (emptyCodeBtns.length) {
        _this.$message.error(`按钮[${emptyCodeBtns.map(btn => btn.title)}]的编码不能为空！`);
        return;
      }

      // 按钮编码重复判断
      let sameCodeBtns = [];
      let btnMap = {};
      buttons.forEach(btn => {
        if (btn.multistate && btn.states) {
          btn.states.forEach(state => {
            if (btnMap[state.code]) {
              sameCodeBtns.push(state);
            } else {
              btnMap[state.code] = state;
            }
          });
        } else {
          if (btnMap[btn.code]) {
            sameCodeBtns.push(btn);
          } else {
            btnMap[btn.code] = btn;
          }
        }
      });
      if (sameCodeBtns.length) {
        _this.$message.error(`按钮[${sameCodeBtns.map(btn => btn.title)}]的编码不能重复！`);
        return;
      }

      let setting = {
        attrKey: 'ACTION',
        attrVal: JSON.stringify(_this.setting),
        enabled: true,
        category: 'WORKFLOW',
        remark: '操作设置'
      };
      $axios
        .post('/api/workflow/setting/save', setting)
        .then(({ data: result }) => {
          if (result.code == 0) {
            _this.$message.success('保存成功！');
          } else {
            _this.$message.error(result.msg || '保存失败！');
          }
        })
        .catch(({ response }) => {
          _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
        });
    },
    restoreActionSetting() {
      this.setting.buttons = this.initButtons();
      this.setting.rights = this.$refs.rights.initRights();
      if (this.setting.group.i18n) {
        this.setting.group = deepClone({ ...DEFAULT_GROUP, i18n: this.setting.group.i18n });
      } else {
        this.setting.group = deepClone(DEFAULT_GROUP);
      }
      this.$message.info('已恢复，未保存！');
    },
    getVpageHeight() {
      let vpageHeight = 700;
      if (this.$root.$el.classList.contains('preview')) {
        vpageHeight = this.$root.$el.offsetHeight;
      } else if (this.$el.closest('.widget-vpage')) {
        vpageHeight = this.$el.closest('.widget-vpage').offsetHeight;
      } else {
        setTimeout(() => {
          this.getVpageHeight();
        }, 0);
      }
      this.scrollHeight = vpageHeight - 46;
    }
  }
};
</script>

<style lang="less" scoped>
.action-setting {
  .content-nav {
    padding: 12px 12px 52px 12px;
    height: e('calc(100vh - 220px)');
    border-right: 1px solid var(--w-border-color-light);
    ::v-deep .ant-menu {
      height: 100%;
      overflow-y: auto;
    }

    .child-nav {
      padding-left: 28px;
    }
  }

  .content-body {
    width: 0;
  }
  .btn-container {
    position: fixed;
    border-top: 1px solid var(--w-border-color-light);
    width: 100%;
    height: 52px;
    line-height: 52px;
    left: 5%;
    bottom: 0;
    text-align: center;
    z-index: 1;
    background-color: var(--w-bg-color-body);
  }
}
</style>
