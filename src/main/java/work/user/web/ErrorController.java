package work.user.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

    @GetMapping("/access-denied")
    public ModelAndView showAccessDeniedPage() {
        return new ModelAndView("access-denied");
    }

}
