package org.hdcola.carnet.Controllers;

import jakarta.validation.Valid;
import org.hdcola.carnet.Entity.Car;
import org.hdcola.carnet.Repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CarController {

    @Autowired
    private CarRepository carRepository;

    @GetMapping("/Seller")
    public ModelAndView listCars(@AuthenticationPrincipal UserDetails userDetails) {
        ModelAndView mv = new ModelAndView("seller");
        //mv.addObject("cars", carRepository.findByUser(userDetails.getUsername()));
        mv.addObject("car", new Car());
        List<String> fields = List.of("VIN", "make", "model", "year");
        mv.addObject("fields", fields);
        return mv;
    }

    @PostMapping("/addCar")
    public String addCar(@Valid Car car, BindingResult result, Model model, RedirectAttributes redirAttr) {
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> System.out.println(error.toString()));
            model.addAttribute("Success", false);
            model.addAttribute("errors", result.getAllErrors());
            return "seller";
        }
        return "seller";
    }

    @GetMapping("/addCarForm")
    public String addCarForm(Model model) {
        model.addAttribute("car", new Car());
        return "fragments/addCar.html :: addCar";
    }
}
