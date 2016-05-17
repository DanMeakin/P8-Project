package dk.aau.ida8.controller;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

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
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date today = cal.getTime();

        Iterable<Competition> competitions = competitionService.findAll();
        List<Competition> futureComps = new ArrayList<>();
        List<Competition> pastComps = new ArrayList<>();
        for (Competition c : competitions) {
            if (c.getCompetitionDate().before(today)) {
                pastComps.add(c);
            } else {
                futureComps.add(c);
            }
        }
        model.addAttribute("competitions", competitionService.findAll());
        model.addAttribute("pastCompetitions", pastComps);
        model.addAttribute("futureCompetitions", futureComps);
        return "index";
    }
}
