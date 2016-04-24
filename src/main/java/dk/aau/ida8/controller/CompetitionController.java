package dk.aau.ida8.controller;

import com.google.gson.Gson;
import dk.aau.ida8.model.*;
import dk.aau.ida8.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
     * @param model
     * @return
     */
    @RequestMapping(value="/new", method = RequestMethod.GET)
    public String newCompetition(Model model){
        model.addAttribute("allClubs", clubService.findAll());
        model.addAttribute("competition", new Competition());
        return "new-competition";
    }

    /**
     * Controller method to save a Sinclair competition. The save method is called from CompetitionService.
     * @param competition
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

    @RequestMapping("/{competitionID}")
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

    @RequestMapping(value = "/{competitionID}/weigh-in", method = RequestMethod.GET)
    public String controlWeighInParticipants(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        model.addAttribute("competition", competition);
        model.addAttribute("participants", competition.getParticipants());
        return "competition-weigh-in";
    }

    @ResponseBody
    @RequestMapping (value = "/{competitionID}/weigh-in/", method = RequestMethod.POST)
    public String registerParticipantWeighIn(Model model, @RequestParam("participantID") long participantID,
                                                            @RequestParam("action") String isChecked){
        Participant participant = participantService.findOne(participantID);
        HashMap<String, String> map = new HashMap<>();

        if(isChecked.equals("checked")){
            participant.setCheckedIn(true);
            map.put("code", "200");
            map.put("msg", "All good, participant checked in!");
        } else if(isChecked.equals("unchecked")){
            participant.setCheckedIn(false);
            map.put("code", "200");
            map.put("msg", "All good, participant checked out!");
        }
        participantService.saveParticipant(participant);
        model.addAttribute("participants", participant.getCompetition().getParticipants());
        Gson gson = new Gson();
        return gson.toJson(map);
    }
}
