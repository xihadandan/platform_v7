;
(function($) {
//	var workData = "";
//	var taskId = workData.taskId;
//	var flowUuid = workData.flowInstUuid;
	
	var minTimeUnit = '60';
	var changeData = new Object();
	// modify by wujx 2016-03-02 处理选中div（单击或双击），div的CSS效果。尤其是在火狐双击已选中区，绿色会变没掉，
	//  onselectstart='return false;' style='-moz-user-select:none;' 可以处理火狐问题
	var tdHtml = "<td class='basic'  timestamp='0,1' colspan=2 >" +
			"<div class='freeDiv' num='0' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='1' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='2' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='3' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='4' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='5' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='6' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='7' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='8' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='9' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='10' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='11' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='1,2' colspan=2 >" +
			"<div class='freeDiv' num='12' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='13' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='14' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='15' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='16' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='17' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='18' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='19' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='20' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='21' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='22' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='23' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='2,3' colspan=2 >" +
			"<div class='freeDiv' num='24' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='25' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='26' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='27' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='28' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='29' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='30' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='31' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='32' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='33' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='34' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='35' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='3,4' colspan=2 ><div class='freeDiv' num='36'>&nbsp;</div>" +
			"<div class='freeDiv' num='37' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='38' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='39' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='40' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='41' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='42' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='43' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='44' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='45' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='46' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='47' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='4,5' colspan=2 >" +
			"<div class='freeDiv' num='48' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='49' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='50' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='51' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='52' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='53' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='54' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='55' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='56' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='57' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='58' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='59' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='5,6' colspan=2 >" +
			"<div class='freeDiv' num='60' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='61' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='62' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='63' onselectstart='return false;' style='-moz-user-select:none;' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='64' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='65' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='66' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='67' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='68' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='69' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='70' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='71' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='6,7' colspan=2 >" +
			"<div class='freeDiv' num='72' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='73' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='74' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='75' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='76' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='77' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='78' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='79' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='80' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='81' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='82' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='83' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='7,8' colspan=2 >" +
			"<div class='freeDiv' num='84' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='85' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='86' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='87' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='88' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='89' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='90' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='91' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='92' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='93' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='94' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='95' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='8,9' colspan=2 >" +
			"<div class='freeDiv' num='96' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='97' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='98' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='99' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='100' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='101' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='102' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='103' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='104' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='105' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='106' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='107' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='9,10' colspan=2 >" +
			"<div class='freeDiv' num='108' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='109' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='110' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='111' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='112' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='113' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='114' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='115' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='116' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='117' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='118' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='119' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='10,11' colspan=2 >" +
			"<div class='freeDiv' num='120' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='121' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='122' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='123' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='124' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='125' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='126' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='127' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='128' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='129' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='130' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='131' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='11,12' colspan=2 >" +
			"<div class='freeDiv' num='132' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='133' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='134' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='135' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='136' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='137' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='138' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='139' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='140' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='141' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='142' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='143' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='12,13' colspan=2 >" +
			"<div class='freeDiv' num='144' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='145' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='146' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='147' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='148' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='149' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='150' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='151' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='152' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='153' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='154' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='155' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='13,14' colspan=2 >" +
			"<div class='freeDiv' num='156' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='157' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='158' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='159' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='160' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='161' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='162' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='163' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='164' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='165' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='166' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='167' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='14,15' colspan=2 >" +
			"<div class='freeDiv' num='168' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='169' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='170' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='171' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='172' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='173' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='174' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='175' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='176' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='177' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='178' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='179' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='15,16' colspan=2 >" +
			"<div class='freeDiv' num='180' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='181' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='182' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='183' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='184' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='185' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='186' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='187' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='188' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='189' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='190' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='191' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='16,17' colspan=2 >" +
			"<div class='freeDiv' num='192' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='193' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='194' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='195' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='196' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='197' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='198' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='199' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='200' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='201' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='202' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='203' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='17,18' colspan=2 >" +
			"<div class='freeDiv' num='204' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='205' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='206' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='207' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='208' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='209' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='210' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='211' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='212' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='213' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='214' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='215' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='18,19' colspan=2 >" +
			"<div class='freeDiv' num='216' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='217' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='218' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='219' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='220' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='221' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='222' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='223' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='224' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='225' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='226' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='227' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='19,20' colspan=2 ><div class='freeDiv' num='228' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='229' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='230' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='231' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='232' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='233' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='234' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='235' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='236' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='237' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='238' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='239' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='20,21' colspan=2 >" +
			"<div class='freeDiv' num='240' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='241' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='242' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='243' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='244' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='245' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='246' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='247' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='248' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='249' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='250' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='251' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='21,22' colspan=2 >" +
			"<div class='freeDiv' num='252' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='253' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='254' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='255' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='256' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='257' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='258' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='259' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='260' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='261' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='262' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='263' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='22,23' colspan=2 >" +
			"<div class='freeDiv' num='264' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='265' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='266' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='267' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='268' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='269' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='270' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='271' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='272' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='273' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='274' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='275' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td><td class='basic'  timestamp='23,24' colspan=2 >" +
			"<div class='freeDiv' num='276' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='277' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='278' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='279' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='280' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='281' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='282' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='283' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='284' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='285' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='286' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"<div class='freeDiv' num='287' onselectstart='return false;' style='-moz-user-select:none;'>&nbsp;</div>" +
			"</td>";
	/**   
	 * newJsonData:首次调用时传入的参数：
	 * '{"beginTime":"","endTime":"","employName":"","uuid":"",
	 * 	"resCode":"MEET_RESOURCE"}';
	 * 	resCode 必须传入数值,例如:CAR_RESOURCE,MEETING_RESOURCE,DRIVER_RESOURCE
	 * ----------------------------------------------------------------------
	 * 第二次入参时参数：  
	 * 例如：var newJsonData =
	 *  '{"beginTime":"2014-09-02 04:00","endTime":"2014-09-02 06:00",
	 * 	"employName":"大会议室","uuid":"b4e1b21f-1003-45e8-9f41-d8879d6a2127",
	 * 	"resCode":"MEET_RESOURCE"}';
	 * 	resCode 必须传入数值,例如:CAR_RESOURCE,MEETING_RESOURCE,DRIVER_RESOURCE  
	 */ 
	$.fn.myTimeEmploy = function(newJsonData, callback){
		//保存资源类型
		var resourceType = newJsonData.resCode;
		var element = $(this);
		var week = '';
		var timeType = 'today';
		var appointDate = '';
		var type = 'MEETING_RES_MANAGE';
		var field = 'meeting_start_time,meeting_end_time';
		//上次点击的时间占用记录
		 var newJsonData = newJsonData;
		 changeData = newJsonData;
		//时间资源数据
		var tmList;
		getRecords(element,week,timeType,appointDate,type,field,resourceType,callback,newJsonData);
		
		//日期输入框调用日期控件
		$("#s_today").live("focus",function(){
			var resourceName = $(this).attr("resourceName");
			var field = $(this).attr("field");
			WdatePicker({startDate:'%y-%M-01 00:00:00',
				dateFmt:"yyyy-MM-dd",alwaysUseStartDate:false,
				onpicking:function(dp){
					getRecords($("#s_today"),"","notoday",dp.cal.getNewDateStr(),resourceName,field,resourceType,callback,newJsonData);
				}
			});
		});
		
		$("#prevWeek").die().live("click",function(){
			var week = $(this).attr("week");
			var resourceName = $(this).attr("resourceName");
			var field = $(this).attr("field");
			getRecords($(this),"","notoday",getPreWeekFirstDate($("#s_today").val()),resourceName,field,resourceType,callback,newJsonData);
		});
		
		$("#nextWeek").die().live("click",function(){
			var week = $(this).attr("week");
			var resourceName = $(this).attr("resourceName");
			var field = $(this).attr("field");
			getRecords($(this),"","notoday",getNextWeekFirstDate($("#s_today").val()),resourceName,field,resourceType,callback,newJsonData);
		});
		
		
		function getNextWeekFirstDate(dateStr){
			var date =new   Date(dateStr.replace(/-/g,   "/"))
			date.setDate(date.getDate()+7);
			return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
		}
		
		function getPreWeekFirstDate(dateStr){
			var date =new   Date(dateStr.replace(/-/g,   "/"))
			date.setDate(date.getDate()-7);
			return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
		}
		
		//请求后台数据路劲
		function getUrl(week,appointDate,type,resourceType){
			url = ctx + '/calendar/calendar_show_new.action?weekCount='+week+'&appointDate='+appointDate+'&resourceName='+type + '&resourceType='+ resourceType;
			return url;
		}
		
		//获得控件的标题栏字符串
		function getTitleStyle(resName){
			var str = "<td class='calendar_rightTd'>" +
							"<table class='title_table'>" +
								"<tr class='calendar_table'>" +
									"<td style='width:80px' class='title_td' >" +
										"<span class='title_text'>" + resName + "</span>" +
									"</td>";
			return str;
		}
		
		//0:00~7:00这段会议时间隐藏
		function hidewWeeHours(){
			$("tr[class='calendar_table']").find("td[timestamp='1,2'],td[timestamp='3,4'],td[timestamp='5,6'],td[timestamp='7,8'],td[timestamp='9,10'],td[timestamp='11,12'],td[timestamp='13,14'],td[timestamp='15,16']").hide();
			$("tr[class!='calendar_table']").find("td[timestamp='0,1'],td[timestamp='1,2'],td[timestamp='2,3'],td[timestamp='3,4'],td[timestamp='4,5'],td[timestamp='5,6'],td[timestamp='6,7'],td[timestamp='7,8']").hide();
		}
		
		//0:00~7:00这段会议时间显示
		function showwWeeHours(){
			$("tr[class='calendar_table']").find("td[timestamp='1,2'],td[timestamp='3,4'],td[timestamp='5,6'],td[timestamp='7,8'],td[timestamp='9,10'],td[timestamp='11,12'],td[timestamp='13,14'],td[timestamp='15,16']").show();
			$("tr[class!='calendar_table']").find("td[timestamp='0,1'],td[timestamp='1,2'],td[timestamp='2,3'],td[timestamp='3,4'],td[timestamp='4,5'],td[timestamp='5,6'],td[timestamp='6,7'],td[timestamp='7,8']").show();
		}
		
		function getRecords(element,week,timeType,appointDate,type,field,resourceType,callback,newJsonData,timeLength){
			var url = "";
			if(resourceType == "MEET_RESOURCE") {
				url = getUrl(week,appointDate,type,resourceType);
			}else if(resourceType == "CAR_RESOURCE") {
				url = getUrl(week,appointDate,type,resourceType);
			}else if(resourceType == "DRIVER_RESOURCE") {
				url = getUrl(week,appointDate,type,resourceType);
			}
			var x = new Array;
			x = ["changeDiv","changeDiv1","changeDiv2","changeDiv3","changeDiv4","changeDiv5","changeDiv6","changeDiv7","changeDiv8","changeDiv9"];
			if(timeType=="today"){
				var json = "";
				html="<div id='meet_resource_html_body_div'></div>"
				if(resourceType == "MEET_RESOURCE") {
					json = {title:"会议室占用情况",
							closeOnEscape: true,
							draggable: true,
							resizable: true,
							height: 950,
							width: 1200,
							content: html,
							async:true,
							defaultBtnName:"关闭"};
				}else if(resourceType == "CAR_RESOURCE") {
					json = {title:"车辆占用情况",
							closeOnEscape: true,
							draggable: true,
							resizable: true,
							height: 950,
							width: 1200,
							content: html,
							defaultBtnName:"关闭"};
				}else if(resourceType == "DRIVER_RESOURCE") {
					json = {title:"司机占用情况",
							closeOnEscape: true,
							draggable: true,
							resizable: true,
							height: 950,
							beforClose:function(){
								oAlert("关闭！");
							},
							width: 1200,content: html,defaultBtnName:"关闭"};
				}
				json.close = function() {
					$(this).dialog("destroy");
					$(this).html("");
				}
				showDialog(json);
			}else{
				//element.parents(".dialogcontent").html(html);
				$("#dialogModule .dialogcontent").html(html);
			}	
			pageLock("show","资源获取中,请稍后.....");
			$.ajax({
				type : "post",
				url : url,
				async:true,
				success : function(data) {
					//html变量
					var html =[];
					var weekCount = data["weekCount"];
					var today = data["today"];//今天的日期
					var weekList = data["weekList"];//	["星期一", "2014-07-21"] ["星期二", "2014-07-22"] ["星期三", "2014-07-23"]
					var weekSplitBg = weekList[0][1].split("-");
					var weekBg = weekSplitBg[1] + "月" + weekSplitBg[2] + "日";//一周的周一的日期
					var weekSplitEd = weekList[weekList.length-1][1].split("-");
					var weekEd = weekSplitEd[1] + "月" + weekSplitEd[2] + "日";//一周的周日的日期
					//显示资源的日期等待
					html.push('<div class="toolbar">');
					html.push('<table width="100%">');
					html.push('<tr>');
					html.push('<td align="left" width="300">');
					html.push('<a class="s_prev_day" href="#" mtype="0" id="prevWeek" resourceName ="'+type+'"  field="'+field+'"  week="'+(weekCount-parseInt(1, 10))+'"><</a>');
					html.push('<input  type="text"  resourceName ="'+type+'"  field="'+field+'" id="s_today" class="s_today" value="'+today+'" size="7">');
					html.push('<a class="s_next_day" href="#" mtype="0"  resourceName ="'+type+'" field="'+field+'" id="nextWeek" week="'+(parseInt(weekCount, 10)+parseInt(1, 10))+'">></a>');
					html.push('<span class="fromandto">'+weekBg+'至'+weekEd+'</span>');
					html.push('</td >');
					html.push('<td align="right">');
//					html.push('<td align="right" width="50px">预占用:</td>');
//					html.push('<td align="right" width="50px"><input id="clear" type="button" style="float:right;font-size: 14px;background:#d2cb80; height:22px;width:50px; margin-right:24px; padding:0 5px;border:1px solid #dee1e2; font-family:Microsoft YaHei"/></td>');
					html.push('<td align="right" width="50px">占用:</td>');
					html.push('<td align="right" width="50px"><input id="clear" type="button" style="float:right;font-size: 14px;background:#fd6f00; height:22px;width:50px; margin-right:24px; padding:0 5px;border:1px solid #dee1e2; font-family:Microsoft YaHei"/></td>');
					html.push('</td>');
					html.push('</tr>');
					html.push('</table>');
					html.push('</div>');
					var queryItem = data["queryItem"];//相关资源Item 例如：会议资源:A栋九楼会议室
					//debugger
					tmList = data["tmList"];	  //时间占用资源Item
					if(newJsonData.beginTime != ""){
						tmList.push(newJsonData);
					}
					
					for(var i=0;i<weekList.length;i++) {//
						var date = weekList[i][1]; //值：2014-07-21
						var dates = date.split("-"); //分割后：["2014", "07", "21"]
						var dateNew = dates[1]+"月"+dates[2]+"日"; //07月21日
						html.push("<table class='weekList_table' date='"+date+"'>");
						if(i%2 != 0) {
							html.push("<tr><td class='calendar_weekList_2'><div class='weekDay'>"+weekList[i][0]+"</div><div class='weedDay2'>"+dateNew+"</div></td>");
						}else {
							html.push("<tr><td class='calendar_weekList'><div class='weekDay'>"+weekList[i][0]+"</div><div class='weedDay2'>"+dateNew+"</div></td>");
						}
						if(resourceType == "MEET_RESOURCE") {
							html.push(getTitleStyle("会议室"));
						}else if(resourceType == "CAR_RESOURCE") {
							html.push(resourceType("车辆类型"));
						}else if(resourceType == "DRIVER_RESOURCE") {
							html.push(resourceType("司机"));
						}
						//循环出时间条：0:00  2:00  4:00  6:00
						for(var index=1;index<49;index++) {
							var str = "";
							if(parseInt(index, 10)>=0 && parseInt(index, 10)<=49) {
								if(index%2 == 0 ){   //18%2 == 0
									str = index/2-1 +":00"; //   18/2-1 = 8
									var starstr = index-1;  //   18 - 1 = 17
									var endstr = index;     //   18
									html.push("<td colspan=2 timestamp='"+starstr+","+endstr+"'>"+str+"</td>");
									
								}
							}
						}
						
						html.push("</tr>");
						for(var j=0;j<queryItem.length;j++) {
								
								//x.sort(function(){return Math.random()-0.5});
								if(resourceType == "MEET_RESOURCE") {
									html.push("<tr date='"+date+"' uuid='" + queryItem[j].uuid +"' resCode='" + queryItem[j].reservedText4 +"'  meetroomName='"+queryItem[j].title+"'><td class='basic' width='80px' title='"+queryItem[j].title+"'><div class='autocut1'>"+queryItem[j].title+"</div></td>");
								}else if(resourceType == "CAR_RESOURCE") {
									html.push("<tr><td class='basic' width='80px' title='"+queryItem[j].title+"'><div class='autocut'>"+queryItem[j].title+"</div></td>");
								}else if(resourceType == "DRIVER_RESOURCE") {
									html.push("<tr><td class='basic' width='80px' title='"+queryItem[j].title+"'><div class='autocut'>"+queryItem[j].title+"</div></td>");
								}
								window.flag2 = "default";
								//开始循环时间占用的记录
								html.push(tdHtml);
								html.push("</tr>");
						}
						html.push("</table></td></tr>");
						html.push("</table>");
					}
					$("#meet_resource_html_body_div").html(html.join(""));
					pageLock("hide");
//					$(".dialogcontent").find(".title_table").each(function() {
//						$(this).find("tr").each(function(index) {
//							$(this).find("td").each(function(index) {
//								if(index>0 && index<7) {
//									$(this).css("display","none");
//								}
//							});
//						})
//					});
					pageLock("show","暂用资源获取中,请稍后....");
					setTimeout(function(){
						showUserMeeting(tmList,0);
					},20);	
					//modifiy by xujm 2015-05-25 
					//会议资源时
					if(resourceType == "MEET_RESOURCE") {
						showMeetingResource()
					};		
					//modifiy by xujm 2015-05-29 解决时间大于1小时时只有一个小时的样式是newAppendDiv
					var title = $(".newAppendDiv:first").attr("title");
					if(title){
						$("div[title='"+title+"']").attr("class","newAppendDiv");
					}
				},
				error : function(data) {
					oAlert(data);
				}
			});		
			
			function showMeetingResource(){	
				//显示按钮
				$(".dialogcontent .toolbar tbody td:first").after("<td width='140px' align='center'><div id='weeHoursBut' div='fromandto' style='margin-left:5px;background-color:#d0d0d0;cursor: pointer;'>显示0:00~7:00会议时间</div></td>");
				//看板抬头部分固定
				$("div[class='ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix']:eq(1)").after($(".toolbar"));
				//防止多次打开看板出现多个头部
				$(".toolbar:not(':last')").remove();
//				if($("#weeHoursBut").size()<1){
//					//显示按钮
//					$(".dialogcontent .toolbar tbody td:first").after("<td width='140px' align='center'><div id='weeHoursBut' div='fromandto' style='margin-left:5px;background-color:#d0d0d0;cursor: pointer;'>显示0:00~7:00会议时间</div></td>");
//					//看板抬头部分固定
//					//$("div[class='ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix']:eq(1)").after($(".toolbar"));
//				}else{
//					//防止多次打开看板出现多个头部
//					//$("#dialogModule").find(".toolbar").remove();  
//				}
				//隐藏0:00~7:00会议时间
				hidewWeeHours();
				//按钮点击事件
				$("#weeHoursBut").toggle(function(){
					$(this).text("隐藏0:00~7:00会议时间");							
//					$(".weekList_table").css("width","97%");							
					showwWeeHours();
				},function(){
					$(this).text("显示0:00~7:00会议时间");							
//					$(".weekList_table").css("width","83%");							
					hidewWeeHours();
				});	
				//调整表格宽度 
//				$(".weekList_table").css("width","83%");
				
				//$(".weekList_table").css("display","none");
				//居中
//				$(".weekList_table").css("margin","auto");
				
				
				$(".title_text").css({"text-align":"center","width":"96%"});
				
				$(".autocut1").css({"text-align":"left"});
			}
			
			function showUserMeeting(tmList, i ){
				window.setTimeout(function(){ 
					var tmListTemp = tmList[i];
					var beginTime = tmListTemp.beginTime;//获得一笔时间占用记录的开始时间
					//处理时间格式，转化成看板能识别的小格子的num
					var doBg = beginTime.split(" ");
					var doBgDate = doBg[0];
					var doBgTime = doBg[1];
					var doBg2 = doBg[1].split(":");
					var beginNum = parseInt(doBg2[0]*12 + doBg2[1]/5)
					
					var endTime = tmListTemp.endTime;//获得一笔时间占用记录的结束时间
					var doEnd = endTime.split(" ");
					var doEndDate = doEnd[0];
					var doEndTime = doEnd[1];
					var doEnd2 = doEnd[1].split(":");
					var endNum = parseInt(doEnd2[0]*12 + doEnd2[1]/5);
					
					var employName = tmListTemp.employName;//获得一笔时间占用记录的占用的会议室
					var employWork = tmListTemp.employWork;//获得一笔时间占用记录的会议标题
					//=======  modefied by huangwy 2015-3-27 start =======
					var employ_leader = tmListTemp.employLeader == null?"":tmListTemp.employLeader;//会议室申请人
					//=======  modefied by huangwy 2015-3-27 end =======
					var resourceName = tmListTemp.resourceName;//获得一笔时间占用记录的占用资源类型
					var resourcePreEmploy = tmListTemp.resourcePreEmploy;//获得一笔时间占用记录的占用状态类型
					var tmListFlowUuid = tmListTemp.flowUuid;
					if(resourcePreEmploy == "Rel" || resourcePreEmploy == "Wate" || resourcePreEmploy == "Over" 
//						&& flowUuid != tmListFlowUuid
						){
						xClass = "relDiv";//占用
					}else if(resourcePreEmploy == "Pre" 
//						&& flowUuid != tmListFlowUuid
						){
						xClass = "notRelDiv";//预占用
					}else  {
						xClass = "newAppendDiv";
					}
//					var id = tmListTemp.id;//获得一笔时间占用记录的uuid
					$(".dialogcontent").find(".weekList_table").each(function() {
						var $this = $(this);
						var date = $this.attr("date");
						$this.find(".title_table tr").each(function() {
							var $this = $(this);
							var spanText = $this.find("td").first().children("div").text();
							if(spanText == employName) {
								if(doBgDate == date && doEndDate == date) {//判断该笔占用落在哪一天的table中
									$this.find("div").each(function(index) {
										var $this = $(this);
										var num = $this.attr("num");
										if(num != undefined && num == beginNum && beginNum<endNum) {
											if(resourcePreEmploy == undefined) {
												$this.attr("class","newAppendDiv");
											}else {
												$this.attr("class","preDiv " + xClass);
												$this.attr("meetroomname",employName);
												$this.attr("title",employ_leader + " " + employWork + " " + doBgDate + " " + doBgTime + "-" + doEndDate + " " + doEndTime);
											}
											beginNum ++; 
										}
									}) 
								}else if(doBgDate<date && date<doEndDate) {
									$this.find("div").each(function(index) {
										var $this = $(this);
										var num = $this.attr("num");
										if(num != undefined) {
											if(resourcePreEmploy == undefined) {
												$this.attr("class","newAppendDiv");
											}else {
												$this.attr("class","preDiv " + xClass);
												$this.attr("meetroomname",employName);
												$this.attr("title",employ_leader + " " + employWork + " " + doBgDate + " " + doBgTime + "-" + doEndDate + " " + doEndTime);
											}
											num ++; 
										}
									}) 
								}
								else if(doBgDate == date && doEndDate != date) {
									$this.find("div").each(function(index) {
										var $this = $(this);
										var num = $this.attr("num");
										if(num != undefined && num == beginNum) {
											if(resourcePreEmploy == undefined) {
												$this.attr("class","newAppendDiv");
											}else {
												$this.attr("class","preDiv " + xClass);
												$this.attr("meetroomname",employName);
												$this.attr("title",employ_leader + " " + employWork + " " + doBgDate + " " + doBgTime + "-" + doEndDate + " " + doEndTime);
											}
											beginNum ++; 
										}
									}) 
								}else if(doBgDate != date && doEndDate == date) {
									$this.find("div").each(function(index) {
										var $this = $(this);
										var num = $this.attr("num");
										if(num != undefined && num<endNum) {
											if(resourcePreEmploy == undefined) {
												$this.attr("class","newAppendDiv");
											}else {
												$this.attr("class","preDiv " + xClass);
												$this.attr("meetroomname",employName);
												$this.attr("title",employ_leader + " " + employWork + " " + doBgDate + " " + doBgTime + "-" + doEndDate + " " + doEndTime);
											}
											num ++; 
										}
									}) 
								}
							}
						})
						
						});
					 if(i != tmList.length - 1){
						 showUserMeeting(tmList, i + 1 );
					 }		
				}, 5);
				 
				
				pageLock("hide");
			}
			
			
			$(".freeDiv").die().live("click",function(e){
				//modiefy by xujm  2015-05-29 改成可以多选时间 默认还是只能选择一天内的
				var $this = $(this);
				var firstNumForThisTd = parseInt($this.parent().find("div:first").attr("num"));
				var num = parseInt($this.attr("num"));
				if(num-firstNumForThisTd>5){
					firstNumForThisTd = firstNumForThisTd + 6;
				}
				var lastNumForThisTd =firstNumForThisTd+5;
				//不相连时移除以前的点击样式
				if($this.parent().parent().find("div[num='"+(lastNumForThisTd+1)+"'][class='newAppendDiv']").size()<1 && $this.parent().parent().find("div[num='"+(firstNumForThisTd-1)+"'][class='newAppendDiv']").size()<1){
					$(".newAppendDiv").attr("class","freeDiv");
				}
				//点击的获取点击的样式 半格 及 半小时
				firstNumForThisTd = parseInt($this.parent().find("div:first").attr("num"));
				if(num-firstNumForThisTd<6){
					$this.parent().find("div:lt(6)").attr("class","newAppendDiv");
				}else{
					$this.parent().find("div:gt(5)").attr("class","newAppendDiv");
				}				
//				var $this = $(this);
//				var basicTimestamp = $this.parents(".basic").attr("timestamp"); 
//				var date = $this.parents(".basic").attr("date");//点击单元格所在的日期
//				if(basicTimestamp == "0,1") {
//					begin = date+" 00:00";
//					end = date+" 01:00";
//				}
//				else if(basicTimestamp == "1,2") {
//					begin = date+" 01:00";
//					end = date+" 02:00";
//				}
//				else if(basicTimestamp == "2,3") {
//					begin = date+" 02:00";
//					end = date+" 03:00";
//				}
//				else if(basicTimestamp == "3,4") {
//					begin = date+" 03:00";
//					end = date+" 04:00";
//				}
//				else if(basicTimestamp == "4,5") {
//					begin = date+" 04:00";
//					end = date+" 05:00";
//				}
//				else if(basicTimestamp == "5,6") {
//					begin = date+" 05:00";
//					end = date+" 06:00";
//				}
//				else if(basicTimestamp == "6,7") {
//					begin = date+" 06:00";
//					end = date+" 07:00";
//				}
//				else if(basicTimestamp == "7,8") {
//					begin = date+" 07:00";
//					end = date+" 08:00";
//				}
//				else if(basicTimestamp == "8,9") {
//					begin = date+" 08:00";
//					end = date+" 09:00";
//				}
//				else if(basicTimestamp == "9,10") {
//					begin = date+" 09:00";
//					end = date+" 10:00";
//				}
//				else if(basicTimestamp == "10,11") {
//					begin = date+" 10:00";
//					end = date+" 11:00";
//				}
//				else if(basicTimestamp == "11,12") {
//					begin = date+" 11:00";
//					end = date+" 12:00";
//				}
//				else if(basicTimestamp == "12,13") {
//					begin = date+" 12:00";
//					end = date+" 13:00";
//				}
//				else if(basicTimestamp == "13,14") {
//					begin = date+" 13:00";
//					end = date+" 14:00";
//				}
//				else if(basicTimestamp == "14,15") {
//					begin = date+" 14:00";
//					end = date+" 15:00";
//				}
//				else if(basicTimestamp == "15,16") {
//					begin = date+" 15:00";
//					end = date+" 16:00";
//				}
//				else if(basicTimestamp == "16,17") {
//					begin = date+" 16:00";
//					end = date+" 17:00";
//				}
//				else if(basicTimestamp == "17,18") {
//					begin = date+" 17:00";
//					end = date+" 18:00";
//				}
//				else if(basicTimestamp == "18,19") {
//					begin = date+" 18:00";
//					end = date+" 19:00";
//				}
//				else if(basicTimestamp == "19,20") {
//					begin = date+" 19:00";
//					end = date+" 20:00";
//				}
//				else if(basicTimestamp == "20,21") {
//					begin = date+" 20:00";
//					end = date+" 21:00";
//				}
//				else if(basicTimestamp == "21,22") {
//					begin = date+" 21:00";
//					end = date+" 22:00";
//				}
//				else if(basicTimestamp == "22,23") {
//					begin = date+" 22:00";
//					end = date+" 23:00";
//				}
//				else if(basicTimestamp == "23,24") {
//					begin = date+" 23:00";
//					end = date+" 23:59";
//				}
//				//去除上一次点击占用的格子
//				$(".dialogcontent").find(".weekList_table").each(function() {
//					$(this).find(".title_table").find(".newAppendDiv").each(function() {
//						var $this = $(this);
//						$this.attr("class","freeDiv");
//					});
//				}); 
//				
//				if($this.attr("class").indexOf("preDiv") == -1) {
//					$this.attr("class","newAppendDiv");
//				}
//				var thisNum = $this.attr("num");
//				if(minTimeUnit == "60") {
//						var	$this2 = $this.prev();
//						var prevClass = $this2.attr("class");
//						var prevNum = $this2.attr("num");
//						var n =0; var n2=0; 
//						var m =0; var m2=0;
//						if(prevNum != undefined && prevClass != undefined && prevClass.indexOf("preDiv")==-1 &&  prevNum%12 == 0) {
//							$this2.attr("class","newAppendDiv");
//							n2 ++;
//						}
//						if(prevClass != undefined && prevClass.indexOf("preDiv")>-1) {
//							n ++;
//						}
//						while(prevClass != undefined &&  prevClass.indexOf("preDiv") == -1 && prevNum%12 != 0) {
//							$this2 = $this2.prev();
//							prevClass = $this2.attr("class");
//							prevNum = $this2.attr("num");
//							
//							if(prevClass != undefined && prevClass.indexOf("preDiv")>-1) {
//								var preNum = $this2.attr("num");
//								for(var jj = preNum;jj<parseInt(thisNum, 10);jj++) {
//									n++;
//									var jjj = parseInt(parseInt(jj, 10)+parseInt(1, 10));
//									$this.parents(".basic").find("div[num='"+jjj+"']").attr("class","newAppendDiv");
//								}
//								break;
//							}else {
//								if(prevNum%12 == 0) {
//									for(var jj=prevNum;jj<parseInt(thisNum, 10);jj++) {
//										n2++;
//										$this.parents(".basic").find("div[num='"+jj+"']").attr("class","newAppendDiv");
//									}
//									break;
//								}
//							}
//						}
//						
//						var $this3 = $this.next();
//						var nextClass = $this3.attr("class");
//						var nextNum = $this3.attr("num");
//						if(parseInt(parseInt(nextNum, 10)+parseInt(1, 10))%12 ==0) {
//							$this3.attr("class","newAppendDiv");
//							m2++;
//						}
//						if(nextClass != undefined && nextClass.indexOf("preDiv") > -1) {
//							m ++;
//						}
//						while(nextClass != undefined && nextClass.indexOf("preDiv") == -1 && parseInt(parseInt(nextNum, 10)+parseInt(1, 10))%12 !=0) {
//							$this3 = $this3.next();
//							nextClass = $this3.attr("class");
//							nextNum = $this3.attr("num");
//							if(nextClass.indexOf("preDiv")>-1) {
//								var relNum = $this3.attr("num");
//								for(var jj = thisNum;jj<parseInt(relNum, 10);jj++) {
//									m++;
//									$this.parents(".basic").find("div[num='"+jj+"']").attr("class","newAppendDiv");
//								}
//								break;
//							}else {
//								if(parseInt(parseInt(nextNum, 10)+parseInt(1, 10))%12 == 0) {
//									for(var jj=thisNum;jj<parseInt(nextNum, 10)+parseInt(1, 10);jj++) {
//										m2++;
//										$this.parents(".basic").find("div[num='"+jj+"']").attr("class","newAppendDiv");
//									}
//									break;
//								}
//							}
//						}
//						if(n==0 && m==0 ) {//说明两边没被堵住
//							
//						}
//						else if(n==0) {//说明左边没被堵住
//							var addNum = parseInt(12-(n2+m));//需要往点击的div所在的左边的td增加的div的数量
//							var prevBasicDivLastClass = $this.parents(".basic").prev().find("div").last().attr("class");
//							if(prevBasicDivLastClass != undefined && prevBasicDivLastClass.indexOf("preDiv") == -1) {
//								addNum --;
//								$this.parents(".basic").prev().find("div").last().attr("class","newAppendDiv");
//							}
//							var prevBasicDivLastNum = $this.parents(".basic").prev().find("div").last().attr("num");
//							var lastPrev = $this.parents(".basic").prev().find("div").last().prev();
//							while(prevBasicDivLastClass !=undefined && prevBasicDivLastClass.indexOf("preDiv") == -1 && addNum > 0) {
//								addNum --;
//								prevBasicDivLastClass = lastPrev.attr("class");
//								prevBasicDivLastNum = lastPrev.attr("num");
//								if(prevBasicDivLastClass != undefined && prevBasicDivLastClass.indexOf("preDiv") == -1) {
//									lastPrev.attr("class","newAppendDiv");
//								}
//								lastPrev = lastPrev.prev();
//							}
//						}
//						else if(m==0) {//说明右边没被堵住
//							var addNum = parseInt(12-(n+m2));//需要往点击的div所在的右边的td增加的div的数量
//							var nextBasicDivFirstClass = $this.parents(".basic").next().find("div").first().attr("class");
//							if(nextBasicDivFirstClass != undefined && nextBasicDivFirstClass.indexOf("preDiv") == -1) {
//								addNum --;
//								$this.parents(".basic").next().find("div").first().attr("class","newAppendDiv");
//							}
//							var nextBasicDivFirstNum = $this.parents(".basic").next().find("div").first().attr("num");
//							var firstNext = $this.parents(".basic").next().find("div").first().next(); 
//							while(nextBasicDivFirstClass != undefined && nextBasicDivFirstClass.indexOf("preDiv") == -1 && addNum > 0) {
//								addNum --;
//								nextBasicDivFirstClass = firstNext.attr("class");
//								nextBasicDivFirstNum = firstNext.attr("num");
//								if(nextBasicDivFirstClass != undefined && nextBasicDivFirstClass.indexOf("preDiv") == -1) {
//									firstNext.attr("class","newAppendDiv");
//								}
//								firstNext = firstNext.next();
//							}
//						}
//				}
			});
			
			// add by wujx 2016-03-01 begin
			//定义setTimeout执行方法，用于处理单击事件和双击事件分开，不处理的话，点击双击会触发单击事件
			var TimeFn = null;
			
			// add by wujx 2016-03-01 点击已占用数据，产生取消占用效果
			$(".newAppendDiv").die().live("click",function(e){
				// 取消上次延时未执行的方法
			    clearTimeout(TimeFn);
			    var $this = $(this);
				var firstNumForThisTd = parseInt($this.parent().find("div:first").attr("num"));
				var num = parseInt($this.attr("num"));
				
				var lastNumForThisTd = firstNumForThisTd + 6;
				if(num - firstNumForThisTd > 5){
					lastNumForThisTd = lastNumForThisTd + 6;
				}
			    //执行延时
			    TimeFn = setTimeout(function(){
			        //do function在此处写单击事件要执行的代码
					//移除以前的点击样式
					$.each($(".newAppendDiv"), function(key, val){
						if ($(val).attr("num") < lastNumForThisTd){
							$(val).attr("class","freeDiv");
						}
					});
			    },300);
			});
			// add by wujx 2016-03-01 end
			
			// modify by wujx 2016-03-01 双击占用数据，表示确定
			$(".newAppendDiv").live("dblclick",function() {
				// 取消上次延时未执行的方法
			    clearTimeout(TimeFn);
			    //双击事件的执行代码
				var date = $(this).parents("tr").attr("date");
				var beginNum = 0;
				var endNum = 0;
				beginNum = $(".newAppendDiv").first().attr("num");
				endNum = $(".newAppendDiv").last().attr("num");
				var beginHour = Math.floor(beginNum*5/60);
				if(beginHour>0 && beginHour<=9) {
					beginHour = "0"+beginHour;
				}
				var beginMinutes = beginNum*5%60;
				if(beginMinutes>=0 && beginMinutes<=9) {
					beginMinutes = "0"+beginMinutes;
				}
				begin =date + " " + beginHour + ":" + beginMinutes;
				
				var endHour = Math.floor((parseInt(endNum, 10)+1)*5/60);
				if(endHour>0 && endHour<=9) {
					endHour = "0"+endHour;
				}
				var endMinutes = (parseInt(endNum, 10)+1)*5%60;
				if(endMinutes>=0 && endMinutes<=9) {
					endMinutes = "0"+endMinutes;
				}
				end =date + " " + endHour + ":" + endMinutes;
				
				var meetroomname = $(this).parents("tr").attr("meetroomname");
				var resourceUUID = $(this).parents("tr").attr("uuid");
				var resCode = $(this).parents("tr").attr("resCode");
				if (callback){
					var returnValue = '{"beginTime": "'+ begin  + '","endTime": "'  +end +  '","employName": "' +meetroomname + '","uuid": "'  + resourceUUID  + '","resCode":"' +resCode + '"}';
					callback($.parseJSON(returnValue));
				}
				closeDialog();
			});
		   }
		};
})(jQuery);