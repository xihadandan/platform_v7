import constant, { langMap, flowTaskRights, requiredOpinion, submitModeOfAfterRollback } from './constant';
import { createPorts } from '../graph/ports';
import NodeTask from '../designer/NodeTask';
import EdgeDirection from '../designer/EdgeDirection';
import NodeSubflow from '../designer/NodeSubflow';
import moment from 'moment';

export const getCurrentDate = format => {
  return moment().format(format);
};

export const sGetRandom = piLength => {
  let lsOne,
    lsResult = '';
  for (let i = 0; i < piLength; i++) {
    lsOne = Math.random() * 10 + '';
    lsResult += lsOne.substring(0, 1);
  }
  return lsResult;
};

export const sGetNewTaskID = ({ type = 'TASK', nodes = [] } = {}) => {
  var lsID;
  var lsHead = type == 'TASK' ? 'T' : 'S';
  do {
    lsID = lsHead + sGetRandom(3);
    for (var i = 0; i < nodes.length; i++) {
      if (nodes[i].id === lsID) {
        break;
      }
    }
    if (i >= nodes.length) {
      return lsID;
    }
  } while (true);
};

export const sGetNewName = ({ type = 'TASK', cells = [] } = {}) => {
  var lsName = langMap[type + '_NAME'];
  var lsNewName = lsName;
  // if (type === 'TASK' || type === 'SUBFLOW' || type === 'CONDITION' || type === 'SWIMLANE') {
  var liIndex = 1;
  do {
    var lsNewName = lsName + liIndex;
    var _len = cells.length;

    for (var i = 0; i < _len; i++) {
      if (
        (cells[i].data && (cells[i].data.name === lsNewName || cells[i].data.conditionName === lsNewName)) ||
        cells[i].title === lsNewName ||
        cells[i].selection === lsNewName
      ) {
        break;
      }
    }

    if (i >= _len) {
      break;
    }
    liIndex++;
  } while (true);
  // }
  return lsNewName;
};

export const sGetNewDirectionID = ({ edges = [] } = {}) => {
  let lsID;
  do {
    lsID = 'D' + sGetRandom(3);
    for (var i = 0; i < edges.length; i++) {
      if (edges[i].id === lsID) {
        break;
      }
    }
    if (i >= edges.length) {
      return lsID;
    }
  } while (true);
};

export const sGetNewID = ({ prefix = '', cells = [] } = {}) => {
  var lsID;
  do {
    lsID = prefix + sGetRandom(3);
    for (var i = 0; i < cells.length; i++) {
      if (cells[i].id === lsID) {
        break;
      }
    }
    if (i >= cells.length) {
      return lsID;
    }
  } while (true);
};

export const sGetTaskCurveWay = (imgObject, x2, y2) => {
  if (!imgObject) {
    return null;
  }
  var loImg = imgObject;
  var x1 = loImg.x + loImg.w / 2;
  var y1 = loImg.y + loImg.h / 2;
  var liAngle1 = Math.atan(Math.abs(y2 - y1) / Math.abs(x2 - x1));
  var liAngle2 = Math.atan(loImg.h / loImg.w);
  if (liAngle1 <= liAngle2) {
    if (x2 >= x1) {
      var lsWay = 'right';
    } else {
      var lsWay = 'left';
    }
  } else {
    if (y2 >= y1) {
      var lsWay = 'down';
    } else {
      var lsWay = 'up';
    }
  }
  console.log('获取方向', lsWay);
  return lsWay;
};

export const getDir = (ev, ele) => {
  const l = ele.getBoundingClientRect().left;
  const t = ele.getBoundingClientRect().top;
  const w = ele.getBoundingClientRect().width;
  const h = ele.getBoundingClientRect().height;
  const x = (ev.clientX - l - w / 2) * (w > h ? h / w : 1);
  const y = (ev.clientY - t - h / 2) * (h > w ? w / h : 1);
  const angle = Math.atan2(y, x) / (Math.PI / 180);
  const d = (Math.round((angle + 180) / 90) + 3) % 4;
  // d的值{上:0,右:1,下:2,左:3}
  const dMap = {
    0: 'up',
    1: 'right',
    2: 'down',
    3: 'left'
  };
  console.log('获取方向', dMap[d]);
  return d;
};

export const oCreateTask = ({
  name = '',
  upCurves = [],
  downCurves = [],
  leftCurves = [],
  rightCurves = [],
  imgObject = {
    w: constant.NodeTaskWidth,
    h: constant.NodeTaskHeight,
    x: 0, // 节点起点不是中间点
    y: 0
  }
} = {}) => {
  return {
    name,
    cursorWay: '',
    upCurves,
    downCurves,
    leftCurves,
    rightCurves,
    outLines: [],
    inLines: [],
    imgObject
  };
};
/* 
清空曲线
document.all.ID_BROKENCURVE.outerHTML = ''; 
62画线的逻辑是先清空再重新绘制

*/
/* 
poFrom: sourceCell
poTo: targetCell
poLineObject: outLine/inLine

返回边数据
loObject.x1,y1 源节点和边的交点坐标
loObject.x2,y2 目标节点和边的交点坐标
*/
export const oCreateCurve = (poFrom, poTo, piStroke, poLineObject, event) => { };

