package rentalapp;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import rentalapp.config.Database;
import rentalapp.repositories.RentalRepository;
import rentalapp.repositories.RentalRepositoryDbImpl;
import rentalapp.service.RentalService;
import rentalapp.service.RentalServiceImpl;
import rentalapp.views.RentalTerminalView;
import rentalapp.views.RentalView;


public class Main {


    public static void main(String[] args) {
        Database database = new Database("rental_kendaraan", "root", "", "localhost", "3306");
        database.setup();

        RentalRepository todoListRepository = new RentalRepositoryDbImpl(database);
        RentalService todoListService = new RentalServiceImpl(todoListRepository);
        RentalView todoListView = new RentalTerminalView(todoListService);
        todoListView.run();
    }

}