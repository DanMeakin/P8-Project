package dk.aau.ida8.controller;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.service.CompetitionService;
import dk.aau.ida8.service.LifterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/competition")
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

    @RequestMapping(method = RequestMethod.GET)
    public String showAll(Model model) {
        model.addAttribute("lifters", lifterService.findAll());
        return "competition";
    }

    @RequestMapping(value = "/{competitionID}/register-lift/{participantID}", method = RequestMethod.GET)
    public String liftOutcomeForm(Model model, @PathVariable long competitionID, @PathVariable long participantID) {
        return "lift-register-form";
    }

    @RequestMapping(value = "/{competitionID}/register-lift/{participantID}", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    public String registerLift(Model model, @RequestParam("action") String action, @PathVariable long competitionID, @PathVariable long participantID) {
        if (action == "PASS") {
            return "<div>It's a pass!</div>";
        } else if (action == "FAIL") {
            return "<div>It's a fail!</div>";
        } else {
            return "<div>It's an abstention!</div>";
        }
    }

    /**
     *     Controller method to create a new competition object when on the specified URL
     * @param model
     * @return
     */
    @RequestMapping("/new")
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
    @RequestMapping(value="/save", method = RequestMethod.POST)
    public String saveComp(Model model, Competition competition){
        Competition savedComp = competitionService.save(competition);
        return "redirect:/";
    }


}
