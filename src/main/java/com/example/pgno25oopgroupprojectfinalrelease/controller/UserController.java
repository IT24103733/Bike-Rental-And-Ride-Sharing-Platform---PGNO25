package com.example.oopprojectbeta.controller;

import com.example.oopprojectbeta.model.User;
import com.example.oopprojectbeta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/bikes";
        }
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password, 
                        HttpSession session,
                        Model model) {
        User user = userService.login(username, password);

        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/bikes";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/signup")
    public String showSignupForm() {
        return "redirect:/login?showSignup=true";
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        User registeredUser = userService.register(user);

        if (registeredUser != null) {
            return "redirect:/login?registered=true";
        } else {
            redirectAttributes.addFlashAttribute("error", "Registration failed");
            return "redirect:/login?showSignup=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // User Profile Management

    @GetMapping("/profile")
    public String viewProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "editProfile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Check if user can modify this profile
        if (!userService.canModifyUser(currentUser, updatedUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to update this profile");
            return "redirect:/profile";
        }

        // Preserve admin status - only admins can change admin status
        if (!currentUser.isAdmin()) {
            updatedUser.setAdmin(currentUser.isAdmin());
        }

        User updated = userService.updateUser(updatedUser);
        if (updated != null) {
            // Update session with new user data
            session.setAttribute("user", updated);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile");
        }

        return "redirect:/profile";
    }

    @GetMapping("/profile/delete")
    public String showDeleteProfileConfirmation(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "deleteProfile";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(@RequestParam int userId, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Check if user can delete this profile
        if (!userService.canModifyUser(currentUser, userId)) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to delete this profile");
            return "redirect:/profile";
        }

        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            // If user deleted their own account, log them out
            if (currentUser.getId() == userId) {
                session.invalidate();
                return "redirect:/login?accountDeleted=true";
            } else {
                redirectAttributes.addFlashAttribute("success", "User deleted successfully");
                return "redirect:/admin/users";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user");
            return "redirect:/profile";
        }
    }

    // Admin User Management

    @GetMapping("/admin/users")
    public String adminUsers(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "adminUsers";
    }

    @GetMapping("/admin/users/add")
    public String showAddUserForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        model.addAttribute("user", new User());
        return "addUser";
    }

    @PostMapping("/admin/users/add")
    public String addUser(@ModelAttribute User newUser, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.isAdmin()) {
            return "redirect:/bikes";
        }

        User created = userService.createUser(newUser);
        if (created != null) {
            redirectAttributes.addFlashAttribute("success", "User created successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to create user");
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String showEditUserForm(@PathVariable int id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.isAdmin()) {
            return "redirect:/bikes";
        }

        User userToEdit = userService.getUserById(id);
        if (userToEdit == null) {
            return "redirect:/admin/users";
        }

        model.addAttribute("user", userToEdit);
        return "editUser";
    }

    @PostMapping("/admin/users/edit/{id}")
    public String editUser(@PathVariable int id, @ModelAttribute User updatedUser, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.isAdmin()) {
            return "redirect:/bikes";
        }

        // Ensure ID matches path variable
        updatedUser.setId(id);

        User updated = userService.updateUser(updatedUser);
        if (updated != null) {
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update user");
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.isAdmin()) {
            return "redirect:/bikes";
        }

        // Prevent admin from deleting themselves
        if (currentUser.getId() == id) {
            redirectAttributes.addFlashAttribute("error", "You cannot delete your own admin account");
            return "redirect:/admin/users";
        }

        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user");
        }

        return "redirect:/admin/users";
    }
}
