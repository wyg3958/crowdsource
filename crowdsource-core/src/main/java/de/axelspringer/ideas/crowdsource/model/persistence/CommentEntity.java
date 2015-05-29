package de.axelspringer.ideas.crowdsource.model.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor // needed for serialization
@Document(collection = "comments")
public class CommentEntity {

    @Id
    private String id;

    @DBRef
    private ProjectEntity project;

    @DBRef
    private UserEntity user;

    private String comment;

    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;

    public CommentEntity(ProjectEntity projectEntity, UserEntity userEntity, String comment) {

        this.project = projectEntity;
        this.user = userEntity;
        this.comment = comment;
    }
}
