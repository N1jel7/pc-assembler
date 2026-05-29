package by.bsu.n1jel.pc.assembler.controller;

import by.bsu.n1jel.pc.assembler.service.api.BuildService;
import by.bsu.n1jel.pc.assembler.service.api.ComponentService;
import by.bsu.n1jel.pc.assembler.service.api.ProducerService;
import by.bsu.n1jel.pc.assembler.service.api.StatisticService;
import by.bsu.n1jel.pc.assembler.service.utils.OverallUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final StatisticService statisticService;
    private final BuildService buildService;
    private final ComponentService componentService;
    private final ProducerService producerService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("activePage", "index");
        model.addAttribute("pageTitle", OverallUtil.getProjectName());
        model.addAttribute("pageSubtitle", "Конфигуратор ПК");
        model.addAttribute("stats", statisticService.getStatistics());
        model.addAttribute("latestComponents", componentService.getLatestComponents());
        model.addAttribute("latestBuilds", buildService.getLatestBuilds());
        model.addAttribute("topProducers", producerService.getTopProducers());
        return "index";
    }
}
