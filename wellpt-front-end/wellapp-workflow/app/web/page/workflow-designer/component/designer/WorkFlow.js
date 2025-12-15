class WorkFlow {
  constructor({
    name = '',
    id = '',
    code = '',
    uuid = '',
    version = '1.0',
    lastVer = '',
    systemUnitId = '',
    moduleId = '',
    property = {},
    timers = [],
    tasks = [],
    directions = [],
    gateways = [],
    titleExpression = '',
    applyId = '',
    graphData = ''
  } = {}) {
    this.name = name
    this.id = id
    this.code = code
    this.uuid = uuid
    this.version = version
    this.lastVer = lastVer
    this.systemUnitId = systemUnitId
    this.moduleId = moduleId
    this.property = property
    this.timers = timers
    this.tasks = tasks
    this.directions = directions
    this.gateways = gateways
    this.titleExpression = titleExpression
    this.applyId = applyId
    this.graphData = graphData
  }
}

export default WorkFlow