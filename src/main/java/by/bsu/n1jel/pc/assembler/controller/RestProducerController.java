package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.request.create.ProducerCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.ProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Producer", description = "Producer rest controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/producers")
public class RestProducerController {

    private final ProducerService producerService;

    @Operation(
            summary = "Get all producers",
            description = "Return all existed producers from database"
    )
    @GetMapping
    public List<ProducerInfoResponseDto> getAllProducers() {
        return producerService.getAllProducers();
    }

    @Operation(
            summary = "Get producer by id",
            description = "Return producer by it's id"
    )
    @GetMapping("/{producerId}")
    public ProducerInfoResponseDto getProducerById(@PathVariable Long producerId) {
        return producerService.getProducerById(producerId);
    }

    @Operation(
            summary = "Create producer",
            description = "Creating new producer from request"
    )
    @PostMapping("/create")
    public ProducerInfoResponseDto createProducer(@RequestBody ProducerCreateRequestDto requestDto) {
        return producerService.createProducer(requestDto);
    }

    @Operation(
            summary = "Edit producer",
            description = "Editing existing parameters of producer"
    )
    @PatchMapping("/edit")
    public ProducerInfoResponseDto editProducer(@RequestBody ProducerEditInfoRequestDto requestDto) {
        return producerService.editProducerInfo(requestDto);
    }

    @Operation(
            summary = "Delete producer",
            description = "Deleting existing producer by id"
    )
    @DeleteMapping("/delete/{producerId}")
    public ProducerInfoResponseDto deleteProducer(@PathVariable Long producerId) {
        return producerService.deleteProducerById(producerId);
    }
}
