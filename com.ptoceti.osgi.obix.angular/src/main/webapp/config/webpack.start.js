
      
var webpackMerge = require('webpack-merge');
var commonConfig = require('./webpack.common.js');
var helpers = require('./helpers');

module.exports = webpackMerge(commonConfig, {
  devtool: 'source-map',
    mode: 'development',

  output: {
    path: helpers.root('dist'),
    publicPath: '/',
    filename: '[name].js',
    chunkFilename: '[id].chunk.js'
  },


  devServer: {
	inline:true,
      port: 8090,
    historyApiFallback: true,
      stats: 'normal',
    setup(app){
	  app.get('/env.json', function(req, res) {
	    res.json({ env: 'development' });
	    	}),
	  app.get('/config.development.json', function(req, res) {
          res.json({
              secure: false,
              lobbyUrl: "http://localhost:8080/obix/rest/",
              oauthTokenUrl: "http://localhost:8080/obix/oauth/token"
          });
		  });
    }
      //proxy: {
      //	  "/obix/rest": "http://localhost:3004"
      //	}
  }
});