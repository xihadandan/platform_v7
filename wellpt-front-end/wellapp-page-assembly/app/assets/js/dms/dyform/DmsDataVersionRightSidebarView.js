define("DmsDataVersionRightSidebarView",
    ["jquery", "commons", "constant", "server", "appContext", "appModal"],
    function ($, commons, constant, server, appContext, appModal) {


        var DmsDataVersionRightSidebarView = function(options) {
            this.options = options;
            var formUuid = options.formUuid;
            var dataUuid = options.dataUuid;
            var container=options.container;
            container.empty();
            //查找版本数据
            server.JDS.call({
                async: true,
                service: "dyFormVersionActionService.getAllVersion",
                data: [formUuid, dataUuid],
                version: "",
                success: function (result) {
                    if (result.success) {
                        var sortedData=_.sortByOrder(result.data,'version','desc');
                        var $table=$("<table>",{"class":"table" ,"id":"formDataVersionTable"});
                        var $tr=[];
                        for(var i=0,len=sortedData.length;i<len;i++){
                            $tr.push(
                                $("<tr>",{"class":"data-version-tr",'data-uuid':sortedData[i].uuid}).append(
                                    $("<td>").text('V'+parseFloat(sortedData[i].version).toFixed(1)),
                                    $("<td>").text(sortedData[i].creatorName),
                                    $("<td>").text(sortedData[i].createTime)
                                )
                            )
                            if(i!=len-1){
                                $tr.push($("<tr>").append(
                                    $("<td>",{"colspan":"3"}).append(
                                        $("<div>",{"class":"version-line"}).html('&nbsp;')
                                    )
                                ));
                            }
                        }

                        $table.append($tr);
                        /*var height=$(".sidebar-container").height()-$("#rightSidebarNav").height();
                        var $container=$("<div>",{"style":"overflow-y:auto;height:"+(height-50)+"px;"});
                        $container.append($table)*/;
                        container.append($table);

                        $(".data-version-tr > td:gt(0)").on('click',function(e){
                            e.preventDefault();
                            if($(this)[0].cellIndex==0){//版本号td不能点击触发详情查看
                                return;
                            }
                            options.dmsDataServices.openWindow({
                                ui:options.ui,
                                urlParams : {
                                    dms_id : options.dmsId,
                                    ac_id : "btn_dyform_open_version",// 打开窗口的操作ID
                                    ep_ac_get : "btn_dyform_get_version_data",// 附加的打开窗口后获取版本数据的操作ID
                                    ep_ac_get_v_id : $(this).parent().attr('data-uuid'),// 附加的版本UUID
                                    v_id : $(this).parent().attr('data-uuid')
                                }
                            });

                        });
                    }
                }
            });





        };



        return DmsDataVersionRightSidebarView;


    });




