describe('authentication service', function () {

    var $httpBackend, $http, Authentication;

    beforeEach(function() {
        module('crowdsource');

        inject(function(_$httpBackend_, _$http_, _Authentication_) {
            $httpBackend = _$httpBackend_;
            $http = _$http_;
            Authentication = _Authentication_;
        });

        localStorage.clear(); // reset
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

        expect(Authentication.isLoggedIn()).toBe(true);
    });

    it('should request an access token from the backend and store the response in localStorage', function () {

        $httpBackend.expectPOST('/oauth/token', 'username=username&password=password&client_id=web&grant_type=password')
            .respond(200, { token_type: 'bearer', access_token: 'xyz' });

        Authentication.login('username', 'password');
        $httpBackend.flush();

        var storedTokensString = localStorage['tokens'];
        expect(storedTokensString).toBe('{"token_type":"bearer","access_token":"xyz"}');
    });

    it('should load the access token from the localStorage and include it in every following request', function() {
        localStorage.setItem('tokens', '{"token_type":"bearer","access_token":"xxxx"}');

        Authentication.init();

        $httpBackend.expectGET('/some-protected-resource', function headersValidator(headers) {
            return headers.Authorization == 'bearer xxxx';
        }).respond(200);

        expect(Authentication.isLoggedIn()).toBe(true);

        $http.get('/some-protected-resource');
        $httpBackend.flush();
    });

    it('should include no access token in every following request if there was none in localStorage', function() {
        Authentication.init();

        $httpBackend.expectGET('/some-protected-resource', function headersValidator(headers) {
            return headers.Authorization == null;
        }).respond(200);

        expect(Authentication.isLoggedIn()).toBe(false);

        $http.get('/some-protected-resource');
        $httpBackend.flush();
    });
});
