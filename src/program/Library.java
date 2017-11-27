package program;
import java.util.ArrayList;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Library {
	
	FileClass file = new FileClass();
	Gson gson = new Gson();
	
	public ArrayList<User> userDirectory = new ArrayList<User>();
	public ArrayList<Book> bookDirectory = new ArrayList<Book>();
	public BorrowedBook borrBook = new BorrowedBook();
	public static int loanAllowance = 14;
	
	// days the book is allowed to be loaned out

	//public List<Book> books = new ArrayList<Book>(this.books.keySet());

	
	//: Function that adds a book to the library.
	// TODO : fix the dublication issue.
	public void addBook(int isbn, String name, String author, int year, String category, int shelf) throws Exception {
		for (Book book : bookDirectory) {
			if (book.getIsbn() == isbn) {
				//book.addCopy();
			}
		}
		Book newbook = new Book(isbn, name, author, year, category, shelf);
		bookDirectory.add(newbook);
	}
	
	
	
	
	/** Register user
	 * Checks if a user with the same SSN already exists.
	 * If the SSN is not already registered a new user will be registered. 
	 * @throws Exception 
	 */
	public void addUser(String firstName, String lastName, String ssn, int phoneNr, String street, int zipCode, String city) throws Exception {
		// Search for duplicate using SSN
		if (findUser(ssn) != null) {
			throw new Exception("Error: Customer with same SSN already exists in db");
		// If no duplicate found - a new User are registered
		}else {
		//	User newUser = new User(firstName, lastName, ssn, phoneNr, street, zipCode, city);
		//	userDirectory.add(newUser);
		}
		
	}
	
	/** Retrieve user from their id
	 * This function should be called to access user functions. Ex: user.getAdress(), user.getFirstName();
	 *  @return User (returns null if no user is found)
	 */
	public User getUser(int id) {
		return userDirectory.get(id);
	}
	
	/** Find User by SSN
	 * @param ssn
	 * @return User (or null if not found)
	 */
	public User findUser(String ssn) {
		for (User val : userDirectory) {
			if (val.getSsn() == ssn) {
				return val;
			}
		}
		return null;
	}
	
	public Book getBook(int bookID){
		
		return bookDirectory.get(bookID);
		
	}
	
	public void loanBook(int userID, int bookID) {
		

		Book book;
		User user;
		
		book = getBook(bookID);
		user = getUser(userID);
		
		
		user.borrowBook(book);
		
		
		
		borrBook.history.add("User of ID: " + userID + " has borrowed a book of ID: " + bookID + " on "
				+ borrBook.today.toString());
		
		
		
//		String s = Integer.toString(userID) + Integer.toString(bookID);
//		boolean b = false;
//
//		for (int i = 0; i < borrBook.bookUser.size(); i++)
//			if (borrBook.bookUser.get(i).equals(s)) {
//
//				System.out.println("User has already borrowed that book.");
//				b = true;
//
//			}
//
//		if (b == false) {
//
//			borrBook.bookUser.add(s);
//			borrBook.dates.add(borrBook.today);
//			borrBook.history.add("User of ID: " + userID + " has borrowed a book of ID: " + bookID + " on "
//					+ borrBook.today.toString());
//
//		}

	}

	public void returnBook(int userID, int bookID) {
		
		Book book;
		User user;
		
		book = getBook(bookID);
		user = getUser(userID);
		
		user.removeBorrowedBook(book);
		
		borrBook.history.add("User of ID: " + userID + " has returned a book of ID: " + bookID + " on "
				+ borrBook.today.toString());
		
		
	}
		
//		String s = Integer.toString(userID) + Integer.toString(bookID);
//
//		for (int i = 0; i < borrBook.bookUser.size(); i++)
//			if (borrBook.bookUser.get(i).equals(s)) {
//
//				borrBook.bookUser.remove(i);
//				borrBook.dates.remove(i);
//				borrBook.history.add("User of ID: " + userID + " has returned a book of ID: " + bookID + " on "
//						+ borrBook.today.toString());
//
//			} else
//				System.out.println("User doesn't have that book.");
//

	
	/**
	 *  TODO : No need for duplicate sorting functions. Descending order can be achieved
	 *  by just adding one line: 
	 *  Collections.reverse(aList);
	 */

	/** TODO
	 * Bunch of code and functions below this line that might be unused or not necessary?
	 * A cleanup might be needed.
	 */

	//: sort functions

	/*
	SortInterface sort = new SortInterface() {


		public void sortBooksName(boolean order) {
			
			
			// order is true for ascending; false for descending order
			// in all methods
			
			
			Collections.sort(bookDirectory, Book.nameComparatorAZ);
			
			if (order == false)
				Collections.reverse(bookDirectory);
			

		

		}
		
		
		

		public void sortBooksAuthor(boolean order) {
			
			
			Collections.sort(bookDirectory, Book.authorComparatorAZ); 
			
			if (order == false)
				Collections.reverse(bookDirectory);

			

		}
		
		
		


		
		public void byShelfNumber(boolean order) {
			
			
			Collections.sort(bookDirectory, Book.shelfComparatorASC);
			
			if (order == false)
				Collections.reverse(bookDirectory);
			
		}
		
		

		
		public void byGenre(boolean order) {
			
			Collections.sort(bookDirectory, Book.genreComparatorAZ);
			
			if (order == false)
				Collections.reverse(bookDirectory);
			
			
			
		}
		
		

		
		
		
		
			
	

	*/
	public void save() {
		
		file.createFolder("Database", file.CurrentDir);
		file.createFolder("USERS", file.CurrentDir + "/Database");
		file.createFolder("BOOKS", file.CurrentDir + "/Database");
		
		for(int i = 0 ; i < userDirectory.size(); i++ ) {
			
			String json = gson.toJson(userDirectory.get(i));
			String fileName = "user" + i;
			String directory = file.CurrentDir + "/Database/USERS";
			
			file.createTextFile(fileName , directory);
			
			file.writeToTextFile(fileName  ,json , directory);
			
		}
		
		for(int i = 0 ; i < bookDirectory.size(); i++ ) {
			
			String json = gson.toJson(bookDirectory.get(i));
			String fileName = "book" + i;
			String directory = file.CurrentDir + "/Database/BOOKS";
			
			file.createTextFile(fileName , directory);
			
			file.writeToTextFile(fileName  ,json , directory);
			main.print(i);
		}
	
	}

	public void load() {
		
		boolean scanUser = true;
		int indexUSer = 0;
		
		while(scanUser) {

			String fileName = "user" + indexUSer ;
			String directory = file.CurrentDir + "/Database/USERS";
			
			String json = file.readFromTextFile( fileName , directory );
			
			if(json != null) {
				User user = gson.fromJson(json , User.class);
				userDirectory.add(user);	
				indexUSer++;
			}else {
				scanUser = false;
			}
			
		}
		
		boolean scanBook = true;
		int indexBook = 0;
		
		while(scanBook) {

			String fileName = "book" + indexBook ;
			String directory = file.CurrentDir + "/Database/BOOKS";
			
			String json = file.readFromTextFile( fileName , directory );
			
			if(json != null) {
				Book book = gson.fromJson(json , Book.class);
				bookDirectory.add(book);	
				indexBook++;
			}else {
				scanBook = false;
			}
			
		}
	
		main.print("Books : " + indexBook);
		main.print("Users : " + indexUSer);
		
	}
	
}
