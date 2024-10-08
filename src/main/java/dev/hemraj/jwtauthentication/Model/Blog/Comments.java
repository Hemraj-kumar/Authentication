package dev.hemraj.jwtauthentication.Model.Blog;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Document("comments")
@Data
public class Comments {
    private String id;
    private String commentAuthorId;
    private String name;
    private String comment;
    private String createdAt;

    public Comments(String id, String commentAuthorId, String name, String comment){
        this.id = UUID.randomUUID().toString();
        this.comment = comment;
        this.commentAuthorId = commentAuthorId;
        this.name= name;
    }

}
