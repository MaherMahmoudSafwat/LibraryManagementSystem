package com.LibraryManagementSystem.AiSystems.Services;

import com.LibraryManagementSystem.AiSystems.Dtos.BookRecordDto;
import com.LibraryManagementSystem.AiSystems.Exceptions.BookAlreadyExistsException;
import com.LibraryManagementSystem.AiSystems.Exceptions.BookNotFoundException;
import com.LibraryManagementSystem.AiSystems.Interfaces.IBook;
import com.LibraryManagementSystem.AiSystems.Mappers.BookMappers;
import com.LibraryManagementSystem.AiSystems.Models.BookImages;
import com.LibraryManagementSystem.AiSystems.Models.Books;
import com.LibraryManagementSystem.AiSystems.Models.UpdatedData;
import com.LibraryManagementSystem.AiSystems.Repositories.BookImageRepository;
import com.LibraryManagementSystem.AiSystems.Repositories.BooksRepository;
import com.LibraryManagementSystem.AiSystems.Utility.Images;
import com.LibraryManagementSystem.AiSystems.Utility.Utility;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class BookService implements IBook
{
    private final BooksRepository booksRepository;
    private final BookMappers bookMappers;
    private final BookImageRepository bookImageRepository;
    @Value("${app.web.url}")
    private String WEB_URL;
    @Value("${app.upload.path")
    private String FILE_SYSTEM;
    public BookService(BooksRepository booksRepository,BookMappers bookMappers,BookImageRepository bookImageRepository)
    {
        this.booksRepository = booksRepository;
        this.bookMappers = bookMappers;
        this.bookImageRepository = bookImageRepository;
    }

    @Override
    @CachePut(value = "Books_Cache",key = "#result.isbn")
    @Transactional
    public Books AddBook(BookRecordDto Book, MultipartFile image) throws IOException {
        if(booksRepository.existsByIsbn(Book.isbn()))
        {
            throw new BookAlreadyExistsException("The book with isbn " + Book.isbn() + " already exists.");
        }
        if(booksRepository.existsByTitleAndAuthor(Book.title(),Book.author()))
        {
            throw new BookAlreadyExistsException("The book with the title " + Book.title() + " and " + Book.isbn() + " already exists");
        }
        Books books = bookMappers.ToEntityRequest(Book);
        if(image != null)
        {
            Images images = new Images();
            String SavedFileName = images.SaveAndUploadFilesToHardDisk(FILE_SYSTEM, image);
            BookImages bookImages = new BookImages();
            bookImages.setName(image.getOriginalFilename());
            bookImages.setType(image.getContentType());
            bookImages.setUrl(WEB_URL + SavedFileName);
            bookImages = bookImageRepository.save(bookImages);
            books.setBookImages(bookImages);
        }
        return booksRepository.save(books);
    }

    @Override
    @Cacheable(value = "Books_Cache",key = "#Title + '-' + #pageable.pageNumber + '-' + #pageable.sort")
    public Page<BookRecordDto> FindByTitle(String Title, Pageable pageable)
    {
        return booksRepository.findByTitle(Title,pageable)
                .map(bookMappers::ToDtoResponse);
    }

    @Override
    @Cacheable(value = "Books_Cache",key = "#Author + '-' + #pageable.pageNumber + '-' + #pageable.sort")
    public Page<BookRecordDto> FindByAuthor(String Author, Pageable pageable)
    {
        return booksRepository.findByAuthor(Author,pageable)
                .map(bookMappers::ToDtoResponse);
    }

    @Override
    @Cacheable(value = "Books_Cache",key = "#Isbn")
    public BookRecordDto FindByIsbn(String Isbn)
    {
        return bookMappers.ToDtoResponse(booksRepository.findByIsbn(Isbn)
                .orElseThrow(() -> new BookNotFoundException("The book with the isbn " + Isbn + " doesn't exist.")));
    }

    @Override
    @Cacheable(value = "Books_Cache" , key = "#Publisher + '-' + #pageable.pageNumber + '-' + #pageable.sort")
    public Page<BookRecordDto> FindByPublisher(String Publisher, Pageable pageable)
    {
        return booksRepository.findByPublisher(Publisher,pageable)
                .map(bookMappers::ToDtoResponse);
    }

    @Override
    @Cacheable(value = "Books_Cache",key = "#Title + '-' + #Author")
    public BookRecordDto FindByTitleAndAuthor(String Title, String Author)
    {
        return bookMappers.ToDtoResponse(booksRepository.findByTitleAndAuthor(Title,Author)
                .orElseThrow(() -> new BookNotFoundException("The book with Title " + Title + " and Author " + Author + " doesn't exist")));
    }

    @Override
    @CacheEvict(value = "Books_Cache",key = "#OldBookIsbn")
    @Transactional
    public UpdatedData<Books> UpdateBooks(String OldBookIsbn, BookRecordDto bookRecordDto, MultipartFile Image) throws IOException
    {
        if(!booksRepository.existsByIsbn(OldBookIsbn))
        {
            throw new BookNotFoundException("This book doesn't exist, please try again.");
        }
        Integer B = 0;
        Integer i = 0;
        if(Image != null )
        {
            Images images = new Images();
            String SavedFileName = images.SaveAndUploadFilesToHardDisk(FILE_SYSTEM, Image);
            B = bookImageRepository.UpdateBookImageByBookIsbn
                    (
                            OldBookIsbn,
                            Image.getOriginalFilename(),
                            Image.getContentType(),
                            WEB_URL + SavedFileName
                    );
        }

        Books books = null;
        if(bookRecordDto != null)
        {
            books = bookMappers.ToEntityRequest(bookRecordDto);
            i = booksRepository.UpdateBooksByIsbn
                    (
                            OldBookIsbn,
                            books.getIsbn(),
                            books.getTitle(),
                            books.getAuthor(),
                            books.getPublicationYear(),
                            books.getPublisher(),
                            books.getDescription()
                    );
        }
        return new UpdatedData<>(books,i != 0 || B != 0);
    }

    @Override
    @Cacheable(value = "Books_Cache",key = "#Description + '-' + #pageable.pageNumber + '-' + #pageable.sort")
    public Page<BookRecordDto> FindByDescription(String Description,Pageable pageable)
    {
        return booksRepository.findByDescription(Description,pageable)
                .map(bookMappers::ToDtoResponse);
    }

    @Override
    @CacheEvict(value = "Books_Cache",key = "#Isbn")
    @Transactional
    public boolean DeleteSpecificBook(String Isbn) throws IOException {
        Books books = booksRepository.findByIsbn(Isbn)
                .orElseThrow(() -> new BookNotFoundException("The book with the isbn " + Isbn + " doesn't exist."));
        if(books != null && books.getBookImages() != null)
        {
            String ImageUrl = books.getBookImages().getUrl();
            String FileName = Utility.extractFileName(ImageUrl);
            String ImagePath = FILE_SYSTEM + FileName;
            Files.deleteIfExists(Paths.get(ImagePath));
        }
        Integer i = booksRepository.DeleteByBookIsbn(Isbn);
        return i != 0;
    }

    @Override
    @Cacheable(value = "Books_Cache",key = "#pageable.pageNumber + '-' + #pageable.sort")
    @EntityGraph(attributePaths = "bookImages")
    public Page<BookRecordDto> ShowAllBooks(Pageable pageable)
    {
        return booksRepository.findAll(pageable)
                .map(bookMappers::ToDtoResponse);
    }
}
