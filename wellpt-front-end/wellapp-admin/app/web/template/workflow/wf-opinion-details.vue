<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="formData" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }" :rules="rules" ref="form">
      <a-form-model-item label="名称" prop="content">
        <a-input v-model="formData.content" :maxLength="50" />
      </a-form-model-item>
      <a-form-model-item label="编号" prop="code">
        <a-input v-model="formData.code" :maxLength="50" />
      </a-form-model-item>
      <a-form-model-item label="所属分类" prop="opinionCategoryUuid">
        <a-tree-select
          v-model="formData.opinionCategoryUuid"
          style="width: 100%"
          :dropdown-style="{ maxHeight: '300px', overflow: 'auto' }"
          :tree-data="categoryTreeData"
          :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
          placeholder="请选择"
        ></a-tree-select>
      </a-form-model-item>
    </a-form-model>
  </a-skeleton>
</template>

<script>
export default {
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event || {};
    let formData = $event.meta || {};
    let uuid = formData.uuid;
    return {
      loading: !!uuid,
      uuid,
      formData,
      rules: {
        content: { required: true, message: '不能为空', trigger: ['blur', 'change'] },
        opinionCategoryUuid: { required: true, message: '不能为空', trigger: ['blur', 'change'] }
      },
      categoryTreeData: []
    };
  },
  created() {
    if (this.uuid) {
      this.loadFormData();
    }
    this.loadCategories();
  },
  methods: {
    loadCategories() {
      $axios
        .post('/json/data/services', {
          serviceName: 'flowOpinionCategoryService',
          methodName: 'getFlowOpinionCategoryTreeByBusinessAppDataDic',
          args: JSON.stringify(['true'])
        })
        .then(({ data: result }) => {
          if (result.data) {
            let treeData = result.data.children || [];
            this.categorySelectable(treeData);
            this.categoryTreeData = treeData;
          }
        });
    },
    categorySelectable(nodes) {
      nodes.forEach(node => {
        if (node.data) {
          node.selectable = false;
        } else {
          node.selectable = true;
        }
        if (node.children) {
          this.categorySelectable(node.children);
        }
      });
    },
    loadFormData() {
      const _this = this;
      debugger;
      _this.loading = true;
      $axios
        .get(`/proxy/api/workflow/opinion/get?uuid=${this.uuid}`)
        .then(({ data: result }) => {
          if (result.data) {
            _this.formData = result.data;
          } else {
            _this.$message.error(result.msg || '服务异常！');
          }
          _this.loading = false;
        })
        .catch(({ response }) => {
          _this.loading = false;
          _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
        });
    },
    save(evt) {
      const _this = this;
      _this.$refs.form.validate().then(valid => {
        if (valid) {
          $axios
            .post('/proxy/api/workflow/opinion/save', _this.formData)
            .then(({ data: result }) => {
              if (result.code == 0) {
                _this.$message.success('保存成功！');
                _this.$event && _this.$event.$evtWidget && _this.$event.$evtWidget.refetch && _this.$event.$evtWidget.refetch(true);
                evt.$evtWidget.closeModal();
              } else {
                _this.$message.error(result.msg || '保存失败！');
              }
            })
            .catch(({ response }) => {
              _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
            });
        }
      });
    }
  },
  META: {
    method: {
      save: '保存意见'
    }
  }
};
</script>

<style></style>
