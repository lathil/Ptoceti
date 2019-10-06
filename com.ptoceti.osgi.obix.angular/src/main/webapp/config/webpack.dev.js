
      
var webpackMerge = require('webpack-merge');
var commonConfig = require('./webpack.common.js');
var helpers = require('./helpers');

module.exports = webpackMerge(commonConfig, {
  devtool: 'source-map',
    mode: 'development',

  output: {
    path: helpers.root('dist'),
    publicPath: './',
    filename: '[name].js',
    chunkFilename: '[id].chunk.js'
  },

  
});