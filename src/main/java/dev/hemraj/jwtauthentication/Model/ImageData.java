package dev.hemraj.jwtauthentication.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "imageData")
@AllArgsConstructor
@NoArgsConstructor
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String type;
    @Lob
    @Column(name = "imagedata", length = 4096)
    private byte[] imageData;
}
