void main() {
    CsvRepo<User> userRepo = new CsvRepo<>("src/users.csv", new UserParser(), new UserValidator() );
    CsvRepo<Friendship> friendRepo = new CsvRepo<>("src/friendships.csv", new FriendshipParser(),new FriendshipValidator());
    Service service = new Service(userRepo,friendRepo);
    Ui ui = new Ui(service);
    ui.run();
}
