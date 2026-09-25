package org.gearbook.inventoryservice.common;

import org.gearbook.inventoryservice.inventory.exception.InventoryDuplicateSkuException;
import org.gearbook.inventoryservice.inventory.exception.InventoryInvalidQuantityException;
import org.gearbook.inventoryservice.inventory.exception.InventoryNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InventoryNotFoundException.class)
    public ErrorResponse handleInventoryNotFoundException(InventoryNotFoundException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InventoryInvalidQuantityException.class)
    public ErrorResponse handleInventoryInvalidQuantityException(InventoryInvalidQuantityException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse handleValidationErrorResponse(MethodArgumentNotValidException ex)
    {
        Map<String, String> errorMap = ex.getBindingResult().getFieldErrors().stream().collect(
                Collectors.toMap(FieldError::getField,
                        f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "Invalid value",
                        (first, _) -> first));
        return new ValidationErrorResponse(errorMap);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(InventoryDuplicateSkuException.class)
    public ErrorResponse handleInventoryDuplicateSkuException(InventoryDuplicateSkuException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolation(
            DataIntegrityViolationException ex)
    {
        return new ErrorResponse("Resource already exists or violates database constraints");
    }

    public record ErrorResponse(String message)
    {

    }

    public record ValidationErrorResponse(Map<String, String> errors)
    {

    }
}
