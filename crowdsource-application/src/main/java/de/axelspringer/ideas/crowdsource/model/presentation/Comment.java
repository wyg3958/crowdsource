package de.axelspringer.ideas.crowdsource.model.presentation;

import de.axelspringer.ideas.crowdsource.model.persistence.CommentEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private DateTime created;

    private String userName;

    @NotNull
    private String comment;

    public Comment(CommentEntity commentEntity) {
        this.created = commentEntity.getCreatedDate();
        this.userName = UserHelper.determineNameFromEmail(commentEntity.getUser().getEmail());
        this.comment = commentEntity.getComment();
    }
}
