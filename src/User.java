
<<<<<<< HEAD
	import java.util.HashMap;
	import java.util.Date;

	public class User {
		private String name;
		private int ssn;
		private int userId;
		private int libraryCardNum;
		private double debt;
		private int phoneNr;
		private Object address;
		
		private HashMap<Book, Date> booksBorrowed = new HashMap<Book, Date>();
		
		User (String name, int userId, double debt, int ssn, int phoneNr, Object adress) {
			this.name = name;
			this.userId = userId;
			this.libraryCardNum = this.userId;
			this.debt = debt;
			this.ssn = ssn;
			this.phoneNr = phoneNr;
			this.address = adress;
		}
		
		public String getName() {
			return this.name;
		}
		
		public int getUserId() {
			return this.userId;
		}
		
		public double getDebt() {
			return this.debt;
		}
		public int getSsn() {
			return this.ssn;
		}
		public int getPhoneNr() {
			return this.phoneNr;
		}
		public Object getAdress() {
			return this.address;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		public void setDebt(double debt) {
			this.debt = debt;
		}
		public void setUserId(int userId) {
			this.userId = userId;
		}
		public void setPhoneNr(int phoneNr) {
			this.phoneNr = phoneNr;
		}
		public void setAddress(Object adress) {
			this.address = address;
		}
		
		public void borrowBook(Book book) {
			Date date = new Date();
			booksBorrowed.put(book, date);
		}
		
		public void removeBorrowedBook(Book book) {
			booksBorrowed.remove(book);
		}
=======
public class User {
>>>>>>> 09b8de99c008536be9b4ab52c7bfb854507ab066

}
