package org.hdcola.carnet.Controllers;

import jakarta.validation.Valid;
import org.hdcola.carnet.Entity.Car;
import org.hdcola.carnet.Repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CarController {

    @Autowired
    private CarRepository carRepository;

    @PostMapping("/car")
    public String car(@Valid Car car, BindingResult result, Model model, RedirectAttributes redirAttr) {

        if(result.hasErrors()) {
            model.addAttribute("Success", false);
            model.addAttribute("errors", result.getAllErrors());
            return "index";
        }
        carRepository.save(car);
        redirAttr.addFlashAttribute("Success", true);
        return "redirect:/";
    }
}
