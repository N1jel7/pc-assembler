package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.request.ProducerCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/producers")
public class RestProducerController {

    private final ProducerService producerService;

    @GetMapping
    public List<ProducerInfoResponseDto> getAllProducers() {
        return producerService.getAllProducers();
    }

    @GetMapping("/{producerId}")
    public ProducerInfoResponseDto getProducerById(@PathVariable Long producerId) {
        return producerService.getProducerById(producerId);
    }

    @PostMapping("/create")
    public ProducerInfoResponseDto createProducer(@RequestBody ProducerCreateRequestDto requestDto) {
        return producerService.createProducer(requestDto);
    }

    @PatchMapping("/edit")
    public ProducerInfoResponseDto editProducer(@RequestBody ProducerEditInfoRequestDto requestDto) {
        return producerService.editProducerInfo(requestDto);
    }

    @DeleteMapping("/delete/{producerId}")
    public ProducerInfoResponseDto deleteProducer(@PathVariable Long producerId) {
        return producerService.deleteProducerById(producerId);
    }
}