/* 
获取节点和边的交点
poObject: sourceCell/targetCell
psWay: edge ----> fromWay/toWay
poLine: edge
x2,y2 节点中间点坐标

*/
export const aTaskCurvePoint = (poObject, psWay, poLine, pbNew, x2, y2) => {
  var x1 = poObject.imgObject.x;
  var y1 = poObject.imgObject.y;
  var x3, y3, laLine, liInterval, liWayIndex;
  if (poLine == null) {
    switch (psWay) {
      case 'up':
        x3 = x1 + poObject.imgObject.w / 2;
        y3 = y1;
        break;
      case 'down':
        x3 = x1 + poObject.imgObject.w / 2;
        y3 = y1 + poObject.imgObject.h;
        break;
      case 'left':
        x3 = x1;
        y3 = y1 + poObject.imgObject.h / 2;
        break;
      case 'right':
        x3 = x1 + poObject.imgObject.w;
        y3 = y1 + poObject.imgObject.h / 2;
        break;
    }
  } else {
    if (psWay == 'up' || psWay == 'down') {
      if (psWay == 'up') {
        laLine = poObject.upCurves;
        y3 = y1;
      } else {
        laLine = poObject.downCurves;
        y3 = y1 + poObject.imgObject.h;
      }
      liInterval = poObject.imgObject.w / (laLine.length + 1);
      if (Math.ceil(liInterval) > liInterval) {
        liInterval--;
      }
      for (var i = 0; i < laLine.length; i++) {
        if (laLine[i].name == poLine.name) {
          x3 = x1 + liInterval * (i + 1);
          break;
        }
      }
      liWayIndex = i;
      if (pbNew == true) {
        for (var i = 0; i < laLine.length; i++) {
          var loObject = laLine[i];
          if (loObject == null || loObject.name == poLine.name) {
            continue;
          }
          if (poObject.name == loObject.fromTask.name) {
            loObject.x1 = x1 + liInterval * (i + 1);
            loObject.y1 = y3;
          } else {
            loObject.x2 = x1 + liInterval * (i + 1);
            loObject.y2 = y3;
          }
          // bReDrawLine(loObject);
        }
      }
    } else {
      if (psWay == 'left' || psWay == 'right') {
        if (psWay == 'left') {
          laLine = poObject.leftCurves;
          x3 = x1;
        } else {
          laLine = poObject.rightCurves;
          x3 = x1 + poObject.imgObject.w;
        }
        liInterval = poObject.imgObject.h / (laLine.length + 1);
        if (Math.ceil(liInterval) > liInterval) {
          liInterval--;
        }
        for (var i = 0; i < laLine.length; i++) {
          if (laLine[i].name == poLine.name) {
            y3 = y1 + liInterval * (i + 1);
            break;
          }
        }
        liWayIndex = i;
        if (pbNew == true) {
          for (var i = 0; i < laLine.length; i++) {
            var loObject = laLine[i];
            if (loObject == null || loObject.name == poLine.name) {
              continue;
            }
            if (poObject.name == loObject.fromTask.name) {
              loObject.x1 = x3;
              loObject.y1 = y1 + liInterval * (i + 1);
            } else {
              loObject.x2 = x3;
              loObject.y2 = y1 + liInterval * (i + 1);
            }
            // bReDrawLine(loObject);
          }
        }
      }
    }
  }
  var laReturn = new Array();
  laReturn[0] = x3;
  laReturn[1] = y3;
  laReturn[2] = liWayIndex;
  // console.log('aTaskCurvePoint', laReturn)
  return laReturn;
};

