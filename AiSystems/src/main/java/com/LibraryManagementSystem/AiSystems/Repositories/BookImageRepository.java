package com.LibraryManagementSystem.AiSystems.Repositories;

import com.LibraryManagementSystem.AiSystems.Models.BookImages;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookImageRepository extends JpaRepository<BookImages, Integer>
{
    @Transactional
    @Modifying
    @Query
            (
                    "UPDATE BookImages Bi " +
                    "SET Bi.name = :Name, " +
                    "Bi.type = :Type, " +
                    "Bi.url = :Url " +
                    "WHERE Bi.books.isbn = :Isbn"
            )
    public Integer UpdateBookImageByBookIsbn
            (
                    @Param("Isbn") String Isbn,
                    @Param("Name") String Name,
                    @Param("Type") String Type,
                    @Param("Url") String Url
            );
}
