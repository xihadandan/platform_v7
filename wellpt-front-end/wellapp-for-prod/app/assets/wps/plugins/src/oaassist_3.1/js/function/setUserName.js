function setUserName(params){
    if (params&&params.userName) {
        wps.WpsApplication().UserName = params.userName; //修改当前文档用户名
    }
}