module.exports = function (grunt) {
    // 插件配置
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        uglify: {
            options: {
                // banner : '/*! <%= pkg.file %> <%=
                // grunt.template.today("yyyy-mm-dd") %> */\n'
            },
            build: {
                src: 'WebContent/mobile/mui/js/mui.js',
                dest: 'WebContent/mobile/mui/js/mui.min.js'
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
                },
            },
            src: ['WebContent/resources/pt/js/constant.js', 'WebContent/resources/pt/js/commons.js',
                'WebContent/resources/pt/js/app/appContext.js', 'WebContent/resources/pt/js/app/app.js',
                'WebContent/mobile/mui/js/app/app.js']
        }
    });

    // 加载提供"uglify"任务的插件
    grunt.loadNpmTasks('grunt-contrib-uglify');
    // 加载提供"jshint"任务的插件
    grunt.loadNpmTasks('grunt-contrib-jshint');

    // 默认任务
    grunt.registerTask('default', ['jshint']);
}