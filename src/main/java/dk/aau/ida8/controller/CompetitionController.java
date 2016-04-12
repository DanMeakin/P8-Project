package dk.aau.ida8.controller;

import dk.aau.ida8.service.LifterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/competition")
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

}
