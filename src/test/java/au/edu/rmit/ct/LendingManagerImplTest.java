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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class LendingManagerImplTest {
    private static LendingManagerImpl lendingManager = new LendingManagerImpl();
    private static List<RMITLibraryItem> bookList = new ArrayList<RMITLibraryItem>();

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        System.out.println("Setting up Before Class");
        RMITLibraryRecordsDAO dao = new RMITLibraryRecordsDAO();
        for (int i = 0; i <= 100; i++)
        {
            LibraryRecord newRecord = new LibraryRecord();
            newRecord.setId(Integer.toUnsignedLong(i));
            RMITLibraryItem newBook = new RMITLibraryItem(Long.valueOf(i), String.valueOf(i));
            bookList.add(newBook);
            newRecord.setId(Long.valueOf(i));
            newRecord.setBook(newBook);
            newRecord.setBorrowingDate(new Date());
            newRecord.setReturningDate(new Date());
            dao.save(newRecord);
        }
        lendingManager.setLibraryRecordDAO(dao);
    }
    @Test
    @DisplayName("Testing for the borrowBook function")
    void borrowBook1() {
        int bookIdx = 15;
        LibraryRecord record = lendingManager.borrowBook(bookList.get(bookIdx));
        assertEquals(null,record.getId(), "We didn't set the ID for the record in borrowBook, so it should be null!");
        assertEquals(Long.valueOf(bookIdx), record.getBook().getId());
        assertEquals(String.valueOf(bookIdx),record.getBook().getISBN());
        assertTrue((new Date().getTime() - record.getBorrowingDate().getTime()) < 1000,"The borrowing date should be really close to the current date");
        assertEquals(null, record.getReturningDate(), "We haven't return the book yet, so the return date should be null");
        assertEquals(null, record.getBook().getName(), "This value should be null because we only initialized ID and ISBN for the testing");
        assertEquals(null, record.getBook().getAuthors(), "This value should be null because we only initialized ID and ISBN for the testing");
        assertEquals(null, record.getBook().getPublisher(), "This value should be null because we only initialized ID and ISBN for the testing");
        assertEquals(null, record.getBook().getPublicationDate(), "This value should be null because we only initialized ID and ISBN for the testing");


    }

    @Test
    @DisplayName("Example Display Name 2")
    void returnBook1() {
        fail("not implemented yet. Just an example test method.");
    }

}