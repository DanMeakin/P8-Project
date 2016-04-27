package dk.aau.ida8.controller;

import dk.aau.ida8.model.Club;
import dk.aau.ida8.model.Lifter;
import dk.aau.ida8.service.ClubService;
import dk.aau.ida8.service.LifterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
        Club currentClub = clubService.findOne(id);

        model.addAttribute("clubs", clubService.findAll());
        model.addAttribute("lifters", currentClub.getLifters());
        model.addAttribute("lifter", new Lifter());

        return "club-lifters";
    }

    @RequestMapping(value="/club/save", method = RequestMethod.POST)
    public String saveLifter(@RequestParam(value = "lifter-club-id", required = false, defaultValue = "1") Long id, Lifter lifter) {
        Club currentClub = clubService.findOne(id);
        lifter.setClub(currentClub);
        currentClub.addLifter(lifter);
        lifterService.saveLifter(lifter);

        return "redirect:/club/new-lifter";
    }

    @RequestMapping("/club/remove/{id}")
    public String removeLifter(@PathVariable Long id) {
        Lifter lifter = lifterService.findOne(id);
        Club club = clubService.findByName(lifter.getClubName());
        lifter.setClub(null);
        club.removeLifter(lifter);
        lifterService.saveLifter(lifter);
        clubService.saveClub(club);
        return "redirect:/club/new-lifter";
    }

    @RequestMapping("/club/lifter/{id}")
    public String updateLifter(@PathVariable Long id, Model model) {
        model.addAttribute("lifter", lifterService.findOne(id));
        return "edit-lifter";
    }

    @RequestMapping(value="club/lifter/save", method = RequestMethod.POST)
    public String saveUpdatedLifter(Lifter lifter) {
        Club club = clubService.findByName(lifter.getClubName());
        lifterService.saveLifter(lifter);

        return "redirect:/club/new-lifter?id=" + club.getId();
    }

}
