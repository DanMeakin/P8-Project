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

/**
 * This class is the controller for all competition-centric activities within
 * this software.
 *
 * The CompetitionController exposes routes relating to the creation and
 * management of competitions, and the viewing of their results.
 */
@Controller
@RequestMapping("/competition")
public class CompetitionController {

    private CompetitionService competitionService;
    private LifterService lifterService;
    private ParticipantService participantService;
    private ClubService clubService;
    private AddressService addressService;

    /**
     * Instantiates a CompetitionController.
     *
     * Uses the @Autowired annotation to automatically create instances of the
     * required services. This allows ORM access as required without manually
     * creating these services.
     *
     * @param lifterService      the service used to access Lifter data
     * @param competitionService the service used to access Competition data
     * @param participantService the service used to access Participant data
     * @param clubService        the service used to access Club data
     * @param addressService     the service used to access Address data
     */
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
     * Displays the create competition view for creating a new competition.
     *
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
     * Displays the competition overview for a given competition.
     *
     * @param model         the Spring model object to pass to the view
     * @param competitionID the ID# of the competition to view
     * @return              the competition overview view
     */
    @RequestMapping("/{competitionID}")
    public String competitionOverview(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        model.addAttribute("competition", competition);
        return "competition-overview";
    }

    /**
     * Displays the participants for a given competition.
     *
     * @param model         the Spring model object to pass to the view
     * @param competitionID the ID# of the competition to view
     * @return              the competition participants view
     */
    @RequestMapping("/{competitionID}/participants")
    public String competitionParticipants(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        model.addAttribute("competition", competition);
        return "competition-participants";
    }

    /**
     * Displays the competition dashboard for a given competition.
     *
     * The dashboard is used to manage the conduct of the competition itself:
     * the lifts complete, which lifter is next, etc. It is only accessible
     * following completion of the weigh-in, and before completion of all
     * lifts.
     *
     * If the competition is complete, this controller will redirect to the
     * {@link #viewRankingGroups(Model, long) results} view. Otherwise, it will
     * redirect to the {@link #competitionOverview(Model, long) overview} view.
     *
     * @param model         the Spring model object to pass to the view
     * @param competitionID the ID# of the competition to view
     * @return              the competition dashboard view
     */
    @RequestMapping("/{competitionID}/dashboard")
    public String competitionDashboard(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        model.addAttribute("competition", competition);

        if (competition.isCompetitionStarted()) {
            Optional<Group> currGroup = competition.getCurrentCompetingGroup();
            model.addAttribute("participants", currGroup.get().getParticipants());
            model.addAttribute("currParticipant", competition.getCurrentParticipant());
            return "competition-dashboard";
        } else if (competition.isCompetitionComplete()) {
            return "redirect:/competition/" + competitionID + "/results";
        } else {
            return "redirect:/competition/" + competitionID;
        }
    }

    /**
     * Displays the sign-up view for a given competition.
     *
     * This view is only accessible prior to completion of sign-up. If sign-up
     * is complete, this controller redirects to the
     * {@link #competitionOverview(Model, long) overview} view.
     *
     * @param id            the ID# of the club to sign-up
     * @param model         the Spring model object to pass to the view
     * @param competitionID the ID# of the competition to view
     * @return              the competition dashboard view
     */
    @RequestMapping("/{competitionID}/sign-up")
    public String competitionSignup(@RequestParam(value = "id", required = false, defaultValue = null) Long id, Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        if (id == null) {
          Club currentClub = clubService.findAll().get(0);
        } else {
          Club currentClub = clubService.findOne(id);
        }

        model.addAttribute("competition", competition);
        model.addAttribute("participants", competition.getParticipants());
        model.addAttribute("clubs", clubService.findAll());
        model.addAttribute("lifters", currentClub.getLifters());
        return "competition-sign-up";
    }

    /**
     * Signs-up a lifter to participant in a competition.
     *
     * @param id            the ID# of the lifter to sign-up
     * @param competitionID the ID# of the competition to sign-up a lifter to
     * @return              redirect to the
     *                      {@link #competitionSignup(Long, Model, long) sign-up}
     *                      view
     */
    @RequestMapping(value = "/{competitionID}/sign-up", method = RequestMethod.POST)
    public String signupLifterToCompetition(@RequestParam(value = "id", required = true) Long id, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        Lifter lifter = lifterService.findOne(id);
        competition.addParticipant(lifter);
        competitionService.save(competition);
        return "redirect:/competition/" + competitionID + "/sign-up";
    }

    /**
     * Removes a lifter from participation in a competition.
     *
     * @param id            the ID# of the lifter to sign-up
     * @param competitionID the ID# of the competition to sign-up a lifter to
     * @return              redirect to the
     *                      {@link #competitionSignup(Long, Model, long) sign-up}
     *                      view
     */
    @RequestMapping(value= "/{competitionID}/remove", method = RequestMethod.POST)
    public String removeLifterFromCompetition(@RequestParam(value = "id", required = false) Long id, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        Lifter lifter = lifterService.findOne(id);
        competition.removeParticipant(lifter);
        competitionService.save(competition);
        return "redirect:/competition/" + competitionID + "/sign-up";
    }

    /**
     * Displays the competition weigh-in view for a given competition.
     *
     * This view is only accessible prior to completion of sign-up. If sign-up
     * is complete, this controller redirects to the
     * {@link #competitionOverview(Model, long) overview} view.
     *
     * @param model         the Spring model object to pass to this view
     * @param competitionID the ID# of the competition for which to weigh-in
     *                      participants
     * @return              display the weigh-in view
     */
    @RequestMapping("/{competitionID}/weigh-in")
    public String controlWeighInParticipants(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        if (competition.isWeighInStarted()) {
            model.addAttribute("competition", competition);
            model.addAttribute("participants", competition.getParticipants());
            return "competition-weigh-in";
        } else {
            return "redirect:/competition/" + competitionID;
        }
    }

