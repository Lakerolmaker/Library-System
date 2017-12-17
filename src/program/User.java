package program;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import frontend.MainWindow;

/**
 * Description: User class with attributes (first name, last name, ssn, user ID (library card number), dept, 
 * phone number, street, zip code and city) and getters/setters.
 * @author Tihana Causevic
 */

public class User {

	private String firstName;
	private String lastName;
	private String ssn; 
	private int userId;
	private double debt;
	private String phoneNr; 
	private String street;
	private String zipCode;
	private String city;
	private ArrayList<LoanInstance> bookList = new ArrayList<>();
	static AtomicInteger nextId = new AtomicInteger();

	public User(String firstName, String lastName, String ssn, String phoneNr, String street, String zipCode, String city) {
		this.userId = nextId.incrementAndGet();
		this.firstName = firstName;
		this.lastName = lastName;
		this.debt = 0;
		this.ssn = ssn;
		this.phoneNr = phoneNr;
		this.street = street;
		this.zipCode = zipCode;
		this.city = city;
	}

	public String getName() {
		return this.firstName + " " + this.lastName;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}

	public int getUserId() {
		return this.userId;
	}

	public double getDebt() {
		return this.debt;
	}

	public String getSsn() {
		return this.ssn;
	}

	public String getPhoneNr() {
		return this.phoneNr;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setDebt(double debt) {
		this.debt = debt;
	}

	public void setPhoneNr(String phoneNr) {
		this.phoneNr = phoneNr;
	}


	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	

	public double getDelayfee(int bookListIndex) {

		LocalDate today = LocalDate.now();
		LocalDate returnDate = getBorrowedBookReturnDate(bookListIndex);

		double days = today.toEpochDay() - returnDate.toEpochDay();

		if (days > 0)
			this.debt = days * 2;

		return this.debt;

		// debt for 1 book only; iterate through borrowed books to get full debt

	}

	public LocalDate getBorrowedBookReturnDate(int bookListIndex) {
		LocalDate returnDate = bookList.get(bookListIndex).getReturnDate();
		return returnDate;
	}
	
	public int getDaysLeft(int bookListIndex) {
		LocalDate today = LocalDate.now();
		LocalDate returnDate = getBorrowedBookReturnDate(bookListIndex);
		int days = (int) (returnDate.toEpochDay() - today.toEpochDay());
		return days;
	}
	
	public ArrayList<LoanInstance> getBookList() throws Exception {
		if (bookList == null) {
			throw new Exception("Users BookList is empty");
		}else {
			return bookList;
		}
	}

	
	public void borrowBook(Book book, LocalDate returnDate) throws Exception {
		LoanInstance tmp = new LoanInstance(book, returnDate);
		bookList.add(tmp);
		book.loan();
	}
	
	public ArrayList<LoanInstance> getDelayedBooks(){
		ArrayList<LoanInstance> temp = new ArrayList<>();
		for (int i = 0; i < bookList.size(); i++) {
			if(getDaysLeft(i) < 0) {
				temp.add(bookList.get(i));
			}
		}
		return temp;
	}
	
	public void setLendDate(int bookListIndex, LocalDate date) {
		bookList.get(bookListIndex).setDate(date);
	}
	
	
	public void removeBorrowedBook(int bookListIndex) {
		Book book = bookList.get(bookListIndex).getBook();
		ArrayList<Book> mainBookList = MainWindow.lib.getBookList();
		for (Book tmpBook : mainBookList) {
			if (tmpBook.getIsbn().equals(book.getIsbn())) { 
				tmpBook.returnBook();
			}
		}
		bookList.remove(bookListIndex);
	}
	
	public ArrayList<Integer> getBookIndex(Book book) {
		ArrayList<Integer> tmp = new ArrayList<>();
		for (int i = 0; i < bookList.size(); i++) {
			if(bookList.get(i).getBook().getIsbn().equals(book.getIsbn())) {
				tmp.add(i);
			}
		}
		return tmp;
	}

	public static AtomicInteger getNextId() {
		return nextId;
	}

	public static void setNextId(AtomicInteger nextId) {
		User.nextId = nextId;
	}
}