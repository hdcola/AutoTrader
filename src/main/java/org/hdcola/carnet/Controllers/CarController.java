package org.hdcola.carnet.Controllers;

import jakarta.validation.Valid;
import org.hdcola.carnet.Configs.CustomUserDetails;
import org.hdcola.carnet.Entity.Car;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Repository.CarRepository;
import org.hdcola.carnet.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/Seller")
    public String listCars(@AuthenticationPrincipal UserDetails userDetails, Model mv) {
        //mv.addObject("cars", carRepository.findByUser(userDetails.getUsername()));
        mv.addAttribute("car", new Car());
        List<String> fields = List.of("VIN", "make", "model", "year");
        mv.addAttribute("fields", fields);
        return "seller";
    }

    @PostMapping("/addCar")
    public String addCar(@Valid Car car, BindingResult result, Model model, RedirectAttributes redirAttr) {
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> System.out.println(error.toString()));
            model.addAttribute("Success", false);
            model.addAttribute("errors", result.getAllErrors());
            return "fragments/addCar";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            User author = userRepo.findById(userDetails.getId()).get();
            car.setUser(author);
            carRepository.save(car);
        }

        return "seller";
    }



}
