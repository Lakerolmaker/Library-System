package frontend.booksUI;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.util.Callback;
import frontend.newBookUI.*;
import frontend.preferencesUI.PreferencesUI;
import frontend.delayedBooksUI.*;
import frontend.homeUI.HomeUI;
import frontend.MainWindow;
import frontend.aboutUI.AboutUI;
import frontend.registerUserUI.*;
import frontend.userListUI.*;
import frontend.bookViewUI.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import program.*;


public class BooksUI implements Initializable {
	public static HashMap<Book, Integer> booksInBasket;
	
	private static Scene bookScene;
	private static boolean showOnlyAvailable;
	private static String searchFieldString = "";
	// Left Panel
	@FXML private ImageView logoImage;
	@FXML private Label menuHome, menuBooks, menuUsers, menuDelayed;
	// SidePanel
	@FXML private Text nameText,streetText,cityText,balanceText,basketText,booksLoaningText,booksLoaningAmount,enterIdText, amountText,currencyText;
	@FXML private Text onlyNumText,noUserFoundText,switchUserText,returnDateText,dateErrorText, basketTitleText, basketQtyText;
 	@FXML private Button goBtn,loanBtn,loanActionBtn,cancelBtn;
	@FXML private TextField userIdField;
	@FXML private DatePicker datePicker;
	@FXML private ListView<HBox> basketList;
	private ContextMenu cm2; // BasketList context menu
	// Class Main
	@FXML private CheckBox showOnlyAv;
	@FXML private TableView<Book> tableBook;
	@FXML private TableColumn<Book, String> titleColumn,authorColumn,yearColumn,isbnColumn,qtyAvColumn;
	@FXML private TableColumn<Button, String> loanActCol;
	@FXML private TextField searchField;
	@FXML private Text bookTableStatusBar;
	@FXML private int showingCounter = 0;
	@FXML private int totalBooks;
	
	private ContextMenu cm;
	

	

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		switchUserText.setId("switchUserText");
		Image logo = new Image("resources/logo.png");
		logoImage.setImage(logo);
		showOnlyAv.setSelected(showOnlyAvailable);
		showOnlyAv.setOnAction(e -> {
			showOnlyAvailable = showOnlyAv.isSelected();
			display();
		});
		
		searchField.setText(searchFieldString);
		
		totalBooks = MainWindow.lib.getBookList().size();
		
		menuHome.setId("menuHome");
		menuBooks.setId("menuBooks");
		menuUsers.setId("menuBooks");
		menuDelayed.setId("menuDelayed");
		showSidePanel();
		
		
		if(MainWindow.user == null) {
			newBasket();
		}else {
			try {
				ArrayList<LoanInstance> loanList = MainWindow.user.getBookList();
				
			}catch  (Exception e){
				// User is not currently loaning any books
			}
			
		}
		
