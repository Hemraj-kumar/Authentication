package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.RequestDto.CreateBlogDto;
import dev.hemraj.jwtauthentication.Service.BlogService.PostBlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/blog")
@RestController
@Slf4j
public class BlogController {
    private final PostBlogService postBlogService;
    public BlogController(PostBlogService postBlogService){
        this.postBlogService = postBlogService;
    }
    @PostMapping("/postblog")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createBlogController(@RequestBody CreateBlogDto data){
        try{
            return postBlogService.createBlog(data);
        }catch (Exception err){
            log.error("Error in creating the blog");
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Check the correct format of data");
    }
}