    /**
     * Attempts to weigh-in a participant with the provided details.
     *
     * This method validates that all input values are correct. If they are not,
     * this method will return a 400 error with details of the error in the
     * response body. Otherwise, returns a 200 code.
     *
     * @param model         the Spring model
     * @param participantID the ID# of the participant to weigh-in
     * @param bwStr         the string value of the input bodyweight
     * @param snatchStr     the string value of the input starting snatch
     * @param cjStr         the string value of the input starting clean & jerk
     * @return              JSON response with 400 code if invalid data is
     *                      input, else 200 if all valid and weigh-in complete
     */
    @ResponseBody
    @RequestMapping (value = "/{competitionID}/weigh-in/check-in", method = RequestMethod.POST)
    public String checkInParticipant(Model model,
                                     @RequestParam("participantID") long participantID,
                                     @RequestParam("bodyWeight") String bwStr,
                                     @RequestParam("startingSnatch") String snatchStr,
                                     @RequestParam("startingCJ") String cjStr){

        Participant participant = participantService.findOne(participantID);
        HashMap<String, String> map = new HashMap<>();


        try {
            //parsing data received from view into expected types
            double bodyWeight = Double.parseDouble(bwStr);
            int startingSnatchWeight = Integer.parseInt(snatchStr);
            int startingCleanAndJerkWeight = Integer.parseInt(cjStr);

            //changing model according to values inserted by user
            participant.weighIn(bodyWeight, startingSnatchWeight, startingCleanAndJerkWeight);

            //on success send success message with code 200
            map.put("code", "200");
            map.put("msg", "All good, participant checked in!");

        } catch (NumberFormatException e) {
            String msg = "unable to process input starting Snatch '" + snatchStr + "' or starting Clean & Jerk '"
                    + cjStr + "' (a number is required)";
            map.put("msg", msg);
            map.put("code", "400");
        } catch (InvalidParameterException e) {
            String msg = "unable to process input starting Snatch '" + snatchStr + "' or starting Clean & Jerk '"
                    + cjStr + "' (the first lift must be greater than 0)";
            map.put("msg", msg);
            map.put("code", "400");
        }

        participantService.saveParticipant(participant);
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    /**
     * Reverses a weigh-in of a participant.
     *
     * This is to be used where, for example, a participant has been erroneously
     * weighed-in, and this must be reversed.
     *
     * @param model         the Spring model
     * @param participantID the ID# of the participant for whom to reverse
     *                      weigh-in
     * @return              JSON response detailing result (should be 200)
     */
    @ResponseBody
    @RequestMapping(value = "/{competitionID}/weigh-in/check-out", method = RequestMethod.POST)
    public String checkOutParticipant(Model model,
                                      @RequestParam("participantID") long participantID) {

        Participant participant = participantService.findOne(participantID);

        HashMap<String, String> map = new HashMap<>();

        participant.setWeighedIn(false);
        map.put("code", "200");
        map.put("msg", "All good, participant checked out!");

        participantService.saveParticipant(participant);

        Gson gson = new Gson();
        return gson.toJson(map);
    }

    /**
     * Completes the weigh-in process for a competition.
     *
     * TODO: rename and refactor this so it is clearer this completes the weigh-
     *       in process
     *
     * @param model         the Spring model
     * @param competitionID the ID# of the competition for which to complete
     *                      weigh-in
     * @return              redirects to the
     *                      {@link #viewCompetingGroups(Model, long) competing groups}
     *                      view
     */
    @RequestMapping(value = "/{competitionID}/competing-groups", method = RequestMethod.POST)
    public String weighInParticipants(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        competition.finishWeighIn();
        competitionService.save(competition);
        return "redirect:/competition/" + competitionID + "/competing-groups";
    }

    /**
     * Displays the competing groups view.
     *
     * This should only be visible after completion of check-in. If this is not
     * the case, this controller redirects to the
     * {@link #competitionOverview(Model, long) overview} view.
     * @param model         the Spring model to pass to the view
     * @param competitionID the ID# of the competition for which to display the
     *                      competing groups
     * @return              the competing groups view
     */
    @RequestMapping("/{competitionID}/competing-groups")
    public String viewCompetingGroups(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        if (competition.isWeighInComplete()) {
            model.addAttribute("competingGroups", competition.getCompetingGroups());
            model.addAttribute("competition", competition);
            return "competition-groups";
        } else {
            return "redirect:/competition/" + competitionID;
        }
    }

    /**
     * Displays the results groups view.
     *
     * This should only be visible after completion of the competition. If this
     * is not the case, this controller redirects to the
     * {@link #competitionOverview(Model, long) overview} view.
     * @param model         the Spring model to pass to the view
     * @param competitionID the ID# of the competition for which to display the
     *                      results groups
     * @return              the results groups view
     */
    @RequestMapping("/{competitionID}/results")
    public String viewRankingGroups(Model model, @PathVariable long competitionID) {
        Competition competition = competitionService.findOne(competitionID);
        if (competition.isCompetitionComplete()) {
            model.addAttribute("rankingGroups", competition.getRankingGroups());
            model.addAttribute("competition", competition);
            return "ranking-groups";
        } else {
            return "redirect:/competition/" + competitionID;
        }
    }
}
