package com.ecse321.visart.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecse321.visart.model.Manager;
import com.ecse321.visart.model.User;
import com.ecse321.visart.repositories.EntityRepository;
import com.ecse321.visart.repositories.ManagerRepository;

@Service
public class ManagerService {
  /**
   *
   * @author danielbucci
   */
  @Autowired
  UserService userService;

  @Autowired
  ManagerRepository managerRepo;

  @Autowired
  EntityRepository entityRepo;

  @Autowired
  ArtListingService artListingService;

  @Transactional
  public Manager createManagerUnified(String aEmailAddress, String aDisplayname,
      String aUsername, String aPassword, String aProfilePicLink, String aProfileDescription) {

    User user = userService.createUser(aEmailAddress, aDisplayname, aUsername, aPassword,
        aProfilePicLink, aProfileDescription);
    return managerRepo.createManager(user.getIdCode());
  }

  /**
   *
   * @param  aEmailAddress
   * @param  aDisplayname
   * @param  aUsername
   * @param  aPassword
   * @param  aProfilePicLink
   * @param  aProfileDescription
   * @return
   */
  @Transactional
  public Manager createManager(String aEmailAddress, String aDisplayname,
      String aUsername, String aPassword, String aProfilePicLink, String aProfileDescription) {

    String aIdCode = EntityRepository.getUniqueKey();

    if (aEmailAddress == null || isValidEmail(aEmailAddress) == false) {
      throw new IllegalArgumentException("Email address is invalid");
    }

    if (aDisplayname == null || aDisplayname.length() < 5 || aDisplayname.length() > 25) {
      throw new IllegalArgumentException(
          "This Display Name is invalid, must be between 5 and 25 characters!");
    }

    List<User> l1 = entityRepo.findEntityByAttribute("displayname", User.class, aDisplayname);
    if (l1 != null && l1.size() > 0) {
      throw new IllegalArgumentException("This Display Name is already taken!");
    }

    if (aUsername == null || aUsername.length() < 5 || aUsername.length() > 25) {
      throw new IllegalArgumentException(
          "This User Name is invalid, must be between 5 and 25 characters!");
    }

    List<User> l2 = entityRepo.findEntityByAttribute("username", User.class, aUsername);
    if (l2 != null && l2.size() > 0) {
      throw new IllegalArgumentException("This Username is already taken!");
    }

    if (aPassword == null || aPassword.length() < 8 || aPassword.length() > 40) {
      throw new IllegalArgumentException("Password must be between 8 and 40 characters");
    }

    if (aProfileDescription == null || aProfileDescription.length() > 255) {
      throw new IllegalArgumentException("Description must be less than 255 characters");
    }

    return managerRepo.createManager(aIdCode, aEmailAddress, aDisplayname, aUsername, aPassword,
        aProfilePicLink, aProfileDescription);

  }

  /**
   * return manager given an id from db
   * 
   * @param  aIdCode
   * @return         Manager
   */

  // @Transactional
  // public Manager addManagerListing(String aIdCode, ArtListing artListing) {
  // Manager manager = managerRepo.getManager(aIdCode, true);
  // if(artListing != null) {
  // manager.addPromotedListing(artListing);
  // } else {
  // throw new IllegalArgumentException("Cannot add a null art listing");
  // }
  // return manager;
  // }

  @Transactional
  public Manager getManager(String aIdCode) {
    return managerRepo.getManager(aIdCode);
  }

  /**
   * return manager with the listings given an id from db
   * 
   * @param  aIdCode
   * @param  promotedListing
   * @return                 Manager
   */
  @Transactional
  public Manager getManager(String aIdCode, Boolean promotedListings) {
    return managerRepo.getManager(aIdCode, promotedListings);
  }

  /**
   * adds listing to a managers db
   * 
   * @param aIdCode
   * @param aListingCode
   */
  @Transactional
  public Manager addListing(String aIdCode, String aListingIdCode) {
    if (aIdCode == null || aIdCode.equals("")) {
      throw new IllegalArgumentException("Cannot find manager");
    }
    if (aListingIdCode == null || aListingIdCode.equals("")) {
      throw new IllegalArgumentException("Cannot find artListing");
    }
    Manager manager = getManager(aIdCode, true);
    manager.addPromotedListing(artListingService.getArtListing(aListingIdCode));
    managerRepo.updateManager(manager);
    return manager;
  }

  /**
   * removes a listing to a managers db
   * 
   * @param aIdCode
   * @param aListingCode
   * @return 
   */
  @Transactional
  public Manager removeListing(String aIdCode, String aListingIdCode) {
    if (aIdCode == null || aIdCode.equals("")) {
      throw new IllegalArgumentException("Cannot find manager");
    }
    if (aListingIdCode == null || aListingIdCode.equals("")) {
      throw new IllegalArgumentException("Cannot find artListing");
    }
    Manager manager = getManager(aIdCode, true);
    manager.removePromotedListing(artListingService.getArtListing(aListingIdCode));
    managerRepo.updateManager(manager);
    return manager;
  }

  /**
   * deletes a manager from a db
   * 
   * @param aIdCode
   */
  @Transactional
  public Boolean deleteManager(String aIdCode) {
    if (aIdCode != null && !aIdCode.contentEquals("")) {
      return managerRepo.deleteManager(aIdCode);
    } else {
      return false;
    }
  }

  /**
   * @return List<Manager> all the managers in the db
   */
  @Transactional
  public List<Manager> getAllManagers() {
    return entityRepo.getAllEntities(Manager.class);
  }

  /**
   * https://www.geeksforgeeks.org/check-email-address-valid-not-java/
   * checks if an email is valid
   * 
   * @param  String email
   * @return        Boolean isValid
   */
  // https://www.geeksforgeeks.org/check-email-address-valid-not-java/
  public static boolean isValidEmail(String email) {
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
        "[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
        "A-Z]{2,7}$";

    Pattern pat = Pattern.compile(emailRegex);
    if (email == null)
      return false;
    return pat.matcher(email).matches();
  }

}
