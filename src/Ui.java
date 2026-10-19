import java.util.Scanner;

public class Ui {
    private final Service service;
    private final Scanner scanner;
    public Ui(Service s){
        this.service = s;
        scanner = new Scanner(System.in);
    }
    private void printMenu(){
        System.out.println("""
                1.Adauga User
                2.Adauga Prietenie
                3.Sterge User
                4.Sterge Prietenie
                5.Printeaza useri
                6.Printeaza prietenii
                """);
    }
    public void run() {
        while (true) {
            printMenu();
            System.out.print("Alege o optiune: ");
            String optiune = scanner.nextLine().trim();

            if (optiune.equals("0")) {
                System.out.println("La revedere!");
                break;
            }

            try {
                switch (optiune) {
                    case "1" -> ui_addUser();
                    case "2" -> ui_addFriendship();
                    case "3" -> ui_removeUser();
                    case "4" -> ui_removeFriendship();
                    case "5" -> printUsers();
                    case "6" -> printFriendships();
                    default -> System.out.println("Optiune invalida! Incearca din nou.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Eroare: ID-ul introdus trebuie sa fie un numar intreg!");
            } catch (RepoException e) {
                System.out.println("Eroare: " + e.getMessage());
            }
        }
        scanner.close();
    }
    private void ui_addUser(){
        System.out.println("Introduceti numele:");
        String nume = scanner.nextLine().trim();
        service.AddUser(nume);
    }
    private void ui_removeUser(){
        System.out.println("Introduceti id-ul:");
        Integer id = Integer.parseInt(scanner.nextLine().trim());
        service.RemoveUser(id);
    }
    private void ui_addFriendship(){
        System.out.println("Introduceti id-urile pe randuri diferite: ");
        Integer id1 = Integer.parseInt(scanner.nextLine().trim());
        Integer id2 = Integer.parseInt(scanner.nextLine().trim());
        service.AddFriendship(id1,id2);
    }
    private void ui_removeFriendship(){
        System.out.println("Introduceti id-ul:");
        Integer id = Integer.parseInt(scanner.nextLine().trim());
        service.RemoveFriendship(id);
    }
    private void printUsers(){
        for(var user : service.get_all_users())
            System.out.println(user);
    }
    private void printFriendships(){
        for(var user : service.get_all_friendships())
            System.out.println(user);
    }
}
