import javax.swing.*;
import java.awt.event.*;

class Controller
{
    private View view;
    private Model model;
    private GameLoop game;
    public static final int SIZE=1000;
    Controller(View view, Model model)
    {
        this.view=view;
        this.model=model;
        game=new GameLoop();
        view.addKeyListener(new Keyboard());
    }
    public void start() {game.run();}
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
        public void run()
        {
            try
            {
                while (true)
                {
                    model.movement(1);
                    model.hit();
                    view.load(model.getPositions());
                    view.append(model.getMissiles());
                    view.append(model.getRockets());
                    view.append(model.getfree());
                    view.repaint();
                    if(model.checkwin()==true) view.dowin();
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
            if (e.getKeyCode()==KeyEvent.VK_LEFT) model.moveship(1,-1);
            if (e.getKeyCode()==KeyEvent.VK_RIGHT) model.moveship(1,1);
            if (e.getKeyCode()==KeyEvent.VK_SPACE) model.shoot();
            if (e.getKeyCode()==KeyEvent.VK_DOWN) model.pause();
            if (e.getKeyCode()==KeyEvent.VK_UP) model.skip();
        }
        public void keyReleased(KeyEvent e) {model.moveship(1,0);}
        public void keyTyped(KeyEvent e) {}
    }
}
