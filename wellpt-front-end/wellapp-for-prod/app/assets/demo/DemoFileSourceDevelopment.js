define(["constant", "commons", "server", "appContext", "appModal", "FileSourceDevelopment"],
    function (constant, commons, server, appContext, appModal, FileSourceDevelopment) {
        var JDS = server.JDS;

        var LocalFileSourceDevelopment = function () {
            FileSourceDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(LocalFileSourceDevelopment, FileSourceDevelopment, {
            init: function () {
                // alert(this.wellFileUpload);
            },
            getDialogParams() {
                return {
                    title: "文件选择示例",
                };
            },
            getDialogContentHtml: function () {
                return '<div><button class="file_set_value">设置值</button><span>文件id</span><input name="fileId" type="text"/><span>文件名称</span><input name="fileName" type="text"/><span>文件大小</span><input name="fileSize" type="text"/></div>';//自定义弹窗内容
            },
            excJs: function ($dialogContent) {
                var _self = this;
                $dialogContent.find('.file_set_value').click(function () {
                    $dialogContent.find('input[name="fileId"]').val('fdb81b9515e049bf9a2b992af55187f7');
                    $dialogContent.find('input[name="fileName"]').val('1.txt');
                    $dialogContent.find('input[name="fileSize"]').val('5');
                });
            },
            getFiles: function () {
                var files = [];

                var fileId = this.fileDialog.find('input[name="fileId"]').val();
                var fileName = this.fileDialog.find('input[name="fileName"]').val();
                var fileSize = 5;
                try {
                    fileSize = parseInt(this.fileDialog.find('input[name="fileSize"]').val());
                } catch (e) {
                    alert("fileSize is invalid!")
                }
                files.push(
                    {fileId: fileId, fileName: fileName, fileSize: fileSize}
                );
                return files
            }
        });
        return LocalFileSourceDevelopment;
    });

