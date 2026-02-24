package com.LibraryManagementSystem.AiSystems.Dtos;

import com.LibraryManagementSystem.AiSystems.Interfaces.CreateValidationGroups;
import com.LibraryManagementSystem.AiSystems.Models.BookImages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;

import java.io.Serializable;

@Schema
        (
                name = "BookRecord",
                description = "This class is used to accept and return the book data in the library management system."
        )
public record BookRecordDto
        (
                @NotBlank(groups = Default.class, message = "Book title is required and cannot be empty")
                @Size(groups = {Default.class, CreateValidationGroups.class}, min = 1, max = 500, message = "Book title must be between 1 and 500 characters")
                @Schema
                        (
                                description = "The title of the book which must be unique when combined with author.",
                                example = "The Great Gatsby",
                                requiredMode = Schema.RequiredMode.REQUIRED
                        )
                String title,

                @NotBlank(groups = Default.class,message = "ISBN is required")
                @Pattern(groups = {Default.class,CreateValidationGroups.class},regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$",
                        message = "Invalid ISBN format. Please enter a valid ISBN-10 or ISBN-13")
                @Schema
                        (
                                description = "The International Standard Book Number (ISBN) which must be unique and valid. Supports ISBN-10 or ISBN-13 format.",
                                example = "978-0-7432-7356-5",
                                requiredMode = Schema.RequiredMode.REQUIRED
                        )
                String isbn,

                @NotBlank(groups = Default.class,message = "Author name is required and cannot be empty")
                @Size(groups = {Default.class,CreateValidationGroups.class}, min = 2, max = 255, message = "Author name must be between 2 and 255 characters")
                @Schema
                        (
                                description = "The name of the author who wrote the book.",
                                example = "F. Scott Fitzgerald",
                                requiredMode = Schema.RequiredMode.REQUIRED
                        )
                String author,

                @Size(max = 200, message = "Publisher name cannot exceed 200 characters")
                @Schema
                        (
                                description = "The name of the publisher who published the book.",
                                example = "Scribner",
                                requiredMode = Schema.RequiredMode.NOT_REQUIRED
                        )
                String publisher,

                @Min(value = 1440, message = "Publication year cannot be before 1440")
                @Max(value = 2026, message = "Publication year cannot be in the future")
                @Schema
                        (
                                description = "The year the book was published. Must be a valid 4-digit year between 1440 and current year.",
                                example = "1925",
                                requiredMode = Schema.RequiredMode.NOT_REQUIRED
                        )
                Integer publicationYear,

                @Size(max = 5000, message = "Description cannot exceed 5000 characters")
                @Schema
                        (
                                description = "A detailed description or summary of the book's content.",
                                example = "A classic American novel set in the Jazz Age",
                                requiredMode = Schema.RequiredMode.NOT_REQUIRED
                        )
                String description,

                @Schema(
                        description = "Book cover image details including filename, type and URL",
                        example = """
            {
            "name": "1771944252157-WhatsApp Image 2025-12-19 at 4.49.17 PM.jpeg",
            "type": "image/jpeg",
            "url": "/images/1771944252157-WhatsApp Image 2025-12-19 at 4.49.17 PM.jpeg"
            }
            """,
                        requiredMode = Schema.RequiredMode.NOT_REQUIRED
                )
                BookImages bookImages
        ) implements Serializable
{
}
