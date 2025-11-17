package com.library.dao;

import com.library.model.Book;
import com.library.util.DatabaseConnection;
import org.junit.*;
import org.mockito.*;

import java.sql.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BookDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private Statement mockStatement;
    @Mock
    private ResultSet mockResultSet;

    private BookDAO bookDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        bookDAO = new BookDAO();
        DatabaseConnection.setTestConnection(mockConnection); // inject mock connection
    }

    @Test
    public void testFindById_exists() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("title")).thenReturn("Test Book");

        assertNotNull(bookDAO.findById(1));
    }

    @Test
    public void testFindById_notExists() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertNull(bookDAO.findById(99));
    }

    @Test
    public void testCreate_success() throws Exception {
        ResultSet mockKeys = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockKeys);
        when(mockKeys.next()).thenReturn(true);
        when(mockKeys.getInt(1)).thenReturn(10);

        Book book = new Book();
        book.setTitle("New Book");

        assertTrue(bookDAO.create(book));
        assertEquals(10, book.getId());
    }

    @Test
    public void testCreate_fail() throws Exception {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Book book = new Book();
        assertFalse(bookDAO.create(book));
    }

    @Test
    public void testDelete_success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(bookDAO.delete(1));
    }

    @Test
    public void testDelete_fail() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(bookDAO.delete(99));
    }

    @Test
    public void testIncreaseAvailableQuantity_success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(bookDAO.increaseAvailableQuantity(1));
    }

    @Test
    public void testIncreaseAvailableQuantity_fail() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(bookDAO.increaseAvailableQuantity(99));
    }

    @Test
    public void testSQLExceptionHandling() throws Exception {
        // Mock both prepareStatement variants to throw SQLException
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException());
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException());

        // DAO methods should handle SQLException gracefully
        assertNull(bookDAO.findById(1));
        assertFalse(bookDAO.create(new Book()));
        assertFalse(bookDAO.delete(1));
        assertFalse(bookDAO.increaseAvailableQuantity(1));
    }

}
