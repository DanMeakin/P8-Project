package dk.aau.ida8.controller;

import dk.aau.ida8.service.LiftService;
import dk.aau.ida8.service.LifterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CompetitionController {

    private LifterService lifterService;

    @Autowired
    public CompetitionController(LifterService lifterService) {
        this.lifterService = lifterService;
    }

    /*
    @RequestMapping(value = "/competition")
    public String comp() {
        return "competition";
    }
    */

    @RequestMapping("/competition")
    public String showAll(Model model) {
        model.addAttribute("lifters", lifterService.findAll());
        return "competition";
    }

}
