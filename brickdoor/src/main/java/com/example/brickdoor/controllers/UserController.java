package com.example.brickdoor.controllers;

import com.example.brickdoor.daos.ReviewDao;
import com.example.brickdoor.daos.UserDao;
import com.example.brickdoor.models.Admin;
import com.example.brickdoor.models.Company;
import com.example.brickdoor.models.InterviewReview;
import com.example.brickdoor.models.Review;
import com.example.brickdoor.models.Role;
import com.example.brickdoor.models.Student;
import com.example.brickdoor.models.User;
import com.example.brickdoor.models.WorkReview;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller()
public class UserController {

  @Autowired
  private UserDao userDao;

  @Autowired
  private ReviewDao reviewDao;

  // This the get route, do not edit this.
  @GetMapping("/login")
  public ModelAndView loginRouteGet(HttpSession session) {
    Student user = session.getAttribute("user") == null ? new Student() : (Student) session.getAttribute("user");
    if (user.getId() != 0) {
      return new ModelAndView("redirect:/login");
    }
    ModelAndView model = new ModelAndView("login");
    model.addObject("user", user);
    return model;
  }

  // Post route for login, handle user authentication here
  @PostMapping("/login")
  public ModelAndView loginRoutePost(HttpSession session, @ModelAttribute("user") User user) {
    String username = user.getUsername();
    String password = user.getPassword();
    User authenticatedUser = userDao.authenticate(username, password);
    if (authenticatedUser == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    session.setAttribute("user", authenticatedUser);
    return new ModelAndView("redirect:/");
  }

  @GetMapping("/logout")
  public ModelAndView logoutRouteGet(HttpSession session) {
    session.invalidate();
    return new ModelAndView("redirect:/");
  }

  // This the get route, do not edit this.
  @GetMapping("/register")
  public ModelAndView registerRouteGet(HttpSession session) {
    Student user = session.getAttribute("user") == null ? new Student() : (Student) session.getAttribute("user");
    if (user.getId() != 0) {
      return new ModelAndView("redirect:/login");
    }
    ModelAndView model = new ModelAndView("register");
    model.addObject("user", user);
    return model;
  }

  // Post route for login, handle user authentication here
  @PostMapping("/registerStudent")
  public ModelAndView registerStudentPost(@ModelAttribute("student") Student student) {
    if (student == null || student.getUsername() == null || student.getPassword() == null || student.getEmail() == null) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Missing Register Credentials");
    }
    if (!userDao.registerUser(student)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }
    return new ModelAndView("redirect:/login");
  }

