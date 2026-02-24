package com.LibraryManagementSystem.AiSystems.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "BookImages",
        description = "Represents a book cover image stored in the library management system",
        example = "{\"id\": 1, \"name\": \"gatsby_cover.jpg\", \"type\": \"image/jpeg\", \"url\": \"/images/gatsby_cover.jpg\"}"
)
public class BookImages
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "ImageId")
    @SequenceGenerator(name = "ImageId",sequenceName = "ImageId",allocationSize = 50)
    @Schema(
            description = "Unique identifier for the book image",
            example = "1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    Integer id;

    @Schema(
            description = "Original filename of the uploaded image",
            example = "gatsby_cover.jpg",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String name;

    @Schema(
            description = "MIME type of the image file",
            example = "image/jpeg",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String type;

    @Schema(
            description = "URL path to access the uploaded image",
            example = "/images/gatsby_cover.jpg",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String url;

    @OneToOne(mappedBy = "bookImages")
    @JsonBackReference
    private Books books;

    public BookImages(String url, String type, String name)
    {
        this.url = url;
        this.type = type;
        this.name = name;
    }
}
