package com.LibraryManagementSystem.AiSystems.Exceptions;

public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(String message) {
    super(message);
  }
}
