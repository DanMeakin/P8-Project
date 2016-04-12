package dk.aau.ida8.controller;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.service.CompetitionService;
import dk.aau.ida8.service.LifterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class CompetitionController {

    private CompetitionService competitionService;

    private LifterService lifterService;

    @Autowired
    public CompetitionController(LifterService lifterService, CompetitionService competitionService) {
        this.lifterService = lifterService;
        this.competitionService = competitionService;
    }

    /*
    @RequestMapping(value = "/competition")
    public String comp() {
        return "competition";
    }
    */



    //Controller method to show all lifters connected to a specific competition
    @RequestMapping("/competition")
    public String showAll(Model model) {
        model.addAttribute("lifters", lifterService.findAll());
        return "competition";
    }

    /**
     *     Controller method to create a new competition object when on the specified URL
     * @param model
     * @return
     */
    @RequestMapping("/competition/new")
    public String newComp(Model model){
        model.addAttribute("competition", new Competition());
        return "new-competition";
    }

    /**
     * Controller method to save a competition. The save method is called from CompetitionService.
     * @param model
     * @param competition
     * @return Returns a redirect to the front page
     */
    @RequestMapping(value="/competition/save", method = RequestMethod.POST)
    public String saveComp(Model model, Competition competition){
        Competition savedComp = competitionService.save(competition);
        return "redirect:/";
    }


}