/* 
x1 = sourceX 源节点和边的交点的 X 坐标
y1 = sourceY 
x2 = targetX/cursorX 目标节点和边的交点的 X 坐标
y2 = targetY/cursorY

psFromWay: 源节点方向
psToWay: 目标节点方向

liFromIndex：源节点输出边数组下标
liToIndex： 目标节点输入边
piMaxIndex piMaxIndex = liFromIndex > liToIndex ? liFromIndex : liToIndex;
*/
export const aGetCurvePoint = (x1, y1, x2, y2, psFromWay, psToWay, piMaxIndex) => {
  var MINIZE_LENGTH = 20;
  if (piMaxIndex != null && piMaxIndex > 0) {
    MINIZE_LENGTH += piMaxIndex * 10;
  }
  var laX = new Array();
  var laY = new Array();
  laX[0] = x1;
  laY[0] = y1;
  if (psToWay == null) {
    laX[3] = x2;
    laY[3] = y2;
    switch (psFromWay) {
      case 'up':
        laX[1] = laX[0];
        laY[1] = (laY[0] > laY[3] ? laY[3] : laY[0]) - MINIZE_LENGTH;
        laX[2] = laX[3];
        laY[2] = laY[1];
        break;
      case 'down':
        laX[1] = laX[0];
        laY[1] = (laY[0] < laY[3] ? laY[3] : laY[0]) + MINIZE_LENGTH;
        laX[2] = laX[3];
        laY[2] = laY[1];
        break;
      case 'left':
        laX[1] = (laX[0] > laX[3] ? laX[3] : laX[0]) - MINIZE_LENGTH;
        laY[1] = laY[0];
        laX[2] = laX[1];
        laY[2] = laY[3];
        break;
      case 'right':
        laX[1] = (laX[0] < laX[3] ? laX[3] : laX[0]) + MINIZE_LENGTH;
        laY[1] = laY[0];
        laX[2] = laX[1];
        laY[2] = laY[3];
        break;
    }
  } else {
    switch (psFromWay) {
      case 'up':
        switch (psToWay) {
          case 'up':
            laX[1] = laX[0];
            laY[1] = (laY[0] > y2 ? y2 : laY[0]) - MINIZE_LENGTH;
            laX[2] = x2;
            laY[2] = laY[1];
            break;
          case 'down':
            laX[1] = laX[0];
            laY[1] = laY[0] - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] - MINIZE_LENGTH;
            laX[2] = (laX[0] + x2) / 2;
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2 + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 + MINIZE_LENGTH;
            laX[4] = x2;
            laY[4] = laY[3];
            break;
          case 'left':
            laX[1] = laX[0];
            laY[1] = laY[0] - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] - MINIZE_LENGTH;
            laX[2] = x2 - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 - MINIZE_LENGTH;
            laY[1] = laX[2] > laX[1] && laY[0] - MINIZE_LENGTH > y2 ? y2 : laY[1];
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2;
            break;
          case 'right':
            laX[1] = laX[0];
            laY[1] = laY[0] - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] - MINIZE_LENGTH;
            laX[2] = x2 + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 + MINIZE_LENGTH;
            laY[1] = laX[2] < laX[1] && laY[0] - MINIZE_LENGTH > y2 ? y2 : laY[1];
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2;
            break;
        }
        break;
      case 'down':
        switch (psToWay) {
          case 'up':
            laX[1] = laX[0];
            laY[1] = laY[0] + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] + MINIZE_LENGTH;
            laX[2] = (laX[0] + x2) / 2;
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2 - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 - MINIZE_LENGTH;
            laX[4] = x2;
            laY[4] = laY[3];
            break;
          case 'down':
            laX[1] = laX[0];
            laY[1] = (laY[0] < y2 ? y2 : laY[0]) + MINIZE_LENGTH;
            laX[2] = x2;
            laY[2] = laY[1];
            break;
          case 'left':
            laX[1] = laX[0];
            laY[1] = laY[0] + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] + MINIZE_LENGTH;
            laX[2] = x2 - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 - MINIZE_LENGTH;
            laY[1] = laX[2] > laX[1] && laY[0] + MINIZE_LENGTH < y2 ? y2 : laY[1];
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2;
            break;
          case 'right':
            laX[1] = laX[0];
            laY[1] = laY[0] + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : laY[0] + MINIZE_LENGTH;
            laX[2] = x2 + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 + MINIZE_LENGTH;
            laY[1] = laX[2] < laX[1] && laY[0] + MINIZE_LENGTH < y2 ? y2 : laY[1];
            laY[2] = laY[1];
            laX[3] = laX[2];
            laY[3] = y2;
            break;
        }
        break;
      case 'left':
        switch (psToWay) {
          case 'up':
            laX[1] = laX[0] - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] - MINIZE_LENGTH;
            laY[1] = laY[0];
            laY[2] = y2 - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 - MINIZE_LENGTH;
            laX[1] = laY[2] > laY[1] && laX[0] - MINIZE_LENGTH > x2 ? x2 : laX[1];
            laX[2] = laX[1];
            laX[3] = x2;
            laY[3] = laY[2];
            break;
          case 'down':
            laX[1] = laX[0] - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] - MINIZE_LENGTH;
            laY[1] = laY[0];
            laY[2] = y2 + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 + MINIZE_LENGTH;
            laX[1] = laY[2] < laY[1] && laX[0] - MINIZE_LENGTH > x2 ? x2 : laX[1];
            laX[2] = laX[1];
            laX[3] = x2;
            laY[3] = laY[2];
            break;
          case 'left':
            laX[1] = (laX[0] > x2 ? x2 : laX[0]) - MINIZE_LENGTH;
            laY[1] = laY[0];
            laX[2] = laX[1];
            laY[2] = y2;
            break;
          case 'right':
            laX[1] = laX[0] - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] - MINIZE_LENGTH;
            laY[1] = laY[0];
            laX[2] = laX[1];
            laY[2] = (laY[0] + y2) / 2;
            laX[3] = x2 + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 + MINIZE_LENGTH;
            laY[3] = laY[2];
            laX[4] = laX[3];
            laY[4] = y2;
            break;
        }
        break;
      case 'right':
        switch (psToWay) {
          case 'up':
            laX[1] = laX[0] + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] + MINIZE_LENGTH;
            laY[1] = laY[0];
            laY[2] = y2 - MINIZE_LENGTH > (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 - MINIZE_LENGTH;
            laX[1] = laY[2] > laY[1] && laX[0] + MINIZE_LENGTH < x2 ? x2 : laX[1];
            laX[2] = laX[1];
            laX[3] = x2;
            laY[3] = laY[2];
            break;
          case 'down':
            laX[1] = laX[0] + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] + MINIZE_LENGTH;
            laY[1] = laY[0];
            laY[2] = y2 + MINIZE_LENGTH < (laY[0] + y2) / 2 ? (laY[0] + y2) / 2 : y2 + MINIZE_LENGTH;
            laX[1] = laY[2] < laY[1] && laX[0] + MINIZE_LENGTH < x2 ? x2 : laX[1];
            laX[2] = laX[1];
            laX[3] = x2;
            laY[3] = laY[2];
            break;
          case 'left':
            laX[1] = laX[0] + MINIZE_LENGTH < (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : laX[0] + MINIZE_LENGTH;
            laY[1] = laY[0];
            laX[2] = laX[1];
            laY[2] = (laY[0] + y2) / 2;
            laX[3] = x2 - MINIZE_LENGTH > (laX[0] + x2) / 2 ? (laX[0] + x2) / 2 : x2 - MINIZE_LENGTH;
            laY[3] = laY[2];
            laX[4] = laX[3];
            laY[4] = y2;
            break;
          case 'right':
            laX[1] = (laX[0] < x2 ? x2 : laX[0]) + MINIZE_LENGTH;
            laY[1] = laY[0];
            laX[2] = laX[1];
            laY[2] = y2;
            break;
        }
        break;
    }
    laX[laX.length] = x2;
    laY[laY.length] = y2;
  }
  var laReturn = new Array();
  laReturn[0] = laX;
  laReturn[1] = laY;
  // console.log('aGetCurvePoint', laReturn)
  return laReturn;
};

