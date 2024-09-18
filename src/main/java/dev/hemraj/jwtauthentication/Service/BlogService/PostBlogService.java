package dev.hemraj.jwtauthentication.Service.BlogService;

import dev.hemraj.jwtauthentication.Model.Blog.Comments;
import dev.hemraj.jwtauthentication.Model.Blog.Post;
import dev.hemraj.jwtauthentication.RequestDto.CreateBlogDto;
import dev.hemraj.jwtauthentication.ResponseDto.FetchAllBlogsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class PostBlogService {

    @Autowired
    MongoTemplate mongoTemplate;

    public ResponseEntity<?> createBlog(CreateBlogDto blogPostData, String authorId) {
        try {
            Post blog = new Post();

            byte[] decodedAuthorId = Base64.getDecoder().decode(authorId);
            blog.setAuthor_id(new String(decodedAuthorId));

            blog.setTitle(blogPostData.getTitle());
            blog.setContent(blogPostData.getContent());

            ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            blog.setCreatedAt(zonedDateTime.format(dateTimeFormatter));

            if (blogPostData.getComments().isEmpty()) {
                blog.setComments(new ArrayList<>());
            } else {
                blog.setComments(blogPostData.getComments());
            }

            mongoTemplate.save(blog);

            return ResponseEntity.status(HttpStatus.OK).body("Data is been successfully saved!");
        } catch (Exception err) {
            log.error("error in saving data to db");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error in saving data!");
    }

    public ResponseEntity<?> addComments(Comments newComments,String id){
        ObjectId objectId = new ObjectId(id);
        try {
            Post blogData = mongoTemplate.findById(objectId, Post.class);
            if(blogData == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("blog data not found!");
            }

            Query query = new Query(Criteria.where("_id").is(objectId));
            Update update = new Update();
            if (newComments != null) {

                byte[] decodedCommenterId = Base64.getDecoder().decode(newComments.getCommentAuthorId());
                newComments.setCommentAuthorId(new String(decodedCommenterId));

                ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                newComments.setCreatedAt(zonedDateTime.format(dateTimeFormatter));

                update.push("comments", newComments);
                mongoTemplate.updateFirst(query, update, Post.class);

                return ResponseEntity.status(HttpStatus.OK).body("Comment added successfully!");
            }
        }catch (IllegalArgumentException err) {
            log.error("blog id not found : ", err);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid postID");
        }catch (Exception err){
            log.error("Exception in adding comment",err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error in adding comment to the blog");
    }

    // need to add the confirmation for deleting the data in frontend
    public ResponseEntity<?> deleteBlog(String id){
        try{
            Object blogID = new ObjectId(id);
            Post blogData = mongoTemplate.findById(blogID, Post.class);
            Query query = new Query(Criteria.where("_id").is(blogID));

            if(blogData == null) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The data is not found for the provided ID");
            }else{
                mongoTemplate.findAndRemove(query, Post.class);
                return ResponseEntity.status(HttpStatus.OK).body("The data is been deleted successfully for the ID provided");
            }
        }catch (IllegalArgumentException err){
            log.error("blogID not found");
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid postID");
        }catch (Exception err){
            log.error("Error in deleting the blog");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in deleting in the blog");
    }

    public List<FetchAllBlogsResponse> getBlogs(){
        try {
            Query query = new Query();
            List<Post> posts = mongoTemplate.find(query, Post.class);
            List<FetchAllBlogsResponse> resultantData = new ArrayList<>();
            for(Post index : posts){
                FetchAllBlogsResponse data = new FetchAllBlogsResponse();
                data.setTitle(index.getTitle());
                data.setContent(index.getContent());
                data.setAuthorId(index.getAuthor_id());
                data.setCreatedAt(index.getCreatedAt());

                if(index.getComments()!=null) {
                    data.setCommentsList(index.getComments());
                }
                resultantData.add(data);
            }
            return resultantData;


        }catch (Exception err){
            log.error("Exception in fetching data :" ,err);
        }
        return new ArrayList<>();
    }

    public ResponseEntity<?> editComment(String commentId, String alternateComment,String userId,String blogID){
        try {
           ObjectId objectId =new ObjectId(blogID);
           Query query = new Query(Criteria.where("_id").is(objectId));
           boolean idExists = mongoTemplate.exists(query, Post.class);

           byte[] decodedByte = Base64.getDecoder().decode(userId);

           if(!idExists){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Given blogId not found");
           }

           Query commentQuery = new Query(Criteria.where("_id").is(objectId)
                    .and("comments.id").is(commentId));
           Post commentedBlog = mongoTemplate.findOne(commentQuery, Post.class);
           if(commentedBlog == null || commentedBlog.getComments().stream().noneMatch(k -> k.getId().equals(commentId))){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Given commentId not found");
           }

           boolean isAuthorized = commentedBlog.getComments().stream().
                   anyMatch(k -> k.getId().equals(commentId) && k.getCommentAuthorId().equals(new String(decodedByte)));

           if(!isAuthorized){
               return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User do not have access to edit the comment");
           }

            Query udpateQuery = new Query(Criteria.where("_id").is(objectId)
                    .and("comments.id").is(commentId));
            Update update = new Update().set("comments.$.comment", alternateComment);
            mongoTemplate.updateFirst(udpateQuery, update, Post.class);
            return ResponseEntity.status(HttpStatus.OK).body("the changed comment is : "+alternateComment);

        }catch (Exception err){
            log.error("Error in editing the comment  :", err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in updating the comment");
    }


}
