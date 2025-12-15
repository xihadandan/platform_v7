<script type="text/jsx">
import { filter, map, each, findIndex, sortBy } from 'lodash';

export default {
  name: 'WTreeSelect',
  inject: ['widgetContext'],
  props: {
    treeData: {
      type: Array,
      default: () => []
    },
    value: [String,Array],
    editMode:{
      type: Object,
      default: () => {}
    },
    multiple: {
      type: Boolean,
      default: false
    },
    dropdownStyle: {
      type: Object,
      default: () => {
        return {
          maxHeight: '400px', overflow: 'auto'
        }
      }
    },
    treeNodeFilterProp: {
      type: String,
      default: 'title'
    },
    treeNodeLabelProp: {
      type: String,
      default: 'label'
    },
    showCheckedStrategy: {
      type: String,
      default: 'SHOW_CHILD'
    },
    placeholder: String,
    disabled: {
      type: Boolean,
      default: false
    },
    // true时父子节点选中状态不再关联
    treeCheckStrictly: {
      type: Boolean,
      default: false
    },
    //下拉菜单和选择器同宽
    dropdownMatchSelectWidth: {
      type: Boolean,
      default: true
    },
    dropdownClassName: {
      type: String,
      default: 'ps__child--consume'
    },
    renderTitle:{
      type: Function,
      default:undefined
    },
    getPopupContainer:{
      type: Function,
      default:(triggerNode) => {
        if(triggerNode.closest('.widget-subform')) {
          return triggerNode.closest('.widget-subform');
        }
        return  triggerNode.parentNode
      }
    },
    dataSourceLoadEveryTime:Boolean
  },
  data() {
    return {
      valueLabelMap: {},
      isSelectAll: false,
      selectAllVal:"tree_selecta_all",
      allOptions:[], // 全部选项
      allNodesKeys: [],
      treeExpandedKeys: [],
      isExpand: false
    }
  },
  computed: {
    checkableParent(){
      if(this.showCheckedStrategy == "SHOW_CHILD"){
        return false;
      }
      return true;
    }
  },
  watch: {
  },
  methods: {
    $t() {
      if (this.widgetContext != undefined) {
        return this.widgetContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    onChange(value, label, extra) {
      this.$emit('change', ...arguments);
      if(this.editMode.selectCheckAll){
        if(this.allOptions.length > value.length){
          this.isSelectAll = false;
        }
      }
    },
    onSelect(value, node, extra) {
      this.$emit('select', ...arguments);
    },
    onFocus() {
      this.$emit('focus');
    },
    onBlur() {
      this.$emit('blur');
    },
    blur(){},
    onSelectSearch(v){
      this.$emit('search',v);
      if(v){
        this.onExpandChange(false);
      }else{
        if(!this.isExpand){
          this.onCollapseChange();
        }
      }
    },
    getValueLabelMap() {
      return this.valueLabelMap
    },
    // 全选
    onChangeAll(){
      if(this.isSelectAll){
        this.isSelectAll = false;
        this.onChange([],[]);
      }else{
        this.setSelectAll();
        this.isSelectAll = true;
      }
    },
    setSelectAll(){
      this.getAllOptions(()=>{
        if(!this.treeCheckStrictly){
          // 父子关联
          this.onChange(map(this.allOptions,"value"),map(this.allOptions,"label"));
        }else{
          this.onChange(this.allOptions,map(this.allOptions,"label"));
        }
      });
      this.getAllOptions();
    },
    setOptionItem(item){
        const key = item.s_tree_key;
        const value = item.s_tree_value;
        const title = item.s_tree_title;
        const label = item.s_tree_label;
        return {
          key:key,
          value:value,
          title:title,
          label:label
        }
    },
    //全部选中时选项
    getAllOptions(callback){
      if(this.checkableParent){
        // 可选中父级
        let keys = [];
        keys = this.convertTreeData(JSON.parse(JSON.stringify(this.treeData)));
        while(findIndex(keys,"children") > -1){
          keys = this.convertTreeData(keys);
        }
        this.allOptions = map(keys,(item)=>{
          return this.setOptionItem(item);
        });
      }else{
        // 不可选中父级,只取叶子
        let keys = {};
        keys = this.convertTreeLeafData(JSON.parse(JSON.stringify(this.treeData)),[]);
        while(findIndex(keys.newOptions,"children") > -1){
          keys = this.convertTreeLeafData(keys.newOptions,keys.leafOptions);
        }
        keys.leafOptions = keys.leafOptions.concat(keys.newOptions);
        this.allOptions = map(keys.leafOptions,(item)=>{
          return this.setOptionItem(item);
        });
      }
      if(typeof(callback) === "function"){
        callback();
      }
    },
    //全部节点
    getAllNodeKeys(callback){
      let keys = [];
      let nodeKey = "0-"
      keys = this.convertTreeNodeData(JSON.parse(JSON.stringify(this.treeData)),[],nodeKey);
      while(findIndex(keys.newOptions,"children") > -1){
        keys = this.convertTreeNodeData(keys.newOptions, keys.completeOptions);
      }
      this.allNodesKeys = map(keys.completeOptions,(item)=>{
        let key = item.s_tree_key;
        return {nodeKey:item.nodeKey,key:key}
      });
      if(typeof(callback) === "function"){
        callback();
      }
      return this.allNodesKeys;
    },
    convertTreeNodeData(menuOptions, completeOptions, key) {
      let newOptions = [];
      let initIndex = 0;
      if(key){
        if(this.editMode.selectCheckAll){
          initIndex ++;
        }
        if(this.editMode.allCollapse){
          initIndex ++;
        }
      }
      for (let i = 0; i < menuOptions.length; i++){
        if(key){
          menuOptions[i].nodeKey = key + (i+ initIndex);
        }
        if (menuOptions[i].children && menuOptions[i].children.length) {
          each(menuOptions[i].children,(item, index)=>{
            item.nodeKey = menuOptions[i].nodeKey + "-" + index;
          })
          newOptions = newOptions.concat(menuOptions[i].children);
          delete menuOptions[i].children;
        }
        completeOptions.push(menuOptions[i]);
      }
      return {newOptions: newOptions, completeOptions: completeOptions};
    },
    convertTreeData(menuOptions) {
      for (let i = 0; i < menuOptions.length; i++){
        if (menuOptions[i].children != undefined) {
          const temp = menuOptions[i].children;
          if(menuOptions[i].children.length){
            menuOptions = menuOptions.concat(temp);
          }
          delete menuOptions[i].children;
        }
      }
      return menuOptions;
    },
    convertTreeLeafData(menuOptions, leafOptions) {
      let newOptions = [];
      for (let i = 0; i < menuOptions.length; i++){
        if (menuOptions[i].children && menuOptions[i].children.length) {
          newOptions = newOptions.concat(menuOptions[i].children);
        }else{
          leafOptions.push(menuOptions[i])
        }
      }
      return {newOptions:newOptions, leafOptions:leafOptions};
    },
    // 展开
    onExpandChange(change){
      if(change){
        this.isExpand = true;
      }
      this.treeExpandedKeys = map(this.getAllNodeKeys(),"nodeKey");
    },
    // 收起
    onCollapseChange(){
      this.isExpand = false;
      this.treeExpandedKeys = [];
      this.$nextTick(()=>{
        //  延迟执行，等待收起动画结束,再次渲染
        setTimeout(()=>{
          this.treeExpandedKeys = [];
        },100)
      })
    },
    treeExpand(treeExpandedKeys){
      this.$emit('treeExpand', ...arguments);
      if(this.editMode.allCollapse){
        this.treeExpandedKeys = treeExpandedKeys;
      }
    },
    onClick(){
      if(this.dataSourceLoadEveryTime && !this.$children[0].$refs.vcTreeSelect.$data._open){
        this.$emit("reloadTreeData")
      }
    }
  },
  render(){
    let {
      treeData,
      value,
      editMode,
      multiple,
      dropdownStyle,
      treeNodeFilterProp,
      treeNodeLabelProp,
      showCheckedStrategy,
      placeholder,
      disabled,
      treeCheckStrictly,
      dropdownMatchSelectWidth,
      dropdownClassName,
      renderTitle,
      getPopupContainer
    } = this.$props;

    let renderChildren = (data) => {
      return data.map(child => {
        const key = child.s_tree_key;
        const value = child.s_tree_value;
        const title = child.s_tree_title;
        const label = child.s_tree_label;
        // 父级不可选时，父级选择框隐藏
        const checkable = this.checkableParent ? true: !(child.children && child.children.length);
        this.valueLabelMap[value] = title;
        const nodeProp = {
          props: {
            key,
            value,
            title: renderTitle?renderTitle(child):()=>{
              if(child.s_tree_extend){
                return <div>{title}<span class='tree_extend_column'>{child.s_tree_extend}</span></div>;
              }
              return title;
            },
            label,
            checkable,
            selectable: checkable
          }
        }
        return child.children ?
          (<a-tree-select-node {...nodeProp }>
            {renderChildren(child.children)}
          </a-tree-select-node>) : (<a-tree-select-node {...nodeProp} />)
      })
    }

    let renderExpand = () => {
      if(editMode.allCollapse){
        const nodeProp = {
          props: {
            key:"tree_selecta_expand",
            value:"tree_selecta_expand",
            checkable: false,
            selectable: false
          }
        }
        const expandProp = {
          on:{
            click:this.onExpandChange
          }
        }
        const collapseProp = {
          on:{
            click:this.onCollapseChange
          }
        }
        let expandLabel = this.$t('WidgetFormSelect.expand', '展开'),collapseLabel = this.$t('WidgetFormSelect.collapse', '收起');
        return <a-tree-select-node {...nodeProp }>
            <div slot = "title" class="tree_selecta_expand">
              <span {...expandProp }>{expandLabel}</span>|<span {...collapseProp }>{collapseLabel}</span>
            </div>
            </a-tree-select-node>
      }
    }

    let renderSelectAll = () => {
      if(editMode.selectCheckAll){
        const nodeProp = {
          props: {
            key:"tree_selecta_all",
            value:"tree_selecta_all",
            checkable: false,
            selectable: false
          }
        }
        const titleProp = {
          props: {
            checked:this.isSelectAll
          },
          on:{
            change:this.onChangeAll
          }
        }
        let selectAll =  this.$t('WidgetFormSelect.selectAll', '全选') ;
        return <a-tree-select-node {...nodeProp }>
            <template slot = "title">
              <a-checkbox {...titleProp}>{selectAll}</a-checkbox>
            </template>
            </a-tree-select-node>
      }
    }
    let filterTreeNode = (inputValue, treeNode)=>{
      if (!inputValue) return false;
      let label = (treeNode && treeNode.data.props.label) || "";
      let isFilter = label.indexOf(inputValue) > -1;
      return isFilter;
    }
    // const getPopupContainer = (triggerNode) => {
    //   if(triggerNode.closest('.widget-subform')) {
    //     return triggerNode.closest('.widget-subform');
    //   }
    //   return  triggerNode.parentNode
    // }
    const treeProps = {
      props: {
        value,
        multiple,
        treeCheckable:multiple,
        allowClear: editMode.allowClear,
        showSearch: editMode.showSearch,
        // treeDefaultExpandAll: editMode.allCollapse,
        treeNodeFilterProp,
        treeNodeLabelProp,
        dropdownStyle,
        showCheckedStrategy,
        placeholder,
        disabled,
        getPopupContainer,
        showArrow: true,
        treeCheckStrictly,
        dropdownMatchSelectWidth,
        dropdownClassName,
        filterTreeNode
      },
      on: {
        focus: this.onFocus,
        blur: this.onBlur,
        change: this.onChange,
        select: this.onSelect,
        treeExpand: this.treeExpand,
        search: this.onSelectSearch,
        click: this.onClick
      }
    }
    if(editMode.allCollapse){
      treeProps.props.treeExpandedKeys = this.treeExpandedKeys;
    }
    return (
      <a-tree-select
        {...treeProps}
      >
        {renderExpand()}
        {renderSelectAll()}
        {renderChildren(treeData)}
      </a-tree-select>
    )
  }
}
</script>
