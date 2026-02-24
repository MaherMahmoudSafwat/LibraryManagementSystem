package com.LibraryManagementSystem.AiSystems.Interfaces;

import com.LibraryManagementSystem.AiSystems.Dtos.BookRecordDto;
import com.LibraryManagementSystem.AiSystems.Models.Books;
import com.LibraryManagementSystem.AiSystems.Models.UpdatedData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IBook
{
    public Books AddBook(BookRecordDto bookRecordDto, MultipartFile Image) throws IOException;
    public Page<BookRecordDto> FindByTitle(String Title, Pageable pageable);
    public Page<BookRecordDto> FindByAuthor(String Author,Pageable pageable);
    public BookRecordDto FindByIsbn(String ISBN);
    public Page<BookRecordDto> FindByPublisher(String Publisher,Pageable pageable);
    public BookRecordDto FindByTitleAndAuthor(String Title,String Author);
    public UpdatedData<Books> UpdateBooks(String Isbn, BookRecordDto bookRecordDto, MultipartFile image) throws IOException;
    public Page<BookRecordDto> FindByDescription(String Description,Pageable pageable);
    public boolean DeleteSpecificBook(String ISBN) throws IOException;
    public Page<BookRecordDto> ShowAllBooks(Pageable pageable);
}
