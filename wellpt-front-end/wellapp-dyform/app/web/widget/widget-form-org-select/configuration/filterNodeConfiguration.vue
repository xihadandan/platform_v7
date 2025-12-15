<template>
  <div>
    <a-form-model-item :label="label">
      <a-radio-group size="small" v-model="typeData" button-style="solid" @change="onChangeColorType">
        <a-radio-button :value="undefined">ID节点</a-radio-button>
        <a-radio-button value="pathValue">完整节点</a-radio-button>
        <a-radio-button value="function">运行时函数判断</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="" class="display-b" :label-col="{}" :wrapper-col="{}">
      <WidgetCodeEditor
        v-if="widget.configuration.filterNode[type + 'Type'] == 'function'"
        v-model="widget.configuration.filterNode[type + 'Function']"
        lang="js"
        width="600px"
        height="500px"
        :hideError="true"
        helpTipWidth="700px"
        :zIndex="1009"
      >
        <div slot="help" class="help-tip-content">
          <div>支持使用函数返回是否显示节点, 入参传入的是node参数。支持参数:</div>
          <div class="flex f_wrap help-tip-content-list">
            <div v-for="(item, i) in tipParamList">
              {{ item.label }}
              <a-tag @click="e => onClickCopy(e, item.value)" :title="'点击复制：' + item.value">{{ item.value }}</a-tag>
            </div>
          </div>
          <div>
            节点类型（String）：
            <a-tag @click="e => onClickCopy(e, 'node.type')">node.type</a-tag>
            <div v-if="typeOptions.length" style="margin-left: 40px; margin-top: 8px" class="flex f_wrap">
              <div v-for="(opt, i) in typeOptions" style="margin: 0 8px 8px 0">
                {{ opt.label }}:
                <a-tag @click="e => onClickCopy(e, opt.value)" class="w-ellipsis" style="max-width: 100px; vertical-align: middle">
                  {{ opt.value }}
                </a-tag>
              </div>
            </div>
          </div>
          <div class="pt-tip-block" v-if="type == 'show'">
            返回说明：如果return 表达式，返回的结果是true, 参数node对应节点显示在树的第一层
          </div>
          <div class="pt-tip-block" v-else>返回说明：如果return 表达式，返回的结果是true, 参数node对应节点及其下级节点隐藏</div>
          <div>例如：return node.type == 'dept';</div>
          <div class="pt-tip-block" v-if="type == 'show'">结果：所有节点类型是部门的节点，都显示在树的第一层</div>
          <div class="pt-tip-block" v-else>结果：所有节点类型是部门的节点及其下级节点隐藏</div>
        </div>
        <a-button icon="code" style="float: right">编写代码</a-button>
      </WidgetCodeEditor>
      <OrgSelect
        v-else
        ref="filterNodeRef"
        :isPathValue="widget.configuration.filterNode[type + 'Type'] == 'pathValue'"
        :orgUuid="widget.configuration.orgUuid"
        v-model="widget.configuration.filterNode[type + 'Data']"
        :orgIdOptions="orgIdOptions"
        :orgType="enableOrgSelectTypes"
        :onlyShowBizOrg="widget.configuration.onlyShowBizOrg === true"
      />
    </a-form-model-item>
  </div>
</template>
<script type="text/babel">
import { copyToClipboard } from '@framework/vue/utils/util';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
export default {
  name: 'WidgetFormOrgSelectFilterNodeConfiguration',
  components: {
    OrgSelect
  },
  props: {
    widget: Object,
    label: {
      type: String,
      default: '显示节点'
    },
    type: {
      type: String,
      default: 'show'
    },
    // 节点类型
    typeOptions: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    orgIdOptions() {
      let map = {};
      if (this.widget.configuration.orgId != undefined) {
        map[this.widget.configuration.orgId] = this.widget.configuration.bizOrgIds || [];
      }
      return map;
    },
    enableOrgSelectTypes() {
      let opt = [];
      for (let i = 0, len = this.widget.configuration.orgSelectTypes.length; i < len; i++) {
        if (this.widget.configuration.orgSelectTypes[i].enable !== false) {
          opt.push(this.widget.configuration.orgSelectTypes[i]);
        }
      }
      return opt;
    }
  },
  data() {
    let typeData = this.widget.configuration.filterNode[this.type + 'Type'];
    return {
      typeData,
      tipParamList: [
        {
          label: '节点具体值（Object）：',
          value: 'node.data'
        },
        {
          label: '节点ID（String）：',
          value: 'node.key'
        },
        {
          label: '完整节点ID（String）：',
          value: 'node.keyPath'
        },
        {
          label: '节点名称（String）：',
          value: 'node.title'
        },
        {
          label: '节点完整名称（String）：',
          value: 'node.titlePath'
        },
        {
          label: '节点简称（String）：',
          value: 'node.shortTitle'
        },
        {
          label: '节点是否有下级节点（Boolean）：',
          value: 'node.isLeaf'
        },
        {
          label: '节点下级节点（Array）：',
          value: 'node.children'
        },
        {
          label: '节点是否可选（Boolean）：',
          value: 'node.checkable'
        }
      ]
    };
  },
  created() {},
  mounted() {},
  methods: {
    onChangeOrgUuid() {
      if (this.$refs.filterNodeRef) {
        if (this.widget.configuration.orgUuid) {
          this.$refs.filterNodeRef.clearAllInput();
          this.$refs.filterNodeRef.currentOrgUuid = this.widget.configuration.orgUuid;
          this.$refs.filterNodeRef.orgSelectTreeData.splice(0, this.$refs.filterNodeRef.orgSelectTreeData.length);
          this.onChangeOnlyShowBizOrg();
        } else {
          this.$refs.filterNodeRef.orgSelectTreeData.splice(0, this.$refs.filterNodeRef.orgSelectTreeData.length);
          this.$refs.filterNodeRef.getOrgSelectOptions();
        }
      }
    },
    onChangeColorType() {
      this.widget.configuration.filterNode[this.type + 'Type'] = this.typeData;
      if (
        this.widget.configuration.filterNode[this.type + 'Type'] != 'function' &&
        this.widget.configuration.filterNode[this.type + 'Data']
      ) {
        this.$nextTick(() => {
          this.$refs.filterNodeRef.emitValueChange();
        });
      }
    },
    onClickCopy(e, text) {
      let _this = this;
      copyToClipboard(text, e, function (success) {
        if (success) {
          // message不支持修改样式，代码编辑组件弹框widget-code-editor层级为2000，导致message提示框显示在遮罩下面
          _this.$message.success({
            content: '已复制'
          });
        }
      });
    },
    onChangeOnlyShowBizOrg() {
      this.$refs.filterNodeRef
        .getOrg({
          orgUuid: this.$refs.filterNodeRef.currentOrgUuid,
          fetchBizOrg: true
        })
        .then(data => {
          this.$refs.filterNodeRef.setBizOrgSelectTreeDataByOrg(data.organization);
        });
    }
  }
};
</script>
<style lang="less" scoped>
.help-tip-content {
  --w-tag-border-radius: 4px;
  > * {
    margin-bottom: 12px;
  }
  .help-tip-content-list {
    margin-bottom: 4px;
    > * {
      margin-bottom: 8px;
    }
  }
}
</style>
