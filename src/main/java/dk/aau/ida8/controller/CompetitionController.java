package dk.aau.ida8.controller;

import com.google.gson.Gson;
import dk.aau.ida8.model.*;
import dk.aau.ida8.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.*;

@Controller
@RequestMapping("/competition")
public class CompetitionController {

    private CompetitionService competitionService;
    private LifterService lifterService;
    private ParticipantService participantService;
    private ClubService clubService;
    private AddressService addressService;

    @Autowired
    public CompetitionController(LifterService lifterService,
                                 CompetitionService competitionService,
                                 ParticipantService participantService,
                                 ClubService clubService,
                                 AddressService addressService) {
        this.lifterService = lifterService;
        this.competitionService = competitionService;
        this.participantService = participantService;
        this.clubService = clubService;
        this.addressService = addressService;
    }

    /**
     * Controller method to create a new competition object when on the specified URL
     * @param model the Spring model object to pass to the view
     * @return      the new competition form view
     */
    @RequestMapping(value="/new", method = RequestMethod.GET)
    public String newCompetition(Model model){
        model.addAttribute("allClubs", clubService.findAll());
        model.addAttribute("competition", new Competition());
        return "new-competition";
    }

    /**
     * Creates a new competition, and redirects to the index page.
     *
     * @param competition the Competition object created in the new competition
     *                    page
     * @param model       the Spring model object to pass to the view
     * @return Returns a redirect to the front page
     */
    @RequestMapping(value="/new", method = RequestMethod.POST)
    public String createCompetition(@ModelAttribute Competition competition,
                                    Model model){
        Address address = competition.getLocation();
        addressService.saveAddress(address);
        competitionService.save(competition);
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

    @RequestMapping("/{competitionID}/dashboard")
    public String competitionDashboard(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        model.addAttribute("competition", competition);

        Optional<Group> currGroup = competition.getCurrentCompetingGroup();
        if (currGroup.isPresent()) {
            model.addAttribute("participants", currGroup.get().getParticipants());
            model.addAttribute("currParticipant", competition.currentParticipant());
            return "competition-dashboard";
        } else {
            return "competition-overview";
        }
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

    @RequestMapping(value = "/{competitionID}/weigh-in", method = RequestMethod.GET)
    public String controlWeighInParticipants(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        model.addAttribute("competition", competition);
        model.addAttribute("participants", competition.getParticipants());
        return "competition-weigh-in";
    }

    @ResponseBody
    @RequestMapping (value = "/{competitionID}/weigh-in/check-in", method = RequestMethod.POST)
    public String checkInParticipant(Model model,
                                     @RequestParam("participantID") long participantID,
                                     @RequestParam("bodyWeight") String bodyWeight,
                                     @RequestParam("startingSnatch") String startingSnatch,
                                     @RequestParam("startingCJ") String startingCJ){

        Participant participant = participantService.findOne(participantID);
        HashMap<String, String> map = new HashMap<>();

        double bw = Double.parseDouble(bodyWeight);

        int firstSnatch = Integer.parseInt(startingSnatch);
        int firstCj = Integer.parseInt(startingCJ);

        try {
            participant.setBodyWeight(bw);
            participant.setStartingSnatchWeight(firstSnatch);
            participant.setStartingCleanAndJerkWeight(firstCj);
            participant.setCheckedIn(true);

            map.put("code", "200");
            map.put("msg", "All good, participant checked in!");

        } catch (NumberFormatException e){
            String msg = "unable to process input starting Snatch '" + startingSnatch + "' or starting Clean & Jerk '"
                    + startingCJ + "' (a number is required)";
            map.put("msg", msg);
            map.put("code", "400");
        } catch (InvalidParameterException e){
            String msg = "unable to process input starting Snatch '" + startingSnatch + "' or starting Clean & Jerk '"
                    + startingCJ + "' (the first lift must be greater than 0)";
            map.put("msg", msg);
            map.put("code", "400");
        }

        participantService.saveParticipant(participant);
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @ResponseBody
    @RequestMapping (value = "/{competitionID}/weigh-in/check-out", method = RequestMethod.POST)
    public String checkOutParticipant(Model model,
                                      @RequestParam("participantID") long participantID){

        Participant participant = participantService.findOne(participantID);

        HashMap<String, String> map = new HashMap<>();

        participant.setCheckedIn(false);
        map.put("code", "200");
        map.put("msg", "All good, participant checked out!");

        participantService.saveParticipant(participant);

        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @RequestMapping (value = "/{competitionID}/groups", method = RequestMethod.POST)
    public String displayGroups(Model model, @PathVariable long competitionID){
        Competition competition = competitionService.findOne(competitionID);
        competition.finishWeighIn();
        competitionService.save(competition);
        return "competition-groups";
    }

}
