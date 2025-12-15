goWorkFlow = parent.goWorkFlow;
//设置条件属性
function setConditionProperty() {
  top.appModal.hideMask();
  if (goWorkFlow.curTaskObj) {
    var conditionXML = goWorkFlow.curTaskObj.xmlObject;
    $('#name').val(conditionXML.attr('name'));
    $('#description').val(conditionXML.text());
    $('#name').on('input propertychange', function () {
      conditionXML.setAttribute('name', $('#name').val());
      top.changeObjText(goWorkFlow.curTaskObj);
    });
  }

  if (goWorkFlow.readonlyMode) {
    setFrameReadOnly(window);
  }
}

function collectPropertiesData() {
  var goWorkFlow = parent.goWorkFlow;
  var conditionXML = goWorkFlow.curTaskObj.xmlObject;
  conditionXML.attr('name', $('#name').val());
  conditionXML.text($('#description').val());
  return true;
}
