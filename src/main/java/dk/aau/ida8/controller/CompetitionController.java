package dk.aau.ida8.controller;

import com.google.gson.Gson;
import dk.aau.ida8.model.*;
import dk.aau.ida8.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/competition")
public class CompetitionController {

    private CompetitionService competitionService;
    private LifterService lifterService;
    private ParticipantService participantService;
    private LiftService liftService;
    private ClubService clubService;

    @Autowired
    public CompetitionController(LiftService liftService,LifterService lifterService, CompetitionService competitionService, ParticipantService participantService, ClubService clubService) {
        this.liftService = liftService;
        this.lifterService = lifterService;
        this.competitionService = competitionService;
        this.participantService = participantService;
        this.clubService = clubService;
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

    @RequestMapping("/{competitionID}/signup")
    public String competitionSignup(@RequestParam(value = "id", required = false, defaultValue = "1") Long id, Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        Club currentClub = clubService.findOne(id);

        model.addAttribute("competition", competition);
        model.addAttribute("participants", competition.getParticipants());
        model.addAttribute("clubs", clubService.findAll());
        model.addAttribute("lifters", currentClub.getLifters());
        return "competition-signup";
    }

    @RequestMapping(value = "/{competitionID}/signup", method = RequestMethod.POST)
    public String signupLifterToCompetition(@RequestParam(value = "id", required = false) Long id, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        Lifter lifter = lifterService.findOne(id);
        competition.addParticipant(lifter, 0);
        competitionService.save(competition);
        return "redirect:/competition/" + competition.getId() + "/signup";
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

    @RequestMapping(value="/correct-lifts/{participantID}", method = RequestMethod.GET)
    public String correctCompletedLift(Model model, @PathVariable long participantID){
        Participant p = participantService.findOne(participantID);
        model.addAttribute("participant", p);
        return "correct-lift-form";
    }

    /**
     * Updates the weight value of the completed lifts for a particular
     * participant.
     *
     * This method must handle input from a text field which should be
     * numerical. As such, this method requires to parse the text as an integer,
     * and also ensure that the integer is greater than 0. This method
     * elegantly handles cases where this is not so.
     *
     * @param model the Spring model object to pass to the view
     * @param participantID the ID# of the participant whose lifts are to change
     * @param liftStrs the new weights to set for the participants lifts
     * @return correct lift result view if a valid weight is passed, or the
     *         correct lift form view with an error message if an invalid weight
     *         is passed
     */
    @ResponseBody
    @RequestMapping(value = "/correct-lifts", method = RequestMethod.POST)
    public String submitCorrectedLifts(Model model,
                                       @RequestParam("id") long participantID,
                                       @RequestParam(value = "lift") List<String> liftStrs) {
        Participant p = participantService.findOne(participantID);
        model.addAttribute("participant", p);
        HashMap<String, String> map = new HashMap<>();
        List<String> msgs = new ArrayList<>();

        for (int i = 0; i < liftStrs.size(); i++) {
            try {
                int currWeight = Integer.parseInt(liftStrs.get(i));
                Lift currLift = p.getLifts().get(i);
                if (currWeight != currLift.getWeight()) {
                    currLift.setWeight(currWeight);
                }
                liftService.saveLift(currLift);
            } catch (NumberFormatException e) {
                String msg = "unable to process input weight '" + liftStrs.get(i) +
                        "' (a number is required)";
                msgs.add(msg);
            } catch (InvalidParameterException e) {
                String msg = "unable to process input weight '" + liftStrs.get(i) +
                        "' (new weight must be 1kg or greater)";
                msgs.add(msg);
            }
        }
        if (msgs.isEmpty()) {
            map.put("msg", "All good!");
            map.put("code", "200");
        } else {
            map.put("msg", String.join("; ", msgs));
            map.put("code", "400");
        }
        Gson gson = new Gson();
        return gson.toJson(map);
    }


    /**
     * Displays the increase weight view for increasing the weight to be lifted
     * by a participant.
     *
     * @param model the Spring model to pass to the view
     * @param participantID the ID# of the participant whose current weight is
     *                      to be changed
     * @return increase weight form view
     */
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

    /**
     * Carries out an increase of the current weight to be lifted for a given
     * participant.
     *
     * This method returns a JSON response with a response code and a message
     * representing the outcome of the request. If successful, code 200 will
     * be returned. If not, code 400 with a message explaining the error will
     * be returned.
     *
     * @param model the Spring model to pass to the view
     * @param participantID the ID# of the participant whose current weight is
     *                      to be changed
     * @param weightStr the new weight for this participant
     * @return a JSON response representing either success or failure
     */
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

    /**
     * Displays the correct weight view for a given participant.
     *
     * This view permits the user to change the current lift weight for a
     * particular participant where it was increased in error.
     *
     * @param model the Spring model to pass to the view
     * @param participantID the ID# of the participant for whom to display info
     * @return the correct weight view
     */
    @RequestMapping(value = "/correct-weight/{participantID}", method = RequestMethod.GET)
    public String correctWeightForm(Model model,
                                    @PathVariable long participantID) {
        Participant p = participantService.findOne(participantID);
        model.addAttribute(p);
        return "correct-weight-form";
    }

    /**
     * Corrects the current weight to be lifted by a given participant.
     *
     * This method returns a JSON response with a response code and a message
     * representing the outcome of the request. If successful, code 200 will
     * be returned. If not, code 400 with a message explaining the error will
     * be returned.
     *
     * @param model the Spring model to pass to the view
     * @param participantID the ID# of the participant whose current weight is
     *                      to be changed
     * @param weightStr the new weight for this participant
     * @return a JSON response representing either success or failure
     */
    @ResponseBody
    @RequestMapping(value = "/correct-weight", method = RequestMethod.POST)
    public String correctWeight(Model model,
                                @RequestParam("id") long participantID,
                                @RequestParam("currentWeight") String weightStr) {
        Participant p = participantService.findOne(participantID);
        model.addAttribute("participant", p);
        HashMap<String, String> map = new HashMap<>();
        try {
            int weight = Integer.parseInt(weightStr);
            p.correctWeight(weight);
            participantService.saveParticipant(p);
            map.put("msg","All good!");
            map.put("code", "200");
        } catch (NumberFormatException e) {
            String msg = "unable to process input weight '" + weightStr +
                    "' (a number is required)";
            map.put("msg", msg);
            map.put("code", "400");
        }
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    /**
     * Reverts a participant's lift weight to the value it was previously.
     *
     * @param model the Spring model to pass to the view
     * @param participantID the ID# of the participant for whom to revert lift
     *                      weight
     * @return JSON response code for success
     */
    @ResponseBody
    @RequestMapping(value = "/revert-weight", method = RequestMethod.POST)
    public String revertWeight(Model model,
                               @RequestParam("id") long participantID) {
        Participant p = participantService.findOne(participantID);
        model.addAttribute("participant", p);
        p.revertWeight();
        participantService.saveParticipant(p);

        HashMap<String, String> map = new HashMap<>();
        map.put("code", "200");
        map.put("msg", "All good!");
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    /**
     * Participant information partial view.
     *
     * @param model the Spring model to pass to the view
     * @param participantID the ID# of the participant for whom to display info
     * @return the participant's information
     */
    @RequestMapping(value = "/participant-info/{participantID}", method = RequestMethod.GET)
    public String displayParticipantInfo(Model model, @PathVariable long participantID){
        model.addAttribute("participant", participantService.findOne(participantID));
        return "participant-info";
    }
}
