package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.dto.request.create.BuildCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.create.BuildPartitionCreateRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.edit.BuildEditRequestDto;
import by.bsu.n1jel.pc.assembler.dto.request.search.BuildFilterRequestDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildInfoResponseDto;
import by.bsu.n1jel.pc.assembler.dto.response.BuildPartitionInfoResponseDto;
import by.bsu.n1jel.pc.assembler.service.api.BuildService;
import by.bsu.n1jel.pc.assembler.service.api.ComponentService;
import by.bsu.n1jel.pc.assembler.service.utils.DefaultImageUtil;
import by.bsu.n1jel.pc.assembler.service.utils.OverallUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/builds")
@RequiredArgsConstructor
public class BuildController {

    private final BuildService buildService;
    private final ComponentService componentService;

    @GetMapping
    public String list(@ModelAttribute BuildFilterRequestDto requestDto,
                       @RequestParam(defaultValue = "1") int page,
                       Model model) {

        Page<BuildInfoResponseDto> result = buildService.findBuildsBySearchFilter(requestDto, page);

        model.addAttribute("slotDefinitions", componentService.getAllComponentTypes());
        model.addAttribute("activePage", "builds");
        model.addAttribute("pageTitle", "Сборки");
        model.addAttribute("buildFilter", requestDto);
        model.addAttribute("builds", result.getContent());
        model.addAttribute("cpuBrandOptions", componentService.getAllProcessorProducers());

        // TODO All sockets
        model.addAttribute("socketOptions", List.of());

        model.addAttribute("currentPage", result.getTotalPages() == 0 ? 1 : result.getNumber() + 1);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalElements", result.getTotalElements());

        return "builds/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        BuildInfoResponseDto build = buildService.getBuildById(id);

        model.addAttribute("activePage", "builds");
        model.addAttribute("pageTitle", build.getName());
        model.addAttribute("build", build);

        model.addAttribute("defaultAvatar",
                build.getAvatar() != null && !build.getAvatar().isBlank()
                        ? build.getAvatar()
                        : DefaultImageUtil.buildAvatar(build.getName()));

        model.addAttribute("buildItems",
                build.getPartitions() == null ? List.of() : build.getPartitions());

        model.addAttribute("buildPhotos", List.of());
        model.addAttribute("totalPrice", build.getPrice());
        model.addAttribute("maxComponentTypeAmount", componentService.getAllComponentTypesSize());

        return "builds/detail";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        BuildCreateRequestDto buildForm = new BuildCreateRequestDto();

        BuildInfoResponseDto buildPreview = new BuildInfoResponseDto();
        buildPreview.setName("Новая сборка");
        buildPreview.setAvatar(DefaultImageUtil.buildAvatar("Новая сборка"));
        buildPreview.setPrice("0 BYN");
        buildPreview.setFilledSlots(0);
        buildPreview.setPartitions(List.of());

        model.addAttribute("activePage", "builds");
        model.addAttribute("pageTitle", "Создать сборку");
        model.addAttribute("mode", "create");

        // Форма только для имени
        model.addAttribute("buildForm", buildForm);

        // Это для превью и отображения слотов
        model.addAttribute("build", buildPreview);

        // Слоты по типам компонентов
        model.addAttribute("slotDefinitions", componentService.getAllComponentTypes());

        // На create пока партиций нет
        model.addAttribute("buildItems", List.of());

        model.addAttribute("buildId", null);
        model.addAttribute("returnUrl", "/builds/create");
        model.addAttribute("defaultAvatar", buildPreview.getAvatar());
        model.addAttribute("totalPrice", "0 BYN");
        model.addAttribute("filledSlots", 0);

        return "builds/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute BuildCreateRequestDto requestDto) {
        BuildInfoResponseDto responseDto = buildService.createBuild(requestDto);

        return "redirect:/builds/" + responseDto.getBuildId() + "/edit";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        BuildInfoResponseDto build = buildService.getBuildById(id);

        BuildCreateRequestDto buildForm = new BuildCreateRequestDto();
        buildForm.setName(build.getName());

        Map<Long, BuildPartitionInfoResponseDto> partitionsByType =
                build.getPartitions().stream()
                        .collect(Collectors.toMap(
                                BuildPartitionInfoResponseDto::getComponentTypeId,
                                partition -> partition,
                                (a, b) -> a,
                                LinkedHashMap::new
                        ));

        model.addAttribute("partitionsByType", partitionsByType);

        model.addAttribute("activePage", "builds");
        model.addAttribute("pageTitle", "Редактировать сборку");
        model.addAttribute("mode", "edit");

        // Имя сборки
        model.addAttribute("buildForm", buildForm);

        // Полная response DTO для предпросмотра/слотов
        model.addAttribute("build", build);

        // Список типов компонентов
        model.addAttribute("slotDefinitions", componentService.getAllComponentTypes());

        model.addAttribute("buildId", id);
        model.addAttribute("returnUrl", "/builds/" + id + "/edit");
        model.addAttribute("defaultAvatar",
                build.getAvatar() != null && !build.getAvatar().isBlank()
                        ? build.getAvatar()
                        : DefaultImageUtil.buildAvatar(build.getName()));
        model.addAttribute("totalPrice", build.getPrice());
        model.addAttribute("filledSlots", build.getFilledSlots());

        return "builds/form";
    }

    @PostMapping("/{buildId}/edit")
    public String edit(@PathVariable Long buildId,
                       @ModelAttribute BuildEditRequestDto buildEditRequestDto) {

        buildEditRequestDto.setBuildId(buildId);
        buildService.editBuildInfo(buildEditRequestDto);

        return "redirect:/builds/" + buildId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        buildService.deleteBuildById(id);
        return "redirect:/builds";
    }

    @PostMapping("/{buildId}/partitions/component")
    public String setSlotComponent(@PathVariable Long buildId,
                                   @RequestParam Long componentId,
                                   @RequestParam String returnUrl) {

        buildService.createOrEditPartition(new BuildPartitionCreateRequestDto(componentId, buildId, 1));
        return "redirect:" + returnUrl;
    }

    @PostMapping("/{buildId}/partitions/delete/{buildPartitionId}")
    public String deleteBuildPartitionById(@PathVariable Long buildId, @PathVariable Long buildPartitionId) {
        buildService.deleteBuildPartitionById(buildPartitionId);
        return "redirect:/builds/" + buildId + "/edit";
    }
}