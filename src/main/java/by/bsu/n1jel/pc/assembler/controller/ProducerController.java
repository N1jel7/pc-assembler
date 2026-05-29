package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.request.create.ProducerCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.ProducerEditInfoRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.ProducerFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.ProducerInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.ProducerService;
import by.bsu.n1jel.pc.assembler.service.utils.DefaultImageUtil;
import by.bsu.n1jel.pc.assembler.service.utils.OverallUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/producers")
@RequiredArgsConstructor
@Controller
public class ProducerController {

    private final ProducerService producerService;

    @GetMapping
    public String list(@ModelAttribute ProducerFilterRequestDto producerFilter,
                       @RequestParam(defaultValue = "1") Integer page,
                       Model model) {

        Page<ProducerInfoResponseDto> result = producerService.searchProducers(producerFilter, page);

        model.addAttribute("activePage", "producers");
        model.addAttribute("pageTitle", "Производители");
        model.addAttribute("producerFilter", producerFilter);
        model.addAttribute("producers", result.getContent());
        model.addAttribute("countries", producerService.findAllProducerCountries());
        model.addAttribute("currentPage", result.getTotalPages() == 0 ? 1 : result.getNumber() + 1);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalElements", result.getTotalElements());

        return "producers/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {

        ProducerInfoResponseDto infoResponseDto = new ProducerInfoResponseDto();
        infoResponseDto.setAvatar(DefaultImageUtil.producerAvatar("Здесь будет имя", "Здесь будет страна"));

        model.addAttribute("activePage", "producers");
        model.addAttribute("pageTitle", "Создать производителя");
        model.addAttribute("mode", "create");
        model.addAttribute("producerForm", infoResponseDto);
        model.addAttribute("existingImages", List.of());
        model.addAttribute("maxImages", OverallUtil.getMaxImagesSize());

        return "producers/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute ProducerCreateRequestDto requestDto) {

        producerService.createProducer(requestDto);
        return "redirect:/producers";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ProducerInfoResponseDto responseDto = producerService.getProducerById(id);
        /*List<ImageView> existingImages = producerService.getExistingImages(id);*/

        model.addAttribute("activePage", "producers");
        model.addAttribute("pageTitle", "Редактировать производителя");
        model.addAttribute("mode", "edit");
        model.addAttribute("producer", responseDto);
        model.addAttribute("avatar",
                responseDto.getAvatar() != null && !responseDto.getAvatar().isBlank()
                        ? responseDto.getAvatar()
                        : DefaultImageUtil.producerAvatar(responseDto.getName(), responseDto.getCountry()));
        model.addAttribute("existingImages", List.of());
        model.addAttribute("maxImages", OverallUtil.getMaxImagesSize());

        return "producers/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @ModelAttribute ProducerEditInfoRequestDto requestDto
                       /*@RequestParam(query = "images", required = false) MultipartFile[] images,
                       @RequestParam(query = "keptImageIds", required = false) List<Long> keptImageIds*/) {

        producerService.editProducerInfo(requestDto);
        return "redirect:/producers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        producerService.deleteProducerById(id);
        return "redirect:/producers";
    }
}
