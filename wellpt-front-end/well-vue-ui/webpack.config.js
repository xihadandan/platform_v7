const path = require('path');
const TerserPlugin = require('terser-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const VueLoaderPlugin = require('vue-loader/lib/plugin');
const shell = require('shelljs');
const fs = require('fs');
const lodash = require('lodash');

const resolve = dir => path.join(__dirname, dir);

shell.rm('-rf', path.resolve(process.cwd(), 'lib'));

function getComponentEntries(_p) {
  let files = fs.readdirSync(resolve(_p));

  const componentEntries = files.reduce((fileObj, item) => {
    // 文件路径
    const itemPath = path.join(_p, item);
    // 在文件夹中
    const isDir = fs.statSync(itemPath).isDirectory();
    const [name, suffix] = item.split('.');

    // 文件中的入口文件
    if (isDir) {
      // lodash.upperFirst(lodash.camelCase(item))
      fileObj[item] = resolve(path.join(itemPath, 'index.js'));
    }

    return fileObj;
  }, {});

  return componentEntries;
}

const entrys = getComponentEntries('src/components');
entrys.index = resolve('src/components/index.js');
console.log(entrys);

module.exports = {
  mode: process.env.NODE_ENV,
  entry: entrys,
  resolve: {
    extensions: ['.js', '.jsx', '.css', '.scss', '.vue'],
    alias: {
      vue$: 'vue/dist/vue.esm.js',
      '@': resolve('src')
    }
  },
  output: {
    path: resolve('lib'),
    filename: function (chunkData) {
      return chunkData.chunk.name === 'index' ? '[name].js' : '[name]/index.js';
    },
    library: 'well-vue-ui',
    libraryTarget: 'umd',
    globalObject: "typeof self !== 'undefined' ? self : this"
  },
  externals: {
    vue: 'vue'
  },
  optimization: {
    minimize: true,
    minimizer: [
      new TerserPlugin({
        extractComments: false,
        parallel: true,
        terserOptions: {
          ecma: undefined,
          warnings: false,
          parse: {},
          compress: {
            drop_console: true,
            drop_debugger: true,
            pure_funcs: ['console.log'] // 移除console
          }
        }
      })
    ]
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        loader: 'babel-loader',
        exclude: /node_modules/
      },
      {
        test: /\.vue$/,
        loader: 'vue-loader',
        options: {
          loaders: {
            scss: {
              use: [MiniCssExtractPlugin.loader, 'vue-style-loader!css-loader!sass-loader']
            },
            sass: {
              use: [MiniCssExtractPlugin.loader, 'vue-style-loader!css-loader!sass-loader?indentedSyntax']
            }
          }
        },
        exclude: /node_modules/
      },
      {
        test: /\.css$/,
        use: [
          MiniCssExtractPlugin.loader,
          'css-loader',
          {
            loader: 'postcss-loader'
          }
        ]
      },
      {
        test: /\.scss$/,
        use: [MiniCssExtractPlugin.loader, { loader: 'css-loader', options: { importLoaders: 3 } }, 'sass-loader', 'postcss-loader']
      }
    ]
  },
  plugins: [
    new VueLoaderPlugin(),
    new OptimizeCSSAssetsPlugin({
      cssProcessor: require('cssnano'),
      cssProcessorPluginOptions: {
        preset: [
          'default',
          {
            discardComments: { removeAll: true },
            normalizeUnicode: false
          }
        ]
      },
      canPrint: true
    }),
    new MiniCssExtractPlugin({
      filename: '[name]/style.css'
    })
  ]
};
