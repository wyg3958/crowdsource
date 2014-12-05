angular.module('crowdsource')

    .factory('Authentication', function ($resource, $http, $q) {

        var TOKENS_LOCAL_STORAGE_KEY = 'tokens';

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
            window.localStorage[TOKENS_LOCAL_STORAGE_KEY] = JSON.stringify(tokens);
        }
        function clearAuthTokens() {
            $http.defaults.headers.common['Authorization'] = undefined;
            window.localStorage.removeItem(TOKENS_LOCAL_STORAGE_KEY);
        }

        return {
            init: function () {
                // browser localStorage
                var storage = window['localStorage'];
                if (!storage) {
                    throw "only browsers with local storage are supported";
                }
                var tokensAsString = storage[TOKENS_LOCAL_STORAGE_KEY];
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

                return $q(function(resolve, reject) {
                    tokenResource.requestTokens(requestBody).$promise
                        .then(function(response) {
                            useAuthTokens(response);
                            resolve();
                        })
                        .catch(function(response) {
                            if (response.status == 400 && response.data && response.data.error && response.data.error == 'invalid_grant') {
                                reject('bad_credentials');
                                return;
                            }
                            else {
                                reject('unknown');
                            }
                        });
                });
            },

            logout: clearAuthTokens
        };
    });
