define([ "server", "commons", "constant", "jquery-ui", "appContext", "appModal", "WorkFlowSimulation" ], function(
		server, commons, constant, ui, appContext, appModal, WorkFlowSimulation) {
	var JDS = server.JDS;
	var StringUtils = commons.StringUtils;
	var startFlowSimulation = function(options) {
		var workFlowSimulation = new WorkFlowSimulation(options);
		workFlowSimulation.startSimulation();
		workFlowSimulation.cleanSimulationData();
	}
	return startFlowSimulation;
});