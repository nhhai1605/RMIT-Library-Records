/**
 *
 * Name: John Smith (( Update with your name here ))
 * Student ID: s45045012  (( Update with your ID))
 *
 * [OPTIONAL: add any notes or comments here about the code]
 */

package au.edu.rmit.ct;

import com.wmw.examples.mockito.library.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class LendingManagerImplTest
{
    private static LendingManagerImpl lendingManager = new LendingManagerImpl();
    private static List<RMITLibraryItem> bookList = new ArrayList<>();
    private static List<LibraryRecord> seedRecordList = new ArrayList<>();
    private static RMITLibraryRecordsDAO dao = new RMITLibraryRecordsDAO();
    private static int bookIdx;
    static void print(String str)
    {
        System.out.println(str);
    }
    @BeforeAll
    static void setUpExampleLibraryRecords() throws Exception
    {
        print("Setting up example Library Records before testing");
        for (int i = 0; i < 100; i++)
        {
            LibraryRecord newRecord = new LibraryRecord();
            newRecord.setId(Integer.toUnsignedLong(i));
            RMITLibraryItem newBook = new RMITLibraryItem(Long.valueOf(i), String.valueOf(i));
            bookList.add(newBook);
            newRecord.setId(Long.valueOf(i));
            newRecord.setBook(newBook);
            newRecord.setBorrowingDate(new Date());
            newRecord.setReturningDate(new Date());
            seedRecordList.add(newRecord);
        }
        dao.myLibraryRecords = new ArrayList<>(seedRecordList);
        lendingManager.setLibraryRecordDAO(dao);
        print("------------------------------");
    }
    @AfterAll
    static void finishedAllTests() throws Exception
    {
        print("Finished all the tests. Terminating the program.");
    }

    @BeforeEach
    void setUpRandomBookForTesting() throws Exception
    {
        print("Setting up before each test");
        bookIdx = (new Random()).nextInt(bookList.size());
        print("Generate a random book idx: " + bookIdx);
        print("");
    }

    @AfterEach
    void resetTheRecordDAO() throws  Exception
    {
        print("");
        print("Reset the record list in the DAO");
        //print("DAO record list size: " + dao.export().size());
        //This will make sure we won't add anything to the record list in the DAO after every test
        dao = new RMITLibraryRecordsDAO();
        dao.myLibraryRecords = new ArrayList<>(seedRecordList);
        lendingManager.setLibraryRecordDAO(dao);
        //print("DAO limit: " + dao.recordLimit);
        //print("DAO record list size after reset: " + dao.export().size());
        print("------------------------------");
    }
    @Test
    @DisplayName("borrowBook: Testing for the correct result record and book")
    @Order(1)
    void borrowBook1()
    {
        LibraryRecord record = lendingManager.borrowBook(bookList.get(bookIdx));
        assertSame(record.getBook(), bookList.get(bookIdx),
                "The result book and book in the book list should be the same");

        //Line 44 in LendingManagerImpl.java ==> new record is created and returned
        assertNotSame(record, seedRecordList.get(bookIdx),
                "The books are the same but the record should be different");

        print("Check for the correct result book and record passed!");
    }
    @Test
    @DisplayName("borrowBook: Testing for the correct returning and borrowing date")
    @Order(2)
    void borrowBook2()
    {
        LibraryRecord record = lendingManager.borrowBook(bookList.get(bookIdx));

        //We just borrowed the book, so the borrowing date should not be null
        assertNotNull(record.getBorrowingDate(),
                "The borrowing date should not be null");
        print("Checking for not null record borrowing date passed!");

        //The borrowing date should be really close to current time, close but not equal because
        //computer takes time to process the code
        assertTrue((new Date().getTime() - record.getBorrowingDate().getTime()) < 100,
                "The borrowing date should be really close to the current date");
        print("Checking for correct record borrowing date passed!");

        //We haven't returned the book yet, so the returning date should be null
        assertNull(record.getReturningDate(),
                "The returning date should be null, because we just haven't returned the book yet");
        print("Checking for null record returning date passed!");
    }
    @Test
    @DisplayName("borrowBook: Testing for the non-exiting book")
    @Order(3)
    void borrowBook3()
    {
        RMITLibraryItem newBook = new RMITLibraryItem(Long.valueOf(bookIdx), String.valueOf(bookIdx));
        LibraryRecord record = lendingManager.borrowBook(newBook);
        //Although we didn't add this new book to the DAO, when running borrowBook, it will create
        //a new record and set the new book to that record. So, we will gain the same book
        assertSame(newBook, record.getBook(),
                "The new book and the result book should be the same");
        print("Checking for the correct result book for the non-exiting book passed!");

    }
    @Test
    @DisplayName("borrowBook: Testing for exception thrown at empty returning date")
    @Order(4)
    void borrowBook4()
    {
        LibraryRecord newRecord = new LibraryRecord();
        RMITLibraryItem newBook = new RMITLibraryItem(Long.valueOf(bookIdx), String.valueOf(bookIdx));
        newRecord.setBook(newBook);
        newRecord.setBorrowingDate(new Date());
        dao.save(newRecord);
        //Because we didn't set the returning date for the new record before adding to the DAO,
        //which means the book haven't been returned yet and an exceptions will appear.
        assertThrows(IllegalStateException.class,()->lendingManager.borrowBook(newBook),
                "The record is having an empty returning date, so an exception should be thrown here");
        print("Checking for exception thrown at empty returning date passed!");

    }
    @Test
    @DisplayName("borrowBook: Testing for exception thrown at saving new record in borrowBook")
    @Order(5)
    void borrowBook5()
    {
        //Set the recordLimit to really low, so now the DAO cannot save a new record anymore
        dao.recordLimit = 1;
        assertThrows(IllegalStateException.class, ()->lendingManager.borrowBook(bookList.get(bookIdx)),
                "The DAO reached the record limit, so it cannot save a new record and an exception should be thrown");
        print("Checking for exception thrown at saving new record in borrowBook passed!");
    }
    @Test
    @DisplayName("returnBook: Testing for exception thrown at returning un-borrowed book")
    @Order(6)
    void returnBook1()
    {
        //If we don't borrow the book but return it, an exception will be thrown here
        assertThrows(IllegalStateException.class,()->lendingManager.returnBook(bookList.get(bookIdx)),
                "You cannot return the book which haven't been borrowed yet, so an exception should be thrown here");
        print("Check for exception thrown at returning un-borrowed bookm passed!");
    }
    @Test
    @DisplayName("returnBook: Testing for correct return record as well as no exception when returning a borrowed book")
    @Order(7)
    void returnBook2()
    {
        //Borrow the book before returning it
        lendingManager.borrowBook(bookList.get(bookIdx));
        //Now there should be no exception when returning it
        assertDoesNotThrow(()-> lendingManager.returnBook(bookList.get(bookIdx)),
                "Because we have already borrowed the book, so there should be no problem returning it");
        print("Checking for NO exception thrown at returning book passed!");

        //Borrow the book again
        LibraryRecord borrowRecord= lendingManager.borrowBook(bookList.get(bookIdx));
        //Return the book
        LibraryRecord returnRecord = lendingManager.returnBook(bookList.get(bookIdx));
        //Because when we borrow the book, it will create a new record with empty returning date
        //and save that to the DAO, so when we return it, the method will return the same record.
        assertSame(returnRecord, borrowRecord, "The borrow record and the return record should be the same");
        print("Checking for the correct record from returnBook method passed!");

    }
    @RepeatedTest(5) //This test is having some random elements, so we will repeat the test for the best result.
    @DisplayName("returnBook: Testing for the correct returning and borrowing date of the return record")
    @Order(8)
    void returnBook3() throws InterruptedException {
        //Make a random time before returning a book
        int randomSleepTime = (new Random()).nextInt(1000) + 1000;
        print("Random sleep time: " + randomSleepTime);
        //so the time limit will be the random time above plus 50ms for the program to process the code
        int timeLimit = randomSleepTime + 100;

        LibraryRecord borrowRecord = lendingManager.borrowBook(bookList.get(bookIdx));
        //Already tested it but this is just to make sure the borrow record is having empty returning date
        assertNull(borrowRecord.getReturningDate(),
                "We haven't returned yet, so returning date should be empty");

        //Wait for a random time we get above
        sleep(randomSleepTime);

        //After waiting, we return a book
        LibraryRecord returnRecord = lendingManager.returnBook(bookList.get(bookIdx));

        //This is just for sure
        assertNotNull(borrowRecord.getReturningDate(),
                "We returned the book, so returning date should not be empty");

        Long returningTime = returnRecord.getReturningDate().getTime();
        Long borrowingTime = returnRecord.getBorrowingDate().getTime();
        Long timeGap = returningTime - borrowingTime; //The time gap between we borrowed and returned the book

        //it should be higher than the sleep time but not exceed the time limit
        assertTrue(timeGap >= randomSleepTime && timeGap <= timeLimit,
                "Because we sleep for some amount of time before returning the book, the time gap should be in this range");
        print("Checking for the correct returning and borrowing date of the return record passed!");
    }

    @Test
    @DisplayName("returnBook: Testing for exception thrown at saving new record in returnBook")
    @Order(9)
    void returnBook4()
    {
        lendingManager.borrowBook(bookList.get(bookIdx));
        //Set the recordLimit to really low, so now the DAO cannot save a new record anymore
        dao.recordLimit = 1;
        assertThrows(IllegalStateException.class, ()->lendingManager.returnBook(bookList.get(bookIdx)),
                "The DAO reached the record limit, so it cannot save a new record and an exception should be thrown");
        print("Checking for exception thrown at saving new record in returnBook passed!");
    }

}