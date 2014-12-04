angular.module('crowdsource')

    .factory('Authentication', function ($resource, $http) {

        // token resource requires a http form post
        var tokenResource = $resource('/oauth/token', {}, {
            requestTokens: {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }
        });

        /**
         * @param tokens will be registered as http headers for requests and stored in browser storage
         */
        function useAuthTokens(tokens) {
            $http.defaults.headers.common['Authorization'] = tokens.token_type + ' ' + tokens.access_token;
            window.localStorage['tokens'] = JSON.stringify(tokens);
        }

        return {
            init: function () {
                // browser localStorage
                var storage = window['localStorage'];
                if (!storage) {
                    throw "only browsers with local storage are supported";
                }
                var tokensAsString = storage['tokens'];
                if (tokensAsString) {

                    var tokens = JSON.parse(tokensAsString);

                    // set as header
                    useAuthTokens(tokens);
                }
            },

            login: function (email, password) {
                // $.param creates a form encoded string, e.g. "username=xyz&password=secret&..."
                var requestBody = $.param({
                    username: email,
                    password: password,
                    client_id: 'web',
                    grant_type: 'password'
                });

                var promise = tokenResource.requestTokens(requestBody).$promise;
                return promise.then(useAuthTokens);
            }
        };
    });