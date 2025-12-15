JSLang_zh_CN = {
  FLOW: {
    WF_TASKNAME: '开始|结束',
    WF_BEGINTASKNAME: '流程开始',
    WF_ENDTASKNAME: '流程结束',
    WF_BEELINE_NAME: '直线',
    WF_CURVE_NAME: '曲线',
    WF_TASK_NAME: '环节',
    WF_SUBFLOW_NAME: '子流程',
    WF_FREETASK_NAME: '自动环节',
    WF_CONDITION_NAME: '判断点',
    WF_BEGIN_NAME: '开始',
    WF_END_NAME: '结束',
    WF_LABEL_NAME: '标签',
    WF_ADDOBJECT: '添加对象：',
    WF_LOADING: '正在导入流程数据...',
    WF_FLOWSAVING: '正在保存流程数据...',
    WF_TASKSAVING: '正在保存环节数据...',
    WF_LINESAVING: '正在保存流向数据...',
    WF_LABELSAVING: '正在保存文字标签数据...',
    WF_SAVESUCCESS: '流程数据保存成功！',
    WF_SAVEFAILURE: '流程数据保存失败！',
    WF_ERRORDOWNLOAD: '流程数据下载错误，请重新打开！',
    WF_FLOWPROPERTYISEMPTY: '请设置流程属性！',
    WF_FLOWNAMEISEMPTY: '请输入流程名称！',
    WF_FLOWALIASISEMPTY: '请输入流程ID！',
    WF_FLOWSYSTEMUNITIDISEMPTY: '请选择归属系统单位！',
    WF_FLOWORGVERSIONIDISEMPTY: '请选择默认组织版本！',
    WF_FLOWALIASISRULE: '流程ID只能包含英文大小写字母、整数和下划线！',
    WF_FLOWTYPEISEMPTY: '请选择流程分类！',
    WF_BEGINISMORE: '不能创建多个开始节点！',
    WF_NOBEGIN: '流程必须有开始节点！',
    WF_NOENDOBJECT: '非自由流程必须有结束节点或者转入子流程节点！',
    WF_BEGINOUTLINEISMORE: '开始节点的流向不可能同时指向两个或者更多的目标节点！',
    WF_NOBEGINOUTLINE: '开始节点没有流向指向目标节点！',
    WF_BEGINLINEERROR: '开始节点不能作为流向的目标节点！',
    WF_SETTINGERROR: '图上显示选中的对象设置存在问题！',
    WF_SETTINGTRUE: '流程设计检查通过！',
    WF_FORMNAMEISEMPTY: '请在流程属性中指定应用表单！',
    WF_EQFLOWMOREOBJECT: '等价流程不需要除文字标签以外的其他对象！',
    WF_ENDOBJECTEXISTOUTLINE: '流程的结束节点或者子流程节点存在指向其他节点的流向！',
    WF_ENDOBJECTMOREINLINE: '流程的结束节点或者子流程节点有且仅有一个来源流向！',
    WF_OBJECTOUTLINEISEMPTY: '流程节点必须有指向其他节点的流向！',
    WF_LABELHTMLISEMPTY: '文字标签必须输入内容！',
    WF_TASKPROPERTYERROR: '环节属性存在问题：名称必须有且唯一，流程ID必须有且唯一！',
    WF_TASKPROPERTY_SETUSER_ERROR: '环节属性存在问题：当办理人为指定办理人时不能为空！',
    WF_TASKPROPERTY_SETCOPYUSER_ERROR: '环节属性存在问题：指定抄送人时不能为空！',
    WF_TASKPROPERTY_SETTRANSFERUSER_ERROR: '环节属性存在问题：指定转办范围不能为空！',
    WF_TASKPROPERTY_SETMONITOR_ERROR: '环节属性存在问题：当办理人为指定督办人时不能为空！',
    WF_TASKMOREOUTLINEFORCD: '当环节为条件分支起始点时不能有多个流向指向其他节点！',
    WF_SUBFLOWPROPERTYERROR: '子流程属性存在问题：名称必须有且唯一，别名必须有且唯一，且必须指定子流程！',
    WF_SUBFLOWMOREOUTLINEFORCD: '当子流程为条件分支起始点时不能有多个流向指向其他节点！',
    WF_CONDICTIONERROR: '判断点必须有名称，且只能有一个指向它的流向，而且这个流向必须从环节节点或者子流程结点出发！',
    WF_LINEPROPERTYERROR: '流向属性设置存在问题：必须有名称，当现在确定归档设置时归档数据库不能为空！',
    WF_LINESOURCEERRORFORCD: '条件分支流向来源节点必须是判断点！',
    WF_LINETOTASKERRORFORBEGIN: '开始节点不能立即指向结束节点、子流程节点或者判断点！',
    WF_CLOSECONFIRM: '将关闭图形化工作流编辑器，请确认是否已经保存且继续？',
    WF_LOADFINISH: '--流程数据导入完成！',
    WF_TASKPLAYING: '开始播放流转历史动画！',
    WF_TASKPLAYED: '流转历史动画播放结束！',
    WF_INFONAMEISEMPTY: '请输入信息名称！',
    WF_INFOFIELDISEMPTY: '请指定信息存储域名！',
    WF_INFOFORMATISEMPTY: '请输入信息格式！',
    WF_INFONAMEISEXIST: '信息名称已存在，请重新指定！',
    WF_PROMPTFLOWCATEGORY: '选择流程分类',
    WF_CONFIRMNEWVERSION: '是否保存为新的版本？',
    WF_NEWVERSION: '新版本',
    WF_CONFIRMUPDATEWORK: '对应流程的更动是否更新正在流转的工作？',
    WF_UPDATEWORK: '更新工作',
    WF_CONFIRMUPDATETIME: '是：现在立即更新？\n否：夜间任务更新？',
    WF_UPDATETIME: '更新时间',
    WF_SENDTOTASK: '送',
    WF_FLOWFORMISEMPTY: '请选择流程使用表单！',
    WF_FLOWMODULEISEMPTY: '请选择流程所属模块！',
    WF_FLOWIDISEXIT: '流程ID已存在,请重新输入！',
    WF_LINEBTNORDEREMPTY: '流向按钮排序号不能为空！',
    WF_LINEBTNCLASSNAMEEMPTY: '流向按钮样式不能为空！'
  }
};
function sGetLang(psID, pvValue) {
  if (psID == null || psID == '') return null;
  var rsRetValue = null;
  var laValue = typeof pvValue == 'string' ? new Array(pvValue) : pvValue;
  var lvArguments = sGetLang.arguments;
  if (lvArguments.length > 2) {
    if (laValue == null) laValue = new Array();
    for (var i = 2; i < lvArguments.length; i++) laValue[laValue.length] = lvArguments[i];
  }
  try {
    rsRetValue = eval('JSLang_zh_CN.' + psID.substring(0, psID.indexOf('_')) + '.' + psID.substring(psID.indexOf('_') + 1, psID.length));
  } catch (e) {
    return null;
  }
  if (laValue == null) return rsRetValue;
  if (rsRetValue != null) {
    var i = 0,
      lsRetValue = rsRetValue;
    rsRetValue = '';
    while (lsRetValue.indexOf('ID_VARIABLE') != -1) {
      rsRetValue += lsRetValue.substr(0, lsRetValue.indexOf('ID_VARIABLE')) + (laValue[i] == null ? 'ID_VARIABLE' : laValue[i]);
      lsRetValue = lsRetValue.substr(lsRetValue.indexOf('ID_VARIABLE') + 'ID_VARIABLE'.length);
      i = i + 1;
    }
    rsRetValue += lsRetValue;
  }
  return rsRetValue;
}
