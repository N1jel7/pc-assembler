package by.bsu.n1jel.pc.assembler.controller.rest;

import by.bsu.n1jel.pc.assembler.dto.request.image.DeleteImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.image.LoadImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.image.UploadImageRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ImageInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.BuildService;
import by.bsu.n1jel.pc.assembler.service.api.ComponentService;
import by.bsu.n1jel.pc.assembler.service.api.ImageService;
import by.bsu.n1jel.pc.assembler.service.api.ProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Image controller", description = "Images rest controller")
@RestController
@RequestMapping("api/v1/images")
@RequiredArgsConstructor
public class RestImageController {

    private final ImageService imageService;
    private final ComponentService componentService;
    private final BuildService buildService;
    private final ProducerService producerService;

    @Operation(
            summary = "Get image by id",
            description = "Return encoded string"
    )
    @GetMapping("/{id}")
    public String get(@PathVariable Long id) {
        return imageService.load(new LoadImageRequestDto(id));
    }

    @Operation(
            summary = "Delete by id",
            description = "Deleting image by id and after success returns info about deleted image"
    )
    @DeleteMapping("/{id}")
    public ImageInfoResponseDto delete(@PathVariable Long id) {
        return imageService.delete(new DeleteImageRequestDto(id));
    }

    @Operation(
            summary = "Upload",
            description = "Uploading image and attaching it to object: component/build photo gallery or producer photo (replaces current)"
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageInfoResponseDto upload(@ModelAttribute UploadImageRequestDto request) {
        return switch (request.objectType()) {
            case COMPONENT -> componentService.addImage(request.objectId(), request.file());
            case BUILD -> buildService.addImage(request.objectId(), request.file());
            case PRODUCER -> producerService.setImage(request.objectId(), request.file());
        };
    }

}
