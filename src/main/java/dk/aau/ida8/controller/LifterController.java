package dk.aau.ida8.controller;

import dk.aau.ida8.model.Lifter;
import dk.aau.ida8.service.LiftService;
import dk.aau.ida8.service.LifterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This class is the controller for all lifter-centric activities within this
 * software.
 *
 * The LifterController exposes routes relating to the viewing and updating of
 * lifters. Creation of lifters is done within the {@link ClubController} as
 * each lifter is created with a club association.
 */
@Controller
public class LifterController {

    private LifterService lifterService;

    /**
     * Instantiates a LifterController.
     *
     * Uses the @Autowired annotation to automatically create instances of the
     * required services. This allows ORM access as required without manually
     * creating these services.
     *
     * @param lifterService the service used to access Lifter data
     */
    @Autowired
    public LifterController(LifterService lifterService) {
        this.lifterService = lifterService;
    }

    /**
     * Displays the lifter view to view a lifter's details.
     *
     * @param id    the ID# of the lifter to view
     * @param model the Spring model object to pass to the view
     * @return      the view lifter view
     */
    @RequestMapping("/lifter/{id}")
    public String lifter(@PathVariable Long id, Model model) {
        model.addAttribute("lifter", lifterService.findOne(id));
        return "lifter-view";
    }

    /**
     * Updates a lifter's details.
     *
     * @param lifter the lifter to update
     * @return       redirect to a non-existent endpoint
     */
    @RequestMapping(value="/lifter/update", method = RequestMethod.POST)
    public String updateLifter(Lifter lifter) {
        lifterService.saveLifter(lifter);
        return "redirect:/competition";
    }
}
