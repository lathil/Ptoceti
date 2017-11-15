var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var CopyWebpackPlugin = require('copy-webpack-plugin');
var helpers = require('./helpers');

module.exports = {
	entry : {
		'polyfills' : './src/polyfills.ts',
		'vendor' : './src/vendor.ts',
		'app' : './src/main.ts'
	},

	resolve : {
		extensions : [ '.ts', '.js' ]
	},

	module : {
		rules : [ {
			test : /\.ts$/,
			loaders : [ {
				loader : 'awesome-typescript-loader',
				options : {
					configFileName : helpers.root('src', 'tsconfig.app.json')
				}
			}, 'angular-router-loader', 'angular2-template-loader' ]
		},
		{
			test : /\.html$/,
			loader : 'html-loader'
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
			test : /\.css$/,
			exclude : helpers.root('src', 'app'),
			loader : ExtractTextPlugin.extract({
				fallbackLoader : 'style-loader',
				loader : 'css-loader?sourceMap'
			})
		}, {
			test : /\.css$/,
			include : helpers.root('src', 'app'),
			loader : 'raw-loader'
		} ]
	},

	plugins : [
	// Workaround for angular/angular#11580
	new webpack.ContextReplacementPlugin(
	// The (\\|\/) piece accounts for path separators in *nix and Windows
	/angular(\\|\/)core(\\|\/)@angular/, helpers.root('./src'), // location of
																// your src
	{} // a map of your routes
	),

	new webpack.optimize.CommonsChunkPlugin({
		name : [ 'app', 'vendor', 'polyfills' ]
	}),
	
	new CopyWebpackPlugin([
	    // Copy directory contents to {output}/
	    { from: './src/env.json' }, { from: './src/config.development.json' }
	  ]),

	new HtmlWebpackPlugin({
		template : 'src/index.html'
	}) ]
};