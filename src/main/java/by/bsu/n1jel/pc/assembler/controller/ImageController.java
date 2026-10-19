package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.response.ImageContentResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> image(@PathVariable Long id) {
        ImageContentResponseDto content = imageService.loadContent(id);
        MediaType mediaType = content.contentType() != null
                ? MediaType.parseMediaType(content.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .header("X-Content-Type-Options", "nosniff")
                .body(content.bytes());
    }
}
