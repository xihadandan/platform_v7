<template>
  <i v-show="false" id="vIntroduce">
    <div v-for="(step, i) in steps" ref="stepIntro">
      <div>
        <div>
          <a-tag>{{ i + 1 }}</a-tag>
          {{ step.intro }}
        </div>
        <div class="operation">
          <a-button
            size="small"
            type="link"
            style="float: left"
            v-if="i == steps.length - 1"
            onclick="document.querySelector('#vIntroduce').__vue__.toStep(1)"
          >
            再看一遍
          </a-button>
          <a-button v-else size="small" type="link" style="float: left" onclick="document.querySelector('#vIntroduce').__vue__.skip()">
            跳过向导
          </a-button>
          <div style="float: right">
            <a-button size="small" v-if="i > 0" onclick="document.querySelector('#vIntroduce').__vue__.toStep('prev');">上一步</a-button>
            <a-button
              size="small"
              v-if="i >= 0 && i <= steps.length - 1"
              type="primary"
              onclick="document.querySelector('#vIntroduce').__vue__.toStep('next');"
            >
              {{ i == steps.length - 1 ? '知道了' : '下一步' }}
            </a-button>
          </div>
        </div>
      </div>
    </div>
  </i>
</template>
<style lang="less">
.pt-intro {
  .introjs-skipbutton {
    display: none;
  }
  .introjs-tooltip-header {
    display: none;
  }
  .operation {
    margin-top: 30px;
    height: 30px;
    line-height: 30px;
    display: flex;
    justify-content: space-between;
    align-items: baseline;
  }
}

.pt-intro-highlight {
  box-shadow: #fff 0px 0px 0px 0px, rgba(33, 33, 33, 0.5) 0px 0px 0px 5000px !important;
}
</style>
<script type="text/babel">
import intro from 'intro.js';
import 'intro.js/introjs.css';
export default {
  name: 'Intro',
  props: {
    steps: Array, // [intro: '介绍文案' , element: HTML Dom 元素 ]
    options: Object,
    functionId: String
  },
  components: {},
  computed: {},
  data() {
    return {
      userParams: { moduleId: 'FUNCTION_INTRODUCE', functionId: this.functionId, dataKey: '-1' },
      initOptions: Object.assign(
        {
          tooltipClass: 'pt-intro',
          disableInteraction: true,
          highlightClass: 'pt-intro-highlight',
          showButtons: false,
          showBullets: false,
          prevLabel: '上一步',
          nextLabel: '下一步',
          doneLabel: '知道了',
          hidePrev: true,
          buttonClass: 'ant-btn ant-btn-sm',
          exitOnOverlayClick: false,
          exitOnEsc: false
        },
        this.options || {}
      )
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    let _this = this;
    this.checkUserIntroduced().then(() => {
      this.initOptions.steps = [];
      for (let i = 0, len = this.steps.length; i < len; i++) {
        let ref = this.$refs.stepIntro[i];
        let el = document.querySelector(this.steps[i].selector);
        if (el) {
          this.initOptions.steps.push({
            intro: ref.innerHTML,
            element: el,
            position: this.steps[i].position || 'right'
          });
        }
      }
      intro()
        .setOptions(this.initOptions)
        .onafterchange(element => {
          _this.$emit('afterchange', element, _this.$intro);
        })
        .onbeforechange(element => {
          _this.$emit('beforechange', element, _this.$intro);
        })
        .onbeforeexit(() => {
          _this.userNoMoreIntroduce();
        })
        .onexit(() => {
          _this.$emit('exit', _this.$intro);
        })
        .start()
        .then(i => {
          this.$intro = i;
        });
    });
  },
  methods: {
    checkUserIntroduced() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/api/user/preferences/get`, {
            params: { ...this.userParams }
          })
          .then(({ data }) => {
            if (data.code == 0 && data.data.uuid == null) {
              resolve();
            }
          });
      });
    },
    userNoMoreIntroduce() {
      $axios
        .post(`/api/user/preferences/save`, {
          ...this.userParams,
          dataValue: '',
          remark: '用户功能引导已查看, 不再提示'
        })
        .then(({ data }) => {});
    },
    skip() {
      this.$intro.exit();
      this.userNoMoreIntroduce();
    },
    toStep(index) {
      if (typeof index == 'string') {
        if (index == 'next') {
          this.$intro.nextStep();
        } else if (index == 'prev') {
          this.$intro.previousStep();
        }
      } else if (typeof index == 'number') {
        this.$intro.goToStep(index);
      }
    }
  }
};
</script>
