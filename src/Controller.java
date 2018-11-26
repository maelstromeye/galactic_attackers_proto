import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
class Controller
{
    private View view;
    private Model model;
    private GameLoop game;
    private static JButton play=new JButton("Play"),settings=new JButton("Settings"), quit=new JButton("Quit"), reset=new JButton("OK"), accept=new JButton("OK");
    private static JTextField seed=new JTextField(11);
    private Menulistener menulistener;
    public static final double SCALE, RATIO;
    public static final int SIZE;
    static
    {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        SCALE=d.getWidth()/2560;
        RATIO=1.2;
        SIZE=(int) (1000*SCALE);
    }
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
        accept.addActionListener(menulistener);
    }
    private void start()
    {
        game.run();
        play.removeActionListener(menulistener);
        settings.removeActionListener(menulistener);
        quit.removeActionListener(menulistener);
        reset.removeActionListener(menulistener);
        accept.addActionListener(menulistener);
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
            long newtime, oldtime;
            double counter=0, sleeptime=0;
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
                        if(model.getstage()%6!=0) model.generator();
                        else model.boss();
                        view.load(model.getPositions(), null, null, null);
                        view.loadmisc(model.getlives(), model.getscore(), model.getstage());
                        view.dowin();
                        view.repaint();
                        model.pause();
                    }
                    oldtime=System.nanoTime();
                    sleeptime-=(double)newtime/1000000;
                    sleeptime+=10;
                    if(sleeptime>0) sleep((int) sleeptime);
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
            if(e.getSource()==settings)
            {
                view.menu(275, 75, "seed: ");
                view.add(seed);
                view.add(accept);
            }
            if(e.getSource()==accept)
            {
                double[] arr=new double[11];
                String string=seed.getText();
                if(string.length()<1) arr[0]=128;
                else arr[0]=(string.charAt(0)%128)+128;
                if(string.length()<2) arr[1]=128;
                else arr[1]=(string.charAt(1)%128)+128;
                if(string.length()<3) arr[2]=64;
                else arr[2]=(string.charAt(2)%192)+64;
                if(string.length()<4) arr[3]=21;
                else arr[3]=(string.charAt(3)%21)+21;
                if(string.length()<5) arr[4]=16;
                else arr[4]=(string.charAt(4)%16)+16;
                if(string.length()<6) arr[5]=8;
                else arr[5]=(string.charAt(5)%8)+8;
                if(string.length()<7) arr[6]=16;
                else arr[6]=(string.charAt(6)%16)+16;
                if(string.length()<8) arr[7]=11;
                else arr[7]=(string.charAt(7)%21)+11;
                if(string.length()<9) arr[8]=16;
                else arr[8]=(string.charAt(8)%16)+16;
                if(string.length()<10) arr[9]=arr[0]+1;
                else arr[9]=(string.charAt(9)%128)+arr[0]+1;
                if(string.length()<11) arr[10]=947*945;
                else arr[10]=string.charAt(10)*947+947*945;
                model.loadseed(arr);
                model.generator();
                view.menu(275, 75, null, play, settings, quit);
            }
        }
    }
}
