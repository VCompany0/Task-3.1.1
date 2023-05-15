package app.web.controller;

import app.dao.UserDaoWithEntityManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import app.user.User;
import app.user.UserService;
import app.user.UserServiceImplementation;

import java.util.List;

@Controller
public class MainController {
    private static final UserService service = new UserServiceImplementation(new UserDaoWithEntityManager());

    @GetMapping(value = {"/users/list", "/index", "/"})
    public static String viewMainPage(ModelMap model) {
        service.createUsersTable();
        List<User> userList = service.getAllUsers();
        model.addAttribute("users", userList);

        return "users";
    }

    @GetMapping("/addUser")
    public String addUser(Model model) {
        // create model attribute to bind form data
        User user = new User();
        model.addAttribute("user", user);
        return "add_user";
    }

    @PostMapping("/saveUser/{id}")
    public String saveUser(@PathVariable(value = "id") long id, @ModelAttribute("user") User user) {
        // save employee to database
        //service.saveUser(user);
        User updatedUser = service.findUser(id);
        updatedUser.setName(user.getName());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setAge(user.getAge());

        return "redirect:/";
    }

    @PostMapping("/saveUser")
    public String saveUser( @ModelAttribute("user") User user) {
        service.saveUser(user);
        return "redirect:/";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable(value = "id") long id, Model model) {

        // get employee from the service
        User user = service.findUser(id);

        // set employee as a model attribute to pre-populate the form
        model.addAttribute("user", user);
        return "update_user";
    }

    @GetMapping(value = "/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        service.removeUserById(id);
        return "redirect:/";
    }

}
