/**
 * 执行一个本地程序，或者从浏览器打开一个网页
 * @param {*} action 一个网页url或执行一个本地程序
 */
function OnOpenOAClicked(action) {
    wps.OAAssist.ShellExecute(action);
}