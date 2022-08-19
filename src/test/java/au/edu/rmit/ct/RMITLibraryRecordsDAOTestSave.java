/**
 *
 * Name: John Smith (( Update with your name here ))
 * Student ID: s45045012  (( Update with your ID))
 *
 * [OPTIONAL: add any notes or comments here about the code]
 */

package au.edu.rmit.ct;

import com.wmw.examples.mockito.library.LibraryRecord;
import org.junit.jupiter.api.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class RMITLibraryRecordsDAOTestSave {

    static void print(String str)
    {
        System.out.println(str);
    }
    @AfterAll
    static void finishedAllTests() throws Exception
    {
        print("Finished all the tests. Terminating the program.");
    }
    @Test
    @DisplayName("save: Testing for saving a large numbers of record but still less than Integer.MAX_VALUE")
    @Order(1)
    void save1()
    {
        RMITLibraryRecordsDAO dao = new RMITLibraryRecordsDAO();
        for (int i=0; i < 10000000; i++)
        {
            assertTrue(dao.save(new LibraryRecord()),
                    "Saving failed because it reached the record limit");
        }
        print("Checking for saving 10 million records passed!");

    }
    @RepeatedTest(10) //This test is having some random elements, so we will repeat the test for the best result.
    @DisplayName("save: Testing for saving with recordLimit = 10")
    @Order(2)
    void save2()
    {
        RMITLibraryRecordsDAO dao = new RMITLibraryRecordsDAO();

        int limit = (new Random()).nextInt(100); //This will result in a random number from 0 to 99
        dao.recordLimit = limit;
        for (int i = 0; i <= 100; i++)
        {
            // If the record limit is n, the highest possible size() will be n + 1.
            // So, if i is <= limit, we still can save the record.
            if (i <= limit)
            {
                assertTrue(dao.save(new LibraryRecord()),
                        "Saving failed because it exceeded the record limit");
            }
            else
            {
                assertFalse(dao.save(new LibraryRecord()),
                        "Saving succeeded but it haven't exceeded the record limit yet");
            }
        }
        print("Checking for saving "+ limit + " records passed!");
    }
    @Test
    @DisplayName("save: Testing for saving with recordLimit is negative")
    @Order(3)
    void save3()
    {
        //If recordLimit is 0, we still can save 1 record. But if it is negative, none record will be saved.
        RMITLibraryRecordsDAO dao = new RMITLibraryRecordsDAO();
        dao.recordLimit = -1;
        for (int i=0; i <= 100; i++)
        {
            assertFalse(dao.save(new LibraryRecord()),
                    "Saving succeeded but it haven't exceeded the record limit yet");
        }
        print("Checking for saving with negative recordLimit passed!");
    }
}