  // Post route for login, handle user authentication here
  @PostMapping("/registerCompany")
  public String registerCompanyPost(@ModelAttribute("company") Company company) {
    if (company == null || company.getUsername() == null || company.getPassword() == null || company.getEmail() == null) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Missing Register Credentials");
    }
    if (!userDao.registerUser(company)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }
    return "registered company";
  }

  @PostMapping("/registerAdmin")
  public String registerAdminPost(@ModelAttribute("admin") Admin admin) {
    if (admin == null || admin.getUsername() == null || admin.getPassword() == null || admin.getEmail() == null) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Missing Register Credentials");
    }
    if (!userDao.registerUser(admin)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }
    return "registered admin";
  }

  @PutMapping("/updateStudent")
  public String updateStudent(HttpSession session, @ModelAttribute("student") Student student) {
    User user = (User) session.getAttribute("user");
    int userId = user.getId();
    Role userRole = userDao.getRole(userId);
    boolean permissionRoles = userRole == Role.STUDENT || userRole == Role.ADMIN;
    if (userId != student.getId() || !permissionRoles) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    User updateUser = userDao.updateStudent(userId, student);
    if (updateUser == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return "updated student";
  }

  @PutMapping("/updateCompany")
  public String updateCompany(HttpSession session, @ModelAttribute("company") Company company) {
    User user = (User) session.getAttribute("user");
    int userId = user.getId();
    Role userRole = userDao.getRole(userId);
    boolean permissionRoles = userRole == Role.COMPANY || userRole == Role.ADMIN;
    if (userId != company.getId() || !permissionRoles) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    User updateUser = userDao.updateCompany(userId, company);
    if (updateUser == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return "updated company";
  }

  @PutMapping("/updateAdmin")
  public String updateAdmin(HttpSession session, @ModelAttribute("admin") Admin admin) {
    User user = (User) session.getAttribute("user");
    int userId = user.getId();
    Role userRole = userDao.getRole(userId);
    boolean permissionRoles = userRole == Role.ADMIN;
    if (userId != admin.getId() || !permissionRoles) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    User updateUser = userDao.updateAdmin(userId, admin);

    if (updateUser == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return "updated admin";
  }

  @DeleteMapping("/deleteUser")
  public String deleteUser(HttpSession session, @ModelAttribute("user") User toDelete) {
    User user = (User) session.getAttribute("user");
    int userId = user.getId();
    if (userDao.getRole(userId) != Role.ADMIN) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    if (!userDao.deleteUser(toDelete.getId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return "deleted user with username: " + toDelete.getUsername();
  }

  @GetMapping("/getAllCompanies")
  public List<Company> getAllCompanies() {
    return userDao.getAllCompanies();
  }

  @GetMapping("/getAllStudents")
  public List<Student> getAllStudents() {
    return userDao.getAllStudents();
  }

  @GetMapping("/getAllAdmin")
  public List<Admin> getAllAdmin() {
    return userDao.getAllAdmin();
  }

  @PostMapping("/search")
  public ModelAndView searchCompanies(HttpSession session, @ModelAttribute("search") SearchObject search) {
    Student user = session.getAttribute("user") == null ? new Student() : (Student) session.getAttribute("user");
    Set<Company> matchedCompanies = userDao.searchCompanies(search.getQuery());
    System.out.println(matchedCompanies.size());
    ModelAndView model = new ModelAndView("search");
    model.addObject("user", user);
    model.addObject("query", search.getQuery());
    model.addObject("results", matchedCompanies);
    return model;
  }

  @PostMapping("/searchStudents")
  public ModelAndView searchStudents(HttpSession session, @ModelAttribute("search") SearchObject search) {
    Student user = session.getAttribute("user") == null ? new Student() : (Student) session.getAttribute("user");
    if (user.getId() == 0) {
      return new ModelAndView("redirect:/login");
    }
    Set<Student> matchedStudents = userDao.searchStudents(search.getQuery());

    ModelAndView model = new ModelAndView("search");
    model.addObject("user", user);
    model.addObject("query", search.getQuery());
    model.addObject("queryResult", matchedStudents);
    return model;
  }

  @PostMapping("/searchAdmin")
  public ModelAndView searchAdmin(HttpSession session, @ModelAttribute("search") SearchObject search) {
    Student user = session.getAttribute("user") == null ? new Student() : (Student) session.getAttribute("user");
    if (user.getId() == 0) {
      return new ModelAndView("redirect:/login");
    }
    Set<Admin> matchedAdmin = userDao.searchAdmin(search.getQuery());

    ModelAndView model = new ModelAndView("search");
    model.addObject("user", user);
    model.addObject("query", search.getQuery());
    model.addObject("queryResult", matchedAdmin);
    return model;
  }

  @GetMapping("/profile")
  public ModelAndView userProfile(HttpSession session) {
    Student user = session.getAttribute("user") == null ? new Student() : (Student) session.getAttribute("user");
    if (user.getId() == 0) {
      return new ModelAndView("redirect:/login");
    }

    List<InterviewReview> interviews = reviewDao.findInterviewReviewsByStudentId(user.getId());
    List<WorkReview> workReviews = reviewDao.findWorkReviewsReviewByStudentId(user.getId());

    ModelAndView model = new ModelAndView("profile");
    model.addObject("user", user);
    model.addObject("interviews", interviews);
    model.addObject("works", workReviews);
    return model;
  }

  @GetMapping("/user/{userId}")
  public ModelAndView userProfileGET(HttpSession session, @PathVariable("userId") Integer userId) {
    Student user = session.getAttribute("user") == null ? new Student() : (Student) session.getAttribute("user");
    Student person = userDao.findStudentById(userId);
    if (person == null) {
      return new ModelAndView("redirect:/");
    }
    List<InterviewReview> interviews = reviewDao.findInterviewReviewsByStudentId(userId);
    List<WorkReview> workReviews = reviewDao.findWorkReviewsReviewByStudentId(userId);

    ModelAndView model = new ModelAndView("user");
    model.addObject("user", user);
    model.addObject("person", person);
    model.addObject("interviews", interviews);
    model.addObject("works", workReviews);
    return model;
  }
}

// Used to store the search query from the search bar.
class SearchObject {
  private String query;

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }
}
