package by.bsu.n1jel.pc.assembler.exception.handler;

import by.bsu.n1jel.pc.assembler.dto.response.ErrorInfoResponseDto;
import by.bsu.n1jel.pc.assembler.exception.common.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfoResponseDto> handleNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<ErrorInfoResponseDto>(new ErrorInfoResponseDto(
                ex.getStatus(),
                ex.getMessage()),
                HttpStatus.NOT_FOUND
                );
    }
}
