package com.mycompany.final_project;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 *
 * @author magno
 */
public class Final_Project {
    private static List<Flight> flights = new ArrayList<>();
    private static List<reservationSystem> reservations = new ArrayList<>();
    private static reservationSystem currentReservation = null; // Added missing field
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {  
        initializeFlights(); // Added missing call to initialize flights
        loadBookedSeats();
        while (true) {
            System.out.println("\n=== Flight Booking System ===");
            System.out.println("1. Book a Flight");
            System.out.println("2. Edit Booking");
            System.out.println("3. View Reservation");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    try {
                        bookFlight(); // Fixed method name from booking() to bookFlight()
                    } catch (IOException e) {
                        System.out.println("Error booking flight: " + e.getMessage());
                    }
                    break;
                case 2:
                    editBooking();
                    break;
                case 3:
                    viewReservation();
                    break;
                case 4:
                    cancelBooking();
                    break;
                case 5:
                    System.out.println("Thank you for using the Flight Booking System. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
     
    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        int input = scanner.nextInt();
        scanner.nextLine(); 
        return input;
    }

   private static void initializeFlights() {
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
         //Domestic Flights
        flights.add(new domesticFlight("Manila", "Domestic", "MNL", LocalDateTime.of(2025, 5, 19, 9, 0).format(formatter), LocalDateTime.of(2025, 5, 19, 10, 10).format(formatter),3000.00, null));
        flights.add(new domesticFlight("Cebu", "Domestic", "CEB", LocalDateTime.of(2025, 6, 2, 8, 30).format(formatter), LocalDateTime.of(2025, 6, 2, 9, 40).format(formatter), 2800.00, null));
        flights.add(new domesticFlight("Iloilo", "Domestic", "ILO",   LocalDateTime.of(2025, 6, 5, 10, 15).format(formatter), LocalDateTime.of(2025, 6, 5, 11, 25).format(formatter), 3400.00, null)); 
        //International Flights
        flights.add(new internationalFlight(true, "Japan", "International", "Tokyo (Narita)", LocalDateTime.of(2025, 6, 10, 7, 15).format(formatter), LocalDateTime.of(2025, 6, 10, 15, 30).format(formatter), 18000.00,  null ));
        flights.add(new internationalFlight(true,"Italy", "International", "Milan (Linate)",LocalDateTime.of(2025, 6, 12, 9, 45).format(formatter), LocalDateTime.of(2025, 6, 13, 10, 30).format(formatter), 26000.00, null ));
        flights.add(new internationalFlight(true, "United States", "International", "Seattle (SEA)", LocalDateTime.of(2025, 6, 15, 11, 30).format(formatter), LocalDateTime.of(2025, 6, 16, 13, 45).format(formatter), 32000.00, null ));

    }
 
    public static double calculateDomesticFare(domesticFlight flight) {
        // Assuming domestic tax is 12% of the fare
        return flight.getAirlineFare() * 1.12;
    }

    public static double calculateInternationalFare(internationalFlight flight) {
        // Assuming international tax is 25% of the fare
        return flight.getAirlineFare() * 1.25;
    }
    
    private static void loadBookedSeats() throws IOException, DateTimeParseException {
        File directory = new File(".");
        File[] receiptFiles = directory.listFiles(new FileFilter() {
            
            @Override 
            public boolean accept(File file) {
            String fileName = file.getName();
                    return fileName.indexOf("receipt_") == 0 &&
                           fileName.lastIndexOf(".txt") == fileName.length() - 4;
            }
        });
        if (receiptFiles == null || receiptFiles.length == 0) {
            System.out.println("No receipt files found.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        for (File file : receiptFiles) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String bookingID = null;
            String destination = null;
            String seatNumber = null;
            LocalDateTime departureTime = null;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Booking ID: ")) {
                    bookingID = line.substring("Booking ID: ".length()).trim();
                } else if (line.startsWith("Flight: ")) {
                    String[] parts = line.split(" to ");
                    if (parts.length > 1) {
                        destination = parts[1].trim();
                    }
                } else if (line.startsWith("Seat: ")) {
                    seatNumber = line.substring("Seat: ".length()).trim();
                } else if (line.startsWith("Departure: ")) {
                    String dateString = line.substring("Departure: ".length()).trim();
                    departureTime = LocalDateTime.parse(dateString, formatter);
                }
            }
            reader.close();

            // Validate required fields
            if (bookingID == null || destination == null || seatNumber == null || departureTime == null) {
                System.out.println("Skipping invalid receipt file: " + file.getName());
                continue;
            }

            // Find the flight with matching destination and departure time
            Flight flight = null;
            for (Flight f : flights) {
                if (f.getDestination().equals(destination) && f.getDepartureTime().equals(departureTime)) {
                    flight = f;
                    break;
                }
            }

            if (flight != null) {
                // Mark the seat as booked and store in reservation
                seatManager[] seats = flight.getSeats();
                seatManager bookedSeat = null;
                for (seatManager seat : seats) {
                    if (seat.getSeatNumber().equals(seatNumber)) {
                        seat.booked();
                        bookedSeat = seat;
                        break;
                    }
                }
                if (bookedSeat != null) {
                    // Create a partial reservation
                    reservationSystem reservation = new reservationSystem(null, flight, bookedSeat, bookingID, 0.0, null);
                    reservations.add(reservation);
                } else {
                    System.out.println("Seat " + seatNumber + " not found for flight to " + destination + " in receipt " + file.getName());
                }
            } else {
                System.out.println("Flight to " + destination + " with departure " + departureTime + " not found for receipt " + file.getName());
            }
        }
        System.out.println("Loaded " + receiptFiles.length + " receipt(s).");
    }

    public static void bookFlight() throws IOException {
        System.out.println("\n--- Book a Flight ---");

        // Collect passenger details
        System.out.print("Enter passenger name: ");
        String name = scanner.nextLine();

        //Checking Age
        boolean validAge = false;
        int ageDiffer = 0;

        do{
            System.out.println("Enter passenger age:");
            String ageInput = scanner.nextLine();
            
            try{
                ageDiffer = Integer.parseInt(ageInput);
                if(ageDiffer >= 18){
                    validAge = true;

                } else {
                    System.out.println("You are too young to book!");
                }
            } catch (NumberFormatException e){
                System.out.println("Invalid Input. Please enter your age.");
            } 
        } while(!validAge);
        
        String age = String.valueOf(ageDiffer);

        //Checing format for numbers.
        boolean validNumber = false;    
        String contactNumber = "";

        do{
            System.out.println("Enter contact number: ");
            String numberInput = scanner.nextLine();          
            boolean isNumber = true;
            boolean isValidFormat = false;
            
            if(numberInput.length() == 11){         
                if(numberInput.charAt(0) == '0' && numberInput.charAt(1) == '9'){
                isValidFormat = true;          
               //Check if everything are numbers 
               for(int i = 2;i < numberInput.length();i++){
                   if(!Character.isDigit(numberInput.charAt(i))){
                       isNumber = false;
                       break;
                   }
               } 
              }  
            } else if(numberInput.length() == 13){
                       if(numberInput.charAt(0) == '+' && numberInput.charAt(1) == '6' && numberInput.charAt(2) == '3'){
                           for(int i = 3;i < numberInput.length();i++){
                                if(!Character.isDigit(numberInput.charAt(i))){
                                    isNumber = false;
                                    break;
                                }
                            } 
                       }
            }
            if(isNumber && isValidFormat){
                contactNumber = numberInput;
                validNumber = true;
            } 
            else{
                System.out.println("Enter a valid number. Use '09XXXXXXXXX' and '+63XXXXXXXXX'. ");
            }    
        } while(!validNumber);

        
        passenger passengerObj = new passenger(name, age, contactNumber);
        
        // Ask for flight type
        System.out.println("Is the flight Domestic or International?");
        String choice = scanner.nextLine().trim().toLowerCase();

        Flight selectedFlight = null;
        int flightChoice = -1;

        List<Flight> domesticFlights = new ArrayList<>();
        List<Flight> internationalFlights = new ArrayList<>();

        // Handle passport for international flights
        if (choice.equals("international")) {
            System.out.print("Enter Passport No.: ");
            String passportNumber = scanner.nextLine();
            if (passportNumber == null || passportNumber.trim().equals("")) {
                System.out.println("Passport number is required for international flights.");
                return;
            }
            passengerObj.setPassportNumber(passportNumber);
        }

        switch(choice) {
            case "domestic":
                System.out.println("\nAvailable Domestic Flights:");
                int index = 1;
                for (Flight flight : flights) {
                    if (flight.getFlightType().equalsIgnoreCase("Domestic")) {
                        domesticFlights.add(flight); // Store domestic flight in list
                        System.out.printf("%d. %s -> %s, Dep: %s, Arr: %s, Fare: %.2f\n",
                            index, flight.getFlightType(), flight.getDestination(),
                            flight.getDepartureTime(), flight.getArrivalTime(), flight.getAirlineFare());
                        index++;
                    }
                }
                break;

            case "international":
                System.out.println("\nAvailable International Flights:");
                int index_ = 1;
                for (Flight flight : flights) {
                    if (flight.getFlightType().equalsIgnoreCase("International")) {
                        internationalFlights.add(flight); // Store international flight in list
                        System.out.printf("%d. %s -> %s, Dep: %s, Arr: %s, Fare: %.2f\n",
                            index_, flight.getFlightType(), flight.getDestination(),
                            flight.getDepartureTime(), flight.getArrivalTime(), flight.getAirlineFare());
                        index_++;
                    }
                }
                break;

            default:
                System.out.println("Invalid selection. Please enter 'Domestic' or 'International'.");
                return;
        }

        // Ensure flights were found
        if (choice.equals("domestic") && domesticFlights.isEmpty()) {
            System.out.println("No domestic flights available.");
            return;
        }
        else if (choice.equals("international") && internationalFlights.isEmpty()) {
            System.out.println("No international flights available.");
            return;
        }
        // Allow user to select a flight
        try {
            flightChoice = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
            return;
        }

        // Validate selection
        if (flightChoice < 1 || flightChoice > (choice.equals("domestic") ? domesticFlights.size() : internationalFlights.size())) {
            System.out.println("Invalid flight selection.");
            return;
        }

        selectedFlight = (choice.equals("domestic")) ? domesticFlights.get(flightChoice - 1) : internationalFlights.get(flightChoice - 1);

        // Seat selection logic
        seatManager[] seats = selectedFlight.getSeats();

        boolean hasAvailableSeats = false;
        for (seatManager seat : seats) {
            if (!seat.isSeatBooked()) {
                hasAvailableSeats = true;
                break;
            }
        }
        if (!hasAvailableSeats) {
            System.out.println("No seats available for this flight.");
            return;
        }

        System.out.println("\nAvailable Seats:");
        for (seatManager seat : seats) {
            if (!seat.isSeatBooked()) {
                System.out.print(" " + seat.getSeatNumber() + " ");
            }
        }

        System.out.print("\nEnter seat number: ");
        String seatNumber = scanner.nextLine().toUpperCase();
        seatManager selectedSeat = null;

        for (seatManager seat : seats) {
            if (seat.getSeatNumber().equals(seatNumber) && !seat.isSeatBooked()) {
                selectedSeat = seat;
                break;
            }
        }

        if (selectedSeat == null) {
            System.out.println("Seat " + seatNumber + " is unavailable or invalid.");
            return;
        }

        selectedSeat.setSeatBooked(true);
        String bookingID = UUID.randomUUID().toString().substring(0, 8);

        // Total fare calculation
        double totalFare;                                   
        if (selectedFlight instanceof domesticFlight) {
            totalFare = calculateDomesticFare((domesticFlight) selectedFlight);
        } else if (selectedFlight instanceof internationalFlight) {
            totalFare = calculateInternationalFare((internationalFlight) selectedFlight);
        } else {
            totalFare = selectedFlight.getAirlineFare();
        }

        // Create a reservation with the current date/time
        LocalDateTime bookingDateTime = LocalDateTime.now();
        
        currentReservation = new reservationSystem(
            passengerObj,       
            selectedFlight,      
            selectedSeat,        
            bookingID,           
            totalFare,           
            bookingDateTime      
        );

        // Print the receipt
        System.out.println("\nBooking successful! Here is your receipt:\n");
        currentReservation.printReceipt();
        //Save receipt.
        currentReservation.saveReceiptFile();
    }

    public static void editBooking() {
        if(currentReservation == null){
            System.out.println("No existing booking found. Please book a flight first.");
            return;
        }
        System.out.println("\n--- Edit Booking ---");
        System.out.println("1. International Flight");
        System.out.println("2. Domestic Flight");
        System.out.println("X. Cancel (Return to main menu)");
        System.out.print("Choose flight type to edit [1/2/X]: ");

        String choice = scanner.nextLine().trim();

        if(choice.equalsIgnoreCase("X")){
            System.out.println("Thank you for checking our booking system.");
            return;
        }
        
        switch(choice){
            case "1":
                if(!currentReservation.getFlight().getFlightType().equalsIgnoreCase("International")){
                    System.out.println("You can only edit International booking here.");
                    return;
                }
                editInternationalBooking();
                break;
            case "2":
                if(!currentReservation.getFlight().getFlightType().equalsIgnoreCase("Domestic")){
                    System.out.println("You can only edit Domestic booking here.");
                    return;
                }
                editDomesticBooking();
                break;
            default:
                System.out.println("Invalid choice. Please enter 1, 2 or X.");
                System.out.println("Thank you for using our Flight Booking System.");
                return;
        }

        System.out.print("Confirm Changes? (Y/N): ");
        String confirmation = scanner.nextLine().trim();
        if(confirmation.equalsIgnoreCase("Y")){
            System.out.println("\nBooking updated successfully.");
        } else {
            System.out.println("Changes are discarded.");
        }
    }

    private static void editInternationalBooking() {
        System.out.println("----------------Editing International Booking----------------");
        System.out.println("Enter new passport number (Current: "+
                            currentReservation.getPassenger().getPassportNumber()+"): ");
        String newPassportNumber = scanner.nextLine().trim();
        if(newPassportNumber != null && !newPassportNumber.equals("")) {
            currentReservation.getPassenger().setPassportNumber(newPassportNumber);
        }

        System.out.println("Enter new contact number (Current: "+
                        currentReservation.getPassenger().getContactNumber()+"): ");
        String newContactNumber = scanner.nextLine().trim();
        if(newContactNumber != null && !newContactNumber.equals("")) {
            currentReservation.getPassenger().setContactNumber(newContactNumber);
        }
        
        boolean validAge = false;
        do{
            System.out.print("Please enter new age (Current: "+
                             currentReservation.getPassenger().getAge()+
                             ") or press Enter to keep current: ");
            String newAge = scanner.nextLine().trim();
            if(newAge == null || newAge.equals("")){
                validAge = true;
            } else if(isValidAge(newAge)){
                currentReservation.getPassenger().setAge(newAge);
                validAge = true;
            }
        } while (!validAge);
    }

    private static void editDomesticBooking() {
        System.out.println("----------------Editing Domestic Booking----------------");
        System.out.println("Enter new contact number (Current: "+
                            currentReservation.getPassenger().getContactNumber()+
                            "): ");
        String newContact = scanner.nextLine().trim();
        if(newContact != null && !newContact.equals("")) {
            currentReservation.getPassenger().setContactNumber(newContact);
        }
        
        boolean validAge = false;
        do{
            System.out.println("Enter new age (Current: "+
                                currentReservation.getPassenger().getAge()+
                                ") or press Enter to keep current: ");
            String newAge2 = scanner.nextLine().trim();
            if(newAge2 == null || newAge2.equals("")){
               validAge = true;
            } else if(isValidAge(newAge2)){
                currentReservation.getPassenger().setAge(newAge2);
                validAge = true;
            }
        } while(!validAge);
    }

    private static boolean isPositiveInteger(String age) {
        if(age == null || age.equals("")){
            return false;
        }
        for(int i = 0; i < age.length(); i++){
            if(!Character.isDigit(age.charAt(i))){
                return false;
            }
        }
        return true;
    }

    private static boolean isValidAge(String ageLimit){
        if(!isPositiveInteger(ageLimit)){
            System.out.println("Error: age must be a positive number");
            return false;
        }
        int age = Integer.parseInt(ageLimit);
        if (age < 18){
            System.out.println("You're too young to book a flight!");
            return false;
        }
        return true;
    }

    public static void viewReservation() {
        if (reservations == null || reservations.size() == 0) {
            System.out.println("No reservations available.");
            return;
        }
        
        System.out.print("Enter your Booking ID: ");
        String searchID = scanner.nextLine().trim();
        
        boolean found = false;
        for (reservationSystem reservation : reservations) {
            if (reservation.getBookingID().equalsIgnoreCase(searchID)) {
                System.out.println("\n--- Reservation Found ---");
                reservation.printReceipt();
                System.out.println("\n--- End of Reservation Details ---");
                found = true;
                break;
            }
        }
        
        if (!found) {
            System.out.println("Reservation with Booking ID '" + searchID + "' does not exist.");
        }
    }

    private static void cancelBooking() {
        if (reservations == null || reservations.size() == 0) {
            System.out.println("No bookings found to cancel.");
            return;
        }
        
        System.out.print("Enter the Booking ID you wish to cancel: ");
        String cancelID = scanner.nextLine().trim();
        
        reservationSystem toCancel = null;
        for (reservationSystem reservation : reservations) {
            if (reservation.getBookingID().equalsIgnoreCase(cancelID)) {
                toCancel = reservation;
                break;
            }
        }
        
        if (toCancel == null) {
            System.out.println("No booking found with ID: " + cancelID);
            return;
        }
        
        while (true) {
            System.out.print("Are you sure you want to cancel the booking? (yes/no): ");
            String confirmation = scanner.nextLine().toLowerCase().trim();
            
            switch (confirmation) {
                case "yes":
                    seatManager seat = toCancel.getSeats();
                    if (seat != null) {
                        seat.setSeatBooked(false); // Mark the seat as available again
                        reservations.remove(toCancel);
                        
                        // If we're canceling the current reservation, set it to null
                        if (currentReservation == toCancel) {
                            currentReservation = null;
                        }
                        
                        System.out.println("Booking cancelled successfully.");
                    } else {
                        System.out.println("Seat info not found. Cannot cancel booking properly.");
                    } 
                    return;
                case "no":
                    System.out.println("Cancellation aborted.");
                    return;
                default:
                    System.out.println("Invalid input. Please type 'yes' or 'no'.");
                    break;
            }
        }
    }
}
