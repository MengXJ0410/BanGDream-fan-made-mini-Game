public class Main {
    public static void main(String[] args) {

        GameFrame MainFrame = new GameFrame();
        Player player = new Player(MainFrame, Player.HEALTH,Player.SHIELD, Player.money);
        MainFrame.addKeyListener(player);
    }
}