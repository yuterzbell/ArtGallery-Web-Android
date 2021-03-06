package com.ecse321.visart.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ecse321.visart.model.ArtListing;
import com.ecse321.visart.model.Artist;
import com.ecse321.visart.model.Customer;
import com.ecse321.visart.model.Tag;
import com.ecse321.visart.model.User;
import com.ecse321.visart.model.ArtListing.PostVisibility;
import com.ecse321.visart.model.ArtPiece;
import com.ecse321.visart.repositories.ArtListingRepository;
import com.ecse321.visart.repositories.EntityRepository;
import com.ecse321.visart.service.ArtListingService;
import com.ecse321.visart.service.ArtPieceService;
import com.ecse321.visart.service.TagService;

@ExtendWith(MockitoExtension.class)
public class TestArtListingService {

  @Mock
  private ArtListingRepository alRepo;

  @Mock
  private EntityRepository entityRepo;
  
  @Mock
  private ArtPieceService serviceAp;

  @Mock
  private TagService serviceT;
  
  @InjectMocks
  private ArtListingService serviceAl;

  private static final String AL_KEY = "MockTestAL";
  private static ArtListing artListingTest = null;
  private static final PostVisibility postVisibility = PostVisibility.Public;
  private static final String aDescription = "description";
  private static final String aTitle = "title";
  private static final User aUser = new User("a", "b", "c", "d", "e", "f", "g");
  private static final Customer aCustomer = new Customer("customerCode", aUser);
  private static final Artist aArtist = new Artist("artistCode", aCustomer);

  @BeforeEach
  public void setMockOutput() {
    // Mock the Repository methods, returning what we want to expect from the
    // database, instead of actually querying the database.

    
    lenient().when(serviceAp.getArtPiece(anyString())).thenAnswer(
        (InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals("1")) {
            ArtPiece artPiece1 = new ArtPiece();
            return artPiece1;
          } else {

            return null;
          }
        });
    
