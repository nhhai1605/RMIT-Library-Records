/**
 *
 * Name: John Smith (( Update with your name here ))
 * Student ID: s45045012  (( Update with your ID))
 *
 * [OPTIONAL: add any notes or comments here about the code]
 */

package au.edu.rmit.ct;

import com.wmw.examples.mockito.library.LendingManagerImpl;
import com.wmw.examples.mockito.library.LibraryRecord;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class LendingManagerImplTestCheckRI {
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

    //Because the checkRecordIntegrity is a private function, so I will use the borrowBook to test
    // the CheckRI (line 38 in LendingManagerImpl.java)
    @Test
    @DisplayName("checkRecordIntegrity: Testing for NO exception thrown for a record having adequate returning and borrowing date")
    @Order(1)
    void checkRecordIntegrity1()
    {
        //If the record have both returning and borrowing date and not missing anything, then there
        //will be no issue when we borrow the book
        assertDoesNotThrow(()->lendingManager.borrowBook(bookList.get(bookIdx)),
                "Because we already set the returning and borrowing date for the record, there should be no exception");
        print("Checking for NO exception thrown for a record having both returning and borrowing date passed!");
    }

    @Test
    @DisplayName("checkRecordIntegrity: Testing for exception thrown at empty borrowing date")
    @Order(2)
    void checkRecordIntegrity2()
    {
        //Make new DAO, add a new record with a new book
        RMITLibraryRecordsDAO newDao = new RMITLibraryRecordsDAO();
        RMITLibraryItem newBook = new RMITLibraryItem(Long.valueOf(bookIdx), String.valueOf(bookIdx));
        LibraryRecord newRecord = new LibraryRecord();
        newRecord.setBook(newBook);
        newDao.save(newRecord);
        lendingManager.setLibraryRecordDAO(newDao);
        //But the new book is having an empty borrowing date, so there will be an exception here
        assertThrows(IllegalStateException.class,()->lendingManager.borrowBook(newBook),
                "This book is having an empty borrowing date, so an exception should be thrown here");
        print("Checking for exception thrown at empty borrowing date passed!");
    }

    @Test
    @DisplayName("checkRecordIntegrity: Testing for exception thrown at multiple empty returning dates")
    @Order(3)
    void checkRecordIntegrity3()
    {
        //Make a new DAO, add multiple new records and set their books to one new book
        RMITLibraryRecordsDAO newDao = new RMITLibraryRecordsDAO();
        RMITLibraryItem newBook = new RMITLibraryItem(Long.valueOf(bookIdx), String.valueOf(bookIdx));
        for (int i = 0; i < 30; i++)
        {
            LibraryRecord newRecord = new LibraryRecord();
            newRecord.setBook(newBook);
            newRecord.setBorrowingDate(new Date());
            newDao.save(newRecord);
        }
        lendingManager.setLibraryRecordDAO(newDao);
        //We already set the borrowing dates for these records, but we haven't set the returning date,
        //so an exception will appear here.
        assertThrows(IllegalStateException.class,()->lendingManager.borrowBook(newBook),
                "This book is having multiple empty returning date records, so an exception should be thrown here");
        print("Checking for exception thrown at multiple empty returning dates passed!");
    }

}