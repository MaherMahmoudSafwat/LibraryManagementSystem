package com.LibraryManagementSystem.AiSystems.Controllers;

import com.LibraryManagementSystem.AiSystems.Dtos.BookRecordDto;
import com.LibraryManagementSystem.AiSystems.Interfaces.CreateValidationGroups;
import com.LibraryManagementSystem.AiSystems.Models.Books;
import com.LibraryManagementSystem.AiSystems.Services.BookService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/Books")
@OpenAPIDefinition
        (
                info = @Info
                        (
                                title = "Books Management Service",
                                version = "1.0.0",
                                description = "This books service is used to perform crud operations on books in the library management system.",
                                contact = @Contact
                                        (
                                                name = "Library Management",
                                                email = "library@example.com",
                                                url = "https://localhost:8080/Books"
                                        ),
                                license = @License
                                        (
                                                name = "Apache 2.0",
                                                url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                                        )
                        )
        )
@Server(url = "/Library-Service")
@Tag(name="Books",description="This is endpoints for handling books management operations.")
public class BooksController
{

    private final BookService bookService;
    @Value("${app.pagination.default-size}")
    private int DefaultSize;
    @Value("${app.pagination.default-page}")
    private int DefaultPage;
    @Value("${app.pagination.max-size}")
    private int MaxPage;
    public BooksController(BookService bookService)
    {
        this.bookService = bookService;
    }

