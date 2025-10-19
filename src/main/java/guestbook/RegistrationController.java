package guestbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

/**
 * handles the incoming webrequests and sends them off to our backend.
 * Translates from json to "java variables", handling any form errors and similar stuff
 */

@Controller
public class RegistrationController {

    private static final String REGISTER_HTML ="register";

    /**
     * we need access to the existing users, thus wire in the user Repository
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * we want to encrypt the passwords, wire in the used Password encoder.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * When /register is queried, return the corresponding html site
     * @param model
     * @return the thyme-leaf html site
     */
    @GetMapping("/register")
    public String register(Model model){
        /**
         * https://stackoverflow.com/questions/51143616/explanation-of-the-model-object-in-spring
         * Model-View-Controller pattern.
         * We add the model of the registration form to our website model and 
         * return the html for the register site.
         * 
         * The addition of this form to our model lets the register html form be linked / parsable
         * to our java register form.
         */
        model.addAttribute("registrationForm", new RegistrationForm());
        return REGISTER_HTML;
    }

    /**
     * called when someone tries to register
     * @valid checks that none of the registration form fields are invalid
     * https://www.baeldung.com/spring-valid-vs-validated
     * siehe auch videoshop:
     * > // (｡◕‿◕｡)
     * > // Über @Valid können wir die Eingaben automagisch prüfen lassen, ob es Fehler gab steht im BindingResult,
     * > // dies muss direkt nach dem @Valid Parameter folgen.
     * > // Siehe außerdem videoshop.model.validation.RegistrationForm
     * > // Lektüre: http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html
     * @param form
     * @param bindResult
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid RegistrationForm form, BindingResult bindResult){
        if(bindResult.hasErrors()){
            return REGISTER_HTML;
        }

        /**
         * check if the user already exists
         */
        if(userRepository.findByUsername(form.getUsername()) != null){
            bindResult.rejectValue("username", "error.user", "Username is already taken");
            return REGISTER_HTML;
        }

        // create new user, with the encoded password, we do NOT want to store the real one.
        User user = new User(form.getUsername(), passwordEncoder.encode(form.getPassword()), "USER");

        // and store it in our database
        userRepository.save(user);

        // then redirect to the login page
        return "redirect:/login";
    }
}
