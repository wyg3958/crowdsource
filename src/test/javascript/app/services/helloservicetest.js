/**
 * tests for transactionservice.js
 */
describe("helloservice", function () {

    var helloservice;
    var $httpBackend;

    beforeEach(function () {
        // app module
        module('crowdsource');

        // let angular inject dependencies
        inject(function (_$httpBackend_, _helloservice_) {
            $httpBackend = _$httpBackend_;
            helloservice = _helloservice_;
        })
    });

    it("should contact the backend when method 'get' is called", function () {

        // expect a get request
        $httpBackend.expect('GET', 'hello').respond(200, {message: 'hi'});

        // query and expect a response
        var hello = helloservice.get();

        // flush http backend
        $httpBackend.flush();

        // assertions
        expect(hello.message).toBe('hi');
    });
});
