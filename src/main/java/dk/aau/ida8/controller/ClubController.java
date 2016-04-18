package dk.aau.ida8.controller;

import dk.aau.ida8.model.Club;
import dk.aau.ida8.model.Lift;
import dk.aau.ida8.model.Lifter;
import dk.aau.ida8.service.ClubService;
import dk.aau.ida8.service.LifterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ClubController {

    private ClubService clubService;
    private LifterService lifterService;

    @Autowired
    public ClubController(ClubService clubService, LifterService lifterService) {
        this.clubService = clubService;
        this.lifterService = lifterService;
    }

    @RequestMapping("/club/new-lifter")
    public String newLifter(@RequestParam(value = "id", required = false, defaultValue = "1") Long id, Model model) {

        Club akjyden = clubService.findOne(1L);
        akjyden.addLifter(new Lifter("Nicklas", "Holm", akjyden, Lifter.Gender.MALE, new Date(), 70.0));

        Club currentClub = clubService.findOne(id);

        model.addAttribute("clubs", clubService.findAll());
        model.addAttribute("lifters", currentClub.getLifters());
        model.addAttribute("lifter", new Lifter());
        return "club-lifters";
    }

    @RequestMapping(value="/club/save", method = RequestMethod.POST)
    public String saveLifter(Lifter lifter) {
        Lifter savedLifter = lifterService.saveLifter(lifter);
        return "redirect:/club/new-lifter";
    }

    @RequestMapping("/club/delete/{id}")
    public String deleteLifter(@PathVariable Long id) {
        lifterService.deleteLifter(id);
        return "redirect:/club-lifters";
    }

}
