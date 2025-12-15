module.exports = function (grunt) {
    // 插件配置
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        uglify: {
            options: {
                // banner : '/*! <%= pkg.file %> <%=
                // grunt.template.today("yyyy-mm-dd") %> */\n'
                mangle: true, // 混淆变量名
                compress: {
                    drop_debugger: true,
                    drop_console: true
                }
            },
            // build1 : {
            // src : 'src/main/webapp/mobile/mui/js/mui.js',
            // dest : 'src/main/webapp/mobile/mui/js/mui.min.js'
            // },
            build2: {
                src: 'src/main/webapp/mobile/mui/js/app/dyform/mui.DyformExplain.js',
                dest: 'src/main/webapp/mobile/mui/js/app/dyform/mui.DyformExplain.min.js'
            },
            build3: {
                src: 'src/main/webapp/mobile/mui/js/app/workflow/mui.WorkViewProxy.js',
                dest: 'src/main/webapp/mobile/mui/js/app/workflow/mui.WorkViewProxy.min.js'
            },
            build4: {
                src: 'src/main/webapp/mobile/mui/js/common/mui.fileupload.js',
                dest: 'src/main/webapp/mobile/mui/js/common/mui.fileupload.min.js'
            },
            build5: {
                src: 'src/main/webapp/mobile/mui/js/common/mui.unit.js',
                dest: 'src/main/webapp/mobile/mui/js/common/mui.unit.min.js'
            }
        },
        jshint: {
            options: {
                evil: true,
                curly: true,
                eqeqeq: true,
                eqnull: true,
                browser: true,
                globals: {
                    jQuery: true
                }
            },
            src: ['src/main/resources/com/wellsoft/pt/dms/js/listview/DmsDataManagementViewDevelopment.js']
        }
    });

    // 加载提供"uglify"任务的插件
    grunt.loadNpmTasks('grunt-contrib-uglify');
    // 加载提供"jshint"任务的插件
    grunt.loadNpmTasks('grunt-contrib-jshint');

    // 默认任务
    grunt.registerTask('default', ['uglify']);
}