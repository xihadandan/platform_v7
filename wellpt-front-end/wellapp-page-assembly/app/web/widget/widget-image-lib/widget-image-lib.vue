<template>
  <a-modal
      title="选择图片"
      :width="780"
      :bodyStyle="{ height: '400px', 'overflow-y': 'auto' }"
      :visible="visible"
      @cancel="$emit('cancel')"
      @ok="onOk"
      :mask="false"
      :maskClosable="false"
    >
    <div>
      <a-tabs tabPosition="top">
          <a-tab-pane key="1" tab="图片上传">
            <div>
              <a-upload
                :multiple="false"
                action="/proxy-repository/repository/file/mongo/saveFilesAndPushToFolder?folderID=pictureL-pict-pict-pict-pictureLibpi"
                list-type="picture"
                :default-file-list="fileList"
                @change="onFileChange"
              >
                <a-button> <a-icon type="upload" /> 选择图片 </a-button>
              </a-upload>
              <br />
            </div>
          </a-tab-pane>
          <a-tab-pane key="2" tab="图片库">
            <a-row>
              <a-col span="6">
                <a-tree
                  :tree-data="imageFolders"
                  :replace-fields="replaceFields"
                  @select="onSelect"
                />
              </a-col>
              <a-col span="18">
                <a-row v-for="row in localImgRow" :key="row">
                  <a-col v-for="col in 3" :key="col" span="8">
                    <div class="thumbnail" v-if="localImages && localImages.length > 0 && localImages[(row - 1) * 3 + (col - 1)]">
                      <img :src="'/static/images' + localImages[(row - 1) * 3 + (col - 1)].id" alt="" style="max-height: 150px; max-width: 100;background-color:#ddd">			
                      <div class="caption">					
                        <input :id="'checkbox_' + row + '_' + col" type="checkbox" 
                          :value="'/static/images' + localImages[(row - 1) * 3 + (col - 1)].id" 
                          :checked="'/static/images' + localImages[(row - 1) * 3 + (col - 1)].id == image"
                          @change="localImageCkeckboxChange($event, localImages[(row - 1) * 3 + (col - 1)].id)">		
                        <label :for="'checkbox_' + row + '_' + col">{{localImages[(row - 1) * 3 + (col - 1)].id}}</label>			
                      </div>
                    </div>
                  </a-col>
                </a-row>
              </a-col>
            </a-row>
          </a-tab-pane>
      </a-tabs>
    </div>
  </a-modal>
</template>
<script type="text/babel">
export default {
  name: 'WidgetImageLib',
  props: {
    visible: Boolean,
    tabPaneHeight: {
      type: Number,
      default: 600
    }
  },
  data() {
    return { 
      imageFolders: [],
      localImages: [],
      localImgRow: 0,
      localImageCheckedValue: '',
      replaceFields: {
        key: "id",
        title: "name"
      },
      fileList: [],
      image: ""
    };
  },
  watch: {},
  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    var _self = this;
    $axios.get("/web/resource/getProjectImagFolders").then(function(result) {
      _self.imageFolders = result.data;
    });
  },
  methods: {
    onFileChange: function(info) {
      var _self = this;
      if (info.file.status === 'done') {
        info.fileList.reverse();
        info.fileList.length = 1;
        var fileInfo = info.file.response.data[0];
        var fileID = fileInfo.fileID;
        _self.image = "/repository/file/mongo/download?preview=true&fileID=" + fileID;
      }
    },
    onSelect: function(ids) {
      var _self = this;
      var selectedId = ids[0];
      var treeNodes = _self.imageFolders;
      var treeNode = _self.getTreeNode(selectedId, treeNodes);
      if(treeNode != null) {
        _self.localImages = treeNode.data.__imgs || [];
      } else {
        _self.localImages = [];
      }
      var length = _self.localImages.length;
      if(length % 3 == 0) {
        _self.localImgRow = length / 3;
      } else {
        _self.localImgRow = Math.ceil(length / 3);
      }
    },
    getTreeNode: function(id, treeNodes) {
      var _self = this;
      if(treeNodes == null || treeNodes.length == 0) {
        return null;
      }
      var treeNode = null;
      for(var i = 0; i < treeNodes.length; i++) {
        var node = treeNodes[i];
        treeNode = _self.getTreeNode(id, node.children);
        if(treeNode != null) {
          break;
        }
        if(node.id == id) {
          treeNode = node;
          break;
        }
      }
      return treeNode;
    },
    localImageCkeckboxChange: function(event, value) {
      this.image = '/static/images' + value;
    },
    onOk: function() {
      this.fileList = [];
      this.$emit("ok", this.image);
    }
  },
  mounted() {}
};
</script>

<style scoped>
.edit-icon-ul {
  margin: 10px 0;
  list-style: none;
}

ul.edit-icon-ul li {
  float: left;
  width: 150px;
  height: 60px;
  text-align: center;
  list-style: none;
  cursor: pointer;
  color: #555;
  transition: color 0.3s ease-in-out, background-color 0.3s ease-in-out;
  position: relative;
  margin: 3px 0;
  border-radius: 4px;
  background-color: #fff;
  overflow: hidden;
  padding: 10px 0 0;
}

ul.edit-icon-ul li:hover {
  background-color: #1890ff;
  color: #fff;
}

ul.edit-icon-ul li .icon-class {
  display: block;
  text-align: center;
  transform: scale(0.83);
  font-family: Lucida Console, Consolas;
  white-space: nowrap;
}

.thumbnail {
    display: block;
    padding: 4px;
    margin-bottom: 20px;
    line-height: 1.43;
    background-color: #f0f3f4;
    border: 1px solid #ddd;
    border-radius: 4px;
    transition: border 0.2s ease-in-out;
}
</style>
