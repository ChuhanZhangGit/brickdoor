package com.example.brickdoor.daos;

import com.example.brickdoor.models.Admin;
import com.example.brickdoor.models.Company;
import com.example.brickdoor.models.Role;
import com.example.brickdoor.models.Student;
import com.example.brickdoor.models.User;
import com.example.brickdoor.repositories.AdminRepository;
import com.example.brickdoor.repositories.CompanyRepository;
import com.example.brickdoor.repositories.StudentRepository;
import com.example.brickdoor.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDao {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private AdminRepository adminRepository;

  public User findById(int userId) {
    return userRepository.findById(userId).orElse(null);
  }

  public Student findStudentById(int studentId) {
    return studentRepository.findStudentById(studentId, Role.STUDENT);
  }

  public Company findCompanyById(int companyId) {
    return companyRepository.findCompanyById(companyId);
  }

  public User authenticate(String username, String password) {
    return userRepository.findUserByUserCredentials(username, password);
  }

  public boolean registerUser(User user) {
    if (userRepository.findUserByUsername(user.getUsername()) == null
            && userRepository.findUserByEmail(user.getEmail()) == null) {
      userRepository.save(user);
      return true;
    }
    return false;
  }

  public User updateStudent(int userId, Student updatedStudent) {
    Student outdatedStudent = studentRepository.findStudentById(userId,Role.STUDENT);
    if (outdatedStudent != null) {
      updateBasicUserCred(outdatedStudent, updatedStudent);
      outdatedStudent.setFirstName(updatedStudent.getFirstName());
      outdatedStudent.setLastName(updatedStudent.getLastName());
      userRepository.save(outdatedStudent);
      return outdatedStudent;
    }
    return null;
  }

  public User updateCompany(int userId, Company updatedCompany) {
    Company outdatedCompany = companyRepository.findCompanyById(userId);
    if (outdatedCompany != null) {
      updateBasicUserCred(outdatedCompany, updatedCompany);
      outdatedCompany.setCompanyAddress(updatedCompany.getCompanyAddress());
      outdatedCompany.setCompanyName(updatedCompany.getCompanyName());
      userRepository.save(outdatedCompany);
      return outdatedCompany;
    }
    return null;
  }

  public User updateAdmin(int userId, Admin updatedAdmin) {
    Admin outdatedAdmin = adminRepository.findAdminById(userId);
    if (outdatedAdmin != null) {
      updateBasicUserCred(outdatedAdmin, updatedAdmin);
      outdatedAdmin.setFirstName(updatedAdmin.getFirstName());
      outdatedAdmin.setLastName(updatedAdmin.getLastName());
      userRepository.save(outdatedAdmin);
      return outdatedAdmin;
    }
    return null;
  }

  private void updateBasicUserCred(User outdatedUser, User updatedUser) {
    outdatedUser.setEmail(updatedUser.getEmail());
    outdatedUser.setPassword(updatedUser.getPassword());
    outdatedUser.setDob(updatedUser.getDob());
    outdatedUser.setPhoneNumbers(updatedUser.getPhoneNumbers());
  }

  public boolean deleteUser(int userId) {
    if (!userRepository.existsById((userId))) {
      return false;
    }
    userRepository.deleteById(userId);
    return true;
  }

  public Role getRole(int userId) {
    Optional<User> optionalUser = userRepository.findById(userId);
    return optionalUser.map(User::getRole).orElse(null);
  }

  public List<Student> getAllStudents() {
    return studentRepository.getAllStudents(Role.STUDENT);
  }

  public List<Company> getAllCompanies() {
    return companyRepository.getAllCompanies(Role.COMPANY);
  }

  public List<Admin> getAllAdmin() {
    return adminRepository.getAllAdmin(Role.ADMIN);
  }

  public Set<Student> searchStudents(String query) {
    return studentRepository.searchStudents(query, Role.STUDENT);
  }

  public Set<Company> searchCompanies(String query) {
    return companyRepository.searchCompanies(query, Role.COMPANY);
  }

  public Set<Admin> searchAdmin(String query) {
    return adminRepository.searchAdmin(query, Role.ADMIN);
  }
}

