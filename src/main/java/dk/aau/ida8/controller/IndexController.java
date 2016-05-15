package dk.aau.ida8.controller;

import dk.aau.ida8.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This class is the controller for the index page of the software.
 */
@Controller
public class IndexController {

    private CompetitionService competitionService;

    @Autowired
    public IndexController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    /**
     * Display the index page view.
     *
     * @param model the Spring model to pass to the view
     * @return      the index page view
     */
    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("competitions", competitionService.findAll());
        return "index";
    }
}
