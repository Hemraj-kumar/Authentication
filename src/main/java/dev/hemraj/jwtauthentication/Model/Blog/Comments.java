package dev.hemraj.jwtauthentication.Model.Blog;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("comments")
@Data
public class Comments {
    private String commentAuthorId;
    private String name;
    private String comment;
}
