package org.hdcola.carnet.Controllers;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import jakarta.validation.Valid;
import org.hdcola.carnet.Configs.CustomUserDetails;
import org.hdcola.carnet.DTO.DecodedVinDTO;
import org.hdcola.carnet.Entity.Car;
import org.hdcola.carnet.Entity.User;
import org.hdcola.carnet.Repository.CarRepository;
import org.hdcola.carnet.Repository.UserRepository;
import org.hdcola.carnet.Service.VinDecodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CarController {

    @Autowired
    private CarRepository carRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private VinDecodeService vinDecodeService;

    @GetMapping("/Seller")
    public String listCars(Model mv) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            List<Car> carList= carRepo.findByUser(userDetails.getUsername());
            mv.addAttribute("carList", carList);
            mv.addAttribute("Login", true);
        } else {
            mv.addAttribute("Login", false);
        }
        return "seller";
    }

    @PostMapping("/addCar")
    public HtmxResponse addCar(@Valid Car newCar, BindingResult result, Model model, RedirectAttributes redirAttr) {
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> System.out.println(error.toString()));
            model.addAttribute("Success", false);
            model.addAttribute("errors", result.getAllErrors());
            return HtmxResponse.builder()
                    .view("fragments/addCar :: addCar")
                    .build();
        }

        if (carRepo.findByVIN(newCar.getVIN()) != null) {
            result.rejectValue("VIN", "VIN.exists", "VIN is already in use");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            User author = userRepo.findById(userDetails.getId()).get();
            DecodedVinDTO dto = vinDecodeService.decodeVin(newCar.getVIN());

            if (dto != null) {
                newCar.setMake(dto.getMake());
                newCar.setModel(dto.getModel());
                newCar.setYear(dto.getYear());
            }

            newCar.setUser(author);
            carRepo.save(newCar);
        }

        model.addAttribute("successMessage", "Car added successfully!");
        model.addAttribute("car", newCar);
        return HtmxResponse.builder()
                .view("fragments/addCar :: addCar")
                .build();
    }

    @PostMapping("/decodeVin")
    public HtmxResponse decodeVin(@RequestParam("VIN") String vin, Model model){
        if (carRepo.findByVIN(vin) != null) {
            model.addAttribute("message", "Car with this VIN already exists");
            return HtmxResponse.builder()
                    .view("fragments/addCar :: addCar")
                    .build();
        }

        String message="";
        if (vin == null || vin.isEmpty()) {
            message = "VIN is empty";
        } else {
            message = vinDecodeService.decodeVin(vin).toString();
        }
        model.addAttribute("message", message);
        return HtmxResponse.builder()
                .view("fragments/addCar :: decodeVin")
                .build();
    }

    @GetMapping("/loadCarForm")
    public HtmxResponse loadCarForm(Model model) {
        model.addAttribute("car", new Car());
        return HtmxResponse.builder()
                .view("fragments/addCar :: addCar")
                .build();
    }

    @GetMapping("/loadCarForm/{carId}")
    public HtmxResponse updateCarForm(@PathVariable Long carId, Model mv) {
        Car car = carRepo.findById(carId).get();
        mv.addAttribute("car", car);
        mv.addAttribute("carLoad", true);
        return HtmxResponse.builder()
                .view("fragments/addCar :: addCar")
                .build();
    }


}