    @PostMapping("/AddBook")
    @Operation
            (
                    summary = "Add new book",
                    description = "This api url endpoint is used to add a new book to the library with optional book image"
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse(responseCode = "201", description = "The book data has been added successfully."),
                                    @ApiResponse(responseCode = "500", description = "Internal server errors from our side."),
                                    @ApiResponse(responseCode = "400", description = "Bad request as the book data entered may not be completed or violate the validation constraints required."),
                                    @ApiResponse(responseCode = "409", description = "Book with this ISBN or title and author combination already exists.")
                            }
            )
    public ResponseEntity<Object> AddNewBook
            (
                    @Validated({Default.class, CreateValidationGroups.class})
                    @Parameter
                            (
                                    description = "The book data to be entered like title, ISBN, author, publisher, publication year, and description.",
                                    example = "The Great Gatsby,978-0-7432-7356-5,F. Scott Fitzgerald,Scribner,1925,A classic American novel",
                                    required = true
                            )
                    @RequestPart BookRecordDto bookRecordDto,
                    @Parameter
                            (
                                    description = "Optional book cover image file",
                                    required = false
                            )
                    @RequestPart(required = false) MultipartFile Image
            ) throws IOException {
        bookService.AddBook(bookRecordDto, Image);
        return new ResponseEntity<>("The book has been added successfully.", HttpStatus.CREATED);
    }

    @GetMapping("/ShowBooks")
    @Operation
            (
                    summary = "Show all books data",
                    description = "This api url endpoint is used to show all books with pagination and sorting support."
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse(responseCode = "200", description= "The books data have all of them have been showed successfully."),
                                    @ApiResponse(responseCode = "500", description = "Internal server errors from our side."),
                                    @ApiResponse(responseCode = "400", description = "Bad request as the pagination parameters entered may be violating the constraints like page number, page size, sort by and sort type.")
                            }
            )
    public ResponseEntity<Page<BookRecordDto>> ShowAllBooks
            (
                    @Parameter
                            (
                                    description = "The page number to be shown to the users.",
                                    example = "1",
                                    required = false
                            )
                    @RequestParam(required = false) Integer PageNumber,
                    @Parameter
                            (
                                    description = "The page size is the number of data to be shown only in one page.",
                                    example = "10",
                                    required = false
                            )
                    @RequestParam(required = false) Integer PageSize,
                    @Parameter
                            (
                                    description = "The way to sort the data to show it to the users.",
                                    example = "title",
                                    schema = @Schema(allowableValues = {"title","author","isbn","publisher","publicationYear"})
                            )
                    @RequestParam(required = false, defaultValue = "title") String SortBy,
                    @Parameter
                            (
                                    description = "The sort type is the way you want to sort the shown data to the users.",
                                    example = "ASC",
                                    schema = @Schema(allowableValues = {"ASC","DESC"})
                            )
                    @RequestParam(required = false, defaultValue = "ASC") String SortType
            )
    {
        if(PageNumber == null)
        {
            PageNumber = DefaultPage;
        }
        if(PageSize == null)
        {
            PageSize = DefaultSize;
        }
        if(PageSize < 0 || PageSize > MaxPage)
        {
            PageSize = MaxPage;
        }
        Sort sort = null;
        if(SortType.equalsIgnoreCase("ASC"))
        {
            sort = Sort.by(SortBy).ascending();
        }
        else if(SortType.equalsIgnoreCase("DESC"))
        {
            sort = Sort.by(SortBy).descending();
        }
        else
        {
            sort = Sort.by("title").ascending();
        }
        Pageable pageable = PageRequest.of(PageNumber-1, PageSize, sort);
        Page<BookRecordDto> books = null;
        books = bookService.ShowAllBooks(pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/SearchByTitle")
    @Operation
            (
                    summary = "Search books by title",
                    description = "This api url endpoint is used to search for books by their title with pagination support."
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse(responseCode = "200", description = "Books matching the title have been found and showed successfully."),
                                    @ApiResponse(responseCode = "500", description = "Internal server errors from our side."),
                                    @ApiResponse(responseCode = "400", description = "Bad request as the search parameters may be violating the constraints.")
                            }
            )
    public ResponseEntity<Page<BookRecordDto>> SearchByTitle
            (
                    @Parameter
                            (
                                    description = "The book title to search for.",
                                    example = "The Great Gatsby",
                                    required = true
                            )
                    @RequestParam String Title,
                    @Parameter
                            (
                                    description = "The page number for pagination.",
                                    example = "1",
                                    required = false
                            )
                    @RequestParam(required = false) Integer PageNumber,
                    @Parameter
                            (
                                    description = "The page size for pagination.",
                                    example = "10",
                                    required = false
                            )
                    @RequestParam(required = false) Integer PageSize,
                    @Parameter
                            (
                                    description = "Sort type ASC or DESC.",
                                    example = "ASC",
                                    schema = @Schema(allowableValues = {"ASC","DESC"})
                            )
                    @RequestParam(required = false, defaultValue = "ASC") String SortType
            )
    {
        if(PageNumber == null)
        {
            PageNumber = DefaultPage;
        }
        if(PageSize == null)
        {
            PageSize = DefaultSize;
        }
        if(PageSize < 0 || PageSize > MaxPage)
        {
            PageSize = MaxPage;
        }
        Sort sort = SortType.equalsIgnoreCase("DESC") ? Sort.by("title").descending() : Sort.by("title").ascending();
        Pageable pageable = PageRequest.of(PageNumber-1, PageSize, sort);
        Page<BookRecordDto> books = bookService.FindByTitle(Title, pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/SearchByAuthor")
    @Operation
            (
                    summary = "Search books by author",
                    description = "This api url endpoint is used to search for books by their author with pagination support."
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse(responseCode = "200", description = "Books matching the author have been found and showed successfully."),
                                    @ApiResponse(responseCode = "500", description = "Internal server errors from our side."),
                                    @ApiResponse(responseCode = "400", description = "Bad request as the search parameters may be violating the constraints.")
                            }
            )
    public ResponseEntity<Page<BookRecordDto>> SearchByAuthor
            (
                    @Parameter
                            (
                                    description = "The book author to search for.",
                                    example = "F. Scott Fitzgerald",
                                    required = true
                            )
                    @RequestParam String Author,
                    @Parameter
                            (
                                    description = "The page number for pagination.",
                                    example = "1",
                                    required = false
                            )
                    @RequestParam(required = false) Integer PageNumber,
                    @Parameter
                            (
                                    description = "The page size for pagination.",
                                    example = "10",
                                    required = false
                            )
                    @RequestParam(required = false) Integer PageSize,
                    @Parameter
                            (
                                    description = "Sort type ASC or DESC.",
                                    example = "ASC",
                                    schema = @Schema(allowableValues = {"ASC","DESC"})
                            )
                    @RequestParam(required = false, defaultValue = "ASC") String SortType
            )
    {
        if(PageNumber == null)
        {
            PageNumber = DefaultPage;
        }
        if(PageSize == null)
        {
            PageSize = DefaultSize;
        }
        if(PageSize < 0 || PageSize > MaxPage)
        {
            PageSize = MaxPage;
        }
        Sort sort = SortType.equalsIgnoreCase("DESC") ? Sort.by("author").descending() : Sort.by("author").ascending();
        Pageable pageable = PageRequest.of(PageNumber-1, PageSize, sort);
        Page<BookRecordDto> books = bookService.FindByAuthor(Author, pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/SearchByIsbn/{isbn}")
    @Operation
            (
                    summary = "Search book by ISBN",
                    description = "This api url endpoint is used to search for a specific book by its ISBN."
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse(responseCode = "200", description = "The book with matching ISBN has been found."),
                                    @ApiResponse(responseCode = "500", description = "Internal server errors from our side."),
                                    @ApiResponse(responseCode = "404", description = "The book with the given ISBN was not found.")
                            }
            )
    public ResponseEntity<Object> SearchByIsbn
            (
                    @PathVariable
                    @Parameter
                            (
                                    description = "The ISBN of the book to search for.",
                                    example = "978-0-7432-7356-5",
                                    required = true
                            )
                    String isbn
            )
    {
        BookRecordDto book = bookService.FindByIsbn(isbn);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @GetMapping("/SearchByPublisher")
    @Operation
            (
                    summary = "Search books by publisher",
                    description = "This api url endpoint is used to search for books by their publisher with pagination support."
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse(responseCode = "200", description = "Books matching the publisher have been found and showed successfully."),
                                    @ApiResponse(responseCode = "500", description = "Internal server errors from our side."),
                                    @ApiResponse(responseCode = "400", description = "Bad request as the search parameters may be violating the constraints.")
                            }
            )
    public ResponseEntity<Page<BookRecordDto>> SearchByPublisher
            (
                    @Parameter
                            (
                                    description = "The book publisher to search for.",
                                    example = "Scribner",
                                    required = true
                            )
                    @RequestParam String Publisher,
                    @Parameter
                            (
                                    description = "The page number for pagination.",
                                    example = "1",
                                    required = false
                            )
                    @RequestParam(required = false) Integer PageNumber,
                    @Parameter
                            (
                                    description = "The page size for pagination.",
                                    example = "10",
                                    required = false
                            )
                    @RequestParam(required = false) Integer PageSize,
                    @Parameter
                            (
                                    description = "Sort type ASC or DESC.",
                                    example = "ASC",
                                    schema = @Schema(allowableValues = {"ASC","DESC"})
                            )
                    @RequestParam(required = false, defaultValue = "ASC") String SortType
            )
    {
        if(PageNumber == null)
        {
            PageNumber = DefaultPage;
        }
        if(PageSize == null)
        {
            PageSize = DefaultSize;
        }
        if(PageSize < 0 || PageSize > MaxPage)
        {
            PageSize = MaxPage;
        }
        Sort sort = SortType.equalsIgnoreCase("DESC") ? Sort.by("publisher").descending() : Sort.by("publisher").ascending();
        Pageable pageable = PageRequest.of(PageNumber-1, PageSize, sort);
        Page<BookRecordDto> books = bookService.FindByPublisher(Publisher, pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @DeleteMapping("/DeleteBook/{Isbn}")
    @Operation
            (
                    summary = "Delete a specific book by ISBN.",
                    description = "This url api endpoint is used to delete the book data for a specific ISBN."
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse(responseCode = "204", description = "The book data has been deleted successfully."),
                                    @ApiResponse(responseCode = "500", description = "Internal server errors from our side."),
                                    @ApiResponse(responseCode = "404",description = "The ISBN entered to delete the specific book is not found.")
                            }
            )
    public ResponseEntity<String> DeleteBook
            (
                    @PathVariable
                    @Parameter
                            (
                                    description = "The ISBN of the book to delete.",
                                    example = "978-0-7432-7356-5",
                                    required = true
                            )
                    String Isbn
            )
    {
        return new ResponseEntity<>("Book data has been deleted successfully", HttpStatus.NO_CONTENT);
    }

    @PutMapping("/UpdateBook/{isbn}")
    @Operation
            (
                    summary = "Update a specific book by ISBN",
                    description = "This url api endpoint is used to update book data for a specific ISBN and the new data to be updated."
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse(responseCode = "200", description = "The book data has been updated successfully."),
                                    @ApiResponse(responseCode = "500", description = "Internal server errors from our side."),
                                    @ApiResponse(responseCode = "404", description = "The book data to be updated is not found.")
                            }
            )
    public ResponseEntity<String> UpdateBook
            (
                    @Parameter
                            (
                                    description = "The ISBN of the book to update.",
                                    example = "978-0-7432-7356-5",
                                    required = true
                            )
                    @PathVariable String isbn,
                    @Validated(CreateValidationGroups.class)
                    @Parameter
                            (
                                    description = "The data you entered to update the book by it.",
                                    example = "The Great Gatsby (Updated Edition),F. Scott Fitzgerald,Scribner,1925,A classic American novel",
                                    required = false
                            )
                    @RequestPart(required=false) BookRecordDto bookRecordDto,
                    @Parameter
                            (
                                    description = "Optional updated book cover image file",
                                    required = false
                            )
                    @RequestPart(required = false) MultipartFile Image
            ) throws IOException {
        var result = bookService.UpdateBooks(isbn, bookRecordDto, Image).getUpdatedDataStatus();
        if(result)
        {
            return new ResponseEntity<>("Book data has been updated successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("ISBN doesn't exist, please try again", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/SearchByTitleAndAuthor")
    @Operation
            (
                    summary = "Search book by title and author",
                    description = "This api url endpoint is used to search for a specific book by its title and author."
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse(responseCode = "200", description = "The book with matching title and author has been found."),
                                    @ApiResponse(responseCode = "500", description = "Internal server errors from our side."),
                                    @ApiResponse(responseCode = "404", description = "The book with the given title and author combination was not found.")
                            }
            )
    public ResponseEntity<Object> SearchByTitleAndAuthor
            (
                    @Parameter
                            (
                                    description = "The title of the book to search for.",
                                    example = "The Great Gatsby",
                                    required = true
                            )
                    @RequestParam String Title,
                    @Parameter
                            (
                                    description = "The author of the book to search for.",
                                    example = "F. Scott Fitzgerald",
                                    required = true
                            )
                    @RequestParam String Author
            )
    {
        BookRecordDto book = bookService.FindByTitleAndAuthor(Title, Author);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @GetMapping("/SearchByDescription")
    @Operation
            (
                    summary = "Search books by description",
                    description = "This api url endpoint is used to search for books by their description content with pagination support."
            )
    @ApiResponses
            (
                    value =
                            {
                                    @ApiResponse(responseCode = "200", description = "Books matching the description have been found and showed successfully."),
                                    @ApiResponse(responseCode = "500", description = "Internal server errors from our side."),
                                    @ApiResponse(responseCode = "400", description = "Bad request as the search parameters may be violating the constraints.")
                            }
            )
    public ResponseEntity<Page<BookRecordDto>> SearchByDescription
            (
                    @Parameter
                            (
                                    description = "The description keyword to search for in book descriptions.",
                                    example = "classic American novel",
                                    required = true
                            )
                    @RequestParam String Description,
                    @Parameter
                            (
                                    description = "The page number for pagination.",
                                    example = "1",
                                    required = false
                            )
                    @RequestParam(required = false) Integer PageNumber,
                    @Parameter
                            (
                                    description = "The page size for pagination.",
                                    example = "10",
                                    required = false
                            )
                    @RequestParam(required = false) Integer PageSize,
                    @Parameter
                            (
                                    description = "Sort type ASC or DESC.",
                                    example = "ASC",
                                    schema = @Schema(allowableValues = {"ASC","DESC"})
                            )
                    @RequestParam(required = false, defaultValue = "ASC") String SortType
            )
    {
        if(PageNumber == null)
        {
            PageNumber = DefaultPage;
        }
        if(PageSize == null)
        {
            PageSize = DefaultSize;
        }
        if(PageSize < 0 || PageSize > MaxPage)
        {
            PageSize = MaxPage;
        }
        Sort sort = SortType.equalsIgnoreCase("DESC") ? Sort.by("description").descending() : Sort.by("description").ascending();
        Pageable pageable = PageRequest.of(PageNumber-1, PageSize, sort);
        Page<BookRecordDto> books = bookService.FindByDescription(Description, pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}
