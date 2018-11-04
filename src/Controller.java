import java.awt.event.*;
class Controller
{
    private View view;
    private Model model;
    private GameLoop game;
    public static final int SIZE=1000;
    private Controller(View view, Model model)
    {
        this.view=view;
        this.model=model;
        game=new GameLoop();
        view.addKeyListener(new Keyboard());
    }
    private void start() {game.run();}
        public static void main(String args[])
        {
            View view=new View();
            Model model=new Model();
            Controller controller=new Controller(view, model);
            controller.start();
            System.out.println("Galactic Attackers");
            return;
        }
    class GameLoop extends Thread
    {
        public void run()
        {
            try
            {
                view.load(model.getPositions());
                model.pause();
                while (true)
                {
                    model.movement(1);
                    if(model.hit()==true)
                    {
                        view.load(model.getPositions(), model.getMissiles(), model.getRockets(), model.getfree());
                        view.repaint();
                        model.shotdown();
                        if(model.islive()==false) view.dolose();
                        model.pause();
                    }
                    view.load(model.getPositions(), model.getMissiles(), model.getRockets(), model.getfree());
                    view.repaint();
                    if(model.getpause()) continue;
                    if(model.checkwin()==true)
                    {
                        view.dowin();
                        view.load(model.getPositions());
                        model.pause();
                    }
                    sleep(10);
                }
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    class Keyboard implements KeyListener
    {
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode()==KeyEvent.VK_LEFT) model.moveship(1,-1);
            if (e.getKeyCode()==KeyEvent.VK_RIGHT) model.moveship(1,1);
            if (e.getKeyCode()==KeyEvent.VK_SPACE) model.shoot(1);
            if (e.getKeyCode()==KeyEvent.VK_DOWN) model.pause();
            if (e.getKeyCode()==KeyEvent.VK_UP) model.skip();
        }
        public void keyReleased(KeyEvent e)
        {
            if (e.getKeyCode()==KeyEvent.VK_LEFT) model.moveship(1,-2);
            if (e.getKeyCode()==KeyEvent.VK_RIGHT) model.moveship(1,2);
            if (e.getKeyCode()==KeyEvent.VK_SPACE) model.shoot(0);
        }
        public void keyTyped(KeyEvent e) {}
    }
}