 		try {
			initTable();
		}catch (NullPointerException e) {}
	}
	
	

	
	public static void display() {
		
		try {
			Class<BooksUI> context = BooksUI.class;
			VBox bookView = (VBox)FXMLLoader.load(context.getResource("Book.fxml"));
			bookScene = new Scene(bookView,1192,650);
		    bookScene.getStylesheets().add(
		    	      MainWindow.class.getResource("application.css").toExternalForm()
		    	    );
			bookView.getStyleClass().add("bookView");
			
			MainWindow.window.setScene(bookScene);
			//MainWindow.window.setResizable(false);
			MainWindow.window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/** Search Button **/
	public void searchFunc(){
		String searchString = searchField.getText().toString();
		ObservableList<Book> bookList = getMatchingBooks(searchString);
		tableBook.setItems(bookList);
		searchFieldString = searchString;
		updateStatusBar(bookList.size());
		
	}
	public void onEnterSearch(ActionEvent event){
		searchFunc();
	}
	
	
	public void updateStatusBar(int showingCount) {
		bookTableStatusBar.setText("Showing " + showingCount + " out of " + totalBooks + " books in library");
	}
	

	/******** BOOK VIEW FUNCTIONS ********/

	// Initialize table
	@SuppressWarnings("unchecked")
	public void initTable(){
		// Title column
		titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		titleColumn.setMaxWidth(8000);
		// Author column
		authorColumn = new TableColumn<>("Author");
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
		// Year column
		yearColumn = new TableColumn<>("Year");
		yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
		yearColumn.setMaxWidth(2500);
		yearColumn.setStyle("-fx-alignment: CENTER;");
		// ISBN column
		isbnColumn = new TableColumn<>("ISBN");
		isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
		isbnColumn.setMaxWidth(4500);
		isbnColumn.setStyle("-fx-alignment: CENTER;");
		// Available quantity
		qtyAvColumn = new TableColumn<>("Available");
		qtyAvColumn.setCellValueFactory(c-> new SimpleStringProperty(Integer.toString(c.getValue().getAvailableQuantity())));
		qtyAvColumn.setMaxWidth(2500);
		qtyAvColumn.setId("qtyAvColumn");
		qtyAvColumn.setStyle("-fx-alignment: CENTER;");
		// Loan column

        TableColumn loanActCol = new TableColumn("");
        loanActCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Book, String>, TableCell<Book, String>> cellFactory
                = 
                new Callback<TableColumn<Book, String>, TableCell<Book, String>>() {
            @Override
            public TableCell call(final TableColumn<Book, String> param) {
                final TableCell<Book, String> cell = new TableCell<Book, String>() {

                    final Button loanActionBtn = new Button("Loan");
                    

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                        	
                            loanActionBtn.setOnAction(event -> {
                                Book book = getTableView().getItems().get(getIndex());
                                addToBasket(book);
                            });
                            loanActionBtn.setId("loanActionBtn");
                            setGraphic(loanActionBtn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        loanActCol.setCellFactory(cellFactory);
        loanActCol.setStyle("-fx-alignment: CENTER;");
        loanActCol.setMaxWidth(2000);

		ObservableList<Book> bookList = getBooks();
		tableBook.setItems(bookList);
		showingCounter = bookList.size();
		tableBook.getColumns().addAll(titleColumn, authorColumn, yearColumn, isbnColumn, qtyAvColumn, loanActCol);
		updateStatusBar	(bookList.size());
		}

	/** Book List Table click functions **/
	@FXML
	public void clickItem(MouseEvent event) {
		
    	if (cm != null) {
	    	if (cm.isShowing()) {
	    		cm.hide(); // Don't allow duplicate context menus open
	    	}
    	}
	    if (event.getClickCount() == 2) {
	    	Book selectedBook = tableBook.getSelectionModel().getSelectedItem(); // Retrieve selected cell
	    	if (selectedBook != null) {
	    		goToBookView(selectedBook);
	    	}
	    }
	    else if(event.getButton() == MouseButton.SECONDARY) {

	        if (tableBook.getSelectionModel().getSelectedItem() != null) { // Check if selected cell contains a book
		        Book selectedBook = tableBook.getSelectionModel().getSelectedItem();
		        cm = new ContextMenu();
		        MenuItem mi1 = new MenuItem("Loan");
		        cm.getItems().add(mi1);
		        MenuItem mi2 = new MenuItem("Delete");
		        cm.getItems().add(mi2);
		        mi1.setOnAction(e -> addToBasket(selectedBook));
		        mi2.setOnAction(e -> System.out.println("Delete"));
		        cm.setAutoHide(true);
	        	cm.show(tableBook , event.getScreenX() , event.getScreenY()); // Context menu is shown
	        }
	    }
	}
	
	
	

	// Return list of books
	public ObservableList<Book> getBooks() {
		ObservableList<Book> books = FXCollections.observableArrayList();
		for (Book book : MainWindow.lib.getBookList()) {
			books.add(book);
		}
		
		if(showOnlyAv.isSelected()) {
			books = showOnlyAvailable(books);
		}
		return books; 
	}
	
	// Return matching list of books
	public ObservableList<Book> getMatchingBooks(String search) {
		if (search.length() < 1) {
			return getBooks(); // If search is empty - returns a list of all books
		}
		ObservableList<Book> books = FXCollections.observableArrayList(); // Create new list
		for (Book book : MainWindow.lib.getBookList()) {
			if (Functions.compareStrings(book.getTitle(), search) || Functions.compareStrings(book.getAuthor(), search) || Functions.compareStrings(book.getIsbn(), search) || Functions.compareStrings(Integer.toString(book.getYear()), search)) {
				books.add(book); // If match add the book to list
			}
		}
		if(showOnlyAv.isSelected()) {
			books = showOnlyAvailable(books);
		}
		return books; // Return the new composed list
	}
	
	
	public ObservableList<Book> showOnlyAvailable(ObservableList<Book> bookList) {
		ObservableList<Book> newBookList = FXCollections.observableArrayList();
		for (Book book : bookList) {
			if (book.getAvailableQuantity() > 0) {
				newBookList.add(book);
			}
		}
		return newBookList;
	}
	
	
	/************************* SIDE PANEL ***************************/

	/** Loan button **/
	public void loanBtnClicked(){
		LocalDate returnDate = datePicker.getValue(); // TODO
		if(returnDate==null){
			dateErrorText.setText("Please fill in return date");
			return;
		}
		if (!returnDate.isAfter(LocalDate.now())) {
			dateErrorText.setText("Return date has to be after today");
			dateErrorText.setVisible(true);
			return;
		}
		if(!returnDate.isBefore(LocalDate.now().plusDays(Library.LOAN_ALLOWANCE+1))){
			dateErrorText.setText("Maximum days to loan is " + Library.LOAN_ALLOWANCE);
			return;
		}
		dateErrorText.setText("");
		for (Entry<Book, Integer> book : booksInBasket.entrySet()) {
			for (int i = 0; i < book.getValue(); i++) {
				try {
					MainWindow.lib.loanBook(MainWindow.user, book.getKey(), returnDate);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			showSidePanel(); // Update side panel
			basketList.getItems().clear(); // Clear basket
			newBasket();
		}
		tableBook.refresh();
	}
	
	public void newBasket(){
		booksInBasket = new HashMap<>();
		if(MainWindow.user!=null) {
			basketText.setVisible(true);
			cancelBtn.setDisable(true);
		}
	}

	public void goBtnClicked(){
		String idStr = userIdField.getText();
		if(Functions.isInt(idStr)) {
			int id = Integer.parseInt(idStr);
			MainWindow.user = MainWindow.lib.getUser(id);
			if (MainWindow.user != null) {
				showSidePanel();
			}else {
				onlyNumText.setVisible(false);
				noUserFoundText.setVisible(true);
			}
			
		}else {
			noUserFoundText.setVisible(false);
			onlyNumText.setVisible(true);
		}
	}
	
	
	public void basketListClick(MouseEvent event) {
		
	   	if (cm2 != null) {
	    	if (cm2.isShowing()) {
	    		cm2.hide(); // Don't allow duplicate context menus open
	    	}
    	}
		if (event.getButton() == MouseButton.SECONDARY) {
			if(basketList.getSelectionModel().getSelectedItem() != null) {
				HBox hBox = (HBox) basketList.getSelectionModel().getSelectedItem();
				ObservableList list =  hBox.getChildren();
				Text txt1 = (Text) list.get(0);
				String tmpQty = txt1.getText().substring(0, 2);
				int tmpQty2 = 0;
				if (Functions.isInt(tmpQty)) {
					tmpQty2 = Integer.parseInt(tmpQty);
				}else {
					tmpQty2 = Integer.parseInt(tmpQty.substring(0, 1));
				}
				final int qty = tmpQty2;
				Label lbl1 = (Label) list.get(1);
				String title = lbl1.getText();
				Label lbl2 = (Label) list.get(2);
				String isbn = lbl2.getText();
				// Context menu
				cm2 = new ContextMenu();
				MenuItem mi1 = new MenuItem("Remove");
				cm2.getItems().add(mi1);
				mi1.setOnAction(e -> {
					try {
						for (Book book : booksInBasket.keySet()) {
							if (book.getIsbn().equals(isbn)) {
								removeFromBasket(book);
							}
						}
					}catch (Exception e2) {}
				});
				if (qty > 1) {
					MenuItem mi2 = new MenuItem("Remove all");
					cm2.getItems().add(mi2);
					mi2.setOnAction(e -> {
						try {
							for (Book book : booksInBasket.keySet()) {
								if (book.getIsbn().equals(isbn)) {
									for (int i = 0; i < qty ; i++) {
										removeFromBasket(book);			
									}
								}
							}
						}catch (Exception e3) {}
						
					});
				}
				
				cm2.show(basketList, event.getScreenX() , event.getScreenY());
				
			}
			
			
		}
	}
	
	public void removeFromBasket(Book book) {
		if (book != null && MainWindow.user != null) {
			if (booksInBasket.containsKey(book)) {
				if (booksInBasket.get(book) > 1) {
					booksInBasket.put(book, booksInBasket.get(book) - 1);
				}else {
					booksInBasket.remove(book);
				}
				updateBasket();
			}
		}
	}
	
	// Add to basket
	public void addToBasket(Book book){
		if (book != null && MainWindow.user != null) {
			if (booksInBasket.containsKey(book)) {
				booksInBasket.put(book, booksInBasket.get(book) +1 );
			}else {
				booksInBasket.put(book, 1);
			}
			
			updateBasket();
		}
	}
	
	public void updateBasket(){
		basketList.getItems().clear();
		for (Entry<Book, Integer> book : booksInBasket.entrySet()) {
			Label isbn = new Label(book.getKey().getIsbn());
			isbn.setId("bookIsbnBasket");
			
			String title = book.getKey().getTitle();
			if (title.length() > 20) {
				title = title.substring(0, 20) + "...";
			}
			Label titleLabel = new Label(title);
			
			titleLabel.setId("bookTitleBasket");
			HBox hBox = new HBox(new Text(book.getValue().toString() + "pcs"), titleLabel, isbn);
			hBox.setSpacing(15);
			basketList.getItems().add(hBox);
		}
		
		if (booksInBasket.isEmpty()) {
			basketText.setVisible(true);
			cancelBtn.setDisable(true);
		}else {
			basketText.setVisible(false);
			cancelBtn.setDisable(false);
		}	
	}

	
	public void showSidePanel(){
		User user = MainWindow.user;
		if (user == null) {
			currencyText.setVisible(false);
			dateErrorText.setText("");
			dateErrorText.setVisible(false);
			returnDateText.setVisible(false);
			datePicker.setVisible(false);
			switchUserText.setVisible(false);
			cancelBtn.setVisible(false);
			noUserFoundText.setVisible(false);
			onlyNumText.setVisible(false);
			basketQtyText.setVisible(false);
			basketTitleText.setVisible(false);
			booksLoaningAmount.setText("");
			nameText.setText("");
			streetText.setText("");
			cityText.setText("");
			booksLoaningText.setVisible(false);
			balanceText.setVisible(false);
			amountText.setText("");
			basketText.setVisible(false);
			basketList.setVisible(false);
			loanBtn.setVisible(false);
			enterIdText.setVisible(true);
			userIdField.setVisible(true);
			goBtn.setVisible(true);
			
		} else {
			String bookCount = "";
			try {
				bookCount = Integer.toString(user.getBookList().size());
			}catch (Exception e) {
				bookCount = "0";
			}
			currencyText.setVisible(true);
			dateErrorText.setVisible(true);
			returnDateText.setVisible(true);
			datePicker.setVisible(true);
			setDatePicker();
			switchUserText.setVisible(true);
			cancelBtn.setVisible(true);
			noUserFoundText.setVisible(false);
			onlyNumText.setVisible(false);
			basketQtyText.setVisible(true);
			basketTitleText.setVisible(true);
			enterIdText.setVisible(false);
			userIdField.setVisible(false);
			goBtn.setVisible(false);
			nameText.setText(user.getName());
			streetText.setText(user.getStreet());
			cityText.setText(user.getCity());
			booksLoaningText.setVisible(true);
			booksLoaningAmount.setText(bookCount);
			balanceText.setVisible(true);
			int dept = (int) user.getDebt();
			String deptStr = "";
			if (dept > 0) {
				deptStr += "- ";
				amountText.setId("amountTextNegative");
			}else {
				amountText.setId("amountText");
			}
			deptStr += Integer.toString(dept);
			amountText.setText(deptStr);
			basketText.setVisible(true);
			basketList.setVisible(true);
			loanBtn.setVisible(true);
			updateBasket();
			
		}
	}
	
	public void setDatePicker(){
		int maxDays = Library.LOAN_ALLOWANCE;
		if (maxDays < 14) {
			datePicker.setValue(LocalDate.now().plusDays(Library.LOAN_ALLOWANCE));
		}else {
			datePicker.setValue(LocalDate.now().plusDays(14));
		}
	}
	
	public void switchUserClick() {
		MainWindow.user = null;
		display();
	}
	
	public void cancelBtnClicked() {
		booksInBasket.clear();
		updateBasket();
	}
	
	public void onEnterLogIn() {
		goBtnClicked();
	}
	
	/******** File MENU ********/
	public void newBook(){
		NewBookUI.display();
	}
	public void quitMenuClick() {
		MainWindow.closeProgram();
	}
	public void saveMenuBtnClick() {
		MainWindow.lib.save();
	}
	public void prefMenuBtnClick(){
		PreferencesUI.display();
	}
	public void aboutMenuBtnClick() {
		AboutUI.display();
	}
	
	/******** Main menu ********/
	public void homeMenuAction(){
		HomeUI.display();
	}
	public void booksMenuAction(){
		BooksUI.display();
	}
	public void usersMenuAction() {
		UserListUI.display();
	}
	public void goToBookView(Book book){
		BookViewUI.display(book);
	}
	public void openDelayedBooks() {
		DelayedBook.display();
	}	
	public void openRegister() {
		RegisterUserUI.display();
	}
	
	
	// for DEBUGGING
	public void returnAllBooks(){
		User user = MainWindow.user;
		ArrayList<LoanInstance> bookList;
		try {
			bookList = user.getBookList();
			for (int i = bookList.size() - 1 ; i >= 0 ; i--) {
				MainWindow.lib.returnBook(user, user.getBookList().get(i).getBook());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	
		System.out.println("All books returned for : " + user.getName());
		display();
		
	}
}
