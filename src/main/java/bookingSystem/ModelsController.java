package bookingSystem;

import bookingSystem.models.Movie;
import bookingSystem.models.Showtime;
import bookingSystem.models.Theater;
import bookingSystem.models.Ticket;
import userSystem.UserController;
import view.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModelsController {
	
	private BookingController bookingController;
	private UserController userController;
	private GUI gui;
	private int userId = -1;
	private String userEmail = "";
	private int selectedSeat = -1;
	private double paymentAmount = 0;
	public static void main(String[] args) {
		ModelsController app = new ModelsController();
		app.run();
	}
	
	private void run() {
		gui = new GUI();
		bookingController = new BookingController();
		UserButtonListener ubl = new UserButtonListener();
		gui.getUserView().addButtonsListener(ubl);
		
		BookingButtonListener bbl = new BookingButtonListener();
		gui.getBookingView().populateMovies(bookingController.getAllMovies());
		gui.getBookingView().addButtonsListener(bbl);
		
		CancellationButtonListener cbl = new CancellationButtonListener();
		gui.getCancellationView().addButtonsListener(cbl);
		
		LoginButtonListener lbl = new LoginButtonListener();
		gui.getLoginView().addButtonsListener(lbl);
		
		RegisterButtonListener rbl = new RegisterButtonListener();
		gui.getRegisterView().addButtonsListener(rbl);
		
		PaymentButtonListener pbl = new PaymentButtonListener();
		gui.getPaymentView().addButtonsListener(pbl);
		
	}
	
	
	
	
	private void handleMovieEvent() {
		Movie m = gui.getBookingView().getSelectedMovie();
		gui.getBookingView().populateTheaters(bookingController.getAllTheaters(m));
	}
	
	private void handleTheaterEvent() {
		Movie m = gui.getBookingView().getSelectedMovie();
		Theater t = gui.getBookingView().getSelectedTheater();
		gui.getBookingView().populateShowtimes(bookingController.getAllShowtimes(m, t));
	}
	
	private void handleShowtimeEvent() {
		Movie m = gui.getBookingView().getSelectedMovie();
		Theater t = gui.getBookingView().getSelectedTheater();
		Showtime s = gui.getBookingView().getSelectedShowtime();
		gui.getBookingView().populateSeats(bookingController.getAllSeats(m, t, s));
	}
	
	private boolean login() {
		String username = gui.getLoginView().getUsernameText();
		String password = gui.getLoginView().getPasswordText();
		userId = checkCredentials(username, password);
		if (userId >= 0) {
			//TODO: Get user credit cards
			//gui.getPaymentView().populateCreditCards(creditCardList);
			return true;
		}
		return false;
	}
	
	private int checkCredentials(String username, String password) {
		//credential checking not implemented, but return User Id if correct, return -1 if incorrect
		return 1;
	}
	
	private void handleRegisterEvent() {
		String email = gui.getRegisterView().getEmailText();
		String password = gui.getRegisterView().getPasswordText();
		String name = gui.getRegisterView().getNameText();
		String address = gui.getRegisterView().getAddressText();
		//TODO: register user
		//if successful registration
		userEmail = email;
		System.out.println("Registered User: " + name);
		paymentAmount += 20.00;
		gui.getPaymentView().setPaymentAmount(paymentAmount);
		gui.setCard(5);
	}
	
	
	private class UserButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			switch (command) {
			case "Purchase Ticket":
				gui.setCard(1);
				break;
			case "Cancel Ticket":
				gui.setCard(2);
				break;
			}
		}
	}

	
	private class BookingButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			System.out.println(command);
			switch (command) {
			case "Back":
				gui.setCard(0);
				break;
			case "Book Seat":
				selectedSeat = gui.getBookingView().getSelectedSeatNumber();
				System.out.println("Selected Seat: " + selectedSeat);
				paymentAmount += Ticket.PRICE;
				gui.getPaymentView().setPaymentAmount(paymentAmount);
				gui.setCard(4);
				break;
			case "Choose Movie":
				handleMovieEvent();
				//For some reason, the seat buttons don't update their visibility unless the card is swapped in and out
				gui.setCard(0);
				gui.setCard(1);
				break;
			case "Choose Theater":
				handleTheaterEvent();
				//For some reason, the seat buttons don't update their visibility unless the card is swapped in and out
				gui.setCard(0);
				gui.setCard(1);
				break;
			case "Choose Showtime":
				handleShowtimeEvent();
				//For some reason, the seat buttons don't update their visibility unless the card is swapped in and out
				gui.setCard(0);
				gui.setCard(1);
				break;
			}	
			
		}
	}
	
	private class CancellationButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			System.out.println(command);
			switch (command) {
			case "Back":
				gui.setCard(0);
				break;
			case "Cancel Ticket":
				try {
					int ticketNumber = gui.getCancellationView().getTicketNumber();
					Ticket ticket = bookingController.findTicket(ticketNumber);
					if (ticket == null) {
						gui.getCancellationView().setResultText("Ticket not found.");
					} else {
						//TODO: cancel ticket
					}
				} catch (NumberFormatException ex) {
					gui.getCancellationView().setResultText("Invalid Ticket Number");
				}
				break;
			}
		}
		
	}
	
	private class LoginButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			System.out.println(command);
			switch (command) {
			case "Back":
				gui.setCard(1);
				break;
			case "Register":
				gui.setCard(3);
				break;
			case "Login":
				if (login()) {
					userEmail = gui.getLoginView().getUsernameText();
					gui.setCard(5);
				} else {
					JOptionPane.showMessageDialog(gui, "Invalid credentials");
				}
				break;
			case "Continue as Guest":
				userEmail = gui.getLoginView().getUsernameText();
				if (userEmail.equals("")) {
					JOptionPane.showMessageDialog(gui, "Please Enter an Email");
				} else {
					//TODO: get user ID
					gui.setCard(5);
				}
				break;
			}
		}
		
	}
	
	private class RegisterButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			System.out.println(command);
			switch (command) {
			case "Back":
				gui.setCard(4);
				break;
			case "Register":
				handleRegisterEvent();
				break;
			}
		}
		
	}
	
	private class PaymentButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			System.out.println(command);
			switch (command) {
			case "Back":
				gui.setCard(4);
				break;
			case "Pay":
				String creditCardNumber = gui.getPaymentView().getCreditCardNumber();
				//charge credit card
				//TODO: save payment
				//TODO: mark ticket as booked
				//send email with ticket and receipt
				gui.setCard(0);
				JOptionPane.showMessageDialog(gui, "Ticket Booked");
				break;
			}
			
		}
		
	}
}
