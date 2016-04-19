package dk.aau.ida8.controller;

import com.google.gson.Gson;
import dk.aau.ida8.model.*;
import dk.aau.ida8.service.CompetitionService;
import dk.aau.ida8.service.LiftService;
import dk.aau.ida8.service.LifterService;
import dk.aau.ida8.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;

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

    /**
     * Displays the form to allow the user to correct an incorrectly entered
     * lift weight value.
     *
     * @param model the Spring model object to pass to the view
     * @return correct lift form view
     * Controller method to create a new competition object when on the specified URL
     * @param model
     * @return
     */
    @RequestMapping("/sinclair/new")
    public String newSinclairComp(Model model){
       model.addAttribute("competition", new CompetitionSinclair());
        return "new-sinclair-competition";
    }

    /**
     * Controller method to create a new competition object when on the specified URL
     * @param model
     * @return
     */
    @RequestMapping("/weightclass/new")
    public String newWeightclassComp(Model model){
        model.addAttribute("competition", new CompetitionTotalWeight());
        return "new-weightclass-competition";
    }

    /**
     * Controller method to save a Sinclair competition. The save method is called from CompetitionService.
     * @param competition
     * @return Returns a redirect to the front page
     */
    @RequestMapping(value="/sinclair/save", method = RequestMethod.POST)
    public String saveComp(CompetitionSinclair competition){
        Competition savedComp = competitionService.save(competition);
        return "redirect:/";
    }

    /**
     * Controller method to save a Weight group competition. The save method is called from CompetitionService.
     * @param competition
     * @return
     */
    @RequestMapping(value="/weightclass/save", method = RequestMethod.POST)
    public String saveComp(CompetitionTotalWeight competition){
        Competition savedComp = competitionService.save(competition);
        return "redirect:/";
    }

    /**
     * Displays the view to allow the users to register a new lift for a given
     * Participant.
     *
     * @param model the Spring model object to pass to the view
     * @param participantID the ID# of the participant for whom to register a
     *                      lift
     * @return lift register form view
     */
    @RequestMapping(value = "/register-lift/{participantID}", method = RequestMethod.GET)
    public String liftOutcomeForm(Model model, @PathVariable long participantID) {
        model.addAttribute("participant", participantService.findOne(participantID));
        return "lift-register-form";
    }

    @RequestMapping("/{competitionID}")
    public String competitionDashboard(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        model.addAttribute("competition", competition);
        model.addAttribute("participants", competition.getParticipants());
        model.addAttribute("participant", competition.currentParticipant());
        return "competition";
    }

    /**
     * Creates a new lift for a particular Participant.
     *
     * @param model the Spring model object to pass to the view
     * @param action the string value representing the outcome of a lift: PASS,
     *               FAIL or ABSTAIN
     * @param participantID the ID# of the participant for whom to register a
     *                      lift
     * @return lift register form view
     */
    @RequestMapping(value = "/register-lift", method = RequestMethod.POST)
    public String registerLift(Model model,
                               @RequestParam("action") String action,
                               @RequestParam("participantID") long participantID) {
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
            default:
                throw new ResourceNotFoundException();
        }
        participantService.saveParticipant(p);
        Competition c = p.getCompetition();
        model.addAttribute("participant", p);
        return "redirect:/competition/" + c.getId();
    }

    @RequestMapping(value="/correct-lift/{liftID}", method = RequestMethod.GET)
    public String correctCompletedLift(Model model, @PathVariable long liftID){
        model.addAttribute("lift", liftService.findOne(liftID));
        return "correct-lift-form";
    }

    /**
     * Updates the weight value of a particular lift, using form data posted
     * from the correct lift form view.
     *
     * This method must handle input from a text field which should be
     * numerical. As such, this method requires to parse the text as an integer,
     * and also ensure that the integer is greater than 0. This method
     * elegantly handles cases where this is not so.
     *
     * @param model the Spring model object to pass to the view
     * @param liftID the ID# of the lift to change
     * @param weightStr the new weight to set for the lift
     * @return correct lift result view if a valid weight is passed, or the
     *         correct lift form view with an error message if an invalid weight
     *         is passed
     */
    @ResponseBody
    @RequestMapping(value = "/correct-lift", method = RequestMethod.POST)
    public String submitCorrectedLift(Model model,
                                      @RequestParam("id") long liftID,
                                      @RequestParam("weight") String weightStr) {
        Lift l = liftService.findOne(liftID);
        model.addAttribute("lift", l);
        HashMap<String, String> map = new HashMap<>();
        try {
            int weight = Integer.parseInt(weightStr);
            l.setWeight(weight);
            liftService.saveLift(l);
            map.put("msg", "All good!");
            map.put("code", "200");
        } catch (NumberFormatException e) {
            String msg = "unable to process input weight '" + weightStr +
                    "' (a number is required)";
            map.put("msg", msg);
            map.put("code", "400");
        } catch (InvalidParameterException e) {
            String msg = "unable to process input weight '" + weightStr +
                    "' (new weight must be 1kg or greater)";
            map.put("msg", msg);
            map.put("code", "400");
        }
        Gson gson = new Gson();
        return gson.toJson(map);
    }


    @RequestMapping(value = "/increase-weight/{participantID}", method = RequestMethod.GET)
    public String increaseWeightForm(Model model,
                                     @PathVariable long participantID) {
        Participant p = participantService.findOne(participantID);
        model.addAttribute(p);
        if (!p.canChangeWeight()) {
            String msg = "unable to increase weight: this participant has " +
                    "already increased their lift weight twice since " +
                    "previous lift";
            model.addAttribute("msg", msg);
        }
        return "increase-weight-form";
    }

    @ResponseBody
    @RequestMapping(value = "/increase-weight", method = RequestMethod.POST)
    public String increaseWeight(Model model,
                                 @RequestParam("id") long participantID,
                                 @RequestParam("currentWeight") String weightStr) {
        Participant p = participantService.findOne(participantID);
        model.addAttribute("participant", p);
        HashMap<String, String> map = new HashMap<>();
        try {
            int weight = Integer.parseInt(weightStr);
            p.increaseWeight(weight);
            participantService.saveParticipant(p);
            map.put("msg","All good!");
            map.put("code", "200");
        } catch (NumberFormatException e) {
            String msg = "unable to process input weight '" + weightStr +
                    "' (a number is required)";
            map.put("msg", msg);
            map.put("code", "400");
        } catch (InvalidParameterException e) {
            String msg = "unable to process input weight '" + weightStr +
                    "' (new weight must be greater than previous weight)";
            map.put("msg", msg);
            map.put("code", "400");
        } catch (UnsupportedOperationException e) {
            String msg = "unable to increase weight: this participant has " +
                    "already increased their lift weight twice since " +
                    "previous lift";
            map.put("msg", msg);
            map.put("code", "400");
        }
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @RequestMapping(value = "/participant-info/{participantID}", method = RequestMethod.GET)
    public String displayParticipantInfo(Model model, @PathVariable long participantID){
        model.addAttribute("participant", participantService.findOne(participantID));
        return "participant-info";
    }


}
