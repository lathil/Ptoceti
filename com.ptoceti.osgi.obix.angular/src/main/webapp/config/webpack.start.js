
      
var webpackMerge = require('webpack-merge');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var commonConfig = require('./webpack.common.js');
var helpers = require('./helpers');

module.exports = webpackMerge(commonConfig, {
  devtool: 'source-map',

  output: {
    path: helpers.root('dist'),
    publicPath: '/',
    filename: '[name].js',
    chunkFilename: '[id].chunk.js'
  },

  plugins: [
    new ExtractTextPlugin('[name].css')
  ],

  devServer: {
      inline: true,
      port: 8081,
      historyApiFallback: true,
      stats: 'minimal',
      setup(app) {
          app.get('/env.json', function (req, res) {
              res.json({env: 'development'});
          }),
              app.get('/config.development.json', function (req, res) {
                  res.json({
                      secure: false,
                      lobbyUrl: "http://192.168.8.89:8080/obix/rest/",
                      oauthTokenUrl: "http://192.168.8.89:8080/obix/oauth/token"
                  });
              });
      },
    proxy: {
        "/obix/rest": "http://localhost:8080"
    }
  }
});