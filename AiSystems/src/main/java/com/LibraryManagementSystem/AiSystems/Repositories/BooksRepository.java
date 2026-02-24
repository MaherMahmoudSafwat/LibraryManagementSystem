package com.LibraryManagementSystem.AiSystems.Repositories;

import com.LibraryManagementSystem.AiSystems.Models.Books;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Books,Integer>
{
    public boolean existsByIsbn(String Isbn);
    @EntityGraph(attributePaths = "bookImages")
    public Page<Books> findByTitle(String Title, Pageable pageable);
    @EntityGraph(attributePaths = "bookImages")
    public Page<Books> findByAuthor(String Author, Pageable pageable);
    @EntityGraph(attributePaths = "bookImages")
    public Optional <Books> findByIsbn(String Isbn);
    @EntityGraph(attributePaths = "bookImages")
    public Page<Books> findByPublisher(String Publisher, Pageable pageable);
    @EntityGraph(attributePaths = "bookImages")
    public Optional<Books> findByTitleAndAuthor(String Title, String Author);
    @Query
            (
                    value = "SELECT B FROM Books B WHERE " +
                            "B.description LIKE %:Description%"
            )
    @EntityGraph(attributePaths = "bookImages")
    public Page<Books> findByDescription(@Param("Description") String Description,Pageable pageable);
    @Transactional
    @Modifying
    @Query
            (
                    value = "DELETE FROM books WHERE isbn =:Isbn",
                    nativeQuery = true
            )
    public Integer DeleteByBookIsbn(@Param("Isbn") String Isbn);
    @Modifying
    @Transactional
    @Query
            (value = """
    UPDATE books
    SET isbn = COALESCE(NULLIF(TRIM(:Isbn), ''), isbn),
        title = COALESCE(NULLIF(TRIM(:Title), ''), title),
        author = COALESCE(NULLIF(TRIM(:Author), ''), author),
        publication_year = COALESCE(:PublicationYear, publication_year),
        publisher = COALESCE(NULLIF(TRIM(:Publisher), ''), publisher),
        description = COALESCE(NULLIF(TRIM(:Description), ''), description)
    WHERE isbn = :OldBookIsbn
    """, nativeQuery = true)
    public Integer UpdateBooksByIsbn
            (
                    @Param("OldBookIsbn") String OldBookIsbn,
                    @Param("Isbn") String Isbn,
                    @Param("Title") String Title,
                    @Param("Author") String Author,
                    @Param("PublicationYear") Integer PublicationYear,
                    @Param("Publisher") String Publisher,
                    @Param("Description") String Description
            );
    public boolean existsByTitleAndAuthor(String Title,String Author);
}
