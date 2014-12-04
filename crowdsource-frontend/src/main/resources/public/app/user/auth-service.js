angular.module('crowdsource')

    .factory('Authentication', function ($resource, $http) {

        // token resource requires a http form post
        var tokenResource = $resource('/oauth/token', {}, {
            save: {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }
        });

        // contains access_token, refresh_token and other stuff that came from the server
        var authTokens;

        function useAuthTokens(tokens) {
            authTokens = tokens;
            $http.defaults.headers.common['Authorization'] = tokens.token_type + ' ' + tokens.access_token;
        }

        return {
            login: function(email, password) {
                // $.param creates a form encoded string, e.g. "username=xyz&password=secret&..."
                var requestBody = $.param({
                    username: email,
                    password: password,
                    client_id: 'web',
                    grant_type: 'password'
                });

                var promise = tokenResource.save(requestBody).$promise;
                return promise.then(useAuthTokens);
            }
        };
    });