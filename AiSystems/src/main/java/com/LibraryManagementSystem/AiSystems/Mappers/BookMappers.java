package com.LibraryManagementSystem.AiSystems.Mappers;

import com.LibraryManagementSystem.AiSystems.Dtos.BookRecordDto;
import com.LibraryManagementSystem.AiSystems.Interfaces.Dtos;
import com.LibraryManagementSystem.AiSystems.Models.Books;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;

@Service
@Schema(
        name = "BookMapper",
        description = "Mapper service for converting between Book entities and BookRecordDTO objects"
)
public class BookMappers implements Dtos<BookRecordDto,Books>
{
    @Override
    public Books ToEntityRequest(BookRecordDto bookRecordDto)
    {
        return new Books
                (
                        bookRecordDto.description(),
                        bookRecordDto.publicationYear(),
                        bookRecordDto.publisher(),
                        bookRecordDto.isbn(),
                        bookRecordDto.author(),
                        bookRecordDto.title()
                );
    }

    @Override
    public BookRecordDto ToDtoResponse(Books books) {
        return new BookRecordDto
                (
                        books.getTitle(),
                        books.getIsbn(),
                        books.getAuthor(),
                        books.getPublisher(),
                        books.getPublicationYear(),
                        books.getDescription(),
                        books.getBookImages()
                );
    }
}

