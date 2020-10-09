package com.ecse321.visart.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ecse321.visart.model.Artist;
import com.ecse321.visart.repositories.ArtistRepository;
@TestMethodOrder(OrderAnnotation.class) 
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ArtistTest {

  @Autowired
  private ArtistRepository aRepo;
  String aEmailAddress;
  String aDisplayname;
  String aUsername;
  String aPassword;
  Long l = System.currentTimeMillis();

  @Before
  void init() {

    aEmailAddress = "timcook@gmail.com";
    aDisplayname = "Tim Cook";
    aUsername = "timcook56";
    aPassword = "apple123";

  }

  @Test
  @Order(1)
  void testEntry() {

    init();
    
    // Create
    Artist testArtist = aRepo.createArtist("12334556", aEmailAddress, aDisplayname, aUsername,
        aPassword);

    // Test if Artist was created
    assertNotNull(testArtist);
    // Print Artist
    System.out.println("============================");
    System.out.println(testArtist);
    System.out.println("============================");

  }
  
  @Test
  @Order(2)
  void testGet() {
	  
	  init();
	 
	 //Getting
    Artist testArtist2 = aRepo.getArtist("12334556");
    
    //Test if got Artist corresponds to entry
    assertNotNull(testArtist2);
    assertEquals(aEmailAddress, testArtist2.getCustomer().getUser().getEmailAddress());
    System.out.println("============================");
    System.out.println(testArtist2);
    System.out.println("============================");
  }
  
  
  

}
