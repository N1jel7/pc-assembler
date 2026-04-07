package by.bsu.n1jel.pc.assembler.exception.common;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final Long id;
    private final Integer status;

    public ResourceNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
        this.status = 404;
    }
}
