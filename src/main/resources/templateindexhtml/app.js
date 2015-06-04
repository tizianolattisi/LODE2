'use strict';

// Declare app level module which depends on views, and components
/*
angular.module('myApp', [
  'ngRoute',
  'myApp.view1',
  'myApp.view2',
  'myApp.version'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/view1'});
}]);
*/

var app = angular.module("App", []);

app
    .filter('getfilename', function() {
        return function(input, delimiter) {
            var delimiter = delimiter || '/';
            var tkns = input.split(delimiter);
            return tkns[tkns.length-1];
        }
    })

    .controller("CourseCtrl", function($scope, $http) {
    $http.get('course.json').
        success(function(data, status, headers, config) {
            $scope.course = data;
        }).
        error(function(data, status, headers, config) {
            // log error
        });
});
