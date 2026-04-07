package by.bsu.n1jel.pc.assembler.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/components")
public class RestComponentController {

    @GetMapping("/{componentType}")
    public String getComponentsByType(@PathVariable String componentType) {
        return "";
    }

    @GetMapping()
    public String getComponentById() {
        return "";
    }

}
