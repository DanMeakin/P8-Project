package dk.aau.ida8.controller;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.CompetitionSinclair;
import dk.aau.ida8.model.Participant;
import dk.aau.ida8.service.CompetitionService;
import dk.aau.ida8.service.LiftService;
import dk.aau.ida8.service.LifterService;
import dk.aau.ida8.service.ParticipantService;
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
    private ParticipantService participantService;
    private LiftService liftService;

    @Autowired
    public CompetitionController(LiftService liftService,LifterService lifterService, CompetitionService competitionService, ParticipantService participantService) {
        this.liftService = liftService;
        this.lifterService = lifterService;
        this.competitionService = competitionService;
        this.participantService = participantService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showAll(Model model) {
        model.addAttribute("lifters", lifterService.findAll());
        return "competition";
    }

    @RequestMapping(value = "/register-lift/{participantID}/", method = RequestMethod.GET)
    public String liftOutcomeForm(Model model, @PathVariable long participantID) {
        model.addAttribute("participant", participantService.findOne(participantID));
        return "lift-register-form";
    }

    @RequestMapping(value = "/register-lift/", method = RequestMethod.POST)
    public String registerLift(Model model, @RequestParam("action") String action, @RequestParam("participantID") long participantID) {
        Participant p = participantService.findOne(participantID);
        switch (action) {
            case "PASS":
                p.addPassedLift();
                break;
            case "FAIL":
                p.addFailedLift();
                break;
            case "ABSTAIN":
                p.addAbstainedLift();
                break;
        }
        participantService.saveParticipant(p);
        model.addAttribute("participant", p);
        return "lift-register-form";
    }

    /**
     *     Controller method to create a new competition object when on the specified URL
     * @param model
     * @return
     */
    @RequestMapping("/new")
    public String newComp(Model model){
       // model.addAttribute("competition", new CompetitionSinclair());
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

    @RequestMapping(value="/correct-lift/{liftID}", method = RequestMethod.GET)
    public String correctCompletedLift(Model model, @PathVariable long liftID){
        model.addAttribute("lift", liftService.findOne(liftID));
        return "correct-lift-form";
    }


}
