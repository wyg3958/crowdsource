package de.axelspringer.ideas.crowdsource.model.presentation;

import de.axelspringer.ideas.crowdsource.model.persistence.CommentEntity;
import de.axelspringer.ideas.crowdsource.util.UserHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private DateTime created;

    private String userName;

    @NotEmpty
    private String comment;

    public Comment(CommentEntity commentEntity) {
        this.created = commentEntity.getCreatedDate();
        this.userName = UserHelper.determineNameFromEmail(commentEntity.getUser().getEmail());
        this.comment = commentEntity.getComment();
    }
}
