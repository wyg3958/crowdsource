package de.asideas.crowdsource.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class UserHelper {

    public static String determineNameFromEmail(String email) {
        if (email == null) {
            return null;
        }

        int atPos = email.indexOf('@');
        if (atPos < 1) {
            return null;
        }

        String localPart = email.substring(0, atPos);
        List<String> localParts = Arrays.asList(localPart.split("\\."));

        return localParts.stream()
                .map(s -> s.replaceAll("\\d+", ""))
                .map(StringUtils::capitalize)
                .collect(joining(" "));
    }
}
