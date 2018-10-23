import javax.swing.*;
import java.awt.event.*;

class Controller
{
    private View view;
    private Model model;
    private GameLoop game;
    public static final int SIZE=500;
    Controller(View view, Model model)
    {
        this.view=view;
        this.model=model;
        game=new GameLoop();
        view.addKeyListener(new Keyboard());
    }
    public void start()
    {
        game.run();
    }
    public static void main(String args[])
    {
        View view=new View();
        Model model=new Model();
        Controller controller=new Controller(view, model);
        controller.start();
        System.out.println("Galactic Attackers");
    }
    class GameLoop extends Thread
    {
        private Keyboard keyboard=new Keyboard();
        public void run()
        {
            try
            {
                while (true)
                {
                    model.movement(1);
                    model.fire();
                    view.load(model.getPositions());
                    view.append(model.getMissiles());
                    view.repaint();
                    this.sleep(10);
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
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
            {
                System.out.println("lol");
                model.moveship(-1,1);
            }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT) model.moveship(1,1);
            else if (e.getKeyCode() == KeyEvent.VK_SPACE) model.shoot();
        }
        public void keyReleased(KeyEvent e)
        {
            model.moveship(0,1);
        }
        public void keyTyped(KeyEvent e)
        {

        }
    }
}
