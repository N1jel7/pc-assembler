package by.bsu.n1jel.pc.assembler.exception.handler;

import by.bsu.n1jel.pc.assembler.dto.response.ErrorInfoResponseDto;
import by.bsu.n1jel.pc.assembler.exception.ImageValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.net.URI;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UploadExceptionHandler {

    public static final String UPLOAD_ERROR_ATTRIBUTE = "uploadError";

    private final DataSize maxFileSize;
    private final DataSize maxRequestSize;

    public UploadExceptionHandler(@Value("${spring.servlet.multipart.max-file-size}") DataSize maxFileSize,
                                  @Value("${spring.servlet.multipart.max-request-size}") DataSize maxRequestSize) {
        this.maxFileSize = maxFileSize;
        this.maxRequestSize = maxRequestSize;
    }

    @ModelAttribute("uploadLimits")
    public UploadLimits uploadLimits() {
        return new UploadLimits(maxFileSize.toBytes(), maxRequestSize.toBytes(),
                maxFileSize.toMegabytes(), maxRequestSize.toMegabytes());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorInfoResponseDto> handleMaxUploadSize(HttpServletRequest request, HttpServletResponse response) {
        String message = "Файлы слишком большие: не больше %d МБ на одно фото и %d МБ на все фото вместе."
                .formatted(maxFileSize.toMegabytes(), maxRequestSize.toMegabytes());
        return respond(HttpStatus.CONTENT_TOO_LARGE, message, request, response);
    }

    @ExceptionHandler(ImageValidationException.class)
    public ResponseEntity<ErrorInfoResponseDto> handleImageValidation(ImageValidationException ex,
                                                                      HttpServletRequest request,
                                                                      HttpServletResponse response) {
        return respond(HttpStatus.BAD_REQUEST, ex.getMessage(), request, response);
    }

    private ResponseEntity<ErrorInfoResponseDto> respond(HttpStatus status, String message,
                                                         HttpServletRequest request, HttpServletResponse response) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.status(status).body(new ErrorInfoResponseDto(status.value(), message));
        }

        String target = sameOriginPath(request.getHeader(HttpHeaders.REFERER));
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        flashMap.put(UPLOAD_ERROR_ATTRIBUTE, message);
        RequestContextUtils.saveOutputFlashMap(target, request, response);

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create(target))
                .build();
    }


    private static String sameOriginPath(String referer) {
        if (referer == null) {
            return "/";
        }
        try {
            URI uri = URI.create(referer);
            String path = uri.getRawPath();
            if (path == null || !path.startsWith("/") || path.startsWith("//")) {
                return "/";
            }
            return uri.getRawQuery() == null ? path : path + "?" + uri.getRawQuery();
        } catch (IllegalArgumentException e) {
            return "/";
        }
    }

    public record UploadLimits(long maxFileBytes, long maxRequestBytes, long maxFileMb, long maxRequestMb) {
    }
}
