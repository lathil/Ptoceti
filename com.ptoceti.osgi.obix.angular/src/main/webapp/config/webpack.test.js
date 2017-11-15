
      
var webpack = require('webpack');
var helpers = require('./helpers');

module.exports = {
  devtool: 'inline-source-map',

  resolve: {
    extensions: ['.ts', '.js']
  },

  module: {
    rules: [
      {
        test: /\.ts$/,
        loaders: [
          {
            loader: 'awesome-typescript-loader',
            options: { configFileName: helpers.root('src', 'tsconfig.spec.json') }
          } , 'angular2-template-loader'
        ]
      },
      {
        test: /\.html$/,
        loader: 'html-loader'

      },
      {
			test : /\.(png|jpe?g|gif|ico)$/,
			loader : 'file-loader?name=assets/[name].[hash].[ext]'
		},
		{
			test : /\.woff(\?v=\d+\.\d+\.\d+)?$/,
			loader : 'url-loader?limit=10000&mimetype=application/font-woff'
		},
		{
			test : /\.woff2(\?v=\d+\.\d+\.\d+)?$/,
			loader : 'url-loader?limit=10000&mimetype=application/font-woff'
		},
		{
			test : /\.ttf(\?v=\d+\.\d+\.\d+)?$/,
			loader : 'url-loader?limit=10000&mimetype=application/octet-stream'
		},
		{
			test : /\.eot(\?v=\d+\.\d+\.\d+)?$/,
			loader : 'file-loader'
		},
		{
			test : /\.svg(\?v=\d+\.\d+\.\d+)?$/,
			loader : 'url-loader?limit=10000&mimetype=image/svg+xml'
		},
		{
			test: /\.scss$/,
			exclude: /node_modules/,
			loaders: ['raw-loader', 'sass-loader'] // sass-loader not scss-loader
		},
      {
        test: /\.css$/,
        exclude: helpers.root('src', 'app'),
        loader: 'null-loader'
      },
      {
        test: /\.css$/,
        include: helpers.root('src', 'app'),
        loader: 'raw-loader'
      }
    ]
  },

  plugins: [
    new webpack.ContextReplacementPlugin(
      // The (\\|\/) piece accounts for path separators in *nix and Windows
      /angular(\\|\/)core(\\|\/)@angular/,
      helpers.root('./src'), // location of your src
      {} // a map of your routes
    )
  ]
}