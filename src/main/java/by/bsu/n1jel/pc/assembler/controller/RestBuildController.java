package by.bsu.n1jel.pc.assembler.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/build")
public class RestBuildController {

    @GetMapping("/{buildId}")
    public String getBuildById(@PathVariable Long buildId) {
        return "";
    }

    @PostMapping("/create")
    public String createBuild() {
        return "";
    }

}
