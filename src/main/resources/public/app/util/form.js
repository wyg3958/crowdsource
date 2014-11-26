angular.module('crowdsource')

    .factory('FormUtils', function () {
        return {
            /**
             * The server response is expected to be
             * {
             *   "fieldViolations":
             *   {
             *     "email": "inactivated",
             *     "name": "unique",
             *     ...
             *   }
             * }
             *
             * For every key-value pair in "fieldViolations", the
             * validity of the form field is set to false:
             *
             *   form.email.$setValidity("inactivated", false);
             *   form.name.$setValidity("unique", false);
             *
             *
             * @param form The form to apply the field validities to
             * @param response The server's response
             * @returns {boolean} if at least one field's validity was set to false
             */
            applyServerErrorResponse: function (form, response) {
                var appliedErrors = false;

                if (response.data && response.data.fieldViolations) {
                    angular.forEach(response.data.fieldViolations, function (rule, field) {
                        if (form[field]) {
                            form[field].$setValidity(rule, false);

                            appliedErrors = true;
                        }
                    });
                }

                return appliedErrors;
            }
        };
    });