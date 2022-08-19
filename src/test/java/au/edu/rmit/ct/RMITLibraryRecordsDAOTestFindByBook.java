/**
 *
 * Name: John Smith (( Update with your name here ))
 * Student ID: s45045012  (( Update with your ID))
 *
 * [OPTIONAL: add any notes or comments here about the code]
 */

package au.edu.rmit.ct;

import com.wmw.examples.mockito.library.Book;
import com.wmw.examples.mockito.library.LibraryRecord;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class RMITLibraryRecordsDAOTestFindByBook {

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
        //print("DAO limit: " + dao.recordLimit);
        //print("DAO record list size after reset: " + dao.export().size());
        print("------------------------------");
    }
    @Test
    @DisplayName("findByBook: Testing for finding an existing book")
    @Order(1)
    void findByBook1()
    {
        List<LibraryRecord> records = dao.findByBook(bookList.get(bookIdx));
        assertEquals(1, records.size(),
                "It should be only 1 book in myLibraryRecords in the DAO");
        print("Checking for the correct size of the result record list passed!");

        //If the book is the same, the ID and ISBN already the same, no need for
        //testing that.
        assertSame(bookList.get(bookIdx), records.get(0).getBook(),
                "The book we are looking for should be the same");
        print("Checking for the correct book passed!");

        //Just to be sure, we will test for the record of book when we save it to the DAO.
        //They should be the same record.
        assertSame(seedRecordList.get(bookIdx), records.get(0),
                "The record for the book should be the same with the one returning");
        print("Checking for the correct record passed!");

    }
    @Test
    @DisplayName("findByBook: Testing for finding a non-existing book")
    @Order(2)
    void findByBook2()
    {
        //There should be a book with the ID and ISBN is bookIdx but now because we make
        // a new book object, it should not be the same with the one currently in
        // the myLibraryRecords.
        RMITLibraryItem newBook = new RMITLibraryItem(Long.valueOf(bookIdx), String.valueOf(bookIdx));
        List<LibraryRecord> records = dao.findByBook(newBook);

        assertEquals(0, records.size(),
                "There should be no result for this new book");
        print("Checking for the correct size of the result record list passed!");
    }
    @Test
    @DisplayName("findByBook: Testing for finding one book but multiple result records")
    @Order(3)
    void findByBook3()
    {
        //Add a new record with an existing book in the DAO
        LibraryRecord newRecord = new LibraryRecord();
        newRecord.setBook(bookList.get(bookIdx));
        dao.save(newRecord);
        List<LibraryRecord> records = dao.findByBook(bookList.get(bookIdx));

        //Now there should be 2 records for one book in the DAO
        assertEquals(2, records.size(),
                "There should be 2 records for this book");
        print("Checking for the correct size of the result record list passed!");

        //Two records should have the same book with the one we are looking for.
        assertSame(bookList.get(bookIdx), records.get(0).getBook(),
                "The book we are looking for should be the same");
        print("Checking for the 1st correct book passed!");

        assertSame(bookList.get(bookIdx), records.get(1).getBook(),
                "The book we are looking for should be the same");
        print("Checking for the 2nd correct book passed!");

    }
    @Test
    @DisplayName("findByBook: Testing for getting exception thrown when passing wrong type of class ")
    @Order(4)
    void findByBook4()
    {
        LibraryRecord newRecord = new LibraryRecord();
        Book newBook = new Book();
        newRecord.setBook(newBook);
        dao.save(newRecord);

        //Although we already a new record with new book into the DAO, using Book class
        //to find a book will receive an exception throw. (line 21 in RMITLibraryRecordDAO)
        assertThrows(IllegalArgumentException.class, ()->dao.findByBook(newBook),
                "The class of the book we passing here should be RMITLibraryItem");
        print("Checking for the exception thrown when passing wrong argument passed!");

    }
}