    lenient().when(serviceT.getTag(anyString())).thenAnswer(
        (InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals("1")) {
            Tag tag1 = new Tag();
            return tag1;
          } else {

            return null;
          }
        });
    
    lenient().when(alRepo.getArtListing(anyString())).thenAnswer(
        (InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(AL_KEY)) {
            ArtListing artListing1 = new ArtListing(0.0,postVisibility, aDescription, aTitle,
                AL_KEY, aArtist);
            return artListing1;
          } else {

            return null;
          }
        });

    lenient().when(alRepo.createArtListing(any(),Mockito.any(PostVisibility.class), anyString(),
        anyString(), anyString(), Mockito.any(Artist.class)))
        .thenAnswer((InvocationOnMock invocation) -> {
          PostVisibility postVisibility = invocation.getArgument(1);
          String description = invocation.getArgument(2);
          String title = invocation.getArgument(3);
          String id = invocation.getArgument(4);
          Artist artist = invocation.getArgument(5);
          ArtListing artListing2 = new ArtListing(0.0,postVisibility, description, title, id,
              artist);
          ;
          return artListing2;
        });

    lenient().doAnswer((InvocationOnMock invocation) -> {
      artListingTest = invocation.getArgument(0);
      return artListingTest;
    }).when(alRepo).updateArtListing(any());

    lenient().when(alRepo.deleteArtListing(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(AL_KEY)) {
            return true;
          } else {
            return false;
          }
        });

    // In the tests below, test the Service class's DATA VALIDATION on the
    // parameters that are given to it.
  }

  @Test
  public void testCreateArtListing() {

    ArtListing artListing = null;
    PostVisibility postVisibility = PostVisibility.Public;
    String description = "123";
    String title = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);

    try {
      artListing = serviceAl.createArtListing(0.0,postVisibility, description,
          title, aArtist);
    } catch (IllegalArgumentException e) {

      fail();
    }

    assertNotNull(artListing);

  }

  @Test
  public void testCreateArtListingNullVisibility() {
    PostVisibility Postvisibility = null;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(artListing);
    assertEquals("Post Visibility cannot be empty!", error); // expected error message for service
                                                             // data
    // validation.
  }

  @Test
  public void testCreateArtListingNullDescription() {

    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(artListing);
    assertEquals("Description cannot be empty!", error); // expected error message for service
                                                         // data
    // validation.
  }

  @Test
  public void testCreateArtListingNullTitle() {

    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(artListing);
    assertEquals("Title cannot be empty!", error); // expected error message for service
                                                   // data
    // validation. // data
  } // validation.

  @Test
  public void testCreateArtListingBadTracker() {

    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    Artist aArtist = null;
    ArtListing artListing = null;
    String error = null;

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(artListing);
    assertEquals("Artist cannot be empty!", error); // expected error message for service
                                                    // data
    // validation. // service data
  }

  @Test
  public void testGetListing() {
    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing2 = null;

    try {
      serviceAl.createArtListing(0.0, Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      fail();
    } // data
    try {
      artListing2 = serviceAl.getArtListing(AL_KEY);
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    }

    assertNotNull(artListing2);

  }

  @Test
  public void testGetNonExistingArtListing() {
    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;
    String idFake = "fake";

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(error);
    // data
    // validation.

    try {
      artListing = serviceAl.getArtListing(idFake);
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    }

    assertNull(artListing);

  }
  
  @Test
  public void testUpdateArtListing() {
    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;
    String newTitle = "new";
    String newDescription = "newdesc";
    PostVisibility newPostvisibility = PostVisibility.Private;

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(error);
    // data
    // validation.

    try {
      artListing = serviceAl.updateArtListing(AL_KEY, 0.0,newPostvisibility, newDescription, newTitle);
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    }

    assertEquals(artListing.getDescription(), newDescription);
    assertEquals(artListing.getTitle(), newTitle);
    assertEquals(artListing.getVisibility(), newPostvisibility);

  }
  
  @Test
  public void testDeleteArtListing() {
    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;
    Boolean success = false;

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(error);
    assertNotNull(artListing);
    // data
    // validation.

    try {
      success = serviceAl.deleteArtListing(AL_KEY);
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    }
    

    assertEquals(success, true);

  }
  
  @Test
  public void testUpdateDimensions() {
    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;
    Float[] dimensions = {(float)10,(float) 10,(float)10};

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(error);
    assertNotNull(artListing);
    // data
    // validation.

    try {
      artListing = serviceAl.updateDimensions(AL_KEY, dimensions);
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    }
    

    assertEquals(artListing.getDimension(0), dimensions[0]);

  }
  
  @Test
  public void testUpdatePostImages() {
    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;
    String[] postimages = {"1","2","3"};

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(error);
    assertNotNull(artListing);
    // data
    // validation.

    try {
      artListing = serviceAl.updatePostImages(AL_KEY, postimages);
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    }
    

    assertEquals(artListing.getPostingPicLink(0), postimages[0]);

  }


  @Test
  public void testGetUnsoldArtWorks() {
    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;
    List<ArtListing> unsold = new ArrayList<ArtListing>();
    
    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(error);
    assertNotNull(artListing);
    // data
    // validation.

    try {
      unsold = serviceAl.getUnsoldArtworks();
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    }
    

    assertEquals(unsold.size(), 0);

  }
  
  @Test
  public void testGetFavoritedCustomers() {
    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;
    List<Customer> customers = new ArrayList<Customer>();
    
    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(error);
    assertNotNull(artListing);
    // data
    // validation.

    try {
      customers = serviceAl.getFavoritedCustomers(AL_KEY);
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    }
    

    assertEquals(customers.size(), 0);

  }
  
  @Test
  public void testAddArtPiece() {
    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;
    ArtPiece artPiece = null;

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(error);
    assertNull(artPiece);
    assertNotNull(artListing);
    // data
    // validation.

    try {
      artPiece = serviceAp.getArtPiece("1");
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    } 
    
    try {
      artListing = serviceAl.addArtPiece(AL_KEY, "1");
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    }
    

    assertNotNull(artListing.getPiece(0));

  }    
  
  
  @Test
  public void testAddTag() {
    PostVisibility Postvisibility = PostVisibility.Public;
    String aDescription = "123";
    String aTitle = "111122223333";
    User aUser = new User("a", "b", "c", "d", "e", "f", "g");
    Customer aCustomer = new Customer("customerCode", aUser);
    Artist aArtist = new Artist("artistCode", aCustomer);
    ArtListing artListing = null;
    String error = null;
    Tag tag = null;

    try {
      artListing = serviceAl.createArtListing(0.0,Postvisibility, aDescription, aTitle, aArtist);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(error);
    assertNull(tag);
    assertNotNull(artListing);
    // data
    // validation.

    try {
      tag = serviceT.getTag("1");
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    } 
    
    try {
      artListing = serviceAl.addTag(AL_KEY, "1");
    } catch (IllegalArgumentException e) {
      // Check that no error occurred
      fail();
    }
    

    assertNotNull(artListing.getTag(0));

  } 
  
}
