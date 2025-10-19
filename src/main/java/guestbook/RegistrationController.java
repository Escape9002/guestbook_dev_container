package guestbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid RegistrationForm form, BindingResult bindResult){
        if(bindResult.hasErrors()){
            return "register";
        }

        if(userRepository.findByUsername(form.getUsername()) != null){
            bindResult.rejectValue("username", "error.user", "Username is already taken");
            return "register";
        }

        User user = new User(form.getUsername(), passwordEncoder.encode(form.getPassword()), "USER");

        userRepository.save(user);

        return "redirect:/login";
    }
}