// 转换62数据
export const transformGraphData = (workFlowData, graphItem, designer) => {
  const { graph } = graphItem;
  const { tasks, directions } = workFlowData;
  const nodeStep = 140; // 节点步长
  const taskW = 170; // 环节、子流程长度
  const taskH = 56;
  const circleW = 56; // 开始、结束的长度
  const circleH = 56;
  const oldTaskW = 144; // 旧环节、子流程长度
  const oldTaskH = 48;
  const oldCircleW = 48; // 旧开始、结束的长度
  const oldCircleH = 48;
  const taskOffset = taskW - oldTaskW;

  let rightMap = {};
  // 创建权限map
  const createRightMap = () => {
    if (designer.flowSettingActions && designer.flowSettingActions.rights) {
      for (const right of designer.flowSettingActions.rights) {
        rightMap[right['code']] = right;
      }
      designer.flowSettingRightMap = rightMap;
    }
  };
  createRightMap();
  // 创建节点
  const createNode = ({
    data = {},
    id = '',
    shape = 'NodeTask',
    size = { width: taskW, height: taskH },
    view = 'vue-shape-view',
    zIndex = 1,
    position = { x: 100, y: 100 }
  } = {}) => {
    position.x = position.x - graph.options.x;
    position.y = position.y - graph.options.y;
    const oportsOrigin = createPorts();
    let ports = JSON.parse(JSON.stringify(oportsOrigin));
    ports.items.forEach(item => {
      item.attrs = {
        circle: {
          style: { visibility: 'hidden' }
        }
      };
      item.id = `${id}-port-${item.group}`;
    });
    return {
      data,
      id,
      ports,
      position,
      shape,
      size,
      view,
      zIndex
    };
  };
  // 创建边
  const createEdge = ({
    data = {},
    id = '',
    labels = [],
    shape = 'EdgeDirection',
    source = {}, // {cell:'nodeid',port:''}
    target = {},
    zIndex = 1
  } = {}) => {
    let edge = {
      id,
      data,
      labels,
      shape,
      source,
      target,
      zIndex
    };
    let currentLine = '';
    if (data.line) {
      currentLine = data.line;
      data.line = data.line.toLowerCase();
    }
    if (currentLine === 'BEELINE') {
      edge.router = {
        args: { way: '' },
        name: 'normal'
      };
      data.line = 'normal';
      edge.source.anchor = 'midSide';
      edge.target.anchor = 'midSide';
    } else if (currentLine.indexOf('CURVE') > -1) {
      currentLine = currentLine.split(';');
      // "CURVE;right;left"
      edge.router = {
        args: {
          way: [currentLine[1], currentLine[2]].join(';')
        },
        name: currentLine[0].toLowerCase()
      };
    }
    return edge;
  };

  const getConditionData = item => {
    const createContionItem = () => {
      return {
        option: '',
        param: '',
        field: '',
        operator: '',
        value: '',
        andOr: '',
        rBracket: '',
        lBracket: '',
        type: '1'
      };
    };
    let data = createContionItem(),
      conditionValue = null,
      laArg = {},
      logic = false;
    if (item.value) {
      conditionValue = decodeURIComponent(item.value);
    }
    if (conditionValue == null) {
      logic = true;
    } else {
      logic = conditionValue.trim().indexOf('&') != -1 || conditionValue.trim().indexOf('|') != -1;
    }
    laArg.value = conditionValue;
    laArg.text = item.argValue;
    laArg.logic = logic;
    let logicType = laArg['type'],
      gsText = laArg['text'],
      gsValue = laArg['value'],
      gbIsLogic = laArg['logic'];

    let lsValue = gsValue;
    if (logicType === 16) {
      // 自定义表达式
      data.type = '5';
      if (lsValue.indexOf(' & ') == 0) {
        if (gbIsLogic) {
          data.anchor = '&';
        }
        lsValue = lsValue.substring(3, lsValue.length);
      } else {
        if (lsValue.indexOf(' | ') == 0) {
          if (gbIsLogic) {
            data.anchor = '|';
          }
          lsValue = lsValue.substring(3, lsValue.length);
        }
      }
      if (lsValue.indexOf('(') == 0) {
        data.lBracket = '(';
        lsValue = lsValue.substring(1, lsValue.length);
      }
      if (lsValue.lastIndexOf(')') == lsValue.length - 1) {
        data.rBracket = ')';
        lsValue = lsValue.substring(0, lsValue.length - 1);
      }
    } else if (logicType == 32) {
      // 通过意见立场判断
      data.type = '6';
      if (lsValue.indexOf(' & ') == 0) {
        if (gbIsLogic) {
          data.anchor = '&';
        }
        lsValue = lsValue.substring(3, lsValue.length);
      } else {
        if (lsValue.indexOf(' | ') == 0) {
          if (gbIsLogic) {
            data.anchor = '|';
          }
          lsValue = lsValue.substring(3, lsValue.length);
        }
      }
      if (lsValue.indexOf('(') == 0) {
        data.lBracket = '(';
        lsValue = lsValue.substring(1, lsValue.length);
      }
      if (lsValue.lastIndexOf(')') == lsValue.length - 1) {
        data.rBracket = ')';
        lsValue = lsValue.substring(0, lsValue.length - 1);
      }
      let positionValues = lsValue.split(' ');
      if (positionValues.length == 3) {
        data.param = positionValues[0];
        data.operator = positionValues[1];
        data.value = positionValues[2];
      }
    } else if (lsValue.indexOf('@ISMEMBER(') != -1) {
      // 办理人归属判断
      data.type = '3';
      let lsTemp = lsValue.substring(0, lsValue.indexOf('@ISMEMBER('));
      if (gbIsLogic) {
        if (lsTemp.indexOf('&') != -1) {
          data.andOr = '&';
        } else {
          if (lsTemp.indexOf('|') != -1) {
            data.andOr = '|';
          }
        }
      }
      if (lsTemp.indexOf('(') != -1) {
        data.lBracket = '(';
      }
      lsTemp = lsValue.substring(lsValue.lastIndexOf('")') + 2, lsValue.length);
      if (lsTemp.indexOf(')') != -1) {
        data.rBracket = ')';
      }
      lsValue = lsValue.substring(lsValue.indexOf('@ISMEMBER("') + 11, lsValue.lastIndexOf('")'));
      let laValue = lsValue.split('","');

      if (laValue[0] != '<CURUSER>') {
        data.param = '0';
        data.field = laValue[0];
      } else if (laValue[0] == '<CURUSER>') {
        data.param = '1';
      }
      let orgFullValue = laValue[1];
      if (orgFullValue != null && orgFullValue.indexOf(':') != -1) {
        let values = orgFullValue.split(':');
        data.option = values[1];
        data.value = values[0];
      } else {
        data.option = '1';
        data.value = orgFullValue;
      }
      if (data.value != null && data.value.trim() != '') {
        let lsText = gsText.substring(gsText.indexOf('@ISMEMBER("') + 11, gsText.lastIndexOf('")'));
        let allData = {};
        allData.unitLabel = data.value.split(',')[0] == '' ? [] : data.value.split(',')[0].split(';');
        allData.unitValue = data.value.split(',')[1] == '' ? [] : data.value.split(',')[1].split(';');
        allData.formField = data.value.split(',')[2] == '' ? [] : data.value.split(',')[2].split(';');
        allData.tasks = data.value.split(',')[3] == '' ? [] : data.value.split(',')[3].split(';');
        allData.options = data.value.split(',')[4] == '' ? [] : data.value.split(',')[4].split(';');

        let valueList = [];
        const key2Type = {
          unitValue: 1,
          formField: 2,
          options: 8,
          tasks: 4
        };
        for (const key in allData) {
          if (key === 'unitLabel') {
            continue;
          }
          const type = key2Type[key];
          allData[key].forEach((value, index) => {
            let argValue = null;
            if (key === 'unitValue') {
              argValue = allData.unitLabel[index];
            }
            valueList.push({
              type,
              value,
              argValue
            });
          });
        }
        data.value = valueList;
      }
    } else if (lsValue.indexOf('@DUTYGRADE(') != -1) {
      data.type = '7';
      let lsTemp = lsValue.substring(0, lsValue.indexOf('@DUTYGRADE('));
      if (gbIsLogic) {
        if (lsTemp.indexOf('&') != -1) {
          data.andOr = '&';
        } else {
          if (lsTemp.indexOf('|') != -1) {
            data.andOr = '|';
          }
        }
      }
      if (lsTemp.indexOf('(') != -1) {
        data.lBracket = '(';
      }
      lsTemp = lsValue.substring(lsValue.lastIndexOf('")') + 2, lsValue.length);
      if (lsTemp.indexOf(')') != -1) {
        data.rBracket = ')';
      }

      lsValue = lsValue.substring(lsValue.indexOf('@DUTYGRADE("') + 12, lsValue.lastIndexOf('")'));
      let laValue = lsValue.split(' ');
      data.param = laValue[0];
      data.operator = laValue[1];
      let dValue = laValue[2].split(':');
      data.option = dValue[1];
      if (dValue[1] == '2') {
        data.value = dValue[0];
      } else {
        if (laValue[0] == 'A1' || laValue[0] == 'A3') {
          data.value = dValue[0];
        } else {
          // 职级
          data.value = dValue[0];
        }
      }
    } else {
      if (lsValue.indexOf('[VOTE=') != -1) {
        // 通过投票比例设置条件
        data.type = '2';
        if (lsValue.indexOf(' & ') == 0) {
          if (gbIsLogic) {
            data.andOr = '&';
          }
          lsValue = lsValue.substring(3, lsValue.length);
        } else {
          if (lsValue.indexOf(' | ') == 0) {
            if (gbIsLogic) {
              data.andOr = '1';
            }
            lsValue = lsValue.substring(3, lsValue.length);
          }
        }
        if (lsValue.indexOf('(') == 0) {
          data.lBracket = '(';
          lsValue = lsValue.substring(1, lsValue.length);
        }
        if (lsValue.lastIndexOf(')') == lsValue.length - 1) {
          data.rBracket = ')';
          lsValue = lsValue.substring(0, lsValue.length - 1);
        }

        let parm = lsValue.substr(0, lsValue.indexOf(' '));
        parm = parm.substring(0, parm.length - 1).split('=');

        data.param = parm[1];
        lsValue = lsValue.substr(lsValue.indexOf(' ') + 1);
        data.operator = lsValue.substr(0, lsValue.indexOf(' '));

        let vFullValue = lsValue.substr(lsValue.indexOf(' ') + 1);
        if (vFullValue != null && vFullValue.indexOf(':') != -1) {
          let values = vFullValue.split(':');
          data.option = values[1];
          data.value = values[0];
        } else {
          data.option = '1';
          data.value = lsValue.substr(lsValue.indexOf(' ') + 1);
        }
      } else if (lsValue.trim() == '&' || lsValue.trim() == '|') {
        // 逻辑条件
        data.type = '4';
        if (lsValue.trim() == '&') {
          data.andOr = '&';
        } else {
          if (lsValue.trim() == '|') {
            data.andOr = '|';
          }
        }
      } else {
        // 通过字段值比较
        data.type = '1';

        if (lsValue.indexOf(' & ') == 0) {
          if (gbIsLogic) {
            data.andOr = '&';
          }
          lsValue = lsValue.substring(3, lsValue.length);
        } else {
          if (lsValue.indexOf(' | ') == 0) {
            if (gbIsLogic) {
              data.andOr = '|';
            }
            lsValue = lsValue.substring(3, lsValue.length);
          }
        }
        if (lsValue.indexOf('(') == 0) {
          data.lBracket = '(';
          lsValue = lsValue.substring(1, lsValue.length);
        }
        if (lsValue.lastIndexOf(')') == lsValue.length - 1) {
          data.rBracket = ')';
          lsValue = lsValue.substring(0, lsValue.length - 1);
        }
        data.field = lsValue.substr(0, lsValue.indexOf(' '));
        lsValue = lsValue.substr(lsValue.indexOf(' ') + 1);
        data.operator = lsValue.substr(0, lsValue.indexOf(' '));
        let fullValue = lsValue.substr(lsValue.indexOf(' ') + 1);
        if (fullValue != null && fullValue.indexOf(':') != -1) {
          let values = fullValue.split(':');
          data.option = values[1];

          data.value = values[0];
        } else {
          data.option = '1';
          data.value = lsValue.substr(lsValue.indexOf(' ') + 1);
        }
      }
    }
    return JSON.stringify(data);
  };

  // 创建边数据
  const createEdgeData = ({ edgeId = '', sourceCell, targetCell, data = {} } = {}) => {
    let name = '';
    let type = '1';
    let terminalType = '';
    let terminalName = '';
    if (!sourceCell || !targetCell) {
      return;
    }
    const sourceData = sourceCell.data;
    const targetData = targetCell.data;

    let fromID = sourceData.id;
    if (sourceCell.shape === 'NodeCondition') {
      type = '2';
    }
    if (sourceData.id === constant.StartFlowId) {
      terminalType = constant.BEGIN;
      terminalName = sourceData.name;
      targetData.canEditForm = '1';
    }

    if (targetData.canEditForm !== '1' && targetData.startRights) {
      targetData.startRights = [];
    }

    const toID = targetData.id;
    if (targetCell.shape === 'NodeCondition') {
      type = '3';
    }
    if (sourceCell.shape !== 'NodeTask' || targetCell.shape !== 'NodeCondition') {
      name = `送${targetData.name || targetData.conditionName}`;
    }

    if (targetData.id === constant.EndFlowId) {
      terminalType = constant.END;
      terminalName = targetData.name;
    }

    let edgeData = new EdgeDirection({
      id: edgeId,
      name,
      type,
      fromID,
      toID,
      terminalType,
      terminalName,
      line: data.line
    });

    let hasValueData = {};
    for (const key in data) {
      if (data[key]) {
        hasValueData[key] = data[key];
      }
    }
    edgeData = { ...edgeData, ...hasValueData };

    if (edgeData.conditions.length) {
      edgeData.conditions.forEach(condition => {
        if (!condition.data && condition.value) {
          condition.data = getConditionData(condition);
        }
      });
    }
    return edgeData;
  };
  const requiredOpinionRelated = {
    B004026: 'B004002', // 旧数据发起权限-必须签署意见  == 发起权限-提交必填意见
    B004029: 'B004006', // 转办必填意见
    B004030: 'B004007', // 会签
    B004031: 'B004003', // 退回
    B004034: 'B004014', // 催办,
    B004032: 'B004015', // 特送个人
    B004033: 'B004016', // 特送环节
    B004039: 'B004005' // 撤回
    // 62数据 B004013办理过程 和查看流程图绑定
  };
  // 创建节点数据
  const createNodeData = ({ data = {} } = {}) => {
    let hasValueData = {}; // 原数据有值的data
    for (const key in data) {
      if (data[key]) {
        hasValueData[key] = data[key];
      }
    }
    let nodeData;
    if (data.type === '1') {
      nodeData = graphItem.initTaskData(data.id, data.name, data.type);
      if (hasValueData.conditionLine) {
        hasValueData.conditionLine = hasValueData.conditionLine.toLowerCase();
      }
      let originalRight = {};
      for (const key in flowTaskRights) {
        const type = flowTaskRights[key]['settingKey'];
        const configKey = flowTaskRights[key]['configKey'];

        let rights = [];

        // 以后台配置为主
        nodeData[key].forEach(data => {
          data.defaultVisible = false;
          let rightValue = data.value;
          if (rightValue === 'B004024') {
            // 管理员删除
            rightValue = 'B004023';
          }
          const hasIndex = hasValueData[key].findIndex(f => f.value === rightValue);
          if (hasIndex > -1) {
            data.defaultVisible = true;
          }
          // if (originalRight[data.value]) {
          //   data.defaultVisible = true
          // }
          if (requiredOpinion[data.value]) {
            nodeData[configKey][requiredOpinion[data.value]] = false;
          }
          rights.push({ ...data, value: data.value });
        });

        hasValueData[key].forEach(rightItem => {
          originalRight[rightItem.value] = rightItem;
          if (requiredOpinionRelated[rightItem.value]) {
            const requiredOpinionKey = requiredOpinion[requiredOpinionRelated[rightItem.value]];
            nodeData[configKey][requiredOpinionKey] = true;
          }
          if (rightItem.value === 'B004020') {
            // 提交时自动套打
            nodeData[configKey]['printAfterSubmit'] = true;
          }
        });
        // 以旧数据为主
        // hasValueData[key].forEach(data => {
        //   if (data.value && rightMap[data.value]) {
        //     const item = rightMap[data.value]
        //     rights.push({
        //       value: data.value,
        //       title: item.title,
        //       name: item.title,
        //       defaultVisible: true,
        //       requiredOpinion: item[type]['requiredOpinion'],
        //       displayState: 'label'
        //     });
        //   }
        // })

        hasValueData[key] = rights;
        if (hasValueData[configKey]) {
          // 用后台配置覆盖服务器的默认值
          hasValueData[configKey] = nodeData[configKey];
        }
      }

      if (hasValueData.isSetTransferUser) {
        // 转办人员设置
        hasValueData.todoRightConfig.isSetTransferUser = hasValueData.isSetTransferUser;
      }
      hasValueData.todoRightConfig.transferUsers = hasValueData.transferUsers; // 转办人员

      // 退回设置
      if (hasValueData.allowReturnAfterRollback === '1' && hasValueData.onlyReturnAfterRollback === '1') {
        hasValueData.todoRightConfig.submitModeOfAfterRollback = submitModeOfAfterRollback[2]['value'];
      } else if (hasValueData.allowReturnAfterRollback === '1') {
        hasValueData.todoRightConfig.submitModeOfAfterRollback = submitModeOfAfterRollback[1]['value'];
      }
    } else if (data.type === '2') {
      nodeData = new NodeSubflow({
        id: data.id,
        name: data.name
      });
      if (hasValueData.undertakeSituationOrders && Array.isArray(hasValueData.undertakeSituationOrders)) {
        // 子流程排序配置
        hasValueData.undertakeSituationOrders.map(order => {
          order.direction = order.direction.toLowerCase();
        });
      }
    }
    nodeData = { ...nodeData, ...hasValueData };
    return nodeData;
  };

  let graphData = {
    cells: []
  };
  let tasksMap = {},
    edgeMap = {};
  for (let index = 0; index < tasks.length; index++) {
    const data = tasks[index];
    let zIndex = graphData.cells.length + 1;
    let shape = 'NodeTask';
    if (data.type === '2') {
      shape = 'NodeSubflow';
    }
    let x = Number(data.x);
    let y = Number(data.y);
    x = x - taskW / 2;
    y = y - taskH / 2;
    const nodeData = createNodeData({ data });
    const node = createNode({
      id: data.id,
      data: nodeData,
      shape,
      zIndex,
      position: { x, y }
    });
    tasksMap[node.id] = node;
    graphData.cells.push(node);

    if (data.conditionX && data.conditionY) {
      // 目标节点是判断点
      const conditionId = sGetNewID({
        prefix: 'Condition',
        cells: graphData.cells
      });
      let conditionX = Number(data.conditionX);
      let conditionY = Number(data.conditionY);
      conditionX = conditionX - taskW / 2;
      conditionY = conditionY - taskH / 2;
      const nodeCondition = createNode({
        id: conditionId,
        data: {
          id: conditionId,
          name: data.conditionName,
          remark: data.conditionBody,
          conditionName: data.conditionName,
          conditionBody: data.conditionBody
        },
        shape: 'NodeCondition',
        zIndex: zIndex + 1,
        position: { x: conditionX, y: conditionY }
      });
      tasksMap[nodeCondition.id] = nodeCondition;
      graphData.cells.push(nodeCondition);
      // 判断点
      if (!tasksMap[node.id].nodeCondition) {
        tasksMap[node.id].nodeCondition = [];
      }
      tasksMap[node.id].nodeCondition.push(nodeCondition);
      // 指向判断点的边
      const sourceId = node.id;
      const targetId = nodeCondition.id;
      const edgeId = sGetNewDirectionID({
        edges: graphData.cells
      });
      const edgeData = createEdgeData({
        edgeId,
        sourceCell: node,
        targetCell: nodeCondition,
        data: {
          line: data.conditionLine
        }
      });

      const edge = createEdge({
        data: edgeData,
        id: edgeId,
        source: {
          cell: sourceId
        },
        target: {
          cell: targetId
        },
        zIndex: graphData.cells.length + 1
      });
      edgeMap[edge.id] = edge;
      graphData.cells.push(edge);
    }
  }
  console.log('tasksMap', tasksMap);

  for (let index = 0; index < directions.length; index++) {
    const data = directions[index];
    const fromID = data.fromID;
    const toID = data.toID;
    let node, sourceCell, targetCell;

    if (fromID === constant.StartFlowId || data.terminalType === 'BEGIN') {
      // terminalX 记录是节点中心坐标 x6的position是节点起点坐标
      // "lineLabel": "1130;988", 边labels的坐标，如“送结束”的坐标
      // "line": "BEELINE", 直线
      // "line": "CURVE;right;left", 曲线
      const nodeId = sGetNewID({
        prefix: 'Start',
        cells: graphData.cells
      });
      let x = Number(data.terminalX);
      let y = Number(data.terminalY);
      x = x - circleW / 2;
      y = y - circleH / 2;
      node = createNode({
        id: nodeId,
        data: {
          id: constant.StartFlowId,
          name: data.terminalName
        },
        shape: 'NodeCircle',
        position: { x, y },
        zIndex: graphData.cells.length + 1,
        size: { width: circleW, height: circleH }
      });
      tasksMap[node.id] = node;
      graphData.cells.push(node);

      sourceCell = node;
    } else {
      if (data.type === '2') {
        // 条件分支
        sourceCell = tasksMap[fromID]['nodeCondition'][0];
      } else {
        sourceCell = tasksMap[fromID];
      }
    }

    if (toID === constant.EndFlowId || data.terminalType === 'END') {
      const nodeId = sGetNewID({
        prefix: 'End',
        cells: graphData.cells
      });
      let x = Number(data.terminalX);
      let y = Number(data.terminalY);
      x = x - circleW / 2;
      y = y - circleH / 2;
      node = createNode({
        id: nodeId,
        data: {
          id: constant.EndFlowId,
          name: data.terminalName
        },
        shape: 'NodeCircle',
        position: { x, y },
        zIndex: graphData.cells.length + 1,
        size: { width: circleW, height: circleH }
      });
      tasksMap[node.id] = node;
      graphData.cells.push(node);

      targetCell = node;
    } else {
      targetCell = tasksMap[toID];
    }
    const sourceId = sourceCell.id;
    const targetId = targetCell.id;
    const edgeData = createEdgeData({
      edgeId: data.id,
      sourceCell,
      targetCell,
      data
    });
    const edge = createEdge({
      data: edgeData,
      id: data.id,
      labels: [data.name],
      source: {
        cell: sourceId
        // port: `${sourceId}-port-bottom`
        // selector: '> foreignobject:nth-child(1)'
      },
      target: {
        cell: targetId
        // port: `${targetId}-port-top`
        // selector: '> foreignobject:nth-child(1)'
      },
      zIndex: graphData.cells.length + 1
    });
    edgeMap[edge.id] = edge;
    graphData.cells.push(edge);
  }

  console.log('cells', graphData.cells);
  return graphData;
};

