describe('authentication service', function () {

    var $httpBackend, $http, Authentication;

    beforeEach(function() {
        module('crowdsource');

        inject(function(_$httpBackend_, _$http_, _Authentication_) {
            $httpBackend = _$httpBackend_;
            $http = _$http_;
            Authentication = _Authentication_;
        });
    });


    it('should request an access token from the backend and include the access token in every following request', function () {

        $httpBackend.expectPOST('/oauth/token', 'username=username&password=password&client_id=web&grant_type=password')
            .respond(200, { token_type: 'bearer', access_token: 'xyz' });

        Authentication.login('username', 'password');
        $httpBackend.flush();

        $httpBackend.expectGET('/some-protected-resource', function headersValidator(headers) {
            return headers.Authorization == 'bearer xyz';
        }).respond(200);

        $http.get('/some-protected-resource');

        $httpBackend.flush();
    });
});
