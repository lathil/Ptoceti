var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var MiniCssExtractPlugin = require('mini-css-extract-plugin');
var CopyWebpackPlugin = require('copy-webpack-plugin');
var helpers = require('./helpers');

module.exports = {
    entry: './src/main.ts',

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
            use: 'html-loader'
		},
		{
			test : /\.(png|jpe?g|gif|ico)$/,
            use: 'file-loader?name=assets/[name].[hash].[ext]'
		},
		{
			test : /\.woff(\?v=\d+\.\d+\.\d+)?$/,
            use: 'url-loader?limit=10000&mimetype=application/font-woff'
		},
		{
			test : /\.woff2(\?v=\d+\.\d+\.\d+)?$/,
            use: 'url-loader?limit=10000&mimetype=application/font-woff'
		},
		{
			test : /\.ttf(\?v=\d+\.\d+\.\d+)?$/,
            use: 'url-loader?limit=10000&mimetype=application/octet-stream'
		},
		{
			test : /\.eot(\?v=\d+\.\d+\.\d+)?$/,
            use: 'file-loader'
		},
		{
			test : /\.svg(\?v=\d+\.\d+\.\d+)?$/,
            use: 'url-loader?limit=10000&mimetype=image/svg+xml'
		},
		{
            test: /\.(scss|css)$/,
            //test: /\.(css)$/,
            use: ['raw-loader', 'sass-loader'], // don't use css-loader for ng2 （unusual）
        }
            //{
            //test: /\.(scss|css)$/,
            //test: /\.scss$/,
            //exclude : helpers.root('src', 'app'),
            //exclude: [/node_modules/],
            //use: [
            //	{
            //		loader: MiniCssExtractPlugin.loader,
            //		options: {
            // you can specify a publicPath here
            // by default it uses publicPath in webpackOptions.output
            //			publicPath: '../',
            //			hmr: process.env.NODE_ENV === 'development',
            //		},
            //	},
            //   'css-loader',
            //    'sass-loader',
            //],
            //}
        ]
	},

	plugins : [
        // Workaround for angular/angular#11580
        new webpack.ContextReplacementPlugin(
            // The (\\|\/) piece accounts for path separators in *nix and Windows
            /angular(\\|\/)core(\\|\/)@angular/, helpers.root('./src'), // location of
            // your src
            {} // a map of your routes
        ),

        new MiniCssExtractPlugin({
            // Options similar to the same options in webpackOptions.output
            // both options are optional
            filename: '[name].[hash].css',
            chunkFilename: '[id].css',
        }),

        new CopyWebpackPlugin([
            // Copy directory contents to {output}/
            {from: './src/env.json'}, {from: './src/config.development.json'}
        ]),

        new HtmlWebpackPlugin({
            template: './src/index.html'
        })
    ]
};