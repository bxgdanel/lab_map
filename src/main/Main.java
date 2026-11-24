import Domain.Friendship;
import Domain.Tuple;
import Exceptions.ValidationException;
import Repo.*;
import Domain.User;
import Service.Service;
import Ui.Ui;
import Validari.FriendshipValidator;
import Validari.UserValidator;
import Validari.Validator;

void main() {
    try {
        Repositoryfactory factory = new Repositoryfactory();
        Validator<User> userValidator = new UserValidator();
        Repo<Long,User> userRepo = factory.createUserRepository(userValidator.andThen(user ->{
            if(user.getUserName().contains("@")) throw new ValidationException("Username-ul nu ar trebui sa contina @");
        }));
        Validator<Friendship> friendshipValidator = new FriendshipValidator(userRepo);
        Repo<Tuple<Long,Long>,Friendship> friendshipRepo = factory.createFriendshipRepository(friendshipValidator);

        Service service = new Service(userRepo, friendshipRepo);
        Ui ui = new Ui(service,factory.getPageSize());
        ui.run();
    }catch(ValidationException | IllegalArgumentException e){
        System.err.println("Eroare de configurare sau fisiere corupte: " + e.getMessage());
    }catch(RuntimeException e){
        System.err.println("Eroare: " + e.getMessage());
    }
}