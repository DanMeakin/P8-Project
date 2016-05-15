package dk.aau.ida8.controller;

import dk.aau.ida8.model.Club;
import dk.aau.ida8.model.Lifter;
import dk.aau.ida8.service.ClubService;
import dk.aau.ida8.service.LifterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * This class is the controller for all club-centric activities within this
 * software.
 *
 * The ClubController exposes routes relating to the creation, updating and
 * removal of lifter members of a club. At present, there is no functionality
 * relating to the creation or removal of clubs themselves.
 */
@Controller
@RequestMapping("/club")
public class ClubController {

    private ClubService clubService;
    private LifterService lifterService;

    /**
     * Instantiates a ClubController.
     *
     * Uses the @Autowired annotation to automatically create instances of the
     * required services. This allows ORM access as required without manually
     * creating these services.
     *
     * @param clubService   the service used to access Club data
     * @param lifterService the service used to access Lifter data
     */
    @Autowired
    public ClubController(ClubService clubService, LifterService lifterService) {
        this.clubService = clubService;
        this.lifterService = lifterService;
    }

    /**
     * Displays the new lifter view for creating a lifter in a particular club.
     *
     * @param id    the club ID#
     * @param model the Spring model object to pass to the view
     * @return      the new lifter form view
     */
    @RequestMapping("/new-lifter")
    public String newLifter(@RequestParam(value = "id", required = false, defaultValue = "1") Long id, Model model) {
        Club currentClub = clubService.findOne(id);

        model.addAttribute("clubs", clubService.findAll());
        model.addAttribute("lifters", currentClub.getLifters());
        model.addAttribute("lifter", new Lifter());

        return "club-lifters";
    }

    /**
     * Saves a new lifter based on details passed to the form in the new lifter
     * view (see {@link #newLifter(Long, Model) newLifter}).
     *
     * The lifter is associated with the club represented by the ID# passed as
     * parameter to this method.
     *
     * No validation of input data is carried out at present. This may lead to
     * incorrect/invalid data being stored within the system.
     *
     * @param id     the club ID#
     * @param lifter the new lifter to create and associate with club
     * @return       redirect to {@link #newLifter(Long, Model) new lifter} view
     */
    @RequestMapping(value="/save", method = RequestMethod.POST)
    public String saveLifter(@RequestParam(value = "lifter-club-id", required = false, defaultValue = "1") Long id, Lifter lifter) {
        Club currentClub = clubService.findOne(id);
        lifter.setClub(currentClub);
        currentClub.addLifter(lifter);
        lifterService.saveLifter(lifter);

        return "redirect:/club/new-lifter";
    }

    /**
     * Removes a lifter from a club.
     *
     * This method removes the association between a lifter and a club. It does
     * not delete the lifter him/herself from the system. This permits a lifter
     * to move to a new club, retaining their profile.
     *
     * @param id the ID# of the lifter to remove
     * @return   redirect to {@link #newLifter(Long, Model) new lifter} view
     */
    @RequestMapping("/remove/{id}")
    public String removeLifter(@PathVariable Long id) {
        Lifter lifter = lifterService.findOne(id);
        Club club = clubService.findByName(lifter.getClubName());
        lifter.setClub(null);
        club.removeLifter(lifter);
        lifterService.saveLifter(lifter);
        clubService.saveClub(club);
        return "redirect:/club/new-lifter";
    }

    /**
     * Displays the edit lifter view for modifying an existing lifter within a
     * club.
     *
     * @param id    the ID# of the lifter to edit
     * @param model the Spring model object to pass to the view
     * @return      the edit lifter view
     */
    @RequestMapping("/lifter/{id}")
    public String updateLifter(@PathVariable Long id, Model model) {
        model.addAttribute("lifter", lifterService.findOne(id));
        return "edit-lifter";
    }

    /**
     * Updates a lifter's details based on what is passed to the form in the
     * {@link #updateLifter(Long, Model) update lifter} view.
     *
     * No validation of input data is carried out at present. This may lead to
     * incorrect/invalid data being stored within the system.
     *
     * @param lifter the lifter to update
     * @return       redirect to {@link #newLifter(Long, Model) new lifter} view
     */
    @RequestMapping(value="/lifter/save", method = RequestMethod.POST)
    public String saveUpdatedLifter(Lifter lifter) {
        Club club = clubService.findByName(lifter.getClubName());
        lifterService.saveLifter(lifter);

        return "redirect:/club/new-lifter?id=" + club.getId();
    }

}
