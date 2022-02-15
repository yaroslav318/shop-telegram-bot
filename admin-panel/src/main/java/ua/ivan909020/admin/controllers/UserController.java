package ua.ivan909020.admin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.ivan909020.admin.models.entities.User;
import ua.ivan909020.admin.models.entities.UserRole;
import ua.ivan909020.admin.services.UserService;
import ua.ivan909020.admin.utils.ControllerUtils;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "main/users/all";
    }

    @GetMapping("/add")
    public String showAddUser(Model model) {
        model.addAttribute("roles", UserRole.values());
        return "main/users/add";
    }

    @GetMapping("/edit/{user}")
    public String showEditUser(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        return "main/users/edit";
    }

    @PostMapping("/create")
    public String createUser(@Valid User user, BindingResult bindingResult, Model model) {
        model.addAttribute("roles", UserRole.values());
        if (bindingResult.hasErrors()) {
            model.mergeAttributes(ControllerUtils.findErrors(bindingResult));
            model.addAttribute("user", user);
            return "main/users/add";
        }
        if (userService.loadUserByUsername(user.getUsername()) != null) {
            model.addAttribute("usernameError", "This username is already in use");
            return "main/users/add";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        userService.save(user);
        return "redirect:/users";
    }

    @PostMapping("/update")
    public String updateUser(
            @AuthenticationPrincipal User authUser,
            @Valid User user,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.mergeAttributes(ControllerUtils.findErrors(bindingResult));
            model.addAttribute("user", user);
            return "main/users/edit";
        }
        if (authUser.getId().equals(user.getId()) && !authUser.getRole().equals(user.getRole())) {
            model.addAttribute("Error", "You cannot change the role for your account!");
            return "main/users/edit";
        }

        User receivedUser = userService.findById(user.getId());
        receivedUser.setName(user.getName());
        receivedUser.setUsername(user.getUsername());
        if (!user.getPassword().isEmpty()) {
            receivedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        receivedUser.setRole(user.getRole());
        userService.update(receivedUser);
        return "redirect:/users/edit/" + user.getId();
    }

    @PostMapping("/delete")
    public String deleteUser(@AuthenticationPrincipal User user, @RequestParam Integer id, Model model) {
        if (user.getId().equals(id)) {
            model.addAttribute("user", user);
            model.addAttribute("Error", "You cannot delete your account!");
            return "main/users/edit";
        }

        userService.deleteById(id);
        return "redirect:/users";
    }

}
