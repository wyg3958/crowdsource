describe('teaser metrics service', function () {

    var TeaserMetrics;

    beforeEach(function () {
        module('crowdsource');

        inject(function (_TeaserMetrics_) {
            TeaserMetrics = _TeaserMetrics_;

            // freeze all calls to new Date() to the current time
            jasmine.clock().mockDate(new Date());
        });
    });

    it('should only return the days if more than 7 days are remaining', function () {
        expect(TeaserMetrics.formatRemainingTime(future(366, 23, 8, 0))).toBe("366 Tage");
        expect(TeaserMetrics.formatRemainingTime(future(7, 12, 30, 2))).toBe("7 Tage");
        expect(TeaserMetrics.formatRemainingTime(future(7, 0, 0, 0))).toBe("7 Tage");
    });

    it('should return the days, hours, minutes and seconds if less than 7 days are remaining', function () {
        expect(TeaserMetrics.formatRemainingTime(future(6, 23, 59, 59))).toBe("6d 23h 59m 59s");
        expect(TeaserMetrics.formatRemainingTime(future(1, 0, 0, 0))).toBe("1d 0h 0m 0s");
    });

    it('should return the hours, minutes and seconds if less than a day is remaining', function () {
        expect(TeaserMetrics.formatRemainingTime(future(0, 23, 59, 59))).toBe("23h 59m 59s");
        expect(TeaserMetrics.formatRemainingTime(future(0, 1, 0, 0))).toBe("1h 0m 0s");
    });

    it('should return the minutes and seconds if less than an hour is remaining', function () {
        expect(TeaserMetrics.formatRemainingTime(future(0, 0, 59, 59))).toBe("59m 59s");
        expect(TeaserMetrics.formatRemainingTime(future(0, 0, 1, 0))).toBe("1m 0s");
    });

    it('should return the seconds if less than a minute is remaining', function () {
        expect(TeaserMetrics.formatRemainingTime(future(0, 0, 0, 59))).toBe("59s");
        expect(TeaserMetrics.formatRemainingTime(future(0, 0, 0, 1))).toBe("1s");
    });

    it('should return "beendet" if less than a second is remaining', function () {
        expect(TeaserMetrics.formatRemainingTime(future(0, 0, 0, 0, 999))).toBe(null);
        expect(TeaserMetrics.formatRemainingTime(future(0, 0, 0, 0, 1))).toBe(null);
        expect(TeaserMetrics.formatRemainingTime(future(0, 0, 0, 0, 0))).toBe(null);
        expect(TeaserMetrics.formatRemainingTime(future(0, 0, 0, 0, -1))).toBe(null);
        expect(TeaserMetrics.formatRemainingTime(future(-366, 0, 0, 0, 0))).toBe(null);
    });

    function future(days, hours, minutes, seconds, milis) {
        return moment()
            .add(days, 'days')
            .add(hours, 'hours')
            .add(minutes, 'minutes')
            .add(seconds, 'seconds')
            .add(milis || 0, 'milliseconds')
            .toDate();
    }

});