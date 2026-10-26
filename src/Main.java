import Domain.Friendship;
import Repo.FriendshipParser;
import Domain.User;
import Repo.UserParser;
import Repo.CsvRepo;
import Service.Service;
import Ui.Ui;
import Validari.FriendshipValidator;
import Validari.UserValidator;

void main() {
    CsvRepo<User> userRepo = new CsvRepo<User>("src/users.csv", new UserParser(), new UserValidator() );
    CsvRepo<Friendship> friendRepo = new CsvRepo<Friendship>("src/friendships.csv", new FriendshipParser(),new FriendshipValidator());
    Service service = new Service(userRepo,friendRepo);
    Ui ui = new Ui(service);
    ui.run();
}