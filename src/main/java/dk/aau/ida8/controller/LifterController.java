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
 * Controller class for the lifter-class.
 * Maps CRUD-ability for the class.
 */
@Controller
public class LifterController {

    private LifterService lifterService;

    @Autowired
    public LifterController(LifterService lifterService) {
        this.lifterService = lifterService;
    }

    @RequestMapping("/lifter/{id}")
    public String lifter(@PathVariable Long id, Model model) {
        model.addAttribute("lifter", lifterService.findOne(id));
        model.addAttribute("cleanJerks", lifterService.findOne(id).getCleanJerks());
        return "lifter-view";
    }

    @RequestMapping(value="/lifter/update", method = RequestMethod.POST)
    public String updateLifter(Lifter lifter) {
        lifterService.saveLifter(lifter);
        return "redirect:/competition";
    }
}