/**
 * 在任意对象/数组树中收集 i18n 条目（每个 code 只生成一条），并在 key 前插入 type（若传入）
 * @param {Object|Array} root - 根数据
 * @param {Object} [opts]
 *   - lang: 目标语言，默认 'zh_CN'
 *   - fallbackLangs: 回退语言数组，默认 ['en_US']
 *   - type: 可选的类型字符串，会放在 key 的最前面（例如 'task'）
 *   - pathSep: 路径分隔符，默认 '.'
 * @returns {Array<{code:string, name: (string|undefined), key:string, langUsed:string, type?:string}>}
 */
export function collectI18nEntries(root, opts = {}) {
  const {
    lang = 'zh_CN',
    fallbackLangs = ['en_US'],
    type = '',
    pathSep = '.',
    fn = undefined
  } = opts;

  const results = [];
  const seen = new WeakSet();

  // 从 i18nObj 中按 lang + fallback 找到 name 与实际使用的语言名（langUsed）
  function getNameAndLangUsed(i18nObj, code) {
    if (!i18nObj || typeof i18nObj !== 'object') return { name: undefined, langUsed: undefined };
    if (i18nObj[lang] && i18nObj[lang][code] != null) return { name: i18nObj[lang][code], langUsed: lang };
    for (const fb of fallbackLangs) {
      if (i18nObj[fb] && i18nObj[fb][code] != null) return { name: i18nObj[fb][code], langUsed: fb };
    }
    return { name: undefined, langUsed: undefined };
  }

  function dfs(node, pathParts) {
    if (!node || typeof node !== 'object') return;
    if (seen.has(node)) return;
    seen.add(node);

    // 如果当前节点含有 i18n（对象），处理：对 code 的并集生成一条记录（按 lang+fallback 选择 name）
    if (node.i18n && typeof node.i18n === 'object') {
      const i18nObj = node.i18n;
      // union of codes across languages to ensure we capture all codes present
      const codeSet = new Set();
      for (const lKey of Object.keys(i18nObj)) {
        const map = i18nObj[lKey];
        if (map && typeof map === 'object') {
          Object.keys(map).forEach(c => codeSet.add(c));
        }
      }

      for (const code of codeSet) {
        const { name, langUsed } = getNameAndLangUsed(i18nObj, code);
        // 构建 key： [type? , pathParts..., langUsed?, code]
        const keyParts = [];
        if (type) keyParts.push(type);
        if (pathParts.length) keyParts.push(...pathParts);
        if (langUsed) keyParts.push(langUsed);
        keyParts.push(code);
        const key = keyParts.join(pathSep);

        let res = {
          code,
          name,
          key,
          rootId: root.id,
          i18n: node.i18n,
          loading: {},
          langUsed,
          type: type || undefined,
        }
        if (fn && typeof fn === 'function') {
          fn(res)
        }
        results.push(res);
      }
    }

    // 遍历子属性（数组与对象）
    if (Array.isArray(node)) {
      node.forEach((item, i) => dfs(item, pathParts.concat(String(i))));
    } else {
      for (const k of Object.keys(node)) {
        if (k === 'i18n') continue;
        const v = node[k];
        if (v && typeof v === 'object') {
          // 如果属性是数组，我们把属性名与索引都加入路径（例如 items.0）
          if (Array.isArray(v)) {
            v.forEach((it, idx) => dfs(it, pathParts.concat(k, String(idx))));
          } else {
            dfs(v, pathParts.concat(k));
          }
        }
      }
    }
  }

  dfs(root, []);
  return results;
}

