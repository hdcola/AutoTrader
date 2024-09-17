package org.hdcola.carnet.Controllers;

import org.hdcola.carnet.Entity.Car;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FragmentsController {

    @GetMapping("/loadCarForm")
    public ModelAndView loadCarForm() {
        ModelAndView mv = new ModelAndView("fragments/addCar :: addCar");
        mv.addObject("car", new Car());
        return mv;
    }
}
