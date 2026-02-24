package com.LibraryManagementSystem.AiSystems.Exceptions;

import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> HandleValidationsErrorsExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (first, second) -> first + "; " + second
                ));

        return errors;
    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public String HandlesBooksNotFoundAlreadyExceptions(BookNotFoundException ex)
    {
        return "Message is :- " + ex.getMessage();
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public String HandleIllegalArgumentExceptions(IllegalArgumentException ex)
    {
        return "Message is :- " + ex.getMessage();
    }
    @ExceptionHandler(BookAlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public String HandlesBookAlreadyExistsExceptions(BookAlreadyExistsException ex)
    {
        return "Message is :- " + ex.getMessage();
    }

    @ExceptionHandler({InvalidContentTypeException.class, MissingServletRequestPartException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public String HandleInvalidContentTypeException()
    {
        return " Message is :- The book addition has been failed, you have to provide the book details.";
    }
}


