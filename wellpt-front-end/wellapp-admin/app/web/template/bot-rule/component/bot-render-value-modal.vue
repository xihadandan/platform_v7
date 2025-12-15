<template>
  <a-modal :title="title" width="950px" :visible="visible" :destroyOnClose="true" @ok="handleOk" @cancel="handleCancel">
    <a-form-model v-if="data" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
      <a-form-model-item label="计算方式">
        <a-radio-group v-model="type" :default-value="-1">
          <a-radio :value="-1">不设置</a-radio>
          <a-radio :value="0">freemarker表达式</a-radio>
          <a-radio :value="1">groovy脚本</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item v-show="type != -1" label="内容">
        <WidgetCodeEditor v-model="expression" width="auto" height="200px"></WidgetCodeEditor>
        <div v-if="type == 0" class="remark" v-html="freemarkerScriptRemark"></div>
        <div v-if="type == 1" class="remark" v-html="groovyScriptRemark"></div>
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';

let freemarkerScriptRemark = `1、freemarker支持的内置变量：<br/>
        sourceValue: 表示源字段值，freemarker表达式可通过\${"\${"}sourceValue}访问<br/>
        targetValue: 表示目标字段值，该值仅在字段反写规则内可取到值。freemarker表达式可通过\${"\${"}targetValue}访问
        formData: 表示所有源数据。如果源数据是表单，则表示表单数据，可以通过formData.表单ID.表单字段的方式取值，例如：<br/>
        \${"\${"}formData.uf_oa_book.book_name}；如果源数据是JSON报文对象，则通过data访问json数据对象，可以通过data.报文字段的方式取值，例如JSON报文为{"name":"test","code":100}，取值name通过\${"\${"}data.name}<br/>
        targetFormData: 表单单据数据，表示目标单据数据，可以通过targetFormData.表单ID.表单字段的方式取值，例如： targetFormData.uf_oa_book.book_name<br/>
        2、其他系统默认的变量：<br/>
        currentUserName ：当前用户的名称<br/>
        currentLoginName ：当前用户的登录名<br/>
        currentUserId ：当前用户ID<br/>
        currentUserUnitId ：当前用户归属的组织ID<br/>
        currentUserUnitName ：当前用户归属的组织名称<br/>
        currentUserDepartmentId ：当前用户归属的部门ID<br/>
        currentUserDepartmentName ：当前用户归属的部门名称<br/>
        sysdate ：当前时间。\${"\${"}sysdate?datetime}获取当前完整的时间格式
      `;
let groovyScriptRemark = `1、groovy支持的内置变量：<br/>
          sourceValue: 表示源字段值，groovy脚本直接可以在脚本内使用sourceValue变量<br/>
          targetValue: 表示目标字段值，groovy脚本直接可以在脚本内使用targetValue变量<br/>
          data: 表示所有源数据。如果源数据是表单，则表示表单数据，可以通过data.表单ID.表单字段的方式取值，例如：<br/>
          data.uf_oa_book.book_name；如果源数据是JSON报文对象，则表示JSON对象，可以通过data.报文字段的方式取值，例如JSON报文为{"name":"test","code":100}，取值name通过data.name<br/>
          targetFormData: 表单单据数据，表示目标单据数据，可以通过targetFormData.表单ID.表单字段的方式取值，例如： targetFormData.uf_oa_book.book_name<br/>
          jsonBody: 表示源数据是JSON报文对象，可以通过jsonBody.报文字段的方式取值，例如JSON报文为{"name":"test","code":100}，取值name通过jsonBody.name<br/>
          2、其他系统默认的变量：<br/>
          currentUserName ：当前用户的名称<br/>
          currentLoginName ：当前用户的登录名<br/>
          currentUserId ：当前用户ID<br/>
          currentUserUnitId ：当前用户归属的组织ID<br/>
          currentUserUnitName ：当前用户归属的组织名称<br/>
          currentUserDepartmentId ：当前用户归属的部门ID<br/>
          currentUserDepartmentName ：当前用户归属的部门名称<br/>
          sysdate ：当前时间
        `;
export default {
  props: {
    title: {
      type: String,
      default: '编辑计算规则'
    },
    visible: {
      type: Boolean,
      default: false
    },
    data: {
      type: Object,
      data() {
        return { renderValueType: -1, renderValueExpression: '' };
      }
    }
  },
  components: { WidgetCodeEditor },
  data() {
    let data = this.data || {};
    return {
      type: data.renderValueType,
      expression: data.renderValueExpression,
      freemarkerScriptRemark,
      groovyScriptRemark
    };
  },
  watch: {
    data: {
      deep: true,
      handler(newVal) {
        if (newVal) {
          this.type = newVal.renderValueType;
          this.expression = newVal.renderValueExpression;
        }
      }
    }
  },
  methods: {
    handleOk() {
      this.$emit('ok', { renderValueType: this.type, renderValueExpression: this.expression });
    },
    handleCancel() {
      this.$emit('cancel', { renderValueType: this.type, renderValueExpression: this.expression });
    }
  }
};
</script>

<style lang="less" scoped>
.remark {
  line-height: 1.5;
  font-size: 14px;
}
</style>
