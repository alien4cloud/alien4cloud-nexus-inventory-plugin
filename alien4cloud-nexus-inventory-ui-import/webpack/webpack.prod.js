const webpack = require('webpack');
const webpackMerge = require('webpack-merge');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
const Visualizer = require('webpack-visualizer-plugin');
const MomentLocalesPlugin = require('moment-locales-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const WorkboxPlugin = require('workbox-webpack-plugin');
const AngularCompilerPlugin = require('@ngtools/webpack').AngularCompilerPlugin;
const CopyWebpackPlugin = require('copy-webpack-plugin');
const path = require('path');

const utils = require('./utils.js');
const commonConfig = require('./webpack.common.js');

const ENV = 'production';
const sass = require('sass');

const mergeStrategy = {
  'resolve.extensions' : 'replace'
};

module.exports = webpackMerge.strategy(mergeStrategy)(commonConfig({ env: ENV }), {
    // Enable source maps. Please note that this will slow down the build.
    // You have to enable it in Terser config below and in tsconfig-aot.json as well
    // devtool: 'source-map',
    resolve: {
        extensions: [ '.prod.ts','.ts', '.js' ]
    },
    entry: {
        polyfills: './projects/alien4cloud-nexus-inventory-ui-import/src/app/polyfills',
        global: './projects/alien4cloud-nexus-inventory-ui-import/assets/styles/main.scss',
        main: './projects/alien4cloud-nexus-inventory-ui-import/src/app/main'
    },
    output: {
        path: utils.root('target/classes/static/'),
        filename: 'app/[name].[hash].bundle.js',
        chunkFilename: 'app/[id].[hash].chunk.js'
    },
    module: {
        rules: [
        {
            test: /(?:\.ngfactory\.js|\.ngstyle\.js|\.ts)$/,
            loader: '@ngtools/webpack'
        },
        {
            test: /\.scss$/,
            use: ['to-string-loader', 'css-loader', {
                loader: 'sass-loader',
                options: { implementation: sass }
            }],
            exclude: /main\.scss/
        },
        {
            test: /main\.scss/,
            use: [
                {
                    loader: MiniCssExtractPlugin.loader,
                    options: {
                        publicPath: '../'
                    }
                },
                'css-loader',
                'postcss-loader',
                {
                    loader: 'sass-loader',
                    options: { implementation: sass }
                }
            ]
        },
        {
            test: /\.css$/,
            use: ['to-string-loader', 'css-loader'],
            exclude: /main\.css/
        },
        {
            test: /main\.css/,
            use: [
                {
                    loader: MiniCssExtractPlugin.loader,
                    options: {
                        publicPath: '../'
                    }
                },
                'css-loader',
                'postcss-loader'
            ]
        }]
    },
    optimization: {
        runtimeChunk: false,
        minimizer: [
            new TerserPlugin({
                parallel: true,
                cache: true,
                // sourceMap: true, // Enable source maps. Please note that this will slow down the build
                terserOptions: {
                    ecma: 6,
                    ie8: false,
                    toplevel: true,
                    module: true,
                    compress: {
                        dead_code: true,
                        warnings: false,
                        properties: true,
                        drop_debugger: true,
                        conditionals: true,
                        booleans: true,
                        loops: true,
                        unused: true,
                        toplevel: true,
                        if_return: true,
                        inline: true,
                        join_vars: true,
                        ecma: 6,
                        module: true,
                        toplevel: true
                    },
                    output: {
                        comments: false,
                        beautify: false,
                        indent_level: 2,
                        ecma: 6
                    },
                    mangle: {
                        module: true,
                        toplevel: true
                    }
                }
            }),
            new OptimizeCSSAssetsPlugin({})
        ]
    },
    plugins: [
        new MiniCssExtractPlugin({
            // Options similar to the same options in webpackOptions.output
            // both options are optional
            filename: 'content/[name].[contenthash].css',
            chunkFilename: 'content/[id].css'
        }),
        new MomentLocalesPlugin({
            localesToKeep: [
            ]
        }),
        new Visualizer({
            // Webpack statistics in target folder
            filename: '../stats.html'
        }),
        new AngularCompilerPlugin({
            mainPath: utils.root('projects/alien4cloud-nexus-inventory-ui-import/src/app/main.ts'),
            tsConfigPath: utils.root('tsconfig-aot.json'),
            sourceMap: true,
            // Locale configuration
            //i18nInFile: "./src/main/locale/messages.fr.xlf",
            //i18nInFormat: "xlf",
            //i18nInMissingTranslations: "ignore",
            //locale: "fr",
        }),
        new webpack.LoaderOptionsPlugin({
            minimize: true,
            debug: false
        }),
        new WorkboxPlugin.GenerateSW({
          clientsClaim: true,
          skipWaiting: true,
        })
    ],
    mode: 'production'
});
