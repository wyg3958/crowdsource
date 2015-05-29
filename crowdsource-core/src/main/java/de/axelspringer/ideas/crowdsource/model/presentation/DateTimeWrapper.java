package de.axelspringer.ideas.crowdsource.model.presentation;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@NoArgsConstructor // required for serialization
public class DateTimeWrapper {

    private DateTime dateTime;

    public DateTimeWrapper(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
