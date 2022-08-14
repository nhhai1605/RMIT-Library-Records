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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.*;

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
        dao.replaceLibraryRecord(new ArrayList<>(seedRecordList));
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
        print("Reset the record DAO");
        print("DAO record list size: " + dao.export().size());
        //This will make sure we won't add anything to the record list in the DAO after every test
        dao.replaceLibraryRecord(new ArrayList<>(seedRecordList));
        lendingManager.setLibraryRecordDAO(dao);
        print("DAO record list size after reset: " + dao.export().size());
        print("------------------------------");
    }
    @Test
    @DisplayName("borrowBook: Testing for the book's ID and ISBN")
    @Order(1)
    void borrowBook1()
    {
        LibraryRecord record = lendingManager.borrowBook(bookList.get(bookIdx));

        assertEquals(Long.valueOf(bookIdx), record.getBook().getId(),
                "The book should have the same ID with the bookIdx");
        print("Checking for correct book ID passed!");

        assertEquals(String.valueOf(bookIdx),record.getBook().getISBN(),
                "The book should have the same ISBN with the bookIdx");
        print("Checking for correct book ISBN passed!");
    }

    @Test
    @DisplayName("borrowBook: Testing for the record and book's null variables")
    @Order(2)
    void borrowBook2()
    {
        LibraryRecord record = lendingManager.borrowBook(bookList.get(bookIdx));
        assertEquals(null,record.getId(),
                "We didn't set the ID for the record in borrowBook, so it should be null!");
        print("Checking for null record ID passed!");

        assertEquals(null, record.getBook().getName(),
                "This value should be null because we only initialized ID and ISBN for the testing");
        print("Checking for null book name passed!");

        assertEquals(null, record.getBook().getAuthors(),
                "This value should be null because we only initialized ID and ISBN for the testing");
        print("Checking for null book authors passed!");

        assertEquals(null, record.getBook().getPublisher(),
                "This value should be null because we only initialized ID and ISBN for the testing");
        print("Checking for null book publisher passed!");

        assertEquals(null, record.getBook().getPublicationDate(),
                "This value should be null because we only initialized ID and ISBN for the testing");
        print("Checking for null book publication date passed!");
    }

    @Test
    @DisplayName("borrowBook: Testing for the returning and the borrowing date of the record")
    @Order(3)
    void borrowBook3()
    {
        LibraryRecord record = lendingManager.borrowBook(bookList.get(bookIdx));

        assertNotEquals(null, record.getBorrowingDate(),
                "The borrowing date should not be null");
        print("Checking for not null record borrowing date passed!");

        assertTrue((new Date().getTime() - record.getBorrowingDate().getTime()) < 1000,
                "The borrowing date should be really close to the current date");
        print("Checking for correct record borrowing date passed!");

        assertEquals(null, record.getReturningDate(),
                "We haven't return the book yet, so the return date should be null");
        print("Checking for null record returning date passed!");
    }
    @Test
    @DisplayName("borrowBook: Testing for exception thrown when borrowing Book class instead of RMITLibraryItem")
    @Order(4)
    void borrowBook4()
    {
        Book book = new Book();
        book.setId(Long.valueOf(bookIdx));
        book.setISBN(String.valueOf(bookIdx));

        assertThrows(IllegalArgumentException.class,() -> lendingManager.borrowBook(new Book()),
                "We are passing a wrong class here, so an exception should be thrown");
        print("Checking for exception thrown when passing wrong class passed!");
    }
    @Test
    @DisplayName("borrowBook: Testing for exception thrown when empty returning and borrowing date")
    @Order(5)
    void borrowBook5()
    {
        RMITLibraryRecordsDAO newDao = dao;
        LendingManagerImpl newLendingManager = new LendingManagerImpl();
        RMITLibraryItem newBook = new RMITLibraryItem(100L, "100");
        LibraryRecord newRecord = new LibraryRecord();
        newRecord.setId(100L);
        newRecord.setBook(newBook);
        newDao.save(newRecord);
        newLendingManager.setLibraryRecordDAO(newDao);
        assertThrows(IllegalStateException.class,()->newLendingManager.borrowBook(newBook),
                "This book is having null borrowing date, so an exception should be thrown");
        print("Checking for exception thrown at null borrowing date passed!");

        assertThrows(IllegalStateException.class,()->newLendingManager.borrowBook(newBook),
                "This book is having null returning date, so an exception should be thrown");
        print("Checking for exception thrown at null returning date passed!");
    }
    @Test
    @DisplayName("borrowBook: Testing for exception thrown when multiple empty returning dates")
    @Order(6)
    void borrowBook6()
    {
        RMITLibraryRecordsDAO newDao = dao;
        LendingManagerImpl newLendingManager = new LendingManagerImpl();
        RMITLibraryItem newBook = new RMITLibraryItem(100L, "100");
        for(int i = 1; i <= 5; i++)
        {
            LibraryRecord newRecord = new LibraryRecord();
            newRecord.setId(100L + i);
            newRecord.setBook(newBook);
            newRecord.setBorrowingDate(new Date());
            newDao.save(newRecord);
        }
        newLendingManager.setLibraryRecordDAO(newDao);
        assertThrows(IllegalStateException.class,()->newLendingManager.borrowBook(newBook),
                "This book is having multiple empty returning dates, so an exception should be thrown");
        print("Checking for exception thrown at multiple null returning dates passed!");
    }
    @Test
    @DisplayName("borrowBook: Testing for exception thrown when the record limit reached")
    @Order(7)
    void borrowBook7()
    {
        dao.setRecordLimit(1);
        assertThrows(IllegalStateException.class,()->lendingManager.borrowBook(bookList.get(bookIdx)),
                "We cannot save any new record here because it reached the record limit," +
                        "so an exception should be thrown");
        print("Checking for exception thrown at multiple null returning dates passed!");
    }
    @Test
    @DisplayName("borrowBook: Testing for borrow a new book that is not existed in the RMITLibraryRecordsDAO")
    @Order(8)
    void borrowBook8()
    {
        int newBookIdx = bookIdx + bookList.size();
        RMITLibraryItem newBook = new RMITLibraryItem(Long.valueOf(newBookIdx), String.valueOf(newBookIdx));
        LibraryRecord record = lendingManager.borrowBook(newBook);

        assertEquals(Long.valueOf(newBookIdx), record.getBook().getId(),
                "The book should have the same ID with the bookIdx");
        print("Checking for correct book ID passed!");

        assertEquals(String.valueOf(newBookIdx),record.getBook().getISBN(),
                "The book should have the same ISBN with the bookIdx");
        print("Checking for correct book ISBN passed!");

        assertEquals(null,record.getId(),
                "We didn't set the ID for the record in borrowBook, so it should be null!");
        print("Checking for null record ID passed!");

        assertEquals(null, record.getBook().getName(),
                "This value should be null because we only initialized ID and ISBN for the testing");
        print("Checking for null book name passed!");

        assertEquals(null, record.getBook().getAuthors(),
                "This value should be null because we only initialized ID and ISBN for the testing");
        print("Checking for null book authors passed!");

        assertEquals(null, record.getBook().getPublisher(),
                "This value should be null because we only initialized ID and ISBN for the testing");
        print("Checking for null book publisher passed!");

        assertEquals(null, record.getBook().getPublicationDate(),
                "This value should be null because we only initialized ID and ISBN for the testing");
        print("Checking for null book publication date passed!");

        assertNotEquals(null, record.getBorrowingDate(),
                "The borrowing date should not be null");
        print("Checking for not null book borrowing date passed!");

        assertTrue((new Date().getTime() - record.getBorrowingDate().getTime()) < 1000,
                "The borrowing date should be really close to the current date");
        print("Checking for correct book borrowing date passed!");

        assertEquals(null, record.getReturningDate(),
                "We haven't return the book yet, so the return date should be null");
        print("Checking for null book returning date passed!");
    }

    @Test
    @DisplayName("returnBook: Testing for the book's ID and ISBN ")
    @Order(9)
    void returnBook1()
    {
        LibraryRecord record = lendingManager.borrowBook(bookList.get(bookIdx));
        record = lendingManager.returnBook(bookList.get(bookIdx));

        assertEquals(Long.valueOf(bookIdx), record.getBook().getId(),
                "The book should have the same ID with the bookIdx");
        print("Checking for correct book ID passed!");

        assertEquals(String.valueOf(bookIdx),record.getBook().getISBN(),
                "The book should have the same ISBN with the bookIdx");
        print("Checking for correct book ISBN passed!");
    }

}