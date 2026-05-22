package by.bsu.n1jel.pc.assembler.controller.rest;

import by.bsu.n1jel.pc.assembler.dto.request.image.DeleteImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.image.LoadImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.image.UploadImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/images")
@RequiredArgsConstructor
public class RestImageController {

    private final ImageService imageService;

    @GetMapping("/{id}")
    public String get(@PathVariable Long id) {
        return imageService.load(new LoadImageRequestDto(id));
    }

    @DeleteMapping("/{id}")
    public ImageInfoResponseDto delete(@PathVariable Long id) {
        return imageService.delete(new DeleteImageRequestDto(id));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageInfoResponseDto upload(@ModelAttribute UploadImageRequestDto uploadImageRequestDto) {
        return imageService.upload(uploadImageRequestDto);
    }

}
