package com.LibraryManagementSystem.AiSystems.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
        (
                indexes =
                        {
                                @Index(name = "Idx_Title", columnList = "title"),
                                @Index(name = "Idx_Author",columnList = "author"),
                                @Index(name = "Idx_TitleAndAuthor",columnList="title,author"),
                                @Index(name = "Idx_Publisher",columnList="publisher")
                        }
        )
@Schema(
        name = "Books",
        description = "Represents a book entity in the library management system with complete book information",
        example = "{\"id\": 1, \"title\": \"The Great Gatsby\", \"author\": \"F. Scott Fitzgerald\", \"isbn\": \"978-0-7432-7356-5\", \"publisher\": \"Scribner\", \"publicationYear\": 1925}"
)
public class Books
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BookId")
    @SequenceGenerator(name = "BookId",sequenceName = "BookId",allocationSize = 50)
    @Schema(
            description = "Unique identifier for the book",
            example = "1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    Integer id;

    @Column(nullable = false, length = 500)
    @Schema(
            description = "Title of the book - must be unique when combined with author",
            example = "The Great Gatsby",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String title;

    @Column(nullable = false, length = 255)
    @Schema(
            description = "Name of the book's author",
            example = "F. Scott Fitzgerald",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String author;

    @Column(unique = true, nullable = false, length = 20)
    @Schema(
            description = "International Standard Book Number - must be unique and in valid ISBN-10 or ISBN-13 format",
            example = "978-0-7432-7356-5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String isbn;

    @Column(length = 200)
    @Schema(
            description = "Publisher of the book",
            example = "Scribner",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    String publisher;

    @Schema(
            description = "Year the book was published - must be between 1440 and current year",
            example = "1925",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    Integer publicationYear;

    @Column(columnDefinition = "TEXT")
    @Schema(
            description = "Detailed description or summary of the book's content",
            example = "A classic American novel set in the Jazz Age of the 1920s",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    String description;

    @OneToOne
            (
                    cascade = CascadeType.ALL,
                    orphanRemoval = true
            )
    @JoinColumn
            (
                    name = "Book_Image_Id"
            )
    @JsonManagedReference
    @Schema(
            description = "Associated book cover image",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private BookImages bookImages;

    public Books(String description, Integer publicationYear, String publisher, String isbn, String author, String title) {
        this.description = description;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
        this.isbn = isbn;
        this.author = author;
        this.title = title;
    }
}
