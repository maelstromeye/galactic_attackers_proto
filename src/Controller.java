import javax.swing.*;
import java.awt.event.*;
class Controller
{
    private View view;
    private Model model;
    private GameLoop game;
    private static JButton play=new JButton("Play"),settings=new JButton("Settings"), quit=new JButton("Quit"), reset=new JButton("OK");
    private Menulistener menulistener;
    public static final int SIZE=1000;
    private Controller(View view, Model model)
    {
        this.view=view;
        this.model=model;
        game=new GameLoop();
        menulistener=new Menulistener();
        play.addActionListener(menulistener);
        settings.addActionListener(menulistener);
        quit.addActionListener(menulistener);
        reset.addActionListener(menulistener);
    }
    private void start()
    {
        game.run();
        play.removeActionListener(menulistener);
        settings.removeActionListener(menulistener);
        quit.removeActionListener(menulistener);
        reset.removeActionListener(menulistener);
    }
    public static void main(String args[])
    {
        View view=new View(play, settings, quit);
        while(true)
        {
            Model model=new Model();
            Controller controller=new Controller(view, model);
            controller.start();
        }
    }
    class GameLoop extends Thread
    {
        public void run()
        {
            long newtime, oldtime, sleeptime=0;
            double counter=0;
            try
            {
                model.pause();
                while(model.getpause()) sleep(10);
                view.load(model.getPositions());
                view.loadmisc(model.getlives(), model.getscore(), model.getstage());
                view.dowin();
                view.repaint();
                model.pause();
                oldtime=System.nanoTime();
                while(true)
                {
                    if(model.getpause())
                    {
                        sleep(10);
                        oldtime=System.nanoTime();
                        continue;
                    }
                    if(model.getlives()<=0)
                    {
                        view.loadmisc(model.getlives(), model.getscore(),  model.getstage());
                        view.dolose(reset);
                        break;
                    }
                    newtime=System.nanoTime()-oldtime;
                    System.out.println((double)newtime/10000000+counter);
                    model.movement((int) (newtime/10000000)+(int)counter);
                    counter+=(double)newtime/10000000-(int)(newtime/10000000)-(int)counter;
                    if(model.hit())
                    {
                        view.load(model.getPositions(), model.getMissiles(), model.getRockets(), model.getfree());
                        view.loadmisc(model.getlives(), model.getscore(),  model.getstage());
                        view.death();
                        view.repaint();
                        model.shotdown();
                        model.pause();
                        continue;
                    }
                    view.load(model.getPositions(), model.getMissiles(), model.getRockets(), model.getfree());
                    view.loadmisc(model.getlives(), model.getscore(),  model.getstage());
                    view.repaint();
                    if(model.checkwin())
                    {
                        if(true) model.generator();
                        else model.boss();
                        view.load(model.getPositions(), null, null, null);
                        view.loadmisc(model.getlives(), model.getscore(), model.getstage());
                        view.dowin();
                        view.repaint();
                        model.pause();
                    }
                    oldtime=System.nanoTime();
                    sleeptime-=newtime/1000000;
                    sleeptime+=10;
                    if(sleeptime>0) sleep(sleeptime);
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
    class Menulistener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource()==play)
            {
                view.game();
                view.addKeyListener(new Keyboard());
                model.pause();
            }
            if(e.getSource()==quit)
            {
                System.exit(0);
            }
            if(e.getSource()==reset)
            {
                view.menu(275, 75, null, play, settings, quit);
            }
        }
    }
}
