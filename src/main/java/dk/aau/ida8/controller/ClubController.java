package dk.aau.ida8.controller;

import dk.aau.ida8.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ClubController {

    private ClubService clubService;

    @Autowired
    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @RequestMapping("/club/new-lifter")
    public String newLifter() {
        return "club-lifters";
    }

}
