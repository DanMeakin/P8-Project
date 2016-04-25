package dk.aau.ida8.controller;


import com.google.gson.Gson;
import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.Lift;
import dk.aau.ida8.model.Participant;
import dk.aau.ida8.service.LiftService;
import dk.aau.ida8.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/participant")
public class ParticipantController {

    private ParticipantService participantService;
    private LiftService liftService;

    @Autowired
    public ParticipantController(ParticipantService participantService,
                                 LiftService liftService) {
        this.participantService = participantService;
        this.liftService = liftService;
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

    /**
     * Displays the correct lifts view for a particular participant.
     *
     * @param model the Spring model object to pass to the view
     * @param participantID the ID# of the participant whose lifts are to change
     * @return the correct lifts view
     */
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
        String response;
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
            response = jsonResponse(200, "All good!");
        } else {
            response = jsonResponse(400, String.join("; ", msgs));
        }
        return response;
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
        String response;
        try {
            int weight = Integer.parseInt(weightStr);
            p.increaseWeight(weight);
            participantService.saveParticipant(p);
            response = jsonResponse(200, "All good!");
        } catch (NumberFormatException e) {
            String msg = "unable to process input weight '" + weightStr +
                    "' (a number is required)";
            response = jsonResponse(400, msg);
        } catch (InvalidParameterException e) {
            String msg = "unable to process input weight '" + weightStr +
                    "' (new weight must be greater than previous weight)";
            response = jsonResponse(400, msg);
        } catch (UnsupportedOperationException e) {
            String msg = "unable to increase weight: this participant has " +
                    "already increased their lift weight twice since " +
                    "previous lift";
            response = jsonResponse(400, msg);
        }
        return response;
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
        String response;
        try {
            int weight = Integer.parseInt(weightStr);
            p.correctWeight(weight);
            participantService.saveParticipant(p);
            response = jsonResponse(200, "All good!");
        } catch (NumberFormatException e) {
            String msg = "unable to process input weight '" + weightStr +
                    "' (a number is required)";
            response = jsonResponse(400, msg);
        }
        return response;
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

        return jsonResponse(200, "All good!");
    }

    /**
     * Generates a JSON response String for a given code and message.
     *
     * @param code the HTTP response code
     * @param msg  the message to return within the JSON response
     */
    private String jsonResponse(int code, String msg) {
        HashMap<String, String> map = new HashMap<>();
        map.put("code", Integer.toString(code));
        map.put("msg", msg);
        Gson gson = new Gson();
        return gson.toJson(map);
    }
}
